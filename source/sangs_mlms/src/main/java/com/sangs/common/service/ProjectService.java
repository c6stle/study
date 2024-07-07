package com.sangs.common.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.support.AuthUtil;
import com.sangs.common.support.CommonDao;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.fwk.common.SangsConstants;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.domain.SangsPagingViewInfo;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsStringUtil;

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
				// 표준세트 선택안하고 등록할 때
				if (prjctInfo.get("stdSetSn") == "" || "".equals(prjctInfo.get("stdSetSn"))) {
					prjctInfo.put("stdSetSn", "0");
				}
				prjctInfo.put("regUserId", regUserId);
				chkUserId = String.valueOf(prjctInfo.get("chkUserId"));
			}
			
			if (paramMap.containsKey("dbmsInfo")) {
				dbmsInfo = (Map<String, Object>) paramMap.get("dbmsInfo");
				if(dbmsInfo.get("dbmsSn") == "" || "".equals(dbmsInfo.get("dbmsSn"))) {
					dbmsInfo.put("dbmsSn", 0);
				}
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
				// 포로젝트 정보 저장
				dao.insert("cmmn_project.insertProjectInfo", prjctInfo);
				
				dbmsInfo.put("dbmsCnncSn", nextDbmsCnncSn);
				dbmsInfo.put("regUserId", regUserId);
				// DBMS 저장
				dao.insert("cmmn_project.insertProjectDbmsCnncInfo", dbmsInfo);
				
				userInfo.put("prjctSn", nextPrjctSn);
				
			} else if("M".equals(pmode)) {
				if (paramMap.containsKey("prjctInfo")) {
					userInfo.put("prjctSn", prjctInfo.get("prjctSn"));
					dao.delete("cmmn_project.deleteProjectUserInfo", userInfo);
				} else {
					// 도메인에서 DBMS 수정할때
					paramMap.put("prjctSn", AuthUtil.getPrjctSn());
					paramMap.put("stdSetSn", AuthUtil.getStdSetSn());
					
					SangsMap list = dao.selectOne("cmmn_project.selectProjectInfo", paramMap);
					dbmsInfo.put("dbmsCnncSn", list.get("dbmsCnncSn"));
				}
				// 프로젝트 정보 수정
				dao.update("cmmn_project.updateProjectInfo", prjctInfo);
				
				// DBMS 정보 수정
				dbmsInfo.put("chgUserId", regUserId);
				dao.update("cmmn_project.updateProjectDbmsCnncInfo", dbmsInfo);
			
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
	 * 사용자의 프로젝트 목록 조회 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getUserProjectList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);

			// 프로젝트 목록 상세 조회

			List<SangsMap> list = dao.selectList("cmmn_project.selectUserProjectList", paramMap);
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
