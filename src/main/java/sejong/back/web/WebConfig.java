package sejong.back.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
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
public class WebConfig implements WebMvcConfigurer {

/*    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }*/

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
                .excludePathPatterns("/", "/members/add", "/login", "/logout", "/css/**", "/*.ico", "/error");
    }
}
