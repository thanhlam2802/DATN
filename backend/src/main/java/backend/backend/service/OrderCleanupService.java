package backend.backend.service;

public interface OrderCleanupService {
	 public void cancelExpiredOrders();
	 
	 // ✅ NEW: Handle expired bus bookings independently
	 public void handleExpiredBusBookings();
}
