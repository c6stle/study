package com.sangs.lib.support.domain;

import java.util.List;
import java.util.Map;

/**
 * 사용자 권한관리를 위한 추상 VO
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
 *   2022.5.02    id.yoon     최초 생성
 * </pre>
 */
public abstract class SangsAbstractAuthBaseVo {
	
	/**
	 * 사용자 아이디 
	 */
	public String userId;
	public String userNm;
	/**
	 * 사용자 속성 Map (사용자 추가 속성에 대해 key, value로 값이 들어있음)
	 */
	public Map<String, String> userAttrMap;
	
	/**
	 * 구현 대상 사용자 아이디 
	 * @return 사용자 아이디
	 */
	public abstract String getUserId();
	/**
	 * 구현 대상 사용자명
	 * @return 사용자명
	 */
	public abstract String getUserNm();
	/**
	 * 구현 대상 사용자 속성 Map
	 * @return 사용자속 성Map
	 */
	public abstract Map<String, String> getUserAttrMap();
	

	/**
	 * 세션상의 사용자 메뉴목록
	 */
	public List<SangsMap> userSessionMenuList = null;
	
	/**
	 * 구현대상 세션에 사용자 메뉴목록 셋팅
	 * @param userSessionMenuList SangsMap 타입의 목록(메뉴목록)
	 */
	public abstract void setUserSessionMenuList(List<SangsMap> userSessionMenuList);
	
	/**
	 * 구현대상 세션의 사용자목록 조회
	 * @return 세션의 메뉴목록
	 */
	public abstract List<SangsMap> getUserSessionMenuList();
	
	
	
	
	
}
