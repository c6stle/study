package com.sangs.common.support;

import com.sangs.fwk.support.SangsAuthUtil;
import com.sangs.lib.support.exception.SangsNoneAuthException;
import com.sangs.lib.support.utils.SangsStringUtil;

public class AuthUtil extends SangsAuthUtil  {

	public static int getPrjctSn() {
		return Integer.parseInt(getUserAttr("prjctSn"));
	}
	public static String getPrjctSnStr() {
		return getUserAttr("prjctSn");
	}
	public static int getPrjctNm() {
		return Integer.parseInt(getUserAttr("prjctNm"));
	}
	public static int getStdSetSn() {
		if(SangsStringUtil.isEmpty(getUserAttr("stdSetSn")))
			return 0;
		return Integer.parseInt(getUserAttr("stdSetSn"));
	}
	public static String getStdSetNm() {
		return getUserAttr("stdSetNm");
	}
	public static String getAuthrtCd() {
		return getUserAttr("authrtCd");
	}

	// 승인자 여부
	public static String isApprover() {
		return getUserAttr("isApprover");	
	}
	
	public static String getDbmsCnncSnStr() {
		return getUserAttr("dbmsCnncSn");
	}
	
	public static int getDbmsCnncSn() {
		return Integer.parseInt(getUserAttr("dbmsCnncSn"));
	}
	
	public static String getDbmsIpAddr() {
		return getUserAttr("dbmsIpAddr");
	}
	public static String getDbmsPortNo() {
		return getUserAttr("dbmsPortNo");
	}
	public static String getDbmsDatabaseNm() {
		return getUserAttr("dbmsDatabaseNm");
	}
	
	public static String getDbmsSchemaNm() {
		return getUserAttr("dbmsSchemaNm");
	}
	public static String getDbmsSidNm() {
		return getUserAttr("dbmsSidNm");
	}
	public static String getDbmsNm() {
		return getUserAttr("dbmsNm");
	}
	public static String getDbmsSn() {
		return getUserAttr("dbmsSn");
	}
	public static String getDbmsDatabaseCn() {
		return getUserAttr("dbmsDatabaseCn");
	}
	public static String getDbmsId() {
		return getUserAttr("dbmsId");
	}
	 

	
	/**
	 * 접근하려는 메뉴 URL에 권한항목코드에 대해서 권한이 없는경우 권한없읍 Exception 발생 
	 * @param menuAuthrtCd
	 */
	public static void checkMenuAuthrtCd(String menuAuthrtCd) {
		String urlAddr = BizUtil.getRequestURI();
		logger.debug("access url : " + urlAddr);
		
		if(!existAuthMenuItemCdByMenuUrl(urlAddr, menuAuthrtCd))
			throw new SangsNoneAuthException();
	}
	
	

}
