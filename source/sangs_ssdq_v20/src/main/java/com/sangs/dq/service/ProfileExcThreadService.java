package com.sangs.dq.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import com.sangs.common.support.BizUtil;
import com.sangs.common.support.BizUtil.DBMS_TYPE_NAME;
import com.sangs.common.support.CommonDao;
import com.sangs.dq.config.AnalsSqlSessionTemplate;
import com.sangs.dq.mapper.RuleMngMapper;
import com.sangs.dq.util.HttpClientUtil;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.utils.SangsStringUtil;

@EnableAsync
@SangsService
public class ProfileExcThreadService {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${analysis.dq_complete.url:}")
	private String requestURL;
	
	@Value("${analysis.limit-mismatch.cnt:100}")
	private int mismatchCnt;
	
	@Autowired
	private RuleMngMapper ruleMngMapper;
	
	@Autowired
	private ProfileExcService profileExcService;
	
	@Autowired
	private VoltDbService voltDbService;
	
	@Autowired
	private MongoDbService mongoDbService;
	
	@Autowired
	private CommonDao dao;
	
	private AnalsSqlSessionTemplate sqlSession = new AnalsSqlSessionTemplate();
	
	@Async
	public void excProfileDgnssProc(Map<String, Object> params, String dbmsNm) throws Exception {
		logger.debug("params : " + params);
		
		boolean isCmptn = false;
		String errMessage = "";
		
		SangsMap dgnssExcSttusCdMap = new SangsMap();
		dgnssExcSttusCdMap.putOrg("excSn", params.get("excSn"));
		
		// int totExcCnt = 0;
		// int errExcCnt = 0;
		try {
			//dao.setLogFlag(false);
			// 패턴/지표 관리 목록 조회 
			List<Map<String, Object>> patternList = ruleMngMapper.selectPatternList(params);
			SangsMap ruleSnList = new SangsMap();
			for (Map<String, Object> patternMap : patternList) {
				ruleSnList.put(String.valueOf(patternMap.get("ruleSn")), patternMap);
			}
			
			// 진단 수행 목록 조회
			List<SangsMap> dgnssExcList = dao.selectList("dq_profile_exc.selectDgnssExcList", params);
			// totExcCnt = dgnssExcList.size();
			List<SangsMap> disMatchList = new ArrayList<SangsMap>();
			
			for (SangsMap dgnssExcMap : dgnssExcList) {
				String dataCndValue = "";
				// api
				if (params.containsKey("dataCndList")) {
					List<Map<String, Object>> dataCndList = (List<Map<String, Object>>) params.get("dataCndList");

					for (Map<String,Object> dataCndMap : dataCndList) {
						String dataCndMapTblNm =  String.valueOf(dataCndMap.get("tblNm"));
						if (dgnssExcMap.getString("tblNm").equals(dataCndMapTblNm)) {
							dataCndValue = String.valueOf(dataCndMap.get("dataCndValue"));
						}
					}
				}
				dgnssExcMap.putOrg("dataCndValue", dataCndValue);
				
				String errNm =""; // 오류 명
				String errCn =""; // 오류 내용
				
				boolean isDgnssCmptn = false;
				boolean isDgnssFqCmptn = false;
				
				dgnssExcMap.putOrg("THREAD_CONN_YN", "Y");
				dgnssExcMap.putOrg("THREAD_DBMS_INFO", (Map<String, Object>)params.get("THREAD_DBMS_INFO"));
				
				try {
					List<SangsMap> pkColList = new ArrayList<SangsMap>();
					
					if(!BizUtil.isEqualDbms(DBMS_TYPE_NAME.MONGODB, dbmsNm) && !BizUtil.isEqualDbms(DBMS_TYPE_NAME.DB2, dbmsNm)
							&& !BizUtil.isEqualDbms(DBMS_TYPE_NAME.VOLTDB, dbmsNm) && !BizUtil.isEqualDbms(DBMS_TYPE_NAME.ALTIBASE, dbmsNm)
							&& !BizUtil.isEqualDbms(DBMS_TYPE_NAME.CSV, dbmsNm)) {
						pkColList = sqlSession.selectList("AnalysisMapper.sealectPkColList", dgnssExcMap);
					}
					
					String[] pkColNmArr = new String[pkColList.size()];
					if (!pkColList.isEmpty()) {
						for(int i = 0; i < pkColList.size(); i++) {
							pkColNmArr[i] = pkColList.get(i).getString("columnName");	
						}
					}
					dgnssExcMap.putOrg("pkColNmArr", pkColNmArr);
					
					int mtchCnt = 0;
					String ruleSn = dgnssExcMap.getString("ruleSn");
					List<SangsMap> dgnssFqList = new ArrayList<SangsMap>();
					List<SangsMap> disMtchList = new ArrayList<SangsMap>();
					List<SangsMap> mtchList = new ArrayList<SangsMap>();
					
					if (ruleSnList.containsKey(ruleSn)) {

						SangsMap ruleSnMap = (SangsMap) ruleSnList.get(ruleSn);
						
						
						if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.MONGODB, dbmsNm)) {
							SangsMap rtnMap = new SangsMap();
							rtnMap = excMongoDb(dgnssExcMap, ruleSnMap, dbmsNm, ruleSn, disMatchList);
							mtchCnt = rtnMap.getInt("mtchCnt");
							
							if("true".equals(rtnMap.getString("isDgnssFqCmptn"))) {								
								isDgnssFqCmptn = true;
								dgnssFqList = (List<SangsMap>) rtnMap.get("dgnssFqList");
							}
						} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.VOLTDB, dbmsNm)) {
							SangsMap rtnMap = new SangsMap();
							rtnMap = excVoltDb(dgnssExcMap, ruleSnMap, dbmsNm, ruleSn, disMatchList);
							mtchCnt = rtnMap.getInt("mtchCnt");
							
							if("true".equals(rtnMap.getString("isDgnssFqCmptn"))) {								
								isDgnssFqCmptn = true;
								dgnssFqList = (List<SangsMap>) rtnMap.get("dgnssFqList");
							}
						} else {
							if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CSV, dbmsNm)) {
								dgnssExcMap.putOrg("dbmsDatabaseNm", dgnssExcMap.get("schemaNm"));
								dgnssExcMap.putOrg("dbmsTableNm", dgnssExcMap.get("tblNm"));
								dgnssExcMap.putOrg("dbmsNm", dbmsNm);
								List<Map<String, Object>> colInfoList = sqlSession.selectList("AnalysisMapper.selectAnalysisTableColumnList", dgnssExcMap);
								
								String colNm = dgnssExcMap.getString("colNm");
								for(Map<String, Object> colInfo : colInfoList) {
									String comments = String.valueOf(colInfo.get("comments"));
									if(colNm.equals(comments)) {
										dgnssExcMap.putOrg("colNm", colInfo.get("columnName"));
										break;
									}
								}
								
								
							}
							String ruleTyCd = ruleSnMap.getString("ruleTyCd"); 				// 규칙 유형 코드
							String ruleSeCd = ruleSnMap.getString("ruleSeCd"); 				// 규칙 구분 코드
							String ruleExprsnValue = ruleSnMap.getString("ruleExprsnValue");// 규칙 표현 값
							String bgngValue = ruleSnMap.getString("bgngValue"); 			// 시작 값
							String endValue = ruleSnMap.getString("endValue");				// 종료 값
							int totDataCnt = dgnssExcMap.getInt("totDataCnt");				// 총 데이터 수
							
