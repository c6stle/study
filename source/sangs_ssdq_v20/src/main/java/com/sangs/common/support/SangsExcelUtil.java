package com.sangs.common.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.sangs.lib.support.domain.SangsMap;
import com.sangs.lib.support.utils.SangsStringUtil;

/**
 * Excel 관련 Util (poi 기반)
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
public class SangsExcelUtil {

	/**
	 * 엑셀파일을 로드 하여 Map형태의 리스트를 반환한다.
	 * 
	 * @param fileFullPath 파일 Full경로
	 * @param skipRowCnt skip할 로우 수
	 * @param skipColumnCnt skip할 컬럼 수
	 * @param columnCount 컬럼수 
	 * @return Map형태의 리스트
	 * @throws Exception Exception
	 */
	public static ArrayList<SangsMap> loadExcelList(String fileFullPath, int skipRowCnt, int skipColumnCnt, int columnCount) throws Exception {
		return loadExcelList(0, fileFullPath, skipRowCnt, skipColumnCnt, columnCount, true);
	}
	/**
	 * 엑셀파일을 로드 하여 Map형태의 리스트를 반환한다.
	 * 
	 * @param fileFullPath 파일 Full경로
	 * @param skipRowCnt skip할 로우 수
	 * @param skipColumnCnt skip할 컬럼 수
	 * @param columnNames 컬럼명 배열을 입력받아 List 안에 있는 Map의 Key로 설정된다.
	 * @return Map형태의 리스트
	 * @throws Exception Exception
	 */
	public static ArrayList<SangsMap> loadExcelList(String fileFullPath, int skipRowCnt, int skipColumnCnt, String[] columnNames) throws Exception {
		return loadExcelList(0, fileFullPath, skipRowCnt, skipColumnCnt, columnNames, true);
	}
	/**
	 * 엑셀파일을 로드 하여 Map형태의 리스트를 반환한다.
	 * 
	 * @param fileFullPath 파일 Full경로
	 * @param skipRowCnt skip할 로우 수
	 * @param skipColumnCnt skip할 컬럼 수
	 * @param columnCount 컬럼수
	 * @param valueStringOnly true 입력시 데이터 값을 String으로 반환한다.
	 * @return Map형태의 리스트
	 * @throws Exception Exception
	 */
	public static ArrayList<SangsMap> loadExcelList(String fileFullPath, int skipRowCnt, int skipColumnCnt, int columnCount, boolean valueStringOnly) throws Exception {
		return loadExcelList(0, fileFullPath, skipRowCnt, skipColumnCnt, columnCount, valueStringOnly);
	}
	/**
	 * 엑셀파일을 로드 하여 Map형태의 리스트를 반환한다.
	 * 
	 * @param fileFullPath 파일 Full경로
	 * @param skipRowCnt skip할 로우 수
	 * @param skipColumnCnt skip할 컬럼 수
	 * @param columnNames 컬럼명 배열을 입력받아 List 안에 있는 Map의 Key로 설정된다.
	 * @param valueStringOnly true 입력시 데이터 값을 String으로 반환한다.
	 * @return Map형태의 리스트
	 * @throws Exception Exception
	 */
	public static ArrayList<SangsMap> loadExcelList(String fileFullPath, int skipRowCnt, int skipColumnCnt, String[] columnNames, boolean valueStringOnly) throws Exception {
		return loadExcelList(0, fileFullPath, skipRowCnt, skipColumnCnt, columnNames, valueStringOnly);
	}
	
	
	/**
	 * 엑셀파일을 로드 하여 Map형태의 리스트를 반환한다.
	 * 
	 * @param sheetIndex sheet index (첫번째 시트 index : 0) 
	 * @param fileFullPath 파일 Full경로
	 * @param skipRowCnt skip할 로우 수
	 * @param skipColumnCnt skip할 컬럼 수
	 * @param columnCount 컬럼수
	 * @param valueStringOnly true 입력시 데이터 값을 String으로 반환한다.
	 * @return  Map형태의 리스트
	 * @throws Exception Exception
	 */
	public static ArrayList<SangsMap> loadExcelList(int sheetIndex, String fileFullPath, int skipRowCnt, int skipColumnCnt, int columnCount, boolean valueStringOnly) throws Exception {
		
		String[] columnNames = new String[columnCount];
		for(int i = 0 ; i < columnCount ; i++) {
			columnNames[i] = "col" + i;
		}
		return loadExcelList(sheetIndex, fileFullPath, skipRowCnt, skipColumnCnt, columnNames, valueStringOnly);
	}
	/**
	 * 엑셀파일을 로드 하여 Map형태의 리스트를 반환한다.
	 * 
	 * @param sheetIndex sheet index (첫번째 시트 index : 0) 
	 * @param fileFullPath 파일 Full경로
	 * @param skipRowCnt skip할 로우 수
	 * @param skipColumnCnt skip할 컬럼 수
	 * @param columnNames 컬럼명 배열을 입력받아 List 안에 있는 Map의 Key로 설정된다.
	 * @param valueStringOnly true 입력시 데이터 값을 String으로 반환한다.
	 * @return Map형태의 리스트
	 * @throws Exception Exception
	 */
	public static ArrayList<SangsMap> loadExcelList(int sheetIndex, String fileFullPath, int skipRowCnt, int skipColumnCnt, String[] columnNames, boolean valueStringOnly) throws Exception {
		ArrayList<SangsMap> rtnList = new ArrayList<SangsMap>();

		FileInputStream fis = null;
		//POIFSFileSystem fs = null;
		Workbook wb = null;

		try {
			//fis = new FileInputStream(fileFullPath);
			//fs = new POIFSFileSystem(fis);
			//wb = new HSSFWorkbook(fs);
			wb = WorkbookFactory.create(new File(fileFullPath));
			Sheet sheet = wb.getSheetAt(sheetIndex);
			
			int curRowNo = 1; 
			int rows = sheet.getPhysicalNumberOfRows();
			for (int r = 0; r < rows; r++) {
				if (r < skipRowCnt)
					continue;

				Row row = sheet.getRow(r);
				if (row != null) {
					// int cells = row.getPhysicalNumberOfCells();
					SangsMap smap = new SangsMap();

					int chkColCnt = 0;
					for (int c = skipColumnCnt; c < columnNames.length; c++) {
						Cell cell = row.getCell(c);

						if (cell != null) {
							String value = "";

							if(valueStringOnly) {
								cell.setCellType(CellType.STRING);
								value = cell.getStringCellValue();
							} else {
								switch (cell.getCellType()) {
		
									case FORMULA:
										value = "=" + cell.getCellFormula();
										break;
									case NUMERIC:
										if (DateUtil.isCellDateFormatted(cell)) {
											SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
											value = sdf.format(cell.getDateCellValue());
										} else {
											value = String.valueOf((Double) cell.getNumericCellValue());
										}
										break;
									case STRING:
										value = cell.getStringCellValue();
										break;
									case BLANK:
										value = String.valueOf(cell.getBooleanCellValue());
										break;
									case ERROR:
										value = String.valueOf(cell.getErrorCellValue());
										break;
									default:
								}
							}
							if ("false".equals(value))
								value = "";							

							value = (SangsStringUtil.nvl(value, "")).trim();
							smap.putOrg(columnNames[c], value);
							//smap.put("col" + c, value);
							
							chkColCnt++;
//							if (!"".equals(value)) {
//								smap.put("col" + c, value);
//								chkColCnt++;
//							}
						} else {
							// cell null 일때 추가
							smap.putOrg(columnNames[c], "");
						}
					}
					if (chkColCnt > 0) {
						smap.putOrg("EXCEL_ROW_NO", curRowNo++);
						rtnList.add(smap);
					}
				}
			}
        } catch(FileNotFoundException e) {
        	e.printStackTrace();
            throw e;
        } catch(IOException e) {
        	e.printStackTrace();
            throw e;
        } catch(Exception e) {
        	e.printStackTrace();
            throw e;
        } finally {
     	   if(wb != null) wb.close();
     	   //if(fs != null) fs.close();
     	   if(fis != null) fis.close();
        }

		return rtnList;
	}

}
