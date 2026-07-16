package tohear.hearo.global.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.JwtException;
import tohear.hearo.user.auth.domain.UserType;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "secretKey", "01234567890123456789012345678901");
        ReflectionTestUtils.setField(provider, "accessTokenValidityInMilliseconds", 60_000L);
        ReflectionTestUtils.setField(provider, "refreshTokenValidityInMilliseconds", 120_000L);
    }

    @Test
    void tokenRoundTripPreservesUserData() {
        String token = provider.createAccessToken("ward-1", UserType.WARD);

        assertThat(provider.getUserId(token)).isEqualTo("ward-1");
        assertThat(provider.getUserType(token)).isEqualTo(UserType.WARD);
        provider.validateAccessToken(token);
    }

    @Test
    void expiredTokenIsRejected() {
        ReflectionTestUtils.setField(provider, "accessTokenValidityInMilliseconds", -1L);
        String token = provider.createAccessToken("ward-1", UserType.WARD);

        assertThatThrownBy(() -> provider.getUserId(token)).isInstanceOf(JwtException.class);
    }

    @Test
    void tokenTypesCannotBeUsedInterchangeably() {
        String accessToken = provider.createAccessToken("ward-1", UserType.WARD);
        String refreshToken = provider.createRefreshToken("ward-1", UserType.WARD);

        assertThatThrownBy(() -> provider.validateRefreshToken(accessToken))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> provider.validateAccessToken(refreshToken))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
