package sejong.back.domain.repository.memory.memberRepository;

import com.mysql.cj.protocol.Resultset;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;
import sejong.back.domain.member.Member;
import sejong.back.domain.member.MemberGrade;
import sejong.back.domain.member.UpdateMemberForm;
import sejong.back.domain.repository.MemberRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.NoSuchElementException;


@Repository
public class DbMemberRepositoryV1 implements MemberRepository {

    private final DataSource dataSource;

    public DbMemberRepositoryV1(DataSource dataSource) {

        this.dataSource = dataSource;
    }

    @Override
    public Member save(Member member) throws SQLException {
        String sql="insert into member(name,department,studentid,nickname,currentgrade,status, openStudentId,openDepartment) values(?,?,?,?,?,?,?,?)";

        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs = null;
        try{
            con=getConnection();
            pstmt=con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);//auto increment로 생성된 키.
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getDepartment());
            pstmt.setLong(3, member.getStudentId());
            pstmt.setString(4, member.getNickname());
            pstmt.setString(5, member.getCurrentGrade());
            pstmt.setString(6, member.getStatus());
            pstmt.setBoolean(7, member.isOpenStudentId());
            pstmt.setBoolean(8, member.isOpenDepartment());//회원가입할땐 설정한 공개 범위로 설정.

            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                Long id = rs.getLong(1);
                member.setKey(id);
            }
            return member;
        } catch (SQLException e) {
            throw e;
        }

        finally{
            close(con,pstmt,null);
        }

    }

    @Override
    public Member findByLoginId(Long loginId) throws SQLException {
        String sql="select * from member where studentid=? ";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, loginId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setKey(rs.getLong("member_id"));
                member.setName(rs.getString("name"));
                member.setDepartment(rs.getString("department"));
                member.setStudentId(rs.getLong("studentid"));
                member.setNickname(rs.getString("nickname"));
                member.setCurrentGrade(rs.getString("currentgrade"));
                member.setStatus(rs.getString("status"));
                member.setOpenStudentId(rs.getBoolean("openStudentId"));
                member.setOpenDepartment(rs.getBoolean("openDepartment"));
                return member;
            } else {
                throw new NoSuchElementException("member not found studentid=" +
                        loginId);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, rs);
        }

    }

    @Override
    public List<Member> findAll() {
        return null;
    }

    @Override
    public void delete(Long key) throws SQLException {//db 저장된 키로 삭제해야 한다.

        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, key);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, null);
        }

    }


    @Override
    public Member findByKey(Long key) throws SQLException {

        String sql="select * from member where member_id=? ";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, key);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setKey(rs.getLong("member_id"));
                member.setName(rs.getString("name"));
                member.setDepartment(rs.getString("department"));
                member.setStudentId(rs.getLong("studentid"));
                member.setNickname(rs.getString("nickname"));
                member.setCurrentGrade(rs.getString("currentgrade"));
                member.setStatus(rs.getString("status"));
                member.setOpenStudentId(rs.getBoolean("openStudentId"));
                member.setOpenDepartment(rs.getBoolean("openDepartment"));

                return member;
            } else {
                throw new NoSuchElementException("member not found studentid=" + key);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, rs);
        }

    }

    @Override
    public void update(Long key, UpdateMemberForm form) throws SQLException {

        String sql="update member set nickname=?,openStudentId=?, openDepartment=? where member_id=?";


        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, form.getNickname());
            pstmt.setBoolean(2, form.isOpenStudentId());
            pstmt.setBoolean(3, form.isOpenDepartment());
            pstmt.setLong(4, key);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, null);
        }

    }

    private Connection getConnection() throws SQLException{//connection 객체 반환.

        Connection con = DataSourceUtils.getConnection(dataSource);
        return con;
    }

    private void close(Connection con, Statement stmt, ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);

        DataSourceUtils.releaseConnection(con, dataSource);
    }
}
