<script setup>
import { ref, onMounted, computed, reactive } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();

// --- STATE MANAGEMENT ---
const order = ref(null);
const flightBookingDetails = ref([]); 
const isLoading = ref(true);
const error = ref(null);

// Reactive state for hotel image sliders
const hotelImageIndices = reactive({});
const slideDirectionMap = reactive({});

// --- COMPUTED PROPERTIES ---
// Checks if the order can be modified
const isEditable = computed(() => order.value?.status === "PENDING_PAYMENT");

// --- HELPER FUNCTIONS ---
// Formats price into VND currency
const formatPrice = (price) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    price || 0
  );

// Formats date string to DD/MM/YYYY
const formatDate = (dateString) =>
  new Date(dateString).toLocaleDateString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });

// Formats date-time string to DD/MM/YYYY, HH:mm
const formatDateTime = (dateString) =>
  new Date(dateString).toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });

// Gets display info (text, color, icon) for an order status
const getStatusInfo = (status) => {
  switch (status) {
    case "PAID":
      return { text: "Đã thanh toán", color: "green", icon: "fa-solid fa-circle-check" };
    case "PENDING_PAYMENT":
      return { text: "Chờ thanh toán", color: "orange", icon: "fa-solid fa-clock" };
    case "CANCELLED":
      return { text: "Đã hủy", color: "red", icon: "fa-solid fa-circle-xmark" };
    default:
      return { text: status, color: "gray", icon: "fa-solid fa-circle-question" };
  }
};

// --- API CALLS ---
const fetchOrderDetails = async () => {
  isLoading.value = true;
  error.value = null;
  const orderId = route.params.id;
  try {
    const orderResponse = await fetch(`http://localhost:8080/api/v1/orders/${orderId}`);
    if (!orderResponse.ok) throw new Error("Failed to fetch order details.");
    
    const data = await orderResponse.json();
    order.value = data.data;

    // Fetch details for each flight booking
    flightBookingDetails.value = [];
    if (order.value.flightBookings && order.value.flightBookings.length > 0) {
      for (const booking of order.value.flightBookings) {
        try {
          const detailRes = await fetch(`http://localhost:8080/api/bookings/flights/reservation-summary/${booking.id}`);
          if (detailRes.ok) {
            const detail = await detailRes.json();
            // Add showDetail property to control the dropdown UI
            flightBookingDetails.value.push({ ...detail, showDetail: false });
          }
        } catch (e) {
          console.error(`Failed to fetch details for flight booking ${booking.id}:`, e);
          // Continue even if one flight detail fails
        }
      }
    }
  } catch (e) {
    error.value = e.message;
  } finally {
    isLoading.value = false;
  }
};

onMounted(fetchOrderDetails);

// --- EVENT HANDLERS ---
// Navigates to the home page to add more services to the current cart
const addMoreServices = () => {
  localStorage.setItem("activeCartId", order.value.id);
  router.push("/");
};

// Deletes a service item from the order
const handleDeleteItem = async (itemId, itemType) => {
  if (!confirm(`Bạn có chắc chắn muốn xóa dịch vụ này khỏi đơn hàng?`)) return;

  try {
    const response = await fetch(
      `http://localhost:8080/api/v1/cart/items?itemId=${itemId}&itemType=${itemType}`,
      { method: "DELETE" }
    );
    if (!response.ok) throw new Error("Xóa dịch vụ thất bại.");

    alert("Đã xóa dịch vụ thành công.");
    await fetchOrderDetails(); // Refresh order data
  } catch (e) {
    alert(e.message);
  }
};

// Navigates to the payment page
const getPay = () => {
  router.push(`/payment/${order.value.id}`);
};

