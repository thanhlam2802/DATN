<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();

// State cho dữ liệu được truyền từ trang trước
const bookingDetails = ref(null);

// State cho các trường trong form liên hệ
const contactInfo = ref({
  fullName: "",
  phone: "",
  email: "",
  notes: "",
});

// State để xử lý trạng thái của nút bấm (tránh nhấn nhiều lần)
const isProcessing = ref(false);

onMounted(() => {
  // Đọc dữ liệu từ query params trên URL khi component được tạo
  bookingDetails.value = {
    orderId: route.query.orderId, // Lấy orderId từ query
    tourId: route.query.tourId,
    tourName: route.query.tourName,
    selectedDate: route.query.selectedDate,
    departureId: route.query.departureId,
    travelers: JSON.parse(route.query.travelers || "{}"),
    totalPrice: Number(route.query.totalPrice),
  };
});

// Hàm định dạng giá tiền
const formatPrice = (price) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    price || 0
  );

/**
 * Hàm xử lý chính khi người dùng nhấn nút "Tiến hành thanh toán".
 * Sẽ gọi API để tạo đơn hàng tạm và giữ chỗ.
 */
const handleProceedToPayment = async () => {
  // Kiểm tra dữ liệu đầu vào đơn giản
  if (
    !contactInfo.value.fullName ||
    !contactInfo.value.phone ||
    !contactInfo.value.email
  ) {
    alert(
      "Vui lòng điền đầy đủ thông tin liên hệ (Họ tên, Số điện thoại, Email)."
    );
    return;
  }

  isProcessing.value = true;

  // Nếu đã có orderId (giữ chỗ xong), chỉ cần chuyển sang payment
  if (bookingDetails.value.orderId) {
    router.push({
      name: "payment",
      params: { orderId: bookingDetails.value.orderId },
    });
    isProcessing.value = false;
    return;
  }

  // Nếu chưa có orderId, gọi API giữ chỗ như cũ
  // Chuẩn bị dữ liệu để gửi lên backend, khớp với DirectTourReservationRequestDto
  const reservationRequest = {
    userId: 1, // TODO: Thay bằng ID của người dùng đã đăng nhập
    tourId: bookingDetails.value.tourId,
    departureId: bookingDetails.value.departureId,
    numberOfAdults: bookingDetails.value.travelers.adults,
    numberOfChildren: bookingDetails.value.travelers.children,
    customerName: contactInfo.value.fullName,
    phone: contactInfo.value.phone,
    email: contactInfo.value.email, // SỬA LỖI: Thêm email vào request
    notes: contactInfo.value.notes,
  };

  try {
    // SỬA LỖI: Gọi đúng endpoint "/reserve-tour-direct" cho luồng "Mua ngay"
    const response = await fetch(
      "http://localhost:8080/api/v1/orders/reserve-tour-direct",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          // 'Authorization': 'Bearer ' + your_auth_token,
        },
        body: JSON.stringify(reservationRequest),
      }
    );

    const result = await response.json();

    if (response.ok && result.statusCode === 201) {
      const order = result.data;
      // Chuyển hướng đến trang thanh toán cuối cùng với ID của đơn hàng tạm
      router.push({
        name: "payment", // Cần có một route tên 'payment' trong router
        params: { orderId: order.id },
      });
    } else {
      // Xử lý lỗi từ backend
      alert(
        `Lỗi giữ chỗ: ${result.message || "Vui lòng kiểm tra lại thông tin."}`
      );
    }
  } catch (error) {
    // Xử lý lỗi mạng hoặc lỗi khác
    console.error("Lỗi khi gọi API giữ chỗ:", error);
    alert("Không thể kết nối đến máy chủ. Vui lòng thử lại sau.");
  } finally {
    isProcessing.value = false;
  }
};
</script>

<template>
  <div class="container mx-auto px-4 py-8">
    <h1 class="text-3xl font-bold mb-6">Xác nhận và Thanh toán</h1>

    <div v-if="bookingDetails" class="grid grid-cols-1 md:grid-cols-3 gap-8">
      <!-- Cột trái: Form nhập thông tin -->
      <div class="md:col-span-2 bg-white p-6 rounded-lg shadow">
        <h2 class="text-xl font-semibold mb-4 border-b pb-2">
          Thông tin liên hệ
        </h2>
        <form @submit.prevent="handleProceedToPayment" class="space-y-4">
          <div>
            <label
              for="fullName"
              class="block text-sm font-medium text-gray-700"
              >Họ và tên</label
            >
            <input
              v-model="contactInfo.fullName"
              type="text"
              id="fullName"
              required
              class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
          <div>
            <label for="phone" class="block text-sm font-medium text-gray-700"
              >Số điện thoại</label
            >
            <input
              v-model="contactInfo.phone"
              type="tel"
              id="phone"
              required
              class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
          <div>
            <label for="email" class="block text-sm font-medium text-gray-700"
              >Email</label
            >
            <input
              v-model="contactInfo.email"
              type="email"
              id="email"
              required
              class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
          <div>
            <label for="notes" class="block text-sm font-medium text-gray-700"
              >Ghi chú thêm</label
            >
            <textarea
              v-model="contactInfo.notes"
              id="notes"
              rows="3"
              class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            ></textarea>
          </div>
        </form>
      </div>

      <!-- Cột phải: Tóm tắt đơn hàng -->
      <div class="md:col-span-1">
        <div class="bg-white p-6 rounded-lg shadow sticky top-4">
          <h2 class="text-xl font-semibold mb-4 border-b pb-2">
            Tóm tắt chuyến đi
          </h2>
          <div class="space-y-3">
            <p class="font-bold text-lg">{{ bookingDetails.tourName }}</p>
            <div>
              <p class="text-sm text-gray-600">Ngày khởi hành</p>
              <p class="font-medium">
                {{
                  new Date(bookingDetails.selectedDate).toLocaleDateString(
                    "vi-VN"
                  )
                }}
              </p>
            </div>
            <div>
              <p class="text-sm text-gray-600">Số lượng khách</p>
              <p class="font-medium">
                {{ bookingDetails.travelers.adults }} người lớn
                <span v-if="bookingDetails.travelers.children > 0"
                  >, {{ bookingDetails.travelers.children }} trẻ em</span
                >
              </p>
            </div>
            <div class="border-t pt-3 flex justify-between items-center">
              <span class="text-lg font-bold">Tổng cộng</span>
              <span class="text-xl font-bold text-blue-600">{{
                formatPrice(bookingDetails.totalPrice)
              }}</span>
            </div>
          </div>
          <button
            @click="handleProceedToPayment"
            :disabled="isProcessing"
            class="mt-6 w-full bg-blue-600 text-white py-3 rounded-lg font-medium hover:bg-blue-700 disabled:opacity-50 flex items-center justify-center"
          >
            <i v-if="isProcessing" class="fas fa-spinner fa-spin mr-2"></i>
            <span>{{
              isProcessing ? "Đang xử lý..." : "Tiến hành thanh toán"
            }}</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
