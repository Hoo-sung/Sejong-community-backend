package sejong.back.domain.repository.memory.noticeRepository;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;
import sejong.back.domain.repository.NoticeRepository;
import sejong.back.web.login.NonReadSticker;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DbNoticeRepository implements NoticeRepository {

    private  final DataSource dataSource;

    public DbNoticeRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<NonReadSticker> getNotice(Long key)  {
        String sql = "select * from notice where member_id=?";
        List<NonReadSticker> alarmCount = new ArrayList<>();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con =  getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, key);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                alarmCount.add(new NonReadSticker(
                        rs.getString("title"),
                        rs.getLong("tree_id"),
                        rs.getInt("not_read_count")));
            }
            return alarmCount;
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Failed to getNotice notice(Data integrity violation",e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    @Override
    public void updateNotice(Long toMemberKey, Long treeId, String title){
        String sql = "select * from notice where member_id=? and tree_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, toMemberKey);
            pstmt.setLong(2, treeId);
            rs = pstmt.executeQuery();
            if (rs.next()) { //아직 그 트리에 안 읽은 스티커가 있는 경우 -> 해당 칼럼의 not_read_count +1
                noticeCountUp(toMemberKey, treeId, rs.getInt("not_read_count"));
            } else { //그 트리에 있는 모든 스티커를 이미 읽은 경우 -> 새로운 칼럼 생성
                createNotice(toMemberKey, treeId, title);
            }
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Failed to updateNotice notice(Data integrity violation",e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    @Override
    public void deleteNotice(Long memberKey, Long treeId){
        String sql = "delete from notice where member_id=? and tree_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, memberKey);
            pstmt.setLong(2, treeId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Failed to deleteNotice notice(Data integrity violation",e);
        } finally {
            close(con, pstmt, null);
        }

    }

    private void noticeCountUp(Long toMemberKey, Long treeId, Integer count){
        String sql = "update notice set not_read_count=? where member_id=? and tree_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, ++count);
            pstmt.setLong(2, toMemberKey);
            pstmt.setLong(3, treeId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Failed to countUp notice(Data integrity violation",e);
        } finally {
            close(con, pstmt, null);
        }
    }

    private void createNotice(Long toMemberKey, Long treeId, String title) {
        String sql = "insert into notice(member_id, tree_id, title, not_read_count) values(?,?,?,?)";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, toMemberKey);
            pstmt.setLong(2, treeId);
            pstmt.setString(3, title);
            pstmt.setInt(4, 1);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Failed to createNotice notice(Data integrity violation",e);
        } finally {
            close(con, pstmt, null);
        }
    }


    private Connection getConnection() {//connection 객체 반환.

        Connection con = DataSourceUtils.getConnection(dataSource);
        return con;
    }

    private void close(Connection con, Statement stmt, ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);

        DataSourceUtils.releaseConnection(con, dataSource);
    }
}
