package com.sangs.mlms.service;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.support.CommonDao;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsCsvFileLoader;
import com.sangs.lib.support.utils.SangsFileUtil;
import com.sangs.lib.support.utils.SangsStringUtil;
import com.sangs.mlms.common.MlmsConstant;

/**
 * 결과 분석 관련 Service 
 * 
 * 
 * @author id.yoon
 *
 */
@SangsService
public class LearningResultAnalysisService extends ServiceBase {

	@Autowired
	private DatasetService datasetService;
	
	@Autowired
	private CommonDao dao;

	@Value("${mls.log.path:}")
	private String learningLogPath;
	
	@Value("${mls.resource.base_path:}")
	private String datasetRootPath;

	String testresultBasePath = MlmsConstant.RESOURCE_TESTRESULT_BASE_PATH;
	
	/**
	 * 결과 분석 정보 조회 
	 * 
	 * @param paramMap input parameter
	 * @return Map type result 
	 * @throws Exception throws Exception
	 */
	public Map<String, Object> getResultAnalysisInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		logger.debug("lrn_excn_sn : " + paramMap);
		// 로그 파일 읽기
		SangsFileUtil readFile = new SangsFileUtil();
		
		Map<String, Object> trainAnalysisInfo = new HashMap<String, Object>();
		Map<String, Object> testAnalysisInfo = new HashMap<String, Object>();
		Map<String, Object> resultAnalysisInfo = new HashMap<String, Object>();
		SangsMap lrnInfo = new SangsMap();
		String trainLog = "";
		String testLog = "";
		String uldModelYn = "";
		
