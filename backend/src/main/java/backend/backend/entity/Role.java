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

    @Column(nullable = false, unique = true, length = 50)
    @Enumerated(EnumType.STRING)
    private UserRoleEnum name;

    @OneToMany(mappedBy = "role")
    private List<UserRole> userRoles;
}