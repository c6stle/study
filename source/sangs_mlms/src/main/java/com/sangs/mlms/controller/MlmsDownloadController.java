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
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.sangs.common.base.ControllerBase;
import com.sangs.fwk.annotation.SangsController;
import com.sangs.fwk.support.SangsPropertyUtil;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsStringUtil;
import com.sangs.mlms.common.MlmsConstant;
import com.sangs.mlms.service.DatasetService;
import com.sangs.mlms.service.LearningService;

@SangsController("/mlms/commdown")
public class MlmsDownloadController extends ControllerBase {
	

	@Value("${mls.resource.base_path:}")
	private String resourceBasePath;
	
	String modelBasePath = MlmsConstant.RESOURCE_MODEL_BASE_PATH;
	String testresultBasePath = MlmsConstant.RESOURCE_TESTRESULT_BASE_PATH;
	
	@Autowired
	private DatasetService datasetService;
	
	@Autowired
	private LearningService learningService;
	
	private Map<String, String> getFullFilePath(String fileType, String refId) throws Exception {
		Map<String, String> rtnMap = new HashMap<String, String>();
		
		if(SangsStringUtil.isEmpty(fileType))
			throw new SangsMessageException("fileType은 필수 항목입니다.");
		
		if(SangsStringUtil.isEmpty(refId))
			throw new SangsMessageException("refId은 필수 항목입니다.");
		
		//if(SangsStringUtil.isEmpty(fileNm))
		//	throw new SangsMessageException("fileNm은 필수 항목입니다.");
		
		//if(!checkFileName(fileNm))
		//	throw new SangsMessageException("파일명에는 경로가 들어갈수 없습니다.");
		
		if("RESULT".equals(fileType)) {
			
			SangsStringUtil.checkRequired(refId, "refId(datasetSn)");
			
			Map<String, Object> searchMap = new HashMap<String, Object>();
			searchMap.put("lrnExcnSn", Integer.parseInt(refId));
			
			Map<String, Object> rtnInfo = learningService.getLearningExcnInfo(searchMap);
			SangsMap execInfo = (SangsMap)rtnInfo.get("info");
			
			//fileFullPath = resourceBasePath + MlmsConstant.RESOURCE_TESTRESULT_BASE_PATH + refId + "/" + fileNm;
			
			rtnMap.put("fileFullPath", resourceBasePath + MlmsConstant.RESOURCE_TESTRESULT_BASE_PATH + refId + "/" + execInfo.getString("resultFlpth"));
			rtnMap.put("fileOrgPath", execInfo.getString("resultFlpth"));
			
		} else if("DATASET".equals(fileType)) { 
			
			SangsStringUtil.checkRequired(refId, "refId(datasetSn)");
			
			Map<String, Object> searchMap = new HashMap<String, Object>();
			searchMap.put("datasetSn", Integer.parseInt(refId));
			Map<String, Object> rtnInfo = datasetService.getSourceDatasetInfo(searchMap);
			SangsMap datasetInfo = (SangsMap)rtnInfo.get("info");
			//System.out.println(datasetInfo);
			
			rtnMap.put("fileFullPath", resourceBasePath + datasetInfo.getString("dataFlpth"));
			rtnMap.put("fileOrgPath", datasetInfo.getString("dataOrginlFlpth"));
			//fileFullPath = resourceBasePath + MlmsConstant.RESOURCE_DATASET_BASE_PATH + fileNm;
		}
		
		logger.debug("", rtnMap);
		
		return rtnMap;
		
	}
	
	
	 
	
	
