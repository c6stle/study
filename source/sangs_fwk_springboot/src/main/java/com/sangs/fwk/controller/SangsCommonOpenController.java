package com.sangs.fwk.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sangs.fwk.annotation.SangsController;
import com.sangs.fwk.support.SangsAuthUtil;
import com.sangs.lib.support.exception.SangsNoneAuthException;
import com.sangs.lib.support.utils.SangsDateUtil;
import com.sangs.lib.support.utils.SangsStringUtil;

@SangsController("/open")
public class SangsCommonOpenController {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	
	@GetMapping("/{d1}/{d2}")
	public String getViewMethod(HttpServletRequest request, Model model, @RequestParam Map<String, String> paramMap, @PathVariable("d1") String d1, @PathVariable("d2") String d2) throws Exception {
		return this.viewMethod(request, model, paramMap, d1, d2, null, null);
	}
	
	@PostMapping("/{d1}/{d2}")
	public String postViewMethod(HttpServletRequest request,Model model,@RequestParam Map<String, String> paramMap, @PathVariable("d1") String d1, @PathVariable("d2") String d2) throws Exception {
		return this.viewMethod(request, model, paramMap, d1, d2, null, null);
	}
	
	
	@GetMapping("/{d1}/{d2}/{d3}")
	public String getViewMethod(HttpServletRequest request,Model model, @RequestParam Map<String, String> paramMap, @PathVariable("d1") String d1, @PathVariable("d2") String d2, @PathVariable("d3") String d3) throws Exception {
		return this.viewMethod(request, model, paramMap, d1, d2, d3, null);
	}
	
	@PostMapping("/{d1}/{d2}/{d3}")
	public String postViewMethod(HttpServletRequest request, Model model,@RequestParam Map<String, String> paramMap, @PathVariable("d1") String d1, @PathVariable("d2") String d2, @PathVariable("d3") String d3) throws Exception {
		return this.viewMethod(request, model, paramMap, d1, d2, d3, null);
	}
	
	
	@GetMapping("/{d1}/{d2}/{d3}/{d4}")
	public String getViewMethod(HttpServletRequest request,Model model, @RequestParam Map<String, String> paramMap
			, @PathVariable("d1") String d1, @PathVariable("d2") String d2, @PathVariable("d3") String d3, @PathVariable("d4") String d4) throws Exception {
		return this.viewMethod(request, model, paramMap, d1, d2, d3, d4);
	}
	
	@PostMapping("/{d1}/{d2}/{d3}/{d4}")
	public String postViewMethod(HttpServletRequest request, Model model,@RequestParam Map<String, String> paramMap
			, @PathVariable("d1") String d1, @PathVariable("d2") String d2, @PathVariable("d3") String d3, @PathVariable("d4") String d4) throws Exception {
		return this.viewMethod(request, model, paramMap, d1, d2, d3, d4);
	}
	
	
	
	public String viewMethod(HttpServletRequest request, Model model, Map<String, String> paramMap, String d1, String d2, String d3, String d4) throws Exception {
		
		String reqUri = request.getRequestURI();
		
		if(SangsAuthUtil.isRegSessionMenu) {	// 메뉴정보가 세셧에 등록이 되어 있을때
			if(!SangsAuthUtil.isAccessibleAuthMenuUrl(reqUri)) {
				logger.error("접근권한이 없습니다. {} " , reqUri);
				throw new SangsNoneAuthException("접근권한이 없습니다. [" + reqUri +"]");
			} 
		}
		
		
		
		model.addAttribute("sangsStringUtil", new SangsStringUtil());
		model.addAttribute("sangsDateUtil", new SangsDateUtil());
		model.addAttribute("paramMap", paramMap);
		
		if(!SangsStringUtil.isEmpty(d4))
			return d1 + "/" + d2 + "/" + d3 + "/" + d4;
		else if(!SangsStringUtil.isEmpty(d3))
			return d1 + "/" + d2 + "/" + d3;
		else if(!SangsStringUtil.isEmpty(d2))
			return d1 + "/" + d2;
		else 
			return d1;
		
	}
	
	
	

	
	
}
