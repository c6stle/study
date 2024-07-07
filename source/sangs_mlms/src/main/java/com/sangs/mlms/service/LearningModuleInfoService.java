package com.sangs.mlms.service;

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
 * 학습 모듈 관련 Service 
 * 
 * 
 * @author id.yoon
 *
 */
@SangsService
public class LearningModuleInfoService extends ServiceBase {
	
	@Autowired
	private CommonDao dao;
	
	
	/**
	 * 학습모듈목록 조회 
	 * @param paramMap prameter Map
	 * @return return Map
	 */
	public Map<String, Object> getLearningModuleList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			logger.debug("searching keywords : " + paramMap);
			
			int pageNum = SangsStringUtil.nvlInt(paramMap.get("pageNum"), 1);
			
			// module 수 조회
			int totalCount = dao.selectCount("mlms_learning_module.selectLearningModuleListCnt", paramMap);
			
			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);
			
			paramMap.put("pageSize", pagingInfo.getPageSize());
			paramMap.put("offset", pagingInfo.getOffset());
			
			List<SangsMap> moduleList = dao.selectList("mlms_learning_module.selectLearningModuleList", paramMap);
			
			rtnMap.put("moduleList", moduleList);
			rtnMap.put("totalCount", totalCount);
			rtnMap.put("pagingInfo", pagingInfo);
		
		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		
		return rtnMap;
	}
	
	
	
	
	/**
	 * 학습모듈정보 조회 
	 * @param paramMap prameter Map
	 * @return return Map
	 */
	public Map<String, Object> viewLearningModuleInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		logger.debug("run module_sn : " + paramMap);
		
		try {
			
			if ("M".equals(paramMap.get("pmode"))) {
				
				SangsMap moduleInfo = dao.selectOne("mlms_learning_module.selectLearningModuleInfo", paramMap);
				moduleInfo.put("pmode", paramMap.get("pmode"));
				List<SangsMap> moduleParamList = dao.selectList("mlms_learning_module.selectLearningModuleParamAllList", paramMap);
				
				rtnMap.put("moduleInfo", moduleInfo);
				rtnMap.put("moduleParamList", moduleParamList);
				rtnMap.put(SangsConstants.FORWARD_VIEW, "mlms/learning_modl/learning_modl_form");
				
			} else if ("R".equals(paramMap.get("pmode"))) {
				
				Map<String, Object> moduleInfo = new HashMap<String, Object>();
				moduleInfo.put("pmode", paramMap.get("pmode"));
				
				moduleInfo.put("moduleSn", "");
				moduleInfo.put("moduleTyCd", "");
				moduleInfo.put("libTyCd", "");
				moduleInfo.put("moduleNm", "");
				moduleInfo.put("modulePckageNm", "");
				moduleInfo.put("moduleClassNm", "");
				moduleInfo.put("moduleDc", "");
				
				rtnMap.put("moduleInfo", moduleInfo);
				rtnMap.put(SangsConstants.FORWARD_VIEW, "mlms/learning_modl/learning_modl_form");
			}
		
		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		
		
		return rtnMap;
	}
	
	
	
	
	/**
	 * 학습모듈정보 등록
	 * @param paramMap prameter Map
	 * @return return Map
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveLearningModuleInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
			logger.debug("save information : " + paramMap);
			
			paramMap.put("regUserId", AuthUtil.getUserId());
			paramMap.put("chgUserId", AuthUtil.getUserId());
			paramMap.put("delYn", "N");
			
			
			// 모듈정보 update 
			if ("M".equals(paramMap.get("pmode"))) {
				rtnMap.put("moduleSnKnown", new SangsMap());
				dao.update("mlms_learning_module.updateLearningModuleInfo", paramMap);
				
			}
			
			// 모듈정보 insert
			else if ("R".equals(paramMap.get("pmode"))){
				// 신규등록 모듈순번 채번
				SangsMap moduleSnKnown = dao.selectOne("mlms_learning_module.selectNextModuleSn", paramMap);
				paramMap.put("moduleSn", moduleSnKnown.get("moduleSn"));
				
				rtnMap.put("moduleSnKnown", moduleSnKnown);
				dao.insert("mlms_learning_module.insertLearningModuleInfo", paramMap);
				
			}
			
			List<Map<String, Object>> dataList = (List<Map<String, Object>>)paramMap.get("dataList");
			
			for (Map<String, Object> dataListRow: dataList) {
				
				if ("insert".equals(dataListRow.get("iuId"))) {
					dataListRow.put("moduleSn", paramMap.get("moduleSn"));
					dataListRow.put("chgUserId", paramMap.get("chgUserId"));
					dataListRow.put("regUserId", paramMap.get("regUserId"));
					dao.insert("mlms_learning_module.insertLearningModuleParamInfo", dataListRow);
					
				} else if ("update".equals(dataListRow.get("iuId"))) {
					if (paramMap.containsKey("moduleSn"))
					dataListRow.put("moduleSn", paramMap.get("moduleSn"));
					dataListRow.put("chgUserId", paramMap.get("chgUserId"));
					dao.update("mlms_learning_module.updateLearningModuleParamInfo", dataListRow);
				} 
				
			}
		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		
		return rtnMap;
	}
	
	
	
	
	public Map<String, Object> getLearningModuleParamSearchList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
			logger.debug("searching keywords : " + paramMap);
			
			int pageNum = SangsStringUtil.nvlInt(paramMap.get("pageNum"), 1);
			
			// module 수 조회
			int totalCount = dao.selectCount("mlms_learning_module.selectLearningModuleParamSearchListCnt", paramMap);
			
			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);
			
			paramMap.put("pageSize", pagingInfo.getPageSize());
			paramMap.put("offset", pagingInfo.getOffset());
			
			List<SangsMap> paramList = dao.selectList("mlms_learning_module.selectLearningModuleParamList", paramMap);
			
			rtnMap.put("paramList", paramList);
			rtnMap.put("totalCount", totalCount);
			rtnMap.put("pagingInfo", pagingInfo);
		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		
		
		return rtnMap;
	}
	
	
	
	
	public Map<String, Object> deleteLearningModuleInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
			logger.debug("update moduleSn : " + paramMap);
			
			paramMap.put("delYn", "Y");
			dao.update("mlms_learning_module.updateLearningModuleInfo", paramMap);
			
			
			
		} catch(SangsMessageException e) {
			logger.error("", e);
		}
		
		return rtnMap;
	}
	
	
	
}
