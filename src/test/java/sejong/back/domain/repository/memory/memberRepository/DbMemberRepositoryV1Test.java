package sejong.back.domain.repository.memory.memberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import sejong.back.TestDataInit;
import sejong.back.domain.member.Member;
import sejong.back.domain.member.MemberGrade;
import sejong.back.domain.member.UpdateMemberForm;
import sejong.back.domain.service.LoginService;
import sejong.back.domain.service.MemberService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import static sejong.back.domain.ConnectionConst.*;

@Slf4j
class DbMemberRepositoryV1Test {


    private  DbMemberRepositoryV1 dbMemberRepositoryV1;
    private  LoginService loginService;



    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        PlatformTransactionManager transactionManager = new
                DataSourceTransactionManager(dataSource);
        dbMemberRepositoryV1 = new DbMemberRepositoryV1(dataSource);
        loginService = new LoginService(dbMemberRepositoryV1);

    }

    @Test
    void search() throws SQLException {

        Long l=18011834L;
        Member byLoginId = dbMemberRepositoryV1.findByLoginId(l);
        log.info("byloginid={}", byLoginId.getName());
        log.info("memberKey={}", byLoginId.getKey());

    }

    @Test
    public void save() {

        Long studentId = 18011834L;

        Member member = null;
        try {
            member = loginService.validateSejong(studentId, "fa484869");
            member.setNickname("난 범안");
            member.setOpenStudentId(false);
            member.setOpenDepartment(false);
            Member save = dbMemberRepositoryV1.save(member);
            log.info("savedMemberKey={}", save.getKey());
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void update(){

        UpdateMemberForm form = new UpdateMemberForm();
        form.setNickname("hwang2");

        form.setOpenStudentId(false);
        form.setOpenDepartment(true);

        try{
            dbMemberRepositoryV1.update(8L, form);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void delete()
    {
        Long key=2L;

        Member member = null;
        try {
            dbMemberRepositoryV1.delete(key);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}