package sejong.back.domain.member;

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

    private MemberType memberType;//멤버 타입도 넣어야 한다.

}


