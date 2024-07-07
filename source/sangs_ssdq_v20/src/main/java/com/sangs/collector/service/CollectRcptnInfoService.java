package com.sangs.collector.service;

import java.util.HashMap;
import java.util.Iterator;
//import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.support.CommonDao;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;
import com.sangs.lib.support.utils.SangsStringUtil;
import com.sangs.lib.support.utils.SangsWebUtil;

/**
 * Description : 메타 통합관리 > 메타전문정보 관련 Service 상세
 *
 * Modification Information
 * 수정일		수정자			수정내용
 * -------		-----------------------------------
 * 2016.01.29 	조남훈			최초작성
 *
 */

@SangsService
public class CollectRcptnInfoService extends ServiceBase {


	@Autowired
	private CommonDao dao;

	/**
	 * 전문정보 정보 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public SangsMap selectMetaMngOutputInfo(Map<String, Object> params) throws Exception {
		return dao.selectOne("ct_collect_rcptn.selectMetaMngOutputInfo", params);
	}


	/**
	 * 전문정보 목록 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<SangsMap> selectMetaMngOutputList(Map<String, Object> params) throws Exception {
		return dao.selectList("ct_collect_rcptn.selectMetaMngOutputList", params);
	}

	/**
	 * 전문순번 조회(MAX)
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int selectMetaMngOutputNextSn(Map<String, Object> params) throws Exception{
		return dao.selectInteger("ct_collect_rcptn.selectMetaMngOutputNextSn", params);
	}

	/**
	 * 전문정보 신규 등록
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public void insertMetaMngOutputInfo(Map<String, Object> params) throws Exception{
		dao.insert("ct_collect_rcptn.insertMetaMngOutputInfo", params);
	}

	/**
	 * 전문정보 수정
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public void updateMetaMngOutputInfo(Map<String, Object> params) throws Exception{
		dao.update("ct_collect_rcptn.updateMetaMngOutputInfo", params);
	}


	/**
	 * 전문정보 삭제
	 * @param params
	 * @throws Exception
	 */
	public void deleteMetaMngOutputInfo(Map<String, Object> params) throws Exception{
		dao.delete("ct_collect_rcptn.deleteMetaMngOutputInfo", params);
	}

	/**
	 * 전문정보칼럼양식 호출
	 * @param params
	 * @throws Exception
	 */
	public List<SangsMap> selectMetaMngOutputExcelList(Map<String, Object> params) throws Exception {
		return dao.selectList("ct_collect_rcptn.selectMetaMngOutputExcelList", params);
	}

	/*
	public Map selectMetaMngOutputExcelList(Map<String, Object> params) throws Exception{

		Map map = new HashMap<String, Object>();

		List<SangsMap> tempList = (List<SangsMap>) metaMngOutputMapper.selectMetaMngOutputExcelList(params);

		map = this.selectMetaMngOutputExcelListTemp(tempList);

		return map;
	}
	*/

	/**
	 * 전문정보칼럼양식템포 호출
	 * @param params
	 * @throws Exception
	 */
	/*
	private Map selectMetaMngOutputExcelListTemp(List<SangsMap> list) {

		Map tempMap = new HashMap<String, Object>();

		if(list.size() >0){
			tempMap.put("colCnt", list.size());
			for(int i=0; i < list.size(); i++){
				SangsMap map = list.get(i);
				tempMap.put("outptNm_", map.get("outptNm")); //_"+(i+1)
			}
		}else {
			tempMap.put("colCnt", 0);
		}

		return tempMap;

	}
	*/

	/**
	 * 전문정보 신규 등록 직전, 해당 테이블의 칼럼을 늘려주는 작업(2019.11.16 mt1716)
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public void alterAddMetaMngDataTable(Map<String, Object> params) throws Exception{
		dao.update("ct_collect_rcptn.alterAddMetaMngDataTable", params);
	}

	/**
	 * 삭제 전 해당 테이블의 칼럼을 삭제할 수 있는지 확인. 데이타가 있다면 삭제하지 않도록 처리해야 함
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int selectMetaMngDataColCnt(Map<String, Object> params) throws Exception{
		return dao.selectCount("ct_collect_rcptn.selectMetaMngDataColCnt", params);
	}

	/**
	 * 전문정보 삭제 전 해당 테이블의 칼럼 삭제
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public void alterDropMetaMngDataTable(Map<String, Object> params) throws Exception{
		dao.update("ct_collect_rcptn.alterDropMetaMngDataTable", params);
	}

	/**
	 * 메타전문정보 신규등록/수정/삭제 일괄 처리
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> saveMetaMngOutputExec(Map<String, Object> paramMap) throws Exception {

		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> outputList = (List<Map<String, Object>>) paramMap.get("outputList");

			for (int i=0; i<outputList.size(); i++) {
				String pmode = SangsStringUtil.nvl(outputList.get(i).get("pmode"), "");

				if (!pmode.equals("")) {


					Map<String, Object> outputMap = new HashMap<String, Object>();

					outputMap.put("apiCd", paramMap.get("apiCd"));
					outputMap.put("apiRcptnSn", outputList.get(i).get("apiRcptnSn"));
					outputMap.put("rcptnNm", outputList.get(i).get("rcptnNm"));
					outputMap.put("rcptnEngNm", outputList.get(i).get("rcptnEngNm"));
					outputMap.put("rcptnDataTyCd", outputList.get(i).get("rcptnDataTyCd"));
					outputMap.put("rcptnDataLtValue", outputList.get(i).get("rcptnDataLtValue"));
					outputMap.put("rcptnDataNullYn", outputList.get(i).get("rcptnDataNullYn"));
					outputMap.put("rcptnDataSumryCn", outputList.get(i).get("rcptnDataSumryCn"));
					outputMap.put("useYn", outputList.get(i).get("useYn"));

					Iterator<String> iterator = outputMap.keySet().iterator();
					while(iterator.hasNext()) {
						String key = (String) iterator.next();
						outputMap.put(key, SangsWebUtil.clearXSSMinimum2((String) outputMap.get(key)));

					}

					if (pmode.equals("INS")) {

						int apiOutptSn = selectMetaMngOutputNextSn(outputMap);
						outputMap.put("apiRcptnSn", String.valueOf(apiOutptSn));

						insertMetaMngOutputInfo(outputMap);

					} else if (pmode.equals("UPD")) {
						updateMetaMngOutputInfo(outputMap);

					} else if (pmode.equals("DEL")) {
						deleteMetaMngOutputInfo(outputMap);

					}

				}
			}

			rtnMap.put("resultCd", "OK");

		} catch (NullPointerException ie) {
			logger.error("", ie);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		} catch (Exception e) {
			logger.error(this.getClass().getName(), e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}

		/*
		 * model.addAttribute("type", "PARENT"); model.addAttribute("msg", msg);
		 * model.addAttribute("url",
		 * "/collector/collect/collect_rcptn_list?apiCode="+params.get("apiCode")+"&tIdx="+
		 * params.get("tIdx")); return "forward:/common/msgForward.do";
		 */

		return rtnMap;
	}


}