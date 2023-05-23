package sejong.back.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sejong.back.domain.repository.MemberRepository;
import sejong.back.domain.repository.StickerRepository;
import sejong.back.domain.repository.TreeRepository;
import sejong.back.domain.repository.memory.memberRepository.DbMemberRepositoryV1;
import sejong.back.domain.repository.memory.memberRepository.MemoryMemberRepository;
import sejong.back.domain.repository.memory.stickerRepository.DbStickerRepositoryV1;
import sejong.back.domain.repository.memory.tagRepository.DbTagRepository;
import sejong.back.domain.repository.memory.treeRepository.DbTreeRepositoryV1;
import sejong.back.domain.repository.memory.treeRepository.MemoryTreeRepository;
import sejong.back.domain.repository.memory.tree_tag.DbTree_TagRepository;
import sejong.back.domain.service.*;

import javax.sql.DataSource;


@Configuration
@RequiredArgsConstructor
public class DatabaseV1Config {


    private final DataSource dataSource;
    @Bean
    public DbMemberRepositoryV1 memberRepository(){
        return new DbMemberRepositoryV1(dataSource);
    }

    @Bean
    public DbTreeRepositoryV1 treeRepository(){
        return new DbTreeRepositoryV1(dataSource);
    }

    @Bean
    public DbTagRepository tagRepository(){return new DbTagRepository(dataSource);}

    @Bean
    public DbTree_TagRepository tree_tagRepository(){ return new DbTree_TagRepository(dataSource);}

    @Bean
    public DbStickerRepositoryV1 stickerRepository(){
        return new DbStickerRepositoryV1(dataSource);
    }


    @Bean
    public TreeService treeService(){
        return new TreeService(treeRepository(),memberRepository(),tagRepository(),tree_tagRepository());
    }

    @Bean
    public MemberService memberService(){
        return new MemberService(memberRepository());
    }

    @Bean
    public LoginService loginService(){
        return new LoginService(memberRepository());
    }

    @Bean
    public TagService tagService(){return new TagService(tagRepository());}

    @Bean
    public TreeTagService treeTagService(){return new TreeTagService(tree_tagRepository());}

    @Bean
    public StickerService stickerService(){
        return new StickerService(stickerRepository(),memberRepository(),treeRepository());
    }


}
