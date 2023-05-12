package sejong.back.domain.tree_tag;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class Tree_Tag {

    private Long tree_id;
    private int tag_id;

    public Tree_Tag(Long tree_id, int tag_id) {
        this.tree_id = tree_id;
        this.tag_id = tag_id;
    }
}
