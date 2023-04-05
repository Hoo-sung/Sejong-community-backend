package sejong.back.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sejong.back.domain.member.Member;
import sejong.back.domain.member.UpdateMemberForm;
import sejong.back.domain.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member save(Member member) {
        return memberRepository.save(member);
    }

    public Member findByKey(Long key) {
        return memberRepository.findByKey(key);
    }

    public Member findByLoginId(Long loginId) {//string으로 일치하는것 있나 찾기.
        return memberRepository.findByLoginId(loginId);
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public void update(Long key, UpdateMemberForm updateMember) {
        memberRepository.update(key, updateMember);
    }
}
