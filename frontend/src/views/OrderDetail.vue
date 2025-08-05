<script setup>
import { ref, onMounted, computed, reactive } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getBearerToken } from "@/services/TokenService"; // Import getBearerToken
import { BusBookingDetailAPI } from "@/api/busAPI_Client/busbookingApi/busBookingDetailApi"; // ✅ NEW: Import bus booking detail API

const route = useRoute();
const router = useRouter();

// --- STATE MANAGEMENT ---
const order = ref(null);
const flightBookingDetails = ref([]);
const isLoading = ref(true);
const error = ref(null);
const processingItemId = ref(null); // State loading cho từng item

// ✅ NEW: State cho bus booking details
const busBookingDetails = ref(new Map()); // Map<bookingId, BusBookingDetailDto>
const isLoadingBusDetails = ref(new Set()); // Set<bookingId> để track loading state

// --- STATE CHO VOUCHER ---
const voucherCode = ref("");
const isApplyingVoucher = ref(false);
const suggestedVouchers = ref([]);
const isLoadingVouchers = ref(false);

// Reactive state for hotel image sliders
const hotelImageIndices = reactive({});
const slideDirectionMap = reactive({});

// --- COMPUTED PROPERTIES ---
const isEditable = computed(() => order.value?.status === "PENDING_PAYMENT");

// --- HELPER FUNCTIONS ---
const formatPrice = (price) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    price || 0
  );

const formatDate = (dateString) =>
  new Date(dateString).toLocaleDateString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });

const formatDateTime = (dateString) =>
  new Date(dateString).toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });

// ✅ NEW: Format time for bus slots
const formatTime = (timeString) => {
  if (!timeString) return '--:--';
  return timeString.substring(0, 5); // Extract HH:MM from time string
};

// ✅ NEW: Calculate duration between departure and arrival
const calculateDuration = (departureTime, arrivalTime) => {
  if (!departureTime || !arrivalTime) return 'N/A';
  
  try {
    const departure = new Date(`2000-01-01T${departureTime}`);
    const arrival = new Date(`2000-01-01T${arrivalTime}`);
    
    // Handle overnight trips
    if (arrival < departure) {
      arrival.setDate(arrival.getDate() + 1);
    }
    
    const diffMs = arrival - departure;
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
    const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));
    
    if (diffHours > 0) {
      return `${diffHours}h ${diffMinutes}m`;
    } else {
      return `${diffMinutes}m`;
    }
  } catch (error) {
    return 'N/A';
  }
};

const getStatusInfo = (status) => {
  switch (status) {
    case "PAID":
      return {
        text: "Đã thanh toán",
        color: "green",
        icon: "fa-solid fa-circle-check",
      };
    case "PENDING_PAYMENT":
      return {
        text: "Chờ thanh toán",
        color: "orange",
        icon: "fa-solid fa-clock",
      };
    case "CANCELLED":
      return { text: "Đã hủy", color: "red", icon: "fa-solid fa-circle-xmark" };
    default:
      return {
        text: status,
        color: "gray",
        icon: "fa-solid fa-circle-question",
      };
  }
};

const getVoucherDiscountText = (voucher) => {
  if (voucher.type === "FIXED_AMOUNT") {
    return `Giảm ${formatPrice(voucher.discountAmount)}`;
  }
  if (voucher.type === "PERCENTAGE") {
    let text = `Giảm ${voucher.discountPercentage}%`;
    if (voucher.maxDiscountAmount) {
      text += ` (tối đa ${formatPrice(voucher.maxDiscountAmount)})`;
    }
    return text;
  }
  return "";
};

// --- API CALLS ---
const fetchOrderDetails = async () => {
  isLoading.value = true;
  error.value = null;
  const orderId = route.params.id;
  try {
    const orderResponse = await fetch(
      `http://localhost:8080/api/v1/orders/${orderId}`,
      { headers: { Authorization: getBearerToken() } }
    );
    if (!orderResponse.ok) throw new Error("Không thể tải chi tiết đơn hàng.");

    const data = await orderResponse.json();
    order.value = data.data;
    
    // ✅ DEBUG: Log order data to see structure
    if (order.value.busBookings && order.value.busBookings.length > 0) {
      // Bus bookings loaded successfully
    }

    if (order.value && isEditable.value) {
      await fetchSuggestedVouchers();
    }

    flightBookingDetails.value = [];
    if (order.value.flightBookings && order.value.flightBookings.length > 0) {
      for (const booking of order.value.flightBookings) {
        try {
          const detailRes = await fetch(
            `http://localhost:8080/api/bookings/flights/reservation-summary/${booking.id}`,
            { headers: { Authorization: getBearerToken() } }
          );
          if (detailRes.ok) {
            const detail = await detailRes.json();
            flightBookingDetails.value.push({ ...detail, showDetail: false });
          }
        } catch (e) {
        }
      }
    }

    // ✅ NEW: Fetch bus booking details
    if (order.value.busBookings && order.value.busBookings.length > 0) {
      await fetchBusBookingDetails();
    }
  } catch (e) {
    error.value = e.message;
  } finally {
    isLoading.value = false;
  }
};

