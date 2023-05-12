package sejong.back.domain.member;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class UpdateMemberForm {//이게 필요가 없어진다. 아니면 공개 여부를 여기서 지정하기로 하자.

    /**
     */
<<<<<<< Updated upstream
    @NotBlank
    String nickname;//닉네임
=======
//    private String sessionId; //현재 로그인한 사용자의 sessionId(클라이언트로부터 받음)
    private String nickname; //닉네임

    private boolean openStudentId;
    private boolean openDepartment;
>>>>>>> Stashed changes
}
