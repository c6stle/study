package com.sangs.collector.util;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;

import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.exception.SangsMessageException;

/**
 * Sangs Util
 * @author soundheaven1
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      	수정자           		수정내용
 *  -------        --------    		---------------------------
 *  2018.11.21	   sangs
 *
 * </pre>
 *
 */
public class SangsExcelDownUtil {

	private static Logger logger = LoggerFactory.getLogger("com.sangs.collector.util.SangsExcelDownUtil");

	/**
	 * 엑셀파일다운로드 POI
	 * @param map // 리스트
	 * @throws Exception
	 */
	public static void downExcleFile(@RequestParam HashMap<String, Object> map, HttpServletRequest req, HttpServletResponse res) throws Exception  {
		/*
		 * not used
		 * Gson gson = new Gson();
		 * String json = null;
		 */
	    String sFileName = "COMPANY_ORDER" + ".xlsx";
	    sFileName = new String ( sFileName.getBytes("KSC5601"), "8859_1");

	    res.reset();  //엑셀 한글 깨짐 방지

	    String strClient = req.getHeader("User-Agent");
	    String fileName = sFileName;

	    if (strClient.indexOf("MSIE 5.5") > -1) {
	        res.setHeader("Content-Disposition", "filename=" + fileName + ";");
	    } else {
	        res.setContentType("application/vnd.ms-excel");
	        res.setHeader("Content-Disposition", "attachment; filename=" + fileName + ";");
	    }

	    OutputStream fileOut = null;
	    XSSFWorkbook objWorkBook = new XSSFWorkbook();

	    try {
	    	XSSFSheet objSheet = objWorkBook.createSheet("COMPANY_ORDER");
	    	XSSFRow objRow = null;
	    	XSSFCell objCell = null;
			/*
			 * not used
			 * DataValidation dataValidation = null;
			 * DataValidationConstraint constraint = null;
			 * DataValidationHelper validationHelper = null;
			 */

	    	// Cell style(border & fontColor-BLUE)
	    	XSSFCellStyle blueStyle = objWorkBook.createCellStyle();
	    	blueStyle.setBorderTop(BorderStyle.THIN);
	    	blueStyle.setBorderBottom(BorderStyle.THIN);
	    	blueStyle.setBorderLeft(BorderStyle.THIN);
	    	blueStyle.setBorderRight(BorderStyle.THIN);
	    	Font blueFont = objWorkBook.createFont();
	    	blueFont.setColor(IndexedColors.BLUE.getIndex());
	    	blueStyle.setFont(blueFont);

	    	// Cell style(border & backgroundColor & align)
	    	XSSFCellStyle criticalStyle = objWorkBook.createCellStyle();
	    	criticalStyle.setFillForegroundColor(IndexedColors.RED.index);
	    	criticalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    	criticalStyle.setBorderTop(BorderStyle.THIN);
	    	criticalStyle.setBorderBottom(BorderStyle.THIN);
	    	criticalStyle.setBorderLeft(BorderStyle.THIN);
	    	criticalStyle.setBorderRight(BorderStyle.THIN);
	    	criticalStyle.setAlignment(HorizontalAlignment.CENTER);
	    	Font whiteFont = objWorkBook.createFont();
	    	whiteFont.setColor(IndexedColors.WHITE.getIndex());
	    	criticalStyle.setFont(whiteFont);

	    	objRow = objSheet.createRow(0);
	    	//헤더 생성
	    	List<SangsMap> list = (List<SangsMap>) map.get("resultList");
	    	int index = 0;
	    	for (SangsMap item : list) {
	    		objCell = objRow.createCell(index++);
	    		objCell.setCellValue(item.getString("rcptnNm"));
	    	}

	    	int startRowNum = 3;
	    	int endRowNum = map.size()+startRowNum; // 시작Row + 데이터 길이
			/*
			 * not used
			 * int total = 0;
			 */
	    	for(int i = startRowNum; i < endRowNum; i++){

	    		objRow = objSheet.createRow(i);
	    		objCell = objRow.createCell(0);
	    		objCell.setCellValue("test1");
	    		objCell = objRow.createCell(1);
	    		objCell.setCellValue("test2");
	    		objCell = objRow.createCell(2);
	    		objCell.setCellValue("test3");
	    		objCell = objRow.createCell(3);
	    		objCell.setCellValue("test4");

	    	}

	    	fileOut = res.getOutputStream();
	    	objWorkBook.write(fileOut);

	    } catch (Exception e) {
	    	logger.info("", e);
	    	throw new SangsMessageException("처리중 에러가 발생하였습니다.");

	    } finally {
	    	if (objWorkBook != null) {
	    		objWorkBook.close();
	    	}
	    	if (fileOut != null) {
	    		fileOut.close();
	    	}
	    }
    }


}

