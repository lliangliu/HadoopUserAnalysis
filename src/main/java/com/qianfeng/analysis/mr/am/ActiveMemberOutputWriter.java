package com.qianfeng.analysis.mr.am;

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

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ActiveMemberOutputWriter implements IoOutputWriter {
    @Override
    public void output(Configuration conf, BaseDimension key, StatsOutputValue statsOutputValue, PreparedStatement ps, IDimension iDimension) {
        try {
            StatsUserDimension k= (StatsUserDimension)key;
            OutputMapWritable value = (OutputMapWritable)statsOutputValue;//reduce端的输出
            //获取活跃会员值
            int activeMember=((IntWritable)(value.getValue().get(new IntWritable(1)))).get();
            int i=0;
            ps.setInt(++i,iDimension.getIDimensionImplByDimension(k.getStatsCommonDimension().getDt()));//获取传入对象的id
            ps.setInt(++i,iDimension.getIDimensionImplByDimension(k.getStatsCommonDimension().getPl()));
            if(value.getKpi().equals(KpiType.BROWSER_ACTIVE_MEMBER)){
                ps.setInt(++i,iDimension.getIDimensionImplByDimension(k.getBrowserDimension()));
            }
            //需要在runner类 在运行赋值的时候设置
            ps.setInt(++i,activeMember);
            ps.setString(++i,conf.get(GlobalConstants.RUNNING_DATE));
            ps.setInt(++i,activeMember);
            ps.addBatch();//添加到批处理中。
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
