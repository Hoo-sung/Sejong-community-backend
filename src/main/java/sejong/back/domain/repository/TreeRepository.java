package sejong.back.domain.repository;

import sejong.back.domain.Tree.Tree;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface TreeRepository {

    Tree save(Tree tree);

    void update(Tree tree, Long key);
    List<Tree> findAll();

    Optional<Tree> findById(Long id);

    Stream<Tree> findByStudentId(Long id);

}
