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

import com.sangs.collector.service.CollectDataInfoService;
import com.sangs.collector.service.CollectLogInfoService;
import com.sangs.collector.service.CollectInfoService;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.utils.SangsStringUtil;

/**
 * Description : 메타 통합관리 > 수집로그 관련 컨트롤러
 *
 * Modification Information
 * 수정일			수정자			수정내용
 * -------		-----------------------------------
 * 2016.01.29 	조남훈			최초작성
 *
 */
@Controller
public class CollectLogInfoController {

	@Autowired
	private CollectInfoService collectInfoService;

	@Autowired
	private CollectLogInfoService collectLogInfoService;

	@Autowired
	private CollectDataInfoService collectDataInfoService;

	/**
	 * 수집로그 목록 조회
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/collector/collect/collect_log_list")
	public String metaMngCollectLogList(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {

		try {
			// 리스트 조회
			params.put("logBeginYmd", params.get("logBeginYmd"));
			params.put("logEndYmd", params.get("logEndYmd"));

			// 검색어 언더바 전처리
			params.put("apiCd", SangsStringUtil.nvl(params.get("apiCd")).replaceAll("[_]", "\\\\_"));
			params.put("rspnsResultCd", SangsStringUtil.nvl(params.get("rspnsResultCd")).replaceAll("[_]", "\\\\_"));

			Map<String, Object> map = collectLogInfoService.selectMetaMngCollectLogList(params);
			//int totCnt = Integer.parseInt((String) map.get("resultCnt"));

			// 페이징관련
			//PaginationInfo paginationInfo = SangsUtil.getPagingInfo(params, totCnt);

			// 검색어 언더바 후처리
			params.put("apiCd", SangsStringUtil.nvl(params.get("apiCd")).replaceAll("\\\\_", "_"));
			params.put("rspnsResultCd", SangsStringUtil.nvl(params.get("rspnsResultCd")).replaceAll("\\\\_", "_"));

			model.addAttribute("resultList", map.get("resultList"));
			model.addAttribute("resultCnt", map.get("resultCnt"));
			model.addAttribute("pagingInfo", map.get("pagingInfo"));
			model.addAttribute("params", params);

		} catch (Exception e) {
			throw new Exception(e);
		}
		return "collector/collect/collect_log_list";
	}



	/**
	 * 수집로그 기준 데이터 조회
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/collector/collect/collect_log_data_list")
	public String metaMngCollectLogDataList(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {

		try {

			// 검색어 언더바 처리
			String paramsColValue = SangsStringUtil.nvl(params.get("colValue"));
			params.put("colValue", paramsColValue.replaceAll("[_]", "\\\\_"));

			// 수집 로그 상세 정보
			SangsMap resultMap = collectLogInfoService.selectMetaMngCollectLogInfo(params);

			// 전문정보(목록) 조회
			int outputCnt = resultMap.getInt("rcptnCnt");

			params.put("cdId", "COL");

			List<SangsMap> outputList = null;
			if (outputCnt > 0) {	// 전문정보가 있을 경우 해당 목록 조회
				outputList = collectDataInfoService.selectMetaMngCollectDataOutputList(params);

			} else  { // 전문정보가 없을 경우 default 목록 조회
				outputList = collectDataInfoService.selectMetaMngCollectDataDefaultOutputList(params);

			}

			// 수집 데이터 상세 리스트 조회
			Map<String, Object> logListDetailMap = collectDataInfoService.selectMetaMngCollectDataList(params, outputList);

			// 검색어 언더바 후처리
			params.put("colValue", paramsColValue);

			model.addAttribute("resultMap", resultMap);
			model.addAttribute("outputList", outputList);
			model.addAttribute("resultList", logListDetailMap.get("resultList"));
			model.addAttribute("resultCnt", logListDetailMap.get("resultCnt"));
			model.addAttribute("pagingInfo", logListDetailMap.get("pagingInfo"));
			model.addAttribute("params", params);

		} catch (Exception e) {
			throw new Exception(e);
		}
		return "collector/collect/collect_log_data_list";
	}


	/**
	 * 수집 데이터 조회 Grid
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @return
	 * @throws Exception
	 */
//	@RequestMapping(value = "/collector/collect/collect_log_data_listAjax.do")
//	public void metaMngCollectLogDataListAjax(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {
//
//		res.setContentType("text/html; charset=utf-8");
//		PrintWriter out = null;
//		Gson gson = new Gson();
//		String json = null;
//
//		try {
//
//			out = res.getWriter();
//
//			//조회할 수집데이터 테이블명 입력
//			params.put("apiStorgeId", "SC_DATA_"+params.get("apiCode"));
//
//			// db 기간조회용 날짜 포맷 별도 저장(yyyymmdd)
//			params.put("logBeginYmd", SangsUtil.replaceAll(params.get("logBeginDe"),"-",""));
//			params.put("logEndYmd", SangsUtil.replaceAll(params.get("logEndDe"),"-",""));
//
//			SangsMap resultMap = metaMngInfoService.selectMetaMngInfoInfo(params);
//
//			// 전문정보(목록) 조회
//			int outputCnt = resultMap.getInt("outputCnt");
//
//			List<SangsMap> outputList = null;
//			if (outputCnt > 0) {	// 전문정보가 있을 경우 해당 목록 조회
//				outputList = metaMngCollectDataService.selectMetaMngCollectDataOutputList(params);
//
//			} else  { // 전문정보가 없을 경우 default 목록 조회
//				outputList = metaMngCollectDataService.selectMetaMngCollectDataDefaultOutputList(params);
//
//			}
//
//			// 리스트 조회
//			Map<String, Object> map = metaMngCollectDataService.selectMetaMngCollectDataList(params, outputList);
//
//			// 페이징관련
//			PaginationInfo paginationInfo = SangsUtil.getPagingInfo(params, Integer.parseInt((String) map.get("resultCnt")));
//
//			ObjectMapper mapper = new ObjectMapper();
//			Map<String, Object> modelMap = new HashMap<String, Object>();
//			//modelMap.put("total", paginationInfo.getTotalPageCount());
//			modelMap.put("total", Math.floor(paginationInfo.getTotalRecordCount()/ Integer.parseInt(params.get("rows")))+1);
//			modelMap.put("records",paginationInfo.getTotalRecordCount());
//			modelMap.put("rows", map.get("resultList"));
//			//modelMap.put("page", paginationInfo.getCurrentPageNo());
//			modelMap.put("page", Integer.parseInt(params.get("page")));
//
//			json = mapper.writeValueAsString(modelMap);
//
//			/*model.addAttribute("resultList", map.get("resultList"));
//			model.addAttribute("resultCnt", map.get("resultCnt"));
//			model.addAttribute("paginationInfo", paginationInfo);
//			model.addAttribute("params", params);*/
//
//
//		} catch (Exception e) {
//			json = gson.toJson("ERROR");
//			throw new Exception(e);
//		}
//
//		out.print(json);
//		out.flush();
//		out.close();
//	}


	/**
	 * 수집로그 삭제 처리
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/mngr/metaMng/metaMngCollectLogDelete.do")
	public String metaMngCollectLogDelete(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {

		try {

			// 메타정보조회
			SangsMap resultMap = collectInfoService.selectMetaMngInfoInfo(params);

			// 데이터저장소 등록
			params.put("apiStrgTblNm", resultMap.getString("apiStrgTblNm"));

			collectDataInfoService.deleteMetaMngCollectLogDataInfo(params);
			collectLogInfoService.deleteMetaMngCollectLogInfo(params);
		} catch (Exception e) {
			throw new Exception(e);
		}

		model.addAttribute("type", "PARENT");
		model.addAttribute("msg", "");
		model.addAttribute("url", "/collector/collect/collect_log_list.do?apiCd="+params.get("apiCd"));
		return "forward:/common/msgForward.do";
	}


}