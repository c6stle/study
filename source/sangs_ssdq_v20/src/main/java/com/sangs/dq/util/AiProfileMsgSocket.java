package com.sangs.dq.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Component
@ServerEndpoint("/a_analysis_msg_socket")
public class AiProfileMsgSocket {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
	
	public AiProfileMsgSocket() {
		System.out.println("AutoAnalysisMsgSocket class is instanced");
	}
	
	
	public static Map<String, Session> socketSessionMap = new HashMap<String, Session>();
	public static final String MSG_DELIM = "_@^!_";
   
    /**
     * 웹소켓 사용자 연결 성립하는 경우 호출
     */
    @OnOpen
    public void handleOpen(Session session) throws Exception {
    	String cid = UUID.randomUUID().toString();
    	
    	System.out.println("Socket handleOpen");
    	
    	try {
    		socketSessionMap.put(cid, session);
	    	System.out.println("created cid:" + cid);
	    	 
	    	sendToClient(cid, "CID", cid);
	    	
	    	if (StringUtil.isEmpty(session.getId())) 
	    		throw new Exception("Socket session create Exception");
	    	
    	} catch(Exception e) {
    		logger.error("", e);
    		
    	}
    }
    
    public Session getSessionByCid(String cid) {
    	System.out.println("cid : --> " + cid);
    	Session sess = socketSessionMap.get(cid);
    	return sess;
    }
    
    public boolean sendToClient(Session sess, String cmd, String message) {
    	
    	if(sess == null) {
    		logger.error("Socket session not Found Exception");
    		return false;
    	}
    	if(!sess.isOpen()) {
    		logger.error("Socket session not Opened Exception");
    		return false;
    	}
    	try {
    		 
    		String sendMsg = cmd + MSG_DELIM + message;
    		 
    		//logger.debug("send to client : " + sendMsg);
    		
    		sess.getBasicRemote().sendText(sendMsg);
    	} catch(Exception e) {
    		logger.error("", e);
    		return false;
    	}
    	
    	return true;
    	
        
    }
	
    public boolean sendToClient(String cid, String cmd, String message) {
    	Session sess = socketSessionMap.get(cid);
    	return sendToClient(sess, cmd, message);
    }
	
    
	
	
}

