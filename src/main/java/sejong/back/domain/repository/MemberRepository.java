package sejong.back.domain.repository;

import sejong.back.domain.member.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {


    public Member save(Member member);

    public Member findByLoginId(String loginId);

    public List<Member> findAll();

    public Member findByKey(Long key);

    public void update(Long key, Member updateMember);


}
