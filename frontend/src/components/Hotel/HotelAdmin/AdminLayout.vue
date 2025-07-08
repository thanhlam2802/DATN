<template>
  <div class="min-h-screen w-full flex font-sans p-6 gap-6">
    <AdminSidebar />
    <div class="flex-1 flex flex-col">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import AdminSidebar from '@/components/Hotel/HotelAdmin/AdminSidebar.vue';
</script>
