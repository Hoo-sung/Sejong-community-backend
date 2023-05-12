package sejong.back.domain.sticker;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UpdateStickerForm {


    @NotBlank
    private String title;

    @NotBlank
    private String message;

    private Integer type;//sticker 디자인 타입. 스티커 색깔을 저장해야한다.
}
