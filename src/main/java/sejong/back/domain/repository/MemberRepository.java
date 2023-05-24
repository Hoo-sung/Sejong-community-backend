package sejong.back.domain.repository;

import sejong.back.domain.member.AddMemberForm;
import sejong.back.domain.member.Member;
import sejong.back.domain.member.UpdateMemberForm;
import sejong.back.web.login.NonReadSticker;

import java.sql.SQLException;
import java.util.List;

public interface MemberRepository {

    public Member save(Member member) throws SQLException;

    public Member findByLoginId(Long loginId) throws SQLException;

    public List<Member> findAll();

    public Member findByKey(Long key) throws SQLException;

    public void update(Long key, UpdateMemberForm form) throws SQLException;

    public void delete(Long key) throws SQLException;

    public List<NonReadSticker> getNotice(Long key) throws SQLException;


}
