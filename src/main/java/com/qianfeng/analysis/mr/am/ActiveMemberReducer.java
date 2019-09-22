package com.qianfeng.analysis.mr.am;

import com.qianfeng.analysis.model.key.StatsUserDimension;
import com.qianfeng.analysis.model.value.OutputMapWritable;
import com.qianfeng.analysis.model.value.TimeOutputValue;
import com.qianfeng.common.KpiType;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ActiveMemberReducer extends Reducer<StatsUserDimension, TimeOutputValue,StatsUserDimension, OutputMapWritable> {
private static final Logger logger=Logger.getLogger(ActiveMemberReducer.class);
private OutputMapWritable v=new OutputMapWritable();
//用于memberid去重的集合
    private Set set=new HashSet<>();
    //{1:1000}
    private MapWritable map=new MapWritable();

    @Override
    protected void reduce(StatsUserDimension key, Iterable<TimeOutputValue> values, Context context) throws IOException, InterruptedException {
        for(TimeOutputValue tv:values){
            this.set.add(tv.getId());
        }
        //设置KPI
        this.v.setKpi(KpiType.valueOfKpiName(key.getStatsCommonDimension().getKpi().getKpiName()));
        //通过集合的size设置新增用户的memberid个数
        this.map.put(new IntWritable(1),new IntWritable(set.size()));//在导入数据库中通过key获取新增用户
        v.setValue(map);
        context.write(key,v);
        //清空集合
        this.set.clear();
    }
}
