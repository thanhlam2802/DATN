<template>
  <div className="w-full min-h-screen bg-gray-50">
    <div class="flex h-screen bg-gray-50">
      <div class="w-64 bg-white shadow-lg flex flex-col">
        <div>
          <div class="p-6 border-b">
            <h1 class="text-2xl font-bold text-gray-800">Tour Manager</h1>
            <p class="text-sm text-gray-600">Quản lý tour du lịch</p>
          </div>
          <nav class="mt-6">
            <button
              v-for="tab in tabs"
              :key="tab.id"
              @click="activeTab = tab.id"
              :class="[
                'w-full flex items-center px-6 py-3 text-left hover:bg-blue-50 transition-colors',
                activeTab === tab.id
                  ? 'bg-blue-50 text-blue-600 border-r-2 border-blue-600'
                  : 'text-gray-700',
              ]"
            >
              <component :is="tab.icon" class="w-5 h-5 mr-3" />
              {{ tab.name }}
            </button>
          </nav>
        </div>

        <div v-if="userStore.user" class="mt-auto p-4 border-t">
          <div class="flex items-center">
            <img
              class="w-10 h-10 rounded-full mr-3 object-cover"
              :src="userStore.user.avatarUrl"
              alt="User Avatar"
            />
            <div class="flex-1 overflow-hidden">
              <p class="font-semibold text-sm text-gray-800 truncate">
                {{ userStore.user.name }}
              </p>
              <p class="text-xs text-gray-500 truncate">
                {{ userStore.user.email }}
              </p>
            </div>
            <button
              @click="handleLogout"
              class="p-2 text-gray-500 hover:bg-red-50 hover:text-red-600 rounded-lg transition-colors"
              title="Đăng xuất"
            >
              <LogOut class="w-5 h-5" />
            </button>
          </div>
        </div>
      </div>

      <div class="flex-1 overflow-hidden">
        <div class="h-full overflow-y-auto p-6">
          <component :is="currentTabComponent" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/store/UserStore.js";

import { clearToken } from "@/services/TokenService.js";

// Import các tab component
import OverviewTab from "../views/OverviewTab.vue";
import ToursTab from "../views/ToursTab.vue";
import ScheduleTab from "../views/ScheduleTab.vue";
import DepartureDatesTab from "../views/DepartureDatesTab.vue";
import BookingsTab from "../views/BookingsTab.vue";
import ReviewsTab from "../views/ReviewsTab.vue";

// Import icon từ lucide-vue-next
import {
  BarChart3,
  MapPin,
  Clock,
  Calendar,
  BookOpen,
  Star,
  LogOut,
} from "lucide-vue-next";

// Khởi tạo store và router
const userStore = useUserStore();
const router = useRouter();

const activeTab = ref("overview");

const tabs = [
  { id: "overview", name: "Tổng quan", icon: BarChart3 },
  { id: "tours", name: "Quản lý Tour", icon: MapPin },
  { id: "schedules", name: "Lịch trình Tour", icon: Clock },
  { id: "departures", name: "Ngày khởi hành", icon: Calendar },
  { id: "bookings", name: "Đặt chỗ", icon: BookOpen },
  { id: "reviews", name: "Đánh giá", icon: Star },
];

const currentTabComponent = computed(() => {
  switch (activeTab.value) {
    case "overview":
      return OverviewTab;
    case "tours":
      return ToursTab;
    case "schedules":
      return ScheduleTab;
    case "departures":
      return DepartureDatesTab;
    case "bookings":
      return BookingsTab;
    case "reviews":
      return ReviewsTab;
    default:
      return OverviewTab;
  }
});

// Hàm đăng xuất hoàn chỉnh
const handleLogout = () => {
  // 1. Xóa thông tin người dùng khỏi Pinia store
  // Hàm logout trong store của bạn đã gọi clearToken() rồi, nên về lý thuyết chỉ cần gọi userStore.logout() là đủ.
  // Tuy nhiên, gọi cả hai để chắc chắn cũng không sao.
  userStore.logout();

  // 2. Xóa access token khỏi storage (đã được cập nhật)
  clearToken();

  // 3. Chuyển hướng người dùng về trang đăng nhập
  router.push("/login");
};
</script>
