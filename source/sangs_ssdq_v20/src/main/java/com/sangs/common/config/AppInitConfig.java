package com.sangs.common.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.sangs.common.support.CommonDao;
import com.sangs.fwk.common.SangsConstants;

@Configuration
public class AppInitConfig {

	@Autowired
	private CommonDao dao;
	
	@Value("${fwk.ip.access.ctrl.yn}")
	private String ipAccessYn;
	
	@Value("${fwk.dbms.type:}")
	private String dbmsType;
	
	@PostConstruct
	public void init() throws Exception {
		
		// 초기 패스워드 Setting 
		SangsConstants.MNGR_LOGIN_INIT_PWD = "DqPwd12#90";
		
		Map<String, Object> map = new HashMap<>();
		map.put("APP_DBMS_TYPE", dbmsType.toUpperCase());
		
		if (!"N".equals(ipAccessYn)){
			// 접근허용 IP 목록
			SangsConstants.CNTN_IP_LIST = dao.selectList("ipMng.selectAllCntnIpList", map);
		}
		
	}
	
}
