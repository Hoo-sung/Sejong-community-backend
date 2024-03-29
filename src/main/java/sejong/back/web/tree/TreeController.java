
package sejong.back.web.tree;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.back.domain.service.LoginService;
import sejong.back.domain.service.NoticeService;
import sejong.back.domain.service.StickerService;
import sejong.back.domain.service.TreeService;
import sejong.back.domain.sticker.FrontSticker;
import sejong.back.domain.tree.AddTreeForm;
import sejong.back.domain.tree.Tree;
import sejong.back.domain.tree.TreeSearchCond;
import sejong.back.domain.tree.UpdateTreeForm;
import sejong.back.exception.UnSupportedDeleteTreeException;
import sejong.back.exception.UnSupportedUpdateTreeException;
import sejong.back.web.ResponseResult;
import sejong.back.web.argumentresolver.Login;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/forest")//게시글 정보들 보는 페이지.
@RequiredArgsConstructor
public class TreeController {

    private final TreeService treeService;
    private final StickerService stickerService;
    private final NoticeService noticeService;



    @GetMapping//tree 전체 찾기.
    public ResponseResult<?> forest(HttpServletRequest request){//멤버 검색 페이지이다. 클라이언트 요청의 경우의 수는 2가지
        /***
         * 1. query parameter 있는 경우
         * 필터링 해서 보내줄 것
         * 2. query parameter 없는 경우
         * 최신 데이터 20개 보내줄 것
         */
        String name = request.getParameter("nickname");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String titdesc = request.getParameter("titdesc"); //title description 동시 검색
        String tag = request.getParameter("tag");
        String page = request.getParameter("page"); //받아올때는 int로 불가능함 filtering logic에서 변환해서 사용


        TreeSearchCond treeSearchCond = new TreeSearchCond(name,title, description, titdesc,tag, page);
        log.info("search getName = {}", treeSearchCond.getName());
        log.info("search getDescription = {}", treeSearchCond.getDescription());
        log.info("search titDesc = {}", treeSearchCond.getTitDesc());
        log.info("search getTitle = {}", treeSearchCond.getTitle());
        log.info("search getTag = {}", treeSearchCond.getTag());
        log.info("search getPage = {}", treeSearchCond.getPage());

        List<Tree> trees = treeService.findAll(treeSearchCond);
        log.info("forest={}", trees);

        if(trees==null)
            return new ResponseResult<>("트리가 1도 없음.", Collections.emptyList());
        return new ResponseResult<>("모든 트리 조회 성공", trees);
    }

    @GetMapping("/{treeKey}")//개별 트리 보여주는 페이지.
    public TreeAndStickers searchTree(@Login Long myKey, @PathVariable Long treeKey) {

        Tree tree = treeService.findByTreeId(treeKey);
        List<FrontSticker> stickers = stickerService.findByTreeId(treeKey);
        if(stickers == null) stickers = Collections.emptyList(); //트리에 붙어있는 스티커가 없으면 빈 비열 반환

        if (tree.getMemberKey() == myKey) {//내 트리일 때 -> notice에서 해당 칼럼 삭제
            noticeService.deleteNotice(myKey, treeKey);
            return new TreeAndStickers(tree, stickers, true);
        }
        return new TreeAndStickers(tree, stickers, false);

    }

    @GetMapping("random-tree")//개별 트리 보여주는 페이지.
    public TreeAndStickers randomTree(@Login Long myKey) {


        Long numTree = treeService.SavedNumTree();//db index수.
        Random random = new Random();
        Long randomNumber=random.nextLong(numTree);

        if(numTree == 0L){
            log.error("트리 ID에 해당되는 트리 X");
            throw new NullPointerException("게시물이 아예 없습니다.");
        }

        Tree tree = treeService.findByTuple(randomNumber);
        List<FrontSticker> stickers = stickerService.findByTreeId(randomNumber);
        if(stickers == null) stickers = Collections.emptyList(); //트리에 붙어있는 스티커가 없으면 빈 비열 반환

        if (tree.getMemberKey() == myKey) {//내 트리일 때 -> notice에서 해당 칼럼 삭제
            noticeService.deleteNotice(myKey, tree.getTreeKey());
            return new TreeAndStickers(tree, stickers, true);
        }
        return new TreeAndStickers(tree, stickers, false);

    }

    @PostMapping  //새로운 트리 생성
    public Map<String, String> save(@Login Long myKey,
                                    @Validated @RequestBody AddTreeForm addTreeForm, BindingResult result) throws IOException {

        if (result.hasErrors()) {
            throw new IllegalArgumentException("빈 값이 있음");
        }

        log.info("add Tree ^^ = {}", addTreeForm.getTitle());
        Tree savedTree = treeService.save(myKey,addTreeForm);

        Map<String, String> responseData = new HashMap<>();
        responseData.put("message", "success");
        responseData.put("redirectURL", "tree/" + savedTree.getTreeKey());

        log.info("Add tree Success");
        return responseData;
    }


    @PatchMapping("/{treeKey}")
    public  ResponseResult edit(@Login Long myKey,@PathVariable Long treeKey,
                                @Validated @RequestBody UpdateTreeForm updateTreeForm, BindingResult result){
        if (result.hasErrors()) {
            throw new IllegalArgumentException("오류 있음");
        }
        log.info("트리 수정");
        Tree tree = treeService.findByTreeId(treeKey);

        if(tree.getMemberKey()!=myKey){
                throw new UnSupportedUpdateTreeException("게시글을 수정 할수 있는 권한이 없습니다.!");
        }
        treeService.update(treeKey, updateTreeForm);
        log.info("트리 수정 완료 제목 ={}", treeService.findByTreeId(treeKey).getTitle());

        return new ResponseResult<>("수정 완료");
    }

    @DeleteMapping("/{treeKey}")
    public  ResponseResult delete(@Login Long myKey,@PathVariable Long treeKey) {


        log.info("트리 삭제");
        Tree tree = treeService.findByTreeId(treeKey);
        if(tree.getMemberKey()!=myKey){
                throw new UnSupportedDeleteTreeException("게시글을 삭제 할수 있는 권한이 없습니다.!");
        }
        treeService.delete(treeKey);
        log.info("트리 삭제 완료 제목");

        return new ResponseResult<>("삭제 완료.");
    }

}

