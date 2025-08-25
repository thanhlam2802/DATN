<template>
  <aside class="w-64 bg-white shadow-lg flex flex-col">
    <div class="h-16 flex items-center justify-center border-b">
      <span class="text-xl font-bold text-indigo-600">Super Admin</span>
    </div>
    <nav class="flex-1 py-4 px-2">
      <ul class="space-y-1">
        <li v-for="item in menu" :key="item.name">
          <router-link
            :to="item.link"
            class="flex items-center justify-between px-4 py-3 text-gray-700 hover:bg-indigo-50 hover:text-indigo-600 transition rounded-lg"
            active-class="bg-indigo-100 text-indigo-700 font-semibold"
          >
            <div class="flex items-center gap-3">
              <i :class="item.icon + ' w-6 text-center text-lg'" />
              <span>{{ item.name }}</span>
            </div>

            <span
              v-if="item.badge && pendingCount > 0"
              class="bg-red-500 text-white text-xs font-bold px-2 py-0.5 rounded-full"
            >
              {{ pendingCount }}
            </span>
          </router-link>
        </li>
      </ul>
    </nav>
  </aside>
</template>
<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";

const pendingCount = ref(0);

const fetchPendingCount = async () => {
  try {
    const res = await axios.get("/api/v1/suppliers/pending-count");
    pendingCount.value = res.data.data;
  } catch (error) {
    console.error("Không thể lấy số lượng đơn chờ duyệt:", error);
  }
};

onMounted(() => {
  fetchPendingCount();
});

const menu = [
  {
    name: "Dashboard",
    icon: "fas fa-tachometer-alt",
    link: "/admin/dashboard",
  },
  { name: "Tour", icon: "fas fa-map-marked-alt", link: "/admin/tours" },
  { name: "Khách sạn", icon: "fas fa-hotel", link: "/admin/hotels" },
  { name: "Chuyến bay", icon: "fas fa-plane", link: "/admin/flights" },
  { name: "Xe bus", icon: "fas fa-bus", link: "/admin/buses" },
  {
    name: "Khuyến mãi & Voucher",
    icon: "fas fa-tags",
    link: "/admin/promotions",
  },
  { name: "Người dùng", icon: "fas fa-users", link: "/admin/users" },
  {
    name: "Duyệt đăng ký",
    icon: "fas fa-user-check",
    link: "/admin/approve-suppliers",
    badge: true,
  },
];
</script>
