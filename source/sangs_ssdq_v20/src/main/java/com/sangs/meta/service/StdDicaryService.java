package com.sangs.meta.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.service.CommonCodeService;
import com.sangs.common.support.AuthUtil;
import com.sangs.common.support.BizUtil;
import com.sangs.common.support.CommonDao;
import com.sangs.common.support.SangsExcelUtil;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.fwk.common.SangsConstants;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.domain.SangsPagingViewInfo;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsSimpleExcelMaker;
import com.sangs.lib.support.utils.SangsStringUtil;
import com.sangs.lib.support.utils.SangsWebUtil;

/**
 * 단어 관련 Service
 * 
 *  
 * @author sw.lee
 *
 */

@SangsService
public class StdDicaryService extends ServiceBase {

	@Autowired
	private CommonDao dao;
	

	@Autowired
	private CommonCodeService commonCodeService; 
	
	@Autowired
	private StdDicaryValidService stdDicaryValidService; 
	
	
	
	@Value("${meta.upload.base_path}")
	private String basePathId;
	/**
	 * 표준사전 단어 목록 조회
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdDicaryWrdList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			logger.debug("parameter : " + paramMap);

			String wrdNm = String.valueOf(paramMap.get("wrdNm"));
			String wrdEngAbrvNm = String.valueOf(paramMap.get("wrdEngAbrvNm"));
			String wrdEngNm = String.valueOf(paramMap.get("wrdEngNm"));
			
			wrdNm = this.getWildCardReplace(wrdNm);
			wrdEngAbrvNm = this.getWildCardReplace(wrdEngAbrvNm);
			wrdEngNm = this.getWildCardReplace(wrdEngNm);
		 
			
			paramMap.put("wrdNm", wrdNm);
			paramMap.put("wrdEngAbrvNm", wrdEngAbrvNm);
			paramMap.put("wrdEngNm", wrdEngNm);
			paramMap.put("prjctSn", AuthUtil.getPrjctSn());
			paramMap.put("stdSetSn", AuthUtil.getStdSetSn());
			int pageNum = SangsStringUtil.nvlInt(paramMap.get("pageNum"), 1);

			// 전체 row 수 조회
			int totalCount = dao.selectCount("meta_stddicary.selectStdDicaryWrdListCnt", paramMap);
			
			/*
			if("Y".equals(AuthUtil.isApprover())) {
				// 요청 건수 조회
				List<SangsMap> sttusCntInfoList = dao.selectList("meta_stddicary.selectWrdCntListBySttusCd", paramMap);
				rtnMap.put("sttusCntInfoList", sttusCntInfoList);
			} else {
				rtnMap.put("sttusCntInfoList", new ArrayList<SangsMap>());
			}
			*/
			
