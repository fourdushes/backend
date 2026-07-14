package tohear.hearo.medicaltreatment.record.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import lombok.Getter;
import tohear.hearo.medicaltreatment.archive.domain.Archive;
import tohear.hearo.user.ward.WardUser;

@Entity
@Getter
public class Record {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    private String recordFile; // https://my-bucket.s3.ap-northeast-2.amazonaws.com/audio/record123.m4a
    private LocalDateTime recordDate;

    @JoinColumn(name = "archive_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Archive archive;

    @JoinColumn(name = "ward_user_id")
    @OneToOne(fetch = FetchType.LAZY)
    private WardUser wardUser;

    public Record() {
    }

    public Record(String recordFile, LocalDateTime recordDate, Archive archive, WardUser wardUser) {
        this.recordFile = "https://my-bucket.s3.ap-northeast-2.amazonaws.com/audio/" + recordFile;
        this.recordDate = recordDate;
        this.archive = archive;
        this.wardUser = wardUser;
    }

    

}
