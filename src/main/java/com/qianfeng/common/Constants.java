package com.qianfeng.common;

/**
 * @Description 定义相关常量
 * @Author cqh <caoqingghai@1000phone.com>
 * @Version V1.0
 * @Since 1.0
 * @Date 2018/11/28 14：45
 */
public class Constants {
    public enum EventEnum{
        LANUCH(1,"lanuch event","e_l"),
        PAGEVIEW(2,"pageview event","e_pv"),
        EVENT(3,"event name","e_e"),
        CHARGEREQUEST(4,"charge request event","e_crt"),//订单的总量类型事件
        CHARGESUCCESS(5,"charge success","e_cs"),//订单成功类型事件
        CHARGEREFUND(6,"charge refund","e_cr")
        ;//退款成功类型事件
        public int id;
        public String name;
        public String alias;

        EventEnum(int id,String name,String alias){
            this.id = id;
            this.name = name;
            this.alias = alias;
        }

        //根据别名获取对应的枚举类型
        public static EventEnum valuesOfAlias(String alia){
            for (EventEnum event:values()) {
                if(event.alias.equals(alia)){
                    return event;
                }
            }
            throw new RuntimeException("没有对应的事件类型");
        }
    }

    public static final String LOG_VERSION = "ver";

    public static final String LOG_SERVER_TIME = "s_time";

    public static final String LOG_EVENT_NAME = "en";//事件类型

    public static final String LOG_UUID = "u_ud";

    public static final String LOG_MEMBER_ID = "u_mid";

    public static final String LOG_SESSION_ID = "u_sd";

    public static final String LOG_CLIENT_TIME = "c_time";

    public static final String LOG_LANGUAGE = "l";

    public static final String LOG_USERAGENT = "b_iev";

    public static final String LOG_RESOLUTION = "b_rst";

    public static final String LOG_CURRENT_URL = "p_url";

    public static final String LOG_PREFFER_URL = "p_ref";

    public static final String LOG_TITLE = "tt";

    public static final String LOG_PLATFORM = "pl";

    public static final String LOG_IP = "ip";


    /**
     * 和订单相关
     */
    public static final String LOG_ORDER_ID = "oid";

    public static final String LOG_ORDER_NAME = "on";

    public static final String LOG_CURRENCY_AMOUTN = "cua";

    public static final String LOG_CURRENCY_TYPE = "cut";

    public static final String LOG_PAYMENT_TYPE = "pt";


    /**
     * 事件相关
     * 点击：点击事件 category转发
     * 下单：下单事件
     */
    public static final String LOG_EVENT_CATEGORY = "ca";

    public static final String LOG_EVENT_ACTION = "ac";

    public static final String LOG_EVENT_KV = "kv_";

    public static final String LOG_EVENT_DURATION = "du";

    /**
     * 浏览器相关
     */

    public static final String LOG_BROWSER_NAME = "browserName";

    public static final String LOG_BROWSER_VERSION = "browserVersion";

    public static final String LOG_OS_NAME = "osName";

    public static final String LOG_OS_VERSION = "osVersion";
    /**
     * 地域相关
     */

    public static final String LOG_COUNTRY = "country";

    public static final String LOG_PROVINCE = "province";

    public static final String LOG_CITY = "city";

}