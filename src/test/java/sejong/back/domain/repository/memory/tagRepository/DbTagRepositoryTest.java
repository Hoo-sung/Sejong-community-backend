package sejong.back.domain.repository.memory.tagRepository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import sejong.back.domain.repository.memory.memberRepository.DbMemberRepositoryV1;
import sejong.back.domain.service.LoginService;
import sejong.back.domain.tag.Tag;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static sejong.back.domain.ConnectionConst.*;



@Slf4j
class DbTagRepositoryTest {


    private DbTagRepository dbTagRepository;

    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,
                USERNAME, PASSWORD);
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        dbTagRepository = new DbTagRepository(dataSource);

    }

    @Test
    void save() throws SQLException {
        Tag tag = new Tag("하이");
        Tag saved = dbTagRepository.save(tag);

    }

    @Test
    void update() throws SQLException {
        dbTagRepository.update(6, "놀러 가장");

    }
    @Test
    void searchById() throws SQLException {

        Tag searchById = dbTagRepository.findByTagId(3);
        log.info("description={}", searchById.getDescription());
    }

    @Test
    void searchByText() throws SQLException {
        Tag searchById = dbTagRepository.findByDescription("술약");
        log.info("description={}", searchById.getTag_Id());

    }

    @Test
    void delete() throws SQLException {
        dbTagRepository.delete(9);
    }
}