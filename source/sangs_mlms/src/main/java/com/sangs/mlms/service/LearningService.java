package com.sangs.mlms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.support.AuthUtil;
import com.sangs.common.support.CommonDao;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.fwk.common.SangsConstants;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.domain.SangsPagingViewInfo;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsStringUtil;
import com.sangs.mlms.common.MlmsConstant;
import com.sangs.mlms.service.mlm.MlExecuterManager;

/**
 * 학습관련 Service 
 * 
 * 
 * @author id.yoon
 *
 */

@SangsService
public class LearningService extends ServiceBase {

	
	@Autowired
	private CommonDao dao;
	
	@Autowired
	private MlExecuterManager mlExecuterManager;
	
	@Value("${mls.resource.base_path:}")
	private String resourceBasePath;
	
	String modelBasePath = MlmsConstant.RESOURCE_MODEL_BASE_PATH;
	
	/**
	 * 학습 모듈 목록 조회 
	 * @param paramMap prameter Map
	 * @return return Map
	 */
	public Map<String, Object> getLearnModuleList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			
			// 데이터 셋 목록 조회 
			List<SangsMap> datasetList = dao.selectList("mlms_dataset.selectLearningDatasetAllList", paramMap);
			
			// 모듈 전체 목록 조회
			List<SangsMap> moduleList = dao.selectList("mlms_learning_module.selectLearningModuleAllList", null);
			
			// 모듈파라미터 목록 조회 
			List<SangsMap> moduleParamList = dao.selectList("mlms_learning_module.selectLearningModuleParamAllList", paramMap);
			
			List<SangsMap> filterList = new ArrayList<SangsMap>();
			List<SangsMap> processorList = new ArrayList<SangsMap>();
			List<SangsMap> modelerList = new ArrayList<SangsMap>();
			
			for(SangsMap map : moduleList) {
				String modultType = map.getString("moduleTyCd");
				if("FILTER".equals(modultType)) 
					filterList.add(map);
				else if("PROCESSOR".equals(modultType))
					processorList.add(map);
				else if("MODELER".equals(modultType))
					modelerList.add(map);
			}
			
			rtnMap.put("datasetList", datasetList);
			rtnMap.put("filterList", filterList);
			rtnMap.put("processorList", processorList);
			rtnMap.put("modelerList", modelerList);
			rtnMap.put("moduleParamList", moduleParamList);
			
			
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
	 * 학습 정보 등록
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> regLearningInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			int nextLrnSn = dao.selectCount("mlms_learning.selectNextLearningSn", null);
			
			Map<String, Object> insMap = new HashMap<String, Object>();
			insMap.put("lrnSn", nextLrnSn);
			insMap.put("delYn", "N");
			insMap.put("regUserId", AuthUtil.getUserId());
			
			// 학습정보 insert 
			dao.insert("mlms_learning.insertLearningInfo", insMap);
			
