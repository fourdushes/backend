package tohear.hearo.medicaltreatment.record.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tohear.hearo.medicaltreatment.record.domain.Record;

public interface MedicalRecordLookupRepository extends JpaRepository<Record, Long> {

    Optional<Record> findFirstByArchiveIdAndRecordFileOrderByRecordDateDescIdDesc(Long archiveId, String recordFile);
}
