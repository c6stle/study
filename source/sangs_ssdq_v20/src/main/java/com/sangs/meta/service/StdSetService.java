package com.sangs.meta.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.support.AuthUtil;
import com.sangs.common.service.CommonCodeService;
import com.sangs.common.support.CommonDao;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.fwk.common.SangsConstants;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.domain.SangsPagingViewInfo;
import com.sangs.lib.support.exception.SangsCallServiceException;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsSimpleExcelMaker;
import com.sangs.lib.support.utils.SangsStringUtil;
import com.sangs.lib.support.utils.SangsWebUtil;

/**
 * 표준세트 관련 Service
 * 
 * 
 * @author sw.lee
 *
 */

@SangsService
public class StdSetService extends ServiceBase {

	@Autowired
	private CommonDao dao;

	@Autowired
	private CommonCodeService commonCodeService;

	/**
	 * 프로젝트 표준세트 목록 조회
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */

	public Map<String, Object> getPrjctStdSetList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		logger.debug("parameter : " + paramMap);

		try {

			List<SangsMap> list = dao.selectList("meta_stdset.selectPrjctStdSetList", paramMap);

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
	 * 표준세트 정보 등록
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> regStdSetInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		logger.debug("parameter : " + paramMap);

		// 표준세트 조회 목록 조회

		try {

			logger.debug("parameter : " + paramMap); // 표준세트 순번 채번 int nextStdSetSn =
			int nextStdSetSn = dao.selectCount("meta_stdset.selectNextStdSetSn", paramMap);

			paramMap.put("stdSetSn", nextStdSetSn);
			paramMap.put("useYn", "Y");
			paramMap.put("deleteYn", "N");
			dao.insert("meta_stdset.insertStdSetInfo", paramMap);

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
	 * 표준세트 리스트 조회
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getDataStdSetList(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			/* int prjctSn = AuthUtil.getPrjctSn(); */
			logger.debug("parameter : " + paramMap);
			// 표준세트 목록 조회
			
			int pageNum = SangsStringUtil.nvlInt(paramMap.get("pageNum"), 1);

			// 전체 row 수 조회
			int totalCount = dao.selectCount("meta_stdset.selectDataStdSetListCount", paramMap);

			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum,
					SangsConstants.DEFAULT_LIST_ROW_SIZE);

			paramMap.put("pageSize", pagingInfo.getPageSize());
			paramMap.put("offset", pagingInfo.getOffset());
			List<SangsMap> stdSetList = dao.selectList("meta_stdset.selectDataStdSetList", paramMap);

			rtnMap.put("list", stdSetList);
			rtnMap.put("totalCount", totalCount);
			rtnMap.put("pagingInfo", pagingInfo);

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
	 * 표준세트 신규등록 및 정보수정
	 * 
	 * @param paramMap
	 * @return
	 * @throws SangsCallServiceException
	 */
	public Map<String, Object> saveDataStdSetInfo(Map<String, Object> paramMap) throws SangsCallServiceException {

		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {

			paramMap.put("regUserId", AuthUtil.getUserId());
			paramMap.put("prjctSn", AuthUtil.getPrjctSn());

			String pmode = String.valueOf(paramMap.get("pmode"));
			String stdSetNm = SangsWebUtil.clearXSSMinimum((String) paramMap.get("stdSetNm"));
			String stdSetCn = SangsWebUtil.clearXSSMinimum((String) paramMap.get("stdSetCn"));
			List<SangsMap> stdSetNmList = dao.selectList("meta_stdset.getStdSetNm", paramMap);

			if ("R".equals(pmode)) {
				for (Map<String, Object> nameMap : stdSetNmList) {
					if (nameMap.containsValue(paramMap.get("stdSetNm"))) {
						throw new SangsMessageException("동일한 표준세트명이 존재합니다.");
					}
				}

				if (AuthUtil.isApprover().equals("Y")) {
					paramMap.put("aprvSttusCd", "APPROVAL");
				} else {
					throw new SangsMessageException("권한이 없습니다.");
				}
				int nextStdSetSn = dao.selectInteger("meta_stdset.selectNextStdSetSn", paramMap);
				paramMap.put("stdSetSn", nextStdSetSn);
				paramMap.put("stdSetNm", stdSetNm);
				paramMap.put("stdSetCn", stdSetCn);
				paramMap.put("stdSetTyCd", "NONE");
				paramMap.put("useYn", "Y");
				paramMap.put("delYn", "N");
				dao.insert("meta_stdset.insertStdSetInfo", paramMap);
				//dao.insert("meta_stdset.insertPrjctStdSetInfo", paramMap);

			} else if ("M".equals(pmode)) {
				if (String.valueOf(paramMap.get("stdSetSn")).equals("1")) {
					throw new SangsMessageException("공통표준세트는 수정할 수 없습니다.");
				}
				paramMap.put("stdSetNm", stdSetNm);
				paramMap.put("stdSetCn", stdSetCn);
				dao.update("meta_stdset.updateStdSetInfo", paramMap);
			}

			rtnMap.put("resultCd", "OK");

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
	 * 표준세트 삭제
	 * 
	 * @param paramMap
	 * @return
	 * @throws SangsCallServiceException
	 */
	public Map<String, Object> deleteDataStdSetInfo(Map<String, Object> paramMap) throws SangsCallServiceException {

		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			if (String.valueOf(paramMap.get("stdSetSn")).equals("1")) {
				throw new SangsMessageException("공통표준세트는 삭제할 수 없습니다.");
			}
			dao.update("meta_stdset.deletestdSetInfo", paramMap);
			rtnMap.put("resultCd", "OK");

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
	 * 불러오기 모달
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> loadDataStdSetInfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			logger.debug("parameter : " + paramMap);
			
			String type = paramMap.get("type").toString();

			dao.setLogFlag(false);
			if (type.equals("wrd")) {
				// 단어 목록 조회
				List<SangsMap> wrdList = dao.selectList("meta_stdset.selectLoadWrdList", paramMap);
				
				// 리스트 안에 있는 코드에 대한 코드명 setting
				commonCodeService.setCmmnCodeNmForList(wrdList
						, new String[] { "WRDCDTY" }
						, new String[] { "wrdTyCd" }
						, new String[] { "wrdTyCdNm"});
				
				
				for(SangsMap map : wrdList) {
					String tempCn = map.getString("wrdCn");
					if(tempCn.getBytes().length > 80) {
						map.putOrg("wrdCn", SangsStringUtil.substringByte(tempCn, 80) + "...");
					}
				}
				rtnMap.put("list", wrdList);
			} else if (type.equals("domn")) {
				// 도메인 목록 조회
				List<SangsMap> domnList = dao.selectList("meta_stdset.selectLoadDomnList", paramMap);

				for(SangsMap map : domnList) {
					String tempCn = map.getString("domnCn");
					if(tempCn.getBytes().length > 80) {
						map.putOrg("domnCn", SangsStringUtil.substringByte(tempCn, 80) + "...");
					}
				}
				rtnMap.put("list", domnList);
			} else if (type.equals("word")) {
				// 용어 목록 조회
				List<SangsMap> wordList = dao.selectList("meta_stdset.selectLoadWordList", paramMap);
				
				// 리스트 안에 있는 코드에 대한 코드명 setting
				commonCodeService.setCmmnCodeNmForList(wordList
						, new String[] { "WRDCDTY" }
						, new String[] { "wordTyCd" }
						, new String[] { "wordTyCdNm"});
				
				for(SangsMap map : wordList) {
					String tempCn = map.getString("wordCn");
					if(tempCn.getBytes().length > 80) {
						map.putOrg("wordCn", SangsStringUtil.substringByte(tempCn, 80) + "...");
					}
				}
				
				rtnMap.put("list", wordList);
			} else {
				throw new SangsMessageException("테이블을 불러올 수 없습니다.");
			}
			
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		} finally {
			dao.setLogFlag(true);
		}
		return rtnMap;
	}

	/**
	 * 단어,도메인,용어 불러오기 후 엑셀파일 다운로드
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */

	public Workbook excelImportStdSetData(Map<String, Object> paramMap) throws Exception {
		Workbook workbook = null;
		try {
			
			String type = String.valueOf(paramMap.get("type"));

			dao.setLogFlag(false);
			if (type.equals("wrd")) {
				try {

					List<SangsMap> wrdList = dao.selectList("meta_stdset.selectLoadWrdList", paramMap);
					
					// 리스트 안에 있는 코드에 대한 코드명 setting
					commonCodeService.setCmmnCodeNmForList(wrdList
							, new String[] { "WRDCDTY" }
							, new String[] { "wrdTyCd" }
							, new String[] { "wrdTyCdNm"});
					
					SangsSimpleExcelMaker em = new SangsSimpleExcelMaker();
					workbook = em.createSheet()
							.setHeaderColNm(new String[] { "단어구분", "단어명", "영문약어명", "영문명", "표준어", "연관어", "금칙어", "설명" })
							.setHeaderColId(new String[] { "wrdTyCdNm", "wrdNm", "wrdEngAbrvNm", "wrdEngNm", "stdWrdNm", "relWrdNm", "prhibtYn", "wrdCn" })
							.setList(wrdList).setAutoSize().getWorkbook();

				} catch (SangsMessageException e) {
					logger.error("", e);
					throw e;
				} catch (Exception e) {
					logger.error("", e);
					throw new SangsMessageException("엑셀 생성중 에러가 발생하였습니다." + e.getMessage());
				}

			} else if (type.equals("domn")) {
				try {

					List<SangsMap> domnList = dao.selectList("meta_stdset.selectLoadDomnList", paramMap);

					SangsSimpleExcelMaker em = new SangsSimpleExcelMaker();
					workbook = em.createSheet()
							.setHeaderColNm(new String[] { "도메인그룹", "도메인분류명", "데이터타입", "데이터길이", "설명" })
							.setHeaderColId(
									new String[] { "domnGroupNm", "domnClNm", "dataTyCd", "dataLtValue", "domnCn" })
							.setList(domnList).setAutoSize().getWorkbook();

				} catch (SangsMessageException e) {
					logger.error("", e);
					throw e;
				} catch (Exception e) {
					logger.error("", e);
					throw new SangsMessageException("엑셀 생성중 에러가 발생하였습니다." + e.getMessage());
				}
			}

			else if (type.equals("word")) {

				try {

					List<SangsMap> wordList = dao.selectList("meta_stdset.selectLoadWordList", paramMap);

					// 리스트 안에 있는 코드에 대한 코드명 setting
					commonCodeService.setCmmnCodeNmForList(wordList
							, new String[] { "WRDCDTY" }
							, new String[] { "wordTyCd" }
							, new String[] { "wordTyCdNm"});
					
					SangsSimpleExcelMaker em = new SangsSimpleExcelMaker();
					workbook = em.createSheet()
							.setHeaderColNm(new String[] { "용어구분", "용어명", "영문약어명", "영문명", "도메인명", "금칙어"})
							.setHeaderColId(new String[] { "wordTyCdNm", "wordNm", "wordEngAbrvNm", "wordEngNm", "domnNm", "prhibtYn"})
							.setList(wordList).setAutoSize().getWorkbook();

				} catch (SangsMessageException e) {
					logger.error("", e);
					throw e;
				} catch (Exception e) {
					logger.error("", e);
					throw new SangsMessageException("엑셀 생성중 에러가 발생하였습니다." + e.getMessage());
				}
			} else {
				throw new SangsMessageException("불러올 수 없습니다.");
			}
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		} finally {
			dao.setLogFlag(true);
		}
		return workbook;
	}

}
