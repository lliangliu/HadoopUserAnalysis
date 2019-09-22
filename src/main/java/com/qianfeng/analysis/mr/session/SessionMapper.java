package com.qianfeng.analysis.mr.session;

import com.qianfeng.analysis.model.base.BrowserDimension;
import com.qianfeng.analysis.model.base.DateDimension;
import com.qianfeng.analysis.model.base.KpiDimension;
import com.qianfeng.analysis.model.base.PlatformDimension;
import com.qianfeng.analysis.model.key.StatsCommonDimension;
import com.qianfeng.analysis.model.key.StatsUserDimension;
import com.qianfeng.analysis.model.value.TimeOutputValue;
import com.qianfeng.common.DateEnum;
import com.qianfeng.common.KpiType;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;

//新增用户类
public class SessionMapper extends Mapper<LongWritable, Text, StatsUserDimension, TimeOutputValue> {
private static final Logger logger=Logger.getLogger(SessionMapper.class);
private StatsUserDimension k=new StatsUserDimension();
private TimeOutputValue v=new TimeOutputValue();
//获取用户模块下的新增用户的kpi
    private KpiDimension sessionKpi=new KpiDimension(KpiType.SESSION.kpiName);
    //获取浏览器模块下的新增用户的kpi
    private KpiDimension sessionBrowserKpi=new KpiDimension(KpiType.BROWSER_SESSION.kpiName);

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line=value.toString();
        if(StringUtils.isEmpty(line)){
            return;
        }
        //不为空 拆分数据
        String fields[]=line.split("\u0001");
        //活跃用户规则 ：当天所有数据中 uuid的去重个数
            //取出想要的字段
            String platform=fields[13];
            String serverTime=fields[1];
            String u_sd=fields[5];
            String browserName=fields[24];
            String browserVersion=fields[25];
            if(StringUtils.isEmpty(serverTime)||StringUtils.isEmpty(u_sd)||serverTime.equals("null")){
                logger.info("serverTime或者u_sd不能为空");
                return;
            }
            //构造输出的key
            long time=Long.valueOf(serverTime);
            this.v.setTime(time);
            PlatformDimension pl=PlatformDimension.getInstance(platform);
            DateDimension dateDimension=DateDimension.buildDate(time, DateEnum.DAY);//按天分析
            StatsCommonDimension statsCommonDimension=k.getStatsCommonDimension();
            statsCommonDimension.setDt(dateDimension);
            statsCommonDimension.setPl(pl);
            statsCommonDimension.setKpi(sessionKpi);
            k.setStatsCommonDimension(statsCommonDimension);
            //获取用户模块下的新增用户  将浏览器下的设置为null
            BrowserDimension browserDimension=new BrowserDimension("","");
            k.setBrowserDimension(browserDimension);
            v.setId(u_sd);
            context.write(k,v);

            //浏览器模块下的赋值
            statsCommonDimension.setKpi(sessionBrowserKpi);
            browserDimension=new BrowserDimension(browserName,browserVersion);
            k.setBrowserDimension(browserDimension);
            k.setStatsCommonDimension(statsCommonDimension);
            context.write(k,v);

        }
    }

