package backend.backend.repository;

import backend.backend.entity.BusCategory;
import backend.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusCategoryRepository extends JpaRepository<BusCategory, Integer> {
    
    List<BusCategory> findByOwner(User owner);
    
    @Query("SELECT bc FROM BusCategory bc WHERE bc.owner.id = :ownerId")
    List<BusCategory> findByOwnerId(@Param("ownerId") Integer ownerId);
    
    List<BusCategory> findByName(String name);
    
    @Query("SELECT bc FROM BusCategory bc WHERE bc.name LIKE %:name%")
    List<BusCategory> findByNameContaining(@Param("name") String name);
}
