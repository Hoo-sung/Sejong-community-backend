package sejong.back.domain.repository.memory.treeRepository;

import org.springframework.stereotype.Repository;
import sejong.back.domain.member.Member;
import sejong.back.domain.repository.TreeRepository;
import sejong.back.domain.tree.Tree;
import sejong.back.domain.tree.TreeSearchCond;
import sejong.back.domain.tree.UpdateTreeForm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Repository
public class MemoryTreeRepository implements TreeRepository {


    private static final Map<Long, Tree> storeTree = new HashMap<>(); //static
    private static long treeSequence = 0L; //static


    @Override
    public Tree save(Tree tree) {
        tree.setTreeKey(++treeSequence);
        storeTree.put(tree.getTreeKey(), tree);
        return tree;
    }

    @Override
    public Tree findByTreeId(Long treeId) {
        return storeTree.get(treeId);
    }

    @Override
    public List<Tree> findAllExcludeMe(Long memberKey) {

//        return new ArrayList<>(storeTree.values());
        return storeTree.values().stream()
                .filter(tree->{
                        return !tree.getMemberKey().equals(memberKey);

                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Tree> findMyTrees(Long myDbKey) {//tree중에 mydbKey값을 가진것만 출력하기.
        return storeTree.values().stream()
                .filter(tree->{
                  return tree.getMemberKey().equals(myDbKey);
                })
                .collect(Collectors.toList());
    }


    /**
     * todo 해야할 부분이다. TreeSearchCond을 정해야한다.
     * @param cond
     * @return
     */
    @Override
    public List<Tree> findAll(TreeSearchCond cond) {
        return null;
    }

    public void clearStore(){
        storeTree.clear();
    }

    @Override
    public void update(Long treeKey, UpdateTreeForm form) {

        Tree updateTree = storeTree.get(treeKey);
        updateTree.setTitle(form.getTitle());
        updateTree.setDescription(form.getDescription());
        updateTree.setTags(form.getTags());
    }
}
