package org.example.model;


import jakarta.persistence.*;
import lombok.*;
import org.example.dto.Event;
import org.example.security.Role;

import java.util.List;

@Setter
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="users", schema = "spring_file_storage")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "password")
    private String password;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventEntity> events;

    @ToString.Include(name = "password")
    private String maskPassword() {
        return "********";
    }
}
