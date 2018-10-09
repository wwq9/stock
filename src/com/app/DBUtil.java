package com.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class DBUtil {
	private static Logger log = Logger.getLogger(DBUtil.class);
	public static ExecutorService executorService = Executors.newCachedThreadPool();
	private static DataSource datasource = null;
	static{
		datasource = new DataSource();
		PoolProperties p = new PoolProperties();
		p.setUrl("jdbc:mysql://10.141.17.16:3307/gsxt?characterEncoding=UTF-8");
//		p.setUrl("jdbc:mysql://127.0.0.1:3306/mystock?characterEncoding=UTF-8");
		p.setDriverClassName("com.mysql.jdbc.Driver");
		p.setUsername("root");
		p.setPassword("rootroot");
		p.setTestWhileIdle(true);
		p.setValidationQuery("select 1");
		datasource.setPoolProperties(p);
	}
	
	public static Connection getConn() throws Exception{
		return datasource.getConnection();
	}
	
	public static int executeUpdateSql(String sql,Object[] param){
		PreparedStatement ps = null;
		Connection conn = null;
		int re = 0;
		try {
			conn = datasource.getConnection();
			ps = conn.prepareStatement(sql);
			if(param!=null){
			for(int i=0;i<param.length;i++){
				ps.setObject(i+1, param[i]);
			}
			}
			re = ps.executeUpdate();
		} catch (Exception e) {		
			log.error(new Date(), e);
		} finally {
			try {ps.close();} catch (Exception e) {}
			try {conn.close();} catch (Exception e) {}
		}
		return re;
	}
	
	public static Map getObject(String sql,Object[] param){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		Map num = new HashMap();
		try {
			conn = datasource.getConnection();
			ps = conn.prepareStatement(sql);
			if(param!=null){
			for(int i=0;i<param.length;i++){
				ps.setObject(i+1, param[i]);
			}
			}
			rs = ps.executeQuery();
			ResultSetMetaData md = rs.getMetaData();
			int count = md.getColumnCount();
			while(rs.next()){
				for(int i=1;i<=count;i++){
					try {
						num.put(md.getColumnName(i), rs.getObject(i));
					} catch (Exception e) {
					}
				}
				break;
			}
		} catch (Exception e) {		
			log.error(new Date(), e);
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {ps.close();} catch (Exception e) {}
			try {conn.close();} catch (Exception e) {}
		}
		return num;
	}
	
	public static List getList(String sql,Object[] param){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		List list = new ArrayList();
		try {
			conn = datasource.getConnection();
			ps = conn.prepareStatement(sql);
			if(param!=null){
				for(int i=0;i<param.length;i++){
					ps.setObject(i+1, param[i]);
				}
			}
			rs = ps.executeQuery();
			ResultSetMetaData md = rs.getMetaData();
			int count = md.getColumnCount();
			while(rs.next()){
				Map num = new HashMap();
				for(int i=1;i<=count;i++){
					try {
						
						num.put(md.getColumnLabel(i), rs.getObject(i));
						
					} catch (Exception e) {
					}
				}
				list.add(num);
			}
		} catch (Exception e) {		
			log.error(new Date(), e);
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {ps.close();} catch (Exception e) {}
			try {conn.close();} catch (Exception e) {}
		}
		return list;
	}
	
	
	public static int getInt(String sql,Object[] param,String column){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		int num = 0;
		try {
			conn = datasource.getConnection();
			ps = conn.prepareStatement(sql);
			if(param!=null){
				for(int i=0;i<param.length;i++){
					ps.setObject(i+1, param[i]);
				}
			}
			rs = ps.executeQuery();
			while(rs.next()){
				try {
					num = Integer.parseInt(rs.getObject(column).toString());
				} catch (Exception e) {
				}
				break;
			}
		} catch (Exception e) {		
			log.error(new Date(), e);
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {ps.close();} catch (Exception e) {}
			try {conn.close();} catch (Exception e) {}
		}
		return num;
	}
	
	
	public static long getLong(String sql,Object[] param,String column){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		long num = 0;
		try {
			conn = datasource.getConnection();
			ps = conn.prepareStatement(sql);
			if(param!=null){
			for(int i=0;i<param.length;i++){
				ps.setObject(i+1, param[i]);
			}
			}
			rs = ps.executeQuery();
			while(rs.next()){
				try {
					num = Long.parseLong(rs.getObject(column).toString());
				} catch (Exception e) {
				}
				break;
			}
		} catch (Exception e) {		
			log.error(new Date(), e);
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {ps.close();} catch (Exception e) {}
			try {conn.close();} catch (Exception e) {}
		}
		return num;
	}
	
	
	public static String getString(String sql,Object[] param,String column){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		String num = "";
		try {
			conn = datasource.getConnection();
			ps = conn.prepareStatement(sql);
			if(param!=null){
			for(int i=0;i<param.length;i++){
				ps.setObject(i+1, param[i]);
			}
			}
			rs = ps.executeQuery();
			while(rs.next()){
				try {
					num = rs.getString(column);
				} catch (Exception e) {
				}
				break;
			}
		} catch (Exception e) {		
			log.error(new Date(), e);
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {ps.close();} catch (Exception e) {}
			try {conn.close();} catch (Exception e) {}
		}
		return num;
	}
}
