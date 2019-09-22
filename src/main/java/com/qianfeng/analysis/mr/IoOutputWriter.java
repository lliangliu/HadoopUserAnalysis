package com.qianfeng.analysis.mr;

import com.qianfeng.analysis.model.base.BaseDimension;
import com.qianfeng.analysis.model.value.StatsOutputValue;
import com.qianfeng.analysis.mr.service.IDimension;
import org.apache.hadoop.conf.Configuration;

import java.sql.PreparedStatement;

//操作结果表中的一个接口
public interface IoOutputWriter {
    /**
     * 为每一个kpi的最终结果赋值的接口
     */
    void output(Configuration conf,
                BaseDimension key,
                StatsOutputValue statsOutputValue,
                PreparedStatement ps,
                IDimension iDimension);
}
