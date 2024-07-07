package com.sangs.dq.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.sangs.common.support.AuthUtil;
import com.sangs.common.support.CommonDao;
import com.sangs.dq.util.AiProfileMsgSocket;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.fwk.common.SangsConstants;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.domain.SangsPagingViewInfo;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsCsvFileLoader;
import com.sangs.lib.support.utils.SangsStringUtil;

@SangsService
public class AiProfileMngService {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	// 속성 이진 분류 실행 순번
	public static final String CLSF_BINARY_MODEL_TEST_EXECUT_SN = "3";
	// 버트 문맥 오류 검출 모델 실행 순번
	public static final String BERT_CONT_MODEL_TEST_EXECUT_SN = "4";
	// 버트 부정 항의글 검출 모델 실행 순번
	public static final String BERT_CLF_MODEL_TEST_EXECUT_SN = "5";

	@Autowired
	private CommonDao dao;

	@Autowired
	AiProfileExcService aiProfileExcService;

	@Autowired
	AiProfileNlpService aiProfileNlpService;

	@Value("${mlms.resource.result_path}")
	private String mlmsApiResultPath;
	
	@Value("${file.aiDiagnosis.dataDir}")
	private String aiDataDir;
	
	@Autowired
	private AiProfileMsgSocket socket;

	

	static String[] targets = { "1_4", "1_8", "2_0", "2_3", "2_6", "2_8", "2_9", "2_10", "2_11", "2_12", "3_0", "4_1", "4_2", "5_0", "5_1", "5_2", "5_3", "6_0", "6_4", "UNKNOWN", "NA", "BLANK" };
	static ArrayList<String> targetList = new ArrayList<String>(Arrays.asList(targets));

