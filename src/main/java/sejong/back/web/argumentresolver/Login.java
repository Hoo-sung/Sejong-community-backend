package sejong.back.web.argumentresolver;

import sejong.back.domain.member.Member;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Login이 붙은 파라미터의 클래스 타입에 따라 주입받는 객체가 다름
 * 1. @Login Member member: 로그인한 멤버 객체
 * 2. @Login Long myKey: 로그인한 멤버의 db key
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Login {
}
