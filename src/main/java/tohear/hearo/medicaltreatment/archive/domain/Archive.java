package tohear.hearo.medicaltreatment.archive.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.Getter;
import tohear.hearo.user.ward.WardUser;

@Entity
@Getter
public class Archive {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archive_id")
    private Long id;

    private String title;
    private LocalDateTime archiveDate;
    private String text;

    @Column(columnDefinition = "TEXT")
    private String allChatText;

    @JoinColumn(name = "ward_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private WardUser wardUser;

    public Archive() {
    }

    public Archive(String text, WardUser wardUser) {
        LocalDateTime createTime = archiveDate == null ? LocalDateTime.now() : archiveDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년MM월dd일");

        this.title = createTime.format(formatter) + " " + wardUser.getName() + "님의 진료 기록";
        this.archiveDate = createTime;
        this.text = text;
        this.wardUser = wardUser;
    }

    public void updateText(String text) {
        this.text = text;
    }

    public void updateAllChatText(String allChatText) {
        this.allChatText = allChatText;
    }

}
