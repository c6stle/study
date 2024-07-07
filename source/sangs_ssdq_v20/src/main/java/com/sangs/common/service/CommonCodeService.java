package com.sangs.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.support.CommonDao;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;

@SangsService
public class CommonCodeService extends ServiceBase {
	
	@Autowired
	private CommonDao dao;
	
	
	
	
	
	/**
	 * 공통 코드 조회 
	 * 
	 * @param paramMap parameter map
	 * @return 반환 map
	 * @throws SangsMessageException throws SangsMessageException
	 */
	public Map<String, Object> getCommonCodeList(Map<String, Object> paramMap) throws SangsMessageException {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			rtnMap.put("list", dao.selectList("cmmn_code.selectCommonCodeList", paramMap));
		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			throw new SangsMessageException("공통 코드 조회중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}
	
	/**
	 * <pre>
	 * <code>
	 * 공통 코드 그룹단위 조회 
	 *  - 다수의 공통 코드 그룹에 대한 코드 목록을 반환
	 *  Map<String, List<SangsMap>> codeListMap = commonCodeService.getGroupCodesCommonCodeList(new String[] {"LRNSTTS", "WRDCDTY"});
	 *  List<SangsMap>> lrnsttsCodeList = codeListMap.get("LRNSTTS");
	 *  List<SangsMap>> wrdcdtyCodeList = codeListMap.get("WRDCDTY");
	 *  
	 * </code>
	 * </pre>
	 * @param groupCmmnCodes 그룹코드배열
	 * @return Map 의 key 는 그룹코드 이고 value 는 그룹코드에 대한 코드 정보 목록
	 * @throws SangsMessageException
	 */
	public Map<String, List<SangsMap>> getGroupCodesCommonCodeList(String[] groupCmmnCodes) throws SangsMessageException {
		Map<String, List<SangsMap>> rtnCommonCodeList = new HashMap<String, List<SangsMap>>();
		
		try {
			Map<String, Object> searchMap = new HashMap<String, Object>();
			searchMap.put("list", groupCmmnCodes);
			
			List<SangsMap> list = dao.selectList("cmmn_code.selectMultiCommonCodeList", searchMap);
			
			for(SangsMap map : list) {
				
				String groupCode = map.getString("groupCmmnCd");
				List<SangsMap> codeList = null;
				
				if(rtnCommonCodeList.containsKey(groupCode))
					codeList = rtnCommonCodeList.get(groupCode);
				else 
					codeList = new ArrayList<SangsMap>();
				
				codeList.add(map);
				rtnCommonCodeList.put(groupCode, codeList);
			}
		} catch(Exception e) {
			logger.error("", e);
			throw new SangsMessageException("공통 코드 조회중 에러가 발생하였습니다.");
		}
		
		return rtnCommonCodeList;
	}
	
	/**
	 * 공통 코드 반환 <br>
	 * 공통 코드 리스트에 대해서 코드 + 리스트 , 코드 + 리스트 와 같이 반환해준다.<br> 
	 * 복수의 코드를 한번에 조회 할때 사용 한다. (UI onload 시 등)<br>
	 * SQL 코드는 공통 코드 테이블이 아닌 일반 테이블에 코드/명으로 표현할수 있는 코드성 테이블에 대해서 조회를 한다.<br>
	 *  - SQL 작성후 mybatis SQL ID 목록을 파라미터로 보내면 된다.  
	 * 
	 * 
	 * @param paramMap parameter map(key : COMMON_CODE_LIST or  SQL_CODE_LIST) 
	 * @return 반환 Map
	 * @throws SangsMessageException throws SangsMessageException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getCommonCodeMultiList(Map<String, Object> paramMap) throws SangsMessageException {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			logger.debug("paramMap " + paramMap);
			
			List<String> commonCodeList = (List<String>)paramMap.get("COMMON_CODE_LIST");
			List<String> sqlCodeList = (List<String>)paramMap.get("SQL_CODE_LIST");
			
			Map<String, List<SangsMap>> rtnCommonCodeList = new HashMap<String, List<SangsMap>>();
			Map<String, List<SangsMap>> rtnSqlCodeList = new HashMap<String, List<SangsMap>>();
			
			if(commonCodeList != null && commonCodeList.size() > 0)
				rtnCommonCodeList = getGroupCodesCommonCodeList(commonCodeList.toArray(new String[commonCodeList.size()]));
			
		 
			
			for(String str : sqlCodeList) {
				// "common.selectList:prjectSn=1,testParam=abc"
				String[] arrStr = str.split(":");
				String sqlId = "";
				
				Map<String, Object> searchMap = new HashMap<String, Object>();
				if(arrStr.length > 1) {
					sqlId = arrStr[0];
					String[] arrParams = arrStr[1].split(",");
					
					for(String param : arrParams) {
						String[] arrParamSet = param.split("=");
						searchMap.put(arrParamSet[0], arrParamSet[1]);
						
					}
				} else {
					sqlId = arrStr[0];
				}
				
				logger.debug("sql id : " + sqlId);
				List<SangsMap> list = dao.selectList(sqlId, searchMap);
				
				rtnSqlCodeList.put(str, list);
			}
			
			rtnMap.put("COMMON_CODE_LIST", rtnCommonCodeList);
			rtnMap.put("SQL_CODE_LIST", rtnSqlCodeList);
		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			logger.error("", e);
			throw new SangsMessageException("공통 코드 조회중 에러가 발생하였습니다.");
		}
		
		return rtnMap;
	}
	
	
 
	/**
	 * <pre>
	 * <code>
	 * 리스트 안에 있는 코드에 대한 코드명 setting  
	 * 
	 * commonCodeService.setCmmnCodeNmForList(list
	 * 		, new String[]{"CONFMCDTY","WRDCDTY"}
	 * 		, new String[]{"confmSttusCode", "wrdTyCode"}
	 * 		, new String[]{"confmSttusCodeNm", "wrdTyCodeNm"} );
	 * </code>
	 * </pre>
	 * @param list
	 * @param groupCodes
	 * @param codeKeys
	 * @param nameKeys
	 */
	public void setCmmnCodeNmForList(List<SangsMap> list, String[] groupCodes, String[] codeKeys, String[] nameKeys) {
		
		if(groupCodes == null || codeKeys == null || nameKeys == null)
			throw new SangsMessageException("input 요소 groupCodes, codeKeys 혹은 nameKeys 는 null 일수 없습니다.");
		
		if(groupCodes.length != codeKeys.length || codeKeys.length != nameKeys.length)
			throw new SangsMessageException("input 요소 groupCodes, codeKeys 혹은 nameKeys 의 length 가 같은 배열로 입력 해야 합니다.");
		
		Map<String, List<SangsMap>> codeListMap = getGroupCodesCommonCodeList(groupCodes);
		
		for(SangsMap map : list) {
			for(int i = 0 ; i < codeKeys.length ; i++) {
				String codeKey = codeKeys[i];
				String codeVal = map.getString(codeKey);
 				if(!"".equals(codeVal)) {
					List<SangsMap> clist = codeListMap.get(groupCodes[i]);
					for(SangsMap codeMap : clist) {
						if((codeMap.getString("code")).equals(codeVal)) {
							map.putOrg(nameKeys[i], codeMap.getString("codeNm"));
						}
					}
				}
			}
		}
	}
	
	

}
