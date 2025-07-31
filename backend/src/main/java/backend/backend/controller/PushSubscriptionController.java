package backend.backend.controller;

import backend.backend.dto.PushSubscriptionDto;
import backend.backend.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/push-subscriptions")
@RequiredArgsConstructor
public class PushSubscriptionController {

    private final PushNotificationService pushNotificationService;

    @PostMapping
    public ResponseEntity<?> saveSubscription(@RequestBody PushSubscriptionDto dto, Authentication authentication) {
        log.info("Save subscription request from user: {}", authentication.getName());
        return pushNotificationService.saveSubscription(dto);
    }

    @DeleteMapping
    public ResponseEntity<?> removeSubscription(@RequestBody PushSubscriptionDto dto, Authentication authentication) {
        log.info("Remove subscription request from user: {}", authentication.getName());
        return pushNotificationService.removeSubscription(dto);
    }

    @PostMapping("/test")
    public ResponseEntity<?> testNotification(Authentication authentication) {
        log.info("Test notification request from user: {}", authentication.getName());

        Long userId = Long.valueOf(authentication.getName());
        return pushNotificationService.testNotification(userId);
    }

    @GetMapping("/status")
    public ResponseEntity<?> getSubscriptionStatus(Authentication authentication) {
        log.info("Get subscription status request from user: {}", authentication.getName());
        return ResponseEntity.ok().body("Push notifications are available");
    }
}