<template>
  <div class="min-h-screen  py-8 px-4 md:px-12 w-full">
    <a class="inline-flex items-center text-xs text-gray-500 mb-6 hover:underline" href="/">
      <i class="fas fa-chevron-left mr-1"></i>
      Back
    </a>
    <h1 class="text-3xl font-semibold mb-10">Account Settings</h1>

    <div class="flex flex-col md:flex-row gap-8">
      <div class="w-full md:w-[250px] h-fit bg-white rounded-lg border border-gray-200 shadow-sm p-6">
        <AccountSidebar/>
      </div>
      <div class="flex-1 bg-white p-5 pt-0">
        <router-view/>
      </div>
    </div>
  </div>
</template>

<script>

import AccountSidebar from "@/components/User/AccountSidebar.vue";

export default {
  name: "AccountView",
  components: {
    AccountSidebar,
  },
};
</script>
