package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.Status;
import org.example.security.Role;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrUserDto {
    private String username;
    private String password;
    private Role role;
}
