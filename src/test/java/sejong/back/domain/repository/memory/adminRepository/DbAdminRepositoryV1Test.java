package sejong.back.domain.repository.memory.adminRepository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import sejong.back.domain.repository.memory.memberRepository.DbMemberRepositoryV1;
import sejong.back.domain.service.LoginService;

import javax.sql.DataSource;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static sejong.back.domain.ConnectionConst.*;

class DbAdminRepositoryV1Test {

    DbAdminRepositoryV1 repository;
    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        PlatformTransactionManager transactionManager = new
                DataSourceTransactionManager(dataSource);
        repository= new DbAdminRepositoryV1(dataSource);

    }
    @Test
    @DisplayName("Admin Check")
    public void adminCheck() throws SQLException {

        Assertions.assertThat(repository.AdminCheck(1L)).isTrue();
    }
}