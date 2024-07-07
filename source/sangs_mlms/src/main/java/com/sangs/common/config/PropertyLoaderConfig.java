package com.sangs.common.config;


import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class PropertyLoaderConfig implements ApplicationRunner {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	ApplicationContext context;
	
	
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		Environment env = context.getEnvironment();
		
		for(String str : env.getActiveProfiles()) {
			logger.debug("getActiveProfiles : " + str);
		}
		for(String str : env.getDefaultProfiles()) {
			logger.debug("getDefaultProfiles : " + str);
		}
		
		Iterator it = args.getOptionNames().iterator();
		while(it.hasNext()) {
			logger.debug("----" + it.next());
		}
		
		
		
		
	}
	
	
	

}
