package tohear.hearo.medicaltreatment.archive.domain;

import java.time.LocalDateTime;

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

    @JoinColumn(name = "ward_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private WardUser wardUser;

}
