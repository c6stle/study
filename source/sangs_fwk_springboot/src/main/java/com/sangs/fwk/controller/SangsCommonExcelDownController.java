package com.sangs.fwk.controller;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sangs.fwk.annotation.SangsController;
import com.sangs.fwk.common.CommonServiceInvoker;
import com.sangs.lib.support.utils.SangsDateUtil;


@SangsController("/exceldown")
public class SangsCommonExcelDownController {

protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CommonServiceInvoker commonServiceInvoker;
	
	
	@ResponseBody
	@GetMapping("/{serviceId}/{methodId}")
	public ResponseEntity<byte[]>  getDataMethod(@RequestParam Map<String, Object> paramMap, @PathVariable("serviceId") String serviceId, @PathVariable("methodId") String methodId) throws Exception {
		return this.excelDownMethod(paramMap, serviceId, methodId);
	}
	
	@ResponseBody
	@PostMapping("/{serviceId}/{methodId}")
	public ResponseEntity<byte[]>  postDataMethod(@RequestBody Map<String, Object> paramMap, @PathVariable("serviceId") String serviceId, @PathVariable("methodId") String methodId) throws Exception {
		return this.excelDownMethod(paramMap, serviceId, methodId);
	}

	private ResponseEntity<byte[]> excelDownMethod(Map<String, Object> paramMap, String serviceId, String methodId) throws Exception {
		
		Workbook workbook = (Workbook)commonServiceInvoker.dataMethod(paramMap, serviceId, methodId);
		ByteArrayOutputStream os = null;
		try {
			
			String fileName = "";
			if(paramMap.containsKey("fileName") && paramMap.get("fileName") != null && !"".equals((String)paramMap.get("fileName")))
				fileName = (String)paramMap.get("fileName");
			else 
				fileName = serviceId.toLowerCase()+"_"+methodId.toLowerCase()+"_" + SangsDateUtil.getToday("yyyyMMddHHmmss") + ".xls";
			
			os = new ByteArrayOutputStream();
			workbook.write(os);
			
			return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+URLEncoder.encode(fileName, "utf-8")+"\"")
				.body(os.toByteArray());
			
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(os != null)
				os.close();
		}
			
	}
	
}
