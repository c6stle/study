package com.sangs.dq.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.websocket.Session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.sangs.common.support.AuthUtil;
import com.sangs.common.support.CommonDao;
import com.sangs.dq.config.AnalsSqlSessionTemplate;
import com.sangs.dq.util.AiProfileMsgSocket;
import com.sangs.dq.util.CSVReader;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.lib.support.domain.SangsMap;


@SangsService
public class AiProfileExcService {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CommonDao dao;
	
	@Value("${mlms.resource.result_path}")
	private String mlmsApiResultPath;
	
	@Value("${file.aiDiagnosis.dataDir}")
	private String aiDataDir;
	
	@Value("${mlms.system-domain}")
	private String mlmsApiServerUrl;
	
	@Autowired
	private AiProfileMsgSocket socket;
	
	private AnalsSqlSessionTemplate sqlSession = new AnalsSqlSessionTemplate();
	
	private String emailExpr = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
	private String cellPhoneNoExpr = "^01(?:0|1|[6-9])[-_.\\s]?(\\d{3}|\\d{4})[-_.\\s]?(\\d{4})$";
	private String phoneNoExpr = "^[+]?(02|0505|0502|0506|0\\d{2})[-_.\\s]?(\\d{3,4})[-_.\\s]?(\\d{4})";
	private String ipv4Expr = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
	private String ipv6Expr = "^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$";
	private String compRegExpr = "^(\\d{3,3})+[-]+(\\d{2,2})+[-]+(\\d{5,5})$";
	private String zipCodeExpr = "[0-6][0-3]\\d{3}";
	
