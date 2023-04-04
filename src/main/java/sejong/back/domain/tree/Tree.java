package sejong.back.domain.tree;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Getter
@Setter
public class Tree {

    private  Long treeKey;
    private final Long memberKey;//만든 사람의 정보 저장하는 값.

    private  String title;//게시글 제목
    private  String description;//게시글 설명
    private  ArrayList<String> tags;
    private  ArrayList<String> stickers;//TODO 이거는 그냥 Sticker의 treeKey를 이용해서 조회하도록 하는 거 아님?

    public Tree(Long memberKey, String title, String description, ArrayList<String> tags) {
        this.memberKey = memberKey;
        this.title = title;
        this.description = description;
        this.tags = tags;
    }


}
