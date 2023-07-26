package sejong.back.domain.repository.memory.tree_tag;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import sejong.back.domain.repository.memory.tagRepository.DbTagRepository;
import sejong.back.domain.tree_tag.Tree_Tag;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static sejong.back.domain.ConnectionConst.*;

@Slf4j
class DbTree_TagRepositoryTest {


    private  DbTree_TagRepository dbTreeTagRepository;

    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        dbTreeTagRepository = new DbTree_TagRepository(dataSource);

    }

    @Test
    void save() throws SQLException {
        Tree_Tag treeTag=new Tree_Tag(1L,2);
        dbTreeTagRepository.save(treeTag);

    }

    @Test
    void delete() throws SQLException {
        dbTreeTagRepository.delete(12L, 1);
    }

    @Test
    void deleteTagsByTreeId() throws SQLException {
        dbTreeTagRepository.deleteByTreeId(13L);
    }
    @Test
    void searchByTree_id() throws SQLException//해당 트리 아이디에 해당하는 태그 싹다 가져와야함.
    {
        ArrayList<Integer> byTreeId = dbTreeTagRepository.findByTree_Id(14L);
//        for (Tree_Tag treeTag : byTreeId) {
//            System.out.println(treeTag.getTag_id());
//        }

    }

    @Test
    void searchByTag_id() throws SQLException//해당 태그로 필터링해서 가진 애들 다 가져와야 함.
    {
        ArrayList<Tree_Tag> byTagId = dbTreeTagRepository.findByTag_Id(1);
        for (Tree_Tag treeTag : byTagId) {
            System.out.println(treeTag.getTree_id());
        }
    }


}