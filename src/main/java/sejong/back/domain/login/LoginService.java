package sejong.back.domain.login;

import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import sejong.back.domain.member.Member;
import sejong.back.domain.member.MemberGrade;
import sejong.back.domain.repository.MemberRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;


//    public Member login(Long key, String department) {//학번이랑 과가 같으면 같은 학생으로 분류**
//        return memberRepository.findByKey(key)
//                .filter(m -> m.get)
//                .orElse(null);
//    }

    public Member validateSejong(String studentId, String password) throws IOException {

        Map<String, String> data = new HashMap<>();
        data.put("userId", studentId);
        data.put("password", password);

        // 로그인 페이지 접속
        //userAgent 는 생략가능하다.
//        String userAgent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36";


            Connection.Response loginPageResponse = Jsoup.connect("http://classic.sejong.ac.kr/userLogin.do")
//                .userAgent(userAgent)
                    .timeout(3000)
                    .header("Origin", "http://classic.sejong.ac.kr")
                    .header("Referer", "https://classic.sejong.ac.kr/userLoginPage.do")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language", "ko,en-US;q=0.9,en;q=0.8,ko-KR;q=0.7")
                    .data(data)
                    .method(Connection.Method.POST)
                    .execute();


// 로그인 페이지에서 얻은 쿠키  만약 쿠키가 없으면 login/loginForm으로 이동.


        //http://classic.sejong.ac.kr/userCertStatus.do?menuInfoId=MAIN_02_05 내 상세 화면이 나와있는 페이지이다.

        Map<String, String> loginCookie = loginPageResponse.cookies();
//        String jsessionid = loginPageResponse.cookie("JSESSIONID");
//        if(jsessionid==null)
//            return "login/loginForm";


// 로그인 성공 후 얻은 쿠키.

            Document loginPageDocument = Jsoup.connect("http://classic.sejong.ac.kr/userCertStatus.do?menuInfoId=MAIN_02_05")
                    .header("Referer", "https://classic.sejong.ac.kr/userLoginPage.do")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language", "ko,en-US;q=0.9,en;q=0.8,ko-KR;q=0.7")
                    .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
                    .get();


            System.out.println("파싱 시작");


            String info[] = new String[]{" ", " ", " ", " ", " ", " ", " ", " "};
            int i = 0;
            Elements elements = loginPageDocument.select("div.content-section > div.contentWrap > ul.tblA>li>dl>dd");
            for (Element element : elements) {
                String value = element.html();
                info[i] = value;
                i++;
            }


            for (String s : info) {
                System.out.println("s = " + s);
            }
            /**
             * 가져온 html 페이지를 가지고 파싱하는 로직이 들어가야 한다.
             * 파싱한 것으로 Member 객체를 생성하고, 메모리에 저장하고, 모델에 담에서 login/form에 뿌려야 한다.
             */


            String department = info[0];
            String studentIdk = info[1];
            String name = info[2];
            String grade = info[3];//학년
            String status = info[4];//재학 휴학 졸업으로 나태내어짐.
            String currentSemester = info[5];//현재 몇학기인지
            String unUsing = info[6];
            String passFlag = info[7];

            if (!studentId.equals(studentIdk)) {
                return null;
            }

            Member member=new Member(name, department, studentId, grade, status, passFlag);
            member.setGrade(MemberGrade.BRONZE);
        return member;//멤버 객체를 만들고 애를 db에 저장하는 형식이다.
    }



}
