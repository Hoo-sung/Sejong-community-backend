package sejong.back.web.login;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


/**
 * 로그인 시 클라이언트에서 넘어오는 데이터 폼
 */
@Data
public class LoginForm {

    @NotBlank
    private String studentId;//학번

    @NotBlank
    private String password;//비밀번호
}
