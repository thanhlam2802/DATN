export const getRedirectPath = (user) => {
  if (!user || !user.roles) {
    return "/";
  }

  const roles = user.roles;

  if (roles.includes("ADMIN_HOTELS") || roles.includes("HOTEL_SUPPLIER")) {
    return "/hotel/admin/dashboard";
  }

  if (roles.includes("ADMIN_FLIGHTS") || roles.includes("FLIGHT_SUPPLIER")) {
    return "/plane/admin";
  }

  if (roles.includes("ADMIN_TOURS") || roles.includes("TOUR_SUPPLIER")) {
    return "/tour/admin";
  }

  if (roles.includes("ADMIN_BUSES") || roles.includes("BUS_SUPPLIER")) {
    return "/bus-management/bus";
  }

  if (roles.includes("SUPER_ADMIN")) {
    return "/admin/dashboard";
  }

  return "/";
};
