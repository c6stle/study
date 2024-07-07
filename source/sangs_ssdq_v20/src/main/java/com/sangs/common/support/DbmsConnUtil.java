package com.sangs.common.support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLInvalidAuthorizationSpecException;
import java.util.HashMap;
import java.util.Map;

import com.sangs.common.common.CommonConstant;
import com.sangs.common.support.BizUtil.DBMS_TYPE_NAME;
import com.sangs.fwk.support.SangsAuthUtil;
import com.sangs.lib.support.domain.SangsAuthVo;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsEncryptUtil;
import com.sangs.lib.support.utils.SangsStringUtil;

public class DbmsConnUtil {
 
	
	/**
	 * session 정보에 있는 dbms 접속정보를 접속하기위한 map으로 변환
	 * @return
	 */
	public static Map<String, Object> getDbmsConnMapFromSession() {
		SangsAuthVo authVo = SangsAuthUtil.getUserAuthVo();
		Map<String, String> userAttrMap = authVo.getUserAttrMap();
		
		Map<String, Object> dbmsCnncInfo = new HashMap<String, Object>();
		dbmsCnncInfo.put("dbmsIpAddr", userAttrMap.get("dbmsIpAddr"));
		dbmsCnncInfo.put("dbmsPortNo", userAttrMap.get("dbmsPortNo"));
		dbmsCnncInfo.put("dbmsSidNm", userAttrMap.get("dbmsSidNm"));
		dbmsCnncInfo.put("dbmsDatabaseNm", userAttrMap.get("dbmsDatabaseNm"));
		dbmsCnncInfo.put("dbmsId", userAttrMap.get("dbmsId"));
		dbmsCnncInfo.put("dbmsPassword", userAttrMap.get("dbmsPassword"));
		dbmsCnncInfo.put("dbmsSchemaNm", userAttrMap.get("dbmsSchemaNm"));
		dbmsCnncInfo.put("dbmsNm", userAttrMap.get("dbmsNm"));
		return dbmsCnncInfo;
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
			dbUserPwd = SangsEncryptUtil.decrypt_AES128(dbUserPwd, CommonConstant.CRYPT_AES_KEY);
			
			DriverManager.setLoginTimeout(5); // timeout 설정 : 5초 , 입력정보 오류로 접속이 안될때
			conn = DriverManager.getConnection(dbmsUrl, dbUserId, dbUserPwd);
		} catch(SQLInvalidAuthorizationSpecException e) {
			throw new SangsMessageException("Database 접속 계정을 확인 해주세요");
		} catch(Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("Database Connection Exception ["+dbmsUrl+"]\n"+e.getMessage());
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
		if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CSV, dbmsNm)) {
			dbmsNm = "MARIADB";
		}
			
		if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.ORACLE, dbmsNm)) 
			return "oracle.jdbc.driver.OracleDriver";
		else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.MYSQL, dbmsNm)) 
			return "com.mysql.cj.jdbc.Driver";
		else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.MARIADB, dbmsNm)) 
			return "org.mariadb.jdbc.Driver";
		else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.TIBERO, dbmsNm))
			return "com.tmax.tibero.jdbc.TbDriver";
		else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.MSSQL, dbmsNm))
			return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CUBRID, dbmsNm))
			return "cubrid.jdbc.driver.CUBRIDDriver";
		else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.POSTGRESQL, dbmsNm))
			return "org.postgresql.Driver";
		else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.ALTIBASE, dbmsNm))
			return "Altibase.jdbc.driver.AltibaseDriver";
		else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.DB2, dbmsNm))
			return "com.ibm.db2.jcc.DB2Driver";
		else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.VOLTDB, dbmsNm))
			return "org.voltdb.jdbc.Driver";
		else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.MONGODB, dbmsNm))
			return "com.mongodb.jdbc.MongoDriver";
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
		if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CSV, dbmsNm)) {
			dbmsNm = "MARIADB";
		}
		
		SangsStringUtil.checkRequiredParam(paramMap, "dbmsIpAddr", "Database Server IP");
		SangsStringUtil.checkRequiredParam(paramMap, "dbmsPortNo", "Database Server Port");
		
		if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.ORACLE, dbmsNm)) { 
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsSidNm", "SID");	
			return "jdbc:oracle:thin:@" + paramMap.get("dbmsIpAddr") + ":" + paramMap.get("dbmsPortNo") + ":" + paramMap.get("dbmsSidNm");
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.MYSQL, dbmsNm)) {   
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "jdbc:mysql://" + paramMap.get("dbmsIpAddr") + ":" + paramMap.get("dbmsPortNo") + "/" + paramMap.get("dbmsDatabaseNm");
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.MARIADB, dbmsNm)) {     
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "jdbc:mariadb://" + paramMap.get("dbmsIpAddr") + ":" + paramMap.get("dbmsPortNo") + "/" + paramMap.get("dbmsDatabaseNm");
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.TIBERO, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsSidNm", "SID");
			return "jdbc:tibero:thin:@" + paramMap.get("dbmsIpAddr") + ":" + paramMap.get("dbmsPortNo") + ":" + paramMap.get("dbmsSidNm");
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.MSSQL, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "jdbc:sqlserver://" + paramMap.get("dbmsIpAddr") + ":" + paramMap.get("dbmsPortNo") + ";databaseName=" + paramMap.get("dbmsDatabaseNm");
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CUBRID, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "jdbc:CUBRID:" + paramMap.get("dbmsIpAddr") + ":" + paramMap.get("dbmsPortNo") + ":" + paramMap.get("dbmsDatabaseNm") + ":::";
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.POSTGRESQL, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "jdbc:postgresql://" + paramMap.get("dbmsIpAddr") + ":" + paramMap.get("dbmsPortNo") + "/" + paramMap.get("dbmsDatabaseNm");
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.ALTIBASE, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "jdbc:Altibase://" + paramMap.get("dbmsIpAddr") + ":" + paramMap.get("dbmsPortNo") + "/" + paramMap.get("dbmsDatabaseNm");
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.DB2, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "jdbc:db2://" + paramMap.get("dbmsIpAddr") + ":" + paramMap.get("dbmsPortNo") + "/" + paramMap.get("dbmsDatabaseNm");
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.MONGODB, dbmsNm)) {
			return "mongodb://" + paramMap.get("dbmsId") + ":" + paramMap.get("dbmsPassword") + "@" + paramMap.get("dbmsIpAddr") + ":"
					+ paramMap.get("dbmsPortNo") + "/" + paramMap.get("dbmsDatabaseNm") + "";
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.VOLTDB, dbmsNm)) {
			return "jdbc:voltdb://" + paramMap.get("dbmsIpAddr") + ":" + paramMap.get("dbmsPortNo") + "";
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
		if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CSV, dbmsNm)) {
			dbmsNm = "MARIADB";
		}
		
		if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.ORACLE, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "select count(owner) as cnt from ALL_TABLES " + "where upper(owner) = upper('" + paramMap.get("dbmsDatabaseNm") + "') group by owner";
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.MYSQL, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "select count(schema_name) as cnt from INFORMATION_SCHEMA.SCHEMATA where upper(schema_name) = upper('" + paramMap.get("dbmsDatabaseNm") + "') group by schema_name";
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.MARIADB, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "select count(schema_name) as cnt from INFORMATION_SCHEMA.SCHEMATA where upper(schema_name) = upper('" + paramMap.get("dbmsDatabaseNm") + "') group by schema_name";
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.TIBERO, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "select count(owner) as cnt from ALL_TABLES where upper(owner) = upper('" + paramMap.get("dbmsDatabaseNm") + "') group by owner";
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.MSSQL, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "select count(catalog_name) as cnt from INFORMATION_SCHEMA.SCHEMATA where upper(catalog_name) = upper('" + paramMap.get("dbmsDatabaseNm") + "')";
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CUBRID, dbmsNm)) {
			return "select count(schema()) cnt";
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.POSTGRESQL, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "select count(catalog_name) as cnt from INFORMATION_SCHEMA.SCHEMATA where upper(catalog_name) = upper('" + paramMap.get("dbmsDatabaseNm") + "')";
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.ALTIBASE, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsDatabaseNm", "Database Name");
			return "select count(db_name) " + "cnt" + " from SYSTEM_.SYS_DATABASE_ where upper(db_name) = upper('" + paramMap.get("dbmsDatabaseNm") + "')";
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.DB2, dbmsNm)) {
			SangsStringUtil.checkRequiredParam(paramMap, "dbmsSchemaNm", "Schema Name");
			return "select count(schemaname) as cnt from SYSCAT.SCHEMATA where schemaname =upper('" + paramMap.get("dbmsSchemaNm") + "')";
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
	public static String getTableListSql(String dbmsNm, String tableSchema, String dbmsId) {
		if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CSV, dbmsNm)) {
			dbmsNm = "MARIADB";
		}
		
		if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.ORACLE, dbmsNm)) {
			return "SELECT T1.TABLE_NAME AS TABLE_NAME, T2.COMMENTS AS TABLE_COMMENT "
					+ "FROM ALL_TABLES T1, ALL_TAB_COMMENTS T2 "
					+ "WHERE T1.OWNER = T2.OWNER "
					+ "AND T1.TABLE_NAME = T2.TABLE_NAME "
					+ "AND UPPER(T1.OWNER) = UPPER('" + tableSchema + "')  "
					+ "ORDER BY T1.TABLE_NAME";
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.MYSQL, dbmsNm)) {
			return "SELECT TABLE_NAME AS TABLE_NAME, TABLE_COMMENT AS TABLE_COMMENT "
					+ "FROM INFORMATION_SCHEMA.TABLES "
					+ "WHERE TABLE_TYPE = 'BASE TABLE' "
					+ "AND UPPER(TABLE_SCHEMA) = UPPER('" + tableSchema + "') "
					+ "ORDER BY TABLE_NAME";
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.MARIADB, dbmsNm)) {
			return "SELECT TABLE_NAME AS TABLE_NAME, TABLE_COMMENT AS TABLE_COMMENT "
					+ "FROM INFORMATION_SCHEMA.TABLES "
					+ "WHERE TABLE_TYPE = 'BASE TABLE' "
					+ "AND UPPER(TABLE_SCHEMA) = UPPER('" + tableSchema + "') "
					+ "ORDER BY TABLE_NAME";
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.TIBERO, dbmsNm)) {
			return "SELECT T1.TABLE_NAME AS TABLE_NAME , T2.COMMENTS as TABLE_COMMENT "
					+ "FROM ALL_TABLES T1 , USER_TAB_COMMENTS t2 "
					+ "WHERE T1.TABLE_NAME = T2.TABLE_NAME(+) "
					+ "AND UPPER(OWNER) = UPPER('" + tableSchema + "') "
					+ "ORDER BY T1.TABLE_NAME";
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.MSSQL, dbmsNm)) {
			return "SELECT A.TABLE_NAME AS TABLE_NAME, B.VALUE AS TABLE_COMMENT "
					+ "FROM INFORMATION_SCHEMA.TABLES A "
					+ "	 LEFT JOIN "
					+ "		(SELECT OBJECT_ID(OBJNAME) AS TABLE_ID, VALUE "
					+ "			FROM ::FN_LISTEXTENDEDPROPERTY(NULL, 'USER','DBO','TABLE', NULL, NULL, NULL) "
					+ "		) B ON OBJECT_ID(A.TABLE_NAME) = B.TABLE_ID "
					+ "WHERE UPPER(A.TABLE_CATALOG) = UPPER('" + tableSchema + "') "
					+ "AND A.TABLE_TYPE = 'BASE TABLE' "
					+ "ORDER BY A.TABLE_NAME"; 
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CUBRID, dbmsNm)) {
			return "select UPPER(class_name) AS TABLE_NAME, COMMENT AS TABLE_COMMENT from DB_CLASS where owner_name = UPPER('"+dbmsId+"') order by class_name";
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.POSTGRESQL, dbmsNm)) {
			return "SELECT C.RELNAME AS TABLE_NAME, OBJ_DESCRIPTION(C.OID) AS TABLE_COMMENT "
					+ "FROM PG_CATALOG.PG_CLASS C INNER JOIN PG_CATALOG.PG_NAMESPACE N ON C.RELNAMESPACE=N.OID "
					+ "WHERE C.RELKIND = 'r' and UPPER(NSPNAME) = UPPER('" + tableSchema + "') "
					+ "ORDER BY 1";
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.ALTIBASE, dbmsNm)) {
			return "SELECT T1.TABLE_NAME AS TABLE_NAME "
					+ "	, (SELECT T01.COMMENTS FROM SYSTEM_.SYS_COMMENTS_ T01 WHERE T01.TABLE_NAME = T1.TABLE_NAME AND T01.COLUMN_NAME IS NULL) AS TABLE_COMMENT " 
					+ "	FROM SYSTEM_.SYS_TABLES_ T1, SYSTEM_.SYS_USERS_ T2 " 
					+ " WHERE 1 = 1 " 
					+ " AND T2.USER_NAME NOT IN ('PUBLIC', 'SYSTEM_') "
					+ " AND T1.USER_ID = T2.USER_ID "
					+ " AND UPPER(T2.USER_NAME) = UPPER('" + tableSchema + "') " 
					+ " ORDER BY T1.TABLE_NAME";
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.DB2, dbmsNm)) {
			return "SELECT TABNAME AS TABLE_NAME, TABNAME AS TABLE_COMMENT "
					+ "FROM SYSCAT.TABLES "
					+ "WHERE TABSCHEMA = UPPER('" + tableSchema + "') "
					+ "ORDER BY TABNAME";
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
	public static String getTableColumnAllListSql(String dbmsNm, String tableSchema, String dbmsId) {
		
		if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CSV, dbmsNm)) {
			dbmsNm = "MARIADB";
		}
		
		if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.ORACLE, dbmsNm)) {
			String sql = 
					"SELECT"
					+ "		UPPER(T1.TABLE_NAME) AS TABLE_NAME "
					+ "		, UPPER(T2.COLUMN_NAME) AS COLUMN_NAME "
					+ "		, T1.DATA_TYPE "
					+ "		, T1.DATA_LENGTH "
					+ " 	, T1.DATA_PRECISION "
					+ "     , T1.DATA_SCALE "
					+ "		, T2.COMMENTS AS COLUMN_COMMENT "
					+ "	FROM ALL_TAB_COLUMNS T1 JOIN USER_COL_COMMENTS T2 ON T1.TABLE_NAME = T2.TABLE_NAME AND T1.COLUMN_NAME = T2.COLUMN_NAME "
					+ "	WHERE 1 = 1 "
					+ "		AND UPPER(T1.OWNER) = UPPER('" + tableSchema + "') "
					+ "	ORDER BY T1.TABLE_NAME, T1.COLUMN_ID ";
				return sql;
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.MYSQL, dbmsNm) || BizUtil.isEqualDbms(DBMS_TYPE_NAME.MARIADB, dbmsNm)) {
			
			String sql = 
				"SELECT"
				+ "		T1.TABLE_NAME "
				+ "		, UPPER(T1.COLUMN_NAME) AS COLUMN_NAME "
				+ "		, UPPER(T1.DATA_TYPE) AS DATA_TYPE"
				+ "		, T1.CHARACTER_MAXIMUM_LENGTH AS DATA_LENGTH "
				+ "		, T1.NUMERIC_PRECISION AS DATA_PRECISION "
				+ " 	, T1.NUMERIC_SCALE AS DATA_SCALE "
				+ "		, T1.COLUMN_COMMENT "
				+ "	FROM INFORMATION_SCHEMA.COLUMNS T1 "
				+ "		, INFORMATION_SCHEMA.TABLES T2 "
				+ "	WHERE 1 = 1 "
				+ "		AND UPPER(T1.TABLE_SCHEMA) = UPPER('" + tableSchema + "') "
				+ "		AND T1.TABLE_SCHEMA = T2.TABLE_SCHEMA "
				+ "		AND T1.TABLE_NAME = T2.TABLE_NAME "
				+ "		AND T1.TABLE_CATALOG = T2.TABLE_CATALOG "
				+ "		AND T2.TABLE_TYPE = 'BASE TABLE'"
				+ "	ORDER BY T1.TABLE_NAME, T1.ORDINAL_POSITION ";
			return sql;
			
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.TIBERO, dbmsNm)) {
			
			String sql = 
				"SELECT "
				+ "		T1.OWNER "
				+ "	    , T1.COLUMN_ID "
				+ "	    , upper(T1.COLUMN_NAME) as COLUMN_NAME "
				+ "	    , upper(T1.TABLE_NAME) as TABLE_NAME"
				+ "	    , T1.DATA_TYPE "
				+ "	    , T1.DATA_LENGTH "
				+ "	    , T1.DATA_PRECISION "
				+ "	    , T1.DATA_SCALE "
				+ "	    , T1.NULLABLE "
				+ "	    , T1.DATA_DEFAULT "
				+ "   	, T2.COMMENTS AS COLUMN_COMMENT "
				+ "	FROM ALL_TAB_COLUMNS T1 "
				+ "		, ALL_COL_COMMENTS T2 "
				+ "	WHERE 1 = 1 "
				+ "		AND T1.OWNER = T2.OWNER "
				+ "		AND T1.TABLE_NAME = T2.TABLE_NAME "
				+ "		AND T1.COLUMN_NAME = T2.COLUMN_NAME "
				+ "		AND UPPER(T1.OWNER) = UPPER('" + tableSchema + "') "
				+ "	ORDER BY T1.TABLE_NAME, T1.COLUMN_ID ";
			return sql;
			
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.MSSQL, dbmsNm)) {
			return "select " 
					+" t1.table_catalog "
					+"	, upper(t1.table_name) as TABLE_NAME "
					+"	, upper(t1.column_name) as COLUMN_NAME "
					+"	, t1.data_type "
					+"	, t1.character_maximum_length as data_length "
					+"	, t3.precision as data_precision "
					+"	, t3.scale as data_scale "
					+"	, t1.is_nullable as nullable "
					+"	, t1.column_default as data_default "
					+"	, t2.value as COLUMN_COMMENT "
					+" from INFORMATION_SCHEMA.COLUMNS t1 "
					+"		left outer join "
					+"		SYS.EXTENDED_PROPERTIES t2 "
					+"		on t2.major_id = object_id(t1.table_name) "
					+"		and t2.minor_id = t1.ordinal_position "
					+"		left outer join SYS.COLUMNS t3 "
					+"		on t3.object_id = object_id(t1.table_name) "
					+"		and t3.name = t1.column_name "
					+"	where 1 = 1 "
					+ " and upper(t1.table_catalog) = upper('" + tableSchema + "') "
					+ " order by t1.table_name, t1.ordinal_position ";
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CUBRID, dbmsNm)) {
			String sql = 
					"	select "
					+ "		UPPER(t1.attr_name) as COLUMN_NAME "
					+ "		, UPPER(t1.class_name) as TABLE_NAME "
					+ "		, CASE WHEN T1.DATA_TYPE = 'STRING' THEN 'VARCHAR' ELSE T1.DATA_TYPE END as DATA_TYPE "
					+ "		, t1.prec as DATA_LENGTH "
					+ "		, t1.prec as DATA_PRECISION "
					+ "		, t1.scale as DATA_SCALE "
					+ "		, t1.comment as COLUMN_COMMENT "
					+ "	from DB_ATTRIBUTE t1, DB_CLASS t2 "
					+ "	where t2.is_system_class ='NO' "
					+ "		and t1.class_name = t2.class_name "
					+ "		and UPPER(t2.owner_name) = UPPER('"+dbmsId+"') "
					+ "	order by t1.class_name, t1.def_order ";
			return sql;
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.POSTGRESQL, dbmsNm)) {
			return "select "
					+ "	upper(t1.table_name) as TABLE_NAME " 
					+ "	, upper(t1.column_name) as COLUMN_NAME"
					+ "	, t1.udt_name as data_type " 
					+ "	, t1.character_maximum_length as data_length " 
					+ "	, t1.numeric_precision as data_precision " 
					+ "	, t1.is_nullable as nullable "
					+ "	, t1.numeric_scale as data_scale " 
					+ "	, t1.column_default as data_default "
					+ "	, (" 
					+ "		select t02.description " 
					+ "		from pg_stat_all_tables t01 " 
					+ "			, pg_description t02 "
					+ "			, pg_attribute t03 " 
					+ "		where t01.relid=t02.objoid " 
					+ "			and t02.objoid=t03.attrelid "
					+ "			and t02.objsubid=t03.attnum "
					+ "			and t01.schemaname= t1.table_schema " 
					+ "			and t03.attname = t1.column_name "
					+ "	) as COLUMN_COMMENT "
					+ " from information_schema.columns t1 "
					+ " where UPPER(t1.table_schema) = UPPER('" + tableSchema + "') " 
					+ " order by t1.table_name, t1.ordinal_position";
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.ALTIBASE, dbmsNm)) {
			return "select " 
					+ "	t2.table_name as TABLE_NAME "  
					+ "	, t1.column_name as COLUMN_NAME " 
					+ "	, t1.is_nullable as nullable " 
					+ "	, t1.default_val as data_default " 
					+ "	, t1.scale as data_scale "
					+ "	, t1.precision as data_length " 
					+ "	, t1.precision as data_precision " 
					+ "	, (select t01.comments from SYSTEM_.SYS_COMMENTS_ t01 where t01.column_name = t1.column_name and t01.table_name = t2.table_name) as COLUMN_COMMENT " 
					+ "	, DECODE(t1.data_type, 1, 'CHAR', 12, 'VARCHAR', -8, 'NCHAR', -9, 'NVARCHAR', 2, 'NUMERIC/DECIMAL', 6, 'FLOAT/NUMBER', 8, 'DOUBLE', 7, 'REAL', -5, 'BIGINT', 4, 'INTEGER', 5, 'SMALLINT', 9, 'DATE', 30, 'BLOB', 40, 'CLOB', 20001, 'BYTE', 20002, 'NIBBLE', -7, 'BIT', -100, 'VARBIT', 10003, 'GEOMETRY') as data_type " 
					+ " from "
					+ "	 SYSTEM_.SYS_COLUMNS_ t1 " 
					+ "	, SYSTEM_.SYS_TABLES_ t2 " 
					+ "	, SYSTEM_.SYS_USERS_ t3 " 
					+ "	where 1 = 1  "
					+ " and t3.user_name not in ('PUBLIC', 'SYSTEM_') " 
					+ "	and t1.table_id = t2.table_id "
					+ "	and t3.user_id = t2.user_id "
					+ " order by t2.table_name, t1.column_order";
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.DB2, dbmsNm)) {
			return "select " 
					+ "	tabschema "
					+ "	, upper(tabname) as TABLE_NAME " 
					+ "	, upper(colname) as COLUMN_NAME " 
					+ "	, typename as data_type "
					+ "	, length as data_length " 
					+ "	, stringunitslength as data_precision " 
					+ "	, nulls as nullable "
					+ "	, scale as data_scale " 
					+ "	, remarks as COLUMN_COMMENT " 
					+ "	, default as data_default " 
					+ "	from  "
					+ "	SYSCAT.COLUMNS " 
					+ " where upper(tabschema) = upper('" + tableSchema + "')" 
					+ " order by tabname, colno";
		} else { 
			throw new SangsMessageException("Not Support DBMS ["+dbmsNm+"]");
		}
	}


	
}
