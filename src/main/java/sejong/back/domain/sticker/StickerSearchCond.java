package sejong.back.domain.sticker;

import lombok.Data;

@Data
public class StickerSearchCond {
    private String title;
    private String message;
    private String treeKey;
    private String fromMemberKey;

    public StickerSearchCond(String title, String message, String treeKey, String fromMemberKey) {
        this.title = title;
        this.message = message;
        this.treeKey = treeKey;
        this.fromMemberKey = fromMemberKey;
    }
}
