package sejong.back.domain.repository;

import sejong.back.domain.member.Member;
import sejong.back.domain.tree.AddTreeForm;
import sejong.back.domain.tree.Tree;
import sejong.back.domain.tree.TreeSearchCond;
import sejong.back.domain.tree.UpdateTreeForm;

import java.sql.SQLException;
import java.util.List;

public interface TreeRepository {

    public Tree save(Long memberKey,AddTreeForm form) throws SQLException;

    public Tree findByTreeId(Long TreeId) throws SQLException;

    public List<Tree> findAllExcludeMe(Long memberKey);

    public List<Tree> findAll() throws SQLException;

    public List<Tree> findAll(TreeSearchCond cond);

    public List<Tree> findMyTrees(Long myTreeKey) throws SQLException;

    public void update(Long treeKey, UpdateTreeForm form) throws SQLException;

    public void delete(Long treeKey) throws SQLException;

}
