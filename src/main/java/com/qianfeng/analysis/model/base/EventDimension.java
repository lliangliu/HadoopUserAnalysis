package com.qianfeng.analysis.model.base;

import com.qianfeng.common.GlobalConstants;
import org.apache.commons.lang.StringUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class EventDimension extends BaseDimension {
    private int id;
    private String category;
    private String action;

    public EventDimension() {
    }

    public EventDimension(int id, String category, String action) {
        this.id = id;
        this.category = category;
        this.action = action;
    }

    public EventDimension(String category, String action) {
        this.category = category;
        this.action = action;
    }

    @Override
    public int compareTo(BaseDimension o) {
        if(this==o){
            return 0;
        }
        EventDimension other=(EventDimension)o;
        int tmp=this.id-other.id;
        if(tmp!=0){
            return tmp;
        }
        tmp=this.category.compareTo(other.category);
        if(tmp!=0){
            return tmp;
        }
        return this.action.compareTo(other.action);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.id);
        dataOutput.writeUTF(this.category);
        dataOutput.writeUTF(this.action);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id=dataInput.readInt();
        this.category=dataInput.readUTF();
        this.action=dataInput.readUTF();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventDimension that = (EventDimension) o;
        return id == that.id &&
                Objects.equals(category, that.category) &&
                Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, category, action);
    }

    @Override
    public String toString() {
        return "EventDimension{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
    public static EventDimension getInstance(String category,String action){
        String cate= StringUtils.isEmpty(category)? GlobalConstants.DEFAULT_VALUE:category;
        String act=StringUtils.isEmpty(action)?GlobalConstants.DEFAULT_VALUE:action;
        return new EventDimension(cate,act);
    }
}
