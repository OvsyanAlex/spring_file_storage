package org.example.mapper;


import lombok.RequiredArgsConstructor;
import org.example.dto.Event;
import org.example.dto.RegistrUserDto;
import org.example.dto.UserDto;
import org.example.model.EventEntity;
import org.example.model.Status;
import org.example.model.UserEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;
    private final EventMapper eventMapper;

    public UserDto entityToUser(UserEntity userEntity) {
        UserDto userDto = null;

        if (userEntity != null) {

            userDto = new UserDto(
                    userEntity.getId(),
                    userEntity.getUserName(),
                    userEntity.getStatus(),
                    new ArrayList<>());
            if (userEntity.getEvents() != null) {
                List<Event> events = new ArrayList<>();
                for (EventEntity eventEntity : userEntity.getEvents()) {
                    events.add(eventMapper.entityToEvent(eventEntity));
                }
                userDto.setEvents(events);
            }
        }
        return userDto;
    }

    public UserEntity userToEntity(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userDto.getId());
        userEntity.setUserName(userDto.getUserName());
        userEntity.setStatus(userDto.getStatus());
        return userEntity;
    }

    public UserEntity registrationUserDto(RegistrUserDto registrUserDto) {
        return new UserEntity()
                .toBuilder()
                .userName(registrUserDto.getUsername())
                .role(registrUserDto.getRole())
                .password(passwordEncoder.encode(registrUserDto.getPassword()))
                .status(Status.ACTIVE)
                .build();
    }
}
