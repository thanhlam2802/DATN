package backend.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "provinces")
public class Province {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(name = "image_url", length = 300)
    private String imageUrl;
    @JsonIgnore 
    @OneToMany(mappedBy = "province")
    private List<Hotel> hotels;

}
