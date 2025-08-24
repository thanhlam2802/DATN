<template>
  <header class="flex flex-col w-full h-19 bg-white border-b border-slate-200 sticky top-0 z-20 justify-center">
    <div class="flex-1 flex items-center justify-end px-8 h-full">
      <AdminBreadcrumb :items="breadcrumbItems" />
      <div class="flex items-center space-x-3 ml-auto">
        <NotificationDropdown />
        
        <div class="relative">
          <button 
            @click="toggleUserDropdown"
            class="flex items-center space-x-3 p-2 rounded-lg hover:bg-slate-100 transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
          >
            <div class="h-10 w-10 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center">
              <span class="text-white font-semibold text-sm">{{ getUserInitials() }}</span>
            </div>
            <div class="flex flex-col items-start">
              <span class="font-semibold text-slate-900 text-sm">{{ userDisplayName }}</span>
              <span class="text-xs text-slate-500">{{ userRole }}</span>
            </div>
            <i class="fas fa-chevron-down text-slate-400 text-xs transition-transform duration-200" :class="{ 'rotate-180': isUserDropdownOpen }"></i>
          </button>

          <div 
            v-if="isUserDropdownOpen"
            class="absolute right-0 mt-2 w-56 bg-white rounded-lg shadow-xl border border-slate-200 z-[9999]"
          >
            <div class="px-4 py-3 border-b border-slate-200">
              <div class="flex items-center space-x-3">
                <div class="h-10 w-10 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center">
                  <span class="text-white font-semibold text-sm">{{ getUserInitials() }}</span>
                </div>
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-medium text-slate-900 truncate">{{ userDisplayName }}</p>
                  <p class="text-xs text-slate-500 truncate">{{ userStore.user?.email }}</p>
                  <p class="text-xs text-slate-400 truncate">{{ userRole }}</p>
                </div>
              </div>
            </div>

            <div class="py-1">
              <button 
                @click="goToUserSite"
                class="w-full px-4 py-2 text-left text-sm text-slate-700 hover:bg-slate-100 transition-colors duration-200 flex items-center space-x-2"
              >
                <i class="fas fa-external-link-alt text-blue-500 w-4"></i>
                <span>Đi đến trang người dùng</span>
              </button>
              
              <button 
                @click="goToAccountSettings"
                class="w-full px-4 py-2 text-left text-sm text-slate-700 hover:bg-slate-100 transition-colors duration-200 flex items-center space-x-2"
              >
                <i class="fas fa-user-cog text-slate-400 w-4"></i>
                <span>Cài đặt tài khoản</span>
              </button>
              
              <button 
                @click="goToProfile"
                class="w-full px-4 py-2 text-left text-sm text-slate-700 hover:bg-slate-100 transition-colors duration-200 flex items-center space-x-2"
              >
                <i class="fas fa-user text-slate-400 w-4"></i>
                <span>Hồ sơ cá nhân</span>
              </button>
              
              <div class="border-t border-slate-200 my-1"></div>
              
              <button 
                @click="handleLogout"
                class="w-full px-4 py-2 text-left text-sm text-red-600 hover:bg-red-50 transition-colors duration-200 flex items-center space-x-2"
              >
                <i class="fas fa-sign-out-alt text-red-400 w-4"></i>
                <span>Đăng xuất</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import AdminBreadcrumb from './AdminBreadcrumb.vue';
import NotificationDropdown from './NotificationDropdown.vue';
import { storeToRefs } from 'pinia';
import { useAdminBreadcrumbStore } from '@/store/useAdminBreadcrumbStore';
import { useUserStore } from '@/store/UserStore.js';
import { computed, ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';

const breadcrumbStore = useAdminBreadcrumbStore();
const { items: breadcrumbItems } = storeToRefs(breadcrumbStore);

const userStore = useUserStore();
const router = useRouter();
const isUserDropdownOpen = ref(false);

const userDisplayName = computed(() => {
  if (!userStore.user) return 'Guest';
  return userStore.user.name || userStore.user.email || 'User';
});

const userRole = computed(() => {
  if (!userStore.user || !userStore.user.roles) return 'Guest';
  
  const roles = userStore.user.roles;
  
  if (roles.includes('SUPER_ADMIN')) return 'Super Administrator';
  if (roles.includes('ADMIN_HOTELS')) return 'Hotel Administrator';
  if (roles.includes('HOTEL_SUPPLIER')) return 'Hotel Supplier';
  if (roles.includes('ADMIN_FLIGHTS')) return 'Flight Administrator';
  if (roles.includes('FLIGHT_SUPPLIER')) return 'Flight Supplier';
  if (roles.includes('ADMIN_TOURS')) return 'Tour Administrator';
  if (roles.includes('TOUR_SUPPLIER')) return 'Tour Supplier';
  if (roles.includes('ADMIN_BUSES')) return 'Bus Administrator';
  if (roles.includes('BUS_SUPPLIER')) return 'Bus Supplier';
  
  return 'User';
});

const getUserInitials = () => {
  if (!userStore.user) return 'G';
  const name = userStore.user.name || userStore.user.email;
  if (!name) return 'U';
  return name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
};

const goToUserSite = () => {
  router.push('/');
  closeUserDropdown();
};

const toggleUserDropdown = () => {
  isUserDropdownOpen.value = !isUserDropdownOpen.value;
};

const closeUserDropdown = () => {
  isUserDropdownOpen.value = false;
};

const goToAccountSettings = () => {
  router.push('/account/personal');
  closeUserDropdown();
};

const goToProfile = () => {
  router.push('/account/personal');
  closeUserDropdown();
};

const handleLogout = async () => {
  try {
    userStore.logout();
    
    await router.push('/login');
    
    if (window.$toast) {
      window.$toast('Đăng xuất thành công', 'success');
    }
  } catch (error) {
    console.error('Error during logout:', error);
    if (window.$toast) {
      window.$toast('Có lỗi xảy ra khi đăng xuất', 'error');
    }
  }
};

const handleClickOutside = (event) => {
  if (isUserDropdownOpen.value && !event.target.closest('.relative')) {
    closeUserDropdown();
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script> 