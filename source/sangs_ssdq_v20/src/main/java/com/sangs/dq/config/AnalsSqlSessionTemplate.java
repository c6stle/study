package com.sangs.dq.config;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.mapping.ParameterMapping;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sangs.common.support.ApplicationContextProvider;
import com.sangs.common.support.AuthUtil;
import com.sangs.common.support.BizUtil;
import com.sangs.common.support.BizUtil.DBMS_TYPE_NAME;
import com.sangs.common.support.CommonDao;
import com.sangs.common.support.DbmsConnUtil;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsStringUtil;

public class AnalsSqlSessionTemplate {

	protected Logger log = LoggerFactory.getLogger(this.getClass());
  
	
	public <T> T selectOne(String statement) throws Exception {
		try {
			return this.selectOne(statement, null);
		}catch(Exception e) {
			// e.printStackTrace();
			log.error("", e);
			throw new RuntimeException(e);
			
		} 
	}

	 
	public <T> T selectOne(String statement, Object parameter) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		SangsMap smap = null;
		try {
			String currentDbmsNm = this.getAnalsDbmsNm(parameter); 
			String sqlId = currentDbmsNm + "." + statement; 
			System.out.println("parameter -- " + parameter);
			String sql = this.getSql(sqlId, parameter);
			// System.out.println("------------------------- anals query sql start --------------------- " + sqlId);
			// System.out.println(sql);
			// System.out.println("------------------------- anals query sql end ---------------------");
			
			conn = getAnalsConnection(parameter);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			rsmd = rs.getMetaData();
			
			int colCnt = rsmd.getColumnCount();
			
			 
			if(rs.next()) {
				smap = new SangsMap();
				
				for(int i = 1 ; i <= colCnt ; i++) {
					String colNm = rsmd.getColumnName(i);
					smap.put(colNm, rs.getObject(i));
				}
			}
			
			if(smap == null)
				return null;
			
			return (T) smap;
		} catch(Exception e) {
			// e.printStackTrace();
			log.error("", e);
			throw new RuntimeException(e);
		} finally {
			if(rs != null)
				rs.close();
			if(stmt != null)
				stmt.close();
			if(conn != null)
				conn.close();
			 
		}
	}
 
	
	public Integer selectInteger(String statement, Object parameter) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		Integer rtnObj = 0;
		try {
			
			String currentDbmsNm = this.getAnalsDbmsNm(parameter);
			String sqlId = currentDbmsNm + "." + statement; 
			String sql = this.getSql(sqlId, parameter);
			// System.out.println("------------------------- anals query sql start --------------------- " + sqlId);
			// System.out.println(sql);
			// System.out.println("------------------------- anals query sql end ---------------------");
			
			conn = getAnalsConnection(parameter);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			rsmd = rs.getMetaData();
			 
			if(rs.next()) {
				
				rtnObj = (Integer)rs.getInt(1);
			}
			
			return rtnObj;
		} catch(Exception e) {
			// e.printStackTrace();
			log.error("", e);
			throw new RuntimeException(e);
		} finally {
			if(rs != null)
				rs.close();
			if(stmt != null)
				stmt.close();
			if(conn != null)
				conn.close();
		}
	}
	
	
	public <E> List<E> selectList(String statement) throws Exception {
		try {
			return this.selectList(statement, null);
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} 
	}


	@SuppressWarnings("unchecked")
	public <E> List<E> selectList(String statement, Object parameter) throws Exception {
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		List<E> list = new ArrayList<E>();
		try {
			//return super.selectList(dbmsNm + "." + statement, parameter);
			
			String currentDbmsNm = this.getAnalsDbmsNm(parameter);
			String sqlId = currentDbmsNm + "." + statement; 
			
			String sql = this.getSql(sqlId, parameter);
			// System.out.println("------------------------- anals query sql start --------------------- " + sqlId);
			// System.out.println(sql);
			// System.out.println("------------------------- anals query sql end ---------------------");
			
			conn = getAnalsConnection(parameter);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			rsmd = rs.getMetaData();
			
			int colCnt = rsmd.getColumnCount();
			while (rs.next()) {
				SangsMap smap = new SangsMap();
				for(int i = 1 ; i <= colCnt ; i++) {
					
					
					
					//System.out.println("getColumnLabel : " + rsmd.getColumnLabel(i));
					
					String colNm = rsmd.getColumnLabel(i);
					
					
					//System.out.println(colNm + "---->" + rsmd.getColumnType(i));
					
					if(Types.TIMESTAMP == rsmd.getColumnType(i)) {
						Timestamp ts = rs.getTimestamp(i);
						if(ts != null)
							smap.put(colNm, ts.toString());
						else 
							smap.put(colNm, null);
					} else if(Types.DATE == rsmd.getColumnType(i)) {
						Date date = rs.getDate(i);
						if(date != null)
							smap.put(colNm, date.toString());
						else 
							smap.put(colNm, null);
						
					} else {
						smap.put(colNm, rs.getObject(i));	
					}
				}
				//System.out.println(smap);
				list.add((E) smap);
			}
			return list;
		} catch(Exception e) {
			// e.printStackTrace();
			log.error("", e);
			throw new RuntimeException(e);
		} finally {
			if(rs != null)
				rs.close();
			if(stmt != null)
				stmt.close();
			if(conn != null)
				conn.close();
			 
		}
	}
	

	/**
	 * 분석 DBMS 커넥션 반환
	 * @return
	 * @throws Exception
	 */
	
	private Connection getAnalsConnection(Object parameter) throws Exception {
	
		Map<String, Object> dbmsCnncInfo = null;
		Map tempParamMap = (Map)parameter;
		if(tempParamMap.containsKey("THREAD_CONN_YN") && "Y".equals(tempParamMap.get("THREAD_CONN_YN"))) {
			// 쓰레드 커넥션인경우 
			dbmsCnncInfo = (Map<String, Object>)tempParamMap.get("THREAD_DBMS_INFO");
		} else {
			dbmsCnncInfo = DbmsConnUtil.getDbmsConnMapFromSession();
		}
		
		
		SangsStringUtil.checkRequiredParam(dbmsCnncInfo, "dbmsIpAddr", "접속 IP 주소");
		SangsStringUtil.checkRequiredParam(dbmsCnncInfo, "dbmsPortNo", "접속 Port 번호");
		String dbmsId = SangsStringUtil.checkRequiredParamStr(dbmsCnncInfo, "dbmsId", "접속 계정");
		String dbmsPassword = SangsStringUtil.checkRequiredParamStr(dbmsCnncInfo, "dbmsPassword", "접속 비밀번호");
		String currentDbmsNm = SangsStringUtil.checkRequiredParamStr(dbmsCnncInfo, "dbmsNm", "dbmsNm");
		String driverNm = DbmsConnUtil.getDbmsDriverNm(currentDbmsNm);
		String dbmsUrl = DbmsConnUtil.getDbmsUrl(currentDbmsNm, dbmsCnncInfo);
		
		Connection conn = null;
		
		try {
			conn = DbmsConnUtil.getConnection(driverNm, dbmsUrl, dbmsId, dbmsPassword);
		} catch(SangsMessageException e) {
			throw e;
		} catch(Exception e) {
			throw e;
		}
		if(conn == null)
			throw new SangsMessageException("DB접속 Connection 생성 실패");
		
		return conn;
	}
	
	 
	
	private String getSql(String id, Object paramObj) throws Exception{
		
		CommonDao beanInstance = (CommonDao)ApplicationContextProvider.getBean("commonDao");
		
		Map param = (Map)paramObj;
		SqlSessionTemplate tsqlSession = beanInstance.getSqlSessionTemplate();
		
	    String sql = tsqlSession.getConfiguration().getMappedStatement(id).getBoundSql(param).getSql();
	    List<ParameterMapping> paramMap = tsqlSession.getConfiguration().getMappedStatement(id)
	                                                .getBoundSql(param).getParameterMappings();
	    for (ParameterMapping par : paramMap) {
	        String parameter = null;
	        parameter = param.get(par.getProperty()).toString();
	        if(parameter == null)
	            sql = sql.replaceFirst("\\?", "NULL");
	        else
	            sql = sql.replaceFirst("\\?", "'" + parameter + "'");
	    }
	    return sql;
	}
	
	

	@SuppressWarnings("rawtypes")
	private String getAnalsDbmsNm(Object parameter) {
		Map tempParamMap = (Map)parameter;
		String currentDbmsNm = "";
		
		if(tempParamMap.containsKey("THREAD_CONN_YN") && "Y".equals(tempParamMap.get("THREAD_CONN_YN"))) {
			// 쓰레드 커넥션인경우 
			Map<String, Object> dbmsInfo = (Map<String, Object>)tempParamMap.get("THREAD_DBMS_INFO");
			currentDbmsNm = String.valueOf(dbmsInfo.get("dbmsNm"));
		} else {
			currentDbmsNm = AuthUtil.getDbmsNm();
		}
		if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.CSV, currentDbmsNm)) {
			currentDbmsNm = "MariaDB";
		}
		return currentDbmsNm;
	}
	
	
	
	// insert method 시험용 임시
	public <R extends Map<String, Object>> void insert(String statement, R parameter) throws Exception {

		Connection conn = null;
		Statement stmt = null;
		
		try {
			//return super.selectList(dbmsNm + "." + statement, parameter);
			
			String currentDbmsNm = this.getAnalsDbmsNm(parameter);
			String sqlId = currentDbmsNm + "." + statement; 
			
			String sql = this.getSql(sqlId, parameter);
			// System.out.println("------------------------- anals query sql start --------------------- " + sqlId);
			// System.out.println(sql);
			// System.out.println("------------------------- anals query sql end ---------------------");
			
			conn = getAnalsConnection(parameter);
			stmt = conn.createStatement();
			stmt.executeQuery(sql);
			
		} catch(Exception e) {
			// e.printStackTrace();
			log.error("", e);
			throw new RuntimeException(e);
		} finally {
			if(stmt != null)
				stmt.close();
			if(conn != null)
				conn.close();
			 
		}
	}
	 
	
}
