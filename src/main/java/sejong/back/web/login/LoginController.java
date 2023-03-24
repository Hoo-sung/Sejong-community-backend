package sejong.back.web.login;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.back.domain.login.LoginService;
import sejong.back.domain.member.Member;
import sejong.back.domain.repository.MemberRepository;
import sejong.back.exception.WrongLoginException;
import sejong.back.web.Response;
import sejong.back.web.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final MemberRepository memberRepository;
    private final LoginService loginService;

    //    @PostMapping("/login")
    public String loginCopied(@Validated @ModelAttribute LoginForm form, BindingResult bindingResult,
                              @RequestParam(defaultValue = "/") String redirectURI,
                              HttpServletRequest request, Model model) throws IOException {
//        if (bindingResult.hasErrors()) {
//            return "login/loginForm";
//        }

        Member member = memberRepository.findByLoginId(form.getStudentId());

        // 아마 이게 학번 뿐 아니라 비밀번호도 맞는지 체크하기 위한 용도인 듯
        // TODO 이미 회원가입할 때 파싱해서 가져왔는데, 다시 파싱하는 것 말고 더 좋은 방법 없나?
        Member validateMember = loginService.validateSejong(form.getStudentId(), form.getPassword());

        if (validateMember == null) {
            bindingResult.reject("loginFail", "아이디, 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }
        if (validateMember != null && member == null) {//DB에 저장이 안되어 있으면 회원가입을 먼저 해야한다.

            log.info("18011825 저장되어야 한다={}", memberRepository.findAll());
            bindingResult.reject("loginFail", "회원 가입을 하셔야 합니다.");
            return "redirect:/members/add";
        } else {
            //정상 처리 로직, 세션이 있으면 세션 반환, 없으면 새로 생성한다.
            HttpSession session = request.getSession(true);
            session.setAttribute(SessionConst.LOGIN_MEMBER, member);
            return "redirect:" + redirectURI;//처음 사용자가 접속하려는 url로 리다이렉트 해준다.
        }
    }

    @PostMapping("/login")
    public Response<?> login(@RequestBody LoginForm form,
                             @RequestParam(defaultValue = "/") String redirectURI,
                             HttpServletRequest request, Model model) throws IOException {

        Member loginMember = loginService.login(form.getStudentId(), form.getPassword());
        log.info("loginMember={}", loginMember);

        if (loginMember == null) { //form 상의 아이디나 패스워드가 틀렸을 때
            throw new WrongLoginException("아이디 또는 비밀번호 오류");
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
        return new Response<>("success", "로그인 성공", loginMember);
    }

    @PostMapping("/logout")
    public Response<?> logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return new Response<>("success", "로그아웃 성공", null);
    }
}
