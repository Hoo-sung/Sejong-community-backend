
package sejong.back.web.tree;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.back.domain.repository.NoticeRepository;
import sejong.back.domain.service.LoginService;
import sejong.back.domain.service.NoticeService;
import sejong.back.domain.service.StickerService;
import sejong.back.domain.service.TreeService;
import sejong.back.domain.sticker.AddStickerForm;
import sejong.back.domain.sticker.BackSticker;
import sejong.back.domain.sticker.FrontSticker;
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
import java.sql.SQLException;
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
    private final NoticeService noticeService;



    @GetMapping//tree 전체 찾기.
    public ResponseResult<?> forest(HttpServletRequest request) throws SQLException {//멤버 검색 페이지이다. 클라이언트 요청의 경우의 수는 2가지
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
    public TreeAndStickers searchTree(@Login Long myKey, @PathVariable Long treeKey) throws NullPointerException, SQLException {
        Tree tree = treeService.findByTreeId(treeKey);
        if(tree == null){
            log.error("트리 ID에 해당되는 트리 X");
            throw new NullPointerException("트리 ID에 해당되는 트리 X");
        }

        List<FrontSticker> stickers = stickerService.findByTreeId(treeKey);
        if(stickers == null) stickers = Collections.emptyList(); //트리에 붙어있는 스티커가 없으면 빈 비열 반환

        if (tree.getMemberKey() == myKey) {//내 트리일 때 -> notice에서 해당 칼럼 삭제
            noticeService.deleteNotice(myKey, treeKey);
            return new TreeAndStickers(tree, stickers, true);
        }
        return new TreeAndStickers(tree, stickers, false);

    }

    @PostMapping  //새로운 트리 생성
    public Map<String, String> save(@Login Long myKey,
                                    @Validated @RequestBody AddTreeForm addTreeForm, BindingResult result) throws IOException, SQLException {

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
                                @Validated @RequestBody UpdateTreeForm updateTreeForm, BindingResult result) throws SQLException {
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

    @DeleteMapping("/{treeKey}")
    public  ResponseResult delete(@Login Long myKey,@PathVariable Long treeKey) throws SQLException {



        log.info("트리 삭제");
        Tree tree = treeService.findByTreeId(treeKey);
        if(tree.getMemberKey()!=myKey){
            ResponseResult responseResult = new ResponseResult<>("나의 트리가 아닙니다.");
            responseResult.setErrorCode(-333);
            return responseResult;
        }
        treeService.delete(treeKey);
        log.info("트리 삭제 완료 제목");

        return new ResponseResult<>("삭제 완료.");
    }

}

