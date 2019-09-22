package com.qianfeng.analysis.model.base;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class CurrencyTypeDimension extends BaseDimension {
    private int id;
    private String currencyName;
    @Override
    public int compareTo(BaseDimension o) {
        if(this==o){
            return 0;
        }
        CurrencyTypeDimension other=(CurrencyTypeDimension)o;
        int tmp=this.id-other.id;
        if(tmp!=0){
            return tmp;
        }
        return this.currencyName.compareTo(other.currencyName);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.id);
        dataOutput.writeUTF(this.currencyName);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id=dataInput.readInt();
        this.currencyName=dataInput.readUTF();
    }

    public CurrencyTypeDimension() {
    }

    public CurrencyTypeDimension(String currencyName) {
        this.currencyName = currencyName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyTypeDimension that = (CurrencyTypeDimension) o;
        return id == that.id &&
                Objects.equals(currencyName, that.currencyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, currencyName);
    }

    @Override
    public String toString() {
        return "CurrencyTypeDimension{" +
                "id=" + id +
                ", currencyName='" + currencyName + '\'' +
                '}';
    }
}
