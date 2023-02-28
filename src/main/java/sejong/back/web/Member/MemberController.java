package sejong.back.web.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sejong.back.domain.login.AddMemberForm;
import sejong.back.domain.login.LoginService;
import sejong.back.domain.member.Member;
import sejong.back.domain.repository.MemberRepository;
import sejong.back.web.SessionConst;
import sejong.back.web.login.LoginController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    private final LoginService loginService;

    @GetMapping
    public String members(HttpServletRequest request,Model model) {//멤버 검색 페이지이다. 여기서 자기 정보 수정 버튼 누르면 이동할 수 있도록 자기의 멤버도 model로 보내자.
        List<Member> members = memberRepository.findAll();
        log.info("members={}", members);

        HttpSession session = request.getSession(false);//세션을 가져와서 자기 member key를 뽑아야한다.
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);//다운 캐스팅.
        model.addAttribute("members", members);
        model.addAttribute("member",member);
        return "members/members";
    }

    @GetMapping("/{memberKey}")//멤버 상세 페이지이다.
    public String member(@PathVariable long memberKey, Model model) {
        Member member = memberRepository.findByKey(memberKey);
        model.addAttribute("member", member);
        return "members/member"; //그 멤버의 페이지를 보여줘야 한다.
    }

    @GetMapping("/add")//회원 가입.
    public String addForm(@ModelAttribute("addMemberForm") AddMemberForm addMemberForm) {
        return "members/addMemberForm";
    }

    @PostMapping("/add")//여기서 회원가입절차에 따라 회원을 db에 save한다.
    public String save(@Validated @ModelAttribute AddMemberForm addMemberForm, BindingResult result) throws IOException {
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
        Member searchmember=memberRepository.findByLoginId(validatemember.getStudentId());//db에 회원 조회.

        if(searchmember==null){//db에 없으면, 회원 가입 절차 정상적으로 진행해야 한다.
            memberRepository.save(validatemember);//db에 저장.
            log.info("savedMember={}",memberRepository.findAll());
            return "redirect:/login";
        }
        else {
            result.reject("loginFail","회원 가입된 사용자입니다. 로그인 페이지로 이동해주세요.");
            log.info("회원 가입된 사용자입니다={}");
            return "members/addMemberForm";//다시 입력해야한다.
        }
    }

    @GetMapping("/{memberKey}/edit")//자시 자신만 수정 할 수 있도록 해야한다. 다른애 꺼 수정 못하게 해야 한다.
    public String editForm(@PathVariable Long memberKey, Model model) {
        Member member = memberRepository.findByKey(memberKey);
        model.addAttribute("member", member);
        return "members/editForm";
    }

//    @PostMapping("/{itemId}/edit")
//    public String edit(@PathVariable Long itemId, @Validated @ModelAttribute("item") ItemUpdateForm form, BindingResult bindingResult) {
//
//        //특정 필드 예외가 아닌 전체 예외
//        if (form.getPrice() != null && form.getQuantity() != null) {
//            int resultPrice = form.getPrice() * form.getQuantity();
//            if (resultPrice < 10000) {
//                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
//            }
//        }
//
//        if (bindingResult.hasErrors()) {
//            log.info("errors={}", bindingResult);
//            return "items/editForm";
//        }
//
//        Item itemParam = new Item();
//        itemParam.setItemName(form.getItemName());
//        itemParam.setPrice(form.getPrice());
//        itemParam.setQuantity(form.getQuantity());
//
//        itemRepository.update(itemId, itemParam);
//        return "redirect:/items/{itemId}";
//    }

}
