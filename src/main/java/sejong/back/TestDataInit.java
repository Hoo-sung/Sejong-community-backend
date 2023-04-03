package sejong.back;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import sejong.back.domain.member.Member;
import sejong.back.domain.service.LoginService;
import sejong.back.domain.service.MemberService;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
public class TestDataInit {

    private final MemberService memberService;

    private final LoginService loginService;

    /**
     * 확인용 초기 데이터 추가
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        log.info("test data init");
        int a=18011825;
        int b=18011834;


        Member member1 = null;
        try {
            member1 = loginService.validateSejong(Long.valueOf(a), "님꺼넣으셈");
            Member member2 = loginService.validateSejong(Long.valueOf(b) ,"fa484869");
            member1.setNickName("하하하");
            member2.setNickName("호호호");
            memberService.save(member1);
            memberService.save(member2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}