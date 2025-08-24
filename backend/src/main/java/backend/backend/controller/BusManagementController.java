package backend.backend.controller;

import backend.backend.dto.BusManagementDTO;
import backend.backend.service.BusManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bus-management")
@RequiredArgsConstructor
public class BusManagementController {
    
    private final BusManagementService busManagementService;

    // Overview endpoints
    @GetMapping("/overview/kpis")
    public ResponseEntity<Object> getOverviewKPIs() {
        return ResponseEntity.ok(busManagementService.getOverviewKPIs());
    }

    @GetMapping("/overview/alerts")
    public ResponseEntity<List<BusManagementDTO.DataQualityAlert>> getDataQualityAlerts(
            @RequestParam(required = false) String query) {
        return ResponseEntity.ok(busManagementService.getDataQualityAlerts(query));
    }

    // Providers endpoints
    @GetMapping("/providers")
    public ResponseEntity<List<BusManagementDTO.ProviderSummary>> getProviders(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(busManagementService.getProviders(query, status));
    }

    @PostMapping("/providers")
    public ResponseEntity<BusManagementDTO.ProviderSummary> createProvider(
            @RequestBody BusManagementDTO.CreateProviderRequest request) {
        return ResponseEntity.ok(busManagementService.createProvider(request));
    }

    @PutMapping("/providers/{id}")
    public ResponseEntity<BusManagementDTO.ProviderSummary> updateProvider(
            @PathVariable Integer id,
            @RequestBody BusManagementDTO.UpdateProviderRequest request) {
        return ResponseEntity.ok(busManagementService.updateProvider(id, request));
    }

    @PutMapping("/providers/{id}/ban")
    public ResponseEntity<Void> banProvider(@PathVariable Integer id) {
        busManagementService.banProvider(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/providers/{id}/unban")
    public ResponseEntity<Void> unbanProvider(@PathVariable Integer id) {
        busManagementService.unbanProvider(id);
        return ResponseEntity.ok().build();
    }

    // Routes endpoints
    @GetMapping("/routes")
    public ResponseEntity<List<BusManagementDTO.RouteSummary>> getRoutes(
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(busManagementService.getRoutes(owner, origin, destination, status));
    }

    @PostMapping("/routes")
    public ResponseEntity<BusManagementDTO.RouteSummary> createRoute(
            @RequestBody BusManagementDTO.CreateRouteRequest request) {
        return ResponseEntity.ok(busManagementService.createRoute(request));
    }

    @PutMapping("/routes/{id}")
    public ResponseEntity<BusManagementDTO.RouteSummary> updateRoute(
            @PathVariable Integer id,
            @RequestBody BusManagementDTO.UpdateRouteRequest request) {
        return ResponseEntity.ok(busManagementService.updateRoute(id, request));
    }

    @PutMapping("/routes/{id}/submit-review")
    public ResponseEntity<Void> submitRouteReview(@PathVariable Integer id) {
        busManagementService.submitRouteReview(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/routes/{id}/approve")
    public ResponseEntity<Void> approveRoute(@PathVariable Integer id) {
        busManagementService.approveRoute(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/routes/{id}/reject")
    public ResponseEntity<Void> rejectRoute(@PathVariable Integer id) {
        busManagementService.rejectRoute(id);
        return ResponseEntity.ok().build();
    }

    // Slots endpoints
    @GetMapping("/slots")
    public ResponseEntity<List<BusManagementDTO.SlotSummary>> getSlots(
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) String route,
            @RequestParam(required = false) String date) {
        return ResponseEntity.ok(busManagementService.getSlots(owner, route, date));
    }

    @PostMapping("/slots/bulk-generate")
    public ResponseEntity<List<BusManagementDTO.SlotSummary>> bulkGenerateSlots(
            @RequestBody BusManagementDTO.BulkGenerateRequest request) {
        return ResponseEntity.ok(busManagementService.bulkGenerateSlots(request));
    }

    @GetMapping("/slots/{id}/detail")
    public ResponseEntity<BusManagementDTO.SlotDetail> getSlotDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(busManagementService.getSlotDetail(id));
    }

    // Moderation endpoints
    @GetMapping("/moderation/queue")
    public ResponseEntity<List<BusManagementDTO.ModerationItem>> getModerationQueue(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(busManagementService.getModerationQueue(type, status));
    }

    @PutMapping("/moderation/{id}/approve")
    public ResponseEntity<Void> approveModeration(@PathVariable Integer id) {
        busManagementService.approveModeration(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/moderation/{id}/reject")
    public ResponseEntity<Void> rejectModeration(@PathVariable Integer id) {
        busManagementService.rejectModeration(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/moderation/{id}/diff")
    public ResponseEntity<Object> getModerationDiff(@PathVariable Integer id) {
        return ResponseEntity.ok(busManagementService.getModerationDiff(id));
    }
}
