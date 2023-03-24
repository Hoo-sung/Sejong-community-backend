package sejong.back.web.login;

import lombok.Data;

import javax.validation.constraints.NotEmpty;


/**
 * 로그인 시 클라이언트에서 넘어오는 데이터 폼
 * @TODO API를 통해 확인해야 하는 정보??
 */
@Data
public class LoginForm {

    @NotEmpty
    private String studentId;//학번

    @NotEmpty
    private String password;//비밀번호
}
