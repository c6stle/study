package com.sangs.lib.support.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import com.sangs.lib.support.exception.SangsMessageException;

/**
 * File 관련 Util
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
public class SangsFileUtil {

	
	
	/**
	 * 파일을 읽어서 내용을 text 로 반환한다. 
	 * 
	 * @param fileFullPath 문리적 파일 경로
	 * @return StringBuffer object
	 * @throws Exception Exception
	 */
	public static StringBuffer getFileText(String fileFullPath) throws Exception {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		InputStreamReader reader = null;
		FileInputStream input = null;
		try {
			
			input = new FileInputStream(fileFullPath);
			reader = new InputStreamReader(input, "euc-kr");
            br = new BufferedReader(reader);
            String line = "";
            while((line = br.readLine()) != null){
            	sb.append(line).append("\n");
            }
		} catch (FileNotFoundException e) {
			throw new SangsMessageException("file not found Exception ["+fileFullPath+"]");
		} catch(Exception e){
			throw e;
		} finally {
			try {
				if(br != null) {
					br.close();
					br = null;
				}
			} catch (Exception e) {
				throw new SangsMessageException(e.getMessage());
			}
			
			try {
				if(reader != null) {
					reader.close();
					reader = null;
				}
			} catch (Exception e) {
				throw new SangsMessageException(e.getMessage());
			}
			
			try {
				if(input != null) {
					input.close();
					input = null;
				}
			} catch (Exception e) {
				throw new SangsMessageException(e.getMessage());
			}
		}
		return sb;
	}

	/**
	 * 유니크 파일명 채번
	 * <br> - 파일명 앞에 prefix로 현재 날짜(yyyyMMdd형식)이 붙습니다.
	 * 
	 * @return 유니크 파일명 채번 
	 */
	public static String makeUniqueFileNm() {
		return makeUniqueFileNm("yyyyMMdd");
	}
	
	/**
	 * 유니크 파일명 채번
	 * <br> - 파일명 앞에 prefix로 현재 날짜(입력값 prefixDateFormat 형식으로)이 붙습니다. 
	 *  
	 * @param prefixDateFormat 파일명 앞에 prefix로 현재 날짜의 포맷
	 * @return 유니크 파일명 채번 
	 */
	public static String makeUniqueFileNm(String prefixDateFormat) {
		String prefixDate = "";
		if(!SangsStringUtil.isEmpty(prefixDateFormat))
			prefixDate = SangsDateUtil.getToday(prefixDateFormat);
		
		return prefixDate + UUID.randomUUID().toString();
	}
	
	/**
	 * 경로가 포함된경우 경로를 제외한 파일명만 unique 한 파일명으로 변환하여 반환
	 * <br> - 파일명 앞에 prefix로 현재 날짜(yyyyMMdd형식)이 붙습니다.
	 * @param fileNm 경로가 포함된 파일명
	 * @return 파일명
	 */
	public static String convertUniqueFileNm(String fileNm) {
		return convertUniqueFileNm("yyyyMMdd", fileNm);
	}
	/**
	 * 경로가 포함된경우 경로를 제외한 파일명만 unique 한 파일명으로 변환하여 반환 
	 * <br> - 파일명 앞에 prefix로 현재 날짜(입력값 prefixDateFormat 형식으로)이 붙습니다.  
	 * @param prefixDateFormat 파일명 앞에 prefix로 현재 날짜의 포맷
	 * @param fileNm 경로가 포함된 파일명
	 * @return 파일명
	 */
	public static String convertUniqueFileNm(String prefixDateFormat, String fileNm) {
		
		String uniquFileNm = makeUniqueFileNm(prefixDateFormat);
		String filePath = "";
		if(fileNm.indexOf("/") >= 0) {
			filePath = fileNm.substring(0, fileNm.lastIndexOf("/")) + "/";
		}
		String extFileNm = fileNm.substring(fileNm.lastIndexOf("."), fileNm.length());
		
		
		return filePath  + uniquFileNm + extFileNm;
	}
	
	
	/**
	 * 파일 복사
	 * 
	 * @param strSourceFile 원본 파일경로
	 * @param strTargetFile 저장할 파일경로
	 * @throws Exception Exception
	 */
	public static void copyFile(String strSourceFile, String strTargetFile) throws Exception {
		FileInputStream input = null;
		FileOutputStream output = null;
		
		try {
			
			// 1. 원본 File, 복사할 File 준비
			File sourceFile = new File(strSourceFile);
			File targetFile = new File(strTargetFile);
			
			if(!sourceFile.exists())
				throw new SangsMessageException("파일이 존재 하지 않습니다. [" + strSourceFile + "]");

			
			// 2. FileInputStream, FileOutputStream 준비
			input = new FileInputStream(sourceFile);
			output = new FileOutputStream(targetFile);
			
			// 3. 한번에 read하고, write할 사이즈 지정
			byte[] buf = new byte[1024];
			
			// 4. buf 사이즈만큼 input에서 데이터를 읽어서, output에 쓴다.
			int readData;
			while ((readData = input.read(buf)) > 0) {
				output.write(buf, 0, readData);
			}
			
			// 5. Stream close
			input.close();
			output.close();
			
		} catch(Exception e) {
			throw e;
		} finally {
			if(input != null)
				input.close();
			if(output != null)
				output.close();
		}
	}
	
	
}
