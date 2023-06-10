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

    public List<NonReadSticker> getNotice(Long key){
        return noticeRepository.getNotice(key);
    }

    public void updateNotice(Long toMemberKey, Long treeId, String title){
        noticeRepository.updateNotice(toMemberKey, treeId, title);
    }

    public void deleteNotice(Long memberKey, Long treeId){
        noticeRepository.deleteNotice(memberKey, treeId);
    }

}
