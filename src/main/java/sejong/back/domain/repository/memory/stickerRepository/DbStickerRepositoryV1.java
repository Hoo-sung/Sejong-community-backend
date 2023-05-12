package sejong.back.domain.repository.memory.stickerRepository;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;
import sejong.back.domain.repository.StickerRepository;
import sejong.back.domain.sticker.AddStickerForm;
import sejong.back.domain.sticker.Sticker;
import sejong.back.domain.sticker.StickerSearchCond;
import sejong.back.domain.sticker.UpdateStickerForm;
import sejong.back.domain.tree.Tree;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
public class DbStickerRepositoryV1 implements StickerRepository {

    private  final DataSource dataSource;

    public DbStickerRepositoryV1(DataSource dataSource) {
        this.dataSource = dataSource;
    }




    @Override
    public Sticker save(Long fromMemberId,Long toMemberId, Long tree_id,String writer, AddStickerForm form) throws SQLException {//애는 timestamp안가짐.

        String sql = "insert into sticker(frommember_id,tomember_id,tree_id,title, message,writer,colortype) values(?,?,?,?,?,?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Sticker sticker = new Sticker(fromMemberId, toMemberId, tree_id, form.getTitle(), form.getMessage(),writer,form.getType());
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);//auto increment로 생성된 키.
            pstmt.setLong(1, fromMemberId);
            pstmt.setLong(2, toMemberId);
            pstmt.setLong(3, tree_id);
            pstmt.setString(4, form.getTitle());
            pstmt.setString(5, form.getMessage());
            pstmt.setString(6, writer);
            pstmt.setInt(7,form.getType());

            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                Long id = rs.getLong(1);
                sticker.setStickerKey(id);
                sticker.setWriter(writer);
            }

            return sticker;
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    @Override
    public List<Sticker> findByTreeId(Long treeKey) throws SQLException {
        String sql = "select * from sticker where tree_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Sticker> stickers = new ArrayList<>();

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, treeKey);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                stickers.add(new Sticker(rs.getLong("sticker_id"), rs.getLong("frommember_id"),
                        rs.getLong("tomember_id"), rs.getLong("tree_id"), rs.getString("title"),
                        rs.getString("message"),rs.getString("writer"),rs.getInt("colorType") ,rs.getTimestamp("created_at"), rs.getTimestamp("updated_at")
                ));
                //여기서 tree_key는 프론트에서 사용을 못한다. null값이다.
            }
            if(stickers.size()==0){
                throw new NoSuchElementException("게시글이 아무것도 없다.");
            }

            return stickers;
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    @Override
    public Sticker findByStickerId(Long stickerKey) throws SQLException {
        String sql = "select * from sticker where sticker_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Sticker sticker = new Sticker();
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, stickerKey);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                sticker.setStickerKey(stickerKey);
                sticker.setFromMemberKey(rs.getLong("frommember_id"));
                sticker.setToMemberKey(rs.getLong("tomember_id"));
                sticker.setTreeKey(rs.getLong("tree_id"));
                sticker.setTitle(rs.getString("title"));
                sticker.setMessage(rs.getString("message"));
                sticker.setWriter(rs.getString("writer"));
                sticker.setType(rs.getInt("colortype"));
                sticker.setCreated_at(rs.getTimestamp("created_at"));
                sticker.setUpdated_at(rs.getTimestamp("updated_at"));


            } else {
                throw new NoSuchElementException("sticker not found treeId=" +
                        stickerKey);
            }
            return sticker;
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    @Override
    public List<Sticker> findByMemberId(Long memberKey) throws SQLException {//즉, 한 사람이 붙인 스티커들 볼 수 있다.
        String sql = "select * from sticker where frommember_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Sticker> stickers = new ArrayList<>();

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, memberKey);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                stickers.add(new Sticker(rs.getLong("sticker_id"), rs.getLong("frommember_id"),
                        rs.getLong("tomember_id"), rs.getLong("tree_id"), rs.getString("title"),
                        rs.getString("message"), rs.getString("writer"),rs.getInt("colortype"),
                        rs.getTimestamp("created_at"), rs.getTimestamp("updated_at")));
                //여기서 tree_key는 프론트에서 사용을 못한다. null값이다.
            }
            if(stickers.size()==0){
                throw new NoSuchElementException("게시글이 아무것도 없다.");
            }

            return stickers;
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    @Override
    public List<Sticker> findAll() {
        return null;
    }

    @Override
    public List<Sticker> search(Long treeKey, StickerSearchCond cond) {
        return null;
    }

    @Override
    public void update(Long stickerKey, UpdateStickerForm form) throws SQLException {

        String sql = "update sticker set title=?, message=?, colortype=?  where sticker_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, form.getTitle());
            pstmt.setString(2, form.getMessage());
            pstmt.setInt(3, form.getType());
            pstmt.setLong(4, stickerKey);
            pstmt.executeUpdate();
        } catch (SQLException e){
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    @Override
    public void delete(Long stickerKey) throws SQLException {

        String sql = "delete from sticker where sticker_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, stickerKey);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    @Override
    public Sticker findByMemberKeyAndTreeKey(Long memberKey, Long treeKey){
            return null;
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
