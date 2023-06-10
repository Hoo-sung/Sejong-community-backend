package sejong.back.domain.repository;

import sejong.back.web.login.NonReadSticker;

import java.sql.SQLException;
import java.util.List;

public interface NoticeRepository {

    public List<NonReadSticker> getNotice(Long key);

    public void updateNotice(Long toMemberKey, Long treeId, String title);

    public void deleteNotice(Long memberKey, Long treeId);
}
