package tohear.hearo.global;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // 실무에서는 이 secret 키를 application.properties 환경변수로 빼서 관리합니다.
    // 최소 32바이트 이상의 임의의 문자열이 필요합니다.
    private final String secretKey = "yourCustomSecretKey";
    private final long validityInMilliseconds = 36000000; // 토큰 유효시간 10시간

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 로그인 성공 시 토큰을 생성하는 메서드
    public String createToken(String userId) {
        Claims claims = Jwts.claims().setSubject(userId); // 토큰 주인의 ID 저장
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
