
package com.sangs.collector.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sangs.lib.support.domain.SangsMap;

/**
 * Sangs Util
 *
 * @author soundheaven1
 *
 *         <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      	수정자           		수정내용
 *  -------    --------    		---------------------------
 *   2015.02.23	soundheaven1
 *
 *         </pre>
 *
 */
public class SangsCollectorUtil {

	private static Logger log = LoggerFactory.getLogger("com.sangs.util.SangsUtil");

	/**
	 * jsp에서 사용하는 list에서 combo의 option을 반환함
	 *
	 * @param list
	 * @param defaultValue
	 * @return
	 */
	public static String getComboOptions(List<SangsMap> list, String selectValue) {
		return getComboOptions(list, selectValue, "code", "name");
	}

	public static String getComboOptions(List<SangsMap> list, String selectValue, String code, String name) {
		StringBuffer sb = new StringBuffer();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {

				SangsMap sangsMap = (SangsMap) list.get(i);

				sb.append("<option value='").append(sangsMap.get(code)).append("' ");
				try {
					if (selectValue.equals(String.valueOf(sangsMap.get(code))))
						sb.append(" selected='selected' ");
				} catch (Exception e) {
					log.info(e.getMessage());
				}
				sb.append(" >");
				sb.append(sangsMap.get(name));
				sb.append("</option>");
			}
		}

