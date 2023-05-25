package sejong.back.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sejong.back.domain.member.Member;
import sejong.back.domain.repository.MemberRepository;
import sejong.back.domain.repository.StickerRepository;
import sejong.back.domain.repository.TreeRepository;
import sejong.back.domain.sticker.*;
import sejong.back.domain.tree.Tree;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StickerService {

    private final StickerRepository stickerRepository;

    private final MemberRepository memberRepository;

    private final TreeRepository treeRepository;


    public void save(Long fromMemberId,Long toMemberId, Long tree_id, AddStickerForm form) throws SQLException {
        stickerRepository.save(fromMemberId, toMemberId, tree_id,form);
    }

    public List<FrontSticker> findByTreeId(Long treeKey) throws SQLException {//게시글에 붙여진 스티커 모두 조회.
        //sticker에 닉네임을 주려면 sticker 생성한 애의 memberid를 알아야 한다. from memberid랑 treekey는 알아야 한다.
        //그리고 backsticker에 게시글 요청자가 원하는 request 공개 정보를 알려면,
        List<FrontSticker> frontStickers = stickerRepository.findByTreeId(treeKey);
        for (FrontSticker frontSticker : frontStickers) {
            frontSticker.setDataRange(new HashMap<>());
            Long fromMember = frontSticker.getFromMember();//sticker 붙인애의 nickname얻기 위해 필요함.
            Member writer = memberRepository.findByKey(fromMember);
            frontSticker.getDataRange().put("nickname", writer.getNickname());
        }
        return frontStickers;
    }

    public List<BackSticker> findByTreeId_Back(Long treeKey) throws SQLException {//게시글에 붙여진 스티커 모두 조회.
        //sticker에 닉네임을 주려면 sticker 생성한 애의 memberid를 알아야 한다. from memberid랑 treekey는 알아야 한다.
        //그리고 backsticker에 게시글 요청자가 원하는 request 공개 정보를 알려면,
        List<BackSticker> backStickers = stickerRepository.findByTreeId_back(treeKey);
        for (BackSticker backSticker : backStickers) {
            backSticker.setDataRange(new HashMap<>());
            Long fromMember = backSticker.getFromMember();//sticker 붙인애의 nickname얻기 위해 필요함.
            Member writer = memberRepository.findByKey(fromMember);

            Tree byTreeId = treeRepository.findByTreeId(backSticker.getTreeKey());
            backSticker.setTreeTitle(byTreeId.getTitle());
            backSticker.getDataRange().put("nickname", writer.getNickname());
        }
        return backStickers;
    }

//    public List<BackSticker> findByTreeId(Long treeKey) throws SQLException {
//        return stickerRepository.findByTreeId(treeKey);
//    }

    public List<BackSticker> findByMemberId(Long memberKey) throws SQLException {//한 사라이 붙인 스티커의 정보 싹다 뽑기.
        //애는 자기만 볼 수 있으므로 공개범위를 풀로 다 넣을거임.
        List<BackSticker> backStickers = stickerRepository.findByMemberId(memberKey);
        for (BackSticker backSticker : backStickers) {
            backSticker.setDataRange(new HashMap<>());
            Long fromMember = backSticker.getFromMember();//sticker 붙인애의 nickname얻기 위해 필요함.
            Member writer = memberRepository.findByKey(fromMember);//backsticker같은경우 tree의 requestid필드에 있는것을
            backSticker.getDataRange().put("nickname", writer.getNickname());

            String idString = String.valueOf(writer.getStudentId()).substring(0, 2);
            backSticker.getDataRange().put("studentId", idString);
            backSticker.getDataRange().put("department", writer.getDepartment());

            Long treeKey = backSticker.getTreeKey();//스티커가 붙은 게시글 제목도 출력하기.
            Tree tree = treeRepository.findByTreeId(treeKey);
            backSticker.setTreeTitle(tree.getTitle());

            //강제적으로 공개 해야함.

        }
        return backStickers;
    }

    public  Sticker findByStickerId(Long stickerKey) throws SQLException{
        return stickerRepository.findByStickerId(stickerKey);

    }

    public FrontSticker findByStickerIdFront(Long stickerKey) throws SQLException{
        FrontSticker frontSticker = stickerRepository.findByStickerIdFront(stickerKey);

        frontSticker.setDataRange(new HashMap<>());
        Long fromMember = frontSticker.getFromMember();//sticker 붙인애의 nickname얻기 위해 필요함.
        Member writer = memberRepository.findByKey(fromMember);
        frontSticker.getDataRange().put("nickname", writer.getNickname());//datarange nickname만 주기.

        return  frontSticker;
    }

    public BackSticker findByStickerIdBack(Long stickerKey) throws SQLException {
        //back정보 같은 경우, 게시자가 설정한 requestid,requestdepartment를 보고 해당하는 값을 줘야한다.

        BackSticker backSticker = stickerRepository.findByStickerIdBack(stickerKey);

        backSticker.setDataRange(new HashMap<>());
        Long fromMember = backSticker.getFromMember();//sticker 붙인애의 nickname얻기 위해 필요함.
        Member writer = memberRepository.findByKey(fromMember);//backsticker같은경우 tree의 requestid필드에 있는것을

        Long treeKey = backSticker.getTreeKey();
        Tree hasSticker = treeRepository.findByTreeId(treeKey);
        backSticker.setTreeTitle(hasSticker.getTitle());//스티커에 게시글 제목 표시.

        backSticker.getDataRange().put("nickname", writer.getNickname());

        if(hasSticker.isRequestId()==true) {
            String idString = String.valueOf(writer.getStudentId()).substring(0, 2);
            backSticker.getDataRange().put("studentId", idString);
        }

        if(hasSticker.isRequestDepartment()==true) {
            backSticker.getDataRange().put("department", writer.getDepartment());
        }

        return backSticker;// service에서는 공개 범위를 설정해서 반환.
    }
//    public Optional<Sticker> findByStickerId(Long myKey, Long stickerKey) throws SQLException {
//        List<Sticker> stickers = stickerRepository.findByMemberId(myKey);
//
//        return stickers.stream()
//                .filter(sticker -> (sticker.getStickerKey() == stickerKey))
//                .findFirst();
//    }


    public List<Sticker> findAll() {
        return stickerRepository.findAll();
    }

    public List<Sticker> search(Long treeKey, StickerSearchCond cond) {
        return stickerRepository.search(treeKey, cond);
    }

    public void update(Long stickerKey, UpdateStickerForm form) throws SQLException {
        stickerRepository.update(stickerKey, form);
    }

    public void delete(Long stickerKey) throws SQLException {
        stickerRepository.delete(stickerKey);

    }

    public Sticker findByMemberKeyAndTreeKey(Long memberKey, Long treeKey) {
        return stickerRepository.findByMemberKeyAndTreeKey(memberKey, treeKey);
    }

}
