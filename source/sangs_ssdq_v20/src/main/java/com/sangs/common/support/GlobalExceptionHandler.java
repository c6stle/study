package com.sangs.common.support;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangs.lib.support.exception.SangsMessageException;


@ControllerAdvice
public class GlobalExceptionHandler  {
	
	@ExceptionHandler(SangsMessageException.class)
	public ResponseEntity<String> handlerArgumentSangsMessageException(SangsMessageException e) {

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
		Map<String, String> m = new HashMap<String, String>();
		m.put("resultCd", "EXCEPTION");
		m.put("message", e.getMessage());
		
		String result = mapToJson(m);
		return new ResponseEntity<String>(result, headers, HttpStatus.INTERNAL_SERVER_ERROR); // 500 반환
		
	}
	
	public String mapToJson(Map<String, String> map) {
	    ObjectMapper mapper = new ObjectMapper();

	    try {
	        return mapper.writeValueAsString(map);
	    } catch (JsonProcessingException e) {
	        return "";
	    }
	}
	
	 
	
}

	