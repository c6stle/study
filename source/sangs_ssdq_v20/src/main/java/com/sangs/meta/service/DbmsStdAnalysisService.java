package com.sangs.meta.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.service.DbmsCatalogSearchService;
import com.sangs.common.support.AuthUtil;
import com.sangs.common.support.BizUtil;
import com.sangs.common.support.CommonDao;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsObjectUtil;
import com.sangs.lib.support.utils.SangsSimpleExcelMaker;
import com.sangs.lib.support.utils.SangsObjectUtil.LIST_SORT_TYPE;
import com.sangs.lib.support.utils.SangsStringUtil;

@SangsService
public class DbmsStdAnalysisService extends ServiceBase {

	@Autowired
	private CommonDao dao;
	
	@Autowired
	private DbmsCatalogSearchService dbmsCatalogSearchService;
	
	 
	/**
	 * 엑셀 다운로드
	 * @param paramMap
	 * @return Workbook
	 */
	@SuppressWarnings("unchecked")
	public Workbook getAnalExcelDown(Map<String, Object> paramMap) {
		Workbook workbook = null;
		
		
		try {
			List<SangsMap> list = (List<SangsMap>) doDbmsStdAnalysis(paramMap).get("tableColumnList");
			
			for(int j=0;j<list.size();j++) {
				
				Map<String, Object> map = new HashMap<String, Object>();
				map = list.get(j);
				map.put("NO_DOMN", "");
				map.put("NO_WRD", "");
				map.put("NO_PHYSICAL", "");
				map.put("NO_LOGICAL", "");
				map.put("EMPTY_LOGICAL", "");
				
				String[] key = ((String) map.get("stdAnalErrorCds")).split(",");
				
				for(int i=0;i<key.length;i++) { 
					
					if("NO_DOMN".equals(key[i])) {
						map.put(key[i], map.get("dataTypeLength"));
					} else if("NO_WRD".equals(key[i])) {
						String stdAnalErrorWrds = (String)map.get("stdAnalErrorWrds");
						String[] arrStdAnalErrorWrds = stdAnalErrorWrds.split(",");
						for(String tempErrWrd : arrStdAnalErrorWrds) {
							if(!SangsStringUtil.isEmpty(tempErrWrd)) {
								map.put(key[i], tempErrWrd);
							}
						}
					} else if("NO_PHYSICAL".equals(key[i])) {
						map.put(key[i], map.get("columnName"));
					} else if("NO_LOGICAL".equals(key[i])) {
						map.put(key[i], map.get("columnComment"));
					} else if("EMPTY_LOGICAL".equals(key[i])) {
						map.put(key[i], "논리명없음");
					}
				}
			}
			
			SangsSimpleExcelMaker em = new SangsSimpleExcelMaker();
			workbook = em.createSheet()
					.setHeaderColNm("표준여부", "테이블명", "컬럼명(물리명)", "컬럼커멘트(논리명)", "데이터타입", "데이터타입길이", "도메인타입없음", "단어없음", "용어약어없음", "용어명없음", "DB논리명없음", "용어불일치")
					.setHeaderColId("analStdYn", "tableName", "columnName", "columnComment", "dataType", "dataTypeLength", "NO_DOMN", "NO_WRD", "NO_PHYSICAL", "NO_LOGICAL", "EMPTY_LOGICAL", "stdAnalErrorWrds")
					.setList(list).setAutoSize().getWorkbook();
			
			
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("엑셀 생성중 에러가 발생하였습니다." + e.getMessage());
		}
		
		return workbook;
	}
	
	
	/**
	 * DBMS 표준 분석
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doDbmsStdAnalysis(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			// 점검 제외테이블 저장
			if(paramMap.containsKey("exceptTabs")) {
				updateStdAnalsExclTable(Integer.parseInt((String)paramMap.get("dbmsCnncSn")), SangsStringUtil.nvl(paramMap.get("exceptTabs")));
			}
			
			Map<String, Object> tableColumnMap = dbmsCatalogSearchService.getCurrDbmsTableColumnList(paramMap);
			
			List<Map<String, Object>> tableList = (List<Map<String, Object>>)tableColumnMap.get("tableList");
			List<Map<String, Object>> tableColumnList = (List<Map<String, Object>>)tableColumnMap.get("tableColumnList");
			SangsMap dbmsCnncInfo = (SangsMap)tableColumnMap.get("dbmsCnncInfo");
			
			 
			// 표준 분석
			Map<String, Object> stdCheckMap = this.doStdAnalysis(dbmsCnncInfo.getString("dbmsNm"), tableColumnList);
			 
			List<Map<String, Object>> errorWordGrpList = (List<Map<String, Object>>)stdCheckMap.get("errorWordGrpList");		// 비표준 용어 그룹 리스트 
			List<Map<String, Object>> errorWrdGrpList = (List<Map<String, Object>>)stdCheckMap.get("errorWrdGrpList");			// 비표준 단어 그룹 리스트
			List<Map<String, Object>> errorDataTypeGrpList = (List<Map<String, Object>>)stdCheckMap.get("errorDataTypeGrpList");// 비표준 데이터타입 그룹 리스트
			
			
			int stdCnt = (Integer)stdCheckMap.get("stdCnt");		// 표준 준수 갯수
			int nonStdCnt = (Integer)stdCheckMap.get("nonStdCnt");	// 비표준 갯수
			 
			// UI 표현을 위한 html 생성 
			for(Map<String, Object> tmap : tableColumnList) {
				this.setErrorValue(tmap);
			}
			
			rtnMap.put("tableList", tableList);
			rtnMap.put("tableColumnList", tableColumnList);
			rtnMap.put("errorWordGrpList", errorWordGrpList);
			rtnMap.put("errorWrdGrpList", errorWrdGrpList);
			rtnMap.put("errorDataTypeGrpList", errorDataTypeGrpList);
			rtnMap.put("stdCnt", stdCnt);
			rtnMap.put("nonStdCnt", nonStdCnt);
			rtnMap.put("stdComplyRate", stdCheckMap.get("stdComplyRate"));	//표준준수율
			
			
			 
		} catch(SangsMessageException e) {
			//e.printStackTrace();
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			//e.printStackTrace();
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}
	
	
	/**
	 * DBMS 도메인 분석
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doDbmsDomnAnalysis(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			
			
			// 점검 제외테이블 저장
			if(paramMap.containsKey("exceptTabs")) {
				updateStdAnalsExclTable(Integer.parseInt((String)paramMap.get("dbmsCnncSn")), SangsStringUtil.nvl(paramMap.get("exceptTabs")));
			}
			
			// 현재 프로젝트의 DBMS 테이블 목록 조회
			Map<String, Object> tableColumnMap = dbmsCatalogSearchService.getCurrDbmsTableColumnList(paramMap);
			
			// 테이블 컬럼 목록
			List<Map<String, Object>> tableColumnList = (List<Map<String, Object>>)tableColumnMap.get("tableColumnList");
			SangsMap dbmsCnncInfo = (SangsMap)tableColumnMap.get("dbmsCnncInfo");

			// 도메인 목록 조회
			Map<String, Object> domnSearchMap = new HashMap<String, Object>();
			domnSearchMap.put("stdSetSn", AuthUtil.getStdSetSn());
			domnSearchMap.put("excelYn", "Y");
			domnSearchMap.put("availUseYn", "Y");
			List<SangsMap> domnList = dao.selectList("meta_stddicary.selectStdDicaryMergedDomnList", domnSearchMap);
			for(SangsMap map : domnList) {
				map.putOrg("dataTypeLength", BizUtil.getDataTypeLengthTxt(dbmsCnncInfo.getString("dbmsNm"), map.getString("dataTyCd"), map.getString("dataLtValue"), map.getString("dataLtValue"),map.getString("dataDcmlpointLtValue")));
			}
			
			
			Map<String, Integer> dataTypeLenCntMap = new HashMap<String, Integer>();
			Map<String, Integer> formWrdCntMap = new HashMap<String, Integer>();
			Map<String, Map<String, Integer>> dataTypeLenFormWrdMap = new HashMap<String, Map<String, Integer>>();
			Map<String, Map<String, Integer>> formWrdDataTypeMap = new HashMap<String, Map<String, Integer>>();
			
			for(Map<String, Object> map : tableColumnList) {
				String dataTypeLength = (String)map.get("dataTypeLength");
				String columnName = (String)map.get("columnName");
				String formWrd = SangsStringUtil.getLastWordFromDelim(columnName, "_");
				
				
				// 데이터타입별 map 셋팅 
				int dataTypeLengthCnt = 0;
				if(dataTypeLenCntMap.containsKey(dataTypeLength))
					dataTypeLengthCnt = dataTypeLenCntMap.get(dataTypeLength);
				dataTypeLenCntMap.put(dataTypeLength, ++dataTypeLengthCnt);
				
				// 데이터 타입별 형식 단어 MAP set 
				Map<String, Integer> tempTypeForm = null;
				if(dataTypeLenFormWrdMap.containsKey(dataTypeLength))
					tempTypeForm = dataTypeLenFormWrdMap.get(dataTypeLength);
				else 
					tempTypeForm = new HashMap<String, Integer>();
				
				int dataLegntFormWrdCnt = 0;
				if(tempTypeForm.containsKey(formWrd))
					dataLegntFormWrdCnt = tempTypeForm.get(formWrd);
			 
				tempTypeForm.put(formWrd, ++dataLegntFormWrdCnt);
				dataTypeLenFormWrdMap.put(dataTypeLength, tempTypeForm);
				
				
				
				// 형식단어별 map 셋팅 
				int formWrdCnt = 0;
				if(formWrdCntMap.containsKey(formWrd))
					formWrdCnt = formWrdCntMap.get(formWrd);
				formWrdCntMap.put(formWrd, ++formWrdCnt);
				map.put("formWrd", formWrd);

				
				
				// 형식단어별 데이터 타입 MAP set
				Map<String, Integer> tempFormType = null;
				if(formWrdDataTypeMap.containsKey(formWrd))
					tempFormType = formWrdDataTypeMap.get(formWrd);
				else 
					tempFormType = new HashMap<String, Integer>();
				
				int formWrdDataTypeCnt = 0;
				if(tempFormType.containsKey(dataTypeLength))
					formWrdDataTypeCnt = tempFormType.get(dataTypeLength);
			 
				tempFormType.put(dataTypeLength, ++formWrdDataTypeCnt);
				formWrdDataTypeMap.put(formWrd, tempFormType);
		
			}
			
			// 1. 데이터타입 목록
			List<Map<String, Object>> dataTypeColList = new ArrayList<Map<String, Object>>();
			dataTypeLenCntMap.forEach((key, value) -> {
				Map<String, Object> gmap = new HashMap<String, Object>();
				gmap.put("dataTypeLength", key);
				gmap.put("cnt", value);
				dataTypeColList.add(gmap);
			});
			
			SangsObjectUtil.sortList(dataTypeColList, "dataTypeLength", LIST_SORT_TYPE.ASC);
			SangsObjectUtil.sortList(tableColumnList, "columnName", LIST_SORT_TYPE.ASC);

			// 2. 형식단어별 목록
			List<Map<String, Object>> formWrdList = new ArrayList<Map<String, Object>>();
			formWrdCntMap.forEach((key, value) -> {
				Map<String, Object> gmap = new HashMap<String, Object>();
				gmap.put("formWrd", key);
				gmap.put("cnt", value);
				
				formWrdList.add(gmap);
			});
			
			SangsObjectUtil.sortList(formWrdList, "cnt", LIST_SORT_TYPE.DESC);
			
			
			// 3. 테이더타입별 형식단어 목록
			List<Map<String, Object>> dataTypeLenFormWrdList = new ArrayList<Map<String, Object>>();
			dataTypeLenFormWrdMap.forEach((key, value) ->
			{
				Map<String, Integer> tempTypeForm = value;
				tempTypeForm.forEach((formWrd, cnt) -> {
					Map<String, Object> tmap = new HashMap<String, Object>();
					tmap.put("dataTypeLength", key);
					tmap.put("formWrd", formWrd);
					tmap.put("cnt", cnt);
					dataTypeLenFormWrdList.add(tmap);
				});
			});
			  
			// 형식단어별 데이터 타입 목록
			List<Map<String, Object>> formWrdDataTypeLenList = new ArrayList<Map<String, Object>>();
			formWrdDataTypeMap.forEach((key, value) ->
			{
				Map<String, Integer> tempFormType = value;
				tempFormType.forEach((dataTypeLength, cnt) -> {
					Map<String, Object> tmap = new HashMap<String, Object>();
					tmap.put("formWrd", key);
					tmap.put("dataTypeLength", dataTypeLength);
					tmap.put("cnt", cnt);
					formWrdDataTypeLenList.add(tmap);
				});
			});
			
			
			SangsObjectUtil.sortList(formWrdDataTypeLenList, "dataTypeLength", LIST_SORT_TYPE.ASC);
			
			
			rtnMap.put("dataTypeColList", dataTypeColList);
			rtnMap.put("tableColumnList", tableColumnList);
			rtnMap.put("formWrdList", formWrdList);
			rtnMap.put("dataTypeLenFormWrdList", dataTypeLenFormWrdList);
			rtnMap.put("formWrdDataTypeLenList", formWrdDataTypeLenList);
			rtnMap.put("domnList", domnList);
			
			 
		} catch(SangsMessageException e) {
			//e.printStackTrace();
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			//e.printStackTrace();
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}
	
	
	/*
	private void setErrorValue(Map<String, String> map, String errorItemCd, String errorTargetNm) {
		
		String errorItemCds = SangsStringUtil.nvl(map.get("errorItemCds"), ",");
		String errorViewHtml = SangsStringUtil.nvl(map.get("errorViewHtml"));
		String errorWrdNms = SangsStringUtil.nvl(map.get("errorItemCds"));
		
		errorItemCds = errorItemCds + errorItemCd + ",";
		
		if(!SangsStringUtil.isEmpty(errorTargetNm))
			errorViewHtml = errorViewHtml + "<span class='cl_" + errorItemCd.toLowerCase() + "'>"+errorTargetNm+"없음</span>";
		
		// 단어없을때
		if("NO_WRD".equals(errorItemCd)) {
			errorWrdNms = errorWrdNms + errorTargetNm + ",";
		}
		
		map.put("errorViewHtml", errorViewHtml);
		//map.put("errorItemCds", errorItemCds);
		map.put("errorWrdNms", errorWrdNms);
		
	}*/
	
