package sejong.back.domain.sticker;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class AddStickerForm {

    @NotBlank
    private String subject;

    @NotBlank
    private String message;
}
