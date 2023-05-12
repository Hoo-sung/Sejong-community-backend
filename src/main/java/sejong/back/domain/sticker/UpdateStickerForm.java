package sejong.back.domain.sticker;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UpdateStickerForm {

    @NotNull
    private Integer type;

    @NotBlank
    private String title;

    @NotBlank
    private String message;
}
