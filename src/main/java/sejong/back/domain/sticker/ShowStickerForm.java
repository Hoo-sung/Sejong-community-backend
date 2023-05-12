package sejong.back.domain.sticker;

import lombok.*;

//남의 트리에 붙어있는 스티커를 볼 때는 이 클래스를 사용
@Getter @Setter
@AllArgsConstructor
public class ShowStickerForm {

    private Long stickerKey;//PK
    private final Long fromMemberKey;//스티커 붙인 당사자
    private final Long toMemberKey;//스티커 받는이 key;

    private final Long treeKey;//스티커를 붙일 트리 key

    private String title;//주제
    private Integer type; // sticker 디자인 타입

}
