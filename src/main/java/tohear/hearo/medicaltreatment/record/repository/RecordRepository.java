package tohear.hearo.medicaltreatment.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tohear.hearo.medicaltreatment.record.domain.Record;

public interface RecordRepository extends JpaRepository<Record, Long>{

}
