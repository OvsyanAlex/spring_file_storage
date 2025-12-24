package org.example.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.exception.UnauthorizedException;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;


// JwtHandler = криптографическая и логическая проверка JWT
// при любых ошибках - 401
public class JwtHandler {

    private final String secret; // secret — симметричный ключ для подписи JWT (HMAC)


    public JwtHandler(String secret) {
        this.secret = secret;
    }


    // Принимает сырой JWT (String) Вызывает verify(...) Если всё ок → VerificationResult
    public Mono<VerificationResult> check(String accessToken) {
        return Mono.fromCallable(() -> verify(accessToken))

                .onErrorResume(e -> Mono.error(new UnauthorizedException(e.getMessage())));
    }

    private VerificationResult verify(String token) {

        // получение claims — payload JWT: sub, roles, exp, userId
        Claims claims = getClaimsFromToken(token);
        final Date expirationDate = claims.getExpiration();

        // Проверка срока действия
        if (expirationDate.before(new Date())) {
            throw new RuntimeException("Token expired");
        }


        return new VerificationResult(claims, token); // claims — payload JWT: sub, roles, exp, userId, token — сам токен (часто нужен дальше)
    }

    private Claims getClaimsFromToken(String token) {
        // Генерация ключа для HMAC (Hash-based Message Authentication Code) — способ доказать, что сообщение не изменилось и подписано тем, кто знает секрет
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        // проверка подписи, проверка структуры, возвращается payload - Claims

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    // DTO между слоями, JwtHandler → Authentication

    public static class VerificationResult {
        public Claims claims;
        public String token;

        public VerificationResult(Claims claims, String token) {
            this.claims = claims;
            this.token = token;
        }
    }
}
