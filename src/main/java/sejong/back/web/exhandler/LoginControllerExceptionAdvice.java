package sejong.back.web.exhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sejong.back.exception.WrongLoginException;
import sejong.back.exception.WrongLogoutException;
import sejong.back.web.ResponseResult;
import sejong.back.web.login.LoginController;

@RestControllerAdvice(assignableTypes = {LoginController.class})
@Slf4j
public class LoginControllerExceptionAdvice {

    @ExceptionHandler
    public ResponseEntity<ResponseResult> wrongLogin(WrongLoginException e) {
        log.error("[exceptionHandle] Wrong Login", e);
        ResponseResult<Object> responseResult = new ResponseResult<>(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity(responseResult, HttpStatus.valueOf(responseResult.getStatusCode()));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseResult> wrongLogout(WrongLogoutException e) {
        log.error("[exceptionHandle] Wrong Logout", e);
        ResponseResult<Object> responseResult = new ResponseResult<>(HttpStatus.FORBIDDEN, e.getMessage());
        return new ResponseEntity(responseResult, HttpStatus.valueOf(responseResult.getStatusCode()));
    }


}
