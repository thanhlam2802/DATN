package backend.backend.utils;

import backend.backend.dto.auth.UserRoleEnum;
import backend.backend.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SecurityUtils {

    /**
     * Lấy email của user hiện tại từ SecurityContext
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * Lấy user ID của user hiện tại từ SecurityContext
     */
    public static Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getCredentials() != null) {
            try {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> credentials = (java.util.Map<String, Object>) authentication.getCredentials();
                return (Integer) credentials.get("userId");
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Kiểm tra user hiện tại có role ADMIN_HOTELS không
     */
    public static boolean isHotelAdmin(User user) {
        if (user == null || user.getUserRoles() == null) {
            return false;
        }
        return user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRole() != null && 
                        userRole.getRole().getName() == UserRoleEnum.ADMIN_HOTELS);
    }

    /**
     * Kiểm tra user hiện tại có role SUPER_ADMIN không
     */
    public static boolean isSuperAdmin(User user) {
        if (user == null || user.getUserRoles() == null) {
            return false;
        }
        return user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRole() != null && 
                        userRole.getRole().getName() == UserRoleEnum.SUPER_ADMIN);
    }

    /**
     * Kiểm tra user hiện tại có role HOTEL_SUPPLIER không
     */
    public static boolean isHotelSupplier(User user) {
        if (user == null || user.getUserRoles() == null) {
            return false;
        }
        return user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRole() != null && 
                        userRole.getRole().getName() == UserRoleEnum.HOTEL_SUPPLIER);
    }

    /**
     * Kiểm tra user có quyền admin hotel (ADMIN_HOTELS, SUPER_ADMIN, hoặc HOTEL_SUPPLIER)
     */
    public static boolean hasHotelAdminAccess(User user) {
        return isHotelAdmin(user) || isSuperAdmin(user) || isHotelSupplier(user);
    }

    /**
     * Lấy danh sách roles của user
     */
    public static List<UserRoleEnum> getUserRoles(User user) {
        if (user == null || user.getUserRoles() == null) {
            return List.of();
        }
        return user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getName())
                .collect(Collectors.toList());
    }
} 