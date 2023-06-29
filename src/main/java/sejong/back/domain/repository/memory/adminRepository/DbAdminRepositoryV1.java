package sejong.back.domain.repository.memory.adminRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;
import sejong.back.domain.member.Member;

import javax.sql.DataSource;
import java.sql.*;

@RequiredArgsConstructor
@Repository
public class DbAdminRepositoryV1 implements AdminRepository {

    private final DataSource dataSource;

    @Override
    public Boolean AdminCheck(Long id) throws SQLException {
        String sql="select * from admin where memberId=? ";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {

                return true;
            } else {
                return false;
                /*throw new NoSuchElementException("member not found studentid=" +
                        loginId);*/
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, rs);
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
