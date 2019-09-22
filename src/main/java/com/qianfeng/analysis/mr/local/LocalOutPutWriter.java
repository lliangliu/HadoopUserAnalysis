package com.qianfeng.analysis.mr.local;

import com.qianfeng.analysis.model.base.BaseDimension;
import com.qianfeng.analysis.model.key.StatsUserLcDimension;
import com.qianfeng.analysis.model.value.OutputMapWritable;
import com.qianfeng.analysis.model.value.StatsOutputValue;
import com.qianfeng.analysis.mr.IoOutputWriter;
import com.qianfeng.analysis.mr.service.IDimension;
import com.qianfeng.common.GlobalConstants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LocalOutPutWriter implements IoOutputWriter {
    @Override
    public void output(Configuration conf, BaseDimension key, StatsOutputValue statsOutputValue, PreparedStatement ps, IDimension iDimension) {
        try {
            StatsUserLcDimension k=(StatsUserLcDimension)key;
            OutputMapWritable v=(OutputMapWritable)statsOutputValue;//reduce的输出端
            int newUser=((IntWritable)(v.getValue().get(new IntWritable(1)))).get();
            int session=((IntWritable)(v.getValue().get(new IntWritable(2)))).get();
            int bounce_sessions=((IntWritable)(v.getValue().get(new IntWritable(3)))).get();
            int new_members=((IntWritable)(v.getValue().get(new IntWritable(4)))).get();
            int i=0;
            ps.setInt(++i,iDimension.getIDimensionImplByDimension(k.getStatsCommonDimension().getDt()));
            ps.setInt(++i,iDimension.getIDimensionImplByDimension(k.getStatsCommonDimension().getPl()));
            ps.setInt(++i,iDimension.getIDimensionImplByDimension(k.getLocalDimension()));
            ps.setInt(++i,newUser);
            ps.setInt(++i,session);
            ps.setInt(++i,bounce_sessions);
            ps.setInt(++i,new_members);
            ps.setString(++i, conf.get(GlobalConstants.RUNNING_DATE));
            ps.setInt(++i,newUser);
            ps.setInt(++i,session);
            ps.setInt(++i,bounce_sessions);
            ps.addBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
