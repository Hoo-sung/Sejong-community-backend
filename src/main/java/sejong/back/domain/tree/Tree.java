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
    private  Long memberKey;//만든 사람의 정보 저장하는 값.

    private  String title;//게시글 제목
    private  String description;//게시글 설명

    private Timestamp created_at;

    private Timestamp updated_at;


    //공개 범위.
    private boolean requestId;
    private boolean requestDepartment;


    private List<String> tags;//담아서 줄게.

    public Tree(Long memberKey, String title, String description,boolean requestId,boolean requestDepartment,Timestamp created_at,Timestamp updated_at) {
        this.memberKey = memberKey;
        this.title = title;
        this.description = description;
        this.requestId = requestId;
        this.requestDepartment=requestDepartment;
        this.created_at=created_at;
        this.updated_at=updated_at;
    }

    public Tree( Long memberKey, String title, String description, boolean requestId, boolean requestDepartment) {

        this.memberKey = memberKey;
        this.title = title;
        this.description = description;
        this.requestId = requestId;
        this.requestDepartment = requestDepartment;
    }

    public Tree(Long memberKey) {
        this.memberKey = memberKey;
    }
}
