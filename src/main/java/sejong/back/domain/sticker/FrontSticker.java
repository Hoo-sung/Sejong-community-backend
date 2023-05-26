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

    private Integer type; // sticker 디자인 타입

    private Map<String,String> dataRange;//여기에는 닉네임만 담는다.

    private Long stickerKey;//front에서 이걸 바탕으로  stickers/stickerKey get으로 스티커 눌렀을때 이동하는데 필요하다.
    private Long treeKey;

    private String treeTitle;//스티커가 붙여진 게시글 제목.


//    public FrontSticker(String title, Long fromMember, Integer type) {
//        this.title = title;
//        this.fromMember = fromMember;
//        this.type = type;
//    }

    public FrontSticker(String title, Long fromMember, Integer type,Long stickerKey) {
        this.title = title;
        this.fromMember = fromMember;
        this.type = type;
        this.stickerKey = stickerKey;
    }



    public FrontSticker() {
    }
}
