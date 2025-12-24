package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.AuthRequestDto;
import org.example.dto.AuthResponseDto;
import org.example.dto.RegistrUserDto;
import org.example.dto.UserDto;
import org.example.security.SecurityService;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthControllerV1 {

    private final UserService userService;
    private final SecurityService securityService;

    @PostMapping("/register")
    public Mono<UserDto> register(@RequestBody @Valid RegistrUserDto registrationRequestDto) {

        return userService.registerUser(registrationRequestDto);
    }

    @PostMapping("/login")
    public Mono<AuthResponseDto> login(@RequestBody @Valid AuthRequestDto authRequestDto) {
        return securityService.authenticate(authRequestDto.getUsername(), authRequestDto.getPassword())
                .flatMap(tokenDetails -> Mono.just(
                        AuthResponseDto.builder()
                                .userId(tokenDetails.getUserId())
                                .token(tokenDetails.getToken())
                                .issuedAt(tokenDetails.getIssuedAt())
                                .expiresAt(tokenDetails.getExpiresAt())
                                .build()
                ));

    }
}
