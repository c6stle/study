package com.sangs.fwk.aspect;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sangs.fwk.common.SangsConstants;
import com.sangs.fwk.common.SangsFwkUtil;
import com.sangs.fwk.support.SangsAuthUtil;
import com.sangs.lib.support.domain.SangsAbstractAuthBaseVo;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.exception.SangsNoneAuthException;
import com.sangs.lib.support.utils.SangsStringUtil;

@Aspect
@Component
public class SangsControllerAspect {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	Map<String, String> userAttrMap = null;
	

	@Pointcut("execution(* com.sangs..*Controller.*(..))")
	public void pointcutMethod() {
		logger.debug("SangsControllerAspect");
	}

	
	@Value("${fwk.login.page:}")
	private String loginPage;
	
	@Value("${fwk.login.yn:Y}")
	private String loginYn;
	
	// 로그인 없이 접근 하는 페이지
	@Value("${fwk.auth-check.ignore.urls:}")
	private String noLoginUrls;

	@Value("${fwk.openmenu.auth-check-yn:Y}")
	private String openmenuAuthCheckYn;
	
	
	@Value("${fwk.openmenu.non-auth.error-page:}")
	private String nonAuthErrorPage;
	
	
	
	@PostConstruct
	private void postConstruct() {
		
		if(SangsStringUtil.isEmpty(noLoginUrls) && !SangsStringUtil.isEmpty(nonAuthErrorPage))
			noLoginUrls = nonAuthErrorPage;
			
		else if(!SangsStringUtil.isEmpty(nonAuthErrorPage))	// 권한 없음 에러페이지 값이 있으면 비로그인 페이지 처리
			noLoginUrls = noLoginUrls + ","+nonAuthErrorPage;
	 
		SangsConstants.AUTH_CHECK_IGNORE_URLS = noLoginUrls;
		
		SangsConstants.OPENMENU_AUTH_CHECK_YN = openmenuAuthCheckYn;
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Around("pointcutMethod()")
	public Object controllerAop(ProceedingJoinPoint joinPoint) throws Throwable {

		String signatureStr = joinPoint.getSignature().toShortString();

		logger.debug("signatureStr, {}", signatureStr);

		Object rtnObj = null;

		try {
			if("Y".equals(loginYn)) {
				if(SangsStringUtil.isEmpty(loginPage))
					throw new SangsMessageException("application properties 파일에 loginPage 페이지 fwk.login.page 가 정의 되어 있지 않습니다.");
			}
			 
			/// 로그인시 로그인 정보
			HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			HttpSession session = req.getSession();
			SangsAbstractAuthBaseVo authVo = null;
			
			String reqUri = req.getRequestURI();
			Object[] args = null;
			
	 		// 로그인 체크
			if(!SangsFwkUtil.isNoLoginUrl(reqUri) && !SangsAuthUtil.isLogin() && "Y".equals(loginYn)) {
				if(reqUri.indexOf(loginPage) < 0) {
					logger.info("로그인이 되어 있지 않거나 로그인 없이 접근 할수 없는 페이지 입니다.[" + reqUri + "]");
					
					// ajax 호출 시
					if(req.getHeader("content-type") != null && "application/json".equals((req.getHeader("content-type").toLowerCase()))) {
						throw new SangsMessageException("TO_LOGIN_PAGE");
					} else {
						return "redirect:" + loginPage;
					}
				}
			}
			 
				
			// 세션정보 
			if(SangsAuthUtil.isLogin()) {
				authVo = (SangsAbstractAuthBaseVo) session.getAttribute(SangsConstants.STANDARD_USER_SESSION_KEY);
				userAttrMap = authVo.getUserAttrMap();
			}  
				
			/* TODO 서비스 제어를 여기서 해야함
			
			*/
				
				
			if(userAttrMap == null)
				userAttrMap = new HashMap<String, String>();
				
			args = Arrays.stream(joinPoint.getArgs()).map(data -> {
				if(data instanceof LinkedHashMap) {
					((LinkedHashMap) data).put("SESS_USER_INFO", userAttrMap);
				} 
				return data; 
			}).toArray();
		 
			rtnObj = joinPoint.proceed(args);
 

		} catch(SangsNoneAuthException e) {
			logger.error("", e);
			if(!SangsStringUtil.isEmpty(nonAuthErrorPage))	// 권한 없음 에러페이지 값이 있으면 비로그인 페이지 처리
				return "redirect:" + nonAuthErrorPage;
			else 
				throw e;
			
		} catch (SangsMessageException e) {
			logger.error("", e);
			// Map<String, Object> map = new HashMap<String, Object>();
			// map.put("FWK_MESSAGE", e.getMessage());
			// rtnObj = map;
			// throw new SangsMessageException("처리중 에러가 발생하였습니다.");

			throw e;
		} catch (Exception e) {
			logger.error("", e);
			// Map<String, Object> map = new HashMap<String, Object>();
			// map.put("FWK_MESSAGE", "처리중 에러가 발생하였습니다.");
			// rtnObj = map;
			throw new SangsMessageException("처리중 에러가 발생하였습니다.(controller aspect)");
		}

		return rtnObj;

	}
	
 
	
	
}
