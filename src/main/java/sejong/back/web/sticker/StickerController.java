
package sejong.back.web.sticker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.back.domain.member.Member;
import sejong.back.domain.repository.StickerRepository;
import sejong.back.domain.service.MemberService;
import sejong.back.domain.service.StickerService;
import sejong.back.domain.service.TreeService;
import sejong.back.domain.sticker.AddStickerForm;
import sejong.back.domain.sticker.Sticker;
import sejong.back.domain.sticker.UpdateStickerForm;
import sejong.back.domain.tree.Tree;
import sejong.back.web.ResponseResult;
import sejong.back.web.SessionConst;
import sejong.back.web.argumentresolver.Login;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/stickers")//게시글 정보들 보는 페이지.
@RequiredArgsConstructor
public class StickerController {

    private final StickerService stickerService;
    private final TreeService treeService;

    private final MemberService memberService;

    /**
     * 이부분 더 수정해야한다.
     *
     * @param request
     * @param model
     * @return
     */
    @GetMapping//내가 보냈던 스티커들 볼 수 있는 기능 제공.
    public ResponseResult<?> sticker(@Login Long myKey, HttpServletRequest request, Model model) throws SQLException {//자기가 보냈던 스티커들 정보 볼 수 있는 페이지.

        List<Sticker> stickers = stickerService.findByMemberId(myKey);

        //스티커에서 붙인 tree를 뽑아서 이것도 모델로 전부 보내줘야한다.
        return new ResponseResult<>("내 스티커 전체 조회 성공", stickers);
    }

    @GetMapping("/{stickerKey}")//스티커에 대한 상세 정보 보는 페이지.
    public ResponseResult<?> searchSticker(@Login Long myKey, @PathVariable Long stickerKey,
                                           Model model, HttpServletRequest request) throws SQLException {

        //TODO 다른 사람 스티커 조회는 상관없음.
        Optional<Sticker> sticker = stickerService.findByStickerId(myKey, stickerKey);
        if (sticker.isEmpty()) {
            //TODO 예외 처리. Optional은 NPE를 방지하기 위해 사용하는 건데 직접 NPE를 던지는 건 좀 오바임
            throw new NullPointerException("stickerKey에 맞는 내 스티커가 없음");
        }
        Sticker findSticker = sticker.get();
        Long treeKey = findSticker.getTreeKey();
        Tree stickerOnThisTree = treeService.findByTreeId(treeKey);

        /**
         * 스티커 붙이는 게시물도 모델로 가져오자. 스티커 상세 정보에서 어떤 게시물에 보내는건지도 간략해 정보 보여줘야 하므로.
         */
        HashMap<String, Object> data = new HashMap<>();

        if (myKey == findSticker.getFromMemberKey() //자신이 쓴 스티커
                || myKey == stickerOnThisTree.getMemberKey()) { //자신의 트리에 붙은 스티커
            data.put("message", findSticker.getMessage());
            return new ResponseResult<>("스티커 열람", data);
        }
        else{
            ResponseResult<Object> responseResult = new ResponseResult<>("열람할 수 없는 스티커입니다.");
            responseResult.setErrorCode(-120);
            return responseResult;
        }
    }

    @PostMapping   //스티커 붙이기
    public ResponseResult<?> save(@Login Long fromMemberKey,
                                  @Validated @ModelAttribute AddStickerForm addStickerForm, @RequestParam Long treeId,
                                  BindingResult result, HttpServletRequest request, Model model) throws IOException, SQLException {

        if (result.hasErrors()) {
            //TODO 예외 처리
            throw new IllegalArgumentException("빈 값이 들어있음");
        }

        Tree tree = treeService.findByTreeId(treeId);
        Long toMemberKey = tree.getMemberKey();
        Member frommember = memberService.findByKey(fromMemberKey);
        Sticker savedSticker = stickerService.save(fromMemberKey,toMemberKey,treeId,frommember.getNickname(),addStickerForm);
        return new ResponseResult<>("스티커 작성 성공", savedSticker);
    }


//    @PatchMapping("/{treeKey}")//자시 자신만 수정 할 수 있도록 해야한다. 다른애 꺼 수정 못하게 해야 한다.
    public String editForm(@Login Long myKey, @PathVariable Long stickerKey,
                           Model model, HttpServletRequest request) throws SQLException {//세션에 있는 db key를 보고, 자시 key일때만 자기 페이지 수정을 할 수있도록, 다른 사용자 정보 수정 시도시, 내정보 수정으로 redirect시.

        Optional<Sticker> sticker = stickerService.findByStickerId(myKey, stickerKey);
        if (sticker.isEmpty()) {
            //TODO 예외 처리. Optional은 NPE를 방지하기 위해 사용하는 건데 직접 NPE를 던지는 건 좀 오바임
            throw new NullPointerException("stickerKey에 맞는 내 스티커가 없음");
        }
        Sticker findSticker = sticker.get();
        Long treeMemberKey = findSticker.getFromMemberKey();//스티커에 붙어있는 발신자 멤버 key값을 가져온다.

        if (myKey == treeMemberKey) {//스티커의 주인이 자기이면
            Long treeKey = findSticker.getTreeKey();
            Tree stickerOnThisTree = treeService.findByTreeId(treeKey);

            //TODO 뷰 렌더링
            model.addAttribute("sticker", findSticker);
            model.addAttribute("tree", stickerOnThisTree);
            return "sticker/editForm";
        } else {//남의 스티커를 자기가 수정하려고 하는거면 괴씸하니까 홈으로 리다이렉트 시킬까?
            return "redirect:/sticker/{stickerKey}";
        }

    }

    @PatchMapping("/{stickerKey}")//
    public ResponseResult<?> edit(@Login Long myKey,
                                  HttpServletRequest request, @PathVariable Long stickerKey,
                                  @Validated @ModelAttribute("updateStickerForm") UpdateStickerForm form,
                                  BindingResult bindingResult) throws SQLException {

        log.info("스티커 수정 ");
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            throw new IllegalArgumentException("빈 값이 있음");
        }

        Optional<Sticker> sticker = stickerService.findByStickerId(myKey, stickerKey);
        if (sticker.isEmpty()) {
            //TODO 예외 처리. Optional은 NPE를 방지하기 위해 사용하는 건데 직접 NPE를 던지는 건 좀 오바임
            throw new NullPointerException("stickerKey에 맞는 내 스티커가 없음");
        }

        Sticker findSticker = sticker.get();

        if (myKey == findSticker.getFromMemberKey()) { //자신의 트리에 붙은 스티커
            stickerService.update(stickerKey, form);
            log.info("나의 스티커 수정 = {}", findSticker.getTitle());
            return new ResponseResult<>("스티커 수정 성공", findSticker);
        }

        return new ResponseResult<>("내 스티커가 아닙니다.");
    }
}
