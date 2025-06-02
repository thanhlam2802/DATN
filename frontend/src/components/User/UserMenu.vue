<template>
  <div v-if="show"
       class="absolute right-0 top-full mt-2 w-52 bg-white border border-gray-200 rounded-xl shadow-lg z-50">
    <ul class="flex flex-col text-sm text-gray-800 divide-y divide-gray-100">
      <li @click="handleNavigate('/login')"
          class="flex items-center gap-2 px-4 py-3 hover:bg-blue-50 hover:text-blue-600 cursor-pointer">
        <i class="fas fa-sign-in-alt text-blue-500"></i>
        Login
      </li>
      <li @click="handleNavigate('/register')"
          class="flex items-center gap-2 px-4 py-3 hover:bg-blue-50 hover:text-blue-600 cursor-pointer">
        <i class="fas fa-user-plus text-blue-500"></i>
        Register
      </li>
      <li
          class="flex items-center gap-2 px-4 py-3 hover:bg-blue-50 hover:text-blue-600 cursor-pointer">
        <i class="fas fa-sign-out-alt text-red-500"></i>
        Logout
      </li>
      <li
          class="flex items-center gap-2 px-4 py-3 hover:bg-blue-50 hover:text-blue-600 cursor-pointer">
        <i class="fas fa-history text-yellow-500"></i>
        Booking History
      </li>
      <li @click="handleNavigate('/account/personal')"
          class="flex items-center gap-2 px-4 py-3 hover:bg-blue-50 hover:text-blue-600 cursor-pointer">
        <i class="fas fa-cog text-gray-500"></i>
        Account Settings
      </li>
    </ul>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { defineEmits, defineProps } from 'vue';

const props = defineProps({ show: Boolean });
const emit = defineEmits(['close']);
const router = useRouter();

function handleNavigate(path) {
    router.push(path);
    emit('close');
}
</script>