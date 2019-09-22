package com.qianfeng.analysis.model.value;

import com.qianfeng.common.KpiType;
import org.apache.commons.lang.StringUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IdOutputValue extends StatsOutputValue {
        private String uuid; // 用户唯一标识符
        private String sid; // 会话id
        private String umid;//新增会员id

        public IdOutputValue(){

        }

        public IdOutputValue(String uuid, String sid,String umid) {
            this.uuid = uuid;
            this.sid = sid;
            this.umid=umid;
        }

    public String getUmid() {
        return umid;
    }

    public void setUmid(String umid) {
        this.umid = umid;
    }

    public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getSid() {
            return sid;
        }

        public void setSid(String sid) {
            this.sid = sid;
        }

        @Override
        public KpiType getKpi() {
            return null;
        }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(this.uuid);
        dataOutput.writeUTF(this.sid);
        dataOutput.writeUTF(this.umid);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.uuid=dataInput.readUTF();
        this.sid=dataInput.readUTF();
        this.umid=dataInput.readUTF();
    }
}

