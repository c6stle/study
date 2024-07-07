package com.sangs.collector.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

/**
 * CSV, XLS, XLSX 파일 리더 유틸리티
 *
 * @return String[][]
 * @author Administrator
 *
 */

public class ExcelReaderUtil {

	public final static String[][] simpleExcelReadCsv(File targetFile) throws Exception {

		String[][] data = null;
		FileInputStream csvFile = new FileInputStream(targetFile);
		InputStreamReader readFile = new InputStreamReader(csvFile, "EUC-KR");

		CSVReader reader = new CSVReader(readFile);
		CSVReader tmpReader = new CSVReader(new FileReader(targetFile));

		try{
			int colCount = tmpReader.readNext().length;
			List tmpList = tmpReader.readAll();
			int rowCount = tmpList.size()+1;
			//if(rowCount > 203){ rowCount = 203; }
			data = new String[rowCount][colCount];

			int idx = 0;
			String[] nextLine;
			while ( (nextLine = reader.readNext()) != null ){
				int i = 0;

				for ( String str : nextLine ){
					data[idx][i] = str;
					//System.out.println("idx : " + idx + " i : " + i +" data : " + str);
					i++;
				}
				idx++;
			}
/*
			//데이터 검증 테스트
			for (int r = 0; r < data.length; r++) {
				for (int c = 0; c < data[0].length; c++) {
					System.out.print( r + " : " + c + " : " + data[r][c] + " ");
				}
				System.out.println();
			}
*/
		} catch (Exception e) {

			System.out.println("CSVReader error : " + e.getMessage());
		}
		return data;
	}

	/* Poi 사용 */
	public final static String[][] simpleExcelReadPoi(File targetFile) throws Exception{
		return simpleExcelReadPoi(targetFile, false, 200);
	}
	public final static String[][] simpleExcelReadPoi(File targetFile, boolean strOnlyFlag, int limitRow) throws Exception{
		org.apache.poi.ss.usermodel.Workbook workbook = null;
		org.apache.poi.ss.usermodel.Sheet sheet = null;
		org.apache.poi.ss.usermodel.Row row = null;
		org.apache.poi.ss.usermodel.Cell cell = null;
		String[][] data = null;
		try {
			workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(targetFile);
			org.apache.poi.ss.usermodel.FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			sheet = workbook.getSheetAt(0);

			int rows = sheet.getLastRowNum()+1;
			int cells = sheet.getRow(0).getLastCellNum();
			//if(rows > 200){ rows = 200; }
			data = new String[rows][cells];
			int idx = 0;
			for(Iterator all = sheet.iterator(); all.hasNext(); ){
				org.apache.poi.ss.usermodel.Row ds = (org.apache.poi.ss.usermodel.Row)all.next();
				for(int i = 0; i< cells; i++){
					if(ds == null)
						continue;
					cell = ds.getCell(i);
					if(cell == null)
						continue;

					if(strOnlyFlag) {
						data[idx][i] = cell.toString();
					} else {


					switch(cell.getCellType().ordinal()){
						case 0 :  //Cell.CELL_TYPE_NUMERIC :
							if(org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)){
								data[idx][i] = cell.getDateCellValue().toString();
							} else{
								//data[idx][i] = Integer.toString((int) cell.getNumericCellValue());
								data[idx][i] = Double.toString((double) cell.getNumericCellValue());
							}
							break;
						case 1 : //Cell.CELL_TYPE_STRING :
							data[idx][i] = cell.getRichStringCellValue().getString();
							break;
						//case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN :
						case 4 :
							data[idx][i] = cell.getBooleanCellValue()+"";
							break;
						//case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA :
						case 2 :
							//if(evaluator.evaluateFormulaCell(cell) == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC){
							if(evaluator.evaluateFormulaCell(cell).ordinal() == 0){
								if(org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)){
									data[idx][i] = "";
								} else{
									Double value = cell.getNumericCellValue();
			                        if((double)value.longValue() == value.doubleValue()){
			                        	data[idx][i] = data[idx][i] = Long.toString(value.longValue());
			                        } else{
			                        	data[idx][i] = data[idx][i] = value.toString();
									}
								}
							//} else if(evaluator.evaluateFormulaCell(cell) == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING){
							} else if(evaluator.evaluateFormulaCell(cell).ordinal() == 1){
								data[idx][i] = cell.getStringCellValue();
							//} else if(evaluator.evaluateFormulaCell(cell) == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN){
							} else if(evaluator.evaluateFormulaCell(cell).ordinal() == 4){
								data[idx][i] = String.valueOf(cell.getBooleanCellValue());
							} else {
								data[idx][i] = cell.toString();
							}
							break;
						default:
					}
					}
				}
				//if(idx == 200){
				if(idx == limitRow) {
					break;
				}
				idx++;
			}
/*
			// 데이터 검증 테스트
			for (int r = 0; r < data.length; r++) {
				for (int c = 0; c < data[0].length; c++) {
					System.out.println("index : " + r + " : " + data[r][c]);
				}
			}
*/
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("ExcelReadPoi error : " + e.getMessage());
		}
		return data;
	}

	/* JXL을 사용한 excelRead */
	public final static String[][] simpleExcelReadJxl(File targetFile) throws Exception {
		jxl.Workbook workbook = null;
		jxl.Sheet sheet = null;
		String[][] data = null;

		try {
			workbook = jxl.Workbook.getWorkbook(targetFile); // 존재하는 엑셀파일 경로를 지정
			sheet = workbook.getSheet(0); // 첫번째 시트를 지정합니다.
			int rowCount = sheet.getRows(); // 총 로우수를 가져옵니다.
			int colCount = sheet.getColumns(); // 총 열의 수를 가져옵니다.
			if (rowCount <= 0) {
				throw new Exception("Read 할 데이터가 엑셀에 존재하지 않습니다.");
			}
			data = new String[rowCount][colCount];
			// 엑셀데이터를 배열에 저장
			for (int i = 0; i < rowCount; i++) {
				for (int k = 0; k < colCount; k++) {
					jxl.Cell cell = sheet.getCell(k, i); // 해당위치의 셀을 가져오는 부분입니다.
					if (cell == null)
						continue;
					System.out.println(cell.getContents());
					data[i][k] = cell.getContents(); // 가져온 셀의 실제 콘텐츠 즉
														// 데이터(문자열)를 가져오는 부분입니다.
				}
			}
/*
			// 데이터 검증 테스트
			for (int r = 0; r < data.length; r++) {
				for (int c = 0; c < data[0].length; c++) {
					System.out.print(data[r][c] + " ");
				}
				System.out.println();
			}
*/
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (workbook != null)
					workbook.close();
			} catch (Exception e) {
			}
		}
		return data;
	}
/**/
}
