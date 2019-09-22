package com.qianfeng.analysis.hive;

import com.qianfeng.analysis.model.base.PlatformDimension;
import com.qianfeng.analysis.mr.service.IDimension;
import com.qianfeng.analysis.mr.service.impl.IDimensionImpl;
import com.qianfeng.common.GlobalConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * 获取平台维度id
 */
public class PlatformDimensionUdf extends UDF {
    IDimension iDimension=new IDimensionImpl();
    public int evaluate(String platform){
        if(StringUtils.isEmpty(platform)||platform.equals("null")){
            platform= GlobalConstants.RUNNING_DATE;
        }
        int id=-1;
        PlatformDimension pl=new PlatformDimension(platform);
        id=iDimension.getIDimensionImplByDimension(pl);
        return id;
    }

    public static void main(String[] args) {
        System.out.println(new PlatformDimensionUdf().evaluate("web"));
    }
}
