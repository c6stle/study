package com.sangs.lib.support.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.sangs.lib.support.exception.SangsMessageException;


/**
 * Excel Marker
 * <br> - 단순한 형태의 엑셀 작성시 사용 
 * <pre>
 * 예)
 * 	List list = dao.selectList("test.testList", paramMap);
	SangsSimpleExcelMaker ssem = new SangsSimpleExcelMaker();
	Workbook workbook = ssem.createSheet()
		.setHeaderColNm(new String[] {"설문번호", "설문유형", "설문개설유형", "설문명", "설문내용", "사용여부", "노출시작일시", "노출종료일시", "등록자아이디", "등록일시"})		// 컬럼 해더의 한글명 셋팅
		.setHeaderColId(new String[] {"rn", "srvyTyCdNm", "srvyEstblTyCdNm", "srvyNm", "srvyCn", "useYn", "expsrBgngDt", "expsrEndDt", "regUserId", "regDt" })		// 컬럼의 아이디값 지정
		.setList(srvyList)	// 목록
		.setAutoSize()	// 자동 cell 조정
		.getWorkbook();	// 셋팅한 정보 workbook 반환
 * </pre>
 * 
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
public class SangsSimpleExcelMaker {

	private HSSFWorkbook workbook;
	private HSSFSheet sheet;
	private List<String> headerColIdList = null;
	private List<Object> headerColNmList = null;
	//private final boolean onlyStringVal = true;
	private int currentRow = 0;
	private int maxColCount = 0;
	//private String fileName;
	
	public CellStyle headerCellStyle;
	public CellStyle bodyCellStyle;
	public Font headerFont;
	public Font bodyFont;
	
	/**
	 * 생성자 (클래스 초기화)
	 */
	public SangsSimpleExcelMaker() {
		this.initialClass();
	}
	/*
	public SimpleExcelMaker(String fileName) {
		this.fileName = fileName;
	}
	*/
	/**
	 * 클래스 초기화
	 */
	public void initialClass() {
		this.workbook = new HSSFWorkbook();
		this.currentRow = 0;
		setDefaultCellStyle();
	}
	/*
	public String getName() {
		return this.fileName;
	}
	*/
	/**
	 * 작성된 워크북을 반환합니다.
	 * @return Workbook Object 
	 */
	public HSSFWorkbook getWorkbook() {
		return this.workbook;
	}
	
	/**
	 * Excel의 sheet 를 생성합니다.(default sheet명 :Sheet1)
	 * 
	 * @return SangsSimpleExcelMaker Object
	 */
	public SangsSimpleExcelMaker createSheet() {
		return this.createSheet("Sheet1");
	}
	
	/**
	 * Excel의 sheet 를 생성합니다.
	 * 
	 * @param sheetName 생성할 sheet명
	 * @return SangsSimpleExcelMaker Object
	 */
	public SangsSimpleExcelMaker createSheet(String sheetName) {
		// 새 시트 추가할때 앞에 생성했던 시트의 row 수만큼 빈 row가 생김 currentRow 초기화 하여 해결
		currentRow = 0;
		sheet = workbook.createSheet(sheetName);
		return this;
	}
	
	
	/**
	 * 기본 cell 스타일을 생성합니다.
	 */
	private void setDefaultCellStyle() {
		String fontName = "맑은 고딕";

		// header font
		headerFont = workbook.createFont();
		headerFont.setFontName(fontName);
		headerFont.setBold(true);

		// body font
		bodyFont = workbook.createFont();
		bodyFont.setFontName(fontName);
		
		
		// header style
		headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setBorderRight(BorderStyle.THIN);
		headerCellStyle.setBorderLeft(BorderStyle.THIN);
		headerCellStyle.setBorderTop(BorderStyle.THIN);
		headerCellStyle.setBorderBottom(BorderStyle.THIN);
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setFillForegroundColor(workbook.getCustomPalette().findSimilarColor(211, 211, 211).getIndex());
		headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		
		// body style
		bodyCellStyle = workbook.createCellStyle();
		bodyCellStyle.setBorderRight(BorderStyle.THIN);
		bodyCellStyle.setBorderLeft(BorderStyle.THIN);
		bodyCellStyle.setBorderTop(BorderStyle.THIN);
		bodyCellStyle.setBorderBottom(BorderStyle.THIN);
		bodyCellStyle.setFont(bodyFont);
	}
	
	/**
	 * Excel Header의 아이디를 설정
	 * <br> - setHeaderColId(new String[] {"rn", "srvyTyCdNm", "srvyEstblTyCdNm", "srvyNm", "srvyCn", "useYn", "expsrBgngDt", "expsrEndDt", "regUserId", "regDt" })		// 컬럼의 아이디값 지정
	 * 
	 * @param colIds String...
	 * @return SangsSimpleExcelMaker Object
	 */
	public SangsSimpleExcelMaker setHeaderColId(String... colIds) {
		if(maxColCount < colIds.length)
			maxColCount = colIds.length;
		headerColIdList = new ArrayList<String>(Arrays.asList(colIds));
		return this;
	}
	/**
	 * Excel Header부분에 들어갈 컬럼명 설정
	 * <br> - setHeaderColNm(new String[] {"설문번호", "설문유형", "설문개설유형", "설문명", "설문내용", "사용여부", "노출시작일시", "노출종료일시", "등록자아이디", "등록일시"})		// 컬럼 해더의 한글명 셋팅
	 * @param colNms String...
	 * @return SangsSimpleExcelMaker Object
	 */
	public SangsSimpleExcelMaker setHeaderColNm(String... colNms) {
		headerColNmList = new ArrayList<Object>(Arrays.asList(colNms));
		return this;
	}
	
	 
	/*
	public SangsSimpleExcelMaker setOnlyStringVal(boolean onlyStringVal) {
		this.onlyStringVal = onlyStringVal;
		return this;
	}
	*/
	
	 
	/*
	public SangsSimpleExcelMaker addHeaderRow() throws Exception {
		return this.addHeaderRow(this.headerColNmList);
	}
	*/
	/**
	 * Excel의 Head row를 추가
	 * 
	 * @param colValList Header의 값 리스트 
	 * @return SangsSimpleExcelMaker Object
	 * @throws Exception Exception
	 */
	public SangsSimpleExcelMaker addHeaderRow(List<Object> colValList) throws Exception {
		return addRow(colValList, headerCellStyle);
	}
	/**
	 * Excel의 Body row를 추가
	 * 
	 * @param colValList Body의 값 리스트
	 * @return SangsSimpleExcelMaker Object
	 * @throws Exception Exception
	 */
	public SangsSimpleExcelMaker addBodyRow(List<Object> colValList) throws Exception {
		return addRow(colValList, bodyCellStyle);
	}
	
	private SangsSimpleExcelMaker addRow(CellInfo... cellInfos) throws Exception {
		HSSFRow row = sheet.createRow(currentRow);
		if(maxColCount < cellInfos.length)
			maxColCount = cellInfos.length;
		
		int currentColIdx = 0;
		for(int i = 0 ; i < cellInfos.length; i++) {
			
			Cell cell = row.createCell(currentColIdx);
			cell.setCellValue(cellInfos[i].cellVal);
			cell.setCellStyle(cellInfos[i].cellStyle);
			
			if(cellInfos[i].colspan != 1) { // cell merge
				int mergeEndColIdx = currentColIdx+cellInfos[i].colspan -1;
				
				for(int j = (currentColIdx +1); j < (mergeEndColIdx + 1) ; j++) { // for border
					Cell tempCell = row.createCell(j);
					tempCell.setCellStyle(cellInfos[i].cellStyle);
				}
				
				sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, currentColIdx, mergeEndColIdx));
				currentColIdx = mergeEndColIdx + 1;
			} else {
				currentColIdx++;
			}
			
		}
		currentRow++;
		return this;
	}
	/**
	 * Excel의 Row 추가 
	 * 
	 * @param colValList 컬럼값 목록
	 * @param cellStyle CellStyle
	 * @return SangsSimpleExcelMaker Object
	 * @throws Exception Exception
	 */
	public SangsSimpleExcelMaker addRow(List<Object> colValList, CellStyle cellStyle) throws Exception {
		HSSFRow row = sheet.createRow(currentRow);
		for(int i = 0 ; i < colValList.size() ; i++) {
			//if(onlyStringVal) {
				Cell cell = row.createCell(i);
				cell.setCellStyle(cellStyle);
				if(colValList.get(i) != null) 
					cell.setCellValue(colValList.get(i).toString());
				else 
					cell.setCellValue("");
			//} else {
			//	throw new SangsMessageException("not support yet");
			//}
		}
		currentRow++;
		return this;
	}
	
	/**
	 * Excel 빈 Row 한줄 추가 
	 * 
	 * @return SangsSimpleExcelMaker Object
	 */
	public SangsSimpleExcelMaker addEmptyRow() {
		HSSFRow row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("");
		currentRow++;
		return this;
	}
	
	/**
	 * Excel에 작성할 bodyList가 (목록 내용) 없는경우 조회된 데이터가 없음처럼 전체 셀을 merge후 메시지를 중앙정렬로 표기함
	 *  
	 * @param noDataMessage 표기할 내용
	 * @return SangsSimpleExcelMaker Object
	 * @throws Exception Exception
	 */
	public SangsSimpleExcelMaker addNoDataRow(String noDataMessage) throws Exception {
		return this.addRow(this.setCell(noDataMessage, this.bodyCellStyle, this.getHeaderColIdList().size(), HorizontalAlignment.CENTER)); 
	}
	/**
	 * Excel 내용에 들어갈 목록(list)를 set 
	 * 
	 * @param list Excel 내용에 들어갈 목록
	 * @param <T> the type of the element
	 * @return SangsSimpleExcelMaker Object
	 * @throws Exception Exception
	 */
	public <T extends Map<String, Object>> SangsSimpleExcelMaker setList(List<T> list) throws Exception {
		if(list == null)
			return this;
		
		if(this.headerColIdList == null || this.headerColIdList.size() == 0) 
			throw new SangsMessageException("header column is list is null Exception.. you should be call setHeaderColId method.. ");
		
		try {
			if(headerColNmList != null) {
				this.addHeaderRow(headerColNmList);
				headerColNmList = null;
			}
			
			if(list.size() == 0)
				return this.addNoDataRow("조회된 데이터가 없습니다");
			
			for(T map : list) {
				List<Object> colValList = new ArrayList<Object>();
				for(int i = 0 ; i < this.headerColIdList.size() ; i++) {
					if(map.containsKey(this.headerColIdList.get(i)))
						colValList.add(map.get(this.headerColIdList.get(i)));
					else 
						colValList.add("");
				}
				this.addBodyRow(colValList);
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new SangsMessageException("mapping Exception");
		}
		
		return this;
	}
	
	/**
	 * 자동 컬럼 사이즈 조정
	 *  
	 * @return SangsSimpleExcelMaker Object
	 */
	public SangsSimpleExcelMaker setAutoSize() {
		for(int i = 0 ; i < maxColCount; i++) {
			sheet.autoSizeColumn(i);
			int width = Math.min(255 * 256, sheet.getColumnWidth(i) + 1200);
			sheet.setColumnWidth(i, width);
		}
		return this;
	}
	
	/**
	 * Excel Header 의 아이디 목록 반환 
	 * 
	 * @return list of String
	 */
	public List<String> getHeaderColIdList() {
		return this.headerColIdList;
	}
	/**
	 * Excel Header의 명(한글명) 목록 반환
	 *  
	 * @return  list of Object
	 */
	public List<Object> getHeaderColNmList() {
		return this.headerColNmList;
	}
	
	
	
	/**
	 * 
	 * Cell 정보 Class
	 *
	 */
	public class CellInfo {
		public CellStyle cellStyle;
		public String cellVal;
		public int colspan;
		public CellInfo(String cellVal, CellStyle cellStyle, int colspan) {
			this.cellVal = cellVal;
			this.cellStyle = cellStyle;
			this.colspan = colspan;
		}
		public CellInfo(String cellVal, CellStyle cellStyle, int colspan, HorizontalAlignment align) {
			this.cellVal = cellVal;
			this.cellStyle = cellStyle;
			this.colspan = colspan;
			this.cellStyle.setAlignment(align);
		}
	}
	
	/**
	 * Cell 정보 set
	 * 
	 * @param cellVal cell값
	 * @param cellStyle cell style (header style, body style)
	 * @return CellInfo Object
	 */
	public CellInfo setCell(String cellVal, CellStyle cellStyle) {
		return new CellInfo(cellVal, cellStyle, 1);
	}
	/**
	 * Cell 정보 set
	 * 
	 * @param cellVal cell값
	 * @param cellStyle cell style (header style, body style)
	 * @param colspan merge 할 값 
	 * @return CellInfo Object
	 */
	public CellInfo setCell(String cellVal, CellStyle cellStyle, int colspan) {
		return new CellInfo(cellVal, cellStyle, colspan);
	}
	/**
	 * Cell 정보 set
	 * 
	 * @param cellVal cell값
	 * @param cellStyle cell style (header style, body style)
	 * @param colspan merge 할 값 
	 * @param align 정렬(HorizontalAlignment type)
	 * @return CellInfo Object
	 */
	public CellInfo setCell(String cellVal, CellStyle cellStyle, int colspan, HorizontalAlignment align) {
		return new CellInfo(cellVal, cellStyle, colspan, align);
	}
	
	
	
	
	
	
	
	
}
