<template>
  <div :class="containerClass">
    <!-- <SearchBar v-if="showSearchBar" /> -->
    <main class="grid grid-cols-1 lg:grid-cols-[280px_1fr] gap-10 mt-8">
      <FilterSidebar v-if="isListingPage" />
      <section :class="isListingPage ? 'flex flex-col -mt-2' : 'col-span-full'">
        <router-view />
      </section>
    </main>
  </div>
</template>

<script setup>
import { useRoute } from "vue-router";
import { computed } from "vue";
import SearchBar from "@/components/Hotel/SearchBar.vue";
import FilterSidebar from "@/components/Hotel/FilterSidebar.vue";

const route = useRoute();

const isListingPage = computed(() => route.name === "HotelListing");

const showSearchBar = computed(() => {
  return route.name === "HotelListing" || route.name === "HotelDetail";
});

const containerClass = computed(() => {
  return route.name === "HotelDetail"
    ? "w-full px-4 sm:px-6 xl:px-20 pt-3 pb-8"
    : "w-full px-4 sm:px-6 xl:px-20 pt-3 py-8";
});
</script>
