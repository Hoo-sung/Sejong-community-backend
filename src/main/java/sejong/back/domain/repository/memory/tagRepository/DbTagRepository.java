package sejong.back.domain.repository.memory.tagRepository;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;
import sejong.back.domain.member.Member;
import sejong.back.domain.tag.Tag;
import sejong.back.domain.tree.UpdateTreeForm;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Repository
public class DbTagRepository {

    private final DataSource dataSource;

    public DbTagRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Tag save(Tag tag){

        String sql="insert into tag(tag_description) values(?)";


        Connection con=null;
        PreparedStatement pstmt=null;

        try{
            con=getConnection();
            pstmt=con.prepareStatement(sql);
            pstmt.setString(1, tag.getDescription());
            pstmt.executeUpdate();

            return tag;
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Failed to save tag(Data integrity violation)",e);
        }

        finally{
            close(con,pstmt,null);
        }
    }

    public Map<Integer,String> findAll() {
        String sql="select * from tag";

        Map<Integer, String> tags = new LinkedHashMap<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                tags.put(rs.getInt("tag_id"), rs.getString("tag_description"));

            } return tags;
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Failed to find All tags(Data integrity violation)",e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    public Tag findByTagId(int tag_Id) {
        String sql="select * from tag where tag_id=? ";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, tag_Id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Tag tag = new Tag();
                tag.setTag_Id(rs.getInt("tag_id"));
                tag.setDescription(rs.getString("tag_description"));

                return tag;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new EmptyResultDataAccessException("Failed to find tag",1,e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    public Tag findByDescription(String description) {
        String sql="select * from tag where tag_description=? ";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, description);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Tag tag = new Tag();
                tag.setTag_Id(rs.getInt("tag_id"));
                tag.setDescription(rs.getString("tag_description"));

                return tag;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new EmptyResultDataAccessException("Failed to find Description tag",1,e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    public void update(int tag_id, String updateContext) {//바꾸자 하는 태그 내용.

        String sql="update tag set tag_description=? where tag_id=?";


        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, updateContext);
            pstmt.setInt(2,tag_id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Failed to update tag(Data integrity violation",e);
        } finally {
            close(con, pstmt, null);
        }

    }


    public void delete(int tag_id) {
        String sql = "delete from tag where tag_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, tag_id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Failed to delete tag(Data integrity violation",e);
        } finally {
            close(con, pstmt, null);
        }

    }

    private Connection getConnection()  {//connection 객체 반환.

        Connection con = DataSourceUtils.getConnection(dataSource);
        return con;
    }

    private void close(Connection con, Statement stmt, ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);

        DataSourceUtils.releaseConnection(con, dataSource);
    }

}
