package com.qianfeng.analysis.mr.ha;

import com.qianfeng.analysis.model.key.StatsUserDimension;
import com.qianfeng.analysis.model.value.OutputMapWritable;
import com.qianfeng.analysis.model.value.TimeOutputValue;
import com.qianfeng.common.DateEnum;
import com.qianfeng.common.KpiType;
import com.qianfeng.util.TimeUtil;
import org.apache.hadoop.io.*;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Map;
import java.util.Set;

public class HourlyActiveUserReducer extends Reducer<StatsUserDimension, TimeOutputValue,StatsUserDimension, OutputMapWritable> {
private static final Logger logger=Logger.getLogger(HourlyActiveUserReducer.class);
    private Set<String> set = new HashSet<String>();//去重uuid
    private Map<Integer, Set<String>> hours = new HashMap<Integer, Set<String>>();//时间段
    private OutputMapWritable v = new OutputMapWritable();//输出
    private MapWritable map = new MapWritable();
    private MapWritable hourMap = new MapWritable();
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        // 进行初始化操作
        for (int i = 0; i < 24; i++) {
            this.hourMap.put(new IntWritable(i), new IntWritable(0));
            this.hours.put(i, new HashSet<String>());
        }
    }

    @Override
    protected void reduce(StatsUserDimension key, Iterable<TimeOutputValue> values, Context context) throws IOException, InterruptedException {
        try {
            String kpiName = key.getStatsCommonDimension().getKpi().getKpiName();
            if (KpiType.HOURLY_ACTIVE_USER.kpiName.equals(kpiName)) {
                // 计算hourly active user
                for (TimeOutputValue value : values) {
                    // 计算出访问的小时，从[0,23]的区间段
                    int hour = TimeUtil.getDateInfo(value.getTime(), DateEnum.HOUR);//将时间戳转化成小时
                    this.hours.get(hour).add(value.getId()); // 将会话id添加到对应的时间段中
                }
                // 设置kpi
                this.v.setKpi(KpiType.HOURLY_ACTIVE_USER);
                // 设置value
                for (Map.Entry<Integer, Set<String>> entry : this.hours.entrySet()) {
                    this.hourMap.put(new IntWritable(entry.getKey()), new IntWritable(entry.getValue().size()));
                }
                this.v.setValue(this.hourMap);//小时 对应的uuid的长度

                // 输出操作
                context.write(key, this.v);
            } else {
                // 计算active user,分别是stats_user和stats_device_browser表上
                // 将uuid添加到set集合中去，方便进行统计uuid的去重个数
                for (TimeOutputValue value : values) {
                    this.set.add(value.getId());
                }
                // 设置kpi
                this.v.setKpi(KpiType.valueOfKpiName(key.getStatsCommonDimension().getKpi().getKpiName()));
                // 设置value
                this.map.put(new IntWritable(-1), new IntWritable(this.set.size()));
                this.v.setValue(this.map);
                // 进行输出
                context.write(key, this.v);
            }
        } finally {
            // 清空操作
            this.set.clear();
            this.map.clear();
            this.hourMap.clear();
            this.hours.clear();
            // 初始化操作
            for (int i = 0; i < 24; i++) {
                this.hourMap.put(new IntWritable(i), new IntWritable(0));
                this.hours.put(i, new HashSet<String>());
            }
        }

    }
    }

