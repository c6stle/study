package com.sangs.fwk.common;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;

/**
 * 
 * Service class invoker 
 * 
 * @author id.yoon
 *
 */
@Component
public class CommonServiceInvoker {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object dataMethod(Map paramMap, String serviceId, String methodId) throws Exception {
		
		Object rtnObj = null;
		try {
			logger.debug("parameter map : " + paramMap);
			
			String serviceClassNm = serviceId.substring(0, 1).toUpperCase() + serviceId.substring(1, serviceId.length()) + "Service";
			
			logger.debug("serviceClassNm : "+serviceClassNm);
			
			MethodInvokingFactoryBean bean = new MethodInvokingFactoryBean();
			Object serviceBean = getBeanObject(serviceClassNm);
			
			Parameter[] defParams = null;	// 파라미터
			Method[] defMethods = serviceBean.getClass().getDeclaredMethods();
			
			for(Method defMethod:defMethods) {
				if(methodId.equals(defMethod.getName())) {
					defParams = defMethod.getParameters();
					break;
				}
			}
			
			
			if(defParams == null) throw new SangsMessageException("Could not found method : " + methodId);
			if(defParams.length > 1) throw new SangsMessageException("Could not execute method with parameters :" + defParams.length + "--> Service Input parameter should be no or one" );
			
			Object[] arrArgs = new Object[defParams.length];
			
			 
			if(defParams.length != 0) {
				 
				String strSvcClassType = defParams[0].getType().toString();
				String strSvcSuperClass = (defParams[0].getType().getSuperclass() != null) ? strSvcSuperClass = defParams[0].getType().getSuperclass().toString():"";
				String strSvcFullParamType = defParams[0].getParameterizedType().toString();
				
				logger.debug("strSvcClassType : " + strSvcClassType);
				logger.debug("strSvcSuperClass : " + 	strSvcSuperClass);
				logger.debug("strSvcFullParamType : " + strSvcFullParamType);
				
				//boolean findFlag = false;
				
				try {
					
					if(strSvcClassType.indexOf("java.util.List") >= 0 ) {
						ArrayList inputList = new ArrayList();
						ArrayList paramList = (ArrayList)paramMap;
						
						for(int j = 0 ; j < paramList.size() ; j++) {
							inputList.add(paramList.get(j));
						}
						arrArgs[0] = inputList;
						//findFlag = true;
					} else {
						if(strSvcClassType.indexOf("java.util.Map") >= 0 || strSvcClassType.indexOf("class com.sangs.lib.support.domain.SangsMap") >= 0 ) {
							arrArgs[0] = paramMap;
							//findFlag = true;
						}
								
					}
					
				} catch(Exception e) {
					e.printStackTrace();
					//findFlag = false;
					logger.error("Service parameter is not mapped from request parameter --> {} " +  strSvcClassType );
					throw new SangsMessageException("Service parameter is not mapped from request parameter --> " + strSvcClassType );
				}
				//if(!findFlag) {
					//logger.error("Service parameter is not mapped from request parameter --> {} " +  strSvcClassType );
					//throw new Exception("Service parameter is not mapped from request parameter --> " + strSvcClassType );
				//}
				
			} else {
				
			}
			
			bean.setTargetObject(serviceBean);
			
			
			bean.setTargetMethod(methodId);
			if(arrArgs != null && arrArgs.length > 0)
				bean.setArguments(arrArgs);
			
			bean.prepare();
			
			Object rslt = null;
		
			rslt = bean.invoke();
			
			if(rslt instanceof java.util.Map || rslt instanceof java.util.LinkedHashMap || rslt instanceof SangsMap) {
				rtnObj = (Map)rslt;
			} else if(rslt instanceof Workbook) {
				rtnObj = (Workbook)rslt;
			} else if(rslt instanceof java.util.List) {
				 
				rtnObj = (List)rslt;
			} else {
				throw new SangsMessageException("Service method return type Exception");
			}
			/*
		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("", e);
			throw e;
		}*/
		} catch(SangsMessageException e) {
			//System.out.println("################################## service invoker 12222222222222");
			logger.error("", e);
			//Map<String, Object> map = new HashMap<String, Object>();
			//map.put(SangsConstants.FWK_ERROR_MESSAGE_KEY, e.getMessage());
			//rtnObj = map;
			throw e;
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("", e);
			String errMsg = "처리중 에러가 발생하였습니다.";
			
			if(e.getCause() != null && (e.getCause().getClass().toGenericString()).indexOf("SangsMessageException") >= 0 && e.getCause().getMessage() != null && !"".equals(e.getCause().getMessage()))
				errMsg = e.getCause().getMessage();
			
			//if(e.getCause() != null && e.getCause().getMessage() != null && !"".equals(e.getCause().getMessage()))
			//	errMsg = e.getCause().getMessage();
			//Map<String, Object> map = new HashMap<String, Object>();
			//map.put(SangsConstants.FWK_ERROR_MESSAGE_KEY, errMsg);
			//rtnObj = map;
			throw new SangsMessageException(errMsg);
		}  
		
		
		return rtnObj;
		
		
	}
	
	
	@Autowired
	private DefaultListableBeanFactory beanFactory;
	
	public Object getBeanObject(String beanNm) throws Exception {
		Object beanObj = null;
		try {
			String svcBeanNm = beanNm.substring((beanNm.lastIndexOf(".") + 1));
			
			StringBuffer sb = new StringBuffer();
			sb.append(String.valueOf(svcBeanNm.charAt(0)).toLowerCase());
			sb.append(svcBeanNm.substring(1));
			String targetBeanNm = sb.toString();
			
			beanObj = beanFactory.getBean(targetBeanNm);
			if(beanObj == null)
				throw new SangsMessageException("can not found bean " + beanNm);
			
			
		} catch(Exception e) {
			throw e;
		}
		return beanObj;
	}
	
	
}
