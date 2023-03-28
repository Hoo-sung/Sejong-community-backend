package sejong.back.web.view;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sejong.back.domain.Tree.Tree;
import sejong.back.domain.login.LoginService;
import sejong.back.domain.member.Member;
import sejong.back.domain.member.MemberType;
import sejong.back.domain.repository.MemberRepository;
import sejong.back.domain.repository.TreeRepository;
import sejong.back.web.SessionConst;
import sejong.back.web.login.LoginForm;
import sejong.back.web.member.AddMemberForm;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

@Controller
@Slf4j
@RequiredArgsConstructor
/**
 * 일단 아직은 Front 없으니 대략적인 뷰들 여기에 나둠
 *
 * TODO
 * Validation 아직 적용 X
 */
public class ViewController {

    private final MemberRepository memberRepository;
    private final LoginService loginService;
    private final TreeRepository treeRepository;

    @ModelAttribute("memberTypes")//모델에 통쨰로보여줘야 라디오 버튼이든 뭐든 내용물을 통쨰로 출력할 수 있다.
    public MemberType[] memberTypes() {
        return MemberType.values();//전체 다 가져오기.
    }


    @ModelAttribute("tags")
    public Map<String, String> tags() {
        Map<String, String> tags = new LinkedHashMap<>();
        tags.put("eat", "밥약");
        tags.put("drink", "술약");
        tags.put("study", "스터디");
        tags.put("contest", "공모전");
        return tags;
    }

    //로그인 페이지
    @GetMapping("/login")//이 url로 Getmapping이 오면 폼을 반환해준다.
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

    //forest 페이지
    @GetMapping("/forest")
    public String members(HttpServletRequest request, Model model) {//멤버 검색 페이지이다. 여기서 자기 정보 수정 버튼 누르면 이동할 수 있도록 자기의 멤버도 model로 보내자.
//        List<Member> members = memberRepository.findAll();
//        log.info("members={}", members.stream().findFirst());
//
        HttpSession session = request.getSession(false);//세션을 가져와서 자기 member key를 뽑아야한다.
        long myKey = (Long) session.getAttribute(SessionConst.DB_KEY);//다운 캐스팅.
        Member member = memberRepository.findByKey(myKey);
//        model.addAttribute("members", members);
        model.addAttribute("member", member);
        List<Tree> treeList = treeRepository.findAll();
        model.addAttribute("Trees", treeList);
        log.info("Trees = {}",treeList);
        return "tree/forest";
    }

    @GetMapping("/forest/{memberKey}")//멤버 상세 페이지이다.
    public String member(@PathVariable long memberKey, HttpServletRequest request,
                         Model model) {

        log.info("Forest page {}", memberKey);
        HttpSession session = request.getSession(false);
        long myKey = (Long) session.getAttribute(SessionConst.DB_KEY);

        //나의 Tree
        if (memberKey == myKey) {//자기가 자신의 edit url을 요청한 경우.
            log.info("To my page");
            return "redirect:/forest/mytree";
        }
        //다른 사람의 Tree

        log.info("To other page");
        Member member = memberRepository.findByKey(memberKey);
        model.addAttribute("member", member);
        return "members/member"; //그 멤버의 페이지를 보여줘야 한다.
    }

    @GetMapping("/members/add")//회원 가입. radio 버튼은 하나를 반드시 가지고 있어야 하므로 여기서 type을 만들어 줘야 한다.
    public String addForm(@ModelAttribute("addMemberForm") AddMemberForm addMemberForm) {
        return "members/addMemberForm";
    }

    /**
     * mytree로 redirect 해줘서 여기로 올 예정
     */
    @GetMapping("/forest/mytree")//자시 자신만 수정 할 수 있도록 해야한다. 다른애 꺼 수정 못하게 해야 한다.
    public String myTree(Model model, HttpServletRequest request) {//세션에 있는 db key를 보고, 자시 key일때만 자기 페이지 수정을 할 수있도록, 다른 사용자 정보 수정 시도시, 내정보 수정으로 redirect시.
        log.info("Connecting MyTree");
        HttpSession session = request.getSession(false);//세션을 가져와서 자기 member key를 뽑아야한다.
        long myKey = (Long) session.getAttribute(SessionConst.DB_KEY);//다운 캐스팅.
        Member member = memberRepository.findByKey(myKey);
        model.addAttribute("member", member);

        return "members/editForm";
    }

    @GetMapping("/forest/mytree/add")
    public String makeTree(Model model){
        log.info("tree add");
        model.addAttribute("Tree", new Tree());
        return "tree/makeTree";
    }

    @GetMapping("/forest/mytree/edit")
    public String editTree(Model model, HttpServletRequest request){
        log.info("tree edit");

        HttpSession session = request.getSession(false);//세션을 가져와서 자기 member key를 뽑아야한다.
        long myKey = (Long) session.getAttribute(SessionConst.DB_KEY);//다운 캐스팅.
        Stream<Tree> treeList = treeRepository.findByStudentId(myKey);

        Optional<Tree> firstTree = treeList.findFirst();//일단 트리 1개
        if (firstTree.isEmpty()) {
            return "redirect:/forest/mytree/add";
        }
        model.addAttribute("Tree", firstTree.get());
        return "tree/editTree";
    }

    @PostConstruct
    public void setting() throws IOException {
        //각자 학번 비번으로 적용바람
        Member validateMember = loginService.validateSejong("18011881", "19991201");
        memberRepository.save(validateMember);//db에 저장.
    }
}
