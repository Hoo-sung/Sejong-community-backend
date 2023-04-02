package sejong.back.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sejong.back.domain.repository.MemberRepository;
import sejong.back.domain.repository.TreeRepository;
import sejong.back.domain.repository.memory.memberRepository.MemoryMemberRepository;
import sejong.back.domain.repository.memory.treeRepository.MemoryTreeRepository;
import sejong.back.domain.service.LoginService;
import sejong.back.domain.service.MemberService;
import sejong.back.domain.service.TreeService;

@Configuration
public class MemoryConfig {

    @Bean
    public MemberRepository memberRepository(){
        return new MemoryMemberRepository();
    }

    @Bean
    public TreeRepository treeRepository(){
        return new MemoryTreeRepository();
    }

    @Bean
    public TreeService treeService(){
        return new TreeService(treeRepository());
    }

    @Bean
    public MemberService memberService(){
        return new MemberService(memberRepository());
    }

    @Bean
    public LoginService loginService(){
        return new LoginService(memberRepository());
    }
}
