package sejong.back.domain.repository;



import sejong.back.domain.member.Member;

import java.util.List;

public interface MemberRepository {

    public Member save(Member member);

    public Member findByLoginId(String loginId);

    public List<Member> findAll();

    public Member findByKey(Long key);


}
