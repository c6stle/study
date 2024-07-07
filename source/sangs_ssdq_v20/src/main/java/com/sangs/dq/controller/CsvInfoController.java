package com.sangs.dq.controller;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sangs.dq.service.CsvInfoService;

/**
 * @author user
 *
 */
@Controller
public class CsvInfoController {

	@Autowired
	CsvInfoService csvInfoService;

	/**
	 * csv 파일 등록 팝업
	 * 
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/dq/csvInfo/readCsvFileData")
	public void readCsvFileData(@RequestParam Map<String, String> params, HttpServletRequest req, HttpServletResponse res,
			ModelMap model) throws Exception {
		// 파일정보를 라인별로 list에 설정
		csvInfoService.readCsvFileData(params, req, res, model);
	}

	/**
	 * csv 파일 데이터 설정 팝업
	 * 
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @return
	 */
	@RequestMapping("/dq/csvInfo/readCsvFileDataPaser")
	public void readCsvFileDataPaser(@RequestParam Map<String, String> params, HttpServletRequest req, HttpServletResponse res,
			ModelMap model) throws Exception {
		// 파일 정보를 설정 값 적용된 list 정보로 변환
		csvInfoService.readCsvFileDataPaser(params, req, res, model);
	}

//	/**
//	 * csv 파일 데이터를 로컬db에 등록
//	 * 
//	 * @param params
//	 * @param req
//	 * @param res
//	 * @param model
//	 * @return
//	 */
//	@RequestMapping("/dq/csvInfo/readCsvFileDataPaserSave")
//	public void readCsvFileDataPaserSave(@RequestParam Map<String, String> params, HttpServletRequest req,
//			HttpServletResponse res, ModelMap model) throws Exception {
//		// 파일 정보를 설정 값 적용된 list 정보로 변화하고 db에 등록
//		csvInfoService.readCsvFileDataPaserSave(params, req, res, model);
//	}
	
}