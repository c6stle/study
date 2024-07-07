package com.sangs.mlms.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

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
import com.sangs.lib.support.utils.SangsCsvFileLoader;
import com.sangs.lib.support.utils.SangsFileUtil;
import com.sangs.lib.support.utils.SangsStringUtil;
import com.sangs.mlms.common.MlmsConstant;

/**
 * 데이터 셋 관련 Service
 * 
 * 
 * @author id.yoon
 *
 */
@SangsService
public class DatasetService extends ServiceBase {

	@Autowired
	private CommonDao dao;

	@Value("${mls.resource.base_path:}")
	private String datasetRootPath;

	String datasetBasePath = MlmsConstant.RESOURCE_DATASET_BASE_PATH;

	String testresultBasePath = MlmsConstant.RESOURCE_TESTRESULT_BASE_PATH;

	/**
	 * 원천데이터셋 정보 조회
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getSourceDatasetInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		SangsMap datasetInfo = dao.selectOne("mlms_dataset.selectSourceDatasetInfo", paramMap);

		rtnMap.put("info", datasetInfo);
		return rtnMap;
	}
	
	
	/**
	 * 훈련데이터 분석 정보 조회
	 * 
	 * @param paramMap input parameter
	 * @return Map type result
	 * @throws Exception throws Exception
	 */
	public Map<String, Object> getTrainDataAnalysisInfo(Map<String, Object> paramMap) throws Exception {
		
		int exclRowCnt = Integer.parseInt(String.valueOf(paramMap.get("exclRowCnt")));
		int traingTrgetHderIndx = Integer.parseInt(String.valueOf(paramMap.get("traingTrgetHderIndx")));
		int lrnDatasetSn = Integer.parseInt(String.valueOf(paramMap.get("lrnDatasetSn")));
		
		Map<String, Object> trainAnalysisInfo = getDatasetFreqInfo(datasetRootPath + (String) paramMap.get("traingDataFlpth"), exclRowCnt, traingTrgetHderIndx, lrnDatasetSn);

		return trainAnalysisInfo;
	}

	/**
	 * 테스트데이터 분석 정보 조회
	 * 
	 * @param paramMap input parameter
	 * @return Map type result
	 * @throws Exception throws Exception
	 */
	public Map<String, Object> getTestDataAnalysisInfo(Map<String, Object> paramMap) throws Exception {
		
		int exclRowCnt = Integer.parseInt(String.valueOf(paramMap.get("exclRowCnt")));
		int testTrgetHderIndx = Integer.parseInt(String.valueOf(paramMap.get("testTrgetHderIndx")));
		int lrnDatasetSn = Integer.parseInt(String.valueOf(paramMap.get("lrnDatasetSn")));
		
		Map<String, Object> testAnalysisInfo = getDatasetFreqInfo(datasetRootPath + (String) paramMap.get("testDataFlpth"), exclRowCnt, testTrgetHderIndx, lrnDatasetSn);

		return testAnalysisInfo;
	}

	/**
	 * 원천데이터셋 목록 조회
	 * 
	 * @param paramMap input parameter
	 * @return Map type result
	 * @throws Exception throws Exception
	 */
	public Map<String, Object> getSourceDatasetList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			logger.debug("searching keywords : " + paramMap);

			int pageNum = SangsStringUtil.nvlInt(paramMap.get("pageNum"), 1);

