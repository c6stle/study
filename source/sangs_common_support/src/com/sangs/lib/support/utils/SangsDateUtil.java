package com.sangs.lib.support.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Date 관련 Util
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
 
public class SangsDateUtil {

	
	/**
	 * 현재 일시를 반환한다
	 * @param format 반환받을 날짜형식 String
	 * @return 현재 일시를 반환한다
	 */
	public static String getToday(String format) {
		SimpleDateFormat format1 = new SimpleDateFormat (format, Locale.KOREAN);
		return format1.format(new Date());
	}
	
	
	
	/**
	 * 날짜 포맷에 맞는지 체크
	 * 
	 * @param date String type 날짜(일시)
	 * @param format 날짜형식 String
	 * @return 정상 날짜 포맷일때 true 반환
	 */
    public static boolean dateFormatCheck(String date, String format) {
        SimpleDateFormat dateFormatParser = new SimpleDateFormat(format, Locale.KOREAN);
        dateFormatParser.setLenient(false);
        try {
            dateFormatParser.parse(date);
            return true;
        } catch (Exception Ex) {
            return false;
        }
    }
    
    
}

