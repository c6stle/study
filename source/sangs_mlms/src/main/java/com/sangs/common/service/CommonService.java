package com.sangs.common.service;

import java.util.HashMap;
import java.util.Map;

import com.sangs.common.base.ServiceBase;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.fwk.support.SangsPropertyUtil;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsStringUtil;

@SangsService
public class CommonService extends ServiceBase {

	 
	 
	/**
	 * 메인 페이지 타입 반환 
	 *  - META, DQ 등 여러개의 시스템이 있을때 어떤 시스템 메인으로 보낼지 선택 하기 위해서 프로퍼티의 key 를 참조 한다. 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getMainDashboardType(Map<String, Object> paramMap) throws Exception {
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			String mainDashboardType = SangsPropertyUtil.getProperty("app.main.dashboard.type");
			if(SangsStringUtil.isEmpty(mainDashboardType))
				mainDashboardType = "DQ";
			
			mainDashboardType = mainDashboardType.toLowerCase();
			
			rtnMap.put("mainDashboardType", mainDashboardType);
			
		} catch(Exception e) {
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}
	 
	 
	
}
