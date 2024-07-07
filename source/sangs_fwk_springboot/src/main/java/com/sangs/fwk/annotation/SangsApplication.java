package com.sangs.fwk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AliasFor;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SpringBootApplication
@EnableAutoConfiguration
//@ComponentScan(basePackageClasses= {ControllerBase.class})
public @interface SangsApplication {
	@AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
	String value();
}