		return sb.toString();
	}

	/**
	 * jsp에서 사용하는 map에서 combo의 option을 반환함
	 *
	 * @param items
	 * @param defaultValue
	 * @return
	 */
	public static String getComboOptionsForMap(Map<String, String> items, String defaultValue) {
		StringBuilder sb = new StringBuilder();

		if (items != null) {

			Set<Entry<String, String>> entries = items.entrySet();

			for (Entry<String, String> entry : entries) {

				sb.append("<option value='").append(entry.getKey()).append("' ");

				try {
					if (defaultValue.equals(String.valueOf(entry.getKey())))
						sb.append(" selected='selected' ");
				} catch (Exception e) {
					log.info(e.getMessage());
				}
				sb.append(" >");
				sb.append(entry.getValue());
				sb.append("</option>");
			}
		}

		return sb.toString();
	}

	/**
	 * null 일때 공백 반환 한다.
	 *
	 * @param obj
	 * @return
	 */
	public static String nchk(Object obj) {
		String rtnVal = "";
		if (obj != null)
			rtnVal = nvl((String) obj, "");
		return rtnVal;
	}

	/*
	*//**
		 * 페이지 정보를 셋팅한다.
		 *
		 * @param params
		 * @param totCnt
		 * @return
		 * @throws Exception
		 *//*
			 * public static PaginationInfo getPagingInfo(Map<String, String> params, int
			 * totCnt) throws Exception { int pageIndex = 1; int perPage =
			 * SangsConstants.PER_PAGE; // 한페이지에 나오는 게시물 수 int pageSize = 10; // 페이지 바에 나오는
			 * 페이지 갯수
			 *
			 * if(!SangsCollectorUtil.isEmpty(params.get("pageIndex"))) pageIndex =
			 * Integer.parseInt(params.get("pageIndex"));
			 *
			 * if(!SangsCollectorUtil.isEmpty(params.get("perPage"))) perPage =
			 * Integer.parseInt(params.get("perPage"));
			 *
			 * if(!SangsCollectorUtil.isEmpty(params.get("pageSize"))) pageSize =
			 * Integer.parseInt(params.get("pageSize"));
			 *
			 * PaginationInfo paginationInfo = new PaginationInfo();
			 * paginationInfo.setCurrentPageNo(pageIndex);
			 * paginationInfo.setRecordCountPerPage(perPage);
			 * paginationInfo.setPageSize(pageSize);
			 * paginationInfo.setTotalRecordCount(totCnt);
			 *
			 * return paginationInfo;
			 *
			 * }
			 */
	/**
	 * Grid용 페이지 정보를 셋팅한다.
	 *
	 * @param params
	 * @param totCnt
	 * @return
	 * @throws Exception
	 */
	/*
	 * public static PaginationInfo getGridPagingInfo(Map<String, String> params,
	 * int totCnt) throws Exception { int pageIndex = 1; int perPage =
	 * SangsConstants.PER_PAGE; // 한페이지에 나오는 게시물 수 int pageSize = 10; // 페이지 바에 나오는
	 * 페이지 갯수
	 *
	 * if(!SangsUtil.isEmpty(params.get("pageIndex"))) pageIndex =
	 * Integer.parseInt(params.get("pageIndex"));
	 *
	 * if(!SangsUtil.isEmpty(params.get("perPage"))) perPage =
	 * Integer.parseInt(params.get("perPage"));
	 *
	 * if(!SangsUtil.isEmpty(params.get("pageSize"))) pageSize =
	 * Integer.parseInt(params.get("pageSize"));
	 *
	 * PaginationInfo paginationInfo = new PaginationInfo();
	 * paginationInfo.setCurrentPageNo(pageIndex);
	 * paginationInfo.setRecordCountPerPage(perPage);
	 * paginationInfo.setPageSize(pageSize);
	 * paginationInfo.setTotalRecordCount(totCnt);
	 *
	 * return paginationInfo;
	 *
	 * }
	 */

	/**
	 * 문자열에 문자를 왼쪽에 추가하여 반환한다. lpad("691111", 8, '-') ▷ --691111
	 *
	 * @param str 문자열
	 * @param len 길이
	 * @param c   추가될 문자
	 * @return 문자열에 문자를 왼쪽에 추가하여 반환한다.
	 */
	public static String lpad(String str, int len, char c) {
		String temp = (str == null) ? "" : str;
		if (temp.length() > len)
			return temp;

		int length = len - temp.length();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			buffer.append(Character.toString(c));
		}
		return buffer.toString() + temp;
	}

	public static String rpad(String str, int len, char c) {
		String temp = (str == null) ? "" : str;
		if (temp.length() > len)
			return temp;

		int length = len - temp.length();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			buffer.append(Character.toString(c));
		}
		return temp + buffer.toString();
	}

	/**
	 * 문자열에서 지정 문자열을 치환한다.
	 *
	 * @param java.lang.String str : 입력문자열
	 * @param java.lang.String pattern : 치환 대상 문자열
	 * @param java.lang.String replace : 치환 문자열
	 * @return java.lang.String
	 */
	public static String replaceAll(String str, String pattern, String replace) {

		if (str == null || str.length() < 1)
			return "";
		if (pattern == null || pattern.length() < 1)
			return str;

		int s = 0;
		int e = 0;
		StringBuffer result = new StringBuffer();
		while ((e = str.indexOf(pattern, s)) >= 0) {
			result.append(str.substring(s, e));
			result.append(replace);
			s = e + pattern.length();
		}
		result.append(str.substring(s));
		return result.toString();
	}

	public static String enter2br(String str) {
		return replaceAll(str, "\n", "<br/>");
	}

	public static String enter2addBr(String str) {
		return replaceAll(str, "<br>", "<br />");
	}

	/**
	 * 이메일 유효성검사
	 *
	 * @param email
	 * @return boolean
	 *
	 */
	public static boolean isValidEmail(String email) {

		Pattern p = Pattern.compile("^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$");
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/*
	public static HashMap<String, Object> checkEmailAddress(String mails) {
		HashMap<String, Object> rtnMap = new HashMap<String, Object>();
		String errMsg = "";
		ArrayList<String> list = new ArrayList<String>();
		try {

			String[] address = mails.split(","); // 받은 주소를 ,를 구분자로이용 배열로 리턴

			for (int i = 0; i < address.length; i++) {
				address[i] = address[i].trim(); // 공백제거
				address[i] = address[i].replace("\n", ""); // 엔터키 제거
				if (!isValidEmail(address[i])) { // 이메일주소 유효성체크
					errMsg = errMsg + address[i] + ",";

				}

				if (SangsCollectorUtil.isNotEmpty(address[i])) {
					list.add(address[i]);
				}

			}

			if (!"".equals(errMsg))
				errMsg = "형식이 맞지않는 이메일 주소가 존재합니다. (" + errMsg + ")";

		} catch (Exception e) {

			errMsg = "처리중에러가 발생하였습니다.";
		}

		rtnMap.put("ERR_MSG", errMsg);
		rtnMap.put("RTN_EMAIL", list);

		return rtnMap;
	}
	 */
	/**
	 * <p>
	 * yyyyMMdd 혹은 yyyy-MM-dd 형식의 날짜 문자열을 입력 받아 년, 월, 일을 증감한다. 년, 월, 일은 가감할 수를 의미하며,
	 * 음수를 입력할 경우 감한다.
	 * </p>
	 *
	 * <pre>
	 * DateUtil.addYearMonthDay("19810828", 0, 0, 19)  = "19810916"
	 * DateUtil.addYearMonthDay("20060228", 0, 0, -10) = "20060218"
	 * DateUtil.addYearMonthDay("20060228", 0, 0, 10)  = "20060310"
	 * DateUtil.addYearMonthDay("20060228", 0, 0, 32)  = "20060401"
	 * DateUtil.addYearMonthDay("20050331", 0, -1, 0)  = "20050228"
	 * DateUtil.addYearMonthDay("20050301", 0, 2, 30)  = "20050531"
	 * DateUtil.addYearMonthDay("20050301", 1, 2, 30)  = "20060531"
	 * DateUtil.addYearMonthDay("20040301", 2, 0, 0)   = "20060301"
	 * DateUtil.addYearMonthDay("20040229", 2, 0, 0)   = "20060228"
	 * DateUtil.addYearMonthDay("20040229", 2, 0, 1)   = "20060301"
	 * </pre>
	 *
	 * @param dateStr 날짜 문자열(yyyyMMdd, yyyy-MM-dd의 형식)
	 * @param year    가감할 년. 0이 입력될 경우 가감이 없다
	 * @param month   가감할 월. 0이 입력될 경우 가감이 없다
	 * @param day     가감할 일. 0이 입력될 경우 가감이 없다
	 * @return yyyyMMdd 형식의 날짜 문자열
	 * @throws IllegalArgumentException 날짜 포맷이 정해진 바와 다를 경우. 입력 값이
	 *                                  <code>null</code>인 경우.
	 */
	public static String addYearMonthDay(String sDate, int year, int month, int day) {
		return addYearMonthDay(sDate, year, month, day, "yyyyMMdd");
	}

	public static String addYearMonthDay(String sDate, int year, int month, int day, String strFormat) {

		// String dateStr = validChkDate(sDate);

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(strFormat, Locale.getDefault());
		try {
			cal.setTime(sdf.parse(sDate));
		} catch (ParseException e) {
			throw new IllegalArgumentException("Invalid date format: " + sDate);
		}

		if (year != 0)
			cal.add(Calendar.YEAR, year);
		if (month != 0)
			cal.add(Calendar.MONTH, month);
		if (day != 0)
			cal.add(Calendar.DATE, day);
		return sdf.format(cal.getTime());
	}

	/**
	 * 입력된 일자 문자열을 확인하고 8자리로 리턴
	 *
	 * @param sDate
	 * @return
	 */
	public static String validChkDate(String dateStr) {
		String rtnDateStr = dateStr;

		if (dateStr == null || !(dateStr.trim().length() == 8 || dateStr.trim().length() == 10)) {
			throw new IllegalArgumentException("Invalid date format: " + dateStr);
		}
		if (dateStr.length() == 10) {
			rtnDateStr = replaceAll(dateStr, "-", "");
		}
		return rtnDateStr;
	}

	/**
	 * yyyyMMdd 형식의 날짜문자열을 원하는 캐릭터(ch)로 쪼개 돌려준다<br/>
	 *
	 * <pre>
	 * ex) 20030405, ch(.) -> 2003.04.05
	 * ex) 200304, ch(.) -> 2003.04
	 * ex) 20040101,ch(/) --> 2004/01/01 로 리턴
	 * </pre>
	 *
	 * @param date yyyyMMdd 형식의 날짜문자열
	 * @param ch   구분자
	 * @return 변환된 문자열
	 */
	public static String formatDate(String sDate, String ch) {
		if (isEmpty(sDate))
			return "";

		String dateStr = validChkDate(sDate);

		String str = dateStr.trim();
		String yyyy = "";
		String mm = "";
		String dd = "";

		if (str.length() == 8) {
			yyyy = str.substring(0, 4);
			if (yyyy.equals("0000"))
				return "";

			mm = str.substring(4, 6);
			if (mm.equals("00"))
				return yyyy;

			dd = str.substring(6, 8);
			if (dd.equals("00"))
				return yyyy + ch + mm;

			return yyyy + ch + mm + ch + dd;
		} else if (str.length() == 6) {
			yyyy = str.substring(0, 4);
			if (yyyy.equals("0000"))
				return "";

			mm = str.substring(4, 6);
			if (mm.equals("00"))
				return yyyy;

			return yyyy + ch + mm;
		} else if (str.length() == 4) {
			yyyy = str.substring(0, 4);
			if (yyyy.equals("0000"))
				return "";
			else
				return yyyy;
		} else
			return "";
	}

	/**
	 * 한글과 영문이 혼용된 주어진 문자열을 주어진 byte 길이만큼만 반환한다. String s = "한글9나라"; truncate(s, 6)
	 * ▷ "한글9"
	 *
	 * @param str    문자열
	 * @param length 반환될 문자열 길이
	 * @return 주어진 문자열을 주어진 길이만큼만 반환한다.
	 */
	public static String truncateByte(String str, int length) {

		if ((str == null) || str.getBytes().length <= length)
			return str;

		StringBuffer buffer = new StringBuffer();
		int currentLength = 0;
		for (int i = 0; i < length; i++) {
			String c = Character.toString(str.charAt(i));
			int byteLength = c.getBytes().length; // ascii : 1 byte, unicode : 2 bytes
			if ((currentLength + byteLength) > length)
				break;
			buffer.append(c);
			currentLength += byteLength;
		}
		return buffer.toString() + "...";
	}

	/**
	 * 내용에서 @아이디 와 같이 표현할때 <span>@아이디</span> 와 같이 반환한다.
	 *
	 * @param str
	 * @return
	 */
	public static String cvtAtAppendSpan(String inStr) {

		String rtnVal = "";
		try {
			if (SangsCollectorUtil.isEmpty(inStr))
				return "";

			StringBuffer sb = new StringBuffer();
			String str = " " + inStr + " "; // 맨압과 끝에 @가 들어올때 정상적으로 작동하게 하기 위해서
			StringTokenizer st = new StringTokenizer(str, "@");

			int idx = 0;
			while (st.hasMoreTokens()) {
				String token = st.nextToken();

				if (idx == 0) {
					sb.append(token);
				} else {
					int firstSpaceIdx = token.indexOf(" ");
					if (firstSpaceIdx == -1)
						firstSpaceIdx = token.length();
					String tmpPre = token.substring(0, firstSpaceIdx);
					String tmpPost = token.substring(firstSpaceIdx, token.length());
					sb.append("<span>@").append(tmpPre).append("</span>").append(tmpPost);
				}
				idx++;
			}

			rtnVal = sb.substring(1, sb.length()); // 앞에 공백 없애기

		} catch (Exception e) {

			rtnVal = inStr;
		}
		return rtnVal;
	}

	/**
	 * 문자열을 받아 원하는 길이만큼 원하는 문자열로 바꿔준다 (ex: abcde -> ab**)
	 *
	 * @param str 바꿀 문자열
	 * @param len 바꿀 자리수
	 * @param ch  대신 들어갈 문자
	 * @return
	 */
	public static String changePostFix(String str, int len, String ch) {

		int endIndex = str.length() - len;
		String resultStr = "";
		if (endIndex > 0) {
			resultStr = str.substring(0, endIndex);

			for (int i = 0; i < len; i++) {
				resultStr += ch;
			}
		}
		return resultStr;
	}

	/**
	 * double형 콤마 문자 포맷으로 변환한다
	 *
	 * @param value
	 * @return
	 */
	public static String getCommaStr(double value) {

		String returnVal;
		java.text.DecimalFormat df = new java.text.DecimalFormat("#,###");

		try {
			returnVal = df.format(Double.valueOf(value));
		} catch (NumberFormatException e) {

			returnVal = "";
		}
		return returnVal;
	}

	/**
	 * 현재 풀URL을 리턴한다 파라미터 포함
	 *
	 * @param request
	 * @return
	 */
	public static String getFullUrl(HttpServletRequest request) {
		String curPage = request.getRequestURI() + "?" + request.getQueryString();
		return curPage;
	}

	/**
	 * 입력한 yyyyMM 형식의 다음달을 구한다.
	 *
	 * @param yyyyMM
	 * @return
	 * @throws Exception
	 */
	public static String nextMonth(String yyyyMM) throws Exception {

		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMM");
		java.util.Date date = sdf.parse(yyyyMM);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		Date calDate = calendar.getTime();

		return new java.text.SimpleDateFormat("yyyyMM").format(calDate);
	}

	/**
	 * 입력한 yyyyMM 형식의 이전달을 구한다.
	 *
	 * @param yyyyMM
	 * @return
	 * @throws Exception
	 */
	public static String previousMonth(String yyyyMM) throws Exception {

		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMM");
		java.util.Date date = sdf.parse(yyyyMM);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1);
		Date calDate = calendar.getTime();

		return new java.text.SimpleDateFormat("yyyyMM").format(calDate);
	}

	/**
	 * 입력 월의 마지막날 반환
	 *
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getEndOfMonth(int year, int month) {

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		int lastDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		return String.valueOf(lastDate);
	}

	// 오늘 날짜 반환
	public static String getToday() {
		return getToday("yyyy-MM-dd");
	}

	public static String getToday(String formatStr) {
		SimpleDateFormat format = new SimpleDateFormat(formatStr, Locale.getDefault());
		return format.format(new Date());
	}

	public static String toDate(long date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault());
		return format.format(new Date(date));
	}

	public static String getRemoveTag(String str, int maxLength, String postFix) {
		String rtn = "";
		try {
			Pattern p = Pattern.compile("\\<(\\/?)(\\w+)*([^<>]*)>");
			Matcher m = p.matcher(str);
			String tempStr = m.replaceAll("");

			rtn = truncateByte(tempStr, maxLength);

			if (tempStr.length() != rtn.length())
				rtn = rtn + postFix;

		} catch (Exception e) {

			rtn = str;
		}
		return rtn;
	}

	/**
	 * 요일명 가져온다.
	 *
	 * @return
	 */
	public static String dayOfWeekNm(String dayStr) {
		return dayOfWeekNm(dayStr, "KO");
	}

	public static String dayOfWeekNm(String dayStr, String lang) {
		String rtn = "";
		try {

			if (dayStr.length() != 10)
				return "";

			Calendar calendar = Calendar.getInstance();

			String year = dayStr.substring(0, 4);
			String month = dayStr.substring(5, 7);
			String day = dayStr.substring(8, 10);

			calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));

			if ("ENG".equals(lang)) {
				String[] weekDay = { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" };
				rtn = weekDay[calendar.get(Calendar.DAY_OF_WEEK) - 1];
			} else {
				String[] weekDay = { "일", "월", "화", "수", "목", "금", "토" };
				rtn = weekDay[calendar.get(Calendar.DAY_OF_WEEK) - 1];
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return rtn;
	}

	/**
	 * 영문 월 표기 반환
	 *
	 * @param month
	 * @return
	 */
	public static String getEngMonthName(int month) {
		if (month == 1)
			return "January";
		else if (month == 2)
			return "February";
		else if (month == 3)
			return "March";
		else if (month == 4)
			return "April";
		else if (month == 5)
			return "May";
		else if (month == 6)
			return "June";
		else if (month == 7)
			return "July";
		else if (month == 8)
			return "August";
		else if (month == 9)
			return "September";
		else if (month == 10)
			return "October";
		else if (month == 11)
			return "November";
		else if (month == 12)
			return "December";
		else
			return "";
	}

	/**
	 * delim 으로 짜른후 앞의 부분을 반환한다.
	 *
	 * @param str
	 * @param delim
	 * @return
	 */
	public static String getSplitFront(String str, String delim) {
		String rtn = "";
		try {
			if (str == null)
				return "";
			int indexOf = str.indexOf(delim);
			if (indexOf > 0)
				rtn = str.substring(0, indexOf);
		} catch (Exception e) {

			rtn = str;
		}

		return rtn;
	}

	public static int indexOfWidthComma(String targetStr, String str) {
		int rtn = -1;
		try {
			rtn = targetStr.indexOf("," + str + ",");
		} catch (Exception e) {
			rtn = -1;
		}
		return rtn;
	}

	/**
	 * 숫자를 이미지로 된 이미지태그 반환
	 *
	 * @param context
	 * @param type
	 * @param number
	 * @return
	 */
	public static String getImageNumber(String context, String type, String number) {
		StringBuffer sb = new StringBuffer();
		try {
			if ("1".equals(type)) {
				/// images/front/site/img_Num00.png 스타일
				/// images/front/site/img_Point.png
				if (SangsCollectorUtil.isEmpty(number))
					return "";

				char[] c = number.toCharArray();

				for (int i = 0; i < c.length; i++) {
					sb.append("<img src='").append(context).append("images/front/site/img_Num0").append(c[i])
							.append(".png' alt='").append(c[i]).append("' >");
				}
				sb.append("<img src='").append(context).append("images/front/site/img_Point.png' alt='P' >");
			}

		} catch (Exception e) {
			log.info(e.getMessage());
		}

		return sb.toString();
	}

	/**
	 * " 를 ' 로 바꾼다.
	 *
	 * @param str
	 * @return
	 */
	public static String removeDoubleQu(String str) {
		return str.replaceAll("\"", "'");
	}

	public static String getCommaStr3(double value) {
		if (value == 0)
			return "";
		return getCommaStr(value);
	}

	// 소수점 1자리 까지 반환
	public static String getCommaStr4(double value) {
		if (value == 0)
			return "";

		String returnVal;
		java.text.DecimalFormat df = new java.text.DecimalFormat("#,###.0");

		try {
			returnVal = df.format(Double.valueOf(value));
		} catch (NumberFormatException e) {

			returnVal = "";
		}
		return returnVal;
	}

	// 소수점 2자리 까지 반환 (값이 0이하로 많이나올때 사용 ex: 0.004 등 )
	public static String getCommaStr5(double value) {
		if (value == 0)
			return "";

		String returnVal;
		java.text.DecimalFormat df = new java.text.DecimalFormat("0.0#");

		try {
			returnVal = df.format(Double.valueOf(value));
		} catch (NumberFormatException e) {

			returnVal = "";
		}
		return returnVal;
	}

	public static String toString(String reqstr) {
		if (reqstr == null || reqstr.equals(""))
			return "";
		else
			return reqstr.trim();
	}

	public static String toString(String reqstr, String dstr) {
		if (reqstr == null || reqstr.equals(""))
			return dstr;
		else
			return reqstr.trim();
	}

	public static int toInt(String reqstr) {
		if (reqstr == null || reqstr.equals(""))
			return 0;
		else
			return toInt(reqstr, 0);
	}

	public static int toInt(String inReqstr, int dstr) {
		try {
			/*
			 * if (reqstr == null || reqstr.equals("")) return dstr; else { reqstr =
			 * reqstr.replace(",", ""); return Integer.parseInt(reqstr); }
			 */
			if (inReqstr == null || inReqstr.equals(""))
				return dstr;
			else {
				String reqstr = inReqstr.replace(",", "");
				return Integer.parseInt(reqstr);
			}
		} catch (NumberFormatException e) {
			return dstr;
		}
	}

	public static int getYear() {
		return getNumberByPattern("yyyy");
	}

	public static int getMonth() {
		return getNumberByPattern("MM");
	}

	public static int getNumberByPattern(String pattern) {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(pattern, java.util.Locale.KOREA);
		String dateString = formatter.format(new java.util.Date());
		// Date date1 = new Date();
		return Integer.parseInt(dateString);

	}

	public static boolean isEmptyObj(Object obj) {
		return isEmpty(obj);
	}

	public static boolean isEmpty(Object obj) {
		/*
		 * if(obj == null) return true; return isEmpty((String)obj);
		 */
		if (obj == null)
			return true;

		String val = "";

		if (obj instanceof java.math.BigDecimal) {
			double d = ((java.math.BigDecimal) obj).doubleValue();
			val = String.valueOf(d);
		} else if (obj instanceof Integer) {
			int i = ((Integer) obj).intValue();
			val = String.valueOf(i);
		} else if (obj instanceof java.lang.Double) {
			double i = ((Double) obj).doubleValue();
			val = String.valueOf(i);
		} else if (obj instanceof java.lang.Long) {
			double i = ((Long) obj).longValue();
			val = String.valueOf(i);
		} else {
			val = (String) obj;
		}
		if (val.equals(""))
			return true;
		else
			return false;
	}

	public static String nvl(Object obj) {
		return nvl(obj, "");
	}

	public static String nvl(Object obj, String nullVal) {
		if (obj == null)
			return nullVal;

		// if(isEmpty((String)obj))
		if (isEmpty(obj)) {
			return nullVal;
		} else {
			// return (String)obj;
			if (obj instanceof java.math.BigDecimal) {
				double d = ((java.math.BigDecimal) obj).doubleValue();
				return String.valueOf(d);
			} else if (obj instanceof Integer) {
				int i = ((Integer) obj).intValue();
				return String.valueOf(i);
			} else if (obj instanceof Long) {
				long l = ((Long) obj).longValue();
				return String.valueOf(l);
			} else {
				return (String) obj;
			}
		}
	}

	public static String nvl(String str) {
		if (isEmpty(str))
			return "";
		else
			return str;
	}

	public static String nvl(String str, String nullVal) {
		if (isEmpty(str))
			return nullVal;
		else
			return str;
	}

	// int 로 바꾼후 String 으로 반환한다.
	public static String toIntStr(Object obj) {
		String val = "";
		if (isEmpty(obj))
			return "";

		if (obj instanceof java.math.BigDecimal) {
			Double d = ((java.math.BigDecimal) obj).doubleValue();
			int i = d.intValue();
			val = String.valueOf(i);
		} else if (obj instanceof Integer) {
			int i = ((Integer) obj).intValue();
			val = String.valueOf(i);
		} else if (obj instanceof java.lang.Double) {
			Double d = ((Double) obj).doubleValue();
			int i = d.intValue();
			val = String.valueOf(i);
		} else {
			val = (String) obj;
		}
		return val;
	}

	// int 로 바꾼후 String 으로 반환한다.
	public static double returnDiv(double a, double b) {
		double result = a / b;

		return result;
	}

	// ' 와 " 을 공백으로 치완한다.
	public static String removeQuValue(String str) {
		String val = "";
		val = replaceAll(str, "\"", "");
		val = replaceAll(val, "'", "");
		return val;
	}

	// target 에 str 의 문자열이 포함되어 있으며 true 반환
	public static boolean isExistStr(String target, String str) {
		if (SangsCollectorUtil.isEmpty(target))
			return false;
		if (SangsCollectorUtil.isEmpty(str))
			return false;
		if (target.indexOf(str) > -1)
			return true;
		else
			return false;
	}

	// 설문에서만 사용!!!!!!
	// 설문 리스트 반환해서 Y값이 넘어가면 기타보기를 만들어 준다.

	public static String isLastSurveyCase(int index, String pid, String cid, List<SangsMap> list) {

		String rtnStr = "N";

		// 마지막 로우인 경우
		if ((index + 1) == list.size()) { // 다음로우가 리스트사이즈와 같은 경우(마지막 로우)
			rtnStr = "Y";

		} else {
			SangsMap surveyMap = list.get(index + 1); // 화면상 로우보다 다음 로우에 데이터
			if (!surveyMap.getString("pid").equals(pid)) { // 같지 않을때 Y를 반환 (pid가 다를때)
				rtnStr = "Y";
			}
		}

		return rtnStr;
	}

	/**
	 * SangsMap에 있는 값을 구분자로 구분나누워 SangMap에 셋팅해준다. 예) setSplitValue(map, "bizrNo",
	 * "12-3456-7890", "-", 3) 로 호출하면 -> map 안에 bizrNoSp1에 12, bizrNoSp2에 3456,
	 * bizrNoSp3에 7890인 map으로 셋팅된다.
	 *
	 * @param map
	 * @param colNm
	 * @param sStr
	 * @param sLeng
	 */
	public static void setSplitValue(SangsMap map, String colNm, String spStr, int sLength) {
		if (map != null) {
			String val = map.getString(colNm);

			String[] spVal = val.split(spStr);

			for (int i = 0; i < sLength; i++) {
				String temStr = "";
				if (spVal.length > i) {
					temStr = spVal[i];
				}
				map.put(colNm + "Sp" + (i + 1), SangsCollectorUtil.nvl(temStr, ""));
			}
		}
	}

	public static void setSplitValue(SangsMap map, String colNm, int sLeng) {
		setSplitValue(map, colNm, "-", sLeng);
	}

	/**
	 * 엑셀파일의 내용을 List 로 반환한다.
	 *
	 * @param fileFullPath : 엑셀파일물리적경로(파일이름까지 지정) c:\aa\aa.xls
	 * @param skipRowCnt   : 상단의 header 등의 row을 몇줄 skip 할것인지 지정
	 * @param columnCount  : 읽을 column 수
	 * @return ArrayList<SangsMap>
	 * @throws Exception
	 */
	/*
	public static ArrayList<SangsMap> loadExcelList(String fileFullPath, int skipRowCnt, int columnCount)
			throws Exception {
		ArrayList<SangsMap> rtnList = new ArrayList<SangsMap>();

		try {
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(fileFullPath));
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);

			int rows = sheet.getPhysicalNumberOfRows();

			for (int r = 0; r < rows; r++) {

				if (r < skipRowCnt)
					continue;

				HSSFRow row = sheet.getRow(r);
				if (row != null) {
					// int cells = row.getPhysicalNumberOfCells();
					SangsMap smap = new SangsMap();

					int chkColCnt = 0;
					for (int c = 0; c < columnCount; c++) {
						HSSFCell cell = row.getCell(c);

						if (cell != null) {
							String value = "";

							switch (cell.getCellType()) {
							case HSSFCell.CELL_TYPE_FORMULA:
								value = cell.getCellFormula();
								break;
							case HSSFCell.CELL_TYPE_NUMERIC:
								if (HSSFDateUtil.isCellDateFormatted(cell)) {
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
									value = sdf.format(cell.getDateCellValue());
								} else {

									value = String.valueOf(((Double) cell.getNumericCellValue()).longValue());
								}

								break;
							case HSSFCell.CELL_TYPE_STRING:
								value = cell.getStringCellValue();
								break;
							case HSSFCell.CELL_TYPE_BLANK:
								value = String.valueOf(cell.getBooleanCellValue());
								break;
							case HSSFCell.CELL_TYPE_ERROR:
								value = String.valueOf(cell.getErrorCellValue());
								break;
							default:
							}

							if ("false".equals(value))
								value = "";

							value = (SangsCollectorUtil.nvl(value, "")).trim();

							if (!"".equals(value)) {
								smap.put("col" + c, value);
								chkColCnt++;
							}
						}
					}

					if (chkColCnt > 0)
						rtnList.add(smap);
				}
			}
		} catch (Exception e) {

			throw e;
		}

		return rtnList;
	}
	 */
	// 날짜 유효성체크
	/*
	 * public static boolean dateFormatCheck(String source, String format) { boolean
	 * flag = true;
	 *
	 * if(isEmpty(source)) return false; if(isEmpty(format)) return false;
	 *
	 * SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.KOREA); Date
	 * date = null; try { date = formatter.parse(source); } catch (Exception e) {
	 * flag = false; } try { if (!formatter.format(date).equals(source)) flag =
	 * false; } catch (Exception e) { flag = false; } return flag; }
	 */

	/**
	 * 공통코드 리스트 안에 원하는 코드 or 코드 명이 있는지 체크
	 *
	 * @param list
	 * @param colNm -> 공통 코드인경우 code 혹은 name
	 * @param val   -> code혹은 name의 값
	 * @return
	 */
	public static boolean chkExistMtCd(List<SangsMap> list, String colNm, String val) {
		boolean flag = false;
		if (list == null)
			return false;

		for (int i = 0; i < list.size(); i++) {
			SangsMap tempMap = list.get(i);
			if (val.equals(tempMap.getString(colNm))) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * mt 코드의 코드명을 넣으면 코드가 반환된다.
	 *
	 * @param list
	 * @param codeNm
	 * @return
	 */
	public static String getMtCdByNm(List<SangsMap> list, String codeNm) {
		String rtnVal = "";

		if (list == null)
			return "";

		for (int i = 0; i < list.size(); i++) {
			SangsMap tempMap = list.get(i);
			if (codeNm.equals(tempMap.getString("name"))) {
				rtnVal = tempMap.getString("code");
				break;
			}
		}
		return rtnVal;
	}

	/**
	 * <p>
	 * XXXXXX - XXXXXXX 형식의 법인번호 앞, 뒤 문자열 2개 입력 받아 유효한 법인번호인지 검사.
	 * </p>
	 *
	 *
	 * @param 6자리 법인앞번호 문자열 , 7자리 법인뒷번호 문자열
	 * @return 유효한 법인번호인지 여부 (True/False)
	 */
	public static boolean checkBubinNumber(String bubin1, String bubin2) {

		String bubinNumber = bubin1 + bubin2;

		int hap = 0;
		int temp = 1; // 유효검증식에 사용하기 위한 변수

		if (bubinNumber.length() != 13)
			return false; // 법인번호의 자리수가 맞는 지를 확인

		for (int i = 0; i < 13; i++) {
			if (bubinNumber.charAt(i) < '0' || bubinNumber.charAt(i) > '9') // 숫자가 아닌 값이 들어왔는지를 확인
				return false;
		}

		// 2012.02.27 법인번호 체크로직 수정( i<13 -> i<12 )
		// 맨끝 자리 수는 전산시스템으로 오류를 검증하기 위해 부여되는 검증번호임
		for (int i = 0; i < 12; i++) {
			if (temp == 3)
				temp = 1;
			hap = hap + (Character.getNumericValue(bubinNumber.charAt(i)) * temp);
			temp++;
		} // 검증을 위한 식의 계산

		if ((10 - (hap % 10)) % 10 == Character.getNumericValue(bubinNumber.charAt(12))) // 마지막 유효숫자와 검증식을 통한 값의 비교
			return true;
		else
			return false;
	}

	/**
	 * <p>
	 * XXXXXXXXXXXXX 형식의 13자리 법인번호 1개를 입력 받아 유효한 법인번호인지 검사.
	 * </p>
	 *
	 *
	 * @param 13자리 법인번호 문자열
	 * @return 유효한 법인번호인지 여부 (True/False)
	 */
	public static boolean checkBubinNumber(String bubin) {

		if (bubin.length() != 13)
			return false;

		return checkBubinNumber(bubin.substring(0, 6), bubin.substring(6, 13));
	}

	/**
	 * XX - XXXXX 형식의 사업자번호 앞,중간, 뒤 문자열 3개 입력 받아 유효한 사업자번호인지 검사.
	 *
	 *
	 * @param 3자리 사업자앞번호 문자열 , 2자리 사업자중간번호 문자열, 5자리 사업자뒷번호 문자열
	 * @return 유효한 사업자번호인지 여부 (True/False)
	 */
	public static boolean checkCompNumber(String comp1, String comp2, String comp3) {

		String compNumber = comp1 + comp2 + comp3;

		int hap = 0;
		int temp = 0;
		int check[] = { 1, 3, 7, 1, 3, 7, 1, 3, 5 }; // 사업자번호 유효성 체크 필요한 수

		if (compNumber.length() != 10) // 사업자번호의 길이가 맞는지를 확인한다.
			return false;

		for (int i = 0; i < 9; i++) {
			if (compNumber.charAt(i) < '0' || compNumber.charAt(i) > '9') // 숫자가 아닌 값이 들어왔는지를 확인한다.
				return false;

			hap = hap + (Character.getNumericValue(compNumber.charAt(i)) * check[temp]); // 검증식 적용
			temp++;
		}

		hap += (Character.getNumericValue(compNumber.charAt(8)) * 5) / 10;

		if ((10 - (hap % 10)) % 10 == Character.getNumericValue(compNumber.charAt(9))) // 마지막 유효숫자와 검증식을 통한 값의 비교
			return true;
		else
			return false;
	}

	/**
	 * <p>
	 * XXXXXXXXXX 형식의 10자리 사업자번호 3개를 입력 받아 유효한 사업자번호인지 검사.
	 * </p>
	 *
	 *
	 * @param 10자리 사업자번호 문자열
	 * @return 유효한 사업자번호인지 여부 (True/False)
	 */
	public static boolean checkCompNumber(String comp) {
		if (comp.length() != 10)
			return false;
		return checkCompNumber(comp.substring(0, 3), comp.substring(3, 5), comp.substring(5, 10));

	}

	/**
	 * 일자 차이 반환
	 *
	 * @param from
	 * @param to
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static int getDayCountWithFormatter(String from, String to, String format) throws ParseException {
		long duration = getTimeCount(from, to, format);
		return (int) (duration / 0x5265c00L);
	}

	public static long getTimeCount(String from, String to, String format) throws ParseException {
		Date d1 = dateFormatCheck2(from, format);
		Date d2 = dateFormatCheck2(to, format);
		long duration = d2.getTime() - d1.getTime();
		return duration;
	}

	public static Date dateFormatCheck2(String source, String format) throws ParseException {
		if (source == null)
			throw new ParseException("date string to check is null", 0);
		if (format == null)
			throw new ParseException("format string to check date is null", 0);
		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.KOREA);
		Date date = null;
		try {
			date = formatter.parse(source);
		} catch (ParseException e) {
			throw new ParseException((new StringBuilder()).append(" wrong date:\"").append(source)
					.append("\" with format \"").append(format).append("\"").toString(), 0);
		}
		if (!formatter.format(date).equals(source))
			throw new ParseException((new StringBuilder()).append("Out of bound date:\"").append(source)
					.append("\" with format \"").append(format).append("\"").toString(), 0);
		else
			return date;
	}

	/**
	 * script 태그를 제거 한다.
	 *
	 * @param str
	 * @return
	 */
	public static String removeScriptTag(String str) {
		StringBuffer sb = new StringBuffer();
		sb.append(str);
		boolean flag = true;
		while (flag) {
			if (((sb.toString()).toLowerCase()).indexOf("<script") >= 0) {
				int tInt = ((sb.toString()).toLowerCase()).indexOf("<script");
				sb.replace(tInt, tInt + 7, "-script");
			}
			if (((sb.toString()).toLowerCase()).indexOf("<script") == -1)
				flag = false;
		}

		flag = true;
		while (flag) {
			if (((sb.toString()).toLowerCase()).indexOf("</script") >= 0) {
				int tInt = ((sb.toString()).toLowerCase()).indexOf("</script");
				sb.replace(tInt, tInt + 8, "-script");
			}
			if (((sb.toString()).toLowerCase()).indexOf("</script") == -1)
				flag = false;
		}
		return sb.toString();
	}

	/**
	 * 경로 문자열에 경로를 벗어나게 하는 코드를 삭제 시킨다.
	 *
	 * @param dir
	 * @return
	 */
	public static String removeJumpDir(String dir) {
		String rtnDir = "";

		rtnDir = dir.replace("../", "");
		rtnDir = rtnDir.replace("./", "");
		rtnDir = rtnDir.replace("..\\", "");
		rtnDir = rtnDir.replace(".\\", "");
		return rtnDir;
	}

	/**
	 * 파일이름 문자열에 경로를 벗어나게 하는 코드를 삭제 시킨다.
	 *
	 * @param fileName
	 * @return
	 */
	public static String removeJumpFileName(String fileName) {
		String rtnFileName = "";
		rtnFileName = fileName.replace("\\", "");
		rtnFileName = rtnFileName.replace("/", "");
		return rtnFileName;
	}

	/**
	 * 해당 폴더가 없으면 폴더 만들기
	 *
	 * @param path
	 */
	public static void makeFolder(String path) {
		File saveFolder = new File(path);
		if (!saveFolder.exists() || saveFolder.isFile()) {
			saveFolder.mkdirs();
		}
	}

	// 임시 비밀번호 10자리 반환(영문+숫자+특수문자)
	public static String getRandomPassword() {
		StringBuffer sb = new StringBuffer();
		StringBuffer sc = new StringBuffer("!@#$%^&*~");

		for (int i = 0; i < 8; i++) {
			int ri = getRandomNumNoOverlap(1, 10)[0];
			sb.append((char) ((ri) + 97));
		}
		for (int i = 0; i < 2; i++) {
			sb.append((char) (((getRandomNumNoOverlap(1, 10)[0])) + 48));
		}
		sb.setCharAt((int) (getRandomNumNoOverlap(1, 5)[0]),
				sc.charAt((int) (getRandomNumNoOverlap(1, sc.length() - 1)[0])));
		return sb.toString();
	}

	/**
	 * 겹치지않는 랜덤수 가져오기
	 *
	 * @param randomCnt 랜덤갯수
	 * @param rangeNum  0~rangeNum미만
	 * @return
	 */

	public static int[] getRandomNumNoOverlap(int randomCnt, int rangeNum) {

		/*
		 * 1) 숫서대로 배열을 만든다. 2) 랜덤값을 구한다. 3) 구한 랜덤값에 위치하는 값을 출력한다. 4) 구한 랜덤값의 위치에 배열의
		 * 맨마지막 값을 넣는다. 5) 구하는 랩덤값의 범위를 한칸 줄인다.
		 */

		int[] nResult = new int[randomCnt];
		int[] list = new int[rangeNum];

		for (int i = 0; i < rangeNum; i++) {// list에 0부터 endNum까지 숫자 세팅
			list[i] = i;
		}

		int lastNum = rangeNum;
		for (int i = 0; i < randomCnt; i++) { // 구하는 갯수만큼 for문 돌린다
			java.util.Random dd = new java.util.Random();
			int ran = (int) (dd.nextInt(rangeNum));
			nResult[i] = list[ran];
			list[ran] = list[--lastNum];
		}
		return nResult;
	}

	/**
	 * WEB 상에서 공백을 size 만큼 문자열로 만들어 반환한다.
	 *
	 * @param size
	 * @return
	 */
	public static String getWebSpaceText(int size) {
		if (size > 1000)
			return "";

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < size; i++) {
			sb.append("&nbsp;");
		}

		return sb.toString();
	}

	/**
	 * 현재 년도 부터 endYear 까지 목록을 반환 ( 2015 | 20
	 *
	 * @param startYear
	 * @return
	 */
	public static ArrayList<SangsMap> getYearList(int endYear) {

		ArrayList<SangsMap> yearList = new ArrayList<SangsMap>();

		int currYear = SangsCollectorUtil.getYear();
		for (int i = currYear; endYear <= i; i--) {
			SangsMap tempMap = new SangsMap();
			tempMap.put("code", String.valueOf(i));
			tempMap.put("name", String.valueOf(i));
			yearList.add(tempMap);
		}
		return yearList;
	}

	public static String getAddNumText(String args) {
		// TODO Auto-generated method stub

		String text1 = args; // 전달받은 param값
		String result[] = null; // 전달받은값에서 숫자부분만 들어간 값
		String tempText = null; // 숫자부분을 제외한 문자
		String result2; // 결과값

		try {
			// 0을 최우선으로 자르고 0이없을경우 1로 대체
			if (text1.indexOf("0", 0) != -1) {

				tempText = text1.substring(0, text1.indexOf("0", 0));

			} else if (text1.indexOf("0", 0) == -1) {

				tempText = text1.substring(0, text1.indexOf("1", 0));
			}

			// 전달받은 param의 숫자를 +1시킴
			result = text1.split(tempText);
			int tempInt = Integer.parseInt(result[1]);

			tempInt++;

			// 전달받은 param의 숫자길이를 구함
			int textLength = (result[1].length() - (String.valueOf(tempInt).length()));

			result2 = tempText;

			// 숫자값에 0이있을경우 가장 뒷자리만 0을 넣어주고
			// 그렇지않다면 그냥 붙임
			if (textLength > 0) {
				for (int i = 0; i < textLength; i++) {

					result2 = result2 + "0";

					if ((i + 1) == textLength) {
						result2 = result2 + "" + String.valueOf(tempInt);
					}

				}
			} else {

				result2 = result2 + String.valueOf(tempInt);

			}

			return result2;

		} catch (Exception e) {
			System.out.println("오류 : " + e);
			return args;
		}

	}

}
