<template>
  <header class="h-16 bg-white shadow flex items-center justify-between px-6">
    <div class="flex items-center gap-3">
      <span class="font-bold text-indigo-600 text-lg">Admin Panel</span>
    </div>
    <div class="flex items-center gap-6">
      <!-- Thông báo -->
      <button class="relative">
        <i class="fas fa-bell text-xl text-gray-500"></i>
        <span
          class="absolute -top-1 -right-1 w-3 h-3 bg-red-500 rounded-full border-2 border-white"
        ></span>
      </button>

      <!-- Tên user + logout -->
      <div class="flex items-center gap-3">
        <span class="font-medium text-gray-700">
          {{ userStore.user?.name || "Người dùng" }}
        </span>
        <button
          @click="handleLogout"
          class="text-gray-500 hover:text-red-500 transition"
          title="Đăng xuất"
        >
          <i class="fas fa-sign-out-alt text-lg"></i>
        </button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useUserStore } from "@/store/UserStore";
import { useRouter } from "vue-router";

const userStore = useUserStore();
const router = useRouter();

const handleLogout = () => {
  userStore.logout();
  router.push("/login");
};
</script>
