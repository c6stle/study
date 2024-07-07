package com.sangs.authmng.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.authmng.base.AuthMngServiceBase;
import com.sangs.authmng.support.AuthMngCommonDao;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.fwk.common.SangsConstants;
import com.sangs.fwk.support.SangsAuthUtil;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsCallServiceException;
import com.sangs.lib.support.exception.SangsMessageException;

/**
 * 
 * @Method Name : AuthMngMenuService
 * @date : 2021. 9. 30
 * @author : ow.park
 * @history :
 * ----------------------------------------------------------------------------------
 * 변경일                        작성자                              변경내역
 * -------------- -------------- ----------------------------------------------------
 * 2021. 9. 30      ow.park              최초작성
 * ----------------------------------------------------------------------------------
 */

@SangsService
public class AuthMngMenuService extends AuthMngServiceBase {
	
	@Autowired
	private AuthMngCommonDao dao;
	
	//@Autowired
	//private DefaultListableBeanFactory df;
	
	
	/**
	 * 관리자 접근 가능 한 메뉴 목록 셋팅
	 * (세션에 접근 가능 메뉴 목록을 셋팅한다)
	 * @param paramMap
	 * @return
	 * @throws SangsCallServiceException
	 */
	public void setMngrAccessibleMenuList() throws SangsCallServiceException {
		try {
			
			Map<String, Object> paramMap = new HashMap<String, Object>();
			
			paramMap.put("userId", SangsAuthUtil.getUserId());
			paramMap.put("accessAuthorCode", SangsConstants.ACCESS_AUTHOR_CODE);
			
			// 메뉴목록 조회 
			List<SangsMap> list = dao.selectList("authMngMenu.selectMngrLoginMenuList", paramMap);
			
			// 사용자 메뉴 권한 항목 Map 조회
			Map<String, Map<String,String>> userMenuAuthrtCdMap = getUserMenuAuthrtCdMap(paramMap);
 
			for(SangsMap smap : list) {
				String menuSn = smap.getString("menuSn");
				if(userMenuAuthrtCdMap.containsKey(menuSn))
					smap.putOrg("authCdMap",userMenuAuthrtCdMap.get(menuSn));
			}
			
			// 세션에 메뉴정보 셋팅
			SangsAuthUtil.setUserSessionMenuList(list);
			
		} catch(SangsMessageException e) {
			e.printStackTrace();
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		
	}
	
	
	
	public Map<String, Object> getMngrLoginMenuTreeList(Map<String, Object> paramMap) throws SangsCallServiceException {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			paramMap.put("userId", SangsAuthUtil.getUserId());
			paramMap.put("accessAuthorCode", SangsConstants.ACCESS_AUTHOR_CODE);
			
			// 메뉴목록 조회 
			List<SangsMap> list = dao.selectList("authMngMenu.selectMngrLoginMenuList", paramMap);
			
			int menuMaxDepthCnt = 0;
			if(list != null && list.size() > 0) { 
				int startIdx = list.size() - 1;
				for(int i = startIdx ; i >= 0 ; i--) {
					
					SangsMap smap = list.get(i);
					/*
					if("N".equals(smap.getString("dtlPageYn")) && smap.getInt("childCnt") == 0) {
						list.remove(i);
						continue;
					}
					*/
					if("Y".equals(smap.getString("singlPageYn"))) {
						list.remove(i);
						continue;
					}
					
					// 싱글페이지는 나오지 않도록 
					int tempDpSn = smap.getInt("menuDpSn");
					if(menuMaxDepthCnt < tempDpSn)
						menuMaxDepthCnt = tempDpSn;
				}
			}
			
			
			// 메뉴 트리를 만들기 위한 node 끊기는 메뉴 제거
			rtnMap.put("maxDpCnt", menuMaxDepthCnt);
			rtnMap.put("list", list);
			
		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}
	
	
	public Map<String, Object> getMenuList(Map<String, Object> paramMap) throws SangsCallServiceException {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			logger.debug("#####----> dao : " + dao);
			
			//for(String name : df.getBeanDefinitionNames()) {
			//	System.out.println("#### bean : " + name +"==>>" + df.getBean(name).getClass().getName());
			//}
				
			// 메뉴 maxDepth 
			int menuMaxDepthCnt = dao.selectInteger("authMngMenu.selectMenuMaxDepthCnt", paramMap);
			
			// 메뉴목록 조회 
			List<SangsMap> list = dao.selectList("authMngMenu.selectMenuList", paramMap);
			
			rtnMap.put("maxDpCnt", menuMaxDepthCnt);
			rtnMap.put("list", list);
			
		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}
	
	public Map<String, Object> getAuthMenuList(Map<String, Object> paramMap) throws SangsCallServiceException {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			// 메뉴 목록 조회
			List<SangsMap> list = dao.selectList("authMngMenu.selectMenuList", paramMap);
			// 메뉴 권한 상세 조회
			List<SangsMap> authDtlList = dao.selectList("authMngMenu.selectMenuAuthDtlList", paramMap);
			
			rtnMap.put("list", list);
			rtnMap.put("authDtlList", authDtlList);
			
		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveMenuExecInfo(Map<String, Object> paramMap) throws SangsCallServiceException {
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			Map<String, Object> menuMap = (Map<String, Object>)paramMap.get("menuInfo");
			List<SangsMap> childrenArr = (List<SangsMap>)paramMap.get("childrenArr");
			String pmode = (String)menuMap.get("pmode");
			
			//menuMap.putAll(paramMap);
			menuMap.put("regUserId", SangsAuthUtil.getUserId());
			
			if("M".equals(pmode)) {
				
				// 수정
				dao.update("authMngMenu.updateMenuInfo", menuMap);
				
				// 상위 노드 sysSeCd, menuSeCd 수정 시 하위 노드 일괄 적용
				if(childrenArr.size() > 0) {
					
					for(Map<String, Object> map : childrenArr) {
						SangsMap cMap = new SangsMap();
						
						cMap.putOrg("menuSn", map.get("menuSn"));
						cMap.putOrg("sysSeCd", (String)map.get("sysSeCd"));
						cMap.putOrg("menuSeCd", (String)map.get("menuSeCd"));
						cMap.putOrg("regUserId", SangsAuthUtil.getUserId());
						
						dao.update("authMngMenu.updateMenuCdInfo", cMap);
					}
				}
				
			
			} else if("C".equals(pmode)){
				int menuSn = dao.selectInteger("authMngMenu.selectNextMenuSn", paramMap);
				menuMap.put("menuSn", menuSn);
				// 메뉴 등록
				dao.insert("authMngMenu.insertMenuInfo", menuMap);
				
				/* 메뉴 추가시 기본권한 부여 하던것 제거 2022.06.02
				// 권한 목록 조회 
				List<SangsMap> authlist = dao.selectList("authMngAuth.selectAuthorList", paramMap);
				
				for(Map<String, Object> map : authlist) {
					SangsMap aMap = new SangsMap();
					aMap.putOrg("authrtCd", map.get("authrtCd"));
					aMap.putOrg("menuSn", menuSn);
					aMap.putOrg("menuAuthrtCd", SangsConstants.ACCESS_AUTHOR_CODE);
					aMap.putOrg("regUserId", SangsAuthUtil.getUserId());
					
					// 메뉴 권한 상세 등록
					dao.insert("authMngAuth.insertMenuAuthorDetailInfo", aMap);
				}
				*/
			}
			
			rtnMap.put("resultCd", "OK");
			
		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		 
		return rtnMap;
	}
	
	
	/**
	 * 사용자 메뉴 권한 항목 Map 조회
	 * @param paramMap
	 * @return
	 * @throws SangsCallServiceException
	 */
	private Map<String, Map<String,String>> getUserMenuAuthrtCdMap(Map<String, Object> paramMap) throws SangsCallServiceException {
		Map<String, Map<String,String>> userMenuAuthrtCdMap = new HashMap<String, Map<String, String>>();
		try {
			List<SangsMap> userMenuAuthrtCdList = dao.selectList("authMngMenu.selectUserMenuAuthrtCdList", paramMap);
			
			for(SangsMap smap: userMenuAuthrtCdList) {
				String menuSn = smap.getString("menuSn");
				String menuAuthrtCd = smap.getString("menuAuthrtCd");
				
				Map<String, String> tempMap = null;
				if(!userMenuAuthrtCdMap.containsKey(menuSn)) 
					tempMap = new HashMap<String, String>();
				else 
					tempMap = userMenuAuthrtCdMap.get(menuSn);
				
				tempMap.put(menuAuthrtCd, "Y");
				
				userMenuAuthrtCdMap.put(menuSn, tempMap);
			}
			
		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		
		return userMenuAuthrtCdMap;
	}
	
	
	
	
}
