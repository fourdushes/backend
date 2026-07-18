package tohear.hearo.medicaltreatment.record.domain;

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
import tohear.hearo.archive.domain.Archive;
import tohear.hearo.user.ward.WardUser;

@Entity
@Getter
public class Record {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    private String recordFile;
    private LocalDateTime recordDate;

    @JoinColumn(name = "archive_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Archive archive;

    @JoinColumn(name = "ward_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private WardUser wardUser;

    public Record() {
    }

    public Record(String recordFile, LocalDateTime recordDate, Archive archive, WardUser wardUser) {
        this.recordFile = recordFile;
        this.recordDate = recordDate;
        this.archive = archive;
        this.wardUser = wardUser;
    }

    

}
