package org.example.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import reactor.core.publisher.Mono;

import java.util.List;



public class UserAuthenticationBearer  {

    // данные из JWT (Claims) в Authentication, понятный Spring Security
    public static Mono<Authentication> create(JwtHandler.VerificationResult verificationResult) {
        Claims claims = verificationResult.claims;

        // subject = id пользователя
        String subject = claims.getSubject();
        Integer principalId = Integer.parseInt(subject);

        List<Permission> permissions = claims.get("permissions", List.class);
        String username = claims.get("username", String.class);

        // SimpleGrantedAuthority — стандартная реализация GrantedAuthority
        // Spring Security работает только с GrantedAuthority. Значение: "ROLE_USER", "ROLE_ADMIN"
        List<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name())).toList();

        // CustomPrincipal — доменный объект пользователя
        CustomPrincipal principal = new CustomPrincipal(principalId, username);

        // credentials = null - пароль не нужен (JWT уже проверен)
        return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(principal, null, authorities));
    }
}
