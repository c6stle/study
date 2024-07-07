package com.sangs.dq.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.util.StopWatch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangs.common.base.ServiceBase;
import com.sangs.common.common.CommonConstant;
import com.sangs.common.support.AuthUtil;
import com.sangs.dq.domain.BaseFile;
import com.sangs.dq.mapper.CsvInfoMapper;
import com.sangs.dq.util.CSVParser;
import com.sangs.dq.util.CSVReader;
import com.sangs.dq.util.FileUploadUtil;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.lib.support.utils.SangsEncryptUtil;

@SangsService
public class CsvInfoService extends ServiceBase {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CsvInfoMapper csvInfoMapper;
	
	@Value("${file.csv.dataDir}")
	private String csvDir;
	
	@Value("${spring.default.datasource.username}")
	private String userName;
	
	@Value("${spring.default.datasource.password}")
	private String password;
	

	/**
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @throws Exception
	 * @throws IOException
	 */
	public void readCsvFileData(Map<String, String> params, HttpServletRequest req, HttpServletResponse res,
			ModelMap model) throws Exception, IOException {
		// 소요 시간 확인 용
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		res.setContentType("text/html; charset=utf-8");
		PrintWriter out = res.getWriter();
		String json = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper(); // parser

		// 등록 파일 확인
		List<Object> authorList = new ArrayList<Object>();
		String attFileOutputPath = csvDir + "/";
		String fileName = "";
		List<BaseFile> fileList = null;
		try {

			if (ServletFileUpload.isMultipartContent(req)) {
				fileList = FileUploadUtil.uploadFiles(req, attFileOutputPath);
			}

			if (fileList != null && fileList.size() > 0) {
				BaseFile vo = fileList.get(0);
				fileName = vo.getFileName();
				String attFileOutputFullPath = attFileOutputPath + vo.getPhysicalName();
				logger.info("attFileOutputFullPath : "+attFileOutputFullPath);
				logger.info("checkEncoding : "+checkEncoding(attFileOutputFullPath));
				File file = new File(attFileOutputFullPath);
				
				// UTF-8 변환 처리
				if(!checkEncoding(attFileOutputFullPath)) {
					// utf8 변환 처리
					String changText = FileUtils.readFileToString(file, "MS949");
					FileUtils.writeStringToFile(file, changText, "UTF-8");
				}

				//FileUtils.
				String fileText = FileUtils.readFileToString(file, "utf8");
			    
				String[] lines = fileText.split("\n");
				char LF = 0x0A;
				char CR = 0x0D;
				// fileText.replaceAll(LF, "\n\r");
				String[] crlflines = fileText.split("" + CR + LF);
				String[] lflines = fileText.split("" + LF);
				String[] crlines = fileText.split("" + CR);
				String osKnd = "WINDOWS";
				logger.info("lines.length : " + lines.length + " / crlflines.length : " + crlflines.length);
				logger.info("lflines.length : " + lflines.length + " / crlines.length : " + crlines.length);
				// 자동으로
				if (lines.length == crlflines.length) {
					osKnd = "WINDOWS";
				} else if (lines.length == crlines.length) {
					osKnd = "MAC";
				} else if (lines.length == lflines.length) {
					osKnd = "UNIX";
				}
				logger.info("osKnd : " + osKnd);
				int idx = 0;
				// 라인만큼 리스트 생성
				for (String str : lines) {
					Map<String, Object> authorMap = new HashMap<String, Object>();

					if (idx < 5) { // 5라인으로 리스트 제한
						authorMap.put("dataMap", str);
						authorList.add(authorMap);

					}
					idx++;
				}
				resultMap.put("dataList", authorList);
				resultMap.put("fileName", fileName);
				resultMap.put("attFileOutputFullPath", attFileOutputFullPath);
				resultMap.put("osKnd", osKnd);
				logger.info("=== 파일 사이즈    : " + (file.length() / 1024) + "KB");
			}
			stopWatch.stop();
			logger.info("=== 파일 업로드 처리 시간 : " + stopWatch.getTotalTimeSeconds());
			logger.info("========================================");

		} catch (Exception e) {
			logger.error("[파일 정보 조회 실패]" + e.getStackTrace());
			logger.debug("error : " + e.getMessage());
			e.printStackTrace();
		}
		json = mapper.writeValueAsString(resultMap);
		out.print(json);
		out.flush();
		out.close();
	}
	
	
	/**
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @throws Exception
	 * @throws IOException
	 */
	public void readCsvFileDataPaser(Map<String, String> params, HttpServletRequest req, HttpServletResponse res,
			ModelMap model) throws Exception, IOException {

		res.setContentType("text/html; charset=utf-8");
		PrintWriter out = res.getWriter();
		String json = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper(); // parser
		CSVReader reader= null;
		// 기존 파일정보 설정
		String fileName = req.getParameter("fileName");
		String attFilePath = req.getParameter("path");
		// 입력 정보 설정
		String culLineCnt = req.getParameter("culLineCnt");	// 헤더 라인수
		String delimiter = req.getParameter("delimiter"); 		// 구분자
		String escapeCharYn = req.getParameter("escapeCharYn");	// 이스케이프 구분자 0: 기본, 1: "\""
		
		int titleCnt = 0;
		// 입력 받은 값을 Int 형으로 변경
		if (!"".equals(culLineCnt) && culLineCnt != null) {
			titleCnt = Integer.parseInt(culLineCnt);
		} else {
			culLineCnt = "0";
		}
		if ("".equals(delimiter) || delimiter == null) {
			// default 값 설정
			delimiter = ",";
		}
		char tempChar[] = delimiter.toCharArray();
		char separator = tempChar[0];

		logger.info("============ 파일 처리 시작 =============");
		// 등록 파일 확인
		// 대상 파일이 CSV 파일일때 처리
		try {
			File file = new File(attFilePath);
			if (file.exists()) {
				
				if("Y".equals(escapeCharYn)) {
					CSVParser.DEFAULT_ESCAPE_CHARACTER ='\'';
				}else {
					CSVParser.DEFAULT_ESCAPE_CHARACTER ='\\';
				}
				
				FileInputStream csvFile = new FileInputStream(file);
				InputStreamReader readFile = new InputStreamReader(csvFile, "utf8");
				reader = new CSVReader(readFile, separator);
				String[] nextLine = null;

				int idx = 0;

				List<Map<String, Object>> columnList = new ArrayList<Map<String,Object>>();
				List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();

				while ((nextLine = reader.readNext()) != null) {
					Map<String, Object> dataMap = new HashMap<String, Object>();
					int i = 0;
					for (String str : nextLine) {
						Map<String, Object> columnMap = new HashMap<String, Object>();
						// str = wutil.clearXSSMinimum(str);
						if (idx < titleCnt) {
							columnMap.put("header", str);
							columnMap.put("name", "column" + i);
							columnMap.put("align", "center");
							columnList.add(columnMap);
						} else {
							if (idx == 0) {
								columnMap.put("header", "column" + i);
								columnMap.put("name", "column" + i);
								columnMap.put("align", "center");
								columnList.add(columnMap);
							}
							dataMap.put("column" + i, str);

						}

						i++;
					}
					if (idx >= titleCnt && idx < 11) {
						dataList.add(dataMap);
					}

					idx++;

					if (idx - titleCnt > 4)
						break;

				}

				resultMap.put("columnList", columnList);
				resultMap.put("dataList", dataList);
				resultMap.put("fileName", fileName);
				resultMap.put("culLineCnt", culLineCnt);
				resultMap.put("delimiter", delimiter);
				resultMap.put("attFilePath", attFilePath);
				resultMap.put("escapeCharYn", escapeCharYn);
			}

		} catch (Exception e) {
			logger.error("[파일 정보 변환 실패]" + e.getStackTrace());
			logger.debug("error : " + e.getMessage());
			e.printStackTrace();
		}

		json = mapper.writeValueAsString(resultMap);
		out.print(json);
		out.flush();
		out.close();
	}
	
