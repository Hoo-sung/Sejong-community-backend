package sejong.back.web.tree;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sejong.back.domain.Tree.Tree;
import sejong.back.domain.repository.TreeRepository;
import sejong.back.web.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TreeController {
    private final TreeRepository treeRepository;


    @PostMapping("/forest/mytree/add")
    @ResponseBody
    public Tree saveTree(@ModelAttribute Tree tree, HttpServletRequest request){
        HttpSession session = request.getSession();
        Long key = (Long) session.getAttribute(SessionConst.DB_KEY);
        tree.setStudentKey(key);
        log.info("makeTree = {} foreignKey = {}", tree.getTitle(), tree.getStudentKey());
        treeRepository.save(tree);
        return tree;
    }

    @PostMapping("/forest/mytree/edit")
    @ResponseBody
    public Tree updateTree(@ModelAttribute Tree tree, HttpServletRequest request){
        HttpSession session = request.getSession();
        Long key = (Long) session.getAttribute(SessionConst.DB_KEY);
        treeRepository.update(tree,key);

        log.info("editTree = {}", tree.getTitle());
        return treeRepository.findByStudentKey(key).findFirst().get();
    }

}
