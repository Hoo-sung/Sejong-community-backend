package sejong.back.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sejong.back.domain.repository.StickerRepository;
import sejong.back.domain.sticker.Sticker;
import sejong.back.domain.sticker.StickerSearchCond;
import sejong.back.domain.sticker.UpdateStickerForm;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StickerService {

    private final StickerRepository stickerRepository;

    public Sticker save(Sticker sticker){
        return stickerRepository.save(sticker);
    }

    public List<Sticker> findByTreeId(Long treeKey){
        return stickerRepository.findByTreeId(treeKey);
    }

    public List<Sticker> findByMemberId(Long memberKey){
        return stickerRepository.findByMemberId(memberKey);
    }

    public Sticker findByStickerId(Long stickerKey){
        return stickerRepository.findByStickerId(stickerKey);
    }


    public List<Sticker> findAll(){
        return stickerRepository.findAll();
    }

    public List<Sticker> search(Long treeKey, StickerSearchCond cond){
        return stickerRepository.search(treeKey, cond);
    }

    public void update(Long stickerKey, UpdateStickerForm form){
        stickerRepository.update(stickerKey, form);
    }

    public Sticker findByMemberKeyAndTreeKey(Long memberKey,Long treeKey){
        return stickerRepository.findByMemberKeyAndTreeKey(memberKey, treeKey);
    }

}
