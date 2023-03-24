package sejong.back.exhandler;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorResult {

    private String code;
    private String message;
}
