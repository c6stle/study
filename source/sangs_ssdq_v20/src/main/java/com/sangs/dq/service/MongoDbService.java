package com.sangs.dq.service;

//import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sangs.common.support.BizUtil.DbmsDataTypeGroup;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.lib.support.domain.SangsMap;

@SangsService
public class MongoDbService {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	/*
	public static Map<String, MongoDatabase> connectionMap = new HashMap<String, MongoDatabase>();
	public static Map<String, Object> dbmsCnncSnMap = new HashMap<String, Object>();
*/
	//public MongoDatabase getConnection(String url, Map<String, Object> paramMap) throws Exception {
	public Object getConnection(String url, Map<String, Object> paramMap) throws Exception {
		/*
		String dbmsCnncSn = String.valueOf(paramMap.get("dbmsCnncSn"));
		String databaseNm = String.valueOf(paramMap.get("dbmsDatabaseNm")); 
		
		if(paramMap.isEmpty()) {
			dbmsCnncSn = AuthUtil.getDbmsCnncSnStr();
			databaseNm = AuthUtil.getDbmsDatabaseNm();
		}

		MongoClient mongoClient = MongoClients.create(url);

		MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseNm);
		connectionMap.put(dbmsCnncSn, mongoDatabase);
		dbmsCnncSnMap.put("dbmsCnncSn", dbmsCnncSn);
		
		return mongoDatabase;
		*/
		return null;
	}
	
