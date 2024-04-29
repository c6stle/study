package hello.proxy.app.v1;

import org.springframework.web.bind.annotation.*;

//@RequestMapping // 스프링은 @Controller 또는 @RequestMapping 있어야 컨트롤러로 인식
//@ResponseBody
@RestController //3.0 부터 @Controller 있어야 인식됨
public interface OrderControllerV1 {

    @GetMapping("/v1/request")
    String request(@RequestParam("itemId") String itemId);

    @GetMapping("/v1/no-log")
    String noLog();
}
