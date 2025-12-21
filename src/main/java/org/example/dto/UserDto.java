package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.Status;
import org.example.security.Role;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;
    private String userName;
    private Status status;
    List<Event> events;

    public UserDto(String userName, Status status) {
        this.userName = userName;
        this.status = status;
    }
}
