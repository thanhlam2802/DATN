package backend.backend.service.implement;

import backend.backend.dto.PushSubscriptionDto;
import backend.backend.entity.PushSubscription;
import backend.backend.entity.User;
import backend.backend.repository.PushSubscriptionRepository;
import backend.backend.repository.UserRepository;
import backend.backend.service.PushNotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationServiceImpl implements PushNotificationService {

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Value("${vapid.public.key}")
    private String vapidPublicKey;

    @Value("${vapid.private.key}")
    private String vapidPrivateKey;

    @Value("${vapid.subject}")
    private String vapidSubject;

    private PushService pushService;

    @Override
    public ResponseEntity<?> saveSubscription(PushSubscriptionDto dto) {
        try {
            log.info("Saving push subscription for user: {}", dto.getUserId());

            Optional<User> userOpt = userRepository.findById(dto.getUserId().intValue());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }

            Optional<PushSubscription> existingSubscription = pushSubscriptionRepository
                .findByUserIdAndEndpoint(dto.getUserId(), dto.getEndpoint());

            if (existingSubscription.isPresent()) {
                PushSubscription subscription = existingSubscription.get();
                subscription.setP256dh(dto.getP256dh());
                subscription.setAuth(dto.getAuth());
                subscription.setIsActive(true);
                pushSubscriptionRepository.save(subscription);
                log.info("Updated existing push subscription for user: {}", dto.getUserId());
            } else {
                PushSubscription subscription = new PushSubscription();
                subscription.setUser(userOpt.get());
                subscription.setEndpoint(dto.getEndpoint());
                subscription.setP256dh(dto.getP256dh());
                subscription.setAuth(dto.getAuth());
                subscription.setIsActive(true);
                pushSubscriptionRepository.save(subscription);
                log.info("Created new push subscription for user: {}", dto.getUserId());
            }

            return ResponseEntity.ok().body("Subscription saved successfully");

        } catch (Exception e) {
            log.error("Error saving push subscription for user: {}", dto.getUserId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error saving subscription: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> removeSubscription(PushSubscriptionDto dto) {
        try {
            log.info("Removing push subscription for user: {}", dto.getUserId());
            pushSubscriptionRepository.deleteByUserIdAndEndpoint(dto.getUserId(), dto.getEndpoint());
            log.info("Removed push subscription for user: {}", dto.getUserId());
            return ResponseEntity.ok().body("Subscription removed successfully");
        } catch (Exception e) {
            log.error("Error removing push subscription for user: {}", dto.getUserId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error removing subscription: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> testNotification(Long userId) {
        try {
            log.info("Sending test notification to user: {}", userId);
            
            sendNotificationToUser(userId, 
                "Test Notification", 
                "Đây là thông báo test từ hệ thống", 
                "/favicon.ico",
                "/admin/hotel/dashboard");
            
            return ResponseEntity.ok().body("Test notification sent successfully");

        } catch (Exception e) {
            log.error("Error sending test notification to user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error sending test notification: " + e.getMessage());
        }
    }

    @Override
    public void sendNotificationToAll(String title, String body, String icon, String url) {
        try {
            log.info("Sending notification to all users: {}", title);
            
            List<PushSubscription> subscriptions = pushSubscriptionRepository.findAllActive();
            
            for (PushSubscription subscription : subscriptions) {
                sendNotificationToSubscription(subscription, title, body, icon, url);
            }
            
        } catch (Exception e) {
            log.error("Error sending notification to all users", e);
        }
    }

    @Override
    public void sendNotificationToUser(Long userId, String title, String body, String icon, String url) {
        try {
            log.info("Sending notification to user {}: {}", userId, title);
            
            List<PushSubscription> subscriptions = pushSubscriptionRepository.findByUserIdAndIsActiveTrue(userId);
            
            for (PushSubscription subscription : subscriptions) {
                sendNotificationToSubscription(subscription, title, body, icon, url);
            }
            
        } catch (Exception e) {
            log.error("Error sending notification to user: {}", userId, e);
        }
    }

    @Override
    public void sendNotificationToUsers(List<Long> userIds, String title, String body, String icon, String url) {
        try {
            log.info("Sending notification to {} users: {}", userIds.size(), title);
            
            for (Long userId : userIds) {
                sendNotificationToUser(userId, title, body, icon, url);
            }
            
        } catch (Exception e) {
            log.error("Error sending notification to users", e);
        }
    }

    @Override
    public void sendNotificationToAllAdmins(String title, String body, String icon, String url) {
        try {
            log.info("Sending notification to all admin users: {}", title);
            
            List<PushSubscription> adminSubscriptions = pushSubscriptionRepository.findAllActiveAdminSubscriptions();
            
            for (PushSubscription subscription : adminSubscriptions) {
                sendNotificationToSubscription(subscription, title, body, icon, url);
            }
            
        } catch (Exception e) {
            log.error("Error sending notification to all admin users", e);
        }
    }

    @Override
    public void sendNotificationToHotelAdmins(String title, String body, String icon, String url) {
        try {
            log.info("Sending notification to hotel admin users: {}", title);
            
            List<PushSubscription> hotelAdminSubscriptions = pushSubscriptionRepository.findAllActiveHotelAdminSubscriptions();
            
            for (PushSubscription subscription : hotelAdminSubscriptions) {
                sendNotificationToSubscription(subscription, title, body, icon, url);
            }
            
        } catch (Exception e) {
            log.error("Error sending notification to hotel admin users", e);
        }
    }

    private void sendNotificationToSubscription(PushSubscription subscription, String title, String body, String icon, String url) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("title", title);
            payload.put("body", body);
            payload.put("icon", icon);
            payload.put("url", url);
            payload.put("timestamp", System.currentTimeMillis());

            String payloadJson = objectMapper.writeValueAsString(payload);

            Subscription.Keys keys = new Subscription.Keys(subscription.getP256dh(), subscription.getAuth());
            Subscription webPushSubscription = new Subscription(subscription.getEndpoint(), keys);

            Notification notification = new Notification(webPushSubscription, payloadJson);

            getPushService().send(notification);
            
            log.debug("Notification sent successfully to endpoint: {}", subscription.getEndpoint());

        } catch (Exception e) {
            log.error("Error sending notification to subscription: {}", subscription.getEndpoint(), e);
            
            if (e.getMessage().contains("410") || e.getMessage().contains("404")) {
                subscription.setIsActive(false);
                pushSubscriptionRepository.save(subscription);
                log.info("Marked subscription as inactive due to error: {}", subscription.getEndpoint());
            }
        }
    }

    private PushService getPushService() throws GeneralSecurityException {
        if (pushService == null) {
            pushService = new PushService(vapidPublicKey, vapidPrivateKey, vapidSubject);
        }
        return pushService;
    }
} 