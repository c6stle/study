package com.sangs.common.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sangs.common.support.AuthUtil;
import com.sangs.lib.support.exception.SangsMessageException;


@Controller
public class IndexController {
	
		
	@RequestMapping(value="/")
	public String index()  {
		return "redirect:/index.html";
	}
	
	
	@RequestMapping(value="/abc")
	public String index2(HttpServletRequest request) throws Exception {
		
		if(!AuthUtil.isAccessibleAuthMenuUrl(request))
			throw new SangsMessageException("접근 권한이 없습니다.");
		
		System.out.println("#####   abc ");
		return "redirect:/index.html";
	}
	

}
