package com.sangs.collector.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sangs.collector.service.CollectInfoService;
import com.sangs.collector.util.SangsCollectorUtil;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsStringUtil;

/**
 * Description : 메타 통합관리 > 정보관리 관련 컨트롤러
 *
 * Modification Information
 * 수정일			수정자			수정내용
 * -------		-----------------------------------
 * 2016.01.25 	송호현			최초작성
 *
 */
@Controller
public class CollectInfoController {

	@Autowired
	private CollectInfoService collectInfoService;

	/**
	 * 데이터 수집정보 관리 페이지
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @return
	 * @throws AdminException
	 */
	@RequestMapping(value = "/collector/collect/collect_info_list")
	public String metaList(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {
		try {
			// 상세페이지 타입
			params.put("DETAIL_TYPE", "INFO");

			// 리스트 조회
			Map<String, Object> metaListMap = collectInfoService.getMetaList(params);

			model.addAttribute("resultList", metaListMap.get("resultList"));
			model.addAttribute("resultCnt", metaListMap.get("resultCnt"));
			model.addAttribute("pagingInfo", metaListMap.get("pagingInfo"));

			model.addAttribute("params", params);

		} catch (Exception e) {
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}

		return "collector/collect/collect_info_list";
	}

	/**
	 * 수집 데이터 목록 페이지
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @return
	 * @throws AdminException
	 */
	@RequestMapping(value = "/collector/collect/collect_info_data_list")
	public String collectorDataList(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {
		try {
			// 상세페이지 타입
			params.put("DETAIL_TYPE", "DATA");

			// 리스트 조회
			Map<String, Object> metaListMap = collectInfoService.getMetaList(params);

			model.addAttribute("resultList", metaListMap.get("resultList"));
			model.addAttribute("resultCnt", metaListMap.get("resultCnt"));
			model.addAttribute("pagingInfo", metaListMap.get("pagingInfo"));

			model.addAttribute("params", params);

		} catch (Exception e) {
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}

		return "collector/collect/collect_info_list";
	}

	/**
	 * 메타정보 신규등록 및 수정 양식
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/collector/collect/collect_info_form")
	public String metaMngInfoForm(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		try {

			String pmode = "";
			String apiCd = SangsStringUtil.nvl(params.get("apiCd"), "");
			String tIdx = SangsStringUtil.nvl(params.get("tIdx"), "");

			if (params.get("tIdx") == null) {
				params.put("tIdx" , tIdx);
			}

			if (apiCd.equals("")) {
				pmode = "INS";
				params.put("apiCd", apiCd);
			} else {
				pmode = "UPD";
			}

			SangsMap resultMap = null;
			if ("UPD".equals(pmode)) {
				// 수정 일때
				// 정보 조회
				resultMap = collectInfoService.selectMetaMngInfoInfo(params);
			} else {
				// 등록일때
				// Default 코드 생성
				String metaCd = collectInfoService.selectMetaMngApiCodeInfo(params);
				metaCd = SangsCollectorUtil.getAddNumText(metaCd);

				params.put("metaCd", metaCd);
				params.put("pmode", "INS");
			}
			model.addAttribute("resultMap", resultMap);
			model.addAttribute("pmode", pmode);
			model.addAttribute("params", params);

		} catch (Exception e) {
			throw new SangsMessageException(e);
		}

		//System.out.println("---------------------------------------------");

		return "collector/collect/collect_info_form";
	}

	/**
	 * 수집설정 정보 수정 양식
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/collector/collect/collect_setting_form")
	public String metaMngCollectForm(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {

		try {

			//공통코드>수집구분
			params.put("cdId", "APICOLCTTY");
			List<SangsMap> colctTyList= collectInfoService.selectCmmnCodeListForCollector(params);

			//메타정보 조회
			SangsMap resultMap = collectInfoService.selectMetaMngInfoInfo(params);

			model.addAttribute("resultMap", resultMap);
			model.addAttribute("colctTyList", colctTyList);
			model.addAttribute("params", params);

		} catch (Exception e) {
			throw new Exception(e);
		}

		return "collector/collect/collect_setting_form";
	}

}