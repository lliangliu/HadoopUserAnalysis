package com.qianfeng.analysis.mr.service.impl;

import com.qianfeng.analysis.model.base.*;
import com.qianfeng.analysis.mr.service.IDimension;
import com.qianfeng.util.JdbcUtil;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class IDimensionImpl implements IDimension {
   //定义内存缓存 用来缓存维度ID
    //移除最老的
    private Map<String,Integer> cache=new LinkedHashMap<String,Integer>(){
       @Override
       protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
           return this.size() >2500;
       }
   };


    @Override
    public int getIDimensionImplByDimension(BaseDimension baseDimension) {
        Connection conn=null;
        //构建缓存的key,根据baseDimension能够确定唯一
        //还可以根据这个baseDimension从缓存中取出来
        String cacheKey=buildcacheKey(baseDimension);
        //判断缓存中是否存在
        if(cache.containsKey(cacheKey)){
            return this.cache.get(cacheKey);
        }
        //缓存中不存在
        //先到数据库中查，如果有返回ID
        //数据中没有 就插入维度并返回ID
        String []sqls=null;
        if(baseDimension instanceof KpiDimension){
            sqls = buildKpiSqls(baseDimension);
        }else if (baseDimension instanceof DateDimension){
            sqls = buildDateSqls(baseDimension);
        }else if (baseDimension instanceof PlatformDimension){
            sqls = buildPlSqls(baseDimension);
        }else if (baseDimension instanceof BrowserDimension){
            sqls = buildBrowserSqls(baseDimension);
        }else if (baseDimension instanceof LocalDimension) {
            sqls = buildLocationSqls(baseDimension);
        }else if (baseDimension instanceof EventDimension) {
            sqls = buildEventSqls(baseDimension);
        }else if(baseDimension instanceof CurrencyTypeDimension){
            sqls=buildCurrencyType(baseDimension);
        }else if(baseDimension instanceof PaymentTypeDimension){
            sqls=buildPaymentType(baseDimension);
        }
        //获取连接
        conn= JdbcUtil.getConn();
        int id=-1;
        id=excuteSql(conn,sqls,baseDimension);
        cache.put(cacheKey,id);
        return id;

    }

    private int excuteSql(Connection conn, String[] sqls, BaseDimension baseDimension) {
        PreparedStatement ps=null;
        ResultSet rs=null;
        try {
            String selectSql=sqls[1];
            //获取ps对象
            ps=conn.prepareStatement(selectSql);
            //为ps赋值
            this.setArgs(baseDimension,ps);//{1，kpiNmae}
            rs=ps.executeQuery();
            if(rs.next()){
                return rs.getInt(1);//返回id
            }
            //数据库中没有这个维度 插入维度信息，并返回id
            ps=conn.prepareStatement(sqls[0], Statement.RETURN_GENERATED_KEYS);//不太理解
            this.setArgs(baseDimension,ps);
            ps.executeUpdate();
            rs=ps.getGeneratedKeys();
            if(rs.next()){
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtil.close(null,ps,rs);
        }
        return -1;
    }
    private void setArgs(BaseDimension baseDimension, PreparedStatement ps) {
        try {
            int i = 0;
            if(baseDimension instanceof KpiDimension){
                KpiDimension kpi = (KpiDimension)baseDimension;
                ps.setString(++i,kpi.getKpiName());
            }else if(baseDimension instanceof PlatformDimension){
                PlatformDimension platformDimension = (PlatformDimension)baseDimension;
                ps.setString(++i,platformDimension.getPlatform());
            }else if (baseDimension instanceof BrowserDimension){
                BrowserDimension browserDimension= (BrowserDimension) baseDimension;
                ps.setString(++i,browserDimension.getBrowserName());
                ps.setString(++i,browserDimension.getBrowserVersion());

            }else if(baseDimension instanceof DateDimension){
                DateDimension dateDimension = (DateDimension)baseDimension;
                ps.setInt(++i, dateDimension.getYear());
                ps.setInt(++i, dateDimension.getSeason());
                ps.setInt(++i, dateDimension.getMonth());
                ps.setInt(++i, dateDimension.getWeek());
                ps.setInt(++i, dateDimension.getDay());
                ps.setString(++i, dateDimension.getType());
                ps.setDate(++i, new Date(dateDimension.getCalendar().getTime()));
            }else if(baseDimension instanceof LocalDimension){
                LocalDimension local=(LocalDimension)baseDimension;
                ps.setString(++i, local.getCountry());
                ps.setString(++i, local.getProvince());
                ps.setString(++i, local.getCity());
            }else if (baseDimension instanceof EventDimension){
               EventDimension eventDimension=(EventDimension)baseDimension;
                ps.setString(++i,eventDimension.getCategory());
                ps.setString(++i,eventDimension.getAction());

            }else if(baseDimension instanceof CurrencyTypeDimension){
                CurrencyTypeDimension currencyTypeDimension=(CurrencyTypeDimension)baseDimension;
                ps.setString(++i,currencyTypeDimension.getCurrencyName());
            }else if(baseDimension instanceof PaymentTypeDimension){
                PaymentTypeDimension paymentTypeDimension=(PaymentTypeDimension)baseDimension;
                ps.setString(++i,paymentTypeDimension.getPaymentType());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    private String[] buildBrowserSqls(BaseDimension baseDimension) {
        String insertSql = "INSERT INTO `dimension_browser`(`browser_name`, `browser_version`) VALUES(?,?)";
        String selectSql = "SELECT `id` FROM `dimension_browser` WHERE `browser_name` = ? AND `browser_version`= ?";
        return new String[]{insertSql,selectSql};
    }

    private String[] buildPlSqls(BaseDimension baseDimension) {
        String insertSql = "insert into `dimension_platform`(platform_name) values(?)";
        String selectSql = "select id from `dimension_platform` where platform_name = ?";
        return new String[]{insertSql,selectSql};
    }

    private String[] buildDateSqls(BaseDimension baseDimension) {
        String insertSql = "INSERT INTO `dimension_date`(`year`, `season`, `month`, `week`, `day`, `type`, `calendar`) VALUES(?, ?, ?, ?, ?, ?, ?)";
        String selectSql = "SELECT `id` FROM `dimension_date` WHERE `year` = ? AND `season` = ? AND `month` = ? AND `week` = ? AND `day` = ? AND `type` = ? AND `calendar` = ?";
        return new String[]{insertSql,selectSql};

    }

    private String[] buildKpiSqls(BaseDimension baseDimension) {
        String insertSql="insert into `dimension_kpi`(kpi_name) values(?)";
        String selectSql="select id from `dimension_kpi` where kpi_name = ?";
        return new String[]{insertSql,selectSql};
    }
    private String[] buildLocationSqls(BaseDimension baseDimension) {
        String insertSql = "INSERT INTO `dimension_location`(`country`,`province`,`city`) VALUES(?,?,?)";
        String selectSql = "SELECT `id` FROM `dimension_location` WHERE `country` = ? AND `province` = ? AND `city` = ?";
        return new String[] {insertSql,selectSql };
    }
    private String[] buildEventSqls(BaseDimension baseDimension) {
        String insertSql="INSERT INTO `dimension_event`(`category`,`action`) VALUES(?,?)";
        String selectSql="SELECT `id` FROM `dimension_event` WHERE `category` = ? AND `action` = ?";
        return new String[]{insertSql,selectSql};
    }

    private String[] buildPaymentType(BaseDimension baseDimension) {
        String insertSql="INSERT INTO `dimension_payment_type`(`payment_type`) VALUES(?)";
        String selectSql="SELECT `id` FROM `dimension_payment_type` WHERE `payment_type` = ? ";
        return new String[]{insertSql,selectSql};
    }

    private String[] buildCurrencyType(BaseDimension baseDimension) {
        String insertSql="INSERT INTO `dimension_currency_type`(`currency_name`) VALUES(?)";
        String selectSql="SELECT `id` FROM `dimension_currency_type` WHERE `currency_name` = ? ";
        return new String[]{insertSql,selectSql};
    }

    private String buildcacheKey(BaseDimension baseDimension) {
        StringBuffer sb=new StringBuffer();
        if(baseDimension instanceof KpiDimension){
            sb.append("kpi_");
            KpiDimension kpi=(KpiDimension)baseDimension;
            sb.append(kpi.getKpiName());
        }else if(baseDimension instanceof PlatformDimension){
            sb.append("pl_");
            PlatformDimension pl = (PlatformDimension)baseDimension;
            sb.append(pl.getPlatform());
        }else if (baseDimension instanceof BrowserDimension){
            sb.append("browser_");
            BrowserDimension browser = (BrowserDimension)baseDimension;
            sb.append(browser.getBrowserName()).append(browser.getBrowserVersion());
        }else if(baseDimension instanceof DateDimension){
            sb.append("dt_");
            DateDimension dt = (DateDimension)baseDimension;
            sb.append(dt.getYear());
            sb.append(dt.getMonth());
            sb.append(dt.getSeason());
            sb.append(dt.getWeek());
            sb.append(dt.getDay());
            sb.append(dt.getType());
        } else if (baseDimension instanceof LocalDimension) {
            sb.append("location_");
            LocalDimension local = (LocalDimension) baseDimension;
            sb.append(local.getCountry()).append(local.getProvince()).append(local.getCity());
        }else if(baseDimension instanceof EventDimension){
            sb.append("ev_");
            EventDimension event=(EventDimension)baseDimension;
            sb.append(event.getCategory()).append(event.getAction());
        }else if(baseDimension instanceof CurrencyTypeDimension){
            sb.append("Cu_");
            CurrencyTypeDimension currencyType=(CurrencyTypeDimension)baseDimension;
            sb.append(currencyType.getCurrencyName());
        }else if(baseDimension instanceof PaymentTypeDimension){
            sb.append("Pay_");
           PaymentTypeDimension paymentType=(PaymentTypeDimension) baseDimension;
            sb.append(paymentType.getPaymentType());
        }

        return sb!=null?sb.toString():null;
    }
}

