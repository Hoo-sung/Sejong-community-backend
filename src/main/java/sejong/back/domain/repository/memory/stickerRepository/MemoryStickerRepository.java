//package sejong.back.domain.repository.memory.stickerRepository;
//
//import sejong.back.domain.repository.StickerRepository;
//import sejong.back.domain.sticker.AddStickerForm;
//import sejong.back.domain.sticker.Sticker;
//import sejong.back.domain.sticker.StickerSearchCond;
//import sejong.back.domain.sticker.UpdateStickerForm;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public class MemoryStickerRepository implements StickerRepository {
//
//
//    private static final Map<Long, Sticker> storeSticker = new HashMap<>(); //static
//    private static long stickerSequence = 0L; //static
//
//
//    @Override
//    public void save(Long fromMemberId,Long toMemberId, Long tree_id, AddStickerForm form) {
//
//        Sticker sticker = new Sticker(fromMemberId, toMemberId, tree_id,form.getTitle(), form.getMessage(),form.getType());
//        sticker.setStickerKey(++stickerSequence);
//        storeSticker.put(sticker.getStickerKey(), sticker);
//
//    }
//
//    @Override
//    public Sticker findByStickerId(Long stickerKey) {
//        return storeSticker.get(stickerKey);
//    }
//
//    @Override
//    public List<Sticker> findByMemberId(Long memberKey) {
//        return storeSticker.values().stream()
//                .filter(sticker->{
//                    return sticker.getFromMemberKey().equals(memberKey);
//                })
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<Sticker> findByTreeId(Long treeKey) {//해당 트리에 저장되어 있는 스티커들 출력.
//
//        return storeSticker.values().stream()
//                .filter(sticker->{
//                    return sticker.getTreeKey().equals(treeKey);
//                })
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<Sticker> findAll() {
//        return new ArrayList<>(storeSticker.values());
//    }
//
//    //  아직 안함.
//    @Override
//    public List<Sticker> search(Long treeKey, StickerSearchCond cond) {
//       return null;
//    }
//
//    @Override
//    public void update(Long stickerKey, UpdateStickerForm form) {//스티커 제목이랑 메시지를 업데이트 해야한다.
//
//        Sticker updateSticker = storeSticker.get(stickerKey);
//        updateSticker.setType(form.getType());
//        updateSticker.setTitle(form.getTitle());
//        updateSticker.setMessage(form.getMessage());
//
//    }
//
//    @Override
//    public Sticker findByMemberKeyAndTreeKey(Long memberKey, Long treeKey) {//한 사람이 한 게시물에 스티커를 한번밖에 못붙인다. 찾아내기 위한 로직.
//            for (Sticker value : storeSticker.values()) {
//                if (value.getFromMemberKey() == memberKey && value.getTreeKey() == treeKey) {
//                    return value;
//                }
//            }
//            return null;
//
//    }
//
//    @Override
//    public void delete(Long stickerKey) {
//
//    }
//}
