<template>
  <aside class="w-64 bg-white shadow-lg h-screen flex flex-col">
    <div class="h-16 flex items-center justify-center border-b">
      <span class="text-xl font-bold text-indigo-600">Super Admin</span>
    </div>
    <nav class="flex-1 py-4 px-2">
      <ul class="space-y-1">
        <li v-for="item in menu" :key="item.name">
          <router-link
            :to="item.link"
            class="flex items-center gap-3 px-4 py-3 text-gray-700 hover:bg-indigo-50 hover:text-indigo-600 transition rounded-lg"
            active-class="bg-indigo-100 text-indigo-700 font-semibold"
          >
            <i :class="item.icon + ' w-6 text-center text-lg'" />
            <span>{{ item.name }}</span>
          </router-link>
        </li>
      </ul>
    </nav>
  </aside>
</template>

<script setup>
const menu = [
  {
    name: "Dashboard",
    icon: "fas fa-tachometer-alt",
    link: "/admin/dashboard",
  },
  { name: "Booking", icon: "fas fa-ticket-alt", link: "/admin/bookings" },
  { name: "Tour", icon: "fas fa-map-marked-alt", link: "/admin/tours" },
  { name: "Khách sạn", icon: "fas fa-hotel", link: "/admin/hotels" },
  { name: "Chuyến bay", icon: "fas fa-plane", link: "/admin/flights" },
  { name: "Xe bus", icon: "fas fa-bus", link: "/admin/buses" },
  {
    name: "Khuyến mãi & Voucher",
    icon: "fas fa-tags",
    link: "/admin/promotions",
  },
  { name: "Đánh giá", icon: "fas fa-comments", link: "/admin/reviews" },
  { name: "Nội dung & Blog", icon: "fas fa-blog", link: "/admin/content" },
  { name: "Người dùng", icon: "fas fa-users", link: "/admin/users" },
  { name: "Doanh thu", icon: "fas fa-coins", link: "/admin/revenue" },
  { name: "Báo cáo", icon: "fas fa-chart-line", link: "/admin/reports" },
  { name: "Hỗ trợ khách hàng", icon: "fas fa-headset", link: "/admin/support" },
  { name: "Cài đặt", icon: "fas fa-cog", link: "/admin/settings" },
];
</script>
