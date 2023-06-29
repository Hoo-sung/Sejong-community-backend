package sejong.back.domain.member;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Data
public class UpdateMemberForm {//이게 필요가 없어진다. 아니면 공개 여부를 여기서 지정하기로 하자.

    /**
     */

    @NotBlank
    private String nickname; //닉네임

    private boolean openStudentId;
    private boolean openDepartment;

}
