package com.sangs.common.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.common.CommonConstant;
import com.sangs.common.support.BizUtil;
import com.sangs.common.support.BizUtil.DBMS_TYPE_NAME;
import com.sangs.common.support.CommonDao;
import com.sangs.common.support.DbmsConnUtil;
import com.sangs.dq.service.MongoDbService;
import com.sangs.dq.service.VoltDbService;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsEncryptUtil;
import com.sangs.lib.support.utils.SangsStringUtil;

@SangsService
public class DbmsConnService extends ServiceBase {

	@Autowired
	private CommonDao dao;
	
	/**
	 * DBMS 연결 테스트
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> doConnectionTest(Map<String, Object> paramMap) throws Exception {
		logger.debug("parameter : " + paramMap);
		if(paramMap.containsKey("stdSetSn") && paramMap.containsKey("prjctSn")) {
			SangsMap info = dao.selectOne("cmmn_project.selectProjectInfo", paramMap);
			paramMap.clear();
			paramMap.putAll(info);
		}
		
		String dbmsNm = String.valueOf(paramMap.get("dbmsNm"));
		String dbmsPassword = String.valueOf(paramMap.get("dbmsPassword"));

		if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.MONGODB, dbmsNm) || BizUtil.isEqualDbms(DBMS_TYPE_NAME.VOLTDB, dbmsNm)) {
			String decryptPwdValue = SangsEncryptUtil.decrypt_AES128(dbmsPassword, CommonConstant.CRYPT_AES_KEY);
			paramMap.put("dbmsPassword", decryptPwdValue);
		}
		return doConnectionTestProc(paramMap);
	}
	/**
	 * DBMS 연결 테스트
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> doConnectionTestDirct(Map<String, Object> paramMap) throws Exception {
		logger.debug("parameter : " + paramMap);
		 
		String dbmsPassword = SangsStringUtil.checkRequiredParamStr(paramMap, "dbmsPassword", "비밀번호");
	 
		// 패스워드 암호화
		paramMap.put("dbmsPassword", SangsEncryptUtil.encrypt_AES128(dbmsPassword, CommonConstant.CRYPT_AES_KEY));
	
		return doConnectionTestProc(paramMap);
	}
	
	
	/**
	 * DBMS 연결 테스트 수행 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> doConnectionTestProc(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		String message = "";

		int resCnt = 0;

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		boolean result = false;
		
		String dbmsNm = String.valueOf(paramMap.get("dbmsNm"));
		String dbmsId = String.valueOf(paramMap.get("dbmsId"));
		String dbmsPassword = String.valueOf(paramMap.get("dbmsPassword"));
		String orginlDbmsPassword = String.valueOf(paramMap.get("dbmsPassword"));

		try {
			String driverNm = DbmsConnUtil.getDbmsDriverNm(dbmsNm);
			String dbmsUrl = DbmsConnUtil.getDbmsUrl(dbmsNm, paramMap);
			
			if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.MONGODB, dbmsNm)) {
				/*
				MongoDatabase mongoDatabase = new MongoDbService().getConnection(dbmsUrl);
				List<String> collectionList = new ArrayList<>();
				for (String name : mongoDatabase.listCollectionNames()) {
					collectionList.add(name);
				}
				resCnt = collectionList.size();
				*/
				resCnt = new MongoDbService().getConnectTest(dbmsUrl, paramMap);

			} else if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.VOLTDB, dbmsNm)) {

				Map<String, Object> voltDatabase = new VoltDbService().getConnection(driverNm, dbmsUrl, paramMap);
				resCnt = (int) voltDatabase.get("tableCnt");

			} else {
				
				// dbms 에 대한 접속 String get
				String sql = DbmsConnUtil.getDbmsHealthCheckSql(dbmsNm, paramMap);
				
				logger.info("[ 드라이버 ] : " + driverNm);
				logger.info("[ 주소 ] : " + dbmsUrl);
				logger.info("[ SQL ] : " + sql);
				logger.info("[ 연결 시작 ] : " + dbmsNm);

				
				// get connection
				connection = DbmsConnUtil.getConnection(driverNm, dbmsUrl, dbmsId, dbmsPassword);
				statement = connection.createStatement();
				resultSet = statement.executeQuery(sql);
				
				if (resultSet != null) {
					if (resultSet.next()) {
						resCnt = resultSet.getInt("cnt");
					} else {
						result = false;
						message = "DATABASE명 혹은 SID를 확인해주세요";
						logger.info(message);
					}
				} else {
					result = false;
					logger.error("[ 연결실패 ]");
					message = "Database 및 Schema 입력 정보를 확인 해 주세요.";
					
				}
			}
			
			if("".equals(message)) {
				if (resCnt >= 0) {
					result = true;
					logger.info("[연결 성공]");
					message = "연결 성공";
				} else {
					result = false;
					logger.info("[연결 실패]");
					message = "입력하신 DBMS 정보로 연결 할 수가 없습니다. 입력정보를 확인 하세요.";
				}
			}
			
			rtnMap.put("result", result);
			rtnMap.put("message", message);
		} catch (SangsMessageException e) {
			rtnMap.put("result", false);
			rtnMap.put("message", e.getMessage());
			logger.error("[ 로드 오류 ]" + e.getStackTrace());
			logger.error("[ error ] : " + e.getMessage());
		} catch (ClassNotFoundException e) {
			rtnMap.put("result", false);
			rtnMap.put("message", e.getMessage());
			logger.error("[ 로드 오류 ]" + e.getStackTrace());
			logger.error("[ error ] : " + e.getMessage());
			e.printStackTrace();
		} catch (SQLException e) {
			rtnMap.put("result", false);
			rtnMap.put("message", e.getMessage());
			logger.error("[ error ] : " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			rtnMap.put("result", false);
			rtnMap.put("message", "처리중 에러가 발생하였습니다.");
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
		paramMap.put("dbmsPassword", orginlDbmsPassword);
		return rtnMap;
	}
}
