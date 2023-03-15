package sejong.back.web.login;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.back.domain.login.LoginForm;
import sejong.back.domain.login.LoginService;
import sejong.back.domain.member.Member;
import sejong.back.domain.repository.MemberRepository;
import sejong.back.domain.repository.MemoryMemberRepository;
import sejong.back.web.SessionConst;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {//세종대 포탈 api를 이용해야 한다.

    private final MemberRepository memberRepository;

    private final LoginService loginService;
    @GetMapping("/login")//이 url로 Getmapping이 오면 폼을 반환해준다.
    public String loginForm(@ModelAttribute("loginForm")LoginForm form){

        return "login/loginForm";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute LoginForm form, BindingResult bindingResult,
                        @RequestParam(defaultValue = "/") String redirectURI,
                        HttpServletRequest request, Model model) throws IOException {
        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }

        Member member=memberRepository.findByLoginId(form.getStudentId());

        Member validatemember = loginService.validateSejong(form.getStudentId(), form.getPassword());

        if(validatemember==null){
            bindingResult.reject("loginFail","아이디, 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }
        if (validatemember!=null&&member==null) {//DB에 저장이 안되어 있으면 회원가입을 먼저 해야한다.

                log.info("18011825 저장되어야 한다={}",memberRepository.findAll());
                bindingResult.reject("loginFail","회원 가입을 하셔야 합니다.");
                return "redirect:/members/add";
            }
        else {
            //정상 처리 로직, 세션이 있으면 세션 반환, 없으면 새로 생성한다.
            HttpSession session = request.getSession(true);
            session.setAttribute(SessionConst.DB_KEY, member.getKey());//자기 키만 세션에 넣자.
            return "redirect:"+redirectURI;//처음 사용자가 접속하려는 url로 리다이렉트 해준다.
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request){

        HttpSession session=request.getSession(false);//있는 그대로 session을 가져온다.
        if(session!=null) {
            session.invalidate();//세션을 제거한다.
        }

        return "redirect:/";//home 컨트롤러로 redirect 시킨다.
    }


}
