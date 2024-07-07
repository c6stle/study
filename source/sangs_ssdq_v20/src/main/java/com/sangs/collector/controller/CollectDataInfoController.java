package com.sangs.collector.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sangs.collector.service.CollectDataInfoService;
import com.sangs.collector.service.CollectInfoService;
import com.sangs.collector.util.SangsCollectorUtil;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.utils.SangsStringUtil;

/**
 * Description : 메타 통합관리 > 수집데이터 관련 컨트롤러
 *
 * Modification Information
 * 수정일			수정자			수정내용
 * -------		-----------------------------------
 * 2016.01.29 		조남훈			최초작성
 *
 */
@Controller
public class CollectDataInfoController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CollectInfoService collectInfoService;

	@Autowired
	private CollectDataInfoService collectDataInfoService;

	/**
	 * 수집 데이터 조회
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/collector/collect/collect_data_list")
	public String metaMngCollectDataList(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {
		try {

			// 검색어 언더바 처리
			String paramsColValue = SangsStringUtil.nvl(params.get("colValue"));
			params.put("colValue", paramsColValue.replaceAll("[_]", "\\\\_"));
			//조회할 수집데이터 테이블명 입력
			params.put("apiStrgTblNm", "CT_DATA_"+params.get("apiCd"));

			// db 기간조회용 날짜 포맷 별도 저장(yyyymmdd)
			params.put("logBeginYmd", SangsStringUtil.nvl(params.get("logBeginDe")).replaceAll("-",""));
			params.put("logEndYmd", SangsStringUtil.nvl(params.get("logEndDe")).replaceAll("-",""));

			logger.info("AdminMetaMngController / params : " + params);

			params.put("cdId", "COL");

			SangsMap resultMap = collectInfoService.selectMetaMngInfoInfo(params);

			// 전문정보(목록) 조회
			int outputCnt = resultMap.getInt("rcptnCnt");

			List<SangsMap> outputList = null;
			if (outputCnt > 0) {	// 전문정보가 있을 경우 해당 목록 조회
				outputList = collectDataInfoService.selectMetaMngCollectDataOutputList(params);
			} else  { // 전문정보가 없을 경우 default 목록 조회
				outputList = collectDataInfoService.selectMetaMngCollectDataDefaultOutputList(params);
			}

			logger.info("AdminMetaMngController / outputList : " + outputList);

			// 수집데이터 조회
			Map<String, Object> dataListMap = collectDataInfoService.selectMetaMngCollectDataList(params, outputList);

			// 검색어 언더바 후처리
			params.put("colValue", paramsColValue);

			model.addAttribute("resultList", dataListMap.get("resultList"));
			model.addAttribute("resultCnt", dataListMap.get("resultCnt"));
			model.addAttribute("pagingInfo", dataListMap.get("pagingInfo"));
			model.addAttribute("outputList", outputList);
			model.addAttribute("params", params);
		} catch (Exception e) {
			throw new Exception(e);
		}
		return "collector/collect/collect_data_list";
	}

	/**
	 * 수집 데이터 조회수정삭제 팝업
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/mngr/metaMng/edit_data_pop.do")
	public String editDataPop(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {

		String pmode = SangsStringUtil.nvl(params.get("pmode"), "");

		try {
			SangsMap resultMap = collectInfoService.selectMetaMngInfoInfo(params);

			// 전문정보(목록) 조회
			int outputCnt = resultMap.getInt("outputCnt");

			List<SangsMap> outputList = null;
			if (outputCnt > 0) {	// 전문정보가 있을 경우 해당 목록 조회
				outputList = collectDataInfoService.selectMetaMngCollectDataOutputList(params);
			} else  { // 전문정보가 없을 경우 default 목록 조회
				outputList = collectDataInfoService.selectMetaMngCollectDataDefaultOutputList(params);
			}

			String apiStorgeId = "CT_DATA_"+params.get("apiCd");	// 저장소명
			params.put("apiStrgTblNm", apiStorgeId);

			//SangsMap map = metaMngCollectDataService.selectMetaMngCollectDataInfo(params, outputList);

			if(pmode.equals("UPD")) {
				//컬럼 list 정의
				List<String> colList = new ArrayList<String>();
				List<String> dataList = new ArrayList<String>();

				Map<String, Object> rowDataMap = new HashMap<String, Object>();
				rowDataMap.put("apiCd", params.get("apiCd"));
				rowDataMap.put("logYmd", params.get("logYmd"));
				rowDataMap.put("logSn", params.get("logSn"));
				rowDataMap.put("dataSn", params.get("dataSn"));
				rowDataMap.put("apiStrgTblNm", params.get("apiStrgTblNm"));

				for (int c=0; c<outputCnt; c++) {
					try {
						colList.add("D" + SangsCollectorUtil.lpad(String.valueOf(c+1), 2, '0'));
						dataList.add(SangsStringUtil.nvl(params.get("d" + SangsCollectorUtil.lpad(String.valueOf(c+1), 2, '0')), ""));
					} catch (Exception ex){
						dataList.add("");
					}
				}
				try {
					rowDataMap.put("colList", colList);
					rowDataMap.put("dataList", dataList);

					collectDataInfoService.updateMetaMngCollectDataInfo(rowDataMap); // 수집데이터 수정
				} catch (Exception ex){}
			}else if(pmode.equals("DEL")) {
				try {
					collectDataInfoService.deleteMetaMngCollectDataInfo(params); // 수집데이터 삭제
				} catch (Exception ex){}
			}

			SangsMap map = collectDataInfoService.selectMetaMngCollectDataInfo(params, outputList);

			model.addAttribute("outputCnt", outputCnt);
			model.addAttribute("outputList", outputList);
			model.addAttribute("outputDataInfo", map.get("outputDataInfo"));
			model.addAttribute("params", params);

		} catch (Exception e) {
			throw new Exception(e);
		}

		return "mngr/metaMng/edit_data_pop";
	}




}