	/**
	 * 자동점검 실행
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public void runAutoAnalysis(Session socketSession, Map<String, Object> params) throws Exception {
		logger.debug("params : " + params);
		
		try {
			// 자동점검 순번 채번
			int atmcDgnssExcnSn = dao.selectInteger("dq_ai_profile.selectNextAtmcDgnssExcnSn", null);
			params.put("atmcDgnssExcnSn", atmcDgnssExcnSn);
			
			Map<String, Object> imap = new HashMap<String, Object>();
			imap.put("prjctSn", AuthUtil.getPrjctSn());
			imap.put("dbmsIpAddr", AuthUtil.getDbmsIpAddr());
			imap.put("dbmsPortNo", AuthUtil.getDbmsPortNo());
			imap.put("dbmsSidNm", AuthUtil.getDbmsSidNm());
			imap.put("dbmsDatabaseNm", AuthUtil.getDbmsDatabaseNm());
			imap.put("dbmsId", AuthUtil.getDbmsId());
			imap.put("regUserId", AuthUtil.getUserId());
			imap.put("atmcDgnssExcnSn", atmcDgnssExcnSn);
			imap.put("nlpAnlsYn", "N");
			imap.put("excSttusCd", "S"); // 시작 : S
			dao.insert("dq_ai_profile.insertDgnssInfo", imap);

			// 분류
			Map<String, Object> rtnMap = aiProfileExcService.doClassification(socketSession, params);
			
			// 자연어 분석
			if ("Y".equals(params.get("nlpAnlsYn"))) {
				aiProfileNlpService.doNlp(socketSession, rtnMap, params);				
			}
			
			// 분류 점검결과 저장, 레포트 작성
			this.saveResult(socketSession, rtnMap, params);

			params.put("excSttusCd", "E"); // 완료 : E

		} catch (Exception e) {
			params.put("excSttusCd", "F"); // 실패 : F
			e.printStackTrace();
			socket.sendToClient(socketSession, "ERROR", e.getMessage());
			throw new SangsMessageException("점검 오류.\n" + e);
			
		} finally {
			dao.update("dq_ai_profile.updateExcSttusCd", params);
		}
	}

	// 자동점검 이력 조회
	public Map<String, Object> getAutoDgnssHistList(Map<String, String> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			int pageNum = SangsStringUtil.nvlInt(params.get("pageNum"), 1);

			Map<String, Object> smap = new HashMap<String, Object>();
			smap.put("prjctSn", AuthUtil.getPrjctSn());
			
			int totCount = dao.selectInteger("dq_ai_profile.selectDgnssHistCnt", smap);
			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totCount, pageNum,
					SangsConstants.DEFAULT_LIST_ROW_SIZE);

			smap.put("pageSize", pagingInfo.getPageSize());
			smap.put("offset", pagingInfo.getOffset());
			smap.put("searchKey", params.get("searchKey"));
			smap.put("searchKeyVal", params.get("searchKeyVal"));

			List<SangsMap> autoDgnssInfoList = dao.selectList("dq_ai_profile.selectAutoDgnssInfo", smap);

			rtnMap.put("autoDgnssInfoList", autoDgnssInfoList);
			rtnMap.put("pagingInfo", pagingInfo);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("이력 조회 오류.");
		}

		return rtnMap;
	}

	// 자동점검 결과페이지 정보 조회
	public Map<String, Object> getAutoDgnssResult(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			logger.debug("params : " + params);
			
			SangsMap anlsBasicInfo = dao.selectOne("dq_ai_profile.selectAnlsBasicInfo", params);
			List<SangsMap> colObsryRate = dao.selectList("dq_ai_profile.selectColObsryRateTrends", params);

			Map<String, Object> smap = new HashMap<String, Object>();
			smap.put("prjctSn", AuthUtil.getPrjctSn());
			smap.put("dbmsIpAddr", AuthUtil.getDbmsIpAddr());
			smap.put("dbmsPortNo", AuthUtil.getDbmsPortNo());
			smap.put("dbmsSidNm", AuthUtil.getDbmsSidNm());
			smap.put("dbmsDatabaseNm", AuthUtil.getDbmsDatabaseNm());
			smap.put("dbmsId", AuthUtil.getDbmsId());
			smap.put("regUserId", AuthUtil.getUserId());
			List<SangsMap> databaseObsryRateTrends = dao.selectList("dq_ai_profile.selectDatabaseObsryRateTrends", smap);

			String reportWritePath = aiDataDir + String.valueOf(params.get("atmcDgnssExcnSn"));

			String clPath = reportWritePath + "/classification.csv";
			
			List<String> headerList;
			List<Map<String, String>> bodyList;
			SangsCsvFileLoader csvLoader;

			csvLoader = new SangsCsvFileLoader(clPath, true);
			headerList = csvLoader.getHeaderList();
			bodyList = csvLoader.getBodyList();
			//System.out.println("!" + headerList);
			
			List<Map<String, Object>> anlsClColCntInfo = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> anlsClErrCntInfo = new ArrayList<Map<String, Object>>();
			for (int i = 3; i < headerList.size(); i++) {
				Map<String, Object> clColCntMap = new HashMap<String, Object>();
				Map<String, Object> clErrCntMap = new HashMap<String, Object>();
				int cnt = 0;
				int err = 0;
				for (Map<String, String> bodyRow : bodyList) {
					if (bodyRow.get("TARGET").equals(headerList.get(i))) {
						cnt += 1;
					} else {
						err += Integer.parseInt(bodyRow.get(headerList.get(i)));
					}
				}
				clColCntMap.put(headerList.get(i), cnt); // 분류별 데이터 전체갯수
				clErrCntMap.put(headerList.get(i), err);
				anlsClColCntInfo.add(clColCntMap);
				anlsClErrCntInfo.add(clErrCntMap);
			}

			Map<String, String> targetMap = targetMap();

			for (Map<String, Object> clColCntMap : anlsClColCntInfo) {
				for (String key : clColCntMap.keySet()) {
					clColCntMap.put(targetMap.get(key), clColCntMap.get(key));
					clColCntMap.remove(key);
				}
			}

			for (Map<String, Object> clErrCntMap : anlsClErrCntInfo) {
				for (String key : clErrCntMap.keySet()) {
					clErrCntMap.put(targetMap.get(key), clErrCntMap.get(key));
					clErrCntMap.remove(key);
				}
			}

			rtnMap.put("colObsryRate", colObsryRate);
			rtnMap.put("databaseObsryRateTrends", databaseObsryRateTrends);

			rtnMap.put("anlsBasicInfo", anlsBasicInfo);
			rtnMap.put("anlsClColCntInfo", anlsClColCntInfo);
			rtnMap.put("anlsClErrCntInfo", anlsClErrCntInfo);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("점검 결과 조회 오류.");
		}

		return rtnMap;
	}

	// 결과 페이지 - 분류 분석 테이블 조회
	public Map<String, Object> getDgnssClList(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {

			int pageNum = SangsStringUtil.nvlInt(params.get("pageNum"), 1);
			int totalCount = dao.selectInteger("dq_ai_profile.selectClAnlsListCnt", params);
			SangsPagingViewInfo clAnlsPagingInfo = new SangsPagingViewInfo(totalCount, pageNum, 10);

			Map<String, Object> smap = new HashMap<String, Object>();
			smap.put("prjctSn", AuthUtil.getPrjctSn());
			smap.put("atmcDgnssExcnSn", params.get("atmcDgnssExcnSn"));
			smap.put("dgnssTblNm", params.get("dgnssTblNm"));
			smap.put("dgnssColNm", params.get("dgnssColNm"));
			smap.put("pageSize", clAnlsPagingInfo.getPageSize());
			smap.put("offset", clAnlsPagingInfo.getOffset());
			List<SangsMap> clAnlsInfo = dao.selectList("dq_ai_profile.selectClAnlsList", smap);

			Map<String, String> targetMap = targetMap();
			for (int i = 0; i < clAnlsInfo.size(); i++) {
				SangsMap clAnlsMap = clAnlsInfo.get(i);
				String target = String.valueOf(clAnlsMap.get("colClPredictValue"));
				if (target == null) {
					continue;
				}
				clAnlsMap.putOrg("colClPredictValue", targetMap.get(target));
			}

			rtnMap.put("anlsColInfo", clAnlsInfo);
			rtnMap.put("anlsColPagingInfo", clAnlsPagingInfo);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("분류 분석 테이블 조회 오류.");
		}

		return rtnMap;
	}

	// 결과 페이지 - 자연어 분석 테이블 조회
	public Map<String, Object> getDgnssNlpList(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			String reportWritePath = aiDataDir + String.valueOf(params.get("atmcDgnssExcnSn"));
			String nlpPath = reportWritePath + "/nlp.csv";
			
			int pageNum = SangsStringUtil.nvlInt(params.get("pageNum"), 1);
			
			SangsCsvFileLoader csvLoader = new SangsCsvFileLoader(nlpPath, true);
			List<Map<String, String>> bodyList = csvLoader.getBodyList();
			
			int startNum = pageNum * 10 - 9;
			int endNum = pageNum * 10;
			
			// 자연어 긍/부정 분류에서 긍정문은 포함X
			List<Map<String, String>> nlpList = new ArrayList<Map<String, String>>();
			if ((!"".equals(params.get("dgnssTblNm")) && params.get("dgnssTblNm") != null)
					|| (!"".equals(params.get("dgnssColNm")) && params.get("dgnssColNm") != null)) {
				String searchTblNm = String.valueOf(params.get("dgnssTblNm"));
				String searchColNm = String.valueOf(params.get("dgnssColNm"));
				for (Map<String, String> bodyListRow:bodyList) {
					if (!"1".equals(bodyListRow.get("PREDICT_VALUE")) && !"N".equals(bodyListRow.get("PREDICT_VALUE"))) {
						if (bodyListRow.get("TABLE_NAME").contains(searchTblNm) && bodyListRow.get("COLUMN_NAME").contains(searchColNm)) {
							nlpList.add(bodyListRow);
						}
					}
				}
			} else {
				for (Map<String, String> bodyListRow:bodyList) {
					if (!"1".equals(bodyListRow.get("PREDICT_VALUE")) && !"N".equals(bodyListRow.get("PREDICT_VALUE"))) {
						nlpList.add(bodyListRow);
					}
				}
			}
			
			if (endNum > nlpList.size()) {
				endNum = nlpList.size();
			}
			
			List<SangsMap> nowPageList = new ArrayList<SangsMap>();
			for (int i=startNum-1; i<endNum; i++) {
				Map<String, String> nlpListRow = nlpList.get(i);
				SangsMap nowPageRow = new SangsMap();
				nowPageRow.putOrg("dbmsTableNm", nlpListRow.get("TABLE_NAME"));
				nowPageRow.putOrg("columnName", nlpListRow.get("COLUMN_NAME"));
				if (!"0".equals(nlpListRow.get("PREDICT_VALUE"))) {
					nowPageRow.putOrg("predict", nlpListRow.get("PREDICT_VALUE"));
				} else {
					nowPageRow.putOrg("predict", "부정");
				}
				nowPageRow.putOrg("inputSentence", nlpListRow.get("INPUT_SENTENCE"));
				nowPageRow.putOrg("checkedSentence", nlpListRow.get("CHECKED_SENTENCE"));
				nowPageRow.putOrg("errorWord", nlpListRow.get("ERROR_WORD"));
				nowPageRow.putOrg("recommendWord", nlpListRow.get("RECOMMEND_WORD"));
				nowPageList.add(nowPageRow);
			}
			
			SangsPagingViewInfo nlpPagingInfo = new SangsPagingViewInfo(nlpList.size(), pageNum, 10);
			rtnMap.put("nowPageList", nowPageList);
			rtnMap.put("nlpPagingInfo", nlpPagingInfo);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("자연어 분석 테이블 조회 오류.");
		}

		return rtnMap;
	}

	// 분류분석 상세빈도, 차트 조회
	public Map<String, Object> getClDetail(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			String reportWritePath = aiDataDir + String.valueOf(params.get("atmcDgnssExcnSn"));
			String clPath = reportWritePath + "/classification.csv";

			List<Map<String, String>> bodyList;
			SangsCsvFileLoader csvLoader;

			csvLoader = new SangsCsvFileLoader(clPath, true);
			bodyList = csvLoader.getBodyList();

			List<Map<String, Object>> clCntDetail = new ArrayList<Map<String, Object>>();
			for (Map<String, String> bodyMap : bodyList) {

				if (bodyMap.get("TABLE_NAME").equals(params.get("dgnssTblNm"))
						&& bodyMap.get("COLUMN_NAME").equals(params.get("dgnssColNm"))) {

					Map<String, String> targetMap = targetMap();

					for (String key : bodyMap.keySet()) {
						Map<String, Object> clCntMap = new HashMap<String, Object>();
						if (targetMap.keySet().contains(key)) {
							clCntMap.put("clName", targetMap.get(key));
							clCntMap.put("clCnt", bodyMap.get(key));
							clCntDetail.add(clCntMap);
						}
					}
				}
			}

			rtnMap.put("clCntDetail", clCntDetail);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("분류 빈도 테이블, 차트 정보 조회 오류.");
		}

		return rtnMap;
	}

	// 분류분석 상세내역 조회
	public Map<String, Object> getClDetailContent(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		Map<String, String> targetMap = targetMap();

		try {
			String reportWritePath = aiDataDir + String.valueOf(params.get("atmcDgnssExcnSn"));
			String errPath = reportWritePath + "/error_classification.csv";

			List<Map<String, String>> bodyList;
			SangsCsvFileLoader csvLoader;

			csvLoader = new SangsCsvFileLoader(errPath, true);
			bodyList = csvLoader.getBodyList();

			String tblNm = params.get("dgnssTblNm").toString();
			String colNm = params.get("dgnssColNm").toString();
			List<Map<String, String>> clCntDetailContent = new ArrayList<Map<String, String>>();
			for (Map<String, String> bodyRow : bodyList) {
				if (tblNm.equals(bodyRow.get("TABLE_NAME")) && colNm.equals(bodyRow.get("COLUMN_NAME"))) {
					bodyRow.put("PREDICT_VALUE", targetMap.get(bodyRow.get("PREDICT_VALUE")));
					clCntDetailContent.add(bodyRow);
				}
			}

			rtnMap.put("clCntDetailContent", clCntDetailContent);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("분류 분석 상세 내용 조회 오류.");
		}

		return rtnMap;
	}

	// 레포팅 csv 저장
	public void saveResult(Session socketSession, Map<String, Object> dataMap, Map<String, Object> params) throws Exception {
		socket.sendToClient(socketSession, "STEP4", "1");
		
		List<SangsMap> dlist = (List<SangsMap>) dataMap.get("dlist");
		List<SangsMap> colInfoList = (List<SangsMap>) dataMap.get("colInfoList");

		String reportWritePath = aiDataDir + String.valueOf(params.get("atmcDgnssExcnSn"));

		File rpDir = new File(reportWritePath);
		if (!rpDir.exists()) {
			rpDir.mkdirs();
		}

		String clPath = reportWritePath + "/classification.csv";
		String errPath = reportWritePath + "/error_classification.csv";

		File file = new File(clPath);
		FileOutputStream output = null;
		OutputStreamWriter writer = null;
		BufferedWriter out = null;
		StringBuffer outColSb;
		StringBuffer writeSb;

		try {
			Map<String, String> targetMap = new HashMap<String, String>();

			// 분류 결과 저장
			file.createNewFile();
			output = new FileOutputStream(clPath, false);
			writer = new OutputStreamWriter(output, "MS949");
			out = new BufferedWriter(writer);

			outColSb = new StringBuffer();
			outColSb.append("TABLE_NAME");
			outColSb.append(",");
			outColSb.append("COLUMN_NAME");
			outColSb.append(",");
			outColSb.append("TARGET");
			outColSb.append(",");

			for (int i = 0; i < targetList.size(); i++) {
				outColSb.append(targetList.get(i));
				if (i == targetList.size() - 1) {
					outColSb.append("\n");
				} else {
					outColSb.append(",");
				}
			}
			out.write(outColSb.toString());

			writeSb = new StringBuffer();
			for (int i = 0; i < colInfoList.size(); i++) {
				SangsMap colMap = colInfoList.get(i);
				String tblNm = colMap.getString("dbmsTableNm");
				String colNm = colMap.getString("columnName");
				String target = colMap.getString("target");

				writeSb.append(tblNm);
				writeSb.append(",");
				writeSb.append(colNm.replace("?", ""));
				writeSb.append(",");
				writeSb.append(target);
				writeSb.append(",");

				targetMap.put(tblNm + "." + colNm, target);

				for (int j = 0; j < targetList.size(); j++) {
					if (colMap.keySet().contains(targetList.get(j))) {
						writeSb.append(colMap.get(targetList.get(j)));
					} else {
						writeSb.append(0);
					}
					if (j == targetList.size() - 1) {
						writeSb.append("\n");
					} else {
						writeSb.append(",");
					}
				}
			}
			out.append(writeSb.toString());
			out.close();

			
			
			
			socket.sendToClient(socketSession, "STEP4", "20");
			// 오류 판단 데이터 저장
			file.createNewFile();
			output = new FileOutputStream(errPath, false);
			writer = new OutputStreamWriter(output, "MS949");
			out = new BufferedWriter(writer);

			outColSb = new StringBuffer();
			outColSb.append("TABLE_NAME");
			outColSb.append(",");
			outColSb.append("COLUMN_NAME");
			outColSb.append(",");
			outColSb.append("TARGET");
			outColSb.append(",");
			outColSb.append("PREDICT_VALUE");
			outColSb.append(",");
			outColSb.append("COL_VAL");
			outColSb.append("\n");

			out.write(outColSb.toString());
			
			writeSb = new StringBuffer();
			for (int i = 0; i < dlist.size(); i++) {
				SangsMap colValMap = dlist.get(i);
				String tblNm = colValMap.getString("dbmsTableNm");
				String colNm = colValMap.getString("columnName");
				String predict = colValMap.getString("target");
				String value = getObjectToString(colValMap.get("colVal"));

				String tabCol = tblNm + "." + colNm;
				if (!targetMap.get(tabCol).equals(predict)) {
					writeSb.append(tblNm);
					writeSb.append(",");
					writeSb.append(colNm);
					writeSb.append(",");
					writeSb.append(targetMap.get(tabCol));
					writeSb.append(",");
					writeSb.append(predict);
					writeSb.append(",");
					writeSb.append(value.replaceAll("[,]", ""));
					writeSb.append("\n");
				}
			}
			out.append(writeSb.toString());
			out.close();
			
			
			
			
			
			
			
			socket.sendToClient(socketSession, "STEP4_END", "50");
			// 자연어 처리 데이터 저장
			String resultFileNm;
			String resultFilePath;
			List<Map<String, String>> bodyList;
			SangsCsvFileLoader csvLoader;
			
			List<Map<String, String>> nlpBodyList = new ArrayList<Map<String, String>>();
			Map<String, String> nlpBodyListRow;
			if (params.keySet().contains("NLP_RESULT_FILE_NAME_1")) {
				
				resultFileNm = String.valueOf(params.get("NLP_RESULT_FILE_NAME_1"));
				
				resultFilePath = mlmsApiResultPath + "/" + AiProfileMngService.BERT_CONT_MODEL_TEST_EXECUT_SN + "/" + resultFileNm;

				csvLoader = new SangsCsvFileLoader(resultFilePath, true);
				bodyList = csvLoader.getBodyList();
				
				for (Map<String, String> bodyListRow: bodyList) {
					nlpBodyListRow = new HashMap<String, String>();
					nlpBodyListRow.put("dbmsTableNm", bodyListRow.get("table_name"));
					nlpBodyListRow.put("columnName", bodyListRow.get("column_name"));
					nlpBodyListRow.put("inputSentence", bodyListRow.get("input_sentence").replaceAll("[,]", ""));
					nlpBodyListRow.put("errWord", bodyListRow.get("error_word").replaceAll("[,]", ""));
					nlpBodyListRow.put("checkedSentence", bodyListRow.get("checked_sentence").replaceAll("[,]", ""));
					nlpBodyListRow.put("recommendWord", bodyListRow.get("recommend_word").replaceAll("[,]", ""));
					nlpBodyListRow.put("predict", bodyListRow.get("Predict"));
					nlpBodyList.add(nlpBodyListRow);
				}
			}
			
			if (params.keySet().contains("NLP_RESULT_FILE_NAME_2")) {
				
				resultFileNm = String.valueOf(params.get("NLP_RESULT_FILE_NAME_2"));
				
				resultFilePath = mlmsApiResultPath + "/" + AiProfileMngService.BERT_CLF_MODEL_TEST_EXECUT_SN + "/" + resultFileNm;

				csvLoader = new SangsCsvFileLoader(resultFilePath, true);
				bodyList = csvLoader.getBodyList();
				
				for (Map<String, String> bodyListRow: bodyList) {
					nlpBodyListRow = new HashMap<String, String>();
					nlpBodyListRow.put("dbmsTableNm", bodyListRow.get("table_name"));
					nlpBodyListRow.put("columnName", bodyListRow.get("column_name"));
					nlpBodyListRow.put("inputSentence", bodyListRow.get("input_sentence").replaceAll("[,]", ""));
					nlpBodyListRow.put("errWord", "");
					nlpBodyListRow.put("checkedSentence", "");
					nlpBodyListRow.put("recommendWord", "");
					nlpBodyListRow.put("predict", bodyListRow.get("Predict"));
					nlpBodyList.add(nlpBodyListRow);
				}
			}
			
			String nlpPath = reportWritePath + "/nlp.csv";
			file.createNewFile();
			output = new FileOutputStream(nlpPath, false);
			writer = new OutputStreamWriter(output, "MS949");
			
			out = new BufferedWriter(writer);
			outColSb = new StringBuffer();
			outColSb.append("TABLE_NAME");
			outColSb.append(",");
			outColSb.append("COLUMN_NAME");
			outColSb.append(",");
			outColSb.append("PREDICT_VALUE");
			outColSb.append(",");
			outColSb.append("INPUT_SENTENCE");
			outColSb.append(",");
			outColSb.append("CHECKED_SENTENCE");
			outColSb.append(",");
			outColSb.append("ERROR_WORD");
			outColSb.append(",");
			outColSb.append("RECOMMEND_WORD");
			outColSb.append("\n");

			out.write(outColSb.toString());
			
			writeSb = new StringBuffer();
			for (int i = 0; i < nlpBodyList.size(); i++) {
				nlpBodyListRow = nlpBodyList.get(i);
				String tblNm = nlpBodyListRow.get("dbmsTableNm");
				String colNm = nlpBodyListRow.get("columnName");
				String predict = nlpBodyListRow.get("predict");
				String inputSentence = getObjectToString(nlpBodyListRow.get("inputSentence"));
				String checkedSentence = getObjectToString(nlpBodyListRow.get("checkedSentence"));
				String errorWord = getObjectToString(nlpBodyListRow.get("errWord"));
				String recommendWord = getObjectToString(nlpBodyListRow.get("recommendWord"));

				writeSb.append(tblNm);
				writeSb.append(",");
				writeSb.append(colNm);
				writeSb.append(",");
				writeSb.append(predict);
				writeSb.append(",");
				writeSb.append(inputSentence);
				writeSb.append(",");
				writeSb.append(checkedSentence);
				writeSb.append(",");
				writeSb.append(errorWord);
				writeSb.append(",");
				writeSb.append(recommendWord);
				writeSb.append("\n");
			}
			out.append(writeSb.toString());
			out.close();
			
			socket.sendToClient(socketSession, "STEP4_END", "100");
		
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (out != null) {
				try {
					output.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (writer != null) {
				try {
					output.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (output != null) {
				try {
					output.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getPrgsPerc(int startPerc, int endPerc, int totalValue, int currentValue) {

		if (totalValue == 0)
			return String.valueOf(startPerc);
		if (currentValue == 0)
			return String.valueOf(startPerc);

		int partPerc = ((currentValue * 100) / totalValue);
		return String.valueOf(startPerc + ((partPerc * (endPerc - startPerc)) / 100));
	}

	private String getObjectToString(Object obj) {
		String data = "";
		if (obj instanceof java.math.BigDecimal) {
			double d = ((java.math.BigDecimal) obj).doubleValue();
			data = String.valueOf(d);
		} else if (obj instanceof Integer) {
			int vint = ((Integer) obj).intValue();
			data = String.valueOf(vint);
		} else if (obj instanceof java.lang.Double) {
			double vdb = ((Double) obj).doubleValue();
			data = String.valueOf(vdb);
		} else if (obj instanceof java.lang.Long) {
			double vdb = ((Long) obj).longValue();
			data = String.valueOf(vdb);
		} else {
			data = String.valueOf(obj);
		}

		return data;
	}

	public static Map<String, String> targetMap() {
		Map<String, String> map = new HashMap<String, String>();
		// map.put("1_1", "년");
		// map.put("1_2", "년월");
		// map.put("1_3", "일자");
		map.put("1_4", "일시");
		// map.put("1_5", "월");
		// map.put("1_6", "일");
		// map.put("1_7", "시각");
		map.put("1_8", "TIMESTAMP");
		// map.put("1_9", "DATE");
		map.put("2_0", "명칭");
		map.put("2_3", "ID");
		map.put("2_6", "이메일");
		// map.put("2_7", "PASSWORD");
		map.put("2_8", "경로");
		map.put("2_9", "기본주소");
		map.put("2_10", "상세주소");
		map.put("2_11", "IPv4");
		map.put("2_12", "IPv6");
		map.put("3_0", "내용");
		map.put("4_1", "코드");
		map.put("4_2", "여부");
		map.put("5_0", "번호");
		map.put("5_1", "전화번호");
		map.put("5_2", "사업자등록번호");
		map.put("5_3", "우편번호");
		map.put("6_0", "수");
		// map.put("6_2", "위도");
		// map.put("6_3", "경도");
		map.put("6_4", "소수");
		// map.put("7_0", "금액");
		// map.put("8_0", "비율");
		map.put("NA", "NA");
		map.put("BLANK", "BLANK");

		// UNKNOWN은 화면상 표시되지않음
		map.put("UNKNOWN", "UNKNWON");

		return map;
	}


}
