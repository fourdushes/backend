package tohear.hearo.user.institution;

public enum InstitutionState {

    APPROVED("승인됨"), // 승인됨
    PENDING("승인 대기 중"), // 승인 대기 중
    REJECTED("거절됨"); // 거절됨

    private final String description;


    InstitutionState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
