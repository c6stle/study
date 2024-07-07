package com.sangs.dq.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sangs.dq.service.ProfileMngService;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;

@Controller
public class ProfileApiController {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ProfileMngService profileMngService;
	
	@ResponseBody
	@RequestMapping("/dq/profileApi/callRealTime")
	public Map<String, Object> callRealTime(@RequestParam Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		String resultCd = "";
		
		try {
			
			Map<String, Object> map = profileMngService.getProfileList(rtnMap);
			
			List<SangsMap> proflList = (List<SangsMap>) map.get("profileList");
			
			List<Map<String, String>> proflJsonList = new ArrayList<Map<String, String>>();
			for (SangsMap proflMap: proflList) {
				Map<String, String> jsonMap = new HashMap<String, String>();
				
				JSONObject proflJson = new JSONObject(proflMap); 
				
				String proflJsonToString = proflJson.toString();
				
				jsonMap.put("proflJson", proflJsonToString);
				
				proflJsonList.add(jsonMap);
			}
			
			rtnMap.put("proflJsonList", proflJsonList);
			
			resultCd = "OK";
			
		} catch (Exception e) {
			logger.error("", e);
			resultCd = "FAIL";
			throw new SangsMessageException(e);
		}
		
		rtnMap.put("resultCd", resultCd);
		return rtnMap;
	}
	
	
	@ResponseBody
	@RequestMapping("/dq/profileApi/callWeb")
	public Map<String, Object> callWeb(@RequestParam Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		String resultCd = "";
		
		try {
			
			Map<String, Object> map = profileMngService.getDgnssResultList(rtnMap);
			
			List<SangsMap> proflList = (List<SangsMap>) map.get("dgnssResultList");
			
			List<Map<String, String>> proflJsonList = new ArrayList<Map<String, String>>();
			for (SangsMap proflMap: proflList) {
				Map<String, String> jsonMap = new HashMap<String, String>();
				
				JSONObject proflJson = new JSONObject(proflMap); 
				
				String proflJsonToString = proflJson.toString();
				
				jsonMap.put("proflJson", proflJsonToString);			
				proflJsonList.add(jsonMap);
			}
			
			rtnMap.put("proflJsonList", proflJsonList);
			
			resultCd = "OK";
			
		} catch (Exception e) {
			logger.error("", e);
			resultCd = "FAIL";
			throw new SangsMessageException(e);
		}
		
		rtnMap.put("resultCd", resultCd);
		return rtnMap;
	}
	
	
	
	
}
