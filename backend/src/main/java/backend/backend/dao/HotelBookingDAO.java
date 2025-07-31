package backend.backend.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backend.backend.entity.HotelBooking;
import backend.backend.dto.CustomerDto;
import backend.backend.dto.HotelBookingDto;


public interface HotelBookingDAO extends JpaRepository<HotelBooking, Integer> {

	List<HotelBooking> findByOrderId(Integer id);
	boolean existsByRoomVariantId(Integer roomVariantId);

    @Query("SELECT b FROM HotelBooking b " +
           "JOIN FETCH b.roomVariant rv " +
           "JOIN FETCH rv.room r " +
           "JOIN FETCH r.hotel h " +
           "LEFT JOIN FETCH b.customer c " +
           "WHERE h.id = :hotelId")
    List<HotelBooking> findAllByHotelIdWithCustomer(@Param("hotelId") Integer hotelId);

    @Query("SELECT new backend.backend.dto.CustomerDto(" +
           "c.id, c.fullName, c.gender, c.dob, c.passport, c.email, c.phone, COUNT(b.id), MAX(b.createdAt)) " +
           "FROM HotelBooking b " +
           "LEFT JOIN b.customer c " +
           "WHERE c.id IS NOT NULL " +
           "GROUP BY c.id, c.fullName, c.gender, c.dob, c.passport, c.email, c.phone")
    List<CustomerDto> findAllHotelCustomers();

    @Query("SELECT new backend.backend.dto.CustomerDto(" +
           "c.id, c.fullName, c.gender, c.dob, c.passport, c.email, c.phone, COUNT(b.id), MAX(b.createdAt)) " +
           "FROM HotelBooking b " +
           "JOIN b.roomVariant rv " +
           "JOIN rv.room r " +
           "JOIN r.hotel h " +
           "LEFT JOIN b.customer c " +
           "WHERE h.id = :hotelId AND c.id IS NOT NULL " +
           "GROUP BY c.id, c.fullName, c.gender, c.dob, c.passport, c.email, c.phone")
    List<CustomerDto> findAllCustomersByHotelId(@Param("hotelId") Integer hotelId);

    @Query("SELECT new map(rv.id as variantId, rv.variantName as variantName, r.roomType as roomType, h.name as hotelName, COUNT(b.id) as bookingCount) " +
           "FROM HotelBooking b " +
           "JOIN b.roomVariant rv " +
           "JOIN rv.room r " +
           "JOIN r.hotel h " +
           "WHERE b.customer.id = :customerId " +
           "GROUP BY rv.id, rv.variantName, r.roomType, h.name")
    List<Map<String, Object>> findBookedRoomsByCustomerId(@Param("customerId") Integer customerId);

    @Query("SELECT new map(" +
           "b.createdAt as createdAt, " +
           "h.name as hotelName, " +
           "r.roomType as roomType, " +
           "rv.variantName as variantName" +
           ") " +
           "FROM HotelBooking b " +
           "JOIN b.roomVariant rv " +
           "JOIN rv.room r " +
           "JOIN r.hotel h " +
           "WHERE b.customer.id = :customerId " +
           "ORDER BY b.createdAt DESC")
    List<Map<String, Object>> findAllBookingsByCustomerId(@Param("customerId") Integer customerId);

    @Query("SELECT new backend.backend.dto.HotelBookingDto(" +
           "b.id, c.fullName, h.name, b.createdAt, b.order.status, b.totalPrice) " +
           "FROM HotelBooking b " +
           "JOIN b.roomVariant rv " +
           "JOIN rv.room r " +
           "JOIN r.hotel h " +
           "LEFT JOIN b.customer c")
    List<HotelBookingDto> findAllHotelBookings();

    @Query("SELECT COUNT(b) FROM HotelBooking b")
    Long countTotalBookings();

    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM HotelBooking b")
    java.math.BigDecimal sumTotalRevenue();

    @Query("SELECT COUNT(DISTINCT b.customer.id) FROM HotelBooking b WHERE b.customer.id IS NOT NULL")
    Long countTotalCustomers();

    @Query("SELECT COUNT(r.id) FROM Review r WHERE r.entityType = 'Hotel'")
    Long countTotalHotelReviews();

    @Query(value = "SELECT COUNT(*) FROM hotel_bookings WHERE CAST(created_at AS DATE) = CAST(GETDATE() AS DATE)", nativeQuery = true)
    Long countTotalBookingsToday();

    @Query(value = "SELECT COUNT(*) FROM hotel_bookings WHERE CAST(created_at AS DATE) = CAST(DATEADD(DAY, -1, GETDATE()) AS DATE)", nativeQuery = true)
    Long countTotalBookingsYesterday();

