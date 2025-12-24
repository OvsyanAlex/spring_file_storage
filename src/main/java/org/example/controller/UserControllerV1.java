package org.example.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.UpdateUserRequestDto;
import org.example.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.example.service.UserService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserControllerV1 {

    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ_ALL') or " +
            "(hasAuthority('USER_READ_SELF') and #userId == authentication.principal.id)")
    public Mono<UserDto> getUser(@PathVariable("id") Integer userId) {

        return userService.findById(userId);
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('USER_READ_ALL')")
    public Flux<UserDto> findAll() {

        return userService.findAll();
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPLOAD_ALL')")
    public Mono<UserDto> updateUser(@PathVariable("id") Integer userId, @RequestBody @Valid UpdateUserRequestDto userDto) {

        userService.findById(userId);
        return userService.update(userDto, userId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE_ALL')")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable("id") Integer userId){

        return userService.delete(userId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
