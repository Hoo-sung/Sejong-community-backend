
package sejong.back.web.sticker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import sejong.back.domain.service.MemberService;
import sejong.back.domain.service.NoticeService;
import sejong.back.domain.service.StickerService;
import sejong.back.domain.service.TreeService;
import sejong.back.domain.sticker.AddStickerForm;
import sejong.back.domain.sticker.BackSticker;
import sejong.back.domain.sticker.Sticker;
import sejong.back.domain.sticker.UpdateStickerForm;
import sejong.back.domain.tree.Tree;
import sejong.back.web.ResponseResult;
import sejong.back.web.argumentresolver.Login;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/stickers")//게시글 정보들 보는 페이지.
@RequiredArgsConstructor
public class StickerController {

    private final StickerService stickerService;
    private final TreeService treeService;

    private final NoticeService noticeService;


    @GetMapping//내가 보냈던 스티커들 볼 수 있는 기능 제공.
    public ResponseResult<?> sticker(@Login Long myKey) throws SQLException {//자기가 보냈던 스티커들 정보 볼 수 있는 페이지.

        List<BackSticker> stickers = stickerService.findByMemberId(myKey);

        if(stickers==null)
            return new ResponseResult<>("내가 보냈던 스티커가 하나도 없지롱~", Collections.emptyList());//빈 배열 보낻나.

        //스티커에서 붙인 tree를 뽑아서 이것도 모델로 전부 보내줘야한다.
        return new ResponseResult<>("내 스티커 전체 조회 성공", stickers);
    }

    @PostMapping   //스티커 붙이기
    public ResponseResult<?> save(@Login Long fromMemberKey, @Validated @RequestBody AddStickerForm addStickerForm,
                                  @RequestParam Long treeId) throws IOException, SQLException {

        Tree tree = treeService.findByTreeId(treeId);
        Long toMemberKey = tree.getMemberKey();

        stickerService.save(fromMemberKey,toMemberKey,treeId,addStickerForm);
        noticeService.updateNotice(toMemberKey, treeId, tree.getTitle());

        return new ResponseResult<>("스티커 작성 성공");

    }


    @GetMapping("/{stickerKey}")//스티커 뒷면 보는 페이지.
    public ResponseResult<?> searchSticker(@Login Long myKey, @PathVariable Long stickerKey) throws SQLException {

        BackSticker backSticker = stickerService.findByStickerIdBack(stickerKey);
        Long treeKey = backSticker.getTreeKey();
        Tree stickerOnThisTree = treeService.findByTreeId(treeKey);

        Map<String, Object> data = new HashMap<>();

        if (myKey == backSticker.getFromMember() //자신이 쓴 스티커
        ) { //자신의 트리에 붙은 스티커
            data.put("backSticker", backSticker);
            data.put("stickerAuth", 2); //자신이 쓴 스티커 경우 del 가능
            return new ResponseResult<>("스티커 열람", data);
        } else if ( myKey == stickerOnThisTree.getMemberKey()) {
            data.put("backSticker", backSticker);
            data.put("stickerAuth", 1); // 게시글 주인(수정 x)
            log.info("열람가능 message = {}", data.get("message"));
            return new ResponseResult<>("스티커 열람", data);

        } else{
            data.put("stickerAuth", 3); //남의 스티커 경우 모두 불가능
            ResponseResult<Object> responseResult = new ResponseResult<>("열람할 수 없는 스티커입니다.",data);
            responseResult.setErrorCode(-120);
            log.info("열람 불가능 message");
            return responseResult;


        }
    }


    @PatchMapping("/{stickerKey}")//스티커 수정(스티커 붙인 당사자만 가능하도록 확인해야함.)
    public ResponseResult<?> edit(@Login Long myKey, @PathVariable Long stickerKey,
                                  @Validated @RequestBody UpdateStickerForm form,
                                  BindingResult bindingResult) throws SQLException {

        log.info("스티커 수정 ");
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            throw new IllegalArgumentException("빈 값이 있음");
        }

        Sticker sticker = stickerService.findByStickerId(stickerKey);
        if (sticker == null) {
            throw new NullPointerException("stickerKey에 맞는 내 스티커가 없음");
        }


        if (myKey == sticker.getFromMemberKey()) { //자신의 트리에 붙은 스티커
            stickerService.update(stickerKey, form);
            log.info("나의 스티커 수정 = {}", sticker.getTitle());
            return new ResponseResult<>("스티커 수정 성공", sticker);
        }

        return new ResponseResult<>("내 스티커가 아닙니다.");
    }

    @DeleteMapping("/{stickerKey}")//스티커 수정(스티커 붙인 당사자만 가능하도록 확인해야함.)
    public ResponseResult<?> delete(@Login Long myKey, @PathVariable Long stickerKey) throws SQLException {

        log.info("스티커 삭제");
        Sticker sticker = stickerService.findByStickerId(stickerKey);
        if (sticker == null) {
            throw new NullPointerException("stickerKey에 맞는 내 스티커가 없음");
        }

        if (myKey == sticker.getFromMemberKey()) { //자신의 트리에 붙은 스티커
            stickerService.delete(stickerKey);
            return new ResponseResult<>("스티커 삭제 성공");
        }

        return new ResponseResult<>("내 스티커가 아닙니다.");
    }

}



