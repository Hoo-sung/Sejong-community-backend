package sejong.back.domain.sticker;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UpdateStickerForm {

    @NotBlank
    private String subject;


    @NotBlank
    private String message;
}
