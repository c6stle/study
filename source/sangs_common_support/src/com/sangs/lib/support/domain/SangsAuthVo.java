package com.sangs.lib.support.domain;

import java.util.List;
import java.util.Map;

/**
 * 사용자권한 VO<br> 
 * - 세션, 사용자 인증시 사용하는 사용자, 권한 VO<br>
 * - SangsAbstractAuthBaseVo 추상클래스 구현 클래스
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
public class SangsAuthVo extends SangsAbstractAuthBaseVo {

	/**
	 * 세션에 사용자 아이디 set
	 * 
	 * @param userId 사용자 아이디
	 */
	public void setUserId(String userId) {
		super.userId = userId;
	}
	/**
	 * 세션에 사용자명 set
	 * @param userNm 사용자명
	 */
	public void setUserNm(String userNm) {
		super.userNm = userNm;
	}
	/**
	 * 세션에 사용자 속성 Map set 
	 * 
	 * @param userAttrMap 사용자 속성Map (사용자 속성을 key, value 로 된 Map) 
	 */
	public void setUserAttrMap(Map<String, String> userAttrMap) {
		super.userAttrMap = userAttrMap;
	}
	
	/**
	 * 세션상의 사용자 아이디 반환
	 * @return 세션상의 사용자 아이디 반환
	 */
	@Override
	public String getUserId() {
		return super.userId;
	}

	/**
	 * 세션상의 사용자명 반환
	 * 
	 * @return 세션상의 사용자명 반환 
	 */
	public String getUserNm() {
		return super.userNm;
	}

	/**
	 * 세션상의 사용자속성 Map 반환
	 * 
	 * @return 세션상의 사용자 속성Map 반환 
	 */
	@Override
	public Map<String, String> getUserAttrMap() {
		return super.userAttrMap;
	}
	
	/**
	 * 세션상의 사용자 속성 값 반환 
	 * 
	 * @param key 사용자 속성 Map의 key 값
	 * @return 사용자 속성 Map 의 key 값에 대한 value 값 반환
	 */
	public String getUserAttr(String key) {
		Map<String, String> userAttrMap = getUserAttrMap();
		if(userAttrMap == null || !userAttrMap.containsKey(key))
			return "";
		return userAttrMap.get(key);
	}
  
	/**
	 * 세션에 사용자 메뉴목록 셋팅
	 * @param userSessionMenuList SangsMap 타입의 목록(메뉴목록)
	 */
	@Override
	public void setUserSessionMenuList(List<SangsMap> userSessionMenuList) {
		super.userSessionMenuList = userSessionMenuList;
	}
	
	/**
	 * 구현대상 세션의 사용자목록 조회
	 * @return 세션의 메뉴목록(SangsMap 타입의 목록)
	 */
	@Override
	public List<SangsMap> getUserSessionMenuList() {
		return super.userSessionMenuList;
	}
	
	
	
 
	
	
}
