package sejong.back.web.member;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 회원가입 시 클라이언트에서 넘어오는 데이터 폼
 * MemberDTO와 마찬가지
 *
 * @TODO 학번이나 비밀번호가 세종대 계정이랑 다르면 회원가입이 안 되지만,
 *      이름은 다르게 입력해도 회원가입 가능함.
 *      회원 상세 정보에는 세종대 계정의 이름이 보임
 */
@Data
public class AddMemberForm {

    @NotEmpty
    private String name;//이름

    @NotEmpty
    private String studentId;//학번

    @NotEmpty
    private String password;//비밀번호

}


