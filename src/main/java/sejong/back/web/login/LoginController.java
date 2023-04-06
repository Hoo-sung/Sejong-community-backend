package sejong.back.web.login;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sejong.back.domain.login.LoginForm;
import sejong.back.domain.member.Member;
import sejong.back.domain.service.LoginService;
import sejong.back.domain.service.MemberService;
import sejong.back.exception.WrongLoginException;
import sejong.back.exception.WrongLogoutException;
import sejong.back.web.ResponseResult;
import sejong.back.web.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final MemberService memberService;
    private final LoginService loginService;

    @GetMapping("/login")
    public String login() {
        return "회원가입 시 리다이렉트 되는지 확인하기 위해 대충 만들어둔 메서드";
    }

    //로그인 성공했을 떄 기본적인 리다이렉트 경로는 /forest(트리(게시판) 검색 페이지)
    @PostMapping("/login")
    public void login(@ModelAttribute LoginForm form,
                                   @RequestParam(defaultValue = "/forest") String redirectURI,
                                   HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {

        Member validateMember = loginService.validateSejong(form.getStudentId(), form.getPassword());
        log.info("validateMember={}", validateMember);
        if (validateMember == null) { //로그인 정보가 세종대 계정정보와 다를 때
            throw new WrongLoginException("아이디나 비밀번호가 세종대 계정과 다름");
        }

        Member loginMember = memberService.findByLoginId(form.getStudentId());
        log.info("loginMember={}", loginMember);
        if (loginMember == null) { //로그인 정보가 세종대 계정과 일치하지만 우리 서비스에 회원가입이 안 돼있을때
            throw new WrongLoginException("회원가입 필요");
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.DB_KEY, loginMember.getKey());
        log.info("redirectURI={}", redirectURI);
        response.sendRedirect(redirectURI);
    }

    @PostMapping("/logout")
    public ResponseResult<?> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            return new ResponseResult<>("로그아웃 성공");
        }

        throw new WrongLogoutException("로그인하지 않은 사용자가 로그아웃 요청");
    }
}
