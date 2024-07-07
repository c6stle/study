package com.sangs.mlms.service.mlm;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MlExecuter implements Runnable {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String execDirectiry = "";
	private String cmd = "";
	
	
	public MlExecuter(String execDirectiry, String cmd) {
		this.execDirectiry = execDirectiry;
		this.cmd = cmd;
	}
	
	@Override
	public void run() {
		execCommand(this.execDirectiry, this.cmd);
	}
	
	
	
	public void execCommand(String execDirectiry, String cmd) {
		
		/*
		List runCmd = new ArrayList();
		runCmd.add("./bin/ycsb");
		runCmd.add("run");
		runCmd.add("basic");
		runCmd.add("-P");
		runCmd.add("workloads/workloada");
		*/
		ProcessBuilder runBuilder = null;
		Process prun = null;
		String str1 = null;
		String str2 = null;
		
		try {
			
			logger.debug("ML command execute start");
			//logger.debug("cmd: " + cmd);
			
			logger.debug("execute cmd : " + cmd);
			
			List<String> cmdList = new ArrayList<String>();

			StringBuffer successOutput = new StringBuffer(); // 성공 스트링 버퍼

			// 운영체제 구분 (window, window 가 아니면 무조건 linux 로 판단)
			if (System.getProperty("os.name").indexOf("Windows") > -1) {
				logger.debug("window execute");
				cmdList.add("cmd");
				cmdList.add("/c");
			} else {
				logger.debug("linux execute");
				cmdList.add("/bin/sh");
				cmdList.add("-c");
			}
			cmdList.add(cmd);
			

 
			runBuilder = new ProcessBuilder(cmdList);
			runBuilder.directory(new File(execDirectiry));
			prun = runBuilder.start();
			BufferedReader stdOut = new BufferedReader(new InputStreamReader(prun.getInputStream()));
			
			logger.debug("ML command execute start");
			while((str1 = stdOut.readLine()) != null) {
				successOutput.append(str1);
			}
			
			if (prun.exitValue() == 0) {
				logger.debug("성공");
            } else {
            	// shell 실행이 비정상 종료되었을 경우
            	logger.debug("비정상 성공");
            	logger.debug(successOutput.toString());
            }
			
			stdOut.close();
			
			logger.debug("ML command execute end");
			
		} catch (Exception e) {
			logger.error("", e);
			e.printStackTrace();
		}
	}
	
	/*
	public Map<Integer, String> execCommand(String... str)  {
	        Map<Integer, String> map = new HashMap<>();
	        ProcessBuilder pb = new ProcessBuilder(str);
	        pb.redirectErrorStream(true);
	        Process process = null;
	        try {
	            process = pb.start();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        BufferedReader reader = null;
	        if (process != null) {
	            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        }

	        String line;
	        StringBuilder stringBuilder = new StringBuilder();
	        try {
	            if (reader != null) {
	                while ((line = reader.readLine()) != null) {
	                    stringBuilder.append(line).append("\n");
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        try {
	            if (process != null) {
	                process.waitFor();
	            }
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }

	        if (process != null) {
	            map.put(0, String.valueOf(process.exitValue()));
	        }

	        try {
	            map.put(1, stringBuilder.toString());
	        } catch (StringIndexOutOfBoundsException e) {
	            if (stringBuilder.toString().length() == 0) {
	                return map;
	            }
	        }
	        return map;
	    }
	*/

}
