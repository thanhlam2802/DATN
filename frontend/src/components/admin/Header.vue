<template>
  <header class="h-16 bg-white shadow flex items-center justify-between px-6">
    <div class="flex items-center gap-3">
      <span class="font-bold text-indigo-600 text-lg">Admin Panel</span>
    </div>
    <div class="flex items-center gap-6">
      <button class="relative">
        <i class="fas fa-bell text-xl text-gray-500"></i>
        <span
          class="absolute -top-1 -right-1 w-3 h-3 bg-red-500 rounded-full border-2 border-white"
        ></span>
      </button>
      <div class="flex items-center gap-2">
        <img src="https://i.pravatar.cc/40" class="w-8 h-8 rounded-full" />
        <span class="font-medium text-gray-700">Admin</span>
      </div>
    </div>
  </header>
</template>
<script setup>
// Không cần logic
</script>
