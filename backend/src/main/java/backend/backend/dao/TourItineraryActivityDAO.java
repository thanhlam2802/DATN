package backend.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.backend.entity.TourItineraryActivity;

@Repository
public interface TourItineraryActivityDAO extends JpaRepository<TourItineraryActivity, Long> {
   
}