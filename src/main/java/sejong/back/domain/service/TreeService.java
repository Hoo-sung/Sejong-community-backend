package sejong.back.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sejong.back.domain.repository.TreeRepository;
import sejong.back.domain.repository.memory.tagRepository.DbTagRepository;
import sejong.back.domain.repository.memory.tree_tag.DbTree_TagRepository;
import sejong.back.domain.tree.AddTreeForm;
import sejong.back.domain.tree.Tree;
import sejong.back.domain.tree.TreeSearchCond;
import sejong.back.domain.tree.UpdateTreeForm;
import sejong.back.domain.tree_tag.Tree_Tag;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TreeService {

    private final TreeRepository treeRepository;

    private final DbTagRepository dbTagRepository;

    private final DbTree_TagRepository dbTreeTagRepository;

    public Tree save(Long memberKey, AddTreeForm form) throws SQLException {
        Tree saved = treeRepository.save(memberKey, form);//tree저장.

        List<Integer> tags = form.getTags();
        for (Integer tag : tags) {
            Tree_Tag treeTag = new Tree_Tag(saved.getTreeKey(), dbTagRepository.findByTagId(tag).getTag_Id());
            dbTreeTagRepository.save(treeTag);
        }

        return saved;
    }

    public Tree findByTreeId(Long treeId) throws SQLException {
        return treeRepository.findByTreeId(treeId);
    }

    public List<Tree> findAllExcludeMe(Long memberKey) {
        return treeRepository.findAllExcludeMe(memberKey);
    }

    public List<Tree> findAll() throws SQLException {   return treeRepository.findAll();}

    public List<Tree> findMyTrees(Long myDbKey) throws SQLException {//tree중에 mydbKey값을 가진것만 출력하기.
        return treeRepository.findMyTrees(myDbKey);
    }

    public List<Tree> findAll(TreeSearchCond cond) {
        return treeRepository.findAll(cond);
    }

    public void update(Long treeKey,  UpdateTreeForm form) throws SQLException {
        treeRepository.update(treeKey, form);
        dbTreeTagRepository.deleteByTreeId(treeKey);

        List<Integer> tags = form.getTags();
        for (Integer tag : tags) {
            Tree_Tag treeTag = new Tree_Tag(treeKey, dbTagRepository.findByTagId(tag).getTag_Id());
            dbTreeTagRepository.save(treeTag);
        }
    }
}
