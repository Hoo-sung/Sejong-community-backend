package sejong.back.domain.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

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
    private Long studentId;//학번
    private String nickname;//닉네임

    private String currentGrade;//현재 학년
    private String status;//재학 휴학 여부.

    private Long key;//서버에서 관리하는 키.
//    private MemberGrade grade;//경험치이다.

    private boolean openStudentId; //공개 범위 ID
    private boolean openDepartment; // 공개 범위 학과

//    사용자가 바꿀 수 있는 필드.
//    private MemberType memberType;//신/재학/휴학 여부.
//    private List<String> tags;//태그들도 우리가 만들어서 고르는 식으로 해야할거 같음.


    public Member(String name, String department, Long studentId, String currentGrade, String status) {
        this.name = name;
        this.department = department;
        this.studentId = studentId;
        this.currentGrade = currentGrade;
        this.status = status;
    }

    public Member(String name, String department, Long studentId, String nickname, String currentGrade, String status, Long key, boolean openStudentId, boolean openDepartment) {
        this.name = name;
        this.department = department;
        this.studentId = studentId;
        this.nickname = nickname;
        this.currentGrade = currentGrade;
        this.status = status;
        this.key = key;
        this.openStudentId = openStudentId;
        this.openDepartment = openDepartment;
    }

    /**
     * 랜덤 field 변수 생성(랜덤 키);
     * final 여부 생각해보자.
     */

}