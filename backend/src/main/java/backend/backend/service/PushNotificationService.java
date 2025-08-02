package backend.backend.service;

import backend.backend.dto.PushSubscriptionDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PushNotificationService {
    
    ResponseEntity<?> saveSubscription(PushSubscriptionDto dto);
    
    ResponseEntity<?> removeSubscription(PushSubscriptionDto dto);
    
    ResponseEntity<?> testNotification(Long userId);
    
    void sendNotificationToAll(String title, String body, String icon, String url);
    
    void sendNotificationToUser(Long userId, String title, String body, String icon, String url);
    
    void sendNotificationToUsers(List<Long> userIds, String title, String body, String icon, String url);
    
    void sendNotificationToAllAdmins(String title, String body, String icon, String url);
    
    void sendNotificationToHotelAdmins(String title, String body, String icon, String url);
} 