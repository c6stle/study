package com.sangs.dq.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.support.AuthUtil;
import com.sangs.common.support.CommonDao;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsObjectUtil;
import com.sangs.lib.support.utils.SangsObjectUtil.LIST_SORT_TYPE;
import com.sangs.lib.support.utils.SangsStringUtil;

/**
 * 메타 메인 서비스
 * 
 * @author id.yoon
 *
 */

@SangsService
public class DqMainService extends ServiceBase {

	@Autowired
	private CommonDao dao;
	
	@Autowired
	private ProfileMngService profileMngService;
 
	public Map<String, Object> main(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			 
			paramMap.put("dbmsSn", SangsStringUtil.nvl(AuthUtil.getDbmsSn(), "0"));
			paramMap.put("prjctSn", AuthUtil.getPrjctSn());

			
			// 프로젝트 정보 조회 
			SangsMap prjctSnInfo = dao.selectOne("dq_main.selectMainPrjctItemCount", paramMap);
			
			// 일별진단수 조회
			List<SangsMap> dgnssCountByDayList = dao.selectList("dq_main.selectDgnssCountByDay", paramMap);
			
			// 일별 평균 불일치수 조회 
			List<SangsMap> disMatchAvgPercByDayList = dao.selectList("dq_main.selectDisMatchAvgPercByDay", paramMap);
			
			
			// 최근 진단 프로파일 목록
			Map<String, Object> dgnssResultMap = profileMngService.getDgnssResultList(paramMap);
			
			
			
			// 진단규칙별 진단 결과 목록
			List<SangsMap> dgnssResultListGrpByRule = dao.selectList("dq_main.selectDgnssResultListGrpByRule", paramMap);
			
			// 프로파일 컬럼별 불일치 Top 목록
			List<SangsMap> topDisMatchProflColList = dao.selectList("dq_main.selectTopDisMatchProflColList", paramMap);
			
			// 프로파일 규칙별 불일치 Top 목록
			List<SangsMap> topDisMatchProflRuleList = dao.selectList("dq_main.selectTopDisMatchProflRuleList", paramMap);
						
			
			rtnMap.put("prjctSnInfo", prjctSnInfo);
			
			rtnMap.put("dgnssCountByDayList", dgnssCountByDayList);
			rtnMap.put("disMatchAvgPercByDayList", disMatchAvgPercByDayList);
			rtnMap.put("dgnssProflResultList", dgnssResultMap.get("dgnssResultList"));
			rtnMap.put("dgnssResultListGrpByRule", dgnssResultListGrpByRule);
			rtnMap.put("topDisMatchProflColList", topDisMatchProflColList);
			rtnMap.put("topDisMatchProflRuleList", topDisMatchProflRuleList);
			
			
			
			
			 
			

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}
	/*
	private List<Map<String, Object>> getPhysicColGrpCntChartList(Map<String, Integer> imap, int listCnt) {
		
 
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		imap.forEach((key, value) -> {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", key);
			map.put("value", value);
			list.add(map);
		});
		
		SangsObjectUtil.sortList(list, "value", LIST_SORT_TYPE.DESC);
		
		// 9개 목록만 반환
		this.getCutList(list, listCnt);
		
		return list;
	}
	*/
	

	
	private List<Map<String, Object>> getWordWrdGrpCntChartList(List<SangsMap> wordList, int listCnt) { 
		// top 용어구성 단어
		Map<String, Integer> wordWrdGrpMap = new HashMap<String, Integer>();		 
		for(SangsMap map : wordList) {
			
			String wrd = map.getString("wrdNm");
			
			int tempCnt = 0;
			if(wordWrdGrpMap.containsKey(wrd))
				tempCnt = wordWrdGrpMap.get(wrd);
			wordWrdGrpMap.put(wrd, ++tempCnt);
		 
		}
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		wordWrdGrpMap.forEach((key, value) -> {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", key);
			map.put("value", value);
			list.add(map);
		});
		
		SangsObjectUtil.sortList(list, "value", LIST_SORT_TYPE.DESC);
		
		
		this.getCutList(list, listCnt);
		
		return list;
	}
 
	
	private void getCutList(List list, int listCnt) { 
		List rtnList = new ArrayList();
		if(list.size() < listCnt)
			listCnt = list.size();
		
		for(int i = list.size() -1 ; i >= 0 ; i--) {
			if(listCnt <= i) {
				list.remove(i);
			}
		}
	}
}