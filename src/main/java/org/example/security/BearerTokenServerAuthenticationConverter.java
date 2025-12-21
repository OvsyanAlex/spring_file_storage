package org.example.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;


@RequiredArgsConstructor // Задача класса - HTTP request → JWT → Authentication
public class BearerTokenServerAuthenticationConverter implements ServerAuthenticationConverter {

    private final JwtHandler jwtHandler;                   // валидирует JWT, проверяет подпись, извлекает данные пользователя
    private static final String BEARER_PREFIX = "Bearer "; // префикс заголовка и получение JWT без заголовка в следующем поле
    private static final Function<String, Mono<String>> getBearerValue = authValue -> Mono.justOrEmpty(authValue.substring(BEARER_PREFIX.length()));

    @Override // convert() вызывается Spring Security автоматически, мы переопределили его ServerAuthenticationConverter
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return extractHeader(exchange)
                .flatMap(getBearerValue) // получаем Mono<String JWT>
                .flatMap(jwtHandler::check)// проверка подписи, проверку срока действия, проверку структуры токена
                .flatMap(UserAuthenticationBearer::create); // получаем Mono<Authentication>
    }

    // получение заголовка
    private Mono<String> extractHeader(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION));
    }
}
