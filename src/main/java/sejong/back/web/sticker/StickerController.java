package sejong.back.web.sticker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.back.domain.repository.StickerRepository;
import sejong.back.domain.service.StickerService;
import sejong.back.domain.service.TreeService;
import sejong.back.domain.sticker.AddStickerForm;
import sejong.back.domain.sticker.Sticker;
import sejong.back.domain.sticker.UpdateStickerForm;
import sejong.back.domain.tree.AddTreeForm;
import sejong.back.domain.tree.Tree;
import sejong.back.domain.tree.UpdateTreeForm;
import sejong.back.web.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;


@Slf4j
@Controller
@RequestMapping("/sticker")//게시글 정보들 보는 페이지.
@RequiredArgsConstructor
public class StickerController {


    private final StickerRepository stickerRepository;
    private final StickerService stickerService;

    private final TreeService treeService;


    /**
     * 이부분 더 수정해야한다.
     * @param request
     * @param model
     * @return
     */
    @GetMapping//내가 보냈던 스티커들 볼 수 있는 기능 제공.
    public String sticker(HttpServletRequest request, Model model) {//자기가 보냈던 스티커들 정보 볼 수 있는 페이지.

        HttpSession session = request.getSession(false);
        Long memberKey = (Long) session.getAttribute(SessionConst.DB_KEY);

        List<Sticker> stickers = stickerRepository.findByMemberId(memberKey);

        //스티커에서 붙인 tree를 뽑아서 이것도 모델로 전부 보내줘야한다.
        model.addAttribute("stickers", stickers);
        return "sticker/myStickers";//게시글 전체 보여주는 페이지.
    }

    @GetMapping("/{stickerKey}")//스티커에 대한 상세 정보 보는 페이지.
    public String searchSticker(@PathVariable Long stickerKey, Model model, HttpServletRequest request) {


        Sticker findSticker = stickerService.findByStickerId(stickerKey);
        Long treeKey = findSticker.getTreeKey();
        Tree StickerOnThistree = treeService.findByTreeId(treeKey);

        model.addAttribute("sticker", findSticker);
        model.addAttribute("tree",StickerOnThistree);

        /**
         * 스티커 붙이는 게시물도 모델로 가져오자. 스티커 상세 정보에서 어떤 게시물에 보내는건지도 간략해 정보 보여줘야 하므로.
         */
        return "sticker/sticker";
    }


    @GetMapping("/{stickerKey}/edit")//자시 자신만 수정 할 수 있도록 해야한다. 다른애 꺼 수정 못하게 해야 한다.
    public String editForm(@PathVariable Long stickerKey, Model model,HttpServletRequest request) {//세션에 있는 db key를 보고, 자시 key일때만 자기 페이지 수정을 할 수있도록, 다른 사용자 정보 수정 시도시, 내정보 수정으로 redirect시.

        HttpSession session=request.getSession(false);//있는 세션을 가져온다.
        Long sessionDbKey = (Long) session.getAttribute(SessionConst.DB_KEY);

        Sticker sticker = stickerService.findByStickerId(stickerKey);
        Long myDbKey = sticker.getFromMemberKey();//스티커에 붙어있는 발신자 멤버 key값을 가져온다.

        if(sessionDbKey==myDbKey){//스티커의 주인이 자기이면
            Sticker findSticker = stickerService.findByStickerId(stickerKey);
            Long treeKey = findSticker.getTreeKey();
            Tree StickerOnThistree = treeService.findByTreeId(treeKey);

            model.addAttribute("sticker", findSticker);
            model.addAttribute("tree",StickerOnThistree);
            return "sticker/editForm";
        }

        else{//남의 스티커를 자기가 수정하려고 하는거면 괴씸하니까 홈으로 리다이렉트 시킬까?
            return "redirect:/sticker/{stickerKey}";
        }

    }

    @PostMapping("/{stickerKey}/edit")//
    public String edit(@Validated @ModelAttribute("updateStickerForm") UpdateStickerForm form, @PathVariable Long stickerKey, BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "sticker/editForm";
        }

        Sticker sticker = stickerService.findByStickerId(stickerKey);
        stickerService.update(stickerKey, form);


        return "redirect:/sticker/{stickerKey}";
    }


}
