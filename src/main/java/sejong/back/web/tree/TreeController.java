package sejong.back.web.tree;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.back.domain.member.Member;
import sejong.back.domain.repository.MemberRepository;
import sejong.back.domain.repository.memory.tree_tag.DbTree_TagRepository;
import sejong.back.domain.service.*;
import sejong.back.domain.sticker.AddStickerForm;
import sejong.back.domain.sticker.Sticker;
import sejong.back.domain.tree.AddTreeForm;
import sejong.back.domain.tree.Tree;
import sejong.back.domain.tree.UpdateTreeForm;
import sejong.back.domain.tree_tag.Tree_Tag;
import sejong.back.web.ResponseResult;
import sejong.back.web.SessionConst;
import sejong.back.web.argumentresolver.Login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/forest")//게시글 정보들 보는 페이지.
@RequiredArgsConstructor
public class TreeController {

    private final LoginService loginService;
    private final TreeService treeService;

    private final MemberService memberService;

    private final StickerService stickerService;

    private final TreeTagService treeTagService;

    private final TagService tagService;

    private final DbTree_TagRepository dbTreeTagRepository;

//    @ModelAttribute("tagGroups")
//    public List<String> tags() {
//        List<String> tags = new ArrayList<>();
//        tags.add("#스터디");
//        tags.add("#팀플");
//        tags.add("#친목");
//
//        return tags;
//    }

    @GetMapping//tree 전체 찾기.
    public ResponseResult<?> forest() throws SQLException {//멤버 검색 페이지이다. 여기서 자기 정보 수정 버튼 누르면 이동할 수 있도록 자기의 멤버도 model로 보내자.

            List<Tree> trees = treeService.findAll();
            log.info("forest={}", trees);
            return new ResponseResult<>("모든 트리 조회 성공", trees);
    }

    @GetMapping("/{treeKey}")//트리 아이디로 게시글 검색. 게시글 목록에서 열로 리다이렉트 되어서 온다.
    public ResponseResult<?> searchTree(@Login Long myKey, @PathVariable Long treeKey,
                                        Model model, HttpServletRequest request) throws SQLException {

        Tree tree = treeService.findByTreeId(treeKey);
        model.addAttribute("tree", tree);
        if (tree.getMemberKey() == myKey) { //트리 검색 페이지에서 클릭한 트리가 내 트리
            //TODO return까지 임시로 만든 것. 나중에 프론트팀과 응답 스펙 다시 맞추고 구체화
            //TODO 트리 게시판에서 내껄 클릭해서 my-page로 리다이렉트 시킬 때 쿼리 파리미터를 treeid=2 등으로 붙여서 보내준다.
            HashMap<String, String> redirectUri = new HashMap<>();
            redirectUri.put("redirectURI", "/forest/my-trees");
            return new ResponseResult<>("클릭한 트리가 내 트리", redirectUri);
        } else { //트리 검색 페이지에서 클릭한 트리가 내 트리가 아님
            return new ResponseResult<>("클릭한 트리가 내 트리가 아님", tree);
        }
    }

    @GetMapping("/my-trees")//자기 트리들 보여주는 페이지.
    public ResponseResult<?> myTrees(@Login Long myKey, HttpServletRequest request, Model model) throws SQLException {

        List<Tree> trees = treeService.findMyTrees(myKey);
        return new ResponseResult<>("본인 전체 트리 조회 성공", trees);
    }

    @GetMapping("/my-trees/add")
    public String addForm(@ModelAttribute("addTreeForm") AddTreeForm addTreeForm) {
        //TODO 뷰 렌더링
        return "forest/addTreeForm";
    }

    @PostMapping("/my-trees/add")
    public ResponseResult<?> save(@Login Long myKey,
                                  @Validated @ModelAttribute AddTreeForm addTreeForm, BindingResult result,
                                  HttpServletRequest request, Model model) throws IOException, SQLException {

        if (result.hasErrors()) {

            throw new IllegalArgumentException("빈 값이 있음");
        }

        Tree savedTree = treeService.save(myKey,addTreeForm);
        /**
         * TODO 이다. treeservice에서 트리를 생성하면, 애를 반환하되, 트리가 가진 태그를 바탕으로 tree_tag 테이블도 save해야 한다.***
         * 태그는 tree에 저장되지 않는다.!!! 애는 tree_Tag에 저장된다. tree_id랑 tag_id로
         */
        return new ResponseResult<>("새로운 트리 생성 성공", savedTree);
    }

