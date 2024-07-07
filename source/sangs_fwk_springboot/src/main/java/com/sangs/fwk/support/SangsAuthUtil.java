package com.sangs.fwk.support;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sangs.fwk.common.SangsConstants;
import com.sangs.fwk.common.SangsFwkUtil;
import com.sangs.lib.support.domain.SangsAuthVo;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsStringUtil;

public class SangsAuthUtil  {
	
	protected static Logger logger = LoggerFactory.getLogger(SangsAuthUtil.class);
	
 	public static String getUserId() {
		if(getUserAuthVo() == null)
			return "";
		else 
			return getUserAuthVo().getUserId();
	}
	
	public static String getUserNm() {
		if(getUserAuthVo() == null)
			return "";
		else 
			return getUserAuthVo().getUserNm();
	}
	
	
	public static boolean isLogin() {
		
		HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = req.getSession();
		return isLogin(session);
	}
	
	
	public static boolean isLogin(HttpSession session) throws RuntimeException {
		
		if(session.getAttribute(SangsConstants.STANDARD_USER_SESSION_KEY) == null)
			return false;
		
		SangsAuthVo vo;
		 try {
			 vo = (SangsAuthVo)session.getAttribute(SangsConstants.STANDARD_USER_SESSION_KEY);
		 } catch(Exception e) {
			 throw new SangsMessageException("로그인 세션 Exception ");
		 }
		 
		 if(vo == null || SangsStringUtil.isEmpty(vo.getUserId()))
			 return false;
		 
		return true;
	}
	
	
	public static void setUserAuthVo(SangsAuthVo vo) {
		HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = req.getSession();
		session.setAttribute(SangsConstants.STANDARD_USER_SESSION_KEY, vo);
		
		
	}
	
	
	public static SangsAuthVo getUserAuthVo() {
		HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = req.getSession();
		if(!isLogin())
			return null;
		
		return (SangsAuthVo)session.getAttribute(SangsConstants.STANDARD_USER_SESSION_KEY);
	}
	
	public static String getUserAttr(String userAttrKey) {
		SangsAuthVo vo = getUserAuthVo();
		if(vo == null)
			return "";
		else 
			return vo.getUserAttr(userAttrKey);
	}
	
	public static boolean isRegSessionMenu = false;
	/**
	 * 사용자의 메뉴 목록 set
	 * @param list
	 */
	public static void setUserSessionMenuList(List<SangsMap> list) {
		isRegSessionMenu = true;
		SangsAuthVo vo = getUserAuthVo();
		vo.setUserSessionMenuList(list);
	}
	/**
	 * 사용자의 메뉴 목록 조회
	 * @return
	 */
	public static List<SangsMap> getUserSessionMenuList() {
		if(!isRegSessionMenu)
			return null;
		if(!isLogin())
			return null;
		
		SangsAuthVo vo = getUserAuthVo();
		
		if(vo == null || vo.getUserSessionMenuList() == null)
			throw new SangsMessageException("세션에 메뉴 목록이 존재 하지 않습니다.");
		
		return vo.getUserSessionMenuList();
	}
	
	/**
	 * menuSn 에 대한 권한 있는 메뉴 인지 확인 한다. 
	 * @param menuSn
	 * @return true 일때 권한 있음
	 */
	public static boolean existAuthMenuByMenuSn(int menuSn) {
		if(!isRegSessionMenu)
			return false;
		for(SangsMap map : getUserSessionMenuList()) {
			if(map.getInt("menuSn") == menuSn) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Url에 대해서 세션의 메뉴 목록이 존재 하는지 확인  
	 * @param url
	 * @return true 일때 권한 있음
	 */
	public static boolean isAccessibleAuthMenuUrl(HttpServletRequest request) {
		return isAccessibleAuthMenuUrl(request.getRequestURI());
	}
	public static boolean isAccessibleAuthMenuUrl(String url) {
		
		if(!SangsStringUtil.isEmpty(SangsConstants.OPENMENU_AUTH_CHECK_YN) && "N".equals(SangsConstants.OPENMENU_AUTH_CHECK_YN))
			return true;
		
		// 비로그인인데 로그인 안해도 되는 URL  로 등록되어 있는경우 
		if(SangsFwkUtil.isNoLoginUrl(url))
			return true;
		
		if(!isRegSessionMenu) {
			logger.info("메뉴 정보가 세션에 등록되어 있지 않습니다.");
			return false;
		}
		
		for(SangsMap map : getUserSessionMenuList()) {
			String inUrl = "";
			String sessUrl = "";
			if(url.indexOf("?") >= 0)
				inUrl = url.substring(0, url.indexOf("?"));
			else 
				inUrl = url;
			
			String tempUrl = map.getString("urlAddr");
			
			if(tempUrl.indexOf("?") >= 0)
				sessUrl = tempUrl.substring(0, tempUrl.indexOf("?"));
			else 
				sessUrl = tempUrl;
			
			if(inUrl.equals(sessUrl))
				return true;
		}
		logger.error(url + "은 접근 불가능한 url 입니다.");
		return false;
	}
	 
	
	
	/**
	 * menuSn 에 대해서 권한항목코드가 있는지 확인 한다.  
	 * @param menuSn
	 * @param menuAuthrtCd
	 * @return true 일때 권한항목 있음
	 */
	public static boolean existAuthMenuItemCdByMenuSn(int menuSn, String menuAuthrtCd) {
		if(!isRegSessionMenu)
			return false;
		for(SangsMap map : getUserSessionMenuList()) {
			if(map.getInt("menuSn") == menuSn) {
				if(map.containsKey("authCdMap")) {
					@SuppressWarnings("unchecked")
					Map<String, String> authCdMap = (Map<String, String>)map.get("authCdMap");
					if(authCdMap.containsKey(menuAuthrtCd) && "Y".equals((String)authCdMap.get(menuAuthrtCd))) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
	/**
	 * 접근URL 에 대해서 권한항목코드가 있는지 확인 한다.  
	 * @param urlAddr
	 * @param menuAuthrtCd
	 * @return true 일때 권한항목 있음
	 */
	public static boolean existAuthMenuItemCdByMenuUrl(String urlAddr, String menuAuthrtCd) {
		if(!isRegSessionMenu)
			return false;
		for(SangsMap map : getUserSessionMenuList()) {
			if(urlAddr.equals(map.getString("urlAddr"))) {
				if(map.containsKey("authCdMap")) {
					@SuppressWarnings("unchecked")
					Map<String, String> authCdMap = (Map<String, String>)map.get("authCdMap");
					if(authCdMap.containsKey(menuAuthrtCd) && "Y".equals((String)authCdMap.get(menuAuthrtCd))) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
	

 
	 
}
