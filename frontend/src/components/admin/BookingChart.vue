<template>
  <div class="bg-white rounded-xl shadow p-6">
    <div class="font-semibold mb-2 text-gray-700">Biểu đồ booking</div>
    <canvas ref="canvas"></canvas>
  </div>
</template>
<script setup>
import { onMounted, ref, watch } from "vue";
let chartInstance = null;
const props = defineProps({
  data: Object,
});
const canvas = ref(null);

onMounted(() => {
  renderChart();
});
watch(() => props.data, renderChart);

async function renderChart() {
  if (!canvas.value) return;
  if (chartInstance) {
    chartInstance.destroy();
  }
  const Chart = (await import("chart.js/auto")).default;
  chartInstance = new Chart(canvas.value, {
    type: "bar",
    data: {
      labels: props.data.labels,
      datasets: [
        {
          label: "Số booking",
          data: props.data.data,
          backgroundColor: "#646ae7",
        },
      ],
    },
    options: {
      responsive: true,
      plugins: {
        legend: { display: false },
      },
      scales: {
        y: { beginAtZero: true },
      },
    },
  });
}
</script>