	/*
	private void setNonStdResultMap(List<String> errorTypeCdList, Map<String, String> map, String errorItemCd, String errorMsg, int cnt) {
		map.put("analResultMsg", errorMsg);
		map.put("stdYn", "N");
		errorTypeCdList.add(errorItemCd);
	}
	*/
	 
	
	/**
	 * 표준 분석
	 * @param targetList
	 */
	public Map<String, Object> doStdAnalysis(String dbmsNm, List<Map<String, Object>> targetList) {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			// 로그 off
			dao.setLogFlag(false);
			
			// 단어 목록 조회
			Map<String, Object> searchMap = new HashMap<String, Object>();
			searchMap.put("prjctSn", AuthUtil.getPrjctSn());
			searchMap.put("stdSetSn", AuthUtil.getStdSetSn());
			searchMap.put("excelYn", "Y");
			List<SangsMap> wrdList = dao.selectList("meta_stddicary.selectStdDicaryWrdList", searchMap);
			
			// 용어 목록 조회 
			List<SangsMap> wordList = dao.selectList("meta_stddicary.selectStdDicaryWordList", searchMap);
			searchMap.put("availUseYn", "Y");
			// 도메인 목록 조회 
			List<SangsMap> domnList = dao.selectList("meta_stddicary.selectStdDicaryMergedDomnList", searchMap);
			
			Map<String, SangsMap> wordEngAbrvChkMap = new HashMap<String, SangsMap>();
			Map<String, SangsMap> wordNmChkMap = new HashMap<String, SangsMap>();
			Map<String, SangsMap> wrdEngAbrvChkMap = new HashMap<String, SangsMap>();
			Map<String, SangsMap> domnChkMap = new HashMap<String, SangsMap>();
			
			
			Map<String, Integer> errorWordGrpMap = new HashMap<String, Integer>();
			Map<String, Integer> errorWrdGrpMap = new HashMap<String, Integer>();
			Map<String, Integer> errorDataTypeGrpMap = new HashMap<String, Integer>();
			
			List<Map<String, Object>> errorWordGrpList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> errorWrdGrpList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> errorDataTypeGrpList = new ArrayList<Map<String, Object>>();
			
			
			Map<String, Integer> complyWordGrpMap = new HashMap<String, Integer>();			// 용어 준수 그룹Map
			Map<String, Integer> complyWrdGrpMap = new HashMap<String, Integer>();		// 단어 준수 그룹Map
			Map<String, Integer> complyDataTypeGrpMap = new HashMap<String, Integer>();		// 데이터타입 준수 그룹Map
			Map<String, Integer> physicColWrdGrpMap = new HashMap<String, Integer>();		// 물리단어 그룹맵
			
			
			for(SangsMap map : wordList) {
				wordEngAbrvChkMap.put(map.getString("wordEngAbrvNm"), map);
				wordNmChkMap.put(this.getPureWord(map.getString("wordNm")), map);
			}
			for(SangsMap map : wrdList) {
				wrdEngAbrvChkMap.put(map.getString("wrdEngAbrvNm"), map);
			}
			for(SangsMap map : domnList) {
				String dataTypeLength = BizUtil.getDataTypeLengthTxt(dbmsNm, map.getString("dataTyCd"), map.getString("dataLtValue"), map.getString("dataLtValue"), map.getString("dataDcmlpointLtValue"));
				domnChkMap.put(dataTypeLength, map);
			}
			
			
			int stdCnt = 0;
			int nonStdCnt= 0;
			
			for(int i = 0 ; i < targetList.size() ; i++) {
				Map<String, Object> map = targetList.get(i);
				String columnName = String.valueOf(map.get("columnName"));
				String columnComment = SangsStringUtil.nvl(map.get("columnComment"));
				String dataTypeLength = SangsStringUtil.nvl(map.get("dataTypeLength"));
				String dataType = SangsStringUtil.nvl(map.get("dataType")).toUpperCase();
				String dataLength = SangsStringUtil.nvl(map.get("dataLength"));
				String stdAnalErrorCds = "";
				String stdAnalErrorWrds = "";
				
				String grpMapKey = columnName+"^:^"+columnComment;
				
				
				 
				// 도메인명 체크
				if(!domnChkMap.containsKey(dataTypeLength)) {
					stdAnalErrorCds += "NO_DOMN" + ",";
					// 비표준 도메인명 대한 중복 카운트 Map setting 
					this.setMapGrpCnt(errorDataTypeGrpMap, dataTypeLength);
				} else {
					this.setMapGrpCnt(complyDataTypeGrpMap, dataTypeLength);
				}
				
				
				
			
				
				// 용어를 단어로 분할 해서 단어 체크 
				String[] dbWrds = columnName.split("_");
				for(String dbWrd : dbWrds) {
					
					// 컬럼명을 _ 로 나눠어 각각이 단어 사전에 있는지 체크 
					if(!wrdEngAbrvChkMap.containsKey(dbWrd)) {
						stdAnalErrorWrds+= dbWrd + ",";
						// 비표준 단어에 대한 중복 카운트 Map setting 
						this.setMapGrpCnt(errorWrdGrpMap, dbWrd);
					} else {
						this.setMapGrpCnt(complyWrdGrpMap, dbWrd);	
					}
					
					// 전체 물리단어 그룹 카운트
					this.setMapGrpCnt(physicColWrdGrpMap, dbWrd);
					
				}
				if(!"".equals(stdAnalErrorWrds)) {
					stdAnalErrorCds += "NO_WRD" + ",";
					stdAnalErrorWrds = stdAnalErrorWrds.substring(0, stdAnalErrorWrds.length() - 1);
				}
				
				
				
				// DB컬럼명이 용어사전의 영문약어에 없을때 
				if(!wordEngAbrvChkMap.containsKey(columnName)) {
					
					stdAnalErrorCds += "NO_PHYSICAL" + ",";
					
					// 비표준 용어에 대한 중복 카운트 Map setting 
					
					this.setMapGrpCnt(errorWordGrpMap, grpMapKey);
				} else {
					this.setMapGrpCnt(complyWordGrpMap, grpMapKey);
				}
				

					
				// DB커멘트가 용어사전의 용어명(논리명)에 없을때 
				String targetColumnComment = this.getPureWord(columnComment);
				if(!SangsStringUtil.isEmpty(columnComment) && !wordNmChkMap.containsKey(targetColumnComment)) 
					stdAnalErrorCds += "NO_LOGICAL" + ",";

	
				// 논리명이 빈값일때 
				if(SangsStringUtil.isEmpty(columnComment))
					stdAnalErrorCds += "EMPTY_LOGICAL" + ",";
				
				
				
				
				// 용어/도메인 정보가 일치 하는지 확인
				if("".equals(stdAnalErrorCds)) {	// 에러가 없을때
					
					// 용어사전 용어정보 
					SangsMap dicWordMap = wordNmChkMap.get(targetColumnComment);
					if(dicWordMap != null) {
						
						String dataTypeLenFromWord = BizUtil.getDataTypeLengthTxt(dbmsNm, dicWordMap.getString("dataTyCd"), dicWordMap.getString("dataLtValue"), dicWordMap.getString("dataLtValue"), dicWordMap.getString("dataDcmlpointLtValue"));
						
						if(!columnName.equals(dicWordMap.getString("wordEngAbrvNm"))
								|| !dataTypeLength.equals(dataTypeLenFromWord)
							//	|| !dataType.equals(dicWordMap.getString("dataTyCd"))
							//	|| !dataLength.equals(dicWordMap.getString("dataLtValue"))
								) {
							
							stdAnalErrorCds += "DIFF_WORD" + ",";
							stdAnalErrorWrds += columnComment + " " + dicWordMap.getString("wordEngAbrvNm") + " ["+dataTypeLenFromWord+"]";
						}
					}
				}
	 
				// 결과 처리
				if(!"".equals(stdAnalErrorCds)) {	// 에러가 있는경우
					stdAnalErrorCds = stdAnalErrorCds.substring(0, stdAnalErrorCds.length() - 1);
					map.put("analStdYn", "N");
					nonStdCnt++;
				} else {
					map.put("analStdYn", "Y");	// 표준인경우 
					stdCnt++;
				}
					
				map.put("stdAnalErrorCds", stdAnalErrorCds);	// 공백이 아닐때 에러 코드 (,) 컴마 구분 
				map.put("stdAnalErrorWrds", stdAnalErrorWrds);	// 공백이 아닐때 에러 단어 (,) 컴마 구분 
			}
			
			 
			// 비표준 용어 중복 카운트 map 처리 
			Iterator<String> it1 = errorWordGrpMap.keySet().iterator();
			while(it1.hasNext()) {
				
				String key = it1.next();
				String[] keys = key.split("\\^\\:\\^");
				
				String columnNm = keys[0];
				String comment = "";
				if(keys.length > 1)
					comment = keys[1];
				
				Map<String, Object> gmap = new HashMap<String, Object>();
				gmap.put("wordEngAbrvNm", columnNm);
				gmap.put("wrdNm", comment);
				gmap.put("cnt", errorWordGrpMap.get(key));
				errorWordGrpList.add(gmap);
			}
			
			
			// 비표준 단어 중복 카운트 map 처리 
			Iterator<String> it2 = errorWrdGrpMap.keySet().iterator();
			while(it2.hasNext()) {
				String key = it2.next();
				Map<String, Object> gmap = new HashMap<String, Object>();
				gmap.put("wrdNm", key);
				gmap.put("cnt", errorWrdGrpMap.get(key));
				errorWrdGrpList.add(gmap);
			}
			
			Iterator<String> it3 = errorDataTypeGrpMap.keySet().iterator();
			while(it3.hasNext()) {
				String key = it3.next();
				Map<String, Object> gmap = new HashMap<String, Object>();
				gmap.put("dataTypeLength", key);
				gmap.put("cnt", errorDataTypeGrpMap.get(key));
				errorDataTypeGrpList.add(gmap);
			}
			
			// 내림차순 정렬
			SangsObjectUtil.sortList(errorWordGrpList, "cnt", LIST_SORT_TYPE.DESC);
			SangsObjectUtil.sortList(errorWrdGrpList, "cnt", LIST_SORT_TYPE.DESC);
			SangsObjectUtil.sortList(errorDataTypeGrpList, "cnt", LIST_SORT_TYPE.DESC);
			
			
			// 표준준수율
			/*	단어/용어/도메인을 합쳐서 계산
			double partStdComplyRate = (Math.round((complyWordGrpMap.size() / ( (complyWordGrpMap.size() + errorWordGrpList.size())*1.0 )) * 10000.0) / 100.0)
				+ (Math.round((complyWrdGrpMap.size() / ( (complyWrdGrpMap.size() + errorWrdGrpList.size())*1.0 )) * 10000.0) / 100.0)
				+ (Math.round((complyDataTypeGrpMap.size() / ( (complyDataTypeGrpMap.size() + errorDataTypeGrpList.size())*1.0 )) * 10000.0) / 100.0);
				
			double stdComplyRate = Math.round(  ((partStdComplyRate / 3.0) * 100.0)) / 100.0;
			*/
			
			
			// 중복 제거된 것으로 계산식  
			//double  stdComplyRate = Math.round((complyWordGrpMap.size() / ( (complyWordGrpMap.size() + errorWordGrpList.size())*1.0 )) * 10000.0) / 100.0;
			double stdComplyRate = Math.round((stdCnt/ ( (stdCnt + nonStdCnt)*1.0 )) * 10000.0) / 100.0;
			 
			rtnMap.put("resultList", targetList);				// 결과 리스트 : 결과 list 는 input targetList에 값이 이미 call by ref 로 들어가 있어서 현 return 객체를 사용하지 않아도 됨  
			rtnMap.put("errorWordGrpList", errorWordGrpList);	// 비표준 용어 목록
			rtnMap.put("errorWrdGrpList", errorWrdGrpList);		// 비표준 단어 목록
			rtnMap.put("errorDataTypeGrpList", errorDataTypeGrpList);		// 비표준 데이터 타입 목록
			rtnMap.put("stdCnt", stdCnt);						// 표준 건수
			rtnMap.put("nonStdCnt", nonStdCnt);					// 비표준 건수
			rtnMap.put("complyWordGrpMap", complyWordGrpMap);			// 용어준수 그룹 Map 
			rtnMap.put("complyWrdGrpMap", complyWrdGrpMap);				// 단어준수 그룹 Map
			rtnMap.put("complyDataTypeGrpMap", complyDataTypeGrpMap);	// 데이터타입 그룹 Map
			rtnMap.put("physicColWrdGrpMap", physicColWrdGrpMap);	 
			
			rtnMap.put("wrdList", wrdList);			// 등록 단어수(공통세트포함)
			rtnMap.put("wordList", wordList);		// 등록 용어수(공통세트포함) 
			rtnMap.put("domnList", domnList);		// 등록 도메인수(공통세트포함)
			rtnMap.put("stdComplyRate", stdComplyRate);
				
			// 로그 on
			dao.setLogFlag(true);

		} catch(SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}
	
	 
 
