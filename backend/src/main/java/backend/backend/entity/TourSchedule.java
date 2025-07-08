package backend.backend.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "tour_schedules")
public class TourSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    @JsonIgnore
    private Tour tour;

    @Column(name = "day_number")
    private int dayNumber;
    // Một "Ngày" (TourSchedule) có nhiều "Hoạt động" (TourItineraryActivity)
    @OneToMany(mappedBy = "tourSchedule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("activityTime ASC")
    private List<TourItineraryActivity> activities;

    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    @Lob
    @Column(nullable = false)
    private String activity;
}