package com.sangs.common.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.support.CommonDao;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.lib.support.common.SangsCmmnSuportConstants;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.domain.SangsPagingViewInfo;
import com.sangs.lib.support.utils.SangsStringUtil;

@SangsService
public class WebLogService extends ServiceBase {

	@Autowired
	private CommonDao dao;

	/**
	 * 시스템 웹 로그 정보를 등록
	 *
	 * @param paramMap
	 * @param 
	 * @throws Exception Exception
	 */
	public void insertWebLog(Map<String, Object> paramMap) throws Exception {
		
		dao.insert("cmmn_web_log.logInsertWebLog", paramMap);
	}
	
	/**
	 * 시스템 웹 로그 정보를 조회
	 *
	 * @param paramMap
	 * @param 
	 * @throws Exception Exception
	 */
	public Map<String, Object> selectWebLogList(Map<String, Object> paramMap) throws Exception{
		int cnt = dao.selectCount("cmmn_web_log.selectWebLogListCnt", paramMap);
		int totalCnt = SangsStringUtil.nvlInt(cnt);
		int pageNum = (int) paramMap.get("pageIndex");
		
		
		SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCnt, pageNum, SangsCmmnSuportConstants.DEFAULT_LIST_ROW_SIZE);
		
		paramMap.put("pageSize", pagingInfo.getPageSize());
		paramMap.put("offset", pagingInfo.getOffset());
		List<SangsMap> list = dao.selectList("cmmn_web_log.selectWebLogList", paramMap);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		map.put("pagingInfo", pagingInfo);
		
		return map;
	}
}