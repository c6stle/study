package com.sangs.lib.support.domain;

import java.util.ArrayList;

import com.sangs.lib.support.common.SangsCmmnSuportConstants;

/**
 * 데이터 목록에 대한 페이징처리를 위한 class<br>
 * - 생성자에 총 데이터수, 페이지 번호, 한페이지에 보여주는 데이터 수를 input으로 넣으면 페이징영역의 필요한 데이터를 생성한다.<br>
 * <pre>
 * <code>
 * - 예) 
 * // 페이징 객체 생성
 * SangsPagingViewInfo pagingInfo = new SangsPagingViewInfo(totalCount, pageNum, SangsConstants.DEFAULT_LIST_ROW_SIZE);
 * // return 데이터에 put
 * rtnMap.put("pagingInfo", pagingInfo);
 * 
 * // 화면영역처리 
 * &lt;div id="dv_paging"&lt;&gt;/div&gt;
 * // 공통 펑션을 통해서 받은 data 객체를 넣으면 페이징영역 생성
 * fnSetPaging("dv_paging", data.pagingInfo);
 * </code>
 * </pre>
 * @author id.yoon
 * @since 2022.05.02
 * @version 1.0
 * @see
 *
 * <pre>
 * &lt;&lt; 개정이력(Modification Information) &gt;&gt;
 *   수정일               수정자              수정내용
 *  -------       --------    ---------------------------
 *  2022.05.02    id.yoon     최초 생성
 * </pre>
 */
public class SangsPagingViewInfo {

	private final int totalCount;				// 총 데이터 수
	private final int pageNum;				// 현재 페이지 번호 
	private final int pageSize;				// 한페이지에서 보여주는 데이터 수
	private int totalPageNum;			// 총 페이지 수 
	private final int pageGroupSize;		// 한 화면에 보여줄 페이지 수
	private int prevGroupPageNum;		// 페이지 번호의 이전 그룹 페이지 번호  << < 1 2 3 > >>
	private int nextGroupPageNum;		// 페이지 번호의 다음 그릅 페이지 번호   << < 1 2 3 > >>
	private final int startPageNum;			// 시작페이지 번호
	private final int startRecordNum;
	private int lastGroupStart;			// 마지막 페이지 그룹의 페이번호 
	private ArrayList<Integer> list = null;
	
	/**
	 * 페이징 객체 생성
	 * @param totalCount 총 데이터 수
	 * @param pageNum 페이지번호
	 * @param pageSize 한페이지에서 보여주는 데이터 수
	 */
	public SangsPagingViewInfo(int totalCount, int pageNum, int pageSize) {
		this.totalCount = totalCount;
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		
		if(pageNum != 0) {
			this.totalPageNum = totalCount / pageSize;
			if(totalCount % pageSize > 0)
				this.totalPageNum = this.totalPageNum + 1;
		}
		
		this.prevGroupPageNum = 0;
		this.nextGroupPageNum = 0;
		
		pageGroupSize = SangsCmmnSuportConstants.FWK_PAGING_PAGE_DISPLAY_CNT;
		
		int startPageNum = (((pageNum -1)  / pageGroupSize)) * pageGroupSize + 1;
		
		nextGroupPageNum = startPageNum + pageGroupSize;
		
		prevGroupPageNum = startPageNum - pageGroupSize;
		
		if(prevGroupPageNum < 0)
			prevGroupPageNum = 1;
		
		if(pageSize > 0 && totalCount > 0) {
			list = new ArrayList<Integer>();
			for(int i = 0 ; i < pageGroupSize ; i++) {
				int iPage = i + startPageNum;
				if(totalPageNum >= iPage) {
					list.add(iPage);
				}
			}
		}
		
		this.startRecordNum = totalCount - ((this.pageNum - 1 ) * this.pageSize);
		this.startPageNum = startPageNum;
		
		this.setLastGroupStart();
		
		if(nextGroupPageNum > totalPageNum) {
			nextGroupPageNum = this.lastGroupStart;
		}
		
	}

	/**
	 * 총 데이터 수 반환 
	 * @return 총 데이터 반환
	 */
	public int getTotalCount() {
		return totalCount;
	}
	/**
	 * 현재 페이지 번호 반환
	 * @return 현재 페이지 번호 반환
	 */
	public int getPageNum() {
		return pageNum;
	}
	/**
	 * 한페이지에서 보여주는 데이터 수 반환
	 * @return 한페이지에서 보여주는 데이터 수 반환
	 */
	public int getPageSize() {
		return pageSize;
	}
	
	/**
	 * 총 페이지 수 반환  
	 * @return 총 페이지 수 
	 */
	public int getTotalPageNum() {
		return totalPageNum;
	}
 
	/**
	 * 한 화면에 보여줄 페이지 수 반환
	 * @return 한 화면에 보여줄 페이지 수 반환
	 */
	public int getPageGroupSize() {
		return pageGroupSize;
	}

	/**
	 * 페이지 번호의 이전 그룹 페이지 번호 반환 &lt;&lt; &lt; 1 2 3 &gt; &gt;&gt; 
	 * @return 페이지 번호의 이전 그룹 페이지 번호 반환 
	 */
	public int getPrevGroupPageNum() {
		return prevGroupPageNum;
	}

	/**
	 * 페이지 번호의 다음 그룹 페이지 번호 반환 &lt;&lt; &lt; 1 2 3 &gt; &gt;&gt;
	 * @return 페이지 번호의 다음 그룹 페이지 번호 반환 
	 */
	public int getNextGroupPageNum() {
		return nextGroupPageNum;
	}

	/**
	 * 시작페이지 번호 반환
	 * @return 시작페이지 번호 반환
	 */
	public int getStartPageNum() {
		return startPageNum;
	}

	/**
	 * 시작되는 데이터 row 번호 반환 
	 * @return 시작되는 데이터 row 번호 반환 
	 */
	public int getStartRecordNum() {
		return startRecordNum;
	}

 
	/**
	 * 페이지 번호 리스트 반환 
	 * @return int 타입 리스트
	 */
	public ArrayList<Integer> getList() {
		return list;
	}

 
	/**
	 * 마지막 페이지 그룹의 페이번호 반환 
	 * @return 마지막 페이지 그룹의 페이번호 반환
	 */
	public int getLastGroupStart() {
		return this.lastGroupStart;
	}
	
	private void setLastGroupStart() {
		if(this.totalPageNum <= this.pageGroupSize) {
			this.lastGroupStart = 1;
		} else {
			this.lastGroupStart = ( ( (this.totalPageNum - 1) / this.pageGroupSize ) * this.pageGroupSize) + 1 ;
			//int groupCnt = (totalPageNum - 1) / pageGroupSize;
		}
	}
	
	/**
	 * offset 반환 
	 * <br>- SQL상에서 offset 처리 할때 필요 한 offset 값  
	 * @return offset 반환
	 */
	public int getOffset() {
		return (this.pageNum - 1) * this.pageSize;
	}
	
	/**
	 * 해당 페이지에 대한 데이터 시작 row 번호 반환 
	 * <br>- SQL상에서 between 의 from 값
	 * @return 조회하려는 데이터의 start row 번호 반환
	 */
	public int getStartRow() {
		return ((this.pageNum - 1) * this.pageSize) + 1;
	}
	
	/**
	 * 해당 페이지에 대한 데이터 마지막 row 번호 반환 
	 * <br>- SQL상에서 between 의 to 값
	 * @return 조회하려는 데이터의 end row 번호 반환
	 */
	public int getEndRow() {
		return this.pageNum * this.pageSize;
	}
	
}
