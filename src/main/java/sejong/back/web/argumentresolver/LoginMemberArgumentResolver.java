package sejong.back.web.argumentresolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import sejong.back.domain.member.Member;
import sejong.back.domain.service.MemberService;
import sejong.back.web.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberService memberService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        log.info("supportsParameter 실행");

        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
        //@Login이 붙은 파리미터 타입이 Member이거나 Long일 때만 동작
        //TODO 이거보다 더 좋은 방법 없나?
        boolean hasCorrectType = (Member.class.isAssignableFrom(parameter.getParameterType())
                || Long.class.isAssignableFrom(parameter.getParameterType()));

        return hasLoginAnnotation && hasCorrectType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        log.info("resolveArgument 실행");

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);
        if (session == null) {
            //TODO 예외 처리. 근데 인터셉터가 있어서 여기서 굳이 세션이 null인지 확인할 필요가 있을까?
            throw new IllegalArgumentException("로그인하지 않은 사용자가 접근");
        }

        Long myKey = (Long) session.getAttribute(SessionConst.DB_KEY);
        if (Long.class.isAssignableFrom(parameter.getParameterType())) { //@Login이 붙은 파라미터의 타입이 Long이면 db Key를 반환
            return myKey;
        } else { //@Login이 붙은 파라미터의 타입이 Member이면 member를 반환
            return memberService.findByKey(myKey);
        }
    }
}
