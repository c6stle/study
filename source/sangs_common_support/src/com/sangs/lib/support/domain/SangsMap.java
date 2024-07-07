package com.sangs.lib.support.domain;

import java.util.LinkedHashMap;

/**
 * Map type 기반의 SangsMap class<br>
 * mybatis 등에서 Map으로 반환시 컬럼명 기반의 key값으로 생성되는경우 해당 SangsMap 을 사용하면 key 값을 camelcase 를 변경하여  put 한다.<br>
 * 주의점은 SangsMap 객체에 put 을 하는경우 key 값이  camelcase로 변환되어 들어감으로 변환을 원하지 않을때는 putOrg함수를 통해서 put 해야함
 * 
 * @author id.yoon
 * @since 2021.05.02
 * @version 1.0
 * @see
 *
 * <pre>
 * &lt;&lt; 개정이력(Modification Information) &gt;&gt;
 *   수정일               수정자              수정내용
 *  -------       --------    ---------------------------
 *   2022.5.02    id.yoon     최초 생성
 * </pre>
 *
 */
public class SangsMap extends LinkedHashMap<String, Object> {
 
	private static final long serialVersionUID = 1L;
	
	/**
	 * Map의 put 처리 하는 메서드로 put 할때의 key값은 _(언더바) 기준의 분리 단어를 camelcase로 바꾸워서  put 한다.
	 * 
	 * @param key Map의 key
	 * @param value Map의 value
	 * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
	 */
	@Override
	public Object put(String key, Object value) {
		return super.put(convertUnderscoreNameToPropertyName(key), value);
	}
	
	/**
	 * SangsMap의 put 시 key값 을 camelcase로 바뀌는것을 원치 않을때 사용
	 * @param key Map의 key
	 * @param value Map의 value
	 * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
	 */
	public Object putOrg(String key, Object value) {
		return super.put(key, value);
	}
	  
	/**
	 * Map의 key에 대한 value값을 String 타입으로 형변환 하여 반환<br>
	 *  - null일때는 공백을 반환한다.
	 *  
	 * @param key Map의 key
	 * @return Map의 value를 String 타입으로 형변환 하여 반환, null일때는 공백을 반환한다.
	 */
	public String getString(String key) {
		
		Object obj = super.get(key);
		
		if (obj == null)
			return "";
		else 
			return String.valueOf(obj);
	}
	
	/**
	 * Map의 key에 대한 value값을 int 타입으로 형변환 하여 반환<br>
	 *  - null일때는 0을 반환한다. 
	 * @param key Map의 key
	 * @return Map의 value를 int 타입으로 형변환 하여 반환
	 */
	public int getInt(String key) {
		String str = this.getString(key);
		if("".equals(str))
			return 0;
		else 
			return Integer.parseInt(str);
	}
	
	
	private String convertUnderscoreNameToPropertyName(String name) {
		if (name == null)
			return  "";
		
		StringBuilder result = new StringBuilder();
		boolean nextIsUpper = false;
		if (name != null && name.length() > 0) {
			if (name.length() > 1 && name.charAt(1) == '_') {
				result.append(Character.toUpperCase(name.charAt(0)));
			}
			else {
				result.append(Character.toLowerCase(name.charAt(0)));
			}
			for (int i = 1; i < name.length(); i++) {
				char c = name.charAt(i);
				if (c == '_') {
					nextIsUpper = true;
				}
				else {
					if (nextIsUpper) {
						result.append(Character.toUpperCase(c));
						nextIsUpper = false;
					}
					else {
						result.append(Character.toLowerCase(c));
					}
				}
			}
		}
		return result.toString();
	}
	

}