    @Query(value = "SELECT COUNT(*) FROM hotel_bookings WHERE CAST(created_at AS DATE) >= CAST(DATEADD(DAY, -7, GETDATE()) AS DATE)", nativeQuery = true)
    Long countTotalBookingsLast7Days();

    @Query(value = "SELECT COUNT(*) FROM hotel_bookings WHERE YEAR(created_at) = YEAR(GETDATE()) AND MONTH(created_at) = MONTH(GETDATE())", nativeQuery = true)
    Long countTotalBookingsThisMonth();

    @Query(value = "SELECT COUNT(*) FROM hotel_bookings WHERE YEAR(created_at) = YEAR(DATEADD(MONTH, -1, GETDATE())) AND MONTH(created_at) = MONTH(DATEADD(MONTH, -1, GETDATE()))", nativeQuery = true)
    Long countTotalBookingsLastMonth();

    @Query(value = "SELECT COUNT(*) FROM hotel_bookings WHERE CAST(created_at AS DATE) = CAST(DATEADD(DAY, -2, GETDATE()) AS DATE)", nativeQuery = true)
    Long countTotalBookings2DaysAgo();

    @Query(value = "SELECT COUNT(*) FROM hotel_bookings WHERE CAST(created_at AS DATE) >= CAST(DATEADD(DAY, -14, GETDATE()) AS DATE) AND CAST(created_at AS DATE) < CAST(DATEADD(DAY, -7, GETDATE()) AS DATE)", nativeQuery = true)
    Long countTotalBookingsPrevious7Days();

    @Query(value = "SELECT COUNT(*) FROM hotel_bookings WHERE YEAR(created_at) = YEAR(DATEADD(MONTH, -2, GETDATE())) AND MONTH(created_at) = MONTH(DATEADD(MONTH, -2, GETDATE()))", nativeQuery = true)
    Long countTotalBookings2MonthsAgo();

    @Query(value = "SELECT COUNT(*) FROM hotel_bookings b LEFT JOIN orders o ON b.order_id = o.id WHERE CAST(b.created_at AS DATE) = CAST(GETDATE() AS DATE) AND o.status = 'PAID'", nativeQuery = true)
    Long countPaidBookingsToday();

    @Query(value = "SELECT COUNT(*) FROM hotel_bookings b LEFT JOIN orders o ON b.order_id = o.id WHERE CAST(b.created_at AS DATE) = CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AND o.status = 'PAID'", nativeQuery = true)
    Long countPaidBookingsYesterday();

    @Query(value = "SELECT COUNT(*) FROM hotel_bookings b LEFT JOIN orders o ON b.order_id = o.id WHERE CAST(b.created_at AS DATE) >= CAST(DATEADD(DAY, -7, GETDATE()) AS DATE) AND o.status = 'PAID'", nativeQuery = true)
    Long countPaidBookingsLast7Days();

    @Query(value = "SELECT COUNT(*) FROM hotel_bookings b LEFT JOIN orders o ON b.order_id = o.id WHERE YEAR(b.created_at) = YEAR(GETDATE()) AND MONTH(b.created_at) = MONTH(GETDATE()) AND o.status = 'PAID'", nativeQuery = true)
    Long countPaidBookingsThisMonth();

    @Query(value = "SELECT COUNT(*) FROM hotel_bookings b LEFT JOIN orders o ON b.order_id = o.id WHERE YEAR(b.created_at) = YEAR(DATEADD(MONTH, -1, GETDATE())) AND MONTH(b.created_at) = MONTH(DATEADD(MONTH, -1, GETDATE())) AND o.status = 'PAID'", nativeQuery = true)
    Long countPaidBookingsLastMonth();

    @Query(value = "SELECT COUNT(*) FROM hotel_bookings b LEFT JOIN orders o ON b.order_id = o.id WHERE CAST(b.created_at AS DATE) = CAST(DATEADD(DAY, -2, GETDATE()) AS DATE) AND o.status = 'PAID'", nativeQuery = true)
    Long countPaidBookings2DaysAgo();

    @Query(value = "SELECT COUNT(*) FROM hotel_bookings b LEFT JOIN orders o ON b.order_id = o.id WHERE CAST(b.created_at AS DATE) >= CAST(DATEADD(DAY, -14, GETDATE()) AS DATE) AND CAST(b.created_at AS DATE) < CAST(DATEADD(DAY, -7, GETDATE()) AS DATE) AND o.status = 'PAID'", nativeQuery = true)
    Long countPaidBookingsPrevious7Days();

    @Query(value = "SELECT COUNT(*) FROM hotel_bookings b LEFT JOIN orders o ON b.order_id = o.id WHERE YEAR(b.created_at) = YEAR(DATEADD(MONTH, -2, GETDATE())) AND MONTH(b.created_at) = MONTH(DATEADD(MONTH, -2, GETDATE())) AND o.status = 'PAID'", nativeQuery = true)
    Long countPaidBookings2MonthsAgo();

