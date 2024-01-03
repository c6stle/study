package hello.servlet.web.springmvc.v1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

//@Controller = @Component, @RequestMapping
@Controller //Component scan 대상, RequestMappingHandlerMapping
public class SpringMemberFormControllerV1 {

    @RequestMapping("/springmvc/v1/members/new-form")
    public ModelAndView process() {
        System.out.println("SpringMemberFormControllerV1.process");
        return new ModelAndView("new-form");
    }
}
