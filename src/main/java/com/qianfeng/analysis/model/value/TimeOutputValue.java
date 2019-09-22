package com.qianfeng.analysis.model.value;

import com.qianfeng.common.KpiType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 封装map端输出的类型 整型 或字符串
 */
public class TimeOutputValue extends StatsOutputValue{
    private String id;//泛指 可以是uuid 可以是u_mid，可以是s_id
    private long time;//时间戳 时长...

    @Override
    public KpiType getKpi() {
        return null;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(this.id);
        dataOutput.writeLong(this.time);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id = dataInput.readUTF();
        this.time = dataInput.readLong();
    }

    public TimeOutputValue(String id, long time) {
        this.id = id;
        this.time = time;
    }

    public TimeOutputValue() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
