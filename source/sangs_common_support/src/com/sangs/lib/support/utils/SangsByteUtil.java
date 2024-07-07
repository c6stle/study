package com.sangs.lib.support.utils;

/**
 * byte 관련 Util
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
public class SangsByteUtil {
	
	/**
	 * byte배열을 hex 문자열로 반환 
	 * @param bytes array of byte
	 * @return byte배열을 hex 문자열로 반환
	 */
	public static String toHexString(byte[] bytes){
		if (bytes == null) {
			return null;
		}

		StringBuffer result = new StringBuffer();
		byte[] arrayOfByte = bytes; int j = bytes.length; for (int i = 0; i < j; i++) { byte b = arrayOfByte[i];
			result.append(Integer.toString((b & 0xF0) >> 4, 16));
			result.append(Integer.toString(b & 0xF, 16));
	    }
		return result.toString();
	}
	
	/**
	 * String to byte Array
	 * @param digits 문자열 
	 * @param radix radix
	 * @return array of byte
	 * @throws IllegalArgumentException IllegalArgumentException
	 * @throws NumberFormatException NumberFormatException
	 */
	public static byte[] toBytes(String digits, int radix) throws IllegalArgumentException, NumberFormatException {
		if (digits == null) {
			return null;
		}
		
		if ((radix != 16) && (radix != 10) && (radix != 8)) {
			throw new IllegalArgumentException("For input radix: \"" + radix + "\"");
		}
		int divLen = radix == 16 ? 2 : 3;
		int length = digits.length();
		if (length % divLen == 1) {
			throw new IllegalArgumentException("For input string: \"" + digits + "\"");
		}
		length /= divLen;
		byte[] bytes = new byte[length];
		for (int i = 0; i < length; i++) {
			int index = i * divLen;
			bytes[i] = (byte)Short.parseShort(digits.substring(index, index + divLen), radix);
		}
		return bytes;
	}
}