			rtnMap.put("resultCd", "OK");
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
	 * 학습 실행 등록 
	 * @param paramMap prameter Map
	 * @return map
	 * @throws Exception throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> regLearningExcnInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {

			// 필수 체크
			SangsStringUtil.checkRequiredParam(paramMap, "traingSttusCd", "저장구분");
			SangsStringUtil.checkRequiredParam(paramMap, "lrnSn", "학습순번");
			
			List<Map<String, Object>> list = (List<Map<String, Object>>)paramMap.get("dataList");
			
			String traingSttusCd = (String)paramMap.get("traingSttusCd");	// TEMP_SAVED:임시저장 , READY:대기
			
			
			Map<String, Object> insMap = new HashMap<String, Object>();
			insMap.put("regUserId", AuthUtil.getUserId());
			insMap.put("lrnSn", paramMap.get("lrnSn"));
			

			int[] arrlrnExcnSn = new int[list.size()];
			int[] arrSubSn = new int[list.size()]; 
			
			int idx = 0;
			for(Map<String, Object> map : list) {
				int nextlrnExcnSn = dao.selectCount("mlms_learning.selectNextLearningExcnSn", null);
				insMap.put("lrnExcnSn", nextlrnExcnSn);
				
				// 학습 실행 정보 insert
				insMap.put("lrnStepCd", "TRAINING");		// 훈련
				insMap.put("uldModelYn", "N");
				insMap.put("crtModelFlpth", "model_"+nextlrnExcnSn+".h5");
				dao.insert("mlms_learning.insertLearningExcnInfo", insMap);
				
				
				// 훈련 이력 정보 insert
				insMap.put("lrnDatasetSn", map.get("aplcnTrainDatasetSn"));
				insMap.put("traingSttusCd", traingSttusCd);
				insMap.put("trgtTraingDataCnt", 0);
				dao.insert("mlms_learning.insertTrainingHistInfo", insMap);
				
		 
	
				// 테스트 이력 정보 insert
				insMap.put("lrnDatasetSn", map.get("aplcnTestDatasetSn"));
				insMap.put("resultFlpth", "result_"+nextlrnExcnSn+"_1.csv");
				insMap.put("testHistSn", 1);
				dao.insert("mlms_learning.insertTestHistInfo", insMap);
				
				
				// 훈련 적용 모듈 정보 insert
				this.insertAplcnModuleInfo(insMap, (List<Map<String, Object>>)map.get("aplcnTrainModuleList"), "PRE_TRAIN");
				// 모델러 적용 모듈정보 insert
				this.insertAplcnModuleInfo(insMap, (List<Map<String, Object>>)map.get("aplcnModelerList"), "MODELER");
				// 테스트 모듈 정보 insert 
				this.insertAplcnModuleInfo(insMap, (List<Map<String, Object>>)map.get("aplcnTestModuleList"), "PRE_TEST");
				
				arrlrnExcnSn[idx] = nextlrnExcnSn;
				arrSubSn[idx] = 1;
				idx++;
			}
			
			
			dao.commit();


			
			if("READY".equals(traingSttusCd)) {
				// DB 정보 insert 후 python 모듈 실행 
				
				// 훈련/테스트 실행 
				boolean rtnFlag = mlExecuterManager.executeMl(arrlrnExcnSn, arrSubSn, "NEW", "BOTH");
				if(!rtnFlag)
					throw new SangsMessageException("학습 모듈 실행시 에러가 발생하였습니다.");
				///mlExecuterManager.executeMl(arrLearningExcnSn, "NEW", "TEST");
				
			}
			
			
			//String runCmd = "py main.py ";
			
			//MlExecutor mlExecutor = new MlExecutor(runCmd);
			//Thread thread = new Thread(mlExecutor);
			//thread.start();
			
			rtnMap.put("resultCd", "OK");
			
		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
			//throw e;
			
		}
		 
