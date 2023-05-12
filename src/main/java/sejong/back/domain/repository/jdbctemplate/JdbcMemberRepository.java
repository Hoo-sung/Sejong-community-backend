package sejong.back.domain.repository.jdbctemplate;

import org.springframework.stereotype.Repository;
import sejong.back.domain.member.Member;
import sejong.back.domain.member.UpdateMemberForm;
import sejong.back.domain.repository.MemberRepository;

import java.sql.SQLException;
import java.util.List;


public class JdbcMemberRepository implements MemberRepository {


    @Override
    public Member save(Member member) {
        return null;
    }

    @Override
    public Member findByLoginId(Long loginId) {
        return null;
    }

    @Override
    public List<Member> findAll() {
        return null;
    }

    @Override
    public Member findByKey(Long key) {
        return null;
    }

    @Override
    public void update(Long key, UpdateMemberForm form) {

    }

    @Override
    public void delete(Long key) throws SQLException {

    }
}
