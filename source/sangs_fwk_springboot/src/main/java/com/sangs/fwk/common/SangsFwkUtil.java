package com.sangs.fwk.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sangs.fwk.support.SangsAuthUtil;
import com.sangs.lib.support.utils.SangsStringUtil;

public class SangsFwkUtil {

	
	
	protected static Logger logger = LoggerFactory.getLogger(SangsFwkUtil.class);
	
	public static boolean isNoLoginUrl(String url) {
		
		String noLoginUrls = SangsConstants.AUTH_CHECK_IGNORE_URLS;
		
		logger.debug("## url :" + url);
		logger.debug("## noLoginUrls :" + noLoginUrls);
		System.out.println("## url :" + url);
		System.out.println("## noLoginUrls :" + noLoginUrls);
		
		
		if("".equals(noLoginUrls))
			return false;
		
		String turl = url;
		
		if(turl.indexOf("?") > 0)
			turl = turl.substring(0, url.indexOf("?"));
		
		if(turl.charAt(turl.length() - 1) != '/') {
			turl = turl + "/";
		}
		
		String[] arrNoLoginUrls = noLoginUrls.split(",");
		
		for(String str : arrNoLoginUrls) {
			String noLoginUrl = str.trim();
			
			if(!SangsStringUtil.isEmpty(noLoginUrl)) {
				if(noLoginUrl.charAt(noLoginUrl.length() - 1) != '/') {
					noLoginUrl = noLoginUrl + "/";
				}
	
				if(turl.startsWith(noLoginUrl))
					return true;
			}
		}  
		return false;
		
	}
	

	
}
