package com.sangs.dq.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.common.service.DbmsConnService;
import com.sangs.common.support.CommonDao;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.fwk.common.SangsConstants;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.domain.SangsPagingViewInfo;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsStringUtil;

@SangsService
public class ApiDqService {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CommonDao dao;
	
	@Autowired
	private DbmsConnService dbmsConnService;
	
	/**
	 * 프로파일 목록 조회
	 * 
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> getApiProfileList(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			int pageNum = SangsStringUtil.nvlInt(params.get("pageNum"), 1);
			int totalDataCount = dao.selectCount("api_dq.selectApiProfileListCnt", params);
			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalDataCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);

			boolean isLastPageYn = false;
			
			if(!params.containsKey("allDataSearchYn") && !"Y".equals(params.get("allDataSearchYn"))) {
				params.put("allDataSearchYn", "N");
				params.put("pageSize", pagingInfo.getPageSize());
				params.put("offset", pagingInfo.getOffset());
				
			}
			
			if (pageNum >= pagingInfo.getTotalPageNum()) {
				isLastPageYn = true; // Y
			}
			List<SangsMap> profileList = dao.selectList("api_dq.selectApiProfileList", params);
			
			if(profileList.isEmpty()) {
				rtnMap.put("resultCd", "FAIL");
				rtnMap.put("resultMsg", "prjctSn에 대한 데이터가 없습니다.");
			} else {
				rtnMap.put("lastPageYn", isLastPageYn ? "Y" : "N");
				rtnMap.put("totalDataCount", totalDataCount);
				rtnMap.put("list", profileList);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("getProfileList 조회 오류");
		}

		return rtnMap;
	}
	
	/**
	 * 
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> getProfileDbmsCnncinfo(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			
			SangsMap cnncInfo = dao.selectOne("api_dq.selectApiDbmsCnncinfo", params);
			
			Map<String, Object> dbmsCnncInfo = new HashMap<String, Object>();
			
			dbmsCnncInfo.put("dbmsPortNo", cnncInfo.get("dbmsPortNo"));
			dbmsCnncInfo.put("dbmsId", cnncInfo.get("dbmsId"));
			dbmsCnncInfo.put("dbmsIpAddr", cnncInfo.get("dbmsIpAddr"));
			dbmsCnncInfo.put("dbmsPassword", cnncInfo.get("dbmsPassword"));
			dbmsCnncInfo.put("dbmsDatabaseNm", cnncInfo.get("dbmsDatabaseNm"));
			dbmsCnncInfo.put("dbmsSidNm", cnncInfo.get("dbmsSidNm"));
			dbmsCnncInfo.put("dbmsSchemaNm", cnncInfo.get("dbmsSchemaNm"));
			dbmsCnncInfo.put("dbmsNm", cnncInfo.get("dbmsNm"));
			dbmsCnncInfo.put("dbmsCnncSn", cnncInfo.get("dbmsCnncSn"));
			
			Map<String, Object> connResultMap = dbmsConnService.doConnectionTest(dbmsCnncInfo);
			
			rtnMap.put("result", connResultMap.get("result"));
			rtnMap.put("dbmsCnncInfo", dbmsCnncInfo);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("getProfileDbmsCnncinfo 조회 오류");
		}

		return rtnMap;
	}
	
	
	/**
	 * 
	 * @param params
	 * @return 
	 * @throws Exception
	 */
	public Map<String, Object> getProflExcResultDisMatchList(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			SangsMap resultInfo = dao.selectOne("api_dq.selectApiProfilResultInfo", params);
			if (resultInfo == null || resultInfo.isEmpty()) {
				rtnMap.put("resultCd", "FAIL");
				rtnMap.put("resultMsg", "excSn에 대한 데이터가 없습니다.");
			} else {
				int pageNum = SangsStringUtil.nvlInt(params.get("pageNum"), 1);
				int totalDataCount = dao.selectCount("api_dq.selectApiProfilResultDisMatchListCnt", params);

				SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalDataCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);

				boolean isLastPageYn = false;

				if (!params.containsKey("allDataSearchYn") && !"Y".equals(params.get("allDataSearchYn"))) {
					params.put("allDataSearchYn", "N");
					params.put("pageSize", pagingInfo.getPageSize());
					params.put("offset", pagingInfo.getOffset());
				}

				if (pageNum >= pagingInfo.getTotalPageNum()) {
					isLastPageYn = true; // Y
				}

				List<SangsMap> resultDisMatchList = dao.selectList("api_dq.selectApiProfilResultDisMatchList", params);

				for (Map<String, Object> map : resultDisMatchList) {
					map.remove("tblSn");
					map.remove("colSn");
				}

				rtnMap.put("lastPageYn", isLastPageYn ? "Y" : "N");
				rtnMap.putAll(resultInfo);
				rtnMap.put("list", resultDisMatchList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("getProfileList 조회 오류");
		}

		return rtnMap;
	}
	
}
