package sejong.back.domain.tree;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
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
    private  List<Integer> tags;
//    private Map<String, Boolean> dataRange;

    private Timestamp created_at;//만든 시간
    private Timestamp updated_at;//수정 시간

    private boolean requestId;
    private boolean requestDepartment;

    public Tree(Long memberKey, String title, String description, List<Integer> tags, boolean requestId, boolean requestDepartment) {
        this.memberKey = memberKey;
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.requestId = requestId;
        this.requestDepartment = requestDepartment;
    }


}
