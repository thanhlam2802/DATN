<template>
  <div class="min-h-screen w-full flex font-sans">
    <AdminSidebar />
    <div class="w-2 bg-slate-200 min-h-screen fixed left-64 top-0 bottom-0 z-30"></div>
    <div class="flex-1 flex flex-col ml-64">
      <AdminHeader />
      <div class="w-full h-2 bg-slate-200"></div>
      <router-view />
    </div>
  </div>
</template>

<script setup>
import AdminSidebar from '@/components/Hotel/HotelAdmin/AdminSidebar.vue';
import AdminHeader from '@/components/Hotel/HotelAdmin/AdminHeader.vue';
</script>
