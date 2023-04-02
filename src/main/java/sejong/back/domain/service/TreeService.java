package sejong.back.domain.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sejong.back.domain.repository.TreeRepository;
import sejong.back.domain.tree.Tree;
import sejong.back.domain.tree.TreeSearchCond;
import sejong.back.domain.tree.UpdateTreeForm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class TreeService {

    private final TreeRepository treeRepository;

    public Tree save(Tree tree) {
        return treeRepository.save(tree);
    }


    public Tree findByTreeId(Long treeId) {
        return treeRepository.findByTreeId(treeId);
    }


    public List<Tree> findAllExcludeMe(Long memberKey) {
        return treeRepository.findAllExcludeMe(memberKey);
    }


    public List<Tree> findMyTrees(Long myDbKey) {//tree중에 mydbKey값을 가진것만 출력하기.
        return treeRepository.findMyTrees(myDbKey);
    }

    public List<Tree> findAll(TreeSearchCond cond) {
        return treeRepository.findAll(cond);
    }

    public void update(Long treeKey,  UpdateTreeForm form) {

     treeRepository.update(treeKey, form);
    }
}
