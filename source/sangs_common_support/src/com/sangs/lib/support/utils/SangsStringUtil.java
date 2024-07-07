package com.sangs.lib.support.utils;

import java.util.Map;

import com.sangs.lib.support.exception.SangsMessageException;

/**
 * String 관련 Util
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
public class SangsStringUtil {

	/**
	 * Object에 대한 비어있는지여부 반환
	 * 
	 * @param obj input Object
	 * @return true 일때 값이 없음
	 */
	public static boolean isEmpty(Object obj) {
		if(obj == null)
			return true;
		
		String val = "";
		
		if(obj instanceof java.math.BigDecimal) {
			double d = ((java.math.BigDecimal)obj).doubleValue();
			val = String.valueOf(d);
		} else if(obj instanceof Integer) {
			int i = ((Integer)obj).intValue();
			val = String.valueOf(i);
		} else if(obj instanceof java.lang.Double) {
			double i = ((Double)obj).doubleValue();
			val = String.valueOf(i);
		}  else if(obj instanceof java.lang.Long) {
			double i = ((Long)obj).longValue();
			val = String.valueOf(i);
		} else {
			val = (String)obj;
		}
		if(val.equals("")){
			return true;
		} else {
		    return false;
		}
	}
	
	/**
	 * Object에 대한 값이 없을때 대채 문자로 반환 
	 * 
	 * @param obj input Object
	 * @param replaceStr 대채할 문자열
	 * @return 값이 비어있을때 대채 문자열로 반환, 값이 있을때는 그대로 반환
	 */
	public static String nvl(Object obj, String replaceStr) {
		if(isEmpty(obj))
			return replaceStr;
		else 
			return String.valueOf(obj);
	}
	
	/**
	 * Object에 대한 값이 없을때 공백으로 반환
	 * <br> - Object의 값이 null 일때 공백으로 반환됨 
	 * 
	 * @param obj input Object
	 * @return 값이 비어있을때 공백으로 반환, 값이 있을때는 그대로 반환
	 */
	public static String nvl(Object obj) {
		return nvl(obj, "");
	}
	
	/**
	 * Object에 대한 값이 없을때 대채 숫자로 반환 
	 * 
	 * @param obj input Object
	 * @param replaceInt 대채 숫자
	 * @return 값이 비어있을때 대채 숫자로 반환, 값이 있을때는 숫자형으로 형변환하여 반환
	 */
	public static int nvlInt(Object obj, int replaceInt) {
		if(isEmpty(obj))
			return replaceInt;
		else
			return Integer.parseInt(String.valueOf(obj));
	}
	/**
	 * Object에 대한 값이 없을때 0 으로 반환 
	 * 
	 * @param obj input Object
	 * @return 값이 비어있을때 0으로 반환, 값이 있을때는 숫자형으로 형변환하여 반환
	 */
	public static int nvlInt(Object obj) {
		return nvlInt(obj, 0);
	}
	 
	
	/**
	 * 필수 체크
	 * <br> - str 의 값이 비어있을때 'keyDesc + 은(는) 필수 입력 항목 입니다.' 라는 message로 SangsMessageException 을 발생시킨다. 
	 * 
	 * @param str 필수 체크 대상 
	 * @param keyDesc 항목명 
	 */
	public static void checkRequired(String str, String keyDesc) {
		 if(isEmpty(str))
			 throw new SangsMessageException(keyDesc + " 은(는) 필수 입력 항목 입니다.");
	}
	/**
	 * Map안의 key값에 대한 필수 체크
	 * <br> - paramMap의key에 대한 값이 비어있을때'keyDesc + 은(는) 필수 입력 항목 입니다.' 라는 message로 SangsMessageException 을 발생시킨다.
	 *  
	 * @param paramMap 데이터 Map
	 * @param key Map의 key(필수 체크 대상)
	 * @param keyDesc 항목명
	 */
	public static void checkRequiredParam(Map<String, Object> paramMap, String key, String keyDesc) {
		 if(isEmpty(paramMap.get(key)))
			 throw new SangsMessageException(keyDesc + " 은(는) 필수 입력 항목 입니다. ("+key+")" );
	}
	
	/**
	 * Map안의 key값에 대한 필수 체크
	 * <br> - paramMap의key에 대한 값이 비어있을때'keyDesc + 은(는) 필수 입력 항목 입니다.' 라는 message로 SangsMessageException 을 발생시킨다. 
	 * <br> 예)
	 * <br> String userId = SangsStringUtil.checkRequiredParamStr(paramMap, "userId", "사용자아이디");
	 * @param paramMap 데이터 Map
	 * @param key Map의 key(필수 체크 대상)
	 * @param keyDesc 항목명
	 * @return 필수 체크 후 정상인경우 Map에 대한 key값으로 value를 반환한다.
	 */
	public static String checkRequiredParamStr(Map<String, Object> paramMap, String key, String keyDesc) {
		 if(isEmpty(paramMap.get(key)))
			 throw new SangsMessageException(keyDesc + " 은(는) 필수 입력 항목 입니다. ("+key+")" );
		 return String.valueOf(paramMap.get(key));
	}
	
	
	
	/**
	 * byte 기준으로 substring 처리
	 * 
	 * @param str input값 
	 * @param cutLeng cut기준 길이
	 * @return subtring 한 문자열
	 */
	public static String substringByte(String str, int cutLeng) {
		if(!isEmpty(str)) {
			if(str.getBytes().length <= cutLeng) {
				return str;
			} else {
				StringBuffer sb = new StringBuffer(cutLeng);
				int nCnt = 0;
				for(char ch:str.toCharArray()) {
					nCnt +=String.valueOf(ch).getBytes().length;
					if(nCnt > cutLeng) 
						break;
					sb.append(ch);
				}
				return sb.toString();
			}
		} else {
			return "";
		}
	}
	
	/**
	 * 구분자로 나누웠을때 마지막 단어를 반환함 
	 * <br>getLastWordFromDelim("abc_def_rop", "_"); // _구분으로 마지막 단어인 rop 가 반환됨
	 * 
	 * @param str input값
	 * @param delim 구분자 
	 * @return 구분자로 나누웠을때 마지막 단어
	 */
	public static String getLastWordFromDelim(String str, String delim) {
		if(SangsStringUtil.isEmpty(str))
			return "";
		return str.substring(str.lastIndexOf(delim) + 1, str.length()); 
	}
	

	
}
