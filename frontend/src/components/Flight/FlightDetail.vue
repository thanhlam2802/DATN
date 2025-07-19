<template>
  <div class="w-full min-h-screen bg-gray-50 py-10">
    <div class="max-w-6xl mx-auto rounded-2xl shadow-xl bg-white overflow-hidden">
      <!-- Header ảnh lớn + overlay -->
      <div class="relative h-72 md:h-96 w-full">
        <img :src="mainImage" class="w-full h-full object-cover object-center" />
        <div class="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent"></div>
        <div class="absolute bottom-0 left-0 p-8 text-white">
          <h1 class="text-4xl md:text-5xl font-bold mb-2 flex items-center gap-3 drop-shadow">
            <i class="fa-solid fa-plane-departure text-indigo-200 text-3xl"></i>
            {{ flightDetail.name }}
          </h1>
          <div class="flex flex-wrap gap-6 text-lg font-semibold drop-shadow">
            <span>Mã chuyến: <span class="font-bold">{{ flightDetail.flightNumber }}</span></span>
            <span>Hãng: <span class="font-bold">{{ flightDetail.airline ? flightDetail.airline.name : 'Khác' }}</span></span>
            <span>Loại: <span class="font-bold">{{ flightDetail.category ? flightDetail.category.name : 'Khác' }}</span></span>
            <span class="bg-indigo-600/80 rounded px-2 py-1 text-sm">Còn {{ flightDetail.totalAvailableSeats }} ghế</span>
          </div>
        </div>
      </div>
      <!-- Main content 2 cột -->
      <div class="flex flex-col md:flex-row gap-8 p-8">
        <!-- Cột trái: Thông tin chuyến bay, tiện ích, gallery -->
        <div class="flex-1 min-w-[320px] space-y-6">
          <!-- Thông tin chuyến bay -->
          <div class="bg-indigo-50 rounded-xl p-6 shadow-inner">
            <div class="flex flex-col gap-2 text-gray-700">
              <div class="flex items-center gap-2">
                <i class="fas fa-plane-departure text-indigo-500"></i>
                <span class="font-semibold">Đi:</span> {{ formatTime(flightDetail.departureTime) }} - {{ flightDetail.departureAirport?.name }}
              </div>
              <div class="flex items-center gap-2">
                <i class="fas fa-plane-arrival text-indigo-500"></i>
                <span class="font-semibold">Đến:</span> {{ formatTime(flightDetail.arrivalTime) }} - {{ flightDetail.arrivalAirport?.name }}
              </div>
              <div class="flex items-center gap-2">
                <i class="fas fa-clock text-indigo-500"></i>
                <span class="font-semibold">Thời lượng:</span> {{ durationDisplay }}
              </div>
              <div class="flex items-center gap-2">
                <i class="fas fa-calendar text-indigo-500"></i>
                <span class="font-semibold">Ngày tạo:</span> {{ formatDate(flightDetail.createdAt) }}
              </div>
              <div class="flex items-center gap-2">
                <i class="fas fa-sync text-indigo-500"></i>
                <span class="font-semibold">Cập nhật:</span> {{ formatDate(flightDetail.updatedAt) }}
              </div>
            </div>
          </div>

  
          <!-- Gallery ảnh nếu có nhiều ảnh -->
          <div v-if="flightDetail.images && flightDetail.images.length > 1" class="bg-white rounded-xl p-4 shadow-inner">
            <div class="font-semibold text-gray-700 mb-2">Hình ảnh khác:</div>
            <div class="flex gap-4 overflow-x-auto">
              <img v-for="img in flightDetail.images" :key="img.imageId" :src="img.imageUrl" :alt="img.altText" class="w-32 h-24 object-cover rounded-lg border border-gray-200" />
            </div>
          </div>
          <div class="bg-indigo-50 rounded-xl p-6 shadow-inner mt-8">
            <h3 class="text-lg font-bold text-indigo-700 mb-4 flex items-center gap-2"><i class="fa-solid fa-ticket-alt"></i> Thông tin vé đã chọn</h3>
            <div v-if="selectedSlot">
              <div class="flex flex-col gap-2 text-gray-700">
                <div><span class="font-semibold">Loại vé:</span> <span v-if="selectedSlot.isBusiness" class="text-yellow-700 font-bold">Thương gia</span><span v-else class="text-indigo-700 font-bold">Phổ thông</span></div>
                <div><span class="font-semibold">Số ghế:</span> {{ selectedSlot.seatNumber }}</div>
                <div><span class="font-semibold">Vị trí:</span>
                  <span v-if="selectedSlot.isWindow">Cửa sổ</span>
                  <span v-else-if="selectedSlot.isAisle">Lối đi</span>
                  <span v-else>Khác</span>
                </div>
                <div>
                  <span class="font-semibold">Giá:</span>
                  <span class="font-bold text-green-700">{{ formatCurrency(selectedSlot.isWindow ? selectedSlot.price + 200000 : selectedSlot.price) }}</span>
                </div>
                <div v-if="selectedSlot.isWindow" class="text-xs text-indigo-500 italic">Đã cộng thêm 200,000 VND do chọn ghế cửa sổ</div>
                <div><span class="font-semibold">Hành lý xách tay:</span> {{ selectedSlot.carryOnLuggage }} kg</div>
              </div>
            </div>
            <div v-else class="text-gray-400 italic">Vui lòng chọn một vé để xem chi tiết.</div>
            <button
              class="mt-6 w-full bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-3 rounded-lg text-lg shadow-lg transition disabled:opacity-50 disabled:cursor-not-allowed"
              :disabled="!selectedSlot"
            >
              Thanh toán
            </button>
          </div>
        </div>


        <!-- Cột phải: Danh sách vé + tổng quan vé đã chọn -->
        <div class="flex-1 min-w-[320px] space-y-8">
          <!-- Danh sách vé -->
          <div>
            <h2 class="text-xl font-bold text-indigo-700 mb-2">Chọn vé của bạn</h2>
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <template v-for="group in seatGroups" :key="group.key">
                <label class="group block cursor-pointer rounded-xl border-2 transition-all duration-200 p-4 shadow hover:shadow-lg hover:border-indigo-400 bg-white relative"
                  :class="selectedGroupKey === group.key ? (group.isBusiness ? 'border-yellow-500 ring-2 ring-yellow-200' : 'border-indigo-600 ring-2 ring-indigo-200') : 'border-gray-200'">
                  <input type="radio" v-model="selectedGroupKey" :value="group.key" class="hidden" />
                  <div class="flex items-center gap-2 mb-2">
                    <span :class="group.isBusiness ? 'bg-yellow-100 text-yellow-700' : 'bg-indigo-100 text-indigo-700'" class="px-2 py-1 rounded-full text-xs font-semibold">
                      {{ group.isBusiness ? 'Thương gia' : 'Phổ thông' }}
                    </span>
                    <span v-if="group.isWindow" class="bg-blue-100 text-blue-700 px-2 py-1 rounded-full text-xs font-semibold ml-2">Cửa sổ</span>
                    <span v-else class="bg-green-100 text-green-700 px-2 py-1 rounded-full text-xs font-semibold ml-2">Lối đi</span>
                  </div>
                  <div class="flex items-center gap-2 mb-1">
                    <i :class="group.isBusiness ? 'fa-solid fa-chair text-yellow-400' : 'fa-solid fa-chair text-indigo-400'"></i>
                    <span class="font-bold">{{ group.count }} ghế khả dụng</span>
                  </div>
                  <div class="flex items-center gap-2 mb-1">
                    <i class="fa-solid fa-money-bill-wave text-green-400"></i>
                    <span :class="group.isBusiness ? 'font-semibold text-yellow-700' : 'font-semibold text-indigo-700'">{{ formatCurrency(group.price) }}</span>
                  </div>
                  <div class="flex items-center gap-2">
                    <i class="fa-solid fa-suitcase-rolling text-gray-400"></i>
                    <span class="text-xs text-gray-600">Hành lý: {{ group.carryOnLuggage }} kg</span>
                  </div>
                </label>
              </template>
            </div>
          </div>
          <!-- Tổng quan vé đã chọn & thanh toán -->
          
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { getFlightDetail, getAllAirlines } from '@/api/flightApi';

