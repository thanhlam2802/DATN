<script setup>
  import { ref, onMounted, computed } from "vue";
  import { useRoute, useRouter } from "vue-router";
  import { getFlightBookingDetail } from '@/api/flightApi'

  const route = useRoute();
  const router = useRouter();

  const order = ref(null);
  const flightBookingDetails = ref([]); // Mảng chi tiết booking flight
  const isLoading = ref(true);
  const error = ref(null);

  // BIẾN MỚI: Dùng để kiểm tra xem đơn hàng có thể chỉnh sửa không
  const isEditable = computed(() => order.value?.status === "PENDING_PAYMENT");

  // Helper functions for formatting
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

  const fetchOrderDetails = async () => {
    isLoading.value = true;
    error.value = null;
    const orderId = route.params.id;
    try {
      const response = await fetch(`http://localhost:8080/api/v1/orders/${orderId}`);
      const data = await response.json();
      order.value = data.data;
      // Lấy chi tiết slot/ghế cho từng booking flight
      flightBookingDetails.value = [];
      if (order.value.flightBookings && order.value.flightBookings.length > 0) {
        for (const booking of order.value.flightBookings) {
          try {
            const res = await fetch(`http://localhost:8080/api/bookings/flights/reservation-summary/${booking.id}`);
            if (res.ok) {
              const detail = await res.json();
              // Thêm thuộc tính showDetail để điều khiển dropdown
              flightBookingDetails.value.push({ ...detail, showDetail: false });
            }
          } catch (e) {
            // Bỏ qua lỗi từng booking nhỏ lẻ
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

  // --- CÁC HÀM XỬ LÝ ---

  // Hàm để thêm dịch vụ mới
  const addMoreServices = () => {
    localStorage.setItem("activeCartId", order.value.id);
    router.push("/"); // Chuyển về trang chủ để chọn thêm dịch vụ
  };

  // Hàm để xóa một dịch vụ
  const handleDeleteItem = async (itemId, itemType) => {
    if (!confirm(`Bạn có chắc chắn muốn xóa dịch vụ này khỏi đơn hàng?`)) return;

    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/cart/items?itemId=${itemId}&itemType=${itemType}`,
        {
          method: "DELETE",
        }
      );
      if (!response.ok) throw new Error("Xóa dịch vụ thất bại.");

      alert("Đã xóa dịch vụ thành công.");
      // Tải lại dữ liệu đơn hàng để cập nhật tổng tiền và danh sách
      await fetchOrderDetails();
    } catch (e) {
      alert(e.message);
    }
  };
  function getPay() {
    const orderId = route.params.id;
    router.push("/payment/" + orderId)
  }

  // Hàm để sửa một dịch vụ (Xóa và thêm lại)
  const handleEditItem = async (item, itemType) => {
    if (
      !confirm(
        `Chức năng sửa sẽ xóa dịch vụ này và đưa bạn về trang sản phẩm để chọn lại. Bạn có muốn tiếp tục?`
      )
    )
      return;

    try {
      const deleteResponse = await fetch(
        `http://localhost:8080/api/v1/cart/items?itemId=${item.id}&itemType=${itemType}`,
        {
          method: "DELETE",
        }
      );
      if (!deleteResponse.ok) throw new Error("Lỗi khi xóa dịch vụ cũ.");

      // Nếu xóa thành công, lưu lại giỏ hàng và chuyển hướng
      localStorage.setItem("activeCartId", order.value.id);

      // Chuyển hướng về trang chi tiết sản phẩm tương ứng
      if (itemType === "TOUR") {
        router.push(`/tours/${item.tourId}`);
      } else if (itemType === "FLIGHT") {
        router.push(`/flights/${item.flightId}`); // Giả sử DTO có flightId
      }
      // Thêm các loại hình khác nếu có
    } catch (e) {
      alert(e.message);
    }
  };
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
              <h1 class="text-4xl font-bold text-gray-800">
                Chi tiết Đơn hàng
              </h1>
              <span class="font-semibold text-3xl text-blue-600">#{{ order.id }}</span>
            </div>
            <p class="text-gray-500 mt-1">
              Xem lại thông tin chi tiết cho các dịch vụ bạn đã đặt.
            </p>
          </div>
          <button v-if="isEditable" @click="addMoreServices"
            class="bg-green-600 text-white font-medium px-5 py-2.5 rounded-lg text-sm text-center hover:bg-green-700 transition-colors w-full sm:w-auto">
            <i class="fa-solid fa-plus mr-2"></i> Thêm dịch vụ khác
          </button>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div class="lg:col-span-2 space-y-6">
            <div v-if="order.tourBookings && order.tourBookings.length > 0">
              <h2 class="text-2xl font-bold mb-4 text-gray-700 flex items-center gap-3">
                <i class="fa-solid fa-mountain-sun text-blue-500"></i> Các tour
                đã đặt
              </h2>
              <div v-for="tour in order.tourBookings" :key="'tour-' + tour.id"
                class="bg-white p-6 rounded-xl shadow-lg border border-gray-200 mb-4">
                <div class="flex justify-between items-start">
                  <h3 class="text-xl font-bold text-blue-700 mb-4">
                    {{ tour.tourName }}
                  </h3>
                  <div v-if="isEditable" class="flex items-center gap-2 flex-shrink-0">
                    <button @click="handleEditItem(tour, 'TOUR')" class="text-sm text-yellow-600 hover:text-yellow-800">
                      <i class="fa-solid fa-pencil"></i> Sửa
                    </button>
                    <button @click="handleDeleteItem(tour.id, 'TOUR')" class="text-sm text-red-600 hover:text-red-800">
                      <i class="fa-solid fa-trash"></i> Xóa
                    </button>
                  </div>
                </div>
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-600">
                  <div class="flex items-center gap-3">
                    <i class="fa-solid fa-calendar-days w-5 text-center text-blue-500"></i><span>Ngày khởi hành:
                      <strong>{{
                        formatDate(tour.departureDate)
                        }}</strong></span>
                  </div>
                  <div class="flex items-center gap-3">
                    <i class="fa-solid fa-users w-5 text-center text-blue-500"></i><span>Hành khách:
                      <strong>{{ tour.numberOfAdults }} người lớn,
                        {{ tour.numberOfChildren }} trẻ em</strong></span>
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

            <!-- PHẦN MỚI: Các chuyến bay đã đặt -->
            <div v-if="flightBookingDetails.length > 0">
              <h2 class="text-2xl font-bold mb-4 text-gray-700 flex items-center gap-3">
                <i class="fa-solid fa-plane-up text-green-500"></i> Các chuyến bay đã đặt
              </h2>
              <div v-for="(flightDetail, idx) in flightBookingDetails" :key="flightDetail.booking.bookingId"
                class="bg-white p-6 rounded-xl shadow-lg border border-gray-200 mb-4">
                <div class="flex justify-between items-start">
                  <h3 class="text-xl font-bold text-green-700 mb-4">
                    {{ flightDetail.booking.flight.name }} - {{ flightDetail.booking.flight.airline?.name }}
                  </h3>
                  <div v-if="isEditable" class="flex items-center gap-2 flex-shrink-0">
                    <button @click="handleDeleteItem(flightDetail.booking.bookingId, 'FLIGHT')"
                      class="text-sm text-red-600 hover:text-red-800">
                      <i class="fa-solid fa-trash"></i> Xóa
                    </button>
                  </div>
                </div>
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-600">
                  <div class="flex items-center gap-3">
                    <i class="fa-solid fa-chair w-5 text-center text-indigo-500"></i>
                    <span>Số ghế: <strong>{{ flightDetail.flightSlot?.seatNumber }}</strong></span>
                  </div>
                  <div class="flex items-center gap-3">
                    <i class="fa-solid fa-couch w-5 text-center text-yellow-500"></i>
                    <span>Hạng vé: <strong>{{ flightDetail.flightSlot?.isBusiness ? 'Thương gia' : 'Phổ thông' }}</strong></span>
                  </div>
                  <div class="flex items-center gap-3">
                    <i class="fa-solid fa-user w-5 text-center text-blue-500"></i>
                    <span>Khách: <strong>{{ flightDetail.customer?.fullName }}</strong> - <strong>{{ flightDetail.customer?.phone }}</strong></span>
                  </div>
                </div>
                <div class="border-t mt-4 pt-4 flex justify-between items-center">
                  <p class="text-lg">
                    Tổng giá vé:
                    <span class="font-bold text-xl text-green-700">{{ formatPrice(flightDetail.booking.totalPrice) }}</span>
                  </p>
                  <button @click="flightDetail.showDetail = !flightDetail.showDetail" class="text-blue-600 hover:underline flex items-center">
                    <i :class="flightDetail.showDetail ? 'fa-solid fa-chevron-up' : 'fa-solid fa-chevron-down'"></i>
                    <span class="ml-1">Chi tiết</span>
                  </button>
                </div>
                <transition name="fade">
                  <div v-if="flightDetail.showDetail" class="mt-4 bg-gray-50 p-4 rounded-lg border border-gray-200">
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                      <div>
                        <span class="font-semibold">Khởi hành:</span>
                        <span>{{ formatDateTime(flightDetail.booking.flight.departureTime) }} tại {{ flightDetail.booking.flight.departureAirport?.name }}</span>
                      </div>
                      <div>
                        <span class="font-semibold">Hạ cánh:</span>
                        <span>{{ formatDateTime(flightDetail.booking.flight.arrivalTime) }} tại {{ flightDetail.booking.flight.arrivalAirport?.name }}</span>
                      </div>
                      <div>
                        <span class="font-semibold">Email:</span> {{ flightDetail.customer?.email }}
                      </div>
                      <div>
                        <span class="font-semibold">Passport:</span> {{ flightDetail.customer?.passport }}
                      </div>
                      <div>
                        <span class="font-semibold">Giới tính:</span> {{ flightDetail.customer?.gender === true ? 'Nam' : (flightDetail.customer?.gender === false ? 'Nữ' : 'Khác') }}
                      </div>
                      <div>
                        <span class="font-semibold">Ngày sinh:</span> {{ flightDetail.customer?.dob ? formatDate(flightDetail.customer.dob) : '' }}
                      </div>
                      <div>
                        <span class="font-semibold">Hành lý xách tay:</span> {{ flightDetail.flightSlot?.carryOnLuggage }} kg
                      </div>
                      <div>
                        <span class="font-semibold">Vị trí ghế:</span> {{ flightDetail.flightSlot?.isWindow ? 'Cửa sổ' : (flightDetail.flightSlot?.isAisle ? 'Lối đi' : 'Khác') }}
                      </div>
                    </div>
                  </div>
                </transition>
              </div>
            </div>
            <!-- END PHẦN MỚI -->
          </div>

          <div class="lg:col-span-1">
            <div class="sticky top-8">
              <div class="bg-white p-6 rounded-xl shadow-lg border border-gray-200">
                <h2 class="text-xl font-semibold mb-4 border-b pb-3">
                  Tóm tắt đơn hàng
                </h2>
                <div class="space-y-3 text-gray-600">
                  <div class="flex justify-between items-center">
                    <span>Trạng thái:</span>
                    <span :class="`bg-${
                        getStatusInfo(order.status).color
                      }-100 text-${getStatusInfo(order.status).color}-700`"
                      class="font-medium px-3 py-1 rounded-full text-sm flex items-center gap-2">
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
                  <div v-if="order.payDate" class="flex justify-between items-center">
                    <span>Ngày thanh toán:</span>
                    <strong class="text-gray-800">{{
                      formatDateTime(order.payDate)
                      }}</strong>
                  </div>
                  <div class="border-t pt-4 mt-4">
                    <div class="flex justify-between items-center text-lg">
                      <span class="font-bold text-gray-800">Tổng cộng</span>
                      <span class="text-2xl font-bold text-blue-600">{{
                        formatPrice(order.amount)
                        }}</span>
                    </div>
                  </div>
                </div>
                <button @click="getPay" v-if="isEditable"
                  class="mt-6 w-full bg-blue-600 text-white font-medium py-3 rounded-lg hover:bg-blue-700 transition">
                  <i class="fa-solid fa-credit-card mr-2"></i> Thanh toán ngay
                </button>
                <button v-else
                  class="mt-6 w-full bg-gray-800 text-white font-medium py-3 rounded-lg hover:bg-black transition">
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