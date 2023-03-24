package sejong.back.domain.Tree;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter @Setter
@RequiredArgsConstructor
public class Tree {

    private Long id;
    private Long studentKey; //foreign key - Member랑
    private String title;
    private String description;
    private ArrayList<String> tags;
    private ArrayList<String> sticker; //sticker 객체 필요


    public Tree(Long studentKey, String title, String description, ArrayList<String> tags) {
        this.studentKey = studentKey;
        this.title = title;
        this.description = description;
        this.tags = tags;
    }

}
