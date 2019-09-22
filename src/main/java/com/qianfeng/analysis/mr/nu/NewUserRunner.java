package com.qianfeng.analysis.mr.nu;

import com.qianfeng.analysis.OutputToMysqlFormat;
import com.qianfeng.analysis.model.base.DateDimension;
import com.qianfeng.analysis.model.key.StatsUserDimension;
import com.qianfeng.analysis.model.value.OutputMapWritable;
import com.qianfeng.analysis.model.value.TimeOutputValue;
import com.qianfeng.analysis.mr.service.IDimension;
import com.qianfeng.analysis.mr.service.impl.IDimensionImpl;
import com.qianfeng.common.DateEnum;
import com.qianfeng.common.GlobalConstants;
import com.qianfeng.util.JdbcUtil;
import com.qianfeng.util.TimeUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;
import org.apache.hadoop.mapreduce.Job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewUserRunner implements Tool {
    private static final Logger logger = Logger.getLogger(NewUserRunner.class);
    private Configuration conf = new Configuration();
    //主函数---入口
    public static void main(String[] args){
        try {
            //mr运行的一个辅助工具类，能运行任何实现了Tool接口的实现类
            ToolRunner.run(new Configuration(),new NewUserRunner(),args);
        } catch (Exception e) {
            logger.warn("NEW_USER TO MYSQL is failed !!!",e);
        }
    }
    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();

        //为结果表中的created赋值，设置到conf中,需要我们传递参数---一定要在job获取前设置参数
        this.setArgs(args, conf);
        Job job = Job.getInstance(conf, "NEW_USER TO MYSQL");
        job.setJarByClass(NewUserRunner.class);

        //设置map相关参数
        job.setMapperClass(NewUserMapper.class);
        job.setMapOutputKeyClass(StatsUserDimension.class);
        job.setMapOutputValueClass(TimeOutputValue.class);


        //设置reduce相关参数
        //设置reduce端的输出格式类
        job.setReducerClass(NewUserReducer.class);
        job.setOutputKeyClass(StatsUserDimension.class);
        job.setOutputValueClass(OutputMapWritable.class);
        job.setOutputFormatClass(OutputToMysqlFormat.class);

        //设置reduce task的数量
        job.setNumReduceTasks(1);

        //设置输入参数
        this.handleInputOutput(job);
        int res=job.waitForCompletion(true)?1:0;
        if(res==1){
            sumTotalUser(conf);
            return 1;
        }
        return 0;
    }
    //计算新增总用户
    private void sumTotalUser(Configuration conf) {
        //将时间转化为long值
        long time = TimeUtil.parseString2Long(conf.get(GlobalConstants.RUNNING_DATE));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        long yester = calendar.getTimeInMillis();
        //long time1=GlobalConstants.DAY_OF_MILLISECONDS;
        Connection conn=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        Map<String,Integer> map=new HashMap<>();//存储的是（plid+broid,总用户）
        IDimension iDimension=new IDimensionImpl();
        int todayId=iDimension.getIDimensionImplByDimension(DateDimension.buildDate(time, DateEnum.DAY));
       // int yesterId=iDimension.getIDimensionImplByDimension(DateDimension.buildDate(time-time1,DateEnum.DAY));
        int yesterId=iDimension.getIDimensionImplByDimension(DateDimension.buildDate(yester,DateEnum.DAY));
        try {
            conn = JdbcUtil.getConn();
            ps = conn.prepareStatement(conf.get("other_new_total_user_yesterday_sql"));
            //从数据库中查询前一天的总用户并放到map中
            ps.setInt(1,yesterId);
            rs = ps.executeQuery();
            while (rs.next()){
                int platformid = rs.getInt("platform_dimension_id");//平台维度id
                int totalusers = rs.getInt("total_install_users");//昨天的总用户
                map.put(platformid+"",totalusers);
            }

            //根据今天的时间维度id对数据库进行查询，返回平台维度id，和新增用户个数，如果map中存在平台维度id则
            //今天的总用户就是前一天的总用户个数+今天的新增用户个数
            //如果无则为今天的新增用户个数
            ps = conn.prepareStatement(conf.get("other_new_total_user_now_sql"));
            ps.setInt(1,todayId);
            rs = ps.executeQuery();
            while (rs.next()){
                int platformid = rs.getInt("platform_dimension_id");
                int newusers = rs.getInt("new_install_users");//今天的新增用户
                if (map.containsKey(platformid+"")){
                    newusers += map.get(platformid+"");
                }
                map.put(platformid+"",newusers);
            }
            //对stats_user表执行更新操作
            ps = conn.prepareStatement(conf.get("other_new_total_user_update_sql"));
            for (Map.Entry<String,Integer> entry: map.entrySet()) {
                ps.setInt(1,todayId);
                ps.setInt(2,Integer.valueOf(entry.getKey()));
                ps.setInt(3,entry.getValue());
                ps.setString(4,conf.get(GlobalConstants.RUNNING_DATE));
                ps.setInt(5,entry.getValue());
                ps.executeUpdate();
            }

            //计算浏览器维度下的总用户
            map.clear();
            //从stats_device_browser表中获取前一天的总用户
            ps = conn.prepareStatement(conf.get("other_new_total_browser_user_yesterday_sql"));
            ps.setInt(1,yesterId);
            rs = ps.executeQuery();
            while (rs.next()){
                int platformid = rs.getInt("platform_dimension_id");//平台维度id
                int browserid = rs.getInt("browser_dimension_id");//浏览器维度id
                int totalusers = rs.getInt("total_install_users");//昨天的总用户
                map.put(platformid+"_"+browserid,totalusers);
            }

            //根据今天的时间维度id对数据库进行查询，返回平台维度id，和新增用户个数，如果map中存在平台维度id则
            //今天的总用户就是前一天的总用户个数+今天的新增用户个数
            //如果无则为今天的新增用户个数
            ps = conn.prepareStatement(conf.get("other_new_total_browser_user_now_sql"));
            ps.setInt(1,todayId);
            rs = ps.executeQuery();
            while (rs.next()){
                int platformid = rs.getInt("platform_dimension_id");//平台维度id
                int browserid = rs.getInt("browser_dimension_id");//浏览器维度id
                int newusers = rs.getInt("new_install_users");//今天的新增用户
                if (map.containsKey(platformid+"_"+browserid)){
                    newusers += map.get(platformid+"_"+browserid);
                }
                map.put(platformid+"_"+browserid,newusers);
            }
            //对stats_device_browser表执行更新操作
            ps = conn.prepareStatement(conf.get("other_new_total_browser_user_update_sql"));
            for (Map.Entry<String,Integer> entry: map.entrySet()) {
                int platformid = Integer.valueOf(entry.getKey().substring(0,entry.getKey().indexOf("_")));

                int browserid = Integer.valueOf(entry.getKey().substring(entry.getKey().indexOf("_")+1));
                ps.setInt(1,todayId);
                ps.setInt(2,platformid);
                ps.setInt(3,browserid);
                ps.setInt(4,entry.getValue());
                ps.setString(5,conf.get(GlobalConstants.RUNNING_DATE));
                ps.setInt(6,entry.getValue());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtil.close(conn,ps,rs);
        }
    }
    @Override
    public void setConf(Configuration configuration) {
        conf.addResource("other_mapping.xml");
        conf.addResource("output_mapping.xml");
        conf.addResource("output_writter.xml");
        conf.addResource("core-site.xml");
        this.conf = conf;
    }

    @Override
    public Configuration getConf() {
        return this.conf;
    }
    /**
     * 参数处理,将接收到的日期存储在conf中，以供后续使用
     * @param args  如果没有传递日期，则默认使用昨天的日期
     * @param conf
     */
    private void setArgs(String[] args, Configuration conf) {
        String date = null;
        for (int i = 0;i < args.length;i++){
            if(args[i].equals("-d")){
                if(i+1 < args.length){
                    date = args[i+1];
                    break;
                }
            }
        }
        //date还是null，默认用昨天的时间
        if(date == null){
            date = TimeUtil.getYesterday();
        }
        //然后将date设置到时间conf中
        conf.set(GlobalConstants.RUNNING_DATE,date);
    }
    /**
     * 设置输入输出,_SUCCESS文件里面是空的，所以可以直接读取清洗后的数据存储目录
     * @param job
     */
    private void handleInputOutput(Job job) {
        String[] fields = job.getConfiguration().get(GlobalConstants.RUNNING_DATE).split("-");
        System.out.println("--------长度"+fields.length);
        String month = fields[1];
        String day = fields[2];

        try {
            FileSystem fs = FileSystem.get(job.getConfiguration());
            Path inpath = new Path("/ods/" + month + "/" + day );
            if(fs.exists(inpath)){
                FileInputFormat.addInputPath(job,inpath);
            }else{
                throw new RuntimeException("输入路径不存在inpath" + inpath.toString());
            }
        } catch (Exception e) {
            logger.warn("设置输入输出路径异常！！！",e);
        }
    }
}
