package com.sangs.lib.support.utils;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sangs.lib.support.exception.SangsMessageException;

/**
 * CSV 파일 로드 Util
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
public class SangsCsvFileLoader {

	
	private final String filePath;
	private final String delim;
	private final List<String> lineList;
	private final List<String> headerList;
	private final List<Map<String, String>> bodyList;
	private final boolean firstHeadRow;
	private final boolean trim;
	private long fileSize = 0;

	
	/**
	 * SangsCsvFileLoader 생성자
	 *  
	 * @param filePath CSV 파일 Full 경로
	 * @param delim CSV 구분자
	 * @param firstHeadRow 첫번째 row 가 Head일때 true
	 * @param trim 데이터에 대한 trim 처리 할것인지여부 true 일때 trim처리
	 */
	public SangsCsvFileLoader(String filePath, String delim, boolean firstHeadRow, boolean trim) {
		this.filePath = filePath;
		this.delim = delim;
		this.lineList = new ArrayList<String>();
		this.headerList = new ArrayList<String>();
		this.bodyList = new ArrayList<Map<String, String>>();
		this.firstHeadRow = firstHeadRow;
		this.trim = trim;
		this.loadFile();
	}
	/**
	 * SangsCsvFileLoader 생성자
	 * 
	 * @param filePath CSV 파일 Full 경로
	 * @param firstHeadRow 첫번째 row 가 Head일때 true
	 */
	public SangsCsvFileLoader(String filePath, boolean firstHeadRow) {
		this.filePath = filePath;
		this.delim = ",";
		this.lineList = new ArrayList<String>();
		this.headerList = new ArrayList<String>();
		this.bodyList = new ArrayList<Map<String, String>>();
		this.firstHeadRow = firstHeadRow;
		this.trim = true;
		this.loadFile();
	}
	
	 
	private void loadFile() throws SangsMessageException {
		BufferedReader br = null;
		
		try {
			
			File file = new File(filePath);
			this.fileSize = file.length();
            //fr = new FileReader(file, Charset.forName("EUC-KR"));
			//fr = new FileReader(file);
			
			FileInputStream input = new FileInputStream(filePath);
			InputStreamReader reader = new InputStreamReader(input, "euc-kr");
            br = new BufferedReader(reader);
            String line = "";
            int loopCnt = 0;
            while((line = br.readLine()) != null){
            	loopCnt++;
            	
            	
            	if(loopCnt == 1 && firstHeadRow) {	// head row 가 있는경우
            		this.setHeadList(line);
            	} else {
            		bodyList.add(this.getBodyMap(line, loopCnt));
            	}
            	lineList.add(line);
            }
		} catch (FileNotFoundException e) {
			throw new SangsMessageException("file not found Exception ["+filePath+"]");
		} catch(Exception e){
			throw new SangsMessageException(e.getMessage());
		} finally {
			try {
				if(br != null)
					br.close();
			} catch (IOException e) {
				throw new SangsMessageException(e.getMessage());
			}
			/*
			try {
				if(fr != null)
					fr.close();
			} catch (IOException e) {
				throw new SangsMessageException(e.getMessage());
			}*/
		}
		
	}
	private void setHeadList(String line) {
		String[] headers = line.split(delim);
		for(String header : headers) {
			if(trim)
				headerList.add(header.trim());
			else 
				headerList.add(header);
		}
	}
	private Map<String, String> getBodyMap(String line, int rowNum) throws SangsMessageException  {
		String[] bodys = line.split(delim);
		
		Map<String, String> map = new HashMap<String, String>();
		
		for(int i = 0 ; i < bodys.length ; i++) {
			String bodyStr = bodys[i];
			
			if(trim)
				bodyStr = bodyStr.trim();
			
			//String key = "col_"+i;
			String key = "";
			if(!firstHeadRow) {
				key = "col_"+i;
			} else { 
				try {
					key = headerList.get(i);
				} catch(Exception e) {
					e.printStackTrace();
					throw new SangsMessageException("Header index Exception header size="+headerList.size() + ", body column index = "+i + " [caused row : " + rowNum + "]");
				}
			}
			map.put(key, bodyStr);
		}
		return map;
	}
	
	/**
	 * CSV 를 line 별로 list에 담아서 반환 
	 * @return 라인기준 전체 컬럼데이터가 붙은 String 을 list로 반환
	 */
	public List<String> getLineList() {
		return this.lineList;
	}
	/**
	 * 파일의 header의 컬럼을 List로 반환
	 * @return List of String
	 */
	public List<String> getHeaderList() {
		return this.headerList;
	}
	
	/**
	 * header명에 대한 index 반환 
	 * <br>- 입력 받은 header명이 없는경우 -1 반환
	 * @param headerName header명 
	 * @return header명에 대한 index 반환
	 */
	public int getHeaderIndex(String headerName) {
		for(int i = 0 ; i < this.headerList.size() ; i++ ) {
			String strHeaderNm = this.headerList.get(i);
			if(headerName.equals(strHeaderNm))
				return i;
		}
		return -1;
	}
	
	/**
	 * 해더를 제외한 본문 데이터 목록 반환 
	 * <br>- 리스트 안에 Map으로 되어 있는 객체로 반환
	 * @return 본문 데이터 목록 반환 
	 */
	public List<Map<String,String>> getBodyList() {
		return this.bodyList;
	}
	
	/**
	 * 파일 size 반환 (byte기준)
	 * @return 파일 size 반환 
	 */
	public long getFileSize() {
		return this.fileSize;
	}
	
	/**
	 * 파일 size 반환 (KB기준)
	 * @return 파일 size 반환 
	 */
	public int getFileSizeKb() {
		if (this.fileSize < 1024) {
			return 1;
		} else {
			return (int) (this.fileSize / 1024);
		}
	}
	
	/**
	 * 파일 저장 
	 * 
	 * @param path 저장하고자 하는 파일 Full 경로(String type)
	 * @param headerList 해더(컬럼)목록
	 * @param bodyList 본문내용 목록(List of Map)
	 * @throws Exception Exception
	 * @return 저장된 파일 Object
	 */
	public File saveFile(String path, List<String> headerList,  List<Map<String, String>> bodyList) throws Exception {
		File file = new File(path);
		
		if(file.exists()) {
			throw new IOException("같은 이름의 파일이 이미 존재합니다.");
		}
		
		
		FileOutputStream output = null;
		OutputStreamWriter writer = null;
		BufferedWriter out = null;
		try {
			file.createNewFile();
			
			output = new FileOutputStream(path, false);
		        
			writer = new OutputStreamWriter(output,"MS949");
		    out = new BufferedWriter(writer);
		    
		    StringBuffer writeSb = new StringBuffer();
		    if(headerList == null || headerList.size() == 0 )
		    	throw new SangsMessageException("생성하려는 파일에 header 목록이 없습니다.");
		    
		    // 해더 리스트
	    	for(int j = 0 ; j < headerList.size() ; j++) {
    			writeSb.append(headerList.get(j));
    			if((j + 1) != headerList.size())	// 마지막에는 , 를 붙이지 않는다.
    				writeSb.append(",");
    		}
	    	
	    	
	    	writeSb.append("\n");
	    
		    out.append(writeSb.toString());
		    writeSb.setLength(0);
		    
		    // body 리스트 
		    if(bodyList != null && bodyList.size() > 0 ) {
		    	//for(Map<String, String> map : bodyList) {
		    	for(int i = 0 ; i < bodyList.size() ; i++ ) {
		    		Map<String, String> map = bodyList.get(i);
		    		
		    		for(int j = 0 ; j < headerList.size() ; j++) {
		    			writeSb.append(map.get(headerList.get(j)));
		    			
		    			if((j + 1) != headerList.size())	// 마지막에는 , 를 붙이지 않는다.
		    				writeSb.append(",");
		    		}
		    		
		    		if((i + 1) != bodyList.size())	// 마지막에는 줄바꿈을 하지 않는다. 
		    			writeSb.append("\n");
		    	}
		    }
		    out.append(writeSb.toString());
		    out.flush();
		    out.close();
		} catch (IOException e) {
		    throw e;
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) { e.printStackTrace();}
			}
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) { e.printStackTrace();}
			}
			
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) { e.printStackTrace();}
			}
			
		}
		return new File(path);
	}
	
	
}
