package com.sangs.collector.util;

import java.io.File;

public class SangsFileUtil {

	/**
	 * 파일을 삭제함
	 * @param file_nm /파일이름
	 * @param filePath //파일 실제경로
	 * @throws Exception
	 */
	public static void deleteFile(String fileName, String filePath) throws Exception  {

		File f = new File(filePath +  File.separator + fileName); // 파일 객체생성
		if( f.exists()) f.delete(); // 파일이 존재하면 파일을 삭제한다.

		//thumnail쪽도 삭제
		File thumb = new File(filePath + "thumb/" + fileName); // 파일 객체생성
		if( thumb.exists()) thumb.delete(); // 파일이 존재하면 파일을 삭제한다.

	}

}
