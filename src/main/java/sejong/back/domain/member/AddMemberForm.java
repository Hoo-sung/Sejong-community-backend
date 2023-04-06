package sejong.back.domain.member;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class AddMemberForm {

    @NotBlank
    private String nickname;//이름

    @NotNull
    private Long studentId;//학번

    @NotBlank
    private String password;//비밀번호

    private Map<String, Boolean> dataRange;//정보 공개범위 설정

//    private MemberType memberType;//멤버 타입도 넣어야 한다.

//    private List<String> tags;//태그들도 우리가 만들어서 고르는 식으로 해야할거 같음.


}


