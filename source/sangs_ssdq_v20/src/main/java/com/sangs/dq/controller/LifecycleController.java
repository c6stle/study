package com.sangs.dq.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sangs.common.support.AuthUtil;
import com.sangs.dq.service.AnalysisService;
import com.sangs.dq.service.LifecycleService;
import com.sangs.dq.service.RuleMngService;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsWebUtil;

@Controller
public class LifecycleController {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	LifecycleService lifecycleService;

	@Value("${file.csv.dataDir}")
	private String csvDir;

	@Resource
	private RuleMngService ruleMngService;

	@Autowired
	AnalysisService analysisService;

	/**
	 * Lifecycle 항목 관리 페이지 이동
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/dq/lifecycle/lifecycleManageList")
	public String lifecycleListMove() throws Exception {
		return "dq/lifecycle/lifecycle_manage_list";

	}

	/**
	 * Lifecycle 분석 페이지 이동
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/dq/lifecycle/lifecycleAnalysis")
	public String lifecycleAnalsMove() throws Exception {
		return "dq/lifecycle/lifecycle_analysis";

	}

	/**
	 * Lifecycle 분석 결과 페이지 이동
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/dq/lifecycle/lifecycleResultList")
	public String lifecycleResultListMove() throws Exception {
		return "dq/lifecycle/lifecycle_analysis_result";

	}

	/**
	 * Lifecycle 분석 결과 상세 페이지 이동
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/dq/lifecycle/lifecycleResultView")
	public String lifecycleResultViewMove(Model model, @RequestParam Map<String, Object> params) throws Exception {
		// Lifecycle 진단 결과 목록 상세 조회
		params.put("prjctSn", AuthUtil.getPrjctSnStr());
		Map<String, Object> rtnMap = lifecycleService.getLifecycleDiagnosisResultInfo(params);
		
		model.addAttribute("info", rtnMap.get("info"));
		return "dq/lifecycle/lifecycle_analysis_result_view";
	}

	
	/**
	 * Lifecycle 항목관리 목록 조회
	 * 
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/dq/lifecycle/getLifecycleMngList")
	public Map<String, Object> getLifecycleMngList(@RequestBody Map<String, Object> params, HttpServletRequest req,	HttpServletResponse res) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {

			rtnMap = lifecycleService.getLifecycleMngList(params, req, res);

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
	 * Lifecycle 항목관리 목록 상세 조회
	 * 
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/dq/lifecycle/getLifecycleMngInfo")
	public Map<String, Object> getLifecycleMngInfo(@RequestBody Map<String, Object> params, HttpServletRequest req,
			HttpServletResponse res) throws Exception {

		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			rtnMap = lifecycleService.getLifecycleMngInfo(params, req, res);

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
	 * Lifecycle 항목관리 목록 등록/수정
	 * 
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/dq/lifecycle/saveLifecycleMngInfo")
	public Map<String, Object> saveLifecycleMngInfo(@RequestBody Map<String, Object> params, HttpServletRequest req,
			HttpServletResponse res) throws Exception {

		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			rtnMap = lifecycleService.saveLifecycleMngInfo(params, req, res);

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
	 * Lifecycle 항목관리 지표 파일 읽어오기
	 * 
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/dq/lifecycle/getLifcycleMngBundleList")
	public Map<String, Object> getLifcycleMngBundleList(@RequestBody Map<String, Object> params, HttpServletRequest req,
			HttpServletResponse res) throws Exception {

		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			rtnMap = lifecycleService.getLifcycleMngBundleList(params, req, res);

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
	 * Lifecycle 항목관리 지표 파일 일괄 등록
	 * 
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/dq/lifecycle/saveLifcycleMngBundleList")
	public Map<String, Object> saveLifcycleMngBundleList(@RequestBody Map<String, Object> params,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			rtnMap = lifecycleService.saveLifcycleMngBundleList(params, req, res);

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
	 * Lifecycle 분석 실행
	 * 
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/dq/lifecycle/executeLifecycleAnalysis")
	public Map<String, Object> executeLifecycleAnalysis(@RequestBody Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		Map<String, Object> analysisMap = new HashMap<String, Object>();
		boolean isCmptn = false;
		try {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			Map<String, Object> cmprMngMap = new HashMap<String, Object>();
			Map<String, Object> mngMap = new HashMap<String, Object>();

			int prjctSn = AuthUtil.getPrjctSn();
			int dbmsCnncSn = AuthUtil.getDbmsCnncSn();

			String dbmsDatabaseNm = AuthUtil.getDbmsDatabaseNm();
			String dbmsSchemaNm = AuthUtil.getDbmsSchemaNm();
			String dateFmt = DateFormatUtils.format(Calendar.getInstance(), "yyyyMMddHHmmss");
			if (!"".equals(dbmsSchemaNm)) {
				dbmsDatabaseNm = dbmsSchemaNm;
			}
			String dgnssInfoId = dbmsCnncSn + "_" + dateFmt;
			String analysisSaveNm = SangsWebUtil.clearXSSMinimum((String) params.get("analysisSaveNm"));
			String dgnssNm = analysisSaveNm + "(" + dbmsCnncSn + "_" + dbmsDatabaseNm + "_" + dateFmt	+ ")";
			int analysisListCnt = Integer.valueOf(String.valueOf(params.get("analysisListCnt")));

			analysisMap.put("dbmsCnncSn", dbmsCnncSn);
			analysisMap.put("prjctSn", prjctSn);
			analysisMap.put("dbmsDatabaseNm", dbmsDatabaseNm);
			analysisMap.put("dgnssInfoId", dgnssInfoId);
			analysisMap.put("dgnssNm", SangsWebUtil.clearXSSMinimum((String) dgnssNm));
			analysisMap.put("trgtCnt", analysisListCnt);

			// Lifecycle 진단 정보 등록
			analysisMap.put("excSttusCd", "S"); // S 시작 
			lifecycleService.saveLifecycleDiagnosisInfo(analysisMap);

			// Lifecycle 항목관리 목록 조회
			params.put("excelYn", "Y");
			mngMap = lifecycleService.getLifecycleMngList(params, req, res);
			list = (List<Map<String, Object>>) mngMap.get("list");

			for (Map<String, Object> map : list) {
				cmprMngMap.put(String.valueOf(map.get("ruleSn")), map);
			}

			list = (List<Map<String, Object>>) params.get("analysisList");
			int analysisCnt = 0;

			for (Map<String, Object> trgtMap : list) {
				trgtMap.putAll(analysisMap);
				lifecycleService.executeLifecycleAnalysis(trgtMap, cmprMngMap);
				analysisCnt++;
			}
			if (analysisListCnt == analysisCnt) {
				analysisMap.put("excSttusCd", "E"); // E 종료
				lifecycleService.saveLifecycleDiagnosisInfo(analysisMap);
				rtnMap.put("resultCd", "OK");
			}
			isCmptn = true;
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("분석중 오류가 발생하였습니다.");
		} finally {
			if(!isCmptn) {
				analysisMap.put("excSttusCd", "F"); // F 실패
				lifecycleService.saveLifecycleDiagnosisInfo(analysisMap);
			}
		}

		return rtnMap;
	}

	
	
	/**
	 * Lifecycle 진단 결과 목록 조회
	 * 
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/dq/lifecycle/getLifecycleDiagnosisResultList")
	public Map<String, Object> getLifecycleDiagnosisResultList(@RequestBody Map<String, Object> params, HttpServletRequest req,	HttpServletResponse res) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {

			rtnMap = lifecycleService.getLifecycleDiagnosisResultList(params, req, res);

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
	 * Lifecycle 진단 결과 목록 상세 테이블 목록 조회
	 * 
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/dq/lifecycle/getLifecycleDiagnosisResultTableList")
	public Map<String, Object> getLifecycleDiagnosisResultTableList(@RequestBody Map<String, Object> params, HttpServletRequest req,	HttpServletResponse res) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {

			rtnMap = lifecycleService.getLifecycleDiagnosisResultTableList(params, req, res);

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
	 * Lifecycle 진단 결과 오류 목록 상세 테이블 목록 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/dq/lifecycle/getLifecycleDiagnosisResultErrTableList")
	public Map<String, Object> getLifecycleDiagnosisResultErrTableList(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {

			rtnMap = lifecycleService.selectLifecycleDiagnosisResultErrTableList(params);

		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}

		return rtnMap;
	}
}