const route = useRoute();
const flightDetail = ref({
  images: [],
  flightSlots: [],
  amenities: []
});
const loading = ref(true);
const error = ref('');
const selectedSlotId = ref(null);

const airlineOptions = ref({})
const airlines = ref([])

// Load airlines từ API
onMounted(async () => {
  try {
    const res = await getAllAirlines()
    airlines.value = res.data
    // Convert array to object for easy lookup
    airlines.value.forEach(airline => {
      airlineOptions.value[airline.id] = airline.name
    })
  } catch (e) {
    console.error('Không thể tải danh sách hãng hàng không:', e)
  }
})

const mainImage = computed(() =>
  flightDetail.value.images && flightDetail.value.images.length > 0
    ? flightDetail.value.images[0].imageUrl
    : 'https://ix-marketing.imgix.net/autotagging.png?auto=format,compress&w=1946'
);

const economySlots = computed(() => (flightDetail.value.flightSlots || []).filter(s => !s.isBusiness));
const businessSlots = computed(() => (flightDetail.value.flightSlots || []).filter(s => s.isBusiness));
const selectedSlot = computed(() => {
  const group = seatGroups.value.find(g => g.key === selectedGroupKey.value)
  return group && group.slots.length > 0 ? group.slots[0] : null
})

function formatTime(val) {
  if (!val) return '';
  const d = new Date(val);
  return d.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
}
function formatCurrency(val) {
  if (!val) return '';
  return Number(val).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
}
function formatDate(val) {
  if (!val) return '';
  const d = new Date(val);
  return d.toLocaleDateString('vi-VN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' });
}
const durationDisplay = computed(() => {
  if (!flightDetail.value) return '';
  const dep = new Date(flightDetail.value.departureTime);
  const arr = new Date(flightDetail.value.arrivalTime);
  const diff = (arr - dep) / 60000;
  const h = Math.floor(diff / 60);
  const m = diff % 60;
  return `${h}h ${m}m`;
});

