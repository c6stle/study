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
public class LoginLogService extends ServiceBase {
	
	@Autowired
	private CommonDao dao;
	
	/**
	 * 시스템 로그인 로그 정보를 등록
	 *
	 * @param paramMap
	 * @param 
	 * @throws Exception Exception
	 */
	public void insertLoginLog(Map<String, Object> paramMap) throws Exception {
		
		dao.insert("cmmn_login_log.insertLoginLog", paramMap);
	}
	
	/**
	 * 시스템 로그인 로그 정보를 조회
	 *
	 * @param paramMap
	 * @param 
	 * @throws Exception Exception
	 */
	public Map<String, Object> selectLoginLogList(Map<String, Object> paramMap) throws Exception {
		int cnt = dao.selectCount("cmmn_login_log.selectLoginLogListCnt", paramMap);
		int totalCnt = SangsStringUtil.nvlInt(cnt);
		int pageNum = (int) paramMap.get("pageIndex");
		
		
		SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCnt, pageNum,	SangsCmmnSuportConstants.DEFAULT_LIST_ROW_SIZE);
		
		paramMap.put("pageSize", pagingInfo.getPageSize());
		paramMap.put("offset", pagingInfo.getOffset());
		List<SangsMap> list = dao.selectList("cmmn_login_log.selectLoginLogList", paramMap);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		map.put("pagingInfo", pagingInfo);
		
		return map;
	}
}