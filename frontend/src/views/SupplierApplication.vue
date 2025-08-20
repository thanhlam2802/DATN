<template>
  <div
    class="flex items-center justify-center min-h-screen bg-gray-100 p-6 transition-all duration-500"
  >
    <!-- Form View -->
    <form
      v-if="!isSubmitted"
      @submit.prevent="submitApplication"
      class="bg-white p-8 rounded-2xl shadow-xl w-full max-w-2xl"
    >
      <h2 class="text-center font-extrabold text-2xl mb-2 text-gray-800">
        Đơn Đăng Ký Nhà Cung Cấp
      </h2>
      <p class="text-center text-sm text-gray-500 mb-8">
        Vui lòng cung cấp thông tin doanh nghiệp của bạn. Đội ngũ của chúng tôi
        sẽ xem xét đơn đăng ký.
      </p>

      <!-- Service Type Selection -->
      <div class="mb-6">
        <label
          class="block text-sm font-semibold text-gray-700 mb-2"
          for="serviceType"
        >
          Loại Hình Dịch Vụ Đăng Ký <span class="text-red-500">*</span>
        </label>
        <select
          v-model="formData.serviceType"
          id="serviceType"
          class="w-full px-4 py-3 text-sm rounded-lg border border-gray-300 bg-white focus:outline-none focus:ring-2 focus:ring-purple-600 transition"
          required
        >
          <option disabled value="">Vui lòng chọn một dịch vụ</option>
          <option value="HOTEL_SUPPLIER">Dịch vụ Khách sạn</option>
          <option value="FLIGHT_SUPPLIER">Dịch vụ Chuyến bay</option>
          <option value="TOUR_SUPPLIER">Dịch vụ Tour du lịch</option>
          <option value="BUS_SUPPLIER">Dịch vụ Xe khách</option>
        </select>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- Business Name -->
        <div class="mb-4">
          <label
            class="block text-sm font-semibold text-gray-700 mb-2"
            for="businessName"
          >
            Tên Doanh Nghiệp <span class="text-red-500">*</span>
          </label>
          <input
            v-model="formData.businessName"
            type="text"
            id="businessName"
            placeholder="Ví dụ: Công ty Du lịch Awesome"
            class="w-full px-4 py-3 text-sm rounded-lg border border-gray-300 bg-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-600 transition"
            required
          />
        </div>

        <!-- Tax ID -->
        <div class="mb-4">
          <label
            class="block text-sm font-semibold text-gray-700 mb-2"
            for="taxId"
          >
            Mã Số Thuế / Mã ĐKKD <span class="text-red-500">*</span>
          </label>
          <input
            v-model="formData.taxId"
            type="text"
            id="taxId"
            class="w-full px-4 py-3 text-sm rounded-lg border border-gray-300 bg-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-600 transition"
            required
          />
        </div>
      </div>

      <!-- Business Address -->
      <div class="mb-4">
        <label
          class="block text-sm font-semibold text-gray-700 mb-2"
          for="address"
        >
          Địa Chỉ Kinh Doanh <span class="text-red-500">*</span>
        </label>
        <textarea
          v-model="formData.address"
          id="address"
          rows="3"
          class="w-full px-4 py-3 text-sm rounded-lg border border-gray-300 bg-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-600 transition"
          required
        ></textarea>
      </div>

      <!-- Contact Person & Phone -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="mb-4">
          <label
            class="block text-sm font-semibold text-gray-700 mb-2"
            for="contactPerson"
          >
            Người Liên Hệ <span class="text-red-500">*</span>
          </label>
          <input
            v-model="formData.contactPerson"
            type="text"
            id="contactPerson"
            class="w-full px-4 py-3 text-sm rounded-lg border border-gray-300 bg-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-600 transition"
            required
          />
        </div>
        <div class="mb-4">
          <label
            class="block text-sm font-semibold text-gray-700 mb-2"
            for="businessPhone"
          >
            Số Điện Thoại Kinh Doanh <span class="text-red-500">*</span>
          </label>
          <input
            v-model="formData.businessPhone"
            type="tel"
            id="businessPhone"
            class="w-full px-4 py-3 text-sm rounded-lg border border-gray-300 bg-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-600 transition"
            required
          />
        </div>
      </div>

      <!-- Submit Button -->
      <div class="mt-6">
        <button
          type="submit"
          class="w-full bg-purple-700 hover:bg-purple-800 text-white font-bold py-3 px-4 rounded-lg transition-all duration-300 transform hover:scale-105 shadow-lg"
        >
          Gửi đi để Xác minh
        </button>
      </div>
    </form>

    <!-- Success View -->
    <div
      v-else
      class="text-center bg-white p-10 rounded-2xl shadow-xl max-w-lg w-full transition-all duration-500 transform scale-100"
    >
      <div class="mx-auto mb-6 h-24 w-24">
        <svg
          class="w-full h-full text-purple-600"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="1"
            d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
          ></path>
        </svg>
      </div>
      <h1 class="text-2xl font-bold text-gray-800 mb-3">
        Đã Gửi Đơn Thành Công!
      </h1>
      <p class="text-gray-600 mb-8 px-4">
        Cảm ơn bạn! Đơn của bạn đã được tiếp nhận. Đội ngũ của chúng tôi sẽ xem
        xét thông tin và bạn sẽ nhận được cập nhật qua email trong vòng
        <strong>24 giờ</strong> tới.
      </p>
      <button
        @click="$router.push('/')"
        class="bg-purple-700 hover:bg-purple-800 text-white font-bold py-3 px-8 rounded-lg transition-all duration-300 transform hover:scale-105 shadow-lg"
      >
        Quay về Trang Chủ
      </button>
    </div>
  </div>
</template>

<script>
import { SupplierApi } from "@/api/SupplierApi.js"; // Giả sử đường dẫn này đúng
import { useLoadingStore } from "@/store/GlobalStore.js"; // Giả sử đường dẫn này đúng

export default {
  name: "SupplierApplication",
  data() {
    return {
      isSubmitted: false,
      formData: {
        serviceType: "",
        businessName: "",
        taxId: "",
        address: "",
        contactPerson: "",
        businessPhone: "",
      },
    };
  },
  methods: {
    async submitApplication() {
      const loadingStore = useLoadingStore();
      loadingStore.startLoading();

      try {
        console.log("Submitting application data:", this.formData);
        const response = await SupplierApi.submitApplication(this.formData);
        console.log("Server response:", response);

        this.isSubmitted = true;
      } catch (error) {
        console.error("Failed to submit application:", error);
        alert(
          `Gửi đơn thất bại: ${
            error.message || "Vui lòng kiểm tra lại thông tin và thử lại."
          }`
        );
      } finally {
        loadingStore.stopLoading();
      }
    },
  },
};
</script>
