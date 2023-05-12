package sejong.back.domain.repository.memory.treeRepository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import sejong.back.domain.tree.Tree;
import sejong.back.domain.tree.TreeSearchCond;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemoryTreeRepositoryTest {

    private MemoryTreeRepository memoryTreeRepository = new MemoryTreeRepository();
    @Test
    void save() {
        for (int i=1;i<41;i++) {
            Long id = Long.valueOf(i);
            String title = "t"+String.valueOf(i);
            String description = title + "I want " + "t" + String.valueOf(i+1);
            Tree tree = new Tree(id, title, description, new ArrayList<>(Arrays.asList(1)),true,true);
            memoryTreeRepository.save(tree);
        }
        Assertions.assertThat(memoryTreeRepository.findAll().size()).isEqualTo(40);
    }

    @Test
    void findAllByCondition() {
        save();

        TreeSearchCond cond = new TreeSearchCond("t30", "t3", "tag", "2");
        List<Tree> trees = memoryTreeRepository.findAll(cond);

        for (Tree tree : trees) {
            log.info("Title = {} Description = {} Tags = {}",tree.getTitle(),tree.getDescription(),tree.getTags());
        }
        Assertions.assertThat(trees.size()).isEqualTo(1);

    }
}