//package com.sangs.dq.config;
//
//import java.util.concurrent.Executor;
//
//import javax.annotation.Resource;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.AsyncConfigurer;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//@Configuration
//@EnableAsync
//public class ThreadPoolConfig implements AsyncConfigurer{	//AsyncConfig
//	protected Logger logger = LoggerFactory.getLogger(getClass());
//    protected Logger errorLogger = LoggerFactory.getLogger("error");
//    
//    /** 비동기 기본 Thread 수 */
//    private static int TASK_CORE_POOL_SIZE = 5;			//1순위
//    
//    /** 비동기 최대 Thread 수 */
//    private static int TASK_MAX_POOL_SIZE = 50;			//3순위
//    
//    /** 비동기 QUEUE 수 */
//    private static int TASK_QUEUE_CAPACITY = 2048;		//2순위
//
//    /* 
//     * Thread Pool은 core_pool_size의 크기만큼 한번에 Thread가 생성 되어지고
//     * 그 이후 메소드가 호출된다면 max_pool_size에 따라 늘어나는게 아닌 
//     * Queue에서 대기 상태이고 대기 Queue도 가득해지면 max_pool_size를 
//     * 늘리는 구조이다.
//     * 
//     * */
//    
//    /** 비동기 Thread Bean Name */
//    private static String EXECUTOR_BEAN_NAME = "asyncExecutor-";
//    
//    /** 비동기 Thread */
//    @Resource(name = "asyncExecutor")	//"executorSample"
//    private ThreadPoolTaskExecutor asyncExecutor;
//
//    /*Async Bean*/
//    @Bean(name = "asyncExecutor")
//    public Executor threadPoolTaskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(TASK_CORE_POOL_SIZE);
//        executor.setMaxPoolSize(TASK_MAX_POOL_SIZE);
//        executor.setQueueCapacity(TASK_QUEUE_CAPACITY);
//        executor.setThreadNamePrefix(EXECUTOR_BEAN_NAME);
//        executor.initialize();
//        return executor;
//    }
//}
