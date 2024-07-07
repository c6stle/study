package com.sangs.dq.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpClientUtil {

	public void get(String requestURL) {
		
		System.out.println("__________________________________________"+ requestURL);
		try {			
			//requestURL = requestURL+"?excSn=3";
			HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성		
			HttpGet getRequest = new HttpGet(requestURL); //GET 메소드 URL 생성
			//getRequest.addHeader("x-api-key", RestTestCommon.API_KEY); //KEY 입력	
			
			HttpResponse response = client.execute(getRequest);			//Response 출력	
			
			if (response.getStatusLine().getStatusCode() == 200) {				
				ResponseHandler<String> handler = new BasicResponseHandler();			
				String body = handler.handleResponse(response);				
				System.out.println(body);	
			} else {	
				System.out.println("response is error : " + response.getStatusLine().getStatusCode());	
			}	
		} catch (Exception e){	
			System.err.println(e.toString());	
		}	
	}
	
	public void post(String requestURL, String jsonMessage) {		
		try {			
			HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성			
			HttpPost postRequest = new HttpPost(requestURL); //POST 메소드 URL 새성 			
			postRequest.setHeader("Accept", "application/json");			
			postRequest.setHeader("Connection", "keep-alive");			
			postRequest.setHeader("Content-Type", "application/json");			
			//postRequest.addHeader("x-api-key", RestTestCommon.API_KEY); //KEY 입력 			
			//postRequest.addHeader("Authorization", token); // token 이용시			
			postRequest.setEntity(new StringEntity(jsonMessage)); //json 메시지 입력 			
			HttpResponse response = client.execute(postRequest);			//Response 출력			
			if (response.getStatusLine().getStatusCode() == 200) {				
				ResponseHandler<String> handler = new BasicResponseHandler();				
				String body = handler.handleResponse(response);				
				System.out.println(body);			
			} else {				
				System.out.println("response is error : " + response.getStatusLine().getStatusCode());			
			}		
		} catch (Exception e){			
			System.err.println(e.toString());		
		}	
	}
	
	
		
		
		
		
}


