package sejong.back.domain.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sejong.back.domain.member.Member;

import org.assertj.core.api.*;

import java.util.List;


class MemoryMemberRepositoryTest {

    private MemoryMemberRepository memberRepository=new MemoryMemberRepository();


//    @AfterEach
//    void afterEach(){  //test가 끝날떄마다 memberrepository 비우기.
//        memberRepository.clearStore();
//    }

//    @Test
//    void save(){
//
//        //given
//        Member member1 = new Member("이후성", "컴공", "18011825");
//        Member member2 = new Member("김정민", "컴공", "17011843");
//        Member member3 = new Member("황중섭", "컴공", "12912992");
//
//        //when
//        Member savedMember1 = memberRepository.save(member1);
//        Member savedMember2 = memberRepository.save(member2);
//        Member savedMember3 = memberRepository.save(member3);
//
//        //then
//        Assertions.assertThat(savedMember1).isSameAs(member1);
//        Assertions.assertThat(savedMember2).isSameAs(member2);
//        Assertions.assertThat(savedMember3).isSameAs(member3);
//
//    }
//
//
//    @Test
//    void findAll(){
//
//        //given
//        Member member1 = new Member("이후성", "컴공", "18011825");
//        Member member2 = new Member("김정민", "컴공", "17011843");
//        Member member3 = new Member("황중섭", "컴공", "12912992");
//
//        memberRepository.save(member1);
//        memberRepository.save(member2);
//        memberRepository.save(member3);
//
//        //when
//        List<Member> result = memberRepository.findAll();
//        //then
//        Assertions.assertThat(result.size()).isEqualTo(3);
//
//    }



}