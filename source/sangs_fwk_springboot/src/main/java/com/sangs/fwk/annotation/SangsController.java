package com.sangs.fwk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Controller
@RequestMapping
public @interface SangsController {
	@AliasFor(annotation = RequestMapping.class)
	String value();
}
