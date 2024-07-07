package com.sangs.fwk.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sangs.lib.support.exception.SangsMessageException;



//@Aspect
//@Component
public class SangsServiceAspect {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//@Pointcut("execution(* com.sangs..*Service.*(..))")
	public void pointcutMethod() {
		 logger.debug("SangsServiceAspect ---> pointcutMethod");
	}
	
	//@Around("pointcutMethod()")
	public Object controllerAop(ProceedingJoinPoint joinPoint) throws Throwable  {
		
		String signatureStr = joinPoint.getSignature().toShortString();
		
		logger.debug("signatureStr, {}", signatureStr);
		
		Object rtnObj = null;
		
		try {
			
			rtnObj = joinPoint.proceed();

		} catch(SangsMessageException e) {
			//System.out.println("################################## 12222222222222");
			//logger.error("", e);
			//Map<String, Object> map = new HashMap<String, Object>();
			//map.put(SangsConstants.FWK_ERROR_MESSAGE_KEY, e.getMessage());
			//rtnObj = map;
			throw e;
		} catch(Exception e) {
			//System.out.println("################################## 122222222222222==-===");
			//logger.error("", e);
			//Map<String, Object> map = new HashMap<String, Object>();
			//map.put(SangsConstants.FWK_ERROR_MESSAGE_KEY, "처리중 에러가 발생하였습니다.");
			//rtnObj = map;
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}  
			
		return rtnObj;
		 

	}
	
	
	 
}
