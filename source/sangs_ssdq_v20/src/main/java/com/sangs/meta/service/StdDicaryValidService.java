package com.sangs.meta.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.support.AuthUtil;
import com.sangs.common.support.BizUtil;
import com.sangs.common.support.BizUtil.DbmsDataTypeGroup;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;

/**
 * 표준사전 validation Service
 * @author ow.park
 */

@SangsService
public class StdDicaryValidService extends ServiceBase {

	
	/**
	 * 표준사전 단어 valid
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> validStdDicaryWrd(List<SangsMap> cmprList, List<SangsMap> targetList, boolean isBundleYn) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		Map<String, Object> cmprWrdEngAbrvNmMap = new HashMap<String, Object>();
		Map<String, Object> cmprWrdNmMap = new HashMap<String, Object>();
		try {

			int totalTargetCnt = targetList.size();

			// CMPR LIST 체크 map
			for (Map<String, Object> map : cmprList) {
				cmprWrdEngAbrvNmMap.put(String.valueOf(map.get("wrdEngAbrvNm")), map);
				cmprWrdNmMap.put(String.valueOf(map.get("wrdNm")), map);
			}
			// logger.debug("cmprMap : " + cmprMap);

			// TARGET LIST 체크 map
			for (Map<String, Object> map : targetList) {
				boolean exist = false;

				StringBuilder errorStr = new StringBuilder();
				StringBuilder etcStr = new StringBuilder();

				map.put("ETC_CN", "");
				map.put("ERROR_INFO", "");
				map.put("ERROR_CODE", "");

				for (Map.Entry<String, Object> entry : map.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();

					// 필수 항목 체크
					if (value == null || value == "") {
						if (key.equals("wrdTyCdNm")) {
							errorStr.append("단어구분 필수 누락, ");
						}

						if (key.equals("wrdNm")) {
							errorStr.append("단어명 필수 누락, ");
						}

						if (key.equals("wrdEngAbrvNm")) {
							errorStr.append("단어영문약어명 필수 누락, ");
						}

						if (key.equals("prhibtYn")) {
							errorStr.append("금직어 필수 누락, ");
						}

					} else {
						if (key.equals("wrdTyCdNm")) {
							if (!value.toString().equals("표준어") && !value.toString().equals("동의어")) {
								errorStr.append("단어구분 표기 오류, ");
							}
						}
						if (key.equals("prhibtYn")) {
							if (!value.toString().equals("Y") && !value.toString().equals("N")) {
								errorStr.append("금칙어 표기 오류, ");
							}
						}
						if (key.equals("wrdEngAbrvNm")) {
							if (value.toString().matches(".*[ !_@#$%^&*(),.?\\\":{}|<>].*")) {
								errorStr.append("단어영문약어명 \"특수문자 또는 공백\" 사용불가, ");
							}
							if (value.toString().matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
								errorStr.append("단어영문약어명 \"한글\" 사용불가, ");
							}
							/*	20220817 주석처리
							if (value.toString().matches(".*[0-9].*")) {
								errorStr.append("단어영문약어명 \"숫자\" 사용불가, ");
							}*/
							

							String stdSetNm = "";
							String cmprWrdEngAbrvNm = "";
							String cmprWrdNm = "";
							String wrdTyCd =  String.valueOf(map.get("wrdTyCd"));
							String wrdEngAbrvNm = String.valueOf(map.get("wrdEngAbrvNm"));
							String wrdNm = String.valueOf(map.get("wrdNm"));
							int myStdSetSn = AuthUtil.getStdSetSn();
							
