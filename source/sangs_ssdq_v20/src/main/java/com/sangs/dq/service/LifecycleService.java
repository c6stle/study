package com.sangs.dq.service;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.support.AuthUtil;
import com.sangs.common.support.BizUtil;
import com.sangs.common.support.BizUtil.DBMS_TYPE_NAME;
import com.sangs.common.support.SangsExcelUtil;
import com.sangs.dq.config.AnalsSqlSessionTemplate;
import com.sangs.dq.mapper.LifecycleMapper;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.fwk.common.SangsConstants;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.domain.SangsPagingViewInfo;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsSimpleExcelMaker;
import com.sangs.lib.support.utils.SangsStringUtil;
import com.sangs.meta.service.StdDicaryValidService;

@SangsService
public class LifecycleService extends ServiceBase {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	LifecycleMapper lifecycleMapper;
	
	@Autowired
	private StdDicaryValidService stdDicaryValidService;
	
	//@Resource(name = "analsSqlSessionTemplate")
	private AnalsSqlSessionTemplate sqlSession = new AnalsSqlSessionTemplate();

	@Value("${file.csv.dataDir}")
	private String csvDir;
	
	@Value("${meta.upload.base_path}")
	private String basePathId;
	
	
	@Autowired
	VoltDbService voltDbService;
	