    @Query(value = "SELECT COALESCE(SUM(b.total_price), 0) FROM hotel_bookings b LEFT JOIN orders o ON b.order_id = o.id WHERE CAST(b.created_at AS DATE) = CAST(GETDATE() AS DATE) AND o.status = 'PAID'", nativeQuery = true)
    java.math.BigDecimal sumTotalRevenueToday();

    @Query(value = "SELECT COALESCE(SUM(b.total_price), 0) FROM hotel_bookings b LEFT JOIN orders o ON b.order_id = o.id WHERE CAST(b.created_at AS DATE) = CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AND o.status = 'PAID'", nativeQuery = true)
    java.math.BigDecimal sumTotalRevenueYesterday();

    @Query(value = "SELECT COALESCE(SUM(b.total_price), 0) FROM hotel_bookings b LEFT JOIN orders o ON b.order_id = o.id WHERE CAST(b.created_at AS DATE) >= CAST(DATEADD(DAY, -7, GETDATE()) AS DATE) AND o.status = 'PAID'", nativeQuery = true)
    java.math.BigDecimal sumTotalRevenueLast7Days();

    @Query(value = "SELECT COALESCE(SUM(b.total_price), 0) FROM hotel_bookings b LEFT JOIN orders o ON b.order_id = o.id WHERE YEAR(b.created_at) = YEAR(GETDATE()) AND MONTH(b.created_at) = MONTH(GETDATE()) AND o.status = 'PAID'", nativeQuery = true)
    java.math.BigDecimal sumTotalRevenueThisMonth();

    @Query(value = "SELECT COALESCE(SUM(b.total_price), 0) FROM hotel_bookings b LEFT JOIN orders o ON b.order_id = o.id WHERE YEAR(b.created_at) = YEAR(DATEADD(MONTH, -1, GETDATE())) AND MONTH(b.created_at) = MONTH(DATEADD(MONTH, -1, GETDATE())) AND o.status = 'PAID'", nativeQuery = true)
    java.math.BigDecimal sumTotalRevenueLastMonth();

    @Query(value = "SELECT COALESCE(SUM(b.total_price), 0) FROM hotel_bookings b LEFT JOIN orders o ON b.order_id = o.id WHERE CAST(b.created_at AS DATE) = CAST(DATEADD(DAY, -2, GETDATE()) AS DATE) AND o.status = 'PAID'", nativeQuery = true)
    java.math.BigDecimal sumTotalRevenue2DaysAgo();

    @Query(value = "SELECT COALESCE(SUM(b.total_price), 0) FROM hotel_bookings b LEFT JOIN orders o ON b.order_id = o.id WHERE CAST(b.created_at AS DATE) >= CAST(DATEADD(DAY, -14, GETDATE()) AS DATE) AND CAST(b.created_at AS DATE) < CAST(DATEADD(DAY, -7, GETDATE()) AS DATE) AND o.status = 'PAID'", nativeQuery = true)
    java.math.BigDecimal sumTotalRevenuePrevious7Days();

    @Query(value = "SELECT COALESCE(SUM(b.total_price), 0) FROM hotel_bookings b LEFT JOIN orders o ON b.order_id = o.id WHERE YEAR(b.created_at) = YEAR(DATEADD(MONTH, -2, GETDATE())) AND MONTH(b.created_at) = MONTH(DATEADD(MONTH, -2, GETDATE())) AND o.status = 'PAID'", nativeQuery = true)
    java.math.BigDecimal sumTotalRevenue2MonthsAgo();

    @Query(value = "SELECT COUNT(DISTINCT customer_id) FROM hotel_bookings WHERE customer_id IS NOT NULL AND CAST(created_at AS DATE) = CAST(GETDATE() AS DATE)", nativeQuery = true)
    Long countTotalCustomersToday();

    @Query(value = "SELECT COUNT(DISTINCT customer_id) FROM hotel_bookings WHERE customer_id IS NOT NULL AND CAST(created_at AS DATE) = CAST(DATEADD(DAY, -1, GETDATE()) AS DATE)", nativeQuery = true)
    Long countTotalCustomersYesterday();

    @Query(value = "SELECT COUNT(DISTINCT customer_id) FROM hotel_bookings WHERE customer_id IS NOT NULL AND CAST(created_at AS DATE) >= CAST(DATEADD(DAY, -7, GETDATE()) AS DATE)", nativeQuery = true)
    Long countTotalCustomersLast7Days();

    @Query(value = "SELECT COUNT(DISTINCT customer_id) FROM hotel_bookings WHERE customer_id IS NOT NULL AND YEAR(created_at) = YEAR(GETDATE()) AND MONTH(created_at) = MONTH(GETDATE())", nativeQuery = true)
    Long countTotalCustomersThisMonth();

