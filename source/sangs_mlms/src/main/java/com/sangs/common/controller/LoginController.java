package com.sangs.common.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sangs.authmng.menu.AuthMngMenuService;
import com.sangs.common.base.ControllerBase;
import com.sangs.common.service.LoginService;
import com.sangs.common.service.ProjectService;
import com.sangs.common.support.AuthUtil;
import com.sangs.fwk.annotation.SangsController;
import com.sangs.fwk.common.SangsConstants;
import com.sangs.lib.support.domain.SangsAuthVo;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsEncryptUtil;
import com.sangs.lib.support.utils.SangsStringUtil;

@SangsController("/login")
public class LoginController extends ControllerBase {

	
	@Value("${fwk.login.page:}")
	private String loginPage;
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private ProjectService projectService;
	 
	@Autowired
	private AuthMngMenuService authMngMenuService;
	
	@RequestMapping("/login")
	public String login(Model model) {
		
		model.addAttribute("loginPage", loginPage);
		
		// 세션 제거
		removeSession();
		return "login/login";
		
	} 
	

 
	/**
	 * 로그인 처리 전 로그인 체크
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping("/checkLogin")
	public Map<String, Object> checkLogin(@RequestBody Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		String resultCd = "";
		String resultMsg = "";
		try {
			
			// 로그인 정보 확인
			Map<String, Object> userCheckMap = this.checkLoginProc(String.valueOf(paramMap.get("userId")), String.valueOf(paramMap.get("pwd")));
			resultCd = String.valueOf(userCheckMap.get("resultCd"));
			resultMsg = String.valueOf(userCheckMap.get("resultMsg"));

			if("OK".equals(resultCd)) {
				rtnMap.put("list", userCheckMap.get("userProjectList"));
			} else if("EXCEPTION".equals(resultCd)) {
				resultMsg = "처리중 에러가 발생하였습니다. ";
			}
		 
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("", e);
			resultCd = "EXCEPTION";
			resultMsg = "처리중 에러가 발생하였습니다. ";
		}
		rtnMap.put("resultCd", resultCd);
		rtnMap.put("resultMsg", resultMsg);
		
		return rtnMap;
	}
	
	

	/**
	 * 초기 패스워드 변경 시 DB 패스워드 체크
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping("/checkDbPassword")
	public Map<String, Object> checkDbPassword(@RequestBody Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		boolean isPassChk = false;
		try {
			Map<String, Object> searchMap = new HashMap<String, Object>();
			searchMap.put("userId", paramMap.get("userId"));
			searchMap.put("userPassword", paramMap.get("userPwd"));
			
			// 로그인 정보 조회 
			SangsMap userInfoMap = loginService.getLoginUserInfo(searchMap);
			
			// SHA-512 암호화 
			String encryptPass = SangsEncryptUtil.encryptPwd_SHA512((String)paramMap.get("userPwd"), (String)paramMap.get("userId"));
			
			String dbPass = (String) userInfoMap.get("userPassword");
			
			if(dbPass.equals(encryptPass)) {
				isPassChk = true;
			}

		} catch(Exception e) {
			e.printStackTrace();
			logger.error("", e);
		}
		
		rtnMap.put("isPassChk", isPassChk);
		
		return rtnMap;
	}
	
	
	/**
	 * 패스워드 변경
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping("/modPasswordInfo")
	public Map<String, Object> modPasswordInfo(@RequestBody Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			// SHA-512 암호화 
			String encryptPass = SangsEncryptUtil.encryptPwd_SHA512((String)paramMap.get("userPwd"), (String)paramMap.get("userId"));
			paramMap.put("encryptPass", encryptPass);
			
			loginService.modPasswordInfo(paramMap);
			
			loginService.modLoginFailedReSetCnt(paramMap);
			

		} catch(Exception e) {
			e.printStackTrace();
			logger.error("", e);
		}
		
		rtnMap.put("resultCd", "OK");
		
		return rtnMap;
	}
	

	/**
	 * 로그인 처리
	 * @param model
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/loginProc")
	public String loginProc(Model model, @RequestParam Map<String, Object> paramMap) throws Exception {
		try {
			
			// 로그인 정보 확인
			Map<String, Object> userCheckMap = this.checkLoginProc(String.valueOf(paramMap.get("userId")), String.valueOf(paramMap.get("pwd")));
			String resultCd = String.valueOf(userCheckMap.get("resultCd"));
			String resultMsg = String.valueOf(userCheckMap.get("resultMsg"));
			 
			
			if("NO_INFO".equals(resultCd)) {
				// 사용자 정보가 존재 하지 않습니다.
				model.addAttribute("msg", resultMsg);
				model.addAttribute("url", loginPage);
				return "cmmn/msg_forward";
				
			} else if("NO_PROJECT".equals(resultCd)) {
				
				model.addAttribute("msg", resultMsg);
				model.addAttribute("url", loginPage);
				return "cmmn/msg_forward";
			} else if("DEFAULT_PASSWORD_LOGIN".equals(resultCd)) {
				
				model.addAttribute("resultCd", resultCd);
				model.addAttribute("url", loginPage);
				return "cmmn/msg_forward";
			}
			
			if(SangsStringUtil.isEmpty(paramMap.get("prjctSn"))) {
				logger.error("프로젝트 순번이 파라미터로 입력되지 않았습니다. ");
				throw new SangsMessageException("로그인 처리중 에러가 발생하였습니다.");
			}
			
			String prjctSn = (String)paramMap.get("prjctSn");
			SangsMap userInfoMap = (SangsMap)userCheckMap.get("userInfoMap");
			
			
			// 로그인 사용자 프로젝트 정보 조회    
			Map<String, Object> searchMap = new HashMap<String, Object>();
			searchMap.put("userId", paramMap.get("userId"));
			searchMap.put("prjctSn", prjctSn);
			Map<String, Object> userProjectMap = loginService.getLoginUserProjectInfo(searchMap);
			SangsMap userProjectInfo = (SangsMap)userProjectMap.get("info");
			
			// 로그인 실패 횟수 초기화 
			loginService.modLoginFailedReSetCnt(searchMap);
			
			
			String stdSetSn = SangsStringUtil.nvl(userProjectInfo.get("stdSetSn"));
			String stdSetNm = SangsStringUtil.nvl(userProjectInfo.get("stdSetNm"));
			String prjctNm = SangsStringUtil.nvl(userProjectInfo.get("prjctNm"));
			
			
			Map<String, String> userAttrInfo = new HashMap<String, String>();
			userAttrInfo.put("prjctSn", prjctSn);
			userAttrInfo.put("prjctNm", prjctNm);
			
			userAttrInfo.put("stdSetSn", stdSetSn);
			userAttrInfo.put("stdSetNm", stdSetNm);

			userAttrInfo.put("regUserId", userInfoMap.getString("userId"));
			userAttrInfo.put("authrtCd", userInfoMap.getString("authrtCd"));
			userAttrInfo.put("isApprover", userInfoMap.getString("isApprover"));	// 승인자 여부
			
			SangsAuthVo authVo = new SangsAuthVo();
			authVo.setUserId(userInfoMap.getString("userId"));
			authVo.setUserNm(userInfoMap.getString("userNm"));
			authVo.setUserAttrMap(userAttrInfo); 
			AuthUtil.setUserAuthVo(authVo);
			
			// 세션에 메뉴정보 셋팅
			authMngMenuService.setMngrAccessibleMenuList();
			
			
		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			logger.error("", e);
			throw new SangsMessageException("로그인 처리중 에러가 발생하였습니다.");
		}
		model.addAttribute("url", "/open/main/main");
		
		return "cmmn/msg_forward";
		
	}
	
	
	/**
	 * 로그인정보 조회 
	 * @param userId
	 * @param userPwd
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> checkLoginProc(String userId, String userPwd) {
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		boolean isPassChk = false;
		boolean isDefPassChk = false;
		String resultCd = "";
		String resultMsg = "";
		
		
		try {
			Map<String, Object> searchMap = new HashMap<String, Object>();
			searchMap.put("userId", userId);
			searchMap.put("userPassword", userPwd);
			
			// SHA-512 암호화 
			String encryptPass = SangsEncryptUtil.encryptPwd_SHA512(userPwd, userId);
			
			// 초기화 암호 암호화 
			String defaultEncryptPass = SangsEncryptUtil.encryptPwd_SHA512(SangsConstants.MNGR_LOGIN_INIT_PWD, userId);
			
			// 로그인 정보 조회 
			SangsMap userInfoMap = loginService.getLoginUserInfo(searchMap);
			
			if(userInfoMap == null || !userInfoMap.containsKey("userId") || "".equals(userInfoMap.get("userId"))) {
				// 로그인 
				resultCd = "NO_INFO";
				resultMsg = "사용자 정보가 존재 하지 않습니다.";
			} else {
			
				String dbPass = (String) userInfoMap.get("userPassword");
				
				if(dbPass.equals(encryptPass)) {
					isPassChk = true;
				}
				
				if(isPassChk) {
					
					// 초기화 암호 사용하여 로그인 시
					if(dbPass.equals(defaultEncryptPass)) {
						isDefPassChk = true;
					}
					// 초기화 암호 로그인 체크
					if(isDefPassChk) {
						
						resultCd = "DEFAULT_PASSWORD_LOGIN";
						resultMsg = "초기 패스워드를 변경 후 로그인 하시기 바랍니다.";
						
					} else {
						// 사용자 정보 set
						rtnMap.put("userInfoMap", userInfoMap);
						
						// 사용자의 프로젝트 목록 조회 
						Map<String, Object> userProjectMap = projectService.getUserProjectList(searchMap);
						List<SangsMap> userProjectList = (List<SangsMap>)userProjectMap.get("list");
						
						if(userProjectList == null || userProjectList.size() == 0) {
							resultCd = "NO_PROJECT";
							resultMsg = "프로젝트에 속하지 않은 사용자 입니다.";
						} else if ((int)userInfoMap.get("lgnFailrCnt") >= SangsConstants.MNGR_LOGIN_FAIL_CTL_DEFAULT_CNT){
							
							resultCd = "MNGR_LOGIN_FAIL_CTL_DEFAULT_CNT";
							resultMsg = "로그인 실패 횟수가 초과 되었습니다. \n실패횟수 : "+userInfoMap.get("lgnFailrCnt") + "회"+ "\n제한 : " + SangsConstants.MNGR_LOGIN_FAIL_CTL_DEFAULT_CNT+" 회"; 
							
						} else {
							// 사용자 프로젝트 정보 set
							rtnMap.put("userProjectList", userProjectList);
							
							resultCd = "OK";
							resultMsg = "정상 회원입니다. "; 
						}
					}
					
				} else {
					// 로그인 실패 시 카운터 증가
					loginService.modLoginFailedCnt(searchMap);
					userInfoMap = loginService.getLoginUserInfo(searchMap);
					
					resultCd = "NO_PASSWORD";
					resultMsg = "패스워드가 일치하지 않습니다.\n"+SangsConstants.MNGR_LOGIN_FAIL_CTL_DEFAULT_CNT+" 회 이상 실패 시 로그인이 제한됩니다. ("+userInfoMap.get("lgnFailrCnt") + "회)";
					
				}
			}

		} catch(Exception e) {
			logger.error("", e);
			resultCd = "EXCEPTION";
			resultMsg = "처리중 에러가 발생하였습니다. ";
		}
		
		rtnMap.put("resultCd", resultCd);
		rtnMap.put("resultMsg", resultMsg);
		
		return rtnMap;
	}
	
	@RequestMapping("/logoutProc")
	public String logoutProc(Model model) {

		// 세션제거 
		removeSession();
		
		model.addAttribute("msg", "로그아웃 되었습니다.");
		model.addAttribute("url", loginPage);
		return "cmmn/msg_forward";
	} 

	
	// 세션 제거 
	private void removeSession() {
		HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		req.getSession().invalidate();
		req.getSession(true);
	}
	
}
