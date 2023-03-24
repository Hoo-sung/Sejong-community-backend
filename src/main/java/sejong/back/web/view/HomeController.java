package sejong.back.web.view;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sejong.back.domain.member.Member;
import sejong.back.domain.repository.MemberRepository;
import sejong.back.web.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Controller
public class HomeController {
    private final MemberRepository repository;
    //첫페이지
    @GetMapping("/")
    public String LoginHome(HttpServletRequest request, Model model) {

        //session이 없으면 홈으로.
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "home";
        }

        Long dbKey = (Long) session.getAttribute(SessionConst.DB_KEY);
        if(dbKey==null){
            return "home";
        }
        //세션이 유지되면 로그인 화면으로 이동.
        Member loginMember = repository.findByKey(dbKey);
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

}