	/**
	 * Lifecycle 항목관리 목록 조회
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getLifecycleMngList(Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {

		logger.debug("params : {}", params);
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			int pageNum = SangsStringUtil.nvlInt(params.get("pageNum"), 1);

			int totalCount = lifecycleMapper.selectLifecycleMngTotalCnt(params);

			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);

			params.put("pageSize", pagingInfo.getPageSize());
			params.put("offset", pagingInfo.getOffset());

			List<Map<String, Object>> list = lifecycleMapper.selectLifecycleMngList(params);

			rtnMap.put("list", list);
			rtnMap.put("totalCount", totalCount);
			rtnMap.put("pagingInfo", pagingInfo);

		} catch (Exception e) {
			throw e;
		}
		return rtnMap;

	}
	
	
	/**
	 * Lifecycle 항목관리 목록 상세 조회
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getLifecycleMngInfo(Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception{
		
		logger.debug("params : {}", params);
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			// 패턴/지표 관리 목록 상세 조회
			Map<String, Object> info = lifecycleMapper.selectLifecycleMngInfo(params);
			
			rtnMap.put("info", info);

		} catch (Exception e) {
			throw e;
		}
		return rtnMap;
	}
	
	
	/**
	 * Lifecycle 항목관리 목록 등록/수정
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> saveLifecycleMngInfo(Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception{
		
		logger.debug("params : {}", params);
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			SangsStringUtil.checkRequiredParam(params, "pmode", "pmode");

			String pomde = String.valueOf(params.get("pmode"));
			
			if ("R".equals(pomde)) {
				boolean isCheck = false;
				List<Map<String, Object>> list = lifecycleMapper.selectLifecycleMngAllList(params);
				String tgrtFieldNm = String.valueOf((params.get("fieldNm")));
				for (Map<String, Object> map : list) {
					String fieldNm = map.get("fieldNm").toString();
					if (tgrtFieldNm.equals(fieldNm)) {
						isCheck = true;
						break;
					}
				}

				if (!isCheck) {
					int ruleSn = lifecycleMapper.selectNextAnlsSn(params);
					params.put("ruleSn", ruleSn);
					lifecycleMapper.insertLifecycleMngInfo(params);

					rtnMap.put("resultCd", "OK");
				} else {
					rtnMap.put("resultCd", "NO");
					rtnMap.put("errorInfo", "\"" + tgrtFieldNm + "\" 은 이미 등록되어 있는 항목 입니다.");
				}
			} else {
				lifecycleMapper.updateLifecycleMngInfo(params);
				
				rtnMap.put("resultCd", "OK");
			}
			
			
		} catch (Exception e) {
			throw e;
		}
		return rtnMap;
	}
		
	
	/**
	 * Lifecycle 항목관리 지표 파일 읽어오기
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getLifcycleMngBundleList(Map<String, Object> params, HttpServletRequest req,	HttpServletResponse res) throws Exception {
		logger.debug("params : {}", params);

		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			String subDir = params.get("subDir").toString();
			String savedFileNm = params.get("savedFileNm").toString();
			String fileFullPath = basePathId + subDir + savedFileNm;

			Workbook workbook = new HSSFWorkbook(new FileInputStream(new File(fileFullPath)));
			int sheetCount = workbook.getNumberOfSheets();
			int cells = -1;
			for (int i = 0; i < sheetCount; i++) {
				Sheet sheet = workbook.getSheetAt(i);
				cells = sheet.getRow(i).getPhysicalNumberOfCells();
			}

			if (cells == 4) {

				String[] columnNames = new String[] { "anlsTyCd", "pdValue", "pdClCd", "rmCn" };

				// 엑셀 단어 목록
				ArrayList<SangsMap> excelList = SangsExcelUtil.loadExcelList(fileFullPath, 1, 0, columnNames);
				String[] upperCaseColumns = new String[] { "anlsTyCd", "pdValue", "pdClCd" };
				List<SangsMap> removeRowList = stdDicaryValidService.removeRowAllColEmptyWithUpper(excelList, upperCaseColumns);
				
				List<Map<String, Object>> list = lifecycleMapper.selectLifecycleMngAllList(params);

				String anlsTyCd = ""; // 분류타입코드
				String pdValue = ""; // 기간값
				String pdClCd = ""; // 기간구분코드
				String ruleValue = "^[1-9]{1}$|^[1-9][0-9]{1}$";
				Pattern pattern = Pattern.compile(ruleValue);
				for (SangsMap map : removeRowList) {
					String fieldNm = ""; // 항목명
					String errorInfo = "";

					anlsTyCd = String.valueOf(map.get("anlsTyCd"));
					pdValue = String.valueOf(map.get("pdValue"));
					pdClCd = String.valueOf(map.get("pdClCd"));

					if (!"".equals(anlsTyCd) && !"".equals(pdValue) && !"".equals(pdClCd)) {
						if (!"YYYYMMDD".equals(anlsTyCd) && !"YYMMDD".equals(anlsTyCd) && !"YYYYMM".equals(anlsTyCd)) {
							errorInfo += "분류타입코드,";
						}

						Matcher matcher = pattern.matcher(pdValue);
						if (!matcher.find()) {
							errorInfo += "기간,";
						}

						if (!"Y".equals(pdClCd) && !"M".equals(pdClCd)) {
							errorInfo += "기간구분,";
						}

						if (errorInfo.length() > 0) {
							errorInfo = errorInfo.substring(0, errorInfo.length() - 1) + " 오류";
						} else {
							fieldNm = anlsTyCd + "_" + pdValue + pdClCd;
						}
					} else {
						errorInfo = "필수값 누락";
					}
					map.putOrg("errorInfo", errorInfo);
					map.putOrg("fieldNm", fieldNm);
				}

				int removeRowListCnt = removeRowList.size();
				for (int i = 0; i < removeRowListCnt; i++) {
					for (int j = 0; j < list.size(); j++) {
						if (removeRowList.get(i).get("fieldNm").equals(list.get(j).get("fieldNm"))) {
							removeRowList.get(i).putOrg("errorInfo", "이미등록된 데이터");
						}
					}
				}

				// 엑셀내 중복 검사
				for (int i = 0; i < removeRowListCnt; i++) {
					for (int j = 0; j < i; j++) {
						if (removeRowList.get(i).get("fieldNm").equals(removeRowList.get(j).get("fieldNm"))) {
							removeRowList.get(i).putOrg("errorInfo", "엑셀내 중복데이터");
						}
					}
				}

				rtnMap.put("list", removeRowList);
			}
		} catch (Exception e) {
			throw e;
		}

		return rtnMap;
	}
	
	
	/**
	 * Lifecycle 항목관리 지표 파일 일괄 등록
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> saveLifcycleMngBundleList(Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			logger.debug("parameter : " + params);

			// 등록
			String subDir = params.get("subDir").toString();
			String savedFileNm = params.get("savedFileNm").toString();
			String fileFullPath = basePathId + subDir + savedFileNm;

			String[] columnNames = new String[] { "anlsTyCd", "pdValue", "pdClCd", "rmCn" };

			ArrayList<SangsMap> excelList = SangsExcelUtil.loadExcelList(fileFullPath, 1, 0, columnNames);
			String[] upperCaseColumns = new String[] { "anlsTyCd", "pdValue", "pdClCd" };
			List<SangsMap> removeRowList = stdDicaryValidService.removeRowAllColEmptyWithUpper(excelList, upperCaseColumns);
			
			String fieldNm = "";
			for (SangsMap map : removeRowList) {
				
				String anlsTyCd = map.getString("anlsTyCd");
				String pdValue = map.getString("pdValue");
				String pdClCd = map.getString("pdClCd");
				fieldNm = anlsTyCd + "_" + pdValue + pdClCd;
				map.putOrg("fieldNm", fieldNm.toUpperCase());

				int ruleSn = lifecycleMapper.selectNextAnlsSn(params);
				map.putOrg("ruleSn", ruleSn);
				map.putOrg("useYn", "Y");
				
				lifecycleMapper.insertLifecycleMngInfo(map);
			}

			rtnMap.put("resultCd", "OK");

		} catch (Exception e) {
			throw e;
		}
		return rtnMap;
	}

	
	/**
	 * Lifecycle 항목관리 지표 엑셀 양식 다운로드
	 * @param params
	 * @return
	 */
	public Workbook getLifcycleMngExcelFormDown(Map<String, Object> params) {

		Workbook workbook = null;

		try {

			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			Map<String, Object> info = new HashMap<String, Object>();
			
			info.put("anlsTyCd", "YYYYMMDD");
			info.put("pdValue", "1");
			info.put("pdClCd", "Y/M");
			info.put("rmCn", "1년");

			list.add(info);

			SangsSimpleExcelMaker em = new SangsSimpleExcelMaker();
			workbook = em.createSheet()
					.setHeaderColNm(new String[] {"*분류", "*기간", "*기간구분", "설명"})
					.setHeaderColId(new String[] {"anlsTyCd", "pdValue", "pdClCd", "rmCn" })
					.setList(list).setAutoSize().getWorkbook();

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("엑셀 생성중 에러가 발생하였습니다." + e.getMessage());
		}

		return workbook;

	}

