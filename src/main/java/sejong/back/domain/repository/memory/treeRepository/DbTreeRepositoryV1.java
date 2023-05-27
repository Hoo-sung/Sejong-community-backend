package sejong.back.domain.repository.memory.treeRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;
import sejong.back.domain.repository.TreeRepository;
import sejong.back.domain.tree.AddTreeForm;
import sejong.back.domain.tree.Tree;
import sejong.back.domain.tree.TreeSearchCond;
import sejong.back.domain.tree.UpdateTreeForm;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Slf4j
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
    public List<Tree> findAll() throws SQLException {

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
    public List<Tree> findAll(TreeSearchCond cond) {
        ArrayList<String> cond_sql = new ArrayList<>();
        Integer page;

        ArrayList<Tree> trees = new ArrayList<>(); //보내는 trees 배열
        ArrayList<Long> treeIds = new ArrayList<>(); //저장되는 treeId
        Map<String,String> dataRange =new HashMap<>();//다른사람이 이 객체에 대해 볼 수 있는 정보. 즉, 이 tree만든 맴버가 member 설정에서 공개한 정보.
        List<Integer> tags; //테그 배열 temp

        if(cond.getName()!=null) {
            cond_sql.add("nickname like '%" + cond.getName()+"%'");
        }
        if(cond.getTitle()!=null) {
            cond_sql.add("REPLACE(title, ' ','') like '%" + cond.getTitle()+"%'");
        }
        if(cond.getDescription()!=null) {
            cond_sql.add("REPLACE(description, ' ','') like '%" + cond.getDescription()+"%'");
        }
        if(cond.getTitDesc()!=null){ //title description 동시 검색
            cond_sql.add("REPLACE(title, ' ','') like '%" + cond.getTitDesc()+"%'" +" or " +
                    "REPLACE(description, ' ','') like '%" + cond.getTitDesc()+"%'");
        }
        if(cond.getTag()!=null) {
            cond_sql.add("tree_tag.tag_id = " + cond.getTag());
        }

        //page 처리
        if(cond.getPage()==null)
            page=1;
        else
            page= Integer.valueOf(cond.getPage());


        String sql="SELECT tree.member_id,tree.tree_id, nickname, studentid, department, " +
                "title, description, " + //보내줄때 사용할 값
                "created_at, updated_at, requestId, requestDepartment,tree_tag.tag_id, " +
                "OPENSTUDENTID, OPENDEPARTMENT\n" +
                "FROM tree\n" +
                "JOIN member ON tree.member_id = member.member_id\n" +
                "JOIN tree_tag ON tree_tag.tree_id = tree.tree_id\n" +
                "JOIN tag ON tag.tag_id = tree_tag.tag_id ";

        if(cond_sql.size()>0){ //조건이 있는 경우에만
            String condition = String.join(" and ", cond_sql); //검색하는 sql들을 and로 묶고
            sql = sql + " where " + condition; //sql에 where 조건부 추가
        }

        sql= sql + " order by tree.tree_id desc" //이건 걍 혹시 몰라서 해둠
                + " LIMIT ? OFFSET ?"; //페이지 부분 처리
        log.info("SQl is = {}",sql);


        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;



        int last;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, 5);
            pstmt.setLong(2, 5*(page-1));
            /**
             * 5를 20으로 바꾸기
             */
            rs = pstmt.executeQuery();

            while (rs.next()) { //구문이 끝나면


                Tree tree = new Tree(rs.getLong("member_id"), rs.getString("title"), rs.getString("description"),
                        rs.getBoolean("requestId"), rs.getBoolean("requestDepartment"), rs.getTimestamp("created_at"), rs.getTimestamp("updated_at"));



                //treeId 처리
                tree.setTreeKey( rs.getLong("tree_id"));

                //tag 처리
                tree.setTags(new ArrayList<>(Arrays.asList(rs.getInt("tag_id"))));

                //공개범위 처리

                //nickname
                dataRange.put("nickname", rs.getString("nickname"));

                //학번
                if(rs.getInt("OPENSTUDENTID")==1)
                    dataRange.put("studentId", String.valueOf(rs.getLong("studentId")));

                //학과
                if(rs.getInt("OPENDEPARTMENT")==1)
                    dataRange.put("department", (rs.getString("department")));

                tree.setDataRange(dataRange);
                trees.add(tree);
                //게시글 당 태그 1개 로직 끝

//여기서부터는 게시글 당 태그 2개 이상 로직임
//                long treeId = rs.getLong("tree_id");
//                tree.setTreeKey(treeId); //PK인지라 검색에 필요해서 어쩔 수 없이 사용할듯
//
//                if (treeIds.contains(treeId)){ //중복되는 트리에 테그만 추가하는 경우임
//                    Optional<Tree> treeFound = trees.stream().filter(tree1 ->
//                            tree1.getTreeKey().equals(treeId)).findFirst();
//                    tags = treeFound.get().getTags();
//                    tags.add(rs.getInt("tag_id"));
//                    tree.setTags(tags);
//                } else{ //처음 추가하는 트리 + 그 전까지 트리에 대한 테그 검사를 먼저 하고
//                    //tree tag 검색 시 로직 수행
//                    if(!treeIds.isEmpty()&&cond.getTag()!=null){
//                        //treeId 안 비어있고 tag 검색 한 경우
//                        last = trees.size() - 1;
//                        if(!(trees.get(last).getTags().contains(Integer.valueOf(cond.getTag())))){
//                            //테그가 포함 안되어 있으면 제거
//                            trees.remove(last);
//                    }
//                    }
//
//                    if(trees.size()>2) //보낼 데이터 개수 20개
//                        break;
//                    /**
//                     * 2개인거 20개로 바꾸기
//                     */
//
//                    //새로운 트리 추가
//                    treeIds.add(treeId);
//                    tags = new ArrayList<>();
//                    tags.add(rs.getInt("tag_id"));
//                    tree.setTags(tags);
//                    trees.add(tree);
//                    log.info("Adding tree = {}", tree.getTreeKey());
//                }


            }
            if(trees.size()==0){
                return null;
            }


            return trees;
        } catch (SQLException e) {
            log.error("exception = {}", e.getMessage());
            // 이 부분 exception으로 바꾸면 도메인 왕창 수정되길래 잠시 보류
            return null;
        } finally {
            close(con, pstmt, rs);
        }
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

    @Override
    public Long SavedNumTree() throws SQLException {

        String sql="select COUNT(*) FROM tree";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Long number=0L;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                number=rs.getLong(1);
            } else {
                return  0L;
            }
            return number;
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, rs);
        }

    }

    @Override
    public Tree findByTuple(Long index) throws SQLException {//index번째 tuple tree반환하기.

        String sql = "select * from tree LIMIT ? OFFSET ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Tree tree = new Tree();
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, 1);
            pstmt.setLong(2,index);
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
