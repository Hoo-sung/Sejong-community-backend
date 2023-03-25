package sejong.back.web.exhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sejong.back.exception.WrongLoginException;
import sejong.back.web.ResponseResult;
import sejong.back.web.login.LoginController;

@RestControllerAdvice(assignableTypes = {LoginController.class})
@Slf4j
public class LoginControllerExceptionAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WrongLoginException.class)
    public ResponseResult<?> wrongLogin(WrongLoginException e) {
        log.error("[exceptionHandle] Wrong Login", e);
        return new ResponseResult<>(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
