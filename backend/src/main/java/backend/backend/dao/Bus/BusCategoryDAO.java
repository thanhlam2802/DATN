package backend.backend.dao.Bus;

import backend.backend.entity.BusCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusCategoryDAO extends JpaRepository<BusCategory, Integer> {

    Optional<BusCategory> findByName(String name);
    boolean existsByName(String name);
    
    // ✅ MULTI-TENANT: Kiểm tra tên trùng trong cùng owner
    @Query("SELECT bc FROM BusCategory bc WHERE bc.name = :name AND bc.owner.id = :ownerId")
    Optional<BusCategory> findByNameAndOwnerId(@Param("name") String name, @Param("ownerId") Integer ownerId);
    
    @Query("SELECT CASE WHEN COUNT(bc) > 0 THEN true ELSE false END FROM BusCategory bc WHERE bc.name = :name AND bc.owner.id = :ownerId")
    boolean existsByNameAndOwnerId(@Param("name") String name, @Param("ownerId") Integer ownerId);
    
    // ✅ RESTORED: BusCategory thuộc về owner
    @Query("SELECT bc FROM BusCategory bc WHERE bc.owner.id = :ownerId")
    List<BusCategory> findByOwnerId(@Param("ownerId") Integer ownerId);
}
