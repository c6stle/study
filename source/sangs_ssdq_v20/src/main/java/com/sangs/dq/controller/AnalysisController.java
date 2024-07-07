package com.sangs.dq.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sangs.common.base.ControllerBase;
import com.sangs.common.support.AuthUtil;
import com.sangs.common.support.BizUtil;
import com.sangs.common.support.BizUtil.DBMS_TYPE_NAME;
import com.sangs.dq.service.AnalysisService;
import com.sangs.dq.service.CsvInfoService;
import com.sangs.dq.service.MongoDbService;
import com.sangs.dq.service.RuleMngService;
import com.sangs.dq.service.VoltDbService;
import com.sangs.lib.support.exception.SangsMessageException;

@Controller
public class AnalysisController extends ControllerBase {

	@Autowired
	AnalysisService analysisService;
	
	@Autowired
	RuleMngService ruleMngService;

	@Autowired
	MongoDbService mongoDbService;
	
	@Autowired
	VoltDbService voltDbService;
	
	@Autowired
	CsvInfoService csvInfoService;
	/**
	 * 테이블 구조 분석 목록 조회
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/dq/analysis/getAnalysisTableList")
	public Map<String, Object> getAnalysisTableList(@RequestBody Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		logger.debug("params : {}", params);
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {

			String dbmsNm = AuthUtil.getDbmsNm().toUpperCase();
			
			if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.MONGODB, dbmsNm)) {
				
				rtnMap = mongoDbService.getAnalysisTableList(params);
				
			} else if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.VOLTDB, dbmsNm)) {
				
				rtnMap = voltDbService.getAnalysisTableList(params);
				
			} else if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.CSV, dbmsNm)) {
				
				rtnMap = csvInfoService.getAnalysisTableList(params);
				
			} else {
				if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CUBRID, dbmsNm)) {
					params.put("dbmsId", AuthUtil.getDbmsId());
				}
				rtnMap = analysisService.getAnalysisTableList(params);
				
			}
			

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
	 * 컬럼 구조 분석 목록 조회
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/dq/analysis/getAnalysisTableColumnList")
	public Map<String, Object> getAnalysisTableColumnList(@RequestBody Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		logger.debug("params : {}", params);
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			String dbmsNm = AuthUtil.getDbmsNm().toUpperCase();
				
			if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.MONGODB, dbmsNm)) {
				rtnMap = mongoDbService.getAnalysisTableColumnList(params);
			} else if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.VOLTDB, dbmsNm)) {
				rtnMap = voltDbService.getAnalysisTableColumnList(params);
			} else if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.CSV, dbmsNm)) {
				rtnMap = csvInfoService.getAnalysisTableColumnList(params);
			} else {
				if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CUBRID, dbmsNm)) {
					params.put("dbmsId", AuthUtil.getDbmsId());
				}
				rtnMap  = analysisService.getAnalysisTableColumnList(params);
			}

			
			
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}

		return rtnMap;
	}
	
}

	
	
	
	
	
