package com.qianfeng.analysis.hive;

import com.qianfeng.analysis.model.base.DateDimension;
import com.qianfeng.analysis.mr.service.IDimension;
import com.qianfeng.analysis.mr.service.impl.IDimensionImpl;

import com.qianfeng.common.DateEnum;
import com.qianfeng.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;

public class DateDimensionUdf extends UDF {
    IDimension iDimension=new IDimensionImpl();
    public int evaluate(String date){
        if (StringUtils.isEmpty(date)||date.equals("null")){
            date= TimeUtil.getYesterday();
        }
        int id=-1;
        DateDimension dt=DateDimension.buildDate(TimeUtil.parseString2Long(date), DateEnum.DAY);
       id=iDimension.getIDimensionImplByDimension(dt);
        return id;
    }

    public static void main(String[] args) {
        System.out.println(new DateDimensionUdf().evaluate("2018-11-12"));
    }
}
