<template>
  <header class="flex flex-col w-full h-19 bg-white border-b border-slate-200 sticky top-0 z-10 justify-center">
    <div class="flex-1 flex items-center justify-end px-8 h-full">
      <AdminBreadcrumb :items="breadcrumbItems" />
      <div class="flex items-center space-x-3 ml-auto">
        <button class="relative mr-6 text-slate-500 hover:text-blue-600 focus:outline-none">
          <i class="fas fa-bell text-xl"></i>
        </button>
        <img src="https://randomuser.me/api/portraits/men/32.jpg" alt="Avatar" class="w-10 h-10 rounded-full object-cover border border-slate-300" />
        <div class="flex flex-col">
          <span class="font-semibold text-slate-900">John Doe</span>
          <span class="text-xs text-slate-500">Administrator</span>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import AdminBreadcrumb from './AdminBreadcrumb.vue';
import { storeToRefs } from 'pinia';
import { useAdminBreadcrumbStore } from '@/store/useAdminBreadcrumbStore';
const breadcrumbStore = useAdminBreadcrumbStore();
const { items: breadcrumbItems } = storeToRefs(breadcrumbStore);
</script> 