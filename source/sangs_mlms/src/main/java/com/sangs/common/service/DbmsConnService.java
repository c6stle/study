package com.sangs.common.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.support.DbmsConnUtil;
import com.sangs.common.support.DbmsConnUtil.DBMS_TYPE_NAME;
import com.sangs.fwk.annotation.SangsService;

@SangsService
public class DbmsConnService extends ServiceBase {


	
	
	/**
	 * DBMS 연결 테스트
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> doConnectionTest(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		logger.debug("parameter : " + paramMap);
		
		String dbmsNm = paramMap.get("dbmsNm").toString();
		String dbmsId = paramMap.get("dbmsId").toString();
		String dbmsPassword = paramMap.get("dbmsPassword").toString();

		String driverNm = "";
		String dbmsUrl = "";
		String sql = "";
		String message = "";

		int resCnt = 0;

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		boolean result = false;
		boolean check = false;

		// dbms 에 대한 접속 String get
		driverNm = DbmsConnUtil.getDbmsDriverNm(dbmsNm);
		dbmsUrl = DbmsConnUtil.getDbmsUrl(dbmsNm, paramMap);
		sql = DbmsConnUtil.getDbmsHealthCheckSql(dbmsNm, paramMap);
		 

		try {

			logger.info("[ 드라이버 ] : " + driverNm);
			logger.info("[ 주소 ] : " + dbmsUrl);
			logger.info("[ SQL ] : " + sql);
			logger.info("[ 연결 시작 ] : " + dbmsNm);

			//if (!DBMS_TYPE_NAME.mongoDB.toString().equals(dbmsId)) {
			if(!DbmsConnUtil.isEqualDbms(DBMS_TYPE_NAME.MONGODB, dbmsId)) {
				
				// get connection
				connection = DbmsConnUtil.getConnection(driverNm, dbmsUrl, dbmsId, dbmsPassword);

				statement = connection.createStatement();

				resultSet = statement.executeQuery(sql);
				
				if (resultSet != null) {
					if (resultSet.next()) {
						
						resCnt = resultSet.getInt("cnt");
						
						if (resCnt > 0) {
							
							check = true;
							result = check;
							logger.info("[ 연결 성공 ]");
							message = "연결 성공";
							
						}
					}
				} else {
					
					check = false;
					result = check;
					logger.error("[ 연결실패 ]");
					message = "Database 및 Schema 입력 정보를 확인 해 주세요.";
					
				}
			} else {
				// mongoDB연결
				if (resCnt > 0) {
					
					logger.info("[ 연결 성공 ]");
					check = true;
					result = check;
					message = "연결 성공";
					
				} else {
					
					logger.error("[ 연결 실패 ]");
					check = false;
					result = check;
					message = "입력하신 DBMS 정보로 연결 할 수가 없습니다. 입력정보를 확인 하세요.";
					
				}
			}

			rtnMap.put("result", result);
			rtnMap.put("message", message);

		} catch (ClassNotFoundException e) {
			
			rtnMap.put("result", result);
			rtnMap.put("message", e.getMessage());
			
			logger.error("[ 로드 오류 ]" + e.getStackTrace());
			logger.error("[ error ] : " + e.getMessage());
			
			e.printStackTrace();
			
		} catch (SQLException e) {
			
			rtnMap.put("result", result);
			rtnMap.put("message", e.getMessage());
			
			logger.error("[ error ] : " + e.getMessage());
			
			e.printStackTrace();
			
		} finally {
			if (connection != null) {
				connection.close();
			}
			if (statement != null) {
				statement.close();
			}
		}
		return rtnMap;
	}
}
