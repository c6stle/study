package com.sangs.fwk.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sangs.fwk.annotation.SangsController;
import com.sangs.fwk.common.CommonServiceInvoker;
import com.sangs.fwk.common.SangsConstants;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsStringUtil;


@SangsController("/view")
public class SangsCommonViewController {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CommonServiceInvoker commonServiceInvoker;
	
	
	@GetMapping("/{d1}/{d2}")
	public String getViewMethod(Model model, @RequestParam Map<String, String> paramMap, @PathVariable("d1") String d1, @PathVariable("d2") String d2) throws Exception {
		logger.debug("CommonViewController get access path : /{}/{}", d1, d2);
		return this.viewMethod(model, paramMap, d1, d2);
	}
	
	
	@PostMapping("/{d1}/{d2}")
	public String postViewMethod(Model model,@RequestParam Map<String, String> paramMap, @PathVariable("d1") String d1, @PathVariable("d2") String d2) throws Exception {
		logger.debug("CommonViewController post access path : /{}/{}", d1, d2);
		return this.viewMethod(model, paramMap, d1, d2);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String viewMethod(Model model, Map<String, String> paramMap, String d1, String d2) throws Exception {
		
		String forwardView = d1 + "/" + d2;
		
		Object rtnObj= (Object)commonServiceInvoker.dataMethod(paramMap, d1, d2);
		
		if(rtnObj instanceof java.util.List) {
			
			model.addAttribute("list", rtnObj);
		} else if(rtnObj instanceof java.util.Map) {
			
			if(((Map)rtnObj).containsKey(SangsConstants.FWK_ERROR_MESSAGE_KEY) && !SangsStringUtil.isEmpty(((Map)rtnObj).get(SangsConstants.FWK_ERROR_MESSAGE_KEY)))
				throw new SangsMessageException((String)((Map)rtnObj).get(SangsConstants.FWK_ERROR_MESSAGE_KEY));
			
			Map rtnMap = (Map)rtnObj;
			model.addAllAttributes((Map)rtnObj);
			if(rtnMap.containsKey(SangsConstants.FORWARD_VIEW))
				forwardView = (String)rtnMap.get(SangsConstants.FORWARD_VIEW);
		}
		
		model.addAttribute("paramMap", paramMap);
		
		return forwardView;
	}
	
	
	
	
}
