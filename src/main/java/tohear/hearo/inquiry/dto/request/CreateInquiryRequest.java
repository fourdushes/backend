package tohear.hearo.inquiry.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateInquiryRequest {

    private String title;

    private String content;
}
