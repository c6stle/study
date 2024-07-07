package com.sangs.authmng.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.authmng.base.AuthMngServiceBase;
import com.sangs.authmng.support.AuthMngCommonDao;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.fwk.support.SangsAuthUtil;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsCallServiceException;
import com.sangs.lib.support.exception.SangsMessageException;

/**
 * 
 * @Method Name : AuthMngAuthService
 * @date : 2021. 9. 30
 * @author : ow.park
 * @history :
 * ----------------------------------------------------------------------------------
 * 변경일                        작성자                              변경내역
 * -------------- -------------- ----------------------------------------------------
 * 2021. 9. 30      ow.park              최초작성
 * ----------------------------------------------------------------------------------
 */
@SangsService
public class AuthMngAuthService extends AuthMngServiceBase {
	
	@Autowired
	private AuthMngCommonDao dao;
	
	
	public Map<String, Object> getAuthorList(Map<String, Object> paramMap) throws SangsCallServiceException {
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			// 데이터 셋 목록 조회 
			List<SangsMap> list = dao.selectList("authMngAuth.selectAuthorList", paramMap);
			
			rtnMap.put("list", list);
			
		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}
	

	public Map<String, Object> getAuthorUserList(Map<String, Object> paramMap) throws SangsCallServiceException {
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			// 데이터 셋 목록 조회 
			List<SangsMap> list = dao.selectList("authMngAuth.selectAuthorUserList", paramMap);
			
			
			rtnMap.put("list", list);
			
		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}
	
	public Map<String, Object> getAuthorIemCodeList(Map<String, Object> paramMap) throws SangsCallServiceException {
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			List<SangsMap> list = dao.selectList("authMngAuth.selectAuthorIemCodeList", paramMap);
			
			rtnMap.put("list", list);
			
		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			logger.error("", e);
			throw new SangsMessageException("권한 항목 코드 조회 중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}
	 
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveAuthorIemExecInfo(Map<String, Object> paramMap) throws SangsCallServiceException {
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {

			List<SangsMap> authList = (List<SangsMap>)paramMap.get("authList");
			List<SangsMap> userAuthList = (List<SangsMap>)paramMap.get("userAuthList");
			
			Map<String, Object> delMap = new HashMap<String, Object>();
			
			// 선택된 권한코드 정보 일괄 삭제
			String authrtCd = (String) paramMap.get("authrtCd");
			String userId = (String) paramMap.get("userId");
			
			if("".equals(userId)) {
				delMap.put("authrtCd", authrtCd);
				// 메뉴 상세권한 삭제
				dao.delete("authMngAuth.deleteMenuAuthorDetailList", delMap);
				
				for(Map<String, Object> map : authList) {
					map.put("regUserId", SangsAuthUtil.getUserId());
					//map.put("dbmsType", paramMap.get("dbmsType"));
					// 메뉴 상세권한 등록
					dao.insert("authMngAuth.insertMenuAuthorDetailInfo", map);
				}
			} else {
				delMap.put("authrtCd", authrtCd);
				delMap.put("userId", userId);
				dao.delete("authMngAuth.deleteMenuUserAditAuthorList", delMap);
				
				for(Map<String, Object> map : userAuthList) {
					map.put("regUserId", SangsAuthUtil.getUserId());
					//map.put("dbmsType", paramMap.get("dbmsType"));
					dao.insert("authMngAuth.insertMenuUserAditAuthorInfo", map);
				}
			}
			
			rtnMap.put("resultCd", "OK");
			
		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		 
		return rtnMap;
	}

}
