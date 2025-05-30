<script setup></script>

<template>
  <header class="flex items-center justify-between px-6 py-4 bg-white shadow">
    <div class="flex items-center space-x-3">
      <img
        src="https://storage.googleapis.com/a1aa/image/9358955a-d854-4fb1-1842-35be93c6eea2.jpg"
        alt="Logo"
        class="w-8 h-8"
      />
      <nav class="ml-8 flex space-x-6 text-sm font-medium text-gray-700">
        <router-link
          to="/"
          class="select-none"
          :class="{ 'text-blue-700 font-bold': $route.path === '/' }"
          >Trending</router-link
        >

        <router-link
          to="/bus"
          class="select-none hover:text-gray-900"
          :class="{ 'text-blue-700 font-bold': $route.path === '/bus' }"
          >Bus</router-link
        >

        <router-link
          to="/plane"
          class="select-none hover:text-gray-900"
          :class="{ 'text-blue-700 font-bold': $route.path === '/plane' }"
          >Flights</router-link
        >

        <router-link
          to="/hotel"
          class="select-none hover:text-gray-900"
          :class="{ 'text-blue-700 font-bold': $route.path === '/hotel' }"
          >Hotels</router-link
        >

        <router-link
          to="/tour"
          class="select-none hover:text-gray-900"
          :class="{ 'text-blue-700 font-bold': $route.path === '/tour' }"
          >Tours</router-link
        >
      </nav>
    </div>

    <div
      class="flex items-center space-x-6 text-gray-900 text-lg cursor-pointer"
    >
      <i class="fas fa-globe-americas"></i>
      <i class="fas fa-user-circle"></i>
    </div>
  </header>
</template>

<style scoped></style>
