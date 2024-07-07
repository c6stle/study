package com.sangs.dq.mapper;

import java.util.List;
import java.util.Map;

public interface LifecycleMapper {
	
	// Lifecycle 항목관리 전체 row 수 조회
	int selectLifecycleMngTotalCnt(Map<String, Object> params);

	// Lifecycle 항목관리 목록 조회
	List<Map<String, Object>> selectLifecycleMngList(Map<String, Object> params);

	// Lifecycle 항목관리 목록 상세 조회
	Map<String, Object> selectLifecycleMngInfo(Map<String, Object> params);

	// Lifecycle 항목관리 목록 등록
	public int insertLifecycleMngInfo(Map<String, Object> params);

	// Lifecycle 항목관리 목록 수정
	public int updateLifecycleMngInfo(Map<String, Object> params);

	// Lifecycle 항목관리 규칙 아이디 생성
	public int selectNextAnlsSn(Map<String, Object> params);
	
	// Lifecycle 진단 정보 등록
	public int insertLifecycleDiagnosisInfo(Map<String, Object> params);
	
	// Lifecycle 진단 정보 수정
	public int updateLifecycleDiagnosisInfo(Map<String, Object> params);

	// Lifecycle 테이블별 진단 항목별 진단 정보 등록
	public int insertLifecycleDiagnosisTableInfo(Map<String, Object> params);

	// Lifecycle 테이블별 진단 항목별 진단 에러정보 등록
	public int insertLifecycleDiagnosisTableErrInfo(Map<String, Object> params);
	
	// Lifecycle 진단 결과 전체 row 수 조회
	int selectLifecycleDiagnosisResultTotalCnt(Map<String, Object> params);

	// Lifecycle 진단 결과 목록 조회
	List<Map<String, Object>> selectLifecycleDiagnosisResultList(Map<String, Object> params);

	// Lifecycle 진단 결과 목록 상세 조회
	Map<String, Object> selectLifecycleDiagnosisResultInfo(Map<String, Object> params);

	// Lifecycle 진단 결과 목록 상세 테이블 목록 조회
	List<Map<String, Object>> selectLifecycleDiagnosisResultTableList(Map<String, Object> params);

	// Lifecycle 항목관리 전체 목록 조회
	List<Map<String, Object>> selectLifecycleMngAllList(Map<String, Object> params);

	// Lifecycle 진단 결과 오류 목록 상세 테이블 목록 조회
	List<Map<String, Object>> selectLifecycleDiagnosisResultErrTableList(Map<String, Object> params);


}
