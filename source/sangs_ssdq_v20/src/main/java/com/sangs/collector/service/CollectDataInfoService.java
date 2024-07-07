package com.sangs.collector.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.sangs.common.base.ServiceBase;
import com.sangs.common.support.CommonDao;
import com.sangs.fwk.annotation.SangsService;
import com.sangs.fwk.common.SangsConstants;
import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.domain.SangsPagingViewInfo;
import com.sangs.lib.support.utils.SangsSimpleExcelMaker;
import com.sangs.lib.support.utils.SangsStringUtil;

/**
 * Description : 메타 통합관리 > 수집데이터 관련 Service 상세
 *
 * Modification Information
 * 수정일		수정자			수정내용
 * -------		-----------------------------------
 * 2016.01.29 	조남훈			최초작성
 *
 */

@SangsService
public class CollectDataInfoService extends ServiceBase {


	@Autowired
	private CommonDao dao;


	/**
	 *  수집데이터 항목(컬럼) 미정의(기본) 목록 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<SangsMap> selectMetaMngCollectDataDefaultOutputList(Map<String, Object> params) throws Exception {
		return dao.selectList("ct_collect_data_mng.selectMetaMngCollectDataDefaultOutputList", params);
	}

	/**
	 *  수집데이터 항목(컬럼) 목록 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<SangsMap> selectMetaMngCollectDataOutputList(Map<String, Object> params) throws Exception {
		return dao.selectList("ct_collect_data_mng.selectMetaMngCollectDataOutputList", params);
	}

	/**
	 * 수집데이터 항목(컬럼) 목록 조회
	 * @param params
	 * @param outputList
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectMetaMngCollectDataList(Map<String, Object> params, List<SangsMap> outputList) throws Exception {

		params.put("apiStrgTblNm", "CT_DATA_"+params.get("apiCd"));

		// 페이징
		int pageIndex = SangsStringUtil.nvlInt(params.get("pageIndex"), 1);
		params.put("pageIndex", pageIndex);
		int pagingRowSize = SangsConstants.DEFAULT_LIST_ROW_SIZE;
		// 리스트 조회조건 컬럼항목 설정
		String cols = "";
		for (int i=0; i<outputList.size(); i++) {
			cols += " ,"+outputList.get(i).getString("colNm");
		}
		params.put("cols", cols);

		// 카운트 조회
    	int cnt = dao.selectInteger("ct_collect_data_mng.selectMetaMngCollectDataListCnt", params);

    	// 페이징 파라미터 셋팅
    	//super.gridPagingSet(params);
    	SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(cnt, pageIndex, pagingRowSize);

    	// 엑셀일때는 조회된 갯수만큼 다 나오게 하기 위해서
    	if("EXCEL".equals(SangsStringUtil.nvl(params.get("listType"))) || "ALL".equals(SangsStringUtil.nvl(params.get("listType")))) {
    		//params.put("perPage", String.valueOf(cnt));	// 페이지당 게시물 수 set
    		pagingRowSize = cnt;
    	} else {
    		params.put("start_row", pagingInfo.getStartRow());
    		params.put("end_row", pagingInfo.getEndRow());
    	}

    	List<SangsMap> result = dao.selectList("ct_collect_data_mng.selectMetaMngCollectDataList", params);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultList", result);
		map.put("resultCnt", Integer.toString(cnt));
		map.put("pagingInfo", pagingInfo);

		return map;
	}

	/**
	 * 수집데이터 목록 카운트조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int selectMetaMngCollectDataListCnt(Map<String, Object> params) throws Exception {
		return dao.selectCount("ct_collect_data_mng.selectMetaMngCollectDataListCnt", params);
	}

	/**
	 * 수집 데이터 등록
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public void insertMetaMngCollectDataInfo(Map<String, Object> params) throws Exception{
		dao.insert("ct_collect_data_mng.insertMetaMngCollectDataInfo", params);
	}

	/**
	 * 수집 데이터 한줄정보
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public SangsMap selectMetaMngCollectDataInfo(Map<String, Object> params, List<SangsMap> outputList) throws Exception {

		// 리스트 조회조건 컬럼항목 설정
		String cols = "";
		for (int i=0; i<outputList.size(); i++) {
			cols += " ,"+outputList.get(i).getString("colNm");
		}
		params.put("cols", cols);
		/*
		 String coals = params.get("cols").replace(" ", "");
		 String[] colsList = coals.split(",");
		*/

