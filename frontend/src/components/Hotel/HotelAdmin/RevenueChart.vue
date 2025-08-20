<template>
  <div class="bg-white rounded-lg shadow-sm p-3">
    <div :style="{ height: height + 'px', overflow: 'hidden' }">
      <canvas
        ref="canvas"
        :height="height"
        style="width: 100%"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from "vue";
let chartInstance = null;
const props = defineProps({
  data: Object,
  height: {
    type: Number,
    default: 400
  }
});
const canvas = ref(null);

const COLORS = [
  '#FFB300', '#42A5F5', '#66BB6A', '#AB47BC', '#FFA726', '#26C6DA', '#EC407A', '#7E57C2', '#FF7043', '#8D6E63', '#789262', '#D4E157', '#FF8A65', '#BA68C8', '#4DD0E1', '#9575CD', '#AED581', '#FFD54F', '#90A4AE', '#F06292'
];

const getXAxisTitle = () => {
  if (!props.data?.labels || props.data.labels.length === 0) {
    return 'Khách sạn';
  }
  
  const firstLabel = props.data.labels[0];
  
  if (firstLabel.includes(':') && firstLabel.length === 5) {
    return 'Giờ';
  }
  
  const weekdays = ['Chủ nhật', 'Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7'];
  if (weekdays.includes(firstLabel)) {
    return 'Ngày trong tuần';
  }
  
  if (firstLabel.includes('/') && firstLabel.length === 5) {
    return 'Ngày';
  }
  
  return 'Khách sạn';
};

onMounted(() => {
  renderChart();
});

watch(() => props.data, (newData) => {
  console.log('RevenueChart data changed, re-rendering chart');
  console.log('New data:', newData);
  renderChart();
}, { deep: true });

async function renderChart() {
  if (!canvas.value) return;

  console.log('RevenueChart renderChart - data:', props.data);
  console.log('RevenueChart renderChart - labels:', props.data?.labels);
  console.log('RevenueChart renderChart - datasets:', props.data?.datasets);

  if (chartInstance) {
    chartInstance.destroy();
    chartInstance = null;
  }

  const Chart = (await import("chart.js/auto")).default;

  let chartData;

  if (props.data && props.data.datasets && Array.isArray(props.data.datasets) && props.data.datasets.length > 0) {
    console.log('Using datasets format');
    const datasets = (props.data.datasets || []).map((ds, idx) => ({
      ...ds,
      backgroundColor: COLORS[idx % COLORS.length],
      borderColor: COLORS[idx % COLORS.length],
      borderWidth: 1,
      barPercentage: 0.3,
      categoryPercentage: 0.8,
    }));
    chartData = {
      labels: props.data.labels || [],
      datasets,
    };
  } else if (props.data && props.data.data && Array.isArray(props.data.data) && props.data.data.length > 0) {
    console.log('Using simple data format');
    chartData = {
      labels: props.data.labels || [],
      datasets: [{
        label: 'Doanh thu',
        data: props.data.data || [],
        backgroundColor: COLORS[0],
        borderColor: COLORS[0],
        borderWidth: 1,
        barPercentage: 0.3,
        categoryPercentage: 0.8,
      }]
    };
  } else {
    console.log('No valid data, using fallback');
    chartData = {
      labels: ['Không có dữ liệu'],
      datasets: [{
        label: 'Doanh thu',
        data: [0],
        backgroundColor: COLORS[0],
        borderColor: COLORS[0],
        borderWidth: 1,
        barPercentage: 0.3,
        categoryPercentage: 0.8,
      }]
    };
  }

  console.log('Final chartData:', chartData);

  try {
    chartInstance = new Chart(canvas.value, {
      type: "bar",
      data: chartData,
      options: {
        responsive: true,
        maintainAspectRatio: false,
        animation: {
          duration: 1000,
          easing: 'easeInOutQuart'
        },
        plugins: {
          legend: { 
            display: true, 
            position: 'bottom',
            labels: {
              usePointStyle: true,
              pointStyle: 'circle',
              font: {
                size: 10
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
                return context[0].dataset.label || 'Khách sạn';
              },
              label: function(context) {
                let v = context.parsed.y;
                return v.toLocaleString('vi-VN') + ' VND';
              }
            }
          }
        },
        scales: {
          x: {
            stacked: false,
            title: { 
              display: true, 
              text: getXAxisTitle(),
              font: {
                size: 10
              }
            },
            ticks: {
              font: {
                size: 9
              }
            },
            grid: {
              display: true,
              color: 'rgba(0, 0, 0, 0.15)',
              borderDash: [6, 6],
              borderWidth: 1,
              drawBorder: false,
              drawOnChartArea: true,
              drawTicks: false,
              lineWidth: 1
            }
          },
          y: {
            beginAtZero: true,
            title: { 
              display: true, 
              text: 'Doanh thu (VNĐ)',
              font: {
                size: 10
              }
            },
            ticks: {
              font: {
                size: 9
              },
              callback: function(value) {
                return value >= 1e9 ? (value/1e9)+ ' tỷ' : value >= 1e6 ? (value/1e6) + ' triệu' : value;
              }
            },
            grid: {
              display: true,
              color: 'rgba(0, 0, 0, 0.15)',
              borderDash: [6, 6],
              borderWidth: 1,
              drawBorder: false,
              drawOnChartArea: true,
              drawTicks: false,
              lineWidth: 1
            }
          },
        },
      },
    });
    console.log('Bar chart created successfully');
  } catch (error) {
    console.error('Error creating bar chart:', error);
  }
}
</script>
