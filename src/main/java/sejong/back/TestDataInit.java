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
        Long studentId = 18011834L;

        Member member = null;
        try {
            member = loginService.validateSejong(studentId, "fa484869");
            member.setNickname("호호호");
            memberService.save(member);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}