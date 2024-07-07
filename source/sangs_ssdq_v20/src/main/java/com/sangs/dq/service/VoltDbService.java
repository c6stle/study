package com.sangs.dq.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.voltdb.VoltTable;
import org.voltdb.client.Client;
import org.voltdb.client.ClientConfig;
import org.voltdb.client.ClientFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangs.common.support.BizUtil.DbmsDataTypeGroup;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.lib.support.domain.SangsMap;

@SangsService
public class VoltDbService {

	public static Map<String, Object> connectionMap = new HashMap<String, Object>();

	/**
	 * 데이터베이스 연결
	 * @param driverNm
	 * @param dbmsUrl
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getConnection(String driverNm, String dbmsUrl, Map<String, Object> params) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {

			connectionMap.put("dbmsId", String.valueOf(params.get("dbmsId")));
			connectionMap.put("dbmsPassword", String.valueOf(params.get("dbmsPassword")));
			connectionMap.put("dbmsIpAddr", String.valueOf(params.get("dbmsIpAddr")));
			connectionMap.put("dbmsPortNo", String.valueOf(params.get("dbmsPortNo")));
			connectionMap.put("driver", driverNm);
			connectionMap.put("url", dbmsUrl);

			List<Map<String, Object>> list = selectAnalysisTableList();

			rtnMap.put("tableCnt", list.size());

		} catch (Exception e) {
			throw e;
		}
		return rtnMap;
	}
	/**
	 * 테이블 구조 분석 목록 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getAnalysisTableList(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnList = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> list = selectAnalysisTableList();

			rtnList.put("tableList", list);
			rtnList.put("tableCnt", list.size());
		} catch (Exception e) {
			throw e;
		}
		return rtnList;
	}

	/**
	 * 테이블목록 정보 조회
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectAnalysisTableList() throws Exception {

		List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();
		Map<String, Object> tableInfo = new HashMap<String, Object>();

		String dbmsId = String.valueOf(connectionMap.get("dbmsId"));
		String dbmsPassword = String.valueOf(connectionMap.get("dbmsPassword"));
		String dbmsIpAddr = String.valueOf(connectionMap.get("dbmsIpAddr"));
		String dbmsPortNo = String.valueOf(connectionMap.get("dbmsPortNo"));

		try {
			ClientConfig config = null;
			config = new ClientConfig(dbmsId, dbmsPassword);

			Client client = ClientFactory.createClient(config);
			client.createConnection(dbmsIpAddr + ":" + dbmsPortNo);

			VoltTable[] result = client.callProcedure("@SystemCatalog", "TABLES").getResults();
			for (VoltTable node : result) {

				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> info = mapper.readValue(node.toJSONString(), Map.class);
				List<Map<String, Object>> dataList = (List<Map<String, Object>>) info.get("data");

				for (int i = 0; i < dataList.size(); i++) {
					tableInfo = new HashMap<String, Object>();
					List<String> tableList = (List<String>) dataList.get(i);
					tableInfo.put("dbmsTableNm", tableList.get(2));
					tableInfo.put("indexCnt", "");
					tableInfo.put("avgRowLen", "");
					tableInfo.put("pkCnt", "");
					tableInfo.put("numRows", selectTableRowDataCnt(tableList.get(2)));
					tableInfo.put("comments", "");
					rtnList.add(tableInfo);
				}
			}

		} catch (Exception e) {
			throw e;
		}

		return rtnList;
	}

	/**
	 * 테이블 row count 조회
	 * @param tableName
	 * @return
	 */
	public int selectTableRowDataCnt(String tableName) {

		int count = 0;

		try {

			String url = String.valueOf(connectionMap.get("url"));
			String driver = String.valueOf(connectionMap.get("driver"));
			String sql = "SELECT COUNT(*) FROM " + tableName + "";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement query = conn.createStatement();
			ResultSet results = query.executeQuery(sql);

			while (results.next()) {
				count = results.getInt(1);
			}
			query.close();
			results.close();
			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;

	}

	/**
	 * 테이블 row 데이터 목록 조회
	 * @param tableName
	 * @param colInfoList
	 * @param rowCnt
	 * @return
	 */
	public List<Map<String, Object>> selectTableRowDataList(String tableName, List<Map<String,Object>> colInfoList, int rowCnt) {

		List<Map<String, Object>> rtnList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {

			
			for (Map<String, Object> map : colInfoList) {
				Iterator<String> it = map.keySet().iterator();
				Map<String, Object> dataMap1 = new HashMap<String, Object>();
				while (it.hasNext()) {
					String key = it.next();
					String value = map.get(key).toString();
					if (key.equals("columnName")) {
						dataMap1.put(value, "");
					}
				}
				list.add(dataMap1);
			}
			
			String url = String.valueOf(connectionMap.get("url"));
			String driver = String.valueOf(connectionMap.get("driver"));
			String sql = "SELECT * FROM " + tableName + "";

			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement query = conn.createStatement();
			ResultSet results = query.executeQuery(sql);

			while (results.next()) {
				SangsMap dataMap1 = new SangsMap();
				for (Map<String, Object> map : list) {
					Iterator<String> it = map.keySet().iterator();
					while (it.hasNext()) {
						String key = it.next();
						dataMap1.put(key, results.getString("" + key + ""));
					}
				}
				if (rtnList.size() < rowCnt) {
					rtnList.add(dataMap1);
				} else {
					break;
				}
			}
			
			query.close();
			results.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnList;

	}
	
	/**
	 * 프로파일링분석 테이블명 선택 조회
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getDiagnosisColumnList(String tableName) throws Exception {
		List<Map<String, Object>> List = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> columnList = selectAnalysisTableColumnList();
		Map<String, Object> dataMap = new HashMap<String, Object>();

		for (Map<String, Object> column : columnList) {
			if (tableName.equals(column.get("dbmsTableNm"))) {
				List.add(column);
			}
		}
		for (Map<String, Object> map : List) {
			Iterator<String> it = map.keySet().iterator();
			dataMap = new HashMap<>();
			while (it.hasNext()) {
				String key = it.next();
				String value = map.get(key).toString();
				if ("columnName".equals(key)) {
					dataMap.put("columnName", value);
				}
				if ("dataType".equals(key)) {
					dataMap.put("dataType", value);
				}
				if("dataLength".equals(key)) {
					dataMap.put("dataLength", value);
				}
			}
			rtnList.add(dataMap);
		}
		return rtnList;
	}
	
	
	/**
	 * 컬럼 구조 분석 목록 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getAnalysisTableColumnList(Map<String, Object> params) throws Exception {
		Map<String, Object> rtnList = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> list = selectAnalysisTableColumnList();
			List<Map<String, Object>> dateColumnList = new ArrayList<Map<String, Object>>();
			if ("Y".equals(params.get("onlyColumnDateType"))) {
				String dataType = "";
				String trgtDataType = "";
				DbmsDataTypeGroup getDataTypeInfo;

				for (Map<String, Object> map : list) {
					dataType = String.valueOf(map.get("dataType")).toUpperCase();
					getDataTypeInfo = DbmsDataTypeGroup.findByDataType(dataType);
					trgtDataType = getDataTypeInfo.getTypeNm();

					if ("DATE".equals(trgtDataType)) {
						dateColumnList.add(map);
					}
				}
				rtnList.put("columnList", dateColumnList);
			} else {
				rtnList.put("columnList", list);
			}

			rtnList.put("columnCnt", list.size());
		} catch (Exception e) {
			throw e;
		}
		return rtnList;
	}

	/**
	 * 컬럼목록 정보 조회
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectAnalysisTableColumnList() throws Exception {

		String dbmsId = String.valueOf(connectionMap.get("dbmsId"));
		String dbmsPassword = String.valueOf(connectionMap.get("dbmsPassword"));
		String dbmsIpAddr = String.valueOf(connectionMap.get("dbmsIpAddr"));
		String dbmsPortNo = String.valueOf(connectionMap.get("dbmsPortNo"));

		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();

		try {
			ClientConfig config = null;

			config = new ClientConfig(dbmsId, dbmsPassword);

			Client client = ClientFactory.createClient(config);

			client.createConnection(dbmsIpAddr + ":" + dbmsPortNo);// 주소

			VoltTable[] result = client.callProcedure("@SystemCatalog", "COLUMNS").getResults();
			for (VoltTable node : result) {
				ObjectMapper mapper = new ObjectMapper();

				Map<String, Object> info = mapper.readValue(node.toJSONString(), Map.class);
				List<Map<String, Object>> dataList = (List<Map<String, Object>>) info.get("data");

				for (int i = 0; i < dataList.size(); i++) {
					List<String> tableList = (List<String>) dataList.get(i);

					dataMap = new HashMap<String, Object>();
					dataMap.put("tableName", tableList.get(2).toUpperCase());
					dataMap.put("dbmsTableNm", tableList.get(2).toUpperCase());
					dataMap.put("columnName", tableList.get(3).toUpperCase());
					dataMap.put("dataType", tableList.get(5));
					dataMap.put("dataLength", tableList.get(6));
					dataMap.put("dataPrecision", "");
					dataMap.put("dataScale", "");
					dataMap.put("dataDefault", "");
					dataMap.put("nullable", "");
					dataMap.put("comments", "");

					dataMap.put("columnComment", "");

					String dataTypeLength = String.valueOf(tableList.get(5)) + "(" + String.valueOf(tableList.get(6)) + ")";
					dataMap.put("dataTypeLength", dataTypeLength.toUpperCase());
					rtnList.add(dataMap);
				}

			}
		} catch (Exception e) {
			throw e;
		}
		return rtnList;
	}

	/**
	 * 라이프사이클 분석 컬럼 MtchgCnt
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int selectLifecycleAnalysisMtchgCnt(Map<String, Object> params) throws Exception {

		int count = 0;
		try {
			String columnNm = String.valueOf(params.get("columnNm"));
			String chkDate = String.valueOf(params.get("chkDate"));
			String dbmsTableNm = String.valueOf(params.get("dbmsTableNm"));
			String anlsTyCd = String.valueOf(params.get("anlsTyCd"));
			// YYYYMMDD
			int begin = 0;
			int end = 11;

			if ("YYYYMM".equals(anlsTyCd)) {
				end = 8;
			} else if ("YYMMDD".equals(anlsTyCd)) {
				begin = 3;
				end = 9;
			}

			String url = String.valueOf(connectionMap.get("url"));
			String driver = String.valueOf(connectionMap.get("driver"));
			String sql = "SELECT COUNT(" + columnNm + ") FROM " + dbmsTableNm + " WHERE SUBSTRING(CAST(" + columnNm	+ " AS VARCHAR)," + begin + ", " + end + ") >= SUBSTRING('" + chkDate + "', " + begin + "," + end + ")";

			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement query = conn.createStatement();
			ResultSet results = query.executeQuery(sql);

			while (results.next()) {
				count = results.getInt(1);
			}
			
			query.close();
			results.close();
			conn.close();
			
		} catch (Exception e) {
			throw e;
		}
		return count;
	}

	/**
	 * 라이프사이클 분석 컬럼 missCnt
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int selectLifecycleAnalysisMissCnt(Map<String, Object> params) throws Exception {

		int count = 0;

		try {
			String columnNm = String.valueOf(params.get("columnNm"));
			String chkDate = String.valueOf(params.get("chkDate"));
			String dbmsTableNm = String.valueOf(params.get("dbmsTableNm"));
			String anlsTyCd = String.valueOf(params.get("anlsTyCd"));

			int begin = 0;
			int end = 11;

			if ("YYYYMM".equals(anlsTyCd)) {
				end = 8;
			} else if ("YYMMDD".equals(anlsTyCd)) {
				begin = 3;
				end = 9;
			}

			String url = String.valueOf(connectionMap.get("url"));
			String driver = String.valueOf(connectionMap.get("driver"));
			String sql = "SELECT COUNT(" + columnNm + ") FROM " + dbmsTableNm + " WHERE SUBSTRING(CAST(" + columnNm	+ " AS VARCHAR)," + begin + ", " + end + ") < SUBSTRING('" + chkDate + "', " + begin + "," + end + ")";

			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement query = conn.createStatement();
			ResultSet results = query.executeQuery(sql);

			while (results.next()) {
				count = results.getInt(1);
			}
			
			query.close();
			results.close();
			conn.close();
			
		} catch (Exception e) {
			throw e;
		}
		return count;
	}
	
	/**
	 * 사용자정의 SQL
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int selectUserCheckSql(Map<String, Object> params) throws Exception {

		int count = 0;

		try {
			String dbmsTableNm = String.valueOf(params.get("dbmsTableNm"));
			String whereValue = String.valueOf(params.get("whereValue"));

			String url = String.valueOf(connectionMap.get("url"));
			String driver = String.valueOf(connectionMap.get("driver"));
			String sql = "SELECT COUNT(*) FROM " + dbmsTableNm + " WHERE " + whereValue + "";

			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement query = conn.createStatement();
			ResultSet results = query.executeQuery(sql);

			while (results.next()) {
				count = results.getInt(1);
			}
			
			query.close();
			results.close();
			conn.close();
			
		} catch (Exception e) {
			throw e;
		}
		return count;
	}
	
	
	
	
	/**
	 * Unique Count
	 * @param tblNm
	 * @param colNm
	 * @return
	 */
	public int selectDgnssUniqueCnt(String tblNm, String colNm) {
		
		int count = 0;
		try {

			String url = String.valueOf(connectionMap.get("url"));
			String driver = String.valueOf(connectionMap.get("driver"));
			String sql = "SELECT COUNT(*) FROM (SELECT "+colNm+", COUNT(*) mycount FROM "+tblNm+"  GROUP BY "+colNm+" HAVING COUNT(*) = 1) myquery";

			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement query = conn.createStatement();
			ResultSet results = query.executeQuery(sql);

			while (results.next()) {
				count = Integer.parseInt(results.getString(1));
			}
			query.close();
			results.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * Duplicate Count
	 * @param tblNm
	 * @param colNm
	 * @return
	 */
	public int selectDgnssDuplicateCnt(String tblNm, String colNm) {
		int count = 0;
		try {

			String url = String.valueOf(connectionMap.get("url"));
			String driver = String.valueOf(connectionMap.get("driver"));
			String sql = "SELECT COALESCE(SUM(mycount),0) FROM (SELECT "+colNm+", COUNT(*) as mycount FROM "+tblNm+" WHERE "+colNm+" IS NOT NULL   GROUP BY "+colNm+" HAVING COUNT(*) > 1) myquery";

			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement query = conn.createStatement();
			ResultSet results = query.executeQuery(sql);

			while (results.next()) {
				count = Integer.parseInt(results.getString(1));
			}
			query.close();
			results.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * Distinct Count
	 * @param tblNm
	 * @param colNm
	 * @return
	 */
	public int selectDgnssDistinctCnt(String tblNm, String colNm) {
		int count = 0;
		try {

			String url = String.valueOf(connectionMap.get("url"));
			String driver = String.valueOf(connectionMap.get("driver"));
			String sql = "SELECT COUNT(*) FROM (SELECT DISTINCT "+colNm+" FROM "+tblNm+" ) A";

			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement query = conn.createStatement();
			ResultSet results = query.executeQuery(sql);

			while (results.next()) {
				count = Integer.parseInt(results.getString(1));
			}
			query.close();
			results.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * NULL Count
	 * @param tblNm
	 * @param colNm
	 * @return
	 */
	public int selectDgnssNullCnt(String tblNm, String colNm) {
		int count = 0;
		try {

			String url = String.valueOf(connectionMap.get("url"));
			String driver = String.valueOf(connectionMap.get("driver"));
			String sql = "SELECT COUNT(*) FROM "+tblNm+" WHERE "+colNm+" IS NULL";

			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement query = conn.createStatement();
			ResultSet results = query.executeQuery(sql);

			while (results.next()) {
				count = Integer.parseInt(results.getString(1));
			}
			query.close();
			results.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * Blank Count
	 * @param tblNm
	 * @param colNm
	 * @return
	 */
	public int selectDgnssBlankCnt(String tblNm, String colNm) {
		int count = 0;
		try {

			String url = String.valueOf(connectionMap.get("url"));
			String driver = String.valueOf(connectionMap.get("driver"));
			String sql = "SELECT COUNT("+colNm+") FROM "+tblNm+" WHERE "+colNm+"=''";

			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement query = conn.createStatement();
			ResultSet results = query.executeQuery(sql);

			while (results.next()) {
				count = Integer.parseInt(results.getString(1));
			}
			query.close();
			results.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * Value Frequency
	 * @param tblNm
	 * @param colNm
	 * @return
	 */
	public List<SangsMap> selectDgnssFqResultList(String tblNm, String colNm) {
		
		List<SangsMap> rtnFrqList = new ArrayList<SangsMap>();
		SangsMap dataMap = new SangsMap();
		
		try {

			String url = String.valueOf(connectionMap.get("url"));
			String driver = String.valueOf(connectionMap.get("driver"));
			String sql = "SELECT (ROW_NUMBER() OVER()) AS SN, R.* FROM (SELECT "+colNm+" as DATA_VALUE, COUNT(*) as DATA_CNT FROM "+tblNm+" GROUP BY "+colNm+" ORDER BY COUNT(*) DESC LIMIT 10) R";

			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement query = conn.createStatement();
			ResultSet results = query.executeQuery(sql);
			while (results.next()) {
				dataMap = new SangsMap();
				// dataMap.putOrg("sn",results.getString("SN"));
				dataMap.putOrg("dataValue",results.getString("DATA_VALUE"));
				dataMap.putOrg("dataCnt",results.getString("DATA_CNT"));
				rtnFrqList.add(dataMap);
			}

			query.close();
			results.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rtnFrqList;
	}
	
	/**
	 * 사용자 정의 SQL 일치 목록
	 * @param ruleExprsnValue
	 * @param colNm
	 * @return
	 */
	public List<SangsMap> selectDgnssUserDfnSqlMtchList(String ruleExprsnValue, String colNm) {
		
		
		List<SangsMap> rtnList = new ArrayList<SangsMap>();
		SangsMap dataMap = new SangsMap();
		try {

			String url = String.valueOf(connectionMap.get("url"));
			String driver = String.valueOf(connectionMap.get("driver"));
			String sql = ruleExprsnValue;

			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement query = conn.createStatement();
			ResultSet results = query.executeQuery(sql);

			while (results.next()) {
				dataMap = new SangsMap();
				dataMap.putOrg(colNm, results.getString(colNm));
				rtnList.add(dataMap);
			}
			query.close();
			results.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnList;
	}
	
	/**
	 * 사용자 정의 SQL 불일치 목록
	 * @param ruleExprsnValue
	 * @param colNm
	 * @param tblNm
	 * @return
	 */
	public List<SangsMap> selectDgnssUserDfnSqlDisMtchList(String ruleExprsnValue, String colNm, String tblNm) {
		
		List<SangsMap> rtnList = new ArrayList<SangsMap>();
		SangsMap dataMap = new SangsMap();
		try {

			String url = String.valueOf(connectionMap.get("url"));
			String driver = String.valueOf(connectionMap.get("driver"));
			String sql = "select "+colNm+" from "+tblNm+" where "+colNm+" not in ("+ruleExprsnValue+")";
					
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement query = conn.createStatement();
			ResultSet results = query.executeQuery(sql);

			while (results.next()) {
				dataMap = new SangsMap();
				dataMap.putOrg(colNm, results.getString(colNm));
				rtnList.add(dataMap);
			}
			query.close();
			results.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnList;
	}
	
	/**
	 * 숫자 일치 범주
	 * @param colNm
	 * @param tblNm
	 * @param bgngValue
	 * @param endValue
	 * @return
	 */
	public List<SangsMap> selectDgnssNumberCtgryMtchList(String colNm, String tblNm, String bgngValue, String endValue) {
		
		List<SangsMap> rtnList = new ArrayList<SangsMap>();
		SangsMap dataMap = new SangsMap();
		try {

			String url = String.valueOf(connectionMap.get("url"));
			String driver = String.valueOf(connectionMap.get("driver"));
			String sql = "SELECT " + colNm + " FROM "+tblNm+" WHERE "+colNm+" BETWEEN "+bgngValue+" AND "+endValue+" AND "+colNm+" IS NOT NULL";
					
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement query = conn.createStatement();
			ResultSet results = query.executeQuery(sql);

			while (results.next()) {
				dataMap = new SangsMap();
				dataMap.putOrg(colNm, results.getString(colNm));
				rtnList.add(dataMap);
			}
			query.close();
			results.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnList;
	}
	
	/**
	 * 숫자 불일치 범주
	 * @param colNm
	 * @param tblNm
	 * @param bgngValue
	 * @param endValue
	 * @return
	 */
	public List<SangsMap> selectDgnssNumberCtgryDisMtchList(String colNm, String tblNm, String bgngValue, String endValue) {
		
		
		List<SangsMap> rtnList = new ArrayList<SangsMap>();
		SangsMap dataMap = new SangsMap();
		try {
			String url = String.valueOf(connectionMap.get("url"));
			String driver = String.valueOf(connectionMap.get("driver"));
			String sql = "SELECT " + colNm + " FROM "+tblNm+" WHERE "+colNm+" NOT BETWEEN "+bgngValue+" AND "+endValue+"";
					
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement query = conn.createStatement();
			ResultSet results = query.executeQuery(sql);

			while (results.next()) {
				dataMap = new SangsMap();
				dataMap.putOrg(colNm, results.getString(colNm));
				rtnList.add(dataMap);
			}
			query.close();
			results.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnList;
	}
	
	/**
	 * 문자 일치 범주
	 * @param colNm
	 * @param tblNm
	 * @param bgngValue
	 * @param endValue
	 * @return
	 */
	public List<SangsMap> selectDgnssChrctrCtgryMtchList(String colNm, String tblNm, String bgngValue, String endValue) {
		
		
		List<SangsMap> rtnList = new ArrayList<SangsMap>();
		SangsMap dataMap = new SangsMap();
		try {

			String url = String.valueOf(connectionMap.get("url"));
			String driver = String.valueOf(connectionMap.get("driver"));
			String sql = "SELECT " + colNm + " FROM "+tblNm+" WHERE "+colNm+" BETWEEN '"+bgngValue+"' AND '"+endValue+"'";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement query = conn.createStatement();
			ResultSet results = query.executeQuery(sql);

			while (results.next()) {
				dataMap = new SangsMap();
				dataMap.putOrg(colNm, results.getString(colNm));
				rtnList.add(dataMap);
			}
			query.close();
			results.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnList;
	}
	
	/**
	 * 문자 불일치 범주
	 * @param colNm
	 * @param tblNm
	 * @param bgngValue
	 * @param endValue
	 * @return
	 */
	public List<SangsMap> selectDgnssChrctrCtgryDisMtchList(String colNm, String tblNm, String bgngValue, String endValue) {
		
		List<SangsMap> rtnList = new ArrayList<SangsMap>();
		SangsMap dataMap = new SangsMap();
		try {
			String url = String.valueOf(connectionMap.get("url"));
			String driver = String.valueOf(connectionMap.get("driver"));
			String sql = "SELECT " + colNm + " FROM "+tblNm+" WHERE "+colNm+" NOT BETWEEN '"+bgngValue+"' AND '"+endValue+"'";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement query = conn.createStatement();
			ResultSet results = query.executeQuery(sql);

			while (results.next()) {
				dataMap = new SangsMap();
				dataMap.putOrg(colNm, results.getString(colNm));
				rtnList.add(dataMap);
			}
			query.close();
			results.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnList;
	}
	
	/**
	 * 날짜 일치 범주
	 * @param colNm
	 * @param tblNm
	 * @param bgngValue
	 * @param endValue
	 * @return
	 */
	public List<SangsMap> selectDgnssDateCtgryMtchList(String colNm, String tblNm, String bgngValue, String endValue) {
		
		List<SangsMap> rtnList = new ArrayList<SangsMap>();
		SangsMap dataMap = new SangsMap();
		try {

			String url = String.valueOf(connectionMap.get("url"));
			String driver = String.valueOf(connectionMap.get("driver"));
			String sql = "SELECT " + colNm + " FROM "+tblNm+" WHERE "+colNm+" BETWEEN '"+bgngValue+" 00:00:00' AND '"+endValue+" 23:59:59'";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement query = conn.createStatement();
			ResultSet results = query.executeQuery(sql);

			while (results.next()) {
				dataMap = new SangsMap();
				dataMap.putOrg(colNm, results.getString(colNm));
				rtnList.add(dataMap);
			}
			query.close();
			results.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnList;
	}
	
	/**
	 * 날짜 불일치 범주
	 * @param colNm
	 * @param tblNm
	 * @param bgngValue
	 * @param endValue
	 * @return
	 */
	public List<SangsMap> selectDgnssDateCtgryDisMtchList(String colNm, String tblNm, String bgngValue, String endValue) {
		
		List<SangsMap> rtnList = new ArrayList<SangsMap>();
		SangsMap dataMap = new SangsMap();
		try {

			String url = String.valueOf(connectionMap.get("url"));
			String driver = String.valueOf(connectionMap.get("driver"));
			String sql = "SELECT " + colNm + " FROM "+tblNm+" WHERE "+colNm+" NOT BETWEEN '"+bgngValue+" 00:00:00' AND '"+endValue+" 23:59:59'";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement query = conn.createStatement();
			ResultSet results = query.executeQuery(sql);

			while (results.next()) {
				dataMap = new SangsMap();
				dataMap.putOrg(colNm, results.getString(colNm));
				rtnList.add(dataMap);
			}
			query.close();
			results.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnList;
	}
	
	
	/**
	 * 테이블 row 데이터 목록 조회
	 * @param tableName
	 * @param colInfoList
	 * @param rowCnt
	 * @return
	 */
	public List<SangsMap> selectTableRowList(String colNm, String tblNm, String nullDataDgnssYn) {

		List<SangsMap> rtnList = new ArrayList<SangsMap>();
		try {

			
			String url = String.valueOf(connectionMap.get("url"));
			String driver = String.valueOf(connectionMap.get("driver"));
			String whereIsNotNull = ""; 
			if("N".equals(nullDataDgnssYn)) {
				whereIsNotNull = " WHERE " + colNm + " IS NOT NULL";
			} 
			
			String sql = "SELECT " + colNm + " FROM " + tblNm + whereIsNotNull;

			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement query = conn.createStatement();
			ResultSet results = query.executeQuery(sql);
			while (results.next()) {
				SangsMap dataMap = new SangsMap();
				dataMap.put(colNm, results.getString(colNm));
				rtnList.add(dataMap);
			}
			query.close();
			results.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnList;

	}
}