	public int getConnectTest(String dbmsUrl, Map<String, Object> paramMap) throws Exception {
		int resCnt = 0;
		/*
		MongoDatabase mongoDatabase = getConnection(dbmsUrl, paramMap);
		List<String> collectionList = new ArrayList<>();
		for (String name : mongoDatabase.listCollectionNames()) {
			collectionList.add(name);
		}
		resCnt = collectionList.size();
		*/
		return resCnt;
	}
	
	
	
	
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
	
	
	// 테이블목록 정보 조회
	public List<Map<String, Object>> selectAnalysisTableList() throws Exception {
//		String dbmsCnncSn = String.valueOf(dbmsCnncSnMap.get("dbmsCnncSn"));
//		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
//		
//		Map<String, Object> map = new HashMap<String, Object>();
//		MongoDatabase mongoDatabase = connectionMap.get(dbmsCnncSn);
//		MongoCollection<Document> collection = null;
//		try {
//			
//			//List<String> countcur = new ArrayList<>();
//			for (String tableName : mongoDatabase.listCollectionNames()) {
//				map = new HashMap<String, Object>();
//				collection = mongoDatabase.getCollection(tableName);
//				
//				map.put("dbmsTableNm", tableName);
//				map.put("numRows", collection.countDocuments());
//				map.put("indexCnt", "");
//				map.put("avgRowLen", "");
//				map.put("pkCnt", "");
//				map.put("comments", "");
//				
//				dataList.add(map);
//				/*
//				 * [{"_id": {"$oid": "62e8c37c2c3f9e44a3ba86c4"}, "name": "user01", "reg_dt": {"$date": 1648825200000}, "chg_dt": {"$date": 1659366000000}}]
//				 * for (Document doc : collection.find()) {
//				 *	countcur.add(doc.toJson());
//				 * }
//				 */			
//			}
//
//		} catch (Exception e) {
//			throw e;
//		}
//
//		return dataList;
		return null;
	}
	
	
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
	 * 컬럼 목록 정보 조회
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectAnalysisTableColumnList() throws Exception {
		List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();
		try {
//			String dbmsCnncSn = String.valueOf(dbmsCnncSnMap.get("dbmsCnncSn"));
//			MongoDatabase mongoDatabase = connectionMap.get(dbmsCnncSn);
//			
//			for (String tableName : mongoDatabase.listCollectionNames()) {
//				List<Map<String, Object>> tableList = new ArrayList<>();
//				Map<String, Object> dataMap = new HashMap<String, Object>();
//				MongoCollection<Document> collection = mongoDatabase.getCollection(tableName);
//				for (Document doc : collection.find()) {
//					tableList.add(doc);
//				}
//				
//				for (Map<String,Object> map : tableList) {
//					Iterator<String> it = map.keySet().iterator();
//					while (it.hasNext()) {
//
//						String key = it.next();
//
//						dataMap.put(key, key);
//					}
//				}
//				Iterator<String> it = dataMap.keySet().iterator();
//				while (it.hasNext()) {
//					Map<String, Object> columnMap = new HashMap<String, Object>();
//					String key = it.next();
//					
//					if("_id".equals(key)) {
//						key = key + " ("+tableName+")";
//					}
//					columnMap.put("columnName", key);
//					columnMap.put("dataType", "VARCHAR");
//					columnMap.put("dataLength","");
//					columnMap.put("dataPrecision", "");
//					columnMap.put("dataScale", "");
//					columnMap.put("dataDefault", "");
//					columnMap.put("nullable", "");
//					columnMap.put("comments", "");
//					columnMap.put("tableName", tableName);
//					columnMap.put("dbmsTableNm", tableName);
//					columnMap.put("columnComment", "");
//					columnMap.put("dataTypeLength", "VARCHAR");
//					rtnList.add(columnMap);
//				}
//			}
			
		} catch (Exception e) {
			throw e;
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
		/*
		String dbmsCnncSn = String.valueOf(dbmsCnncSnMap.get("dbmsCnncSn"));
		MongoDatabase mongoDatabase = connectionMap.get(dbmsCnncSn);
		
		MongoCollection<Document> collection = mongoDatabase.getCollection(tableName);

		List<Map<String, Object>> columnList = new ArrayList<>();
		Map<String, String> columnMap = new HashMap<String, String>();
		List<Map<String, Object>> diagnosisColumnList = new ArrayList<Map<String, Object>>();

		
		for (Document doc : collection.find()) {
			columnList.add(doc);
		}

		for (Map<String, Object> map : columnList) {
			Iterator<String> it = map.keySet().iterator();
			while (it.hasNext()) {

				String key = it.next();
				if(!"_id".equals(key)) {
					columnMap.put(key, key);
				}
			}
		}

		Iterator<String> it = columnMap.keySet().iterator();
		while (it.hasNext()) {
			Map<String, Object> dataMap = new HashMap<>();
			String key = it.next();
			
			dataMap.put("columnName", key);
			dataMap.put("dataType", "VARCHAR");

			diagnosisColumnList.add(dataMap);
		}

		return diagnosisColumnList;
		*/
		return null;
	}
	
	/**
	 * 샘플 데이터 조회
	 * @param tableName
	 * @param dbmsCnncSn
	 * @param rowCount
	 * @return
	 */
	public List<Map<String, Object>> selectTableRowDataList(String tableName, int rowCnt) {
		/*
		String dbmsCnncSn = String.valueOf(dbmsCnncSnMap.get("dbmsCnncSn"));
		MongoDatabase mongoDatabase = connectionMap.get(dbmsCnncSn);

		MongoCollection<Document> collection = mongoDatabase.getCollection(tableName);

		List<Map<String, Object>> dataList = new ArrayList<>();
		List<Map<String, Object>> diagnosisDataList = new ArrayList<Map<String, Object>>();
		
		for (Document doc : collection.find().limit(rowCnt)) {
			dataList.add(doc);
		}
		
		for (Map<String, Object> map : dataList) {
			Iterator<String> it = map.keySet().iterator();
			SangsMap dataMap = new SangsMap();
			while (it.hasNext()) {

				String key = it.next();
				String value = map.get(key).toString();

				if(!"_id".equals(key)) {
					dataMap.put(key, value);
				}

			}
			diagnosisDataList.add(dataMap);
		}

		return diagnosisDataList;
		*/
		return null;
	}
	
	
	/**
	 * Unique Count
	 * @param tblNm
	 * @param colNm
	 * @return
	 */
	public int selectDgnssUniqueCnt(String tblNm, String colNm) {
		/*
		String dbmsCnncSn = String.valueOf(dbmsCnncSnMap.get("dbmsCnncSn"));
		MongoDatabase mongoDatabase = connectionMap.get(dbmsCnncSn);
		MongoCollection<Document> collection = mongoDatabase.getCollection(tblNm);
		int count = 0;
		AggregateIterable<Document> output = collection.aggregate(Arrays.asList(
				new Document("$group", new Document("_id", "$" + colNm).append("count", new Document("$sum", 1))),
				new Document("$match", new Document("count", new Document("$eq", 1)))));

		for (Document dbObject : output) {
			count++;
		}

		return count;
		*/
		return 0;
	}
	/**
	 * Duplicate Count
	 * @param totDataCnt
	 * @param tblNm
	 * @param colNm
	 * @return
	 */
	public int selectDgnssDuplicateCnt(int totDataCnt, String tblNm, String colNm) {
		int count = 0;
		int uniqueCount = selectDgnssUniqueCnt(tblNm, colNm);
		
		count = totDataCnt - uniqueCount; 
//		
//		MongoDatabase mongoDatabase = connectionMap.get(dbmsCnncSn);
//		MongoCollection<Document> collection = mongoDatabase.getCollection(tblNm);
//		
//		AggregateIterable<Document> output = collection.aggregate(Arrays.asList(
//				new Document("$group", new Document("_id", "$" + colNm).append("count", new Document("$sum", 1))),
//				new Document("$match", new Document("count", new Document("$gt", 1)))));
//
//		for (Document dbObject : output) {
//			count++;
//			//count = (int) dbObject.get("count");
//		}

		return count;

	}
	
	/**
	 * Distinct Count
	 * @param tblNm
	 * @param colNm
	 * @return
	 */
	public int selectDgnssDistinctCnt(String tblNm, String colNm) {
		/*
		String dbmsCnncSn = String.valueOf(dbmsCnncSnMap.get("dbmsCnncSn"));
		MongoDatabase mongoDatabase = connectionMap.get(dbmsCnncSn);
		MongoCollection<Document> collection = mongoDatabase.getCollection(tblNm);
		int count = 0;

		if (!colNm.equals("_id")) {
			MongoCursor<String> files = collection.distinct(colNm, String.class).iterator();
			while (files.hasNext()) {
				files.next();
				count++;
			}
		}

		return count;
		*/
		return 0;

	}

	/**
	 * NULL Count
	 * @param tblNm
	 * @param colNm
	 * @return
	 */
	public int selectDgnssNullCnt(String tblNm, String colNm) {
		/*
		String dbmsCnncSn = String.valueOf(dbmsCnncSnMap.get("dbmsCnncSn"));
		MongoDatabase mongoDatabase = connectionMap.get(dbmsCnncSn);
		MongoCollection<Document> collection =  mongoDatabase.getCollection(tblNm);
		int count = 0;
		FindIterable<Document> findIterable = collection.find(eq(colNm, null));

		for (Document doc : findIterable) {
			count++;
		}
		return count;
		*/
		return 0;
		
	}

	/**
	 * Blank Count
	 * @param tblNm
	 * @param colNm
	 * @return
	 */
	public int selectDgnssBlankCnt(String tblNm, String colNm) {

		int count = 0;
		/*
		String dbmsCnncSn = String.valueOf(dbmsCnncSnMap.get("dbmsCnncSn"));
		MongoDatabase mongoDatabase = connectionMap.get(dbmsCnncSn);

		MongoCollection<Document> collection = mongoDatabase.getCollection(tblNm);

		if (!colNm.equals("_id")) {
			// db.test_tb.aggregate([{$project:{usernm:{$trim:{ input: "$usernm" }}}}]);
			AggregateIterable<Document> output = collection.aggregate(Arrays.asList(new Document("$project",
					new Document(colNm, new Document("$trim", new Document("input", "$" + colNm))))));

			for (Document dbObject : output) {
				if (dbObject.get(colNm) != null) {
					if (dbObject.get(colNm).equals("") || dbObject.get(colNm) == "") {
						count++;
					}
				}
			}

		}
		*/
		return count;
	}
	
	/**
	 * Value Frequency
	 * @param tblNm
	 * @param colNm
	 * @return
	 */
	public List<SangsMap> selectDgnssFqResultList(String tblNm, String colNm) {
		/*

		String dbmsCnncSn = String.valueOf(dbmsCnncSnMap.get("dbmsCnncSn"));
		MongoDatabase mongoDatabase = connectionMap.get(dbmsCnncSn);

		
		List<SangsMap> frqList = new ArrayList<SangsMap>();
		List<Map<String, Object>> dataList = new ArrayList<>();

		MongoCollection<Document> collection = mongoDatabase.getCollection(tblNm);

		int i = 1;
		AggregateIterable<Document> output = collection.aggregate(Arrays.asList(
				new Document("$group", new Document("_id", "$" + colNm).append("dataCnt", new Document("$sum", 1))),
				new Document("$limit", 10)));

		for (Document doc : output) {
			dataList.add(doc);
		}
		for (Map<String, Object> map : dataList) {
			Iterator<String> it = map.keySet().iterator();
			SangsMap dataMap = new SangsMap();
			while (it.hasNext()) {
				String key = it.next();
				String value = map.get(key).toString();

				if ("_id".equals(key)) {
					dataMap.putOrg("dataValue", value);
				} else {
					dataMap.putOrg(key, value);
				}

			}
			frqList.add(dataMap);
		}
		return frqList;
		*/
		return null;
	}
	
	/**
	 * 테이블 row count 조회
	 * @param tblNm
	 * @return
	 */
	public int selectTableRowDataCnt(String tblNm) {
		/*
		String dbmsCnncSn = String.valueOf(dbmsCnncSnMap.get("dbmsCnncSn"));
		MongoDatabase mongoDatabase = connectionMap.get(dbmsCnncSn);
		MongoCollection<Document> collection = mongoDatabase.getCollection(tblNm);

		List<Map<String, Object>> list = new ArrayList<>();

		for (Document doc : collection.find()) {
			list.add(doc);
		}
		int count = list.size();
		return count;
		*/
		return 0;
	}

	/**
	 * 테이블 row 데이터 목록 조회
	 * @param tblNm
	 * @param colNm
	 * @return
	 */
	public List<SangsMap> selectTableRowList(String tblNm, String colNm) {
		/*
		String dbmsCnncSn = String.valueOf(dbmsCnncSnMap.get("dbmsCnncSn"));
		MongoDatabase mongoDatabase = connectionMap.get(dbmsCnncSn);

		List<Map<String, Object>> dataList = new ArrayList<>();

		MongoCollection<Document> collection = mongoDatabase.getCollection(tblNm);
		AggregateIterable<Document> output = collection
				.aggregate(Arrays.asList(new Document("$project", new Document(colNm, "$" + colNm))));
		List<SangsMap> list = new ArrayList<SangsMap>();
		try {
			for (Document doc : output) {
				dataList.add(doc);
			}
			
			for(Map<String,Object> map : dataList) {
				SangsMap dataMap = new SangsMap();
				for(String key : map.keySet()) {
					if(!"_id".equals(key)) {
						dataMap.put(key, map.get(key));
					}
				}
				list.add(dataMap);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
		*/
		return null;
	}

}
