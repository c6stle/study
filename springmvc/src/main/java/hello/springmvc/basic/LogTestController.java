package hello.springmvc.basic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LogTestController {

    //private final Logger log = LoggerFactory.getLogger(getClass()); // -> @Slf4j

    @RequestMapping("/log-test")
    public String logTest() {
        String name = "Spring";

        //application.properties
        //logging.level.hello.springmvc=trace

        //log.trace("trace log=" + name); //연산이 먼저 수행되기 때문에 사용하면안됨
        log.trace("trace log={}", name);
        log.debug("trace log={}", name);
        log.info("trace log={}", name);
        log.warn("trace log={}", name);
        log.error("info log={}", name);

        return "ok";
    }

}
