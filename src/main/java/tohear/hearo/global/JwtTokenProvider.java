package tohear.hearo.global;

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
    private long validityInMilliseconds;

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 로그인 성공 시 토큰을 생성하는 메서드
    public String createToken(String userId, UserType userType) {
        Claims claims = Jwts.claims().setSubject(userId); // 토큰 주인의 ID 저장
        claims.put("userType", userType.name()); // UserType을 문자열(WARD, GUARDIAN 등)로 저장
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserId(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public UserType getUserType(String token) {
        String userTypeStr = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .get("userType", String.class); // 클레임에서 userType 문자열 꺼내기
            
        return UserType.valueOf(userTypeStr); // 문자열을 다시 오리지널 Enum 타입으로 변환해서 반환
    }
}
