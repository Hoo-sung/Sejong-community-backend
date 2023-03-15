package sejong.back.domain.member;

import lombok.Data;

import java.util.List;

@Data
public class UpdateMemberForm {


    private  MemberType memberType;

    private List<String> tags;



    //    private List<String> tag;//태그들도 우리가 만들어서 고르는 식으로 해야할거 같음.

}
