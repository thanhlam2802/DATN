package backend.backend.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "airlines")
public class Airline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;
} 