package sejong.back.domain.login;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class LoginForm {//api를 통해 확인해야 하는 정보.


    @NotNull
    private Long studentId;//학번

    @NotEmpty
    private String password;//비밀번호
}
