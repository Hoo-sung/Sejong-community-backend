package sejong.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import sejong.back.config.DatabaseV1Config;
import sejong.back.domain.service.LoginService;
import sejong.back.domain.service.MemberService;

@Import(DatabaseV1Config.class)
@SpringBootApplication(scanBasePackages ="sejong.back.web")
public class BackApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackApplication.class, args);
	}

	@Bean
	@Profile("local")
	public TestDataInit testDataInit(MemberService memberService, LoginService loginService) {
		return new TestDataInit(memberService,loginService);
	}
}
