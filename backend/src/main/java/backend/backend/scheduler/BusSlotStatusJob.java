package backend.backend.scheduler;

import backend.backend.entity.enumBus.BusSlotStatus;
import backend.backend.service.busService.BusSlotService;
import backend.backend.controller.WebSocketController;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 🚌 Bus Slot Status Update Job
 * Job để tự động cập nhật status của bus slots
 */
@Component
@Slf4j
public class BusSlotStatusJob implements Job {

    @Autowired
    private BusSlotService busSlotService;
    
    @Autowired
    private WebSocketController webSocketController;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        
        Integer busSlotId = dataMap.getInt("busSlotId");
        String action = dataMap.getString("action");
        String busName = dataMap.getString("busName");
        String routeInfo = dataMap.getString("routeInfo");
        
        log.info("🕒 [QUARTZ] Executing {} for BusSlot ID: {} ({} - {})", 
                action, busSlotId, busName, routeInfo);
        
        try {
            switch (action) {
                case "START_TRIP":
                    handleStartTrip(busSlotId);
                    break;
                    
                case "CHECK_DELAY":
                    handleCheckDelay(busSlotId);
                    break;
                    
                case "COMPLETE_TRIP":
                    handleCompleteTrip(busSlotId);
                    break;
                    
                default:
                    log.warn("⚠️ [QUARTZ] Unknown action: {} for BusSlot ID: {}", action, busSlotId);
            }
        } catch (Exception e) {
            log.error("❌ [QUARTZ] Error executing {} for BusSlot ID: {}", action, busSlotId, e);
            throw new JobExecutionException(e);
        }
    }

    /**
     * Xử lý bắt đầu chuyến đi (SCHEDULED → IN_PROGRESS)
     */
    private void handleStartTrip(Integer busSlotId) {
        try {
            var busSlot = busSlotService.findBusSlotById(busSlotId);
            if (busSlot.isPresent() && busSlot.get().getStatus() == BusSlotStatus.SCHEDULED) {
                busSlotService.markInProgress(busSlotId);
                
                // ✅ WEBSOCKET: Gửi real-time update
                sendWebSocketUpdate(busSlotId, "IN_PROGRESS", "START_TRIP");
                
                log.info("✅ [QUARTZ] Started trip for BusSlot ID: {}", busSlotId);
            } else {
                log.warn("⚠️ [QUARTZ] Cannot start trip - BusSlot ID: {} not in SCHEDULED status", busSlotId);
            }
        } catch (Exception e) {
            log.error("❌ [QUARTZ] Failed to start trip for BusSlot ID: {}", busSlotId, e);
        }
    }

    /**
     * Kiểm tra và đánh dấu chuyến bị delay
     */
    private void handleCheckDelay(Integer busSlotId) {
        try {
            var busSlotOpt = busSlotService.findBusSlotById(busSlotId);
            if (busSlotOpt.isPresent()) {
                var busSlot = busSlotOpt.get();
                
                // Nếu vẫn còn SCHEDULED sau departure time → DELAYED
                if (busSlot.getStatus() == BusSlotStatus.SCHEDULED) {
                    log.warn("⚠️ [QUARTZ] BusSlot ID: {} is DELAYED - should be IN_PROGRESS by now", busSlotId);
                    
                    // TODO: Implement updateStatusWithReason method if needed
                    // For now, we'll just log the delay
                }
            }
        } catch (Exception e) {
            log.error("❌ [QUARTZ] Failed to check delay for BusSlot ID: {}", busSlotId, e);
        }
    }

    /**
     * Xử lý hoàn thành chuyến đi (IN_PROGRESS → COMPLETED)
     */
    private void handleCompleteTrip(Integer busSlotId) {
        try {
            var busSlot = busSlotService.findBusSlotById(busSlotId);
            if (busSlot.isPresent() && busSlot.get().getStatus() == BusSlotStatus.IN_PROGRESS) {
                busSlotService.markCompleted(busSlotId);
                
                // ✅ WEBSOCKET: Gửi real-time update
                sendWebSocketUpdate(busSlotId, "COMPLETED", "COMPLETE_TRIP");
                
                log.info("✅ [QUARTZ] Completed trip for BusSlot ID: {}", busSlotId);
            } else {
                log.warn("⚠️ [QUARTZ] Cannot complete trip - BusSlot ID: {} not in IN_PROGRESS status", busSlotId);
            }
        } catch (Exception e) {
            log.error("❌ [QUARTZ] Failed to complete trip for BusSlot ID: {}", busSlotId, e);
        }
    }

    /**
     * ✅ Helper method để gửi WebSocket updates
     */
    private void sendWebSocketUpdate(Integer busSlotId, String newStatus, String action) {
        try {
            var busSlotOpt = busSlotService.findBusSlotById(busSlotId);
            if (busSlotOpt.isPresent()) {
                var busSlot = busSlotOpt.get();
                Integer ownerId = busSlot.getOwnerId();
                String busName = busSlot.getBus() != null ? busSlot.getBus().getName() : "Unknown Bus";
                String routeInfo = getRouteInfo(busSlot);
                
                webSocketController.sendBusStatusUpdate(
                    busSlotId, ownerId, newStatus, busName, routeInfo, action
                );
                
                log.info("📡 [WEBSOCKET] Sent real-time update: {} for BusSlot ID: {}", action, busSlotId);
            }
        } catch (Exception e) {
            log.error("❌ [WEBSOCKET] Failed to send update for BusSlot ID: {}", busSlotId, e);
        }
    }

    /**
     * Helper method để lấy route info
     */
    private String getRouteInfo(Object busSlot) {
        try {
            // Safely extract route info using reflection or casting
            var busSlotResponse = (backend.backend.dto.BusDTO.BusSlotResponse) busSlot;
            if (busSlotResponse.getRoute() != null) {
                var route = busSlotResponse.getRoute();
                if (route.getOriginLocation() != null && route.getDestinationLocation() != null) {
                    return route.getOriginLocation().getName() + " → " + route.getDestinationLocation().getName();
                }
            }
        } catch (Exception e) {
            log.debug("Could not extract route info: {}", e.getMessage());
        }
        return "Unknown Route";
    }
}