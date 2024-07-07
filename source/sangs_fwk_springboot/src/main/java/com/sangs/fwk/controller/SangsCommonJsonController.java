package com.sangs.fwk.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sangs.fwk.annotation.SangsController;
import com.sangs.fwk.common.CommonServiceInvoker;
import com.sangs.fwk.common.SangsConstants;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsStringUtil;


@SangsController("/json")
public class SangsCommonJsonController {
	
	@Autowired
	private CommonServiceInvoker commonServiceInvoker;
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@ResponseBody
	@GetMapping("/{serviceId}/{methodId}")
	public Object getDataMethod(@RequestParam Map<String, Object> paramMap, @PathVariable("serviceId") String serviceId, @PathVariable("methodId") String methodId) throws Exception {
		return this.dataMethod(paramMap, serviceId, methodId); 
	}
	
	
	@ResponseBody
	@PostMapping("/{serviceId}/{methodId}")
	public Object postDataMethod(@RequestBody Map<String, Object> paramMap, @PathVariable("serviceId") String serviceId, @PathVariable("methodId") String methodId) throws Exception {
		return this.dataMethod(paramMap, serviceId, methodId);
	}
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object dataMethod(Map<String, Object> paramMap, String serviceId, String methodId) throws Exception {
		logger.debug("CommonDataController post access path : /{}/{} --> call service : {}.{}", serviceId, methodId, serviceId.substring(0, 1).toUpperCase() + serviceId.substring(1, serviceId.length()) + "Service" , methodId );
		Object obj = commonServiceInvoker.dataMethod(paramMap, serviceId, methodId);
		if(obj instanceof Map) {
			if(((Map<String, Object>)obj).containsKey(SangsConstants.FWK_ERROR_MESSAGE_KEY) && !SangsStringUtil.isEmpty(((Map)obj).get(SangsConstants.FWK_ERROR_MESSAGE_KEY)))
				throw new SangsMessageException((String)((Map)obj).get(SangsConstants.FWK_ERROR_MESSAGE_KEY));
			
		}
		return obj;
	}
	
	
	
}
