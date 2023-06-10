package sejong.back.domain.repository;

import sejong.back.domain.member.Member;
import sejong.back.domain.tree.AddTreeForm;
import sejong.back.domain.tree.Tree;
import sejong.back.domain.tree.TreeSearchCond;
import sejong.back.domain.tree.UpdateTreeForm;

import java.sql.SQLException;
import java.util.List;

public interface TreeRepository {

    public Tree save(Long memberKey,AddTreeForm form);

    public Tree findByTreeId(Long TreeId);

    public List<Tree> findAllExcludeMe(Long memberKey);

    public List<Tree> findAll();

    public List<Tree> findAll(TreeSearchCond cond);

    public List<Tree> findMyTrees(Long myTreeKey);

    public Long SavedNumTree();

    public Tree findByTuple(Long index);

    public void update(Long treeKey, UpdateTreeForm form);

    public void delete(Long treeKey);


}
