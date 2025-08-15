<template>
  <div v-if="show"
       class="absolute right-0 top-full mt-2 w-52 bg-white border border-gray-200 rounded-xl shadow-lg z-50">
    <ul class="flex flex-col text-sm text-gray-800 divide-y divide-gray-100">

      <li v-if="!userStore.isLoggedIn" @click="handleNavigate('/login')"
          class="flex items-center gap-2 px-4 py-3 hover:bg-blue-50 hover:text-blue-600 cursor-pointer">
        <i class="fas fa-sign-in-alt text-blue-500"></i>
        Login
      </li>
      <li v-if="!userStore.isLoggedIn" @click="handleNavigate('/register')"
          class="flex items-center gap-2 px-4 py-3 hover:bg-blue-50 hover:text-blue-600 cursor-pointer">
        <i class="fas fa-user-plus text-blue-500"></i>
        Register
      </li>
      <li v-if="userStore.isLoggedIn" @click="handleNavigate('/account/personal')"
          class="flex items-center gap-2 px-4 py-3 hover:bg-blue-50 hover:text-blue-600 cursor-pointer">
        <i class="fas fa-cog text-gray-500"></i>
        Account Settings
      </li>
      <li v-if="userStore.isLoggedIn" @click="handleLogout()"
          class="flex items-center gap-2 px-4 py-3 hover:bg-blue-50 hover:text-blue-600 cursor-pointer">
        <i class="fas fa-sign-out-alt text-red-500"></i>
        Logout
      </li>
    </ul>
  </div>
</template>

<script setup>
import {useRouter} from 'vue-router';
import {defineEmits, defineProps} from 'vue';
import {clearToken} from "@/services/TokenService.js";
import {useUserStore} from "@/store/UserStore.js";

const props = defineProps({show: Boolean});
const emit = defineEmits(['close']);
const router = useRouter();

const userStore = useUserStore()

function handleNavigate(path) {
  router.push(path);
  emit('close');
}

function handleLogout() {
  clearToken();
  userStore.logout();
  handleNavigate("/login")
}
</script>