	private String getPureWord(String str) {
		if(SangsStringUtil.isEmpty(str))
			return "";
		String tempStr = str.replaceAll("_", "");
		return tempStr.replaceAll(" ", "");
	}
	
	private void setErrorValue(Map<String, Object> map) {
		String stdAnalErrorCds = (String)map.get("stdAnalErrorCds");
		String columnName = (String)map.get("columnName");
		String columnComment = (String)map.get("columnComment");
		String dataTypeLength = (String)map.get("dataTypeLength");
		String errorViewHtml = "";
		
		String[] arrStdAnalErrorCds = stdAnalErrorCds.split(",");
		
		for(String tempErrCd : arrStdAnalErrorCds) {
			
			if(!SangsStringUtil.isEmpty(tempErrCd)) {
				/* 링크전
				if("NO_PHYSICAL".equals(tempErrCd)) {
					errorViewHtml = errorViewHtml + "<span class='cl_" + tempErrCd.toLowerCase() + "'>"+columnName+"없음</span>";
				} else if("EMPTY_LOGICAL".equals(tempErrCd)) {
					errorViewHtml = errorViewHtml + "<span class='cl_" + tempErrCd.toLowerCase() + "'>논리명없음</span>";
				} else if("NO_LOGICAL".equals(tempErrCd)) {
					errorViewHtml = errorViewHtml + "<span class='cl_" + tempErrCd.toLowerCase() + "'>"+columnComment+"없음</span>";
				} else if("NO_WRD".equals(tempErrCd)) {
					String stdAnalErrorWrds = (String)map.get("stdAnalErrorWrds");
					String[] arrStdAnalErrorWrds = stdAnalErrorWrds.split(",");
					for(String tempErrWrd : arrStdAnalErrorWrds) {
						if(!SangsStringUtil.isEmpty(tempErrWrd)) {
							errorViewHtml = errorViewHtml + "<span class='cl_" + tempErrCd.toLowerCase() + "'>"+tempErrWrd+"없음</span>";
						}
					}
				} else if("NO_DOMN".equals(tempErrCd)) {
					errorViewHtml = errorViewHtml + "<span class='cl_" + tempErrCd.toLowerCase() + "'>"+dataTypeLength+"없음</span>";
				} else if("DIFF_WORD".equals(tempErrCd)) {
					errorViewHtml = errorViewHtml + "<span class='cl_" + tempErrCd.toLowerCase() + "'>"+(String)map.get("stdAnalErrorWrds")+"와 불일치</span>";
				}
				*/
				if("NO_DOMN".equals(tempErrCd)) {
					errorViewHtml = errorViewHtml + "<span class='cl_" + tempErrCd.toLowerCase() + "'  onclick='fnClickErrorItem(\""+tempErrCd+"\", this);' >"+dataTypeLength+"</span>";
				} else if("NO_WRD".equals(tempErrCd)) {
					String stdAnalErrorWrds = (String)map.get("stdAnalErrorWrds");
					String[] arrStdAnalErrorWrds = stdAnalErrorWrds.split(",");
					for(String tempErrWrd : arrStdAnalErrorWrds) {
						if(!SangsStringUtil.isEmpty(tempErrWrd)) {
							errorViewHtml = errorViewHtml + "<span class='cl_" + tempErrCd.toLowerCase() + "' onclick='fnClickErrorItem(\""+tempErrCd+"\", this);'>"+tempErrWrd+"</span>";
						}
					}
				} else if("NO_PHYSICAL".equals(tempErrCd)) {
					errorViewHtml = errorViewHtml + "<span class='cl_" + tempErrCd.toLowerCase() + "' onclick='fnClickErrorItem(\""+tempErrCd+"\", this);' >"+columnName+"</span>";
				} else if("NO_LOGICAL".equals(tempErrCd)) {
					errorViewHtml = errorViewHtml + "<span class='cl_" + tempErrCd.toLowerCase() + "' onclick='fnClickErrorItem(\""+tempErrCd+"\", this);' >"+columnComment+"</span>";					
				} else if("EMPTY_LOGICAL".equals(tempErrCd)) {
					errorViewHtml = errorViewHtml + "<span class='cl_" + tempErrCd.toLowerCase() + "'>논리명없음</span>";
				} else if("DIFF_WORD".equals(tempErrCd)) {
					errorViewHtml = errorViewHtml + "<span class='cl_" + tempErrCd.toLowerCase() + "'>"+(String)map.get("stdAnalErrorWrds")+"와 불일치</span>";
				}
			}
		}
		
		map.put("errorViewHtml", errorViewHtml);
		
	}
	
