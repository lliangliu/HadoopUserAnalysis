package com.qianfeng.analysis.model.base;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

import com.qianfeng.common.GlobalConstants;
import org.apache.commons.lang.StringUtils;
public class PlatformDimension extends BaseDimension {
    private int id;
    private String platform;

    public PlatformDimension() {
    }
    public PlatformDimension(String platform) {
        this.platform = platform;
    }
    public PlatformDimension(int id, String platform) {
        this.id = id;
        this.platform = platform;
    }
    public static PlatformDimension getInstance(String platformName){
        String pl = StringUtils.isEmpty(platformName)? GlobalConstants.DEFAULT_VALUE:platformName;
        return new PlatformDimension(pl);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
    dataOutput.writeInt(this.id);
    dataOutput.writeUTF(this.platform);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
    this.id=dataInput.readInt();
    this.platform=dataInput.readUTF();
    }

    @Override
    public int compareTo(BaseDimension o) {
        if(this==o){
            return 0;
        }
        PlatformDimension other=(PlatformDimension)o;
        int tmp=this.id-other.id;
        if(tmp!=0){
            return tmp;
        }
        return this.platform.compareTo(other.platform);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlatformDimension that = (PlatformDimension) o;
        return id == that.id &&
                Objects.equals(platform, that.platform);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, platform);
    }

    @Override
    public String toString() {
        return "PlatformDimension{" +
                "id=" + id +
                ", platform='" + platform + '\'' +
                '}';
    }
}

