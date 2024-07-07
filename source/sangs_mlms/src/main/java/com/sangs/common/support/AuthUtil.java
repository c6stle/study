package com.sangs.common.support;

import com.sangs.fwk.support.SangsAuthUtil;
import com.sangs.lib.support.utils.SangsStringUtil;

public class AuthUtil extends SangsAuthUtil  {

	public static int getPrjctSn() {
		return Integer.parseInt(getUserAttr("prjctSn"));
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
	@Deprecated
	public static String getRegisterId() {
		return getUserAttr("regUserId");
	}
	// 승인자 여부
	public static String isApprover() {		
		return getUserAttr("isApprover");	
	}
	

}
