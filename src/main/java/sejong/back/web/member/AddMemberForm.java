package sejong.back.web.member;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 회원가입 시 클라이언트에서 넘어오는 데이터 폼
 * MemberDTO와 마찬가지
 */
@Data
public class AddMemberForm {


    @NotEmpty
    private Long studentId;//학번

    @NotEmpty
    private String password;//비밀번호

    @NotEmpty
    private String nickname;//닉네임(이름X)
}