// Handles editing a service (deletes the old one and redirects to the product page)
const handleEditItem = async (item, itemType) => {
  if (!confirm(`Chức năng sửa sẽ xóa dịch vụ này và đưa bạn về trang sản phẩm để chọn lại. Bạn có muốn tiếp tục?`)) return;

  try {
    const deleteResponse = await fetch(
      `http://localhost:8080/api/v1/cart/items?itemId=${item.id}&itemType=${itemType}`,
      { method: "DELETE" }
    );
    if (!deleteResponse.ok) throw new Error("Lỗi khi xóa dịch vụ cũ.");

    // If deletion is successful, set the active cart and redirect
    localStorage.setItem("activeCartId", order.value.id);

    if (itemType === "TOUR") {
      router.push(`/tours/${item.tourId}`);
    } else if (itemType === "FLIGHT") {
      router.push(`/flights/${item.flightId}`); // Assumes DTO has flightId
    } else if (itemType === "HOTEL") {
      router.push(`/hotels/${item.hotelId}`); // Assumes DTO has hotelId
    }
    // Add other item types if necessary
  } catch (e) {
    alert(e.message);
  }
};

// --- Hotel Image Slider Functions ---
function nextHotelImage(hotel) {
  if (!hotel.imageUrls || hotel.imageUrls.length <= 1) return;
  if (!(hotel.id in hotelImageIndices)) hotelImageIndices[hotel.id] = 0;
  slideDirectionMap[hotel.id] = 'next';
  hotelImageIndices[hotel.id] = (hotelImageIndices[hotel.id] + 1) % hotel.imageUrls.length;
}

