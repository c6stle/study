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
import com.sangs.dq.service.RuleMngService;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsStringUtil;

/**
 * @author user
 *
 */
@Controller
public class RuleMngController extends ControllerBase{

	@Autowired
	RuleMngService ruleMngService;

	
	/**
     * 패턴/지표 관리 페이지 이동
	 * 
	 * @return
	 * @throws Exception
     */
	@RequestMapping("/dq/ruleMng/ruleManageList")
	public String patternListMove() throws Exception{
		return "dq/ruleMng/rule_manage_list";
	}
	
	
	/**
	 * 패턴/지표 관리 목록 조회
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/dq/ruleMng/getPatternList")
	public Map<String, Object> getPatternList(@RequestBody Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {

			rtnMap = ruleMngService.getPatternList(params, req, res);

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
	 * 패턴/지표 관리 목록 상세 조회
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/dq/ruleMng/getPatternInfo")
	public Map<String, Object> getAnalsPatternInfo(@RequestBody Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			rtnMap = ruleMngService.getPatternInfo(params, req, res);

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
	 * 패턴/지표 관리 목록 등록/수정
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/dq/ruleMng/savePatternInfo")
	public Map<String, Object> savePatternInfo(@RequestBody Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {

		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			SangsStringUtil.checkRequiredParam(params, "pmode", "pmode");

			String pomde = String.valueOf(params.get("pmode"));
			int dbmsSn = 0;
			
			if ("AG000402".equals(params.get("ruleSeCd"))) {
				dbmsSn = Integer.valueOf((AuthUtil.getDbmsSn()));
			}

			params.put("dbmsSn", dbmsSn);

			if ("R".equals(pomde)) {

				rtnMap = ruleMngService.insertPatternInfo(params, req, res);

			} else if ("M".equals(pomde)) {

				rtnMap = ruleMngService.updatePatternInfo(params, req, res);

			} else {
				throw new SangsMessageException("잘못된 접근입니다.");
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
	 * 사용자정의 (SQL, 패턴)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/dq/ruleMng/checkRuleSqlAndPattern")
	public Map<String, Object> checkRuleSqlAndPattern(@RequestBody Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();	

		try {

			SangsStringUtil.checkRequiredParam(params, "ruleSeCd", "ruleSeCd");

			String ruleSeCd = String.valueOf(params.get("ruleSeCd"));

			if ("AG000402".equals(ruleSeCd)) {
				try {

					rtnMap = ruleMngService.checkRuleSql(params, req, res);

				} catch (Exception e) {

					throw new SangsMessageException("where 절 문에 오류가 있습니다.");
				}
			} else if ("AG000401".equals(ruleSeCd)) {

				rtnMap = ruleMngService.checkRulePattern(params, req, res);

			} else if ("AG000501".equals(ruleSeCd)) {

				rtnMap = ruleMngService.checkDfRulePattern(params);

			}else {
				throw new SangsMessageException("잘못된 접근입니다.");
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