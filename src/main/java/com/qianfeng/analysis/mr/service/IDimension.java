package com.qianfeng.analysis.mr.service;

import com.qianfeng.analysis.model.base.BaseDimension;

//根据传入维度对象获取维度的id
public interface IDimension {
    int getIDimensionImplByDimension(BaseDimension baseDimension);
}
