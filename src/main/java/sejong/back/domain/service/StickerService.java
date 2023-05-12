package sejong.back.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sejong.back.domain.member.Member;
import sejong.back.domain.repository.MemberRepository;
import sejong.back.domain.repository.StickerRepository;
import sejong.back.domain.sticker.AddStickerForm;
import sejong.back.domain.sticker.Sticker;
import sejong.back.domain.sticker.StickerSearchCond;
import sejong.back.domain.sticker.UpdateStickerForm;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StickerService {

    private final StickerRepository stickerRepository;


    public Sticker save(Long fromMemberId,Long toMemberId, Long tree_id,String writer, AddStickerForm form) throws SQLException {
        return stickerRepository.save(fromMemberId, toMemberId, tree_id, writer,form);
    }

    public List<Sticker> findByTreeId(Long treeKey) throws SQLException {
        return stickerRepository.findByTreeId(treeKey);
    }

    public List<Sticker> findByMemberId(Long memberKey) throws SQLException {
        return stickerRepository.findByMemberId(memberKey);
    }

    public Optional<Sticker> findByStickerId(Long myKey, Long stickerKey) throws SQLException {
        List<Sticker> stickers = stickerRepository.findByMemberId(myKey);

        return stickers.stream()
                .filter(sticker -> (sticker.getStickerKey() == stickerKey))
                .findFirst();
    }


    public List<Sticker> findAll() {
        return stickerRepository.findAll();
    }

    public List<Sticker> search(Long treeKey, StickerSearchCond cond) {
        return stickerRepository.search(treeKey, cond);
    }

    public void update(Long stickerKey, UpdateStickerForm form) throws SQLException {
        stickerRepository.update(stickerKey, form);
    }

    public Sticker findByMemberKeyAndTreeKey(Long memberKey, Long treeKey) {
        return stickerRepository.findByMemberKeyAndTreeKey(memberKey, treeKey);
    }

}
