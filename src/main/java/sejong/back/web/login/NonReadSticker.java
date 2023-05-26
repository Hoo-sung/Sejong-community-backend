package sejong.back.web.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class NonReadSticker {

    private String name; //트리 제목
    private Long id; //트리 id
    private Integer count; //안 읽은 스티커 개수

}
