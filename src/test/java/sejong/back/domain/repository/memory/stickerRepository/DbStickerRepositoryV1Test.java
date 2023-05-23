package sejong.back.domain.repository.memory.stickerRepository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import sejong.back.domain.member.Member;
import sejong.back.domain.repository.memory.memberRepository.DbMemberRepositoryV1;
import sejong.back.domain.repository.memory.tagRepository.DbTagRepository;
import sejong.back.domain.repository.memory.treeRepository.DbTreeRepositoryV1;
import sejong.back.domain.repository.memory.tree_tag.DbTree_TagRepository;
import sejong.back.domain.service.LoginService;
import sejong.back.domain.service.StickerService;
import sejong.back.domain.service.TreeService;
import sejong.back.domain.sticker.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sejong.back.domain.ConnectionConst.*;

@Slf4j
class DbStickerRepositoryV1Test {


    private DbStickerRepositoryV1 dbStickerRepositoryV1;
    private DbMemberRepositoryV1 dbMemberRepositoryV1;

    private DbTreeRepositoryV1 dbTreeRepositoryV1;

    private DbTree_TagRepository dbTreeTagRepository;

    private DbTagRepository dbTagRepository;

    private TreeService treeService;

    private StickerService stickerService;

    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        dbMemberRepositoryV1 = new DbMemberRepositoryV1(dataSource);
        dbTreeRepositoryV1 = new DbTreeRepositoryV1(dataSource);
        dbStickerRepositoryV1 = new DbStickerRepositoryV1(dataSource);
        treeService = new TreeService(dbTreeRepositoryV1, dbMemberRepositoryV1, dbTagRepository, dbTreeTagRepository);
        stickerService = new StickerService(dbStickerRepositoryV1,dbMemberRepositoryV1,dbTreeRepositoryV1);
    }

    @Test
    void save() throws SQLException {
        AddStickerForm form = new AddStickerForm();
        form.setTitle("지금 2시 35분이다.");
        form.setMessage("속도 내자");
        form.setType(0);

       dbStickerRepositoryV1.save(8L, 10L, 15L, form);

    }

    @Test
    void searchByTreeID() throws SQLException {//treeid로 검색하므로 frontsticker을 줘야함.
        List<FrontSticker> byTreeId = stickerService.findByTreeId(15L);
        for (FrontSticker sticker : byTreeId) {
            log.info("frontSticker.title={}", sticker.getTitle());
            log.info("frontSticker.dataRange={}", sticker.getDataRange().get("nickname"));
            log.info("frontSticker.dataRange={}", sticker.getDataRange().get("studentId"));
        }
    }

    @Test
    void searchByStickerIDFront() throws SQLException {
        FrontSticker frontSticker = stickerService.findByStickerIdFront(8L);
        log.info("frontSticker.gettitle={}", frontSticker.getTitle());
        log.info("frontSticker.getDataRange={}", frontSticker.getDataRange().get("nickname"));
    }

    @Test
    void searchByStickerID() throws SQLException {
        Sticker sticker = stickerService.findByStickerId(8L);
        log.info("frontSticker.gettitle={}", sticker.getTitle());
        log.info("frontSticker.getDataRange={}", sticker.getUpdated_at());
    }

    @Test
    void searchByStickerIDBack() throws SQLException {
        BackSticker backSticker = stickerService.findByStickerIdBack(8L);
        log.info("backSticker.getmessage={}", backSticker.getMessage());
        log.info("backSticker.getcreatedate={}", backSticker.getCreated_at());
        log.info("backSticker.getnickname={}", backSticker.getDataRange().get("nickname"));
        log.info("backSticker.getstudentid={}", backSticker.getDataRange().get("studentId"));
        log.info("backSticker.getdepartment={}", backSticker.getDataRange().get("department"));

    }

    @Test
    void searchByFromMemberId() throws SQLException {
        List<BackSticker> byMemberId = stickerService.findByMemberId(4L);
        for (BackSticker backSticker : byMemberId) {
            log.info("sticker.update={}", backSticker.getUpdated_at());
        }
    }

    @Test
    void update() throws SQLException {
        UpdateStickerForm updateStickerForm = new UpdateStickerForm();
        updateStickerForm.setTitle("hello");
        updateStickerForm.setMessage("you");
        updateStickerForm.setType(2);
        stickerService.update(4L,updateStickerForm);
    }

    @Test
    void delete() throws SQLException {
        stickerService.delete(4L);
    }
}