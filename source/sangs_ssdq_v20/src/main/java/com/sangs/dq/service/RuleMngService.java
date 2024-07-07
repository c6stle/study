package com.sangs.dq.service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.support.AuthUtil;
import com.sangs.common.support.BizUtil;
import com.sangs.common.support.BizUtil.DBMS_TYPE_NAME;
import com.sangs.dq.config.AnalsSqlSessionTemplate;
import com.sangs.dq.mapper.RuleMngMapper;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.fwk.common.SangsConstants;
import com.sangs.lib.support.domain.SangsPagingViewInfo;
import com.sangs.lib.support.utils.SangsStringUtil;

@SangsService
public class RuleMngService extends ServiceBase{

	protected Logger logger = LoggerFactory.getLogger(getClass());

	//@Resource(name = "analsSqlSessionTemplate")
	private AnalsSqlSessionTemplate sqlSession = new AnalsSqlSessionTemplate();

	@Autowired
	private RuleMngMapper ruleMngMapper;

	@Autowired
	VoltDbService voltDbService;
	
	@Autowired
	CsvInfoService csvInfoService;
	
	/**
	 * 패턴/지표 관리 목록 조회
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getPatternList(Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {

		logger.debug("params : {}", params);
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			int pageNum = SangsStringUtil.nvlInt(params.get("pageNum"), 1);

			int totalCount = ruleMngMapper.selectPatternTotalCnt(params);

			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);

			params.put("pageSize", pagingInfo.getPageSize());
			params.put("offset", pagingInfo.getOffset());

			List<Map<String, Object>> list = ruleMngMapper.selectPatternList(params);

			for(Map<String, Object> map : list) {
				String tempCn1 = String.valueOf(map.get("ruleExprsnValue"));
				String tempCn2 = String.valueOf(map.get("ruleDc"));
				if(tempCn1.getBytes().length > 100) {
					map.put("rule_exprsn_value", SangsStringUtil.substringByte(tempCn1, 100) + "...");
				}
				if(tempCn2.getBytes().length > 100) {
					map.put("rule_dc", SangsStringUtil.substringByte(tempCn2, 100) + "...");
				}
			}
			
			if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.MONGODB, AuthUtil.getDbmsNm())) {

				for (Iterator<Map<String, Object>> it = list.iterator(); it.hasNext();) {
					Map<String, Object> obj = it.next();

					String ruleSeCd = String.valueOf(obj.get("ruleSeCd"));
					if ("AG000601".equals(ruleSeCd) || "AG000602".equals(ruleSeCd) || "AG000603".equals(ruleSeCd)) {
						it.remove();
					}
				}
			}
			
			rtnMap.put("list", list);
			rtnMap.put("totalCount", totalCount);
			rtnMap.put("pagingInfo", pagingInfo);

		} catch (Exception e) {
			throw e;
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
	public Map<String, Object> getPatternInfo(Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception{
		
		logger.debug("params : {}", params);
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			// 패턴/지표 관리 목록 상세 조회
			Map<String, Object> info = ruleMngMapper.selectPatternInfo(params);
			
			rtnMap.put("info", info);

		} catch (Exception e) {
			throw e;
		}
		return rtnMap;
	}

	/**
	 * 패턴/지표 관리 목록 등록
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> insertPatternInfo(Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception{
		logger.debug("params : {}", params);

		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
			
			SangsStringUtil.checkStringLegnth(String.valueOf(params.get("ruleNm")), 200, String.valueOf(params.get("keyDesc")));
			if ("AG000402".equals(params.get("ruleSeCd"))) {
				params.put("ruleExprsnValue", makeSql(params));
			}
			int ruleSn = ruleMngMapper.selectNextRuleSn(params);
			params.put("ruleSn", ruleSn);
			
			ruleMngMapper.insertPatternInfo(params);
			
			rtnMap.put("resultCd", "OK");
			
		} catch(Exception e) {
			throw e;
		}
		
		
		return rtnMap;
	}
	
	/**
	 * Sql 생성
	 */
	public String makeSql(Map<String, Object> params) {
		
		String schema = String.valueOf(params.get("schema"));
		String table = String.valueOf(params.get("table"));
		String whereVal = String.valueOf(params.get("ruleExprsnValue"));
		String sql = "";
		
		String dbmsNm = AuthUtil.getDbmsNm();
		if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.CUBRID, dbmsNm) || BizUtil.isEqualDbms(DBMS_TYPE_NAME.MSSQL, dbmsNm)
				|| BizUtil.isEqualDbms(DBMS_TYPE_NAME.ALTIBASE, dbmsNm)) {
			sql = "SELECT ${columnName} FROM " + table + " WHERE " + whereVal + "";
		} else {
			sql = "SELECT ${columnName} FROM " + schema + "." + table + " WHERE " + whereVal + "";
		}
		
		return sql;
		
	}
	
	/**
	 * 패턴/지표 관리 목록 수정
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> updatePatternInfo(Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception{
		logger.debug("params : {}", params);
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
			
			String ruleSeCd = String.valueOf((params.get("ruleSeCd")));
			
			// 기본 분석 , value Frequency, 기본 패턴은 사용 여부만 수정 가능
			if("AG000100".equals(ruleSeCd) || "AG000200".equals(ruleSeCd) || "AG000300".equals(ruleSeCd) ) {
				
				ruleMngMapper.updatePatternUseYnInfo(params);
				
			} else {
				if ("AG000402".equals(ruleSeCd)) {
					params.put("ruleExprsnValue", makeSql(params));
				}
				ruleMngMapper.updatePatternInfo(params);
				
			}
			
			rtnMap.put("resultCd", "OK");
			
		} catch(Exception e) {
			throw e;
		}
		
		return rtnMap;
	}

	/**
	 * 사용자정의 기본패턴
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> checkRulePattern(Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception{
		logger.debug("params : {}", params);
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {

			String testValue = String.valueOf(params.get("testValue"));
			String ruleExprsnValue = String.valueOf(params.get("ruleExprsnValue"));

			Pattern pattern = Pattern.compile(ruleExprsnValue);

			Matcher matcher = pattern.matcher(testValue);

			rtnMap.put("result", matcher.find() ? "Y" : "N");

		} catch (Exception e) {
			throw e;
		}
		
		return rtnMap;
	}

	/**
	 * 사용자정의 SQL
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> checkRuleSql(Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		logger.debug("params : {}", params);

		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {

			boolean bResultYn = false;
			String dbmsNm = AuthUtil.getDbmsNm().toUpperCase();
			int resultCnt = -1;
			
			if(params.containsKey("columnName")) {
				String whereValue = String.valueOf(params.get("whereValue"));
				String columnName = String.valueOf(params.get("columnName"));
				whereValue = StringUtils.replace(whereValue, "${columnName}", columnName);
				params.put("whereValue", whereValue);
			}
			
			if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.VOLTDB, dbmsNm)) {
				resultCnt = voltDbService.selectUserCheckSql(params);
			} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CSV, dbmsNm)) {
				resultCnt = csvInfoService.selectUserCheckSql(params);
			} else {
				resultCnt = sqlSession.selectInteger("AnalysisMapper.selectUserCheckSql", params);
			}
			if (resultCnt > -1) {
				bResultYn = true;
			}
			rtnMap.put("result", bResultYn ? "Y" : "N");

		} catch (Exception e) {
			throw e;
		}

		return rtnMap;
	}
	
	/**
	 * 사용자정의 날짜패턴
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> checkDfRulePattern(Map<String, Object> params) throws Exception {
		logger.debug("params : {}", params);

		Map<String, Object> rtnMap = new HashMap<String, Object>();

		String testValue = String.valueOf(params.get("testValue"));
		String ruleExprsnValue = String.valueOf(params.get("ruleExprsnValue"));

		boolean isValeLengthSame = true;
		if (testValue.length() != ruleExprsnValue.length()) {
			isValeLengthSame = false;
		}
		boolean isYn = false;

		if (isValeLengthSame) {
			SimpleDateFormat format1 = new SimpleDateFormat(ruleExprsnValue, Locale.KOREAN);

			format1.setLenient(false);

			try {
				format1.parse(testValue);
				isYn = true;
			} catch (Exception e) {
				isYn = false;
			}
		}

		rtnMap.put("result", isYn ? "Y" : "N");

		return rtnMap;
	}



}
