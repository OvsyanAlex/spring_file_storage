package org.example.service;

import lombok.RequiredArgsConstructor;

import org.example.dto.RegistrUserDto;
import org.example.dto.UpdateUserRequestDto;


import org.example.dto.UserDto;
import org.example.mapper.UserMapper;
import org.example.model.UserEntity;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.example.repository.UserRepository;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;



@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Mono<UserDto> findById(Integer userId) {

        return Mono.fromCallable(() -> userRepository.findByIdWithEvents(userId)
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


    public Mono<UserDto> registerUser(RegistrUserDto registrUserDto) {
        UserEntity userEntity = userMapper.registrationUserDto(registrUserDto);


        return Mono.fromCallable(() -> userRepository.save(userEntity))
                .map(userMapper::entityToUser)
                .subscribeOn(Schedulers.boundedElastic());
    }


    public Mono<Void> delete(Integer userId) {
        return findById(userId)
                .flatMap(userDto -> Mono.fromRunnable(() -> userRepository.deleteById(userId))
                        .subscribeOn(Schedulers.boundedElastic())).then();
    }

//    Subscriber создаётся автоматически WebFlux при HTTP-запросе
    public Mono<UserDto> update(UpdateUserRequestDto updateUserRequestDto, Integer userId) {

        // оборачиваем блокирующий userRepository.findByIdWithEvents в реактивный поток
        return Mono.fromCallable(() -> userRepository.findByIdWithEvents(userId).orElseThrow(() ->
                                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
                .subscribeOn(Schedulers.boundedElastic())
                // flatMap создаёт новый Publisher (Mono) для сохранения сущности
                .flatMap(userEntity -> {
                    userEntity.setUserName(updateUserRequestDto.getUsername());
                    userEntity.setStatus(updateUserRequestDto.getStatus());

                    return Mono.fromCallable(() -> userRepository.save(userEntity))
                            .subscribeOn(Schedulers.boundedElastic());
                }).map(userMapper::entityToUser);
    }

    public Flux<UserDto> findAll() {
        return Flux.fromIterable(
                        userRepository.findAll()
                )
                .map(userMapper::entityToUser)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
