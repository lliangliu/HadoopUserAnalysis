package com.qianfeng.etl.mr;

import com.qianfeng.common.Constants;
import com.qianfeng.etl.util.LogParserUtil;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import java.io.IOException;
import java.util.Map;

public class MapperDemo extends Mapper<LongWritable, Text,LogWritable, NullWritable> {
    private static Logger logger = Logger.getLogger(MapperDemo.class);
    private static LogWritable k = new LogWritable();
    private static int inputRecords,filterRecords,outputRecords;

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        //记录了输入了多少行数据
        inputRecords++;
        if(StringUtils.isEmpty(line)){
            //过滤了多少条数据
            filterRecords++;
            return;
        }
        Map<String,String>map = LogParserUtil.parseLog(line);
        //获取事件名称，根据事件名字不同进行多文件输出
        //根据en 获取对应事件的名称
        //e_e e_l
        String eventName = map.get(Constants.LOG_EVENT_NAME);
        //根据事件的别名获取对应的枚举类型
        Constants.EventEnum event = Constants.EventEnum.valuesOfAlias(eventName);
        //可以根据事件类型不同多文件输出，但是这里没有做
        switch (event){
            case LANUCH:
            case PAGEVIEW:
            case EVENT:
            case CHARGEREQUEST:
            case CHARGESUCCESS:
            case CHARGEREFUND:
                //这个方法是将map中的数据赋值给LogWritable,然后使用context输出
                handleLog(map,context);
                break;
            default:
                break;
        }
    }

    /**
     * 处理map中的数据，赋值给LogWritable.
     * @param map
     * @param context
     */
    private void handleLog(Map<String, String> map, Context context) {
        for (Map.Entry<String,String>en:map.entrySet()) {
            //循环map，取出的每一个键值对，不知道具体是哪儿个吧
            //所以判断。
            //如果key是ip ,取出对应的value,将value赋值给k的ip属性
            //k.setIp(en.getValue());
            switch (en.getKey()){
                case "ver" : this.k.setVer(en.getValue());break;
                case "s_time" : this.k.setS_time(en.getValue());break;
                case "en" : this.k.setEn(en.getValue());break;
                case "u_ud" : this.k.setU_ud(en.getValue());break;
                case "u_mid" : this.k.setU_mid(en.getValue());break;
                case "u_sd" : this.k.setU_sd(en.getValue());break;
                case "c_time" : this.k.setC_time(en.getValue());break;
                case "l" : this.k.setL(en.getValue());break;
                case "b_iev" : this.k.setB_iev(en.getValue());break;
                case "b_rst" : this.k.setB_rst(en.getValue());break;
                case "p_url" : this.k.setP_url(en.getValue());break;
                case "p_ref" : this.k.setP_ref(en.getValue());break;
                case "tt" : this.k.setTt(en.getValue());break;
                case "pl" : this.k.setPl(en.getValue());break;
                case "ip" : this.k.setIp(en.getValue());break;
                case "oid" : this.k.setOid(en.getValue());break;
                case "on" : this.k.setOn(en.getValue());break;
                case "cua" : this.k.setCua(en.getValue());break;
                case "cut" : this.k.setCut(en.getValue());break;
                case "pt" : this.k.setPt(en.getValue());break;
                case "ca" : this.k.setCa(en.getValue());break;
                case "ac" : this.k.setAc(en.getValue());break;
                case "kv_" : this.k.setKv_(en.getValue());break;
                case "du" : this.k.setDu(en.getValue());break;
                case "browserName" : this.k.setBrowserName(en.getValue());break;
                case "browserVersion" : this.k.setBrowserVersion(en.getValue());break;
                case "osName" : this.k.setOsName(en.getValue());break;
                case "osVersion" : this.k.setOsVersion(en.getValue());break;
                case "country" : this.k.setCountry(en.getValue());break;
                case "province" : this.k.setProvince(en.getValue());break;
                case "city" : this.k.setCity(en.getValue());break;
            }

        }
        //this.ou
        try {
            outputRecords++;
            context.write(k,NullWritable.get());
        } catch (Exception e) {
            logger.error("etl输出异常",e);
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        logger.info("输入："+inputRecords+"过滤："+filterRecords+
                "输出："+outputRecords);
    }
}







