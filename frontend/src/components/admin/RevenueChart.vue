<template>
  <div class="bg-white rounded-xl shadow p-6">
    <div class="font-semibold mb-2 text-gray-700">Biểu đồ doanh thu</div>
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
    type: "line",
    data: {
      labels: props.data.labels,
      datasets: [
        {
          label: "Doanh thu (triệu VNĐ)",
          data: props.data.data,
          borderColor: "#646ae7",
          backgroundColor: "rgba(100,106,231,0.1)",
          tension: 0.4,
          fill: true,
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
