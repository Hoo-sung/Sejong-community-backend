package sejong.back.domain.sticker;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class AddStickerForm {

    @NotBlank
    private Integer type; // sticker 디자인 타입

    @NotBlank
    private String title;

    @NotBlank
    private String message;
}
