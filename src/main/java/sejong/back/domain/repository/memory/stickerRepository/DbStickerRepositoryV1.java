package sejong.back.domain.repository.memory.stickerRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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
    public void save(Long fromMemberId,Long toMemberId, Long tree_id, AddStickerForm form){//애는 timestamp안가짐.

        String sql = "insert into sticker(frommember_id,tomember_id,tree_id,title, message,colortype) values(?,?,?,?,?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;
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

        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Failed to save sticker(Data integrity violation",e);
        } finally {
            close(con, pstmt, null);
        }
    }

    @Override
    public List<FrontSticker> findByTreeId(Long treeKey) {
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
                stickers.add(new FrontSticker(rs.getString("title"), rs.getLong("frommember_id"),rs.getInt("colortype"),rs.getLong("sticker_id")));
                //여기서 tree_key는 프론트에서 사용을 못한다. null값이다.
            }
            if(stickers.size()==0){
                return null;
            }

            return stickers;
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Failed to findTreeId sticker(Data integrity violation",e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    @Override
    public List<BackSticker> findByTreeId_back(Long treeKey) {
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
                stickers.add(new BackSticker(rs.getLong("frommember_id"),rs.getLong("tree_id"),rs.getString("title"),rs.getInt("colortype"),
                        rs.getString("message"), rs.getTimestamp("created_at"), rs.getTimestamp("updated_at")));
                //여기서 tree_key는 프론트에서 사용을 못한다. null값이다.
            }
            if(stickers.size()==0){
                return null;
            }

            return stickers;
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Failed to findTreeId_back sticker(Data integrity violation",e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    @Override
    public List<BackSticker> findByMemberId(Long memberKey) {//즉, 한 사람이 붙인 스티커들 볼 수 있다.
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
                stickers.add(new BackSticker(rs.getLong("frommember_id"),rs.getLong("tree_id"),rs.getString("title"),rs.getInt("colortype"),
                        rs.getString("message"), rs.getTimestamp("created_at"), rs.getTimestamp("updated_at")));
                //여기서 tree_key는 프론트에서 사용을 못한다. null값이다.


            }
            if(stickers.size()==0){
                return null;//스티커가 아무것도 없으면 null 반환.
            }

            return stickers;
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Failed to findMemberID sticker(Data integrity violation",e);
        } finally {
            close(con, pstmt, rs);
        }
    }


    @Override
    public FrontSticker findByStickerIdFront(Long stickerKey) {
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
                frontSticker.setType(rs.getInt("colortype"));
                frontSticker.setStickerKey(rs.getLong("sticker_id"));

            } else {
                throw new NoSuchElementException("sticker not found treeId=" +
                        stickerKey);
            }
            return frontSticker;
        } catch (SQLException e) {
            throw new EmptyResultDataAccessException("Failed to findStickerIdFront sticker",1,e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    @Override
    public BackSticker findByStickerIdBack(Long stickerKey) {
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
                backSticker.setType(rs.getInt("colortype"));
                backSticker.setCreated_at(rs.getTimestamp("created_at"));
                backSticker.setUpdated_at(rs.getTimestamp("updated_at"));
            } else {
                throw new NoSuchElementException("sticker not found treeId=" +
                        stickerKey);
            }
            return backSticker;
        } catch (SQLException e) {
            throw new EmptyResultDataAccessException("Failed to findStickerIDBack sticker(Data integrity violation",1,e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    @Override
    public Sticker findByStickerId(Long stickerKey)  {
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
                sticker.setType(rs.getInt("colortype"));
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
            throw new EmptyResultDataAccessException("Failed to findStickerId sticker",1,e);
        } finally {
            close(con, pstmt, rs);
        }
    }


    @Override
    public List<Sticker> findAll() throws SQLException {
        String sql="select * from sticker";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<Sticker> stickers = new ArrayList<>();
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Sticker sticker = new Sticker(rs.getLong("FROMMEMBER_ID"), rs.getLong("TOMEMBER_ID"), rs.getLong("TREE_ID"),
                        rs.getString("title"), rs.getString("message"), rs.getInt("colortype"));
                sticker.setStickerKey(rs.getLong("Sticker_ID"));
                sticker.setCreated_at(rs.getTimestamp("CREATED_AT"));
                sticker.setUpdated_at(rs.getTimestamp("UPDATED_AT"));

                stickers.add(sticker);
                //여기서 tree_key는 프론트에서 사용을 못한다. null값이다.


            }
            if(stickers.size()==0){
                /**
                 * exception 터뜨리는게 맞나
                 */
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
    public List<Sticker> findAll(StickerSearchCond cond) throws SQLException {
        String sql="select * from sticker";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<Sticker> stickers = new ArrayList<>();
        ArrayList<String> cond_sql = new ArrayList<>();

        if(cond.getTitle()!=null)
            cond_sql.add("REPLACE(title, ' ') like '%" + cond.getTitle()+"%'");

        if(cond.getMessage()!=null)
            cond_sql.add("REPLACE(message, ' ') like '%" + cond.getMessage()+"%'");

        if (cond.getFromMemberKey() != null)
            cond_sql.add("fromMember_ID like " + cond.getFromMemberKey());

        if (cond.getTreeKey() != null)
            cond_sql.add("tree_id like " + cond.getTreeKey());


        if(cond_sql.size()>0){ //조건이 있는 경우에만
            String condition = String.join(" and ", cond_sql); //검색하는 sql들을 and로 묶고
            sql = sql + " where " + condition; //sql에 where 조건부 추가
        }
        System.out.println("cond_sql = " + sql);
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Sticker sticker = new Sticker(rs.getLong("FROMMEMBER_ID"), rs.getLong("TOMEMBER_ID"), rs.getLong("TREE_ID"),
                        rs.getString("title"), rs.getString("message"), rs.getInt("colortype"));
                sticker.setStickerKey(rs.getLong("Sticker_ID"));
                sticker.setCreated_at(rs.getTimestamp("CREATED_AT"));
                sticker.setUpdated_at(rs.getTimestamp("UPDATED_AT"));

                stickers.add(sticker);
                //여기서 tree_key는 프론트에서 사용을 못한다. null값이다.


            }
            if(stickers.size()==0){
                return null;
            }

            return stickers;
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    @Override
    public List<Sticker> search(Long treeKey, StickerSearchCond cond) {
        return null;
    }

    @Override
    public void update(Long stickerKey, UpdateStickerForm form) {

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
            throw new DataIntegrityViolationException("Failed to update sticker(Data integrity violation",e);
        } finally {
            close(con, pstmt, null);
        }
    }

    @Override
    public void delete(Long stickerKey)  {

        String sql = "delete from sticker where sticker_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, stickerKey);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Failed to delete sticker(Data integrity violation",e);
        } finally {
            close(con, pstmt, null);
        }
    }

    @Override
    public Boolean findByMemberKeyAndTreeKey(Long memberKey, Long treeKey){

        String sql = "select * from sticker where  frommember_id=? and tree_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, memberKey);
            pstmt.setLong(2, treeKey);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new EmptyResultDataAccessException("Failed to findMemTreeKey sticker(Data integrity violation",1,e);
        } finally {
            close(con, pstmt, rs);
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
