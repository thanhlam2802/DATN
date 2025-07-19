<template>
  <div class="min-h-screen flex bg-gray-100">
    <Sidebar />
    <div class="flex-1 flex flex-col">
      <Header />
      <main class="flex-1 p-6 overflow-y-auto">
        <slot />
      </main>
    </div>
  </div>
</template>
<script setup>
import Sidebar from "../components/admin/Sidebar.vue";
import Header from "../components/admin/Header.vue";
</script>
