package com.sangs.meta.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.service.DbmsCatalogSearchService;
import com.sangs.common.service.ProjectService;
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
public class MetaMainService extends ServiceBase {

	@Autowired
	private CommonDao dao;

	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private DbmsCatalogSearchService dbmsCatalogSearchService;
	
	@Autowired
	private DbmsStdAnalysisService dbmsStdAnalysisService;

	
	public Map<String, Object> main(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			

			// 프로젝트 정보 조회
			Map<String, Object> rtnProjcetInfo = projectService.getCurrProjectInfo(paramMap);
			SangsMap projcetInfo = (SangsMap)rtnProjcetInfo.get("info");
			rtnMap.put("projectInfo", projcetInfo);
			
			String dbConnectedYn = "N";
			String stdSetConnectedYn = "N";
			
			if(!SangsStringUtil.isEmpty(projcetInfo.get("dbmsIpAddr")) && !SangsStringUtil.isEmpty(projcetInfo.get("dbmsId")))
				dbConnectedYn = "Y";
			if(projcetInfo.get("stdSetSn") != null) 
				stdSetConnectedYn = "Y";
		
			rtnMap.put("dbConnectedYn", dbConnectedYn);
			rtnMap.put("stdSetConnectedYn", stdSetConnectedYn);
			
			
			
			if("Y".equals(dbConnectedYn)) {
				paramMap.put("dbmsCnncSn", projcetInfo.get("dbmsCnncSn"));
				
				// 접속 테이블 정보 조회
				Map<String, Object> tableColumnMap = dbmsCatalogSearchService.getCurrDbmsTableColumnList(paramMap);
				List<Map<String, Object>> tableList = (List<Map<String, Object>>)tableColumnMap.get("tableList");
				List<Map<String, Object>> tableColumnList = (List<Map<String, Object>>)tableColumnMap.get("tableColumnList");
				rtnMap.put("tableCnt", tableList.size());
				rtnMap.put("colCnt", tableColumnList.size());
				
				// 표준 분석
				Map<String, Object> stdCheckMap = dbmsStdAnalysisService.doStdAnalysis(projcetInfo.getString("dbmsNm"), tableColumnList);
				
				// 물리단어명 전체 Group Count (top 9 개만 list 에 담는다.)
				List<Map<String, Object>> physicColGrpCntChartList = this.getPhysicColGrpCntChartList((Map<String, Integer>)stdCheckMap.get("physicColWrdGrpMap"), 9);
				rtnMap.put("physicColGrpCntChartList", physicColGrpCntChartList);	// 물리단어 차트목록
				
				if("Y".equals(stdSetConnectedYn)) {
					// 표준사전 정보 조회
					List<SangsMap> wrdList = (List<SangsMap>)stdCheckMap.get("wrdList");
					List<SangsMap> wordList = (List<SangsMap>)stdCheckMap.get("wordList");
					List<SangsMap> domnList = (List<SangsMap>)stdCheckMap.get("domnList");
					rtnMap.put("wrdCnt", wrdList.size());
					rtnMap.put("wordCnt", wordList.size());
					rtnMap.put("domnCnt", domnList.size());
					
					 
					Map<String, Integer> complyWordGrpMap = (Map<String, Integer>)stdCheckMap.get("complyWordGrpMap");		// 용어 준수 그룹맵  
					Map<String, Integer> complyWrdGrpMap = (Map<String, Integer>)stdCheckMap.get("complyWrdGrpMap");		// 단어 준수 그룹맵
					Map<String, Integer> complyDataTypeGrpMap = (Map<String, Integer>)stdCheckMap.get("complyDataTypeGrpMap");
					
					rtnMap.put("complyWordGrpCnt", complyWordGrpMap.size());
					rtnMap.put("complyWrdGrpCnt", complyWrdGrpMap.size());
					rtnMap.put("complyDataTypeGrpCnt", complyDataTypeGrpMap.size());
					rtnMap.put("stdComplyRate", stdCheckMap.get("stdComplyRate"));
					
					
					List<Map<String, Object>> errorWordGrpList = (List<Map<String, Object>>)stdCheckMap.get("errorWordGrpList");		
					List<Map<String, Object>> errorWrdGrpList = (List<Map<String, Object>>)stdCheckMap.get("errorWrdGrpList");
					List<Map<String, Object>> errorDataTypeGrpList = (List<Map<String, Object>>)stdCheckMap.get("errorDataTypeGrpList");
					rtnMap.put("errorWordGrpCnt", errorWordGrpList.size());
					rtnMap.put("errorWrdGrpCnt", errorWrdGrpList.size());
					rtnMap.put("errorDataTypeGrpCnt", errorDataTypeGrpList.size());
					
					this.getCutList(errorWordGrpList, 20);
					rtnMap.put("errorWordGrpList", errorWordGrpList);
					
					this.getCutList(errorWrdGrpList, 20);
					rtnMap.put("errorWrdGrpList", errorWrdGrpList);
					
					this.getCutList(errorDataTypeGrpList, 20);
					rtnMap.put("errorDataTypeGrpList", errorDataTypeGrpList);
					
					
					paramMap.put("prjctSn", projcetInfo.get("prjctSn"));
					
					List<SangsMap> wordWrdList = dao.selectList("meta_stddicary.getStdDicaryWordWrdList", paramMap);
					List<Map<String, Object>> wordWrdGrpCntChartList = this.getWordWrdGrpCntChartList(wordWrdList, 9);
					rtnMap.put("wordWrdGrpCntChartList", wordWrdGrpCntChartList);
					
					
				}
				
			
			} else {
				// db연결 안되어 있고 표준세트만 연결되어 있을때 
				if("Y".equals(stdSetConnectedYn)) {
					// 표준 분석
					Map<String, Object> stdCheckMap = dbmsStdAnalysisService.doStdAnalysis("", new ArrayList());
					
					// 표준사전 정보 조회
					List<SangsMap> wrdList = (List<SangsMap>)stdCheckMap.get("wrdList");
					List<SangsMap> wordList = (List<SangsMap>)stdCheckMap.get("wordList");
					List<SangsMap> domnList = (List<SangsMap>)stdCheckMap.get("domnList");
					rtnMap.put("wrdCnt", wrdList.size());
					rtnMap.put("wordCnt", wordList.size());
					rtnMap.put("domnCnt", domnList.size());
					
					List<SangsMap> wordWrdList = dao.selectList("meta_stddicary.getStdDicaryWordWrdList", paramMap);
					List<Map<String, Object>> wordWrdGrpCntChartList = this.getWordWrdGrpCntChartList(wordWrdList, 9);
					rtnMap.put("wordWrdGrpCntChartList", wordWrdGrpCntChartList);
					
				}
			}
			 
			

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw new SangsMessageException("프로젝트 DBMS 접속중 에러가 발생하였습니다.\n프로젝트 DBMS 접속 정보를 확인해주세요");
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("프로젝트 DBMS 접속중 에러가 발생하였습니다.\n프로젝트 DBMS 접속 정보를 확인해주세요");
		}
		return rtnMap;
	}
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