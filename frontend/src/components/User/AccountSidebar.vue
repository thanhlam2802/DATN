<template>
  <aside aria-label="Account settings navigation" class="bg-white rounded-lg w-full mb-10 md:mb-0">
    <ul class="px-2">
      <li v-for="tab in tabs" :key="tab.name">
        <router-link :to="`/account/${tab.name}`"
                     class="flex items-center space-x-3 px-4 py-3 rounded-lg transition-colors duration-200" :class="{
                        'text-blue-600 bg-blue-50': $route.path === `/account/${tab.name}`,
                        'text-gray-700 hover:text-gray-900 hover:bg-gray-50': $route.path !== `/account/${tab.name}`
                    }">
                    <span class="flex items-center justify-center w-7 h-7 rounded-full"
                          :class="$route.path === `/account/${tab.name}` ? 'bg-blue-100 text-blue-600' : 'bg-gray-100 text-gray-500'">
                        <i :class="tab.icon + ' text-sm'"></i>
                    </span>
          <span :class="['text-sm', $route.path === `/account/${tab.name}` ? 'font-medium' : 'font-normal']">
                        {{ tab.label }}
                    </span>
        </router-link>
      </li>
    </ul>
  </aside>
</template>

<script>
export default {
  name: "AccountSidebar",
  data() {
    return {
      tabs: [
        {name: "personal", label: "Personal info", icon: "fas fa-user"},
        {name: "bookings", label: "All bookings", icon: "fas fa-user"},
        {name: "payment", label: "Payment details", icon: "far fa-credit-card"},
        {name: "security", label: "Security", icon: "fas fa-lock"},
        {name: "preferences", label: "Preferences", icon: "fas fa-sliders-h"},
        {name: "notifications", label: "Notification settings", icon: "far fa-bell"},
      ],
    };
  },
};
</script>
