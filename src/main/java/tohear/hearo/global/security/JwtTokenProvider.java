package tohear.hearo.global.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import tohear.hearo.user.auth.domain.UserType;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.validity-in-milliseconds}")
    private long accessTokenValidityInMilliseconds;

    @Value("${jwt.refresh-token-validity-in-milliseconds}")
    private long refreshTokenValidityInMilliseconds;

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 로그인 성공 시 토큰을 생성하는 메서드
    public String createAccessToken(String userId, UserType userType) {
        return createToken(
            userId,
            userType,
            "ACCESS",
            accessTokenValidityInMilliseconds
        );
    }

    public String createRefreshToken(String userId, UserType userType) {
        return createToken(
            userId,
            userType,
            "REFRESH",
            refreshTokenValidityInMilliseconds
        );
    }
    public String getUserId(String token) {
        return getClaims(token).getSubject();
    }

    public UserType getUserType(String token) {
        String userTypeStr = getClaims(token).get("userType", String.class); // 클레임에서 userType 문자열 꺼내기
            
        return UserType.valueOf(userTypeStr); // 문자열을 다시 오리지널 Enum 타입으로 변환해서 반환
    }

    public void validateAccessToken(String token) {
        String tokenType = getClaims(token).get("tokenType", String.class);

        if (!"ACCESS".equals(tokenType)) {
            throw new IllegalArgumentException("Access Token이 아닙니다.");
        }
    }

    public void validateRefreshToken(String token) {
        String tokenType = getClaims(token).get("tokenType", String.class);

        if (!"REFRESH".equals(tokenType)) {
            throw new IllegalArgumentException("Refresh Token이 아닙니다.");
        }
    }

    public long getRefreshTokenValidityInMilliseconds() {
        return refreshTokenValidityInMilliseconds;
    }

    /**
     * claims는 JWT 토큰 안에 묶이는 정보 묶음이다
     * 여기서는 유저 아이디 + 유저 타입 + 토큰 타입을 넣는다
     */
    private String createToken(String userId, UserType userType, String tokenType, long validityInMilliseconds) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("userType", userType.name());
        claims.put("tokenType", tokenType);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims getClaims(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
}
}
