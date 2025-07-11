package backend.backend.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.backend.entity.Tag;
import backend.backend.entity.Tour;


public interface TagDAO extends JpaRepository<Tag, Long > {

	 Optional<Tag> findByName(String name);
    
}
