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




    @PostMapping("/login")
    public Response<?> login(@ModelAttribute LoginForm form,
                             @RequestParam(defaultValue = "/") String redirectURI,
                             HttpServletRequest request, Model model) throws IOException {

        Member loginMember = loginService.login(form.getStudentId(), form.getPassword());
        log.info("loginMember={}", loginMember);

        if (loginMember == null) { //form 상의 아이디나 패스워드가 틀렸을 때
            throw new WrongLoginException("아이디 또는 비밀번호 오류");
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
        session.setAttribute(SessionConst.DB_KEY,loginMember.getKey());
        /**
         * TODO
         * 세션값만 넘겨주기
         */
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
