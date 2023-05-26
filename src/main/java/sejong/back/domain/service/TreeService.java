package sejong.back.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sejong.back.domain.member.Member;
import sejong.back.domain.repository.MemberRepository;
import sejong.back.domain.repository.TreeRepository;
import sejong.back.domain.repository.memory.tagRepository.DbTagRepository;
import sejong.back.domain.repository.memory.tree_tag.DbTree_TagRepository;
import sejong.back.domain.tree.AddTreeForm;
import sejong.back.domain.tree.Tree;
import sejong.back.domain.tree.TreeSearchCond;
import sejong.back.domain.tree.UpdateTreeForm;
import sejong.back.domain.tree_tag.Tree_Tag;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



@Service
@RequiredArgsConstructor
public class TreeService {

    private final TreeRepository treeRepository;

    private final MemberRepository memberRepository;

    private final DbTagRepository dbTagRepository;

    private final DbTree_TagRepository dbTreeTagRepository;

    public Tree save(Long memberKey, AddTreeForm form) throws SQLException {
        Tree saved = treeRepository.save(memberKey, form);//tree저장.

        List<Integer> tags = form.getTags();

        if(tags==null)//태그 없으면 바로 return;
        {
            Member createPerson = memberRepository.findByKey(memberKey);
            settingDataRange(saved, createPerson);
            return saved;
        }

        for (Integer tag : tags) {
            Tree_Tag treeTag = new Tree_Tag(saved.getTreeKey(), dbTagRepository.findByTagId(tag).getTag_Id());
            dbTreeTagRepository.save(treeTag);
        }


        Member createPerson = memberRepository.findByKey(memberKey);
        settingDataRange(saved, createPerson);

        return saved;

    }

    private static void settingDataRange(Tree saved, Member createPerson) {
        String nickname = createPerson.getNickname();
        saved.setDataRange(new HashMap<>());
        saved.getDataRange().put("nickname",nickname);//nickname은 필수적으로 저장한다.
        if(createPerson.isOpenStudentId()==true) {//tree생성한 사람이 자기 학번 공개범위를 open해 놓았냐.
            //그러면, 학번 앞 두개 가져온다.
            String idString = String.valueOf(createPerson.getStudentId()).substring(0, 2);
            saved.getDataRange().put("studentId",idString);
        }

        if(createPerson.isOpenDepartment()==true){

            String department = createPerson.getDepartment();
            saved.getDataRange().put("department", department);
        }
    }


    public Tree findByTreeId(Long treeId) throws SQLException {
        Tree findObject = treeRepository.findByTreeId(treeId);

        if(findObject==null)
            return null;

        findObject.setTreeKey(treeId);//tree id 설정.

        Long memberKey = findObject.getMemberKey();

        Member createPerson = memberRepository.findByKey(memberKey);
        //datarange설정하는 과정. 결국 memberepository를 거쳐야 하는 문제 발생.

        ArrayList<Integer> tags = dbTreeTagRepository.findByTree_Id(findObject.getTreeKey());
        findObject.setTags(tags);//태그 담기.

        settingDataRange(findObject,createPerson);
        return findObject;
    }


    public List<Tree> findAll() throws SQLException {
        List<Tree> all = treeRepository.findAll();

        if(all==null)
            return null;
        for (Tree tree : all) {//tree마다 datarange 설정해야 한다.
            Long memberKey = tree.getMemberKey();
            Member createPerson = memberRepository.findByKey(memberKey);
            settingDataRange(tree, createPerson);

            ArrayList<Integer> tags = dbTreeTagRepository.findByTree_Id(tree.getTreeKey());
            tree.setTags(tags);//태그 담기.

        }
        return all;
    }

    public List<Tree> findMyTrees(Long myDbKey) throws SQLException {//tree중에 mydbKey값을 가진것만 출력하기.

        List<Tree> myTrees = treeRepository.findMyTrees(myDbKey);

        if(myTrees==null)
            return null;
        for (Tree myTree : myTrees) {//트리에 태그 넣기.

            ArrayList<Integer> tags = dbTreeTagRepository.findByTree_Id(myTree.getTreeKey());
            myTree.setTags(tags);//태그 담기.

            myTree.setDataRange(new HashMap<>());
            Member createPerson = memberRepository.findByKey(myDbKey);

            settingDataRange(myTree, createPerson);
        }

        return myTrees;
    }

    public List<Tree> findAll(TreeSearchCond cond) throws SQLException {//이 부분은 안건듬.

        List<Tree> all = treeRepository.findAll(cond);
        if(all==null)
            return null;
        for (Tree tree : all) {//tree마다 datarange 설정해야 한다.
            Long memberKey = tree.getMemberKey();
            Member createPerson = memberRepository.findByKey(memberKey);
            settingDataRange(tree, createPerson);

            ArrayList<Integer> tags = dbTreeTagRepository.findByTree_Id(tree.getTreeKey());
            tree.setTags(tags);//태그 담기.

        }
        return all;
    }

    public void update(Long treeKey,  UpdateTreeForm form) throws SQLException {
        treeRepository.update(treeKey, form);
        dbTreeTagRepository.deleteByTreeId(treeKey);

        List<Integer> tags = form.getTags();
        for (Integer tag : tags) {
            Tree_Tag treeTag = new Tree_Tag(treeKey, dbTagRepository.findByTagId(tag).getTag_Id());
            dbTreeTagRepository.save(treeTag);
        }
    }
    public void delete(Long treeKey) throws SQLException {
        treeRepository.delete(treeKey);
    }
}
