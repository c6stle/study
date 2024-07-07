package com.sangs.dq.mapper;

import java.util.List;
import java.util.Map;

public interface CsvInfoMapper {

	public int createCsvTable(Map<String, Object> params);

	public int dropCsvTable(Map<String, Object> params);

	public int mysqlimportFile(Map<String, Object> params);

	
	// 테이블 구조 분석 목록 조회	
	public List<Map<String, Object>> selectAnalysisTableList(Map<String, Object> params);
	// 테이블 row 데이터 count 조회
	public int selectTableRowDataCnt(Map<String, Object> prjctInfo);
	// 컬럼 구조 분석 목록 조회
	public List<Map<String, Object>> selectAnalysisTableColumnList(Map<String, Object> params);

	public int selectUserCheckSql(Map<String, Object> params);


}
