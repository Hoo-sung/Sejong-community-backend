package sejong.back.web.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.back.domain.member.AddMemberForm;
import sejong.back.domain.login.LoginService;
import sejong.back.domain.member.Member;
import sejong.back.domain.member.MemberType;
import sejong.back.domain.member.UpdateMemberForm;
import sejong.back.domain.repository.MemberRepository;
import sejong.back.web.SessionConst;

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


    @ModelAttribute("memberTypes")//모델에 통쨰로보여줘야 라디오 버튼이든 뭐든 내용물을 통쨰로 출력할 수 있다.
    public MemberType[] memberTypes(){
        return MemberType.values();//전체 다 가져오기.
    }


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

    @GetMapping("/add")//회원 가입. radio 버튼은 하나를 반드시 가지고 있어야 하므로 여기서 type을 만들어 줘야 한다.
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

            /**
             * radio 값 하나는 반드시 선택해야 해서 주어야 한다.
             * 태그들 같은 경우는 이런 방식 안해도 될듯?
             */
            validatemember.setMemberType(addMemberForm.getMemberType());//post 폼에서 가져온 값 바탕으로 저장.

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

    @PostMapping("/{memberKey}/edit")
    public String edit(@PathVariable Long memberKey, @Validated @ModelAttribute("updateMemberForm") UpdateMemberForm form, BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "members/editForm";
        }

        Member updateMemberParam = memberRepository.findByKey(memberKey);
        updateMemberParam.setMemberType(form.getType());
        /**
         * Todo 객체를 찾아서 똒같은걸 하나 new해서 updatemember을 만들어서 memory.update시키냐, 아님 간단하게 하냐 고민.
         */


//        member.setTag(form.getTag());

        memberRepository.update(memberKey, updateMemberParam);

        return "redirect:/members/{memberKey}";
    }

}
