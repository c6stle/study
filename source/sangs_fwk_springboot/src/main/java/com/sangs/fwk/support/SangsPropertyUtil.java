package com.sangs.fwk.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SangsPropertyUtil implements ApplicationContextAware {
	
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		applicationContext = context;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static String getProperty(String propertyName) {
		ApplicationContext applicationContext = getApplicationContext();
		if (applicationContext.getEnvironment().getProperty(propertyName) == null) 
			return "";
		else 
			return applicationContext.getEnvironment().getProperty(propertyName).toString();
	
	}

}
