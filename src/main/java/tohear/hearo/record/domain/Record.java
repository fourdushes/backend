package tohear.hearo.record.domain;

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
import tohear.hearo.Archive.domain.Archive;
import tohear.hearo.user.ward.WardUser;

@Entity
@Getter
public class Record {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    private byte[] recordFile;
    private LocalDateTime recordDate;

    @JoinColumn(name = "archive_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Archive archive;

    @JoinColumn(name = "ward_user_id")
    @OneToOne(fetch = FetchType.LAZY)
    private WardUser wardUser;

    public Record() {
    }

    public Record(byte[] recordFile, LocalDateTime recordDate, Archive archive, WardUser wardUser) {
        this.recordFile = recordFile;
        this.recordDate = recordDate;
        this.archive = archive;
        this.wardUser = wardUser;
    }

    

}
