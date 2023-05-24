package sejong.back.domain.sticker;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;


@Data
public class BackSticker {

    /**
     * 스티커 만든이와, 게시글 작성자 둘만 보는 페이지로, 게시글 작성자가 요구하는 DataRange를 담아서 보여주고,
     * 스티커의 title, message, updated_at, created_at 필드를 보여준다.
     */


    private Long fromMember;//스티커 붙인애의 멤버 아아디

    private Long treeKey;//sticker가 붙여진 tree의 번호.

    private String title;//제목

    private Integer type; // sticker 디자인 타입

    private String message;//스티커 내용.

    private  String treeTitle;//스티커가 붙여진 게시글 제목.




    private Timestamp created_at;//만든 시간
    private Timestamp updated_at;//수정 시간

    private Map<String,String> dataRange;//여기에는 닉네임은 필수, 추가적으로 게시물 사용자가 지정한 공개범위를 전부 보여준다.


    public BackSticker(Long fromMember, Long treeKey, String title, Integer type, String message, Timestamp created_at, Timestamp updated_at) {
        this.fromMember = fromMember;
        this.treeKey = treeKey;
        this.title = title;
        this.type = type;
        this.message = message;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public BackSticker() {
    }
}
