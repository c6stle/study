package com.sangs.common.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.common.CommonConstant;
import com.sangs.common.support.AppJwtUtil;
import com.sangs.common.support.AuthUtil;
import com.sangs.common.support.BizUtil;
import com.sangs.common.support.BizUtil.DBMS_TYPE_NAME;
import com.sangs.common.support.CommonDao;
import com.sangs.dq.service.CsvInfoService;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.fwk.common.SangsConstants;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.domain.SangsPagingViewInfo;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsEncryptUtil;
import com.sangs.lib.support.utils.SangsStringUtil;
import com.sangs.lib.support.utils.SangsWebUtil;

/**
 * 프로젝트 관련 Service
 * 
 * 
 * @author sw.lee
 *
 */

@SangsService
public class ProjectService extends ServiceBase {

	@Autowired
	private CommonDao dao;

	@Autowired
	CsvInfoService csvInfoService;
	
	/**
	 * 프로젝트 목록 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getProjectList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);

			int pageNum = SangsStringUtil.nvlInt(paramMap.get("pageNum"), 1);

			// 전체 row 수 조회
			int totalCount = dao.selectCount("cmmn_project.selectProjectListCnt", paramMap);

			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);

			paramMap.put("pageSize", pagingInfo.getPageSize());
			paramMap.put("offset", pagingInfo.getOffset());

			// 프로젝트 목록 조회
			List<SangsMap> list = dao.selectList("cmmn_project.selectProjectList", paramMap);
			for(Map<String,Object> map : list) {
				String dbmsNm = String.valueOf(map.get("dbmsNm"));
				if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CSV, dbmsNm)) {
					map.put("dbms_ip_addr", "");
					map.put("dbms_port_no", "");
					map.put("dbms_id", "");
					map.put("dbms_database_nm", "");
				}
			}
			
			rtnMap.put("list", list);
			rtnMap.put("totalCount", totalCount);
			rtnMap.put("pagingInfo", pagingInfo);

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}

	/**
	 * 프로젝트 목록 상세 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getProjectInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);

			// 프로젝트 목록 상세 조회

			SangsMap prjctInfo = dao.selectOne("cmmn_project.selectProjectInfo", paramMap);
			List<SangsMap> userList = dao.selectList("cmmn_project.selectProjectUserList", paramMap);
			String pwd = prjctInfo.getString("dbmsPassword");
			if(!"".equals(pwd)) {
				String decryptPwdValue = SangsEncryptUtil.decrypt_AES128(pwd, CommonConstant.CRYPT_AES_KEY);
				
				String str ="";
				for (int i = 0; i < decryptPwdValue.length(); i++) {
					str +="*";
				}
				
				prjctInfo.putOrg("dbmsPassword", str);
			}
			
			rtnMap.put("prjctInfo", prjctInfo);
			rtnMap.put("userList", userList);

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}
	/**
	 * 현재 프로젝트 dbms 정보 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getCurrProjectInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			paramMap.put("prjctSn", AuthUtil.getPrjctSn());
			paramMap.put("stdSetSn", AuthUtil.getStdSetSn());

			// 프로젝트 정보 조회
			SangsMap projcetInfo = dao.selectOne("cmmn_project.selectProjectInfo", paramMap);

			rtnMap.put("info", projcetInfo);

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}
	
	
	
	/**
	 * 프로젝트 사용자 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getProjectUserList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);
			// 프로젝트 목록 상세 조회
			List<SangsMap> list = dao.selectList("cmmn_project.selectProjectUserListSearch", paramMap);
			rtnMap.put("list", list);

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}

	/**
	 * 프로젝트 정보 등록/수정 처리
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> saveProjectInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
		
			logger.debug("parameter : " + paramMap);

			SangsStringUtil.checkRequiredParam(paramMap, "pmode", "pmode");
			String pmode = String.valueOf(paramMap.get("pmode"));
			
			// 사용자정보
			Map<String, Object> userInfo = new HashMap<String, Object>();
			// 프로젝트정보
			Map<String, Object> prjctInfo = new HashMap<String, Object>();
			// 표준세트정보
			Map<String, Object> dbmsInfo = new HashMap<String, Object>();
			
			String regUserId = AuthUtil.getUserId();
			String chkUserId = "";
			
			if (paramMap.containsKey("prjctInfo")) {
				prjctInfo = (Map<String, Object>) paramMap.get("prjctInfo");
				String prjctNm = (String) prjctInfo.get("prjctNm");
				prjctInfo.put("prjctNm", SangsWebUtil.clearXSSMinimum((String) prjctNm));
				
				// 표준세트 선택안하고 등록할 때
				if (prjctInfo.get("stdSetSn") == "" || "".equals(prjctInfo.get("stdSetSn"))) {
					prjctInfo.put("stdSetSn", "0");
				}
				prjctInfo.put("regUserId", regUserId);
				chkUserId = String.valueOf(prjctInfo.get("chkUserId"));
				
				SangsStringUtil.checkStringLegnth(prjctNm, 200, "프로젝트명");
				SangsStringUtil.checkStringLegnth(String.valueOf(prjctInfo.get("prjctCn")), 1000, "프로젝트설명");
			}
			
			String dbmsNm = "";
			if (paramMap.containsKey("dbmsInfo")) {
				dbmsInfo = (Map<String, Object>) paramMap.get("dbmsInfo");
				if(dbmsInfo.get("dbmsSn") == "" || "".equals(dbmsInfo.get("dbmsSn"))) {
					dbmsInfo.put("dbmsSn", 0);
				}else {
					if(dbmsInfo.containsKey("dbmsPassword")) {
						
						Map<String, Object> rtnTokenMap = AppJwtUtil.validateToken(dbmsInfo.get("dbmsPassword").toString());
						// jwt 토큰 에러일때 
						if(!"".equals(rtnTokenMap.get("errorCode"))) {
							logger.error("", rtnTokenMap.get("errorCode").toString());
							logger.error("", rtnTokenMap.get("msg").toString());
							throw new SangsMessageException("암호화 토큰 에러가 발생했습니다.");
						}
						
						Map<String, Object> decodeTokenMap = (Map<String, Object>) rtnTokenMap.get("decodeToken");
						String pwd = (String) decodeTokenMap.get("dbmsPassword");
						//String pwd = String.valueOf(dbmsInfo.get("dbmsPassword"));
						
						if(!"".equals(pwd)) {
							dbmsInfo.put("dbmsPassword", SangsEncryptUtil.encrypt_AES128(pwd, CommonConstant.CRYPT_AES_KEY));
						} else {
							dbmsInfo.put("dbmsPassword", "");
						}
					}
				}
				dbmsNm = String.valueOf(dbmsInfo.get("dbmsNm"));
			} else {
				dbmsInfo.put("dbmsSn", 0);
			}
			
			if("R".equals(pmode)) {
				int nextPrjctSn = dao.selectCount("cmmn_project.selectNextProjectSn", paramMap);
				int nextDbmsCnncSn = dao.selectCount("cmmn_project.selectNextProjectDbmsCnncSn", paramMap);
				
				prjctInfo.put("prjctSn", nextPrjctSn);
				prjctInfo.put("dbmsCnncSn", nextDbmsCnncSn);
				prjctInfo.put("useYn", "Y");
				prjctInfo.put("delYn", "N");
				prjctInfo.put("regUserId", regUserId);
						
				if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CSV, dbmsNm)){
					Map<String, Object> csvMap = csvInfoService.readCsvFileDataPaserSave(dbmsInfo);
					prjctInfo.putAll(csvMap);
					dbmsInfo.putAll(csvMap);
				}
				
				// 포로젝트 정보 저장
				dao.insert("cmmn_project.insertProjectInfo", prjctInfo);
				// 프로젝트 표준세트 매핑 테이블 저장
				dao.insert("cmmn_project.insertMetaPrjctStdSetInfo", prjctInfo);
				
				dbmsInfo.put("dbmsCnncSn", nextDbmsCnncSn);
				dbmsInfo.put("regUserId", regUserId);
				// DBMS 정보 저장
				dao.insert("cmmn_project.insertProjectDbmsCnncInfo", dbmsInfo);
				
				userInfo.put("prjctSn", nextPrjctSn);
				
			} else if("M".equals(pmode)) {
				dbmsInfo.put("chgUserId", regUserId);
				
				
				if (paramMap.containsKey("prjctInfo")) {
					prjctInfo.put("pmode", pmode);
					String prjctNm = (String) prjctInfo.get("prjctNm");
					prjctInfo.put("prjctNm", SangsWebUtil.clearXSSMinimum((String) prjctNm));
					
					userInfo.put("prjctSn", prjctInfo.get("prjctSn"));
					dao.delete("cmmn_project.deleteProjectUserInfo", userInfo);
					// 프로젝트 정보 수정
					dao.update("cmmn_project.updateProjectInfo", prjctInfo);
					// 프로젝트 표준세트 매핑 테이블 수정
					dao.update("cmmn_project.updateMetaPrjctStdSetInfo", prjctInfo);
					// 프로젝트 DBMS 연결 정보 수정
					dao.update("cmmn_project.updateProjectDbmsCnncInfo", dbmsInfo);
					
				} else {
					// 도메인에서 DBMS 수정할때
					paramMap.put("prjctSn", AuthUtil.getPrjctSn());
					paramMap.put("stdSetSn", AuthUtil.getStdSetSn());
					
					SangsMap list = dao.selectOne("cmmn_project.selectProjectInfo", paramMap);
					dbmsInfo.put("dbmsCnncSn", list.get("dbmsCnncSn"));
					dao.update("cmmn_project.updateProjectDbmsSn", dbmsInfo);
				}
			
			} else if("D".equals(pmode)) {
				paramMap.put("chgUserId", regUserId);
				dao.update("cmmn_project.updateProjectInfo", paramMap);
			} else {
				throw new SangsMessageException("잘못된 접근입니다.");
			}
			
			if(!"".equals(chkUserId)) {
				String[] chkUserIdArr = chkUserId.split(",");
				for(String userId : chkUserIdArr) {
					userInfo.put("userId", userId);
					dao.insert("cmmn_project.insertProjectUserInfo", userInfo);
				}
			}
			
			rtnMap.put("resultCd", "OK");

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}
	

 
	
	/**
	 * 프로젝트 DBMS 연결 정보 조회  
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getProjectDbmsCnncInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			SangsMap info = dao.selectOne("cmmn_project.selectProjectDbmsCnncInfo", paramMap);
			rtnMap.put("info", info);
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}
	
}
