<template>
  <div class="min-h-screen  py-4 px-4 md:px-12 w-full">
    <a class="inline-flex items-center text-xs text-gray-500 mb-3 hover:underline" href="/">
      <i class="fas fa-chevron-left mr-1"></i>
      Back
    </a>

    <div class="flex flex-col md:flex-row gap-4">
      <div class="md:w-[270px] h-fit bg-white rounded-lg border border-gray-200 shadow-sm px-2">
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
