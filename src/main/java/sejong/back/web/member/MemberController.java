package sejong.back.web.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.back.domain.login.LoginService;
import sejong.back.domain.member.Member;
import sejong.back.domain.repository.MemberRepository;
import sejong.back.exception.DoubleSignUpException;
import sejong.back.exception.WrongSignUpException;
import sejong.back.web.ResponseResult;

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
     *
     * ==> 우선은 회원가입 시 이름이 아니라 닉네임을 작성하도록 변경해봤음.
     *     어차피 이름은 아이디, 비번만 맞으면 세종대 사이트에서 가져오면 되니까
     *     그래서 AddMemberForm의 name을 nickname으로 바꾸고, Member에 nickname 필드 추가
     *
     * @TODO Gradle을 통해서 실행하면 이상하게 AddMemberForm을 인식하지 못함
     */
    @PostMapping("/add")
    public ResponseResult<?> save(@Validated @ModelAttribute AddMemberForm addMemberForm, BindingResult bindingResult) throws IOException {

        if (bindingResult.hasErrors()) { //닉네임, 학번, 비번 중 빈 값이 있을 경우
            throw new WrongSignUpException("비어있는 값이 있음");
        }

        Member validateMember = loginService.validateSejong(addMemberForm.getStudentId(), addMemberForm.getPassword());
        if (validateMember == null) { // 잘못된 계정으로 회원가입 시도한 경우
            throw new WrongSignUpException("잘못된 계정으로 회원가입 시도");
        }

        //학사 시스템 회원 조회 성공.
        Member searchMember = memberRepository.findByLoginId(validateMember.getStudentId());//db에 회원 조회.

        if (searchMember == null) {//db에 없으면, 회원 가입 절차 정상적으로 진행해야 한다.
            //닉네임은 검증이 다 끝난 후 따로 추가. TODO 근데 setter가 컨트롤러에 직접 보이는게 좀 별로임
            validateMember.setNickname(addMemberForm.getNickname());
            memberRepository.save(validateMember);//db에 저장.
            log.info("savedMember={}", memberRepository.findAll());
            return new ResponseResult<>("회원가입 성공");
        } else {
            log.info("회원 가입된 사용자입니다={} {}", searchMember.getStudentId(), searchMember.getName());
            throw new DoubleSignUpException("이미 회원가입된 사용자");
        }
    }


}
