package sejong.back.domain.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 회원 이름
 * 학과
 * 학번(교수: 아이디)
 * <p>
 * 회원 타입(재학생, 신입생 등)
 * 회원 태그(동아리장, 학생회장)
 * 회원 등급
 */
@NoArgsConstructor
@Getter
@Setter
public class Member {

    private String name;
    private String department;//과
    private String studentId;//학번

    /**
     * @JsonIgnore: 데이더의 이동에서 패스워드를 숨기기 위해
     * 컨트롤러에서 json 데이터로 리턴해줄때 이 부분은 뺌
     */
    @JsonIgnore
    private String password;//비밀번호

    private String currentGrade;//현재 학년
    private String status;//재학 휴학 여부.
    private String flag;//고전독서 인증 여부.

    private Long key;//서버에서 관리하는 키.

    private MemberType type;
    private List<String> tag;//태그들도 우리가 만들어서 고르는 식으로 해야할거 같음.
    private MemberGrade grade;

    public Member(String name, String department, String studentId, String password, String currentGrade, String status, String flag) {
        this.name = name;
        this.department = department;
        this.studentId = studentId;
        this.password = password;
        this.currentGrade = currentGrade;
        this.status = status;
        this.flag = flag;
    }

    /**
     * 랜덤 field 변수 생성(랜덤 키);
     * final 여부 생각해보자.
     */
}