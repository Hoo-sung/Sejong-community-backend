package sejong.back.domain.sticker;

import lombok.Data;

import java.util.Map;


@Data
public class FrontSticker {
    /**
     * 스티커의 앞면으로, 여기에는 DataRange에 닉네임만 들어가고, 스티커의 제목만 쓸 수 있도록 한다.
     *
     */

    private String title;//제목

    private Long fromMember;//스티커 붙인애의 멤버 정보.

    private Map<String,String> dataRange;//여기에는 닉네임만 담는다.

    public FrontSticker(String title, Long fromMember) {
        this.title = title;
        this.fromMember = fromMember;
    }

    public FrontSticker() {
    }
}
