package backend.backend.service;

import backend.backend.entity.BusSlot;
import backend.backend.scheduler.BusSlotStatusJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 📅 Bus Slot Scheduler Service
 * Service để lập lịch tự động cho các bus slots
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BusSlotSchedulerService {

    private final Scheduler scheduler;

    /**
     * Lập lịch tự động cho một bus slot
     * @param busSlot BusSlot cần lập lịch
     */
    public void scheduleBusSlot(BusSlot busSlot) {
        try {
            // 1. Schedule START_TRIP (đúng departure time)
            scheduleStartTrip(busSlot);
            
            // 2. Schedule CHECK_DELAY (5 phút sau departure time)
            scheduleDelayCheck(busSlot);
            
            // 3. Schedule COMPLETE_TRIP (đúng arrival time)
            scheduleCompleteTrip(busSlot);
            
            log.info("✅ [SCHEDULER] Scheduled all jobs for BusSlot ID: {} ({} → {})", 
                    busSlot.getId(), 
                    busSlot.getDepartureTime(), 
                    busSlot.getArrivalTime());
                    
        } catch (SchedulerException e) {
            log.error("❌ [SCHEDULER] Failed to schedule BusSlot ID: {}", busSlot.getId(), e);
        }
    }

    /**
     * Hủy lịch cho một bus slot
     */
    public void unscheduleBusSlot(Integer busSlotId) {
        try {
            // Xóa tất cả jobs liên quan đến bus slot này
            JobKey startJobKey = JobKey.jobKey("start-" + busSlotId, "busSlots");
            JobKey delayJobKey = JobKey.jobKey("delay-" + busSlotId, "busSlots");
            JobKey completeJobKey = JobKey.jobKey("complete-" + busSlotId, "busSlots");
            
            scheduler.deleteJob(startJobKey);
            scheduler.deleteJob(delayJobKey);
            scheduler.deleteJob(completeJobKey);
            
            log.info("✅ [SCHEDULER] Unscheduled all jobs for BusSlot ID: {}", busSlotId);
            
        } catch (SchedulerException e) {
            log.error("❌ [SCHEDULER] Failed to unschedule BusSlot ID: {}", busSlotId, e);
        }
    }

    /**
     * Cập nhật lịch cho bus slot (unschedule cũ + schedule mới)
     */
    public void updateScheduleBusSlot(BusSlot busSlot) {
        // Hủy lịch cũ
        unscheduleBusSlot(busSlot.getId());
        
        // Tạo lịch mới
        scheduleBusSlot(busSlot);
    }

    /**
     * Schedule job để start trip đúng departure time
     */
    private void scheduleStartTrip(BusSlot busSlot) throws SchedulerException {
        LocalDateTime departureDateTime = LocalDateTime.of(
            busSlot.getSlotDate(), 
            busSlot.getDepartureTime()
        );
        
        JobDetail job = JobBuilder.newJob(BusSlotStatusJob.class)
            .withIdentity("start-" + busSlot.getId(), "busSlots")
            .usingJobData("busSlotId", busSlot.getId())
            .usingJobData("action", "START_TRIP")
            .usingJobData("busName", busSlot.getBus().getName())
            .usingJobData("routeInfo", getRouteInfo(busSlot))
            .build();

        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("start-trigger-" + busSlot.getId(), "busSlots")
            .startAt(Date.from(departureDateTime.atZone(ZoneId.systemDefault()).toInstant()))
            .build();

        scheduler.scheduleJob(job, trigger);
        
        log.info("📅 [SCHEDULER] Scheduled START_TRIP for BusSlot ID: {} at {}", 
                busSlot.getId(), departureDateTime);
    }

    /**
     * Schedule job để check delay (5 phút sau departure time)
     */
    private void scheduleDelayCheck(BusSlot busSlot) throws SchedulerException {
        LocalDateTime delayCheckTime = LocalDateTime.of(
            busSlot.getSlotDate(), 
            busSlot.getDepartureTime()
        ).plusMinutes(5); // 5 phút sau departure time
        
        JobDetail job = JobBuilder.newJob(BusSlotStatusJob.class)
            .withIdentity("delay-" + busSlot.getId(), "busSlots")
            .usingJobData("busSlotId", busSlot.getId())
            .usingJobData("action", "CHECK_DELAY")
            .usingJobData("busName", busSlot.getBus().getName())
            .usingJobData("routeInfo", getRouteInfo(busSlot))
            .build();

        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("delay-trigger-" + busSlot.getId(), "busSlots")
            .startAt(Date.from(delayCheckTime.atZone(ZoneId.systemDefault()).toInstant()))
            .build();

        scheduler.scheduleJob(job, trigger);
        
        log.info("📅 [SCHEDULER] Scheduled CHECK_DELAY for BusSlot ID: {} at {}", 
                busSlot.getId(), delayCheckTime);
    }

    /**
     * Schedule job để complete trip đúng arrival time
     */
    private void scheduleCompleteTrip(BusSlot busSlot) throws SchedulerException {
        LocalDateTime arrivalDateTime = LocalDateTime.of(
            busSlot.getSlotDate(), 
            busSlot.getArrivalTime()
        );
        
        JobDetail job = JobBuilder.newJob(BusSlotStatusJob.class)
            .withIdentity("complete-" + busSlot.getId(), "busSlots")
            .usingJobData("busSlotId", busSlot.getId())
            .usingJobData("action", "COMPLETE_TRIP")
            .usingJobData("busName", busSlot.getBus().getName())
            .usingJobData("routeInfo", getRouteInfo(busSlot))
            .build();

        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("complete-trigger-" + busSlot.getId(), "busSlots")
            .startAt(Date.from(arrivalDateTime.atZone(ZoneId.systemDefault()).toInstant()))
            .build();

        scheduler.scheduleJob(job, trigger);
        
        log.info("📅 [SCHEDULER] Scheduled COMPLETE_TRIP for BusSlot ID: {} at {}", 
                busSlot.getId(), arrivalDateTime);
    }

    /**
     * Helper method để lấy route info
     */
    private String getRouteInfo(BusSlot busSlot) {
        var route = busSlot.getRoute();
        return route.getOriginLocation().getName() + " → " + route.getDestinationLocation().getName();
    }
}