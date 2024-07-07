package com.sangs.mlms.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.support.CommonDao;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsCsvFileLoader;
import com.sangs.mlms.common.MlmsConstant;
import com.sangs.mlms.controller.ApiController;

@SangsService
public class ApiLearningService extends ServiceBase {

	
	
	@Autowired
	private LearningService learningService;
	

	@Value("${mls.resource.base_path:}")
	private String resourceBaseBath;
	
	
	
	@Autowired
	private CommonDao dao;
	
	public Map<String, Object> runTestFromApi(int lrnExcnSn, int exclRowCnt, Map<String, String> fileParamMap, ApiController apiControllerClass, String reqServerUrl) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		

		
		try {
			
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("lrnExcnSn", lrnExcnSn);
			
			// 업로드 모델 여부 조회
			SangsMap uldModelInfo = dao.selectOne("mlms_learning.selectUldModelInfo", paramMap);
			SangsMap info = new SangsMap();
			if ("N".equals(uldModelInfo.get("uldModelYn"))) {
				paramMap.put("lrnExcnSn", uldModelInfo.get("traingLrnExcnSn"));
				// 수행 정보 조회(업로드 모델이 아닌경우 등록된 훈련에서 실행정보를 조회함)
				info = dao.selectOne("mlms_learning.selectLearningExcnInfo", paramMap);
			} else {
				// 수행 정보 조회(업로드 모델의 경우 실행순번으로 매핑된 데이터셋 정보를 조회)
				info = dao.selectOne("mlms_learning.selectUldModelExcnInfo", paramMap);
			}

			if(info == null)
				throw new SangsMessageException("학습수행정보가 존재 하지 않습니다.");
			
			
			// dataset 순번 채번
			int datasetSn = dao.selectCount("mlms_dataset.selectNextDatasetSn", null);	

			String newFileId = fileParamMap.get("newFileId");
			String orgFileNm = fileParamMap.get("orgFileNm");
			String orgExtFileNm = fileParamMap.get("orgExtFileNm");
			String saveFilePath = fileParamMap.get("saveFilePath");
			String fileNm = newFileId + orgExtFileNm;

	
			// csv file load
			SangsCsvFileLoader csvFileLoader = new SangsCsvFileLoader(saveFilePath + fileNm, true);
			int fileSizeKb = csvFileLoader.getFileSizeKb();
			int dataRowCnt = csvFileLoader.getBodyList().size();
			int dataColCnt = csvFileLoader.getHeaderList().size();
			String datasetNm = info.getString("lrnNm") + "_" + newFileId;		// 데이터셋명 = 학습명 + 물리파일명(UUID)
			String dataFlpth = MlmsConstant.RESOURCE_DATASET_BASE_PATH + MlmsConstant.RESOURCE_API_DATASET_BASE_PATH + fileNm;
			
			Map<String, Object> sourceDataInsMap = new HashMap<String, Object>();
			sourceDataInsMap.put("datasetSn", datasetSn);
			sourceDataInsMap.put("datasetNm", datasetNm);
			sourceDataInsMap.put("datasetDc", "API를 통해서 등록된 데이터 입니다.");
			sourceDataInsMap.put("dataRowCnt", dataRowCnt);
			sourceDataInsMap.put("dataColCnt", dataColCnt);
			sourceDataInsMap.put("dataFlpth", dataFlpth);
			sourceDataInsMap.put("dataOrginlFlpth", orgFileNm);
			sourceDataInsMap.put("dataKbSize", fileSizeKb);
			sourceDataInsMap.put("delYn", "N");
			sourceDataInsMap.put("regUserId", "API");
			sourceDataInsMap.put("chgUserId", "API");
			sourceDataInsMap.put("apiDataYn", "Y");		//API 를 통한 데이터 셋 등록인경우
			
			
			// 1. 원천데이터 셋 등록
			dao.insert("mlms_dataset.insertSourceDatasetInfo", sourceDataInsMap);
			
			
			// 학습데이터 등록 
			Map<String, Object> lrnDataInsMap = new HashMap<String, Object>();
			int lrnDatasetSn = dao.selectInteger("mlms_dataset.selectNextLearningDatasetSn", null);	// 학습데이터셋순번 채번
			lrnDataInsMap.put("lrnDatasetSn", lrnDatasetSn);
			lrnDataInsMap.put("datasetSn", datasetSn);
			lrnDataInsMap.put("datasetTyCd", "TEST");
			lrnDataInsMap.put("lrnDatasetNm", datasetNm);
			lrnDataInsMap.put("lrnDatasetDc", "API를 통해서 등록된 데이터 입니다.");
			lrnDataInsMap.put("exclRowCnt", exclRowCnt);
			lrnDataInsMap.put("delYn", "N");
			lrnDataInsMap.put("regUserId", "API");
			lrnDataInsMap.put("chgUserId", "API");
			
			
			// 2. 학습데이터 셋 등록
			dao.insert("mlms_dataset.insertLearningDatasetInfo", lrnDataInsMap);
			
			// 3. 기존 학습데이터셋의 해더 정조 조회
			Map<String, Object> paramMap2 = new HashMap<String, Object>();
			paramMap2.put("lrnDatasetSn", info.get("lrnDatasetSn"));
			List<SangsMap> dsHeaderList = dao.selectList("mlms_dataset.selectLearningDatasetHeaderList", paramMap2);
			
			// 4. 기존 학습데이터셋의 라벨정보 조회 
			List<SangsMap> dsLabelList = dao.selectList("mlms_dataset.selectLearningDatasetLabelList", paramMap2);
			
			// 5. 해더 정보 insert 
			for(SangsMap headerMap : dsHeaderList) {
				//if(!"Y".equals(headerMap.getString("trgtValueYn"))) {
					headerMap.putOrg("lrnDatasetSn", lrnDatasetSn);
					headerMap.putOrg("regUserId", "API");
					headerMap.putOrg("chgUserId", "API");
					dao.insert("mlms_dataset.insertLearningDatasetHeaderInfo", headerMap);
				//}
			}
			
			
			// 6. 라벨 정보 insert 
			for(SangsMap headerMap : dsLabelList) {
				headerMap.putOrg("lrnDatasetSn", lrnDatasetSn);
				headerMap.putOrg("regUserId", "API");
				headerMap.putOrg("chgUserId", "API");
				dao.insert("mlms_dataset.insertLearningDatasetLabelInfo", headerMap);
			}
			
			
			dao.commit();
			
			
			// 3. 테스트 실행
			try {
				 
				
				//apiControllerClass.updatePrgs(reqServerUrl, cid, stepId, "10");
				
				Map<String, Object> runParamMap = new HashMap<String, Object>();
				runParamMap.put("lrnExcnSn", lrnExcnSn);
				runParamMap.put("lrnDatasetSn", lrnDatasetSn);
				if (info.containsKey("uldModelYn")) {
					runParamMap.put("uldModelYn", info.get("uldModelYn"));					
				} else {
					runParamMap.put("uldModelYn", "N");
				}
				Map<String, Object> resultMap = learningService.runTest(runParamMap);

				
				if(resultMap == null || resultMap.get("resultFlpth") == null) {
					throw new SangsMessageException("학습모듈 수행중 에러가 발생하였습니다.");
				}
				String resultFlpth = (String)resultMap.get("resultFlpth");
				String resultFullPath = resourceBaseBath + MlmsConstant.RESOURCE_TESTRESULT_BASE_PATH + lrnExcnSn + "/" +resultFlpth;
				logger.debug("결과 파일 경로 : " + resultFullPath);
				
				resultMap.put("resultFlpth", resultFlpth);
				
				
				File resultFile = new File(resultFullPath);
				if(!resultFile.exists())
					throw new SangsMessageException("학습모듈 수행중 에러가 발생하였습니다.(결과파일 생성 에러)");
				
				rtnMap.putAll(resultMap);
				
			} catch(SangsMessageException e) {
				e.printStackTrace();
				throw e;
			} catch(Exception e) {
				e.printStackTrace();
				throw new SangsMessageException("학습모듈 호출 중 에러가 발생하였습니다.");
			} 
			
		} catch(Exception e) {
			
			e.printStackTrace();
			logger.error("", e);
			throw e;
		}
		
		return rtnMap;
	}
	
	
}
