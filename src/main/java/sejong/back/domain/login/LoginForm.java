package sejong.back.domain.login;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;


@Data
public class LoginForm {//api를 통해 확인해야 하는 정보.


    @NotEmpty
    private String studentId;//학번

    @NotEmpty
    private String password;//비밀번호
}
