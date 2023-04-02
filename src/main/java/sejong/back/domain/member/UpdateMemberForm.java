package sejong.back.domain.member;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class UpdateMemberForm {//이게 필요가 없어진다. 아니면 공개 여부를 여기서 지정하기로 하자.


//    /**
//     *
//     * 정보 공개/비공개를 여기서 설정 할 수 있도록 해야하는게 낫다. 그래서 학번/학과/닉네임/실명 여부/학년 공개 여부 설정을 여기서 하자.
//     */
    @NotBlank
    String nickName;//닉네임

}
