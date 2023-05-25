package sejong.back.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sejong.back.domain.repository.NoticeRepository;
import sejong.back.web.login.NonReadSticker;

import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public List<NonReadSticker> getNotice(Long key) throws SQLException {
        return noticeRepository.getNotice(key);
    }

    public void updateNotice(Long toMemberKey, Long treeId, String title) throws SQLException {
        noticeRepository.updateNotice(toMemberKey, treeId, title);
    }

    public void deleteNotice(Long memberKey, Long treeId) throws SQLException {
        noticeRepository.deleteNotice(memberKey, treeId);
    }

}
