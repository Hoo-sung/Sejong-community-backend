package sejong.back.domain.repository.memory.stickerRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;
import sejong.back.domain.repository.StickerRepository;
import sejong.back.domain.sticker.*;
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
    public void save(Long fromMemberId,Long toMemberId, Long tree_id, AddStickerForm form) throws SQLException {//애는 timestamp안가짐.

        String sql = "insert into sticker(frommember_id,tomember_id,tree_id,title, message,colortype) values(?,?,?,?,?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//        Sticker sticker = new Sticker(fromMemberId, toMemberId, tree_id, form.getTitle(), form.getMessage(),form.getType());
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);//auto increment로 생성된 키.
            pstmt.setLong(1, fromMemberId);
            pstmt.setLong(2, toMemberId);
            pstmt.setLong(3, tree_id);
            pstmt.setString(4, form.getTitle());
            pstmt.setString(5, form.getMessage());
            pstmt.setInt(6,form.getType());

            pstmt.executeUpdate();

//            rs = pstmt.getGeneratedKeys();
//            if (rs.next()) {
//                Long id = rs.getLong(1);
//                sticker.setStickerKey(id);
//            }
//
//            return sticker;
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    @Override
    public List<FrontSticker> findByTreeId(Long treeKey) throws SQLException {
        String sql = "select * from sticker where tree_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<FrontSticker> stickers = new ArrayList<>();

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, treeKey);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                stickers.add(new FrontSticker(rs.getString("title"), rs.getLong("frommember_id")));
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
    public List<BackSticker> findByTreeId_back(Long treeKey) throws SQLException {
        String sql = "select * from sticker where tree_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<BackSticker> stickers = new ArrayList<>();

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, treeKey);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                stickers.add(new BackSticker(rs.getLong("frommember_id"),rs.getLong("tree_id"),rs.getString("title"),
                        rs.getString("message"), rs.getTimestamp("created_at"), rs.getTimestamp("updated_at")));
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
    public List<BackSticker> findByMemberId(Long memberKey) throws SQLException {//즉, 한 사람이 붙인 스티커들 볼 수 있다.
        String sql = "select * from sticker where frommember_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<BackSticker> stickers = new ArrayList<>();

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, memberKey);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                stickers.add(new BackSticker(rs.getLong("frommember_id"),rs.getLong("tree_id"),rs.getString("title"),
                        rs.getString("message"), rs.getTimestamp("created_at"), rs.getTimestamp("updated_at")));
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
    public FrontSticker findByStickerIdFront(Long stickerKey) throws SQLException {
        String sql = "select * from sticker where sticker_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        FrontSticker frontSticker = new FrontSticker();
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, stickerKey);
            rs = pstmt.executeQuery();
            if (rs.next()) {
               frontSticker.setFromMember(rs.getLong("frommember_id"));
                frontSticker.setTitle(rs.getString("title"));

            } else {
                throw new NoSuchElementException("sticker not found treeId=" +
                        stickerKey);
            }
            return frontSticker;
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    @Override
    public BackSticker findByStickerIdBack(Long stickerKey) throws SQLException {
        String sql = "select * from sticker where sticker_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        BackSticker backSticker = new BackSticker();
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, stickerKey);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                backSticker.setFromMember(rs.getLong("frommember_id"));
                backSticker.setTreeKey(rs.getLong("tree_id"));
                backSticker.setTitle(rs.getString("title"));
                backSticker.setMessage(rs.getString("message"));
                backSticker.setCreated_at(rs.getTimestamp("created_at"));
                backSticker.setUpdated_at(rs.getTimestamp("updated_at"));
            } else {
                throw new NoSuchElementException("sticker not found treeId=" +
                        stickerKey);
            }
            return backSticker;
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

    @Override
    public void updateNotice(Long toMemberKey, Long treeId, String title) throws SQLException {
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
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    private void noticeCountUp(Long toMemberKey, Long treeId, Integer count) throws SQLException {
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
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    private void createNotice(Long toMemberKey, Long treeId, String title) throws SQLException {
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
