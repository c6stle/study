package com.sangs.common.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.support.AuthUtil;
import com.sangs.common.support.BizUtil;
import com.sangs.common.support.BizUtil.DBMS_TYPE_NAME;
import com.sangs.common.support.CommonDao;
import com.sangs.common.support.DbmsConnUtil;
import com.sangs.dq.service.MongoDbService;
import com.sangs.dq.service.VoltDbService;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;

@SangsService
public class DbmsCatalogSearchService extends ServiceBase {
	

	@Autowired
	private CommonDao dao;
	 
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	MongoDbService mongoDbService;
	
	@Autowired
	VoltDbService voltDbService;
	 
	/**
	 * 현재 프로젝트의 DBMS 테이블 목록 조회
	 * @param pjctSn
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getCurrDbmsTableColumnList(Map<String, Object> paramMap) throws Exception {
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		try {
 
			List<Map<String, Object>> tableList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> tableColumnList = new ArrayList<Map<String, Object>>();
			
			
			// 프로젝트에 대한 DB연결 정보 조회
			Map<String, Object> searchMap = new HashMap<String, Object>();
			searchMap.put("prjctSn", AuthUtil.getPrjctSn());
			Map<String, Object> rtnDbmsMap = projectService.getProjectDbmsCnncInfo(searchMap);
			SangsMap dbmsCnncInfo = (SangsMap)rtnDbmsMap.get("info");
			if(dbmsCnncInfo == null) {
				throw new SangsMessageException("DBMS 접속정보가 존재 하지 않습니다.");
			}
			
			String dbmsNm = dbmsCnncInfo.getString("dbmsNm");
			String dbmsDatabaseNm = dbmsCnncInfo.getString("dbmsDatabaseNm");
			String dbmsSchemaNm = dbmsCnncInfo.getString("dbmsSchemaNm");
			String dbmsId = dbmsCnncInfo.getString("dbmsId");
			String dbmsPassword = dbmsCnncInfo.getString("dbmsPassword");
			
			if(!"".equals(dbmsSchemaNm)){
				// PostgreSQL, DB2, Altibase
				dbmsDatabaseNm = dbmsSchemaNm;
			}
			/*
			String exceptTabs = "";
			if(paramMap.get("exceptTabs") != null)
				exceptTabs = (String)paramMap.get("exceptTabs");
			
			String exceptTabsArr[] = exceptTabs.split(",");
			HashMap<String, String> exceptTabMap = new HashMap<String, String>();
			for(String et : exceptTabsArr) {
				exceptTabMap.put(et.trim().toUpperCase() , et.trim().toUpperCase());
			}
			*/
			
			SangsMap exclTblInfoMap = dao.selectOne("meta_stdanals.selectStdAnalsExclTblInfo", paramMap);
			
			String exceptTabs = "";
			if(exclTblInfoMap != null && exclTblInfoMap.get("exclTblCn") != null)
				exceptTabs = exclTblInfoMap.getString("exclTblCn");
			
			String exceptTabsArr[] = exceptTabs.split(",");
			HashMap<String, String> exceptTabMap = new HashMap<String, String>();
			for(String et : exceptTabsArr) {
				exceptTabMap.put(et.trim().toUpperCase() , et.trim().toUpperCase());
			}
			
			
			if(!BizUtil.isEqualDbms(DBMS_TYPE_NAME.MONGODB, dbmsNm)) {
				
				// connection create
				conn = DbmsConnUtil.getConnection(DbmsConnUtil.getDbmsDriverNm(dbmsNm), DbmsConnUtil.getDbmsUrl(dbmsNm, dbmsCnncInfo), dbmsId, dbmsPassword);
				
				
				if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.VOLTDB, dbmsNm)) {
					tableList = voltDbService.selectAnalysisTableList();
					tableColumnList = voltDbService.selectAnalysisTableColumnList();
				} else {
					// 테이블 목록 조회 sql
					String tableListSql = DbmsConnUtil.getTableListSql(dbmsNm, dbmsDatabaseNm, dbmsId);
					stmt = conn.createStatement();
					rs = stmt.executeQuery(tableListSql);
					
					while (rs.next()) {
						String tableName = rs.getString("TABLE_NAME");
						String tableComment = rs.getString("TABLE_COMMENT");
						
						// 제외 테이블이 아닌경우만 add
						if (!exceptTabMap.containsKey(tableName.toUpperCase())) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("tableName", tableName);
							map.put("tableComment", tableComment);
							tableList.add(map);
						}
					}
					
					// 테이블 컬럼 목록 조회(전체목록)
					String tableColumnAllListSql = DbmsConnUtil.getTableColumnAllListSql(dbmsNm, dbmsDatabaseNm, dbmsId);
					
					rs2 = stmt.executeQuery(tableColumnAllListSql);
					
					while (rs2.next()) {
						
						String tableName = rs2.getString("TABLE_NAME");
						String columnName = rs2.getString("COLUMN_NAME");
						String dataType = rs2.getString("DATA_TYPE");
						String dataLength = rs2.getString("DATA_LENGTH");
						String columnComment = rs2.getString("COLUMN_COMMENT");
						String dataPrecision = rs2.getString("DATA_PRECISION");
						String dataScale = rs2.getString("DATA_SCALE");
						
						// 제외 테이블이 아닌경우만 add
						if (!exceptTabMap.containsKey(tableName.toUpperCase())) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("tableName", tableName);
							map.put("columnName", columnName);
							map.put("dataType", dataType);
							map.put("dataLength", dataLength);
							map.put("dataPrecision", dataPrecision);
							map.put("dataScale", dataScale);
							map.put("columnComment", columnComment);
							map.put("dataTypeLength", BizUtil.getDataTypeLengthTxt(dbmsNm, dataType, dataLength, dataPrecision, dataScale));
							
							tableColumnList.add(map);
						}
					}
				}
				
			} else {
				tableList = mongoDbService.selectAnalysisTableList();
				tableColumnList = mongoDbService.selectAnalysisTableColumnList();
			}
			
			rtnMap.put("tableList", tableList);
			rtnMap.put("tableColumnList", tableColumnList);
			rtnMap.put("dbmsCnncInfo", dbmsCnncInfo);
			

		} catch(SangsMessageException e) {
			e.printStackTrace();
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		} finally {
			if(rs != null)
				rs.close();
			if(rs2 != null)
				rs2.close();
			if(stmt != null)
				stmt.close();
			if(conn != null)
				conn.close();
		}
		
		return rtnMap;
	}
	
	
	
	
	
	 

}
