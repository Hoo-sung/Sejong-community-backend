package sejong.back.domain.repository.memory.tree_tag;


import org.springframework.boot.jta.atomikos.AtomikosDependsOnBeanFactoryPostProcessor;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;
import sejong.back.domain.tag.Tag;
import sejong.back.domain.tree_tag.Tree_Tag;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;

@Repository
public class DbTree_TagRepository {

    private final DataSource dataSource;

    public DbTree_TagRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Tree_Tag save(Tree_Tag treeTag) throws SQLException {

        String sql="insert into tree_tag(tree_id,tag_id) values(?,?)";


        Connection con=null;
        PreparedStatement pstmt=null;

        try{
            con=getConnection();
            pstmt=con.prepareStatement(sql);
            pstmt.setLong(1, treeTag.getTree_id());
            pstmt.setInt(2, treeTag.getTag_id());

            pstmt.executeUpdate();

            return treeTag;
        } catch (SQLException e) {
            throw e;
        }

        finally{
            close(con,pstmt,null);
        }
    }

    public ArrayList<Tree_Tag> findByTree_Id(Long tree_id) throws SQLException {//하나의 tree id로 조회되는 태그들을 다 가져와야한다. (한 게시물에 걸린 태그 다.)
        String sql="select * from tree_tag where tree_id=? ";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList<Tree_Tag> tags = new ArrayList<>();
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, tree_id);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                tags.add(new Tree_Tag(rs.getLong("tree_id"), rs.getInt("tag_id")));
            }
            if(tags.size()==0){
                throw new NoSuchElementException("treetags not found tree_id=" + tree_id);
            }
            return tags;
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    public ArrayList<Tree_Tag> findByTag_Id(int tag_id) throws SQLException {//태그별로 검색 기능 마련할때 사용.
        String sql="select * from tree_tag where tag_id=? ";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<Tree_Tag> tags = new ArrayList<>();
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, tag_id);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                tags.add(new Tree_Tag(rs.getLong("tree_id"), rs.getInt("tag_id")));
            }
            if(tags.size()==0){
                throw new NoSuchElementException("treetags not found tree_id=" + tag_id);
            }
            return tags;
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

//    public void update(Long tree_Id, int tag_id, String updateContext) throws SQLException {//바꾸자 하는 태그 내용. primary key가 tree_id, tag_id 두개로 판단한다.
//        //태그가 3개였다가 2개로 다시 정정하면, update기능을 못이용한다. tree_tag에서 해당 tree_id를 싹 다 삭제하고, 새롭게 만드는게 맞다.
//        String sql="update tag set =? where tag_id=?";
//
//
//        Connection con = null;
//        PreparedStatement pstmt = null;
//        try {
//            con = getConnection();
//            pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, updateContext);
//            pstmt.setInt(2,tag_id);
//            pstmt.executeUpdate();
//        } catch (SQLException e) {
//            throw e;
//        } finally {
//            close(con, pstmt, null);
//        }
//
//    }


    public void delete(Long tree_id,int tag_id) throws SQLException {
        String sql = "delete from tree_tag where tree_id=? and tag_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, tree_id);
            pstmt.setLong(2, tag_id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, null);
        }

    }

    public void deleteByTreeId(Long tree_id) throws SQLException {//해당 tree_id를 가진 tree_Tag를 싹 다 제거.
        String sql = "delete from tree_tag where tree_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, tree_id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, null);
        }

    }

    private Connection getConnection() throws SQLException {//connection 객체 반환.

        Connection con = DataSourceUtils.getConnection(dataSource);
        return con;
    }

    private void close(Connection con, Statement stmt, ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);

        DataSourceUtils.releaseConnection(con, dataSource);
    }
}
