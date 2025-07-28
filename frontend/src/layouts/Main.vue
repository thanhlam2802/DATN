<script setup>
import Header from "../components/Header.vue";
import Footer from "../components/Footer.vue";
import LoadingLayout from "@/layouts/LoadingLayout.vue";
</script>

<template>
  <div class="flex flex-col min-h-screen">
    <LoadingLayout/>
    <!-- Header cố định -->
    <Header/>

    <!-- Main content -->
    <main class="flex w-full px-4 sm:px-6 lg:px-8 justify-center">
      <router-view/>
    </main>

    <!-- Footer -->
    <Footer/>
  </div>
</template>
