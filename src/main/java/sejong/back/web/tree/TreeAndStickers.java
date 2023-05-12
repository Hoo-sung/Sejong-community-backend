package sejong.back.web.tree;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import sejong.back.domain.sticker.ShowStickerForm;
import sejong.back.domain.sticker.Sticker;
import sejong.back.domain.tree.Tree;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL) //Json 데이터로 반환할 떄 null인 필드는 표시하지 않음
@Data
public class TreeAndStickers {
    private Tree tree;
    private List stickers;
    private Boolean isMine;

    public TreeAndStickers(Tree tree, List stickers, Boolean isMine) {
        this.tree = tree;
        this.stickers = stickers;
        this.isMine = isMine;
    }
}
