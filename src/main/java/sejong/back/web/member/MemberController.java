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
import sejong.back.domain.service.TreeService;
import sejong.back.exception.WrongSessionIdException;
import sejong.back.web.ResponseResult;
import sejong.back.web.argumentresolver.Login;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

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

//    @ModelAttribute("memberTypes")//모델에 통쨰로보여줘야 라디오 버튼이든 뭐든 내용물을 통쨰로 출력할 수 있다.
//    public MemberType[] memberTypes(){
//        return MemberType.values();//전체 다 가져오기.
//    }

    //TODO 멤버 검색 페이지로 다른 멤버의 정보를 볼 일은 없을 듯
    //      왜냐하면 "/forest"에서 다른 사람의 트리를 보면 되니까
    //      우선 주석 처리

    /**
     * @TODO 우리 서비스에서 회원가입할 때 입력한 이름과 세종대 계정에 등록된 이름이 다른 경우엔 어떻게 처리?
     * ==> 우선은 회원가입 시 이름이 아니라 닉네임을 작성하도록 변경해봤음.
     * 어차피 이름은 아이디, 비번만 맞으면 세종대 사이트에서 가져오면 되니까
     * 그래서 AddMemberForm의 name을 nickname으로 바꾸고, Member에 nickname 필드 추가
     * @TODO Gradle을 통해서 실행하면 이상하게 AddMemberForm을 인식하지 못함
     * <p>
     * 학사시스템을 바탕으로 이름, 학번 등 가져올 수 있는 정보는 가져와서 validatemember에 저장되어있다.
     * 그리므로 여기에 회원가입 폼에서 사용자가 추가해야할 요소들인
     * 1.신입 재학, 휴학 여부를 선택하고
     * 2.태그들을 가지고 싶은거 선택하게 해야한다.
     * <p>
     * 이것을 받아서 학사 시스템으로 로그인 잘 되서 검증된 멤버인 validatemember에 담고 내용을 추가해서 저장한다. db에
     */
    @PostMapping
    public ResponseResult<?> save(@Validated @RequestBody AddMemberForm addMemberForm, BindingResult bindingResult,
                                  HttpServletResponse response) throws IOException {

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
        return new ResponseResult<>();
    }

    @GetMapping("/my-page/edit")//자시 자신만 수정 할 수 있도록 해야한다. 다른애 꺼 수정 못하게 해야 한다.
    public String editForm(@Login Member member, Model model, HttpServletRequest request) {//세션에 있는 db key를 보고, 자시 key일때만 자기 페이지 수정을 할 수있도록, 다른 사용자 정보 수정 시도시, 내정보 수정으로 redirect시.

        if (member == null) {
            //TODO 예외 처리
            throw new NullPointerException("내 정보를 찾을 수 없음");
        }
        //TODO 뷰 렌더링
        model.addAttribute("member", member);
        return "members/editForm";
    }

    @PatchMapping//여기를 공개 정보 수정이라고 하자.
    public void edit(@RequestBody UpdateMemberForm updateMemberForm,
                     HttpServletRequest request,@Login Long myKey) throws Exception {

        HttpSession session = request.getSession(false);
        String sessionId = session.getId();
        log.info("Patching Session = {}", sessionId);

        if (sessionId==null) { //클라이언트로부터 받은 sessionId와 api 서버에 저장된 sessionId가 다를 때
            throw new WrongSessionIdException("로그인 X");
        }

        Member member = memberService.findByKey(myKey);
        member.setNickname(updateMemberForm.getNickname());
        member.setOpenStudentId(updateMemberForm.isOpenStudentId());
        member.setOpenDepartment(updateMemberForm.isOpenDepartment());

        log.info("확인용 로그 = {}", member.getName());
    }

    //회원 정보 수정이 정상적으로 이루어졌는지 테스트하는 컨트롤러
    @GetMapping
    public HashMap<String, Object> showMember(@Login Member member) {
        log.info("정보 열람 = {}", member.getKey());
        HashMap<String, Object> data = new HashMap<>();
        data.put("member", member);
        data.put("treeId", treeService.findMyTrees(member.getKey()));


        return data;
    }

    @PostConstruct
    public void TestEnvironment() throws IOException {

        //member
        Member m1 = new Member("A", "Computer Science", Long.valueOf(19011901), "3", "재학");

        Member validateMember = loginService.validateSejong(Long.valueOf(18011881), "19991201");
        Member m2 = new Member("B", "Computer Science", Long.valueOf(18011881), "4", "재학");
        Member m3 = new Member("C", "Computer Science", Long.valueOf(20000001), "1", "재학");
        Member m4 = new Member("C", "Electric Communication", Long.valueOf(18010741), "3", "재학");

        //save

        memberService.save(m1);
        memberService.save(m2);
        memberService.save(m3);
        memberService.save(m4);

    }

}
