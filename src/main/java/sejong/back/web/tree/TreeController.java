package sejong.back.web.tree;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
import java.util.Optional;
import java.util.stream.Stream;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TreeController {
    private final TreeRepository treeRepository;
    private final MemberRepository memberRepository;
    private final StickerRepository stickerRepository;

    @PostMapping("/forest/mytree/add")
    @ResponseBody
    public Tree saveTree(@ModelAttribute Tree tree, HttpServletRequest request){
        HttpSession session = request.getSession();
        Long key = (Long) session.getAttribute(SessionConst.DB_KEY);
        tree.setStudentKey(key);
        log.info("makeTree = {} foreignKey = {}", tree.getTitle(), tree.getStudentKey());
        treeRepository.save(tree);
        return tree;
    }

    @PostMapping("/forest/mytree/edit")
    @ResponseBody
    public Tree updateTree(@ModelAttribute Tree tree, HttpServletRequest request){
        HttpSession session = request.getSession();
        Long key = (Long) session.getAttribute(SessionConst.DB_KEY);
        treeRepository.update(tree,key);

        log.info("editTree = {}", tree.getTitle());
        return treeRepository.findByStudentKey(key).findFirst().get();
    }

    @GetMapping("/forest/mytree")//자시 자신만 수정 할 수 있도록 해야한다. 다른애 꺼 수정 못하게 해야 한다.
    @ResponseBody
    public Response<?> myTree(Model model, HttpServletRequest request) {//세션에 있는 db key를 보고, 자시 key일때만 자기 페이지 수정을 할 수있도록, 다른 사용자 정보 수정 시도시, 내정보 수정으로 redirect시.
        log.info("Connecting MyTree");
        HttpSession session = request.getSession(false);//세션을 가져와서 자기 member key를 뽑아야한다.
        long myKey = (Long) session.getAttribute(SessionConst.DB_KEY);//다운 캐스팅.
        Member member = memberRepository.findByKey(myKey);
        model.addAttribute("member", member);
        Stream<Tree> treeList = treeRepository.findByStudentKey(member.getKey());
        //일단은 하나만
        Optional<Tree> tree = treeList.findFirst();
        if(!tree.isEmpty()) {
            model.addAttribute("tree", tree.get());
            Stream<Sticker> sticker = stickerRepository.findByIdTreeId(tree.get().getId());
            model.addAttribute("sticker", sticker);
        }
        return new Response<>("success", "LoadMyTree", model);
    }
}
