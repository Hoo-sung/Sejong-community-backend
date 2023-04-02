package sejong.back.domain.tree;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
public class AddTreeForm {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private ArrayList<String> tags;

}