							// 공통 세트 영문약어명 중복 체크
							if (cmprWrdEngAbrvNmMap.containsKey(wrdEngAbrvNm)) {
								SangsMap cmprEngAbrvNm = (SangsMap) cmprWrdEngAbrvNmMap.get(wrdEngAbrvNm);
								stdSetNm = cmprEngAbrvNm.getString("stdSetNm");
								cmprWrdEngAbrvNm = cmprEngAbrvNm.getString("wrdEngAbrvNm");
								cmprWrdNm = cmprEngAbrvNm.getString("wrdNm");
								int stdSetSn = cmprEngAbrvNm.getInt("stdSetSn");
								if (myStdSetSn != stdSetSn) {
									if (wrdEngAbrvNm.equals(cmprWrdEngAbrvNm) && wrdNm.equals(cmprWrdNm)) {
										exist =true;
									}
								}
							}
							// 공통 세트 단어명 중복 체크
							if (cmprWrdNmMap.containsKey(wrdNm)) {
								SangsMap cmprNm = (SangsMap) cmprWrdNmMap.get(wrdNm);
								stdSetNm = cmprNm.getString("stdSetNm");
								cmprWrdEngAbrvNm = cmprNm.getString("wrdEngAbrvNm");
								cmprWrdNm = cmprNm.getString("wrdNm");
								int stdSetSn = cmprNm.getInt("stdSetSn");
								if (myStdSetSn != stdSetSn) {
									if (wrdEngAbrvNm.equals(cmprWrdEngAbrvNm) && wrdNm.equals(cmprWrdNm)) {
										exist =true;
									}
								}
							}
							if(exist) {
								errorStr.insert(0, "\"" + wrdNm + "\" 단어명 + " + "\"" + wrdEngAbrvNm + "\" 영문약어명 중복, ");
								etcStr.insert(0, "[" + stdSetNm + "]");
							}
							
