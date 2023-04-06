package sejong.back.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * TODO 1. HTTP 상태코드는 보통 응답 스펙에 안 집어넣음. 어차피 클라에서 다 확인할 수 있음.
 * TODO 2. 그 대신 보통 에러 코드를 자체적으로 정함.
 *          HTTP 상태 코드랑 유사하게 가지만 조금 더 세부적으로 나눠서, 에러 코드만 보고도 어느 컨트롤러에서 발생한 어떤 에러인지 알 수 있도록.
 *          ex) 카카오 rest api: code는 별다른 규칙이 없는 음수로 정함
 * TODO 3. 응답 스펙을 조금 세분화해도 됨. 정상 응답과 에러 응답 등(더 자세히 나눠도 되고). api 문서만 정확하게 만들면 되니까.
 * TODO 4. 앵간하면 정상 로직을 다 웬만큼 개발한 다음에 예외 처리를 하는게 좋을 듯
 */
@JsonInclude(JsonInclude.Include.NON_NULL) //Json 데이터로 반환할 떄 null인 필드는 표시하지 않음
@Builder
@Getter @Setter
public class ResponseResult<T> {

    private int errorCode;
    private String message; //결과 메시지(한글. 에러 메시지도 여기에 저장)
    private T data;

    public ResponseResult(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public ResponseResult(String message) {
        this.message = message;
    }

    public ResponseResult() {
    }
}
