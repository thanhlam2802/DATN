package backend.backend.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

public class SecurityUtil {

    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public static Integer getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> credentials = (Map<String, Object>) auth.getCredentials();
        return Integer.valueOf(String.valueOf(credentials.get("userId")));
    }
}
