package sejong.back.domain.sticker;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@RequiredArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL) //Json 데이터로 반환할 떄 null인 필드는 표시하지 않음
public class Sticker {

    private Long stickerKey;//PK

    private final Long fromMemberKey;//스티커 붙인 당사자
    private final Long toMemberKey;//스티커 받는이 key;

    private final Long treeKey;//스티커를 붙일 트리 key



    private String writer; //일단은 굳이 참조해서 얻어올 필요 없을 것으로 보임
    private String title;//주제

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
}