const selectedGroupKey = ref(null)

// Gom nhóm slot thành 4 loại
const seatGroups = computed(() => {
  const slots = flightDetail.value.flightSlots || [];
  // 4 nhóm: phổ thông lối đi, phổ thông cửa sổ, thương gia lối đi, thương gia cửa sổ
  const groups = [
    { key: 'eco-aisle', isBusiness: false, isWindow: false, label: 'Phổ thông - Lối đi' },
    { key: 'eco-window', isBusiness: false, isWindow: true, label: 'Phổ thông - Cửa sổ' },
    { key: 'biz-aisle', isBusiness: true, isWindow: false, label: 'Thương gia - Lối đi' },
    { key: 'biz-window', isBusiness: true, isWindow: true, label: 'Thương gia - Cửa sổ' },
  ];
  return groups.map(g => {
    const filtered = slots.filter(s => s.isBusiness === g.isBusiness && (!!s.isWindow) === g.isWindow && (!!s.isAisle) === !g.isWindow);
    return {
      ...g,
      count: filtered.length,
      price: filtered.length ? filtered[0].price : 0,
      carryOnLuggage: filtered.length ? filtered[0].carryOnLuggage : 0,
      slots: filtered
    };
  }).filter(g => g.count > 0);
});

onMounted(async () => {
  loading.value = true;
  error.value = '';
  try {
    const id = route.params.id;
    const res = await getFlightDetail(id);
    flightDetail.value = res.data;
  } catch (e) {
    error.value = 'Không thể tải dữ liệu chuyến bay.';
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
</style> 