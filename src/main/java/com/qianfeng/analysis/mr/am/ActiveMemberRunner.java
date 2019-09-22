package com.qianfeng.analysis.mr.am;

import com.qianfeng.analysis.OutputToMysqlFormat;
import com.qianfeng.analysis.model.key.StatsUserDimension;
import com.qianfeng.analysis.model.value.OutputMapWritable;
import com.qianfeng.analysis.model.value.TimeOutputValue;
import com.qianfeng.common.GlobalConstants;
import com.qianfeng.util.TimeUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

public class ActiveMemberRunner implements Tool {
    private static final Logger logger = Logger.getLogger(ActiveMemberRunner.class);
    private Configuration conf = new Configuration();
    //主函数---入口
    public static void main(String[] args){
        try {
            //mr运行的一个辅助工具类，能运行任何实现了Tool接口的实现类
            ToolRunner.run(new Configuration(),new ActiveMemberRunner(),args);
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
        job.setJarByClass(ActiveMemberRunner.class);

        //设置map相关参数
        job.setMapperClass(ActiveMemberMapper.class);
        job.setMapOutputKeyClass(StatsUserDimension.class);
        job.setMapOutputValueClass(TimeOutputValue.class);


        //设置reduce相关参数
        //设置reduce端的输出格式类
        job.setReducerClass(ActiveMemberReducer.class);
        job.setOutputKeyClass(StatsUserDimension.class);
        job.setOutputValueClass(OutputMapWritable.class);
        job.setOutputFormatClass(OutputToMysqlFormat.class);

        //设置reduce task的数量
        job.setNumReduceTasks(1);

        //设置输入参数
        this.handleInputOutput(job);
        return job.waitForCompletion(true)?1:0;
    }

    @Override
    public void setConf(Configuration configuration) {
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