							String rulePostvExprsnYn = ruleSnMap.getString("rulePostvExprsnYn"); // 규칙 긍정 표현 여부
							
							if ("AT000100".equals(ruleTyCd)) { // SQL
								if ("AG000100".equals(ruleSeCd)) {
									if ("1".equals(ruleSn)) {
										// Data Count
										//mtchCnt = totDataCnt;
										mtchCnt = sqlSession.selectInteger("AnalysisMapper.selectDgnssDataRowCnt", dgnssExcMap);
									} else if ("2".equals(ruleSn)) {
										// Unique Count
										mtchCnt = sqlSession.selectInteger("AnalysisMapper.selectDgnssUniqueCnt", dgnssExcMap);
									} else if ("3".equals(ruleSn)) {
										// Duplicate Count
										mtchCnt = sqlSession.selectInteger("AnalysisMapper.selectDgnssDuplicateCnt", dgnssExcMap);
									} else if ("4".equals(ruleSn)) {
										// Distinct Count
										mtchCnt = sqlSession.selectInteger("AnalysisMapper.selectDgnssDistinctCnt", dgnssExcMap);
									} else if ("5".equals(ruleSn)) {
										// NULL Count
										mtchCnt = sqlSession.selectInteger("AnalysisMapper.selectDgnssNullCnt", dgnssExcMap);
									} else if ("6".equals(ruleSn)) {
										// Blank Count
										mtchCnt = sqlSession.selectInteger("AnalysisMapper.selectDgnssBlankCnt", dgnssExcMap);
									}
								} else if ("AG000200".equals(ruleSeCd)) {
									// 진단 빈도 결과 목록
									dgnssFqList = sqlSession.selectList("AnalysisMapper.selectDgnssFqResultList", dgnssExcMap);
									isDgnssFqCmptn = true;
	
								} else {
									
									if(ruleExprsnValue.indexOf("${columnName}") > -1) {
										String colNm = dgnssExcMap.getString("colNm");
										ruleExprsnValue = StringUtils.replace(ruleExprsnValue, "${columnName}", colNm);
									}
									dgnssExcMap.putOrg("ruleExprsnValue", ruleExprsnValue);
									// 사용자 정의 SQL
									mtchList = sqlSession.selectList("AnalysisMapper.selectDgnssUserDfnSqlMtchList", dgnssExcMap);
									disMtchList = sqlSession.selectList("AnalysisMapper.selectDgnssUserDfnSqlDisMtchList", dgnssExcMap);
									
									if("N".equals(rulePostvExprsnYn)) {
										mtchCnt = disMtchList.size();
										excProfileDgnssResultDisMtchList(mtchList, dgnssExcMap, disMatchList);
									} else {
										mtchCnt = mtchList.size();
										excProfileDgnssResultDisMtchList(disMtchList, dgnssExcMap, disMatchList);
									}
								}
								
							} else if("AT000300".equals(ruleTyCd)) { // 범주 AT000300
								dgnssExcMap.putOrg("bgngValue", bgngValue);
								dgnssExcMap.putOrg("endValue", endValue);
								if ("AG000601".equals(ruleSeCd)) {
									// 숫자 범주
									mtchList = sqlSession.selectList("AnalysisMapper.selectDgnssNumberCtgryMtchList", dgnssExcMap);
									disMtchList = sqlSession.selectList("AnalysisMapper.selectDgnssNumberCtgryDisMtchList", dgnssExcMap);
								} else if ("AG000602".equals(ruleSeCd)) {
									// 문자 범주
									mtchList = sqlSession.selectList("AnalysisMapper.selectDgnssChrctrCtgryMtchList", dgnssExcMap);
									disMtchList = sqlSession.selectList("AnalysisMapper.selectDgnssChrctrCtgryDisMtchList", dgnssExcMap);
								} else {
									// 날짜 범주
									mtchList = sqlSession.selectList("AnalysisMapper.selectDgnssDateCtgryMtchList", dgnssExcMap);
									disMtchList = sqlSession.selectList("AnalysisMapper.selectDgnssDateCtgryDisMtchList", dgnssExcMap);
								}
								dgnssExcMap.putOrg("missCnt", disMtchList.size());
								if("N".equals(rulePostvExprsnYn)) {
									mtchCnt = disMtchList.size();
									excProfileDgnssResultDisMtchList(mtchList, dgnssExcMap, disMatchList);
								} else {
									mtchCnt = mtchList.size();
									excProfileDgnssResultDisMtchList(disMtchList, dgnssExcMap, disMatchList);
								}
							} else if("AT000200".equals(ruleTyCd) || "AT000400".equals(ruleTyCd)) {
								mtchCnt = excProfileDgnssPttrnMtchCnt(totDataCnt, ruleExprsnValue, ruleTyCd, dgnssExcMap, rulePostvExprsnYn, disMatchList);
							} else {
								mtchCnt = 0;	
							}
						}
						
						if (!disMatchList.isEmpty()) {
							SangsMap disMatch = new SangsMap();
							disMatch.putOrg("disMatchList", disMatchList);
							dao.insert("dq_profile_exc.insertDgnssResultDisMtchInfo", disMatch);
							disMatchList.clear();
						}
						
					}
					isDgnssCmptn = true;
					if (isDgnssCmptn) {
						if (isDgnssFqCmptn) {
							for (int i = 0; i < dgnssFqList.size(); i++) {
								dgnssExcMap.putAll(dgnssFqList.get(i));
								dgnssExcMap.putOrg("fqSn", i + 1);
								// 진단 빈도 결과 등록
								dao.insert("dq_profile_exc.insertDgnssFqResultInfo", dgnssExcMap);
							}
						} else {
							if(!dgnssExcMap.containsKey("ruleExcResultCd")) {
								dgnssExcMap.putOrg("ruleExcResultCd", "S"); // 성공: S, 에러: E, 불일치 제한 수 초과: L
							}
							dgnssExcMap.putOrg("disMtchCnt", dgnssExcMap.getInt("missCnt"));
							dgnssExcMap.putOrg("mtchCnt", mtchCnt);
							// 진단 결과 컬럼 규칙 정보 등록
							dao.insert("dq_profile_exc.insertDgnssResultColRuleInfo", dgnssExcMap);
						}
					}
				} catch (Exception e) {
					errNm = e.getMessage();
					errCn = e.toString();
					e.printStackTrace();
					isDgnssCmptn = false;
					//errExcCnt++;
				} finally {
					if (!isDgnssCmptn) {
						dgnssExcMap.putOrg("errNm", errNm);
						dgnssExcMap.putOrg("errCn", errCn);
						saveDgnssResultErrInfo(dgnssExcMap);
						if (!isDgnssFqCmptn) {
							dgnssExcMap.putOrg("mtchCnt", "0");
							dgnssExcMap.putOrg("disMtchCnt", "0");
							dgnssExcMap.putOrg("ruleExcResultCd", "E");
							// 진단 결과 컬럼 규칙 정보 등록
							dao.insert("dq_profile_exc.insertDgnssResultColRuleInfo", dgnssExcMap);
						}
					}
				}
				// commit 처리
				dao.getSqlSessionTemplate().getConnection().commit();
			} // for
			
			isCmptn = true;
			if (isCmptn) {
				dgnssExcSttusCdMap.putOrg("excSttusCd", "E");
				profileExcService.chgDgnssExcSttusCd(dgnssExcSttusCdMap);
			}
			//dao.setLogFlag(true);
		} catch (Exception e) {
			isCmptn = false;
			errMessage = e.toString();
			e.printStackTrace();
		} finally {
			if (!isCmptn) {
			//if (!isCmptn || (totExcCnt == errExcCnt)) {
				dgnssExcSttusCdMap.putOrg("excErrCn", errMessage);
				dgnssExcSttusCdMap.putOrg("excSttusCd", "F");
				profileExcService.chgDgnssExcSttusCd(dgnssExcSttusCdMap);
			}
			
			if(isCmptn) {
				if(!StringUtils.isEmpty(requestURL)) {
					if(params.containsKey("isApiYn") && "Y".equals(String.valueOf(params.get("isApiYn")))) {
						HttpClientUtil hcu = new HttpClientUtil();
						requestURL = requestURL+"?excSn="+params.get("excSn");
						hcu.get(requestURL);
					}
				}
			}
		}
	}
	/**
	 * 프로파일 진단 패턴 일치 수
	 * @param totDataCnt
	 * @param ruleExprsnValue
	 * @param ruleTyCd
	 * @param dgnssExcMap
	 * @param disMatchList 
	 * @return
	 * @throws Exception
	 */
	public int excProfileDgnssPttrnMtchCnt(int totDataCnt, String ruleExprsnValue, String ruleTyCd, SangsMap dgnssExcMap, String rulePostvExprsnYn, List<SangsMap> disMatchList) throws Exception {
		List<SangsMap> colDataList = new ArrayList<SangsMap>();
		int scope = 100000; // 10만건씩 나눠서 실행
		int loopCnt = totDataCnt / scope; // 몫(수행 횟수)
		int remainder = totDataCnt % scope; // 나머지
		int mtchCnt = 0;

		if (remainder != 0) {
			loopCnt++;
		}
		int missCnt = 0;
		dgnssExcMap.putOrg("missCnt", missCnt);
		try {
			// 날짜 패턴 AT000400, java 정규식 패턴 AT000200
			if (loopCnt == 1) {
				// 컬럼 데이터 목록 조회
				colDataList = sqlSession.selectList("AnalysisMapper.selectDgnssColDataList", dgnssExcMap);
				if ("AT000400".equals(ruleTyCd)) { // 날짜 패턴
					mtchCnt = excProfileDgnssDatePttrnMtchCnt(ruleExprsnValue, colDataList, dgnssExcMap, rulePostvExprsnYn, disMatchList);
				} else {
					mtchCnt = excProfileDgnssJavaPttrnMtchCnt(ruleExprsnValue, colDataList, dgnssExcMap, rulePostvExprsnYn, disMatchList);
				}

			} else {
				
				for (int i = 0; i < loopCnt; i++) {
					int startRow = i * scope;
					int endRow = startRow + scope;

					// between startRow and endRow
					dgnssExcMap.putOrg("startRow", startRow + 1);
					dgnssExcMap.putOrg("endRow", endRow);
					dgnssExcMap.putOrg("scope", scope);

					// 컬럼 초과 데이터 목록 조회
					colDataList = sqlSession.selectList("AnalysisMapper.selectDgnssColExcessDataList", dgnssExcMap);

					if ("AT000400".equals(ruleTyCd)) { // 날짜
						mtchCnt += excProfileDgnssDatePttrnMtchCnt(ruleExprsnValue, colDataList, dgnssExcMap, rulePostvExprsnYn, disMatchList);
					} else {
						mtchCnt += excProfileDgnssJavaPttrnMtchCnt(ruleExprsnValue, colDataList, dgnssExcMap, rulePostvExprsnYn, disMatchList);
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return mtchCnt;
	}
	
	
	/**
	 * 프로파일 진단 java 패턴 일치 수
	 * @param ruleExprsnValue
	 * @param colDataList
	 * @param dgnssExcMap
	 * @param disMatchList 
	 * @return
	 * @throws Exception
	 */
	public int excProfileDgnssJavaPttrnMtchCnt(String ruleExprsnValue, List<SangsMap> colDataList, SangsMap dgnssExcMap, String rulePostvExprsnYn, List<SangsMap> disMatchList) throws Exception {

		int mtchCnt = 0;
		int missCnt = dgnssExcMap.getInt("missCnt");
		try {
			Pattern p = Pattern.compile(ruleExprsnValue);
			
			String colNm = dgnssExcMap.getString("colNm").replace("_", "");
			if(!"".equals(colNm)) {
				colNm = colNm.toUpperCase();
			}
			boolean isDisMtchLimitCntYn = false;
			
			for (SangsMap colDataInfo : colDataList) {
				
				Iterator<String> it = colDataInfo.keySet().iterator();
				while (it.hasNext()) {
					boolean isMissDataYn = false;
					StringBuffer strBuf = new StringBuffer("");
					String key = it.next();
					
					if (colNm.equals(key.toUpperCase())) {
						strBuf.append(colDataInfo.getString(key));
						Matcher m = p.matcher(String.valueOf(strBuf));
						if ("N".equals(rulePostvExprsnYn)) {
							if (!m.find()) {
								mtchCnt++;
							} else {
								missCnt++;
								isMissDataYn = true;
							}
						} else {
							if (m.find()) {
								mtchCnt++;
							} else {
								missCnt++;
								isMissDataYn = true;
							}
						}
						if(isMissDataYn) {
							if(!isDisMtchLimitCntYn) {
								if(mismatchCnt != 0 && mismatchCnt < missCnt) {
									isDisMtchLimitCntYn = true;
									dgnssExcMap.putOrg("ruleExcResultCd", "L");
								}
								if(mismatchCnt != 0 && !isDisMtchLimitCntYn) {
									excProfileDgnssResultDisMtchInfo(String.valueOf(strBuf), dgnssExcMap, colDataInfo, disMatchList);
								}
							}
						}
					}
				}
			}
			
			
			
			dgnssExcMap.putOrg("missCnt", missCnt);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return mtchCnt;
	}
	
	/**
	 * 프로파일 진단 불일치 데이터 목록 저장
	 * @param ctgryDisMtchList
	 * @param dgnssExcMap
	 * @throws Exception
	 */
	public void excProfileDgnssResultDisMtchList(List<SangsMap> disMtchList, SangsMap dgnssExcMap, List<SangsMap> disMatchList) throws Exception {
		try {
			int missCnt = 0;
			
			String colNm = dgnssExcMap.getString("colNm").replace("_", "");
			boolean isDisMtchLimitCntYn = false;
			for (SangsMap map : disMtchList) {
				Iterator<String> it = map.keySet().iterator();

				while (it.hasNext()) {
					StringBuffer strBuf = new StringBuffer("");
					String key = it.next();
					
					if (colNm.equals(key.toUpperCase())) {
						missCnt++;
						
						if(!isDisMtchLimitCntYn) {
							if(mismatchCnt != 0 && mismatchCnt < missCnt) {
								isDisMtchLimitCntYn = true;
								dgnssExcMap.putOrg("ruleExcResultCd", "L");
							}
							
							if(mismatchCnt != 0 && !isDisMtchLimitCntYn) {
								strBuf.append(map.getString(key));
								excProfileDgnssResultDisMtchInfo(String.valueOf(strBuf), dgnssExcMap, map, disMatchList);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 프로파일 진단 불일치 데이터 저장
	 * @param str
	 * @param dgnssExcMap
	 * @param disMatchList 
	 */
	public void excProfileDgnssResultDisMtchInfo(String str, SangsMap dgnssExcMap, SangsMap colDataInfo, List<SangsMap> disMatchList) {
		//logger.debug("params : " + dgnssExcMap);
		try {
			
			String[] pkColNmArr = (String[]) (dgnssExcMap.get("pkColNmArr"));
			String pkInfoValue = "";

			if (pkColNmArr.length != 0) {
				for (String key : colDataInfo.keySet()) {
					for (int i = 0; i < pkColNmArr.length; i++) {
						String pkColNm = pkColNmArr[i].replace("_", "");
						String keyUpper = key.toUpperCase();
						
						if (keyUpper.equals(pkColNm)) {
							pkInfoValue += pkColNmArr[i] + "=" + colDataInfo.get(key) + ",";
						}
					}

				}
				
				if(!SangsStringUtil.isEmpty(pkInfoValue))
					pkInfoValue = pkInfoValue.substring(0, pkInfoValue.length() - 1);
			}
			
			if(str.getBytes().length > 3900) {
				str = SangsStringUtil.substringByte(str, 3900);
			}
			
			SangsMap disMatchMap = new SangsMap();
			disMatchMap.putOrg("excSn", dgnssExcMap.get("excSn"));
			disMatchMap.putOrg("tblSn", dgnssExcMap.get("tblSn"));
			disMatchMap.putOrg("colSn", dgnssExcMap.get("colSn"));
			disMatchMap.putOrg("ruleSn", dgnssExcMap.get("ruleSn"));
			disMatchMap.putOrg("subSn", disMatchList.size() + 1);
			disMatchMap.putOrg("dataValue", str);
			disMatchMap.putOrg("pkInfoValue", pkInfoValue);
			disMatchList.add(disMatchMap);
			// dao.insert("dq_profile_exc.insertDgnssResultDisMtchInfo", dgnssExcMap);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * 프로파일 진단 날짜 패턴 일치 수
	 * @param ruleExprsnValue
	 * @param colDataList
	 * @param dgnssExcMap
	 * @return
	 * @throws Exception
	 */
	public int excProfileDgnssDatePttrnMtchCnt(String ruleExprsnValue, List<SangsMap> colDataList, SangsMap dgnssExcMap, String rulePostvExprsnYn, List<SangsMap> disMatchList) throws Exception {
		
		int mtchCnt = 0;
		int missCnt = dgnssExcMap.getInt("missCnt");
		try {
			
			String colNm = dgnssExcMap.getString("colNm").replace("_", "");
			if(!"".equals(colNm)) {
				colNm = colNm.toUpperCase();
			}
			
			boolean isDisMtchLimitCntYn = false;
			
			for (SangsMap colDataInfo : colDataList) {
				Iterator<String> it = colDataInfo.keySet().iterator();
				while (it.hasNext()) {
					boolean isMissDataYn = false;
					StringBuffer strBuf = new StringBuffer("");
					String key = it.next();
					
					if (colNm.equals(key.toUpperCase())) {
						
						strBuf.append(colDataInfo.getString(key));
						try {
							
							if("N".equals(rulePostvExprsnYn)) {
								if (!isDate(String.valueOf(strBuf), ruleExprsnValue)) {
									mtchCnt++;
								} else {
									missCnt++;
									isMissDataYn = true;
								}
							} else {
								if (isDate(String.valueOf(strBuf), ruleExprsnValue)) {
									mtchCnt++;
								} else {
									missCnt++;
									isMissDataYn = true;
								}
							}
							
							if(isMissDataYn) {
								if(!isDisMtchLimitCntYn) {
									if(mismatchCnt != 0 && mismatchCnt < missCnt) {
										isDisMtchLimitCntYn = true;
										dgnssExcMap.putOrg("ruleExcResultCd", "L");
									}
									if(mismatchCnt != 0 && !isDisMtchLimitCntYn) {
										excProfileDgnssResultDisMtchInfo(String.valueOf(strBuf), dgnssExcMap, colDataInfo, disMatchList);
									}
								}
							}
						}catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}
	
			}
			dgnssExcMap.putOrg("missCnt", missCnt);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return mtchCnt;
	}
	
	/**
	 * 날짜 유효성 체크(윤년도 가능)
	 * @param str
	 * @param format
	 * @return
	 */
	public boolean isDate(String str, String format) {
		boolean isValeLengthSame = true;
		if (str.length() != format.length()) {
			isValeLengthSame = false;
		}
		if (isValeLengthSame) {
			SimpleDateFormat format1 = new SimpleDateFormat(format, Locale.KOREAN);
			format1.setLenient(false);
			try {
				format1.parse(str);
				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}

	}
	
	
	/**
	 * 진단 결과 에러 정보 저장
	 * @param params
	 * @throws Exception
	 */
	public void saveDgnssResultErrInfo(SangsMap params) throws Exception {
		logger.debug("params : " + params);
		try {
			dao.update("dq_profile_exc.insertDgnssResultErrInfo", params);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * voltDb 프로파일 수행
	 * @param dgnssExcMap
	 * @param ruleSnMap
	 * @param dbmsNm
	 * @param ruleSn
	 * @return
	 * @throws Exception
	 */
	public SangsMap excVoltDb(SangsMap dgnssExcMap, SangsMap ruleSnMap, String dbmsNm, String ruleSn, List<SangsMap> disMatchList) throws Exception {
		SangsMap rtnMap = new SangsMap();
		List<SangsMap> dgnssFqList = new ArrayList<SangsMap>();
		List<SangsMap> disMtchList = new ArrayList<SangsMap>();
		List<SangsMap> mtchList = new ArrayList<SangsMap>();
		String ruleTyCd = ruleSnMap.getString("ruleTyCd"); 				// 규칙 유형 코드
		String ruleSeCd = ruleSnMap.getString("ruleSeCd"); 				// 규칙 구분 코드
		String ruleExprsnValue = ruleSnMap.getString("ruleExprsnValue");// 규칙 표현 값
		String bgngValue = ruleSnMap.getString("bgngValue"); 			// 시작 값
		String endValue = ruleSnMap.getString("endValue");				// 종료 값
		int totDataCnt = dgnssExcMap.getInt("totDataCnt");				// 총 데이터 수
		
		String rulePostvExprsnYn = ruleSnMap.getString("rulePostvExprsnYn"); // 규칙 긍정 표현 여부
		boolean isDgnssFqCmptn = false;
		
		int mtchCnt = 0;
		
		String tblNm = dgnssExcMap.getString("tblNm");
		String colNm = dgnssExcMap.getString("colNm");
		String nullDataDgnssYn = dgnssExcMap.getString("nullDataDgnssYn");
		
		if ("AT000100".equals(ruleTyCd)) { // SQL
			if ("AG000100".equals(ruleSeCd)) {
				if ("1".equals(ruleSn)) {
					// Data Count
					mtchCnt = totDataCnt;
				} else if ("2".equals(ruleSn)) {
					// Unique Count
					mtchCnt = voltDbService.selectDgnssUniqueCnt(tblNm, colNm);
				} else if ("3".equals(ruleSn)) {
					// Duplicate Count
					mtchCnt = voltDbService.selectDgnssDuplicateCnt(tblNm, colNm);
				} else if ("4".equals(ruleSn)) {
					// Distinct Count
					mtchCnt = voltDbService.selectDgnssDistinctCnt(tblNm, colNm);
				} else if ("5".equals(ruleSn)) {
					// NULL Count
					mtchCnt = voltDbService.selectDgnssNullCnt(tblNm, colNm);
				} else if ("6".equals(ruleSn)) {
					// Blank Count
					mtchCnt = voltDbService.selectDgnssBlankCnt(tblNm, colNm);
				}
			} else if ("AG000200".equals(ruleSeCd)) {
				// 진단 빈도 결과 목록
				// dgnssFqList = sqlSession.selectList("AnalysisMapper.selectDgnssFqResultList", dgnssExcMap);
				dgnssFqList = voltDbService.selectDgnssFqResultList(tblNm, colNm);
				isDgnssFqCmptn = true;

			} else {
				
				if(ruleExprsnValue.indexOf("${columnName}") > -1) {
					//String colNm = dgnssExcMap.getString("colNm");
					ruleExprsnValue = StringUtils.replace(ruleExprsnValue, "${columnName}", colNm);
				}
				// 사용자 정의 SQL
				mtchList = voltDbService.selectDgnssUserDfnSqlMtchList(ruleExprsnValue, colNm);
				
				disMtchList = voltDbService.selectDgnssUserDfnSqlDisMtchList(ruleExprsnValue, colNm, tblNm);
				
				if("N".equals(rulePostvExprsnYn)) {
					mtchCnt = disMtchList.size();
					excProfileDgnssResultDisMtchList(mtchList, dgnssExcMap, disMatchList);
				} else {
					mtchCnt = mtchList.size();
					excProfileDgnssResultDisMtchList(disMtchList, dgnssExcMap, disMatchList);
				}
			}
			
		} else if("AT000300".equals(ruleTyCd)) { // 범주 AT000300
			dgnssExcMap.putOrg("bgngValue", bgngValue);
			dgnssExcMap.putOrg("endValue", endValue);
			if ("AG000601".equals(ruleSeCd)) {
				// 숫자 범주
				mtchList = voltDbService.selectDgnssNumberCtgryMtchList(colNm, tblNm, bgngValue, endValue);
				disMtchList = voltDbService.selectDgnssNumberCtgryDisMtchList(colNm, tblNm, bgngValue, endValue);
			} else if ("AG000602".equals(ruleSeCd)) {
				// 문자 범주
				mtchList = voltDbService.selectDgnssChrctrCtgryMtchList(colNm, tblNm, bgngValue, endValue);
				disMtchList = voltDbService.selectDgnssChrctrCtgryDisMtchList(colNm, tblNm, bgngValue, endValue);
			} else {
				// 날짜 범주
				mtchList = voltDbService.selectDgnssDateCtgryMtchList(colNm, tblNm, bgngValue, endValue);
				disMtchList = voltDbService.selectDgnssDateCtgryDisMtchList(colNm, tblNm, bgngValue, endValue);
			}
			if("N".equals(rulePostvExprsnYn)) {
				mtchCnt = disMtchList.size();
				excProfileDgnssResultDisMtchList(mtchList, dgnssExcMap, disMatchList);
			} else {
				mtchCnt = mtchList.size();
				excProfileDgnssResultDisMtchList(disMtchList, dgnssExcMap, disMatchList);
			}
		} else if("AT000200".equals(ruleTyCd) || "AT000400".equals(ruleTyCd)) {
			
			List<SangsMap> colDataList = voltDbService.selectTableRowList(colNm, tblNm, nullDataDgnssYn);
			
			if ("AT000400".equals(ruleTyCd)) { // 날짜 패턴
				mtchCnt = excProfileDgnssDatePttrnMtchCnt(ruleExprsnValue, colDataList, dgnssExcMap, rulePostvExprsnYn, disMatchList);
			} else {
				mtchCnt = excProfileDgnssJavaPttrnMtchCnt(ruleExprsnValue, colDataList, dgnssExcMap, rulePostvExprsnYn, disMatchList);
			}
		} else {
			mtchCnt = 0;	
		}
		
		rtnMap.putOrg("mtchCnt", mtchCnt);
		if(isDgnssFqCmptn) {
			rtnMap.putOrg("isDgnssFqCmptn", isDgnssFqCmptn);
			rtnMap.putOrg("dgnssFqList", dgnssFqList);
		}
		
		return rtnMap;
	}
	
	/**
	 * mongoDb 프로파일 수행
	 * @param dgnssExcMap
	 * @param ruleSnMap
	 * @param dbmsNm
	 * @param ruleSn
	 * @return
	 * @throws Exception
	 */
	public SangsMap excMongoDb(SangsMap dgnssExcMap, SangsMap ruleSnMap, String dbmsNm, String ruleSn, List<SangsMap> disMatchList) throws Exception {
		SangsMap rtnMap = new SangsMap();
		List<SangsMap> dgnssFqList = new ArrayList<SangsMap>();
		String ruleTyCd = ruleSnMap.getString("ruleTyCd"); 				// 규칙 유형 코드
		String ruleSeCd = ruleSnMap.getString("ruleSeCd"); 				// 규칙 구분 코드
		String ruleExprsnValue = ruleSnMap.getString("ruleExprsnValue");// 규칙 표현 값
		int totDataCnt = dgnssExcMap.getInt("totDataCnt");				// 총 데이터 수
		
		String rulePostvExprsnYn = ruleSnMap.getString("rulePostvExprsnYn"); // 규칙 긍정 표현 여부
		boolean isDgnssFqCmptn = false;
		
		int mtchCnt = 0;
		
		String tblNm = dgnssExcMap.getString("tblNm");
		String colNm = dgnssExcMap.getString("colNm");
		
		if ("AT000100".equals(ruleTyCd)) { // SQL
			if ("AG000100".equals(ruleSeCd)) {
				if ("1".equals(ruleSn)) {
					// Data Count
					mtchCnt = totDataCnt;
				} else if ("2".equals(ruleSn)) {
					// Unique Count
					mtchCnt = mongoDbService.selectDgnssUniqueCnt(tblNm, colNm);
				} else if ("3".equals(ruleSn)) {
					// Duplicate Count
					mtchCnt = mongoDbService.selectDgnssDuplicateCnt(totDataCnt, tblNm, colNm);
				} else if ("4".equals(ruleSn)) {
					// Distinct Count
					mtchCnt = mongoDbService.selectDgnssDistinctCnt(tblNm, colNm);
				} else if ("5".equals(ruleSn)) {
					// NULL Count
					mtchCnt = mongoDbService.selectDgnssNullCnt(tblNm, colNm);
				} else if ("6".equals(ruleSn)) {
					// Blank Count
					mtchCnt = mongoDbService.selectDgnssBlankCnt(tblNm, colNm);
				}
			} else if ("AG000200".equals(ruleSeCd)) {
				// 진단 빈도 결과 목록
				dgnssFqList = mongoDbService.selectDgnssFqResultList(tblNm, colNm);
				isDgnssFqCmptn = true;

			}
		} else if("AT000200".equals(ruleTyCd) || "AT000400".equals(ruleTyCd)) {
			
			List<SangsMap> colDataList = mongoDbService.selectTableRowList(tblNm, colNm);
			if ("AT000400".equals(ruleTyCd)) { // 날짜 패턴
				mtchCnt = excProfileDgnssDatePttrnMtchCnt(ruleExprsnValue, colDataList, dgnssExcMap, rulePostvExprsnYn, disMatchList);
			} else {
				mtchCnt = excProfileDgnssJavaPttrnMtchCnt(ruleExprsnValue, colDataList, dgnssExcMap, rulePostvExprsnYn, disMatchList);
			}
		} else {
			mtchCnt = 0;	
		}
		
		rtnMap.putOrg("mtchCnt", mtchCnt);
		if(isDgnssFqCmptn) {
			rtnMap.putOrg("isDgnssFqCmptn", isDgnssFqCmptn);
			rtnMap.putOrg("dgnssFqList", dgnssFqList);
		}
		
		return rtnMap;
	}
	
	
}
