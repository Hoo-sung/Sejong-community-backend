
package sejong.back.web.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sejong.back.domain.login.LoginForm;
import sejong.back.domain.member.Member;
import sejong.back.domain.service.LoginService;
import sejong.back.domain.service.MemberService;
import sejong.back.domain.service.NoticeService;
import sejong.back.exception.SignUpRequiredException;
import sejong.back.exception.WrongLoginException;
import sejong.back.exception.WrongLogoutException;
import sejong.back.web.ResponseResult;
import sejong.back.web.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final MemberService memberService;
    private final LoginService loginService;
    private final NoticeService noticeService;

    @GetMapping("/login")
    public LoginCheck loginCheck(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionConst.DB_KEY) == null) { //로그인이 안 되어있거나 세션이 만료된 경우
            log.info("[loginCheck] login X");
            return new LoginCheck(false, null);
        }

        Long myKey = (Long) session.getAttribute(SessionConst.DB_KEY);
        List<NonReadSticker> alarmCount = noticeService.getNotice(myKey);
        log.info("[LoginCheck] login O: {}, alarmCount={}", myKey, alarmCount);
        return new LoginCheck(true, alarmCount);
    }

    //로그인 성공했을 떄 기본적인 리다이렉트 경로는 /forest(트리(게시판) 검색 페이지)
    @PostMapping("/login")
    public ResponseResult<Object> login(@RequestBody LoginForm form,
                                        HttpServletRequest request) throws IOException{

        Member validateMember = loginService.validateSejong(form.getStudentId(), form.getPassword());
        log.info("validateMember={}", validateMember);
        if (validateMember == null) { //로그인 정보가 세종대 계정정보와 다를 때
            log.error("아이디 또는 비밀번호가 맞지 않음");
            throw new WrongLoginException("아이디 또는 비밀번호가 맞지 않음");
        }

        Member loginMember = memberService.findByLoginId(form.getStudentId());
        log.info("loginMember={}", loginMember);
        if (loginMember == null) { //로그인 정보가 세종대 계정과 일치하지만 우리 서비스에 회원가입이 안 돼있을때

            log.error("회원가입 필요");
            throw new SignUpRequiredException("회원가입 필요");
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.DB_KEY, loginMember.getKey());

        return new ResponseResult<>(0,"로그인 성공");
        /**
         * 이부분을을 리다이렉트할 주소가 있으면,
         */

    }

    @PostMapping("/logout")
    public ResponseResult<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            return new ResponseResult<>("로그아웃 성공");
        }

        throw new WrongLogoutException("로그인하지 않은 사용자가 로그아웃 요청");
    }

    @AllArgsConstructor
    @Getter
    static class LoginCheck {
        private Boolean isLogin;
        private List<NonReadSticker> alarmCount;
    }
}
