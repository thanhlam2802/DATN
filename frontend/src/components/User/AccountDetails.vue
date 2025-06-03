<template>

  <h2 class="text-2xl font-semibold text-gray-800 mb-5">Thông tin cá nhân</h2>

  <div class="space-y-6">
    <div v-for="item in infoItems" :key="item.label"
         class="flex items-center justify-between pb-4 border-b border-gray-100 last:border-b-0">
      <div class="flex items-start space-x-3">
        <i :class="item.icon + ' text-gray-500 text-lg mt-0.5'"></i>
        <div>
          <p class="text-sm text-gray-500 font-medium">{{ item.label }}</p>
          <p class="text-base text-gray-800 font-semibold mt-1">{{ item.value }}</p>
        </div>
      </div>
      <button class="text-sm font-medium text-blue-600 hover:text-blue-800 hover:underline transition">
        {{ item.action }}
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: "AccountDetails",
  data() {
    return {
      infoItems: [
        {label: "Full name", value: "Jimmy Scott", action: "Edit", icon: "fas fa-user"},
        {label: "Email", value: "jimmysco283@gmail.com", action: "Edit", icon: "fas fa-envelope"},
        {label: "Gender", value: "Male", action: "Edit", icon: "fas fa-venus-mars"},
        {label: "Date of birth", value: "August 02, 1987", action: "Edit", icon: "fas fa-birthday-cake"},
        {label: "Phone number", value: "(+1) 012 345 6789", action: "Edit", icon: "fas fa-phone"},
        {label: "Emergency contact", value: "Not provided", action: "Add", icon: "fas fa-exclamation-triangle"}
      ]
    };
  }
};
</script>

<style scoped>
</style>
