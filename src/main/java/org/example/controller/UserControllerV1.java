package org.example.controller;


import lombok.RequiredArgsConstructor;
import org.example.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserControllerV1 {

    private final UserService userService;

    @GetMapping("/{id}")
    public Mono<UserDto> getUser(@PathVariable("id") Integer userId) {

        return userService.findById(userId);

    }

//    @PostMapping
//    public ResponseEntity<UserDto> saveUser(@RequestBody @Valid UserDto userDto) {
//
//        if (userDto == null) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        Optional<UserDto> save = userService.save(userDto);
//        return new ResponseEntity<>(save.get(), HttpStatus.OK);
//    }
}
