package com.sangs.collector.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.support.CommonDao;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.fwk.common.SangsConstants;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.domain.SangsPagingViewInfo;
import com.sangs.lib.support.utils.SangsStringUtil;

/**
 * Description : 메타 통합관리 > 수집로그정보 관련 Service 상세
 *
 * Modification Information
 * 수정일		수정자			수정내용
 * -------		-----------------------------------
 * 2016.01.29 	조남훈			최초작성
 *
 */

@SangsService
public class CollectLogInfoService extends ServiceBase {


	@Autowired
	private CommonDao dao;

	/**
	 *  수집로그 목록 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectMetaMngCollectLogList(Map<String, Object> params) throws Exception {

		// 페이징
		int page = SangsStringUtil.nvlInt(params.get("pageIndex"), 1);
		int pagingRowSize = SangsConstants.DEFAULT_LIST_ROW_SIZE;

		// 카운트 조회
    	int cnt = dao.selectCount("ct_collect_log.selectMetaMngCollectLogListCnt", params);

    	// 엑셀일때는 조회된 갯수만큼 다 나오게 하기 위해서
    	if("EXCEL".equals(SangsStringUtil.nvl(params.get("listType"))) || "ALL".equals(SangsStringUtil.nvl(params.get("listType")))) {
    		//params.put("perPage", String.valueOf(cnt));	// 페이지당 게시물 수 set
    		pagingRowSize = cnt;
    	}

    	// 페이징 파라미터 셋팅
    	//super.pagingSet(params);
    	SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(cnt, page, pagingRowSize);

    	params.put("start_row", pagingInfo.getStartRow());
    	params.put("end_row", pagingInfo.getEndRow());

    	// 리스트 조회
    	List<SangsMap> result = dao.selectList("ct_collect_log.selectMetaMngCollectLogList", params);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultList", result);
		map.put("resultCnt", Integer.toString(cnt));
		map.put("pagingInfo", pagingInfo);

		return map;
	}

	/**
	 * 수집로그 목록 카운트조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int selectMetaMngCollectLogListCnt(Map<String, Object> params) throws Exception {
		return dao.selectCount("ct_collect_log.selectMetaMngCollectLogListCnt", params);
	}

	/**
	 *  수집로그 상세정보
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public SangsMap selectMetaMngCollectLogInfo(Map<String, Object> params) throws Exception {
		return dao.selectOne("ct_collect_log.selectMetaMngCollectLogInfo", params);
	}

	/**
	 *  수집 로그 일련번호 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int selectMetaMngCollectLogNextSeq(Map<String, Object> params) throws Exception {
		return dao.selectInteger("ct_collect_log.selectMetaMngCollectLogNextSeq", params);
	}
	/**
	 *  selectMetaMngCollectLogNextSe
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public void insertMetaMngCollectLogInfo(Map<String, Object> params) throws Exception {
		dao.insert("ct_collect_log.insertMetaMngCollectLogInfo", params);
	}

	/**
	 * 수집 로그 삭제
	 * @param params
	 * @throws Exception
	 */
	public void deleteMetaMngCollectLogInfo(Map<String, Object> params) throws Exception {
		dao.delete("ct_collect_log.deleteMetaMngCollectLogInfo", params);
	}
}