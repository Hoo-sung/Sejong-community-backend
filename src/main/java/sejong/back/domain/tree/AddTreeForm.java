package sejong.back.domain.tree;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
@Setter
public class AddTreeForm {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private List<Integer> tags;//정수 배열로.

    private boolean requestId;
    private boolean requestDepartment;


}
