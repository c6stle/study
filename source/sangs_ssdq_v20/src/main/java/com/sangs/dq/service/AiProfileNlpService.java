package com.sangs.dq.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.sangs.dq.util.AiProfileMsgSocket;
import com.sangs.lib.support.domain.SangsMap;


@Service
public class AiProfileNlpService {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private AiProfileMsgSocket socket;
	
	@Value("${mlms.system-domain}")
	private String mlmsApiServerUrl;
	
	@Value("${file.aiDiagnosis.dataDir}")
	private String aiDataDir;
	
	public void doNlp(Session socketSession, Map<String, Object> dataMap, Map<String, Object> params) throws Exception {
		List<SangsMap> dlist = (List<SangsMap>) dataMap.get("dlist");
		List<SangsMap> nlpList = new ArrayList<SangsMap>();
		Set<String> valueDistinct = new HashSet<String>();
		
		try {
			for (SangsMap colValMap:dlist) {
				String target = colValMap.getString("target");
				String colVal = colValMap.getString("colVal");
				
				if ("3_0".equals(target)) {
					if (valueDistinct.contains(colVal)) {
			    		continue;
			    	} else {
			    		valueDistinct.add(colVal);
			    		nlpList.add(colValMap);
			    	}
				}
			}
			
			this.saveText(nlpList, params);
			
			this.doContextErrExtraction(socketSession, params);
			
			this.doNegtvExtraction(socketSession, params);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	
	public void saveText(List<SangsMap> nlpList, Map<String, Object> params) throws Exception{

		SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
		String filePostFix = format1.format(new Date());
		String path = aiDataDir + String.valueOf(params.get("atmcDgnssExcnSn"));
		
		File saveDir = new File(path);
		
		if (!saveDir.exists()) {
			saveDir.mkdirs();
		}
		
		String resultFileNm = "NLP_" + filePostFix + UUID.randomUUID().toString() + ".csv";
		path += "/" + resultFileNm;
				
		File file = new File(path);
		FileOutputStream output = null;
		OutputStreamWriter writer = null;
		BufferedWriter out = null;
		
		try {
			file.createNewFile();
			output = new FileOutputStream(path, false);
	        
			writer = new OutputStreamWriter(output,"MS949");
		    out = new BufferedWriter(writer);
		    
		    List<String> outCol = new ArrayList<String>();
		    outCol.add("TABLE_NAME");
		    outCol.add("COLUMN_NAME");
		    outCol.add("DATA");
		    outCol.add("TARGET");
		    
		    StringBuffer outColSb = new StringBuffer();
		    for(int i = 0 ; i < outCol.size() ; i++) {
		    	outColSb.append(outCol.get(i));
		    	if(i == (outCol.size() - 1))
		    		outColSb.append("\n");
		    	else
		    		outColSb.append(",");
		    }
		    out.write(outColSb.toString());
		    
		    StringBuffer writeSb = new StringBuffer();
		    for (SangsMap colValMap: nlpList) {
		    	String tableName = colValMap.getString("dbmsTableNm");
		    	String columnName = colValMap.getString("columnName");
		    	String colVal = colValMap.getString("colVal");
		    	String target = colValMap.getString("target");
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
		    
		    params.put("NLP_FILE_NAME", resultFileNm);
		    out.close();
		    
		} catch (Exception e) {
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
	
	
	
	// 문맥 오류 추출
	public Map<String, Object> doContextErrExtraction(Session socketSession, Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			socket.sendToClient(socketSession, "STEP2", "10");
			
			String uploadUrl = mlmsApiServerUrl + "/api/upload_test_file";
			String filePath = aiDataDir + String.valueOf(params.get("atmcDgnssExcnSn")) + "/" + String.valueOf(params.get("NLP_FILE_NAME"));
		
			RestTemplate restTemplate = new RestTemplate();
			FileSystemResource fileResource = new FileSystemResource(filePath);
		    MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
			
		    body.add("upFile", fileResource);
		    body.add("lrnExcnSn", AiProfileMngService.BERT_CONT_MODEL_TEST_EXECUT_SN);
		    body.add("reqServerUrl", mlmsApiServerUrl);
		    body.add("cid", params.get("cid"));
		    body.add("exclRowCnt", "1");	// 데이터셋의 제외 row
		    body.add("stepId", "");
			
		    ResponseEntity<String> result = restTemplate.postForEntity(uploadUrl, body, String.class);
		    
		    if (result != null && result.getStatusCodeValue() == 200) {
		    	String resultBody = result.getBody();
		    	
		    	String resultFlpth = "";
		    	if(resultBody.indexOf("OK") > -1) {
		    		String[] arrResult = resultBody.split(":");
		    		//String resultCd = arrResult[0];
		    		resultFlpth = arrResult[1];
		    		
		    		params.put("NLP_RESULT_FILE_NAME_1", resultFlpth);
		    		logger.debug(resultFlpth);
		    	}
		    }
//		    params.put("NLP_RESULT_FILE_NAME_1", "result_475_10.csv");
		    socket.sendToClient(socketSession, "STEP2_END", "100");
		    
		} catch(Exception e) {
			socket.sendToClient(socketSession, "ERROR", "머신러닝 학습시스템 구동 에러");
			socket.sendToClient(socketSession, "STEP2_END", "100");
			logger.debug("머신러닝 학습시스템 구동 에러");
		}
		return rtnMap;
	}
	
	
	
	// 부정문 검출
	public Map<String, Object> doNegtvExtraction(Session socketSession, Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			socket.sendToClient(socketSession, "STEP3", "10");
			
			String uploadUrl = mlmsApiServerUrl + "/api/upload_test_file";
			String filePath = aiDataDir + String.valueOf(params.get("atmcDgnssExcnSn")) + "/" + String.valueOf(params.get("NLP_FILE_NAME"));
		
			RestTemplate restTemplate = new RestTemplate();
			FileSystemResource fileResource = new FileSystemResource(filePath);
		    MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
			
		    body.add("upFile", fileResource);
		    body.add("lrnExcnSn", AiProfileMngService.BERT_CLF_MODEL_TEST_EXECUT_SN);
		    body.add("reqServerUrl", mlmsApiServerUrl);
		    body.add("cid", params.get("cid"));
		    body.add("exclRowCnt", "1");	// 데이터셋의 제외 row
		    body.add("stepId", "");
			
		    ResponseEntity<String> result = restTemplate.postForEntity(uploadUrl, body, String.class);
		    
		    if (result != null && result.getStatusCodeValue() == 200) {
		    	String resultBody = result.getBody();
		    	
		    	String resultFlpth = "";
		    	if(resultBody.indexOf("OK") > -1) {
		    		String[] arrResult = resultBody.split(":");
		    		//String resultCd = arrResult[0];
		    		resultFlpth = arrResult[1];
		    		
		    		params.put("NLP_RESULT_FILE_NAME_2", resultFlpth);
		    		logger.debug(resultFlpth);
		    	}
		    }
//		    params.put("NLP_RESULT_FILE_NAME_2", "result_489_4.csv");
		    socket.sendToClient(socketSession, "STEP3_END", "100");
		    
		} catch(Exception e) {
			socket.sendToClient(socketSession, "ERROR", "머신러닝 학습시스템 구동 에러");
			socket.sendToClient(socketSession, "STEP3_END", "100");
			logger.debug("머신러닝 학습시스템 구동 에러");
		}
		return rtnMap;
	}
	
}
