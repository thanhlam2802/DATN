<template>
  <div class="p-4 rounded-lg min-h-[250px] z-1000">
    <!-- Form tìm kiếm xe khách -->
    <BusSearchForm @search="onSearch" />

    <!-- Modal kết quả -->
    <BusSearchModal
      :show="showModal"
      :searchParams="searchResults.searchParams"
      :searchResults="searchResults"
      @close="showModal = false"
    />
  </div>
 </template>

<script setup>
import { ref } from 'vue'
import BusSearchForm from '@/components/Bus/BusSearchForm.vue'
import BusSearchModal from '@/components/Bus/BusSearchModal.vue'

const showModal = ref(false)
const searchResults = ref({ busSlots: [], searchParams: {} })

const onSearch = (results) => {
  searchResults.value = results || { busSlots: [], searchParams: {} }
  showModal.value = true
}
</script>