    @Query(value = "SELECT COUNT(DISTINCT customer_id) FROM hotel_bookings WHERE customer_id IS NOT NULL AND YEAR(created_at) = YEAR(DATEADD(MONTH, -1, GETDATE())) AND MONTH(created_at) = MONTH(DATEADD(MONTH, -1, GETDATE()))", nativeQuery = true)
    Long countTotalCustomersLastMonth();

    @Query(value = "SELECT COUNT(DISTINCT customer_id) FROM hotel_bookings WHERE customer_id IS NOT NULL AND CAST(created_at AS DATE) = CAST(DATEADD(DAY, -2, GETDATE()) AS DATE)", nativeQuery = true)
    Long countTotalCustomers2DaysAgo();

    @Query(value = "SELECT COUNT(DISTINCT customer_id) FROM hotel_bookings WHERE customer_id IS NOT NULL AND CAST(created_at AS DATE) >= CAST(DATEADD(DAY, -14, GETDATE()) AS DATE) AND CAST(created_at AS DATE) < CAST(DATEADD(DAY, -7, GETDATE()) AS DATE)", nativeQuery = true)
    Long countTotalCustomersPrevious7Days();

    @Query(value = "SELECT COUNT(DISTINCT customer_id) FROM hotel_bookings WHERE customer_id IS NOT NULL AND YEAR(created_at) = YEAR(DATEADD(MONTH, -2, GETDATE())) AND MONTH(created_at) = MONTH(DATEADD(MONTH, -2, GETDATE()))", nativeQuery = true)
    Long countTotalCustomers2MonthsAgo();

    default Long countTotalBookingsByPeriod(String period) {
        switch (period) {
            case "today":
                return countTotalBookingsToday();
            case "yesterday":
                return countTotalBookingsYesterday();
            case "last_7_days":
                return countTotalBookingsLast7Days();
            case "this_month":
                return countTotalBookingsThisMonth();
            case "last_month":
                return countTotalBookingsLastMonth();
            case "2_days_ago":
                return countTotalBookings2DaysAgo();
            case "previous_7_days":
                return countTotalBookingsPrevious7Days();
            case "2_months_ago":
                return countTotalBookings2MonthsAgo();
            default:
                return countTotalBookingsThisMonth();
        }
    }

    default Long countPaidBookingsByPeriod(String period) {
        switch (period) {
            case "today":
                return countPaidBookingsToday();
            case "yesterday":
                return countPaidBookingsYesterday();
            case "last_7_days":
                return countPaidBookingsLast7Days();
            case "this_month":
                return countPaidBookingsThisMonth();
            case "last_month":
                return countPaidBookingsLastMonth();
            case "2_days_ago":
                return countPaidBookings2DaysAgo();
            case "previous_7_days":
                return countPaidBookingsPrevious7Days();
            case "2_months_ago":
                return countPaidBookings2MonthsAgo();
            default:
                return countPaidBookingsThisMonth();
        }
    }

    default java.math.BigDecimal sumTotalRevenueByPeriod(String period) {
        switch (period) {
            case "today":
                return sumTotalRevenueToday();
            case "yesterday":
                return sumTotalRevenueYesterday();
            case "last_7_days":
                return sumTotalRevenueLast7Days();
            case "this_month":
                return sumTotalRevenueThisMonth();
            case "last_month":
                return sumTotalRevenueLastMonth();
            case "2_days_ago":
                return sumTotalRevenue2DaysAgo();
            case "previous_7_days":
                return sumTotalRevenuePrevious7Days();
            case "2_months_ago":
                return sumTotalRevenue2MonthsAgo();
            default:
                return sumTotalRevenueThisMonth();
        }
    }

    default Long countTotalCustomersByPeriod(String period) {
        switch (period) {
            case "today":
                return countTotalCustomersToday();
            case "yesterday":
                return countTotalCustomersYesterday();
            case "last_7_days":
                return countTotalCustomersLast7Days();
            case "this_month":
                return countTotalCustomersThisMonth();
            case "last_month":
                return countTotalCustomersLastMonth();
            case "2_days_ago":
                return countTotalCustomers2DaysAgo();
            case "previous_7_days":
                return countTotalCustomersPrevious7Days();
            case "2_months_ago":
                return countTotalCustomers2MonthsAgo();
            default:
                return countTotalCustomersThisMonth();
        }
    }

    @Query(value = "SELECT h.name as hotelName, COALESCE(SUM(b.total_price), 0) as revenue " +
                   "FROM hotels h " +
                   "LEFT JOIN hotel_rooms r ON h.id = r.hotel_id " +
                   "LEFT JOIN hotel_room_variants rv ON r.id = rv.room_id " +
                   "LEFT JOIN hotel_bookings b ON rv.id = b.room_variant_id " +
                   "LEFT JOIN orders o ON b.order_id = o.id " +
                   "WHERE CAST(b.created_at AS DATE) = CAST(GETDATE() AS DATE) AND o.status = 'PAID' " +
                   "GROUP BY h.id, h.name " +
                   "ORDER BY revenue DESC", nativeQuery = true)
    List<Object[]> getHotelRevenueToday();

