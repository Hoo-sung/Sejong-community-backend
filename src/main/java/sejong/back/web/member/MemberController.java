package sejong.back.web.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.back.domain.login.LoginService;
import sejong.back.domain.member.Member;
import sejong.back.domain.repository.MemberRepository;
import sejong.back.exception.DoubleSignUpException;
import sejong.back.exception.WrongSignUpException;
import sejong.back.web.Response;

import java.io.IOException;

//TODO rest api를 쓰는거면 로그인 안된 사용자가 접근할 떄 리다이렉트시키는 걸 서버에서 해줘야하는거 아님 클라에서 해주는거?
@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
@Slf4j
public class MemberController {

    private final LoginService loginService;
    private final MemberRepository memberRepository;

    /**
     * @TODO 우리 서비스에서 회원가입할 때 입력한 이름과 세종대 계정에 등록된 이름이 다른 경우엔 어떻게 처리?
     */
    @PostMapping("/add")
    public Response<?> save(@Validated @ModelAttribute AddMemberForm addMemberForm, BindingResult bindingResult) throws IOException {
        //TODO 회원가입 시 빈 값이 넘어올 경우 그걸 서버에서 처리하나 아님 클라이언트에서 처리하나?
        if (bindingResult.hasErrors()) {
            log.info("bindingResult error");
            return new Response<>("error", "에러 발생", bindingResult.getAllErrors().toArray());
        }

        //학사시스템 확인후 정보 안 맞으면 다시 폼 반환.
        Member validateMember = loginService.validateSejong(addMemberForm.getStudentId(), addMemberForm.getPassword());
        if (validateMember == null) { // 잘못된 계정으로 회원가입 시도한 경우
            throw new WrongSignUpException("잘못된 계정으로 회원가입 시도");
        }

        //학사 시스템 회원 조회 성공.
        Member searchMember = memberRepository.findByLoginId(validateMember.getStudentId());//db에 회원 조회.

        if (searchMember == null) {//db에 없으면, 회원 가입 절차 정상적으로 진행해야 한다.
            memberRepository.save(validateMember);//db에 저장.
            log.info("savedMember={}", memberRepository.findAll());
            return new Response<>("success", "회원가입 성공", null);
        }
        else {
            log.info("회원 가입된 사용자입니다={} {}", searchMember.getStudentId(), searchMember.getName());
            throw new DoubleSignUpException("이미 회원가입된 사용자");
        }
    }


}
