package com.qianfeng.analysis.hive;

import com.qianfeng.analysis.model.base.PaymentTypeDimension;
import com.qianfeng.analysis.mr.service.IDimension;
import com.qianfeng.analysis.mr.service.impl.IDimensionImpl;
import com.qianfeng.common.GlobalConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;

public class PaymentTypeDimensionUdf extends UDF {
    IDimension iDimension=new IDimensionImpl();

    public int evaluate(String paymentTypeName){
        if(StringUtils.isEmpty(paymentTypeName)||paymentTypeName.equals("null")){
            paymentTypeName= GlobalConstants.DEFAULT_VALUE;
        }
        int id=-1;
        PaymentTypeDimension pt=new PaymentTypeDimension(paymentTypeName);
        id=iDimension.getIDimensionImplByDimension(pt);
        return id;
    }
    public static void main(String[] args) {
        System.out.println(new PaymentTypeDimensionUdf().evaluate("支付1宝"));
    }
}
