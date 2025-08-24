<template>
  <div class="container mx-auto px-4 py-8 relative">
    <h2 class="text-2xl font-bold mb-8">Ưu đãi hấp dẫn, đặt sớm giảm sâu</h2>
    <button
      @click="scrollLeft"
      class="absolute left-0 top-1/2 transform -translate-y-1/2 z-10 bg-white shadow p-2 rounded-full hidden md:block"
    >
      <i class="fas fa-chevron-left"></i>
    </button>
    <button
      @click="scrollRight"
      class="absolute right-0 top-1/2 transform -translate-y-1/2 z-10 bg-white shadow p-2 rounded-full hidden md:block"
    >
      <i class="fas fa-chevron-right"></i>
    </button>
    <div
      ref="scrollContainer"
      class="flex space-x-4 overflow-x-auto pb-4 pt-8 px-4 scrollbar-hide scroll-smooth"
    >
      <div
        v-for="voucher in vouchers"
        :key="voucher.id"
        class="min-w-[320px] flex-shrink-0 relative"
      >
        <div
          class="absolute -top-4 -left-4 bg-amber-500 text-white px-3 py-1 rounded-full text-xs font-bold shadow-md z-10 whitespace-nowrap"
        >
          Hạn đến: {{ formatDate(voucher.expiryDate) }}
        </div>

        <div class="bg-gray-50 p-6 rounded-lg shadow h-full">
          <div class="flex items-start mb-4">
            <div class="bg-gray-200 p-3 rounded-lg mr-4 flex-shrink-0">
              <i class="fas fa-tags text-3xl text-gray-600"></i>
            </div>
            <div class="flex-grow">
              <div class="flex justify-between items-start mb-2">
                <h3 class="font-bold text-lg text-gray-900 leading-tight pr-2">
                  {{ voucher.name }}
                </h3>
                <i
                  class="fas fa-info-circle text-gray-400 text-sm cursor-pointer flex-shrink-0"
                  :title="voucher.description"
                ></i>
              </div>
              <div
                v-for="(line, index) in voucher.descriptionLines"
                :key="index"
                class="text-sm text-gray-600"
              >
                {{ line }}
              </div>
            </div>
          </div>

          <div class="border-t-2 border-dashed border-gray-200 my-4"></div>

          <div class="flex justify-between items-center">
            <div
              class="flex items-center bg-blue-50 text-blue-700 px-3 py-1 rounded text-sm font-mono"
            >
              <i class="far fa-copy mr-2"></i> {{ voucher.code }}
            </div>
            <button
              @click="copyCode(voucher.code)"
              class="whitespace-nowrap bg-blue-100 hover:bg-blue-200 text-blue-800 font-semibold py-2 px-5 rounded-full text-sm cursor-pointer ml-4"
            >
              Copy
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import voucherApi from "@/api/voucherApi.js"; // Import module API đã tạo

export default {
  name: "CouponSection",
  data() {
    return {
      vouchers: [], // Khởi tạo mảng rỗng để chứa dữ liệu từ API
    };
  },
  mounted() {
    this.fetchVouchers(); // Gọi hàm lấy dữ liệu khi component được gắn vào DOM
  },
  methods: {
    async fetchVouchers() {
      try {
        const response = await voucherApi.getAllVouchers();

        if (Array.isArray(response.data.content)) {
          // Bỏ dòng .filter() để xem tất cả vouchers có hiện không
          this.vouchers = response.data.content.map((voucher) => ({
            ...voucher,
            descriptionLines: this.getVoucherDescription(voucher),
          }));
        } else {
          console.error("Dữ liệu content từ API không phải là mảng.");
          this.vouchers = [];
        }
      } catch (error) {
        console.error("Lỗi khi tải voucher:", error);
      }
    },
    getVoucherDescription(voucher) {
      const lines = [];
      if (voucher.type === "PERCENTAGE") {
        lines.push(`Giảm ${voucher.discountPercentage}%`);
        if (voucher.maxDiscountAmount) {
          lines.push(
            `Tối đa: ${this.formatCurrency(voucher.maxDiscountAmount)}`
          );
        }
      } else if (voucher.type === "FIXED_AMOUNT") {
        lines.push(
          `Giảm trực tiếp ${this.formatCurrency(voucher.discountAmount)}`
        );
      }

      if (voucher.conditionMinAmount) {
        lines.push(
          `Cho đơn từ ${this.formatCurrency(voucher.conditionMinAmount)}`
        );
      }
      return lines;
    },

    formatCurrency(value) {
      if (value == null) return "";
      return new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      }).format(value);
    },

    formatDate(dateString) {
      if (!dateString) return "";
      const [year, month, day] = dateString.split("-");
      return `${day}/${month}/${year}`;
    },

    async copyCode(code) {
      try {
        await navigator.clipboard.writeText(code);
        alert(`Đã sao chép mã: ${code}`);
      } catch (err) {
        console.error("Không thể sao chép mã: ", err);
        alert("Lỗi khi sao chép mã!");
      }
    },

    scrollLeft() {
      if (this.$refs.scrollContainer) {
        this.$refs.scrollContainer.scrollLeft -= 336;
      }
    },

    scrollRight() {
      if (this.$refs.scrollContainer) {
        this.$refs.scrollContainer.scrollLeft += 336;
      }
    },
  },
};
</script>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}

/* For IE, Edge and Firefox */
.scrollbar-hide {
  -ms-overflow-style: none; /* IE and Edge */
  scrollbar-width: none; /* Firefox */
}
</style>
