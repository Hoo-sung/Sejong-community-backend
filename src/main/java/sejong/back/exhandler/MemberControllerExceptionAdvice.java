package sejong.back.exhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sejong.back.exception.DoubleSignUpException;
import sejong.back.exception.WrongSignUpException;
import sejong.back.web.member.MemberController;

@RestControllerAdvice(assignableTypes = {MemberController.class})
@Slf4j
public class MemberControllerExceptionAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WrongSignUpException.class)
    public ErrorResult wrongSignUpExceptionAdvice(WrongSignUpException e) {
        log.error("[exceptionHandle] Wrong Sign Up", e);
        return new ErrorResult("Wrong Sign Up", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DoubleSignUpException.class)
    public ErrorResult doubleSignUpException(DoubleSignUpException e) {
        log.error("[exceptionHandle] Double Sign Up", e);
        return new ErrorResult("Double Sign Up", e.getMessage());
    }
}