	/**
	 * @param params
	 * @param req
	 * @param res
	 * @param model
	 * @throws Exception
	 * @throws IOException
	 */
	public Map<String, Object> readCsvFileDataPaserSave(Map<String, Object> params) throws Exception {

		Map<String, Object> rtnMap = new HashMap<String, Object>();
		// 소요 시간 확인 용
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
		String date = format.format(new Date());

		// 파일정보 수신
		String fileName = params.get("fileName").toString();
		String culLineCnt = params.get("culLineCnt").toString();
		String delimiter = params.get("delimiter").toString();
		String attFilePath = params.get("attFilePath").toString();
		String escapeCharYn = params.get("escapeCharYn").toString();
		String osKnd = params.get("osKnd").toString();

		int titleCnt = 0;
		// 입력 받은 값을 Int 형으로 변경
		if (!"".equals(culLineCnt) && culLineCnt != null) {
			titleCnt = Integer.parseInt(culLineCnt);
		}
		if ("".equals(delimiter) || delimiter == null) {
			// default 값 설정
			delimiter = ",";
		}
		char tempChar[] = delimiter.toCharArray();
		char separator = tempChar[0];

		logger.info("===================================");
		date = format.format(new Date());
		logger.debug("=== 파일등록 시작 : " + date);
		boolean isCreateCsvTable = false;
		try {
			CSVReader reader = null;
			CSVReader tmpReader = null;

			File file = new File(attFilePath);
			if (file.exists()) {
				FileInputStream csvFile = new FileInputStream(file);
				InputStreamReader readFile = new InputStreamReader(csvFile, "utf8");
				reader = new CSVReader(readFile, separator);
				tmpReader = new CSVReader(new FileReader(file));
				List<String[]> tmpList = tmpReader.readAll();
				int rowCount = tmpList.size();
				logger.info("===================================" + rowCount);

				String[] nextLine = null;

				int idx = 0;

				List<Map<String, Object>> columnList = new ArrayList<Map<String, Object>>();
				// 컬럼 정보 설정
				while ((nextLine = reader.readNext()) != null) {
					int i = 0;
					for (String str : nextLine) {
						Map<String, Object> columnMap = new HashMap<String, Object>();
						if (idx < titleCnt) {
							columnMap.put("title", str);
							columnMap.put("column", "column" + i);
							columnList.add(columnMap);
						} else {
							if (idx == 0) {
								columnMap.put("title", "column" + i);
								columnMap.put("column", "column" + i);
								columnList.add(columnMap);
							}
							break;
						}

						i++;
					}
					if (idx >= titleCnt) {
						break;
					}
					idx++;

				}
				// 생성될 테이블 명 설정
				String tableName = fileName.substring(0, fileName.indexOf(".")) + "_file_" + date;
				// 생성될 테이블의 컬럼 속성 설정
				String culumnInfo = "";
				StringBuffer strBuf = new StringBuffer("");
				for (int k = 0; k < columnList.size(); k++) {
					//strBuf.append("`" + columnList.get(k).get("column") + "`" + " varchar(255) null COMMENT '"
					strBuf.append("`" + columnList.get(k).get("column") + "`" + " text null COMMENT '"
							+ columnList.get(k).get("title") + "'");
					if (k < columnList.size() - 1) {
						strBuf.append(",");
					}
				}
				culumnInfo = strBuf.toString();
				// 테이블명과 컬럼정보를 map으로 등록
				params.put("tableName", tableName);
				params.put("culumnInfo", culumnInfo);

				// 결과 리턴값 변수
				int ct = -1;
				int totIdres = 0;
				int dataCnt = rowCount - titleCnt;
				// CREATE TABLE
				ct = csvInfoMapper.createCsvTable(params);
				date = format.format(new Date());
				logger.debug("=== 테이블 생성 : " + date);
				// 테이블 생성시
				if (ct >= 0) {
					isCreateCsvTable = true;
					Map<String, Object> paramMap = new HashMap<>();
					paramMap.put("tableName", tableName);
					paramMap.put("columnList", columnList);
					paramMap.put("delimiter", delimiter);
//					paramMap.put("fullFilePath", file.getPath());
					paramMap.put("fullFilePath", attFilePath);
					paramMap.put("titleCnt", titleCnt);
					paramMap.put("osKnd", osKnd);
					paramMap.put("escapeCharYn", escapeCharYn);
					System.out.println(file.getPath());
					logger.info("============ 파일 처리 : OK");
					logger.info("============ 데이터 등록 시작 ===");
					// 파일 데이터 등록
					totIdres = csvInfoMapper.mysqlimportFile(paramMap);
					
					logger.info("totIdres : " + totIdres + " / dataCount : " + (dataCnt));
					if (totIdres == dataCnt) {
						
						String pwd = SangsEncryptUtil.encrypt_AES128(password, CommonConstant.CRYPT_AES_KEY);
						logger.info("등록된 데이터에 대하여 DB 정보 등록");
						// 등록된 데이터에 대하여 DB 정보 등록
						logger.info("파라메터 설정");
						// 파라메터 설정
						rtnMap.put("dbmsIpAddr", "125.7.207.6");
						rtnMap.put("dbmsPortNo", "13306");
						rtnMap.put("dbmsDatabaseNm", "ssdq_v2_db");
						rtnMap.put("dbmsId", userName);
						rtnMap.put("dbmsPassword", pwd);
						rtnMap.put("dbmsDatabaseCn", tableName);

						logger.info("파라메터 설정 완료");
					}
				}
			}
		}  catch (Exception e) {
			if(isCreateCsvTable) {
				csvInfoMapper.dropCsvTable(params);
			}
			throw e;
		}
		return rtnMap;
	}
	
