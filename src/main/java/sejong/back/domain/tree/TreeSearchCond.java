package sejong.back.domain.tree;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TreeSearchCond {//게시물 검색 조건 :게시물 이름과 설명 또는 태그로 조회.
    private String name;
    private String title;
    private String description;
    private String titDesc; //title description 동시 검색
    private String tag; //filtering logic(TreeRepository)에서 Integer로 바꿔서 처리
    private String page; //filtering logic(TreeRepository)에서 Integer로 바꿔서 처리

    public TreeSearchCond(String name, String title, String description, String titDesc,String tag, String page) {
        this.name = name;
        this.title = title;
        this.titDesc = titDesc;
        this.description = description;
        this.tag = tag;
        this.page = page;
    }
}
