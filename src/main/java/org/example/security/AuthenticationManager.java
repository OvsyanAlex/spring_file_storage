package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.exception.UnauthorizedException;
import org.example.model.Status;
import org.example.service.UserService;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final UserService userService;

    // проверка, что для принятого JWT существует пользак и он активен

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        return userService.findById(principal.getId())
                .filter(userEntity-> userEntity.getStatus().equals(Status.ACTIVE))
                .switchIfEmpty(Mono.error(new UnauthorizedException("User not active")))
                .map(user -> authentication);
    }
}
