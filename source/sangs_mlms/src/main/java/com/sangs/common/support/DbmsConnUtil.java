package com.sangs.common.support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsStringUtil;

public class DbmsConnUtil {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// DBMS 종류 (db에 들어 있는 명칭CMMN_PRJCT_DBMS.DBMS_NM)
	public enum DBMS_TYPE_NAME {
		ORACLE
		,CSV
		,MYSQL
		,TIBERO
		,MSSQL
		,CUBRID
		,POSTGRESQL
		,ALTIBASE
		,DB2
		,MONGODB
		,MARIADB
	}
	
	
	public static boolean isEqualDbms(DBMS_TYPE_NAME dbmsTypeName, String strDbmsTypeName) {
		if(SangsStringUtil.isEmpty(strDbmsTypeName))
			return false;
		
		if(dbmsTypeName.toString().equals(strDbmsTypeName.toUpperCase()))
			return true;
		else 
			return false;
	}
	
	
	/**
	 * Connection 반환 
	 * 
	 * @param driverNm
	 * @param dbmsUrl
	 * @param dbUserId
	 * @param dbUserPwd
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection(String driverNm, String dbmsUrl, String dbUserId, String dbUserPwd) throws Exception {
		Connection conn = null; 
		try {
			Class.forName(driverNm);
		} catch(Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("Database Driver load Exception ["+driverNm+"]");
		}
		
		try {
			
			DriverManager.setLoginTimeout(5); // timeout 설정 : 5초 , 입력정보 오류로 접속이 안될때
			conn = DriverManager.getConnection(dbmsUrl, dbUserId, dbUserPwd);
		} catch(Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("Database Connection Exception ["+dbmsUrl+"]");
		}
		return conn;
	}
	
	 
	

	/**
	 * DBMS 에 대한 Driver class Name 반환 
	 * 
	 * @param dbmsNm
	 * @return
	 */
	public static String getDbmsDriverNm(String dbmsNm) {
		if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.ORACLE, dbmsNm)) 
			return "oracle.jdbc.driver.OracleDriver";
		else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.MYSQL, dbmsNm)) 
			return "com.mysql.cj.jdbc.Driver";
		else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.MARIADB, dbmsNm)) 
			return "org.mariadb.jdbc.Driver";
		else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.TIBERO, dbmsNm))
			return "com.tmax.tibero.jdbc.TbDriver";
		else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.MSSQL, dbmsNm))
			return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.CUBRID, dbmsNm))
			return "cubrid.jdbc.driver.CUBRIDDriver";
		else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.POSTGRESQL, dbmsNm))
			return "org.postgresql.Driver";
		else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.ALTIBASE, dbmsNm))
			return "Altibase.jdbc.driver.AltibaseDriver";
		else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.DB2, dbmsNm))
			return "com.ibm.db2.jcc.DB2Driver";
		else 
			throw new SangsMessageException("Not Support DBMS ["+dbmsNm+"]");
	}
	
	/**
	 * DBMS 에 대한 접속 URL 반환 
	 * 
	 * @param dbmsNm
	 * @param paramMap
	 * @return
	 */
	public static String getDbmsUrl(String dbmsNm, Map<String, Object> paramMap) {
	 
		SangsStringUtil.checkRequiredParam(paramMap, "dbmsIpAddr", "Database Server IP");
		SangsStringUtil.checkRequiredParam(paramMap, "dbmsPortNo", "Database Server Port");
		
		if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.ORACLE, dbmsNm)) { 
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsSidNm", "SID");	
			return "jdbc:Oracle:thin:@" + paramMap.get("dbmsIpAddr") + ":" + paramMap.get("dbmsPortNo") + ":" + paramMap.get("dbmsSidNm");
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.MYSQL, dbmsNm)) {   
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "jdbc:mysql://" + paramMap.get("dbmsIpAddr") + ":" + paramMap.get("dbmsPortNo") + "/" + paramMap.get("dbmsDatabaseNm");
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.MARIADB, dbmsNm)) {     
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "jdbc:mariadb://" + paramMap.get("dbmsIpAddr") + ":" + paramMap.get("dbmsPortNo") + "/" + paramMap.get("dbmsDatabaseNm");
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.TIBERO, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsSidNm", "SID");
			return "jdbc:tibero:thin:@" + paramMap.get("dbmsIpAddr") + ":" + paramMap.get("dbmsPortNo") + ":" + paramMap.get("dbmsSidNm");
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.MSSQL, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "jdbc:sqlserver://" + paramMap.get("dbmsIpAddr") + ":" + paramMap.get("dbmsPortNo") + ";databaseName=" + paramMap.get("dbmsDatabaseNm");
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.CUBRID, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "jdbc:CUBRID:" + paramMap.get("dbmsIpAddr") + ":" + paramMap.get("dbmsPortNo") + ":" + paramMap.get("dbmsDatabaseNm") + ":::";
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.POSTGRESQL, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "jdbc:postgresql://" + paramMap.get("dbmsIpAddr") + ":" + paramMap.get("dbmsPortNo") + "/" + paramMap.get("dbmsDatabaseNm");
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.ALTIBASE, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "jdbc:Altibase://" + paramMap.get("dbmsIpAddr") + ":" + paramMap.get("dbmsPortNo") + "/" + paramMap.get("dbmsDatabaseNm");
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.DB2, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "jdbc:db2://" + paramMap.get("dbmsIpAddr") + ":" + paramMap.get("dbmsPortNo") + "/" + paramMap.get("dbmsDatabaseNm");
		} else { 
			throw new SangsMessageException("Not Support DBMS ["+dbmsNm+"]");
		}
	}

	
	/**
	 * DBMS에 대한 health 체크 쿼리 반환 
	 *  
	 * @param dbmsNm
	 * @param paramMap
	 * @return
	 */
	public static String getDbmsHealthCheckSql(String dbmsNm, Map<String, Object> paramMap) {
		if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.ORACLE, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "select count(owner) as cnt from ALL_TABLES " + "where upper(owner) = upper('" + paramMap.get("dbmsDatabaseNm") + "') group by owner";
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.MYSQL, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "select count(schema_name) as cnt from INFORMATION_SCHEMA.SCHEMATA where schema_name = '" + paramMap.get("dbmsDatabaseNm") + "' group by schema_name";
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.MARIADB, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "select count(schema_name) as cnt from INFORMATION_SCHEMA.SCHEMATA where schema_name = '" + paramMap.get("dbmsDatabaseNm") + "' group by schema_name";
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.TIBERO, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "select count(owner) as cnt from ALL_TABLES where upper(owner) = upper('" + paramMap.get("dbmsDatabaseNm") + "') group by owner";
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.MSSQL, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "select count(catalog_name) as cnt from INFORMATION_SCHEMA.SCHEMATA where catalog_name = '" + paramMap.get("dbmsDatabaseNm") + "' ";
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.CUBRID, dbmsNm)) {
			return "select count(schema()) cnt";
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.POSTGRESQL, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "select count(catalog_name) as cnt from INFORMATION_SCHEMA.SCHEMATA where catalog_name = '" + paramMap.get("dbmsDatabaseNm") + "'";
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.ALTIBASE, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "select count(db_name) " + "cnt" + " from v$database where db_name ='" + paramMap.get("dbmsDatabaseNm") + "'";
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.DB2, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsSchema", "Schema");
			return "select count(distinct owner) as cnt from SYSCAT.TABLES where tabschema =upper('" + paramMap.get("dbmsSchema") + "')";
		} else { 
			throw new SangsMessageException("Not Support DBMS ["+dbmsNm+"]");
		}
	}
	
	/**
	 * 테이블 목록 조회 SQL 반환
	 * @param dbmsNm
	 * @param tableSchema
	 * @return
	 */
	public static String getTableListSql(String dbmsNm, String tableSchema) {
		
		if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.ORACLE, dbmsNm)) {
			return "SELECT T1.TABLE_NAME AS TABLE_NAME, T2.COMMENTS AS TABLE_COMMENT FROM ALL_TAB_COLUMNS T1 JOIN USER_TAB_COMMENTS T2 ON T1.TABLE_NAME = T2.TABLE_NAME WHERE 1=1 AND UPPER(T1.OWNER) = UPPER('" + tableSchema + "')  GROUP BY T1.TABLE_NAME, T2.COMMENTS";
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.MYSQL, dbmsNm)) {
			return "SELECT TABLE_NAME AS TABLE_NAME, TABLE_COMMENT AS TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + tableSchema + "'";
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.MARIADB, dbmsNm)) {
			return "SELECT TABLE_NAME AS TABLE_NAME, TABLE_COMMENT AS TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_SCHEMA = '" + tableSchema + "'";
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.TIBERO, dbmsNm)) {
			return "SELECT T1.TABLE_NAME , T2.COMMENTS as TABLE_COMMENT FROM ALL_TABLES T1 , USER_TAB_COMMENTS t2 WHERE T1.TABLE_NAME = T2.TABLE_NAME(+) AND UPPER(OWNER) = UPPER('" + tableSchema + "')";
		//} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.MSSQL, dbmsNm)) {
		//	return "";
		//} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.CUBRID, dbmsNm)) {
		//	return "";
		//} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.POSTGRESQL, dbmsNm)) {
		//	return "";
		//} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.ALTIBASE, dbmsNm)) {
		//	return "";
		//} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.DB2, dbmsNm)) {
		//	return "";
		} else { 
			throw new SangsMessageException("Not Support DBMS ["+dbmsNm+"]");
		}
	}
	
	/**
	 * 테이블 컬럼 전체 목록 조회 SQL 반환
	 * @param dbmsNm
	 * @param tableSchema
	 * @return
	 */
	public static String getTableColumnAllListSql(String dbmsNm, String tableSchema) {
		
		if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.ORACLE, dbmsNm)) {
			String sql = 
					"SELECT"
					+ "		T1.TABLE_NAME "
					+ "		, T2.COLUMN_NAME "
					+ "		, T1.DATA_TYPE "
					+ "		, T1.DATA_LENGTH "
					+ "		, T2.COMMENTS AS COLUMN_COMMENT "
					+ "	FROM ALL_TAB_COLUMNS T1 JOIN USER_COL_COMMENTS T2 ON T1.TABLE_NAME = T2.TABLE_NAME AND T1.COLUMN_NAME = T2.COLUMN_NAME "
					+ "	WHERE 1 = 1 "
					+ "		AND UPPER(T1.OWNER) = UPPER('" + tableSchema + "') "
					+ "	ORDER BY T1.TABLE_NAME ";
				return sql;
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.MYSQL, dbmsNm)) {
			
			String sql = 
				"SELECT"
				+ "		T1.TABLE_NAME "
				+ "		, T1.COLUMN_NAME "
				+ "		, T1.DATA_TYPE "
				+ "		, T1.CHARACTER_MAXIMUM_LENGTH AS DATA_LENGTH "
				+ "		, T1.COLUMN_COMMENT "
				+ "	FROM INFORMATION_SCHEMA.COLUMNS T1 "
				+ "		, INFORMATION_SCHEMA.TABLES T2 "
				+ "	WHERE 1 = 1 "
				+ "		AND T1.TABLE_SCHEMA = '" + tableSchema + "' "
				+ "		AND T1.TABLE_SCHEMA = T2.TABLE_SCHEMA "
				+ "		AND T1.TABLE_NAME = T2.TABLE_NAME "
				+ "		AND T1.TABLE_CATALOG = T2.TABLE_CATALOG "
				+ "		AND T2.TABLE_TYPE = 'BASE TABLE'"
				+ "	ORDER BY T1.TABLE_NAME, T1.ORDINAL_POSITION ";
			return sql;
			
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.MARIADB, dbmsNm)) {
			
			String sql = 
				"SELECT"
				+ "		T1.TABLE_NAME "
				+ "		, T1.COLUMN_NAME "
				+ "		, T1.DATA_TYPE "
				+ "		, T1.CHARACTER_MAXIMUM_LENGTH AS DATA_LENGTH "
				+ "		, T1.COLUMN_COMMENT "
				+ "	FROM INFORMATION_SCHEMA.COLUMNS T1 "
				+ "		, INFORMATION_SCHEMA.TABLES T2 "
				+ "	WHERE 1 = 1 "
				+ "		AND T1.TABLE_SCHEMA = '" + tableSchema + "' "
				+ "		AND T1.TABLE_SCHEMA = T2.TABLE_SCHEMA "
				+ "		AND T1.TABLE_NAME = T2.TABLE_NAME "
				+ "		AND T1.TABLE_CATALOG = T2.TABLE_CATALOG "
				+ "		AND T2.TABLE_TYPE = 'BASE TABLE'"
				+ "	ORDER BY T1.TABLE_NAME, T1.ORDINAL_POSITION ";
			return sql;
			
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.TIBERO, dbmsNm)) {
			
			String sql = 
				"SELECT \r\n"
				+ "		T1.OWNER \r\n"
				+ "	    , T1.COLUMN_ID \r\n"
				+ "	    , T1.COLUMN_NAME \r\n"
				+ "	    , T1.TABLE_NAME \r\n"
				+ "	    , T1.DATA_TYPE \r\n"
				+ "	    , T1.DATA_LENGTH \r\n"
				+ "	    , T1.DATA_PRECISION \r\n"
				+ "	    , T1.DATA_SCALE \r\n"
				+ "	    , T1.NULLABLE \r\n"
				+ "	    , T1.DATA_DEFAULT \r\n"
				+ "   	, T2.COMMENTS AS COLUMN_COMMENT \r\n"
				+ "	FROM ALL_TAB_COLUMNS T1 \r\n"
				+ "		, ALL_COL_COMMENTS T2 \r\n"
				+ "	WHERE 1 = 1 \r\n"
				+ "		AND T1.OWNER = T2.OWNER \r\n"
				+ "		AND T1.TABLE_NAME = T2.TABLE_NAME \r\n"
				+ "		AND T1.COLUMN_NAME = T2.COLUMN_NAME \r\n"
				+ "		AND NOT T1.DATA_TYPE IN ('CLOB','BLOB') \r\n"
				+ "		AND UPPER(T1.OWNER) = UPPER('" + tableSchema + "') \r\n"
				+ "	ORDER BY T1.COLUMN_ID ";
			return sql;
			
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.MSSQL, dbmsNm)) {
			return "";
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.CUBRID, dbmsNm)) {
			return "";
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.POSTGRESQL, dbmsNm)) {
			return "";
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.ALTIBASE, dbmsNm)) {
			return "";
		} else if(DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.DB2, dbmsNm)) {
			return "";
		} else { 
			throw new SangsMessageException("Not Support DBMS ["+dbmsNm+"]");
		}
	}


	
}
