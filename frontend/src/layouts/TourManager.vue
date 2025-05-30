<template>
  <div className="w-full min-h-screen bg-gray-50">
    <div class="flex h-screen bg-gray-50">
      <!-- Sidebar -->
      <div class="w-64 bg-white shadow-lg">
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

      <!-- Main Content -->
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
} from "lucide-vue-next";

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
</script>
