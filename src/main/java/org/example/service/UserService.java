package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.RegistrationUserDto;
import org.example.dto.UserDto;
import org.example.mapper.UserMapper;
import org.example.model.UserEntity;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.example.repository.UserRepository;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Mono<UserDto> findById(Integer userId) {
        return Mono.fromCallable(() -> userRepository.findById(userId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(userMapper::entityToUser)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<UserEntity> getUserByUsername(String username) {
        return Mono.fromCallable(() ->
                        userRepository.findByUserName(username)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                )
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<UserDto> registerUser(RegistrationUserDto registrationUserDto) {
        UserEntity userEntity = userMapper.registrationUserDto(registrationUserDto);

        return Mono.fromCallable(() -> userRepository.save(userEntity))
                .map(userMapper::entityToUser)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public void delete(Integer userId) {

    }

    public UserDto update(UserDto userDto) {
        return null;
    }

    public List<UserDto> getAll() {
        return null;
    }

}
