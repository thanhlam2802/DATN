<template>
  <div class="max-w-md mx-auto bg-white p-6 rounded-xl shadow">
    <h2 class="text-xl font-semibold text-gray-800 mb-4">Thay đổi mật khẩu</h2>

    <!-- Mật khẩu hiện tại -->
    <div class="relative mb-3">
      <input
          v-model="oldPassword"
          :type="showOldPassword ? 'text' : 'password'"
          placeholder="Mật khẩu hiện tại"
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
      />
      <button
          type="button"
          class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
          @click="showOldPassword = !showOldPassword"
      >
        <i :class="showOldPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
      </button>
      <p v-if="oldPasswordError" class="text-red-500 text-sm">{{ oldPasswordError }}</p>
    </div>

    <!-- Mật khẩu mới -->
    <div class="relative mb-3">
      <input
          v-model="newPassword"
          :type="showNewPassword ? 'text' : 'password'"
          placeholder="Mật khẩu mới"
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
      />
      <button
          type="button"
          class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
          @click="showNewPassword = !showNewPassword"
      >
        <i :class="showNewPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
      </button>
      <p v-if="newPasswordError" class="text-red-500 text-sm">{{ newPasswordError }}</p>
    </div>

    <!-- Nút lưu -->
    <button
        @click="handleChangePassword"
        class="w-full px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
    >
      Lưu mật khẩu
    </button>

    <p v-if="message"
       :class="['text-sm mt-3 text-center', success ? 'text-green-600' : 'text-red-600']">
      {{ message }}
    </p>
  </div>
</template>

<script>
import { AccountApi } from "@/api/AccountApi.js";

export default {
  name: "ChangePassword",
  data() {
    return {
      oldPassword: "",
      newPassword: "",
      oldPasswordError: "",
      newPasswordError: "",
      showOldPassword: false,
      showNewPassword: false,
      message: "",
      success: false,
    };
  },
  methods: {
    validatePasswords() {
      let isValid = true;

      if (!this.oldPassword) {
        this.oldPasswordError = "Vui lòng nhập mật khẩu hiện tại.";
        isValid = false;
      } else {
        this.oldPasswordError = "";
      }

      if (!this.newPassword || this.newPassword.length < 4) {
        this.newPasswordError = "Mật khẩu mới phải có ít nhất 4 ký tự.";
        isValid = false;
      } else {
        this.newPasswordError = "";
      }

      return isValid;
    },
    async handleChangePassword() {
      if (!this.validatePasswords()) return;

      try {
        const res = await AccountApi.changePassword({
          oldPassword: this.oldPassword,
          newPassword: this.newPassword,
        });

        if (res.errorCode) {
          this.message = "Mật khẩu hiện tại không đúng.";
          this.success = false;
        } else {
          this.message = "Đổi mật khẩu thành công!";
          this.success = true;
          this.oldPassword = "";
          this.newPassword = "";
        }
      } catch (err) {
        this.message = "Có lỗi xảy ra. Vui lòng thử lại.";
        this.success = false;
      }
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
