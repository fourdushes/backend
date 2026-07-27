package tohear.hearo.inquiry.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import tohear.hearo.user.auth.domain.UserType;

class InquiryTest {

    @Test
    void inquiryStartsPendingAndCanBeAnswered() {
        Inquiry inquiry = new Inquiry("ward", UserType.WARD, "제목", "내용");

        assertThat(inquiry.getStatus()).isEqualTo(InquiryStatus.PENDING);
        assertThat(inquiry.getCreatedAt()).isNotNull();

        inquiry.answer(" 답변입니다. ");

        assertThat(inquiry.getStatus()).isEqualTo(InquiryStatus.ANSWERED);
        assertThat(inquiry.getAnswer()).isEqualTo("답변입니다.");
        assertThat(inquiry.getAnsweredAt()).isNotNull();
    }

    @Test
    void blankAnswerIsRejected() {
        Inquiry inquiry = new Inquiry("ward", UserType.WARD, "제목", "내용");

        assertThatThrownBy(() -> inquiry.answer(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
