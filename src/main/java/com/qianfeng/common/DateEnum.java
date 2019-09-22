package com.qianfeng.common;

/**
 * @Description 时间的枚举类型
 * @Author cqh <caoqingghai@1000phone.com>
 * @Version V1.0
 * @Since 1.0
 * @Date 2018/11/9 01: 20
 */
public enum  DateEnum {
    YEAR("year"),
    SEASON("season"),
    MONTH("month"),
    WEEK("week"),
    DAY("day"),
    HOUR("hour")
    ;

    public String dateType;

    DateEnum(String dateType) {
        this.dateType = dateType;
    }

    /**
     * 根据type返回对应的枚举
     * @param type
     * @return
     */
    public static DateEnum valueOfDateType(String type){
        for (DateEnum date : values()){
            if(date.dateType.equals(type)){
                return date;
            }
        }
        throw new RuntimeException("没有对应的kpi类型");
    }
}