function prevHotelImage(hotel) {
  if (!hotel.imageUrls || hotel.imageUrls.length <= 1) return;
  if (!(hotel.id in hotelImageIndices)) hotelImageIndices[hotel.id] = 0;
  slideDirectionMap[hotel.id] = 'prev';
  hotelImageIndices[hotel.id] = (hotelImageIndices[hotel.id] - 1 + hotel.imageUrls.length) % hotel.imageUrls.length;
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
        <div class="mb-8 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
          <div>
            <div class="flex items-center gap-4">
              <h1 class="text-4xl font-bold text-gray-800">Chi tiết Đơn hàng</h1>
              <span class="font-semibold text-3xl text-blue-600">#{{ order.id }}</span>
            </div>
            <p class="text-gray-500 mt-1">Xem lại thông tin chi tiết cho các dịch vụ bạn đã đặt.</p>
          </div>
          <button v-if="isEditable" @click="addMoreServices" class="bg-green-600 text-white font-medium px-5 py-2.5 rounded-lg text-sm text-center hover:bg-green-700 transition-colors w-full sm:w-auto">
            <i class="fa-solid fa-plus mr-2"></i> Thêm dịch vụ khác
          </button>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div class="lg:col-span-2 space-y-6">
            
            <div v-if="order.tourBookings && order.tourBookings.length > 0">
              <h2 class="text-2xl font-bold mb-4 text-gray-700 flex items-center gap-3">
                <i class="fa-solid fa-mountain-sun text-blue-500"></i> Các tour đã đặt
              </h2>
              <div v-for="tour in order.tourBookings" :key="'tour-' + tour.id" class="bg-white p-6 rounded-xl shadow-lg border border-gray-200 mb-4">
                <div class="flex justify-between items-start">
                  <h3 class="text-xl font-bold text-blue-700 mb-4">{{ tour.tourName }}</h3>
                  <div v-if="isEditable" class="flex items-center gap-2 flex-shrink-0">
                    <button @click="handleEditItem(tour, 'TOUR')" class="text-sm text-yellow-600 hover:text-yellow-800"><i class="fa-solid fa-pencil"></i> Sửa</button>
                    <button @click="handleDeleteItem(tour.id, 'TOUR')" class="text-sm text-red-600 hover:text-red-800"><i class="fa-solid fa-trash"></i> Xóa</button>
                  </div>
                </div>
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-600">
                  <div class="flex items-center gap-3"><i class="fa-solid fa-calendar-days w-5 text-center text-blue-500"></i><span>Ngày khởi hành: <strong>{{ formatDate(tour.departureDate) }}</strong></span></div>
                  <div class="flex items-center gap-3"><i class="fa-solid fa-users w-5 text-center text-blue-500"></i><span>Hành khách: <strong>{{ tour.numberOfAdults }} người lớn, {{ tour.numberOfChildren }} trẻ em</strong></span></div>
                </div>
                <div class="border-t mt-4 pt-4">
                  <p class="text-lg text-right">Tổng giá tour: <span class="font-bold text-xl text-blue-700">{{ formatPrice(tour.totalPrice) }}</span></p>
                </div>
              </div>
            </div>

            <div v-if="flightBookingDetails.length > 0">
              <h2 class="text-2xl font-bold mb-4 text-gray-700 flex items-center gap-3">
                <i class="fa-solid fa-plane-up text-green-500"></i> Các chuyến bay đã đặt
              </h2>
              <div v-for="flightDetail in flightBookingDetails" :key="flightDetail.booking.bookingId" class="bg-white p-6 rounded-xl shadow-lg border border-gray-200 mb-4">
                <div class="flex justify-between items-start">
                  <h3 class="text-xl font-bold text-green-700 mb-4">{{ flightDetail.booking.flight.name }} - {{ flightDetail.booking.flight.airline?.name }}</h3>
                  <div v-if="isEditable" class="flex items-center gap-2 flex-shrink-0">
                    <button @click="handleDeleteItem(flightDetail.booking.bookingId, 'FLIGHT')" class="text-sm text-red-600 hover:text-red-800"><i class="fa-solid fa-trash"></i> Xóa</button>
                  </div>
                </div>
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-600">
                    <div class="flex items-center gap-3"><i class="fa-solid fa-chair w-5 text-center text-indigo-500"></i><span>Số ghế: <strong>{{ flightDetail.flightSlot?.seatNumber }}</strong></span></div>
                    <div class="flex items-center gap-3"><i class="fa-solid fa-couch w-5 text-center text-yellow-500"></i><span>Hạng vé: <strong>{{ flightDetail.flightSlot?.isBusiness ? 'Thương gia' : 'Phổ thông' }}</strong></span></div>
                    <div class="flex items-center gap-3"><i class="fa-solid fa-user w-5 text-center text-blue-500"></i><span>Khách: <strong>{{ flightDetail.customer?.fullName }}</strong> - <strong>{{ flightDetail.customer?.phone }}</strong></span></div>
                </div>
                <div class="border-t mt-4 pt-4 flex justify-between items-center">
                  <p class="text-lg">Tổng giá vé: <span class="font-bold text-xl text-green-700">{{ formatPrice(flightDetail.booking.totalPrice) }}</span></p>
                  <button @click="flightDetail.showDetail = !flightDetail.showDetail" class="text-blue-600 hover:underline flex items-center">
                    <i :class="flightDetail.showDetail ? 'fa-solid fa-chevron-up' : 'fa-solid fa-chevron-down'"></i><span class="ml-1">Chi tiết</span>
                  </button>
                </div>
                <transition name="fade">
                  <div v-if="flightDetail.showDetail" class="mt-4 bg-gray-50 p-4 rounded-lg border border-gray-200">
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                      <div><span class="font-semibold">Khởi hành:</span> <span>{{ formatDateTime(flightDetail.booking.flight.departureTime) }} tại {{ flightDetail.booking.flight.departureAirport?.name }}</span></div>
                      <div><span class="font-semibold">Hạ cánh:</span> <span>{{ formatDateTime(flightDetail.booking.flight.arrivalTime) }} tại {{ flightDetail.booking.flight.arrivalAirport?.name }}</span></div>
                      <div><span class="font-semibold">Email:</span> {{ flightDetail.customer?.email }}</div>
                      <div><span class="font-semibold">Passport:</span> {{ flightDetail.customer?.passport }}</div>
                      <div><span class="font-semibold">Giới tính:</span> {{ flightDetail.customer?.gender === true ? 'Nam' : (flightDetail.customer?.gender === false ? 'Nữ' : 'Khác') }}</div>
                      <div><span class="font-semibold">Ngày sinh:</span> {{ flightDetail.customer?.dob ? formatDate(flightDetail.customer.dob) : '' }}</div>
                      <div><span class="font-semibold">Hành lý xách tay:</span> {{ flightDetail.flightSlot?.carryOnLuggage }} kg</div>
                      <div><span class="font-semibold">Vị trí ghế:</span> {{ flightDetail.flightSlot?.isWindow ? 'Cửa sổ' : (flightDetail.flightSlot?.isAisle ? 'Lối đi' : 'Khác') }}</div>
                    </div>
                  </div>
                </transition>
              </div>
            </div>

            <div v-if="order.hotelBookings && order.hotelBookings.length > 0">
              <h2 class="text-2xl font-bold mb-4 text-gray-700 flex items-center gap-3">
                <i class="fa-solid fa-hotel text-indigo-500"></i> Các phòng khách sạn đã đặt
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
                    <div class="text-right font-bold text-2xl text-indigo-600 absolute right-6 bottom-6">{{ formatPrice(hotel.totalPrice) }}</div>
                  </div>
                  <div v-if="isEditable" class="flex gap-2 absolute right-6 top-6 z-10">
                    <button @click="handleEditItem(hotel, 'HOTEL')" class="text-sm text-yellow-600 hover:text-yellow-800 flex items-center"><i class="fa-solid fa-pencil mr-1"></i> Sửa</button>
                    <button @click="handleDeleteItem(hotel.id, 'HOTEL')" class="text-sm text-red-600 hover:text-red-800 flex items-center"><i class="fa-solid fa-trash mr-1"></i> Xóa</button>
                  </div>
                </div>
              </div>
            </div>

          </div>

          <div class="lg:col-span-1">
            <div class="sticky top-8">
              <div class="bg-white p-6 rounded-xl shadow-lg border border-gray-200">
                <h2 class="text-xl font-semibold mb-4 border-b pb-3">Tóm tắt đơn hàng</h2>
                <div class="space-y-3 text-gray-600">
                  <div class="flex justify-between items-center">
                    <span>Trạng thái:</span>
                    <span :class="`bg-${getStatusInfo(order.status).color}-100 text-${getStatusInfo(order.status).color}-700`" class="font-medium px-3 py-1 rounded-full text-sm flex items-center gap-2">
                      <i :class="getStatusInfo(order.status).icon"></i>
                      {{ getStatusInfo(order.status).text }}
                    </span>
                  </div>
                  <div class="flex justify-between items-center">
                    <span>Ngày đặt:</span>
                    <strong class="text-gray-800">{{ formatDateTime(order.createdAt) }}</strong>
                  </div>
                  <div v-if="order.payDate" class="flex justify-between items-center">
                    <span>Ngày thanh toán:</span>
                    <strong class="text-gray-800">{{ formatDateTime(order.payDate) }}</strong>
                  </div>
                  <div class="border-t pt-4 mt-4">
                    <div class="flex justify-between items-center text-lg">
                      <span class="font-bold text-gray-800">Tổng cộng</span>
                      <span class="text-2xl font-bold text-blue-600">{{ formatPrice(order.amount) }}</span>
                    </div>
                  </div>
                </div>
                <button @click="getPay" v-if="isEditable" class="mt-6 w-full bg-blue-600 text-white font-medium py-3 rounded-lg hover:bg-blue-700 transition">
                  <i class="fa-solid fa-credit-card mr-2"></i> Thanh toán ngay
                </button>
                <button v-else class="mt-6 w-full bg-gray-800 text-white font-medium py-3 rounded-lg hover:bg-black transition">
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