package sejong.back.domain.repository;

import sejong.back.domain.member.Member;
import sejong.back.domain.tree.Tree;
import sejong.back.domain.tree.TreeSearchCond;
import sejong.back.domain.tree.UpdateTreeForm;

import java.util.List;

public interface TreeRepository {

    public Tree save(Tree tree);

    public Tree findByTreeId(Long TreeId);

    public List<Tree> findAllExcludeMe(Long memberKey);

    public List<Tree> findAll();

    public List<Tree> findAll(TreeSearchCond cond);

    public List<Tree> findMyTrees(Long myTreeKey);

    public void update(Long treeKey, UpdateTreeForm form);

}
