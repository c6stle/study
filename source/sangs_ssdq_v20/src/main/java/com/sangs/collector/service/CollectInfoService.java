package com.sangs.collector.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.collector.util.SangsCollectorUtil;
import com.sangs.common.base.ServiceBase;
import com.sangs.common.support.CommonDao;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.fwk.support.SangsPropertyUtil;
import com.sangs.lib.support.common.SangsCmmnSuportConstants;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.domain.SangsPagingViewInfo;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsStringUtil;
import com.sangs.lib.support.utils.SangsWebUtil;

/**
 * Description : 메타 통합관리 > 정보관리 관련 Service 상세
 *
 * Modification Information
 * 수정일		수정자			수정내용
 * -------		-----------------------------------
 * 2016.01.25 	송호현			최초작성
 * 2019.11.15   mt1716        globals 추가
 */

@SangsService
public class CollectInfoService extends ServiceBase {

	@Autowired
	private CommonDao dao;

	/**
	 * 메타목록 리스트 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getMetaList(Map<String, Object> paramMap) throws Exception {

		Map<String, Object> rtnMap = new HashMap<String, Object>();

		// 페이지 번호
		int pageNum = SangsStringUtil.nvlInt(paramMap.get("pageNum"));
		int pagingRowSize = SangsCmmnSuportConstants.DEFAULT_LIST_ROW_SIZE;
		try {
			// 카운트 조회
			int totalCnt = dao.selectCount("ct_collect_info.selectMetaListCnt", paramMap);
			// 엑셀일때는 조회된 갯수만큼 다 나오게 하기 위해서
			if("EXCEL".equals(SangsStringUtil.nvl(paramMap.get("listType"))) || "ALL".equals(SangsStringUtil.nvl(paramMap.get("listType")))) {
				//paramMap.put("perPage", String.valueOf(totalCnt));	// 페이지당 게시물 수 set
				pagingRowSize = totalCnt;
			}

			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCnt, pageNum, pagingRowSize);
			paramMap.put("startRow", pagingInfo.getStartRow());
			paramMap.put("endRow", pagingInfo.getEndRow());

			// 리스트 조회
			List<SangsMap> result = dao.selectList("ct_collect_info.selectMetaList", paramMap);

			rtnMap.put("resultList", result);
			rtnMap.put("resultCnt", Integer.toString(totalCnt));
			rtnMap.put("pagingInfo", pagingInfo);
		} catch (SangsMessageException e) {
			logger.info("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		} catch (Exception e) {
			logger.info("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}

		return rtnMap;
	}


	/**
	 * 메타정보 신규등록 및 수정 양식
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @return
	 * @throws AdminException
	 */
	public Map<String, Object> getMngInfoForm(Map<String, Object> paramMap) throws Exception {

		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			String pmode = "";
			String apiCode = SangsStringUtil.nvl(paramMap.get("apiCode"), "");

			if (apiCode.equals("")) {
				pmode = "INS";
			} else {
				pmode = "UPD";
			}

			SangsMap resultMap = null;
			if ("UPD".equals(pmode)) {
				// 수정 일때
				// 정보 조회
				resultMap = dao.selectOne("ct_collect_info.selectMetaMngInfoInfo", paramMap);
			} else {
				// 등록일때
				// Default 코드 생성
				String metaCode = dao.selectOne("ct_collect_info.selectMetaMngApiCodeInfo", paramMap).getString("apiCode");
				metaCode = SangsCollectorUtil.getAddNumText(metaCode);

				paramMap.put("metaCode", metaCode);
				paramMap.put("pmode", "INS");
			}
			rtnMap.put("resultMap", resultMap);
			rtnMap.put("pmode", pmode);
			rtnMap.put("paramMap", paramMap);

		} catch (SangsMessageException e) {
			logger.info("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		} catch (Exception e) {
			logger.info("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}

		return rtnMap;
	}


	/**
	 * 메타 정보 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public SangsMap selectMetaMngInfoInfo(Map<String, Object> params) throws Exception {
		return dao.selectOne("ct_collect_info.selectMetaMngInfoInfo", params);
	}

	/**
	 * 메타 정보 코드 조회 갯수 조회 (중복체크용)
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int selectMetaMngCntByCodeInfo(Map<String, Object> params) throws Exception {
		return dao.selectCount("ct_collect_info.selectMetaMngCntByCodeInfo", params);
	}


	/**
	 * 메타정보(등록시) 메타코드 기본제공
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public String selectMetaMngApiCodeInfo(Map<String, Object> params) throws Exception {
		return dao.selectOne("ct_collect_info.selectMetaMngApiCodeInfo", params).getString("apiCd");
	}

	/**
	 * 메타정보 서비스구분 코드 프로그램명 조회 (ajax)
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectCmmnCodeDetailInfo(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			SangsMap sMap= dao.selectOne("ct_collect_info.selectCmmnCodeDetailInfo", params);
			String cdDc = sMap.getString("cdDc");

			rtnMap.put("cdDc", cdDc);

		} catch (SangsMessageException e) {
			logger.info("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		} catch (Exception e) {
			logger.info("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		return rtnMap;
	}

	/**
	 * 메타정보 저장 (등록, 수정)
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> saveMetaMngInfoInfo(Map<String, Object> paramMap) throws Exception {

		Map<String, Object> rtnMap = new HashMap<String, Object>();

		paramMap.put("apiNm", SangsWebUtil.clearXSSMinimum((String) paramMap.get("apiNm")));

		try {
			if ("UPD".equals(paramMap.get("pmode"))) {
				// 수정 일때
				dao.update("ct_collect_info.updateMetaMngInfoInfo", paramMap);
			} else if ("INS".equals(paramMap.get("pmode"))) {
				// 저장소 생성
				createMetaMngDataTable(paramMap);
				// 등록일때
				dao.insert("ct_collect_info.insertMetaMngInfoInfo", paramMap);
			}


			//rtnMap.put("type", "PARENT");
			//rtnMap.put("url", "/collector/collect/collect_info_form.do?apiCd="+paramMap.get("apiCd")+"&tIdx="+paramMap.get("tIdx"));
			rtnMap.put("resultCd", "OK");

		} catch (Exception e) {
			logger.error(this.getClass().getName(), e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}

		return rtnMap;
	}

	/**
	 * 메타 > 수집설정 정보 수정
	 * @param params
	 * @throws Exception
	 */
	public Map<String, Object> saveMetaMngCollectInfo(Map<String, Object> paramMap) throws Exception {

		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			dao.update("ct_collect_info.updateMetaMngCollectInfo", paramMap);

			rtnMap.put("resultCd", "OK");
		} catch (SangsMessageException e) {
			logger.info("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		} catch (Exception e) {
			logger.info("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}

		return rtnMap;
	}

	/**
	 * 메타 > 메타별 저장소 테이블 생성
	 * @param params
	 * @throws Exception
	 */
	public void createMetaMngDataTable(Map<String, Object> params) throws Exception {

		String apiStrgTblNm = "CT_DATA_"+params.get("apiCd");	// 저장소명생성
		String serviceGu = (String) params.get("cmmnApiSrvcTyCd");	//서비스 구분값 세팅
		params.put("dataSchemaId", SangsPropertyUtil.getProperty("Globals.DataDBSchemaId"));
		params.put("apiStrgTblNm", apiStrgTblNm);
		params.put("apiStrgComment", "DS_"+params.get("apiNm"));

		logger.info("params = "+params);


		int apiStorgeCnt = selectMetaMngDataTableCnt(params);
		if (apiStorgeCnt == 0) {

			if(serviceGu.equalsIgnoreCase("SS05")) {	//구분값이 파일일때 테이블 생성 쿼리 변경
//				metaMngInfoDao.update("ct_collect_info.createMetaMngFileDataTable", params); // 기존 쿼리가 없음
			}else {
				dao.update("ct_collect_info.createMetaMngDataTable", params);
			}
		}
	}

	/**
	 * 메타 > 메타별 저장소 테이블 존재 여부 확인
	 * @param params
	 * @throws Exception
	 */
	public int selectMetaMngDataTableCnt(Map<String, Object> params) throws Exception {
		return dao.selectCount("ct_collect_info.selectMetaMngDataTableCnt", params);
	}

	/**
	 * 메타 > 공통코드 조회 (컬럼추가)
	 * @param params
	 * @throws Exception
	 */
	public List<SangsMap> selectCmmnCodeListForCollector(Map<String, Object> params) throws Exception {
		return dao.selectList("ct_collect_info.selectCommonCodeListForCollector", params);
	}

	/**
	 * 메타 코드 중복 체크
	 * @param paramMap
	 * @param req
	 * @param res
	 * @param model
	 * @return
	 */
	public Map<String, Object> checkDuplApiCode(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		String resultCd = "";
		try {
			int cnt = selectMetaMngCntByCodeInfo(paramMap);
			if (cnt == 0)
				resultCd = "OK";
			else
				resultCd = "DUPL";

			rtnMap.put("resultCd", resultCd);
		} catch (Exception e) {
			logger.error(this.getClass().getName(), e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}

		return rtnMap;
	}


}