			// 요청 건수 조회
			List<SangsMap> sttusCntInfoList = dao.selectList("meta_stddicary.selectWrdCntListBySttusCd", paramMap);
			rtnMap.put("sttusCntInfoList", sttusCntInfoList);


			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);

			paramMap.put("pageSize", pagingInfo.getPageSize());
			paramMap.put("offset", pagingInfo.getOffset());

			// 표준사전 단어 목록 조회
			dao.setLogFlag(false);
			List<SangsMap> list = dao.selectList("meta_stddicary.selectStdDicaryWrdList", paramMap);
			dao.setLogFlag(true);
			rtnMap.put("list", list);
			rtnMap.put("totalCount", totalCount);
			rtnMap.put("pagingInfo", pagingInfo);
			

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}

	/**
	 * 표준사전 단어 목록 상세 조회
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdDicaryWrdInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);

			if (!paramMap.containsKey("stdSetSn")) {
				paramMap.put("stdSetSn", AuthUtil.getStdSetSn());
			}

			// 표준사전 단어 목록 상세 조회
			SangsMap info = dao.selectOne("meta_stddicary.selectStdDicaryWrdInfo", paramMap);
			rtnMap.put("info", info);

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}

	/**
	 * 표준사전 단어 엑셀 다운로드
	 * 
	 * @param paramMap
	 * @return
	 */
	public Workbook getStdDicaryWrdExcelDown(Map<String, Object> paramMap) {

		Workbook workbook = null;

		try {

			String wrdNm = String.valueOf(paramMap.get("wrdNm"));
			String wrdEngAbrvNm = String.valueOf(paramMap.get("wrdEngAbrvNm"));
			String wrdEngNm = String.valueOf(paramMap.get("wrdEngNm"));
			
			wrdNm = this.getWildCardReplace(wrdNm);
			wrdEngAbrvNm = this.getWildCardReplace(wrdEngAbrvNm);
			wrdEngNm = this.getWildCardReplace(wrdEngNm);
			 
			
			paramMap.put("wrdNm", wrdNm);
			paramMap.put("wrdEngAbrvNm", wrdEngAbrvNm);
			paramMap.put("wrdEngNm", wrdEngNm);
			
			paramMap.put("prjctSn", AuthUtil.getPrjctSn());
			paramMap.put("excelYn", "Y");
			dao.setLogFlag(false);
			List<SangsMap> list = dao.selectList("meta_stddicary.selectStdDicaryWrdList", paramMap);
			dao.setLogFlag(true);

			// 리스트 안에 있는 코드에 대한 코드명 setting  
			commonCodeService.setCmmnCodeNmForList(list
					, new String[]{"CONFMCDTY","WRDCDTY"}
					, new String[]{"aprvSttusCd", "wrdTyCd"}
					, new String[]{"aprvSttusCdNm", "wrdTyCdNm"}
			);
			
			SangsSimpleExcelMaker em = new SangsSimpleExcelMaker();
			workbook = em.createSheet()
					.setHeaderColNm(new String[] { "단어구분", "단어명", "영문약어명", "영문명", "표준어", "연관어", "금칙어", "승인상태", "세트명" })
					.setHeaderColId(new String[] { "wrdTyCdNm", "wrdNm", "wrdEngAbrvNm", "wrdEngNm", "stdWrdNm", "relWrdNm", "prhibtYn", "aprvSttusCdNm", "stdSetNm" })
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
	 * 표준사전 단어 표준어 목록 조회
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdWrdNmList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);
			
			paramMap.put("stdSetSn", AuthUtil.getStdSetSn());
			List<SangsMap> list = dao.selectList("meta_stddicary.selectStdWrdNmList", paramMap);

			rtnMap.put("list", list);
			rtnMap.put("totalCount", list.size());
			
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}

	/**
	 * 표준사전 단어검사 목록
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdDicaryWrdCheckList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);

			paramMap.put("stdSetSn", AuthUtil.getStdSetSn());

			List<SangsMap> list = dao.selectList("meta_stddicary.selectStdDicaryWrdCheckList", paramMap);

			rtnMap.put("list", list);

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}

	/**
	 * 표준사전 단어 등록/수정 처리
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> saveStdDicaryWrdInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);

			String regUserId = AuthUtil.getUserId();
			String isApprover = AuthUtil.isApprover();
			int stdSetSn = AuthUtil.getStdSetSn();

			String getIsApprover = paramMap.get("isApprover").toString();

			if (!isApprover.equals(getIsApprover)) {
				throw new SangsMessageException("잘못된 접근입니다.");
			}

			SangsStringUtil.checkRequiredParam(paramMap, "pmode", "pmode");
			String pmode = String.valueOf(paramMap.get("pmode"));

			// 작성한 정보
			Map<String, Object> wrdMap = (Map<String, Object>) paramMap.get("wrdMap");
			wrdMap.put("wrdNm", SangsWebUtil.clearXSSMinimum((String) wrdMap.get("wrdNm")));
			wrdMap.put("wrdEngAbrvNm", SangsWebUtil.clearXSSMinimum((String) wrdMap.get("wrdEngAbrvNm")));
			wrdMap.put("wrdEngNm", SangsWebUtil.clearXSSMinimum((String) wrdMap.get("wrdEngNm")));
			wrdMap.put("wrdCn", SangsWebUtil.clearXSSMinimum((String) wrdMap.get("wrdCn")));
			wrdMap.put("relWrdNm", SangsWebUtil.clearXSSMinimum((String) wrdMap.get("relWrdNm")));
			wrdMap.put("stdSetSn", stdSetSn);
			
			// 승인관리
			Map<String, Object> confmMap = new HashMap<String, Object>();
			int wrdSn = -1;
			String prcsSeCd = "INS";
			if (!"".equals(wrdMap.get("wrdSn"))) {
				wrdSn = Integer.parseInt(wrdMap.get("wrdSn").toString());
				prcsSeCd = "UPD";
			}

			// 등록
			if ("R".equals(pmode)) {

				// 등록전 체크 사항
				String errorMsg = chkSaveStdDicaryWrdInfo(wrdMap);

				if (!SangsStringUtil.isEmpty(errorMsg)) {
					throw new SangsMessageException(errorMsg);
				}

				int nextWrdSn = dao.selectInteger("meta_stddicary.selectNextStdDicaryWrdSn", wrdMap);
				wrdMap.put("wrdSn", nextWrdSn);
				wrdMap.put("delYn", "N");
				wrdMap.put("useYn", "Y");
				wrdMap.put("regUserId", regUserId);
				dao.insert("meta_stddicary.insertStdDicaryWrdInfo", wrdMap);

				wrdSn = nextWrdSn;
				confmMap.put("aprvSttusCdNm", wrdMap.get("aprvSttusCdNm").toString());
			} else if ("M".equals(pmode)) {
				
				SangsMap befMap = dao.selectOne("meta_stddicary.selectStdDicaryWrdInfo", wrdMap);
				confmMap.put("aprvSttusCd", befMap.get("aprvSttusCd").toString());
				// DB의 값과 화면에서 수정한 값 비교하여 바뀐 함목정보 반환
				List<Map<String, Object>> chgList = getChangeItemList(befMap, wrdMap, (List<Map<String, Object>>) paramMap.get("chgKeyList"));

				// 단어 복사
				int nextWrdCopySn = dao.selectInteger("meta_stddicary.selectNextStdDicaryWrdCopySn", befMap);
				befMap.putOrg("wrdCopySn", nextWrdCopySn);
				dao.insert("meta_stddicary.insertStdDicaryWrdCopyInfo", befMap);
				
				// 표준사전 단어 이력 순번 조회
				int nextWrdHistSn = dao.selectInteger("meta_stddicary.selectNextStdDicaryWrdHistSn", wrdMap);
				for (Map<String, Object> chgMap : chgList) {
					chgMap.put("stdSetSn", stdSetSn);
					chgMap.put("wrdSn", wrdMap.get("wrdSn"));
					chgMap.put("wrdHistSn", nextWrdHistSn);
					chgMap.put("isApprover", isApprover);
					chgMap.put("regUserId", regUserId);
					chgMap.put("wrdCopySn", nextWrdCopySn);
					dao.insert("meta_stddicary.insertStdDicaryWrdHistInfo", chgMap);
					nextWrdHistSn++;
				}
				
				// 표준사전 단어 수정
				dao.update("meta_stddicary.updateStdDicaryWrdInfo", wrdMap);

			} else {
				throw new SangsMessageException("잘못된 접근입니다.");
			}

			confmMap.put("wrdSn", wrdSn);
			confmMap.put("pmode", "A");
			confmMap.put("location", "wrd");
			confmMap.put("isApprover", isApprover);
			confmMap.put("prcsSeCd", prcsSeCd);
			
			Map<String, Object> rtnVal = saveStdDicaryConfmInfo(confmMap);
			
			if("OK".equals(rtnVal.get("resultCd"))) {
				rtnMap.put("resultCd", "OK");
			} else {
				rtnMap.put("resultCd", "NO");
			}
			
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}

	// 단어 등록 전 체크 
	private String chkSaveStdDicaryWrdInfo(Map<String, Object> paramMap) throws Exception {
		String errorMsg = "";
		
		SangsMap targetMap = new SangsMap();
		//targetMap.putOrg("wrdEngAbrvNm", paramMap.get("wrdEngAbrvNm"));
		targetMap.putOrg("prjctSn", AuthUtil.getPrjctSn());
		targetMap.putOrg("stdSetSn", paramMap.get("stdSetSn"));
		
		

		int totalCount = dao.selectCount("meta_stddicary.selectStdDicaryWrdListCnt", targetMap);
		targetMap.putOrg("pageSize", totalCount);	// 전체 조회를 위해서
		targetMap.putOrg("offset", 0);
		

		// 표준사전 단어 목록 조회
		dao.setLogFlag(false);
		List<SangsMap> list = dao.selectList("meta_stddicary.selectStdDicaryWrdList", targetMap);
		dao.setLogFlag(true);

		targetMap.putOrg("wrdNm", paramMap.get("wrdNm"));
		targetMap.putOrg("wrdEngNm", paramMap.get("wrdEngNm"));
		targetMap.putOrg("wrdEngAbrvNm", paramMap.get("wrdEngAbrvNm"));
		targetMap.putOrg("prhibtYn", paramMap.get("prhibtYn"));
		targetMap.putOrg("wrdTyCd", paramMap.get("wrdTyCd"));

		List<SangsMap> targetList = new ArrayList<SangsMap>();
		targetList.add(targetMap);
		
		Map<String, Object> resultValidationMap = stdDicaryValidService.validStdDicaryWrd(list, targetList, false);
		
		if(!"".equals(resultValidationMap.get("errorInfo"))) {
			SangsMap targetTempMap = targetList.get(0);
			errorMsg = String.valueOf(targetTempMap.get("errorInfo"));
		}
		/*
		int totalErrorCnt  = (Integer)resultValidationMap.get("totalErrorCnt");
		if(totalErrorCnt > 0) {
			SangsMap targetTempMap = targetList.get(0);
			errorMsg = String.valueOf(targetTempMap.get("errorInfo"));
		}*/
		
		return errorMsg;
	}
	
	
	// DB의 값과 화면에 등록된 값 비교 하여 바뀐 함목정보 반환 
	public List<Map<String, Object>> getChangeItemList (SangsMap befMap, Map<String, Object> aftMap, List<Map<String, Object>> chgKeyList) {
		
		List<Map<String, Object>> rtnList = new ArrayList<>();
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		for (Map<String, Object> keyInfo : chgKeyList) {
			
			Iterator<String> it = keyInfo.keySet().iterator();
			
			while (it.hasNext()) {
				String key = it.next();
				String keyName = (String) keyInfo.get(key);

				String beforeVal = befMap.getString(key);
				String afterVal = "";
				
				if ((String) aftMap.get(key) != null) {
					afterVal = String.valueOf(aftMap.get(key));
				}

				if (!beforeVal.equals(afterVal)) {
					
					rtnMap = new HashMap<String, Object>();
					
					rtnMap.put("aprvSttusCd", "REQUEST");
					rtnMap.put("prcsSeCd", "UPD");
					rtnMap.put("chgIemNm", keyName);
					rtnMap.put("bfchgCn", beforeVal);
					rtnMap.put("aftchCn", afterVal);
					rtnMap.put("key", key);
					
					rtnList.add(rtnMap);
				}
				
			}
			
		}
		
		return rtnList;
	}
	
	
	/**
	 * 단어 변경 영향도 조회
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getChangeEffectByList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 단어, 도메인 , 용어 
			List<SangsMap> wrdList = new ArrayList<SangsMap>();
			List<SangsMap> wordList = new ArrayList<SangsMap>();
			List<SangsMap> domnList = new ArrayList<SangsMap>();
			
			SangsMap info = new SangsMap();

			logger.debug("parameter : " + paramMap);
			String effectLocation = paramMap.get("effectLocation").toString();
			map.put("effectLocation", effectLocation);
			map.put("cnt", paramMap.get("cnt"));
			map.put("detailPageYn", paramMap.get("detailPageYn"));
			
			if (paramMap.containsKey("effectSn")) {
				paramMap.put(effectLocation + "Sn", paramMap.get("effectSn"));
			}
			
			if ("wrd".equals(effectLocation)) {
				info = dao.selectOne("meta_stddicary.selectStdDicaryWrdInfo", paramMap);
				
				map.put("stdSetSn", AuthUtil.getStdSetSn());
				map.put("wrdSn", paramMap.get("wrdSn"));
				wordList = dao.selectList("meta_stddicary.selectStdDicaryChangeWordEffecList", map);
				domnList = dao.selectList("meta_stddicary.selectStdDicaryChangeDomnEffecList", map);
				
			} else if ("domn".equals(effectLocation)) {
				info = dao.selectOne("meta_stddicary.selectStdDicaryDomnInfo", paramMap);
				
				map.put("stdSetSn", AuthUtil.getStdSetSn());
				map.put("domnSn", paramMap.get("domnSn"));
				wrdList = dao.selectList("meta_stddicary.selectStdDicaryChangeWrdEffecList", map);
				wordList = dao.selectList("meta_stddicary.selectStdDicaryChangeWordEffecList", map);
				
			} else if ("word".equals(effectLocation)) {
				
				paramMap.put("stdSetSn", AuthUtil.getStdSetSn());
				info = dao.selectOne("meta_stddicary.selectStdDicaryWordInfo", paramMap);
				
				map.put("stdSetSn", AuthUtil.getStdSetSn());
				map.put("wordSn", paramMap.get("wordSn"));
				wrdList = dao.selectList("meta_stddicary.selectStdDicaryChangeWrdEffecList", map);
				domnList = dao.selectList("meta_stddicary.selectStdDicaryChangeDomnEffecList", paramMap);
			}
			rtnMap.put("info", info);
			rtnMap.put("wrdList", wrdList); 			
			rtnMap.put("wordList", wordList);
			rtnMap.put("domnList", domnList);
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}

	
	/**
	 * 표준사전 변경이력 목록 조회 단어/도메인/용어
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdDicaryStdChangeHistList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);

			List<SangsMap> list = new ArrayList<SangsMap>();
			SangsMap info = new SangsMap();

			String histLocation = paramMap.get("histLocation").toString();
			//paramMap.put("stdSetSn", AuthUtil.getStdSetSn());

			if (paramMap.containsKey("histSn")) {
				paramMap.put(histLocation + "Sn", paramMap.get("histSn"));
			}

			if ("wrd".equals(histLocation)) {

				info = dao.selectOne("meta_stddicary.selectStdDicaryWrdInfo", paramMap);
				list = dao.selectList("meta_stddicary.selectStdDicaryStdChangeWrdHistList", paramMap);

			} else if ("domn".equals(histLocation)) {
				info = dao.selectOne("meta_stddicary.selectStdDicaryDomnInfo", paramMap);
				list = dao.selectList("meta_stddicary.selectStdDicaryStdChangeDomnHistList", paramMap);

			} else if ("word".equals(histLocation)) {
				paramMap.put("stdSetSn", AuthUtil.getStdSetSn());
				info = dao.selectOne("meta_stddicary.selectStdDicaryWordInfo", paramMap);
				list = dao.selectList("meta_stddicary.selectStdDicaryStdChangeWordHistList", paramMap);
			}
			rtnMap.put("info", info);
			rtnMap.put("list", list);

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}

	/**
	 * 표준사전 단어 일괄 등록 파일 읽어오기
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdDicaryWrdBundleList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			logger.debug("parameter : " + paramMap);

			String subDir = paramMap.get("subDir").toString();
			String savedFileNm = paramMap.get("savedFileNm").toString();
			String fileFullPath = basePathId + subDir + savedFileNm;

			Workbook workbook = new HSSFWorkbook(new FileInputStream(new File(fileFullPath)));
			int sheetCount = workbook.getNumberOfSheets();
			int cells = -1;
			for (int i = 0; i < sheetCount; i++) {
				Sheet sheet = workbook.getSheetAt(i);
				cells = sheet.getRow(i).getPhysicalNumberOfCells();
			}
			if (cells == 7) {
				String[] columnNames = new String[] { "wrdTyCdNm", "wrdNm", "wrdEngAbrvNm", "wrdEngNm", "relWrdNm", "prhibtYn", "wrdCn" };

				// 엑셀 단어 목록
				ArrayList<SangsMap> excelList = SangsExcelUtil.loadExcelList(fileFullPath, 1, 0, columnNames);

				// 데이터의 row전체가 빈값일 경우 row는 skip하여 값 있는 row 까지만 정상 데이터로 판단
				String[] upperCaseColumns = new String[] { "wrdEngAbrvNm", "wrdEngNm", "prhibtYn" };
				List<SangsMap> removeRowList = stdDicaryValidService.removeRowAllColEmptyWithUpper(excelList, upperCaseColumns);

				// DB 단어 목록
				dao.setLogFlag(false);
				paramMap.put("prjctSn", AuthUtil.getPrjctSn());
				paramMap.put("excelYn", "Y");
				List<SangsMap> wrdList = dao.selectList("meta_stddicary.selectStdDicaryWrdList", paramMap);
				dao.setLogFlag(true);

				for (SangsMap map : removeRowList) {
					if ("표준어".equals(map.get("wrdTyCdNm"))) {
						map.putOrg("wrdTyCd", "TYPE01");
					} else if ("동의어".equals(map.get("wrdTyCdNm"))) {
						map.putOrg("wrdTyCd", "TYPE02");
					}
				}
				
				// 표준 사전 단어 유효성 검사				
				Map<String, Object> validMap = stdDicaryValidService.validStdDicaryWrd(wrdList, removeRowList, true);
				List<SangsMap> validList = (List<SangsMap>) validMap.get("resultList");

				// 엑셀내 중복 검사
				List<SangsMap> dpcnChecList = stdDicaryValidService.setDpcnCheckWithRowNo(validList, "wrdEngAbrvNm", "영문약어명", "wrd");
				
				// 승인상태 검사
				List<SangsMap> resultList = stdDicaryValidService.setApprovalCheck(dpcnChecList, wrdList, "wrdEngAbrvNm");

				int totalErrorCnt = 0;
				int totalClearCnt = 0;

				for (SangsMap map : resultList) {
					if("".equals(map.getString("errorInfo"))) {
						totalClearCnt++;
					} else {
						totalErrorCnt++;
					}
				}
				rtnMap.put("totalTargetCnt", resultList.size()); // 총 오류 개수
				rtnMap.put("totalErrorCnt", totalErrorCnt); 	 // 총 오류 개수
				rtnMap.put("totalClearCnt", totalClearCnt); 	 // 총 정상 개수
				
				for(SangsMap map : resultList) {
					String tempCn = map.getString("wrdCn");
					String wrdSumCn = map.getString("wrdCn");
					if(tempCn.getBytes().length > 10) {
						wrdSumCn = SangsStringUtil.substringByte(tempCn, 10) + "...";
					}
					map.putOrg("wrdSumCn", wrdSumCn);
				}
				
				rtnMap.put("list", resultList);
			} else {
				throw new SangsMessageException("형식이 맞지 않아 업로드할 수 없습니다.");
			}

			
		} catch (OfficeXmlFileException e) {
			throw new SangsMessageException("파일확장자 xls 파일만 지원합니다. 양식다운로드 파일을 이용해주세요");
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}
	
	/**
	 * 표준사전 단어 파일 등록 
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> saveStdDicaryWrdFileList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);

			String regUserId = AuthUtil.getUserId();
			String isApprover = AuthUtil.isApprover();
			int stdSetSn = AuthUtil.getStdSetSn();

			String getIsApprover = paramMap.get("isApprover").toString();

			if (!isApprover.equals(getIsApprover)) {
				throw new SangsMessageException("잘못된 접근입니다.");
			}

			// 등록
			String subDir = paramMap.get("subDir").toString();
			String savedFileNm = paramMap.get("savedFileNm").toString();
			String fileFullPath = basePathId + subDir + savedFileNm;

			String[] columnNames = new String[] { "wrdTyCdNm", "wrdNm", "wrdEngAbrvNm", "wrdEngNm", "relWrdNm", "prhibtYn", "wrdCn" };

			ArrayList<SangsMap> excelList = SangsExcelUtil.loadExcelList(fileFullPath, 1, 0, columnNames);

			// 데이터의 row전체가 빈값일 경우  row는 skip하여 값 있는 row 까지만 정상 데이터로 판단
			String[] upperCaseColumns = new String[] { "wrdEngAbrvNm", "wrdEngNm", "prhibtYn" };
			List<SangsMap> removeRowList = stdDicaryValidService.removeRowAllColEmptyWithUpper(excelList, upperCaseColumns);

			for (SangsMap map : removeRowList) {
				if ("표준어".equals(map.getString("wrdTyCdNm"))) {
					map.putOrg("wrdTyCd", "TYPE01");
				} else if ("동의어".equals(map.getString("wrdTyCdNm"))) {
					map.putOrg("wrdTyCd", "TYPE02");
				}
				map.putOrg("stdSetSn", stdSetSn);
				map.putOrg("regUserId", regUserId);
				map.putOrg("delYn", "N");
				map.putOrg("useYn", "Y");
				int nextWrdSn = dao.selectInteger("meta_stddicary.selectNextStdDicaryWrdSn", map);
				map.putOrg("wrdSn", nextWrdSn);
				dao.insert("meta_stddicary.insertStdDicaryWrdInfo", map);

				Map<String, Object> histMap = new HashMap<String, Object>();
				histMap.put("wrdSn", nextWrdSn);
				histMap.put("location", "wrd");
				histMap.put("isApprover", isApprover);
				histMap.put("pmode", "A");
				histMap.put("prcsSeCd", "INS");
				if ("Y".equals(isApprover)) {
					histMap.put("aprvSttusCdNm", "등록");
				} else if ("N".equals(isApprover)) {
					histMap.put("aprvSttusCdNm", "요청");
				} else {
					throw new SangsMessageException("잘못된 접근입니다.");
				}
				// 단어 히스토리 등록 및 승인상태
				saveStdDicaryConfmInfo(histMap);
			}

			rtnMap.put("resultCd", "OK");

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}

	/**
	 * 표준사전 일괄등록 승인관리
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> saveStdDicaryFileConfmInfo(int sn, String location) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			int stdSetSn = AuthUtil.getStdSetSn();
			String isApprover = AuthUtil.isApprover();
			String regUserId = AuthUtil.getUserId();

			Map<String, Object> histMap = new HashMap<String, Object>();
			Map<String, Object> updtMap = new HashMap<String, Object>();

			if ("wrd".equals(location)) {
				histMap.put(location + "Sn", sn);
				updtMap.put(location + "Sn", sn);
			} else if ("word".equals(location)) {
				histMap.put(location + "Sn", sn);
				updtMap.put(location + "Sn", sn);
			} else if ("domn".equals(location)) {
				histMap.put(location + "Sn", sn);
				updtMap.put(location + "Sn", sn);
			} else {
				throw new SangsMessageException("잘못된 접근입니다.");
			}
			histMap.put("regUserId", regUserId);
			histMap.put("stdSetSn", stdSetSn);
			histMap.put("isApprover", isApprover);
			histMap.put("pmode", "A");
			histMap.put("chgIemNm", "승인상태");
			histMap.put("prcsSeCd", "INS");
			histMap.put("bfchgCn", "등록");

			updtMap.put("regUserId", regUserId);
			updtMap.put("stdSetSn", stdSetSn);

			if ("Y".equals(isApprover)) {
				histMap.put("aprvSttusCd", "APPROVAL");
				histMap.put("aftchCn", "승인");
				
				updtMap.put("aprvSttusCd", "APPROVAL");
			} else if ("N".equals(isApprover)) {
				histMap.put("aprvSttusCd", "REQUEST");
				histMap.put("aftchCn", "요청");

				updtMap.put("aprvSttusCd", "REQUEST");
			} else {
				throw new SangsMessageException("잘못된 접근입니다.");
			}

			if ("domn".equals(location)) {
				int nextDomnHistSn = dao.selectInteger("meta_stddicary.selectNextStdDicaryDomnHistSn", histMap);
				histMap.put("domnHistSn", nextDomnHistSn);
				dao.insert("meta_stddicary.insertStdDicaryDomnHistInfo", histMap);
				dao.update("meta_stddicary.updateStdDicaryDomnInfo", updtMap);
			} else if ("wrd".equals(location)) {
				int nextWrdHistSn = dao.selectInteger("meta_stddicary.selectNextStdDicaryWrdHistSn", histMap);
				histMap.put("wrdHistSn", nextWrdHistSn);
				dao.insert("meta_stddicary.insertStdDicaryWrdHistInfo", histMap);
				dao.update("meta_stddicary.updateStdDicaryWrdInfo", updtMap);
			} else if ("word".equals(location)) {
				int nextWordHistSn = dao.selectInteger("meta_stddicary.selectNextStdDicaryWordHistSn", histMap);
				histMap.put("wordHistSn", nextWordHistSn);
				dao.insert("meta_stddicary.insertStdDicaryWordHistInfo", histMap);
				dao.update("meta_stddicary.updateStdDicaryWordInfo", updtMap);
			}
			rtnMap.put("resultCd", "OK");

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}
	
	/**
	 * 표준사전 단어 일괄 등록 엑셀 양식 다운로드
	 * 
	 * @param paramMap
	 * @return
	 */
	public Workbook getStdDicaryWrdExcelFormDown(Map<String, Object> paramMap) {

		Workbook workbook = null;

		try {

			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			Map<String, Object> info = new HashMap<String, Object>();
			
			info.put("wrdTyCdNm", "표준어");
			info.put("wrdNm", "일");
			info.put("wrdEngAbrvNm", "ONE");
			info.put("wrdEngNm", "ONE");
			info.put("relWrdNm", "하나");
			info.put("wrdCn", "숫자 일");
			info.put("prhibtYn", "Y/N");

			list.add(info);

			SangsSimpleExcelMaker em = new SangsSimpleExcelMaker();
			workbook = em.createSheet()
					.setHeaderColNm(new String[] {"*단어구분", "*단어명", "*영문약어명", "*영문명", "연관어", "*금칙어", "설명" })
					.setHeaderColId(new String[] {"wrdTyCdNm", "wrdNm", "wrdEngAbrvNm", "wrdEngNm", "relWrdNm", "prhibtYn", "wrdCn" })
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
	 * 표준사전 용어 일괄 등록 파일 읽어오기
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdDicaryWordBundleList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			logger.debug("parameter : " + paramMap);

			String subDir = paramMap.get("subDir").toString();
			String savedFileNm = paramMap.get("savedFileNm").toString();
			String fileFullPath = basePathId + subDir + savedFileNm;

			Workbook workbook = new HSSFWorkbook(new FileInputStream(new File(fileFullPath)));
			int sheetCount = workbook.getNumberOfSheets();
			int cells = -1;
			Sheet sheet = workbook.getSheetAt(0);
			cells = sheet.getRow(0).getPhysicalNumberOfCells();
//			for(int i =0; i<sheetCount; i++) {
//				Sheet sheet = workbook.getSheetAt(i);
//				cells = sheet.getRow(i).getPhysicalNumberOfCells();
//			}
			if (cells == 6) {
				
				String[] columnNames = new String[] { "wordTyCdNm", "wordEngAbrvNm", "relWordNm", "domnNm", "prhibtYn", "wordCn" };

				// 엑셀 단어 목록
				ArrayList<SangsMap> excelList = SangsExcelUtil.loadExcelList(fileFullPath, 1, 0, columnNames);
				// 데이터의 row전체가 빈값일 경우  row는 skip하여 값 있는 row 까지만 정상 데이터로 판단
				String[] upperCaseColumns = new String[] { "wordEngAbrvNm", "prhibtYn", "domnNm" };
				List<SangsMap> removeRowList = stdDicaryValidService.removeRowAllColEmptyWithUpper(excelList, upperCaseColumns);
				
				dao.setLogFlag(false);
				paramMap.put("prjctSn", AuthUtil.getPrjctSn());
				paramMap.put("stdSetSn", AuthUtil.getStdSetSn());
				paramMap.put("excelYn", "Y");
				// DB 용어 목록
				List<SangsMap> wordList = dao.selectList("meta_stddicary.selectStdDicaryWordList", paramMap);
				// DB 단어 목록
				List<SangsMap> wrdList = dao.selectList("meta_stddicary.selectStdDicaryWrdList", paramMap);
				// DB 도메인 목록 조회(자신세트)
				paramMap.put("availUseYn", "Y");
				List<SangsMap> domnList = dao.selectList("meta_stddicary.selectStdDicaryMergedDomnList", paramMap);
				dao.setLogFlag(true);
				
				// DB저장된 용어랑 엑셀용어 비교
				Map<String, Object> validMap = stdDicaryValidService.validStdDicaryWord(wordList, removeRowList);
				List<SangsMap> validList = (List<SangsMap>) validMap.get("resultList");
				
				// 엑셀내 중복 검사
				List<SangsMap> dpcnChecList = stdDicaryValidService.setDpcnCheckWithRowNo(validList, "wordEngAbrvNm", "영문약어명", "word");

				// 승인상태 검사
				List<SangsMap> resultList = stdDicaryValidService.setApprovalCheck(dpcnChecList, wordList, "wordEngAbrvNm");
				//resultList = stdDicaryValidService.setApprovalCheck(resultList, wrdList, "wrdEngAbrvNm");
				resultList = stdDicaryValidService.setApprovalCheck(resultList, domnList, "domnNm");

				// 사용도메인 검사
				SangsMap domnMap = new SangsMap();

				for (SangsMap map : domnList) {
					domnMap.putOrg(map.getString("domnNm"), map);
				}

				String errorInfo = ""; // 오류상태
				String etcCn = "";
				String stdSetNm = "";
				for (SangsMap map : resultList) {

					String wordEngAbrvNm = map.getString("wordEngAbrvNm");
					String[] wordEngAbrvNmArr = new String[] {};

					if (wordEngAbrvNm.contains("_")) {
						wordEngAbrvNmArr = wordEngAbrvNm.split("_");
					}

					String wrdNm = "";
					String wrdEngNm = "";
					int stdSetSn = AuthUtil.getStdSetSn();
					for (int i = 0; i < wordEngAbrvNmArr.length; i++) {
						String wrdEngAbrvNm = wordEngAbrvNmArr[i];
						int sameWrdEngAbrvNmCnt = 0;
						
						boolean isMyStdSetSn = false; 			// 나의 표준세트 인가?
						boolean isAddWrd = false;				// 단어를 추가했는가
						boolean isWrdEngAbrvNmEquals = false;	// 영문약어명이 존재하는가?
						boolean isWrdEngAbrvNmCnt = false;		// 영문약어명이 두개이상인가?

						for (SangsMap wrdMap : wrdList) {
							String jWrdEngAbrvNm = wrdMap.getString("wrdEngAbrvNm");
							if (wrdEngAbrvNm.equals(jWrdEngAbrvNm)) {
								String jWrdTyCd = wrdMap.getString("wrdTyCd");
								int jStdSetSn = wrdMap.getInt("stdSetSn");
								String jWrdNm = wrdMap.getString("wrdNm");
								sameWrdEngAbrvNmCnt++;

								if (sameWrdEngAbrvNmCnt > 1) {
									isWrdEngAbrvNmCnt = true;
								} else {

									if (!isAddWrd) {
										if ("TYPE01".equals(jWrdTyCd)) {
											if (stdSetSn == jStdSetSn) {
												wrdNm += wrdMap.getString("wrdNm") + "_";
												wrdEngNm += wrdMap.getString("wrdEngNm") + " ";
												isMyStdSetSn = true;
											}
											if (!isMyStdSetSn) {
												wrdNm += wrdMap.getString("wrdNm") + "_";
												wrdEngNm += wrdMap.getString("wrdEngNm") + " ";
											}
											isAddWrd = true;
										} else {
											if (!isAddWrd) {
												wrdNm += wrdMap.getString("wrdNm") + "_";
												wrdEngNm += wrdMap.getString("wrdEngNm") + " ";
												map.putOrg("etcCn", "\"" + jWrdNm + "\" 동의어");
											}
										}
										if (!"APPROVAL".equals(wrdMap.getString("aprvSttusCd"))) {
											errorInfo = map.getString("errorInfo");
											etcCn = map.getString("etcCn");
											stdSetNm = wrdMap.getString("stdSetNm");
											if ("".equals(errorInfo)) {
												map.putOrg("etcCn", "[" + stdSetNm + "]");
												map.putOrg("errorInfo", "\"" + jWrdNm + "\" 미승인");
											} else {
												if (!etcCn.contains(stdSetNm)) {
													map.putOrg("etcCn", etcCn + ", [" + stdSetNm + "]");
												}
												map.putOrg("errorInfo", errorInfo + ",\"" + jWrdNm + "\" 미승인");
											}
										}

										String prhibtYn = wrdMap.getString("prhibtYn");
										if ("Y".equals(prhibtYn)) {
											errorInfo = map.getString("errorInfo");
											etcCn = map.getString("etcCn");
											stdSetNm = wrdMap.getString("stdSetNm");
											if ("".equals(errorInfo)) {
												map.putOrg("etcCn", "[" + stdSetNm + "]");
												map.putOrg("errorInfo", "\"" + jWrdNm + "\" 금칙어");
											} else {
												if (!etcCn.contains(stdSetNm)) {
													map.putOrg("etcCn", etcCn + ", [" + stdSetNm + "]");
												}
												map.putOrg("errorInfo", errorInfo + ",\"" + jWrdNm + "\" 금칙어");
											}
										}
									}
								}
								isWrdEngAbrvNmEquals = true;

							}

						}

						if (isWrdEngAbrvNmCnt) {
							map.putOrg("etcCn", "");
							map.putOrg("errorInfo", "\"" + wrdEngAbrvNm + "\" 2개이상으로 용어등록화면에서 신규등록하세요");
							wrdNm = "";
							wrdEngNm = "";
						} else {
							if (!isWrdEngAbrvNmEquals) {
								errorInfo = map.getString("errorInfo");
								if ("".equals(errorInfo)) {
									map.putOrg("errorInfo", "\"" + wrdEngAbrvNm + "\" 미등록영문약어명");
								} else {
									map.putOrg("errorInfo", errorInfo + ", \"" + wrdEngAbrvNm + "\" 미등록영문약어명");
								}
							}
						}
						map.putOrg("wordNm", wrdNm);
						map.putOrg("wordEngNm", wrdEngNm);
					}

					String wordNm = map.getString("wordNm");
					String wordEngNm = map.getString("wordEngNm");

					if (!wordNm.equals("") && !wordEngNm.equals("")) {
						char wordNmLastChar = wordNm.charAt(wordNm.length() - 1);
						char wordEngNmLastChar = wordEngNm.charAt(wordEngNm.length() - 1);
						if (wordNmLastChar == '_' || wordEngNmLastChar == ' ') {
							wordNm = wordNm.substring(0, wordNm.length() - 1);
							wordEngNm = wordEngNm.substring(0, wordEngNm.length() - 1);
							map.putOrg("wordNm", wordNm);
							map.putOrg("wordEngNm", wordEngNm);
						}
					}

					// 도메인 검사
					String domnNm = map.getString("domnNm");
					if (!domnMap.containsKey(domnNm)) {
						errorInfo = map.getString("errorInfo");
						if ("".equals(errorInfo)) {
							map.putOrg("errorInfo", "\"" + domnNm + "\" 미사용도메인명");
						} else {
							map.putOrg("errorInfo", errorInfo + ", \"" + domnNm + "\" 미사용도메인명");
						}
					}
				}

				
				int totalErrorCnt = 0;
				int totalClearCnt = 0;
				for (SangsMap map : resultList) {
					if("".equals(map.getString("errorInfo"))) {
						totalClearCnt++;
					} else {
						totalErrorCnt++;
					}
				}
				rtnMap.put("totalTargetCnt", resultList.size()); // 총 오류 개수
				rtnMap.put("totalErrorCnt", totalErrorCnt); // 총 오류 개수
				rtnMap.put("totalClearCnt", totalClearCnt); // 총 정상 개수
				
				for(SangsMap map : resultList) {
					String tempCn = map.getString("wordCn");
					String wordSumCn = map.getString("wordCn");
					if(tempCn.getBytes().length > 10) {
						wordSumCn = SangsStringUtil.substringByte(tempCn, 10) + "...";
					}
					map.putOrg("wordSumCn", wordSumCn);
				}
				
				rtnMap.put("list", resultList);
			} else {
				throw new SangsMessageException("형식이 맞지 않아 업로드할 수 없습니다.");
			}
		} catch (OfficeXmlFileException e) {
			throw new SangsMessageException("파일확장자 xls 파일만 지원합니다. 양식다운로드 파일을 이용해주세요");
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}
	
	/**
	 * 표준사전 용어 파일 등록 
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> saveStdDicaryWordFileList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {

			logger.debug("parameter : " + paramMap);

			String regUserId = AuthUtil.getUserId();
			String isApprover = AuthUtil.isApprover();
			int prjctSn = AuthUtil.getPrjctSn();
			int stdSetSn = AuthUtil.getStdSetSn();

			paramMap.put("prjctSn", prjctSn);
			paramMap.put("stdSetSn", stdSetSn);
			paramMap.put("excelYn", "Y");
			String getIsApprover = paramMap.get("isApprover").toString();

			if (!isApprover.equals(getIsApprover)) {
				throw new SangsMessageException("잘못된 접근입니다.");
			}

			// 등록
			String subDir = paramMap.get("subDir").toString();
			String savedFileNm = paramMap.get("savedFileNm").toString();
			String fileFullPath = basePathId + subDir + savedFileNm;

			// 엑셀 일괄등록 리스트
			String[] columnNames = new String[] { "wordTyCdNm", "wordEngAbrvNm", "relWordNm", "domnNm", "prhibtYn",	"wordCn" };

			// 엑셀 목록
			ArrayList<SangsMap> excelList = SangsExcelUtil.loadExcelList(fileFullPath, 1, 0, columnNames);

			// 데이터의 row전체가 빈값일 경우 row는 skip하여 값 있는 row 까지만 정상 데이터로 판단
			String[] upperCaseColumns = new String[] { "wordEngAbrvNm", "prhibtYn", "domnNm" };
			List<SangsMap> removeRowList = stdDicaryValidService.removeRowAllColEmptyWithUpper(excelList, upperCaseColumns);

			// DB 단어 목록
			dao.setLogFlag(false);
			List<SangsMap> wrdList = dao.selectList("meta_stddicary.selectStdDicaryWrdList", paramMap);

			// 도메인 목록
			paramMap.put("availUseYn", "Y");
			List<SangsMap> domnList = dao.selectList("meta_stddicary.selectStdDicaryMergedDomnList", paramMap);
			dao.setLogFlag(true);
			// 도메인
			SangsMap domnMap = new SangsMap();
			for (SangsMap map : domnList) {
				domnMap.putOrg(map.getString("domnNm"), map);
			}
			// 단어
//			SangsMap wrdMap = new SangsMap();
//			for (SangsMap map : wrdList) {
//				wrdMap.putOrg(map.getString("wrdEngAbrvNm"), map);
//			}
			// 용어 등록
			for (SangsMap map : removeRowList) {
				String domnNm = map.getString("domnNm");
				String wordEngAbrvNm = map.getString("wordEngAbrvNm");
				int nextWordSn = dao.selectInteger("meta_stddicary.selectNextStdDicaryWordSn", paramMap);
				map.putOrg("stdSetSn", stdSetSn);
				map.putOrg("wordSn", nextWordSn);
				String[] wordEngAbrvNmArr = new String[] {};
				if (wordEngAbrvNm.contains("_")) {
					wordEngAbrvNmArr = wordEngAbrvNm.split("_");
				}
				String wrdNm = "";
				String wrdEngNm = "";
				int index = 1;
				for (String item : wordEngAbrvNmArr) {
					boolean isAddWrd = false;
					boolean isMyStdSetSn = false;
					for (SangsMap wrdMap : wrdList) {
						String wrdEngAbrvNm = wrdMap.getString("wrdEngAbrvNm");
						String wrdTyCd = wrdMap.getString("wrdTyCd");
						int jStdSetSn = wrdMap.getInt("stdSetSn");
						if (item.equals(wrdEngAbrvNm)) {
							if ("TYPE01".equals(wrdTyCd)) {
								if (stdSetSn == jStdSetSn) {
									wrdNm += wrdMap.getString("wrdNm") + "_";
									wrdEngNm += wrdMap.getString("wrdEngNm") + " ";
									isMyStdSetSn = true;
								}
								if (!isMyStdSetSn) {
									wrdNm += wrdMap.getString("wrdNm") + "_";
									wrdEngNm += wrdMap.getString("wrdEngNm") + " ";
								}
								isAddWrd = true;
							} else {
								if (!isAddWrd) {
									wrdNm += wrdMap.getString("wrdNm") + "_";
									wrdEngNm += wrdMap.getString("wrdEngNm") + " ";

								}
							}
							map.putOrg("wrdSn", wrdMap.getInt("wrdSn"));
							map.putOrg("wrdRefrnStdSetSn", jStdSetSn);
							map.putOrg("wrdSortSn", index++);
							// 용어단어 맵핑테이블 등록
							dao.insert("meta_stddicary.insertStdDicaryWordWrdInfo", map);
						}
					}
					map.putOrg("wordNm", wrdNm);
					map.putOrg("wordEngNm", wrdEngNm);
				}
				String wordNm = map.getString("wordNm");
				String wordEngNm = map.getString("wordEngNm");
				char wordNmLastChar = wordNm.charAt(wordNm.length() - 1);
				char wordEngNmLastChar = wordEngNm.charAt(wordEngNm.length() - 1);
				if (wordNmLastChar == '_' || wordEngNmLastChar == ' ') {
					wordNm = wordNm.substring(0, wordNm.length() - 1);
					wordEngNm = wordEngNm.substring(0, wordEngNm.length() - 1);
					map.putOrg("wordNm", wordNm);
					map.putOrg("wordEngNm", wordEngNm);
				}

				if (domnMap.containsKey(domnNm)) {
					SangsMap domnNmMap = (SangsMap) domnMap.get(domnNm);
					map.putOrg("domnSn", domnNmMap.get("domnSn"));
					map.putOrg("domnRefrnStdSetSn", domnNmMap.get("mergedStdSetSn"));
					map.putOrg("delYn", "N");
					map.putOrg("useYn", "Y");
					map.putOrg("regUserId", regUserId);
					if ("표준어".equals(map.get("wordTyCdNm"))) {
						map.putOrg("wordTyCd", "TYPE01");
					} else if ("동의어".equals(map.get("wordTyCdNm"))) {
						map.putOrg("wordTyCd", "TYPE02");
					}
					// 용어 등록
					dao.insert("meta_stddicary.insertStdDicaryWordInfo", map);
				}

				Map<String, Object> histMap = new HashMap<String, Object>();
				histMap.put("wordSn", nextWordSn);
				histMap.put("location", "word");
				histMap.put("isApprover", isApprover);
				histMap.put("pmode", "A");
				histMap.put("prcsSeCd", "INS");

				if ("Y".equals(isApprover)) {
					histMap.put("aprvSttusCdNm", "등록");
				} else if ("N".equals(isApprover)) {
					histMap.put("aprvSttusCdNm", "요청");
				}
				// 용어 히스토리 등록 및 승인상태
				saveStdDicaryConfmInfo(histMap);
			}
			rtnMap.put("resultCd", "OK");
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}
	
	/**
	 * 표준사전 용어 일괄 등록 엑셀 양식 다운로드
	 * 
	 * @param paramMap
	 * @return
	 */
	public Workbook getStdDicaryWordExcelFormDown(Map<String, Object> paramMap) {

		Workbook workbook = null;

		try {
			
			List<Map<String, Object>> wordList = new ArrayList<Map<String,Object>>();
			Map<String, Object> wordParamMap = new HashMap<String, Object>();
			
			wordParamMap.put("wordTyCdNm", "표준어/동의어");
			//wordParamMap.put("wordNm", "상상회사");
			//wordParamMap.put("wordEngNm", "SANGSANG COMPANY");
			wordParamMap.put("wordEngAbrvNm", "USE_YN");
			wordParamMap.put("relWordNm", "");
			wordParamMap.put("domnNm", "여부C1");
			wordParamMap.put("prhibtYn", "Y/N");
			wordParamMap.put("wordCn", "설명");
			wordList.add(wordParamMap);
			
			Map<String, Object> domnParamMap = new HashMap<String, Object>();
			domnParamMap.put("stdSetSn", AuthUtil.getStdSetSn());
			domnParamMap.put("prjctSn", AuthUtil.getPrjctSn());
			domnParamMap.put("availUseYn", "Y");
			domnParamMap.put("excelYn", "Y");
			domnParamMap.put("aprvSttusCdArrList", paramMap.get("aprvSttusCdArrList"));
			List<SangsMap> domnList = dao.selectList("meta_stddicary.selectStdDicaryMergedDomnList", domnParamMap);
			
			SangsSimpleExcelMaker em = new SangsSimpleExcelMaker();
			workbook = em.createSheet("용어등록")
					.setHeaderColNm(new String[] {"*용어구분", "*영문약어명", "연관어", "*도메인명", "*금칙어", "설명" })
					.setHeaderColId(new String[] {"wordTyCdNm", "wordEngAbrvNm", "relWordNm", "domnNm", "prhibtYn", "wordCn"})
					.setList(wordList).setAutoSize().getWorkbook();
			
			workbook = em.createSheet("도메인정보")
					.setHeaderColNm(new String[] {"도메인그륩", "도메인분류명", "데이터타입", "데이터길이", "도메인명" })
					.setHeaderColId(new String[] {"domnGroupNm", "domnClNm", "dataTyCd", "dataLtValue", "domnNm"})
					.setList(domnList).setAutoSize().getWorkbook();
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
	 * 표준사전 도메인 일괄 등록 파일 읽어오기
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdDicaryDomnBundleList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			logger.debug("parameter : " + paramMap);

			String subDir = paramMap.get("subDir").toString();
			String savedFileNm = paramMap.get("savedFileNm").toString();
			String fileFullPath = basePathId + subDir + savedFileNm;
			Workbook workbook = new HSSFWorkbook(new FileInputStream(new File(fileFullPath)));
			int sheetCount = workbook.getNumberOfSheets();
			int cells = -1;

			for (int i = 0; i < sheetCount; i++) {
				Sheet sheet = workbook.getSheetAt(i);
				cells = sheet.getRow(i).getPhysicalNumberOfCells();
			}
			
			if (cells == 5) {
				String[] columnNames = new String[] { "domnGroupNm", "domnClNm", "dataTyCd", "dataLtValue", "domnCn" };
				// 엑셀 도메인 목록
				ArrayList<SangsMap> excelList = SangsExcelUtil.loadExcelList(fileFullPath, 1, 0, columnNames);
				
				// 데이터의 row전체가 빈값일 경우 row는 skip하여 값 있는 row 까지만 정상 데이터로 판단
				String[] upperCaseColumns = new String[] { "dataTyCd" };
				List<SangsMap> removeRowList = stdDicaryValidService.removeRowAllColEmptyWithUpper(excelList, upperCaseColumns);
			
				dao.setLogFlag(false);
				
				paramMap.put("stdSetSn", AuthUtil.getStdSetSn());
				paramMap.put("prjctSn", AuthUtil.getPrjctSn());
				paramMap.put("availUseYn", "Y");
				paramMap.put("excelYn", "Y");
				
				// DB 도메인 목록
				List<SangsMap> domnList = dao.selectList("meta_stddicary.selectStdDicaryMergedDomnList", paramMap);
				// DB 도메인 그룹 목록
				List<SangsMap> domnGroupList = dao.selectList("meta_stddicary.selectStdDicaryDomnGroupList", paramMap);
				// DB 데이터 타입 조회
				List<SangsMap> dataTyCdList = dao.selectList("meta_code.selectPrjectCnncDbmsSnList", paramMap);
				
				dao.setLogFlag(true);
				
				
				Map<String, Object> validMap = stdDicaryValidService.validStdDicaryDomn(domnList, removeRowList);
				List<SangsMap> validList = (List<SangsMap>) validMap.get("resultList");

				// 엑셀내 중복 검사
				List<SangsMap> dpcnChecList = stdDicaryValidService.setDpcnCheckWithRowNo(validList, "", "", "domn");
				
				// 승인상태 검사
				List<SangsMap> resultList = stdDicaryValidService.setApprovalCheck(dpcnChecList, domnList, "domnNm");
				
				// 도메인 그룹 목록
				SangsMap domnGroupMap = new SangsMap();

				for (SangsMap map : domnGroupList) {
					domnGroupMap.putOrg(map.getString("domnGroupNm"), map);
				}

				// 데이터 타입 목록
				SangsMap dataTyCdMap = new SangsMap();

				for (SangsMap map : dataTyCdList) {
					dataTyCdMap.putOrg(map.getString("code"), map);
				}
				
				String errorInfo = "";
				for (int i = 0; i < resultList.size(); i++) {
					String domnGroupNm = resultList.get(i).getString("domnGroupNm");
					String dataTyCd = resultList.get(i).getString("dataTyCd");

					if (!domnGroupMap.containsKey(domnGroupNm)) {
						errorInfo = resultList.get(i).getString("errorInfo");
						if ("".equals(errorInfo)) {
							resultList.get(i).putOrg("errorInfo", "존재하지않은도메인그룹명");
						} else {
							resultList.get(i).putOrg("errorInfo", errorInfo + ", 존재하지않은도메인그룹명");
						}
					}
					if (!dataTyCdMap.containsKey(dataTyCd)) {
						errorInfo = resultList.get(i).getString("errorInfo");
						if ("".equals(errorInfo)) {
							resultList.get(i).putOrg("errorInfo", "존재하지않은데이터타입");
						} else {
							resultList.get(i).putOrg("errorInfo", errorInfo + ", 존재하지않은데이터타입");
						}
					}
				}

				int totalErrorCnt = 0;
				int totalClearCnt = 0;
				for (SangsMap map : resultList) {
					if("".equals(map.getString("errorInfo"))) {
						totalClearCnt++;
					} else {
						totalErrorCnt++;
					}
				}
				rtnMap.put("totalTargetCnt", resultList.size()); // 총 오류 개수
				rtnMap.put("totalErrorCnt", totalErrorCnt); // 총 오류 개수
				rtnMap.put("totalClearCnt", totalClearCnt); // 총 정상 개수
				
				for(SangsMap map : resultList) {
					String tempCn = map.getString("domnCn");
					String domnSumCn = map.getString("domnCn");
					if(tempCn.getBytes().length > 10) {
						domnSumCn = SangsStringUtil.substringByte(tempCn, 30) + "...";
					}
					map.putOrg("domnSumCn", domnSumCn);
				}
				
				rtnMap.put("list", resultList);
			} else {
				throw new SangsMessageException("형식이 맞지 않아 업로드할 수 없습니다.");
			}
		} catch (OfficeXmlFileException e) {
			throw new SangsMessageException("파일확장자 xls 파일만 지원합니다. 양식다운로드 파일을 이용해주세요");
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}
	
	/**
	 * 표준사전 도메인 파일 등록
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> saveStdDicaryDomnFileList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);

			String regUserId = AuthUtil.getUserId();
			String isApprover = AuthUtil.isApprover();
			int stdSetSn = AuthUtil.getStdSetSn();

			String getIsApprover = paramMap.get("isApprover").toString();

			if (!isApprover.equals(getIsApprover)) {
				throw new SangsMessageException("잘못된 접근입니다.");
			}

			// 등록
			String subDir = paramMap.get("subDir").toString();
			String savedFileNm = paramMap.get("savedFileNm").toString();
			String fileFullPath = basePathId + subDir + savedFileNm;

			String[] columnNames = new String[] { "domnGroupNm", "domnClNm", "dataTyCd", "dataLtValue", "domnCn" };
			// 엑셀 도메인 목록
			ArrayList<SangsMap> excelList = SangsExcelUtil.loadExcelList(fileFullPath, 1, 0, columnNames);

			// 데이터의 row전체가 빈값일 경우  row는 skip하여 값 있는 row 까지만 정상 데이터로 판단
			String[] upperCaseColumns = new String[] { "dataTyCd" };
			List<SangsMap> removeRowList = stdDicaryValidService.removeRowAllColEmptyWithUpper(excelList, upperCaseColumns);

			paramMap.put("stdSetSn", AuthUtil.getStdSetSn());
			paramMap.put("excelYn", "Y");
			// 도메인그룹, 도메인분류 비교
			List<SangsMap> domnGroupList = dao.selectList("meta_stddicary.selectStdDicaryDomnGroupList", paramMap);
			SangsMap domnGroupMap = new SangsMap();
			for (SangsMap map : domnGroupList) {
				domnGroupMap.put(map.getString("domnGroupNm"), map);
			}
			for (SangsMap map : removeRowList) {
				int nextDomnSn = -1;
				String domnGroupNm = map.getString("domnGroupNm");
				String domnClNm = map.getString("domnClNm");
				if (domnGroupMap.containsKey(domnGroupNm)) {
					SangsMap domnGroupInfo = (SangsMap) domnGroupMap.get(domnGroupNm);
					int domnGroupSn = domnGroupInfo.getInt("domnGroupSn");
					map.putOrg("stdSetSn", stdSetSn);
					nextDomnSn = dao.selectInteger("meta_stddicary.selectNextStdDicaryDomnSn", map);
					String domnNm = domnClNm + map.getString("dataTyCd").substring(0, 1) + map.getString("dataLtValue");
					map.putOrg("domnGroupSn", domnGroupSn);
					map.putOrg("regUserId", regUserId);
					map.putOrg("domnSn", nextDomnSn);
					map.putOrg("domnNm", domnNm);
					map.putOrg("delYn", "N");
					map.putOrg("useYn", "Y");
					dao.insert("meta_stddicary.insertStdDicaryDomnInfo", map);
				}

				Map<String, Object> histMap = new HashMap<String, Object>();
				histMap.put("domnSn", nextDomnSn);
				histMap.put("location", "domn");
				histMap.put("isApprover", isApprover);
				histMap.put("pmode", "A");
				histMap.put("prcsSeCd", "INS");
				if ("Y".equals(isApprover)) {
					histMap.put("aprvSttusCdNm", "등록");
				} else if ("N".equals(isApprover)) {
					histMap.put("aprvSttusCdNm", "요청");
				} else {
					throw new SangsMessageException("잘못된 접근입니다.");
				}
				// 도메인 히스토리 등록 및 승인상태
				saveStdDicaryConfmInfo(histMap);
			}
			
			rtnMap.put("resultCd", "OK");
			
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}
	
	/**
	 * 표준사전 도메인 일괄 등록 엑셀 양식 다운로드
	 * 
	 * @param paramMap
	 * @return
	 */
	public Workbook getStdDicaryDomnExcelFormDown(Map<String, Object> paramMap) {

		Workbook workbook = null;

		try {
			
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			Map<String, Object> info = new HashMap<String, Object>();

			info.put("domnGroupNm", "내용");
			info.put("domnClNm", "내용");
			info.put("dataTyCd", "VARCHAR");
			info.put("dataLtValue", "2000");
			info.put("domnCn", "게시물 내용");

			list.add(info);

			SangsSimpleExcelMaker em = new SangsSimpleExcelMaker();
			workbook = em.createSheet()
					.setHeaderColNm(new String[] {"*도메인그룹", "*도메인분류명", "*데이터타입", "데이터길이(문자 유형 필수)", "설명"})
					.setHeaderColId(new String[] {"domnGroupNm", "domnClNm", "dataTyCd", "dataLtValue", "domnCn" })
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
	 * 표준사전 도메인 목록 조회
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdDicaryDomnList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			logger.debug("parameter : " + paramMap);

			paramMap.put("prjctSn", AuthUtil.getPrjctSn());
			paramMap.put("stdSetSn", AuthUtil.getStdSetSn());

			int pageNum = SangsStringUtil.nvlInt(paramMap.get("pageNum"), 1);

			// 전체 row 수 조회
			int totalCount = dao.selectCount("meta_stddicary.selectStdDicaryMergedDomnListCnt", paramMap);

			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);
			/*
			if("Y".equals(AuthUtil.isApprover())) {
				// 요청 건수 조회selectStdDicaryMergedDomnListCnt
				int requestCnt = dao.selectCount("meta_stddicary.selectStdDicaryMergedRequestDomnCnt", paramMap);
				rtnMap.put("requestCnt", requestCnt);
			}
 			*/

			/*
			if("Y".equals(AuthUtil.isApprover())) {
				// 요청 건수 조회
				List<SangsMap> sttusCntInfoList = dao.selectList("meta_stddicary.selectDomnCntListBySttusCd", paramMap);
				rtnMap.put("sttusCntInfoList", sttusCntInfoList);
			} else {
				rtnMap.put("sttusCntInfoList", new ArrayList<SangsMap>());
			}
			*/
			
			List<SangsMap> sttusCntInfoList = dao.selectList("meta_stddicary.selectDomnCntListBySttusCd", paramMap);
			rtnMap.put("sttusCntInfoList", sttusCntInfoList);
			
			paramMap.put("pageSize", pagingInfo.getPageSize());
			paramMap.put("offset", pagingInfo.getOffset());

			// 표준사전 도메인 목록 조회
			List<SangsMap> list = dao.selectList("meta_stddicary.selectStdDicaryMergedDomnList", paramMap);
			
			for(SangsMap smap : list) {
				String dataTypeLength = BizUtil.getDataTypeLengthTxt(AuthUtil.getDbmsNm() , smap.getString("dataTyCd"), smap.getString("dataLtValue"), smap.getString("dataLtValue"), smap.getString("dataDcmlpointLtValue"));
				smap.putOrg("dataTypeLength", dataTypeLength);
				
			}
			
			rtnMap.put("list", list);
			rtnMap.put("totalCount", totalCount);
			rtnMap.put("pagingInfo", pagingInfo);

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}

	/**
	 * 표준사전 도메인 등록/수정 처리
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> saveStdDicaryDomnInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			logger.debug("parameter : " + paramMap);

			String regUserId = AuthUtil.getUserId();
			String isApprover = AuthUtil.isApprover();
			int stdSetSn = AuthUtil.getStdSetSn();

			String getIsApprover = paramMap.get("isApprover").toString();

			if (!isApprover.equals(getIsApprover)) {
				throw new SangsMessageException("잘못된 접근입니다.");
			}

			SangsStringUtil.checkRequiredParam(paramMap, "pmode", "pmode");
			String pmode = String.valueOf(paramMap.get("pmode"));
			
			Map<String, Object> domnMap = (Map<String, Object>) paramMap.get("domnMap");
			domnMap.put("domnClNm", SangsWebUtil.clearXSSMinimum((String) domnMap.get("domnClNm")));
			domnMap.put("domnNm", SangsWebUtil.clearXSSMinimum((String) domnMap.get("domnNm")));
			domnMap.put("domnCn", SangsWebUtil.clearXSSMinimum((String) domnMap.get("domnCn"))); 
			domnMap.put("stdSetSn", stdSetSn);
			
			// 승인관리
			Map<String, Object> confmMap = new HashMap<String, Object>();
			int domnSn = -1;
			String prcsSeCd = "INS";
			if (!"".equals(domnMap.get("domnSn"))) {
				domnSn = Integer.parseInt(domnMap.get("domnSn").toString());
				prcsSeCd = "UPD";
			}
			// R : 등록, M : 수정
			if ("R".equals(pmode)) {

				// 신규 등록시 도메인명 중복 체크 2021.12.15 추가
				int chkCnt = dao.selectCount("meta_stddicary.selectCheckDuplDomnNmCnt", domnMap);
				if (chkCnt > 0) {
					throw new SangsMessageException("이미 존재하는 도메인명 입니다.");
				}

				// 도메인
				int nextDomnSn = dao.selectInteger("meta_stddicary.selectNextStdDicaryDomnSn", domnMap);
				domnMap.put("domnSn", nextDomnSn);
				domnMap.put("useYn", "Y");
				domnMap.put("delYn", "N");
				domnMap.put("regUserId", regUserId);
				dao.insert("meta_stddicary.insertStdDicaryDomnInfo", domnMap);

				domnSn = nextDomnSn;
				confmMap.put("aprvSttusCdNm", domnMap.get("aprvSttusCdNm").toString());
			} else if ("M".equals(pmode)) {
				// DB에 있는 데이터 비교
				SangsMap befMap = dao.selectOne("meta_stddicary.selectStdDicaryDomnInfo", domnMap);
				confmMap.put("aprvSttusCd", befMap.get("aprvSttusCd").toString());

				int nextDomnCopySn = dao.selectInteger("meta_stddicary.selectNextStdDicaryDomnCopySn", befMap);
				befMap.putOrg("domnCopySn", nextDomnCopySn);
				dao.insert("meta_stddicary.insertStdDicaryDomnCopyInfo", befMap);

				// DB의 값과 화면에서 수정한 값 비교하여 바뀐 함목정보 반환
				List<Map<String, Object>> chgList = getChangeItemList(befMap, domnMap, (List<Map<String, Object>>) paramMap.get("chgKeyList"));
				int nextDomnHistSn = dao.selectInteger("meta_stddicary.selectNextStdDicaryDomnHistSn", domnMap);
				for (Map<String, Object> chgMap : chgList) {
					chgMap.put("stdSetSn", stdSetSn);
					chgMap.put("domnSn", domnMap.get("domnSn"));
					chgMap.put("domnHistSn", nextDomnHistSn);
					chgMap.put("isApprover", isApprover);
					chgMap.put("regUserId", regUserId);
					chgMap.put("domnCopySn", nextDomnCopySn);
					dao.insert("meta_stddicary.insertStdDicaryDomnHistInfo", chgMap);
					nextDomnHistSn++;
				}

				// 표준사전 도메인 수정
				dao.update("meta_stddicary.updateStdDicaryDomnInfo", domnMap);

			} else {
				throw new SangsMessageException("잘못된 접근입니다.");
			}

			// 승인처리
			confmMap.put("domnSn", domnSn);
			confmMap.put("pmode", "A");
			confmMap.put("location", "domn");
			confmMap.put("prcsSeCd", prcsSeCd);
			confmMap.put("isApprover", isApprover);
			Map<String, Object> rtnVal = saveStdDicaryConfmInfo(confmMap);

			if ("OK".equals(rtnVal.get("resultCd"))) {
				rtnMap.put("resultCd", "OK");
			} else {
				rtnMap.put("resultCd", "NO");
			}

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}

	/**
	 * 표준사전 도메인 목록 상세 조회
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdDicaryDomnInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);

			if (!paramMap.containsKey("stdSetSn")) {
				paramMap.put("stdSetSn", AuthUtil.getStdSetSn());
			}

			// 표준사전 단어 목록 상세 조회
			SangsMap info = dao.selectOne("meta_stddicary.selectStdDicaryDomnInfo", paramMap);

			rtnMap.put("info", info);

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}

	/**
	 * 표준사전 도메인검사 목록
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdDicaryDomnCheckList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);
			paramMap.put("stdSetSn", AuthUtil.getStdSetSn());
			List<SangsMap> list = dao.selectList("meta_stddicary.selectStdDicaryDomnCheckList", paramMap);

			rtnMap.put("list", list);

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}
	
	/**
	 * 표준사전 도메인 엑셀 다운로드
	 * 
	 * @param paramMap
	 * @return
	 */
	public Workbook getStdDicaryDomnExcelDown(Map<String, Object> paramMap) {

		Workbook workbook = null;

		try {
			paramMap.put("stdSetSn", AuthUtil.getStdSetSn());
			paramMap.put("excelYn", "Y");

			List<SangsMap> list = dao.selectList("meta_stddicary.selectStdDicaryMergedDomnList", paramMap);
			commonCodeService.setCmmnCodeNmForList(list
					, new String[] { "CONFMCDTY" }
					, new String[] { "aprvSttusCd" }
					, new String[] { "aprvSttusCdNm" }
			);

			SangsSimpleExcelMaker em = new SangsSimpleExcelMaker();
			workbook = em.createSheet()
					.setHeaderColNm(new String[] { "도메인그룹명", "도메인분류명", "데이터타입", "데이터길이", "도메인명", "사용여부", "승인상태" })
					.setHeaderColId(new String[] { "domnGroupNm", "domnClNm", "dataTyCd", "dataLtValue", "domnNm", "availUseYn", "aprvSttusCdNm" })
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
	 * 표준사전 도메인그룹관리 목록 조회
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdDicaryDomnGroupList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			logger.debug("parameter : " + paramMap);

			// 표준사전 도메인그룹 관리 목록 조회
			List<SangsMap> list = dao.selectList("meta_stddicary.selectStdDicaryDomnGroupList", paramMap);

			rtnMap.put("list", list);

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}
	
	/**
	 * 표준사전 도메인그룹관리 상세 조회
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdDicaryDomnGroupListDetail(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			logger.debug("parameter : " + paramMap);
			paramMap.put("stdSetSn", AuthUtil.getStdSetSn());
			paramMap.put("excelYn", "Y");
			// 표준사전 도메인 목록 조회
			List<SangsMap> list = dao.selectList("meta_stddicary.selectStdDicaryMergedDomnList", paramMap);
			rtnMap.put("list", list);

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}
	
	/**
	 * 표준사전 도메인그룹 등록/수정/삭제
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> saveStdDicaryDomnGroupInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			logger.debug("parameter : " + paramMap);

			SangsStringUtil.checkRequiredParam(paramMap, "pmode", "pmode");

			String pmode = String.valueOf(paramMap.get("pmode"));
			if (!"D".equals(pmode)) {
				List<SangsMap> list = dao.selectList("meta_stddicary.selectStdDicaryDomnGroupList", paramMap);

				String domnGroupNm = paramMap.get("domnGroupNm").toString();
				for (SangsMap map : list) {
					if (domnGroupNm.equals(map.getString("domnGroupNm"))) {
						String errorMsg = "\"" + domnGroupNm + "\" 도메인그룹명 중복";
						if (!SangsStringUtil.isEmpty(errorMsg))
							throw new SangsMessageException(errorMsg);
					}
				}
			}

			String regUserId = AuthUtil.getUserId();
			paramMap.put("regUserId", regUserId);

			if ("R".equals(pmode)) {
				// 도메인 그룹 정렬 번호
				int nextDomnGroupSortSn = dao.selectInteger("meta_stddicary.selectNextStdDicaryDomnGroupSortSn", paramMap);
				// 도메인 그룹
				paramMap.put("sortSn", nextDomnGroupSortSn);
				paramMap.put("delYn", "N");
				paramMap.put("domnGroupSn", nextDomnGroupSortSn);
				dao.insert("meta_stddicary.insertStdDicaryDomnGroupInfo", paramMap);
			} else if ("M".equals(pmode)) {
				dao.update("meta_stddicary.updateStdDicaryDomnGroupInfo", paramMap);
			} else if ("D".equals(pmode)) {
				paramMap.put("stdSetSn", AuthUtil.getStdSetSn());
				paramMap.put("excelYn", "Y");
				List<SangsMap> domnList = dao.selectList("meta_stddicary.selectStdDicaryMergedDomnList", paramMap);
				int domnGroupSn = Integer.parseInt(String.valueOf(paramMap.get("domnGroupSn")));
				for (SangsMap map : domnList) {
					if (domnGroupSn == map.getInt("domnGroupSn")) {
						String domnGroupNm = map.getString("domnGroupNm");
						String errorMsg = "\"" + domnGroupNm + "\" 사용중인 도메인그룹 삭제불가";
						if (!SangsStringUtil.isEmpty(errorMsg))
							throw new SangsMessageException(errorMsg);
					}
				}

				paramMap.put("delYn", "Y");
				dao.update("meta_stddicary.updateStdDicaryDomnGroupInfo", paramMap);
			}

			rtnMap.put("resultCd", "OK");

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}
	
	/**
	 * 표준사전 용어 목록 조회
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdDicaryWordList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			logger.debug("parameter : " + paramMap);

			String wordNm = String.valueOf(paramMap.get("wordNm"));
			String wordEngAbrvNm = String.valueOf(paramMap.get("wordEngAbrvNm"));

			wordNm = this.getWildCardReplace(wordNm);
			wordEngAbrvNm = this.getWildCardReplace(wordEngAbrvNm);
 

			paramMap.put("wordNm", wordNm);
			paramMap.put("wordEngAbrvNm", wordEngAbrvNm);
			paramMap.put("prjctSn", AuthUtil.getPrjctSn());
			paramMap.put("stdSetSn", AuthUtil.getStdSetSn());

			int pageNum = SangsStringUtil.nvlInt(paramMap.get("pageNum"), 1);

			// 전체 row 수 조회
			int totalCount = dao.selectCount("meta_stddicary.selectStdDicaryWordListCnt", paramMap);
			
			/*
			if ("Y".equals(AuthUtil.isApprover())) {
				// 요청 건수 조회 selectStdDicaryMergedDomnListCnt
				//int requestCnt = dao.selectCount("meta_stddicary.selectStdDicaryWordCntBySttusCd", paramMap);
				List<SangsMap> sttusCntInfoList = dao.selectList("meta_stddicary.selectWordCntListBySttusCd", paramMap);
				rtnMap.put("sttusCntInfoList", sttusCntInfoList);
			} else {
				rtnMap.put("sttusCntInfoList", new ArrayList<SangsMap>());
			}
 			*/
			
			List<SangsMap> sttusCntInfoList = dao.selectList("meta_stddicary.selectWordCntListBySttusCd", paramMap);
			rtnMap.put("sttusCntInfoList", sttusCntInfoList);
			
			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);

			paramMap.put("pageSize", pagingInfo.getPageSize());
			paramMap.put("offset", pagingInfo.getOffset());

			// 표준사전 단어 목록 조회
			List<SangsMap> list = dao.selectList("meta_stddicary.selectStdDicaryWordList", paramMap);

			rtnMap.put("list", list);
			rtnMap.put("totalCount", totalCount);
			rtnMap.put("pagingInfo", pagingInfo);

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}
	
	/**
	 * 표준사전 용어 목록 상세 조회
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdDicaryWordInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);
			paramMap.put("stdSetSn", AuthUtil.getStdSetSn());
			// 표준사전 단어 목록 상세 조회
			SangsMap info = dao.selectOne("meta_stddicary.selectStdDicaryWordInfo", paramMap);
			
			rtnMap.put("info", info);

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}
	
	/**
	 * 표준사전 용어명 조회 목록
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdDicarySearchWordNmWrdList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			logger.debug("parameter : " + paramMap);

			paramMap.put("stdSetSn", AuthUtil.getStdSetSn());

			List<SangsMap> wrdList = dao.selectList("meta_stddicary.selectStdDicarySearchWordNmWrdList", paramMap);
			List<SangsMap> list = new ArrayList<SangsMap>();
			List<SangsMap> resultList = new ArrayList<SangsMap>();
			
			String wordNm = paramMap.get("wordNm").toString().toUpperCase();
			String[] wrdNmArr = new String[] {};

			if (wordNm.contains(" ")) {
				wrdNmArr = wordNm.split(" ");
			} else if (wordNm.contains("_")) {
				wrdNmArr = wordNm.split("_");
			}

			if (wrdNmArr.length != 0) {
				int stdSetSort = 0; // stdSetSort = 1. 자신 세트, 2. 공통 세트, 3. 타 세트
				String wrdNm = "";
				String wrdEngAbrvNm = "";
				boolean exist;
				
				// 비교할 타 세트 목록 
				List<SangsMap> otherSetList = new ArrayList<SangsMap>();
				
				for (String cmprWrdNm : wrdNmArr) {
					exist = false;
					for (SangsMap map : wrdList) {
						stdSetSort = map.getInt("stdSetSort");
						wrdNm = map.getString("wrdNm");
						wrdEngAbrvNm = map.getString("wrdEngAbrvNm");

						if (cmprWrdNm.equals(wrdNm) || cmprWrdNm.equals(wrdEngAbrvNm)) {
							if (stdSetSort != 3) {
								list.add(map);
							} else {
								otherSetList.add(map);
							}
							exist = true;
						}
					}
					if (!exist) {
						SangsMap map = new SangsMap();
						map.putOrg("stdSetSn", "");
						map.putOrg("wrdNm", cmprWrdNm);
						map.putOrg("wrdSn", "");
						map.putOrg("relWrdNm", "-");
						map.putOrg("wrdEngNm", "-");
						map.putOrg("wrdEngAbrvNm", "-");
						map.putOrg("prhibtYn", "-");
						map.putOrg("wrdTyCd", "-");
						map.putOrg("stdWrdNm", "-");
						map.putOrg("aprvSttusCd", "-");
						map.putOrg("stdSetNm", "-");
						map.putOrg("stdSetTyCd", "");
						map.putOrg("noRegist", "미등록단어");
						map.putOrg("groupSort", "");
						map.putOrg("rowSort", "");
						map.putOrg("registYn", "N");
						map.putOrg("stdSetSort", "4");
						map.putOrg("wrdUseCnt", "0");
						list.add(map);
					}
				}
				
				// 타 세트와 비교 for문
				for (SangsMap otherSetMap : otherSetList) {
					exist = false;
					String oWrdNm = otherSetMap.getString("wrdNm");
					String oWrdEngAbrvNm = otherSetMap.getString("wrdEngAbrvNm");

					for (SangsMap map : list) {

						wrdNm = map.getString("wrdNm");
						wrdEngAbrvNm = map.getString("wrdEngAbrvNm");

						if (oWrdNm.contains(wrdNm) || oWrdEngAbrvNm.contains(wrdEngAbrvNm)) {
							exist = true;
						}
					}
					if (!exist) {
						list.add(otherSetMap);
					}
				}
				// 중복 데이터 비교 for문
				for (int i = 0; i < list.size(); i++) {
					exist = false;
					for (int j = i + 1; j < list.size(); j++) {
						
						String iWrdNm = list.get(i).get("wrdNm").toString();
						String jWrdNm = list.get(j).get("wrdNm").toString();

						String iWrdEngAbrvNm = list.get(i).get("wrdEngAbrvNm").toString();
						String jWrdEngAbrvNm = list.get(j).get("wrdEngAbrvNm").toString();

						if (iWrdNm.equals(jWrdNm) && iWrdEngAbrvNm.equals(jWrdEngAbrvNm)) {
							exist = true;
						}
					}
					if (!exist) {
						resultList.add(list.get(i));
					}
				}
				
				rtnMap.put("list", resultList);
			} else {
				rtnMap.put("list", wrdList);
			}
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}
	
	/**
	 * 표준사전 다른 표준세트 단어 등록
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdDicarySaveOtherWrdInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);

			String isApprover = AuthUtil.isApprover();
			String getIsApprover = paramMap.get("isApprover").toString();

			if (!isApprover.equals(getIsApprover)) {
				throw new SangsMessageException("잘못된 접근입니다.");
			}

			SangsStringUtil.checkRequiredParam(paramMap, "pmode", "pmode");
			String pmode = String.valueOf(paramMap.get("pmode"));

			SangsMap info = dao.selectOne("meta_stddicary.selectStdDicaryWrdInfo", paramMap);

			// 등록
			if ("R".equals(pmode)) {

				// 등록전 체크 사항
				String errorMsg = chkSaveStdDicaryWrdInfo(info);

				if (!SangsStringUtil.isEmpty(errorMsg))
					throw new SangsMessageException(errorMsg);

				info.put("std_set_sn", AuthUtil.getStdSetSn());

				int nextWrdSn = dao.selectInteger("meta_stddicary.selectNextStdDicaryWrdSn", info);
				info.put("wrd_sn", nextWrdSn);
				info.put("del_yn", "N");
				info.put("use_yn", "Y");
				dao.insert("meta_stddicary.insertStdDicaryWrdInfo", info);

				Map<String, Object> map = new HashMap<String, Object>();
				if ("Y".equals(isApprover)) {
					map.put("aprvSttusCd", "APPROVAL");
					map.put("aprvSttusCdNm", "등록");
				} else {
					map.put("aprvSttusCd", "REQUEST");
					map.put("aprvSttusCdNm", "요청");
				}
				map.put("pmode", "A");
				map.put("location", "wrd");
				map.put("prcsSeCd", "INS");
				map.put("wrdSn", nextWrdSn);
				map.put("isApprover", isApprover);
				Map<String, Object> rtnVal = saveStdDicaryConfmInfo(map);

				if ("OK".equals(rtnVal.get("resultCd"))) {
					rtnMap.put("resultCd", "OK");
				} else {
					rtnMap.put("resultCd", "NO");
				}
			} else {
				throw new SangsMessageException("잘못된 접근입니다.");
			}

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}
	
	/**
	 * 표준사전 용어 등록/수정 처리
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> saveStdDicaryWordInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);

			String regUserId = AuthUtil.getUserId();
			String isApprover = AuthUtil.isApprover();
			int stdSetSn = AuthUtil.getStdSetSn();

			String getIsApprover = paramMap.get("isApprover").toString();

			if (!isApprover.equals(getIsApprover)) {
				throw new SangsMessageException("잘못된 접근입니다.");
			}

			SangsStringUtil.checkRequiredParam(paramMap, "pmode", "pmode");
			String pmode = String.valueOf(paramMap.get("pmode"));

			// 작성한 정보
			Map<String, Object> wordMap = (Map<String, Object>) paramMap.get("wordMap");
			wordMap.put("wordEngNm", SangsWebUtil.clearXSSMinimum((String) wordMap.get("wordEngNm")));
			wordMap.put("relWordNm", SangsWebUtil.clearXSSMinimum((String) wordMap.get("relWordNm")));
			wordMap.put("wordCn", SangsWebUtil.clearXSSMinimum((String) wordMap.get("wordCn")));
			wordMap.put("stdSetSn", stdSetSn);

			// 승인관리
			Map<String, Object> confmMap = new HashMap<String, Object>();
			int wordSn = -1;
			String prcsSeCd = "INS";
			if (!"".equals(wordMap.get("wordSn"))) {
				wordSn = Integer.parseInt(wordMap.get("wordSn").toString());
				prcsSeCd = "UPD";
			}

			// 등록
			if ("R".equals(pmode)) {

				// 등록전 체크 사항
				String errorMsg = chkSaveStdDicaryWordInfo(wordMap);

				if (!SangsStringUtil.isEmpty(errorMsg)) {
					throw new SangsMessageException(errorMsg);
				}

				int nextWordSn = dao.selectInteger("meta_stddicary.selectNextStdDicaryWordSn", wordMap);
				wordMap.put("wordSn", nextWordSn);
				wordMap.put("delYn", "N");
				wordMap.put("useYn", "Y");
				wordMap.put("regUserId", regUserId);
				dao.insert("meta_stddicary.insertStdDicaryWordInfo", wordMap);

				String wrdSnList = wordMap.get("wrdSn").toString();
				String wrdRefrnStdSetSnList = wordMap.get("wrdRefrnStdSetSn").toString();

				String[] wrdSnArr = wrdSnList.split(",");
				String[] wrdRefrnStdSetSnArr = wrdRefrnStdSetSnList.split(",");

				for (int i = 0; i < wrdSnArr.length; i++) {
					Map<String, Object> wordWrdInfo = new HashMap<String, Object>();
					wordWrdInfo.put("wordSn", nextWordSn);
					wordWrdInfo.put("stdSetSn", stdSetSn);
					wordWrdInfo.put("wrdSn", wrdSnArr[i]);
					wordWrdInfo.put("wrdSortSn", i + 1);
					wordWrdInfo.put("wrdRefrnStdSetSn", wrdRefrnStdSetSnArr[i]);

					// 표준사전 용어단어 맵핑테이블 등록
					dao.insert("meta_stddicary.insertStdDicaryWordWrdInfo", wordWrdInfo);
				}
				wordSn = nextWordSn;
				confmMap.put("aprvSttusCdNm", wordMap.get("aprvSttusCdNm").toString());
			} else if ("M".equals(pmode)) {

				SangsMap befMap = dao.selectOne("meta_stddicary.selectStdDicaryWordInfo", wordMap);
				confmMap.put("aprvSttusCd", befMap.get("aprvSttusCd").toString());
				// DB의 값과 화면에서 수정한 값 비교하여 바뀐 함목정보 반환
				List<Map<String, Object>> chgList = getChangeItemList(befMap, wordMap, (List<Map<String, Object>>) paramMap.get("chgKeyList"));

				int nextWordCopySn = dao.selectInteger("meta_stddicary.selectNextStdDicaryWordCopySn", befMap);
				befMap.putOrg("wordCopySn", nextWordCopySn);
				dao.insert("meta_stddicary.insertStdDicaryWordCopyInfo", befMap);

				// 표준사전 단어 이력 순번 조회
				int nextWordHistSn = dao.selectInteger("meta_stddicary.selectNextStdDicaryWordHistSn", wordMap);
				for (Map<String, Object> chgMap : chgList) {
					chgMap.put("stdSetSn", stdSetSn);
					chgMap.put("wordSn", wordMap.get("wordSn"));
					chgMap.put("wordHistSn", nextWordHistSn);
					chgMap.put("isApprover", isApprover);
					chgMap.put("regUserId", regUserId);
					chgMap.put("wordCopySn", nextWordCopySn);
					// 표준사전 단어 이력 등록
					dao.insert("meta_stddicary.insertStdDicaryWordHistInfo", chgMap);
					nextWordHistSn++;
				}
				// 표준사전 단어 수정
				dao.update("meta_stddicary.updateStdDicaryWordInfo", wordMap);

			} else {
				throw new SangsMessageException("잘못된 접근입니다.");
			}

			confmMap.put("wordSn", wordSn);
			confmMap.put("pmode", "A");
			confmMap.put("location", "word");
			confmMap.put("isApprover", isApprover);
			confmMap.put("prcsSeCd", prcsSeCd);
			Map<String, Object> rtnVal = saveStdDicaryConfmInfo(confmMap);

			if ("OK".equals(rtnVal.get("resultCd"))) {
				rtnMap.put("resultCd", "OK");
			} else {
				rtnMap.put("resultCd", "NO");
			}

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}
	
	// 용어 등록 전 체크 
	private String chkSaveStdDicaryWordInfo(Map<String, Object> paramMap) throws Exception {
		String errorMsg = "";

		SangsMap targetMap = new SangsMap();
		targetMap.putOrg("wordEngAbrvNm", paramMap.get("wordEngAbrvNm"));
		targetMap.putOrg("prjctSn", AuthUtil.getPrjctSn());
		targetMap.putOrg("stdSetSn", paramMap.get("stdSetSn"));

		int totalCount = dao.selectCount("meta_stddicary.selectStdDicaryWordListCnt", targetMap);
		targetMap.putOrg("pageSize", totalCount); // 전체 조회를 위해서
		targetMap.putOrg("offset", 0);

		// 표준사전 단어 목록 조회
		List<SangsMap> list = dao.selectList("meta_stddicary.selectStdDicaryWordList", targetMap);

		targetMap.putOrg("wrdEngNm", paramMap.get("wrdEngNm"));
		targetMap.putOrg("wrdEngAbrvNm", paramMap.get("wrdEngAbrvNm"));
		targetMap.putOrg("prhibtYn", paramMap.get("prhibtYn"));

		List<SangsMap> targetList = new ArrayList<SangsMap>();
		targetList.add(targetMap);

		Map<String, Object> resultValidationMap = stdDicaryValidService.validStdDicaryWord(list, targetList);

		if (!"".equals(resultValidationMap.get("errorInfo"))) {
			SangsMap targetTempMap = targetList.get(0);
			errorMsg = String.valueOf(targetTempMap.get("errorInfo"));
		}

		return errorMsg;
	}
		
	/**
	 * 표준사전 용어 엑셀 다운로드
	 * 
	 * @param paramMap
	 * @return
	 */
	public Workbook getStdDicaryWordExcelDown(Map<String, Object> paramMap) {

		Workbook workbook = null;

		try {

			String wordNm = String.valueOf(paramMap.get("wordNm"));
			String wordEngAbrvNm = String.valueOf(paramMap.get("wordEngAbrvNm"));

			
			wordNm = this.getWildCardReplace(wordNm);
			wordEngAbrvNm = this.getWildCardReplace(wordEngAbrvNm);
 

			paramMap.put("wordNm", wordNm);
			paramMap.put("wordEngAbrvNm", wordEngAbrvNm);
			paramMap.put("excelYn", "Y");
			paramMap.put("prjctSn", AuthUtil.getPrjctSn());
			paramMap.put("stdSetSn", AuthUtil.getStdSetSn());

			List<SangsMap> list = dao.selectList("meta_stddicary.selectStdDicaryWordList", paramMap);
			// 리스트 안에 있는 코드에 대한 코드명 setting
			commonCodeService.setCmmnCodeNmForList(list, new String[] { "CONFMCDTY", "WRDCDTY" },
					new String[] { "aprvSttusCd", "wordTyCd" }, new String[] { "aprvSttusCdNm", "wordTyCdNm" });
			SangsSimpleExcelMaker em = new SangsSimpleExcelMaker();
			workbook = em.createSheet()
					.setHeaderColNm(new String[] { "용어구분", "용어명", "영문약어명", "영문명", "표준어", "도메인그룹", "도메인명", "금칙어", "승인상태" })
					.setHeaderColId(new String[] { "wordTyCdNm", "wordNm", "wordEngAbrvNm", "wordEngNm", "","domnGroupNm", "domnNm", "prhibtYn", "aprvSttusCdNm" })
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
	 * 표준사전 승인관리
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> saveStdDicaryConfmInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			logger.debug("parameter : " + paramMap);

			int stdSetSn = AuthUtil.getStdSetSn();
			String regUserId = AuthUtil.getUserId();
			String isApprover = AuthUtil.isApprover();

			String pmode = String.valueOf(paramMap.get("pmode"));
			String location = String.valueOf(paramMap.get("location"));
			String getIsApprover = String.valueOf(paramMap.get("isApprover"));

			SangsStringUtil.checkRequiredParam(paramMap, "pmode", "pmode");

			if (!isApprover.equals(getIsApprover)) {
				throw new SangsMessageException("잘못된 접근입니다.");
			}

			paramMap.put("stdSetSn", stdSetSn);
			paramMap.put("chgIemNm", "승인상태");

			SangsMap info = new SangsMap();
			SangsMap copyInfo = new SangsMap();

			if (!paramMap.containsKey("aprvSttusCdNm")) {
				// 승인,반려,삭제,수정 버튼
				if ("word".equals(location)) {
					info = dao.selectOne("meta_stddicary.selectStdDicaryWordHistInfo", paramMap);
				} else if ("wrd".equals(location)) {
					info = dao.selectOne("meta_stddicary.selectStdDicaryWrdHistInfo", paramMap);
				} else if ("domn".equals(location)) {
					info = dao.selectOne("meta_stddicary.selectStdDicaryDomnHistInfo", paramMap);
				}
				paramMap.put("bfchgCn", info.getString("aftchCn")); // 변경후 내용을 변경전 내용에 이동
				paramMap.put("rqstDt", info.getString("rqstDt")); // 요청일자
				paramMap.put("regDt", info.getString("regDt")); // 등록일자
				String prcsSeCd = info.getString("prcsSeCd"); // 처리구분코드
				paramMap.put("prcsSeCd", prcsSeCd);

				if ("Y".equals(isApprover)) {
					paramMap.put("regUserId", ""); // 요청자
					paramMap.put("chgUserId", regUserId);
					if ("A".equals(pmode)) {
						if (!"DEL".equals(prcsSeCd)) {
							paramMap.put("aprvSttusCd", "APPROVAL");
							paramMap.put("aftchCn", "승인");
						} else {
							paramMap.put("aftchCn", "삭제승인");
						}
					} else if ("D".equals(pmode)) {
						paramMap.put("aftchCn", "삭제");
						paramMap.put("prcsSeCd", "DEL");
					} else if ("RT".equals(pmode)) {
						if ("INS".equals(prcsSeCd) || "DEL".equals(prcsSeCd)) {
							paramMap.put("aftchCn", "반려");
							paramMap.put("aprvSttusCd", "RETURN");
							paramMap.put("prcsSeCd", "UPD");
						} else if ("UPD".equals(prcsSeCd)) {
							paramMap.put("aftchCn", "반려");
							paramMap.put("aprvSttusCd", "RETURN");
							paramMap.put("prcsSeCd", "UPD");
							if ("wrd".equals(location)) {
								copyInfo = dao.selectOne("meta_stddicary.selectStdDicaryWrdCopyInfo", paramMap);
							} else if ("domn".equals(location)) {
								copyInfo = dao.selectOne("meta_stddicary.selectStdDicaryDomnCopyInfo", paramMap);
							} else if ("word".equals(location)) {
								copyInfo = dao.selectOne("meta_stddicary.selectStdDicaryWordCopyInfo", paramMap);
							}
						}
					}
				} else if ("N".equals(isApprover)) {
					paramMap.put("regUserId", regUserId); // 요청자
					paramMap.put("aprvSttusCd", "REQUEST");
					if ("A".equals(pmode)) {
						paramMap.put("aftchCn", "승인요청");
						if ("INS".equals(prcsSeCd)) {
							paramMap.put("prcsSeCd", "UPD");
						}
					} else if ("D".equals(pmode)) {
						paramMap.put("aftchCn", "삭제요청");
						paramMap.put("prcsSeCd", "DEL");
					}
				}

			} else {

				// 등록 할때
				String aprvSttusCdNm = String.valueOf(paramMap.get("aprvSttusCdNm"));
				paramMap.put("bfchgCn", aprvSttusCdNm);
				paramMap.put("regUserId", regUserId);
				if ("Y".equals(isApprover)) {
					paramMap.put("aprvSttusCd", "APPROVAL");
					paramMap.put("chgUserId", regUserId);
					if ("A".equals(pmode)) {
						paramMap.put("aftchCn", "승인");
					}
				} else if ("N".equals(isApprover)) {
					paramMap.put("aprvSttusCd", "REQUEST");
					if ("A".equals(pmode)) {
						paramMap.put("aftchCn", "승인요청");
					}
				}

			}

			// 수정, 등록
			if (!"D".equals(pmode)) {
				if (copyInfo.isEmpty()) {
					if ("wrd".equals(location)) {
						dao.update("meta_stddicary.updateStdDicaryWrdConfmInfo", paramMap);
					} else if ("domn".equals(location)) {
						dao.update("meta_stddicary.updateStdDicaryDomnConfmInfo", paramMap);
					} else if ("word".equals(location)) {
						dao.update("meta_stddicary.updateStdDicaryWordConfmInfo", paramMap);
					}
				}
			}
			// 삭제
			if ("DEL".equals(paramMap.get("prcsSeCd"))) {
				if ("Y".equals(isApprover)) {
					paramMap.put("useYn", "N");
					paramMap.put("delYn", "Y");
					paramMap.put("aprvSttusCd", "DELETE");
				}
				if ("wrd".equals(location)) {
					dao.update("meta_stddicary.updateStdDicaryWrdConfmInfo", paramMap);
				} else if ("domn".equals(location)) {
					dao.update("meta_stddicary.updateStdDicaryDomnConfmInfo", paramMap);
				} else if ("word".equals(location)) {
					dao.update("meta_stddicary.updateStdDicaryWordConfmInfo", paramMap);
				}
			}
			// 반려
			if ("RT".equals(pmode)) {
				if (!copyInfo.isEmpty()) {
					copyInfo.putOrg("regDt", copyInfo.getString("regDt"));
					if ("wrd".equals(location)) {
						dao.update("meta_stddicary.updateStdDicaryWrdRtnInfo", copyInfo);
					} else if ("domn".equals(location)) {
						dao.update("meta_stddicary.updateStdDicaryDomnRtnInfo", copyInfo);
					} else if ("word".equals(location)) {
						dao.update("meta_stddicary.updateStdDicaryWordRtnInfo", copyInfo);
					}
				}
			}

			if ("domn".equals(location)) {
				int nextDomnHistSn = dao.selectInteger("meta_stddicary.selectNextStdDicaryDomnHistSn", paramMap);
				paramMap.put("domnHistSn", nextDomnHistSn);
				dao.insert("meta_stddicary.insertStdDicaryDomnHistInfo", paramMap);
			} else if ("wrd".equals(location)) {
				int nextWrdHistSn = dao.selectInteger("meta_stddicary.selectNextStdDicaryWrdHistSn", paramMap);
				paramMap.put("wrdHistSn", nextWrdHistSn);
				dao.insert("meta_stddicary.insertStdDicaryWrdHistInfo", paramMap);
			} else if ("word".equals(location)) {
				int nextWordHistSn = dao.selectInteger("meta_stddicary.selectNextStdDicaryWordHistSn", paramMap);
				paramMap.put("wordHistSn", nextWordHistSn);
				dao.insert("meta_stddicary.insertStdDicaryWordHistInfo", paramMap);
			}

			rtnMap.put("resultCd", "OK");

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}
	
	/**
	 * 표준사전 용어 엑셀로저장 다운로드
	 * 
	 * @param paramMap
	 * @return
	 */
	public Workbook getStdDicaryWordExcelSaveDown(Map<String, Object> paramMap) {

		Workbook workbook = null;

		try {
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = (List<Map<String, Object>>) paramMap.get("list");	

			SangsSimpleExcelMaker em = new SangsSimpleExcelMaker();
			workbook = em.createSheet()
					.setHeaderColNm(new String[] { "NO", "용어구분", "용어명", "영문약어명", "영문명", "연관어", "설명", "도메인명", "금칙어", "비고", "오류상태" })
					.setHeaderColId(new String[] { "EXCEL_ROW_NO", "wordTyCdNm", "wordNm", "wordEngAbrvNm", "wordEngNm", "relWordNm", "wordCn", "domnNm", "prhibtYn", "etcCn", "errorInfo" })
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
	 * 표준사전 단어 엑셀로저장 다운로드
	 * 
	 * @param paramMap
	 * @return
	 */
	public Workbook getStdDicaryWrdExcelSaveDown(Map<String, Object> paramMap) {

		Workbook workbook = null;

		try {
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = (List<Map<String, Object>>) paramMap.get("list");			

			SangsSimpleExcelMaker em = new SangsSimpleExcelMaker();
			workbook = em.createSheet()
					.setHeaderColNm(new String[] { "NO", "단어구분", "단어명", "영문약어명", "영문명", "연관어", "금칙어", "설명", "오류상태", "비고" })
					.setHeaderColId(new String[] { "EXCEL_ROW_NO", "wrdTyCdNm", "wrdNm", "wrdEngAbrvNm", "wrdEngNm", "relWrdNm", "prhibtYn", "wrdCn", "etcCn", "errorInfo" })
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
	 * 표준사전 도메인 엑셀로저장 다운로드
	 * 
	 * @param paramMap
	 * @return
	 */
	public Workbook getStdDicaryDomnExcelSaveDown(Map<String, Object> paramMap) {

		Workbook workbook = null;

		try {
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = (List<Map<String, Object>>) paramMap.get("list");	

			SangsSimpleExcelMaker em = new SangsSimpleExcelMaker();
			workbook = em.createSheet()
					.setHeaderColNm(new String[] { "NO", "도메인그룹", "도메인분류명", "데이터타입", "데이터길이", "설명", "비고", "오류상태" })
					.setHeaderColId(new String[] { "EXCEL_ROW_NO", "domnGroupNm", "domnClNm", "domnClNm", "dataLtValue", "domnCn", "etcCn", "errorInfo" })
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
	 * 공통표준세트에서 자신의 도메인으로 복사
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> saveCopyCmmnToMyDomn(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);

			String isApprover = AuthUtil.isApprover();
			int stdSetSn = AuthUtil.getStdSetSn();
			 
			String getIsApprover = paramMap.get("isApprover").toString();
			if (!isApprover.equals(getIsApprover)) {
				throw new SangsMessageException("잘못된 접근입니다.");
			}
			
			// copy할 도메인 조회
			paramMap.put("stdSetSn", paramMap.get("fromStdSetSn"));
			paramMap.put("domnSn", paramMap.get("fromDomnSn"));
			SangsMap info = dao.selectOne("meta_stddicary.selectStdDicaryDomnInfo", paramMap);
	 
			info.putOrg("stdSetSn", stdSetSn);
			info.putOrg("regUserId", AuthUtil.getUserId());
			
			// 신규 도메인 순번 추출 
			info.putOrg("domnSn", dao.selectInteger("meta_stddicary.selectNextStdDicaryDomnSn", info));
			
			// 도메인 신규 등록
			dao.insert("meta_stddicary.insertStdDicaryDomnInfo", info);
			
			paramMap.putAll(info);
			paramMap.put("pmode", "A");
			
			
			paramMap.put("location", "domn");
			paramMap.put("aprvSttusCdNm", "등록");
			paramMap.put("prcsSeCd", "INS");
			paramMap.put("isApprover", isApprover);
			Map<String, Object> rtnVal = saveStdDicaryConfmInfo(paramMap);
			
			if("OK".equals(rtnVal.get("resultCd"))) {
				rtnMap.put("resultCd", "OK");
			} else {
				rtnMap.put("resultCd", "NO");
			}
			
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}
	
	/**
	 * 표준사전 데이터타입그룹 목록 조회
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdDicaryDatatypeGroupList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			logger.debug("parameter : " + paramMap);

			// 표준사전 데이터타입그룹 목록 조회
			List<SangsMap> list = dao.selectList("meta_code.selectDatatypeGroupList", paramMap);
			rtnMap.put("list", list);

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}

	/**
	 * 표준사전 DBMS별 데이터타입 목록 조회
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdDicaryDbmsDataTypeList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			logger.debug("parameter : " + paramMap);

			// 표준사전 DBMS별 데이터타입 목록 조회
			List<SangsMap> list = dao.selectList("meta_code.selectDbmsDatatypeList", paramMap);
			rtnMap.put("list", list);

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}

	/**
	 * 표준사전 데이터타입 등록/수정 처리
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> saveStdDicaryDatatypeInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);

			String regUserId = AuthUtil.getUserId();

			SangsStringUtil.checkRequiredParam(paramMap, "pmode", "pmode");
			String pmode = String.valueOf(paramMap.get("pmode"));

			// 등록
			if ("R".equals(pmode)) {
				paramMap.put("useYn", "Y");
				paramMap.put("regUserId", regUserId);
				dao.insert("meta_code.insertStdDicaryDbmsDatatypeInfo", paramMap);

			} else if ("M".equals(pmode)) {
				paramMap.put("chgUserId", regUserId);
				dao.update("meta_code.updateStdDicaryDbmsDatatypeInfo", paramMap);
			} else {
				throw new SangsMessageException("잘못된 접근입니다.");
			}
			
			rtnMap.put("resultCd", "OK");
			
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}
	
	/**
	 * 표준사전 용어영문약어명 중복 체크
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getCheckDpcnNm(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);
			
			paramMap.put("prjctSn", AuthUtil.getPrjctSn());
			paramMap.put("stdSetSn", AuthUtil.getStdSetSn());
			
			int totalCount = dao.selectCount("meta_stddicary.selectStdDicaryWordListCnt", paramMap);
			paramMap.put("pageSize", totalCount); // 전체 조회를 위해서
			paramMap.put("offset", 0);
			
			List<SangsMap> list = dao.selectList("meta_stddicary.selectStdDicaryWordList", paramMap);
			
			String cmprWordEngAbrvNm = paramMap.get("cmprWordEngAbrvNm").toString();
			String cmprWordNm = "";
			if(paramMap.containsKey("cmprWordNm")) {
				cmprWordNm = paramMap.get("cmprWordNm").toString();
			}
			boolean bCheckDpcn = false;
			for(SangsMap map : list) {
				
				String wordEngAbrvNm = map.getString("wordEngAbrvNm");
				String wordNm = map.getString("wordNm");
				
				if(!"".equals(cmprWordEngAbrvNm)) {
					if(cmprWordEngAbrvNm.equals(wordEngAbrvNm)) {
						bCheckDpcn = true;
					}
				}
				if(!"".equals(cmprWordNm)) {
					if(cmprWordNm.equals(wordNm)) {
						bCheckDpcn = true;
					}
				}
				
			}

			if(bCheckDpcn) {
				rtnMap.put("resultCd", "NO");
				rtnMap.put("errorInfo", "\"" + cmprWordEngAbrvNm +"\"은 이미 등록되어 있는 용어 입니다.");
			} else {
				rtnMap.put("resultCd", "OK");
			}
			
				
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}
	
	
	/**
	 * 표준사전 단어명 조합
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getCmbntWrdNm(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);

			int getStdSetSn = AuthUtil.getStdSetSn();
			paramMap.put("prjctSn", AuthUtil.getPrjctSn());
			paramMap.put("stdSetSn", getStdSetSn);

			int totalCount = dao.selectCount("meta_stddicary.selectStdDicaryWrdListCnt", paramMap);
			paramMap.put("pageSize", totalCount); // 전체 조회를 위해서
			paramMap.put("offset", 0);

			// 단어 조회
			dao.setLogFlag(false);
			List<SangsMap> list = dao.selectList("meta_stddicary.selectStdDicaryWrdList", paramMap);
			dao.setLogFlag(true);

			// 내셋
			List<SangsMap> mySetList = new ArrayList<SangsMap>();
			// 공통셋
			List<SangsMap> cmSetList = new ArrayList<SangsMap>();

			for (SangsMap map : list) {
				int stdSetSn = map.getInt("stdSetSn");
				String prhibtYn = map.getString("prhibtYn");
				if(!"Y".equals(prhibtYn)) {
					if (getStdSetSn == stdSetSn) {
						mySetList.add(map);
					} else {
						cmSetList.add(map);
					}
				}
			}

			String[] wordEngAbrvNmArr = new String[] {};
			String wordEngAbrvNm = String.valueOf(paramMap.get("multiWordEngAbrvNm"));

			if (!"".equals(wordEngAbrvNm)) {
				if (wordEngAbrvNm.contains("_")) {
					wordEngAbrvNmArr = wordEngAbrvNm.split("_");
				}
			}

			String[] wordNmArr = new String[] {};
			String wordNm = String.valueOf(paramMap.get("multiWordNm"));

			if (!"".equals(wordNm)) {
				if (wordNm.contains("_")) {
					wordNmArr = wordNm.split("_");
				}
			}

			int engAbrvArrLen = wordEngAbrvNmArr.length;
			int wordNmArrLen = wordNmArr.length;
			ArrayList<Integer> wrdArrList = new ArrayList<Integer>();
			ArrayList<Integer> setArrList = new ArrayList<Integer>();

			String wordEngNm = "";
			rtnMap.put("resultCd", "OK");
			int dpcnCnt = 0;
			boolean bExist;
			int existCnt = 0;
			if (engAbrvArrLen > 0) {
				wordNm = "";
				for (String engAbrvNm : wordEngAbrvNmArr) {
					dpcnCnt = 0;
					for (SangsMap map : list) {
						if (engAbrvNm.equals(map.getString("wrdEngAbrvNm"))) {
							dpcnCnt++;
						}
					}
					if (dpcnCnt > 1) {
						rtnMap.put("resultCd", "NO");
						break;
					}
				}
				if (dpcnCnt < 2) {
					Map<String, Object> cmprMyEngAbrvMap = new HashMap<String, Object>();
					for (SangsMap map : mySetList) {
						cmprMyEngAbrvMap.put(map.getString("wrdEngAbrvNm"), map);
					}

					for (String engAbrvNm : wordEngAbrvNmArr) {
						bExist = false;
						existCnt = 0;
						if (cmprMyEngAbrvMap.containsKey(engAbrvNm)) {
							SangsMap cmprMyWrdMap = (SangsMap) cmprMyEngAbrvMap.get(engAbrvNm);
							wrdArrList.add(cmprMyWrdMap.getInt("wrdSn"));
							setArrList.add(cmprMyWrdMap.getInt("stdSetSn"));
							wordEngNm += cmprMyWrdMap.getString("wrdEngNm") + " ";
							wordNm += cmprMyWrdMap.getString("wrdNm") + "_";
							bExist = true;
							existCnt++;
						}
						if (!bExist) {
							for (SangsMap cmSetMap : cmSetList) {
								if (engAbrvNm.equals(cmSetMap.getString("wrdEngAbrvNm"))) {
									if (wordNm.indexOf(cmSetMap.getString("wrdNm")) == -1) {
										wrdArrList.add(cmSetMap.getInt("wrdSn"));
										setArrList.add(cmSetMap.getInt("stdSetSn"));
										wordEngNm += cmSetMap.getString("wrdEngNm") + " ";
										wordNm += cmSetMap.getString("wrdNm") + "_";
										existCnt++;
									}
								}

							}
						}
						if (existCnt == 0) {
							rtnMap.put("resultCd", "NO");
							break;
						}
					}
				}
			} else if (wordNmArrLen > 0) {
				wordEngAbrvNm = "";
				for (String wrdNm : wordNmArr) {
					dpcnCnt = 0;
					for (SangsMap map : list) {
						if (wrdNm.equals(map.getString("wrdNm"))) {
							dpcnCnt++;
						}
					}
					if (dpcnCnt > 1) {
						rtnMap.put("resultCd", "NO");
						break;
					}
				}
				if (dpcnCnt < 2) {
					for (String wrdNm : wordNmArr) {
						bExist = false;
						existCnt = 0;
						for (SangsMap mySetMap : mySetList) {
							if (wrdNm.equals(mySetMap.getString("wrdNm"))) {
								wrdArrList.add(mySetMap.getInt("wrdSn"));
								setArrList.add(mySetMap.getInt("stdSetSn"));
								wordEngNm += mySetMap.getString("wrdEngNm") + " ";
								wordEngAbrvNm += mySetMap.getString("wrdEngAbrvNm") + "_";
								bExist = true;
								existCnt++;
							}
						}
						if (!bExist) {
							for (SangsMap cmSetMap : cmSetList) {
								if (wrdNm.equals(cmSetMap.getString("wrdNm"))) {
									wrdArrList.add(cmSetMap.getInt("wrdSn"));
									setArrList.add(cmSetMap.getInt("stdSetSn"));
									wordEngNm += cmSetMap.getString("wrdEngNm") + " ";
									wordEngAbrvNm += cmSetMap.getString("wrdEngAbrvNm") + "_";
									existCnt++;
								}
							}
						}
						if (existCnt == 0) {
							rtnMap.put("resultCd", "NO");
							break;
						}
					}
				}
			}

			if (rtnMap.get("resultCd") == "OK") {
				rtnMap.put("wrdArrList", wrdArrList);
				rtnMap.put("setArrList", setArrList);
				if (!"".equals(wordEngNm)) {
					rtnMap.put("wordEngNm", wordEngNm.substring(0, wordEngNm.length() - 1));
				}
				if (!"".equals(wordNm)) {
					rtnMap.put("wordNm", wordNm.substring(0, wordNm.length() - 1));
				}
				if (!"".equals(wordEngAbrvNm)) {
					rtnMap.put("wordEngAbrvNm", wordEngAbrvNm.substring(0, wordEngAbrvNm.length() - 1));
				}

			}

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}
	
	/**
	 * 표준사전 용어 다중 등록
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> saveStdDicaryWordMultiInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + paramMap);

			String regUserId = AuthUtil.getUserId();
			String isApprover = AuthUtil.isApprover();
			int stdSetSn = AuthUtil.getStdSetSn();

			String getIsApprover = paramMap.get("isApprover").toString();

			if (!isApprover.equals(getIsApprover)) {
				throw new SangsMessageException("잘못된 접근입니다.");
			}

			SangsStringUtil.checkRequiredParam(paramMap, "pmode", "pmode");
			String pmode = String.valueOf(paramMap.get("pmode"));

			// 등록
			List<Map<String, Object>> wordList =  (List<Map<String, Object>>) paramMap.get("wordMultiInfo");
			boolean bDpcnCheck = false;
			// 중복 체크
			for (int i = 0; i < wordList.size(); i++) {
				for (int j = 0; j < i; j++) {
					
					String iEngAbrvVal = String.valueOf(wordList.get(i).get("wordEngAbrvNm"));
					String jEngAbrvVal = String.valueOf(wordList.get(j).get("wordEngAbrvNm"));
					
					if(iEngAbrvVal.equals(jEngAbrvVal)) {
						rtnMap.put("errorMsg", iEngAbrvVal+" 약어명 중복");
						rtnMap.put("resultCd", "NO");
						bDpcnCheck = true;
					}
				}
			}
			
			if(!bDpcnCheck) {
				rtnMap.put("resultCd", "OK");
				rtnMap.put("errorMsg", "");
				// 승인관리
				if("R".equals(pmode)) {
					for(int i = 0; i < wordList.size(); i++) {
						wordList.get(i).put("stdSetSn", stdSetSn);
						
						int nextWordSn = dao.selectInteger("meta_stddicary.selectNextStdDicaryWordSn", wordList.get(i));
						wordList.get(i).put("wordSn", nextWordSn);
						wordList.get(i).put("delYn", "N");
						wordList.get(i).put("useYn", "Y");
						wordList.get(i).put("regUserId", regUserId);
						
						dao.insert("meta_stddicary.insertStdDicaryWordInfo", wordList.get(i));
						
						String[] wrdSnArr = wordList.get(i).get("wrdSn").toString().split(",");
						String[] wrdRefrnStdSetSnArr = wordList.get(i).get("wrdRefrnStdSetSn").toString().split(",");
						
						for (int j = 0; j < wrdSnArr.length; j++) {
							Map<String, Object> wordWrdInfo = new HashMap<String, Object>();
							wordWrdInfo.put("wordSn", nextWordSn);
							wordWrdInfo.put("stdSetSn", stdSetSn);
							wordWrdInfo.put("wrdSn", wrdSnArr[j]);
							wordWrdInfo.put("wrdSortSn", j + 1);
							wordWrdInfo.put("wrdRefrnStdSetSn", wrdRefrnStdSetSnArr[j]);
							
							// 표준사전 용어단어 맵핑테이블 등록
							dao.insert("meta_stddicary.insertStdDicaryWordWrdInfo", wordWrdInfo);
						}
						Map<String, Object> confmMap = new HashMap<String, Object>();
						confmMap.put("prcsSeCd", "INS");
						confmMap.put("aprvSttusCdNm", "등록");
						confmMap.put("wordSn", nextWordSn);
						confmMap.put("pmode", "A");
						confmMap.put("location", "word");
						confmMap.put("isApprover", isApprover);
						Map<String, Object> rtnVal = saveStdDicaryConfmInfo(confmMap);
						
						if (!"OK".equals(rtnVal.get("resultCd"))) {
							rtnMap.put("resultCd", "NO");
							break;
						}
					}
				}
			}
			

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}

		return rtnMap;
	}
	
	
	public Map<String, Object> getCharacterTypeList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		List<String> characterTypeList = BizUtil.DbmsDataTypeGroup.CHARACTERTYPE.getDataTypeList();
		rtnMap.put("characterTypeList", characterTypeList);
		return rtnMap;
	}
	
	
	public String getWildCardReplace(String str) {
		String rtnStr = str;
		if (rtnStr.contains("*")) {
			rtnStr = rtnStr.replace("*", "%");
		} 
		if (rtnStr.contains("?")) {
			rtnStr = rtnStr.replace("?", "_");
		}
		return rtnStr;
	}
	
}