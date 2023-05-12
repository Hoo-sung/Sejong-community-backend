package sejong.back.domain.repository.memory.treeRepository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.transaction.PlatformTransactionManager;
import sejong.back.domain.repository.memory.memberRepository.DbMemberRepositoryV1;
import sejong.back.domain.repository.memory.tagRepository.DbTagRepository;
import sejong.back.domain.repository.memory.tree_tag.DbTree_TagRepository;
import sejong.back.domain.service.LoginService;
import sejong.back.domain.service.TreeService;
import sejong.back.domain.tree.AddTreeForm;
import sejong.back.domain.tree.Tree;
import sejong.back.domain.tree.UpdateTreeForm;
import sejong.back.domain.tree_tag.Tree_Tag;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static sejong.back.domain.ConnectionConst.*;

@Slf4j

class DbTreeRepositoryV1Test {

    private LoginService loginService;
    private DbMemberRepositoryV1 dbMemberRepositoryV1;


    private DbTagRepository dbTagRepository;
    private DbTree_TagRepository dbTreeTagRepository;

    private DbTreeRepositoryV1 dbTreeRepositoryV1;

    private TreeService treeService;

    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);

        dbMemberRepositoryV1 = new DbMemberRepositoryV1(dataSource);
        loginService = new LoginService(dbMemberRepositoryV1);
        dbTreeRepositoryV1 = new DbTreeRepositoryV1(dataSource);
        dbTagRepository = new DbTagRepository(dataSource);
        dbTreeTagRepository = new DbTree_TagRepository(dataSource);
        treeService = new TreeService(dbTreeRepositoryV1, dbTagRepository, dbTreeTagRepository);
    }

    @Test
    void save() throws SQLException {//게시글 저장하는 부분이다.  트리번호와 해당되는 태그번호를 매핑한 테이블에 추가해야 한다.


        List<Integer> tags=new ArrayList<>();
        tags.add(4);
        tags.add(5);
        tags.add(6);

        boolean requestId=true;
        boolean requestDepartment=false;

        AddTreeForm addTreeForm = new AddTreeForm();
        addTreeForm.setTitle("지금 몇시냐");
        addTreeForm.setDescription("지금 10시다");
        addTreeForm.setTags(tags);
        addTreeForm.setRequestId(true);
        addTreeForm.setRequestDepartment(false);


        Tree saved = treeService.save(3L,addTreeForm);
        log.info("savedTreeKey={}", saved.getTreeKey());

    }


    @Test
    void  searchByTreeId() throws SQLException {
        Tree searchedTree = dbTreeRepositoryV1.findByTreeId(1L);
        log.info("searchedtree id={},created_at={},updated_at={}", searchedTree.getTreeKey(), searchedTree.getCreated_at(), searchedTree.getUpdated_at());

    }

    @Test
    void update() throws SQLException {
        UpdateTreeForm updateTreeForm = new UpdateTreeForm();
        updateTreeForm.setTitle("저장 잘 되었다.");
        updateTreeForm.setDescription("저정상 동작gk.");


        List<Integer> tags=new ArrayList<>();
        tags.add(6);

        updateTreeForm.setTags(tags);
        updateTreeForm.setRequestId(false);
        updateTreeForm.setRequestDepartment(true);

        dbTreeRepositoryV1.update(2L, updateTreeForm);
        dbTreeTagRepository.deleteByTreeId(2L);

        for (Integer tag : tags) {
            Tree_Tag treeTag = new Tree_Tag(2L, dbTagRepository.findByTagId(tag).getTag_Id());
            dbTreeTagRepository.save(treeTag);
        }
        //수행하면 tree_Tag는 12 3 12 4있어야 하고, 다른것들은 삭제되어야 한다. tree의 공개범위도 0 0 으로 설정되었나 확인해보자.
    }


    @Test
    void find_myTrees() throws SQLException {

        List<Tree> myTrees = dbTreeRepositoryV1.findMyTrees(3L);
        log.info(("myTrees={}"), myTrees);
        for (Tree myTree : myTrees) {
            System.out.println(myTree.getTitle());

        }
    }

    @Test
    void findAll() throws SQLException {

        List<Tree> all = dbTreeRepositoryV1.findAll();
        for (Tree tree : all) {
            System.out.println(tree.getDescription());

        }

    }

    @Test
    void delete() throws SQLException {
        dbTreeRepositoryV1.delete(12L);
    }
}