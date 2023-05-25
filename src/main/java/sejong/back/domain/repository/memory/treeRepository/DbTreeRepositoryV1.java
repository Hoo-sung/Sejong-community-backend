package sejong.back.domain.repository.memory.treeRepository;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;
import sejong.back.domain.repository.TreeRepository;
import sejong.back.domain.tree.AddTreeForm;
import sejong.back.domain.tree.Tree;
import sejong.back.domain.tree.TreeSearchCond;
import sejong.back.domain.tree.UpdateTreeForm;
import sejong.back.domain.tree_tag.Tree_Tag;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Repository
public class DbTreeRepositoryV1 implements TreeRepository {


    private final DataSource dataSource;

    public DbTreeRepositoryV1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Tree save(Long memberKey,AddTreeForm form) throws SQLException {
        /**
         * tree save부분은 datarange를 반환안한다. null이다. datarange는 공개 범위 배열로, front에서 요청할때 반환하는 것
         */
        String sql="insert into tree(member_id,title,description,requestId, requestDepartment) values(?,?,?,?,?)";

        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs = null;
        Tree tree=new Tree();
        try{
            con=getConnection();
            pstmt=con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);//auto increment로 생성된 키.
            pstmt.setLong(1, memberKey);
            pstmt.setString(2, form.getTitle());
            pstmt.setString(3, form.getDescription());
            pstmt.setBoolean(4, form.isRequestId());
            pstmt.setBoolean(5, form.isRequestDepartment());

            Timestamp now = new Timestamp(System.currentTimeMillis());//현재 시간.

            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                Long id = rs.getLong(1);

                tree.setTreeKey(id);
                tree.setMemberKey(memberKey);
                tree.setTitle(form.getTitle());
                tree.setDescription(form.getDescription());
                tree.setCreated_at(now);
                tree.setUpdated_at(now);
                tree.setRequestId(form.isRequestId());
                tree.setRequestDepartment(form.isRequestDepartment());

            }
            return tree;
        } catch (SQLException e) {
            throw e;
        }

        finally{
            close(con,pstmt,null);
        }
    }

    @Override
    public Tree findByTreeId(Long treeId) throws SQLException {//front에 보낼때 사용. DataRange 공개 범위.

        String sql = "select * from tree where tree_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Tree tree = new Tree();
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, treeId);
            rs = pstmt.executeQuery();
            if (rs.next()) {

                tree.setTreeKey(rs.getLong("tree_id"));
                tree.setMemberKey(rs.getLong("member_id"));//보안상 이부분 안보내줘도 된긴함.
                tree.setTitle(rs.getString("title"));
                tree.setDescription(rs.getString("description"));
                tree.setCreated_at(rs.getTimestamp("created_at"));
                tree.setUpdated_at(rs.getTimestamp("updated_at"));
                tree.setRequestId(rs.getBoolean("requestId"));
                tree.setRequestDepartment(rs.getBoolean("requestDepartment"));

                /**
                 * tree를 생성한 member id바탕으로 공개 허용정보를 담아서 dataRange에 담아서 반환.
                 */


            } else {
                return  null;
            }
            return tree;
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    @Override
    public List<Tree> findAllExcludeMe(Long memberKey) {
        return null;
    }

    @Override
    public List<Tree> findAll() throws SQLException {//이것만 하면 됨.

        String sql="select * from tree";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<Tree> trees = new ArrayList<>();
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                trees.add(new Tree(rs.getLong("tree_id"),rs.getLong("member_id"), rs.getString("title"), rs.getString("description"),
                        rs.getBoolean("requestId"), rs.getBoolean("requestDepartment"), rs.getTimestamp("created_at"), rs.getTimestamp("updated_at")));
                //여기서 tree_key는 프론트에서 사용을 못한다. null값이다.
            }

            return trees;
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    @Override
    public List<Tree> findAll(TreeSearchCond cond) throws SQLException {

            return findAll().stream()
                    .filter(tree->{
                        if (cond.getTitle() != null) {
                            return tree.getTitle().contains(cond.getTitle().strip());
                        }
                        return true;
                    }) //title
                    .filter(tree->{
                        if (cond.getDescription() != null) {
                            return tree.getDescription().contains(cond.getDescription().strip());
                        }
                        return true;
                    }) //description
                    .filter(tree->{
                        Integer tag;
                        if (cond.getTag() != null) {
                            tag = Integer.valueOf(cond.getTag());
                            return tree.getTags().get(0).equals(tag); //일단 테그 한개
                        }
                        return true;
                    })//tag
                    .filter(tree->{
                        Integer page;
                        if (cond.getPage() != null) {
                            page = Integer.valueOf(cond.getPage());

                        }
                        else{
                            page=1;
                        }
                        return tree.getTreeKey()>20*(page-1)&& tree.getTreeKey()<=20*page;
                    })//page

                    .collect(Collectors.toList());

    }//이거는 정민 branch

    @Override
    public List<Tree> findMyTrees(Long myMemberKey) throws SQLException {

        String sql="select * from tree where member_id=? ";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<Tree> trees = new ArrayList<>();
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, myMemberKey);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                trees.add(new Tree(rs.getLong("tree_id"),rs.getLong("member_id"), rs.getString("title"), rs.getString("description"),
                        rs.getBoolean("requestId"), rs.getBoolean("requestDepartment"), rs.getTimestamp("created_at"), rs.getTimestamp("updated_at")));
                //여기서 tree_key는 프론트에서 사용을 못한다. null값이다.
            }
            if(trees.size()==0){
                return null;
            }
            return trees;
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, rs);
        }


    }

    @Override
    public void update(Long treeKey, UpdateTreeForm form) throws SQLException {

        String sql = "update tree set title=?, description=?, requestId=?,requestDepartment=? where tree_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, form.getTitle());
            pstmt.setString(2, form.getDescription());
            pstmt.setBoolean(3, form.isRequestId());
            pstmt.setBoolean(4, form.isRequestDepartment());
            pstmt.setLong(5, treeKey);
            pstmt.executeUpdate();
        } catch (SQLException e){
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    @Override
    public void delete(Long treeKey) throws SQLException {

        String sql = "delete from tree where tree_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, treeKey);
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
