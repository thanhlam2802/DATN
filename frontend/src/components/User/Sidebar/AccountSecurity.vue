<template>
  <div>
    <h2 class="text-2xl font-semibold text-gray-800 mb-3">Bảo mật tài khoản</h2>
    <div class="space-y-4">
      <div
          v-for="item in securityItems"
          :key="item.title"
          class="p-5 bg-white border border-gray-200 rounded-xl shadow-sm hover:shadow-md transition"
      >
        <div class="flex items-center justify-between gap-4">
          <div class="flex items-center gap-4 flex-1">
            <div
                class="w-10 h-10 flex-shrink-0 flex items-center justify-center bg-gray-100 rounded-full text-blue-600 text-xl"
            >
              <i :class="item.icon"></i>
            </div>
            <div>
              <p class="text-base font-semibold text-gray-900">{{ item.title }}</p>
              <p class="text-sm text-gray-600 mt-1">{{ item.description }}</p>
            </div>
          </div>

          <div class="flex-shrink-0">
            <label
                v-if="item.title === 'Xác thực hai bước'"
                class="relative inline-flex items-center cursor-pointer"
            >
              <input type="checkbox" class="sr-only peer" v-model="is2FAEnabled" />
              <div
                  class="w-11 h-6 bg-gray-300 rounded-full peer peer-checked:bg-green-600 transition-colors duration-300"
              ></div>
              <div
                  class="absolute left-0.5 top-0.5 w-5 h-5 bg-white rounded-full shadow transform peer-checked:translate-x-full transition duration-300"
              ></div>
            </label>

            <button
                v-else-if="item.title === 'Thay đổi mật khẩu'"
                @click="toggleForm(item.title)"
                class="text-sm font-medium text-blue-600 hover:text-blue-800 hover:underline transition whitespace-nowrap"
            >
              {{ activeItem === item.title ? 'Đóng' : item.action }}
            </button>

            <button
                v-else
                class="text-sm font-medium text-blue-600 hover:text-blue-800 hover:underline transition whitespace-nowrap"
            >
              {{ item.action }}
            </button>
          </div>
        </div>

        <div
            v-if="item.title === 'Thay đổi mật khẩu' && activeItem === item.title"
            class="mt-4 space-y-3"
        >
          <input
              v-model="oldPassword"
              type="password"
              placeholder="Mật khẩu hiện tại"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <input
              v-model="newPassword"
              type="password"
              placeholder="Mật khẩu mới"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <button
              @click="handleChangePassword"
              class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
          >
            Lưu mật khẩu
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "AccountSecurity",
  data() {
    return {
      is2FAEnabled: false,
      activeItem: null,
      oldPassword: "",
      newPassword: "",
      securityItems: [
        {
          title: "Thay đổi mật khẩu",
          description: "Cập nhật mật khẩu hiện tại để tăng tính bảo mật.",
          action: "Thay đổi",
          icon: "fas fa-key",
        },
        {
          title: "Xác thực hai bước",
          description: "Thêm lớp bảo mật với xác thực qua thiết bị di động.",
          action: "Cài đặt",
          icon: "fas fa-shield-alt",
        },
        {
          title: "Hoạt động gần đây",
          description: "Xem các lần đăng nhập và hoạt động gần nhất.",
          action: "Xem",
          icon: "fas fa-history",
        },
        {
          title: "Thiết bị đã đăng nhập",
          description: "Quản lý các thiết bị hiện đang đăng nhập vào tài khoản.",
          action: "Xem danh sách",
          icon: "fas fa-laptop",
        },
        {
          title: "Email khôi phục",
          description: "Thiết lập email phụ để khôi phục mật khẩu khi cần.",
          action: "Cập nhật",
          icon: "fas fa-envelope-open-text",
        }
      ]
    };
  },
  methods: {
    toggleForm(title) {
      this.activeItem = this.activeItem === title ? null : title;
    },
    handleChangePassword() {
      if (!this.oldPassword || !this.newPassword) {
        alert("Vui lòng điền đầy đủ thông tin.");
        return;
      }

      console.log("Đổi mật khẩu:", {
        old: this.oldPassword,
        new: this.newPassword,
      });

      this.oldPassword = "";
      this.newPassword = "";
      this.activeItem = null;

      alert("Thay đổi mật khẩu thành công!");
    }
  }
};
</script>

<style scoped>
i.fas {
  width: 1em;
  text-align: center;
}
</style>