	/** 
	 * 다운받을 파일을 체크 한다. 
	 * @param model
	 * @param paramMap
	 * @param fileType
	 * @param fileNm
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/checkFile")
	@ResponseBody
	public Map<String, Object> checkFile(@RequestBody Map<String, String> paramMap) throws Exception {
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		String resultCd = "";
		String resultMsg = "";
		
		try {
			String fileType = paramMap.get("fileType");
			//String fileNm = paramMap.get("fileNm");
			String refId = SangsStringUtil.nvl(paramMap.get("refId"), "");
			

			//String fileFullPath = this.getFullFilePath(fileType, fileNm, refId);
			Map<String, String> filInfoMap = this.getFullFilePath(fileType, refId);
			String fileFullPath = filInfoMap.get("fileFullPath");
			
			File file = new File(fileFullPath);
			
			if(!file.exists()) 
				throw new SangsMessageException("파일이 존재 하지 않습니다.");
			
			resultCd = "OK";
			
		} catch(SangsMessageException e) {
			resultCd = "FAIL";
			resultMsg = e.getMessage();
		} catch(Exception e) {
			resultCd = "FAIL";
			resultMsg = "처리중 에러가 발생하였습니다.";
		}
		rtnMap.put("resultCd", resultCd);
		rtnMap.put("resultMsg", resultMsg);
		return rtnMap;
	}
	
	
	
	
	
	/**
	 * 파일 다운로드 
	 * @param model
	 * @param paramMap
	 * @param fileType
	 * @param fileNm
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/download")
	public ResponseEntity<Resource> download(@RequestParam String fileType, @RequestParam(required = false) String refId) throws Exception {
		ByteArrayOutputStream os = null;
		try {
			
			logger.debug("#### input refId : " + refId);
			
			//String fileFullPath = this.getFullFilePath(fileType, fileNm, refId);
			Map<String, String> filInfoMap = this.getFullFilePath(fileType, refId);
			String fileFullPath = filInfoMap.get("fileFullPath");
			String fileOrgPath = filInfoMap.get("fileOrgPath");	
			
			
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
	
	
	
	/**
	 * 파일 카피 
	 * @param model
	 * @param file
	 * @param basePathId (property)
	 * @param subDir (하위경로)
	 * @param fileNm (파일명)
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/upload")
	@ResponseBody
	public Map<String, Object> fileCopy(Model model, @RequestPart("file") MultipartFile file, @RequestPart("file2") MultipartFile file2, 
										@RequestPart("file3") MultipartFile[] file3, @RequestParam(required = false) String subDir,
										@RequestPart("mupl_pyPathOrigin") String pyPathOrigin, @RequestParam(required = false) String fileNm) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			String tSubDir = SangsStringUtil.nvl(subDir);

			// save file
			if (!(file.isEmpty())) {
				String tFileNm = "";
				
				if(SangsStringUtil.isEmpty(fileNm))
					tFileNm = file.getOriginalFilename();
				else 
					tFileNm = fileNm;
				
				String fileFullPath = resourceBasePath + modelBasePath + tSubDir + tFileNm;				
				File saveFile = new File(fileFullPath);
				if(!saveFile.exists())
					saveFile.mkdirs();
				file.transferTo(saveFile);
			} 
			
			// save file2
			if (!(file2.isEmpty())) {
				
				if (!(pyPathOrigin.equals(file2.getOriginalFilename()))) {
					String orginFile2FullPath = resourceBasePath + modelBasePath + tSubDir + pyPathOrigin;
					File originPyFile = new File(orginFile2FullPath);
					if (originPyFile.exists()) {
						originPyFile.delete();
						logger.debug(orginFile2FullPath + "파일 삭제 완료");
					}
				}
				
				String file2FullPath = resourceBasePath + modelBasePath + tSubDir + file2.getOriginalFilename();
				File saveFile2 = new File(file2FullPath);

				if(!saveFile2.exists())
					saveFile2.mkdirs();
				file2.transferTo(saveFile2);
			}
			
			// save file3
			for (MultipartFile f3: file3) {
				if (!(f3.isEmpty())) {
					String file3FullPath = resourceBasePath + modelBasePath + tSubDir + f3.getOriginalFilename();
					File saveFile3 = new File(file3FullPath);
					
					if(!saveFile3.exists())
						saveFile3.mkdirs();
					f3.transferTo(saveFile3);
				}
			}
			
			
			rtnMap.put("resultCd", "OK");
			
		} catch(SangsMessageException e) {
			logger.error("", e);
			rtnMap.put("resultCd", "FAIL");
			rtnMap.put("resultMsg", e.getMessage());
		} catch(Exception e) {
			logger.error("", e);
			rtnMap.put("resultCd", "FAIL");
			rtnMap.put("resultMsg", "업로드중 에러가 발생하였습니다.");
		}
	
		return rtnMap;
	}
	
	
	
	/*
	private boolean checkFileName(String fileNm) {
		if(fileNm.indexOf("..") > -1)
			return false;
		
		return true;
	}
	*/
	
	

	
}
