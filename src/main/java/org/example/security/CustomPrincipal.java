package org.example.security;

import lombok.*;

import java.security.Principal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomPrincipal implements Principal {
    private Integer id;
    private String name;
}
