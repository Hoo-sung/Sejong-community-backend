package sejong.back.web.exhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import sejong.back.exception.DoubleSignUpException;
import sejong.back.exception.WrongSignUpException;
import sejong.back.web.ResponseResult;
import sejong.back.web.member.MemberController;

@RestControllerAdvice(assignableTypes = {MemberController.class})
@Slf4j
public class MemberControllerExceptionAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WrongSignUpException.class)
    public ResponseResult<?> wrongSignUp(WrongSignUpException e) {
        log.error("[exceptionHandle] Wrong Sign Up", e);
        return new ResponseResult<>(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DoubleSignUpException.class)
    public ResponseResult<?> doubleSignUp(DoubleSignUpException e) {
        log.error("[exceptionHandle] Double Sign Up", e);
        return new ResponseResult<>(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
