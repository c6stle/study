package com.sangs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.sangs.fwk.annotation.SangsApplication;

@EnableScheduling
@SangsApplication("com.sangs")
public class WebApplicationMain extends SpringBootServletInitializer {

	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WebApplicationMain.class);
    }
	
	public static void main(String[] args) {
		SpringApplication.run(WebApplicationMain.class, args);
	}

}
