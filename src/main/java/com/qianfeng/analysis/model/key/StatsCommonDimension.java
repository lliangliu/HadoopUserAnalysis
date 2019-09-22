package com.qianfeng.analysis.model.key;

import com.qianfeng.analysis.model.base.BaseDimension;
import com.qianfeng.analysis.model.base.DateDimension;
import com.qianfeng.analysis.model.base.KpiDimension;
import com.qianfeng.analysis.model.base.PlatformDimension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

/**
 * 公共维度类
 */
public class StatsCommonDimension extends BaseDimension {
    public PlatformDimension pl = new PlatformDimension();
    public DateDimension dt = new DateDimension();
    public KpiDimension kpi = new KpiDimension();
    @Override
    public int compareTo(BaseDimension o) {
        if(this==o){
            return 0;
        }
        StatsCommonDimension other = (StatsCommonDimension)o;
        int tmp=this.pl.compareTo(other.pl);
        if(tmp!=0){
            return tmp;
        }
        tmp = this.dt.compareTo(other.dt);
        if(tmp != 0){
            return tmp;
        }
        return this.kpi.compareTo(other.kpi);

    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        this.pl.write(dataOutput);
        this.dt.write(dataOutput);
        this.kpi.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.pl.readFields(dataInput);
        this.dt.readFields(dataInput);
        this.kpi.readFields(dataInput);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatsCommonDimension that = (StatsCommonDimension) o;
        return Objects.equals(pl, that.pl) &&
                Objects.equals(dt, that.dt) &&
                Objects.equals(kpi, that.kpi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pl, dt, kpi);
    }

    @Override
    public String toString() {
        return "StatsCommonDimension{" +
                "pl=" + pl +
                ", dt=" + dt +
                ", kpi=" + kpi +
                '}';
    }

    public PlatformDimension getPl() {
        return pl;
    }

    public void setPl(PlatformDimension pl) {
        this.pl = pl;
    }

    public DateDimension getDt() {
        return dt;
    }

    public void setDt(DateDimension dt) {
        this.dt = dt;
    }

    public KpiDimension getKpi() {
        return kpi;
    }

    public void setKpi(KpiDimension kpi) {
        this.kpi = kpi;
    }

}
