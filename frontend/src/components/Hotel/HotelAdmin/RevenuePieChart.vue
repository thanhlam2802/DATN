<template>
  <div class="bg-white rounded-lg shadow-sm p-3">
    <div class="font-medium text-sm mb-2 text-gray-700">Doanh thu theo khách sạn</div>
    <div :style="{ height: height + 'px', overflow: 'hidden' }">
      <canvas
        v-if="dataKey"
        ref="canvas"
        :height="height"
        style="width: 100%"
        :key="dataKey"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, computed, watch } from "vue";
let chartInstance = null;
const props = defineProps({
  data: Object,
  height: {
    type: Number,
    default: 350
  }
});
const canvas = ref(null);
const dataKey = computed(() => JSON.stringify(props.data));

const COLORS = [
  '#FFB300', '#42A5F5', '#66BB6A', '#AB47BC', '#FFA726', '#26C6DA', '#EC407A', '#7E57C2', '#FF7043', '#8D6E63', '#789262', '#D4E157', '#FF8A65', '#BA68C8', '#4DD0E1', '#9575CD', '#AED581', '#FFD54F', '#90A4AE', '#F06292'
];

onMounted(() => {
  renderChart();
});

watch(() => props.data, (newData) => {
  console.log('RevenuePieChart data changed, re-rendering chart');
  console.log('New data:', newData);
  renderChart();
}, { deep: true });

async function renderChart() {
  if (!canvas.value) return;
  
  if (chartInstance) {
    try {
      chartInstance.destroy();
      chartInstance = null;
    } catch (error) {
      console.log('Error destroying chart:', error);
    }
  }
  
  canvas.value.height = props.height;
  const Chart = (await import("chart.js/auto")).default;
  
  let ChartDataLabels = null;
  try {
    ChartDataLabels = (await import("chartjs-plugin-datalabels")).default;
  } catch (error) {
    console.log('chartjs-plugin-datalabels not available, using fallback');
  }
  
  let chartData;
  
  if (props.data && props.data.datasets && Array.isArray(props.data.datasets) && props.data.datasets.length > 0) {
    console.log('Using datasets format for pie chart');
    const hotelRevenue = {};
    props.data.datasets.forEach(dataset => {
      const hotelName = dataset.label;
      const totalRevenue = dataset.data.reduce((sum, val) => sum + val, 0);
      hotelRevenue[hotelName] = totalRevenue;
    });
    
    const labels = Object.keys(hotelRevenue);
    const data = Object.values(hotelRevenue);
    
    chartData = {
      labels: labels,
      datasets: [{
        data: data,
        backgroundColor: COLORS.slice(0, labels.length),
        borderColor: COLORS.slice(0, labels.length),
        borderWidth: 1,
      }]
    };
  } else if (props.data && props.data.data && Array.isArray(props.data.data) && props.data.data.length > 0) {
    console.log('Using simple data format for pie chart');
    chartData = {
      labels: props.data.labels || [],
      datasets: [{
        data: props.data.data || [],
        backgroundColor: COLORS.slice(0, (props.data.labels || []).length),
        borderColor: COLORS.slice(0, (props.data.labels || []).length),
        borderWidth: 1,
      }]
    };
  } else {
    console.log('No valid data for pie chart, using fallback');
    chartData = {
      labels: ['Không có dữ liệu'],
      datasets: [{
        data: [1],
        backgroundColor: COLORS[0],
        borderColor: COLORS[0],
        borderWidth: 1,
      }]
    };
  }
  
  console.log('Final pie chartData:', chartData);
  
  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { 
        display: true, 
        position: 'bottom',
        labels: {
          usePointStyle: true,
          pointStyle: 'circle',
          font: {
            size: 10
          },
          generateLabels: function(chart) {
            const data = chart.data;
            if (data.labels.length && data.datasets.length) {
              const dataset = data.datasets[0];
              return data.labels.map((label, index) => {
                return {
                  text: label,
                  fillStyle: dataset.backgroundColor[index],
                  strokeStyle: dataset.borderColor[index],
                  lineWidth: dataset.borderWidth,
                  pointStyle: 'circle',
                  hidden: false,
                  index: index
                };
              });
            }
            return [];
          }
        }
      },
      tooltip: {
        titleFont: {
          size: 11
        },
        bodyFont: {
          size: 10
        },
        callbacks: {
          title: function(context) {
            return context[0].label;
          },
          label: function(context) {
            const value = context.parsed;
            const total = context.dataset.data.reduce((sum, val) => sum + val, 0);
            const percentage = ((value / total) * 100).toFixed(1);
            return `${value.toLocaleString('vi-VN')} VND (${percentage}%)`;
          }
        }
      }
    },
    elements: {
      arc: {
        borderWidth: 1
      }
    }
  };
  
  if (ChartDataLabels) {
    options.plugins.datalabels = {
      color: '#fff',
      font: {
        weight: 'bold',
        size: 9
      },
      formatter: function(value, context) {
        const total = context.dataset.data.reduce((sum, val) => sum + val, 0);
        const percentage = ((value / total) * 100).toFixed(1);
        return percentage + '%';
      }
    };
  }
  
  const chartConfig = {
    type: "pie",
    data: chartData,
    options: options
  };
  
  if (ChartDataLabels) {
    chartConfig.plugins = [ChartDataLabels];
  }
  
  try {
    chartInstance = new Chart(canvas.value, chartConfig);
    console.log('Pie chart created successfully');
  } catch (error) {
    console.error('Error creating pie chart:', error);
  }
}
</script> 