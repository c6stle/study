package com.sangs.mlms.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.sangs.common.base.ControllerBase;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsFileUtil;
import com.sangs.lib.support.utils.SangsStringUtil;
import com.sangs.mlms.common.MlmsConstant;
import com.sangs.mlms.service.ApiLearningService;
import com.sangs.mlms.service.DatasetService;
import com.sangs.mlms.service.LearningService;
import com.sangs.mlms.service.mlm.MlExecuterManager;

//@SangsController("/api")
@RestController
@RequestMapping("/api")
public class ApiController extends ControllerBase {

	@Value("${mls.resource.base_path:}")
	private String resourceBaseBath;
	
	@Autowired
	private ApiLearningService apiLearningService;
	
	@Autowired
	private LearningService learningService;
	
	
	@Autowired
	private DatasetService datasetService;
	
	@Autowired
	private MlExecuterManager mlExecuterManager;
	
	@RequestMapping(value="/upload_test_file", method = RequestMethod.POST)
	public String uploadTest(
			@RequestParam(value="upFile", required = true) MultipartFile upFile
			, @RequestParam(value="lrnExcnSn", required = true) String lrnExcnSn 
			//, @RequestParam(value="cid", required = true) String cid
			, @RequestParam(value="reqServerUrl", required = true) String reqServerUrl
			, @RequestParam(value="exclRowCnt", required = true) String exclRowCnt
			//, @RequestParam(value="stepId", required = false) String stepId
			){
		String resultCd = "";
		try {
			
			String saveFilePath = resourceBaseBath + MlmsConstant.RESOURCE_DATASET_BASE_PATH + MlmsConstant.RESOURCE_API_DATASET_BASE_PATH;
			
			String newFileId = SangsFileUtil.makeUniqueFileNm();
			String orgFileNm = upFile.getOriginalFilename();
			String orgExtFileNm = orgFileNm.substring(orgFileNm.lastIndexOf("."));
			String saveFileNm = newFileId + orgExtFileNm;

			File saveDir = new File(saveFilePath);
			if(!saveDir.exists())
				saveDir.mkdirs();
		   
			File saveFile = new File(saveFilePath + saveFileNm);
			upFile.transferTo(saveFile);
	        
			int ilrnExcnSn = Integer.parseInt(lrnExcnSn);
			
			Map<String, String> fileParamMap = new HashMap<String, String>();
			fileParamMap.put("newFileId", newFileId);
			fileParamMap.put("orgFileNm", orgFileNm);
			fileParamMap.put("orgExtFileNm", orgExtFileNm);
			fileParamMap.put("saveFilePath", saveFilePath);
			
			// call python executor method		
			Map<String, Object> resultMap = apiLearningService.runTestFromApi(ilrnExcnSn, Integer.parseInt(exclRowCnt), fileParamMap, this, reqServerUrl);
			
			resultCd = "OK:" +resultMap.get("resultFlpth");
		
		} catch(SangsMessageException e) {
			System.out.println("##### eeeee"+ e.getMessage());
			e.printStackTrace();
			resultCd = "FAIL";
		} catch(Exception e) {
			e.printStackTrace();
			resultCd = "FAIL";
		}
		return resultCd;
	} 

	
	
	/*
	 * @Async public void updatePrgs(String reqServerUrl, String cid, String stepId,
	 * String prgsVal) { logger.debug("!!!!!! updatePrgs start"); try {
	 * if(SangsStringUtil.isEmpty(stepId) || "STEP".equals(stepId)) return;
	 * 
	 * RestTemplate restTemplate = new RestTemplate(); HttpHeaders headers = new
	 * HttpHeaders(); headers.setContentType(MediaType.MULTIPART_FORM_DATA);
	 * 
	 * 
	 * String callUrl = reqServerUrl + "/dq/aiProfile/updatePrgs.do";
	 * 
	 * MultiValueMap<String, Object> body = new LinkedMultiValueMap<String,
	 * Object>(); body.add("stepId", stepId); body.add("prgsVal", prgsVal);
	 * body.add("cid", cid);
	 * 
	 * 
	 * ResponseEntity<String> result = restTemplate.postForEntity(callUrl, body,
	 * String.class);
	 * 
	 * System.out.println("result : " +result);
	 * //System.out.println("#### result.getStatusCode( : " +
	 * result.getStatusCodeValue());
	 * 
	 * if(result != null && result.getStatusCodeValue() == 200) {
	 * logger.debug("OK"); //String resultBody = result.getBody(); } } catch
	 * (Exception e) { e.printStackTrace(); } logger.debug("!!!!!! updatePrgs end");
	 * }
	 */
	
	
	
	/**
	 * 결과 파일 다운로드 
	 * @param lrnExcnSn
	 * @param resultFlpth
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/result_download")
	public ResponseEntity<Resource> resultDownload(@RequestParam String lrnExcnSn, @RequestParam String resultFlpth) throws Exception {
		ByteArrayOutputStream os = null;
		try {
			
			logger.debug("#### input lrnExcnSn : " + lrnExcnSn);
			logger.debug("#### input resultFlpth : " + resultFlpth);

			String fileFullPath = resourceBaseBath + MlmsConstant.RESOURCE_TESTRESULT_BASE_PATH + lrnExcnSn + "/" + resultFlpth;
			
			logger.debug("#### fileFullPath : " + fileFullPath);
			
			Path path = Paths.get(fileFullPath);
			String contentType = Files.probeContentType(path);
			
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_TYPE, contentType);
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+URLEncoder.encode(resultFlpth, "utf-8")+"\"");

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
	
	
	/**
	 * 라벨정보 조회
	 * @param lrnExcnSn
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/get_label_info")
	public Map<String, Object> getLabelInfo(@RequestParam String lrnExcnSn) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			logger.debug("#### input lrnExcnSn : " + lrnExcnSn);
			Map<String, Object> searchMap = new HashMap<String, Object>();
			searchMap.put("lrnExcnSn", lrnExcnSn);
			Map<String, Object> info = learningService.getLearningExcnInfo(searchMap);
			
			SangsMap execInfo = (SangsMap)info.get("info");
			
			searchMap.put("lrnDatasetSn", execInfo.get("lrnDatasetSn"));	// 훈련데이터셋의 라벨을 조회
			
			Map<String, String> labelInfo = datasetService.getTargetNmMap(searchMap);
			
			
			if(rtnMap.size() == 0) {
				searchMap.put("lrnDatasetSn", execInfo.get("testDatasetSn"));	// 테스트데이터셋의 라벨을 조회
				labelInfo = datasetService.getTargetNmMap(searchMap);
			}
			
			rtnMap.put("info", labelInfo);
			rtnMap.put("label_length", labelInfo.size());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return rtnMap;
	}
	
	
	
	
	
	
	@PostMapping(value="/get_exec_py_result")
	public Map<String, Object> getExecPyResult(@RequestBody Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		String resultCd = "";
		String resultMsg = "";
		try {
			
			//System.out.println("pyName!! : " + paramMap);
			//System.out.println("strParam : " + strParam);
			
			String pyName = (String)paramMap.get("pyName");
			String strParam = (String)paramMap.get("strParam");
			resultMsg = mlExecuterManager.getExecPyResult(pyName, strParam);
			
			resultCd = "OK";
		} catch(Exception e) {
			e.printStackTrace();
			resultCd = "FAIL";
		}
		rtnMap.put("resultCd", resultCd);
		rtnMap.put("resultMsg", resultMsg);
		return rtnMap;
	}
	
	
	
	
}
