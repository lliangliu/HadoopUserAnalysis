package com.qianfeng.analysis;
import com.qianfeng.analysis.model.base.BaseDimension;
import com.qianfeng.analysis.model.value.StatsOutputValue;
import com.qianfeng.analysis.mr.IoOutputWriter;
import com.qianfeng.analysis.mr.service.IDimension;
import com.qianfeng.analysis.mr.service.impl.IDimensionImpl;
import com.qianfeng.common.KpiType;
import com.qianfeng.util.JdbcUtil;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


//将reduce端输出的数据 保存到mysql中
public class OutputToMysqlFormat extends OutputFormat<BaseDimension, StatsOutputValue> {

    @Override
    public RecordWriter<BaseDimension, StatsOutputValue> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
        Connection conn= JdbcUtil.getConn();//获得连接  jdbc连接数据库
        Configuration conf=context.getConfiguration();//获取sql语句，获取不同的sql语句的实现类
        IDimension iDimension = new IDimensionImpl();//获取维度id的方法
        return new OutputToMysqlRecordWriter(conf,conn,iDimension);

    }

    @Override
    public void checkOutputSpecs(JobContext jobContext) throws IOException, InterruptedException {

    }

    @Override
    public OutputCommitter getOutputCommitter(TaskAttemptContext context) throws IOException, InterruptedException {
        return new FileOutputCommitter(FileOutputFormat.getOutputPath(context),
                context);
    }
    public static class OutputToMysqlRecordWriter extends  RecordWriter<BaseDimension, StatsOutputValue>{
        Configuration conf = null;//用于获取sql语句
        Connection conn = null;//用于获取连接
        IDimension iDimension = null;//用于查询维度ID  每个kpi对应的id代表执行不同的sql语句
        //缓存ps语句
        //NEW_USER :ps语句
        private Map<KpiType, PreparedStatement> map=new HashMap<KpiType, PreparedStatement>();//不同的kpiName对应不同的sql语句
        //存储kpi对应的sql，进行批量处理
        private Map<KpiType,Integer>batch = new HashMap<KpiType,Integer>();
        public OutputToMysqlRecordWriter() { }
        public OutputToMysqlRecordWriter(Configuration conf, Connection conn, IDimension iDimension) {
            this.conf = conf;
            this.conn = conn;
            this.iDimension = iDimension;
        }

        //真正写出的方法
        @Override
        public void write(BaseDimension key, StatsOutputValue value) throws IOException, InterruptedException {
           //获取kpi对象
            KpiType kpi=value.getKpi();
            //获取ps对象
            PreparedStatement ps=null;
            int count=1;
            try {
                if(map.containsKey(kpi)){
                    ps=map.get(kpi);
                }else{
                    //通过配置文件，output_mapping.xml文件获取sql语句
                    //通过kpiName获取对应的sql语句  sql语句在配置文件中
                    ps=conn.prepareStatement(conf.get(kpi.kpiName));
                    //缓存语句
                    map.put(kpi,ps);//保存的是kpi对象  以经济对应的sql语句
                }
                batch.put(kpi,count);
                count++;
                //conf idimension ps key value
                //实际操作数据将数据插入表中 (在配置文件中通过key得到执行的类的包名和类名)
                String className=conf.get("writter_"+kpi.kpiName);//NewUserOutputWriter类
                //获取处理这个kpi写入表的实现类
                Class<?> classz = Class.forName(className);
                IoOutputWriter writer = (IoOutputWriter)classz.newInstance();
                writer.output(conf,key,value,ps,iDimension);//接口定义的是输出的规范
                if(batch.size()%50==0||batch.get(kpi)%50==0){
                    ps.executeBatch();
                    batch.remove(kpi);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void close(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {

            try {
                //循环执行
                for (Map.Entry<KpiType,PreparedStatement> en:map.entrySet()) {
                    en.getValue().executeBatch();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                //循环关闭
                for (Map.Entry<KpiType,PreparedStatement> en :map.entrySet()) {
                    JdbcUtil.close(conn,en.getValue(),null);
                }
            }


        }
    }
}
