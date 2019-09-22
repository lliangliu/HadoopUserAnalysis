package com.qianfeng.analysis.hive;

import com.qianfeng.analysis.model.base.EventDimension;
import com.qianfeng.analysis.mr.service.IDimension;
import com.qianfeng.analysis.mr.service.impl.IDimensionImpl;
import com.qianfeng.common.GlobalConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * 事件维度的id
 */
public class EventDimensionUdf extends UDF {
    IDimension iDimension=new IDimensionImpl();
    public int evaluate(String category,String action){
        if(StringUtils.isEmpty(category)||category.equals("null")){
            category=action= GlobalConstants.DEFAULT_VALUE;
        }
        if(StringUtils.isEmpty(action)||action.equals("null")){
            action=GlobalConstants.DEFAULT_VALUE;
        }
        int id=-1;
        EventDimension ed=new EventDimension(category,action);
        id=iDimension.getIDimensionImplByDimension(ed);
        return id;
    }

    public static void main(String[] args) {
        System.out.println(new EventDimensionUdf().evaluate("订单事件1","%E4%B8%8B%E5%8D%95%E6%93%8D%E4%BD%9C"));
    }
}
