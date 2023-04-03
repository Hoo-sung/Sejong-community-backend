package sejong.back.web.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.back.domain.member.AddMemberForm;
import sejong.back.domain.service.LoginService;
import sejong.back.domain.member.Member;
import sejong.back.domain.member.UpdateMemberForm;
import sejong.back.domain.service.MemberService;
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

    private final MemberService MemberService;

    private final LoginService loginService;


//    @ModelAttribute("memberTypes")//모델에 통쨰로보여줘야 라디오 버튼이든 뭐든 내용물을 통쨰로 출력할 수 있다.
//    public MemberType[] memberTypes(){
//        return MemberType.values();//전체 다 가져오기.
//    }



    @GetMapping
    public String members(HttpServletRequest request,Model model) {//멤버 검색 페이지이다. 여기서 자기 정보 수정 버튼 누르면 이동할 수 있도록 자기의 멤버도 model로 보내자.
        List<Member> members = MemberService.findAll();
        log.info("members={}", members);

        HttpSession session = request.getSession(false);//세션을 가져와서 자기 member key를 뽑아야한다.
        long myKey = (Long) session.getAttribute(SessionConst.DB_KEY);//다운 캐스팅.
        Member member = MemberService.findByKey(myKey);
        model.addAttribute("members", members);
        model.addAttribute("member",member);
        return "members/members";
    }

    @GetMapping("/{memberKey}")//멤버 상세 페이지이다.
    public String member(@PathVariable long memberKey,HttpServletRequest request, Model model) {

        HttpSession session = request.getSession(false);
        Long dbKey = (Long) session.getAttribute(SessionConst.DB_KEY);

        Member member = MemberService.findByKey(memberKey);
        model.addAttribute("member", member);

        if(dbKey==memberKey)
            return "members/member2";//자기 자신의정보를 볼때는 쪽지 보내기가 없는 html을 보내야한다.
        else
            return "members/member"; //그 멤버의 페이지를 보여줘야 한다.
    }

    @GetMapping("/add")//회원 가입. radio 버튼은 하나를 반드시 가지고 있어야 하므로 여기서 type을 만들어 줘야 한다.
    public String addForm(@ModelAttribute("addMemberForm") AddMemberForm addMemberForm) {
        return "members/addMemberForm";
    }

    @PostMapping("/add")//여기서 회원가입절차에 따라 회원을 db에 save한다.
    /**
     * 학사시스템을 바탕으로 이름, 학번 등 가져올 수 있는 정보는 가져와서 validatemember에 저장되어있다.
     * 그리므로 여기에 회원가입 폼에서 사용자가 추가해야할 요소들인
     * 1.신입 재학, 휴학 여부를 선택하고
     * 2.태그들을 가지고 싶은거 선택하게 해야한다.
     *
     * 이것을 받아서 학사 시스템으로 로그인 잘 되서 검증된 멤버인 validatemember에 담고 내용을 추가해서 저장한다. db에
     */
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
        Member searchmember=MemberService.findByLoginId(validatemember.getStudentId());//db에 회원 조회.

        if(searchmember==null){//db에 없으면, 회원 가입 절차 정상적으로 진행해야 한다.

            /**
             * radio 값 하나는 반드시 선택해야 해서 주어야 한다.
             * 태그들 같은 경우는 이런 방식 안해도 될듯?
             */
//            validatemember.setMemberType(addMemberForm.getMemberType());//post 폼에서 가져온 값 바탕으로 저장.
//            validatemember.setTags(addMemberForm.getTags());//태그도 없데이트 해야함.


            validatemember.setNickName(addMemberForm.getNickName());//닉네임까지 멤버 db에 저장한다.
            MemberService.save(validatemember);//db에 저장.
            log.info("savedMember={}",MemberService.findAll());
            return "redirect:/login";
        }
        else {
            result.reject("loginFail","회원 가입된 사용자입니다. 로그인 페이지로 이동해주세요.");
            log.info("회원 가입된 사용자입니다={}");
            return "members/addMemberForm";//다시 입력해야한다.
        }
    }

    @GetMapping("/{memberKey}/edit")//자시 자신만 수정 할 수 있도록 해야한다. 다른애 꺼 수정 못하게 해야 한다.
    public String editForm(@PathVariable Long memberKey, Model model,HttpServletRequest request) {//세션에 있는 db key를 보고, 자시 key일때만 자기 페이지 수정을 할 수있도록, 다른 사용자 정보 수정 시도시, 내정보 수정으로 redirect시.

        HttpSession session=request.getSession(false);//있는 세션을 가져온다.
        Long myKey = (Long) session.getAttribute(SessionConst.DB_KEY);

        if(myKey==memberKey){//자기가 자신의 edit url을 요청한 경우.
            Member member = MemberService.findByKey(memberKey);
            model.addAttribute("member", member);
            return "members/editForm";
        }

        else{//남의 페이지 시도한 경우. 자기 멤버 상세를 보여주도록 하자.
            Member member = MemberService.findByKey(myKey);//
            model.addAttribute("member", member);
            return "redirect:/";
        }

    }

    @PostMapping("/{memberKey}/edit")//여기를 공개 정보 수정이라고 하자.
    public String edit(@PathVariable Long memberKey, @Validated @ModelAttribute("updateMemberForm") UpdateMemberForm form, BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "members/editForm";
        }

        Member updateMemberParam = MemberService.findByKey(memberKey);
//        updateMemberParam.setMemberType(form.getMemberType());
//        updateMemberParam.setTags(form.getTags());

        /**
         * Todo 객체를 찾아서 똒같은걸 하나 new해서 updatemember을 만들어서 memory.update시키냐, 아님 간단하게 하냐 고민.
         */
        MemberService.update(memberKey, form);

        return "redirect:/members/{memberKey}";
    }

}
