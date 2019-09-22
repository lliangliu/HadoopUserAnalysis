package com.qianfeng.analysis.mr.local;


import com.qianfeng.analysis.model.key.StatsUserLcDimension;
import com.qianfeng.analysis.model.value.IdOutputValue;
import com.qianfeng.analysis.model.value.OutputMapWritable;

import com.qianfeng.common.KpiType;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;


public class  LocalReduce extends Reducer<StatsUserLcDimension, IdOutputValue,StatsUserLcDimension, OutputMapWritable> {
    private static final Logger logger=Logger.getLogger(com.qianfeng.analysis.mr.au.ActiveUserReducer.class);
    private OutputMapWritable v=new OutputMapWritable();
    //用于uuid去重的集合
    private Set<String>set=new HashSet<>();
    //{1:1000}
    private MapWritable map=new MapWritable();
    //用于u_sd去重的集合
    private Map<String,Integer> map1=new HashMap<>();
    //用于memberid去重的集合
    private Set<String> set1=new HashSet<>();
    @Override
    protected void reduce(StatsUserLcDimension key, Iterable<IdOutputValue> values, Context context) throws IOException, InterruptedException {
        for(IdOutputValue tv:values){
            this.set.add(tv.getUuid());
            this.set1.add(tv.getUmid());
            if(map1.containsKey(tv.getSid())){
                map1.put(tv.getSid(),2);//第二次会话
            }else{
                map1.put(tv.getSid(),1);
            }
        }
        // 输出
        //设置KPI
        this.v.setKpi(KpiType.LOCAL);
        //通过集合的size设置新增用户的uuid个数
        this.map.put(new IntWritable(1),new IntWritable(set.size()));
        this.map.put(new IntWritable(2),new IntWritable(map1.size()));
        this.map.put(new IntWritable(4),new IntWritable(set1.size()));
        int bounce_sessions = 0;
        for (Map.Entry<String, Integer> entry : this.map1.entrySet()) {
            if (entry.getValue() == 1) {
                bounce_sessions++;
                System.out.println(bounce_sessions+"-------个数");
            }
        }
        this.map.put(new IntWritable(3),new IntWritable(bounce_sessions));
        this.v.setValue(map);
        context.write(key,v);
        //清空集合
        this.set.clear();
        this.set1.clear();
        this.map1.clear();
    }
}
