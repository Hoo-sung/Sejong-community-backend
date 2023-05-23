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


    private Long treeKey;
    private  Long memberKey;//만든 사람의 정보 저장하는 값.

    private String title;//게시글 제목
    private String description;//게시글 설명
    /**
     * 1.스터디
     * 2.팀플
     * 3.잡담
     * 4.친목
     * 5.고민 상담
     * 6.정보 교류
     */
    private List<Integer> tags;//태그들 정수로 번호 가져오기.

    private Map<String,String> dataRange;//다른사람이 이 객체에 대해 볼 수 있는 정보. 즉, 이 tree만든 맴버가 member 설정에서 공개한 정보.


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


    public Tree(Long memberKey, String title, String description, boolean requestId, boolean requestDepartment,Timestamp created_at, Timestamp updated_at) {

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
