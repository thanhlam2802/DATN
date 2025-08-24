<template>
  <aside
      aria-label="Account settings navigation"
      class="bg-white rounded-lg w-full mb-10 md:mb-0"
  >
    <ul class="px-2 py-4 space-y-1">
      <li v-for="tab in tabs" :key="tab.name">
        <router-link
            :to="`/account/${tab.name}`"
            class="flex items-center space-x-3 px-4 py-3 rounded-lg transition-colors duration-200"
            :class="{
            'text-blue-600 bg-blue-50': $route.path === `/account/${tab.name}`,
            'text-gray-700 hover:text-gray-900 hover:bg-gray-100':
              $route.path !== `/account/${tab.name}`,
          }"
        >
          <span
              class="flex items-center justify-center w-7 h-7 rounded-full"
              :class="
              $route.path === `/account/${tab.name}`
                ? 'bg-blue-100 text-blue-600'
                : 'bg-gray-100 text-gray-500'
            "
          >
            <i :class="tab.icon + ' text-sm'"></i>
          </span>
          <span
              :class="[
              'text-sm',
              $route.path === `/account/${tab.name}`
                ? 'font-font-small'
                : 'font-normal',
            ]"
          >
            {{ tab.label }}
          </span>
        </router-link>
      </li>
    </ul>
  </aside>
</template>

<script>
export default {
  name: "AccountSidebar",
  data() {
    return {
      tabs: [
        { name: "personal", label: "Thông tin cá nhân", icon: "fas fa-user" },
        { name: "bookings", label: "Chuyến đi của tôi", icon: "fas fa-suitcase-rolling" },
        { name: "reviews", label: "Đánh giá dịch vụ", icon: "fas fa-star" },
        { name: "payment", label: "Ngân hàng", icon: "far fa-credit-card" },
        { name: "change-password", label: "Thay đổi mật khẩu", icon: "fas fa-lock" },
        { name: "notifications", label: "Cài đặt thông báo", icon: "far fa-bell" },
      ],
    };
  },
};
</script>
