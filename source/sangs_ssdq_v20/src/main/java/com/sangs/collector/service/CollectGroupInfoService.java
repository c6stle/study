package com.sangs.collector.service;

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
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsStringUtil;
import com.sangs.lib.support.utils.SangsWebUtil;

@SangsService
public class CollectGroupInfoService extends ServiceBase {

	@Autowired
	private CommonDao dao;

	/**
	 * 메타 그룹 정보를 등록
	 *
	 * @param paramMap
	 * @param
	 * @throws Exception Exception
	 */
	public Map<String, Object> saveMetaGroup(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		//중복 체크
		try {
			int cnt = dao.selectCount("ct_collect_group.selectMetaGroupInfoCkDup", paramMap);
			
			String apiGroupNm = SangsWebUtil.clearXSSMinimum((String) paramMap.get("apiGroupNm"));
			String apiStngNm = SangsWebUtil.clearXSSMinimum((String) paramMap.get("apiStngNm"));
			String apiGroupCn = SangsWebUtil.clearXSSMinimum((String) paramMap.get("apiGroupCn"));
			
			paramMap.put("apiGroupNm", apiGroupNm);
			paramMap.put("apiStngNm", apiStngNm);
			paramMap.put("apiGroupCn", apiGroupCn);
			
			if(cnt > 0) {
				dao.update("ct_collect_group.updateMetaGroup", paramMap);
			}else {
				dao.insert("ct_collect_group.insertMetaGroup", paramMap);
			}

			rtnMap.put("resultCd" , "OK");
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}

	/**
	 * 메타 그룹 리스트를 조회
	 *
	 * @param paramMap
	 * @return 반환 map
	 * @throws Exception Exception
	 */
	public Map<String, Object> selectMetaGroupList(Map<String, Object> paramMap) throws Exception {
		int cnt = dao.selectCount("ct_collect_group.selectMetaGroupListCnt", paramMap);
		int totalCnt = SangsStringUtil.nvlInt(cnt);
		int pageNum = (int) paramMap.get("pageIndex");


		SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCnt, pageNum,	SangsCmmnSuportConstants.DEFAULT_LIST_ROW_SIZE);

		paramMap.put("pageSize", pagingInfo.getPageSize());
		paramMap.put("offset", pagingInfo.getOffset());
		List<SangsMap> list = dao.selectList("ct_collect_group.selectMetaGroupList", paramMap);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		map.put("pagingInfo", pagingInfo);

		return map;

	}

	/**
	 * 메타 그룹 상세 조회
	 *
	 * @param paramMap
	 * @return 반환 map
	 * @throws Exception Exception
	 */
	public Map<String, Object> selectMetaGroupInfo(Map<String, Object> paramMap) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		map = dao.selectOne("ct_collect_group.selectMetaGroupInfo", paramMap);

		return map;
	}

	/**
	 * 메타 그룹 중복 조회
	 *
	 * @param paramMap
	 * @return 반환 map
	 * @throws Exception Exception
	 */
	public Map<String, Object> selectMetaGroupInfoCkDup(Map<String, Object> paramMap) throws Exception {
		int cnt = dao.selectCount("ct_collect_group.selectMetaGroupInfoCkDup", paramMap);


		Map<String, Object> map = new HashMap<String, Object>();

		map.put("cnt", cnt);
		return map;
	}
}