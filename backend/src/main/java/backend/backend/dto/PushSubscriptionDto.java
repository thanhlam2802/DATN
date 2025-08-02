package backend.backend.dto;

import lombok.Data;
import java.util.Map;

@Data
public class PushSubscriptionDto {
    private Long userId;
    private Map<String, Object> subscription;

    public String getEndpoint() {
        return subscription != null ? (String) subscription.get("endpoint") : null;
    }

    @SuppressWarnings("unchecked")
    public String getP256dh() {
        if (subscription != null && subscription.get("keys") instanceof Map) {
            Map<String, String> keys = (Map<String, String>) subscription.get("keys");
            return keys.get("p256dh");
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public String getAuth() {
        if (subscription != null && subscription.get("keys") instanceof Map) {
            Map<String, String> keys = (Map<String, String>) subscription.get("keys");
            return keys.get("auth");
        }
        return null;
    }
}