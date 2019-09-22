package com.qianfeng.analysis.model.value;

import com.qianfeng.common.KpiType;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.WritableUtils;
/**
 * reduce 阶段输出的value
 */
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class OutputMapWritable extends StatsOutputValue {
    private KpiType kpi;//为了通过kpi得到连接到那个数据库的数据表（用户新增 浏览器新增） 以及将数据插入到哪张数据表中
    private MapWritable value=new MapWritable();//将reduce端传来的数据序列化
    @Override
    public KpiType getKpi() {
        return this.kpi;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
    //注意枚举类型的写法
        WritableUtils.writeEnum(dataOutput,kpi);
        this.value.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
    WritableUtils.readEnum(dataInput,KpiType.class);
    this.value.readFields(dataInput);
    }

    public void setKpi(KpiType kpi) {
        this.kpi = kpi;
    }

    public MapWritable getValue() {
        return value;
    }

    public void setValue(MapWritable value) {
        this.value = value;
    }
}