const fetchSuggestedVouchers = async () => {
  isLoadingVouchers.value = true;
  try {
    const response = await fetch(
      `http://localhost:8080/api/vouchers/suggested?orderAmount=${order.value.amount}`,
      {
        headers: { Authorization: getBearerToken() },
      }
    );
    if (response.ok) {
      suggestedVouchers.value = await response.json();
    }
  } catch (e) {
    // Silent error handling
  } finally {
    isLoadingVouchers.value = false;
  }
};

// ✅ NEW: Fetch bus booking details
const fetchBusBookingDetails = async () => {
  if (!order.value?.busBookings) return;
  
  for (const busBooking of order.value.busBookings) {
    if (!busBookingDetails.value.has(busBooking.id)) {
      await fetchBusBookingDetail(busBooking.id);
    }
  }
};

// ✅ NEW: Fetch single bus booking detail
const fetchBusBookingDetail = async (bookingId) => {
  if (isLoadingBusDetails.value.has(bookingId)) return; // Prevent duplicate requests
  
  isLoadingBusDetails.value.add(bookingId);
  try {
    const response = await BusBookingDetailAPI.getBookingDetailForDisplay(bookingId);
    
    if (response.data) {
      busBookingDetails.value.set(bookingId, response.data);
    }
  } catch (error) {
    // Don't throw error to prevent breaking the page
  } finally {
    isLoadingBusDetails.value.delete(bookingId);
  }
};

onMounted(fetchOrderDetails);

// --- EVENT HANDLERS ---
const handleApplyVoucher = async () => {
  if (!voucherCode.value.trim()) {
    return (
      window.$toast && window.$toast("Vui lòng nhập mã giảm giá.", "success")
    );
  }
  isApplyingVoucher.value = true;
  try {
    const response = await fetch(
      `http://localhost:8080/api/v1/orders/${order.value.id}/apply-voucher`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: getBearerToken(),
        },
        body: JSON.stringify({ voucherCode: voucherCode.value }),
      }
    );
    const result = await response.json();
    if (!response.ok)
      throw new Error(result.message || "Mã giảm giá không hợp lệ.");

    order.value = result.data;
    window.$toast &&
      window.$toast("Áp dụng mã giảm giá thành công!", "success");
    voucherCode.value = "";
  } catch (e) {
    window.$toast && window.$toast(e.message, "error");
  } finally {
    isApplyingVoucher.value = false;
  }
};

const selectVoucher = (code) => {
  voucherCode.value = code;
};

const addMoreServices = () => {
  localStorage.setItem("activeCartId", order.value.id);
  router.push("/");
};

// ✅ NEW: Cancel bus booking before removing from cart
const cancelBusBooking = async (busBookingId) => {
  try {
    
    const response = await fetch(
      `http://localhost:8080/api/v1/bus-booking/${busBookingId}/cancel`,
      { 
        method: "POST", 
        headers: { 
          Authorization: getBearerToken(),
          'Content-Type': 'application/json'
        } 
      }
    );
    
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || "Không thể hủy vé xe.");
    }
    
    const result = await response.json();
    return result;
    
  } catch (error) {
    throw error;
  }
};

