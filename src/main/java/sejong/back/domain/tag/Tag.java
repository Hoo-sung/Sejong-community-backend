package sejong.back.domain.tag;


import lombok.Data;

@Data
public class Tag {

    private  int tag_Id;
    private String description;//태그 이름.

    public Tag(String description) {
        this.description = description;
    }

    public Tag() {
    }
}
