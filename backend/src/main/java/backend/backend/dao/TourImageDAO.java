package backend.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.backend.entity.TourImage;
import backend.backend.entity.TourImageId;

@Repository
public interface TourImageDAO extends JpaRepository<TourImage, TourImageId> {
   
}