    @Query(value = "SELECT h.name as hotelName, COALESCE(SUM(b.total_price), 0) as revenue " +
                   "FROM hotels h " +
                   "LEFT JOIN hotel_rooms r ON h.id = r.hotel_id " +
                   "LEFT JOIN hotel_room_variants rv ON r.id = rv.room_id " +
                   "LEFT JOIN hotel_bookings b ON rv.id = b.room_variant_id " +
                   "LEFT JOIN orders o ON b.order_id = o.id " +
                   "WHERE CAST(b.created_at AS DATE) = CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AND o.status = 'PAID' " +
                   "GROUP BY h.id, h.name " +
                   "ORDER BY revenue DESC", nativeQuery = true)
    List<Object[]> getHotelRevenueYesterday();

    @Query(value = "SELECT h.name as hotelName, COALESCE(SUM(b.total_price), 0) as revenue " +
                   "FROM hotels h " +
                   "LEFT JOIN hotel_rooms r ON h.id = r.hotel_id " +
                   "LEFT JOIN hotel_room_variants rv ON r.id = rv.room_id " +
                   "LEFT JOIN hotel_bookings b ON rv.id = b.room_variant_id " +
                   "LEFT JOIN orders o ON b.order_id = o.id " +
                   "WHERE CAST(b.created_at AS DATE) >= CAST(DATEADD(DAY, -7, GETDATE()) AS DATE) AND o.status = 'PAID' " +
                   "GROUP BY h.id, h.name " +
                   "ORDER BY revenue DESC", nativeQuery = true)
    List<Object[]> getHotelRevenueLast7Days();

    @Query(value = "SELECT h.name as hotelName, COALESCE(SUM(b.total_price), 0) as revenue " +
                   "FROM hotels h " +
                   "LEFT JOIN hotel_rooms r ON h.id = r.hotel_id " +
                   "LEFT JOIN hotel_room_variants rv ON r.id = rv.room_id " +
                   "LEFT JOIN hotel_bookings b ON rv.id = b.room_variant_id " +
                   "LEFT JOIN orders o ON b.order_id = o.id " +
                   "WHERE YEAR(b.created_at) = YEAR(GETDATE()) AND MONTH(b.created_at) = MONTH(GETDATE()) AND o.status = 'PAID' " +
                   "GROUP BY h.id, h.name " +
                   "ORDER BY revenue DESC", nativeQuery = true)
    List<Object[]> getHotelRevenueThisMonth();

    @Query(value = "SELECT h.name as hotelName, COALESCE(SUM(b.total_price), 0) as revenue " +
                   "FROM hotels h " +
                   "LEFT JOIN hotel_rooms r ON h.id = r.hotel_id " +
                   "LEFT JOIN hotel_room_variants rv ON r.id = rv.room_id " +
                   "LEFT JOIN hotel_bookings b ON rv.id = b.room_variant_id " +
                   "LEFT JOIN orders o ON b.order_id = o.id " +
                   "WHERE YEAR(b.created_at) = YEAR(DATEADD(MONTH, -1, GETDATE())) AND MONTH(b.created_at) = MONTH(DATEADD(MONTH, -1, GETDATE())) AND o.status = 'PAID' " +
                   "GROUP BY h.id, h.name " +
                   "ORDER BY revenue DESC", nativeQuery = true)
    List<Object[]> getHotelRevenueLastMonth();

    default Map<String, Object> getHotelRevenueChartData(String period) {
        List<Object[]> results;
        switch (period) {
            case "today":
                results = getHotelRevenueToday();
                break;
            case "yesterday":
                results = getHotelRevenueYesterday();
                break;
            case "last_7_days":
                results = getHotelRevenueLast7Days();
                break;
            case "this_month":
                results = getHotelRevenueThisMonth();
                break;
            case "last_month":
                results = getHotelRevenueLastMonth();
                break;
            default:
                results = getHotelRevenueThisMonth();
                break;
        }

        List<String> labels = new java.util.ArrayList<>();
        List<Number> data = new java.util.ArrayList<>();

        for (Object[] row : results) {
            String hotelName = (String) row[0];
            java.math.BigDecimal revenue = (java.math.BigDecimal) row[1];
            labels.add(hotelName);
            data.add(revenue);
        }

        Map<String, Object> chartData = new java.util.HashMap<>();
        chartData.put("labels", labels);
        chartData.put("data", data);
        return chartData;
    }

