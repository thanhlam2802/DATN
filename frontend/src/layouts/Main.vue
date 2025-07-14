<script setup>
import Header from "../components/Header.vue";
import Footer from "../components/Footer.vue";
</script>

<template>
  <div class="flex flex-col min-h-screen">
    <!-- Header cố định -->
    <Header />

    <!-- Main content -->
    <main class="flex-1 w-full px-4 sm:px-6 lg:px-8 py-6">
      <router-view />
    </main>

    <!-- Footer -->
    <Footer />
  </div>
</template>
