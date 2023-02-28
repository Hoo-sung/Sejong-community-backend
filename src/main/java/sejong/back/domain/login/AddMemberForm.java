package sejong.back.domain.login;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AddMemberForm {


    @NotEmpty
    private String name;//이름

    @NotEmpty
    private String studentId;//학번

    @NotEmpty
    private String password;//비밀번호

}


