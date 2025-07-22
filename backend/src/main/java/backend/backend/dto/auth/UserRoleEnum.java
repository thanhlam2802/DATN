package backend.backend.dto.auth;

import java.util.HashSet;
import java.util.Set;

public enum UserRoleEnum {
    USER,
    SUPER_ADMIN,
    ADMIN_BUSES,
    ADMIN_FLIGHTS,
    ADMIN_HOTELS,
    ADMIN_TOURS,
    HOTEL_SUPPLIER,
    FLIGHT_SUPPLIER,
    TOUR_SUPPLIER,
    BUS_SUPPLIER;


    public static Set<UserRoleEnum> registrableUserRoles() {
        Set<UserRoleEnum> roles = new HashSet<>();
        roles.add(USER);
        roles.addAll(needVerifyRoles());
        return roles;
    }

    public static Set<UserRoleEnum> needVerifyRoles() {
        return Set.of(HOTEL_SUPPLIER, FLIGHT_SUPPLIER, TOUR_SUPPLIER, BUS_SUPPLIER);
    }
}
