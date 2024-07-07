package com.sangs.mlms.service.mlm;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sangs.common.support.CommonDao;
import com.sangs.lib.support.utils.SangsStringUtil;

@Component
public class MlExecuterManager {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
 
	@Value("${mls.executer.path:}")
	private String mlsExecuterPath;
	
	@Value("${mls.executer.cmd:}")
	private String mlsExecuterCmd;
	
	
	@Autowired
	private CommonDao dao;
	
	
	public boolean executeMl(int learningExecSn, int subSn, String trainingType, String executeType) {
		return executeMl(new int[] {learningExecSn}, new int[] {subSn}, trainingType, executeType);
	}
	
	/**
	 * 학습 실행
	 * 
	 * @param arrLearningExecSn
	 * @param trainingType NEW, RE
	 * @param executeType TRAIN, TEST, BOTH
	 * @return
	 */
	public boolean executeMl(int[] arrLearningExecSn, int[] subSn, String trainingType, String executeType) {
		
		boolean rstFlag = true;

		logger.debug("##### mlsExecuterPath : " + mlsExecuterPath);

		for(int i = 0 ; i < arrLearningExecSn.length ; i++) {
		
			int learningExecSn = arrLearningExecSn[i]; 
					
			try {

				
				testCmd(mlsExecuterPath, mlsExecuterCmd+ " " + arrLearningExecSn[i]+" " +  subSn[i] +" " + trainingType + " " + executeType);

			} catch(Exception e) {
				e.printStackTrace();
				logger.error("", e);
				rstFlag = false;
				
				// 에러 로그 update
				this.updateErrorMsg(learningExecSn, e);
			}
		}

		return rstFlag;
		
	}
	
	public void execCommand(String execDirectory, String cmd) {
		
		ProcessBuilder runBuilder = null;
		Process prun = null;
		String str1 = null;
		//String str2 = null;
		
		try {
			
			logger.debug("ML command execute start");
			//logger.debug("cmd: " + cmd);
			
			logger.debug("execDirectiry : " + execDirectory);
			logger.debug("cmd : " + cmd);
			
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
			
			
			logger.debug("#######################---------0------------");
			runBuilder = new ProcessBuilder(cmdList);
			runBuilder.directory(new File(execDirectory));
			prun = runBuilder.start();
			BufferedReader stdOut = new BufferedReader(new InputStreamReader(prun.getInputStream()));
			
			logger.debug("#######################---------1------------");
			logger.debug("ML command execute start");
			while((str1 = stdOut.readLine()) != null) {
				logger.debug("#######################----2-----------------");
				logger.debug(str1);
				successOutput.append(str1);
				
			}
			
			if (prun.exitValue() == 0) {
                logger.debug("성공");
                logger.debug(successOutput.toString());
            } else {
                // shell 실행이 비정상 종료되었을 경우
                logger.debug("비정상 종료");
                logger.debug(successOutput.toString());
            }
			
			stdOut.close();
			
			logger.debug("ML command execute end");
			
		} catch (Exception e) {
			logger.debug("#######################-Exception--------------------");
			logger.error("", e);
			e.printStackTrace();
		}
	}

	
	private void testCmd(String execDirectory, String cmd) {
		String s;
        Process p;
        try {
        	
        	logger.debug("execDirectiry : " + execDirectory);
			logger.debug("cmd : " + cmd);
			
            List<String> cmdList = new ArrayList<String>();
            
            
    		// 운영체제 구분 (window, window 가 아니면 무조건 linux 로 판단)
			if (System.getProperty("os.name").indexOf("Windows") > -1) {
				// shell execute source -> C:/py-workspace/TestPrj/venv/Scripts/python C:/py-workspace/Sangs_MLS/main.py var1 var2 var3
				logger.debug("window execute");
				cmdList.add("cmd");
				cmdList.add("/c");
			} else {
				logger.debug("linux execute");
				cmdList.add("/bin/sh");
				cmdList.add("-c");
			}
			cmdList.add(execDirectory + cmd);
			//logger.debug("full exe ##########" + cmdList);
			
			//logger.debug("full exe $$$$$$$$$$" + cmdList.toArray(new String[0]));
            p = Runtime.getRuntime().exec(cmdList.toArray(new String[0]));
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "euc-kr" ));
            while ((s = br.readLine()) != null) {
                logger.debug(s);
            }
            p.waitFor();
            logger.debug("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) {
        	logger.error("", e);
        	e.printStackTrace();
        }
	}
	
	
	
	public boolean execPython(String execDirectiry, String[] command) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			CommandLine commandLine = CommandLine.parse(command[0]);
			for(int i = 1, n = command.length; i < n; i++) {
				commandLine.addArgument(command[i]);
			}
			
			
			logger.debug("execDirectiry : " + execDirectiry);
			
			
			PumpStreamHandler pumpStreamHandler = new PumpStreamHandler(outputStream);
			
			DefaultExecutor executor = new DefaultExecutor();
			
			executor.setWorkingDirectory(new File(execDirectiry));
			executor.setStreamHandler(pumpStreamHandler);
			logger.debug("$$$$ --> " + (executor.getWorkingDirectory()).getAbsolutePath());
			
			for(String str : commandLine.getArguments()) {
				logger.debug("---" + str);
			}
			
			int result = executor.execute(commandLine);
			
			
			
			logger.debug("result: " + result);
			logger.debug("output: " + outputStream.toString());
			
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug("error output: " + outputStream.toString());
			
			logger.error("", e);
			return false;
		} finally {
			if(outputStream != null)
				outputStream.close();
		}
	}    
	 
	
	private void updateErrorMsg(int learningExecSn, Exception e) {
		try {
			Map<String, Object> errMap = new HashMap<String, Object>();
			
			errMap.put("trainingErrorMessage", SangsStringUtil.substringByte(e.getMessage(), 1900)); 
			errMap.put("learningExecSn", learningExecSn);
			errMap.put("chgUserId", "");
			
			//  훈련 이력 에러 메시지 update
			dao.update("learning.updateTrainingHistErrorMsg", errMap);
		} catch(Exception ex) {
			logger.error("", ex);
		}
	}

	
  
	
	public String getExecPyResult(String pyName, String strParam) throws Exception {
		String strResult = "";
		String s;
        Process p;
        try {
        	
        	
        	logger.debug("exe cmd : " + mlsExecuterCmd.replace("main.py", pyName+".py"));
			logger.debug("param : " + strParam);
			
            List<String> cmdList = new ArrayList<String>();
            
            String cmd = mlsExecuterCmd.replace("main.py", pyName+".py");
            
            
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
			cmdList.add(mlsExecuterPath + cmd);
			cmdList.add(strParam.replace(" ", "%20"));
			
			for(String str : cmdList) {
				System.out.println(str);
			}
			
			
            p = Runtime.getRuntime().exec(cmdList.toArray(new String[0]));
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "euc-kr" ));
            
            while ((s = br.readLine()) != null) {
                logger.debug(s);
                strResult = strResult + s;
            }
            p.waitFor();
            logger.debug("exit: " + p.exitValue());
            
            
            if(p.exitValue() != 0)
            	throw new Exception();
            p.destroy();
        } catch (Exception e) {
        	logger.error("", e);
        	throw e;
        }
        return strResult;
	}
	
	
	
	
	
	
}
