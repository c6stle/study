package com.sangs.collector.controller;

import java.io.File;
//import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sangs.collector.service.CollectRcptnInfoService;
import com.sangs.collector.util.CollectorFileUploadUtil;
import com.sangs.collector.util.CollectorFormBasedFileVo;
import com.sangs.collector.util.SangsExcelDownUtil;
import com.sangs.fwk.support.SangsPropertyUtil;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsStringUtil;
import com.sangs.lib.support.utils.SangsWebUtil;

import jxl.Sheet;
import jxl.Workbook;

/**
 * Description : 메타 통합관리 > 메타전문정보 관련 컨트롤러
 *
 * Modification Information
 * 수정일			수정자			수정내용
 * -------		-----------------------------------
 * 2016.01.29 	조남훈			최초작성
 * 2019.11.16   mt1716			alter table 기능 추가 (입력 필드 작성 후 테이블에 칼럼 추가로 방식을 변경)
 */
@Controller
public class CollectorRcptnInfoController {

	@Autowired
	private CollectRcptnInfoService collectRcptnInfoService;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 메타전문정보 조회 및 등록 양식
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/collector/collect/collect_rcptn_list")
	public String metaMngOutputList(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {

		try {

			if ("EXCEL".equals(SangsStringUtil.nvl(params.get("listType")))) { // 엑셀 다운로드인 경우

				//엑셀다운 TH 칼럼 리스트
				List<SangsMap> tempList = collectRcptnInfoService.selectMetaMngOutputExcelList(params);
				model.addAttribute("tempList", tempList);
				//Map tempMap = metaMngOutputService.selectMetaMngOutputExcelList(params);

			} else {
				//전문정보 리스트
				List<SangsMap> resultList = collectRcptnInfoService.selectMetaMngOutputList(params);

				//공통코드>데이터구분
//				List<SangsMap> dataTyList = commonService.selectCmmnCodeDetailListForCombo("CC_OUTPT_DATA_TY_CODE");

//				model.addAttribute("dataTyList", dataTyList);
				model.addAttribute("resultList", resultList);
				model.addAttribute("params", params);
			}

		} catch (Exception e) {
			logger.info("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}

//		if ("EXCEL".equals(SangsStringUtil.nvl(params.get("listType")))) {
//			// 엑셀 다운로드인 경우
//			return "mngr/metaMng/excel/metamng_output_list_excel";
//		} else {
			// 일반 리스트 일경우
			return "collector/collect/collect_rcptn_list";
//		}
	}

	/**
	 * 메타전문정보 조회 및 등록 양식
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/mngr/metaMng/metaMngOutputExcleList")
	public void metaMngOutputExcleList(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {

		try {

			HashMap<String, Object> map = new HashMap<String, Object>();

			List<SangsMap> titleList = collectRcptnInfoService.selectMetaMngOutputExcelList(params);

			map.put("resultList", titleList);

			SangsExcelDownUtil.downExcleFile(map, req, res);

		} catch (Exception e) {
			logger.info("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}

	}

	/**
	 * 메타전문정보 엑셀일괄 등록 처리
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/mngr/metaMng/metaMngOutputImportExec")
	public String metaMngOutputImportExec(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {

		String msg = "";

		try {

			String attFileMetaMngOutputPath = SangsPropertyUtil.getProperty("Globals.attFileBasePath") + SangsPropertyUtil.getProperty("Globals.fileIdntfrCode_meta");

			List<CollectorFormBasedFileVo> fileList = null;
			if (ServletFileUpload.isMultipartContent(req)) {
				fileList = CollectorFileUploadUtil.uploadFiles(req, attFileMetaMngOutputPath);
			}

			if (fileList != null && fileList.size() > 0) {

				CollectorFormBasedFileVo vo = fileList.get(0);
				String attFileMetaMngOutputFullPath = attFileMetaMngOutputPath + "/" + vo.getPhysicalName();

				File f1 = new File(attFileMetaMngOutputFullPath);

				Workbook workbook = Workbook.getWorkbook(f1);
				Sheet sheet = workbook.getSheet(0);

				for (int h = 1; h < sheet.getRows(); h++) {
					if (!sheet.getCell(0, h).getContents().equals("") || !sheet.getCell(1, h).getContents().equals("") || !sheet.getCell(2, h).getContents().equals("") || !sheet.getCell(3, h).getContents().equals("") || !sheet.getCell(4, h).getContents().equals("") || !sheet.getCell(5, h).getContents().equals("") || !sheet.getCell(6, h).getContents().equals("")) {

						Map<String, Object> outputMap = new HashMap<String, Object>();

						outputMap.put("apiCode", params.get("apiCode"));
						int apiOutptSn = collectRcptnInfoService.selectMetaMngOutputNextSn(outputMap);
						outputMap.put("apiOutptSn", String.valueOf(apiOutptSn));

						if(SangsStringUtil.isEmpty(sheet.getCell(0, h).getContents()) || SangsStringUtil.isEmpty(sheet.getCell(1, h).getContents()) || SangsStringUtil.isEmpty(sheet.getCell(2, h).getContents())) {
							msg = "일부 필수 입력사항이 누락되어 대체하여 등록되었으니 전문정보에서 확인바랍니다.";
						}
						outputMap.put("outptNm", SangsStringUtil.nvl(sheet.getCell(0, h).getContents(), "NAN"));
						outputMap.put("outptEngNm", SangsStringUtil.nvl(sheet.getCell(1, h).getContents(), "NAN"));
						outputMap.put("ccOutptDataTyCode", SangsStringUtil.nvl(sheet.getCell(2, h).getContents(), "DT02"));
						outputMap.put("outptDataLtValue", SangsStringUtil.nvl(sheet.getCell(3, h).getContents()));
						outputMap.put("outptDataNullAt", SangsStringUtil.nvl(sheet.getCell(4, h).getContents()));
						outputMap.put("outptDataScopeCn", SangsStringUtil.nvl(sheet.getCell(5, h).getContents()));
						outputMap.put("useAt", SangsStringUtil.nvl(sheet.getCell(6, h).getContents()));

						Iterator<String> iterator = outputMap.keySet().iterator();
						while(iterator.hasNext()) {
							String key = (String) iterator.next();
							outputMap.put(key, SangsWebUtil.clearXSSMinimum2((String) outputMap.get(key)));

						}

						//바로 인설트
						collectRcptnInfoService.insertMetaMngOutputInfo(outputMap);
					}
				}
			}

		} catch (Exception e) {
			logger.error(this.getClass().getName(), e);
			msg = "처리중 에러가 발생하였습니다.";
		}

		model.addAttribute("type", "PARENT");
		model.addAttribute("msg", msg);
		model.addAttribute("url", "/collector/collect/collect_rcptn_list.do?apiCode="+params.get("apiCode")+"&tIdx="+params.get("tIdx"));
		return "forward:/common/msgForward.do";
	}


}