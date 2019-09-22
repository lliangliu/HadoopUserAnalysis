package com.qianfeng.analysis.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import com.qianfeng.util.JdbcUtil;

import org.apache.hadoop.conf.Configuration;

/**
 * 操作member_info表的工具类，主要作用是判断是否是一个新的访问会员id
 */
public class MemberUtil {

    /**
     * 判断memberid是否是一个新会员id，如果是，则返回true。否则返回false。
     */
    public static boolean isNewMember(String u_mid) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        boolean isNew = true;
        try {
            ps = null;
            rs = null;
            con = null;
            con = JdbcUtil.getConn();
            String selectSql = "select member_id from member_info where member_id = ?";
            ps = con.prepareStatement(selectSql);
            ps.setString(1,u_mid);
            rs = ps.executeQuery();
            if(rs.next()){
                isNew = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            JdbcUtil.close(con,ps,rs);
        }
        return isNew;
    }
    public static void insertMemberId(String u_mid, Configuration conf){
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = JdbcUtil.getConn();
            //String selectSql = "insert into `member_info`(`member_id`,`last_visit_date`,`member_id_server_date`,`created`)values(?,?,?,?) on duplicate key update `last_visit_date` = ?";
            String selectSql = "INSERT INTO `member_info`(`member_id`) VALUES(?)";
            ps = con.prepareStatement(selectSql);
            int i=0;
            ps.setString(++i,u_mid);
            /*  ps.setString(++i,conf.get(GlobalConstants.RUNNING_DATE));
            ps.setString(++i,conf.get(GlobalConstants.RUNNING_DATE));
            ps.setString(++i,conf.get(GlobalConstants.RUNNING_DATE));
            ps.setString(++i,conf.get(GlobalConstants.RUNNING_DATE));
            ps.executeUpdate();*/
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            JdbcUtil.close(con,ps,rs);
        }
    }
    }
