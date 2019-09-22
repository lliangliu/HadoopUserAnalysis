package com.qianfeng.analysis.model.value;
/**
 * 封装map或者reduce输出的value的顶级类
 */

import com.qianfeng.common.KpiType;
import org.apache.hadoop.io.Writable;

public abstract  class StatsOutputValue implements Writable {
    //获取kpi的抽象方法,决定了reduce输出的结果写入到那张表中的那个字段
    //以便于自定义输出到mysql的格式化类区别数据要输出到哪张表的那个字段
    public abstract KpiType getKpi();
}