		try {
			
			SangsMap uldModelInfo = dao.selectOne("mlms_learning.selectUldModelInfo", paramMap);
			
			if ("Y".equals(uldModelInfo.get("uldModelYn"))) {
				
				lrnInfo = dao.selectOne("mlms_learning.selectUldModelExcnInfo", paramMap);
				
				
				String testLogPath = learningLogPath + "test_" + (String)paramMap.get("lrnExcnSn") + "_" + (String)paramMap.get("testHistSn") + ".log";
				try {
					StringBuffer testLogBuffer = readFile.getFileText(testLogPath);
					testLog = testLogBuffer.toString();
					
				} catch (Exception e) {
					testLog = "테스트 로그 파일을 찾을 수 없습니다.";
				}
				
				uldModelYn = (String)uldModelInfo.get("uldModelYn");
				
				
			} else {
				lrnInfo = dao.selectOne("mlms_learning.selectLearningExcnInfo", paramMap);
				
				List<SangsMap> modelAttrIpt = dao.selectList("mlms_learning.selectModelAttrImportantList", lrnInfo);
				trainAnalysisInfo = datasetService.getTrainDataAnalysisInfo(lrnInfo);				
				testAnalysisInfo = datasetService.getTestDataAnalysisInfo(lrnInfo);
				
				// 결과파일 에러인경우
				try {
					resultAnalysisInfo = getResultDataset(lrnInfo);
				} catch (Exception e) {
					resultAnalysisInfo.put("BY_RESULT_HEADER", new ArrayList<String>());
					resultAnalysisInfo.put("BY_RESULT_CONTENTS", new ArrayList<Map<String, String>>()); // 에측 실패 table
					resultAnalysisInfo.put("BY_LABEL", new HashMap<String, Object>()); // 타겟별 분류 accuracy
					resultAnalysisInfo.put("BY_CONFUSION_MATRIX", new HashMap<String, Object>()); // confusion matrix
				}
				
				
				int testHistSn = lrnInfo.getInt("testHistSn");
				if(testHistSn == 0)
					testHistSn = 1;
				
				String trainLogPath = learningLogPath + "train_" + lrnInfo.get("traingLrnExcnSn") + ".log";
				try { 
					StringBuffer trainLogBuffer = readFile.getFileText(trainLogPath);
					trainLog = trainLogBuffer.toString();
				} catch (Exception e) {
					trainLog = "훈련 로그 파일을 찾을 수 없습니다.";
				}
				
				String testLogPath = learningLogPath + "test_" + lrnInfo.get("lrnExcnSn") + "_" + testHistSn + ".log";
				try {
					StringBuffer testLogBuffer = readFile.getFileText(testLogPath);
					testLog = testLogBuffer.toString();
				} catch (Exception e) {
					testLog = "테스트 로그 파일을 찾을 수 없습니다.";
				}
				
				
				
				if (modelAttrIpt.size() > 0) {
					rtnMap.put("modelAttrIpt", modelAttrIpt);
				} else {
					rtnMap.put("modelAttrIpt", "");
				}
				
				uldModelYn = "N";
				
			}
			
			rtnMap.put("lrnInfo", lrnInfo);
			rtnMap.put("trainAnalysisInfo", trainAnalysisInfo);
			rtnMap.put("testAnalysisInfo", testAnalysisInfo);
			rtnMap.put("resultAnalysisInfo", resultAnalysisInfo);
			rtnMap.put("trainLog", trainLog);
			rtnMap.put("testLog", testLog);
			rtnMap.put("uldModelYn", uldModelYn);

		} catch(SangsMessageException e) {
			logger.error("", e);
		} catch(Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		
		
		return rtnMap;
	}
	
	
	// Call by getResultAnalysisInfo
	public Map<String, Object> getResultDataset(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		Map<String, Object> targetParam = new HashMap<String, Object>();
		List<Map<String, String>> bodyListFail = new ArrayList<Map<String, String>>();
		Map<String, HashMap<String, Integer>> targetMapping = new HashMap<String, HashMap<String, Integer>>();
		
		targetParam.put("lrnDatasetSn", paramMap.get("lrnDatasetSn"));

		Map<String, String> targetNmMap = datasetService.getTargetNmMap(targetParam);

		String mapKeyPrefix = "_key_";
		// SangsCsvFileLoader csvLoder = new SangsCsvFileLoader(datasetRootPath +
		// testresultBasePath + (String)paramMap.get("resultFilePath"), true);
		String resultFlpth = (String) paramMap.get("resultFlpth");

		try {
			SangsCsvFileLoader csvLoder = new SangsCsvFileLoader(datasetRootPath + testresultBasePath
					+ paramMap.get("lrnExcnSn") + "/" + resultFlpth.replace(".csv", ".csv"), ",", true, true);
			List<String> headerList = csvLoder.getHeaderList();
			List<Map<String, String>> bodyList = csvLoder.getBodyList();
			
			
			// 라벨 등록 되어있을 경우
			if ((targetNmMap != null) && (!targetNmMap.isEmpty())) {
				// 타겟 라벨 등록 했을 때 
				
				for (String targetReal: targetNmMap.keySet()) {
					HashMap<String, Integer> targetPredictMap = new HashMap<String, Integer>();
					for (String targetPredict: targetNmMap.keySet()) {
						targetPredictMap.put(targetNmMap.get(targetPredict), 0);
					}
					targetMapping.put(targetNmMap.get(targetReal), targetPredictMap);
				}
				
				
				// 각 클래스별 분류 정확도, 각 클래스별 예측 행렬 , 예측 실패 목록
				
				for (int i=0; i<bodyList.size(); i++) {
					Map<String, String> bodyListRow = bodyList.get(i);
					
					bodyListRow.put("Target", targetNmMap.get(bodyListRow.get("Target")));
					bodyListRow.put("Predict", targetNmMap.get(bodyListRow.get("Predict")));
					
					// 예측 실패 목록 데이터
					if (bodyListRow.get("Target") != bodyListRow.get("Predict")) {
						bodyListFail.add(bodyListRow);
					}
					
					bodyList.set(i, bodyListRow);
					
					HashMap<String, Integer> targetMappingPredict = targetMapping.get(bodyListRow.get("Target"));
					Integer cnt = targetMappingPredict.get(bodyListRow.get("Predict"));
					cnt += 1;
					targetMappingPredict.put(bodyListRow.get("Predict"), cnt);
					targetMapping.put(bodyListRow.get("Target"), targetMappingPredict);
				}
			}
			
			// 라벨 등록 안되있을 경우
			else {
				for (int i=0; i<bodyList.size(); i++) {
					Map<String, String> bodyListRow = bodyList.get(i);
					if (!bodyListRow.get("Target").equals(bodyListRow.get("Predict"))) {
						bodyListFail.add(bodyListRow);
					}
					
					if (targetMapping.keySet().contains(bodyListRow.get("Target"))) {
						HashMap<String, Integer> targetMappingRow = targetMapping.get(bodyListRow.get("Target"));
						if (targetMappingRow.keySet().contains(bodyListRow.get("Predict"))) {
							Integer cnt = targetMappingRow.get(bodyListRow.get("Predict"));
							cnt += 1;
							targetMappingRow.put(bodyListRow.get("Predict"), cnt);
						} else {
							targetMappingRow.put(bodyListRow.get("Predict"), 1);
						}
						targetMapping.put(bodyListRow.get("Target"), targetMappingRow);
						
					} else {
						HashMap<String, Integer> targetMappingRow = new HashMap<String, Integer>();
						targetMappingRow.put(bodyListRow.get("Predict"), 1);
						targetMapping.put(bodyListRow.get("Target"), targetMappingRow);
					}
				}
			}
			
			
			
			for (String target: targetMapping.keySet()) {
				
				HashMap<String, Integer> targetMappingRow = targetMapping.get(target);
				for (String predict: targetMapping.keySet()) {
					if (!targetMappingRow.keySet().contains(predict)) {
						targetMappingRow.put(predict, 0);
					}
				}
				targetMapping.put(target, targetMappingRow);
			}
			
			
			/*
			List<String> targetList = new ArrayList<String>();
			for (int i = 0; i < bodyListFail.size(); i++) {
				Map<String, String> bodyListRow = new HashMap<String, String>();
				bodyListRow = bodyListFail.get(i);
				String target = bodyListRow.get("Target");
				targetList.add(target);
			}

			List<String> targetName = targetList.stream().distinct().collect(Collectors.toList());
			List<Integer> targetCount = new ArrayList<Integer>();
			for (String s : targetName) {
				targetCount.add(Collections.frequency(targetList, s));
			}

			Map<String, Object> byLabelMapVal = new HashMap<String, Object>();
			for (int i = 0; i < targetName.size(); i++) {
				byLabelMapVal.put(targetName.get(i), targetCount.get(i));
			}

			List<String> keySort = new ArrayList<String>();
			byLabelMapVal.forEach((key, value) -> {
				keySort.add(key);
			});

			Map<String, Object> byLabelMap = new HashMap<String, Object>();
			Collections.sort(keySort);
			for (String str : keySort) {
				int cnt = (int) byLabelMapVal.get(str);
				String targetKey = str.replace(mapKeyPrefix, "");
				// rtnMap.put(targetKey, cnt);

				String outKey = "";
				if (SangsStringUtil.isEmpty(targetNmMap.get(targetKey)))
					outKey = targetKey;
				else
					outKey = targetNmMap.get(targetKey);

				logger.debug(targetKey + "(" + outKey + ")" + "\t" + cnt);

				byLabelMap.put(outKey, cnt);
			}


			
			
			// confusion matrix 데이터 생성
			Map<String, HashMap<String, Integer>> targetMap = new HashMap<String, HashMap<String, Integer>>();
			
			for (Map<String, String> bodyListRow : bodyList) {

				String predict = bodyListRow.get("Predict");
				String target = bodyListRow.get("Target");

				if (!(targetMap.containsKey(target))) {

					Map<String, Integer> predictMap = new HashMap<String, Integer>();
					predictMap.put(predict, 1);
					targetMap.put(target, (HashMap<String, Integer>) predictMap);

				} else {

					Map<String, Integer> predictMap = targetMap.get(target);

					if (!(predictMap.containsKey(predict))) {

						predictMap.put(predict, 1);

					} else {

						int cnt = predictMap.get(predict) + 1;
						predictMap.put(predict, cnt);
						targetMap.put(target, (HashMap<String, Integer>) predictMap);

					}
				}
			}
			
			Map<String, Object> cmTargetMap = new HashMap<String, Object>();
			Collections.sort(keySort);
			for (String str : keySort) {
				Map<String, Integer> obj = targetMap.get(str);

				// rtnMap.put(targetKey, cnt);

				List<String> inKeySort = new ArrayList<String>();
				obj.forEach((inkey, invalue) -> {
					inKeySort.add(inkey);
				});

				Map<String, Integer> changeObj = new HashMap<String, Integer>();
				Collections.sort(inKeySort);
				for (String instr : inKeySort) {
					int inObj = obj.get(instr);
					String inTargetKey = instr.replace(mapKeyPrefix, "");
					String inoutKey = "";
					if (SangsStringUtil.isEmpty(targetNmMap.get(inTargetKey)))
						inoutKey = inTargetKey;
					else
						inoutKey = targetNmMap.get(inTargetKey);

					changeObj.put(inoutKey, inObj);

				}

				String targetKey = str.replace(mapKeyPrefix, "");
				String outKey = "";
				if (SangsStringUtil.isEmpty(targetNmMap.get(targetKey)))
					outKey = targetKey;
				else
					outKey = targetNmMap.get(targetKey);

				cmTargetMap.put(outKey, changeObj);
			}
			*/
			
			
			
			
			
			
			rtnMap.put("BY_RESULT_HEADER", headerList);
			rtnMap.put("BY_RESULT_CLC", targetMapping);
			rtnMap.put("BY_RESULT_CONTENTS", bodyListFail); // 에측 실패 table
			
			//rtnMap.put("BY_LABEL", byLabelMap); // 타겟별 분류 accuracy
			//rtnMap.put("BY_CONFUSION_MATRIX", cmTargetMap); // confusion matrix

		} catch (SangsMessageException e) {
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}
	
	
	
}