	public Map<String, Object> doClassification(Session socketSession, Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		// db 접속정보, 분석할 테이블 리스트
		logger.debug("params : " + params);
		int searchLimitCount = 1000;
		if (!"".equals(params.get("limitCount")) && params.get("limitCount") != null) {
			searchLimitCount = Integer.valueOf((String)params.get("limitCount"));
		}
		
		Map<String, Object> smap = new HashMap<String, Object>();
		smap.put("dbmsDatabaseNm", AuthUtil.getDbmsDatabaseNm());
		smap.put("dbmsId", AuthUtil.getDbmsId());
		
		try {
			// 머신러닝 분석용 데이터
			List<SangsMap> mlList = new ArrayList<SangsMap>();
			// 패턴분류 결과 데이터
			List<SangsMap> clList = new ArrayList<SangsMap>();
			// 컬럼정보 데이터
			List<SangsMap> colInfoList = new ArrayList<SangsMap>();
			
			socket.sendToClient(socketSession, "STEP1", "1");
			int rowIndex = 0;

			Set<String> pkColList = new HashSet<String>();

			List<SangsMap> tblColList = sqlSession.selectList("AnalysisMapper.selectAnalysisTableColumnList", smap);
			
			for (SangsMap colMap: tblColList) {
				
				smap.put("dbmsTableNm", colMap.getString("dbmsTableNm"));
				int totCnt = sqlSession.selectInteger("AnalysisMapper.selectTableRowDataCnt", smap);
				
				// dataType 공통타입으로 변경
				String dataType = colMap.getString("dataType").toUpperCase();
				
				// 컬럼정보에 총카운트, 점검카운트 추가
				if (totCnt > searchLimitCount) {
					colMap.putOrg("totCnt", totCnt);
					colMap.putOrg("dgnssCnt", searchLimitCount);
				} else {
					colMap.putOrg("totCnt", totCnt);
					colMap.putOrg("dgnssCnt", totCnt);
				}
				
				// CLOB 타입 일시 봉인
				if ("CLOB".equals(dataType)) {
					continue;
				} else if ("TIMESTAMP".equals(dataType) || "DATE".equals(dataType) || "DATETIME".equals(dataType) || "TIMESTAMP WITHOUT TIME ZONE".equals(dataType) || "TIMESTAMP WITH TIME ZONE".equals(dataType)) {
					colMap.putOrg("target", "1_8");
					colMap.putOrg("1_8", colMap.getInt("dgnssCnt"));
					colMap.putOrg("dataType", "DATE");
					continue;
					
				} else if ("NUMBER".equals(dataType) || "INT".equals(dataType) || "INTEGER".equals(dataType) || "TINYINT".equals(dataType) || "SMALLINT".equals(dataType) || "MEDIUMINT".equals(dataType) || "BIGINT".equals(dataType)) {
					colMap.putOrg("dataType", "INTEGER");
					
				} else if ("VARCHAR2".equals(dataType) || "VARCHAR".equals(dataType) || "NVARCHAR".equals(dataType) || "LONG".equals(dataType) || "TINYTEXT".equals(dataType) || "TEXT".equals(dataType) || "MEDIUMTEXT".equals(dataType) || "LONGTEXT".equals(dataType) || "CHARACTER VARYING".equals(dataType)) {
					colMap.putOrg("dataType", "VARCHAR");
					
				} else if ("CHAR".equals(dataType) || "NCHAR".equals(dataType) || "CHARACTER".equals(dataType)) {
					colMap.putOrg("dataType", "CHAR");
					
				} else {
					colMap.putOrg("dataType", "OTHER");
				}
				
				// 토탈데이터가 searchLimitCount를 넘으면 searchLimitCount건만 조회
				colMap.putOrg("limitCount", searchLimitCount);
				List<SangsMap> colValList = sqlSession.selectList("AnalysisMapper.selectAnalysisColumnValueWithLimit", colMap);

				// 5_0 PK 인 컬럼 추출
				if ("INTEGER".equals(dataType) && pkColList.contains(colMap.getString("columnName"))) {
					colMap.putOrg("target", "5_0");
					colMap.putOrg("5_0", colMap.getInt("dgnssCnt"));
					continue;
				}
				
				// 전체정보를 보기위한 Set 데이터
				Set<String> valueDistinct = new HashSet<String>(); 
				
				
				for (SangsMap colValMap: colValList) {
					String target = "UNKNOWN";
					
					// Object To String
					String value = getObjectToString(colValMap.get("colVal"));
					
					if ("null".equals(value)) {
						target = "NA";
						if (colMap.keySet().contains(target)) {
							colMap.putOrg(target, colMap.getInt(target) + 1);
						} else {
							colMap.putOrg(target, 1);
						}
						colValMap.putOrg("dbmsTableNm", colMap.get("dbmsTableNm"));
						colValMap.putOrg("columnName", colMap.getString("columnName").replace("?", ""));
						colValMap.putOrg("target", target);
						clList.add(colValMap);
						valueDistinct.add(value);
						continue;
					} else if ("".equals(value)) {
						target = "BLANK";
						if (colMap.keySet().contains(target)) {
							colMap.putOrg(target, colMap.getInt(target) + 1);
						} else {
							colMap.putOrg(target, 1);
						}
						colValMap.putOrg("dbmsTableNm", colMap.get("dbmsTableNm"));
						colValMap.putOrg("columnName", colMap.getString("columnName").replace("?", ""));
						colValMap.putOrg("target", target);
						clList.add(colValMap);
						valueDistinct.add(value);
						continue;
					}
					// 패턴 분류기
					if (StringUtils.isNumeric(value)) {
						if (value.matches(phoneNoExpr) || value.matches(cellPhoneNoExpr)) {
							target = "5_1";
						} else {
							target = "6_0";
						}
					} else if(value.startsWith("-") && StringUtils.isNumeric(value.substring(1))) {
						target = "6_0";
					} else {
						if (value.matches(zipCodeExpr)) {
							target = "5_3";
						} else if (value.matches(compRegExpr)) {
							target = "5_2";
						}  else if (value.matches(phoneNoExpr) || value.matches(cellPhoneNoExpr)) {
							target = "5_1";							
						} else if (value.matches(ipv4Expr)) {
							target = "2_11";
						} else if (value.matches(ipv6Expr)) {
							target = "2_12";
						} else if (value.matches(emailExpr)) {
							target = "2_6";
						} else if ("Y".equals(value) || "N".equals(value)) {
							target = "4_2";
						} else if ( value.contains(".") & StringUtils.isNumeric(value.replace("-", "").replace(".", "")) ) {
							target = "6_4";
						} else {
							target = "UNKNOWN";
						}
					}
					
					valueDistinct.add(value);
					
					colValMap.putOrg("dbmsTableNm", colMap.get("dbmsTableNm"));
					colValMap.putOrg("columnName", colMap.getString("columnName").replace("?", ""));
					colValMap.putOrg("target", target);
					if (colMap.getInt("dgnssCnt") != 0) {
						if ("UNKNOWN".equals(target)) {
							// ai classification data
							mlList.add(colValMap);
						} else {
							// pattern classification data
							clList.add(colValMap);
						}
					}
				}
				
				// 유니크카운트 계산
				int uniqueCnt = valueDistinct.size();
				colMap.putOrg("uniqueCnt", uniqueCnt);
				
				// pk 컬럼 추출
				if ( uniqueCnt==colMap.getInt("totCnt") && "INTEGER".equals(dataType) ) {
					pkColList.add(colMap.getString("columnName"));
				}
				
				// column info insert (colMap) -> 인서트 대신 컬럼정보를 담은 객체 생성
				colInfoList.add(colMap);

				socket.sendToClient(socketSession, "STEP1", AiProfileMngService.getPrgsPerc(1, 60, tblColList.size(), rowIndex));
				rowIndex++;
			}		
			
			// ML 데이터 저장
			this.saveCsvFile(mlList, params);
			
			// API 송신, 결과파일패스 수신
			try {
				String uploadUrl = mlmsApiServerUrl + "/api/upload_test_file";
				String filePath = aiDataDir + String.valueOf(params.get("atmcDgnssExcnSn")) + "/" + String.valueOf(params.get("ML_FILE_NAME"));
				
				RestTemplate restTemplate = new RestTemplate();
				FileSystemResource fileResource = new FileSystemResource(filePath);
				
				MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
				body.add("upFile", fileResource);
				body.add("lrnExcnSn", AiProfileMngService.CLSF_BINARY_MODEL_TEST_EXECUT_SN);
				body.add("reqServerUrl", mlmsApiServerUrl);
				body.add("exclRowCnt", "1");	// 데이터셋의 제외 row
				
				ResponseEntity<String> result = restTemplate.postForEntity(uploadUrl, body, String.class);
				
				if(result != null && result.getStatusCodeValue() == 200) {
					String resultBody = result.getBody();
					String resultFlpth = "";
					if(resultBody.indexOf("OK") > -1) {
						String[] arrResult = resultBody.split(":");
						resultFlpth = arrResult[1];
						params.put("ML_RESULT_FILE_NAME", resultFlpth);
					}
				}
			} catch (Exception e) {
				socket.sendToClient(socketSession, "ERROR", "머신러닝 학습시스템 구동 에러");
				logger.debug("머신러닝 학습시스템 구동 에러");
				//throw e;
			}
			socket.sendToClient(socketSession, "STEP1", "90");
			
			// 분석결과 csv 읽기, mlList에 타겟 할당
			this.readMlFile(mlList, params);

			// 패턴분류리스트에 머신러닝분류리스트 추가 : clList
			clList.addAll(mlList);
			
			// 컬럼 데이터 리스트 루프돌면서 타겟정보 객체 생성(key:테이블명.컬럼명, value:타겟카운트맵)
			Map<String, Map<String, Integer>> targetMap = new HashMap<String, Map<String, Integer>>();
			Map<String, Integer> targetCntMap;
			for (SangsMap colValMap:clList) {
				// clMap key : dbmsTableNm, columnName, target, colVal
				String tblColNm = colValMap.getString("dbmsTableNm") + "." + colValMap.getString("columnName");
				String target = colValMap.getString("target");
				
				if (targetMap.keySet().contains(tblColNm)) {
					targetCntMap = targetMap.get(tblColNm);
					if (targetCntMap.keySet().contains(target)) {
						targetCntMap.put(target, targetCntMap.get(target) + 1);
					} else {
						targetCntMap.put(target, 1);
					}
				} else {
					targetCntMap = new HashMap<String, Integer>();
					targetCntMap.put(target, 1);
				}
				
				targetMap.put(tblColNm, targetCntMap);
			}

			// 컬럼정보 리스트 업데이트 후 DB 인서트
			int tblNo = 1; 
			for (SangsMap colMap:colInfoList) {
				// 1. 타겟 맵 불러와서 컬럼정보에 추가
				String tblColNm = colMap.getString("dbmsTableNm") + "." + colMap.getString("columnName");
				targetCntMap = targetMap.get(tblColNm);

				if (colMap.getInt("totCnt") != 0 && colMap.getInt("dgnssCnt") != 0) {
					if ( pkColList.contains(colMap.getString("columnName")) ||
							(targetCntMap.keySet().contains("6_0") && colMap.getInt("dgnssCnt") == colMap.getInt("uniqueCnt") )) {
						colMap.putOrg("5_0", colMap.getInt("dgnssCnt"));
						targetCntMap.put("5_0", colMap.getInt("dgnssCnt"));
					} else {
						for (String target: targetCntMap.keySet()) {					
							colMap.putOrg(target, targetCntMap.get(target));
						}
					}
				}
				
				// 2. 타겟이 아닌대상 제거
				//targetCntMap.remove("NA");
				//targetCntMap.remove("BLANK");
				//targetCntMap.remove("UNKNOWN");
				if (targetCntMap == null) {
					targetCntMap = new HashMap<String, Integer>();
					targetCntMap.put("UNKNOWN", 0);
				}
				
				// 3. 예측된 값중 제일 많은 타겟을 최종 타겟으로 컬럼정보에 추가
				if (!colMap.keySet().contains("target")) {
					colMap.putOrg("target", Collections.max(targetCntMap.entrySet(), Map.Entry.comparingByValue()).getKey());
				}
				
				// 4. DB insert
				Map<String, Object> imap = new HashMap<String, Object>();
				imap.put("atmcDgnssExcnSn", params.get("atmcDgnssExcnSn"));
				imap.put("dgnssTrgtNo", tblNo);
				imap.put("dgnssTblNm", colMap.get("dbmsTableNm"));
				imap.put("dgnssColNm", colMap.get("columnName"));
				imap.put("totDataCnt", colMap.get("totCnt"));
				imap.put("dgnssDataCnt", colMap.get("dgnssCnt"));
				imap.put("regUserId", AuthUtil.getUserId());
				
				if (colMap.getInt("totCnt") == 0 || colMap.getInt("dgnssCnt") == 0) {
					imap.put("colClPredictValue", colMap.getString("target"));
					imap.put("colObsryRate", null);
					imap.put("clAnlsErrCnt", 0);
				} else {
					String target = colMap.getString("target");
					int finalTagetCnt = colMap.getInt(target);
					double colObsryRate = ((double)finalTagetCnt / colMap.getInt("dgnssCnt") * 10000) / 100.00;
					imap.put("colClPredictValue", colMap.getString("target"));
					imap.put("colObsryRate", colObsryRate);
					imap.put("clAnlsErrCnt", colMap.getInt("dgnssCnt") - finalTagetCnt);
				}
				dao.insert("dq_ai_profile.insertDgnssAnls", imap);
				socket.sendToClient(socketSession, "STEP1", AiProfileMngService.getPrgsPerc(91, 99, colInfoList.size(), tblNo));
				tblNo += 1;
			}
			
			socket.sendToClient(socketSession, "STEP1_END", "100");
			rtnMap.put("dlist", clList);
			rtnMap.put("colInfoList", colInfoList);
			
		} catch(Exception e) {
			throw e;
		}
		
		return rtnMap;
	}
	
	
	
	
	private void saveCsvFile(List<SangsMap> mlList, Map<String, Object> params) throws Exception {
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
		String filePostFix = format.format(new Date());
		String path = aiDataDir + String.valueOf(params.get("atmcDgnssExcnSn"));
		
		File saveDir = new File(path);
		
		if (!saveDir.exists()) {
			saveDir.mkdirs();
		}
		
		String resultFileNm = "ML_ANALYSIS_" + filePostFix + UUID.randomUUID().toString() + ".csv";
		path += "/" + resultFileNm;
				
		logger.debug("save file : " + path);
		
		File file = new File(path);
		FileOutputStream output = null;
		OutputStreamWriter writer = null;
		BufferedWriter out = null;
		
		try {
			
			file.createNewFile();
			output = new FileOutputStream(path, false);
	        
			writer = new OutputStreamWriter(output,"MS949");
		    out = new BufferedWriter(writer);
			
		    List<String> outColList = new ArrayList<String>();
		    outColList.add("TABLE_NAME");
		    outColList.add("COLUMN_NAME");
		    outColList.add("DATA");
		    outColList.add("TARGET");
		    
		    StringBuffer outColSb = new StringBuffer();
		    for(int i = 0 ; i < outColList.size() ; i++) {
		    	outColSb.append(outColList.get(i));
		    	if(i == (outColList.size() - 1))
		    		outColSb.append("\n");
		    	else
		    		outColSb.append(",");
		    }
		    out.write(outColSb.toString());
		    
		    StringBuffer writeSb = new StringBuffer();
		    for (Map<String, Object> colValMap: mlList) {
		    	String tableName = String.valueOf(colValMap.get("dbmsTableNm"));
		    	String columnName = String.valueOf(colValMap.get("columnName"));
		    	String colVal = String.valueOf(colValMap.get("colVal"));
		    	String target = String.valueOf(colValMap.get("target"));
		    	writeSb.append(tableName);
	    		writeSb.append(",");
	    		writeSb.append(columnName);
	    		writeSb.append(",");
	    		writeSb.append(colVal.replaceAll("(,|\r\n|\r|\n|\n\r)", ""));
	    		writeSb.append(",");
	    		writeSb.append(target);
	    		writeSb.append("\n");
		    }
		    
		    out.append(writeSb.toString());
		    
		    params.put("ML_FILE_NAME", resultFileNm);
		    out.close();
		    
		} catch (IOException e) {
		    e.printStackTrace();
		    throw e;
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) { e.printStackTrace();}
			}
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) { e.printStackTrace();}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) { e.printStackTrace();}
			}
		}
	}
	
	
	
	
	public void readMlFile(List<SangsMap> mlList, Map<String, Object> params){
		FileInputStream input = null;
		CSVReader in = null;
		try {
			if (params.keySet().contains("ML_RESULT_FILE_NAME")) {
				String resultFileNm = String.valueOf(params.get("ML_RESULT_FILE_NAME"));
				
				input = new FileInputStream(mlmsApiResultPath + "/" + AiProfileMngService.CLSF_BINARY_MODEL_TEST_EXECUT_SN + "/" + resultFileNm);
				in = new CSVReader(new InputStreamReader(input, "cp949"), ',');
				
				String[] nextLine = null;
				int rowIndex = 0;
				
				while ((nextLine = in.readNext()) != null) {
					if (rowIndex != 0) {
						Map<String, Object> colValMap = mlList.get(rowIndex -1);
						colValMap.put("target", nextLine[2]);
					}
					rowIndex++;
				}
			}
			
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) { e.printStackTrace();}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) { e.printStackTrace();}
			}
		}
	}
	
	
	
	
	
	private String getObjectToString(Object obj) {
		String data = "";
		if(obj instanceof java.math.BigDecimal) {
			double d = ((java.math.BigDecimal)obj).doubleValue();
			data = String.valueOf(d);
		} else if(obj instanceof Integer) {
			int vint = ((Integer)obj).intValue();
			data = String.valueOf(vint);
		} else if(obj instanceof java.lang.Double) {
			double vdb = ((Double)obj).doubleValue();
			data = String.valueOf(vdb);
		} else if(obj instanceof java.lang.Long) {
			double vdb = ((Long)obj).longValue();
			data = String.valueOf(vdb);
		} else {
			data = String.valueOf(obj);
		}
		
		return data;
	}
	

	
}
