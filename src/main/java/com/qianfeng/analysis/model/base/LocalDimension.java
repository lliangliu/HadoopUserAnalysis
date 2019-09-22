package com.qianfeng.analysis.model.base;

import com.qianfeng.common.GlobalConstants;
import org.apache.commons.lang.StringUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class LocalDimension extends BaseDimension {
    private int id;
    private String country;
    private String province;
    private String city;

    public LocalDimension() {
    }

    public LocalDimension(int id, String country, String province, String city) {
        this.id = id;
        this.country = country;
        this.province = province;
        this.city = city;
    }
    public LocalDimension(String country, String province, String city) {
        this.country = country;
        this.province = province;
        this.city = city;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public int compareTo(BaseDimension o) {
        if(this==o){
            return 0;
        }
        LocalDimension other=(LocalDimension)o;
        int tmp=this.id-other.id;
        if(tmp!=0){
            return tmp;
        }
        tmp=this.country.compareTo(other.country);
        if(tmp!=0){
            return tmp;
        }
        tmp=this.province.compareTo(other.province);
        if(tmp!=0){
            return tmp;
        }
        return this.city.compareTo(other.city);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.id);
        dataOutput.writeUTF(this.country);
        dataOutput.writeUTF(this.province);
        dataOutput.writeUTF(this.city);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id=dataInput.readInt();
        this.country=dataInput.readUTF();
        this.province=dataInput.readUTF();
        this.city=dataInput.readUTF();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalDimension that = (LocalDimension) o;
        return id == that.id &&
                Objects.equals(country, that.country) &&
                Objects.equals(province, that.province) &&
                Objects.equals(city, that.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, country, province, city);
    }

    @Override
    public String toString() {
        return "LocalDimension{" +
                "id=" + id +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
    //创建地域的维度对象
    public static LocalDimension getInstance(String country, String province, String city){
        String coun = StringUtils.isEmpty(country)? GlobalConstants.DEFAULT_VALUE:country;
        String prov = StringUtils.isEmpty(province)? GlobalConstants.DEFAULT_VALUE:province;
        String ci = StringUtils.isEmpty(city)? GlobalConstants.DEFAULT_VALUE:city;
        return new LocalDimension(coun,prov,ci);
    }
}
