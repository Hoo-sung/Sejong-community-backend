package sejong.back.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL) //Json 데이터로 반환할 떄 null인 필드는 표시하지 않음
@Getter
public class ResponseResult<T> {

    private int statusCode; //HTTP 응답 상태코드
    private String message; //결과 메시지(한글. 에러 메시지도 여기에 저장)
    private T data;

    public ResponseResult(HttpStatus statusCode, String message, T data) {
        this.statusCode = statusCode.value();
        this.message = message;
        this.data = data;
    }

    public ResponseResult(HttpStatus statusCode, String message) {
        this.statusCode = statusCode.value();
        this.message = message;
    }
}