    @Query(value = "SELECT h.name as hotelName, FORMAT(b.created_at, 'dd/MM') as day, COALESCE(SUM(b.total_price), 0) as revenue " +
                   "FROM hotels h " +
                   "LEFT JOIN hotel_rooms r ON h.id = r.hotel_id " +
                   "LEFT JOIN hotel_room_variants rv ON r.id = rv.room_id " +
                   "LEFT JOIN hotel_bookings b ON rv.id = b.room_variant_id " +
                   "LEFT JOIN orders o ON b.order_id = o.id " +
                   "WHERE YEAR(b.created_at) = YEAR(GETDATE()) AND MONTH(b.created_at) = MONTH(GETDATE()) AND o.status = 'PAID' " +
                   "GROUP BY h.name, FORMAT(b.created_at, 'dd/MM') " +
                   "ORDER BY h.name, day", nativeQuery = true)
    List<Object[]> getHotelRevenueByDayThisMonth();

    @Query(value = "SELECT h.name as hotelName, FORMAT(b.created_at, 'dd/MM') as day, COALESCE(SUM(b.total_price), 0) as revenue " +
                   "FROM hotels h " +
                   "LEFT JOIN hotel_rooms r ON h.id = r.hotel_id " +
                   "LEFT JOIN hotel_room_variants rv ON r.id = rv.room_id " +
                   "LEFT JOIN hotel_bookings b ON rv.id = b.room_variant_id " +
                   "LEFT JOIN orders o ON b.order_id = o.id " +
                   "WHERE CAST(b.created_at AS DATE) = CAST(GETDATE() AS DATE) AND o.status = 'PAID' " +
                   "GROUP BY h.name, FORMAT(b.created_at, 'dd/MM') " +
                   "ORDER BY h.name, day", nativeQuery = true)
    List<Object[]> getHotelRevenueByDayToday();

    @Query(value = "SELECT h.name as hotelName, FORMAT(b.created_at, 'dd/MM') as day, COALESCE(SUM(b.total_price), 0) as revenue " +
                   "FROM hotels h " +
                   "LEFT JOIN hotel_rooms r ON h.id = r.hotel_id " +
                   "LEFT JOIN hotel_room_variants rv ON r.id = rv.room_id " +
                   "LEFT JOIN hotel_bookings b ON rv.id = b.room_variant_id " +
                   "LEFT JOIN orders o ON b.order_id = o.id " +
                   "WHERE CAST(b.created_at AS DATE) = CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AND o.status = 'PAID' " +
                   "GROUP BY h.name, FORMAT(b.created_at, 'dd/MM') " +
                   "ORDER BY h.name, day", nativeQuery = true)
    List<Object[]> getHotelRevenueByDayYesterday();

    @Query(value = "SELECT h.name as hotelName, FORMAT(b.created_at, 'dd/MM') as day, COALESCE(SUM(b.total_price), 0) as revenue " +
                   "FROM hotels h " +
                   "LEFT JOIN hotel_rooms r ON h.id = r.hotel_id " +
                   "LEFT JOIN hotel_room_variants rv ON r.id = rv.room_id " +
                   "LEFT JOIN hotel_bookings b ON rv.id = b.room_variant_id " +
                   "LEFT JOIN orders o ON b.order_id = o.id " +
                   "WHERE CAST(b.created_at AS DATE) >= CAST(DATEADD(DAY, -7, GETDATE()) AS DATE) AND o.status = 'PAID' " +
                   "GROUP BY h.name, FORMAT(b.created_at, 'dd/MM') " +
                   "ORDER BY h.name, day", nativeQuery = true)
    List<Object[]> getHotelRevenueByDayLast7Days();

    @Query(value = "SELECT h.name as hotelName, FORMAT(b.created_at, 'dd/MM') as day, COALESCE(SUM(b.total_price), 0) as revenue " +
                   "FROM hotels h " +
                   "LEFT JOIN hotel_rooms r ON h.id = r.hotel_id " +
                   "LEFT JOIN hotel_room_variants rv ON r.id = rv.room_id " +
                   "LEFT JOIN hotel_bookings b ON rv.id = b.room_variant_id " +
                   "LEFT JOIN orders o ON b.order_id = o.id " +
                   "WHERE YEAR(b.created_at) = YEAR(DATEADD(MONTH, -1, GETDATE())) AND MONTH(b.created_at) = MONTH(DATEADD(MONTH, -1, GETDATE())) AND o.status = 'PAID' " +
                   "GROUP BY h.name, FORMAT(b.created_at, 'dd/MM') " +
                   "ORDER BY h.name, day", nativeQuery = true)
    List<Object[]> getHotelRevenueByDayLastMonth();