const handleDeleteItem = async (itemId, itemType) => {
  // ✅ Customize confirm message cho từng loại service
  let confirmMessage = '';
  switch (itemType) {
    case 'BUS':
      confirmMessage = `Bạn có chắc chắn muốn hủy và xóa vé xe này khỏi đơn hàng?\n\n⚠️ Lưu ý: Vé xe sẽ được hủy và ghế sẽ được giải phóng để khách khác có thể đặt.`;
      break;
    case 'TOUR':
      confirmMessage = 'Bạn có chắc chắn muốn xóa tour này khỏi đơn hàng?';
      break;
    case 'FLIGHT':
      confirmMessage = 'Bạn có chắc chắn muốn xóa chuyến bay này khỏi đơn hàng?';
      break;
    case 'HOTEL':
      confirmMessage = 'Bạn có chắc chắn muốn xóa phòng khách sạn này khỏi đơn hàng?';
      break;
    default:
      confirmMessage = 'Bạn có chắc chắn muốn xóa dịch vụ này khỏi đơn hàng?';
  }
  
  if (!confirm(confirmMessage)) return;

  processingItemId.value = `${itemType}-${itemId}`;
  try {
    // ✅ STEP 1: Nếu là BUS, cancel booking trước để release seats
    if (itemType === 'BUS') {
      try {
        await cancelBusBooking(itemId);
      } catch (error) {
        // ✅ Handle case where booking is already cancelled/expired
        if (error.message && (error.message.includes('already cancelled') || error.message.includes('already expired'))) {
        } else {
          throw error; // Re-throw other errors
        }
      }
    }
    
    // ✅ STEP 2: Xóa item khỏi cart
    const response = await fetch(
      `http://localhost:8080/api/v1/cart/items?orderId=${order.value.id}&itemId=${itemId}&itemType=${itemType}`,
      { method: "DELETE", headers: { Authorization: getBearerToken() } }
    );
    if (!response.ok) throw new Error("Xóa dịch vụ thất bại.");

    // ✅ Success messages theo loại service
    const successMessages = {
      'BUS': '🚌 Đã hủy vé xe và xóa khỏi đơn hàng thành công! Ghế đã được giải phóng.',
      'TOUR': '🏔️ Đã xóa tour khỏi đơn hàng thành công!',
      'FLIGHT': '✈️ Đã xóa chuyến bay khỏi đơn hàng thành công!',
      'HOTEL': '🏨 Đã xóa phòng khách sạn khỏi đơn hàng thành công!'
    };
    
    const message = successMessages[itemType] || "Đã xóa dịch vụ thành công.";
    window.$toast && window.$toast(message, "success");
                                                                                                                                  
    // ✅ Clear activeCartId từ localStorage nếu đơn hàng này là cart hiện tại
    const activeCartId = localStorage.getItem('activeCartId');
    if (activeCartId && parseInt(activeCartId) === order.value.id) {
      localStorage.removeItem('activeCartId');
    }
    
    await fetchOrderDetails();
    
  } catch (e) {
    
    // ✅ Error messages theo loại service
    const errorMessages = {
      'BUS': '❌ Lỗi khi hủy vé xe: ' + e.message,
      'TOUR': '❌ Lỗi khi xóa tour: ' + e.message,
      'FLIGHT': '❌ Lỗi khi xóa chuyến bay: ' + e.message,
      'HOTEL': '❌ Lỗi khi xóa phòng khách sạn: ' + e.message
    };
    
    const message = errorMessages[itemType] || ('❌ Lỗi: ' + e.message);
    window.$toast && window.$toast(message, "error");
  } finally {
    processingItemId.value = null;
  }
};

const getPay = () => {
  router.push(`/payment/${order.value.id}`);
};

const handleEditItem = async (item, itemType) => {
  if (
    !confirm(
      `Chức năng này sẽ xóa dịch vụ hiện tại và đưa bạn về trang sản phẩm để chọn lại. Bạn có muốn tiếp tục?`
    )
  )
    return;

  processingItemId.value = `${itemType}-${item.id}`;
  try {
    const deleteResponse = await fetch(
      `http://localhost:8080/api/v1/cart/items?orderId=${order.value.id}&itemId=${item.id}&itemType=${itemType}`,
      { method: "DELETE", headers: { Authorization: getBearerToken() } }
    );
    if (!deleteResponse.ok) throw new Error("Lỗi khi xóa dịch vụ cũ.");

    localStorage.setItem("activeCartId", order.value.id);
    const paths = {
      TOUR: `/tours/${item.tourId}`,
      FLIGHT: `/flights/${item.flightId}`,
      HOTEL: `/hotels/${item.hotelId}`,
      BUS: `/bus`, // ✅ Route cho bus booking
    };
    if (paths[itemType]) router.push(paths[itemType]);
  } catch (e) {
    window.$toast && window.$toast(e.message, "error");
  } finally {
    processingItemId.value = null;
  }
};

// --- Hotel Image Slider Functions ---
function nextHotelImage(hotel) {
  if (!hotel.imageUrls || hotel.imageUrls.length <= 1) return;
  if (!(hotel.id in hotelImageIndices)) hotelImageIndices[hotel.id] = 0;
  slideDirectionMap[hotel.id] = "next";
  hotelImageIndices[hotel.id] =
    (hotelImageIndices[hotel.id] + 1) % hotel.imageUrls.length;
}

function prevHotelImage(hotel) {
  if (!hotel.imageUrls || hotel.imageUrls.length <= 1) return;
  if (!(hotel.id in hotelImageIndices)) hotelImageIndices[hotel.id] = 0;
  slideDirectionMap[hotel.id] = "prev";
  hotelImageIndices[hotel.id] =
    (hotelImageIndices[hotel.id] - 1 + hotel.imageUrls.length) %
    hotel.imageUrls.length;
}
</script>

