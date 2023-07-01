package sejong.back.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import sejong.back.web.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();


        log.info("인증 체크 인터셉터 실행 {}", requestURI);
        //POST /members 로 요청이 왔을 경우 인터셉터를 성공시킨다.
        if (requestURI.equals("/members") && request.getMethod().equals("POST")) return true;

        HttpSession session = request.getSession(false);//세션을 있는 그대로 가져온다.

        /**
         * 예외적으로 /members post인 경우에는 인터셉터가 적용 안되게 하자.
         */

        if(request.getMethod().equals("POST") &&request.getRequestURI().equals("/members")){
            return true;//controller로 쭉 진행.
        }
        else if (session == null || session.getAttribute(SessionConst.DB_KEY) == null) {//로그인 안된거면 로그인페이지로 리다이렉트
            log.info("미인증 사용자 요청");

            //TODO 이렇게 리다이렉트 시키는 부분들은 나중에 클라이언트 쪽 url로 바꿔야 할 듯 (ex. https://www.sejongcommunity.com/login?redirectURL=)

//            response.setHeader("Location", "http://localhost:8080/login?redirectURL=" + requestURI);
//            response.sendRedirect("http://sejongsticker.s3-website.ap-northeast-2.amazonaws.com/login?redirectURL=" + requestURI);
//            throw new WrongSessionIdException("로그인 화면으로 이동합니다.")
            return false;
        }


        return true;
    }

}
