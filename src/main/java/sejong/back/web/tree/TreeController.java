package sejong.back.web.tree;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.back.domain.member.AddMemberForm;
import sejong.back.domain.member.Member;
import sejong.back.domain.member.UpdateMemberForm;
import sejong.back.domain.service.LoginService;
import sejong.back.domain.service.TreeService;
import sejong.back.domain.tree.AddTreeForm;
import sejong.back.domain.tree.Tree;
import sejong.back.domain.tree.UpdateTreeForm;
import sejong.back.web.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/forest")//게시글 정보들 보는 페이지.
@RequiredArgsConstructor
public class TreeController {


    private final LoginService loginService;
    private final TreeService treeService;


    @ModelAttribute("tagGroups")
    public List<String> tags(){
        List<String> tags = new ArrayList<>();
        tags.add("#스터디");
        tags.add("#팀플");
        tags.add("#친목");

        return tags;
    }


    @GetMapping//tree 전체 찾기.
    public String forest(HttpServletRequest request, Model model) {//멤버 검색 페이지이다. 여기서 자기 정보 수정 버튼 누르면 이동할 수 있도록 자기의 멤버도 model로 보내자.

        HttpSession session = request.getSession(false);
        Long memberKey = (Long) session.getAttribute(SessionConst.DB_KEY);
        List<Tree> trees = treeService.findAllExcludeMe(memberKey);
        log.info("forest={}", trees);

        model.addAttribute("forest", trees);
        return "forest/forest";//게시글 전체 보여주는 페이지.
    }

    @GetMapping("/{treeKey}")//트리 아이디로 게시글 검색.
    public String searchTree(@PathVariable Long treeKey, Model model) {

        Tree tree = treeService.findByTreeId(treeKey);
        model.addAttribute("tree", tree);
        return "forest/tree"; //그 멤버의 페이지를 보여줘야 한다.
    }

    @GetMapping("/myPage")//자기 트리들 보여주는 페이지.
    public String myTrees(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession(false);
        long myKey = (Long) session.getAttribute(SessionConst.DB_KEY);

        List<Tree> trees=treeService.findMyTrees(myKey);
        model.addAttribute("myTrees", trees);
        return "forest/myTrees"; //그 멤버의 페이지를 보여줘야 한다.
    }

    @GetMapping("/add")
    public String addForm(@ModelAttribute("addTreeForm") AddTreeForm addTreeForm) {
        return "forest/addTreeForm";
    }

    @PostMapping("/add")
    /**
     * 학사시스템을 바탕으로 이름, 학번 등 가져올 수 있는 정보는 가져와서 validatemember에 저장되어있다.
     * 그리므로 여기에 회원가입 폼에서 사용자가 추가해야할 요소들인
     * 1.신입 재학, 휴학 여부를 선택하고
     * 2.태그들을 가지고 싶은거 선택하게 해야한다.
     *
     * 이것을 받아서 학사 시스템으로 로그인 잘 되서 검증된 멤버인 validatemember에 담고 내용을 추가해서 저장한다. db에
     */
    public String save(@Validated @ModelAttribute AddTreeForm addTreeForm, BindingResult result,HttpServletRequest request
    ,Model model) throws IOException {
        if (result.hasErrors()) {
            return "forest/addTreeForm";
        }

        HttpSession session = request.getSession(false);
        Long memberKey = (Long)session.getAttribute(SessionConst.DB_KEY);
        Tree tree = new Tree(memberKey, addTreeForm.getTitle(), addTreeForm.getDescription(), addTreeForm.getTags());

        Tree savedTree = treeService.save(tree);
        model.addAttribute("tree", savedTree);
        return "forest/tree";
    }

    @GetMapping("/{treeKey}/edit")//자시 자신만 수정 할 수 있도록 해야한다. 다른애 꺼 수정 못하게 해야 한다.
    public String editForm(@PathVariable Long treeKey, Model model,HttpServletRequest request) {//세션에 있는 db key를 보고, 자시 key일때만 자기 페이지 수정을 할 수있도록, 다른 사용자 정보 수정 시도시, 내정보 수정으로 redirect시.

        HttpSession session=request.getSession(false);//있는 세션을 가져온다.
        Long sessionDbKey = (Long) session.getAttribute(SessionConst.DB_KEY);

        Tree tree = treeService.findByTreeId(treeKey);//트리 키로 찾아서 이 트리에 있는 dbkey가 자신 세션에 있는거면 편집가능하다.*********
        Long myDbKey = tree.getMemberKey();

        if(sessionDbKey==myDbKey){//자기가 자신의 edit url을 요청한 경우.
            model.addAttribute("tree", tree);
            return "forest/editForm";
        }

        else{//남의 페이지 시도한 경우. 자기 멤버 상세를 보여주도록 하자.
            return "redirect:/forest/{treeKey}";
        }

    }

    @PostMapping("/{treeKey}/edit")//
    public String edit(@PathVariable Long treeKey, @Validated @ModelAttribute("updateTreeForm") UpdateTreeForm form, BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "forest/editForm";
        }

        Tree tree = treeService.findByTreeId(treeKey);
        treeService.update(treeKey, form);

        /**
         * Todo 객체를 찾아서 똒같은걸 하나 new해서 updatemember을 만들어서 memory.update시키냐, 아님 간단하게 하냐 고민.
         */

        return "redirect:/forest/{treeKey}";
    }



}