	/**
	 * Lifecycle 진단 정보 분석
	 * @param params
	 * @param cmprMngMap 
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public void executeLifecycleAnalysis(Map<String, Object> trgtMap, Map<String, Object> cmprMngMap) throws Exception {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

			String dbmsNm = AuthUtil.getDbmsNm().toUpperCase();
			String columnNm = String.valueOf(trgtMap.get("columnNm"));

			String ruleSn = String.valueOf(trgtMap.get("ruleSn"));
			String anlsTyCd = "";
			String pdClCd = "";
			int pdValue = 0;
			if (cmprMngMap.containsKey(ruleSn)) {
				SangsMap map = (SangsMap) cmprMngMap.get(ruleSn);

				anlsTyCd = String.valueOf(map.get("anlsTyCd"));
				pdClCd = String.valueOf(map.get("pdClCd"));
				pdValue = -Integer.valueOf(String.valueOf(map.get("pdValue"))); // 기간 계산을 위한 음수값
			}

			if ("M".equalsIgnoreCase(pdClCd)) {
				cal.add(Calendar.MONTH, pdValue);
			} else if ("Y".equalsIgnoreCase(pdClCd)) {
				cal.add(Calendar.YEAR, pdValue);
			}

			String whereParam = "";
			String CharNumber = "";
			String chkDate = df.format(cal.getTime()).replace("-", "").trim();
			
			if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.ORACLE, dbmsNm)
				|| BizUtil.isEqualDbms(DBMS_TYPE_NAME.ALTIBASE, dbmsNm)
				|| BizUtil.isEqualDbms(DBMS_TYPE_NAME.DB2, dbmsNm)
				|| BizUtil.isEqualDbms(DBMS_TYPE_NAME.POSTGRESQL, dbmsNm)
				|| BizUtil.isEqualDbms(DBMS_TYPE_NAME.TIBERO, dbmsNm)
				|| BizUtil.isEqualDbms(DBMS_TYPE_NAME.CUBRID, dbmsNm)) {
				
				whereParam = "WHERE TO_CHAR(" + columnNm + ", '" + anlsTyCd + "')";
				
			} else if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.MYSQL, dbmsNm)
					|| BizUtil.isEqualDbms(DBMS_TYPE_NAME.MARIADB, dbmsNm)) {
				
				if ("YYYYMMDD".equals(anlsTyCd)) {
					anlsTyCd = "%Y%m%d";
				} else if ("YYYYMM".equals(anlsTyCd)) {
					anlsTyCd = "%Y%m";
					chkDate = chkDate.substring(0, 6);
				} else if ("YYMMDD".equals(anlsTyCd)) {
					anlsTyCd = "%y%m%d";
					chkDate = chkDate.substring(2);
				}
				
				whereParam = "WHERE DATE_FORMAT(" + columnNm + ", '" + anlsTyCd + "')";

			} else if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.MSSQL, dbmsNm)) {
				
				if ("YYYYMMDD".equals(anlsTyCd)) {
					anlsTyCd = "112";
					CharNumber = "8";
				} else if ("YYYYMM".equals(anlsTyCd)) {
					anlsTyCd = "112";
					CharNumber = "6";
					chkDate = chkDate.substring(0, 6);
				} else if ("YYMMDD".equals(anlsTyCd)) {
					anlsTyCd = "12";
					CharNumber = "6";
					chkDate = chkDate.substring(2);
				}
				
				whereParam = "WHERE CONVERT(CHAR(" + CharNumber + "), " + columnNm + ", " + anlsTyCd + ")";
			} else if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.VOLTDB, dbmsNm)) {
				chkDate = df.format(cal.getTime()).trim();
				trgtMap.put("anlsTyCd", anlsTyCd);
			}
			
			trgtMap.put("chkDate", chkDate);
			trgtMap.put("whereParam", whereParam);
			
			int totalCnt = 0;
			int mtchgCnt = 0;
			int missCnt = 0;
			
			if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.VOLTDB, dbmsNm)) {
				totalCnt = voltDbService.selectTableRowDataCnt(String.valueOf(trgtMap.get("dbmsTableNm")));
				mtchgCnt = voltDbService.selectLifecycleAnalysisMtchgCnt(trgtMap);
				missCnt = voltDbService.selectLifecycleAnalysisMissCnt(trgtMap);
			} else {
				totalCnt = sqlSession.selectInteger("AnalysisMapper.selectTableRowDataCnt", trgtMap);
				mtchgCnt = sqlSession.selectInteger("AnalysisMapper.selectLifecycleAnalysisMtchgCnt", trgtMap);
				missCnt = sqlSession.selectInteger("AnalysisMapper.selectLifecycleAnalysisMissCnt", trgtMap);
			}

			trgtMap.put("totalCnt", totalCnt);
			trgtMap.put("mtchgCnt", mtchgCnt);
			trgtMap.put("excessCnt", missCnt);

			lifecycleMapper.insertLifecycleDiagnosisTableInfo(trgtMap);

		} catch (Exception e) {
			logger.error("", e);
			trgtMap.put("dgnssErrId", DateFormatUtils.format(Calendar.getInstance(), "yyyyMMddHHmmssSSS"));
			
			trgtMap.put("errNm", String.valueOf(ExceptionUtils.getMessage(e)));
			trgtMap.put("errCn", String.valueOf(ExceptionUtils.getStackTrace(e)));
			// 에러테이블
			lifecycleMapper.insertLifecycleDiagnosisTableErrInfo(trgtMap);
			// throw e;
		}
	}
	
	/**
	 * Lifecycle 진단 정보 등록/수정
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public void  saveLifecycleDiagnosisInfo(Map<String, Object> params) throws Exception {
		logger.debug("params : {}", params);
		try {
			
			String excSttusCd = String.valueOf(params.get("excSttusCd"));
			
			if("S".equals(excSttusCd)) {
				// 등록
				lifecycleMapper.insertLifecycleDiagnosisInfo(params);
			} else {
				// 종료, 실패
				lifecycleMapper.updateLifecycleDiagnosisInfo(params);
			}
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	
	/**
	 * Lifecycle 진단 결과 목록 조회
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getLifecycleDiagnosisResultList(Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {

		logger.debug("params : {}", params);
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			params.put("prjctSn", AuthUtil.getPrjctSnStr());
			int pageNum = SangsStringUtil.nvlInt(params.get("pageNum"), 1);

			int totalCount = lifecycleMapper.selectLifecycleDiagnosisResultTotalCnt(params);

			SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);

			params.put("pageSize", pagingInfo.getPageSize());
			params.put("offset", pagingInfo.getOffset());

			List<Map<String, Object>> list = lifecycleMapper.selectLifecycleDiagnosisResultList(params);

			rtnMap.put("list", list);
			rtnMap.put("totalCount", totalCount);
			rtnMap.put("pagingInfo", pagingInfo);

		} catch (Exception e) {
			throw e;
		}
		return rtnMap;

	}
	
	/**
	 * Lifecycle 진단 결과 목록 상세 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getLifecycleDiagnosisResultInfo(Map<String, Object> params) throws Exception {

		logger.debug("params : {}", params);
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			Map<String, Object> info = lifecycleMapper.selectLifecycleDiagnosisResultInfo(params);
			
			rtnMap.put("info", info);

		} catch (Exception e) {
			throw e;
		}
		return rtnMap;

	}
	
	
	/**
	 * Lifecycle 진단 결과 목록 상세 테이블 목록 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getLifecycleDiagnosisResultTableList(Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {

		logger.debug("params : {}", params);
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			params.put("prjctSn", AuthUtil.getPrjctSnStr());
			List<Map<String, Object>> list = lifecycleMapper.selectLifecycleDiagnosisResultTableList(params);

			rtnMap.put("list", list);

		} catch (Exception e) {
			throw e;
		}
		return rtnMap;

	}

	/**
	 * Lifecycle 진단 결과 오류 목록 상세 테이블 목록 조회
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 */
	public Map<String, Object> selectLifecycleDiagnosisResultErrTableList(Map<String, Object> params) {
		

		logger.debug("params : {}", params);
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			params.put("prjctSn", AuthUtil.getPrjctSnStr());
			List<Map<String, Object>> list = lifecycleMapper.selectLifecycleDiagnosisResultErrTableList(params);

			rtnMap.put("list", list);

		} catch (Exception e) {
			throw e;
		}
		return rtnMap;
		
	}
	
}
