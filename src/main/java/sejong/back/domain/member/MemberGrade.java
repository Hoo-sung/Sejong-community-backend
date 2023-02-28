package sejong.back.domain.member;

public enum MemberGrade {

    BRONZE("브론즈"),
    SILVER("실버"),
    GOLD("골드"),
    PLATINUM("플래티넘"),
    DIAMOND("다이아몬드");

    private final String description;

    MemberGrade(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}