package org.example.config;



import org.example.security.AuthenticationManager;
import org.example.security.BearerTokenServerAuthenticationConverter;
import org.example.security.JwtHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;


@Configuration                  // конфигурационный класс, который создаёт бины
@EnableWebFluxSecurity          // включает реактивную безопасность для WebFlux
@EnableReactiveMethodSecurity   // позволяет использовать аннотации @PreAuthorize и @PostAuthorize на методах сервисов
public class WebSecurityConfig {

    @Value("${jwt.secret}")     // ключ для подписи JWT, берётся из application.properties или application.yml
    private String secret;


    @Bean // бин настраивает цепочку фильтров безопасности для WebFlux
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthenticationManager authenticationManager) {
        return http
                .csrf(csrfSpec -> csrfSpec.disable()) // CSRF отключён, это нормально, если фронт отдельный и используется JWT
                .authorizeExchange(ex -> ex
                        .pathMatchers("/api/v1/auth/**").permitAll()
                        .pathMatchers("/api/v1/**").authenticated()
                        .anyExchange().authenticated())

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((swe, e) -> // если пользователь не авторизован → 401
                                Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))

                        .accessDeniedHandler((swe, e) -> // если у авторизованного пользователя нет прав → 403
                                Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))))

                .addFilterAt( // фильтр, который обрабатывает аутентификацию = JWT
                        bearerAuthenticationFilter(authenticationManager),
                        SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    // кастомный фильтр для обработки JWT, добавляется в цепочку безопасности
    // получает HTTP-запрос, вынимает JWT из хедера и помещает объект Authentication в ReactiveSecurityContextHolder

    // AuthenticationWebFilter —  реактивный фильтр Spring Security, срабатывает на каждый запрос
    // AuthenticationManager - подтверждает, что Authentication валидный
    // BearerTokenServerAuthenticationConverter - читает заголовок Authorization с типом Bearer - Достаёт JWT
    // JwtHandler(secret) — проверяет подпись JWT с секретом. Парсит payload токена. Создает Authorities
    // setRequiresAuthenticationMatcher — для каких запросов нужно проверять аутентификацию "/**" → фильтр применяем ко всем путям

    // HTTP request
    //   ↓
    //AuthenticationWebFilter
    //   ↓
    //BearerTokenServerAuthenticationConverter.convert(...)
    //   ↓
    //AuthenticationManager.authenticate(...)
    //   ↓
    //SecurityContext

    private AuthenticationWebFilter bearerAuthenticationFilter(AuthenticationManager authenticationManager) {
        AuthenticationWebFilter bearerAuthenticationFilter = new AuthenticationWebFilter(authenticationManager);
        bearerAuthenticationFilter.setServerAuthenticationConverter(new BearerTokenServerAuthenticationConverter(new JwtHandler(secret)));
        bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(
                "/api/v1/users/**",
                "/api/v1/files/**",
                "/api/v1/events/**"));

        return bearerAuthenticationFilter;
    }
}
