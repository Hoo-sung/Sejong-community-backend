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
    public ResponseEntity<ResponseResult<?>> loginExHandler(Exception e) {
        log.error("[loginExHandler] ex = {}", e);
        HttpStatus status;

        if (e instanceof WrongLoginException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (e instanceof WrongLogoutException) {
            status = HttpStatus.FORBIDDEN;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ResponseResult<Object> responseResult = new ResponseResult<>(e.getMessage());
        return new ResponseEntity(responseResult, status);
        /**
         * rest api가 통신이 되는 경우는 status를 200으로 보내야지
         * 안그러면 front가 못 받고 뱉어냄
         */
    }

}