		return rtnMap;
	}
	
	
	/**
	 * 적용 모듈 insert 처리 
	 * 
	 * @param insMap param map
	 * @param aplcnModuleList 적용 모듈 리스트
	 * @param moduleAplcnTyCd 적용모듈 타입 
	 * @throws Exception throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void insertAplcnModuleInfo(Map<String, Object> insMap, List<Map<String, Object>> aplcnModuleList, String moduleAplcnTyCd) throws Exception {
		if(aplcnModuleList != null) {
			for(Map<String, Object> atmMap : aplcnModuleList) {
				
				int nextModuleApplSn = dao.selectCount("mlms_learning.selectNextModuleApplSn", insMap);
				
				List<Map<String, Object>> paramList = (List<Map<String, Object>>)atmMap.get("paramList");
				
				insMap.put("moduleAplcnSn", nextModuleApplSn);
				insMap.put("moduleSn", atmMap.get("moduleSn"));
				insMap.put("moduleAplcnTyCd", moduleAplcnTyCd);	// 훈련 전처리 
				
				// 적용 모듈 정보 insert 
				dao.insert("mlms_learning.insertAplcnModuleInfo", insMap);
				
				if(paramList != null) {
					for(Map<String, Object> prMap : paramList) {
						
						insMap.put("paramtrNm", prMap.get("paramtrNm"));
						insMap.put("paramtrValue", prMap.get("paramtrValue"));
					
						// 적용 모듈 파라미터 정보 insert 
						dao.insert("mlms_learning.insertAplcnModuleParamInfo", insMap);
					}
				}
			}
		}
	}
	
	
	/**
	 * 학습 이력 조회 
	 * @param paramMap 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getModelTrainList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		logger.debug("parameter : " + paramMap);
		
		int pageNum = SangsStringUtil.nvlInt(paramMap.get("pageNum"), 1);
		
		// 전체 row 수 조회 
		int totalCount = dao.selectCount("mlms_learning.selectModelTrainListCnt", paramMap);
		
		SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);
		
		
		// 목록 조회
		paramMap.put("pageSize", pagingInfo.getPageSize());
		paramMap.put("offset", pagingInfo.getOffset());
	
		List<SangsMap> list = dao.selectList("mlms_learning.selectModelTrainList", paramMap);
	
		
		rtnMap.put("list", list);
		rtnMap.put("totalCount", totalCount);
		rtnMap.put("pagingInfo", pagingInfo);
		
		return rtnMap;
	}
	
	/**
	 * 모델훈련이력조회 
	 * @param paramMap 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getModelTrainHistList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		logger.debug("parameter : " + paramMap);
		
		int pageNum = SangsStringUtil.nvlInt(paramMap.get("pageNum"), 1);
		
		// 전체 row 수 조회 
		int totalCount = dao.selectCount("mlms_learning.selectModelTrainHistListCnt", paramMap);
		
		SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);
		
		
		// 목록 조회
		paramMap.put("pageSize", pagingInfo.getPageSize());
		paramMap.put("offset", pagingInfo.getOffset());
	
		List<SangsMap> list = dao.selectList("mlms_learning.selectModelTrainHistList", paramMap);
	
		
		rtnMap.put("list", list);
		rtnMap.put("totalCount", totalCount);
		rtnMap.put("pagingInfo", pagingInfo);
		
		return rtnMap;
	}
	
	
	
	
	/**
	 * 학습정보 조회
	 * @param paramMap 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getLearningInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		logger.debug("parameter : " + paramMap);
		
		SangsMap info = dao.selectOne("mlms_learning.selectLearningInfo", paramMap);
		
		if(info == null)
			throw new SangsMessageException("데이터가 존재 하지 않습니다.");
		
		rtnMap.put("info", info);
		
		return rtnMap;
	}
	
	 
	/**
	 * 실행학습적용모듈정보조회
	 * @param paramMap 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getLearningExcnAplcnModule(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		logger.debug("parameter : " + paramMap);
		
		// 학습실행적용데이터셋조회
		List<SangsMap> aplcnDatasetList = dao.selectList("mlms_learning.selectLearningExcnAplcnDatasetList", paramMap);
		
		// 학습실행적용모듈목록조회
		List<SangsMap> aplcnModuleList = dao.selectList("mlms_learning.selectLearningExcnAplcnModuleList", paramMap);
		
		// 학습실행적용모듈목록파라미터조회
		List<SangsMap> aplcnModuleParamList = dao.selectList("mlms_learning.selectLearningExcnAplcnModuleParamList", paramMap);
		
		rtnMap.put("aplcnDatasetList", aplcnDatasetList);
		rtnMap.put("aplcnModuleList", aplcnModuleList);
		rtnMap.put("aplcnModuleParamList", aplcnModuleParamList);
		
		return rtnMap;
	}
	
	
	 


	/**
	 * 모델 퍼블리싱 처리
	 * @param paramMap prameter Map
	 * @return map
	 * @throws Exception throws Exception
	 */
	public Map<String, Object> regModelPublishing(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {

			// 필수 체크
			SangsStringUtil.checkRequiredParam(paramMap, "lrnExcnSn", "학습수행순번");
			
			// 기존 학습실행 정보 조회 
			SangsMap excnInfo = dao.selectOne("mlms_learning.selectLearningExcnInfo", paramMap);
			
			// 신규 학습실행순번 채번 
			int nextlrnExcnSn = dao.selectCount("mlms_learning.selectNextLearningExcnSn", null);

			logger.debug("nextlrnExcnSn : " + nextlrnExcnSn);
			
			logger.debug(excnInfo.toString());
			
			// 학습실행 정보 등록
			Map<String, Object> insMap = new HashMap<String, Object>();
			insMap.put("lrnExcnSn", nextlrnExcnSn);	// 신규 번호로 set
			insMap.put("lrnSn", excnInfo.get("lrnSn"));
			insMap.put("lrnStepCd", "PUBLISHED");	// 퍼블리싱
			insMap.put("crtModelFlpth", excnInfo.get("crtModelFlpth"));
			insMap.put("trainglrnExcnSn", excnInfo.get("lrnExcnSn"));		// 훈련한 수행 순번
			insMap.put("uldModelYn", "N");
			dao.insert("mlms_learning.insertLearningExcnInfo", insMap);
			
			// 훈련 이력 정보 등록
//			Map<String, Object> trainHistInsMap = new HashMap<String, Object>();
//			trainHistInsMap.put("lrnExcnSn", nextlrnExcnSn);	// 신규 번호로 set
//			trainHistInsMap.put("lrnDatasetSn", excnInfo.getInt("traingDatasetSn"));
//			trainHistInsMap.put("traingSttusCd", "READY");	// 대기로 저장
//			trainHistInsMap.put("trgtTraingDataCnt", excnInfo.getInt("trgtTraingDataCnt"));
//			trainHistInsMap.put("regUserId", AuthUtil.getUserId());
//			dao.insert("mlms_learning.insertTrainingHistInfo", trainHistInsMap);
			
			
			// 적용 모듈 목록 조회(기존 수행순번에 대한)
			Map<String, Object> searchMap = new HashMap<String, Object>();
			searchMap.put("lrnExcnSn", paramMap.get("lrnExcnSn"));
			List<SangsMap> excnAplcnModuleList = dao.selectList("mlms_learning.selectLearningExcnAplcnModuleList", searchMap);
			
			for(SangsMap sangsMap : excnAplcnModuleList) {
				
				Map<String, Object> excnAplcnInsMap = new HashMap<String, Object>();
				excnAplcnInsMap.put("lrnExcnSn", nextlrnExcnSn);
				excnAplcnInsMap.put("moduleAplcnSn", sangsMap.getInt("moduleAplcnSn"));
				excnAplcnInsMap.put("moduleSn", sangsMap.getInt("moduleSn"));
				excnAplcnInsMap.put("moduleAplcnTyCd", sangsMap.getString("moduleAplcnTyCd"));
				excnAplcnInsMap.put("regUserId", AuthUtil.getUserId());
				// 적용 모듈 등록
				dao.insert("mlms_learning.insertAplcnModuleInfo", excnAplcnInsMap);
			}
			
			// 적용 모듈 파라미터 목록 조회(기존 수행순번에 대한)
			List<SangsMap> excnAplcnModuleParamList = dao.selectList("mlms_learning.selectLearningExcnAplcnModuleParamList", searchMap);
			for(SangsMap sangsMap : excnAplcnModuleParamList) {
				
				Map<String, Object> excnAplcnInsMap = new HashMap<String, Object>();
				excnAplcnInsMap.put("lrnExcnSn", nextlrnExcnSn);
				excnAplcnInsMap.put("moduleAplcnSn", sangsMap.getInt("moduleAplcnSn"));
				excnAplcnInsMap.put("moduleSn", sangsMap.getInt("moduleSn"));
				excnAplcnInsMap.put("paramtrNm", sangsMap.getString("paramtrNm"));
				excnAplcnInsMap.put("paramtrValue", sangsMap.getString("paramtrValue"));
				excnAplcnInsMap.put("regUserId", AuthUtil.getUserId());
				// 적용 모듈 파라미터 등록
				dao.insert("mlms_learning.insertAplcnModuleParamInfo", excnAplcnInsMap);
			}

			rtnMap.put("resultCd", "OK");
			
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
	 * 학습그룹 목록 조회
	 * @param paramMap 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getLearningGroupList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		int nextLrnGroupSn = dao.selectInteger("mlms_code.selectNextLearningGroupSn", null);
		
		List<SangsMap> lrnGroupList = dao.selectList("mlms_code.selectLearningGroupList", null);
		
		rtnMap.put("nextLrnGroupSn", nextLrnGroupSn);
		rtnMap.put("lrnGroupList", lrnGroupList);
		
		return rtnMap;
	}
	
	
	
	/**
	 * 학습그룹관리 등록/수정
	 * @param paramMap 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> saveLearningGroupInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		logger.debug("parameter : " + paramMap);
		List<Map<String, Object>> updateList = (List<Map<String, Object>>) paramMap.get("lrnGroupList");
		
		for (Map<String, Object> iu : updateList) {
			
			if (iu.get("iuId").equals("insert")) {
				iu.put("regUserId", AuthUtil.getUserId());
				iu.put("delYn", "N");
				
				dao.insert("mlms_learning.insertLearningGroupInfo", iu);
				
			} else {
				iu.put("chgUserId", AuthUtil.getUserId());
				dao.update("mlms_learning.updateLearningGroupInfo", iu);
			}
		}
		rtnMap.put("resultCd", "OK");
		
		return rtnMap;
	}
	
	
	
	
	/**
	 * 학습그룹관리 삭제
	 * @param paramMap 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> rmvLearningGroupInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		logger.debug("parameter : " + paramMap);
		
		dao.delete("mlms_learning.deleteLearningGroupInfo", paramMap);
		rtnMap.put("resultCd", "OK");
		
		return rtnMap;
	}
	
	
	
	
	/**
	 * 학습 목록 조회
	 * @param paramMap 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getLearningList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		int nextLrnSn = dao.selectInteger("mlms_learning.selectNextLearningSn", null);
		
		List<SangsMap> lrnMngList = dao.selectList("mlms_learning.selectLearningList", paramMap);
		
		rtnMap.put("nextLrnSn", nextLrnSn);
		rtnMap.put("lrnMngList", lrnMngList);
		
		return rtnMap;
	}
	
	
	
	/**
	 * 학습 정보 등록/수정
	 * @param paramMap 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> saveLearningInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		logger.debug("parameter : " + paramMap);
		List<Map<String, Object>> updateList = (List<Map<String, Object>>) paramMap.get("lrnMngList");
		
		for (Map<String, Object> iu : updateList) {
			
			if (iu.get("iuId").equals("insert")) {
				
				int nextLrnSn = dao.selectCount("mlms_learning.selectNextLearningSn", null);
				iu.put("lrnSn", nextLrnSn);
				iu.put("regUserId", AuthUtil.getUserId());
				iu.put("delYn", "N");
				dao.insert("mlms_learning.insertLearningInfo", iu);
				
			} else {
				iu.put("chgUserId", AuthUtil.getUserId());
				dao.update("mlms_learning.updateLearningInfo", iu);
			}
		}
		rtnMap.put("resultCd", "OK");
		
		return rtnMap;
	}
	
	
	
	
	/**
	 * 학습 정보 삭제
	 * @param paramMap 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> rmvLearningInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		logger.debug("parameter : " + paramMap);
		
		dao.delete("mlms_learning.deleteLearningInfo", paramMap);
		rtnMap.put("resultCd", "OK");
		
		return rtnMap;
	}
	
	
	
	/**
	 * 학습모델 테스트 목록 조회 
	 * @param paramMap 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getModelTestList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		logger.debug("parameter : " + paramMap);
		
		int pageNum = SangsStringUtil.nvlInt(paramMap.get("pageNum"), 1);
		
		// 전체 row 수 조회 
		int totalCount = dao.selectCount("mlms_learning.selectModelTestListCnt", paramMap);
		
		SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);
		
		// 목록 조회
		paramMap.put("pageSize", pagingInfo.getPageSize());
		paramMap.put("offset", pagingInfo.getOffset());
	
		List<SangsMap> list = dao.selectList("mlms_learning.selectModelTestList", paramMap);
	
		rtnMap.put("list", list);
		rtnMap.put("totalCount", totalCount);
		rtnMap.put("pagingInfo", pagingInfo);
		
		return rtnMap;
	}
	
	/**
	 * 학습모델 테스트 이력 목록 조회 
	 * @param paramMap 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getModelTestHistList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		logger.debug("parameter : " + paramMap);
		
		int pageNum = SangsStringUtil.nvlInt(paramMap.get("pageNum"), 1);
		
		// 전체 row 수 조회 
		int totalCount = dao.selectCount("mlms_learning.selectModelTestHistListCnt", paramMap);
		
		SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);
		
		// 목록 조회
		paramMap.put("pageSize", pagingInfo.getPageSize());
		paramMap.put("offset", pagingInfo.getOffset());
	
		List<SangsMap> list = dao.selectList("mlms_learning.selectModelTestHistList", paramMap);
	
		rtnMap.put("list", list);
		rtnMap.put("totalCount", totalCount);
		rtnMap.put("pagingInfo", pagingInfo);
		
		return rtnMap;
	}
	
	/**
	 * 학습수행 정보 조회 
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getLearningExcnInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		// 업로드 모델 여부 조회
		SangsMap uldModelInfo = dao.selectOne("mlms_learning.selectUldModelInfo", paramMap);
		SangsMap info = new SangsMap();
		if ("N".equals(uldModelInfo.get("uldModelYn"))) {
			// 수행 정보 조회(업로드 모델이 아닌경우 등록된 훈련에서 실행정보를 조회함)
			info = dao.selectOne("mlms_learning.selectLearningExcnInfo", paramMap);
		} else {
			// 수행 정보 조회(업로드 모델의 경우 실행순번으로 매핑된 데이터셋 정보를 조회)
			info = dao.selectOne("mlms_learning.selectUldModelExcnInfo", paramMap);
		}
		
		rtnMap.put("info", info);
		return rtnMap;
	}
	
	
	

	/**
	 * 테스트 실행
	 * 
	 * @param paramMap prameter Map
	 * @return map
	 * @throws Exception throws Exception
	 */
	public Map<String, Object> runTest(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			logger.debug("parameter : " + paramMap);
			// 필수 체크
			SangsStringUtil.checkRequiredParam(paramMap, "lrnExcnSn", "학습수행순번");
			
			//int[] arrlrnExcnSn = new int[] {Integer.parseInt((String)paramMap.get("lrnExcnSn"))};

			int nextTestHistSn = dao.selectInteger("mlms_learning.selectNextTestHistSn", paramMap);
			
			Map<String, Object> insMap = new HashMap<String, Object>();
			insMap.put("lrnExcnSn", paramMap.get("lrnExcnSn"));
			insMap.put("testHistSn", nextTestHistSn);
			insMap.put("lrnDatasetSn", paramMap.get("lrnDatasetSn"));
			insMap.put("resultFlpth", "result_"+paramMap.get("lrnExcnSn")+"_" + nextTestHistSn + ".csv");
			insMap.put("regUserId", AuthUtil.getUserId());
			dao.insert("mlms_learning.insertTestHistInfo", insMap);
			dao.commit();
			
			//dao.selectOne("mlms_learning.select", reqMap)
			//boolean rtnFlag = mlExecuterManager.executeMl(Integer.parseInt((String)paramMap.get("lrnExcnSn")), nextTestHistSn, "NEW", "TEST");
			boolean rtnFlag = mlExecuterManager.executeMl(Integer.parseInt( String.valueOf(paramMap.get("lrnExcnSn"))), nextTestHistSn, "NEW", "TEST");
			
			// 훈련/테스트 실행 
			//boolean rtnFlag = mlExecuterManager.executeMl(arrlrnExcnSn, "NEW", "TEST");
			
			if(!rtnFlag)
				throw new SangsMessageException("학습 모듈 실행시 에러가 발생하였습니다.");
			
			rtnMap.put("resultCd", "OK");
			rtnMap.put("lrnExcnSn", insMap.get("lrnExcnSn"));
			rtnMap.put("testHistSn", insMap.get("testHistSn"));
			insMap.put("lrnDatasetSn", insMap.get("lrnDatasetSn"));
			rtnMap.put("resultFlpth", insMap.get("resultFlpth"));
			
			
		} catch(SangsMessageException e) {
			e.printStackTrace();
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
			//throw e;
			
		}
		 
		return rtnMap;
	}
	
	
	
	
	
	/**
	 * 모델 업로드 / 실행순번 추가
	 * 
	 * @param paramMap prameter Map
	 * @return map
	 * @throws Exception throws Exception
	 */
	public Map<String, Object> saveUldModelPublishing(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
			int lrnDatasetSn;
			logger.debug("upload model save paramMap :" + paramMap);
			
			if ("R".equals(paramMap.get("pmode"))) {
				// 신규 학습실행순번 채번 
				int nextLrnExcnSn = dao.selectCount("mlms_learning.selectNextLearningExcnSn", null);
				logger.debug("nextLrnExcnSn : " + nextLrnExcnSn);
				
				// 학습실행 정보 등록
				Map<String, Object> insExcnMap = new HashMap<String, Object>();
				if ((paramMap.get("crtModelFlpth") == null) || ("".equals(paramMap.get("crtModelFlpth")))) {
					insExcnMap.put("crtModelFlpth", null);
				} else {					
					insExcnMap.put("crtModelFlpth", "model_"+nextLrnExcnSn+".h5");
				}
				
				insExcnMap.put("lrnExcnSn", nextLrnExcnSn);	// 신규 번호로 set
				insExcnMap.put("lrnSn", paramMap.get("lrnSn"));
				insExcnMap.put("lrnStepCd", "PUBLISHED");	// 퍼블리싱
				insExcnMap.put("trainglrnExcnSn", null);		// 훈련한 수행 순번
				insExcnMap.put("uldModelYn", "Y");
				dao.insert("mlms_learning.insertLearningExcnInfo", insExcnMap);
				
				int nextLrnDatasetSn = dao.selectCount("mlms_learning.selectNextLearningDatasetSn", null);
				logger.debug("nextLrnDatasetSn : " + nextLrnDatasetSn);
				
				Map<String, Object> insLrnDataMap = new HashMap<String, Object>();
				insLrnDataMap.put("lrnDatasetSn", nextLrnDatasetSn);
				insLrnDataMap.put("datasetSn", null);
				insLrnDataMap.put("datasetTyCd", "TEST");
				insLrnDataMap.put("lrnDatasetNm", paramMap.get("lrnDatasetNm"));
				insLrnDataMap.put("lrnDatasetDc", paramMap.get("lrnDatasetDc"));
				insLrnDataMap.put("exclRowCnt", paramMap.get("exclRowCnt"));
				insLrnDataMap.put("delYn", "N");
				insLrnDataMap.put("regUserId", null);
				dao.insert("mlms_dataset.insertLearningDatasetInfo", insLrnDataMap);
				
				Map<String, Object> insUldInfoMap = new HashMap<String, Object>();
				insUldInfoMap.put("lrnExcnSn", nextLrnExcnSn);	// 신규 번호로 set
				insUldInfoMap.put("lrnDatasetSn", nextLrnDatasetSn);
				insUldInfoMap.put("testExcnFlpth", nextLrnExcnSn + "/" + paramMap.get("testExcnFlpth"));
				dao.insert("mlms_learning.insertUploadModelInfo", insUldInfoMap);
				
				lrnDatasetSn = nextLrnDatasetSn;
				rtnMap.put("nextLrnExcnSn", nextLrnExcnSn);
				rtnMap.put("crtModelFlpth", insExcnMap.get("crtModelFlpth"));
				
			// pmode = M(수정)
			} else {
				Map<String, Object> updExcnMap = new HashMap<String, Object>();
				updExcnMap.put("lrnExcnSn", paramMap.get("lrnExcnSn"));	// 신규 번호로 set
				updExcnMap.put("lrnSn", paramMap.get("lrnSn"));
				dao.update("mlms_learning.updateUploadModelInfo", updExcnMap);
				
				Map<String, Object> updLrnDataMap = new HashMap<String, Object>();
				updLrnDataMap.put("lrnDatasetNm", paramMap.get("lrnDatasetNm"));
				updLrnDataMap.put("lrnDatasetDc", paramMap.get("lrnDatasetDc"));
				updLrnDataMap.put("exclRowCnt", paramMap.get("exclRowCnt"));
				updLrnDataMap.put("lrnDatasetSn", paramMap.get("lrnDatasetSn"));
				dao.update("mlms_learning.updateUploadDatasetInfo", updLrnDataMap);
				
				if (!("".equals(paramMap.get("testExcnFlpth"))) && (paramMap.get("testExcnFlpth") != null)) {
					Map<String, Object> updPyInfo = new HashMap<String, Object>();
					updPyInfo.put("testExcnFlpth", paramMap.get("lrnExcnSn") + "/" + paramMap.get("testExcnFlpth"));
					updPyInfo.put("lrnExcnSn", paramMap.get("lrnExcnSn"));
					dao.update("mlms_learning.updateUploadPyInfo", updPyInfo);
				}
				
				dao.delete("mlms_dataset.deleteLearningDatasetHeaderInfo", updLrnDataMap);
				dao.delete("mlms_dataset.deleteLearningDatasetLabelInfo", updLrnDataMap);
				
				lrnDatasetSn = Integer.parseInt(String.valueOf(paramMap.get("lrnDatasetSn")));
				rtnMap.put("nextLrnExcnSn",  paramMap.get("lrnExcnSn"));
				rtnMap.put("crtModelFlpth", "model_" + paramMap.get("lrnExcnSn") + ".h5");
			}
			
			
			List<Map<String, Object>> hderList = (List<Map<String, Object>>) paramMap.get("hderList");
			List<Object> trgtValueYnArr = new ArrayList<Object>();
			for (int i=0; i<hderList.size(); i++) {
				Map<String, Object> insHderMap = hderList.get(i);
				insHderMap.put("lrnDatasetSn", lrnDatasetSn);
				insHderMap.put("useYn", "Y");
				insHderMap.put("hderIndx", i);
				trgtValueYnArr.add(insHderMap.get("trgtValueYn"));
				insHderMap.put("regUserId", AuthUtil.getUserId());
				if (i == hderList.size()-1) {
					if (!(trgtValueYnArr.contains("Y"))) {
						insHderMap.put("trgtValueYn", "Y");
					}
				}
				dao.insert("mlms_dataset.insertLearningDatasetHeaderInfo", insHderMap);
				
			}
			
			List<Map<String, Object>> lblList = (List<Map<String, Object>>) paramMap.get("lblList");
			if (lblList.size() > 1) {
				for (int i=0; i<lblList.size(); i++) {
					Map<String, Object> insLblMap = lblList.get(i);
					insLblMap.put("lrnDatasetSn", lrnDatasetSn);
					insLblMap.put("regUserId", AuthUtil.getUserId());
					dao.insert("mlms_dataset.insertLearningDatasetLabelInfo", insLblMap);
				}
			}
			
			rtnMap.put("resultCd", "OK");
			
		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		
		return rtnMap;
	}
	
	
}



