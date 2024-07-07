package com.sangs.dq.controller;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sangs.dq.service.AiProfileMngService;
import com.sangs.dq.util.AiProfileMsgSocket;


@Controller
public class AiProfileController {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	
	@Autowired
	private AiProfileMsgSocket socket; 
	
	@Autowired
	private AiProfileMngService aiProfileMngService;
	
	@Value("${file.aiDiagnosis.dataDir}")
	private String tempDir;
	
	public static Map<String, Object> autoAnalysisInfoMap = new HashMap<String, Object>();
	
	@ResponseBody
	@RequestMapping("/dq/aiProfile/doAiAnalysis")
	public Map<String, Object> doAiAnalysis(@RequestBody Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) {
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		String resultCd = "";
		try {
			//System.out.println("## socket : " + socket);
			//System.out.println(params);
			
			String cid = (String)params.get("cid");
					
			Session socketSession = socket.getSessionByCid(cid);
			
			aiProfileMngService.runAutoAnalysis(socketSession, params);
			
			resultCd = "OK";
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("", e);
			resultCd = "FAIL";
		}
		
		rtnMap.put("resultCd", resultCd);
		return rtnMap;
	}
	
	
	/*
	 * @ResponseBody
	 * 
	 * @RequestMapping("/dq/aiProfile/updatePrgs.do") public Map<String, Object>
	 * updatePrgs(@RequestParam Map<String, Object> params, HttpServletRequest req)
	 * { Map<String, Object> rtnMap = new HashMap<String, Object>();
	 * 
	 * System.out.println("params--->" + params);
	 * 
	 * String stepId = (String)params.get("stepId"); String prgsVal =
	 * (String)params.get("prgsVal"); String cid = (String)params.get("cid");
	 * 
	 * Session socketSession = socket.getSessionByCid(cid);
	 * 
	 * socket.sendToClient(socketSession, stepId, prgsVal);
	 * 
	 * rtnMap.put("resultCd", "OK"); return rtnMap; }
	 */
	
	
	
	@PostMapping("/dq/aiProfile/downUnCorrectData")
	public ResponseEntity<Resource> download(@RequestParam int atmcDgnssExcnSn) throws Exception {
		ByteArrayOutputStream os = null;
		try {
			String unCorFilePath = tempDir + String.valueOf(atmcDgnssExcnSn);
			
			String unCorFullPath = unCorFilePath + "/error_classification.csv";
			
			
			//String fileFullPath = this.getFullFilePath(fileType, fileNm, refId);
			Map<String, String> fileInfoMap = new HashMap<String, String>();
			fileInfoMap.put("fileFullPath", unCorFullPath);
			fileInfoMap.put("fileOrgPath", "error_classification.csv");
			
			
			String fileFullPath = fileInfoMap.get("fileFullPath");
			String fileOrgPath = fileInfoMap.get("fileOrgPath");	
			
			
			String outFileNm = "";		// 원본파일명
			if(fileOrgPath.indexOf("/") >= 0) {
				outFileNm = fileOrgPath.substring(fileOrgPath.lastIndexOf("/") + 1, fileOrgPath.length());
			} else {
				outFileNm = fileOrgPath;
			}
			
			logger.debug("#### outFileNm : " + outFileNm);
			
			Path path = Paths.get(fileFullPath);
			String contentType = Files.probeContentType(path);
			
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_TYPE, contentType);
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+URLEncoder.encode(outFileNm, "utf-8")+"\"");

			Resource resource = new InputStreamResource(Files.newInputStream(path));
			
			return new ResponseEntity<>(resource, headers, HttpStatus.OK);
			
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(os != null)
				os.close();
		}
			
	}
	
	
}
