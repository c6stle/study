package hello.core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
//@Component 가 붙은 클래스를 빈으로 등록함, 의존관계 자동주입은 @Autowired 사용    기본적으로 @SpringBootApplication 에 포함되어있음
@ComponentScan(
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)//Configuration 어노테이션 붙은 메소드 제외시킴
)
public class AutoAppConfig {

}