			int totalCount = dao.selectCount("mlms_dataset.selectSourceDatasetListCnt", paramMap);
			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);

			paramMap.put("pageSize", pagingInfo.getPageSize());
			paramMap.put("offset", pagingInfo.getOffset());
			
			List<SangsMap> sourceDataList = dao.selectList("mlms_dataset.selectSourceDatasetList", paramMap);

			rtnMap.put("sourceDataList", sourceDataList);
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
	 * 원천데이터셋 정보 조회
	 * 
	 * @param paramMap input parameter
	 * @return Map type result
	 * @throws Exception throws Exception
	 */
	public Map<String, Object> viewSourceDatasetForm(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			// datasetSn, pmode, dataFlpth 필수 파라미터 -> R 등록 모드일때는 datasetSn 없음(채번 필요)

			if ("R".equals(paramMap.get("pmode"))) {
				
				SangsCsvFileLoader csvLoder = new SangsCsvFileLoader(
						datasetRootPath + paramMap.get("subDir") + paramMap.get("savedFileNm"), true);
				List<String> headerList = csvLoder.getHeaderList();
				List<Map<String, String>> bodyList = csvLoder.getBodyList();

				Map<String, Object> datasetInfo = new HashMap<String, Object>();
				datasetInfo.put("dataColCnt", headerList.size());
				datasetInfo.put("dataRowCnt", bodyList.size());
				datasetInfo.put("datasetSn", "");
				datasetInfo.put("datasetNm", "");
				datasetInfo.put("datasetDc", "");
				datasetInfo.put("dataOriginDc", "");
				datasetInfo.put("regDt", "");
				datasetInfo.put("regUserId", "");
				datasetInfo.put("dataKbSize", csvLoder.getFileSizeKb());
				datasetInfo.put("dataFlpth", (String) paramMap.get("subDir") + paramMap.get("savedFileNm"));
				datasetInfo.put("dataOrginlFlpth", paramMap.get("orgFileNm"));
				datasetInfo.put("pmode", paramMap.get("pmode"));
				
				// 데이터셋 3000개 까지만 표시
				int previewSize = 0;
				if (bodyList.size() > 1000) {
					bodyList = bodyList.subList(0, 1000);
					previewSize = bodyList.size();
				}
				
				rtnMap.put("previewSize", previewSize);
				rtnMap.put("headerList", headerList);
				rtnMap.put("bodyList", bodyList);
				rtnMap.put("datasetInfo", datasetInfo);
				rtnMap.put(SangsConstants.FORWARD_VIEW, "mlms/dataset/source_dataset_form");

			} else if ("M".equals(paramMap.get("pmode"))) {

				SangsMap datasetInfo = dao.selectOne("mlms_dataset.selectSourceDatasetInfo", paramMap);
				datasetInfo.putOrg("pmode", paramMap.get("pmode"));

				SangsCsvFileLoader csvLoder = new SangsCsvFileLoader(datasetRootPath + datasetInfo.get("dataFlpth"), true);
				// list
				List<String> headerList = csvLoder.getHeaderList();
				// row map
				List<Map<String, String>> bodyList = csvLoder.getBodyList();
				
				// 데이터셋 3000개 까지만 표시
				int previewSize = 0;
				if (bodyList.size() > 1000) {
					bodyList = bodyList.subList(0, 1000);
					previewSize = bodyList.size();
				}
				
				rtnMap.put("previewSize", previewSize);
				rtnMap.put("headerList", headerList);
				rtnMap.put("bodyList", bodyList);
				rtnMap.put("datasetInfo", datasetInfo);
				rtnMap.put(SangsConstants.FORWARD_VIEW, "mlms/dataset/source_dataset_form");
			}

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
	 * 원천데이터셋 정보 수정
	 * 
	 * @param paramMap input parameter
	 * @return Map type result
	 * @throws Exception throws Exception
	 */
	public Map<String, Object> saveSourceDatasetInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		logger.debug("update infomation : " + paramMap);

		try {
			paramMap.put("delYn", "N");
			paramMap.put("regUserId", AuthUtil.getUserId());
			paramMap.put("chgUserId", AuthUtil.getUserId());

			if ("M".equals(paramMap.get("pmode"))) {
				dao.update("mlms_dataset.updateSourceDatasetInfo", paramMap);

			} else if ("R".equals(paramMap.get("pmode"))) {
				
				paramMap.put("datasetSn", dao.selectInteger("mlms_dataset.selectNextDatasetSn", null));	// datasetSn 순번 채번
				paramMap.put("apiDataYn", "N"); 		// API 로 등록된 데이터 셋이 아닌경우
				dao.insert("mlms_dataset.insertSourceDatasetInfo", paramMap);

			} else if ("D".equals(paramMap.get("pmode"))) {
				paramMap.put("delYn", "Y");
				dao.update("mlms_dataset.updateSourceDatasetInfo", paramMap);

			}

			rtnMap.put("resultCd", "OK");

		} catch (SangsMessageException e) {
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}

	/**
	 * 원천데이터셋 파일 등록 -----> 메서드 삭제 예정
	 * 
	 * @param paramMap input parameter
	 * @return Map type result
	 * @throws Exception throws Exception
	 */
	public Map<String, Object> regSourceDatasetFile(String dataFilePath) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			SangsCsvFileLoader csvLoder = new SangsCsvFileLoader(dataFilePath, true);

			List<String> headerList = csvLoder.getHeaderList();
			List<Map<String, String>> bodyList = csvLoder.getBodyList();

			Map<String, Object> datasetInfo = new HashMap<String, Object>();
			datasetInfo.put("dataColCnt", headerList.size());
			datasetInfo.put("dataRowCnt", bodyList.size());
			
			// 데이터셋 3000개 까지만 표시
			int previewSize = 0;
			if (bodyList.size() > 1000) {
				bodyList = bodyList.subList(0, 1000);
				previewSize = bodyList.size();
			}
			
			rtnMap.put("previewSize", previewSize);
			rtnMap.put("headerList", headerList);
			rtnMap.put("bodyList", bodyList);
			rtnMap.put("datasetInfo", datasetInfo);

			rtnMap.put(SangsConstants.FORWARD_VIEW, "mlms/dataset/source_dataset_form");

		} catch (SangsMessageException e) {
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중에러가 발생하였습니다.service");
		}

		return rtnMap;
	}

	/**
	 * 학습데이터셋 목록 조회
	 * 
	 * @param paramMap input parameter
	 * @return Map type result
	 * @throws Exception throws Exception
	 */
	public Map<String, Object> getLearningDatasetList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			logger.debug("searching keywords : " + paramMap);

			int pageNum = SangsStringUtil.nvlInt(paramMap.get("pageNum"), 1);

			int totalCount = dao.selectCount("mlms_dataset.selectLearningDatasetListCnt", paramMap);
			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);

			paramMap.put("pageSize", pagingInfo.getPageSize());
			paramMap.put("offset", pagingInfo.getOffset());

			List<SangsMap> learningDataList = dao.selectList("mlms_dataset.selectLearningDatasetList", paramMap);

			rtnMap.put("learningDataList", learningDataList);
			rtnMap.put("totalCount", totalCount);
			rtnMap.put("pagingInfo", pagingInfo);

		} catch (SangsMessageException e) {
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}

	/**
	 * 학습데이터셋 정보 조회
	 * 
	 * @param paramMap input parameter
	 * @return Map type result
	 * @throws Exception throws Exception
	 */
	public Map<String, Object> viewLearningDatasetForm(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			if ("M".equals(paramMap.get("pmode"))) {
				logger.debug("run lrn_dataset_sn : " + paramMap);

				// lrrDatasetSn 으로 학습데이터셋 상세페이지 조회 : pmode=M
				SangsMap datasetInfo = dao.selectOne("mlms_dataset.selectLearningDatasetInfo", paramMap);
				datasetInfo.put("pmode", paramMap.get("pmode"));

				List<SangsMap> lrnDataHderList = dao.selectList("mlms_dataset.selectLearningDatasetHeaderList", paramMap);

				SangsCsvFileLoader csvLoder = new SangsCsvFileLoader(datasetRootPath + datasetInfo.get("dataFlpth"), true);
				List<String> headerList = csvLoder.getHeaderList();
				List<Map<String, String>> bodyList = csvLoder.getBodyList();

				// 라벨 리스트 가져오기 or 생성하기
				List<SangsMap> labelListMap = dao.selectList("mlms_dataset.selectLearningDatasetLabelList", paramMap);

				if (!(labelListMap.size() > 0)) {

					List<String> targetList = new ArrayList<String>();
					String targetValue = headerList.get((int) datasetInfo.get("hderIndx"));

					for (Map<String, String> bodyListRow : bodyList) {
						targetList.add(bodyListRow.get(targetValue));
					}

					HashSet<String> targetListSet = new HashSet<String>(targetList);
					ArrayList<String> labelValue = new ArrayList<String>(targetListSet);
					for (int i=0; i<labelValue.size(); i++) {
						if (labelValue.get(i)==null) {
							labelValue.set(i, "null");
						}
					}
					
					List<Map<String, String>> labelList = new ArrayList<Map<String, String>>();
					
					Collections.sort(labelValue);
					
					for (String label : labelValue) {
						Map<String, String> labelMap = new HashMap<String, String>();
						labelMap.put("lblValue", label);
						labelMap.put("lblNm", "");
						labelList.add(labelMap);
					}
					
					rtnMap.put("labelList", labelList);

				} else {

					rtnMap.put("labelList", labelListMap);
				}
				
				// 데이터셋 3000개 까지만 표시
				int previewSize = 0;
				if (bodyList.size() > 1000) {
					bodyList = bodyList.subList(0, 1000);
					previewSize = bodyList.size();
				}
				
				rtnMap.put("previewSize", previewSize);
				rtnMap.put("lrnDataHderList", lrnDataHderList);
				rtnMap.put("headerList", headerList);
				rtnMap.put("bodyList", bodyList);
				rtnMap.put("datasetInfo", datasetInfo);
				rtnMap.put(SangsConstants.FORWARD_VIEW, "mlms/dataset/learning_dataset_form");

			} else if ("R".equals(paramMap.get("pmode"))) {
				logger.debug("run dataset_sn : " + paramMap);

				// datasetSn 으로 학습데이터셋 상세페이지 조회 : pmode=R
				SangsMap datasetInfo = dao.selectOne("mlms_dataset.selectSourceDatasetInfo", paramMap);
				datasetInfo.putOrg("lrnDatasetSn", "");
				datasetInfo.putOrg("lrnDatasetNm", "");
				datasetInfo.putOrg("exclRowCnt", "");
				datasetInfo.putOrg("lrnDatasetDc", "");
				datasetInfo.putOrg("hderValue", "");
				datasetInfo.putOrg("datasetTyCd", "");
				datasetInfo.putOrg("delYn", "");
				datasetInfo.putOrg("regDt", "");
				datasetInfo.putOrg("regUserId", "");
				datasetInfo.putOrg("pmode", paramMap.get("pmode"));

				SangsCsvFileLoader csvLoder = new SangsCsvFileLoader(datasetRootPath + datasetInfo.get("dataFlpth"), true);
				List<String> headerList = csvLoder.getHeaderList();
				List<Map<String, String>> bodyList = csvLoder.getBodyList();
				
				// 데이터셋 3000개 까지만 표시
				int previewSize = 0;
				if (bodyList.size() > 1000) {
					bodyList = bodyList.subList(0, 1000);
					previewSize = bodyList.size();
				}
				
				rtnMap.put("previewSize", previewSize);
				rtnMap.put("lrnDataHderList", null);
				rtnMap.put("headerList", headerList);
				rtnMap.put("bodyList", bodyList);
				rtnMap.put("datasetInfo", datasetInfo);
				rtnMap.put(SangsConstants.FORWARD_VIEW, "mlms/dataset/learning_dataset_form");

			}

		} catch (SangsMessageException e) {
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}

	/**
	 * 학습데이터셋 정보 수정
	 * 
	 * @param paramMap input parameter
	 * @return Map type result
	 * @throws Exception throws Exception
	 */
	public Map<String, Object> saveLearningDatasetInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		logger.debug("dataset infomation : " + paramMap);

		try {

			SangsStringUtil.checkRequiredParam(paramMap, "lrnDatasetNm", "학습데이터셋 명");
			SangsStringUtil.checkRequiredParam(paramMap, "datasetTyCd", "데이터타입(구분)");
			SangsStringUtil.checkRequiredParam(paramMap, "exclRowCnt", "제외 로우 수");
			// SangsStringUtil.checkRequiredParam(paramMap, "trgtValueYnArr", "타겟 컬럼");

			paramMap.put("chgUserId", AuthUtil.getUserId());
			paramMap.put("regUserId", AuthUtil.getUserId());

			// 학습데이터셋 update
			if ("M".equals(paramMap.get("pmode"))) {
				dao.update("mlms_dataset.updateLearningDatasetInfo", paramMap);
				// 기존 헤더 정보 delete
				dao.delete("mlms_dataset.deleteLearningDatasetHeaderInfo", paramMap);

				// 학습데이터셋 insert
			} else if ("R".equals(paramMap.get("pmode"))) {
				// lrnDatasetSn 채번
				int lrnDatasetSn = dao.selectCount("mlms_dataset.selectNextLearningDatasetSn", null);
				paramMap.put("lrnDatasetSn", lrnDatasetSn);
				dao.insert("mlms_dataset.insertLearningDatasetInfo", paramMap);

			}

			// 헤더 정보 insert
			List<String> useYnArr = (List<String>) paramMap.get("useYnArr");
			List<String> trgtValueYnArr = (List<String>) paramMap.get("trgtValueYnArr");
			List<String> hderValueArr = (List<String>) paramMap.get("hderValueArr");

			for (int i = 0; i < useYnArr.size(); i++) {
				Map<String, Object> hderParamMap = new HashMap<String, Object>();

				hderParamMap.put("lrnDatasetSn", paramMap.get("lrnDatasetSn"));
				hderParamMap.put("useYn", useYnArr.get(i));
				hderParamMap.put("trgtValueYn", trgtValueYnArr.get(i));
				
				hderParamMap.put("hderValue", hderValueArr.get(i));
				hderParamMap.put("hderIndx", i);
				hderParamMap.put("regUserId", AuthUtil.getUserId());

				dao.insert("mlms_dataset.insertLearningDatasetHeaderInfo", hderParamMap);
			}

			rtnMap.put("resultCd", "OK");

		} catch (SangsMessageException e) {
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}

	/**
	 * 학습데이터셋 분할
	 * 
	 * @param paramMap input parameter
	 * @return Map type result
	 * @throws Exception throws Exception
	 */
	public Map<String, Object> regLearningDatasetSplit(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		logger.debug("split dataset information : " + paramMap);
		try {

			SangsStringUtil.checkRequiredParam(paramMap, "trainNm", "훈련 데이터셋명");
			SangsStringUtil.checkRequiredParam(paramMap, "testNm", "테스트 데이터셋명");

			if (Integer.valueOf((String) paramMap.get("trainCnt")) == 0)
				throw new SangsMessageException("분할 데이터 수 오류입니다.");

			if (Integer.valueOf((String) paramMap.get("testCnt")) == 0)
				throw new SangsMessageException("분할 데이터 수 오류입니다.");

			SangsMap lrnDatasetInfo = dao.selectOne("mlms_dataset.selectLearningDatasetInfo", paramMap);
			
			SangsCsvFileLoader csvLoder = new SangsCsvFileLoader(datasetRootPath + lrnDatasetInfo.get("dataFlpth"), true);
			
			List<String> headerList = csvLoder.getHeaderList();
			List<Map<String, String>> bodyList = csvLoder.getBodyList();

			List<Map<String, String>> testList = new ArrayList<Map<String, String>>();
			List<Map<String, String>> trainList = new ArrayList<Map<String, String>>();

			int bodyListSize = bodyList.size();
			
			String targetValue = (String) headerList.get( lrnDatasetInfo.getInt("hderIndx") );
			
			Map<String, Integer> trainTargetNmMap = new HashMap<String, Integer>();
			Map<String, Integer> testTargetNmMap = new HashMap<String, Integer>();
			Map<String, Integer> targetNmMap = new HashMap<String, Integer>();
			
			// 뒤에서 부터 삭제해야 인덱스 안밀림
			for (int i = bodyList.size() - 1; i >= 0; i--) {
				if (i < (int)lrnDatasetInfo.get("exclRowCnt")-1)
					continue;
				Map<String, String> bodyListRow = bodyList.get(i);
				String target = bodyListRow.get(targetValue); // target= 실제 타겟 라벨값 ex)5_1
				
				if (targetNmMap.containsKey(target)) {
					Integer cnt = targetNmMap.get(target);
					targetNmMap.put(target, ++cnt);
				} else {
					targetNmMap.put(target, 1);
				}
				
				if (testTargetNmMap.containsKey(target)) {
					Integer cnt = testTargetNmMap.get(target);
					testTargetNmMap.put(target, ++cnt);
				} else {
					testTargetNmMap.put(target, 1);
					testList.add(bodyListRow); // testList에 라벨별 최소 한개씩 추가한다.
					bodyList.remove(i);
				}
				
			}
			for (int i = bodyList.size() - 1; i >= 0; i--) {
				if (i < (int)lrnDatasetInfo.get("exclRowCnt")-1)
					continue;
				Map<String, String> bodyListRow = bodyList.get(i);
				String target = bodyListRow.get(targetValue);
				
				if (trainTargetNmMap.containsKey(target)) {
					Integer cnt = trainTargetNmMap.get(target);
					trainTargetNmMap.put(target, ++cnt);
				} else {
					trainTargetNmMap.put(target, 1);
					trainList.add(bodyListRow); // testList에 라벨별 최소 한개씩 추가한다.
					bodyList.remove(i);
				}

			}
			
			
			targetNmMap.forEach((key, value) -> {
				Integer targetCnt = targetNmMap.get(key);
				if (targetCnt < 2)
					throw new SangsMessageException("타겟별 데이터가 2개 이상 시 분할이 가능합니다.["+key+"]");
			});
			
			
			if (trainTargetNmMap.size() != testTargetNmMap.size())
				throw new SangsMessageException("분할할수 없는 데이터셋입니다.");
			
			// 훈련데이터셋의 target 명으로 테스트데이터셋에 target key가 있는지 확인
			trainTargetNmMap.forEach((key, value) -> {
				if (!testTargetNmMap.containsKey(key))
					throw new SangsMessageException("분할할수 없는 데이터셋입니다.");
			});

			// bodyList 에서 뽑아야할 개수 = n
			int n = Integer.valueOf((String) paramMap.get("testCnt")) - testTargetNmMap.size();
			if (n < 0)
				throw new SangsMessageException("각 데이터셋에 타겟 종류별 한개 이상의 데이터가 필요합니다.");
			
			int trainN = Integer.valueOf((String) paramMap.get("trainCnt")) - trainTargetNmMap.size();
			if (trainN < 0)
				throw new SangsMessageException("각 데이터셋에 타겟 종류별 한개 이상의 데이터가 필요합니다.");
			
			// 랜덤 인덱스
			int indexSet[] = new int[n];
			List<Integer> testIndex = new ArrayList<Integer>();
			Random random = new Random();
			for (int i = 0; i < n; i++) {
				indexSet[i] = random.nextInt(bodyListSize - testTargetNmMap.size() - trainTargetNmMap.size());
				for (int j = 0; j < i; j++) {
					if (indexSet[i] == indexSet[j]) {
						i--;
					}
				}
			}
			// 테스트파일로 추출할 인덱스리스트 : testIndex
			for (int index : indexSet) {
				testIndex.add(index);
			}
			Collections.sort(testIndex);
			for (int q = testIndex.size() - 1; q >= 0; q--) {
				int index = testIndex.get(q);
				testList.add(bodyList.get(index));
				bodyList.remove(index);
			}
			
			// 훈련파일로 추출
			for (Map<String, String> bodyListRow : bodyList) {
				trainList.add(bodyListRow);
			}
			
			// 저장
			String subDir = (String)paramMap.get("dataFlpth");
			String subDir_spl = subDir.substring(0, subDir.lastIndexOf("/") + 1);
			String rootBasePath = datasetRootPath + subDir_spl;
			String trainOrginlFlpth = paramMap.get("trainNm") + ".csv";
			String testOrginlFlpth = paramMap.get("testNm") + ".csv";
			String trainFlpth = SangsFileUtil.convertUniqueFileNm(trainOrginlFlpth);
			String testFlpth = SangsFileUtil.convertUniqueFileNm(testOrginlFlpth);
			
			File trainFile = csvLoder.saveFile(rootBasePath + trainFlpth, headerList, trainList); // UUID
			//csvLoder.saveFile(rootBasePath + trainOrginlFlpth, headerList, trainList); // Original File
			
			File testFile = csvLoder.saveFile(rootBasePath + testFlpth, headerList, testList); // UUID
			//csvLoder.saveFile(rootBasePath + testOrginlFlpth, headerList, testList); // Original File
			
			// 원천데이터셋 등록
			int trainDatasetSn = dao.selectCount("mlms_dataset.selectNextDatasetSn", null);
			int trainLrnDatasetSn = dao.selectCount("mlms_dataset.selectNextLearningDatasetSn", null);
			// 훈련용 원천데이터셋 등록
			Map<String, Object> insertMap = new HashMap<String, Object>();
			insertMap.put("delYn", "N");
			
			int trainFileLength = (int) (trainFile.length() / 1024);
			if (trainFile.length() / 1024 < 1)
				trainFileLength = 1;
			
			int testFileLength = (int) (testFile.length() / 1024);
			if (testFile.length() / 1024 < 1)
				testFileLength = 1;
			
			insertMap.put("datasetSn", trainDatasetSn);
			insertMap.put("datasetNm", paramMap.get("trainNm"));
			insertMap.put("dataRowCnt", paramMap.get("trainCnt"));
			insertMap.put("dataFlpth", subDir_spl + trainFlpth);
			insertMap.put("dataOrginlFlpth", trainOrginlFlpth);
			insertMap.put("dataColCnt", lrnDatasetInfo.get("dataColCnt"));
			insertMap.put("dataKbSize", trainFileLength);
			insertMap.put("regUserId", AuthUtil.getUserId());
			insertMap.put("apiDataYn", "N"); 		// API 로 등록된 데이터 셋이 아닌경우
			dao.insert("mlms_dataset.insertSourceDatasetInfo", insertMap);
			// 훈련 학습데이터셋 등록
			insertMap.put("lrnDatasetSn", trainLrnDatasetSn);
			insertMap.put("datasetTyCd", "TRAINING");
			insertMap.put("lrnDatasetNm", paramMap.get("trainNm"));
			insertMap.put("exclRowCnt", lrnDatasetInfo.get("exclRowCnt"));
			dao.insert("mlms_dataset.insertLearningDatasetInfo", insertMap);
			
			
			
			// 학습데이터셋 등록
			int testDatasetSn = dao.selectCount("mlms_dataset.selectNextDatasetSn", null);
			int testLrnDatasetSn = dao.selectCount("mlms_dataset.selectNextLearningDatasetSn", null);
			// 테스트용 원천데이터셋 등록
			insertMap.put("datasetSn", testDatasetSn);
			insertMap.put("datasetNm", paramMap.get("testNm"));
			insertMap.put("dataRowCnt", paramMap.get("testCnt"));
			insertMap.put("dataFlpth", subDir_spl + testFlpth);
			insertMap.put("dataOrginlFlpth", testOrginlFlpth);
			insertMap.put("dataKbSize", testFileLength);
			dao.insert("mlms_dataset.insertSourceDatasetInfo", insertMap);
			// 테스트 학습데이터셋 등록
			insertMap.put("lrnDatasetSn", testLrnDatasetSn);
			insertMap.put("datasetTyCd", "TEST");
			insertMap.put("lrnDatasetNm", paramMap.get("testNm"));
			dao.insert("mlms_dataset.insertLearningDatasetInfo", insertMap);

			
			// 헤더 정보 등록
			List<SangsMap> lrnDatasetHderList = dao.selectList("mlms_dataset.selectLearningDatasetHeaderList", paramMap);

			for (SangsMap hderMap: lrnDatasetHderList) {
				hderMap.putOrg("regUserId", AuthUtil.getUserId());
				// 훈련 학습데이터 헤더
				hderMap.putOrg("lrnDatasetSn", trainLrnDatasetSn);
				dao.insert("mlms_dataset.insertLearningDatasetHeaderInfo", hderMap);

				// 테스트 학습데이터 헤더
				hderMap.putOrg("lrnDatasetSn", testLrnDatasetSn);
				dao.insert("mlms_dataset.insertLearningDatasetHeaderInfo", hderMap);
			}
			
			// 라벨 정보 등록
			List<SangsMap> lrnDatasetLblList = dao.selectList("mlms_dataset.selectLearningDatasetLabelList", paramMap);
			
			for (SangsMap lblMap: lrnDatasetLblList) {

				lblMap.putOrg("regUserId", AuthUtil.getUserId());
				lblMap.putOrg("lrnDatasetSn", trainLrnDatasetSn);
				dao.insert("mlms_dataset.insertLearningDatasetLabelInfo", lblMap);
				
				lblMap.putOrg("lrnDatasetSn", testLrnDatasetSn);
				dao.insert("mlms_dataset.insertLearningDatasetLabelInfo", lblMap);
			}
			
			rtnMap.put("resultCd", "OK");
			
		} catch (SangsMessageException e) {
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중에러가 발생하였습니다.");
		}

		return rtnMap;
	}

	/**
	 * 학습데이터셋 라벨 목록 조회
	 * 
	 * @param paramMap input parameter
	 * @return Map type result
	 * @throws Exception throws Exception
	 */
	/*
	 * public Map<String, Object> getLearningDatasetLabelList(Map<String, Object>
	 * paramMap) throws Exception { Map<String, Object> rtnMap = new HashMap<String,
	 * Object>();
	 * 
	 * logger.debug("label dataset search key : " + paramMap); try { List<SangsMap>
	 * labelList = dao.selectList("mlms_dataset.selectLearningDatasetLabelList",
	 * paramMap);
	 * 
	 * rtnMap.put("labelList", labelList);
	 * 
	 * } catch(SangsMessageException e) { throw e; } catch(Exception e) {
	 * logger.error("", e); throw new
	 * SangsMessageException("처리중 에러가 발생하였습니다.service"); } return rtnMap; }
	 */

	/**
	 * 학습데이터셋 라벨 저장처리
	 * 
	 * @param paramMap input parameter
	 * @return Map type result
	 * @throws Exception throws Exception
	 */
	public Map<String, Object> saveLearningDatasetLabelInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		logger.debug("label dataset update : " + paramMap);

		try {
			dao.delete("mlms_dataset.deleteLearningDatasetLabelInfo", paramMap);

			// paramMap.put("regUserId", "admin"); // 수정

			List<Map<String, Object>> dataList = (List<Map<String, Object>>) paramMap.get("dataList");

			for (int i = 0; i < dataList.size(); i++) {

				Map<String, Object> dataListRow = dataList.get(i);

				dataListRow.put("lrnDatasetSn", paramMap.get("lrnDatasetSn"));
				dataListRow.put("regUserId", AuthUtil.getUserId());
				dao.insert("mlms_dataset.insertLearningDatasetLabelInfo", dataListRow);
			}

			rtnMap.put("resultCd", "OK");

		} catch (SangsMessageException e) {
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중에러가 발생하였습니다.");
		}

		return rtnMap;
	}

	/**
	 * 학습데이터셋 분포 보기
	 * 
	 * @param paramMap input parameter
	 * @return Map type result
	 * @throws Exception throws Exception
	 */
	public Map<String, Object> getLrnDatasetFreqInfo(Map<String, Object> paramMap) throws Exception {

		SangsMap lrnPreviewInfo = dao.selectOne("mlms_dataset.selectLearningDatasetInfo", paramMap);

		Map<String, Object> rtnMap = getDatasetFreqInfo(
				datasetRootPath + (String) lrnPreviewInfo.get("dataFlpth"),
				(int) lrnPreviewInfo.get("exclRowCnt"), (int) lrnPreviewInfo.get("hderIndx"),
				(int) lrnPreviewInfo.get("lrnDatasetSn"));

		return rtnMap;
	}

	/**
	 * 데이터셋의 빈도수 정보 조회
	 * 
	 * @param filePath 파일 경로
	 * @return 반환 Map 안에 Label 별
	 * @throws SangsMessageException
	 */
	// Call by getResultAnalysisInfo
	public Map<String, Object> getDatasetFreqInfo(String filePath, int headCount, int targetIndex, int lrnDatasetSn)
			throws SangsMessageException {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		Map<String, Object> byLabelMap = new HashMap<String, Object>(); // Label 별 데이터 빈도수
		Map<String, Object> byFeatureUniqueMap = new HashMap<String, Object>(); // Feature별 Unique 빈도수

		try {

			// csv 파일 조회
			SangsCsvFileLoader csvLoder = new SangsCsvFileLoader(filePath, true);

			Map<String, Object> targetParam = new HashMap<String, Object>();

			List<String> headerList = csvLoder.getHeaderList();
			List<Map<String, String>> bodyList = csvLoder.getBodyList();
			Map<String, Integer> countByTargetMap = new HashMap<String, Integer>();
			Map<String, HashSet<String>> uniqueCountByCol = new HashMap<String, HashSet<String>>();

			String mapKeyPrefix = "_key_";

			for (int i = 0; i < headerList.size(); i++) {
				String headerNm = headerList.get(i);
				// 컬럼별로 Map을 만들어서 unique count 를 위한 map에 넣어둠
				uniqueCountByCol.put("_col_" + i, new HashSet<String>());
				uniqueCountByCol.put(headerNm, new HashSet<String>());
			}

			for (int i = 0; i < bodyList.size(); i++) {
				Map<String, String> bodyMap = bodyList.get(i);

				if (i < headCount-1)
					continue;

				String targetColNm = headerList.get(targetIndex);
				String targetVal = bodyMap.get(targetColNm);
				String findKey = mapKeyPrefix + targetVal;

				if (countByTargetMap.containsKey(findKey)) {
					Integer cnt = countByTargetMap.get(findKey);
					cnt++;
					countByTargetMap.put(findKey, cnt);
				} else {
					countByTargetMap.put(findKey, 1);
				}

				// unique count
				for (int j = 0; j < headerList.size(); j++) {
					String tgtHeaderNm = headerList.get(j);
					// if(bodyMap.get("col_"+j) != null) {
					if (bodyMap.get(tgtHeaderNm) != null) {
						String bodyValue = bodyMap.get(tgtHeaderNm);
						HashSet<String> tempSet = uniqueCountByCol.get("_col_" + j);
						if (tempSet != null)
							tempSet.add(bodyValue);

					}
				}
			}

			List<String> keySort = new ArrayList<String>();
			countByTargetMap.forEach((key, value) -> {
				keySort.add(key);
			});

			logger.debug("@target 별 count");

			targetParam.put("lrnDatasetSn", lrnDatasetSn);
			Map<String, String> targetNmMap = getTargetNmMap(targetParam);
			Collections.sort(keySort);
			for (String str : keySort) {
				int cnt = countByTargetMap.get(str);
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

			logger.debug("@컬럼별 unique 수");
			
			List<SangsMap> hderInfo = dao.selectList("mlms_dataset.selectLearningDatasetHeaderList", targetParam);
			for (int i = 0; i < headerList.size(); i++) {
				HashSet<String> hs = uniqueCountByCol.get("_col_" + i);

				SangsMap hderInfoRow = hderInfo.get(i);
				int ucnt = hs.size();
				logger.debug(hderInfoRow.get("hderValue") + "\t" + ucnt);

				byFeatureUniqueMap.put((String)hderInfoRow.get("hderValue"), ucnt);
			}

			
			// boxplot data create
			List<List<String>> bodyContents = new ArrayList<List<String>>();
			for (int i = 0; i < headerList.size(); i++) {
				bodyContents.add(new ArrayList<String>());
			}

			for (int i = 0; i < bodyList.size(); i++) {
				Map<String, String> bodyMap = bodyList.get(i);
				if (i < headCount-1)
					continue;
				for (int j = 0; j < headerList.size(); j++) {
					bodyContents.get(j).add(bodyMap.get(headerList.get(j)));
				}
			}
			
			// boxplot 문자열 포함 컬럼 삭제
			int bodyContentsSize = bodyContents.size();
			for (int i=bodyContentsSize-1; i >= 0; i--) {
				List<String> bodyContentsOne = bodyContents.get(i);
				for (int j=bodyContentsOne.size()-1; j >= 0; j--) {
					if ("".equals(bodyContentsOne.get(j)) || "null".equals(bodyContentsOne.get(j))) {
						bodyContentsOne.remove(j);
					} else {
						if (!Pattern.matches("^?\\d*(\\.?\\d*)$", bodyContentsOne.get(j))) {
							bodyContents.remove(i);
							break;
						}
					}
				}
			}
			
			// boxplot 정규화?
			List<List<Float>> boxplotData = new ArrayList<List<Float>>();
			for (int i=0; i<bodyContents.size(); i++) {
				List<String> bodyContentsOne = bodyContents.get(i);
				List<Float> boxplotDataOne = new ArrayList<Float>();
				
				Float max = 0.0f;
				for (int n=0; n<bodyContentsOne.size(); n++) {
					Float bodyContentsVal = Float.parseFloat(bodyContentsOne.get(n));
					if (max < bodyContentsVal) {
						max = bodyContentsVal;
					}
				}
				
				for (int j=0; j<bodyContentsOne.size(); j++) {
					boxplotDataOne.add(j, Float.parseFloat(bodyContentsOne.get(j)) / max);
				}
				boxplotData.add(boxplotDataOne);
			}
			
			rtnMap.put("BY_LABEL", byLabelMap);
			rtnMap.put("BY_FEATURE_UNIQUE", byFeatureUniqueMap);
			rtnMap.put("BY_FEATURE_CONTENTS", boxplotData);

		} catch (SangsMessageException e) {
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중에러가 발생하였습니다.");
		}

		return rtnMap;

	}


	
	/**
	 * 데이터셋 공통 팝업
	 * 
	 * @param paramMap input parameter
	 * @return Map type result
	 * @throws Exception throws Exception
	 */
	public Map<String, Object> viewDatasetInfoPop(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		if ("S".equals(paramMap.get("dst"))) {
			paramMap.put("datasetSn", paramMap.get("itemSn"));
			SangsMap datasetInfo = dao.selectOne("mlms_dataset.selectSourceDatasetInfo", paramMap);
			SangsCsvFileLoader csvLoder = new SangsCsvFileLoader(datasetRootPath + datasetInfo.get("dataFlpth"), true);
			List<String> headerList = csvLoder.getHeaderList();
			List<Map<String, String>> bodyList = csvLoder.getBodyList();
			
			// 데이터셋 3000개 까지만 표시
			int previewSize = 0;
			if (bodyList.size() > 1000) {
				bodyList = bodyList.subList(0, 1000);
				previewSize = bodyList.size();
			}
			
			rtnMap.put("previewSize", previewSize);
			rtnMap.put("datasetInfo", datasetInfo);
			rtnMap.put("headerList", headerList);
			rtnMap.put("bodyList", bodyList);

		} else if ("L".equals(paramMap.get("dst"))) {
			paramMap.put("lrnDatasetSn", paramMap.get("itemSn"));

			SangsMap datasetInfo = dao.selectOne("mlms_dataset.selectLearningDatasetInfo", paramMap);
			SangsCsvFileLoader csvLoder = new SangsCsvFileLoader(datasetRootPath + datasetInfo.get("dataFlpth"), true);
			List<String> headerList = csvLoder.getHeaderList();
			List<Map<String, String>> bodyList = csvLoder.getBodyList();
			
			// 데이터셋 3000개 까지만 표시
			int previewSize = 0;
			if (bodyList.size() > 1000) {
				bodyList = bodyList.subList(0, 1000);
				previewSize = bodyList.size();
			}
			
			rtnMap.put("previewSize", previewSize);
			rtnMap.put("datasetInfo", datasetInfo);
			rtnMap.put("headerList", headerList);
			rtnMap.put("bodyList", bodyList);

		}

		rtnMap.put("paramMap", paramMap);
		rtnMap.put(SangsConstants.FORWARD_VIEW, "mlms/dataset/dataset_info_pop");

		return rtnMap;
	}

	/**
	 * 테스트 데이터셋 목록 조회
	 * 
	 * @param paramMap input parameter
	 * @return Map type result
	 * @throws Exception throws Exception
	 */
	public Map<String, Object> getTestDatasetList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {

			int pageNum = SangsStringUtil.nvlInt(paramMap.get("pageNum"), 1);

			int totalCount = dao.selectCount("mlms_dataset.selectTestDatasetListCnt", paramMap);
			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum,
					SangsConstants.DEFAULT_LIST_ROW_SIZE);

			paramMap.put("pageSize", pagingInfo.getPageSize());
			paramMap.put("offset", pagingInfo.getOffset());

			List<SangsMap> sourceDataList = dao.selectList("mlms_dataset.selectTestDatasetList", paramMap);

			rtnMap.put("list", sourceDataList);
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
	 * 모델명으로 패핑되는 실행순번, 헤더정보, 라벨정보 조회
	 * (업로드 모델 헤더, 라벨 자동입력)
	 * @param paramMap input parameter
	 * @return Map type result
	 * @throws Exception throws Exception
	 */
	public Map<String, Object> getModelHderLblInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		logger.debug("lrnDatasetSn :", paramMap);
		try {
			
			List<SangsMap> headerList = new ArrayList<SangsMap>();
			List<SangsMap> labelList = new ArrayList<SangsMap>();
			
			if (!("".equals(paramMap.get("lrnDatasetSn"))) && paramMap.get("lrnDatasetSn") != null) {
				headerList = dao.selectList("mlms_dataset.selectLearningDatasetHeaderList", paramMap);
				labelList = dao.selectList("mlms_dataset.selectLearningDatasetLabelList", paramMap);
				if (headerList.size() == 0) {
					SangsMap map = new SangsMap();
					map.putOrg("hderIndx", "");
					map.putOrg("hderValue", "");
					map.putOrg("trgtValueYn", "");
					headerList.add(map);
				}
				
				if (labelList.size() == 0) {
					SangsMap map2 = new SangsMap();
					map2.putOrg("lblIndx", "");
					map2.putOrg("lblNm", "");
					map2.putOrg("lblValue", "");
					labelList.add(map2);
				}
				
			}
			rtnMap.put("headerList", headerList);
			rtnMap.put("labelList", labelList);
			
			
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
	 * 데이터셋 타겟 라벨 매핑
	 * 
	 * @param paramMap input parameter
	 * @return Map type result
	 * @throws Exception throws Exception
	 */
	public Map<String, String> getTargetNmMap(Map<String, Object> paramMap) throws Exception {
		List<SangsMap> labelList = dao.selectList("mlms_dataset.selectLearningDatasetLabelList", paramMap);

		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < labelList.size(); i++) {
			Map<String, Object> labelMap = labelList.get(i);
			map.put((String) labelMap.get("lblValue"), (String) labelMap.get("lblNm"));
		}

		return map;
	}

	
}