	private boolean checkEncoding(String path) throws Exception{
		boolean check = false;
		
		try {
			byte[] buf = new byte[4096]; 
			String fileName = path; 
			FileInputStream fis = new FileInputStream(fileName); 
			UniversalDetector detector = new UniversalDetector(null); 
			int nread; 
			
			while ((nread = fis.read(buf)) > 0 && !detector.isDone()) { 
				detector.handleData(buf, 0, nread); 
			}
			
			detector.dataEnd(); 
			
			String encoding = detector.getDetectedCharset();//파일 인코딩 체크
			logger.info("encoding : "+encoding);
			if (encoding.equalsIgnoreCase("UTF-8")) {  
				check = true;
			} else { 
				check = false; 
			} 
			detector.reset();
			fis.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return check;
	}
	

	/**
	 * 테이블 구조 분석 목록 조회
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getAnalysisTableList(Map<String, Object> params) throws Exception {

		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			params.put("dbmsDatabaseNm", AuthUtil.getDbmsDatabaseNm());
			params.put("dbmsDatabaseCn", AuthUtil.getDbmsDatabaseCn());
			List<Map<String, Object>> list = csvInfoMapper.selectAnalysisTableList(params);
			int numRows = csvInfoMapper.selectTableRowDataCnt(params);
			list.get(0).put("num_rows", numRows);
			rtnMap.put("tableCnt", list.size());
			rtnMap.put("tableList", list);
			
			
		} catch (Exception e) {
			throw e;
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
	public  Map<String, Object> getAnalysisTableColumnList(Map<String, Object> params) throws Exception {

		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		try {
			params.put("dbmsDatabaseNm", AuthUtil.getDbmsDatabaseNm());
			params.put("dbmsDatabaseCn", AuthUtil.getDbmsDatabaseCn());
			List<Map<String, Object>> list = csvInfoMapper.selectAnalysisTableColumnList(params);
			
			String dataDefault = "";
			String nullable = "";
			for(Map<String, Object> map : list) {
				// 디폴트 값
				dataDefault = String.valueOf(map.get("dataDefault"));
				if ("NULL".equals(dataDefault)) {
					map.put("data_default", "");
				}
				
				// 널 허용
				nullable = String.valueOf(map.get("nullable"));
				if ("YES".equals(nullable)) {
					map.put("nullable", "Y");
				} else if ("NO".equals(nullable)) {
					map.put("nullable", "N");
				}
			}
		
			rtnMap.put("columnList", list);
			rtnMap.put("columnCnt", list.size());

		} catch (Exception e) {
			throw e;
		}

		return rtnMap;

	}


	public int selectUserCheckSql(Map<String, Object> params) throws Exception {

		int count = 0;

		try {
			count = csvInfoMapper.selectUserCheckSql(params);
		} catch (Exception e) {
			throw e;
		}
		return count;
	}
}
