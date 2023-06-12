package sejong.back.web.exhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import sejong.back.exception.*;
import sejong.back.web.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestControllerAdvice()
@Slf4j
public class customizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(WrongLoginException.class)
    public final ResponseEntity<ResponseResult<?>> handleWrongLoginException(Exception e) {

        ResponseResult<Object> responseResult = new ResponseResult<>(e.getMessage());
        responseResult.setErrorCode(-101);
        return new ResponseEntity<>(responseResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SignUpRequiredException.class)
    public final ResponseEntity<ResponseResult<?>> handleSignUpRequiredException(Exception e) {

        ResponseResult<Object> responseResult = new ResponseResult<>(e.getMessage());
        responseResult.setErrorCode(-102);
        return new ResponseEntity<>(responseResult, HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(WrongLogoutException.class)
    public final ResponseEntity<ResponseResult<?>> handleWrongLogoutException(Exception e) {

        ResponseResult<Object> responseResult = new ResponseResult<>(e.getMessage());
        responseResult.setErrorCode(-111);
        return new ResponseEntity<>(responseResult, HttpStatus.FORBIDDEN);

    }

    @ExceptionHandler(WrongSignUpException.class)
    public final ResponseEntity<ResponseResult<?>> handleWrongSignUpException(Exception e) {

        ResponseResult<Object> responseResult = new ResponseResult<>(e.getMessage());
        responseResult.setErrorCode(-121);
        return new ResponseEntity<>(responseResult, HttpStatus.BAD_REQUEST);

    }
    @ExceptionHandler(DoubleSignUpException.class)
    public final ResponseEntity<ResponseResult<?>> handleDoubleSignUpException(Exception e) {

        ResponseResult<Object> responseResult = new ResponseResult<>(e.getMessage());
        responseResult.setErrorCode(-122);
        return new ResponseEntity<>(responseResult, HttpStatus.CONFLICT);

    }

    @ExceptionHandler(DataAccessException.class)
    public final ResponseEntity<ResponseResult<?>> handleDataAccessException(Exception e){

        ResponseResult<Object> responseResult = new ResponseResult<>(e.getMessage());
        responseResult.setErrorCode(-50);
        return new ResponseEntity<>(responseResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<ResponseResult<?>> handleRumTimeException(Exception e){

        ResponseResult<Object> responseResult = new ResponseResult<>(e.getMessage());
        responseResult.setErrorCode(-100);
        return new ResponseEntity<>(responseResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(WrongSessionIdException.class)
    public final ResponseEntity<ResponseResult<?>> handleWrongSessionIdException(HttpServletRequest request,HttpServletResponse response, Exception e) throws IOException {

        ResponseResult<Object> responseResult = new ResponseResult<>(e.getMessage());
        responseResult.setErrorCode(-70);//프론트가 해야하는게 맞다. 우리가 로그인 창을 주느 폼을 가지고있는게 아니라 /login으로 리다이렉트하면 아이디/비번 일치 하지 않늗나 뜬다.
        String requestURI = request.getRequestURI();
        response.setHeader("Location", "http://localhost:8080/login?redirectURL=" + requestURI);
        return new ResponseEntity<>(responseResult, HttpStatus.UNAUTHORIZED);


    }


    @ExceptionHandler(OneStickerPerBoardException.class)
    public final ResponseEntity<ResponseResult<?>> handleOneStickerPerBoardException(Exception e){

        ResponseResult<Object> responseResult = new ResponseResult<>(e.getMessage());
        responseResult.setErrorCode(-141);
        return new ResponseEntity<>(responseResult, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(PutMyStickerOnMyBoardException.class)
    public final ResponseEntity<ResponseResult<?>> handlePutMyStickerOnMyBoardException(Exception e){

        ResponseResult<Object> responseResult = new ResponseResult<>(e.getMessage());
        responseResult.setErrorCode(-142);
        return new ResponseEntity<>(responseResult, HttpStatus.BAD_REQUEST);

    }



    @ExceptionHandler(BackStickerAccessDeniedException.class)
    public final ResponseEntity<ResponseResult<?>> handleBackStickerAccessDeniedException(Exception e){

        ResponseResult<Object> responseResult = new ResponseResult<>(e.getMessage());
        responseResult.setErrorCode(-143);
        return new ResponseEntity<>(responseResult, HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(UnSupportedUpdateStickerException.class)
    public final ResponseEntity<ResponseResult<?>> handleUnSupportedUpdateStickerException(Exception e){

        ResponseResult<Object> responseResult = new ResponseResult<>(e.getMessage());
        responseResult.setErrorCode(-144);
        return new ResponseEntity<>(responseResult, HttpStatus.UNAUTHORIZED);

    }


    @ExceptionHandler(UnSupportedDeleteStickerException.class)
    public final ResponseEntity<ResponseResult<?>> handleUnSupportedDeleteStickerException(Exception e){

        ResponseResult<Object> responseResult = new ResponseResult<>(e.getMessage());
        responseResult.setErrorCode(-145);
        return new ResponseEntity<>(responseResult, HttpStatus.UNAUTHORIZED);

    }


    @ExceptionHandler(UnSupportedUpdateTreeException.class)
    public final ResponseEntity<ResponseResult<?>> handleUnSupportedUpdateTreeException(Exception e){

        ResponseResult<Object> responseResult = new ResponseResult<>(e.getMessage());
        responseResult.setErrorCode(-161);
        return new ResponseEntity<>(responseResult, HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(UnSupportedDeleteTreeException.class)
    public final ResponseEntity<ResponseResult<?>> handleUnSupportedDeleteTreeException(Exception e){

        ResponseResult<Object> responseResult = new ResponseResult<>(e.getMessage());
        responseResult.setErrorCode(-162);
        return new ResponseEntity<>(responseResult, HttpStatus.UNAUTHORIZED);

    }
}
