package sejong.back.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sejong.back.domain.repository.TreeRepository;
import sejong.back.domain.repository.memory.tree_tag.DbTree_TagRepository;
import sejong.back.domain.tree.Tree;
import sejong.back.domain.tree.TreeSearchCond;
import sejong.back.domain.tree.UpdateTreeForm;
import sejong.back.domain.tree_tag.Tree_Tag;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TreeTagService {

    private final DbTree_TagRepository dbTreeTagRepository;

    public Tree_Tag save(Tree_Tag treeTag) {
        return dbTreeTagRepository.save(treeTag);
    }

    public ArrayList<Integer> findByTree_Id(Long tree_id) {
        return dbTreeTagRepository.findByTree_Id(tree_id);
    }


    public ArrayList<Tree_Tag> findByTag_Id(int tag_id) {
        return dbTreeTagRepository.findByTag_Id(tag_id);
    }

    public void delete(Long tree_id,int tag_id) {
        dbTreeTagRepository.delete(tree_id, tag_id);
    }

}
