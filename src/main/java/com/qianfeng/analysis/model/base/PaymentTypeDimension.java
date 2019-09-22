package com.qianfeng.analysis.model.base;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class PaymentTypeDimension extends BaseDimension {
    private int id;
    private String paymentType;
    @Override
    public int compareTo(BaseDimension o) {
        if(this==o){
            return 0;
        }
      PaymentTypeDimension other=(PaymentTypeDimension) o;
        int tmp=this.id-other.id;
        if(tmp!=0){
            return tmp;
        }
        return this.paymentType.compareTo(other.paymentType);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
            dataOutput.write(this.id);
            dataOutput.writeUTF(this.paymentType);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id=dataInput.readInt();
        this.paymentType=dataInput.readUTF();
    }

    public PaymentTypeDimension() {
    }

    public PaymentTypeDimension(String paymentType) {
        this.paymentType = paymentType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentTypeDimension that = (PaymentTypeDimension) o;
        return id == that.id &&
                Objects.equals(paymentType, that.paymentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paymentType);
    }

    @Override
    public String toString() {
        return "PaymentTypeDimension{" +
                "id=" + id +
                ", paymentType='" + paymentType + '\'' +
                '}';
    }
}
