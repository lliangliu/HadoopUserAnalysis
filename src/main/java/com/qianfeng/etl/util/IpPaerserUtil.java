package com.qianfeng.etl.util;
import  com.qianfeng.common.GlobalConstants;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @Description :解析ip的类
 * @Author cqh <caoqingghai@1000phone.com>
 * @Version V1.0
 * @Since 1.0
 * @Date 2018/11/28 10：29
 */
public class IpPaerserUtil extends IPSeeker {
    //将信息打印到控制台上
    private static Logger logger  = Logger.getLogger(IpPaerserUtil.class);
    public static RegionInfo regionInfo = new RegionInfo();
    /**
     * 纯真数据库解析ip地址的方法
     */
    public static RegionInfo ipParser(String ip){
        if(StringUtils.isNotEmpty(ip)){
            String country = IPSeeker.getInstance().getCountry(ip);
            if(StringUtils.isNotEmpty(country.trim())){
                if(country.equals("局域网")){
                    System.out.println(country);
                    regionInfo.setCountry("中国");
                    regionInfo.setProvince("北京市");
                    regionInfo.setCity("昌平区");
                }else {
                    int index = country.indexOf("省");
                    if(index>0){
                        regionInfo.setCountry("中国");
                        regionInfo.setProvince(country.substring(0,index+1));
                        int  index2=country.indexOf("市");
                        if(index2>0){
                            regionInfo.setCity(country.substring(index+1,index2+1));
                        }
                    }else {
                        String flag = country.substring(0,2);
                        switch (flag){
                            case "内蒙":
                                regionInfo.setProvince("内蒙古");
                                country.substring(3);
                                index=country.indexOf("市");
                                if(index>0){
                                    regionInfo.setCity(country.substring(0,index+1));
                                }
                                break;
                            case "宁夏":
                            case "广西":
                            case "新疆":
                            case "西藏":
                                regionInfo.setProvince(flag+"省");
                                country.substring(2);
                                index=country.indexOf("市");
                                if(index>0){
                                    regionInfo.setCity(country.substring(0,index+1));
                                }
                                break;
                            case "北京":
                            case "天津":
                            case "上海":
                            case "重庆":
                                regionInfo.setProvince(flag + "市");
                                country.substring(2);
                                index = country.indexOf("区");
                                if (index > 0) {
                                    char ch = country.charAt(index - 1);
                                    if (ch != '小' || ch != '校' || ch != '军') {
                                        regionInfo.setCity(country.substring(0, index + 1));
                                    }
                                }

                                index = country.indexOf("县");
                                if (index > 0) {
                                    regionInfo.setCity(country.substring(0, index + 1));
                                }
                                break;
                            case "香港":
                            case "澳门":
                            case "台湾":
                                regionInfo.setProvince(flag + "特别行政区");
                                break;
                            default:
                                break;

                        }
                    }
                }
            }
        }
        return regionInfo;
    }

    /**
     * 淘宝解析IP地址
     */
    public static RegionInfo ipParser2(String url,String charset){
        try {
            if(url==null || !url.startsWith("http")){
                throw new RuntimeException("url格式错误");
            }
            //获取http客户端对象
            HttpClient client = new HttpClient();
            //获取发送get请求的对象
            GetMethod method = new GetMethod(url);
            if(null!=charset){
                method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + charset);
            }else {
                method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + "utf-8");
            }
            //执行get请求，返回状态码
            int statusCode = client.executeMethod(method);
            //判断状态码是否正常
            if(statusCode != HttpStatus.SC_OK){
                System.out.println("failed message:" + method.getStatusLine());

            }
            //根据返回的字符串进行解码获取字节数组
            byte [] responsBody = method.getResponseBodyAsString().getBytes(charset);
            //根据字节数组获取字符串
            String response = new String(responsBody,"utf-8");
            //将字符串转换成json对象
            JSONObject jo = JSONObject.parseObject(response);
            //通过key（“data”）获取json对象
            JSONObject jo1 = JSONObject.parseObject(jo.getString("data"));
            //设置国家，省，市
            regionInfo.setCountry(jo1.getString("country"));
            regionInfo.setProvince(jo1.getString("region"));
            regionInfo.setCity(jo1.getString("city"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return regionInfo;
    }

    //国家，省，市
    /**
     * 封装解析出来的地域信息
     */
    public static class RegionInfo{
        private String default_value = GlobalConstants.DEFAULT_VALUE;
        private String country = default_value;
        private String province = default_value;
        private String city = default_value;

        public RegionInfo() {
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        @Override
        public String toString() {
            return "RegionInfo{" +
                    "country='" + country + '\'' +
                    ", province='" + province + '\'' +
                    ", city='" + city + '\'' +
                    '}';
        }
    }

}