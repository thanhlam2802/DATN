<template>
  <aside class="w-64 bg-white shadow-lg h-screen flex flex-col">
    <div class="h-16 flex items-center justify-center border-b">
      <span class="text-xl font-bold text-indigo-600">Super Admin</span>
    </div>
    <nav class="flex-1 py-4">
      <ul class="space-y-1">
        <li v-for="item in menu" :key="item.name">
          <a
            :href="item.link"
            class="flex items-center gap-3 px-6 py-3 text-gray-700 hover:bg-indigo-50 transition rounded-lg"
          >
            <i :class="item.icon + ' text-lg'" />
            <span>{{ item.name }}</span>
          </a>
        </li>
      </ul>
    </nav>
  </aside>
</template>

<script setup>
const menu = [
  { name: "Dashboard", icon: "fas fa-tachometer-alt", link: "#" },
  { name: "Booking", icon: "fas fa-ticket-alt", link: "#" },
  // Core Products
  { name: "Tour", icon: "fas fa-map-marked-alt", link: "#" },
  { name: "Khách sạn", icon: "fas fa-hotel", link: "#" },
  { name: "Chuyến bay", icon: "fas fa-plane", link: "#" },
  { name: "Xe bus", icon: "fas fa-bus", link: "#" },
  // Marketing & Sales
  { name: "Khuyến mãi & Voucher", icon: "fas fa-tags", link: "#" },
  { name: "Đánh giá", icon: "fas fa-comments", link: "#" },
  { name: "Nội dung & Blog", icon: "fas fa-blog", link: "#" },
  // Management
  { name: "Người dùng", icon: "fas fa-users", link: "#" },
  { name: "Doanh thu", icon: "fas fa-coins", link: "#" },
  { name: "Báo cáo", icon: "fas fa-chart-line", link: "#" },
  // System
  { name: "Hỗ trợ khách hàng", icon: "fas fa-headset", link: "#" },
  { name: "Cài đặt", icon: "fas fa-cog", link: "#" },
];
</script>
