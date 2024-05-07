package hello.proxy.common.advice;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

// Proxy Factory : Advice 활용 방법
@Slf4j
public class TimeAdvice implements MethodInterceptor { //MethodInterceptor -> Interceptor -> Advice

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("TimeProxy 실행");

        long startTime = System.currentTimeMillis();
        Object result = invocation.proceed(); //target 찾아서 호출
        long endTime = System.currentTimeMillis();

        long resultTime = endTime - startTime;
        log.info("TimeProxy 종료 resultTime={}", resultTime);
        return result;
    }
}
