package sejong.back.domain.tree;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Getter
@Setter
public class Tree {

    private  Long treeKey;
    private final Long memberKey;//만든 사람의 정보 저장하는 값.

    private  String title;//게시글 제목
    private  String description;//게시글 설명
    private  ArrayList<String> tags;
    private Map<String, Boolean> dataRange;

    public Tree(Long memberKey, String title, String description, ArrayList<String> tags, Map<String, Boolean> dataRange) {
        this.memberKey = memberKey;
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.dataRange = dataRange;
    }


}
