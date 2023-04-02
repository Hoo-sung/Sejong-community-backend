package sejong.back.web.exhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sejong.back.exception.DoubleSignUpException;
import sejong.back.exception.WrongSignUpException;
import sejong.back.web.ResponseResult;
import sejong.back.web.member.MemberController;

@RestControllerAdvice(assignableTypes = {MemberController.class})
@Slf4j
public class MemberControllerExceptionAdvice {

    @ExceptionHandler
    public ResponseEntity<ResponseResult<?>> memberExHandler(Exception e) {
        log.error("[memberExHandler] ex", e);
        HttpStatus status;

        if (e instanceof WrongSignUpException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (e instanceof DoubleSignUpException) {
            status = HttpStatus.BAD_REQUEST;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ResponseResult<Object> responseResult = new ResponseResult<>(e.getMessage());
        return new ResponseEntity(responseResult, status);
    }

}
