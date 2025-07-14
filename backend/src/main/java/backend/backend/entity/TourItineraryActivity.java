// trong package backend.backend.entity
package backend.backend.entity;

import java.util.Optional;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tour_itinerary_activities") 
public class TourItineraryActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

  
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_schedule_id", nullable = false)
    private TourSchedule tourSchedule;

    @Column(name = "activity_time", length = 10)
    private String activityTime;

    @Column(name = "activity_title", nullable = false)
    private String activityTitle; 

    @Column(length = 1000)
    private String description; 

    @Column(length = 50)
    private String icon;

	
}