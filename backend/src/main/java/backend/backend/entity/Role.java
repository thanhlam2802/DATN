package backend.backend.entity;


import backend.backend.dto.auth.UserRoleEnum;
import lombok.Data;
import jakarta.persistence.*;

import java.util.List;

@Data
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private UserRoleEnum name;

    @OneToMany(mappedBy = "role")
    private List<UserRole> userRoles;
}