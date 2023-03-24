package sejong.back.domain.repository;

import sejong.back.domain.Tree.Tree;

import java.util.List;
import java.util.Optional;

public interface TreeRepository {

    Tree save(Tree tree);

    List<Tree> findAll();

    Optional<Tree> findById(Long id);


}