    @GetMapping("/my-trees/{treeKey}/edit")//자시 자신만 수정 할 수 있도록 해야한다. 다른애 꺼 수정 못하게 해야 한다.
    public String editForm(@Login Long myKey,
                           @PathVariable Long treeKey, Model model, HttpServletRequest request) throws SQLException {//세션에 있는 db key를 보고, 자시 key일때만 자기 페이지 수정을 할 수있도록, 다른 사용자 정보 수정 시도시, 내정보 수정으로 redirect시.

        Tree tree = treeService.findByTreeId(treeKey);//트리 키로 찾아서 이 트리에 있는 dbkey가 자신 세션에 있는거면 편집가능하다.*********
        if (myKey != tree.getMemberKey()) {//남의 페이지 시도한 경우. 자기 멤버 상세를 보여주도록 하자.
            return "redirect:/forest/{treeKey}";
        }
        //TODO 뷰 렌더링
        model.addAttribute("tree", tree);
        return "forest/editForm";
    }

    @PostMapping("/my-trees/{treeKey}/edit")//
    public ResponseResult<?> edit(@Login Long myKey, @PathVariable Long treeKey,
                                  @Validated @ModelAttribute("updateTreeForm") UpdateTreeForm form, BindingResult bindingResult) throws IllegalAccessException, SQLException {

        if (bindingResult.hasErrors()) {
            //TODO 예외 처리
            log.info("errors={}", bindingResult);
            throw new IllegalArgumentException("빈 값이 들어있음");
        }

        Tree tree = treeService.findByTreeId(treeKey);
        if (myKey != tree.getMemberKey()) {
            throw new IllegalAccessException("남의 트리에 접근 시도");
        }
        treeService.update(treeKey, form);
        return new ResponseResult<>("트리 정보 수정 성공", tree);
    }

    /**
     * 여기부터 스티커 add/edit만 이 url을 이용한다.
     * forest/add는 게시글 추가 기능.
     * *forest/{treeKey}/add는 스티커추가 기능이다.
     */
    @GetMapping("/{treeKey}/add") //한번 스티커 붙이면 또 못붙이고 자기 스티커 상세로 라디이렉트 시켜야함.
    public String addForm(@Login Long myKey,
                          @ModelAttribute("addStickerForm") AddStickerForm addStickerForm, HttpServletRequest request,
                          @PathVariable Long treeKey, Model model) throws SQLException {

        Sticker sticker = stickerService.findByMemberKeyAndTreeKey(myKey, treeKey);

        if (sticker != null) {//이미 한 사용자가 한 개시물로 보낸 쪽지가 있으면 그것 상세 페이지로 라디이렉트 시켜야함.
            Long stickerKey = sticker.getStickerKey();
            return "redirect:/sticker";
        }
        //TODO 뷰 렌더링
        Tree StickerOnThistree = treeService.findByTreeId(treeKey);
        model.addAttribute("tree", StickerOnThistree);
        return "sticker/addStickerForm";
    }

    @PostMapping("{treeKey}/add")   //한번 스티커 붙이면 또 못붙이고 자기 스티커 상세로 라디이렉트 시켜야함.
    public ResponseResult<?> save(@Login Long fromMemberKey,
                                  @Validated @ModelAttribute AddStickerForm addStickerForm, @PathVariable Long treeKey,
                                  BindingResult result, HttpServletRequest request, Model model) throws IOException, SQLException {

        if (result.hasErrors()) {
            //TODO 예외 처리
            throw new IllegalArgumentException("빈 값이 들어있음");
        }

        Tree tree = treeService.findByTreeId(treeKey);
        Long toMemberKey = tree.getMemberKey();

        Member byKey = memberService.findByKey(fromMemberKey);
        String nickname = byKey.getNickname();

        Sticker savedSticker = stickerService.save(fromMemberKey,toMemberKey,treeKey,nickname, addStickerForm);
        return new ResponseResult<>("스티커 작성 성공", savedSticker);
    }
}
