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
 * @Method Name : AuthMngUserService
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
public class AuthMngUserService extends AuthMngServiceBase{
	
	@Autowired
	private AuthMngCommonDao dao;
	
	public Map<String, Object> getCmmnUserList(Map<String, Object> paramMap) throws SangsCallServiceException {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
		
			int pageNum = SangsStringUtil.nvlInt(paramMap.get("pageNum"), 1);
			// 전체 row 수 조회
			int totalCount = dao.selectCount("authMngUser.selectUserListCnt", paramMap);
			
			// 페이징 셋팅
			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);
			
			paramMap.put("startRow", pagingInfo.getStartRow());
			paramMap.put("endRow", pagingInfo.getEndRow());
		
			List<SangsMap> list = dao.selectList("authMngUser.selectUserList", paramMap);
			
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
	
	
	public Map<String, Object> getCmmnUserInfo(Map<String, Object> paramMap) throws SangsCallServiceException {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
						
			SangsMap userInfo = dao.selectOne("authMngUser.selectUserInfo", paramMap);
			List<SangsMap> userAttrbList = dao.selectList("authMngUser.selectUserInfoAttrbList", paramMap);
			List<SangsMap> userAttrbDtlList = dao.selectList("authMngUser.selectUserInfoAttrbDtlList", paramMap);
			List<SangsMap> userAttrbDataList = dao.selectList("authMngUser.selectUserInfoAttrbDataList", paramMap);
			
			rtnMap.put("userInfo", userInfo);
			rtnMap.put("userAttrbList", userAttrbList);
			rtnMap.put("userAttrbDtlList", userAttrbDtlList);
			rtnMap.put("userAttrbDataList", userAttrbDataList);
			
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveUserExecInfo(Map<String, Object> paramMap) throws SangsCallServiceException {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			Map<String, Object> userInfo = (Map<String, Object>) paramMap.get("userInfo");
			
			//userInfo.put("dbmsType", paramMap.get("dbmsType"));
			userInfo.put("regUserId", SangsAuthUtil.getUserId());
			
			if("M".equals(paramMap.get("pmode"))) {
				
				List<SangsMap> userInfoList = (List<SangsMap>)paramMap.get("userInfoList");
				
				// 사용자 데이터 수정 
				dao.update("authMngUser.updateUser", userInfo);
				
				// 사용자 속성 데이터 유무 확인 
				int infoCount = dao.selectCount("authMngUser.selectUserInfoCnt", userInfo);
				
				if(infoCount >= 0) {
					// 사용자 정보 삭제 (CMMN_USER_INFO)
					dao.delete("authMngUser.deleteUserInfo", userInfo);
					for(Map<String, Object> map : userInfoList) {
						dao.insert("authMngUser.insertUserInfo", map);
					}
				} else {
					for(Map<String, Object> map : userInfoList) {
						dao.insert("authMngUser.insertUserInfo", map);
					}
				}
			} else {
				
				String userId = (String) userInfo.get("userId");
				String userPassword = (String) userInfo.get("userPassword");
				// 단방향 암호화
				String encryptPass = SangsEncryptUtil.encryptPwd_SHA512(userPassword, userId);
				
				userInfo.put("encryptPass", encryptPass);
				
				// 사용자 정보 등록
				dao.insert("authMngUser.insertUser", userInfo);
				
				// 사용자 권한 등록
				//dao.insert("authMngAuth.insertUserAuthorInfo", userInfo);
				
				// 사용자 로그인 정보 등록 
				dao.insert("authMngUser.insertUserLoginInfo", userInfo);
					
				//userInfo.put("prjctSn", paramMap.get("prjctSn"));
				// 사용자 프로젝트 정보 등록
				//dao.insert("authMngUser.insertUserPrjctInfo", userInfo);
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
	
	public Map<String, Object> getCmmnUserAttrbList(Map<String, Object> paramMap) throws SangsCallServiceException {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
		

			int pageNum = SangsStringUtil.nvlInt(paramMap.get("pageNum"), 1);
			// 전체 row 수 조회
			int totalCount = dao.selectCount("authMngUser.selectUserAttrbListCnt", paramMap);
			
			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);
			
			paramMap.put("startRow", pagingInfo.getStartRow());
			paramMap.put("endRow", pagingInfo.getEndRow());
			
			List<SangsMap> list = dao.selectList("authMngUser.selectUserAttrbList", paramMap);
			
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
	
	public Map<String, Object> getUserAttrbDtlList(Map<String, Object> paramMap) throws SangsCallServiceException {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
		 

			List<SangsMap> list = dao.selectList("authMngUser.selectUserAttrbDtlList", paramMap);
			
			rtnMap.put("list", list);
			
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}
	
	public Map<String, Object> getUserAttrbDtlCodeChkInfo(Map<String, Object> paramMap) throws SangsCallServiceException {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
			
			int codeCount = dao.selectCount("authMngUser.selectUserAttrbDtlCodeCnt", paramMap);
			
			rtnMap.put("codeCount", codeCount);
			
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveUserAttrbExecInfo(Map<String, Object> paramMap) throws SangsCallServiceException {
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			Map<String, Object> info = (Map<String, Object>)paramMap.get("info");
			
			info.put("regUserId", SangsAuthUtil.getUserId());
			//info.put("dbmsType", paramMap.get("dbmsType"));
			
			if("C".equals(paramMap.get("pmode"))) {
				int userAttrbSn = dao.selectInteger("authMngUser.selectNextUserAttrbSn", paramMap);
				info.put("userAttrbSn", userAttrbSn);
				dao.insert("authMngUser.insertUserAttrbInfo", info);
			} else if("M".equals(paramMap.get("pmode"))) {
				dao.update("authMngUser.updateUserAttrbInfo", info);
			} else if("D".equals(paramMap.get("pmode"))) {
				dao.delete("authMngUser.deleteUserAttrbInfo", info);
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
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveUserAttrbDtlExecInfo(Map<String, Object> paramMap) throws SangsCallServiceException {
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			List<SangsMap> insList = (List<SangsMap>)paramMap.get("insList");
			List<SangsMap> updList = (List<SangsMap>)paramMap.get("updList");
			List<SangsMap> delList = (List<SangsMap>)paramMap.get("delList");
			
			if(insList.size() > 0) {
				for(Map<String, Object> map : insList) {
					map.put("regUserId", SangsAuthUtil.getUserId());
					//map.put("dbmsType", paramMap.get("dbmsType"));
					dao.insert("authMngUser.insertUserAttrbDtlInfo", map);
				}
			}
			if(updList.size() > 0) {
				for(Map<String, Object> map : updList) {
					map.put("regUserId", SangsAuthUtil.getUserId());
					//map.put("dbmsType", paramMap.get("dbmsType"));
					dao.update("authMngUser.updateUserAttrbDtlInfo", map);
				}
			}
			if(delList.size() > 0) {
				for(Map<String, Object> map : delList) {
					map.put("regUserId", SangsAuthUtil.getUserId());
					//map.put("dbmsType", paramMap.get("dbmsType"));
					dao.delete("authMngUser.deleteUserAttrbDtlInfo", map);
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
	
	public Map<String, Object> modUserPasswordExecInfo(Map<String, Object> paramMap) throws SangsCallServiceException {
		
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
			
			dao.update("authMngUser.updateUserLoginInfo", paramMap);
			
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
	
	public Map<String, Object> getUserIdChkInfo(Map<String, Object> paramMap) throws SangsCallServiceException {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
			int codeCount = dao.selectCount("authMngUser.selectUserIdCnt", paramMap);
			rtnMap.put("codeCount", codeCount);
			
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			System.out.println(e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}
}
