package sejong.back.domain.member;

import lombok.*;

import java.util.List;

/**
 * 회원 이름
 * 학과
 * 학번(교수: 아이디)
 *
 * 회원 타입(재학생, 신입생 등)
 * 회원 태그(동아리장, 학생회장)
 * 회원 등급
 */

@RequiredArgsConstructor
@Getter
@Setter

public class Member {

    private  final String name;
    private  final String department;//과
    private  final String studentId;//학번


    private final String currentGrade;//현재 학년

    private final String status;//재학 휴학 여부.

    private final String flag;//고전독서 인증 여부.

    private Long key;//서버에서 관리하는 키.
    private MemberGrade grade;//경험치이다.



    //사용자가 바꿀 수 있는 필드.
    private MemberType memberType;//신/재학/휴학 여부.
    private List<String> tags;//태그들도 우리가 만들어서 고르는 식으로 해야할거 같음.




    /**
     * 랜덤 field 변수 생성(랜덤 키);
     * final 여부 생각해보자.
     */


}