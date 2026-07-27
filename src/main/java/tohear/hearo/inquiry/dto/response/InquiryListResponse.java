package tohear.hearo.inquiry.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InquiryListResponse {

    private long totalElements;
    private int page;
    private int size;
    private boolean hasNext;
    private List<InquiryResponse> inquiries;
}
