package backend.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(length = 100)
    private String email;

    @Column(name = "fullName", length = 100)
    private String fullName;

    @Column(length = 10)
    private Boolean gender;

    @Column(length = 20)
    private String passport;

    @Column(length = 20)
    private String phone;

} 