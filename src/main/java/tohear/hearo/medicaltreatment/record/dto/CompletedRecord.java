package tohear.hearo.medicaltreatment.record.dto;

import tohear.hearo.medicaltreatment.record.domain.Record;

public record CompletedRecord(Record record, String recordText) {
}
