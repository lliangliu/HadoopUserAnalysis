package com.qianfeng.etl.util;

import com.qianfeng.common.Constants;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogParserUtil {
    /**
     * 解析日志
     * 1--IP
     * 2--时间戳
     * 3--主机
     * 4--参数列表
     */
    private static Logger logger=Logger.getLogger(LogParserUtil.class);
    public static Map<String,String> parseLog(String log){
        Map<String,String> map=new ConcurrentHashMap<>();
        if(StringUtils.isNotEmpty(log)){
            //拆分日志
            String[]fields=log.split("\\^A");
            if(fields.length==4){
                //IP段
                map.put(Constants.LOG_IP,fields[0]);
                map.put(Constants.LOG_SERVER_TIME,fields[1].replaceAll("\\.",""));
                //参数列表，单独定义方法处理
                String params=fields[3];
                //处理参数列表的方法
                handleParam(params,map);
                //处理ip
                handleIp(map);
                //处理浏览器的信息
                handleAgent(map);

            }
        }
        return map;

    }

    /**
     * 处理浏览器信息
     * @param map
     */
    private static void handleAgent(Map<String,String>map){
        if(map.containsKey(Constants.LOG_USERAGENT)){
            UserAgnetParserUtil.AgentInfo info = UserAgnetParserUtil.parserUserAgent(map.get(Constants.LOG_USERAGENT));
            map.put(Constants.LOG_BROWSER_NAME,info.getBrowserName());
            map.put(Constants.LOG_BROWSER_VERSION,info.getBrowserVersion());
            map.put(Constants.LOG_OS_NAME,info.getOsName());
            map.put(Constants.LOG_OS_VERSION,info.getOsVersion());
        }
    }


//处理IP
    private static void handleIp(Map<String,String> map){
        if(map.containsKey(Constants.LOG_IP)){
            //调用ip处理的方法  获取 国家 省 区
            IpPaerserUtil.RegionInfo info=IpPaerserUtil.ipParser(map.get(Constants.LOG_IP));
            map.put(Constants.LOG_COUNTRY,info.getCountry());
            map.put(Constants.LOG_PROVINCE,info.getProvince());
            map.put(Constants.LOG_CITY,info.getCity());
        }
    }
//处理参数列表。将参数列表放入map集合中
    private static void handleParam(String params,Map<String,String>map){
        if(StringUtils.isNotEmpty(params)){
            int index=params.indexOf("?");
            if(index>0){
                //先截取再拆分，拆分的结果是键值对（字符串）的数组
                String[] fields = params.substring(index + 1).split("&");
                for (String field :fields) {
                    String[]kvs=field.split("=");
                    String k=kvs[0];
                    String v=null;
                    try {
                        v= URLDecoder.decode(kvs[1],"utf-8");
                    } catch (UnsupportedEncodingException e) {
                        logger.error("url解码异常",e);
                    }

                    if(StringUtils.isNotEmpty(k)){
                        map.put(k,v);
                    }
                }
            }
        }
    }
}
