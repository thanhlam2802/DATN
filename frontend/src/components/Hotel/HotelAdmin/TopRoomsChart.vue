<template>
  <div class="bg-white rounded-lg shadow-sm p-3">
    <div :style="{ height: height + 'px', overflow: 'hidden' }">
      <canvas ref="canvas" :height="height" style="width: 100%" />
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

onMounted(() => {
  renderChart();
});

watch(() => props.data, (newData) => {
  console.log('TopRoomsChart data changed, re-rendering chart');
  console.log('New data:', newData);
  renderChart();
}, { deep: true });

async function renderChart() {
  if (!canvas.value) return;

  console.log('TopRoomsChart renderChart - data:', props.data);
  console.log('TopRoomsChart renderChart - labels:', props.data?.labels);
  console.log('TopRoomsChart renderChart - data:', props.data?.data);
  console.log('TopRoomsChart renderChart - hotels:', props.data?.hotels);
  console.log('TopRoomsChart renderChart - data length:', props.data?.data?.length);
  console.log('TopRoomsChart renderChart - labels length:', props.data?.labels?.length);
  console.log('TopRoomsChart renderChart - hotels length:', props.data?.hotels?.length);

  if (chartInstance) {
    chartInstance.destroy();
    chartInstance = null;
  }

  const Chart = (await import("chart.js/auto")).default;

  let chartData;

  if (props.data && props.data.data && Array.isArray(props.data.data) && props.data.data.length > 0) {
    console.log('Using top rooms data format');

    const hotels = props.data.hotels || [];
    const labels = props.data.labels || [];
    const data = props.data.data || [];

    const hotelDatasets = {};

    for (let i = 0; i < labels.length; i++) {
      const hotelName = hotels[i] || 'Unknown Hotel';
      if (!hotelDatasets[hotelName]) {
        hotelDatasets[hotelName] = {
          label: hotelName,
          data: new Array(labels.length).fill(0),
          backgroundColor: COLORS[Object.keys(hotelDatasets).length % COLORS.length],
          borderColor: COLORS[Object.keys(hotelDatasets).length % COLORS.length],
          borderWidth: 1,
        };
      }
      hotelDatasets[hotelName].data[i] = data[i];
    }

    chartData = {
      labels: labels,
      datasets: Object.values(hotelDatasets),
    };
  } else {
    console.log('No valid data, using fallback');
    chartData = {
      labels: ['Không có dữ liệu'],
      datasets: [{
        label: 'Số đặt phòng',
        data: [0],
        backgroundColor: COLORS[0],
        borderColor: COLORS[0],
        borderWidth: 1,
      }]
    };
  }

  console.log('Final chartData:', chartData);
  console.log('Datasets:', chartData.datasets);
  console.log('First dataset data:', chartData.datasets[0]?.data);

  try {
    chartInstance = new Chart(canvas.value, {
      type: "bar",
      data: chartData,
      options: {
        indexAxis: 'y',
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
              title: function (context) {
                return context.label || 'Tên phòng';
              },
              label: function (context) {
                console.log('Tooltip context:', context);
                console.log('Context.parsed:', context?.parsed);

                if (!context || context.parsed === undefined) {
                  return 'Không có dữ liệu';
                }

                const bookingCount = context.parsed.x;
                console.log('Booking count:', bookingCount);
                return `${bookingCount} đặt phòng`;
              }
            }
          }
        },
        scales: {
          x: {
            beginAtZero: true,
            title: {
              display: true,
              text: 'Số đặt phòng',
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
            title: {
              display: true,
              text: 'Tên phòng',
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
        },
      },
    });
    console.log('Horizontal bar chart created successfully');
  } catch (error) {
    console.error('Error creating horizontal bar chart:', error);
  }
}
</script>