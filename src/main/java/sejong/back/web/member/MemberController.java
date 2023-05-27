
package sejong.back.web.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.back.domain.member.AddMemberForm;
import sejong.back.domain.member.Member;
import sejong.back.domain.member.UpdateMemberForm;
import sejong.back.domain.service.LoginService;
import sejong.back.domain.service.MemberService;
import sejong.back.domain.service.NoticeService;
import sejong.back.domain.service.TreeService;
import sejong.back.domain.tree.Tree;
import sejong.back.exception.WrongSessionIdException;
import sejong.back.web.ResponseResult;
import sejong.back.web.SessionConst;
import sejong.back.web.argumentresolver.Login;
import sejong.back.web.login.NonReadSticker;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

//TODO rest api를 쓰는거면 로그인 안된 사용자가 접근할 떄 리다이렉트시키는 걸 서버에서 해줘야하는거 아님 클라에서 해주는거?
//==> PRG 참고
@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final LoginService loginService;
    private final TreeService treeService;
    private final NoticeService noticeService;

    @GetMapping//멤버 객체와 애의 트리들을 전부 줘야 한다.
    public HashMap<String, Object> showMember(@Login Member member) throws SQLException {
        log.info("정보 열람 = {}", member.getKey());
        HashMap<String, Object> data = new HashMap<>();
        data.put("member", member);

        List<Tree> myTrees = treeService.findMyTrees(member.getKey());
        if(myTrees==null)
            data.put("treeId", Collections.emptyList());//비어 있으면,
        else
            data.put("treeId", myTrees);

        List<NonReadSticker> notices = noticeService.getNotice(member.getKey());
        data.put("alarmCount", notices);

        return data;
    }

    @PostMapping
    public ResponseResult<?> save(@Validated @RequestBody AddMemberForm addMemberForm, BindingResult bindingResult) throws IOException, SQLException {

        log.info("studentId={}", addMemberForm.getStudentId());
        log.info("password={}", addMemberForm.getPassword());
        log.info("dataRange={}", addMemberForm.isOpenStudentId());

        ResponseResult<Object> responseResult = new ResponseResult();

        if (bindingResult.hasErrors()) { //닉네임, 학번, 비번 중 빈 값이 있을 경우
            responseResult.setMessage("비어있는 값이 있음");
            responseResult.setErrorCode(-101);
            return responseResult;
        }

        Member validateMember = loginService.validateSejong(addMemberForm.getStudentId(), addMemberForm.getPassword());
        if (validateMember == null) { //잘못된 계정으로 회원가입 시도한 경우
            responseResult.setMessage("잘못된 계정으로 회원가입 시도");
            responseResult.setErrorCode(-102);
            return responseResult;
        }

        //학사 시스템 회원 조회 성공.
        Member searchMember = memberService.findByLoginId(validateMember.getStudentId());//db에 회원 조회.
        if (searchMember != null) { //해당 계정으로 회원가입한 적이 있으면
            log.info("회원 가입된 사용자입니다={} {}", searchMember.getStudentId(), searchMember.getName());
            responseResult.setMessage("이미 회원가입된 사용자");
            responseResult.setErrorCode(-103);
            return responseResult;
        }

        //db에 없으면, 회원 가입 절차 정상적으로 진행해야 한다.
        //닉네임과 공개 벙위는 검증이 다 끝난 후 따로 추가. TODO 근데 setter가 컨트롤러에 직접 보이는게 좀 별로임
        validateMember.setNickname(addMemberForm.getNickname());
        validateMember.setOpenStudentId(addMemberForm.isOpenStudentId());
        validateMember.setOpenDepartment(addMemberForm.isOpenDepartment());
        memberService.save(validateMember);//db에 저장.
        log.info("validateMember={} {}", validateMember.getStudentId(), validateMember.getName());

        responseResult.setMessage("저장 되었습니다.");
        return responseResult;
    }



    @PatchMapping//여기를 공개 정보 수정이라고 하자.
    public ResponseResult<?> edit(@Login Long myKey, @RequestBody UpdateMemberForm updateMemberForm) throws Exception {

        //Login에서 예외처리 하고,
        memberService.update(myKey, updateMemberForm);

        ResponseResult<Object> responseResult = new ResponseResult<>();
        responseResult.setMessage("회원 정보 수정됨.");
        return responseResult;
    }

    @DeleteMapping//여기를 공개 정보 수정이라고 하자.
    public ResponseResult<?> delete(@Login Long myKey) throws Exception {
        memberService.delete(myKey);

        ResponseResult<Object> responseResult = new ResponseResult<>();
        responseResult.setMessage("회원 탈퇴 됨.");
        return responseResult;
    }
}

