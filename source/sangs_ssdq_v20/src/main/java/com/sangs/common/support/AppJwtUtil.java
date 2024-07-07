package com.sangs.common.support;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sangs.common.common.CommonConstant;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsStringUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;


/**
 * Application JWT 관련 Util 
 * @sessionMapor ow.park
 *
 */
public class AppJwtUtil {
	
	protected static Logger logger = LoggerFactory.getLogger(AppJwtUtil.class);
	
	public static String generateToken(String username, String password, String type, String userGubun) {
		Calendar cal = Calendar.getInstance();
		//cal.add(Calendar.DATE, 7); 	// 일주일  
		cal.add(Calendar.MINUTE, 60);	// 1분 후 
		
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date linkExpryDt = new Date(cal.getTimeInMillis());
        
        JwtBuilder builder = Jwts.builder()
                .setSubject("JWT-TOKEN")
                .claim("id", username)
                .claim("pw", password)
                .claim("type", type)
                .claim("userGubun", userGubun)
                .setExpiration(linkExpryDt)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS512, CommonConstant.JWT_SINGLEKEY);

        return builder.compact();
    }
	
    public static Map<String, Object> validateToken(String token) throws Exception {
    	Map<String, Object> map = new HashMap<String, Object>();
    	
        if (!SangsStringUtil.isEmpty(token)) {
            try {
            	Jws claims = Jwts.parser().setSigningKey(CommonConstant.JWT_SINGLEKEY.getBytes()).parseClaimsJws(token);
            	
                map.put("errorCode", "");
                map.put("error", "");
            	map.put("msg", "정상 인증 값 입니다.");
            	map.put("decodeToken", claims.getBody());
            } catch (ExpiredJwtException e) {
            	map.put("errorCode", "EXPRY_CODE");
            	map.put("error", e);
            	map.put("msg", "만료된 인증 값 입니다.");
            } catch (SignatureException e) {
            	map.put("errorCode", "ERR_CODE");
            	map.put("error", e);
            	map.put("msg", "잘못된 인증 값 입니다.");
            } catch (MalformedJwtException e) {
            	map.put("errorCode", "ALTR_CODE");
            	map.put("error", e);
            	map.put("msg", "변조된 인증 값 입니다.");
            } catch (Exception e) {
            	logger.error("", e);
				throw new SangsMessageException("처리중 에러가 발생하였습니다.");
            }
        }
        return map;
    }
    

}
