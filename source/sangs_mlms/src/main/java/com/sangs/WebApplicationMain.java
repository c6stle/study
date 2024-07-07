package com.sangs;

import org.springframework.boot.SpringApplication;

import com.sangs.fwk.annotation.SangsApplication;

@SangsApplication("com.sangs")
public class WebApplicationMain {

	public static void main(String[] args) {
		SpringApplication.run(WebApplicationMain.class, args);
	}

}
