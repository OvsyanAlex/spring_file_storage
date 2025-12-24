package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.security.Role;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationUserDto {
    private String userName;
    private String password;
    private Role role;
}
