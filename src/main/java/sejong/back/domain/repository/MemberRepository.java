package sejong.back.domain.repository;

import sejong.back.domain.member.Member;
import sejong.back.domain.member.UpdateMemberForm;

import java.util.List;

public interface MemberRepository {

    public Member save(Member member);

    public Member findByLoginId(Long loginId);

    public List<Member> findAll();

    public Member findByKey(Long key);

    public void update(Long key, UpdateMemberForm form);


}
