package sejong.back.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sejong.back.domain.member.Member;
import sejong.back.domain.member.UpdateMemberForm;
import sejong.back.domain.repository.MemberRepository;
import sejong.back.web.login.NonReadSticker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member save(Member member)  {
        return memberRepository.save(member);
    }

    public Member findByKey(Long key) {
        return memberRepository.findByKey(key);
    }

    public Member findByLoginId(Long loginId) {//string으로 일치하는것 있나 찾기.
        return memberRepository.findByLoginId(loginId);
    }

    public List<Member> findAll() throws SQLException {
        return memberRepository.findAll();
    }

    public Member findByName(String name) throws SQLException {
        return memberRepository.findByName(name);
    }

    public Member findByNickName(String nickname) throws SQLException {
        return memberRepository.findByNickName(nickname);
    }

    public List<Member> searchMember(String category, String keyword) throws SQLException {
        List<Member> members = new ArrayList<>();
        Member member = null;
        if (category != null && keyword.length()>0) {

            if (category.equals("studentId")) {
                member = memberRepository.findByLoginId(Long.valueOf(keyword));
            } else if (category.equals("name"))
                member = memberRepository.findByName(keyword);
            else if (category.equals("nickname"))
                member = memberRepository.findByNickName(keyword);

            if (member!=null)
                members.add(member);
        }
        else
            members = memberRepository.findAll();

        return members;
    }

    public void update(Long key, UpdateMemberForm updateMember) {
        memberRepository.update(key, updateMember);
    }

    public void delete(Long key)  {
        memberRepository.delete(key);
    }


}
