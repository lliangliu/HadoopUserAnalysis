package com.qianfeng.analysis.model.key;

import com.qianfeng.analysis.model.base.BaseDimension;
import com.qianfeng.analysis.model.base.BrowserDimension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

/**
 * 用于用户模块和浏览器模块map端和reduce端输出的key
 */
public class StatsUserDimension extends BaseDimension {
    private BrowserDimension browserDimension=new BrowserDimension();
    private StatsCommonDimension statsCommonDimension=new StatsCommonDimension();

    public StatsUserDimension() {
    }
    public StatsUserDimension(BrowserDimension browserDimension, StatsCommonDimension statsCommonDimension) {
        this.browserDimension = browserDimension;
        this.statsCommonDimension = statsCommonDimension;
    }
    @Override
    public int compareTo(BaseDimension o) {
        if(this==o){
            return 0;
        }
        StatsUserDimension other=(StatsUserDimension)o;
        int tmp=this.browserDimension.compareTo(other.browserDimension);
        if(tmp!=0){
            return tmp;
        }
        return this.statsCommonDimension.compareTo(other.statsCommonDimension);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
    this.browserDimension.write(dataOutput);
    this.statsCommonDimension.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
    this.browserDimension.readFields(dataInput);
    this.statsCommonDimension.readFields(dataInput);
    }

    public BrowserDimension getBrowserDimension() {
        return browserDimension;
    }

    public void setBrowserDimension(BrowserDimension browserDimension) {
        this.browserDimension = browserDimension;
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
        StatsUserDimension that = (StatsUserDimension) o;
        return Objects.equals(browserDimension, that.browserDimension) &&
                Objects.equals(statsCommonDimension, that.statsCommonDimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(browserDimension, statsCommonDimension);
    }

    @Override
    public String toString() {
        return "StatsUserDimension{" +
                "browserDimension=" + browserDimension +
                ", statsCommonDimension=" + statsCommonDimension +
                '}';
    }
}
