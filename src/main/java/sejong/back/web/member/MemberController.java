package sejong.back.web.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.back.domain.member.AddMemberForm;
import sejong.back.domain.member.Member;
import sejong.back.domain.member.UpdateMemberForm;
import sejong.back.domain.service.LoginService;
import sejong.back.domain.service.MemberService;
import sejong.back.exception.DoubleSignUpException;
import sejong.back.exception.WrongSignUpException;
import sejong.back.web.ResponseResult;
import sejong.back.web.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

//TODO rest api를 쓰는거면 로그인 안된 사용자가 접근할 떄 리다이렉트시키는 걸 서버에서 해줘야하는거 아님 클라에서 해주는거?
@Slf4j
@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final LoginService loginService;

//    @ModelAttribute("memberTypes")//모델에 통쨰로보여줘야 라디오 버튼이든 뭐든 내용물을 통쨰로 출력할 수 있다.
//    public MemberType[] memberTypes(){
//        return MemberType.values();//전체 다 가져오기.
//    }

    @GetMapping
    public String members(HttpServletRequest request, Model model) {//멤버 검색 페이지이다. 여기서 자기 정보 수정 버튼 누르면 이동할 수 있도록 자기의 멤버도 model로 보내자.
        List<Member> members = memberService.findAll();
        log.info("members={}", members);

        HttpSession session = request.getSession(false);//세션을 가져와서 자기 member key를 뽑아야한다.
        long myKey = (Long) session.getAttribute(SessionConst.DB_KEY);//다운 캐스팅.
        Member member = memberService.findByKey(myKey);
        model.addAttribute("members", members);
        model.addAttribute("member", member);
        return "members/members";
    }

    @GetMapping("/{memberKey}")//멤버 상세 페이지이다.
    public String member(@PathVariable long memberKey, HttpServletRequest request, Model model) {

        HttpSession session = request.getSession(false);
        Long dbKey = (Long) session.getAttribute(SessionConst.DB_KEY);

        Member member = memberService.findByKey(memberKey);
        model.addAttribute("member", member);

        if (dbKey == memberKey)
            return "members/member2";//자기 자신의정보를 볼때는 쪽지 보내기가 없는 html을 보내야한다.
        else
            return "members/member"; //그 멤버의 페이지를 보여줘야 한다.
    }

    @GetMapping("/add")//회원 가입. radio 버튼은 하나를 반드시 가지고 있어야 하므로 여기서 type을 만들어 줘야 한다.
    public String addForm(@ModelAttribute("addMemberForm") AddMemberForm addMemberForm) {
        return "members/addMemberForm";
    }


    /**
     * @TODO 우리 서비스에서 회원가입할 때 입력한 이름과 세종대 계정에 등록된 이름이 다른 경우엔 어떻게 처리?
     * <p>
     * ==> 우선은 회원가입 시 이름이 아니라 닉네임을 작성하도록 변경해봤음.
     * 어차피 이름은 아이디, 비번만 맞으면 세종대 사이트에서 가져오면 되니까
     * 그래서 AddMemberForm의 name을 nickname으로 바꾸고, Member에 nickname 필드 추가
     * @TODO Gradle을 통해서 실행하면 이상하게 AddMemberForm을 인식하지 못함
     * <p>
     * 학사시스템을 바탕으로 이름, 학번 등 가져올 수 있는 정보는 가져와서 validatemember에 저장되어있다.
     * 그리므로 여기에 회원가입 폼에서 사용자가 추가해야할 요소들인
     * 1.신입 재학, 휴학 여부를 선택하고
     * 2.태그들을 가지고 싶은거 선택하게 해야한다.
     * <p>
     * 이것을 받아서 학사 시스템으로 로그인 잘 되서 검증된 멤버인 validatemember에 담고 내용을 추가해서 저장한다. db에
     */
    @PostMapping("/add")
    public ResponseResult<?> save(@Validated @ModelAttribute AddMemberForm addMemberForm, BindingResult bindingResult) throws IOException {

        if (bindingResult.hasErrors()) { //닉네임, 학번, 비번 중 빈 값이 있을 경우
            throw new WrongSignUpException("비어있는 값이 있음");
        }

        Member validateMember = loginService.validateSejong(addMemberForm.getStudentId(), addMemberForm.getPassword());
        if (validateMember == null) { // 잘못된 계정으로 회원가입 시도한 경우
            throw new WrongSignUpException("잘못된 계정으로 회원가입 시도");
        }

        //학사 시스템 회원 조회 성공.
        Member searchMember = memberService.findByLoginId(validateMember.getStudentId());//db에 회원 조회.

        if (searchMember == null) {//db에 없으면, 회원 가입 절차 정상적으로 진행해야 한다.
            //닉네임은 검증이 다 끝난 후 따로 추가. TODO 근데 setter가 컨트롤러에 직접 보이는게 좀 별로임
            validateMember.setNickname(addMemberForm.getNickname());
            memberService.save(validateMember);//db에 저장.
            log.info("savedMember={}", memberService.findAll());
            return new ResponseResult<>("회원가입 성공");
        } else {
            log.info("회원 가입된 사용자입니다={} {}", searchMember.getStudentId(), searchMember.getName());
            throw new DoubleSignUpException("이미 회원가입된 사용자");
        }
    }

    @GetMapping("/{memberKey}/edit")//자시 자신만 수정 할 수 있도록 해야한다. 다른애 꺼 수정 못하게 해야 한다.
    public String editForm(@PathVariable Long memberKey, Model model, HttpServletRequest request) {//세션에 있는 db key를 보고, 자시 key일때만 자기 페이지 수정을 할 수있도록, 다른 사용자 정보 수정 시도시, 내정보 수정으로 redirect시.

        HttpSession session = request.getSession(false);//있는 세션을 가져온다.
        Long myKey = (Long) session.getAttribute(SessionConst.DB_KEY);

        if (myKey == memberKey) {//자기가 자신의 edit url을 요청한 경우.
            Member member = memberService.findByKey(memberKey);
            model.addAttribute("member", member);
            return "members/editForm";
        } else {//남의 페이지 시도한 경우. 자기 멤버 상세를 보여주도록 하자.
            Member member = memberService.findByKey(myKey);//
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

        Member updateMemberParam = memberService.findByKey(memberKey);
//        updateMemberParam.setMemberType(form.getMemberType());
//        updateMemberParam.setTags(form.getTags());

        /**
         * Todo 객체를 찾아서 똒같은걸 하나 new해서 updatemember을 만들어서 memory.update시키냐, 아님 간단하게 하냐 고민.
         */
        memberService.update(memberKey, form);

        return "redirect:/members/{memberKey}";
    }
}
