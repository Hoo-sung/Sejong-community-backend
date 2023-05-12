package sejong.back.domain.sticker;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import sejong.back.domain.tree.Tree;

import java.sql.Timestamp;


@Getter
@Setter
public class Sticker {

    private  Long stickerKey;//PK
    private  Long fromMemberKey;//스티커 붙인 당사자
    private  Long toMemberKey;//스티커 받는이 key;

    private  Long treeKey;//스티커를 붙일 트리 key


    private  String title;//주제
    private  String message;


    private String writer;//닉네임 저장.
    private Integer type;//sticker 디자인 타입. 스티커 색깔을 저장해야한다.


    private Timestamp created_at;

    private Timestamp updated_at;

    public Sticker(Long fromMemberKey, Long toMemberKey, Long treeKey, String title, String message) {
        this.fromMemberKey = fromMemberKey;
        this.toMemberKey = toMemberKey;
        this.treeKey = treeKey;
        this.title = title;
        this.message = message;
    }


    public Sticker(Long stickerKey, Long fromMemberKey, Long toMemberKey, Long treeKey, String title, String message, String writer, Integer type, Timestamp created_at, Timestamp updated_at) {
        this.stickerKey = stickerKey;
        this.fromMemberKey = fromMemberKey;
        this.toMemberKey = toMemberKey;
        this.treeKey = treeKey;
        this.title = title;
        this.message = message;
        this.writer = writer;
        this.type = type;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Sticker(Long stickerKey, Long fromMemberKey, Long toMemberKey, Long treeKey, String title, String message, String writer, Integer type) {
        this.stickerKey = stickerKey;
        this.fromMemberKey = fromMemberKey;
        this.toMemberKey = toMemberKey;
        this.treeKey = treeKey;
        this.title = title;
        this.message = message;
        this.writer = writer;
        this.type = type;
    }
    public Sticker(Long fromMemberKey, Long toMemberKey, Long treeKey, String title, String message, String writer, Integer type, Timestamp created_at, Timestamp updated_at) {
        this.fromMemberKey = fromMemberKey;
        this.toMemberKey = toMemberKey;
        this.treeKey = treeKey;
        this.title = title;
        this.message = message;
        this.writer = writer;
        this.type = type;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Sticker() {
    }

    public Sticker(Long fromMemberId, Long toMemberId, Long treeId, String title, String message, String writer, Integer type) {
        this.fromMemberKey = fromMemberKey;
        this.toMemberKey = toMemberKey;
        this.treeKey = treeKey;
        this.title = title;
        this.message = message;
        this.writer = writer;
        this.type = type;
    }
}