<template>
  <div class="bg-gray-50 w-full min-h-screen">
    <div class="container mx-auto px-4 py-10">
      <div v-if="isLoading" class="text-center py-20">
        <i class="fas fa-spinner fa-spin text-4xl text-blue-500"></i>
        <p class="mt-4 text-gray-600">Đang tải chi tiết đơn hàng...</p>
      </div>

      <div v-else-if="error" class="text-center py-20 bg-red-50 p-6 rounded-lg">
        <i class="fas fa-exclamation-triangle text-4xl text-red-500"></i>
        <p class="mt-4 font-semibold text-red-700">Đã xảy ra lỗi</p>
        <p class="text-red-600">{{ error }}</p>
      </div>

      <div v-else-if="order">
        <div
          class="mb-8 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4"
        >
          <div>
            <div class="flex items-center gap-4">
              <h1 class="text-4xl font-bold text-gray-800">
                Chi tiết Đơn hàng
              </h1>
              <span class="font-semibold text-3xl text-blue-600"
                >#{{ order.id }}</span
              >
            </div>
            <p class="text-gray-500 mt-1">
              Xem lại thông tin chi tiết cho các dịch vụ bạn đã đặt.
            </p>
          </div>
          <button
            v-if="isEditable"
            @click="addMoreServices"
            class="bg-green-600 text-white font-medium px-5 py-2.5 rounded-lg text-sm text-center hover:bg-green-700 transition-colors w-full sm:w-auto"
          >
            <i class="fa-solid fa-plus mr-2"></i> Thêm dịch vụ khác
          </button>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div class="lg:col-span-2 space-y-6">
            <div v-if="order.tourBookings && order.tourBookings.length > 0">
              <h2
                class="text-2xl font-bold mb-4 text-gray-700 flex items-center gap-3"
              >
                <i class="fa-solid fa-mountain-sun text-blue-500"></i> Các tour
                đã đặt
              </h2>
              <div
                v-for="tour in order.tourBookings"
                :key="'tour-' + tour.id"
                class="bg-white p-6 rounded-xl shadow-lg border border-gray-200 mb-4"
              >
                <div class="flex justify-between items-start">
                  <h3 class="text-xl font-bold text-blue-700 mb-4">
                    {{ tour.tourName }}
                  </h3>
                  <div
                    v-if="isEditable"
                    class="flex items-center gap-2 flex-shrink-0"
                  >
                    <button
                      :disabled="processingItemId === `TOUR-${tour.id}`"
                      @click="handleEditItem(tour, 'TOUR')"
                      class="text-sm text-yellow-600 hover:text-yellow-800 disabled:opacity-50"
                    >
                      <i
                        :class="
                          processingItemId === `TOUR-${tour.id}`
                            ? 'fas fa-spinner fa-spin'
                            : 'fa-solid fa-pencil'
                        "
                      ></i>
                      Sửa
                    </button>
                    <button
                      :disabled="processingItemId === `TOUR-${tour.id}`"
                      @click="handleDeleteItem(tour.id, 'TOUR')"
                      class="text-sm text-red-600 hover:text-red-800 disabled:opacity-50"
                    >
                      <i
                        :class="
                          processingItemId === `TOUR-${tour.id}`
                            ? 'fas fa-spinner fa-spin'
                            : 'fa-solid fa-trash'
                        "
                      ></i>
                      Xóa
                    </button>
                  </div>
                </div>
                <div
                  class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-600"
                >
                  <div class="flex items-center gap-3">
                    <i
                      class="fa-solid fa-calendar-days w-5 text-center text-blue-500"
                    ></i
                    ><span
                      >Ngày khởi hành:
                      <strong>{{
                        formatDate(tour.departureDate)
                      }}</strong></span
                    >
                  </div>
                  <div class="flex items-center gap-3">
                    <i
                      class="fa-solid fa-users w-5 text-center text-blue-500"
                    ></i
                    ><span
                      >Hành khách:
                      <strong
                        >{{ tour.numberOfAdults }} người lớn,
                        {{ tour.numberOfChildren }} trẻ em</strong
                      ></span
                    >
                  </div>
                </div>
                <div class="border-t mt-4 pt-4">
                  <p class="text-lg text-right">
                    Tổng giá tour:
                    <span class="font-bold text-xl text-blue-700">{{
                      formatPrice(tour.totalPrice)
                    }}</span>
                  </p>
                </div>
              </div>
            </div>

            <div v-if="flightBookingDetails.length > 0">
              <h2
                class="text-2xl font-bold mb-4 text-gray-700 flex items-center gap-3"
              >
                <i class="fa-solid fa-plane-up text-green-500"></i> Các chuyến
                bay đã đặt
              </h2>
              <div
                v-for="flightDetail in flightBookingDetails"
                :key="flightDetail.booking.bookingId"
                class="bg-white p-6 rounded-xl shadow-lg border border-gray-200 mb-4"
              >
                <div class="flex justify-between items-start">
                  <h3 class="text-xl font-bold text-green-700 mb-4">
                    {{ flightDetail.booking.flight.name }} -
                    {{ flightDetail.booking.flight.airline?.name }}
                  </h3>
                  <div
                    v-if="isEditable"
                    class="flex items-center gap-2 flex-shrink-0"
                  >
                    <button
                      :disabled="
                        processingItemId ===
                        `FLIGHT-${flightDetail.booking.bookingId}`
                      "
                      @click="
                        handleDeleteItem(
                          flightDetail.booking.bookingId,
                          'FLIGHT'
                        )
                      "
                      class="text-sm text-red-600 hover:text-red-800 disabled:opacity-50"
                    >
                      <i
                        :class="
                          processingItemId ===
                          `FLIGHT-${flightDetail.booking.bookingId}`
                            ? 'fas fa-spinner fa-spin'
                            : 'fa-solid fa-trash'
                        "
                      ></i>
                      Xóa
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="order.hotelBookings && order.hotelBookings.length > 0">
              <h2
                class="text-2xl font-bold mb-4 text-gray-700 flex items-center gap-3"
              >
                <i class="fa-solid fa-hotel text-indigo-500"></i> Các phòng
                khách sạn đã đặt
              </h2>
              <div v-for="hotel in order.hotelBookings" :key="'hotel-' + hotel.id" class="bg-gradient-to-br from-indigo-50 to-white p-6 rounded-2xl shadow-xl border border-indigo-100 mb-6 flex flex-col md:flex-row gap-6 items-center md:items-stretch hover:shadow-2xl transition-shadow duration-200 relative">
                <div class="relative flex-shrink-0 flex flex-col items-center">
                  <template v-if="(hotel.imageUrls && hotel.imageUrls.length) || hotel.imageUrl">
                    <div class="relative w-40 h-32 md:w-56 md:h-40 flex items-center justify-center overflow-hidden rounded-xl">
                      <transition :name="slideDirectionMap[hotel.id] === 'next' ? 'slide-right' : 'slide-left'">
                        <img :key="hotel.imageUrls && hotel.imageUrls.length ? hotel.imageUrls[hotelImageIndices[hotel.id] || 0] : hotel.imageUrl" :src="hotel.imageUrls && hotel.imageUrls.length ? hotel.imageUrls[hotelImageIndices[hotel.id] || 0] : hotel.imageUrl" class="w-40 h-32 md:w-56 md:h-40 object-cover border-2 border-indigo-200 shadow-md mb-2 absolute left-0 top-0" />
                      </transition>
                      <div v-if="hotel.imageUrls && hotel.imageUrls.length > 1" class="flex gap-2 absolute top-1/2 left-0 right-0 justify-between px-2 -translate-y-1/2 z-10">
                        <button @click="prevHotelImage(hotel)" class="bg-white/80 hover:bg-indigo-100 rounded-full p-1 shadow border border-indigo-200"><i class="fa-solid fa-chevron-left text-indigo-600"></i></button>
                        <button @click="nextHotelImage(hotel)" class="bg-white/80 hover:bg-indigo-100 rounded-full p-1 shadow border border-indigo-200"><i class="fa-solid fa-chevron-right text-indigo-600"></i></button>
                      </div>
                    </div>
                  </template>
                </div>
                <div class="flex justify-between items-start">
                  <div class="flex-1 flex flex-col justify-between">
                    <h3 class="text-2xl font-extrabold text-indigo-700 mb-1 flex items-center gap-2"><i class="fa-solid fa-bed text-indigo-400"></i> {{ hotel.hotelName }}</h3>
                    <div class="text-base text-gray-700 font-semibold mb-1 flex items-center gap-2"><i class="fa-solid fa-door-closed text-gray-400"></i> {{ hotel.roomType }} <span class="mx-1">-</span> <span class="text-indigo-600 font-bold">{{ hotel.variantName }}</span></div>
                    <div class="flex flex-wrap gap-4 text-sm text-gray-500 mb-1 mt-2">
                      <div class="flex items-center gap-1"><i class="fa-solid fa-calendar-days text-blue-400"></i> Nhận phòng: <b>{{ formatDate(hotel.checkInDate) }}</b></div>
                      <div class="flex items-center gap-1"><i class="fa-solid fa-calendar-check text-green-400"></i> Trả phòng: <b>{{ formatDate(hotel.checkOutDate) }}</b></div>
                    </div>
                    <div class="flex items-center gap-2 text-sm text-gray-600 mt-1"><i class="fa-solid fa-users text-pink-400"></i> Khách: <b>{{ hotel.numAdults }}</b> người lớn, <b>{{ hotel.numChildren }}</b> trẻ em</div>
                    <div class="flex items-center gap-1 mt-1 text-sm text-indigo-700 pt-1"><i class="fa-solid fa-door-open text-indigo-400"></i> Số lượng phòng đã đặt: <b>{{ hotel.numberOfRooms ?? hotel.rooms ?? 1 }}</b></div>
                    <div class="text-right font-bold text-2xl text-indigo-600 absolute right-6 bottom-3">{{ formatPrice(hotel.totalPrice) }}</div>
                  </div>
                  <div v-if="isEditable" class="flex gap-2 absolute right-6 top-6 z-10">
                    <button @click="handleEditItem(hotel, 'HOTEL')" class="text-sm text-yellow-600 hover:text-yellow-800 flex items-center"><i class="fa-solid fa-pencil mr-1"></i> Sửa</button>
                    <button @click="handleDeleteItem(hotel.id, 'HOTEL')" class="text-sm text-red-600 hover:text-red-800 flex items-center"><i class="fa-solid fa-trash mr-1"></i> Xóa</button>
                  </div>
                </div>
              </div>
            </div>

            <!-- ✅ Bus Bookings Section - Compact Design -->
            <div v-if="order.busBookings && order.busBookings.length > 0">
              <h2 class="text-xl font-bold mb-3 text-gray-700 flex items-center gap-2">
                <i class="fa-solid fa-bus text-orange-500"></i> Vé xe đã đặt
              </h2>
              
              <div
                v-for="bus in order.busBookings"
                :key="'bus-' + bus.id"
                class="bg-white p-4 rounded-lg shadow-sm border border-gray-200 mb-3"
              >
                <!-- ✅ Compact Header -->
                <div class="flex justify-between items-center mb-3">
                  <div class="flex-1">
                    <h3 class="text-lg font-bold text-orange-700">
                      🚌 #{{ bus.bookingReference || bus.id }}
                    </h3>
                    
                    <!-- ✅ Compact Route Info -->
                    <div v-if="isLoadingBusDetails.has(bus.id)" class="mt-1">
                      <div class="flex items-center gap-2 text-gray-500 text-sm">
                        <i class="fa-solid fa-spinner fa-spin"></i>
                        <span>Đang tải...</span>
                      </div>
                    </div>
                    
                    <div v-else-if="busBookingDetails.has(bus.id)" class="mt-1">
                      <div class="flex items-center gap-2 text-sm">
                        <i class="fa-solid fa-map-marker-alt text-red-500 text-xs"></i>
                        <span class="text-red-600 font-medium">{{ busBookingDetails.get(bus.id).departureLocation || 'Điểm đi' }}</span>
                        <i class="fa-solid fa-arrow-right text-gray-400 text-xs"></i>
                        <i class="fa-solid fa-map-marker-alt text-green-500 text-xs"></i>
                        <span class="text-green-600 font-medium">{{ busBookingDetails.get(bus.id).arrivalLocation || 'Điểm đến' }}</span>
                      </div>
                    </div>
                    
                    <div v-else class="mt-1">
                      <div class="flex items-center gap-2 text-sm">
                        <i class="fa-solid fa-map-marker-alt text-red-500 text-xs"></i>
                        <span class="text-red-600 font-medium">{{ bus.busSlot?.route?.startLocation || 'Điểm đi' }}</span>
                        <i class="fa-solid fa-arrow-right text-gray-400 text-xs"></i>
                        <i class="fa-solid fa-map-marker-alt text-green-500 text-xs"></i>
                        <span class="text-green-600 font-medium">{{ bus.busSlot?.route?.endLocation || 'Điểm đến' }}</span>
                      </div>
                    </div>
                  </div>
                  
                  <!-- ✅ Compact Action buttons -->
                  <div v-if="isEditable" class="flex items-center gap-1">
                    <button
                      :disabled="processingItemId === `BUS-${bus.id}`"
                      @click="handleEditItem(bus, 'BUS')"
                      class="text-xs text-yellow-600 hover:text-yellow-800 disabled:opacity-50 px-2 py-1 rounded"
                    >
                      <i :class="processingItemId === `BUS-${bus.id}` ? 'fas fa-spinner fa-spin' : 'fa-solid fa-pencil'"></i>
                    </button>
                    <button
                      :disabled="processingItemId === `BUS-${bus.id}`"
                      @click="handleDeleteItem(bus.id, 'BUS')"
                      class="text-xs text-red-600 hover:text-red-800 disabled:opacity-50 px-2 py-1 rounded"
                    >
                      <i :class="processingItemId === `BUS-${bus.id}` ? 'fas fa-spinner fa-spin' : 'fa-solid fa-trash'"></i>
                    </button>
                  </div>
                </div>

                <!-- ✅ Compact Trip Info với dữ liệu từ API mới -->
                <div v-if="busBookingDetails.has(bus.id)" class="bg-gray-50 rounded-lg p-3 mb-3">
                  <div class="grid grid-cols-4 gap-3 text-xs">
                    <div class="flex items-center gap-1">
                      <i class="fa-solid fa-calendar text-blue-500"></i>
                      <span class="font-medium">{{ formatDate(busBookingDetails.get(bus.id).departureDate) }}</span>
                    </div>
                    <div class="flex items-center gap-1">
                      <i class="fa-solid fa-clock text-green-500"></i>
                      <span class="font-medium">{{ formatTime(busBookingDetails.get(bus.id).departureTime) }}</span>
                    </div>
                    <div class="flex items-center gap-1">
                      <i class="fa-solid fa-chair text-purple-500"></i>
                      <span class="font-medium">{{ busBookingDetails.get(bus.id).totalSeats || 0 }} ghế</span>
                    </div>
                    <div class="flex items-center gap-1">
                      <i class="fa-solid fa-route text-orange-500"></i>
                      <span class="font-medium">{{ busBookingDetails.get(bus.id).tripDuration }}</span>
                    </div>
                  </div>
                </div>

                <!-- ✅ Compact Trip Info với dữ liệu cũ -->
                <div v-else class="bg-gray-50 rounded-lg p-3 mb-3">
                  <div class="grid grid-cols-4 gap-3 text-xs">
                    <div class="flex items-center gap-1">
                      <i class="fa-solid fa-calendar text-blue-500"></i>
                      <span class="font-medium">{{ formatDate(bus.busSlot?.slotDate) }}</span>
                    </div>
                    <div class="flex items-center gap-1">
                      <i class="fa-solid fa-clock text-green-500"></i>
                      <span class="font-medium">{{ formatTime(bus.busSlot?.departureTime) }}</span>
                    </div>
                    <div class="flex items-center gap-1">
                      <i class="fa-solid fa-chair text-purple-500"></i>
                      <span class="font-medium">{{ bus.selectedSeats?.length || 0 }} ghế</span>
                    </div>
                    <div class="flex items-center gap-1">
                      <i class="fa-solid fa-route text-orange-500"></i>
                      <span class="font-medium">{{ calculateDuration(bus.busSlot?.departureTime, bus.busSlot?.arrivalTime) }}</span>
                    </div>
                  </div>
                </div>
                
                <!-- ✅ Compact Seat Info (chỉ hiển thị khi hover hoặc click) -->
                <div v-if="busBookingDetails.has(bus.id) && busBookingDetails.get(bus.id).selectedSeats?.length > 0" class="text-xs text-gray-600 mb-2">
                  <i class="fa-solid fa-chair text-purple-500 mr-1"></i>
                  Ghế: {{ busBookingDetails.get(bus.id).selectedSeats.map(seat => seat.seatNumber).join(', ') }}
                </div>

                <!-- ✅ Compact Additional Info -->
                <div class="text-xs text-gray-600 mb-2">
                  <i class="fa-solid fa-users text-blue-500 mr-1"></i>
                  {{ busBookingDetails.has(bus.id) ? busBookingDetails.get(bus.id).numPassengers : bus.numPassengers }} hành khách
                </div>

                <!-- ✅ Compact Customer Info -->
                <div v-if="busBookingDetails.has(bus.id) && busBookingDetails.get(bus.id).customerName" class="text-xs text-gray-600 mb-2">
                  <i class="fa-solid fa-user text-blue-500 mr-1"></i>
                  {{ busBookingDetails.get(bus.id).customerName }}
                  <span v-if="busBookingDetails.get(bus.id).customerPhone" class="ml-2">
                    <i class="fa-solid fa-phone text-green-500 mr-1"></i>
                    {{ busBookingDetails.get(bus.id).customerPhone }}
                  </span>
                </div>

                <div v-else-if="bus.customer" class="text-xs text-gray-600 mb-2">
                  <i class="fa-solid fa-user text-blue-500 mr-1"></i>
                  {{ bus.customer.fullName }}
                  <span v-if="bus.customer.phone" class="ml-2">
                    <i class="fa-solid fa-phone text-green-500 mr-1"></i>
                    {{ bus.customer.phone }}
                  </span>
                </div>

                <!-- ✅ Compact Footer: Status & Price -->
                <div class="border-t pt-2 flex justify-between items-center">
                  <div class="flex items-center gap-2">
                    <!-- Status Badge -->
                    <div v-if="busBookingDetails.has(bus.id) ? busBookingDetails.get(bus.id).status : bus.status" class="flex items-center gap-1">
                      <span :class="(busBookingDetails.has(bus.id) ? busBookingDetails.get(bus.id).status : bus.status) === 'RESERVED' ? 'bg-orange-100 text-orange-700' : (busBookingDetails.has(bus.id) ? busBookingDetails.get(bus.id).status : bus.status) === 'CONFIRMED' ? 'bg-green-100 text-green-700' : (busBookingDetails.has(bus.id) ? busBookingDetails.get(bus.id).status : bus.status) === 'CANCELLED' ? 'bg-red-100 text-red-700' : 'bg-gray-100 text-gray-700'" 
                            class="px-2 py-0.5 rounded-full font-medium text-xs">
                        {{ (busBookingDetails.has(bus.id) ? busBookingDetails.get(bus.id).status : bus.status) === 'RESERVED' ? '🟡 Đã đặt' : (busBookingDetails.has(bus.id) ? busBookingDetails.get(bus.id).status : bus.status) === 'CONFIRMED' ? '✅ Đã xác nhận' : (busBookingDetails.has(bus.id) ? busBookingDetails.get(bus.id).status : bus.status) === 'CANCELLED' ? '❌ Đã hủy' : (busBookingDetails.has(bus.id) ? busBookingDetails.get(bus.id).status : bus.status) }}
                      </span>
                    </div>
                    
                    <!-- Booking Date -->
                    <div class="text-xs text-gray-500">
                      <i class="fa-solid fa-calendar text-gray-400 mr-1"></i>
                      {{ formatDateTime(busBookingDetails.has(bus.id) ? busBookingDetails.get(bus.id).bookingDate : bus.bookingDate) }}
                    </div>
                  </div>
                  
                  <div class="text-right">
                    <div class="text-base font-bold text-orange-700">
                      {{ formatPrice(busBookingDetails.has(bus.id) ? busBookingDetails.get(bus.id).totalPrice : bus.totalPrice) }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="lg:col-span-1">
            <div class="sticky top-8">
              <div
                class="bg-white p-6 rounded-xl shadow-lg border border-gray-200"
              >
                <h2 class="text-xl font-semibold mb-4 border-b pb-3">
                  Tóm tắt đơn hàng
                </h2>
                <div class="space-y-3 text-gray-600">
                  <div class="flex justify-between items-center">
                    <span>Trạng thái:</span>
                    <span
                      :class="`bg-${
                        getStatusInfo(order.status).color
                      }-100 text-${getStatusInfo(order.status).color}-700`"
                      class="font-medium px-3 py-1 rounded-full text-sm flex items-center gap-2"
                    >
                      <i :class="getStatusInfo(order.status).icon"></i>
                      {{ getStatusInfo(order.status).text }}
                    </span>
                  </div>
                  <div class="flex justify-between items-center">
                    <span>Ngày đặt:</span>
                    <strong class="text-gray-800">{{
                      formatDateTime(order.createdAt)
                    }}</strong>
                  </div>
                  <div
                    v-if="order.payDate"
                    class="flex justify-between items-center"
                  >
                    <span>Ngày thanh toán:</span>
                    <strong class="text-gray-800">{{
                      formatDateTime(order.payDate)
                    }}</strong>
                  </div>

                  <div
                    v-if="order.voucher"
                    class="flex justify-between items-center"
                  >
                    <span>Tạm tính:</span>
                    <strong class="text-gray-800 line-through">{{
                      formatPrice(order.originalAmount)
                    }}</strong>
                  </div>
                  <div
                    v-if="order.voucher"
                    class="flex justify-between items-center text-green-600"
                  >
                    <span>Giảm giá ({{ order.voucher.code }}):</span>
                    <strong class="font-semibold"
                      >-
                      {{
                        formatPrice(order.originalAmount - order.amount)
                      }}</strong
                    >
                  </div>
                </div>

                <div class="border-t pt-4 mt-4">
                  <div class="flex justify-between items-center text-lg">
                    <span class="font-bold text-gray-800">Tổng cộng</span>
                    <span class="text-2xl font-bold text-blue-600">{{
                      formatPrice(order.amount)
                    }}</span>
                  </div>
                </div>

                <div v-if="isEditable" class="mt-4 pt-4 border-t">
                  <div
                    v-if="isLoadingVouchers"
                    class="text-center text-sm text-gray-500 py-2"
                  >
                    <i class="fas fa-spinner fa-spin mr-1"></i> Đang tìm mã giảm
                    giá...
                  </div>
                  <div
                    v-else-if="suggestedVouchers.length > 0 && !order.voucher"
                  >
                    <p class="block text-sm font-medium text-gray-700 mb-2">
                      Mã giảm giá cho bạn:
                    </p>
                    <div class="space-y-2 max-h-32 overflow-y-auto pr-2">
                      <button
                        v-for="voucher in suggestedVouchers"
                        :key="voucher.id"
                        @click="selectVoucher(voucher.code)"
                        class="w-full text-left p-2 border-l-4 border-green-500 bg-green-50 rounded-md hover:bg-green-100 transition"
                      >
                        <strong class="text-green-700">{{
                          voucher.code
                        }}</strong>
                        <p class="text-xs text-gray-600">
                          {{ getVoucherDiscountText(voucher) }}
                        </p>
                      </button>
                    </div>
                  </div>

                  <div class="flex gap-2 mt-3" v-if="!order.voucher">
                    <input
                      v-model="voucherCode"
                      @keyup.enter="handleApplyVoucher"
                      type="text"
                      class="block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                      placeholder="Hoặc nhập mã khác"
                    />
                    <button
                      @click="handleApplyVoucher"
                      :disabled="isApplyingVoucher"
                      class="flex-shrink-0 bg-gray-800 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-black disabled:opacity-50"
                    >
                      <i
                        v-if="isApplyingVoucher"
                        class="fas fa-spinner fa-spin"
                      ></i>
                      <span v-else>Áp dụng</span>
                    </button>
                  </div>
                </div>

                <button
                  @click="getPay"
                  v-if="isEditable"
                  class="mt-6 w-full bg-blue-600 text-white font-medium py-3 rounded-lg hover:bg-blue-700 transition"
                >
                  <i class="fa-solid fa-credit-card mr-2"></i> Thanh toán ngay
                </button>
                <button
                  v-else
                  class="mt-6 w-full bg-gray-800 text-white font-medium py-3 rounded-lg hover:bg-black transition"
                >
                  <i class="fa-solid fa-print mr-2"></i> In hóa đơn
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Transition for flight detail dropdown */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Transitions for hotel image slider */
.slide-left-enter-active,
.slide-left-leave-active,
.slide-right-enter-active,
.slide-right-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: absolute;
  width: 100%;
  height: 100%;
}
.slide-left-enter-from {
  transform: translateX(100%);
  opacity: 0.7;
}
.slide-left-leave-to {
  transform: translateX(-100%);
  opacity: 0.7;
}
.slide-right-enter-from {
  transform: translateX(-100%);
  opacity: 0.7;
}
.slide-right-leave-to {
  transform: translateX(100%);
  opacity: 0.7;
}
</style>
