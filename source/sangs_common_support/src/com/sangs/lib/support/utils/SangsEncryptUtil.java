package com.sangs.lib.support.utils;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.sangs.lib.support.exception.SangsMessageException;

/**
 * 암호화 관련 Util
 * 
 * @author id.yoon
 * @since 2022.05.02
 * @version 1.0
 * @see
 *
 * <pre>
 * &lt;&lt; 개정이력(Modification Information) &gt;&gt;
 *   수정일               수정자              수정내용
 *  -------       --------    ---------------------------
 *  2022.05.02    id.yoon     최초 생성
 * </pre>
 */
public class SangsEncryptUtil {
	
 
	/**
	 * 암호화 Key 생성
	 * @param algorithm 암호와 알고리즘(String)
	 * @param keyData keyData
	 * @return 생성된 Key Object
	 * @throws NoSuchAlgorithmException NoSuchAlgorithmException
	 * @throws InvalidKeyException InvalidKeyException
	 * @throws InvalidKeySpecException InvalidKeySpecException
	 */
	public static Key generateKey(String algorithm, byte[] keyData) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException{
			
		String upper = algorithm.toUpperCase();
			
		if("DES".equals(upper)){
			KeySpec keySpec = new DESKeySpec(keyData);
			SecretKeyFactory secreKeyFactory = SecretKeyFactory.getInstance(algorithm);
			SecretKey secretKey = secreKeyFactory.generateSecret(keySpec);
			return secretKey;
		}else if("DESede".equals(upper) || "TripleDES".equals(upper) ){
			KeySpec keySpec = new DESedeKeySpec(keyData);
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
			SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
			return secretKey;
		} else {
			SecretKeySpec keySpec = new SecretKeySpec(keyData, algorithm);
			return keySpec;
		}
	}

 
	/**
	 * AES128 로 암호화 하여 반환(양방향)
	 * @param to_encrypt 암호화 할 문자열
	 * @param cryptKey 암호화 KEY
	 * @return AES128 로 암호화된 문자열 반환
	 */
	public static String encrypt_AES128(String to_encrypt, String cryptKey) {
		try {
			Key key = generateKey("AES", SangsByteUtil.toBytes(cryptKey, 16));
			String transformation = "AES/ECB/PKCS5Padding";
			Cipher cipher = Cipher.getInstance(transformation);
			cipher.init(1, key);
			
			byte[] plain = to_encrypt.getBytes("UTF-8");
			byte[] encrypt = cipher.doFinal(plain);
			return SangsByteUtil.toHexString(encrypt);
		      
		} catch (Exception e) { 
			throw new SangsMessageException(e);
		}
	}
	
	/**
	 * AES128 로 암호된 문자열 복호화(양방향)
	 * @param to_decrypt 복호화 할 문자열 
	 * @param cryptKey 복호화 KEY
	 * @return 복호화된 문자열
	 */
	public static String decrypt_AES128(String to_decrypt, String cryptKey) {
		try {
		    	
			String transformation = "AES/ECB/PKCS5Padding";
			Cipher cipher = Cipher.getInstance(transformation);
			Key key = generateKey("AES", SangsByteUtil.toBytes(cryptKey, 16));
			cipher.init(2, key);
			byte[] decrypt = cipher.doFinal(SangsByteUtil.toBytes(to_decrypt, 16));
			return new String(decrypt, "UTF8");
		      
		} catch (Exception e) { 
			throw new SangsMessageException(e);
		}
	}
	
	/**
	 * SHA512 단방향 암호화 
	 * @param input 암호화 할 문자열
	 * @return 암호화된 문자열 
	 * @throws Exception Exception
	 */
	public static String encrypt_SHA512(String input) throws Exception {
		String toReturn = "";
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			digest.reset();
			digest.update(input.getBytes("utf8"));
			toReturn = String.format("%0128x", new BigInteger(1, digest.digest()));
		} catch (NoSuchAlgorithmException e) {
		    // throws가 싫어서 RuntimeException을 사용
			throw e;
		}
		return toReturn;
	}
	
 
	
	/**
     * 단방향 패스워드 암호화(SHA-512기반) 
     * 
     * @param password 패스워드
     * @param id 사용자 아이디
     * @return 암호화 된 문자열 
     * @throws Exception Exception
     */
    public static String encryptPwd_SHA512(String password, String id) throws Exception {

		if (password == null) return "";
		if (id == null) return ""; 
		
		byte[] hashValue = null; // 해쉬값
	
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		
		md.reset();
		md.update(id.getBytes());
		
		hashValue = md.digest(password.getBytes());
	
		return new String(Base64.encodeBase64(hashValue));
    }
	
}
