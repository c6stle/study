package com.sangs.fwk.base;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sangs.fwk.common.SangsConstants;

@Component
public class SagnsInitSetting {
	
	//@Value("${spring.DbType:}")
	//private String dbType;
	
	@PostConstruct
	public void init() {
		// FWK_DBMS_TYPE 초기 셋팅 
		//System.out.println("####### dbType : -->" + dbType);
		//SangsConstants.FWK_DBMS_TYPE = dbType;
	}	

	//public String getDbType() {
	//	return dbType;
	//}

}