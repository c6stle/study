package com.sangs.collector.service;

import java.util.HashMap;
import java.util.Iterator;
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
 * Description : 메타 통합관리 > 메타주소정보 관련 Service 상세
 *
 * Modification Information
 * 수정일		수정자			수정내용
 * -------		-----------------------------------
 * 2016.01.25 	송호현			최초작성
 *
 */

@SangsService
public class CollectAddressInfoService extends ServiceBase {


	@Autowired
	private CommonDao dao;


	/**
	 * 주소정보 목록 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<SangsMap> selectMetaMngAddressList(Map<String, Object> params) throws Exception {
		return dao.selectList("ct_collect_address.selectMetaMngAddressList", params);
	}

	/**
	 * 주소순번 조회(MAX)
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int selectMetaMngAddressNextSn(Map<String, Object> params) throws Exception{
		return dao.selectInteger("ct_collect_address.selectMetaMngAddressNextSn", params);
	}

	/**
	 * 주소정보 신규 등록
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public void insertMetaMngAddressInfo(Map<String, Object> params) throws Exception{
		dao.insert("ct_collect_address.insertMetaMngAddressInfo", params);
	}

	/**
	 * 주소정보 수정
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public void updateMetaMngAddressInfo(Map<String, Object> params) throws Exception{
		dao.update("ct_collect_address.updateMetaMngAddressInfo", params);
	}


	/**
	 * 주소정보 삭제
	 * @param params
	 * @throws Exception
	 */
	public void deleteMetaMngAddressInfo(Map<String, Object> params) throws Exception{
		dao.delete("ct_collect_address.deleteMetaMngAddressInfo", params);
	}

	/**
	 * 메타주소 신규등록/수정/삭제 일괄 처리
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> saveMetaMngAddressExec(Map<String, Object> paramMap) throws Exception {

		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {

			/**
			 *
			 * 1. JSP에서 수신되는 항목
			 * - apicode, PMODE(INS,UPD,DEL), SN(INS->0), 주소명, 주소, 설명, 요청정보, 사용여부
			 *
			 * 2. 목록을 loop 처리하면서 pmode에 따라 신규등록, 수정, 삭제처리
			 *
			 *
			 */

			@SuppressWarnings("unchecked")
			List<Map<String, Object>> addrList = (List<Map<String, Object>>) paramMap.get("addrList");

			for (int i=0; i<addrList.size(); i++) {
				String pmode = SangsStringUtil.nvl(addrList.get(i).get("pmode"), "");

				if (!pmode.equals("")) {

					Map<String, Object> addressMap = new HashMap<String, Object>();

					addressMap.put("apiCd", paramMap.get("apiCd"));
					addressMap.put("apiAddrSn", addrList.get(i).get("apiAddrSn"));
					addressMap.put("apiAddrNm", addrList.get(i).get("apiAddrNm"));
					addressMap.put("apiAddr", addrList.get(i).get("apiAddr"));
					addressMap.put("apiAddrCn", addrList.get(i).get("apiAddrCn"));
					addressMap.put("apiRqstCn", addrList.get(i).get("apiRqstCn"));
					addressMap.put("useYn", addrList.get(i).get("useYn"));

					Iterator<String> iterator = addressMap.keySet().iterator();
					while(iterator.hasNext()) {
						String key = (String) iterator.next();
						addressMap.put(key, SangsWebUtil.clearXSSMinimum2((String) addressMap.get(key)));

					}

					if (pmode.equals("INS")) {

						int apiAdresSn = selectMetaMngAddressNextSn(addressMap);
						addressMap.put("apiAddrSn", String.valueOf(apiAdresSn));

						insertMetaMngAddressInfo(addressMap);

					} else if (pmode.equals("UPD")) {
						updateMetaMngAddressInfo(addressMap);

					} else if (pmode.equals("DEL")) {
						deleteMetaMngAddressInfo(addressMap);

					}

				}
			}

			rtnMap.put("resultCd", "OK");

		} catch (Exception e) {
			logger.error("" , e);
			throw new SangsMessageException("처리중 에러가 발생하였습니다.");
		}

		return rtnMap;
	}
}