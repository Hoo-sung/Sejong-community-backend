package sejong.back.domain.repository;

import org.springframework.stereotype.Repository;
import sejong.back.domain.Tree.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Repository
public class MemoryTreeRepository implements TreeRepository {

    private static final Map<Long, Tree> store = new ConcurrentHashMap<>();
    private static Long sequence = 0L;

    @Override
    public Tree save(Tree tree) {
        tree.setId(++sequence);
        tree.setSticker(new ArrayList<>());
        store.put(tree.getId(), tree);
        return tree;
    }

    @Override
    public void update(Tree tree, Long key) {
        Optional<Tree> findTree = findById(key);
        findTree.get().setStudentKey(tree.getStudentKey());
        findTree.get().setTitle(tree.getTitle());
        findTree.get().setTags(tree.getTags());
        findTree.get().setSticker(tree.getSticker());
        findTree.get().setDescription(tree.getDescription());
    }

    @Override
    public Stream<Tree> findByStudentId(Long id) {
        return  findAll().stream().filter(m -> m.getStudentKey().equals(id));
    }

    @Override
    public List<Tree> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Tree> findById(Long id) {
        return findAll().stream().filter(m->m.getId().equals(id)).findFirst();
    }

}
