<template>
  <nav class="flex items-center space-x-2 text-sm" aria-label="Breadcrumb">
    <template v-for="(item, idx) in items" :key="idx">
      <span v-if="idx > 0" class="text-slate-400">&#8250;</span>
      <span v-if="item.to && !item.active">
        <router-link :to="item.to" class="text-slate-500 hover:text-blue-600 font-medium cursor-pointer">{{ item.label }}</router-link>
      </span>
      <span v-else-if="item.onClick && !item.active" @click="item.onClick" class="text-slate-500 hover:text-blue-600 font-medium cursor-pointer">{{ item.label }}</span>
      <span v-else :class="item.active ? 'text-orange-600 font-semibold' : 'text-slate-500 font-medium'">{{ item.label }}</span>
    </template>
  </nav>
</template>

<script setup>
import { defineProps } from 'vue';
const props = defineProps({
  items: {
    type: Array,
    required: true
  }
});
</script> 