package com.sangs.authmng.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.authmng.base.AuthMngServiceBase;
import com.sangs.authmng.support.AuthMngCommonDao;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.fwk.common.SangsConstants;
import com.sangs.fwk.support.SangsAuthUtil;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.domain.SangsPagingViewInfo;
import com.sangs.lib.support.exception.SangsCallServiceException;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsEncryptUtil;
import com.sangs.lib.support.utils.SangsStringUtil;

/**
 * 
 * @Method Name : AuthMngMngrService
 * @date : 2021. 11. 11
 * @author : ow.park
 * @history :
 * ----------------------------------------------------------------------------------
 * 변경일                        작성자                              변경내역
 * -------------- -------------- ----------------------------------------------------
 * 2021. 11. 11      ow.park              최초작성
 * ----------------------------------------------------------------------------------
 */

@SangsService
public class AuthMngMngrService extends AuthMngServiceBase{
	
	@Autowired
	private AuthMngCommonDao dao;
	
	// 관리자 목록 조회 
	public Map<String, Object> getCmmnMngrList(Map<String, Object> paramMap) throws SangsCallServiceException {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
		
			int pageNum = SangsStringUtil.nvlInt(paramMap.get("pageNum"), 1);
			// 전체 row 수 조회
			int totalCount = dao.selectCount("authMngMngr.selectMngrListCnt", paramMap);
			
			// 페이징 셋팅
			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);
			
			paramMap.put("startRow", pagingInfo.getStartRow());
			paramMap.put("endRow", pagingInfo.getEndRow());
		
			List<SangsMap> list = dao.selectList("authMngMngr.selectMngrList", paramMap);
			
			rtnMap.put("list", list);
			rtnMap.put("totalCount", totalCount);
			rtnMap.put("pagingInfo", pagingInfo);
			
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}
	
	// 관리자 상세 조회
	public Map<String, Object> getCmmnMngrInfo(Map<String, Object> paramMap) throws SangsCallServiceException {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
			SangsMap userInfo = dao.selectOne("authMngMngr.selectMngrInfo", paramMap);
			userInfo.putOrg("mngrLoginFailCtlDefaultCnt", SangsConstants.MNGR_LOGIN_FAIL_CTL_DEFAULT_CNT);
			
			rtnMap.put("userInfo", userInfo);
			
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}
	
	// 관리자 패스워드 변경
	public Map<String, Object> modMngrPasswordExecInfo(Map<String, Object> paramMap) throws SangsCallServiceException {
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
	
			
			String userId = (String) paramMap.get("userId");
			String userPassword = (String) paramMap.get("userPassword");
			
			// 단방향 암호화
			String encryptPass = SangsEncryptUtil.encryptPwd_SHA512(userPassword, userId);
			// 양방향 암호화
			//String encryptPass = SangsEncryptUtil.encrypt_AES128(userPassword);
			// 양방향 복호화 
			//String decryptPass = SangsEncryptUtil.decrypt_AES128(encryptPass);
			
			paramMap.put("encryptPass", encryptPass);
			
			dao.update("authMngMngr.updateUserLoginInfo", paramMap);
			
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
	
	// 관리자 등록
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveMngrExecInfo(Map<String, Object> paramMap) throws SangsCallServiceException {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			Map<String, Object> userInfo = (Map<String, Object>) paramMap.get("userInfo");
			
			userInfo.put("regUserId", SangsAuthUtil.getUserId());
			
			if("M".equals(paramMap.get("pmode"))) {
				
				// 사용자 데이터 수정 
				dao.update("authMngMngr.updateMngr", userInfo);
				
			} else {
				
				String userId = (String) userInfo.get("userId");
				String userPassword = (String) userInfo.get("userPassword");
				// 단방향 암호화
				String encryptPass = SangsEncryptUtil.encryptPwd_SHA512(userPassword, userId);
				
				userInfo.put("encryptPass", encryptPass);
				
				// 사용자 정보 등록
				dao.insert("authMngMngr.insertMngr", userInfo);
				
				// 사용자 권한 등록
				dao.insert("authMngAuth.insertMngrAuthorInfo", userInfo);
				
				// 사용자 로그인 정보 등록 
				dao.insert("authMngMngr.insertMngrLoginInfo", userInfo);
					
				userInfo.put("prjctSn", SangsAuthUtil.getUserAuthVo().getUserAttrMap().get("prjctSn"));
				// 사용자 프로젝트 정보 등록
				dao.insert("authMngMngr.insertMngrPrjctInfo", userInfo);
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
	
	// 관리자 아이디 체크
	public Map<String, Object> getMngrIdChkInfo(Map<String, Object> paramMap) throws SangsCallServiceException {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
			int mngrCnt = dao.selectCount("authMngMngr.selectMngrIdCnt", paramMap);
			rtnMap.put("mngrCnt", mngrCnt);
			
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			System.out.println(e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}
	
	// 관리자 패스워드 초기화
	public Map<String, Object> modMngrResetPasswordFailCntExec(Map<String, Object> paramMap) throws SangsCallServiceException {
			
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			dao.update("authMngMngr.updateLoginFailedReSetCnt", paramMap);
			
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
	
	// 관리자 패스워드 초기화
	public Map<String, Object> modMngrResetPasswordExec(Map<String, Object> paramMap) throws SangsCallServiceException {
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			String userId = (String) paramMap.get("userId");
			String userPassword = SangsConstants.MNGR_LOGIN_INIT_PWD;
			
			// 단방향 암호화
			String encryptPass = SangsEncryptUtil.encryptPwd_SHA512(userPassword, userId);
			// 양방향 암호화
			//String encryptPass = SangsEncryptUtil.encrypt_AES128(userPassword);
			// 양방향 복호화 
			//String decryptPass = SangsEncryptUtil.decrypt_AES128(encryptPass);
			paramMap.put("encryptPass", encryptPass);
			
			dao.update("authMngMngr.updateMngrLoginInfo", paramMap);
			
			rtnMap.put("resultCd", "OK");
			rtnMap.put("resetPassword", userPassword);
			
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
