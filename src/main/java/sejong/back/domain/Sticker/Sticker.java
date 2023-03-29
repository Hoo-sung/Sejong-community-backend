package sejong.back.domain.Sticker;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class Sticker {
    private Long id;
    private String studentId;
    private String message;
    private Long treeId;

    public Sticker(String studentId, String message,Long treeId) {
        this.studentId = studentId;
        this.message = message;
        this.treeId = treeId;
    }
}
