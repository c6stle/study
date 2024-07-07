package com.sangs.dq.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.support.CronExpression;

import com.sangs.common.support.AuthUtil;
import com.sangs.common.support.BizUtil;
import com.sangs.common.support.BizUtil.DBMS_TYPE_NAME;
import com.sangs.common.support.BizUtil.DbmsDataTypeGroup;
import com.sangs.common.support.CommonDao;
import com.sangs.dq.config.AnalsSqlSessionTemplate;
import com.sangs.dq.mapper.RuleMngMapper;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.fwk.common.SangsConstants;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.domain.SangsPagingViewInfo;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsSimpleExcelMaker;
import com.sangs.lib.support.utils.SangsStringUtil;
import com.sangs.lib.support.utils.SangsWebUtil;

@SangsService
public class ProfileMngService {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CommonDao dao;
	
	@Autowired
	private VoltDbService voltDbService;
	
	@Autowired
	private MongoDbService mongoDbService;
	
	@Autowired
	private RuleMngMapper ruleMngMapper;
	
	private AnalsSqlSessionTemplate sqlSession = new AnalsSqlSessionTemplate();
	
	/**
	 * 등록된 프로파일 리스트 조회
	 * 
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> getProfileList(Map<String, Object> params) throws Exception { 
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {		
			Map<String, Object> smap = new HashMap<String, Object>();
			smap.put("prjctSn", AuthUtil.getPrjctSn());
			smap.put("proflNm", params.get("proflNm"));
			
			int pageNum = SangsStringUtil.nvlInt(params.get("pageNum"), 1);
			int totCnt = dao.selectCount("dq_profile.selectProfileListCnt", smap);
			
			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totCnt, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);
			smap.put("pageSize", pagingInfo.getPageSize());
			smap.put("offset", pagingInfo.getOffset());
			
			List<SangsMap> profileList = new ArrayList<SangsMap>();
			if (totCnt != 0) {
				profileList = dao.selectList("dq_profile.selectProfileList", smap);
				
				for (SangsMap map:profileList) {
					if ("N".equals(map.get("useYn"))) {
						map.putOrg("nextExcSchdulDt", "비활성");
					}
				}
				
			}
			
			rtnMap.put("profileList", profileList);
			rtnMap.put("pagingInfo", pagingInfo);
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("getProfileList 조회 오류");
		}
		
		return rtnMap;
	}
	
	
	
	/**
	 * 진단 테이블 리스트 조회
	 * 
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> getDiagnosisTableList(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		logger.debug("params : " + params);
		
		try {
			Map<String, Object> smap = new HashMap<String, Object>();
			smap.put("prjctSn", AuthUtil.getPrjctSn());
			smap.put("proflSn", params.get("proflSn"));
			
			int pageNum = SangsStringUtil.nvlInt(params.get("pageNum"), 1);
			int totCnt = dao.selectCount("dq_profile.selectDiagnosisTableListCnt", smap);
			
			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totCnt, pageNum, 17);
			smap.put("pageSize", pagingInfo.getPageSize());
			smap.put("offset", pagingInfo.getOffset());
			
			List<SangsMap> dgnssTblList = new ArrayList<SangsMap>();
			if (totCnt != 0)
				dgnssTblList = dao.selectList("dq_profile.selectDiagnosisTableList", smap);
			
			rtnMap.put("dgnssTblList", dgnssTblList);
			rtnMap.put("pagingInfo", pagingInfo);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("getDgnssTblList 조회 오류");
		}
		
		return rtnMap;
	}
	
	
	
	
	/**
	 * 프로파일 등록/수정
	 * 
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> regProflInfo(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
			String proflNm = (String)params.get("proflNm");
			
			if ("R".equals(params.get("pmode"))) {
				int proflSn = dao.selectInteger("dq_profile.selectNextProfileSn", null);
				
				Map<String, Object> imap = new HashMap<String, Object>();
				imap.put("prjctSn", AuthUtil.getPrjctSn());
				imap.put("proflSn", proflSn);
				imap.put("proflNm", SangsWebUtil.clearXSSMinimum(proflNm));
				imap.put("regUserId", AuthUtil.getUserId());
				
				dao.insert("dq_profile.insertProfileInfo", imap);
			} else {
				
				Map<String, Object> umap = new HashMap<String, Object>();
				umap.put("proflSn", params.get("proflSn"));
				umap.put("proflNm", SangsWebUtil.clearXSSMinimum(proflNm));
				umap.put("useYn", params.get("useYn"));
				umap.put("regUserId", AuthUtil.getUserId());
				dao.update("dq_profile.updateProfileInfo", umap);
			}
			
			rtnMap.put("resultCd", "OK");
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("regProflInfo 신규 등록 오류");
		}
		
		return rtnMap;
	}
	
	
	
	
	/**
	 * 프로파일 관리 - 프로파일명 수정
	 * 
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> delProfileInfo(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			Map<String, Object> umap = new HashMap<String, Object>();
			umap.put("proflSn", params.get("proflSn"));
			
			dao.update("dq_profile.deleteProfileInfo", umap);
			rtnMap.put("resultCd", "OK");
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("delProfileInfo 프로파일 삭제 오류");
		}
		
		return rtnMap;
	}
	
	
	
	/**
	 * 연결된 데이터베이스 테이블명 조회
	 * 
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> getDatabaseTable(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			String dbmsDatabaseNm = AuthUtil.getDbmsDatabaseNm();
			String dbmsSchemaNm = AuthUtil.getDbmsSchemaNm();
			if(!"".equals(dbmsSchemaNm)){
				// PostgreSQL, DB2, Altibase
				dbmsDatabaseNm = dbmsSchemaNm;
			}
			params.put("dbmsDatabaseNm", dbmsDatabaseNm);
			
			String dbmsNm = AuthUtil.getDbmsNm();
			
			List<Map<String, Object>> tableList = new ArrayList<Map<String, Object>>();
			
			if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.MONGODB, dbmsNm)) {
				tableList = mongoDbService.selectAnalysisTableList();
			} else if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.VOLTDB, dbmsNm)) {
				tableList = voltDbService.selectAnalysisTableList();
			} else {
				if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CUBRID, dbmsNm)) {
					params.put("dbmsId", AuthUtil.getDbmsId());
				} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CSV, dbmsNm)) {
					params.put("dbmsDatabaseCn", AuthUtil.getDbmsDatabaseCn());
				}
				
				tableList = sqlSession.selectList("AnalysisMapper.selectAnalysisTableList", params);
			}
			
			SangsMap selectTableInfo = new SangsMap();
			if ("M".equals(params.get("pmode"))) {
				selectTableInfo = dao.selectOne("dq_profile.selectDgnssTableNm", params);
			}
			
			rtnMap.put("selectTableNm", selectTableInfo.get("tblNm"));
			rtnMap.put("tableList", tableList);
			rtnMap.put("resultCd", "OK");
			
		} catch (Exception e) {
			throw new SangsMessageException("getDatabaseTable 테이블명 조회 오류");
		}
		
		return rtnMap;
	}
	
	
	
	/**
	 * 컬럼 정보 조회, 샘플 데이터 조회(30건)
	 * 
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> getDgnssColInfo(Map<String, Object> params) throws Exception {		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			
			String dbmsDatabaseNm = AuthUtil.getDbmsDatabaseNm();
			String dbmsSchemaNm = AuthUtil.getDbmsSchemaNm();
			String dbmsTableNm = String.valueOf(params.get("dbmsTableNm"));
			int rowCnt = 30;
			if(!"".equals(dbmsSchemaNm)){
				// PostgreSQL, DB2, Altibase
				dbmsDatabaseNm = dbmsSchemaNm;
			}
			map.put("dbmsDatabaseNm", dbmsDatabaseNm);
			map.put("dbmsTableNm", dbmsTableNm);
			map.put("rowCnt", rowCnt);
			
			String dbmsNm = AuthUtil.getDbmsNm();
			
			List<Map<String, Object>> colInfoList = new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> sampleDataList = new ArrayList<Map<String,Object>>();
			
			if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.MONGODB, dbmsNm)) {
				colInfoList = mongoDbService.getDiagnosisColumnList(dbmsTableNm);
				sampleDataList = mongoDbService.selectTableRowDataList(dbmsTableNm, rowCnt);
			} else if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.VOLTDB, dbmsNm)) {
				colInfoList = voltDbService.getDiagnosisColumnList(dbmsTableNm);
				sampleDataList = voltDbService.selectTableRowDataList(dbmsTableNm, colInfoList, rowCnt);
			} else {
				if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CUBRID, dbmsNm)) {
					map.put("dbmsId", AuthUtil.getDbmsId());
				}
				
				colInfoList = sqlSession.selectList("AnalysisMapper.selectAnalysisTableColumnList", map);
				sampleDataList = sqlSession.selectList("AnalysisMapper.selectTableRowDataList", map);
				if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CSV, dbmsNm)) {
					
					SangsMap colNmMap = new SangsMap();
					String comments = "";
					for(Map<String,Object> colMap : colInfoList) {
						comments = String.valueOf(colMap.get("comments"));
						String columnNm = String.valueOf(colMap.get("columnNm"));
						colMap.put("column_name", comments);
						colNmMap.put(columnNm, comments);
					}
					List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
					for(Map<String,Object> colMap : sampleDataList) {
						SangsMap dataMap = new SangsMap();
						for(String key : colMap.keySet()) {
							if(colNmMap.containsKey(key)) {
								dataMap.put(String.valueOf(colNmMap.get(key)), colMap.get(key));
							}
						}
						list.add(dataMap);
					}
					sampleDataList = list;
					
				}
			}
			
			
			List<SangsMap> bfeColInfoList = new ArrayList<SangsMap>();
			if ("M".equals(params.get("pmode"))) {
				// 이미 등록되어 있는 컬럼 진단정보 조회
				bfeColInfoList = dao.selectList("dq_profile.selectDgnssColInfo", params);
			}
			
			ArrayList<String> colNmArrList = new ArrayList<>();
			DbmsDataTypeGroup getDataTypeInfo;
			String trgtDataType = "";
		    for (Iterator<Map<String, Object>> it = colInfoList.iterator(); it.hasNext();) {
		        Map<String, Object> info = it.next();

		        String dataType = String.valueOf(info.get("dataType"));
		        
		        getDataTypeInfo = DbmsDataTypeGroup.findByDataType(dataType);
				trgtDataType = getDataTypeInfo.getTypeNm();

				if ("LARGEOBJECT".equals(trgtDataType)) {
					colNmArrList.add(String.valueOf(info.get("columnName")).replace("_", "").toUpperCase());
		            it.remove();
				}
		    }
			
			if(!colNmArrList.isEmpty()) {
			    for (Iterator<Map<String, Object>> it = sampleDataList.iterator(); it.hasNext();) {
			    	Map<String, Object> dataMap = it.next();
			        
			        for(String key : dataMap.keySet()) {
			        	if(colNmArrList.contains(key.toUpperCase())) {
			        		it.remove();
			        	}
			        }
			    }
			}
		    
			rtnMap.put("bfeColInfoList", bfeColInfoList);
			rtnMap.put("sampleDataList", sampleDataList);
			rtnMap.put("colInfoList", colInfoList);
			rtnMap.put("resultCd", "OK");
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("getDgnssColInfo 컬럼정보 조회 오류");
		}
		return rtnMap;
	}
	
	/**
	 * 진단 패턴/룰 조회
	 * 
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> getDgnssColRule(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			Map<String, Object> smap = new HashMap<String, Object>();
			smap.put("dbmsSn", AuthUtil.getDbmsSn());
			smap.put("useYn", "Y"); // 사용여부 Y인것만 조회
			List<Map<String, Object>> ruleInfoList = ruleMngMapper.selectPatternList(smap);

			if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.MONGODB, AuthUtil.getDbmsNm())) {

				for (Iterator<Map<String, Object>> it = ruleInfoList.iterator(); it.hasNext();) {
					Map<String, Object> obj = it.next();

					String ruleSeCd = String.valueOf(obj.get("ruleSeCd"));
					if ("AG000601".equals(ruleSeCd) || "AG000602".equals(ruleSeCd) || "AG000603".equals(ruleSeCd)) {
						it.remove();
					}
				}
			}

			List<SangsMap> dgnssColRuleInfo = new ArrayList<SangsMap>();
			if ("M".equals(params.get("pmode"))) {
				dgnssColRuleInfo = dao.selectList("dq_profile.selectDgnssColRuleInfo", params);
			}

			rtnMap.put("dgnssColRuleInfo", dgnssColRuleInfo);
			rtnMap.put("ruleInfoList", ruleInfoList);
			rtnMap.put("chkColNm", params.get("chkColNm"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("getDgnssColRule 컬럼정보 조회 오류");
		}

		return rtnMap;
	}
	
	
	
	/**
	 * 프로파일 점검 테이블 추가/수정(테이블정보, 컬럼, 컬럼 룰)
	 * 
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> saveProflTblInfo(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
			if ("R".equals(params.get("pmode"))) {
				params.put("tblSn", dao.selectInteger("dq_profile.selectNextTblSn", params));
			}
			
			String dbmsDatabaseNm = AuthUtil.getDbmsDatabaseNm();
			String dbmsSchemaNm = AuthUtil.getDbmsSchemaNm();
			if(!"".equals(dbmsSchemaNm)){
				// PostgreSQL, DB2, Altibase
				dbmsDatabaseNm = dbmsSchemaNm;
			}
			
			// 기존 정보 삭제
			dao.delete("dq_profile.deleteProflDgnssColRule", params);
			dao.delete("dq_profile.deleteProflDgnssCol", params);
			dao.delete("dq_profile.deleteProflDgnssTbl", params);
			
			// 진단 테이블정보 insert(proflSn, tblSn, schemaNm, 
			Map<String, Object> tblInsrtMap = new HashMap<String, Object>(); 
			tblInsrtMap.put("proflSn", params.get("proflSn"));
			tblInsrtMap.put("tblSn", params.get("tblSn"));
			tblInsrtMap.put("dbmsDatabaseNm", dbmsDatabaseNm);
			tblInsrtMap.put("tblNm", params.get("tblNm"));
			tblInsrtMap.put("delYn", "N");
			tblInsrtMap.put("regUserId", AuthUtil.getUserId());
			if (!"".equals(params.get("crtrYmdColNm"))) {
				tblInsrtMap.put("crtrYmdColNm", params.get("crtrYmdColNm"));
			}
			dao.insert("dq_profile.insertProflDgnssTbl", tblInsrtMap);
			
			List<Map<String, Object>> chkColInfo = (List<Map<String, Object>>) params.get("chkColInfo");
			for (Map<String, Object> colInsrtMap : chkColInfo) {
				// 진단 컬럼정보 insert
				colInsrtMap.put("proflSn", params.get("proflSn"));
				colInsrtMap.put("tblSn", params.get("tblSn"));
				colInsrtMap.put("regUserId", AuthUtil.getUserId());
				dao.insert("dq_profile.insertProflDgnssCol", colInsrtMap);
			}
			
			List<Map<String, Object>> chkColRule = (List<Map<String, Object>>) params.get("chkColRule");
			for (Map<String, Object> colRuleInsrtMap : chkColRule) {

				colRuleInsrtMap.put("proflSn", params.get("proflSn"));
				colRuleInsrtMap.put("tblSn", params.get("tblSn"));
				colRuleInsrtMap.put("nullDataDgnssYn", colRuleInsrtMap.get("nullDataDgnssYn"));
				colRuleInsrtMap.put("regUserId", AuthUtil.getUserId());
				dao.insert("dq_profile.insertProflDgnssColRule", colRuleInsrtMap);
			}
			rtnMap.put("resultCd", "OK");
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("saveProflTblInfo 프로파일 테이블 저장 오류");
		}
		
		return rtnMap;
	}
	
	
	
	/**
	 * 진단 테이블 삭제(업데이트)
	 * 
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> delProflDgnssTbl(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			dao.update("dq_profile.deleteDgnssTbl", params);
			
			rtnMap.put("resultCd", "OK");
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("delProflDgnssTbl 진단 테이블 삭제");
			
		}
		return rtnMap;
	}
	
	
	
	/**
	 * 진단 샘플 데이터 목록 조회
	 * 
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> getDgnssSampleDataList(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			String dbmsNm = AuthUtil.getDbmsNm();
			String dbmsDatabaseNm = AuthUtil.getDbmsDatabaseNm();
			String dbmsSchemaNm = AuthUtil.getDbmsSchemaNm();
			String dbmsTableNm = String.valueOf(params.get("dbmsTableNm"));
			int rowCnt = 300;
			if(!"".equals(dbmsSchemaNm)){
				// PostgreSQL, DB2, Altibase
				dbmsDatabaseNm = dbmsSchemaNm;
			}
			map.put("dbmsDatabaseNm", dbmsDatabaseNm);
			map.put("dbmsTableNm", dbmsTableNm);
			map.put("rowCnt", rowCnt);
			
			List<Map<String, Object>> sampleDataList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> colInfoList = new ArrayList<Map<String, Object>>(); 
			if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.MONGODB, dbmsNm)) {
				sampleDataList = mongoDbService.selectTableRowDataList(dbmsTableNm, rowCnt);
			} else if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.VOLTDB, dbmsNm)) {
				colInfoList = voltDbService.getDiagnosisColumnList(dbmsTableNm);
				sampleDataList = voltDbService.selectTableRowDataList(dbmsTableNm, colInfoList, rowCnt);
			} else {
				sampleDataList = sqlSession.selectList("AnalysisMapper.selectTableRowDataList", map);
				
				if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CSV, dbmsNm)) {
					colInfoList = sqlSession.selectList("AnalysisMapper.selectAnalysisTableColumnList", map);
					Map<String,Object> colNmMap = new SangsMap();
					String comments = "";
					for(Map<String,Object> colMap : colInfoList) {
						comments = String.valueOf(colMap.get("comments"));
						String columnNm = String.valueOf(colMap.get("columnNm"));
						colMap.put("column_name", comments);
						colNmMap.put(columnNm, comments);
					}
					List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
					for(Map<String,Object> colMap : sampleDataList) {
						SangsMap dataMap = new SangsMap();
						for(String key : colMap.keySet()) {
							if(colNmMap.containsKey(key)) {
								dataMap.put(String.valueOf(colNmMap.get(key)), colMap.get(key));
							}
						}
						list.add(dataMap);
					}
					sampleDataList = list;
					
				}
			}
			
			rtnMap.put("sampleDataList", sampleDataList);
			rtnMap.put("resultCd", "OK");
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("getDgnssSampleDataList 샘플데이터 조회 오류");
			
		}
		return rtnMap;
	}
	
	
	
	/**
	 * 프로파일 관리 - 스케쥴 cron 표현식 저장
	 * 
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> saveCronExprsnValue(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			if(SangsStringUtil.isEmpty(params.get("cronExprsnValue"))) {
				// 표현식 값 없을때 
				params.put("nextExcSchdulDt", null);
			} else {
				
				String cronExprsnValue = "0 " + params.get("cronExprsnValue");
				
				if (!this.isValidExpression(cronExprsnValue)) {
					throw new SangsMessageException("표현식이 올바르지 않습니다.");
				}
				
				CronExpression cronTrigger = CronExpression.parse(cronExprsnValue);
				LocalDateTime now = LocalDateTime.now();
				LocalDateTime nextExcSchdulDt = cronTrigger.next(now);
				params.put("nextExcSchdulDt", nextExcSchdulDt);
			}
			
			dao.update("dq_profile.updateCronExprsnValue", params);
			rtnMap.put("resultCd", "OK");
			
		}  catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
			
		} catch (NullPointerException e) {
			logger.error("", e);
			// 없는 날짜 입력시 NullPointerException
			throw new SangsMessageException("날짜가 없습니다.");
			
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("saveCronExprsnValue 변경 오류");
		}
		
		return rtnMap;
	}
	
	
	
	/**
	 * 프로파일 관리 - 스케쥴 관리(버튼) - 다음일정 조회(5건)
	 * 
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> getNextSchedule(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
			String cronExprsnValue = "0 " + params.get("cronExprsnValue");
			
			if (!this.isValidExpression(cronExprsnValue)) {
				throw new SangsMessageException("표현식이 올바르지 않습니다.");
			}
			
			CronExpression cronTrigger = CronExpression.parse(cronExprsnValue);
			
			// sql TIMESTAMP format -> DATE_FORMAT(REG_DT, '%Y-%m-%dT%H:%i:%s')   공백 허용 안함
			int nextCnt = 5;
	        LocalDateTime now = LocalDateTime.now();
	        List<String> nextScheduleList = new ArrayList<String>();
	        for (int i=0; i<nextCnt; i++) {
	        	LocalDateTime next = cronTrigger.next(now);
	        	nextScheduleList.add(next.toString().replace("T", " "));
	        	
	        	now = next;
	        }
	        
	        rtnMap.put("nextScheduleList", nextScheduleList);
			rtnMap.put("resultCd", "OK");
		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		}  catch (NullPointerException e) {
			logger.error("", e);
			throw new SangsMessageException("날짜가 없습니다.");
			
		} catch (Exception e) {
			//e.printStackTrace();
			throw new SangsMessageException("getNextSchedule 다음스케쥴 조회 오류");
		}
		
		return rtnMap ;
	}
	
	
	
	/**
	 * 스케쥴러 모두 비활성 : !DB만 업데이트! 정지 로직 구현 필요
	 * 
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> stopAllSchedule() throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			dao.update("dq_profile.updateScheduleUseYn", null);
			rtnMap.put("resultCd", "OK");
			
		} catch (Exception e) {
			throw new SangsMessageException("stopAllSchedule 스케쥴 정지 오류");
		}
		
		
		return rtnMap;
	}
	
	
	
	/**
	 * Cron 표현식 유효성 체크
	 * 
	 * @param expression
	 * @return boolean
	 */
	private boolean isValidExpression(@Nullable String expression) {
		if (expression == null) {
			return false;
		}
		try {
			CronExpression.parse(expression);
			return true;
		}
		catch (IllegalArgumentException ex) {
			return false;
		}
	}
	
	
	
	
	/**
	 * 프로파일 진단 결과 목록 조회
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getDgnssResultList(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
			Map<String, Object> smap = new HashMap<String, Object>();
			smap.put("prjctSn", AuthUtil.getPrjctSn());
			smap.put("proflNm", params.get("proflNm"));
			smap.put("beginDtFrom", params.get("beginDtFrom"));
			smap.put("beginDtTo", params.get("beginDtTo"));
			
			int pageNum = SangsStringUtil.nvlInt(params.get("pageNum"), 1);
			int totCnt = dao.selectCount("dq_profile.selectDgnssResultListCnt", smap);
			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totCnt, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);
			
			smap.put("pageSize", pagingInfo.getPageSize());
			smap.put("offset", pagingInfo.getOffset());
			
			List<SangsMap> dgnssResultList =  dao.selectList("dq_profile.selectDgnssResultList", smap);

			for (SangsMap map:dgnssResultList) {
				if ("I".equals(map.get("excSttusCd"))) {
					map.putOrg("excSttusCdText", "분석중");
				} else if ("F".equals(map.get("excSttusCd"))) {
					map.putOrg("excSttusCdText", "실패");
				} else if ("E".equals(map.get("excSttusCd"))) {
					map.putOrg("excSttusCdText", "완료");
				} else if ("R".equals(map.get("excSttusCd"))) {
					map.putOrg("excSttusCdText", "분석대기");
				} else {
					map.putOrg("excSttusCdText", "?");
				}
			}
			
			rtnMap.put("pagingInfo", pagingInfo);
			rtnMap.put("dgnssResultList", dgnssResultList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("getDgnssResultList 진단 결과 조회 오류");
		}
		
		return rtnMap ;
	}
	
	
	
	
	/**
	 * 프로파일 진단 결과 정보 조회
	 * 
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> getProflDgnssResultViewInfo(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			SangsMap map = new SangsMap();
			map.putOrg("excSn", params.get("excSn"));
			SangsMap resultInfo =  dao.selectOne("dq_profile.selectDgnssResultDetailInfo", map);
			
			rtnMap.put("resultInfo", resultInfo);
			rtnMap.put(SangsConstants.FORWARD_VIEW, "dq/profile/profl_dgnss_result_view");
	
		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("진단 결과 정보 조회 오류");
		}
		return rtnMap;
	}
	
	
	/**
	 * 프로파일 진단 결과 테이블 목록
	 * 
	 * @params params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> getDgnssResultDetailTblList(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			// 컬럼 널 데이터 진단 여부 목록
			List<SangsMap> colNullDataDgnssYnList = dao.selectList("dq_profile.selectProfilResultNullDataDgnssYnList", params);
			
			// 테이블 진단 목록
			List<SangsMap> tblAnlsList = dao.selectList("dq_profile.selectDgnssResultDetailTblList", params);
			params.put("basicAnlsYn", "Y");
			// 기본 분석
			List<SangsMap> tblBasicAnlsList = dao.selectList("dq_profile.selectDgnssResultDetailTblAnlsList", params);
			params.put("basicAnlsYn", "N");
			// 패턴 분석
			List<SangsMap> tblPttrnAnlsList = dao.selectList("dq_profile.selectDgnssResultDetailTblAnlsList", params);
			// Value Frequency
			List<SangsMap> tblFqAnlsList = dao.selectList("dq_profile.selectDgnssResultDetailTblFqAnlsList", params);
			
			SangsMap colNullDataDgnssYnMap = new SangsMap();
			
			for (SangsMap map : colNullDataDgnssYnList) {
				String key = map.getString("tblSn") + "_" + map.getString("colSn") + "_" + map.getString("ruleSn");
				colNullDataDgnssYnMap.putOrg(key, map.getString("nullDataDgnssYn"));
			}
			
			for (SangsMap tblPttrnAnlsMap : tblPttrnAnlsList) {
				String key = tblPttrnAnlsMap.getString("tblSn") + "_" + tblPttrnAnlsMap.getString("colSn") + "_" + tblPttrnAnlsMap.getString("ruleSn");
				if (colNullDataDgnssYnMap.containsKey(key)) {
					tblPttrnAnlsMap.putOrg("nullDataDgnssYn", colNullDataDgnssYnMap.getString(key));
				}
			}
			
			rtnMap.put("tblAnlsListCnt", tblAnlsList.size());
			rtnMap.put("tblAnlsList", tblAnlsList);
			rtnMap.put("tblBasicAnlsList", tblBasicAnlsList);
			rtnMap.put("tblPttrnAnlsList", tblPttrnAnlsList);
			rtnMap.put("tblFqAnlsList", tblFqAnlsList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("테이블 목록 조회 오류");
		}
		return rtnMap;	
	}
	
	
	
	/**
	 * 프로파일 진단 결과 테이블 분석 목록 조회
	 * 
	 * @params params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> getDgnssResultDetailTblAnlsList(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			List<SangsMap> tblList = dao.selectList("dq_profile.selectDgnssResultDetailTblList", params);
			rtnMap.put("tblList", tblList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("테이블 분석 목록 조회 오류");
		}
		return rtnMap;	
	}
	
	
	
	
	
	/**
	 * 프로파일 결과 상세 불일치 목록 조회
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> getProflExcResultDisMatchList(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			SangsMap resultInfo = dao.selectOne("dq_profile.selectProfilResultDisMatchInfo", params);
			int pageNum = SangsStringUtil.nvlInt(params.get("pageNum"), 1);
			int totalDataCount = dao.selectCount("dq_profile.selectProfilResultDisMatchListCnt", params);

			int rowSize = Integer.parseInt(String.valueOf(params.get("pageSize")));

			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalDataCount, pageNum, rowSize);

			params.put("pageSize", pagingInfo.getPageSize());
			params.put("offset", pagingInfo.getOffset());

			List<SangsMap> resultDisMatchList = dao.selectList("dq_profile.selectProfilResultDisMatchList", params);

			for (SangsMap map : resultDisMatchList) {
				String tempCn1 = map.getString("ruleExprsnValue");
				String tempCn2 = map.getString("ruleDc");
				String tempCn3 = map.getString("ruleNm");
				String tempCn4 = map.getString("dataValue");

				if (tempCn1.getBytes().length > 25) {
					map.putOrg("ruleExprsnValue", SangsStringUtil.substringByte(tempCn1, 25) + "...");
				}
				if (tempCn2.getBytes().length > 25) {
					map.putOrg("ruleDc", SangsStringUtil.substringByte(tempCn2, 25) + "...");
				}
				if (tempCn3.getBytes().length > 25) {
					map.putOrg("ruleNm", SangsStringUtil.substringByte(tempCn3, 25) + "...");
				}
				if (tempCn4.getBytes().length > 25) {
					map.putOrg("dataValue", SangsStringUtil.substringByte(tempCn4, 25) + "...");
				}
			}
			for (String key : resultInfo.keySet()) {
				if ("excSttusCd".equals(key)) {
					String value = String.valueOf(resultInfo.get(key));
					if ("I".equals(value)) {
						resultInfo.putOrg("excSttusCdText", "분석중");
					} else if ("F".equals(value)) {
						resultInfo.putOrg("excSttusCdText", "실패");
					} else if ("E".equals(value)) {
						resultInfo.putOrg("excSttusCdText", "완료");
					} else if ("R".equals(value)) {
						resultInfo.putOrg("excSttusCdText", "분석대기");
					} else {
						resultInfo.putOrg("excSttusCdText", "?");
					}

				}
			}
			rtnMap.put("pagingInfo", pagingInfo);
			rtnMap.putAll(resultInfo);
			rtnMap.put("list", resultDisMatchList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("getProfileList 조회 오류");
		}

		return rtnMap;
	}
	
	
	
	
	/**
	 * 프로파일 결과 불일치 상세 selectBox 조회
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> getProflExcResultDisMatchSelectBox(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			List<SangsMap> resultDisMatchRuleList = dao.selectList("dq_profile.selectProfilResultDisMatchRuleList", params);
			List<SangsMap> resultDisMatchTableList = dao.selectList("dq_profile.selectProfilResultDisMatchTableList", params);
			rtnMap.put("resultDisMatchRuleList", resultDisMatchRuleList);
			rtnMap.put("resultDisMatchTableList", resultDisMatchTableList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("getProfileList 조회 오류");
		}

		return rtnMap;
	}

	/**
	 * 프로파일 결과 불일치 상세 엑셀 다운로드
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public Workbook getDisMatchExcelDown(Map<String, Object> params) {
		Workbook workbook = null;

		try {
			params.put("excelYn", "Y");
			List<SangsMap> resultDisMatchList = dao.selectList("dq_profile.selectProfilResultDisMatchList", params);

			for (SangsMap map : resultDisMatchList) {
				String tempCn1 = map.getString("ruleExprsnValue");
				String tempCn2 = map.getString("ruleDc");
				String tempCn3 = map.getString("ruleNm");
				String tempCn4 = map.getString("dataValue");

				if (tempCn1.getBytes().length > 100) {
					map.putOrg("ruleExprsnValue", SangsStringUtil.substringByte(tempCn1, 100) + "...");
				}
				if (tempCn2.getBytes().length > 100) {
					map.putOrg("ruleDc", SangsStringUtil.substringByte(tempCn2, 100) + "...");
				}
				if (tempCn3.getBytes().length > 100) {
					map.putOrg("ruleNm", SangsStringUtil.substringByte(tempCn3, 100) + "...");
				}
				if (tempCn4.getBytes().length > 100) {
					map.putOrg("dataValue", SangsStringUtil.substringByte(tempCn4, 100) + "...");
				}
			}
			
			SangsSimpleExcelMaker em = new SangsSimpleExcelMaker();
			workbook = em.createSheet()
					.setHeaderColNm(new String[] { "데이블 명", "컬럼 명", "규칙 순번", "규칙 명", "규칙 표현 값", "긍 정규칙 여부", "규칙 설명", "데이터 값", "기본키 정보 값", "Rule 오류 여부" })
					.setHeaderColId(new String[] { "tblNm", "colNm", "ruleSn", "ruleNm", "ruleExprsnValue", "rulePostvExprsnYn", "ruleDc", "dataValue", "pkInfoValue", "errRuleYn"})
					.setList(resultDisMatchList).setAutoSize().getWorkbook();

			
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("엑셀 생성중 에러가 발생하였습니다." + e.getMessage());
		}

		return workbook;
	}
	
	/**
	 * 진단 결과 에러 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getDgnssResultErrInfo(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			
			SangsMap info = dao.selectOne("dq_profile.selectDgnssResultErrInfo", params);
			rtnMap.put("info", info);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("getDgnssResultErrInfo 진단 결과 에러 조회");
		}
		return rtnMap;
	}

	/**
	 * 진단 결과 규칙 에러 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getDgnssResultRuleErrInfo(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			
			SangsMap info = dao.selectOne("dq_profile.selectDgnssResultRuleErrInfo", params);
			rtnMap.put("info", info);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("getDgnssResultRuleErrInfo 진단 결과 에러 조회");
		}
		return rtnMap;
	}

}
