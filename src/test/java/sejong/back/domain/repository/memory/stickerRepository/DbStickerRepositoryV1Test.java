package sejong.back.domain.repository.memory.stickerRepository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import sejong.back.domain.member.Member;
import sejong.back.domain.repository.memory.memberRepository.DbMemberRepositoryV1;
import sejong.back.domain.repository.memory.treeRepository.DbTreeRepositoryV1;
import sejong.back.domain.service.LoginService;
import sejong.back.domain.sticker.AddStickerForm;
import sejong.back.domain.sticker.Sticker;
import sejong.back.domain.sticker.UpdateStickerForm;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sejong.back.domain.ConnectionConst.*;

@Slf4j
class DbStickerRepositoryV1Test {


    private DbStickerRepositoryV1 dbStickerRepositoryV1;
    private DbMemberRepositoryV1 dbMemberRepositoryV1;

    private DbTreeRepositoryV1 dbTreeRepositoryV1;

    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        dbMemberRepositoryV1 = new DbMemberRepositoryV1(dataSource);
        dbTreeRepositoryV1 = new DbTreeRepositoryV1(dataSource);
        dbStickerRepositoryV1 = new DbStickerRepositoryV1(dataSource);
    }

    @Test
    void save() throws SQLException {
        AddStickerForm form = new AddStickerForm();
        form.setTitle("지금 11시임");
        form.setMessage("자 자");
        form.setType(1);

        Member postMember = dbMemberRepositoryV1.findByKey(4L);
        Sticker saved = dbStickerRepositoryV1.save(4L, 3L, 2L,postMember.getNickname(), form);
        log.info("savedId={}",saved.getStickerKey());
    }

    @Test
    void searchByTreeID() throws SQLException {
        List<Sticker> byTreeId = dbStickerRepositoryV1.findByTreeId(2L);
        for (Sticker sticker : byTreeId) {
            System.out.println("sticker.nickname="+ sticker.getWriter());
        }
    }

    @Test
    void searchByStickerID() throws SQLException {
        Sticker byStickerId = dbStickerRepositoryV1.findByStickerId(2L);
        System.out.println("byStickerId.update={}"+byStickerId.getWriter());
    }

    @Test
    void searchByFromMemberId() throws SQLException {
        List<Sticker> byMemberId = dbStickerRepositoryV1.findByMemberId(4L);
        for (Sticker sticker : byMemberId) {
            log.info("sticker.update={}", sticker.getUpdated_at());
        }
    }

    @Test
    void update() throws SQLException {
        UpdateStickerForm updateStickerForm = new UpdateStickerForm();
        updateStickerForm.setTitle("hello");
        updateStickerForm.setMessage("you");
        updateStickerForm.setType(2);
        dbStickerRepositoryV1.update(2L,updateStickerForm);
    }

    @Test
    void delete() throws SQLException {
        dbStickerRepositoryV1.delete(2L);
    }
}