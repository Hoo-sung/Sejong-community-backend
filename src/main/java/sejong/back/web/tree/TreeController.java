package sejong.back.web.tree;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.back.domain.service.LoginService;
import sejong.back.domain.service.StickerService;
import sejong.back.domain.service.TreeService;
import sejong.back.domain.sticker.AddStickerForm;
import sejong.back.domain.sticker.ShowStickerForm;
import sejong.back.domain.sticker.Sticker;
import sejong.back.domain.tree.AddTreeForm;
import sejong.back.domain.tree.Tree;
import sejong.back.domain.tree.TreeSearchCond;
import sejong.back.domain.tree.UpdateTreeForm;
import sejong.back.web.ResponseResult;
import sejong.back.web.argumentresolver.Login;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/forest")//게시글 정보들 보는 페이지.
@RequiredArgsConstructor
public class TreeController {

    private final LoginService loginService;
    private final TreeService treeService;
    private final StickerService stickerService;

    @ModelAttribute("tagGroups")
    public List<String> tags() {
        List<String> tags = new ArrayList<>();
        tags.add("#스터디");
        tags.add("#팀플");
        tags.add("#친목");

        return tags;
    }

    @GetMapping//tree 전체 찾기.
    public ResponseResult<?> forest(HttpServletRequest request) {//멤버 검색 페이지이다. 클라이언트 요청의 경우의 수는 2가지
        /***
         * 1. query parameter 있는 경우
         * 필터링 해서 보내줄 것
         * 2. query parameter 없는 경우
         * 최신 데이터 20개 보내줄 것
         */
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String tag = request.getParameter("tag");
        String page = request.getParameter("page"); //받아올때는 int로 불가능함 filtering logic에서 변환해서 사용

        TreeSearchCond treeSearchCond = new TreeSearchCond(title, description, tag, page);
        log.info("search condition = {}", treeSearchCond);

        List<Tree> trees = treeService.findAll(treeSearchCond);
        log.info("forest={}", trees);

        return new ResponseResult<>("모든 트리 조회 성공", trees);
    }

    @GetMapping("/{treeKey}")//트리 아이디로 게시글 검색. 게시글 목록에서 열로 리다이렉트 되어서 온다.
    public TreeAndStickers searchTree(@Login Long myKey, @PathVariable Long treeKey) throws NullPointerException {
        Tree tree = treeService.findByTreeId(treeKey);
        if(tree == null){
            log.error("트리 ID에 해당되는 트리 X");
            throw new NullPointerException("트리 ID에 해당되는 트리 X");
        }
        List<Sticker> stickers = stickerService.findByTreeId(treeKey);

        //내 트리에 접근하는 경우와 다른 사람 트리에 접근하는 경우를 나눠서 로직 구현
        if (tree.getMemberKey() == myKey) {
            return new TreeAndStickers(tree, stickers, true);
        }
        else {
            List<ShowStickerForm> otherStickers = new ArrayList<>();
            stickers.stream()
                    .forEach((sticker) -> otherStickers.add(stickerService.register(sticker)));
            return new TreeAndStickers(tree, otherStickers, false);
        }

/*
        if (tree.getMemberKey() == myKey) { //트리 검색 페이지에서 클릭한 트리가 내 트리
            //TODO return까지 임시로 만든 것. 나중에 프론트팀과 응답 스펙 다시 맞추고 구체화
            //TODO 트리 게시판에서 내껄 클릭해서 my-page로 리다이렉트 시킬 때 쿼리 파리미터를 treeid=2 등으로 붙여서 보내준다.
            HashMap<String, String> redirectUri = new HashMap<>();
            redirectUri.put("redirectURI", "/forest/my-trees");
            return new ResponseResult<>("클릭한 트리가 내 트리", redirectUri);
        } else { //트리 검색 페이지에서 클릭한 트리가 내 트리가 아님
            return new ResponseResult<>("클릭한 트리가 내 트리가 아님", tree);
        }*/


    }

    /**
     * tree 수정
     * @param myKey //로그인 값
     * @param treeKey //트리 주소
     * @param updateTreeForm //수정 내용
     * @param result //오류 값 저장
     * @return
     */
    @PatchMapping("/{treeKey}")
    public  ResponseResult edit(@Login Long myKey,@PathVariable Long treeKey,
                                @Validated @RequestBody UpdateTreeForm updateTreeForm, BindingResult result){
        if (result.hasErrors()) {
            throw new IllegalArgumentException("오류 있음");
        }
        log.info("트리 수정");
        Tree tree = treeService.findByTreeId(treeKey);
        if(tree.getMemberKey()!=myKey){
            ResponseResult responseResult = new ResponseResult<>("나의 트리가 아닙니다.");
            responseResult.setErrorCode(-333);
            return responseResult;
        }
        treeService.update(treeKey, updateTreeForm);
        log.info("트리 수정 완료 제목 ={}", treeService.findByTreeId(treeKey).getTitle());

        return new ResponseResult<>("수정 완료");
    }

