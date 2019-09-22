package com.qianfeng.analysis.mr.nm;

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
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewMemberRunner implements Tool {
    private static final Logger logger = Logger.getLogger(NewMemberRunner.class);
    private Configuration conf = new Configuration();
    //主函数---入口
    public static void main(String[] args){
        try {
            //mr运行的一个辅助工具类，能运行任何实现了Tool接口的实现类
            ToolRunner.run(new Configuration(),new NewMemberRunner(),args);
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
        job.setJarByClass(NewMemberRunner.class);

        //设置map相关参数
        job.setMapperClass(NewMemberMapper.class);
        job.setMapOutputKeyClass(StatsUserDimension.class);
        job.setMapOutputValueClass(TimeOutputValue.class);


        //设置reduce相关参数
        //设置reduce端的输出格式类
        job.setReducerClass(NewMemberReducer.class);
        job.setOutputKeyClass(StatsUserDimension.class);
        job.setOutputValueClass(OutputMapWritable.class);
        job.setOutputFormatClass(OutputToMysqlFormat.class);

        //设置reduce task的数量
        job.setNumReduceTasks(1);

        //设置输入参数
        this.handleInputOutput(job);
        int res= job.waitForCompletion(true)?1:0;
        if(res==1){
           sumTotalmemberUser(conf);
            return 1;
        }
        return 0;
    }
    //计算新增总会员
    private void sumTotalmemberUser(Configuration conf) {
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
            ps = conn.prepareStatement(conf.get("other_new_total_member_now_sql"));
            //从数据库中查询今天的维度和新增会员并存入map中
            ps.setInt(1,todayId);
            rs = ps.executeQuery();
            while(rs.next()){
                int platformId = rs.getInt("platform_dimension_id");
                int newUsers = rs.getInt("new_members");
                map.put(platformId+"_",newUsers);//存储今天的平台维度和新增会员
            }
            //查询前一天的数据
            ps = conn.prepareStatement(conf.get("other_new_total_member_yesterday_sql"));
            ps.setInt(1,yesterId);
            rs = ps.executeQuery();
            while(rs.next()){
                int platformId = rs.getInt("platform_dimension_id");
                int totalUser = rs.getInt("total_members");
                if(map.containsKey(platformId+"_")){
                    map.put(platformId+"_",map.get(platformId+"_")+totalUser);
                    //如果今天的维度集合里面包含前一天的维度，将前一天的总用户加上今天的新增用户作为今天的总用户
                }
            }
            //将map中的数据更新到数据库stats_user表中
            ps = conn.prepareStatement(conf.get("other_new_total_member_update_sql"));
            for(Map.Entry<String ,Integer> entry : map.entrySet()){
                ps.setInt(1,todayId);
                ps.setInt(2,Integer.valueOf(entry.getKey().replace("_","")));
                ps.setInt(3,entry.getValue());
                ps.setString(4,conf.get(GlobalConstants.RUNNING_DATE));
                ps.setInt(5,entry.getValue());
                ps.executeUpdate();
            }

            //浏览器模块下
            map.clear();
            ps = conn.prepareStatement(conf.get("other_new_total_browser_member_now_sql"));
            //从数据库中查询今天的浏览器、平台、新增用户并存入map中
            ps.setInt(1,todayId);
            rs = ps.executeQuery();
            while(rs.next()){
                int platformId = rs.getInt("platform_dimension_id");
                int browserId = rs.getInt("browser_dimension_id");
                int newUsers = rs.getInt("new_members");
                map.put(platformId+"_"+browserId,newUsers);//存储今天的平台维度和新增用户
            }
            //查询前一天的数据
            ps = conn.prepareStatement(conf.get("other_new_total_browser_member_yesterday_sql"));
            ps.setInt(1,yesterId);
            rs = ps.executeQuery();
            while(rs.next()){
                int platformId = rs.getInt("platform_dimension_id");
                int browserId = rs.getInt("browser_dimension_id");
                int totalUser = rs.getInt("total_members");
                if(map.containsKey(platformId+"_"+browserId)){
                    map.put(platformId+"_"+browserId,map.get(platformId+"_"+browserId)+totalUser);
                    //如果今天的维度集合里面包含前一天的维度，将前一天的总用户加上今天的新增用户作为今天的总用户
                }
            }
            //将map中的数据更新到数据库stats_user表中
            ps = conn.prepareStatement(conf.get("other_new_total_browser_member_update_sql"));
            for(Map.Entry<String,Integer> entry : map.entrySet()){
                String [] fields = entry.getKey().split("_");
                int platformId = Integer.parseInt(fields[0]);
                int browserId = Integer.parseInt(fields[1]);
                ps.setInt(1,todayId);
                ps.setInt(2,platformId);
                ps.setInt(3,browserId);
                ps.setInt(4,entry.getValue());
                ps.setString(5,conf.get(GlobalConstants.RUNNING_DATE));
                ps.setInt(6,entry.getValue());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            JdbcUtil.close(conn,ps,rs);
        }
    }
    @Override
    public void setConf(Configuration configuration) {
        conf.addResource("output_mapping.xml");
        conf.addResource("output_writter.xml");
        conf.addResource("core-site.xml");
        conf.addResource("other_mapping.xml");
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
