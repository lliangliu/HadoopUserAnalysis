package com.qianfeng.analysis.mr.nm;

import com.qianfeng.analysis.model.base.BrowserDimension;
import com.qianfeng.analysis.model.base.DateDimension;
import com.qianfeng.analysis.model.base.KpiDimension;
import com.qianfeng.analysis.model.base.PlatformDimension;
import com.qianfeng.analysis.model.key.StatsCommonDimension;
import com.qianfeng.analysis.model.key.StatsUserDimension;
import com.qianfeng.analysis.model.value.TimeOutputValue;
import com.qianfeng.analysis.util.MemberUtil;
import com.qianfeng.common.Constants;
import com.qianfeng.common.DateEnum;
import com.qianfeng.common.KpiType;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

//新增会员
public class NewMemberMapper extends Mapper<LongWritable, Text, StatsUserDimension, TimeOutputValue> {
private static final Logger logger=Logger.getLogger(NewMemberMapper.class);
private StatsUserDimension k=new StatsUserDimension();
private TimeOutputValue v=new TimeOutputValue();
//获取用户模块下的新增会员的kpi
    private KpiDimension newMemberKpi=new KpiDimension(KpiType.NEW_MEMBER.kpiName);
    //获取浏览器模块下的新增会员的kpi
    private KpiDimension newMemberBrowserKpi=new KpiDimension(KpiType.BROWSER_NEW_MEMBER.kpiName);
    MemberUtil memberUtil=new MemberUtil();
    Configuration conf = null;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        conf = context.getConfiguration();
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line=value.toString();
        if(StringUtils.isEmpty(line)){
            return;
        }
        //不为空 拆分数据
            String fields[]=line.split("\u0001");
        //新增会员规则,计算当天(由维度信息确定)的所有数据中的member id，
        //要求member id以前没有访问过网站(在日志收集模块上线后没法访问过)，
        //统计这部分的member id格式作为新会员的数量值。
            String en=fields[2];
            //取出想要的字段
            String platform=fields[13];
            String serverTime=fields[1];
            String memberid=fields[4];
            String browserName=fields[24];
            String browserVersion=fields[25];
        try {
            if(StringUtils.isEmpty(serverTime)||StringUtils.isEmpty(memberid)||!MemberUtil.isNewMember(memberid)||serverTime.equals("null")){
            return ;
            }
        } catch (SQLException e) {
            logger.info("serverTime或者memberid不能为空，会员之前未出现过");
        }
        memberUtil.insertMemberId(memberid,conf);
            //构造输出的key
            long time=Long.valueOf(serverTime);
            PlatformDimension pl=PlatformDimension.getInstance(platform);
            DateDimension dateDimension=DateDimension.buildDate(time, DateEnum.DAY);//按天分析
            StatsCommonDimension statsCommonDimension=k.getStatsCommonDimension();
            statsCommonDimension.setDt(dateDimension);
            statsCommonDimension.setPl(pl);
            statsCommonDimension.setKpi(newMemberKpi);
            k.setStatsCommonDimension(statsCommonDimension);
            //获取用户模块下的新增用户  将浏览器下的设置为null
            BrowserDimension browserDimension=new BrowserDimension("","");
            k.setBrowserDimension(browserDimension);
            v.setId(memberid);
            context.write(k,v);

            //浏览器模块下的赋值
            statsCommonDimension.setKpi(newMemberBrowserKpi);
            browserDimension=new BrowserDimension(browserName,browserVersion);
            k.setBrowserDimension(browserDimension);
            k.setStatsCommonDimension(statsCommonDimension);
            context.write(k,v);

        }

    }
