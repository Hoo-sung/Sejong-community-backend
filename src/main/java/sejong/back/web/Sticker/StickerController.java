package sejong.back.web.Sticker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sejong.back.domain.Sticker.Sticker;
import sejong.back.domain.Tree.Tree;
import sejong.back.domain.member.Member;
import sejong.back.domain.repository.MemberRepository;
import sejong.back.domain.repository.StickerRepository;
import sejong.back.domain.repository.TreeRepository;
import sejong.back.web.Response;
import sejong.back.web.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.stream.Stream;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StickerController {

    private final StickerRepository stickerRepository;
    private final MemberRepository memberRepository;
    private final TreeRepository treeRepository;

    @PostMapping("/forest/{memberKey}")//멤버 상세 페이지이다.
    @ResponseBody
    public Response<?> makeSticker(@PathVariable long memberKey, @RequestParam String message, HttpServletRequest request) {
        log.info("Sending sticker");

        /**
         * TODO
         * 아래 로직 걍 public 함수 하나 파기
         * 뷰에다가 스티커 띄우기
         */
        HttpSession session = request.getSession(false);
        Long myKey =(long) session.getAttribute(SessionConst.DB_KEY);
        Member member = memberRepository.findByKey(myKey);
        Tree tree = treeRepository.findByStudentKey(memberKey).findFirst().get();

        Sticker sticker = new Sticker(member.getStudentId(), message, tree.getId());
        Sticker savedSticker = stickerRepository.save(sticker);
        return new Response<>("send success", "스티커를 보냈습니다.", savedSticker);
    }

}