    default Map<String, Object> getHotelRevenueByDayChartData(String period) {
        List<Object[]> results;
        java.util.List<String> labels = new java.util.ArrayList<>();
        
        switch (period) {
            case "today":
                results = getHotelRevenueByDayToday();
                java.time.LocalDate today = java.time.LocalDate.now();
                labels.add(String.format("%02d/%02d", today.getDayOfMonth(), today.getMonthValue()));
                break;
            case "yesterday":
                results = getHotelRevenueByDayYesterday();
                java.time.LocalDate yesterday = java.time.LocalDate.now().minusDays(1);
                labels.add(String.format("%02d/%02d", yesterday.getDayOfMonth(), yesterday.getMonthValue()));
                break;
            case "last_7_days":
                results = getHotelRevenueByDayLast7Days();
                for (int i = 6; i >= 0; i--) {
                    java.time.LocalDate date = java.time.LocalDate.now().minusDays(i);
                    labels.add(String.format("%02d/%02d", date.getDayOfMonth(), date.getMonthValue()));
                }
                break;
            case "this_month":
                results = getHotelRevenueByDayThisMonth();
                java.time.LocalDate now = java.time.LocalDate.now();
                int daysInMonth = now.lengthOfMonth();
                for (int d = 1; d <= daysInMonth; d++) {
                    labels.add(String.format("%02d/%02d", d, now.getMonthValue()));
                }
                break;
            case "last_month":
                results = getHotelRevenueByDayLastMonth();
                java.time.LocalDate lastMonth = java.time.LocalDate.now().minusMonths(1);
                int daysInLastMonth = lastMonth.lengthOfMonth();
                for (int d = 1; d <= daysInLastMonth; d++) {
                    labels.add(String.format("%02d/%02d", d, lastMonth.getMonthValue()));
                }
                break;
            default:
                results = getHotelRevenueByDayThisMonth();
                java.time.LocalDate defaultNow = java.time.LocalDate.now();
                int defaultDaysInMonth = defaultNow.lengthOfMonth();
                for (int d = 1; d <= defaultDaysInMonth; d++) {
                    labels.add(String.format("%02d/%02d", d, defaultNow.getMonthValue()));
                }
                break;
        }

        java.util.Map<String, double[]> hotelRevenueMap = new java.util.HashMap<>();
        for (Object[] row : results) {
            String hotelName = (String) row[0];
            String day = (String) row[1];
            java.math.BigDecimal revenue = (java.math.BigDecimal) row[2];
            int dayIdx = labels.indexOf(day);
            if (dayIdx == -1) continue;
            hotelRevenueMap.putIfAbsent(hotelName, new double[labels.size()]);
            hotelRevenueMap.get(hotelName)[dayIdx] = revenue.doubleValue();
        }
        
        java.util.List<java.util.Map<String, Object>> datasets = new java.util.ArrayList<>();
        for (String hotel : hotelRevenueMap.keySet()) {
            java.util.Map<String, Object> ds = new java.util.HashMap<>();
            ds.put("label", hotel);
            ds.put("data", hotelRevenueMap.get(hotel));
            datasets.add(ds);
        }
        
        java.util.Map<String, Object> chartData = new java.util.HashMap<>();
        chartData.put("labels", labels);
        chartData.put("datasets", datasets);
        return chartData;
    }

    default Map<String, Object> getHotelRevenuePieChartData(String period) {
        List<Object[]> results;
        switch (period) {
            case "today":
                results = getHotelRevenueToday();
                break;
            case "yesterday":
                results = getHotelRevenueYesterday();
                break;
            case "last_7_days":
                results = getHotelRevenueLast7Days();
                break;
            case "this_month":
                results = getHotelRevenueThisMonth();
                break;
            case "last_month":
                results = getHotelRevenueLastMonth();
                break;
            default:
                results = getHotelRevenueThisMonth();
                break;
        }

        java.util.List<String> labels = new java.util.ArrayList<>();
        java.util.List<Number> data = new java.util.ArrayList<>();

        for (Object[] row : results) {
            String hotelName = (String) row[0];
            java.math.BigDecimal revenue = (java.math.BigDecimal) row[1];
            if (revenue.doubleValue() > 0) {
                labels.add(hotelName);
                data.add(revenue);
            }
        }

        java.util.Map<String, Object> chartData = new java.util.HashMap<>();
        chartData.put("labels", labels);
        chartData.put("data", data);
        return chartData;
    }

    @Query(value = "SELECT rv.variant_name as roomName, h.name as hotelName, COUNT(b.id) as bookingCount " +
                   "FROM hotel_bookings b " +
                   "JOIN hotel_room_variants rv ON b.room_variant_id = rv.id " +
                   "JOIN hotel_rooms r ON rv.room_id = r.id " +
                   "JOIN hotels h ON r.hotel_id = h.id " +
                   "LEFT JOIN orders o ON b.order_id = o.id " +
                   "WHERE CAST(b.created_at AS DATE) = CAST(GETDATE() AS DATE) AND o.status = 'PAID' " +
                   "GROUP BY rv.id, rv.variant_name, h.name " +
                   "ORDER BY bookingCount DESC", nativeQuery = true)
    List<Object[]> getTopRoomsToday();

