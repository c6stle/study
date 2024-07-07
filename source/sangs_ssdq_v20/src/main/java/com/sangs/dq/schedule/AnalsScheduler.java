package com.sangs.dq.schedule;

 
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import com.sangs.common.support.CommonDao;
import com.sangs.dq.service.ApiDqService;
import com.sangs.dq.service.ProfileExcService;
import com.sangs.lib.support.domain.SangsMap;

@Component
public class AnalsScheduler implements ApplicationListener<ContextClosedEvent> {

	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	@Value("${analysis.scheduler.use.yn:N}")
	private String schedulerUseYn;
		
	@Autowired
	private CommonDao dao;
 
	@Autowired
	private ApiDqService apiDqService;

	@Autowired
	private ProfileExcService profileExcService;
	
	

	@Scheduled(cron = "1 * * * * ?")		// 1분마다 실행 
	public void schedule() throws Exception {
		
		try {
			
			logger.debug("analysis.scheduler.use.yn : " + schedulerUseYn);
			//System.out.println("apiDqController:" + apiDqController);
			//ApiDqController apiDqController = new ApiDqController();
			
			if("Y".equals(schedulerUseYn)) {
			
				logger.info("DQ Anals Schedule target Search..");
				
				// 진단 수행 대상 조회
				List<SangsMap> list = dao.selectList("dq_profile_schedule.selectTargetProfileList", null);
				
				logger.info("DQ Anals Schedule target Count : " + list.size());
				
				for(int i = 0 ; i < list.size() ; i++) {
					SangsMap smap = list.get(i);
					
					logger.info("DQ Anals Schedule target : " + smap);
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("regUserId", "BATCH");
					params.put("proflSn", smap.get("proflSn"));
					
					
					String cronExprsnValue = "0 " + smap.getString("cronExprsnValue");
					System.out.println("cronExprsnValue: -->" + cronExprsnValue + "|");
					
					
					if (this.isValidExpression(cronExprsnValue)) {
						
						CronExpression cronTrigger = CronExpression.parse(cronExprsnValue);
						LocalDateTime now = LocalDateTime.now();
						LocalDateTime nextExcSchdulDt = cronTrigger.next(now);
						params.put("nextExcSchdulDt", nextExcSchdulDt);
						
						// 다음 스케줄링 시간으로 스케쥴 변경
						dao.update("dq_profile_schedule.updateNextExcSchdulDt", params);
						
						// 진단 수행 
						Map<String, Object> reqResult = this.excProflInfoProc(params);
						
						logger.info("profile request result " + reqResult.get("resultCd")  +  ": profile sn : " + smap.get("proflSn"));
					} else {
						logger.info("cronExprsnValue is not valid : profile sn : " + smap.get("proflSn"));
					}
					
				
				}
				
				logger.info("DQ Anals Schedule target Search..End ");
			}
    	} catch (Exception e){
    		logger.error("", e);
    	}
		
	}
	
	// 프로파일 실행
	private Map<String, Object> excProflInfoProc(Map<String, Object> params) {
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			
			if(!params.containsKey("regUserId")) {
				params.put("regUserId", "API");
			}
			
			Map<String, Object> connResultMap = apiDqService.getProfileDbmsCnncinfo(params);
			
			if((Boolean)connResultMap.get("result")) {
				
				params.put("THREAD_CONN_YN", "Y");
				params.put("THREAD_DBMS_INFO", connResultMap.get("dbmsCnncInfo"));
				params.put("isApiYn", "N");
				
				Map<String, Object> resultMap = profileExcService.excProfileDgnssRqst(params);
				
				rtnMap.putAll(resultMap);
				
				if("OK".equals(String.valueOf(resultMap.get("resultCd")))) {
					rtnMap.put("resultCd","SUCCESS");
				} else {
					rtnMap.put("resultCd","FAIL");
				}
			} else {
				rtnMap.put("resultCd","FAIL");
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			rtnMap.put("resultCd","FAIL");
			rtnMap.put("resultMsg",e.getMessage());
		}
		return rtnMap;
	}
	
	

	
	/**
	 * Cron 표현식 유효성 체크
	 * 
	 * @param expression
	 * @return boolean
	 */
	private boolean isValidExpression(@Nullable String expression) {
		if (expression == null) {
			return false;
		}
		try {
			CronExpression.parse(expression);
			return true;
		}
		catch (IllegalArgumentException ex) {
			return false;
		}
	}

	/**
	 * 서버 종료시 수행중인 진단 상태코드 실패 처리 
	 */
	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		logger.info("Web Server is down");
		try {
			dao.update("dq_profile_schedule.updateExcSttusCdForServerDown", null);
			
		} catch (Exception e) {
			logger.error("", e);
		}
		
		
		
	}
	
	
	
}	
	
