package sejong.back.domain.sticker;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

import java.sql.Timestamp;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL) //Json 데이터로 반환할 떄 null인 필드는 표시하지 않음
public class Sticker {//이 객체는 db에 저장할떄만 사용한다. front로 보내는 객체가 아니다. front에는 frontsticker, backsticker을
    //보낸다.


    private Long stickerKey;//PK

    private  Long fromMemberKey;//스티커 붙인 당사자
    private  Long toMemberKey;//스티커 받는이 key;

    private  Long treeKey;//스티커를 붙일 트리 key


    //처음에는 닉네임만 보내고, 열면 트리 만든이가 강제ㅏㄴ
    private String title;//제목

    private String message;

    private Integer type; // sticker 디자인 타입

    private Timestamp created_at;//만든 시간
    private Timestamp updated_at;//수정 시간

    public Sticker(Long fromMemberKey, Long toMemberKey, Long treeKey, String title, String message, Integer type) {
        this.fromMemberKey = fromMemberKey;
        this.toMemberKey = toMemberKey;
        this.treeKey = treeKey;
        this.title = title;
        this.message = message;
        this.type = type;
    }


    public Sticker(Long stickerKey, Long fromMemberKey, Long toMemberKey, Long treeKey, String title, String message, Integer type, Timestamp created_at, Timestamp updated_at) {
        this.stickerKey = stickerKey;
        this.fromMemberKey = fromMemberKey;
        this.toMemberKey = toMemberKey;
        this.treeKey = treeKey;
        this.title = title;
        this.message = message;
        this.type = type;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Sticker(Long stickerKey, Long fromMemberKey, Long toMemberKey, Long treeKey, String title, String message, Integer type) {
        this.stickerKey = stickerKey;
        this.fromMemberKey = fromMemberKey;
        this.toMemberKey = toMemberKey;
        this.treeKey = treeKey;
        this.title = title;
        this.message = message;
        this.type = type;
    }
    public Sticker(Long fromMemberKey, Long toMemberKey, Long treeKey, String title, String message, Integer type, Timestamp created_at, Timestamp updated_at) {
        this.fromMemberKey = fromMemberKey;
        this.toMemberKey = toMemberKey;
        this.treeKey = treeKey;
        this.title = title;
        this.message = message;
        this.type = type;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Sticker() {
    }

}