		SangsMap outputDataInfo = dao.selectOne("ct_collect_data_mng.selectMetaMngCollectDataInfo", params);
		outputDataInfo.put("outputDataInfo", outputDataInfo);

		return outputDataInfo;
	}

	/**
	 * 수집 데이터 수정
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public void updateMetaMngCollectDataInfo(Map<String, Object> params) throws Exception{
		dao.update("ct_collect_data_mng.updateMetaMngCollectDataInfo", params);
	}

	/**
	 * 수집 데이터 삭제
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public void deleteMetaMngCollectDataInfo(Map<String, Object> params) throws Exception{
		dao.delete("ct_collect_data_mng.deleteMetaMngCollectDataInfo", params);
	}

	/**
	 * 수집 데이터 로그단위 데이터 삭제
	 * @param params
	 * @throws Exception
	 */
	public void deleteMetaMngCollectLogDataInfo(Map<String, Object> params) throws Exception {
		dao.delete("ct_collect_data_mng.deleteMetaMngCollectLogDataInfo", params);
	}

	/**
	 * 수집 데이터 데이터 삭제
	 * @param params
	 * @throws Exception
	 */
	/*
	public void metaMngTruncateData(Map<String, Object> params) throws Exception {
		try {
			metaMngCollectDataDao.metaMngTruncateData(params);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	*/

	public Workbook getMetaMngCollectDataListExcel(Map<String, Object> paramMap) throws Exception {

		Workbook workbook = null;

		//조회할 수집데이터 테이블명 입력
		paramMap.put("apiStrgTblNm", "CT_DATA_"+paramMap.get("apiCd"));

		SangsMap resultMap = dao.selectOne("ct_collect_info.selectMetaMngInfoInfo", paramMap);

		// 전문정보(목록) 조회
		int outputCnt = resultMap.getInt("rcptnCnt");

		List<SangsMap> outputList = null;
		if (outputCnt > 0) {	// 전문정보가 있을 경우 해당 목록 조회
			outputList = selectMetaMngCollectDataOutputList(paramMap);

		} else  { // 전문정보가 없을 경우 default 목록 조회
			outputList = selectMetaMngCollectDataDefaultOutputList(paramMap);
		}

		// 리스트 조회
		Map<String, Object> dataListMap = selectMetaMngCollectDataList(paramMap, outputList);

		@SuppressWarnings("unchecked")
		List<SangsMap> resultList = (List<SangsMap>) dataListMap.get("resultList");

		String[] colNmArr = new String[outputList.size()+4];
		String[] colIdArr = new String[outputList.size()+4];

		colNmArr[0] = "";
		colNmArr[1] = "YMD";
		colNmArr[2] = "LogNo";
		colNmArr[3] = "DataNo";

		colIdArr[0] = "rn";
		colIdArr[1] = "logYmd";
		colIdArr[2] = "logSn";
		colIdArr[3] = "dataSn";


		for (int i=0; i<outputList.size(); i++) {
			colNmArr[i+4] = outputList.get(i).getString("apiRcptnNm");
			colIdArr[i+4] = outputList.get(i).getString("colNm").toLowerCase();
		}

		SangsSimpleExcelMaker em = new SangsSimpleExcelMaker();
		workbook = em.createSheet()
				.setHeaderColNm(colNmArr)		// 컬럼 해더의 한글명 셋팅
				.setHeaderColId(colIdArr)		// 컬럼의 아이디값 지정
				.setList(resultList)			// 목록
				.setAutoSize()					// 자동 cell 조정
				.getWorkbook();					// 셋팅한 정보 workbook 반환

		return workbook;
	}

}