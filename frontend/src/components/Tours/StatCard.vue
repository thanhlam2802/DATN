<template>
  <div class="p-5 bg-white rounded-lg shadow flex items-start space-x-4">
    <div class="flex-shrink-0">
      <div
        class="w-12 h-12 flex items-center justify-center rounded-full"
        :class="iconBgColor"
      >
        <component :is="iconComponent" class="h-6 w-6" :class="iconColor" />
      </div>
    </div>
    <div class="flex-grow">
      <p class="text-sm font-medium text-gray-600 truncate">
        {{ title }}
        <span class="font-bold text-blue-600">{{ filterText }}</span>
      </p>
      <p class="text-3xl font-bold text-gray-900">{{ value }}</p>
      <div
        v-if="change !== undefined && change !== null"
        class="flex items-center text-sm mt-1"
        :class="trendColor"
      >
        <svg
          v-if="change >= 0"
          class="w-4 h-4 mr-1"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M13 7l5 5m0 0l-5 5m5-5H6"
          ></path>
        </svg>
        <svg
          v-else
          class="w-4 h-4 mr-1"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M11 17l-5-5m0 0l5-5m-5 5h12"
          ></path>
        </svg>
        <span>{{ formattedChange }} so với kỳ trước</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";

// Define props to receive data from the parent component (OverviewTab.vue)
const props = defineProps({
  icon: { type: String, required: true },
  title: { type: String, required: true },
  value: { type: [String, Number], required: true },
  change: { type: Number, default: null },
  filterText: { type: String, default: "" },
});

// Map icon names to colors for styling
const iconStyles = {
  cash: { bg: "bg-green-100", text: "text-green-600" },
  users: { bg: "bg-blue-100", text: "text-blue-600" },
  ticket: { bg: "bg-yellow-100", text: "text-yellow-600" },
  briefcase: { bg: "bg-indigo-100", text: "text-indigo-600" },
};

const iconBgColor = computed(() => iconStyles[props.icon]?.bg || "bg-gray-100");
const iconColor = computed(
  () => iconStyles[props.icon]?.text || "text-gray-600"
);

// SVG components for icons
const icons = {
  cash: {
    template: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v.01" />`,
  },
  users: {
    template: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M15 21a6 6 0 00-9-5.197" />`,
  },
  ticket: {
    template: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 5v2m0 4v2m0 4v2M5 5a2 2 0 00-2 2v3a2 2 0 110 4v3a2 2 0 002 2h14a2 2 0 002-2v-3a2 2 0 110-4V7a2 2 0 00-2-2H5z" />`,
  },
  briefcase: {
    template: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />`,
  },
};

const iconComponent = computed(() => {
  const svgContent = icons[props.icon]?.template || "";
  return {
    template: `<svg fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">${svgContent}</svg>`,
  };
});

// Logic for trend indicator
const trendColor = computed(() => {
  if (props.change > 0) return "text-green-600";
  if (props.change < 0) return "text-red-600";
  return "text-gray-500";
});

const formattedChange = computed(() => {
  if (props.change === null) return "";
  const percentage = Math.abs(props.change * 100).toFixed(1);
  return `${percentage}%`;
});
</script>
