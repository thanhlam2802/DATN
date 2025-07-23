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
  if (menuWrapperRef.value && !menuWrapperRef.value.contains(event.target)) {
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
      <!-- Logo + Hamburger -->
      <div class="flex items-center space-x-3">
        <!-- Hamburger (hiện ở mobile) -->
        <button class="md:hidden text-xl" @click="toggleMobileMenu">
          <i class="fas fa-bars"></i>
        </button>

        <!-- Logo -->
        <router-link to="/" class="flex items-center">
          <img
            src="https://storage.googleapis.com/a1aa/image/9358955a-d854-4fb1-1842-35be93c6eea2.jpg"
            alt="Logo"
            class="w-8 h-8 md:w-10 md:h-10"
          />
        </router-link>
      </div>

      <!-- Navigation (ẩn trên mobile) -->
      <nav
        class="hidden md:flex ml-8 space-x-6 text-sm font-medium text-gray-700"
      >
        <router-link
          to="/"
          class="select-none"
          :class="{ 'text-blue-700 font-bold': $route.path === '/' }"
          >Trending</router-link
        >
        <router-link
          to="/bus"
          class="select-none hover:text-gray-900"
          :class="{ 'text-blue-700 font-bold': $route.path === '/bus' }"
          >Bus</router-link
        >
        <router-link
          to="/bus-management"
          class="select-none hover:text-gray-900"
          :class="{ 'text-blue-700 font-bold': $route.path.startsWith('/bus-management') }"
          >Bus Admin</router-link
        >
        <router-link
          to="/plane"
          class="select-none hover:text-gray-900"
          :class="{ 'text-blue-700 font-bold': $route.path === '/plane' }"
          >Flights</router-link
        >
        <router-link
          to="/hotel"
          class="select-none hover:text-gray-900"
          :class="{ 'text-blue-700 font-bold': $route.path === '/hotel' }"
          >Hotels</router-link
        >
        <router-link
          to="/tour"
          class="select-none hover:text-gray-900"
          :class="{ 'text-blue-700 font-bold': $route.path === '/tour' }"
          >Tours</router-link
        >
      </nav>

      <!-- User/Notification -->
      <div class="relative" ref="menuWrapperRef">
        <div
          class="flex items-center space-x-4 text-gray-900 text-lg cursor-pointer"
        >
          <i class="fas fa-globe-americas"></i>
          <i class="fas fa-bell" @click.stop="toggleNotificationMenu"></i>
          <i class="fas fa-user-circle" @click.stop="toggleUserMenu"></i>
        </div>
        <UserMenu
          :show="isUserMenuVisible"
          @close="isUserMenuVisible = false"
        />
        <NotificationMenu :show="isNotificationVisible" />
      </div>
    </div>

    <!-- Mobile Nav -->
    <transition name="fade">
      <nav
        v-if="isMobileMenuOpen"
        class="md:hidden px-4 pb-4 flex flex-col space-y-2 text-sm font-medium text-gray-700"
      >
        <router-link
          to="/"
          class="block py-1"
          :class="{ 'text-blue-700 font-bold': $route.path === '/' }"
          @click="isMobileMenuOpen = false"
          >Trending</router-link
        >
        <router-link
          to="/bus"
          class="block py-1"
          :class="{ 'text-blue-700 font-bold': $route.path === '/bus' }"
          @click="isMobileMenuOpen = false"
          >Bus</router-link
        >
        <router-link
          to="/bus-management"
          class="block py-1"
          :class="{ 'text-blue-700 font-bold': $route.path.startsWith('/bus-management') }"
          @click="isMobileMenuOpen = false"
          >Bus Admin</router-link
        >
        <router-link
          to="/plane"
          class="block py-1"
          :class="{ 'text-blue-700 font-bold': $route.path === '/plane' }"
          @click="isMobileMenuOpen = false"
          >Flights</router-link
        >
        <router-link
          to="/hotel"
          class="block py-1"
          :class="{ 'text-blue-700 font-bold': $route.path === '/hotel' }"
          @click="isMobileMenuOpen = false"
          >Hotels</router-link
        >
        <router-link
          to="/tour"
          class="block py-1"
          :class="{ 'text-blue-700 font-bold': $route.path === '/tour' }"
          @click="isMobileMenuOpen = false"
          >Tours</router-link
        >
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
