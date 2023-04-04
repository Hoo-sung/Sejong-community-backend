package sejong.back.domain.sticker;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import sejong.back.domain.tree.Tree;

@RequiredArgsConstructor
@Getter
@Setter
public class Sticker {

    private  Long stickerKey;//PK
    private final Long fromMemberKey;//스티커 붙인 당사자
    private final Long toMemberKey;//스티커 받는이 key;

    private final Long treeKey;//스티커를 붙일 트리 key
    private  String subject;//주제
    private  String message;


    public Sticker(Long fromMemberKey, Long toMemberKey, Long treeKey, String subject, String message) {
        this.fromMemberKey = fromMemberKey;
        this.toMemberKey = toMemberKey;
        this.treeKey = treeKey;
        this.subject = subject;
        this.message = message;
    }
}