	/*
	private void sortErrorList(List<Map<String, Object>> errorList) {
		errorList.sort(new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				int o1Cnt = (Integer)o1.get("errorCnt");
				int o2Cnt = (Integer)o2.get("errorCnt");
				
				if(o1Cnt == o2Cnt) return 0;
				else if(o1Cnt < o2Cnt) return 1;
				else return -1;
			}
		});
	}
	*/
	
	/**
	 * 표준 점검 제외 테이블 갱신
	 * @param dbmsCnncSn
	 * @param exclTblCn
	 * @throws Exception
	 */
	private void updateStdAnalsExclTable(int dbmsCnncSn, String exclTblCn) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("dbmsCnncSn", dbmsCnncSn);
		paramMap.put("exclTblCn", exclTblCn);
		paramMap.put("regUserId", AuthUtil.getUserId());
		SangsMap resultMap = dao.selectOne("meta_stdanals.selectStdAnalsExclTblInfo", paramMap);
		
		if(resultMap == null) {
			dao.insert("meta_stdanals.insertStdAnalsExclTblInfo", paramMap);
		} else {
			dao.update("meta_stdanals.updateStdAnalsExclTblInfo", paramMap);
		}
	}
		
	/**
	 * 표준 점검 제외 테이블 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdAnalsExclTblInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			
			SangsMap resultMap = dao.selectOne("meta_stdanals.selectStdAnalsExclTblInfo", paramMap);
			rtnMap.put("info", resultMap);
	 
			 
		} catch(SangsMessageException e) {
			//e.printStackTrace();
			logger.error("", e);
			throw e;
		} catch(Exception e) {
			//e.printStackTrace();
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}
	
	private void setMapGrpCnt(Map<String, Integer> grpCntMap, String key) {
		// 물리명 전체 Group Count 
		int tempCnt = 0;
		if(grpCntMap.containsKey(key))
			tempCnt = grpCntMap.get(key);
		grpCntMap.put(key, ++tempCnt);
	}
	
}
 