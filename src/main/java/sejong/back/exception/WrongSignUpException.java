package sejong.back.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.OK,reason = "잘못된 아이디 또는 비밀번호")
public class WrongSignUpException extends RuntimeException{

    public WrongSignUpException() {
        super();
    }

    public WrongSignUpException(String s) {
        super(s);
    }

    public WrongSignUpException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongSignUpException(Throwable cause) {
        super(cause);
    }
}
