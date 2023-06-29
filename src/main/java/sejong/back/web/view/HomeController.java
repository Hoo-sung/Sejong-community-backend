package sejong.back.web.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sejong.back.domain.member.Member;
import sejong.back.domain.repository.MemberRepository;
import sejong.back.domain.service.MemberService;
import sejong.back.web.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class HomeController {
    private final MemberRepository repository;
    private final MemberService memberService;

    @GetMapping
    public String LoginHome(HttpServletRequest request, Model model) {

        //session이 없으면 홈으로.
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "home";
        }

        //세션은 있는데 만료되어서 sessionid에 해당하는 member가 세션 저장소에 없는 경우,
//        Member loginMember = (Member) session.getAttribute(SessionConst.DB_KEY);
//        if(loginMember==null){
//            return "home";
//        }
        Long dbKey = (Long) session.getAttribute(SessionConst.DB_KEY);
        if (dbKey == null) {
            return "home";
        }
        //세션이 유지되면 로그인 화면으로 이동.
        Member loginMember = memberService.findByKey(dbKey);
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

}