							// 자신세트 영문약어 중복 체크
							for (SangsMap cmprMap : cmprList) {
								int stdSetSn = cmprMap.getInt("stdSetSn");

								if (myStdSetSn == stdSetSn) {
									stdSetNm = AuthUtil.getStdSetNm();
									cmprWrdEngAbrvNm = cmprMap.getString("wrdEngAbrvNm");

									if (wrdEngAbrvNm.equals(cmprWrdEngAbrvNm)) {
										
										if(wrdTyCd.equals(cmprMap.getString("wrdTyCd"))) {
											etcStr.insert(0, "[" + stdSetNm + "]");
											errorStr.insert(0, "\"" + wrdEngAbrvNm + "\" 자신세트영문약어명 중복, ");
										}
									}
								}
							}
						}
					}

					map.put("ETC_CN", etcStr);
					// 맨 마지막 문자열 제거
					if (errorStr.length() > 0) {
						map.put("ERROR_INFO", errorStr.substring(0, errorStr.length() - 2));
					} else {
						map.put("ERROR_INFO", errorStr);
					}
				}

			}

			if(isBundleYn) {
				// 동의어 표준어 필수 체크
				for (SangsMap map : targetList) {
					String wrdTyCd = map.getString("wrdTyCd");
					if ("TYPE02".equals(wrdTyCd)) {
						map.putOrg("errorInfo", "동의어는 단어화면에서 신규등록하세요");
					}
				}
			}
			
			rtnMap.put("totalTargetCnt", totalTargetCnt); // 총 목록 개수
			rtnMap.put("resultList", targetList);

			// logger.debug("rtnMap : " + rtnMap);

		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}

	/**
	 * 표준사전 도메인 valid
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> validStdDicaryDomn(List<SangsMap> cmprList, List<SangsMap> targetList) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		Map<String, Object> cmprMap = new HashMap<String, Object>();

		try {
			
			int totalTargetCnt = targetList.size();
			
			// CMPR LIST 체크 map
			for (Map<String, Object> map : cmprList) {
				cmprMap.put(map.get("domnNm").toString(), map.get("domnNm").toString());
			}
			//logger.debug("cmprMap : " + cmprMap);
			
			// TARGET LIST 체크 map
			for (Map<String, Object> map : targetList) {
				
				StringBuilder errorStr = new StringBuilder();
				StringBuilder etcStr = new StringBuilder();
				String resultDomnNm = "";
				
				map.put("DOMN_NM", "");
				map.put("RESULT_DOMN_NM", "");
				map.put("ETC_CN", "");
				map.put("ERROR_INFO", "");
				map.put("ERROR_CODE", "");
				String dataTyCdTemp = "";
				String trgtDataType = "";
				DbmsDataTypeGroup getDataTypeInfo;
				
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					
					if(key.equals("dataTyCd")) {
						dataTyCdTemp = String.valueOf((value));
					}
					
					// 필수 항목 체크 
					if(value == null || value == "") {
						if(key.equals("domnGroupNm")) {
							errorStr.append("도메인그룹 필수 누락, ");
						}
						
						if(key.equals("domnClNm")) {
							errorStr.append("도메인분류명 필수 누락, ");
						}
						
						if(key.equals("dataTyCd")) {
							errorStr.append("데이터타입 필수 누락, ");
						}
						
						getDataTypeInfo = DbmsDataTypeGroup.findByDataType(dataTyCdTemp);
						trgtDataType = getDataTypeInfo.getTypeNm();
						
						if (key.equals("dataLtValue")) {
							if (!"DATE".equals(trgtDataType) && !"NUMERIC".equals(trgtDataType)) {
								List<String> characterTypeList = BizUtil.DbmsDataTypeGroup.CHARACTERTYPE.getDataTypeList();
								if (characterTypeList.indexOf(dataTyCdTemp) > -1) {
									errorStr.append("도메인길이 필수 누락, ");
								}
							}

						}
						
						
					} else {
						
						String domnClNm = map.get("domnClNm") == null ? "" : map.get("domnClNm").toString();
						String dataTyCd = map.get("dataTyCd") == null ? "" : map.get("dataTyCd").toString();
						String dataLtValue = map.get("dataLtValue") == null ? "" : map.get("dataLtValue").toString();
						
						if(dataTyCd.length() > 0) {
							dataTyCd = dataTyCd.substring(0, 1).toUpperCase();
						}
						
						resultDomnNm = domnClNm + dataTyCd + dataLtValue;
						
						if(key.equals("dataTyCd")) {
							if(value.toString().matches(".*[ !_@#$%^&*(),.?\\\":{}|<>].*")) {
								errorStr.append("데이터타입 \"특수문자 또는 공백\" 사용불가, ");
							}
							if(value.toString().matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
								errorStr.append("데이터타입 \"한글\" 사용불가, ");
							}
							/*	20220817 주석처리
							if(value.toString().matches(".*[0-9].*")) {
								errorStr.append("데이터타입 \"숫자\" 사용불가, ");
							}*/
							// 중복 체크 
							if(cmprMap.containsKey(resultDomnNm)){
								//etcStr.insert(0, "[" + cmprMap.get(resultDomnNm)+"]");
								errorStr.insert(0, "\"" + resultDomnNm +"\" 도메인명 중복, ");
							}
						}
						
						if(key.equals("dataLtValue")) {
							if(!value.toString().matches("^[0-9]+$")) {
								errorStr.append("데이터길이 \"숫자\" 외 사용불가, ");
							}
						}
					}
					
					map.put("RESULT_DOMN_NM", resultDomnNm);
					map.put("DOMN_NM", resultDomnNm);
					map.put("ETC_CN", etcStr);
					// 맨 마지막 문자열 제거
					if(errorStr.length() > 0) {
						map.put("ERROR_INFO", errorStr.substring(0, errorStr.length()-2));	
					} else {
						map.put("ERROR_INFO", errorStr);
					}
				}
			}
			
			rtnMap.put("totalTargetCnt", totalTargetCnt); 			// 총 목록 개수
			rtnMap.put("resultList", targetList);
			
			//logger.debug("rtnMap : " + rtnMap);
			
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}
	
	/**
	 * 표준사전 용어 valid
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> validStdDicaryWord(List<SangsMap> cmprList, List<SangsMap> targetList) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		Map<String, Object> cmprMap = new HashMap<String, Object>();

		try {
			
			int totalTargetCnt = targetList.size();
			
			// CMPR LIST 체크 map
			for (Map<String, Object> map : cmprList) {
				cmprMap.put(String.valueOf(map.get("wordEngAbrvNm")), String.valueOf(map.get("stdSetNm")));
			}
			//logger.debug("cmprMap : " + cmprMap);
			
			// TARGET LIST 체크 map
			for (Map<String, Object> map : targetList) {
				
				StringBuilder errorStr = new StringBuilder();
				StringBuilder etcStr = new StringBuilder();
				
				map.put("ETC_CN", "");
				map.put("ERROR_INFO", "");
				map.put("ERROR_CODE", "");
				
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					// 필수 항목 체크 
					if(value == null || value == "") {
						if(key.equals("wordTyCdNm")) {
							errorStr.append("용어구분 필수 누락, ");
						}
						
						if(key.equals("wordNm")) {
							errorStr.append("용어명 필수 누락, ");
						}
						
						if(key.equals("wordEngNm")) {
							errorStr.append("용어영문명 필수 누락, ");
						}
						
						if(key.equals("wordEngAbrvNm")) {
							errorStr.append("용어영문약어명 필수 누락, ");
						}
						
						if(key.equals("domnNm")) {
							errorStr.append("도메인명 필수 누락, ");
						}
						
//						if(key.equals("wordCn")) {
//							errorStr.append("용어 설명 누락, ");
//						}
						
						if(key.equals("prhibtYn")) {
							errorStr.append("금직어 필수 누락, ");
						}
						
					} else {
						
						if(key.equals("wordTyCdNm")) {
							if (!value.toString().equals("표준어") && !value.toString().equals("동의어")) {
								errorStr.append("용어구분 표기 오류, ");
							}
						}
						
						if(key.equals("prhibtYn")) {
							if (!value.toString().equals("Y") && !value.toString().equals("N")) {
								errorStr.append("금칙어 표기 오류, ");
							}
						}
						
						if(key.equals("wordEngAbrvNm")) {
							if(value.toString().matches(".*[ !@#$%^&*(),.?\\\":{}|<>].*")) {
								errorStr.append("용어영문약어명 \"_ 외 특수문자 또는 공백\" 사용불가, ");
							}
							if(value.toString().matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
								errorStr.append("용어영문약어명 \"한글\" 사용불가, ");
							}
							/*	20220817 주석처리
							if(value.toString().matches(".*[0-9].*")) {
								errorStr.append("용어영문약어명 \"숫자\" 사용불가, ");
							} */
							// 중복 체크 
							if(cmprMap.containsKey(map.get("wordEngAbrvNm"))){
								etcStr.insert(0, "[" + cmprMap.get(map.get("wordEngAbrvNm"))+"]");
								errorStr.insert(0, "\"" + map.get("wordEngAbrvNm") +"\" 용어영문약어명 중복, ");
							}
						}
						
					}
					
					map.put("ETC_CN", etcStr);
					// 맨 마지막 문자열 제거
					if(errorStr.length() > 0) {
						map.put("ERROR_INFO", errorStr.substring(0, errorStr.length()-2));	
					} else {
						map.put("ERROR_INFO", errorStr);
					}
				}
				
			}
			
			rtnMap.put("totalTargetCnt", totalTargetCnt); 			// 총 목록 개수
			rtnMap.put("resultList", targetList);
			
			
		} catch (SangsMessageException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.service");
		}
		return rtnMap;
	}
	
	/**
	 * 엑셀내 중복 데이터 검사 및 엑셀 row 번호 재배치
	 * @param list
	 * @param column
	 * @param columnNm
	 * @return
	 * @throws Exception
	 */
	public List<SangsMap> setDpcnCheckWithRowNo(List<SangsMap> list, String cmprColumn, String cmprColumnErrNm, String cmprNm) throws Exception {
		List<SangsMap> resultList = new ArrayList<SangsMap>();

		for (SangsMap map : list) {
			map.remove("EXCEL_ROW_NO");
		}

		for (int i = 0; i < list.size(); i++) {
			boolean isError = false;
			for (int j = 0; j < i; j++) {

				String iVal = String.valueOf(list.get(i));
				String jVal = String.valueOf(list.get(j));
				String iErrorInfo = String.valueOf(list.get(j).get("errorInfo"));
				
				if (iVal.contentEquals(jVal)) {
					isError = true;

					if ("".equals(iErrorInfo)) {
						list.get(i).putOrg("errorInfo", "동일 데이터 중복");
						list.get(j).putOrg("errorInfo", "동일 데이터 중복");
					} else {
						if(iErrorInfo.indexOf("동일 데이터 중복") > -1) {
							list.get(i).putOrg("errorInfo", iErrorInfo);
							list.get(j).putOrg("errorInfo", iErrorInfo);
						} else {
							list.get(i).putOrg("errorInfo", iErrorInfo + ", 동일 데이터 중복");
							list.get(j).putOrg("errorInfo", iErrorInfo + ", 동일 데이터 중복");
						}
					}
				} else {
					isError = false;
				}

				if(!"".equals(cmprColumn) && !"".equals(cmprColumnErrNm)) {
					iVal = list.get(i).getString(cmprColumn);
					jVal = list.get(j).getString(cmprColumn);
					
					String iTyCdNm = "";
					String jTyCdNm = "";
					
					String iNm = "";
					String jNm = "";
					
					if("wrd".equals(cmprNm)) {
						iTyCdNm = list.get(i).getString("wrdTyCdNm");
						jTyCdNm = list.get(j).getString("wrdTyCdNm");
						
						iNm = list.get(i).getString("wrdNm");
						jNm = list.get(j).getString("wrdNm");
					}
					
					String iWrdTyCdNmVal =iTyCdNm;
					String jWrdTyCdNmVal =jTyCdNm;
					
					String iWrdNmVal = iNm;
					String jWrdNmVal = jNm;
					
					iErrorInfo = String.valueOf(list.get(j).get("errorInfo"));
					
					if(iWrdTyCdNmVal.equals(jWrdTyCdNmVal)) {
						
						if (iVal.equals(jVal)) {
							isError = true;
							if ("".equals(iErrorInfo)) {
								list.get(i).putOrg("errorInfo", "\"" + iVal + "\" " + cmprColumnErrNm + " 중복");
								list.get(j).putOrg("errorInfo", "\"" + iVal + "\" " + cmprColumnErrNm + " 중복");
							} else {
								
								if(iErrorInfo.indexOf(iVal) > -1) {
									list.get(i).putOrg("errorInfo", iErrorInfo);
									list.get(j).putOrg("errorInfo", iErrorInfo);
								} else {
									list.get(i).putOrg("errorInfo", iErrorInfo + ", \"" + iVal + "\" " + cmprColumnErrNm + " 중복");
									list.get(j).putOrg("errorInfo", iErrorInfo + ", \"" + iVal + "\" " + cmprColumnErrNm + " 중복");
								}
							}
						} else {
							isError = false;
						}
					} else {
						// 동의어 표준어는 단어명 다르게.
						if(iWrdNmVal.equals(jWrdNmVal)) {
							isError = true;
							String name = "";
							if("wrd".equals(cmprNm)) {
								name = "단어명";
							} 

							if ("".equals(iErrorInfo)) {
								list.get(i).putOrg("errorInfo", name+" 중복" );
								list.get(j).putOrg("errorInfo", name+" 중복");
							} else {
								list.get(i).putOrg("errorInfo", iErrorInfo + ", "+ name+" 중복");
								list.get(j).putOrg("errorInfo", iErrorInfo + ", "+ name+" 중복");
							}
						}
					}
					if (isError) {
						String iEtcCn = String.valueOf(list.get(j).get("etcCn"));
						
						if ("".equals(iEtcCn)) {
							list.get(i).putOrg("etcCn", "[업로드파일]");
							list.get(j).putOrg("etcCn", "[업로드파일]");
						} else {
							if(iEtcCn.indexOf("[업로드파일]") > -1) {
								list.get(i).putOrg("etcCn", iEtcCn);
								list.get(j).putOrg("etcCn", iEtcCn);
							} else {
								list.get(i).putOrg("etcCn", iEtcCn + ", [업로드파일]");
								list.get(j).putOrg("etcCn", iEtcCn + ", [업로드파일]");
							}
						}
					}
					
				}
			}
		}

		int i = 1;
		for (SangsMap map : list) {
			map.putOrg("EXCEL_ROW_NO", i++);
		}
		
		resultList.addAll(list);
		return resultList;
	}
	
	/**
	 * 리스트의 row 중 전체 컬럼이 빈경우 로우를 제거 하고 upperCaseCol의 컬럼에 대해서 value를 대문자로 변경
	 * @param list
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	public List<SangsMap> removeRowAllColEmptyWithUpper(List<SangsMap> list, String[] columns) throws Exception {
		List<SangsMap> resultList = new ArrayList<SangsMap>();
		
		// 대문자로 변환 for문
		for (SangsMap map : list) {

			for (String column : columns) {
				map.putOrg(column, map.getString(column).toUpperCase());
			}
		}
		
		// 데이터의 row전체가 빈값일 경우 이하 row는 skip하여 값 있는 row 까지만 정상 데이터로 판단
		for (SangsMap map : list) {
			Iterator<String> it = map.keySet().iterator();
			int keyCnt = 0; 	// 컬럼 count
			int nullValCnt = 0; // null 값 count
			while (it.hasNext()) {

				String key = it.next();
				String value = map.get(key).toString();

				if (!"EXCEL_ROW_NO".equals(key)) {
					if ("".equals(value)) {
						nullValCnt++;
					}
					keyCnt++;
				}
			}
			if (keyCnt != nullValCnt) {
				resultList.add(map);
			}
		}
		return resultList;
	}
	
	/**
	 * 승인상태 검사
	 * @param list
	 * @param cmprlist
	 * @param cmprColumn
	 * @return
	 * @throws Exception
	 */
	public List<SangsMap> setApprovalCheck(List<SangsMap> list, List<SangsMap> cmprlist, String cmprColumn)	throws Exception {
		List<SangsMap> resultList = new ArrayList<SangsMap>();
		List<SangsMap> notAprvList = new ArrayList<SangsMap>();

		for (SangsMap map : cmprlist) {
			String aprvSttusCd = map.getString("aprvSttusCd");
			if (!"APPROVAL".equals(aprvSttusCd)) {
				notAprvList.add(map);
			}
		}
		
		if (!CollectionUtils.isEmpty(notAprvList)) {

			for (SangsMap map : list) {

				for (SangsMap cmprMap : notAprvList) {

					String value = map.getString(cmprColumn);
					String cmprValue = cmprMap.getString(cmprColumn);

					if (value.equals(cmprValue)) {
						String errorInfo = map.getString("errorInfo");
						String etcCn = map.getString("etcCn");
						String stdSetNm = cmprMap.getString("stdSetNm");
						if ("".equals(errorInfo)) {
							map.putOrg("etcCn", "[" + stdSetNm + "]");
							map.putOrg("errorInfo", "\"" + value + "\" 미승인");
						} else {
							if (!etcCn.contains(stdSetNm)) {
								map.putOrg("etcCn", etcCn + ", [" + stdSetNm + "]");
							}
							map.putOrg("errorInfo", errorInfo + ", \"" + value + "\" 미승인");
						}
					}
				}
			}
		}
		
		resultList.addAll(list);
		return resultList;
	}
}