package sejong.back.domain.repository;

import sejong.back.domain.member.AddMemberForm;
import sejong.back.domain.member.Member;
import sejong.back.domain.member.UpdateMemberForm;
import sejong.back.web.login.NonReadSticker;

import java.sql.SQLException;
import java.util.List;

public interface MemberRepository {

    public Member save(Member member);

    public Member findByLoginId(Long loginId);

    public List<Member> findAll() throws SQLException;

    Member findByName(String name) throws SQLException;

    Member findByNickName(String nickname) throws SQLException;


    public Member findByKey(Long key);

    public void update(Long key, UpdateMemberForm form);

    public void delete(Long key);

}
