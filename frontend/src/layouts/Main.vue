<script setup>
import Header from "../components/Header.vue";
import Footer from "../components/Footer.vue";


</script>

<template>
  <Header />

  <main class="min-h-[calc(100vh-120px)] flex justify-center px-6">
    <router-view />
  </main>

  <Footer />
</template>