    @GetMapping("/my-trees")//자기 트리들 보여주는 페이지.
    public ResponseResult<?> myTrees(@Login Long myKey, HttpServletRequest request, Model model) {

        List<Tree> trees = treeService.findMyTrees(myKey);
        return new ResponseResult<>("본인 전체 트리 조회 성공", trees);
    }

    @GetMapping("/my-trees/add")
    public String addForm(@ModelAttribute("addTreeForm") AddTreeForm addTreeForm) {
        //TODO 뷰 렌더링
        return "forest/addTreeForm";
    }

    @PostMapping  //새로운 트리 생성
    public Map<String, String> save(@Login Long myKey,
                                    @Validated @RequestBody AddTreeForm addTreeForm, BindingResult result) throws IOException {

        if (result.hasErrors()) {
            throw new IllegalArgumentException("빈 값이 있음");
        }

        log.info("add Tree ^^ = {}", addTreeForm.getTitle());
        Tree tree = new Tree(myKey, addTreeForm.getTitle(), addTreeForm.getDescription(), addTreeForm.getTags(), addTreeForm.isRequestId(), addTreeForm.isRequestDepartment());
        Tree savedTree = treeService.save(tree);

        Map<String, String> responseData = new HashMap<>();
        responseData.put("message", "success");
        responseData.put("redirectURL", "forest/" + savedTree.getTreeKey());

        log.info("Add tree Success");
        return responseData;
    }


    @GetMapping("/my-trees/{treeKey}/edit")//자시 자신만 수정 할 수 있도록 해야한다. 다른애 꺼 수정 못하게 해야 한다.
    public String editForm(@Login Long myKey,
                           @PathVariable Long treeKey, Model model, HttpServletRequest request) {//세션에 있는 db key를 보고, 자시 key일때만 자기 페이지 수정을 할 수있도록, 다른 사용자 정보 수정 시도시, 내정보 수정으로 redirect시.

        Tree tree = treeService.findByTreeId(treeKey);//트리 키로 찾아서 이 트리에 있는 dbkey가 자신 세션에 있는거면 편집가능하다.*********
        if (myKey != tree.getMemberKey()) {//남의 페이지 시도한 경우. 자기 멤버 상세를 보여주도록 하자.
            return "redirect:/forest/{treeKey}";
        }
        //TODO 뷰 렌더링
        model.addAttribute("tree", tree);
        return "forest/editForm";
    }


    /**
     * 여기부터 스티커 add/edit만 이 url을 이용한다.
     * forest/add는 게시글 추가 기능.
     * *forest/{treeKey}/add는 스티커추가 기능이다.
     */
    @GetMapping("/{treeKey}/add") //한번 스티커 붙이면 또 못붙이고 자기 스티커 상세로 라디이렉트 시켜야함.
    public String addForm(@Login Long myKey,
                          @ModelAttribute("addStickerForm") AddStickerForm addStickerForm, HttpServletRequest request,
                          @PathVariable Long treeKey, Model model) {

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

    @PostConstruct
    public void testEnvironment() {

        Tree tree1 = new Tree(1L," 공부하자","7시에",new ArrayList<>(Arrays.asList(1)),true,false);
        Tree tree2 = new Tree(2L, "팀플하자", "8시에", new ArrayList<>(Arrays.asList(2)), true, false);
        Tree tree3 = new Tree(3L,"카톡하자","9시에",new ArrayList<>(Arrays.asList(3)),true, false);
        Tree tree4 = new Tree(1L,"밥 먹자","11시에",new ArrayList<>(Arrays.asList(4)),false, true);
        Tree tree5 = new Tree(2L,"고민 들어줘","6시에",new ArrayList<>(Arrays.asList(5)),false, false);
        Tree tree6 = new Tree(3L,"취업 어떻게 해요","6시에",new ArrayList<>(Arrays.asList(6)),true, true);

        treeService.save(tree1);
        treeService.save(tree2);
        treeService.save(tree3);
        treeService.save(tree4);
        treeService.save(tree5);
        treeService.save(tree6);
    }

}
