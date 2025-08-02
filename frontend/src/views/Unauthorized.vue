<template>
  <div class="min-h-screen w-full bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
    <div class="sm:mx-auto sm:w-full sm:max-w-md">
      <div class="text-center">
        <i class="fas fa-shield-alt text-6xl text-red-500 mb-4"></i>
        <h2 class="text-3xl font-bold text-gray-900 mb-2">Truy cập bị từ chối</h2>
        <p class="text-lg text-gray-600 mb-8">Bạn không có quyền truy cập vào trang này</p>
      </div>
    </div>

    <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
      <div class="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
        <div class="text-center">
          <p class="text-gray-600 mb-6">
            Vui lòng liên hệ với quản trị viên để được cấp quyền truy cập hoặc đăng nhập với tài khoản có quyền admin.
          </p>
          
          <div class="space-y-4">
            <button
              @click="goHome"
              class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              <i class="fas fa-home mr-2"></i>
              Về trang chủ
            </button>
            
            <button
              @click="goLogin"
              class="w-full flex justify-center py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              <i class="fas fa-sign-in-alt mr-2"></i>
              Đăng nhập lại
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const goHome = () => {
  window.location.href = '/';
};

const goLogin = () => {
  window.location.href = '/login';
};
</script> 