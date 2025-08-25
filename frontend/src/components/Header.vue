<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import UserMenu from "./User/UserMenu.vue";
import NotificationMenu from "./User/NotificationPopup.vue";

const isUserMenuVisible = ref(false);
const isNotificationVisible = ref(false);
const isMobileMenuOpen = ref(false);
const menuWrapperRef = ref(null);

function toggleUserMenu() {
  isUserMenuVisible.value = !isUserMenuVisible.value;
  isNotificationVisible.value = false;
}

function toggleNotificationMenu() {
  isNotificationVisible.value = !isNotificationVisible.value;
  isUserMenuVisible.value = false;
}

function toggleMobileMenu() {
  isMobileMenuOpen.value = !isMobileMenuOpen.value;
}

function handleClickOutside(event) {
  // Simple null check to prevent parentNode error
  if (menuWrapperRef.value && event.target && menuWrapperRef.value.contains && !menuWrapperRef.value.contains(event.target)) {
    isUserMenuVisible.value = false;
    isNotificationVisible.value = false;
  }
}

onMounted(() => {
  document.addEventListener("click", handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener("click", handleClickOutside);
});
</script>

<template>
  <header class="sticky-header shadow bg-white">
    <div class="flex items-center justify-between px-4 py-4 md:px-6">
      <div class="flex items-center gap-4">
  <button aria-label="open menu" @click="toggleMobileMenu"
    class="md:hidden border hover:scale-95 duration-300 relative group cursor-pointer text-sky-50 overflow-hidden w-12 h-12 rounded-md bg-sky-200 flex items-center justify-center">
    <div class="absolute right-6 -top-2 group-hover:top-1 group-hover:right-1 z-10 w-14 h-14 rounded-full group-hover:scale-150 duration-500 bg-sky-900"></div>
    <div class="absolute right-1 -top-2 group-hover:top-1 group-hover:right-1 z-10 w-12 h-12 rounded-full group-hover:scale-150 duration-500 bg-sky-800"></div>
    <div class="absolute -right-6 top-1 group-hover:top-1 group-hover:right-1 z-10 w-8 h-8 rounded-full group-hover:scale-150 duration-500 bg-sky-700"></div>
    <div class="absolute right-4 -top-2 group-hover:top-1 group-hover:right-1 z-10 w-6 h-6 rounded-full group-hover:scale-150 duration-500 bg-sky-600"></div>
    <i class="fas fa-bars z-20 text-sky-900"></i>
  </button>

  <router-link to="/" class="relative group border hover:scale-95 duration-300 cursor-pointer overflow-hidden h-16 w-64 rounded-md bg-sky-200 p-2 flex items-center">
    <div class="absolute right-32 -top-4 group-hover:top-1 group-hover:right-2 z-10 w-40 h-40 rounded-full group-hover:scale-150 duration-500 bg-sky-900"></div>
    <div class="absolute right-2 -top-4 group-hover:top-1 group-hover:right-2 z-10 w-32 h-32 rounded-full group-hover:scale-150 duration-500 bg-sky-800"></div>
    <div class="absolute -right-12 top-4 group-hover:top-1 group-hover:right-2 z-10 w-24 h-24 rounded-full group-hover:scale-150 duration-500 bg-sky-700"></div>
    <div class="absolute right-20 -top-4 group-hover:top-1 group-hover:right-2 z-10 w-16 h-16 rounded-full group-hover:scale-150 duration-500 bg-sky-600"></div>

    <img src="@/icon/logo.png" alt="logo" class="relative z-20 w-20 h-20 object-contain" />
    <span class="relative z-20 text-xl ml-3 font-extrabold text-white tracking-wide">TRAVELAKO</span>
  </router-link>
</div>


      <!-- Navigation (ẩn trên mobile) -->
      <nav class="hidden md:flex ml-8 space-x-6 text-sm font-medium text-gray-700">
        <router-link to="/" class="select-none"
          :class="{ 'text-blue-700 font-bold': $route.path === '/' }">Trending</router-link>
        <router-link to="/bus" class="select-none hover:text-gray-900"
          :class="{ 'text-blue-700 font-bold': $route.path === '/bus' }">Bus</router-link>

        <router-link to="/plane" class="select-none hover:text-gray-900"
          :class="{ 'text-blue-700 font-bold': $route.path === '/plane' }">Flights</router-link>
        <router-link to="/hotel" class="select-none hover:text-gray-900"
          :class="{ 'text-blue-700 font-bold': $route.path === '/hotel' }">Hotels</router-link>
        <router-link to="/tour" class="select-none hover:text-gray-900"
          :class="{ 'text-blue-700 font-bold': $route.path === '/tour' }">Tours</router-link>
      </nav>

      <!-- User/Notification -->
      <div class="relative" ref="menuWrapperRef">
        <div class="flex items-center space-x-4 text-gray-900 text-lg cursor-pointer">
          <i class="fas fa-globe-americas"></i>

          <!-- ✨ THÊM MỚI: Icon "Chuyến đi" (My Trips) -->
          <router-link to="/my-trips" class="hover:text-blue-600 transition-colors">
            <i class="fas fa-suitcase-rolling"></i>
          </router-link>

         
          <i class="fas fa-user-circle" @click.stop="toggleUserMenu"></i>
        </div>
        <UserMenu :show="isUserMenuVisible" @close="isUserMenuVisible = false" />
        <NotificationMenu :show="isNotificationVisible" />
      </div>
    </div>

    <!-- Mobile Nav -->
    <transition name="fade">
      <nav v-if="isMobileMenuOpen"
        class="md:hidden px-4 pb-4 flex flex-col space-y-2 text-sm font-medium text-gray-700">
        <router-link to="/" class="block py-1" :class="{ 'text-blue-700 font-bold': $route.path === '/' }"
          @click="isMobileMenuOpen = false">Trending</router-link>
        <router-link to="/bus" class="block py-1" :class="{ 'text-blue-700 font-bold': $route.path === '/bus' }"
          @click="isMobileMenuOpen = false">Bus</router-link>
        <router-link to="/bus-management" class="block py-1"
          :class="{ 'text-blue-700 font-bold': $route.path.startsWith('/bus-management') }"
          @click="isMobileMenuOpen = false">Bus Admin</router-link>
        <router-link to="/plane" class="block py-1" :class="{ 'text-blue-700 font-bold': $route.path === '/plane' }"
          @click="isMobileMenuOpen = false">Flights</router-link>
        <router-link to="/hotel" class="block py-1" :class="{ 'text-blue-700 font-bold': $route.path === '/hotel' }"
          @click="isMobileMenuOpen = false">Hotels</router-link>
        <router-link to="/tour" class="block py-1" :class="{ 'text-blue-700 font-bold': $route.path === '/tour' }"
          @click="isMobileMenuOpen = false">Tours</router-link>
      </nav>
    </transition>
  </header>
</template>

<style scoped>
.sticky-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  background-color: white;
}

/* Hiệu ứng mở nav mobile */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
