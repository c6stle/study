package com.sangs.authmng.base;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sangs.fwk.common.SangsConstants;

/**
 * 
 * @Method Name : AuthMngServiceBase
 * @date : 2021. 10. 1
 * @author : ow.park
 * @history :
 * ----------------------------------------------------------------------------------
 * 변경일                        작성자                              변경내역
 * 2021. 10. 1      ow.park              최초작성
 * -------------- -------------- ----------------------------------------------------
 * ----------------------------------------------------------------------------------
 */
public abstract class AuthMngServiceBase {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/*
	@SuppressWarnings("unchecked")
	protected void putParamMap(Map<String, Object> paramMap) {
		// 비즈니스 로직 DBMS Type을 paramMap에 추가 
		
		String dbmsType = (SangsConstants.FWK_DBMS_TYPE).toUpperCase();
		System.out.println(SangsConstants.FWK_DBMS_TYPE);
		System.out.println("###### AuthMngServiceBase.dbmsType : =="+ dbmsType);
		
		if(!dbmsType.equals("MARIADB") && !dbmsType.equals("MYSQL")) {
			Map<String, Object> sessUserInfo = (Map<String, Object>) paramMap.get("SESS_USER_INFO");
			paramMap.putAll(sessUserInfo);
		}
		paramMap.put("dbmsType", dbmsType);
		//paramMap.put("limitTypeDbms", getLimitTypeDbms());
		
	}
	
	public boolean getLimitTypeDbms() {
		boolean limitTypeDbms = false;
		
		for(SangsConstants.LIMIT_TYPE_DBMS name : SangsConstants.LIMIT_TYPE_DBMS.values()) {
			if(SangsConstants.FWK_DBMS_TYPE.equals(name.toString())) {
				limitTypeDbms = true;
			}
		}
		return limitTypeDbms;
	}
	*/
 
}


