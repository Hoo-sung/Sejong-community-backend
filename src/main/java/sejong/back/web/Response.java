package sejong.back.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 나중에 모든 응답 json 폼을 이것만 사용해서 구현해보기 (ErrorResult 이런 다른 폼 쓰지 말고)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@AllArgsConstructor
public class Response<T> {

    private String status;
    private String message;
    private T data;
}
