package sejong.back.domain.sticker;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AddStickerForm {


    @NotBlank
    private String title;

    @NotBlank
    private String message;

    @NotNull
    private Integer type;//sticker 디자인 타입. 스티커 색깔을 저장해야한다.


}
