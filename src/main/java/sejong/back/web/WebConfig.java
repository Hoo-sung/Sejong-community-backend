package sejong.back.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sejong.back.domain.service.MemberService;
import sejong.back.web.argumentresolver.LoginMemberArgumentResolver;
import sejong.back.web.interceptor.LoginCheckInterceptor;

import java.util.List;

/**
 * 이 인터셉트가 원래는 domain에 있었는데 web상 클래스에 의존하고 있으므로 밖으로 빼줬음
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginMemberArgumentResolver loginMemberArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://sejongsticker.s3-website.ap-northeast-2.amazonaws.com") // 허용할 출처
                .allowedMethods("GET", "POST","PATCH","DELETE") // 허용할 HTTP method
                .allowCredentials(true) // 쿠키 인증 요청 허용
                .maxAge(1500); // 원하는 시간만큼 pre-flight 리퀘스트를 캐싱
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
                /*
                registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error");
                */

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/login", "/logout", "/css/**", "/*.ico", "/error");
    }
}
