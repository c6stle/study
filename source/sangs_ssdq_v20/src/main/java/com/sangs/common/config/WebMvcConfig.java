package com.sangs.common.config;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.sangs.common.interceptor.WebLogInterceptor;

 


/**
 * 사용여부 확인 필요
 * @author gtman5
 *
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	protected Logger log = LoggerFactory.getLogger(this.getClass());

//	@Autowired
//	private LoggerInterceptor loggerInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("lang");
		registry.addInterceptor(localeChangeInterceptor);

//		//if (log.isInfoEnabled()) {
//			registry.addInterceptor(loggerInterceptor)
//			.addPathPatterns("/**")
//			.addPathPatterns("/**/*.do");
//		//}
		
		registry.addInterceptor(webLogInterceptor())
			.addPathPatterns("/**")
			.excludePathPatterns("/css/**", "/fonts/**",  
					"/img/**", "/js/**", "/vender/**");
	}
	
	@Bean
	public WebLogInterceptor webLogInterceptor() {
		return new WebLogInterceptor();
	}
	
	@Value("${server.language.kind:KO}")
	private String lang;
	
	@Bean
	public LocaleResolver localeResolver() {
		// 쿠키를 사용한 예제
		//CookieLocaleResolver resolver = new CookieLocaleResolver();
		//resolver.setCookieName("lang");
		
		SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
		if("KO".equals(lang.toUpperCase()))
			sessionLocaleResolver.setDefaultLocale(Locale.KOREAN);
		else
			sessionLocaleResolver.setDefaultLocale(Locale.ENGLISH);     
	    return sessionLocaleResolver;
	}
	
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:/i18n/messages");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}
}