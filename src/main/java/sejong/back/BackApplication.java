package sejong.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import sejong.back.config.MemoryConfig;



@Import(MemoryConfig.class)
@SpringBootApplication(scanBasePackages ="sejong.back.web")
public class BackApplication {


	public static void main(String[] args) {
		SpringApplication.run(BackApplication.class, args);
	}
}
