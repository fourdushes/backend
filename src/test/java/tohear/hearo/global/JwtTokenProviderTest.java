package tohear.hearo.global;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.JwtException;
import tohear.hearo.user.auth.domain.UserType;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "secretKey", "01234567890123456789012345678901");
        ReflectionTestUtils.setField(provider, "validityInMilliseconds", 60_000L);
    }

    @Test
    void tokenRoundTripPreservesUserData() {
        String token = provider.createToken("ward-1", UserType.WARD);

        assertThat(provider.getUserId(token)).isEqualTo("ward-1");
        assertThat(provider.getUserType(token)).isEqualTo(UserType.WARD);
    }

    @Test
    void expiredTokenIsRejected() {
        ReflectionTestUtils.setField(provider, "validityInMilliseconds", -1L);
        String token = provider.createToken("ward-1", UserType.WARD);

        assertThatThrownBy(() -> provider.getUserId(token)).isInstanceOf(JwtException.class);
    }
}
