package sejong.back.web.view;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.back.domain.login.LoginForm;
import sejong.back.domain.member.UpdateMemberForm;
import sejong.back.domain.repository.memory.adminRepository.AdminRepository;
import sejong.back.domain.repository.memory.tagRepository.DbTagRepository;
import sejong.back.domain.service.LoginService;
import sejong.back.domain.service.MemberService;
import sejong.back.domain.service.StickerService;
import sejong.back.domain.service.TreeService;
import sejong.back.domain.sticker.Sticker;
import sejong.back.domain.sticker.StickerSearchCond;
import sejong.back.domain.sticker.UpdateStickerForm;
import sejong.back.domain.tree.Tree;
import sejong.back.domain.member.Member;
import sejong.back.domain.member.MemberType;
import sejong.back.domain.repository.MemberRepository;
import sejong.back.domain.tree.TreeSearchCond;
import sejong.back.domain.tree.UpdateTreeForm;
import sejong.back.web.SessionConst;
import sejong.back.domain.member.AddMemberForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

//TODO TreeController랑 겹치는 게 많아서 일단 주석 처리함
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
/**
 * 일단 아직은 Front 없으니 대략적인 뷰들 여기에 나둠
 */
public class ViewController {

    private final MemberRepository memberRepository;
    private final TreeService treeService;
    private final MemberService memberService;
    private final LoginService loginService;
    private final StickerService stickerService;

    private final DbTagRepository dbTagRepository;
    private final AdminRepository adminRepository;

    @ModelAttribute("memberTypes")//모델에 통쨰로보여줘야 라디오 버튼이든 뭐든 내용물을 통쨰로 출력할 수 있다.
    public MemberType[] memberTypes() {
        return MemberType.values();//전체 다 가져오기.
    }


