package hello.exception.exhandler;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResult {//ipt
    private String code;
    private String message;
}
