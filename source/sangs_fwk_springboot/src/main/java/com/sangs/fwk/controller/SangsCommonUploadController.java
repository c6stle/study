package com.sangs.fwk.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.sangs.fwk.annotation.SangsController;
import com.sangs.fwk.support.SangsPropertyUtil;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsFileUtil;
import com.sangs.lib.support.utils.SangsStringUtil;


@SangsController("/cmmnUpload")
public class SangsCommonUploadController {

protected Logger logger = LoggerFactory.getLogger(this.getClass());
 
	@PostMapping("/upload")
	@ResponseBody
	public Map<String, Object> upload(Model model, @RequestPart("file") MultipartFile file, @RequestParam(required = true) String basePathId, @RequestParam(required = false) String subDir, @RequestParam(required = false) String fileNm) throws SangsMessageException {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			SangsStringUtil.checkRequired(basePathId, "basePathId");
			
			logger.debug("org file name : " + file.getOriginalFilename());
			String tFileNm = "";
			
			String tSubDir = SangsStringUtil.nvl(subDir);
			
			if(SangsStringUtil.isEmpty(fileNm))
				tFileNm = file.getOriginalFilename();
			else 
				tFileNm = fileNm;
			
			if(tFileNm.indexOf("..") >= 0) 
				throw new SangsMessageException("업로드 할수 없는 파일 명입니다.");
			
			String basePath = SangsPropertyUtil.getProperty(basePathId);
			logger.debug("basepath = {}", basePath);
			
			if(SangsStringUtil.isEmpty(basePath))
				throw new SangsMessageException(basePathId + "에 대한 property 값이 없습니다.(application.properties 파일을 확인 해주세요");
			
			String phycFileNm = SangsFileUtil.convertUniqueFileNm(tFileNm);
			
			String fileFullPath = basePath + tSubDir + phycFileNm;
			
			logger.debug("upload full path = {}", fileFullPath);
			
			File saveFile = new File(fileFullPath);
			
			if(!saveFile.exists())
				saveFile.mkdirs();
			
			file.transferTo(saveFile);
			
			rtnMap.put("resultCd", "OK");
			rtnMap.put("orgFileNm", tFileNm);
			rtnMap.put("savedFileNm", phycFileNm);
			rtnMap.put("basePathId", basePathId);
			rtnMap.put("subDir", tSubDir);
			
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
	
	

 

	
}
