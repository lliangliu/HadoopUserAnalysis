package com.qianfeng.etl.mr;

import com.qianfeng.common.GlobalConstants;
import com.qianfeng.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.log4j.Logger;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.util.ToolRunner;
import java.io.IOException;

public class DriverDemo implements Tool {
    private static Logger logger=Logger.getLogger(DriverDemo.class);
    private Configuration conf=new Configuration();
    public static void main(String[] args) {
        try {
            ToolRunner.run(new Configuration(),new DriverDemo(),args);
        } catch (Exception e) {
            logger.error("执行etl异常",e);
        }
    }
    @Override
    public int run(String[] args) throws Exception {
       //运行的时候yarn jar xxx.jar 包名.类名 -d 2018-11-11固定格式
        //如果不给日期，我们就要指定默认日期
        //默认日期指定当前时间的前一天的数据
        Configuration conf=getConf();;
        //获取时间 将时间参数加入到conf中
        handleArgs(conf,args);
        Job job= Job.getInstance(conf,"GO TO HDFS");
        //设置路径类
        job.setJarByClass(DriverDemo.class);
        //设置map相关
        job.setMapperClass(MapperDemo.class);
        job.setMapOutputKeyClass(LogWritable.class);
        job.setMapOutputValueClass(NullWritable.class);
        //设置reduce数量
        job.setNumReduceTasks(0);
        //设置输入输出路径
        handleInputOutput(job);
        return job.waitForCompletion(true)?1:0;

    }
/**
 * 设置输入输出路径
 */
      private void handleInputOutput(Job job){
          try {
              String[] fields = job.getConfiguration().get(GlobalConstants.RUNNING_DATE).split("-");
              String month=fields[1];
              String day=fields[2];
              FileSystem fs=FileSystem.get(job.getConfiguration());
              //输入路径 /logs/11/11
              Path inPath=new Path("/logs/"+month+"/"+day);
              Path outPath = new Path("/ods/"+month+"/"+day);
              if(fs.exists(inPath)){
                  FileInputFormat.addInputPath(job,inPath);
              }else{
                  throw new RuntimeException("输入路径不存在"+inPath.toString());
              }
              if(fs.exists(outPath)){
                  fs.delete(outPath,true);
              }
              FileOutputFormat.setOutputPath(job,outPath);


          } catch (IOException e) {
              logger.error("设置输入输出路径异常",e);
          }

      }

    /**
     * 处理参数
     */
    private void handleArgs(Configuration conf,String[] args){
        String date=null;
        //-d 年-月-日
        if(args.length>0){
            for(int i=0;i<args.length;i++){
                if(args[i].equals("-d")){
                    if(i+1<args.length){
                        date=args[i+1];
                        break;
                    }
                }
            }
        }
        if(StringUtils.isEmpty(date)){
            date= TimeUtil.getYesterday();
        }
        conf.set(GlobalConstants.RUNNING_DATE,date);
    }
    @Override
    public void setConf(Configuration conf) {
        conf.addResource("core-site.xml");
        this.conf=conf;
    }

    @Override
    public Configuration getConf() {
        return this.conf;
    }
}
