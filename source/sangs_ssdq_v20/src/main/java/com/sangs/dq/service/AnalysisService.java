package com.sangs.dq.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.support.AuthUtil;
import com.sangs.common.support.BizUtil;
import com.sangs.common.support.BizUtil.DBMS_TYPE_NAME;
import com.sangs.common.support.BizUtil.DbmsDataTypeGroup;
import com.sangs.dq.config.AnalsSqlSessionTemplate;
import com.sangs.dq.mapper.CsvInfoMapper;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.utils.SangsSimpleExcelMaker;

@SangsService
public class AnalysisService extends ServiceBase {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	//@Resource(name = "analsSqlSessionTemplate")
	private AnalsSqlSessionTemplate sqlSession = new AnalsSqlSessionTemplate();

	@Autowired
	CsvInfoMapper csvInfoMapper;
	
	@Autowired
	RuleMngService ruleMngService;
	
	
	/**
	 * 엑셀 다운로드
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Workbook getSchemaExcelDown(Map<String, Object> params) throws Exception {
		Workbook workbook = null;

		
		try {
			List<Map<String, Object>> tableList = (List<Map<String, Object>>) getAnalysisTableList(params).get("tableList");			//테이블 목록
			List<Map<String, Object>> columnList = (List<Map<String, Object>>) getAnalysisTableColumnList(params).get("columnList");	//컬럼 목록
			
			
			//EXCEL 생성
			SangsSimpleExcelMaker em = new SangsSimpleExcelMaker();
			
			workbook = em.createSheet("tableList")
					.setHeaderColNm("테이블명", "Row 수", "Index 수", "PK컬럼 수", "테이블 설명")
					.setHeaderColId("dbmsTableNm", "numRows", "indexCnt", "pkCnt", "comments")
					.setList(tableList).setAutoSize().getWorkbook();
			
			workbook = em.createSheet("columnList")
					.setHeaderColNm("테이블명", "컬럼 명", "Data 타입", "Data 길이", "Data 정밀도", "Data 스케일", "널 허용", "디폴트 값", "컬럼 설명")
					.setHeaderColId("dbmsTableNm", "columnName", "dataType", "dataLength", "dataPrecision", "dataScale", "nullable", "dataDefault", "comments")
					.setList(columnList).setAutoSize().getWorkbook();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workbook;
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
			
			String dbmsDatabaseNm = AuthUtil.getDbmsDatabaseNm();
			String dbmsSchemaNm = AuthUtil.getDbmsSchemaNm();
			if(!"".equals(dbmsSchemaNm)){
				// PostgreSQL, DB2, Altibase
				dbmsDatabaseNm = dbmsSchemaNm;
			}
			params.put("dbmsDatabaseNm", dbmsDatabaseNm);
			
			List<Map<String, Object>> list = sqlSession.selectList("AnalysisMapper.selectAnalysisTableList", params);
			
			if(!params.containsKey("onlyTable") || !"Y".equals(String.valueOf(params.get("onlyTable")))) {
				for (Map<String, Object> map : list) {
					
					Map<String, Object> tempMap = new HashMap<String, Object>();
					tempMap.put("dbmsTableNm", map.get("dbmsTableNm"));
					tempMap.put("dbmsDatabaseNm", params.get("dbmsDatabaseNm"));
					
					int numRows = sqlSession.selectInteger("AnalysisMapper.selectTableRowDataCnt", tempMap);
					map.put("num_rows", numRows);
				}
				rtnMap.put("tableCnt", list.size());
			}
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
			
			String dbmsDatabaseNm = AuthUtil.getDbmsDatabaseNm();
			String dbmsSchemaNm = AuthUtil.getDbmsSchemaNm();
			if(!"".equals(dbmsSchemaNm)){
				// PostgreSQL, DB2, Altibase
				dbmsDatabaseNm = dbmsSchemaNm;
			}
			params.put("dbmsDatabaseNm", dbmsDatabaseNm);
			
			List<Map<String, Object>> list = sqlSession.selectList("AnalysisMapper.selectAnalysisTableColumnList", params);
			List<Map<String, Object>> dateColumnList = new ArrayList<Map<String,Object>>();
			String dataDefault = "";
			String nullable = "";
			String dataType = "";
			String trgtDataType = "";
			DbmsDataTypeGroup getDataTypeInfo;
			for(Map<String, Object> map : list) {
				if(!params.containsKey("onlyColumnDateType") || !"Y".equals(String.valueOf(params.get("onlyColumnDateType")))) {
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
					
					rtnMap.put("columnList", list);
					rtnMap.put("columnCnt", list.size());
					
				} else {
					
					dataType = String.valueOf(map.get("dataType")).toUpperCase();
					getDataTypeInfo = DbmsDataTypeGroup.findByDataType(dataType);
					trgtDataType = getDataTypeInfo.getTypeNm();
					
					if("DATE".equals(trgtDataType)) {
						dateColumnList.add(map);
					}
					rtnMap.put("columnList", dateColumnList);
				}
			}
			
		} catch (Exception e) {
			throw e;
		}

		return rtnMap;

	}
	
	/**
	 * 컬럼별 최댓값 and 최솟값 조회  
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getAnalysisColumnsMaxAndMinValue(List<Map<String, Object>> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();
		
		try {
			StringBuffer strBuf = new StringBuffer("");
			
			String dbmsNm = AuthUtil.getDbmsNm().toUpperCase();
			String columnName = "";
			String dataType = "";
			String dbmsTableNm = "";
			String dbmsDatabaseNm = "";
			Object replValue = null;
			
			DbmsDataTypeGroup getDataTypeInfo;
			String trgtDataType = "";
			for(Map<String, Object> map : params) {
				columnName = String.valueOf(map.get("columnName"));
				dataType = String.valueOf(map.get("dataType"));
				dbmsTableNm = String.valueOf(map.get("dbmsTableNm"));
				dbmsDatabaseNm = String.valueOf(map.get("dbmsDatabaseNm"));
				
				getDataTypeInfo = DbmsDataTypeGroup.findByDataType(dataType.toUpperCase());
				
				trgtDataType = getDataTypeInfo.getTypeNm();
				
				if("NUMERIC".equals(trgtDataType)) {
					replValue = 0;
				} else if("DATE".equals(trgtDataType)) {
					replValue = "'9999-12-31'";
				} else if ("CHARACTER".equals(trgtDataType)){
					replValue = "'NULL'";
				} else if("NULL".equals(trgtDataType)){
					continue;
				}
				
				if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.POSTGRESQL, dbmsNm)) {
					strBuf.append("COALESCE((select MAX(" + columnName + ") from " + dbmsDatabaseNm + "."	+ dbmsTableNm + ")) \"MAX_" + columnName + "\"" + ",");
					strBuf.append("COALESCE((select MIN(" + columnName + ") from " + dbmsDatabaseNm + "."	+ dbmsTableNm + ")) \"MIN_" + columnName + "\"" + ",");
				} else if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.DB2, dbmsNm)) {
					strBuf.append("COALESCE(MAX(" + columnName + "), "+replValue+") AS MAX_" + columnName + ",");
					strBuf.append("COALESCE(MIN(" + columnName + "), "+replValue+") AS MIN_" + columnName + ",");
				} else if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.MYSQL, dbmsNm) || BizUtil.isEqualDbms(DBMS_TYPE_NAME.MARIADB, dbmsNm)) {
					strBuf.append("IFNULL(MAX(" + columnName + "), "+replValue+") AS MAX_" + columnName + ",");
					strBuf.append("IFNULL(MIN(" + columnName + "), "+replValue+") AS MIN_" + columnName + ",");
				} else if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.MSSQL, dbmsNm)) {
					strBuf.append("ISNULL(MAX(" + columnName + "), "+replValue+") AS MAX_" + columnName + ",");
					strBuf.append("ISNULL(MIN(" + columnName + "), "+replValue+") AS MIN_" + columnName + ",");
				} else {
					strBuf.append("NVL(MAX(" + columnName + "), "+replValue+") AS MAX_" + columnName + ",");
					strBuf.append("NVL(MIN(" + columnName + "), "+replValue+") AS MIN_" + columnName + ",");
				}
				
			}
			if (strBuf.length() > 0) {
				Map<String, Object> minMaxMap = new HashMap<String, Object>();

				Map<String, Object> tempMap = new HashMap<String, Object>();
				tempMap.put("columnNm", String.valueOf(strBuf.substring(0, strBuf.length() - 1)));
				tempMap.put("dbmsDatabaseNm", dbmsDatabaseNm);
				tempMap.put("dbmsTableNm", dbmsTableNm);

				minMaxMap = sqlSession.selectOne("AnalysisMapper.selectAnalysisColumnsMaxAndMinValue", tempMap);
				String maxColumnName = "";
				String minColumnName = "";
				String dataDefault = "";
				String nullable = "";
				for (Map<String, Object> map : params) {
					maxColumnName = "MAX_" + String.valueOf(map.get("columnName"));
					minColumnName = "MIN_" + String.valueOf(map.get("columnName"));

					// 최솟값 최댓값
					if (minMaxMap.containsKey(maxColumnName) && minMaxMap.containsKey(minColumnName)) {
						String maxValue = String.valueOf(minMaxMap.get(maxColumnName));
						String minValue = String.valueOf(minMaxMap.get(maxColumnName));

						if (("NULL".equals(maxValue.toUpperCase()) && "NULL".equals(minValue.toUpperCase()))
								|| ("0".equals(maxValue) && "0".equals(minValue))
								|| (maxValue.indexOf("9999-12-31") != -1 && minValue.indexOf("9999-12-31") != -1)) {
							maxValue = "";
							minValue = "";
						}
						
						map.put("min_value", minValue);
						map.put("max_value", maxValue);
					}

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
				rtnList.addAll(params);

			} else {
				rtnList.addAll(params);
			}
		} catch (Exception e) {
			throw e;
		}

		return rtnList;

	}
}

