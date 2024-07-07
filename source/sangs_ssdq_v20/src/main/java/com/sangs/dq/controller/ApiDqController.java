package com.sangs.dq.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sangs.dq.service.ApiDqService;
import com.sangs.dq.service.ProfileExcService;

@Controller
public class ApiDqController {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ApiDqService apiDqService;

	@Autowired
	private ProfileExcService profileExcService;
	
	@ResponseBody
	@PostMapping("/api/dq/getProflList")
	public Map<String, Object> getProflList(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			Map<String, Object> resultMap = apiDqService.getApiProfileList(params);
			
			rtnMap.putAll(resultMap);
			
		} catch(Exception e) {
			e.printStackTrace();
			rtnMap.put("resultCd","FAIL");
			rtnMap.put("resultMsg",e.getMessage());
		}
		return rtnMap;
	}
	
	@ResponseBody
	@PostMapping("/api/dq/excProflInfo")
	public Map<String, Object> excProflInfo(@RequestBody Map<String, Object> params) throws Exception {
		 
		return this.excProflInfoProc(params);
	}
	
	public Map<String, Object> excProflInfoProc(Map<String, Object> params) {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
			boolean isDpcnYn = false;
			String tableNm = "";
			
			if (params.containsKey("dataCndList")) {
				List<Map<String, Object>> dataCndList = (List<Map<String, Object>>) params.get("dataCndList");
				List<String> tblNmList = new ArrayList<String>();
				
				for (Map<String, Object> map : dataCndList) {
					tblNmList.add(String.valueOf(map.get("tblNm")));
				}
				
				if (tblNmList.size() != tblNmList.stream().distinct().count()) {
					List<String> distinctList = tblNmList.stream().distinct().collect(Collectors.toList());
					
					for (String value : distinctList) {
						tblNmList.remove(value);
					}
					
					tableNm = String.valueOf(new HashSet<String>(tblNmList)); 
					
					isDpcnYn = true;
				}
			}
			
			if(!isDpcnYn) {
				if(!params.containsKey("regUserId")) {
					params.put("regUserId", "API");
				}
				
				Map<String, Object> connResultMap = apiDqService.getProfileDbmsCnncinfo(params);
				
				if((Boolean)connResultMap.get("result")) {
					
					params.put("THREAD_CONN_YN", "Y");
					params.put("THREAD_DBMS_INFO", connResultMap.get("dbmsCnncInfo"));
					params.put("isApiYn", "Y");
					
					Map<String, Object> resultMap = profileExcService.excProfileDgnssRqst(params);
					
					rtnMap.putAll(resultMap);
					
					if("OK".equals(String.valueOf(resultMap.get("resultCd")))) {
						rtnMap.put("resultCd","SUCCESS");
					} else {
						rtnMap.put("resultCd","FAIL");
					}
				} else {
					rtnMap.put("resultCd","FAIL");
				}
				
			} else {
				rtnMap.put("resultCd","FAIL");
				rtnMap.put("resultMsg", tableNm + " 테이블명 조건이 2개 이상입니다. ");
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			rtnMap.put("resultCd","FAIL");
			rtnMap.put("resultMsg",e.getMessage());
		}
		return rtnMap;
	}
	
	
	@ResponseBody
	@PostMapping("/api/dq/getProflExcResultDisMatchList")
	public Map<String, Object> getProflExcResultDisMatchList(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			Map<String, Object> resultMap = apiDqService.getProflExcResultDisMatchList(params);
			
			rtnMap.putAll(resultMap);
			
		} catch(Exception e) {
			e.printStackTrace();
			rtnMap.put("resultCd","FAIL");
			rtnMap.put("resultMsg",e.getMessage());
		}
		return rtnMap;
	}
}
