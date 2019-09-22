package com.qianfeng.analysis.mr.local;

import com.qianfeng.analysis.model.base.DateDimension;
import com.qianfeng.analysis.model.base.KpiDimension;
import com.qianfeng.analysis.model.base.LocalDimension;
import com.qianfeng.analysis.model.base.PlatformDimension;
import com.qianfeng.analysis.model.key.StatsCommonDimension;
import com.qianfeng.analysis.model.key.StatsUserLcDimension;
import com.qianfeng.analysis.model.value.IdOutputValue;
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

import static com.qianfeng.analysis.util.MemberUtil.insertMemberId;
import static com.qianfeng.analysis.util.MemberUtil.isNewMember;

public class LocalMapper extends Mapper<LongWritable, Text, StatsUserLcDimension, IdOutputValue> {
    private static final Logger logger = Logger.getLogger(LocalMapper.class);
    private StatsUserLcDimension k = new StatsUserLcDimension();
    private IdOutputValue v = new IdOutputValue();
    //获取地域模块下的新增用户的kpi
    private KpiDimension activeLocalUserKpi = new KpiDimension(KpiType.LOCAL.kpiName);

    Configuration conf = null;
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        if (StringUtils.isEmpty(line)) {
            return;
        }
        String fields[] = line.split("\u0001");
        //活跃用户规则：当天所有数据uuid的去重个数
        //取出想要的字段
        //新增用户规则,lantch事件下的uuid的去重个数

            String platform = fields[13];//平台id
            String serverTime = fields[1];//时间戳
            String uuid = fields[3];//获取uuid
            String u_sd = fields[5];//会话id
            String u_mid=fields[4];//新增会员id
            String countryName = fields[28];//国家名
            String provinceName = fields[29];//省份名
            String cityName = fields[30];//城市名

        try {
            if (StringUtils.isEmpty(serverTime) || StringUtils.isEmpty(uuid) || uuid.equals("null") || StringUtils.isEmpty(u_sd) || u_sd.equals("null") ||StringUtils.isEmpty(u_mid)||!isNewMember(u_mid)|| u_mid.equals("null")|| serverTime.equals("null")) {
                logger.info("serverTime或者uuid或者u_sd或者u_mid不能为空");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
            insertMemberId(u_mid,conf);
        //构造输出key 地域模式下
            long time = Long.valueOf(serverTime);
            PlatformDimension pl = PlatformDimension.getInstance(platform);//获取平台
            DateDimension dateDimension = DateDimension.buildDate(time, DateEnum.DAY);//按天分析
            StatsCommonDimension statsCommonDimension = k.getStatsCommonDimension();
            statsCommonDimension.setDt(dateDimension);
            statsCommonDimension.setPl(pl);
            statsCommonDimension.setKpi(activeLocalUserKpi);
            LocalDimension localDimension = LocalDimension.getInstance(countryName, provinceName, cityName);
            k.setLocalDimension(localDimension);
            k.setStatsCommonDimension(statsCommonDimension);
            v.setUuid(uuid);
            v.setSid(u_sd);
            v.setUmid(u_mid);
            context.write(k, v);
        }
    }
