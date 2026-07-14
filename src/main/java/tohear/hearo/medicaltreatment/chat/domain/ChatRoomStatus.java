package tohear.hearo.medicaltreatment.chat.domain;

public enum ChatRoomStatus {
    IN_PROGRESS("진료 중"),
    COMPLETED("완료됨");

    private final String description;

    ChatRoomStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
