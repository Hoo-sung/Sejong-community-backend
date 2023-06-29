package sejong.back.web.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import sejong.back.domain.repository.memory.adminRepository.DbAdminRepositoryV1;
import sejong.back.web.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class AdminInterceptor implements HandlerInterceptor{


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("Admin Check");
        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionConst.DB_KEY) == null) {//로그인 안된거면 로그인페이지로 리다이렉트

            log.info("로그인 X 사용자 요청");
            response.sendRedirect("/admin/login?redirectURI=" + requestURI);
            return false;// login화면으로 리다이렉트 시키고 이제 여기서 진행 종료한다.
        }
        
        //Admin인지 체크
        boolean adminCheck = (boolean) session.getAttribute(SessionConst.ADMIN);

        //Admin의 경우
        if (adminCheck)
            return true;
        //아닌 경우
        log.info("관리자 외 접근 금지");
        response.sendRedirect("/admin?redirectURI=" + requestURI);
        return false;// login화면으로 리다이렉트 시키고 이제 여기서 진행 종료한다.

    }
}
