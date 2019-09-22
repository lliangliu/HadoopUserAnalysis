package com.qianfeng.analysis.mr.session;

import com.qianfeng.analysis.model.key.StatsUserDimension;
import com.qianfeng.analysis.model.value.OutputMapWritable;
import com.qianfeng.analysis.model.value.TimeOutputValue;
import com.qianfeng.common.GlobalConstants;
import com.qianfeng.common.KpiType;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;


import java.io.IOException;
import java.util.*;

public class SessionReducer extends Reducer<StatsUserDimension, TimeOutputValue,StatsUserDimension, OutputMapWritable> {
private static final Logger logger=Logger.getLogger(SessionReducer.class);
private OutputMapWritable v=new OutputMapWritable();
    //用于u_sd去重的集合
    Map<String,List<Long>> map1=new HashMap<>();
   List<Long> list=new ArrayList();
    //{1:1000}
    private MapWritable map=new MapWritable();

    @Override
    protected void reduce(StatsUserDimension key, Iterable<TimeOutputValue> values, Context context) throws IOException, InterruptedException {
       int sessionLengh=0;//会话长度
        int totalSession=0;//会话个数
        for(TimeOutputValue tv:values){
            String u_sd =tv.getId();
            long time=tv.getTime();
            this.list.add(time);
            this.map1.put(u_sd,list);
        }
         for (Map.Entry<String,List<Long>> en:this.map1.entrySet()){
             List<Long> list = en.getValue();
         }
         Collections.sort(list);
        if(list.size()>1){

            // if(list.get(i)>=0&&list.get(i)<= GlobalConstants.DAY_OF_MILLISECONDS) {
            sessionLengh += (int) (list.get(list.size()-1) - list.get(0))/(60*1000);
        }

            totalSession=this.map.size();
        //设置KPI
        this.v.setKpi(KpiType.valueOfKpiName(key.getStatsCommonDimension().getKpi().getKpiName()));
        this.map.put(new IntWritable(1),new IntWritable(totalSession));//在导入数据库中通过key获取新增用户
        if(sessionLengh>0){
        this.map.put(new IntWritable(2),new IntWritable(sessionLengh));
        }
        v.setValue(map);
        context.write(key,v);
        list.clear();
        map1.clear();
    }
}
