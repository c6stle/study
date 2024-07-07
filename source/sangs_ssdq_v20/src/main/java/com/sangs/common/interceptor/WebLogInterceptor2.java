package com.sangs.common.interceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sangs.common.service.WebLogService;
import com.sangs.common.support.AuthUtil;
import com.sangs.fwk.common.SangsConstants;
import com.sangs.fwk.support.SangsAuthUtil;
import com.sangs.fwk.support.SangsClientIpUtil;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.utils.SangsStringUtil;


public class WebLogInterceptor2 implements HandlerInterceptor {

//	@Resource
//	private WebLogService webLogService;
// 
//	protected static Logger logger = LoggerFactory.getLogger(SangsAuthUtil.class);
//
//	@Value("${fwk.access-ajax-request.auth-check-yn:Y}")
//	private String reqAjaxAuthCheckYn;
//	
//	
//	/**
//	 * 시스템 웹 로그 정보를 등록
//	 *
//	 * @param request HttpServletRequest
//	 * @param response HttpServletResponse
//	 * @param handler Object
//	 * @throws Exception Exception
//	 */
//	@Override
//	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modeAndView) throws Exception {
//		Map<String, Object> map = new HashMap<String, Object>();
//
//		String sysSeCd = SangsConstants.APP_MNGR_SYS_SE_CD;
//		String cntnURL = request.getRequestURI();									//url
//		String relMenuNm = "";														//메뉴 이름
//		String rqesterId = SangsAuthUtil.getUserId();								//요청된 id
//		String nrmltYn = SangsStringUtil.nvl(request.getAttribute("nrmltYn"), "Y"); // 정상여부
//		String refererUrl = SangsAuthUtil.getRequestRefererUrl(request, false);
//		
//
//		map.put("rqesterId", rqesterId);
//		map.put("sysSeCd", sysSeCd);
//		map.put("rqesterIp", SangsClientIpUtil.getClientIp(request));
//
//		// ajax 호출 시
//		if(request.getHeader("content-type") != null && "application/json".equals((request.getHeader("content-type").toLowerCase()))) {
//
//			if(isSkipMenu(refererUrl) || isSkipMenu(cntnURL))
//				return;
//
//			relMenuNm = getSessionMenuNm(refererUrl) +" (AJAX)";
//		} else {
//			if(isSkipMenu(cntnURL))
//				return;
//			relMenuNm = getSessionMenuNm(cntnURL);
//
//		}
//
//		map.put("relMenuNm", relMenuNm);//
//		map.put("URL", cntnURL);		//
//
//		// 404에러 referer URL 웹로그
//		if (cntnURL.indexOf("/error") == 0) {
// 
//			String refererURI = (String)request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
//			if (isSkipRefererMenu(refererURI)) {
//				return;
//			}
//			
// 		} else {
//			if(AuthUtil.isLogin()) {
//				
//				// 해당 URL , Request 에 대한 권한이 있는지 체크
//				String authMsg = SangsAuthUtil.checkAccessAuth(request, cntnURL, refererUrl, reqAjaxAuthCheckYn);
//				
//				if(!SangsStringUtil.isEmpty(authMsg))	// null 아닐때 권한 없음
//					nrmltYn = "N";
//				
//			} 
//		}
//		map.put("nrmltYn", nrmltYn);	// 정상여부
//		
//		this.setAddMenuNmForEmpty(map);	// 메뉴명 하드코딩
//		webLogService.insertWebLog(map);
//		
//	}
//
//	private String getSessionMenuNm(String url) {
//		if(url == null)
//			return "";
//
//		List<SangsMap> userSessionMenuList = SangsAuthUtil.getUserSessionMenuList();
//
//		if(userSessionMenuList == null)
//			return "";
//
//		for(SangsMap map : userSessionMenuList) {
//			if(!"/".equals(map.getString("urlAddr"))) {
//				if (url.indexOf(map.getString("urlAddr")) >= 0) {
//
//					return map.getString("menuNm");
//				}
//			} // else { /* urlAddr "/" 에 대한 처리 */ }
//		}
//
//		return "";
//	}
//
//	private boolean isSkipMenu(String url) {
//		Map<String, String> skipMap = new HashMap<String, String>();
//
//
//		skipMap.put("/json/authMngMenu/getMngrLoginMenuTreeList", "");	// 관리자 메뉴 조회(GNB or LEFT)
//		skipMap.put("/json/commonCode/getCommonCodeMultiList", "");		// 공통 코드 조회
//		skipMap.put("/favicon.ico", "");		
//
//		return skipMap.containsKey(url);
//	}
//
//	private boolean isSkipRefererMenu(String url) {
//		Map<String, String> skipRefererMap = new HashMap<String, String>();
//
//
//		skipRefererMap.put("/favicon.ico", "");							// favicon.ico
//
//		return skipRefererMap.containsKey(url);
//	}
//	
//	
//	private void setAddMenuNmForEmpty(Map<String, Object> map) {
//		
//		// 메뉴명 하드 코딩
//		//if(SangsStringUtil.isEmpty(map.get("relMenuNm"))) {
//			String url = SangsStringUtil.nvl(map.get("URL"));
//			if("/login/login".equals(url))
//				map.put("relMenuNm", "로그인페이지");
//			else if("/login/loginProc".equals(url))
//				map.put("relMenuNm", "로그인처리");
//			else if("/login/checkLogin".equals(url))
//				map.put("relMenuNm", "로그인체크(AJAX)");
//			else if("/cmmnUpload/upload".equals(url))
//				map.put("relMenuNm", "공통업로드");
//			
//			
//			
//		//}
//		
//		
//	}
//	
//	
	
}