package com.qianfeng.analysis.model.base;


import org.apache.hadoop.io.WritableComparable;

/**
 * 所有基础维度类的顶级父类
 * 规定所有实现这个类的子类序列化
 */
public abstract  class BaseDimension implements WritableComparable<BaseDimension> {
}
