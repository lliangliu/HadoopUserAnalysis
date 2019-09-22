package com.qianfeng.analysis.model.key;

import com.qianfeng.analysis.model.base.BaseDimension;
import com.qianfeng.analysis.model.base.BrowserDimension;
import com.qianfeng.analysis.model.base.LocalDimension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class StatsUserLcDimension extends BaseDimension {
/**
 * 用于用户模块和地域模块map端和reduce端输出的key
 */

    private LocalDimension localDimension=new LocalDimension();
    private StatsCommonDimension statsCommonDimension=new StatsCommonDimension();

    public StatsUserLcDimension() {
    }
    public StatsUserLcDimension( LocalDimension localDimension, StatsCommonDimension statsCommonDimension) {
        this.localDimension = localDimension;
        this.statsCommonDimension = statsCommonDimension;
    }
    @Override
    public int compareTo(BaseDimension o) {
        if(this==o){
            return 0;
        }
        StatsUserLcDimension other=(StatsUserLcDimension)o;
        int tmp=this.localDimension.compareTo(other.localDimension);
        if(tmp!=0){
            return tmp;
        }
        return this.statsCommonDimension.compareTo(other.statsCommonDimension);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        this.localDimension.write(dataOutput);
        this.statsCommonDimension.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.localDimension.readFields(dataInput);
        this.statsCommonDimension.readFields(dataInput);
    }

    public LocalDimension getLocalDimension() {
        return localDimension;
    }

    public void setLocalDimension(LocalDimension localDimension) {
        this.localDimension = localDimension;
    }

    public StatsCommonDimension getStatsCommonDimension() {
        return statsCommonDimension;
    }

    public void setStatsCommonDimension(StatsCommonDimension statsCommonDimension) {
        this.statsCommonDimension = statsCommonDimension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatsUserLcDimension that = (StatsUserLcDimension) o;
        return Objects.equals(localDimension, that.localDimension) &&
                Objects.equals(statsCommonDimension, that.statsCommonDimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localDimension, statsCommonDimension);
    }

    @Override
    public String toString() {
        return "StatsUserLcDimension{" +
                "localDimension=" + localDimension +
                ", statsCommonDimension=" + statsCommonDimension +
                '}';
    }

}
