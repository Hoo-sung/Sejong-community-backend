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
    public ResponseEntity<ResponseResult> wrongSignUp(WrongSignUpException e) {
        log.error("[exceptionHandle] Wrong Sign Up", e);
        ResponseResult<Object> responseResult = new ResponseResult<>(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity(responseResult, HttpStatus.valueOf(responseResult.getStatusCode()));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseResult> doubleSignUp(DoubleSignUpException e) {
        log.error("[exceptionHandle] Double Sign Up", e);
        ResponseResult<Object> responseResult = new ResponseResult<>(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity(responseResult, HttpStatus.valueOf(responseResult.getStatusCode()));
    }


}
