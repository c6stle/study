package com.sangs.common.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.support.CommonDao;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;

@SangsService
public class LoginService extends ServiceBase {

	
	@Autowired
	private CommonDao dao;
	
	public SangsMap getLoginUserInfo(Map<String, Object> paramMap) throws Exception {
		return dao.selectOne("cmmn_login_user.selectLoginUserInfo", paramMap);
	}
	 
	/**
	 *  로그인 사용자의 프로젝트 정보 조회 
	 * @param paramMap 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getLoginUserProjectInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			SangsMap map = dao.selectOne("cmmn_login_user.selectLoginUserProjectInfo", paramMap);
			rtnMap.put("info", map);
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	
	}

	public void modLoginFailedCnt(Map<String, Object> searchMap) throws Exception {
		dao.update("cmmn_login_user.updateLoginFailedCnt", searchMap);
	}
	
	public void modLoginFailedReSetCnt(Map<String, Object> searchMap) throws Exception {
		dao.update("authMngMngr.updateLoginFailedReSetCnt", searchMap);
	}
	
	public void modPasswordInfo(Map<String, Object> paramMap) throws Exception {
		dao.update("cmmn_login_user.updatePasswordInfo", paramMap);
	}
	
	
}