    @ModelAttribute("tags")
    public Map<Integer, String> tags() {

        return dbTagRepository.findAll();
    }
    //로그인 페이지
    @GetMapping("/login")//이 url로 Getmapping이 오면 폼을 반환해준다.
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }
    @PostMapping("/login")
    public String getSession(HttpServletRequest request, @ModelAttribute("loginForm") @Validated LoginForm form,
                             BindingResult bindingResult) throws SQLException, IOException {

        log.info("Get Login");

        if(bindingResult.hasErrors()){
            log.info("Binding Error");
            return "login/loginForm";
        }
        Long id = Long.valueOf(form.getStudentId());
        Member member = memberService.findByLoginId(id);

        Member validatemember = loginService.validateSejong(id, form.getPassword());

        if(validatemember==null){
            bindingResult.reject("loginFail","아이디, 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }
        if (validatemember!=null&&member==null) {//DB에 저장이 안되어 있으면 회원가입을 먼저 해야한다.

            log.info("회원가입 필요");
            bindingResult.reject("loginFail","회원 가입을 하셔야 합니다.");
            return "login/loginForm";
        }
        else {
            //정상 처리 로직, 세션이 있으면 세션 반환, 없으면 새로 생성한다.
            HttpSession session = request.getSession(true);
            session.setAttribute(SessionConst.DB_KEY, member.getKey());
            session.setAttribute(SessionConst.ADMIN, adminRepository.AdminCheck(member.getKey()));
            return "redirect:/admin";//홈으로 보냄
        }
    }

    @GetMapping("/forest/{memberKey}")//멤버 상세 페이지이다.
    public String member(@PathVariable long memberKey, HttpServletRequest request,
                         Model model)  {

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

    @GetMapping("/members")
    public String members(Model model, HttpServletRequest request) throws SQLException {
        String category = request.getParameter("category");
        String keyword = request.getParameter("keyword");

        log.info("key = {}", keyword);
        List<Member> members = memberService.searchMember(category,keyword);

        model.addAttribute("members", members);
        log.info("members = {}", members);

        model.addAttribute("category",category);
        model.addAttribute("keyword",keyword);
        return "members/members";
    }


    @GetMapping("/members/{memberKey}/edit")
    public String memberEdit(@PathVariable long memberKey,
                         Model model, HttpServletRequest request) throws SQLException {
        log.info("Edit member page {}", memberKey);
        Member member = memberService.findByKey(memberKey);
        model.addAttribute("member", member);

        return "members/editForm";
    }

    @PostMapping("/members/{memberKey}/edit")
    public String memberEditPost(@PathVariable long memberKey, @ModelAttribute UpdateMemberForm updateMemberForm) throws SQLException {

        ;
        memberService.update(memberKey,updateMemberForm);

        return "redirect:/admin/members/{memberKey}";
    }

    @GetMapping("/members/add")//회원 가입. radio 버튼은 하나를 반드시 가지고 있어야 하므로 여기서 type을 만들어 줘야 한다.
    public String addForm(@ModelAttribute("addMemberForm") AddMemberForm addMemberForm) {

        return "members/addMemberForm";
    }

    @PostMapping("/members/add")//여기서 회원가입절차에 따라 회원을 db에 save한다.
    public String save( @ModelAttribute AddMemberForm addMemberForm, @Validated BindingResult result) throws IOException, SQLException {
        if (result.hasErrors()) {
            return "members/addMemberForm";
        }

        //학사시스템 확인후 정보 안 맞으면 다시 폼 반환.
        Member validatemember = loginService.validateSejong(addMemberForm.getStudentId(), addMemberForm.getPassword());
        if(validatemember==null){
            result.reject("loginFail","아이디 또는 비밀번호가 맞지 않습니다.");
            return "members/addMemberForm";//다시 입력해야한다.
        }

        //학사 시스템 회원 조회 성공.
        Member searchmember=memberService.findByLoginId(validatemember.getStudentId());//db에 회원 조회.

        if(searchmember==null){//db에 없으면, 회원 가입 절차 정상적으로 진행해야 한다.

            validatemember.setNickname(addMemberForm.getNickname());
            memberService.save(validatemember);//db에 저장.
            log.info("savedMember={}",memberService.findAll());
            return "redirect:/admin/login";
        }
        else {
            result.reject("loginFail","회원 가입된 사용자입니다. 로그인 페이지로 이동해주세요.");
            log.info("회원 가입된 사용자입니다={}");
            return "members/addMemberForm";//다시 입력해야한다.
        }
    }

    @GetMapping("/members/{memberKey}/del")
    public String memberDel(@PathVariable long memberKey) throws SQLException {
        memberService.delete(memberKey);

        return "redirect:/admin/members";
    }

    /**
     * Forest
     */

    @GetMapping("/forest")
    public String forest(HttpServletRequest request, Model model) throws SQLException {//멤버 검색 페이지이다. 여기서 자기 정보 수정 버튼 누르면 이동할 수 있도록 자기의 멤버도 model로 보내자.

//        TreeSearchCond cond = searchCond(request.getParameter("category"), request.getParameter("keyword"));
        String category = request.getParameter("category");
        String keyword = request.getParameter("keyword");
        TreeSearchCond cond = new TreeSearchCond(null, null, null, null, null, "-1");
        if (category!=null&&keyword!=null)
            getSearchCond(category, keyword,cond);

        log.info("search category = {}",category);
        model.addAttribute("category", category);
        model.addAttribute("keyword", keyword);

        List<Tree> trees = treeService.findAll(cond);

        log.info("Get forest = {}", trees);
        model.addAttribute("forest", trees);

        return "forest/forest";
    }

    private static TreeSearchCond getSearchCond(String category, String keyword,TreeSearchCond cond) {
        //page 값이 0보다 작으면 걍 DB 전체 긁어옴

        if (category.equals("title"))
            cond.setTitle(keyword);
        else if (category.equals("description"))
            cond.setDescription(keyword);
        else if (category.equals("name"))
            cond.setName(keyword);
        else if (category.equals("tag"))
            cond.setTag(keyword);
        return cond;
    }


    @GetMapping("/forest/{treeKey}")//멤버 상세 페이지이다.
    public String tree(@PathVariable long treeKey, HttpServletRequest request,
                         Model model) throws SQLException {

        log.info("Forest Key {}", treeKey);
        Tree tree = treeService.findByTreeId(treeKey);
        model.addAttribute("tree", tree);
        return "forest/tree"; //그 멤버의 페이지를 보여줘야 한다.
    }


    @GetMapping("/forest/{treeKey}/edit")
    public String myTree(@PathVariable long treeKey,
                         Model model, HttpServletRequest request) throws SQLException {
        log.info("Edit tree page {}", treeKey);
        Tree tree = treeService.findByTreeId(treeKey);
        model.addAttribute("tree", tree);

        return "forest/editForm";
    }

    @PostMapping("/forest/{treeKey}/edit")
    public String myTreeEdit(@PathVariable long treeKey, @ModelAttribute UpdateTreeForm updateTreeForm,
                         Model model, HttpServletRequest request) throws SQLException {

        log.info("Tree Update id = {}, form = {}",treeKey,updateTreeForm);
        treeService.update(treeKey,updateTreeForm);
        return "redirect:/admin/forest/{treeKey}";
    }

    @GetMapping("/forest/{treeKey}/del")
    public String myTreeDel(@PathVariable long treeKey) throws SQLException {
        treeService.delete(treeKey);
        return "redirect:/admin/forest";
    }


    @GetMapping("/forest/mytree/add")
    public String makeTree(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        Long memberKey = (Long) session.getAttribute(SessionConst.DB_KEY);
        model.addAttribute("Tree", new Tree(memberKey));
        return "tree/makeTree";
    }


    /**
     * Sticker
     */

    @GetMapping("/stickers")//내가 보냈던 스티커들 볼 수 있는 기능 제공.
    public String stickers(HttpServletRequest request, Model model) throws SQLException {//자기가 보냈던 스티커들 정보 볼 수 있는 페이지.

        String category = request.getParameter("category");
        String keyword = request.getParameter("keyword");
        StickerSearchCond cond = getStickerSearchCond(category, keyword);

        List<Sticker> stickers = stickerService.findAll(cond);

        log.info("stickers = {}", stickers);
        //스티커에서 붙인 tree를 뽑아서 이것도 모델로 전부 보내줘야한다.
        model.addAttribute("stickers", stickers);

        log.info("search category = {}",category);
        model.addAttribute("category", category);
        model.addAttribute("keyword", keyword);

        return "sticker/myStickers";//게시글 전체 보여주는 페이지.
    }

    private static StickerSearchCond getStickerSearchCond(String category, String keyword) {
        StickerSearchCond cond = new StickerSearchCond(null,null,null,null);
        if (category != null && keyword != null){
            if (category.equals("title"))
                cond.setTitle(keyword);
            if (category.equals("message"))
                cond.setMessage(keyword);
            if (category.equals("treeKey"))
                cond.setTreeKey(keyword);
            if (category.equals("fromMemberKey"))
                cond.setFromMemberKey(keyword);

        }
        return cond;
    }

    @GetMapping("/stickers/{stickerKey}")
    public String sticker(@PathVariable long stickerKey,
                              Model model, HttpServletRequest request) throws SQLException {
        log.info("Edit tree page {}", stickerKey);
        Sticker sticker = stickerService.findByStickerId(stickerKey);
        log.info("sticker = {}",sticker);
        model.addAttribute("sticker", sticker);
        return "sticker/sticker";
    }

    @GetMapping("/stickers/{stickerKey}/edit")
    public String stickerEdit(@PathVariable long stickerKey,
                         Model model, HttpServletRequest request) throws SQLException {
        log.info("Edit tree page {}", stickerKey);
        Sticker sticker = stickerService.findByStickerId(stickerKey);
        log.info("sticker = {}",sticker);
        model.addAttribute("sticker", sticker);
        return "sticker/editForm";
    }
    @PostMapping("/stickers/{stickerKey}/edit")
    public String stickerEditPost(@PathVariable long stickerKey, @ModelAttribute UpdateStickerForm updateStickerForm,
                                  Model model, HttpServletRequest request) throws SQLException {

        Sticker sticker = stickerService.findByStickerId(stickerKey);
        log.info("sticker Edit ={}", updateStickerForm);
        log.info("title = {}", updateStickerForm.getTitle());

        updateStickerForm.setType(sticker.getType());
        stickerService.update(stickerKey, updateStickerForm);

        return "redirect:/admin/stickers/{stickerKey}";
    }
    @GetMapping("/stickers/{stickerKey}/del")
    public String stickerDel(@PathVariable long stickerKey) throws SQLException {
        stickerService.delete(stickerKey);
        return "redirect:/admin/stickers";
    }


}
