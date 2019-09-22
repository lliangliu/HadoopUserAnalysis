package com.qianfeng.analysis.mr.ha;

import com.qianfeng.analysis.model.base.BaseDimension;
import com.qianfeng.analysis.model.key.StatsUserDimension;
import com.qianfeng.analysis.model.value.OutputMapWritable;
import com.qianfeng.analysis.model.value.StatsOutputValue;
import com.qianfeng.analysis.mr.IoOutputWriter;
import com.qianfeng.analysis.mr.service.IDimension;
import com.qianfeng.common.GlobalConstants;
import com.qianfeng.common.KpiType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HourlyActiveUserOutputWriter implements IoOutputWriter {
    @Override
    public void output(Configuration conf, BaseDimension key, StatsOutputValue statsOutputValue, PreparedStatement ps, IDimension iDimension) {

        try {
            StatsUserDimension k= (StatsUserDimension)key;
            OutputMapWritable value = (OutputMapWritable)statsOutputValue;//reduce端的输出
            MapWritable map = value.getValue();
            //获取新增用户值
            int activeUser=((IntWritable)(value.getValue().get(new IntWritable(1)))).get();
            int i=0;
            ps.setInt(++i,iDimension.getIDimensionImplByDimension(k.getStatsCommonDimension().getDt()));//获取传入对象的id
            ps.setInt(++i,iDimension.getIDimensionImplByDimension(k.getStatsCommonDimension().getPl()));
            ps.setInt(++i,iDimension.getIDimensionImplByDimension(k.getStatsCommonDimension().getKpi()));
            // 设置每个小时的情况
            for (i++; i < 28; i++) {
                int v = ((IntWritable)map.get(new IntWritable(i - 4))).get();
                ps.setInt(i, v);
                ps.setInt(i + 25, v);
            }
            ps.setString(i, conf.get(GlobalConstants.RUNNING_DATE));
            ps.addBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
