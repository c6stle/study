package com.sangs.fwk.common;

public class SangsConstants {

	//public static String LOGIN_PAGE = "/login/login";
	//public static String MAIN_PAGE = "/open/main/main";
	//public static int DEFAULT_PAGE_SIZE = 10;
	
	public static enum COMMON_RESULT_CODE {SUCCESS, FAIL};
	
	public static final String FORWARD_VIEW = "_FORWARD_VIEW_"; 
	
	public static final int DEFAULT_LIST_ROW_SIZE = 20; 
	
	public static final String FWK_ERROR_MESSAGE_KEY = "FWK_ERROR_MESSAGE";
	
	public static final String STANDARD_USER_SESSION_KEY = "STANDARD_USER_SESSION_KEY";
	
	public static final String ADMIN_USER_SESSION_KEY = "ADMIN_USER_SESSION_KEY";
	
	public enum LIMIT_TYPE_DBMS {mysql,mariadb}
	
	public static String FWK_DBMS_TYPE = "";
	
	public static String ACCESS_AUTHOR_CODE = "ACCESS";
	
	public static String AUTH_CHECK_IGNORE_URLS = "";
	
	public static String OPENMENU_AUTH_CHECK_YN = "";
	
	public static int MNGR_LOGIN_FAIL_CTL_DEFAULT_CNT = 5;
	
	public static String MNGR_LOGIN_INIT_PWD = "sangs#1234@";
	
	public static String APP_MNGR_SYS_SE_CD = "MNGR";

	public static String APP_WWW_SYS_SE_CD = "WWW";
			
	
}
