package com.qianfeng.analysis.hive;

import com.qianfeng.analysis.model.base.CurrencyTypeDimension;
import com.qianfeng.analysis.mr.service.IDimension;
import com.qianfeng.analysis.mr.service.impl.IDimensionImpl;
import com.qianfeng.common.GlobalConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;

public class CurrencyTypeDimensionUdf extends UDF {
    IDimension iDimension=new IDimensionImpl();
    public int evaluate(String  currencyTypeNmae){
        if(StringUtils.isEmpty(currencyTypeNmae)||currencyTypeNmae.equals("null")){
            currencyTypeNmae= GlobalConstants.DEFAULT_VALUE;
        }
        int id=-1;
        CurrencyTypeDimension ct=new CurrencyTypeDimension(currencyTypeNmae);
        id=iDimension.getIDimensionImplByDimension(ct);
        return id;
    }

    public static void main(String[] args) {
        System.out.println(new CurrencyTypeDimensionUdf().evaluate("eqw"));
    }
}
