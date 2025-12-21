package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.RegistrationUserDto;
import org.example.dto.UserDto;
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

    @PostMapping("/register")
    public Mono<UserDto> register(@RequestBody @Valid RegistrationUserDto registrationRequestDto) {
        // В WebFlux:
        //если тело запроса отсутствует → 400 Bad Request
        //если JSON некорректный → 400 Bad Request
        //если валидация @Valid не проходит → 400 Bad Request

        return userService.registerUser(registrationRequestDto);
    }
}
