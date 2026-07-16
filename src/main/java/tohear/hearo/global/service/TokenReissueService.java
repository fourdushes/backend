package tohear.hearo.global.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tohear.hearo.global.security.JwtTokenProvider;
import tohear.hearo.user.auth.domain.UserType;
import tohear.hearo.user.auth.dto.request.TokenReissueRequest;
import tohear.hearo.user.auth.dto.response.TokenReissueResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenReissueService {

    private final JwtTokenProvider tokenProvider;
    private final StringRedisTemplate redisTemplate;

    public TokenReissueResponse reissue(TokenReissueRequest request) {
        String refreshToken = request.getRefreshToken();

        // 서명, 만료시간, REFRESH 종류 확인
        tokenProvider.validateRefreshToken(refreshToken);

        String userId = tokenProvider.getUserId(refreshToken);

        UserType userType = tokenProvider.getUserType(refreshToken);

        String redisKey = "refresh-token:" + userId;

        String savedRefreshToken = redisTemplate.opsForValue().get(redisKey);

        if (savedRefreshToken == null) {
            throw new IllegalArgumentException("저장된 Refresh Token이 없습니다.");
        }

        if (!savedRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 일치하지 않습니다.");
        }

        String newAccessToken = tokenProvider.createAccessToken(userId, userType);

        String newRefreshToken = tokenProvider.createRefreshToken(userId, userType);

        // 기존 Refresh Token을 새 토큰으로 교체
        redisTemplate.opsForValue().set(
            redisKey,
            newRefreshToken,
            Duration.ofMillis(
                tokenProvider
                .getRefreshTokenValidityInMilliseconds()
            )
        );

        return new TokenReissueResponse(
            newAccessToken,
            newRefreshToken
        );
    }
}