    @Query(value = "SELECT rv.variant_name as roomName, h.name as hotelName, COUNT(b.id) as bookingCount " +
                   "FROM hotel_bookings b " +
                   "JOIN hotel_room_variants rv ON b.room_variant_id = rv.id " +
                   "JOIN hotel_rooms r ON rv.room_id = r.id " +
                   "JOIN hotels h ON r.hotel_id = h.id " +
                   "LEFT JOIN orders o ON b.order_id = o.id " +
                   "WHERE CAST(b.created_at AS DATE) = CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AND o.status = 'PAID' " +
                   "GROUP BY rv.id, rv.variant_name, h.name " +
                   "ORDER BY bookingCount DESC", nativeQuery = true)
    List<Object[]> getTopRoomsYesterday();

    @Query(value = "SELECT rv.variant_name as roomName, h.name as hotelName, COUNT(b.id) as bookingCount " +
                   "FROM hotel_bookings b " +
                   "JOIN hotel_room_variants rv ON b.room_variant_id = rv.id " +
                   "JOIN hotel_rooms r ON rv.room_id = r.id " +
                   "JOIN hotels h ON r.hotel_id = h.id " +
                   "LEFT JOIN orders o ON b.order_id = o.id " +
                   "WHERE CAST(b.created_at AS DATE) >= CAST(DATEADD(DAY, -7, GETDATE()) AS DATE) AND o.status = 'PAID' " +
                   "GROUP BY rv.id, rv.variant_name, h.name " +
                   "ORDER BY bookingCount DESC", nativeQuery = true)
    List<Object[]> getTopRoomsLast7Days();

    @Query(value = "SELECT rv.variant_name as roomName, h.name as hotelName, COUNT(b.id) as bookingCount " +
                   "FROM hotel_bookings b " +
                   "JOIN hotel_room_variants rv ON b.room_variant_id = rv.id " +
                   "JOIN hotel_rooms r ON rv.room_id = r.id " +
                   "JOIN hotels h ON r.hotel_id = h.id " +
                   "LEFT JOIN orders o ON b.order_id = o.id " +
                   "WHERE YEAR(b.created_at) = YEAR(GETDATE()) AND MONTH(b.created_at) = MONTH(GETDATE()) AND o.status = 'PAID' " +
                   "GROUP BY rv.id, rv.variant_name, h.name " +
                   "ORDER BY bookingCount DESC", nativeQuery = true)
    List<Object[]> getTopRoomsThisMonth();

    @Query(value = "SELECT rv.variant_name as roomName, h.name as hotelName, COUNT(b.id) as bookingCount " +
                   "FROM hotel_bookings b " +
                   "JOIN hotel_room_variants rv ON b.room_variant_id = rv.id " +
                   "JOIN hotel_rooms r ON rv.room_id = r.id " +
                   "JOIN hotels h ON r.hotel_id = h.id " +
                   "LEFT JOIN orders o ON b.order_id = o.id " +
                   "WHERE YEAR(b.created_at) = YEAR(DATEADD(MONTH, -1, GETDATE())) AND MONTH(b.created_at) = MONTH(DATEADD(MONTH, -1, GETDATE())) AND o.status = 'PAID' " +
                   "GROUP BY rv.id, rv.variant_name, h.name " +
                   "ORDER BY bookingCount DESC", nativeQuery = true)
    List<Object[]> getTopRoomsLastMonth();

    default Map<String, Object> getTopRoomsChartData(String period) {
        List<Object[]> results;
        switch (period) {
            case "today":
                results = getTopRoomsToday();
                break;
            case "yesterday":
                results = getTopRoomsYesterday();
                break;
            case "last_7_days":
                results = getTopRoomsLast7Days();
                break;
            case "this_month":
                results = getTopRoomsThisMonth();
                break;
            case "last_month":
                results = getTopRoomsLastMonth();
                break;
            default:
                results = getTopRoomsThisMonth();
                break;
        }

        java.util.List<String> labels = new java.util.ArrayList<>();
        java.util.List<Number> data = new java.util.ArrayList<>();
        java.util.List<String> hotels = new java.util.ArrayList<>();

        int limit = Math.min(results.size(), 10);
        for (int i = 0; i < limit; i++) {
            Object[] row = results.get(i);
            String roomName = (String) row[0];
            String hotelName = (String) row[1];
            Number bookingCount = (Number) row[2];
            
            labels.add(roomName);
            data.add(bookingCount);
            hotels.add(hotelName);
        }

        java.util.Map<String, Object> chartData = new java.util.HashMap<>();
        chartData.put("labels", labels);
        chartData.put("data", data);
        chartData.put("hotels", hotels);
        return chartData;
    }
}
