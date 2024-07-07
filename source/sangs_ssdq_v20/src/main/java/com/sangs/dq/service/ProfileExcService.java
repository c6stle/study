package com.sangs.dq.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.common.support.AuthUtil;
import com.sangs.common.support.BizUtil;
import com.sangs.common.support.BizUtil.DBMS_TYPE_NAME;
import com.sangs.common.support.CommonDao;
import com.sangs.dq.config.AnalsSqlSessionTemplate;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.fwk.support.SangsAuthUtil;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;

@SangsService
public class ProfileExcService {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CommonDao dao;
	
	@Autowired
	private VoltDbService voltDbService;
	
	@Autowired
	private MongoDbService mongoDbService;
	
	@Autowired
	private ProfileExcThreadService profileExcThreadService;
	
	private AnalsSqlSessionTemplate sqlSession = new AnalsSqlSessionTemplate();
	
	
	/**
	 * 프로파일 실행 요청
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> excProfileDgnssRqst(Map<String, Object> params) throws Exception {
		logger.debug("params : " + params);

		Map<String, Object> rtnMap = new HashMap<String, Object>();
		SangsMap dgnssExcSttusCdMap = new SangsMap();

		boolean isCmptn = false;
		String errMessage = "";
		try {
			// 진단 프로파일 정보 조회
			SangsMap dgnssProfileInfo = dao.selectOne("dq_profile.selectProfileInfo", params);
			
			String dbmsNm = String.valueOf(dgnssProfileInfo.get("dbmsNm"));

			// 진단 테이블 목록 조회
			List<SangsMap> dgnssTblList = dao.selectList("dq_profile.selectDiagnosisTableList", params);

			// 진단 대상 컬럼 목록 조회
			List<SangsMap> dgnssTrgtColList = dao.selectList("dq_profile.selectDgnssTrgtColList", params);

			// 진단 수행 순번 채번
			int nextExcSn = dao.selectInteger("dq_profile_exc.selectNextExcSn", params);
			dgnssExcSttusCdMap.putOrg("excSn", nextExcSn);

			// 접속 데이터베이스 정보 조회
			params.put("prjctSn", dgnssProfileInfo.get("prjctSn"));
			params.put("dbmsSn", dgnssProfileInfo.get("dbmsSn"));
			params.put("excSn", nextExcSn);

			SangsMap prjctDbmsInfo = dao.selectOne("cmmn_project.selectProjectDbmsCnncInfo", params);

			SangsMap proflExcMap = new SangsMap();
			proflExcMap.putOrg("excSn", nextExcSn);										// 수행_순번
			proflExcMap.putOrg("proflSn", params.get("proflSn"));						// 프로파일_순번
			proflExcMap.putOrg("excSchdulDt", dgnssProfileInfo.get("nextExcSchdulDt")); // 수행_일정_일시
			proflExcMap.putOrg("excSttusCd", "R");										// 수행_상태_코드
			proflExcMap.putOrg("dbmsIpAddr", prjctDbmsInfo.get("dbmsIpAddr"));			// DBMS_아이피_주소
			proflExcMap.putOrg("dbmsPortNo", prjctDbmsInfo.get("dbmsPortNo"));			// DBMS_포트_번호
			proflExcMap.putOrg("dbmsSchemaNm", prjctDbmsInfo.get("dbmsSchemaNm"));		// DBMS_스키마_명
			proflExcMap.putOrg("dbmsSidNm", prjctDbmsInfo.get("dbmsSidNm"));			// DBMS_SID_명
			proflExcMap.putOrg("dbmsDatabaseNm", prjctDbmsInfo.get("dbmsDatabaseNm"));	// DBMS_데이터베이스_명
			proflExcMap.putOrg("dbmsId", prjctDbmsInfo.get("dbmsId"));					// DBMS_아이디
			proflExcMap.putOrg("dbmsPassword", prjctDbmsInfo.get("dbmsPassword"));		// DBMS_비밀번호
			// 파람 없으면 AuthUtil.getUserId() 넣어주면 넣어준값 api
			
			String regUserId = "";
			if(params.containsKey("regUserId")) {
				regUserId = String.valueOf(params.get("regUserId"));
			} else {
				regUserId = AuthUtil.getUserId();
			}
			
			proflExcMap.putOrg("regUserId", regUserId);
			proflExcMap.putOrg("dbmsNm", dbmsNm);// 등록_사용자_아이디
			
			// 진단 수행 정보 등록
			dao.insert("dq_profile_exc.insertDgnssExcInfo", proflExcMap);
			
			for (SangsMap dgnssTblMap : dgnssTblList) {

				String dbmsDatabaseNm = prjctDbmsInfo.getString("dbmsDatabaseNm");
				String dbmsSchemaNm = prjctDbmsInfo.getString("dbmsSchemaNm");
				if (!"".equals(dbmsSchemaNm)) {
					dbmsDatabaseNm = dbmsSchemaNm;
				}

				SangsMap map = new SangsMap();
				map.putOrg("dbmsDatabaseNm", dbmsDatabaseNm);
				map.putOrg("dbmsTableNm", dgnssTblMap.getString("tblNm"));
				map.putOrg("dbmsNm", dbmsNm);
				
				
				if(params.containsKey("THREAD_CONN_YN")) {
					map.putOrg("THREAD_CONN_YN", params.get("THREAD_CONN_YN"));
					map.putOrg("THREAD_DBMS_INFO", params.get("THREAD_DBMS_INFO"));
					rtnMap.put("excSn", nextExcSn);
					rtnMap.put("proflNm", dgnssProfileInfo.get("proflNm"));
					rtnMap.put("proflSn", dgnssProfileInfo.get("proflSn"));
					
				} else {
					map.putOrg("THREAD_CONN_YN", "Y");
					map.putOrg("THREAD_DBMS_INFO", SangsAuthUtil.getUserAuthVo().getUserAttrMap());
					params.put("THREAD_DBMS_INFO", SangsAuthUtil.getUserAuthVo().getUserAttrMap());
				}
				int tblDataCnt = 0;
				
				if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.VOLTDB, dbmsNm)) {
					tblDataCnt = voltDbService.selectTableRowDataCnt(map.getString("dbmsTableNm"));
				} else if (BizUtil.isEqualDbms(DBMS_TYPE_NAME.MONGODB, dbmsNm)) {
					tblDataCnt = mongoDbService.selectTableRowDataCnt(map.getString("dbmsTableNm"));
				} else {
					tblDataCnt = sqlSession.selectInteger("AnalysisMapper.selectTableRowDataCnt", map);
				}

				SangsMap dgnssResultTblMap = new SangsMap();
				dgnssResultTblMap.putOrg("excSn", nextExcSn);
				dgnssResultTblMap.putOrg("tblSn", dgnssTblMap.getInt("tblSn"));
				dgnssResultTblMap.putOrg("tblNm", dgnssTblMap.getString("tblNm"));
				dgnssResultTblMap.putOrg("schemaNm", dbmsDatabaseNm);
				dgnssResultTblMap.putOrg("totDataCnt", tblDataCnt);
				// 진단 결과 테이블 정보 등록
				dao.insert("dq_profile_exc.insertDgnssResultTblInfo", dgnssResultTblMap);
			}

			for (SangsMap dgnssTrgtColMap : dgnssTrgtColList) {
				dgnssTrgtColMap.putOrg("excSn", nextExcSn);
				// 진단 결과 컬럼 정보 등록
				dao.insert("dq_profile_exc.insertDgnssResultColInfo", dgnssTrgtColMap);
			}

			isCmptn = true;
			if (isCmptn) {
				dgnssExcSttusCdMap.putOrg("excSttusCd", "I");
				chgDgnssExcSttusCd(dgnssExcSttusCdMap);
				// 쓰레드 분석 시작 I
				profileExcThreadService.excProfileDgnssProc(params, dbmsNm);
			}

		} catch (Exception e) {
			isCmptn = false;
			errMessage = e.toString();
			e.printStackTrace();
			throw new SangsMessageException("즉시 실행 오류");
		} finally {
			if (!isCmptn) {
				dgnssExcSttusCdMap.putOrg("excErrCn", errMessage);
				dgnssExcSttusCdMap.putOrg("excSttusCd", "F");
				chgDgnssExcSttusCd(dgnssExcSttusCdMap);
			}
			
			if(params.containsKey("THREAD_CONN_YN")) {
				rtnMap.put("resultMsg", errMessage);
			}
			
			rtnMap.put("resultCd", isCmptn ? "OK" : "NO");
			
		}
		return rtnMap;
	}
	
	/**
	 * 진단 수행 상태 코드 변경
	 * @param params
	 * @throws Exception
	 */
	public void chgDgnssExcSttusCd(SangsMap params) throws Exception {
		logger.debug("params : " + params);
		try {
			// R 분석대기 (시작 전) , I 분석중 (시작 후), E 종료 , F 실패
			dao.update("dq_profile_exc.updateDgnssExcSttusCd", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
