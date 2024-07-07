package com.sangs.common.base;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sangs.lib.support.utils.SangsStringUtil;

public class ServiceBase {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	public static int PER_PAGE = 5;
	
	public void pagingSet(Map<String, String> params) {

		int pageIndex = 1;	// 페이지번호
		int perPage = 0;
		int itemsPerPage = params.get("itemsPerPage") == null ? 0 : Integer.parseInt(params.get("itemsPerPage"));

		perPage = params.get("itemsPerPage") == null ? PER_PAGE : itemsPerPage;
		
		int startRow = 0;
		int endRow = 0;
		
		if(!SangsStringUtil.isEmpty(params.get("pageIndex"))) {
			pageIndex = Integer.parseInt(params.get("pageIndex"));
		}
		
		if(!SangsStringUtil.isEmpty(params.get("perPage"))) {
			perPage = Integer.parseInt(params.get("perPage"));
		}
		
		startRow = ((pageIndex - 1) * perPage) + 1;
		endRow = pageIndex * perPage;
		
		params.put("start_row", String.valueOf(startRow));
		params.put("end_row", String.valueOf(endRow));
		 
	}
	
	public void pagingSetMySql(Map<String, Object> params) {

		int pageIndex = MapUtils.getIntValue(params, "pageIndex", 1);	// 페이지번호
		int itemsPerPage = MapUtils.getIntValue(params, "itemsPerPage", PER_PAGE);
		int perPage = MapUtils.getIntValue(params, "perPage", itemsPerPage);

		params.put("startIdx", (pageIndex - 1) * perPage);
		params.put("perPage", perPage);
	}
	
}
