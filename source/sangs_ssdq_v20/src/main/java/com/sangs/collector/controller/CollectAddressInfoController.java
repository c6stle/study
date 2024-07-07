package com.sangs.collector.controller;

import java.io.File;
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

import com.sangs.collector.service.CollectAddressInfoService;
import com.sangs.collector.util.CollectorFileUploadUtil;
import com.sangs.collector.util.CollectorFormBasedFileVo;
import com.sangs.common.base.ServiceBase;
import com.sangs.fwk.support.SangsPropertyUtil;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsStringUtil;
import com.sangs.lib.support.utils.SangsWebUtil;

import jxl.Sheet;
import jxl.Workbook;

/**
 * Description : 메타 통합관리 > 메타주소정보 관련 컨트롤러
 *
 * Modification Information
 * 수정일			수정자			수정내용
 * -------		-----------------------------------
 * 2016.01.27 	송호현			최초작성
 *
 */
@Controller
public class CollectAddressInfoController extends ServiceBase {

	@Autowired
	private CollectAddressInfoService collectAddressInfoService;


	private Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * 메타주소 조회 및 등록 양식
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/collector/collect/collect_address_list")
	public String metaMngAddressList(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {

		try {
			List<SangsMap> resultList = collectAddressInfoService.selectMetaMngAddressList(params);

			model.addAttribute("resultList", resultList);
			model.addAttribute("params", params);

		} catch (Exception e) {
			logger.info("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}
		return "collector/collect/collect_address_list";
	}

	/**
	 * 메타주소 엑셀일괄 등록 처리
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/mngr/metaMng/metaMngAddressImportExec.do")
	public String metaMngAddressImportExec(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {

		String msg = "";

		try {

			String attFileMetaMngAddressPath = SangsPropertyUtil.getProperty("Globals.attFileBasePath") + SangsPropertyUtil.getProperty("Globals.fileIdntfrCode_meta");
			System.out.println(" attFileMetaMngAddressPath :"  + attFileMetaMngAddressPath);

			List<CollectorFormBasedFileVo> fileList = null;
			if (ServletFileUpload.isMultipartContent(req)) {
				fileList = CollectorFileUploadUtil.uploadFiles(req, attFileMetaMngAddressPath);
			}

			if (fileList != null && fileList.size() > 0) {

				CollectorFormBasedFileVo vo = fileList.get(0);
				String attFileMetaMngAddressFullPath = attFileMetaMngAddressPath + "/" + vo.getPhysicalName();

				File f1 = new File(attFileMetaMngAddressFullPath);

				Workbook workbook = Workbook.getWorkbook(f1);
				Sheet sheet = workbook.getSheet(0);

				for (int h = 1; h < sheet.getRows(); h++) {
					if (!sheet.getCell(1, h).getContents().equals("")) {

						Map<String, Object> addressMap = new HashMap<String, Object>();


						addressMap.put("apiCd", params.get("apiCd"));
						int apiAdresSn = collectAddressInfoService.selectMetaMngAddressNextSn(addressMap);
						addressMap.put("apiAddrSn", String.valueOf(apiAdresSn));

						addressMap.put("apiAddrNm", SangsStringUtil.nvl(sheet.getCell(0, h).getContents()));
						addressMap.put("apiAddr", SangsStringUtil.nvl(sheet.getCell(1, h).getContents()));
						addressMap.put("apiAddrCn", SangsStringUtil.nvl(sheet.getCell(2, h).getContents()));
						addressMap.put("apiRqstCn", SangsStringUtil.nvl(sheet.getCell(3, h).getContents()));
						addressMap.put("useYn", SangsStringUtil.nvl(sheet.getCell(4, h).getContents()));

						Iterator<String> iterator = addressMap.keySet().iterator();
						while(iterator.hasNext()) {
							String key = (String) iterator.next();
							addressMap.put(key, SangsWebUtil.clearXSSMinimum2((String) addressMap.get(key)));
						}

						//바로 인설트
						collectAddressInfoService.insertMetaMngAddressInfo(addressMap);

					}
				}
			}

		} catch (Exception e) {
			log.error(this.getClass().getName(), e);
			msg = "처리중 에러가 발생하였습니다.";
		}

		model.addAttribute("type", "PARENT");
		model.addAttribute("msg", msg);
		model.addAttribute("url", "/collector/collect/collect_address_list.do?apiCode="+params.get("apiCode")+"&tIdx="+params.get("tIdx"));
		return "forward:/common/msgForward.do";
	}



}