package sejong.back.domain.member;

public enum MemberType {

    FRESHMAN("신입생"),
    UNDERGRADUATE("재학생"),
    LEAVE_STUDENT("휴학생"),
    RETURN_STUDENT("복학생"),
    GRADUATE("졸업생"),
    PROFESSOR("교수");

    private final String description;

    MemberType(String description) {
        this.description = description;

    }

    public String getDescription() {
        return description;
    }

}