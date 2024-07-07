package com.sangs.dq.mapper;

import java.util.List;
import java.util.Map;

public interface RuleMngMapper {
	
	// 패턴/지표 관리 전체 row 수 조회
	int selectPatternTotalCnt(Map<String, Object> params);

	// 패턴/지표 관리 목록 조회
	List<Map<String, Object>> selectPatternList(Map<String, Object> params);

	// 패턴/지표 관리 목록 상세 조회
	public Map<String, Object> selectPatternInfo(Map<String, Object> params);

	// 패턴/지표 관리 목록 등록
	public int insertPatternInfo(Map<String, Object> params);

	// 패턴/지표 관리 목록 수정
	public int updatePatternInfo(Map<String, Object> params);

	// 패턴/지표 관리 목록 사용 여부 수정
	public int updatePatternUseYnInfo(Map<String, Object> params);

	// 패턴/지표 관리 목록 등록 규칙 아이디 생성
	public int selectNextRuleSn(Map<String, Object> params);

}
