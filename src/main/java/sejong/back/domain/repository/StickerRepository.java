package sejong.back.domain.repository;

import sejong.back.domain.sticker.Sticker;
import sejong.back.domain.sticker.StickerSearchCond;
import sejong.back.domain.sticker.UpdateStickerForm;
import sejong.back.domain.tree.Tree;
import sejong.back.domain.tree.TreeSearchCond;
import sejong.back.domain.tree.UpdateTreeForm;

import java.util.List;

public interface StickerRepository { //트리 key로 해당 트리 키가 가진 스티커들을 전부 출력해야한다.

    public Sticker save(Sticker sticker);

    public List<Sticker> findByTreeId(Long treeKey);//해당 게시물(트리)가 가진 스티커들을 모두 출력

    public Sticker findByStickerId(Long stickerKey);


    public List<Sticker> findByMemberId(Long memberKey);//한 사용자가 자기가 보낸 스티커들 조회할 수 있는 기능.


    public List<Sticker> findAll();//db에 스티커들 전부 출력.

    public List<Sticker> search(Long treeKey, StickerSearchCond cond);//하나의 게시물이 가진 스티커가 매우 많을때 검색기능 지원.

    public void update(Long stickerKey, UpdateStickerForm form);

    public Sticker findByMemberKeyAndTreeKey(Long memberKey,Long treeKey);
}
