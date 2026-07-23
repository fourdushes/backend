package tohear.hearo.user.auth.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock JavaMailSender mailSender;
    @Mock StringRedisTemplate redisTemplate;
    @Mock ValueOperations<String, String> values;
    private MailService service;

    @BeforeEach
    void setUp() {
        service = new MailService(mailSender, redisTemplate);
        when(redisTemplate.opsForValue()).thenReturn(values);
    }

    @Test
    void correctCodeIsConsumedAndMarksEmailVerified() {
        when(values.get("mail:user@test.com")).thenReturn("123456");

        assertThat(service.checkCode("user@test.com", "123456")).isTrue();
        verify(redisTemplate).delete("mail:user@test.com");
        verify(values).set("mail-verified:user@test.com", "true", Duration.ofMinutes(10));
    }

    @Test
    void wrongOrExpiredCodeDoesNotChangeRedis() {
        when(values.get("mail:user@test.com")).thenReturn(null);

        assertThat(service.checkCode("user@test.com", "123456")).isFalse();
        verify(redisTemplate, never()).delete(any(String.class));
        verify(values, never()).set(any(String.class), any(String.class), any(Duration.class));
    }

    @Test
    void verifiedEmailIsAcceptedAndConsumedAfterJoin() {
        when(values.get("mail-verified:user@test.com")).thenReturn("true");

        service.validateVerifiedEmail("user@test.com");
        service.consumeVerifiedEmail("user@test.com");

        verify(redisTemplate).delete("mail-verified:user@test.com");
    }

    @Test
    void unverifiedEmailIsRejected() {
        when(values.get("mail-verified:user@test.com")).thenReturn(null);

        assertThatThrownBy(() -> service.validateVerifiedEmail("user@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일 인증이 완료되지 않았습니다. 이메일 인증을 먼저 진행해주세요.");
    }
}
