<template>
  <div class="w-full min-h-screen bg-gray-50 py-10">
    <div class="max-w-6xl mx-auto rounded-2xl shadow-xl bg-white overflow-hidden">
      <!-- Header ảnh lớn + overlay -->
      <div class="relative h-72 md:h-96 w-full">
        <img :src="mainImage" class="w-full h-full object-cover object-center" />
        <div class="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent"></div>
        
        <!-- Nút ẩn cho admin flight - chỉ hiện khi hover vào góc phải trên -->
        
        
        <div class="absolute bottom-0 left-0 p-8 text-white">
          <h1 class="text-4xl md:text-5xl font-bold mb-2 flex items-center gap-3 drop-shadow">
            <i class="fa-solid fa-plane-departure text-indigo-200 text-3xl"></i>
            {{ flightDetail.name }}
          </h1>
          <div class="flex flex-wrap gap-6 text-lg font-semibold drop-shadow">
            <span>Mã chuyến:
              <span class="font-bold">{{
                flightDetail.flightNumber
              }}</span></span>
            <span>Hãng:
              <span class="font-bold">{{
                flightDetail.airline ? flightDetail.airline.name : "Khác"
              }}</span></span>
            <span>Loại:
              <span class="font-bold">{{
                flightDetail.category ? flightDetail.category.name : "Khác"
              }}</span></span>
            <span class="bg-indigo-600/80 rounded px-2 py-1 text-sm">Còn {{ flightDetail.totalAvailableSeats }}
              ghế</span>
          </div>
        </div>
      </div>
      <!-- Main content 2 cột -->
      <div class="flex flex-col md:flex-row gap-8 p-8">
        <!-- Cột trái: Thông tin chuyến bay, tiện ích, gallery -->
        <div class="flex-[2] min-w-[320px] space-y-6">
          <div>
            <h2 class="text-xl font-bold text-indigo-700 mb-2">
              Chọn vé của bạn
            </h2>
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <template v-for="group in seatGroups" :key="group.key">
                <label v-if="group.count > 0"
                  class="group block cursor-pointer rounded-xl border-2 transition-all duration-200 p-4 shadow hover:shadow-lg hover:border-indigo-400 bg-white relative"
                  :class="selectedGroupKey === group.key
                      ? group.isBusiness
                        ? 'border-yellow-500 ring-2 ring-yellow-200'
                        : 'border-indigo-600 ring-2 ring-indigo-200'
                      : 'border-gray-200'
                    ">
                  <input type="radio" v-model="selectedGroupKey" :value="group.key" class="hidden" />
                  <div class="flex items-center gap-2 mb-2">
                    <span :class="group.isBusiness
                        ? 'bg-yellow-100 text-yellow-700'
                        : 'bg-indigo-100 text-indigo-700'
                      " class="px-2 py-1 rounded-full text-xs font-semibold">
                      {{ group.isBusiness ? "Thương gia" : "Phổ thông" }}
                    </span>
                    <span v-if="group.isWindow"
                      class="bg-blue-100 text-blue-700 px-2 py-1 rounded-full text-xs font-semibold ml-2">Cửa sổ</span>
                    <span v-else
                      class="bg-green-100 text-green-700 px-2 py-1 rounded-full text-xs font-semibold ml-2">Lối
                      đi</span>
                  </div>
                  <div class="flex items-center gap-2 mb-1">
                    <i :class="group.isBusiness
                        ? 'fa-solid fa-chair text-yellow-400'
                        : 'fa-solid fa-chair text-indigo-400'
                      "></i>
                    <span class="font-bold">{{ group.count }} ghế khả dụng</span>
                  </div>
                  <div class="flex items-center gap-2 mb-1">
                    <i class="fa-solid fa-money-bill-wave text-green-400"></i>
                    <span :class="group.isBusiness
                        ? 'font-semibold text-yellow-700'
                        : 'font-semibold text-indigo-700'
                      ">{{ formatCurrency(group.price) }}</span>
                  </div>
                  <div class="flex items-center gap-2">
                    <i class="fa-solid fa-suitcase-rolling text-gray-400"></i>
                    <span class="text-xs text-gray-600">Hành lý: {{ group.carryOnLuggage }} kg</span>
                  </div>
                </label>
              </template>
            </div>
          </div>
          <!-- Thông tin chuyến bay -->
          

          <!-- Gallery ảnh nếu có nhiều ảnh -->
          <div v-if="flightDetail.images && flightDetail.images.length > 1"
            class="bg-white rounded-xl p-4 shadow-inner">
            <div class="flex-shrink-0 mb-4 md:mb-0">
                <img
                  :src="currentGalleryImage?.imageUrl"
                  :alt="currentGalleryImage?.altText"
                  class="w-full h-auto object-cover rounded-xl border border-gray-300 shadow"
                />
              </div>
            <div class="font-semibold text-gray-700 my-2">Hình ảnh khác:</div>
            <div class="flex gap-4 overflow-x-auto">
              <img v-for="img in flightDetail.images" :key="img.imageId" :src="img.imageUrl" :alt="img.altText"
                class="w-32 h-24 object-cover rounded-lg border border-gray-200"  @mouseenter="setCurrentGalleryImage(img)" />
            </div>
          </div>
          
        </div>

        <!-- Cột phải: Danh sách vé + tổng quan vé đã chọn -->
        <div class="flex-1 min-w-[320px] space-y-8">
          <!-- Danh sách vé -->
          <div class="bg-indigo-50 rounded-xl p-6 shadow-inner">
            <div class="flex flex-col gap-2 text-gray-700">
              <div class="flex items-center gap-2">
                <i class="fas fa-plane-departure text-indigo-500"></i>
                <span class="font-semibold">Đi:</span>
                {{ formatTime(flightDetail.departureTime) }} -
                {{ flightDetail.departureAirport?.name }}
              </div>
              <div class="flex items-center gap-2">
                <i class="fas fa-plane-arrival text-indigo-500"></i>
                <span class="font-semibold">Đến:</span>
                {{ formatTime(flightDetail.arrivalTime) }} -
                {{ flightDetail.arrivalAirport?.name }}
              </div>
              <div class="flex items-center gap-2">
                <i class="fas fa-clock text-indigo-500"></i>
                <span class="font-semibold">Thời lượng:</span>
                {{ durationDisplay }}
              </div>
              <div class="flex items-center gap-2">
                <i class="fas fa-calendar text-indigo-500"></i>
                <span class="font-semibold">Ngày tạo:</span>
                {{ formatDate(flightDetail.createdAt) }}
              </div>
              <div class="flex items-center gap-2">
                <i class="fas fa-sync text-indigo-500"></i>
                <span class="font-semibold">Cập nhật:</span>
                {{ formatDate(flightDetail.updatedAt) }}
              </div>
            </div>
          </div>
         <div class="bg-indigo-50 rounded-xl p-6 shadow-inner mt-8">
          <div class="  hover:opacity-100 transition-opacity duration-300 group">
          <button @click="goToFlightAdmin" 
                  >
            <i class="fas fa-edit"></i>
            <span class="hidden group-hover:inline">Cập nhật chuyến bay</span>
          </button>
        </div>
            <h3 class="text-lg font-bold text-indigo-700 mb-4 flex items-center gap-2">
              <i class="fa-solid fa-ticket-alt"></i> Thông tin vé đã chọn
            </h3>
            <div v-if="selectedGroup">
              <div class="flex flex-col gap-2 text-gray-700">
                <div>
                  <span class="font-semibold">Loại vé:</span>
                  <span v-if="selectedGroup.isBusiness" class="text-yellow-700 font-bold">Thương gia</span><span v-else
                    class="text-indigo-700 font-bold">Phổ thông</span>
                </div>
                <div>
                  <span class="font-semibold">Số ghế:</span>
                  {{ selectedGroup.slots[0]?.seatNumber }}
                </div>
                <div>
                  <span class="font-semibold">Vị trí:</span>
                  <span v-if="selectedGroup.slots[0]?.isWindow"> Cửa sổ</span>
                  <span v-else-if="selectedGroup.slots[0]?.isAisle"> Lối đi</span>
                  <span v-else>Khác</span>
                </div>
                <div>
                  <span class="font-semibold">Giá: </span>
                  <span class="font-bold text-green-700"> {{
                    formatCurrency(selectedGroup.slots[0]?.price)
                  }}</span>
                </div>
                <div v-if="selectedGroup.isWindow" class="text-xs text-indigo-500 italic">
                  Đã cộng thêm 200,000 VND do chọn ghế cửa sổ
                </div>
                <div>
                  <span class="font-semibold">Hành lý xách tay:</span>
                  {{ selectedGroup.slots[0]?.carryOnLuggage }} kg
                </div>
              </div>
            </div>
            <div v-else class="text-gray-400 italic">
              Vui lòng chọn một vé để xem chi tiết.
            </div>
            <button @click="handleBooking"
              class="mt-6 w-full bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-3 rounded-lg text-lg shadow-lg transition disabled:opacity-50 disabled:cursor-not-allowed"
              :disabled="!selectedGroup">
              Đặt chỗ
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted ,watch} from "vue";
import { useRoute, useRouter } from "vue-router";
import { getFlightDetail, getAvailableSeats } from "@/api/flightApi";
import FindAvailableSlotRequestDto from "@/dto/FindAvailableSlotRequestDto";
import { useAdminAuth } from "@/composables/useAdminAuth";

const route = useRoute();
const flightDetail = ref({
  images: [],
  flightSlots: [],
  amenities: [],
});
const loading = ref(true);
const error = ref("");

const currentGalleryImage = ref(null);

function setCurrentGalleryImage(img) {
  currentGalleryImage.value = img;
}
watch(
  () => flightDetail.value.images,
  (imgs) => {
    if (imgs && imgs.length > 0) {
      currentGalleryImage.value = imgs[0];
    }
  },
  { immediate: true }
);
const mainImage = computed(() =>
  flightDetail.value.images && flightDetail.value.images.length > 0
    ? flightDetail.value.images[0].imageUrl
    : "https://ix-marketing.imgix.net/autotagging.png?auto=format,compress&w=1946"
);

function formatTime(val) {
  if (!val) return "";
  const d = new Date(val);
  return d.toLocaleTimeString("vi-VN", { hour: "2-digit", minute: "2-digit" });
}
function formatCurrency(val) {
  if (!val) return "";
  return Number(val).toLocaleString("vi-VN", {
    style: "currency",
    currency: "VND",
  });
}
function formatDate(val) {
  if (!val) return "";
  const d = new Date(val);
  return d.toLocaleDateString("vi-VN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
}
const durationDisplay = computed(() => {
  if (!flightDetail.value) return "";
  const dep = new Date(flightDetail.value.departureTime);
  const arr = new Date(flightDetail.value.arrivalTime);
  const diff = (arr - dep) / 60000;
  const h = Math.floor(diff / 60);
  const m = diff % 60;
  return `${h}h ${m}m`;
});
const availableSeats = ref({
  economyWindow: 0,
  economyAisle: 0,
  businessWindow: 0,
  businessAisle: 0,
});
const seatGroups = computed(() => {
  const slots = flightDetail.value.flightSlots || [];
  const groups = [
    {
      key: "eco-aisle",
      isBusiness: false,
      isWindow: false,
      label: "Phổ thông - Lối đi",
    },
    {
      key: "eco-window",
      isBusiness: false,
      isWindow: true,
      label: "Phổ thông - Cửa sổ",
    },
    {
      key: "biz-aisle",
      isBusiness: true,
      isWindow: false,
      label: "Thương gia - Lối đi",
    },
    {
      key: "biz-window",
      isBusiness: true,
      isWindow: true,
      label: "Thương gia - Cửa sổ",
    },
  ];
  return groups
    .map((g) => {
      const filtered = slots.filter(
        (s) =>
          s.isBusiness === g.isBusiness &&
          !!s.isWindow === g.isWindow &&
          !!s.isAisle === !g.isWindow
      );
      let scount;
      if (g.isBusiness && g.isWindow) scount = availableSeats.value.businessWindow;
      else if (g.isBusiness && !g.isWindow) scount = availableSeats.value.businessAisle;
      else if (!g.isBusiness && !g.isWindow) scount = availableSeats.value.economyAisle;
      else  scount = availableSeats.value.economyWindow;
      return {
        ...g,
        count: scount,
        price: filtered.length ? filtered[0].price : 0,
        carryOnLuggage: filtered.length ? filtered[0].carryOnLuggage : 0,
        slots: filtered,
      };
    })
    ;
});

onMounted(async () => {
  loading.value = true;
  error.value = "";
  try {
    const id = route.params.id;
    const res = await getFlightDetail(id);
    flightDetail.value = res.data;
  } catch (e) {
    error.value = "Không thể tải dữ liệu chuyến bay.";
  } finally {
    loading.value = false;
  }
  const resavailableSeats = await getAvailableSeats(route.params.id);
  availableSeats.value = resavailableSeats.data;
  console.log(availableSeats.value);
  
});

const router = useRouter();

// Bỏ selectedSlot, luôn dùng FindAvailableSlotRequestDto
const selectedGroupKey = ref(null);

const selectedGroup = computed(() => {
  return seatGroups.value.find((g) => g.key === selectedGroupKey.value) || null;
});

const findAvailableSlotDto = computed(() => {
  if (!selectedGroup.value) return null;
  // Truyền đúng tham số: flightId, isAisle, isWindow, isBusiness
  return new FindAvailableSlotRequestDto(
    flightDetail.value.id,
    selectedGroup.value.isAisle ?? null,
    selectedGroup.value.isWindow ?? null,
    selectedGroup.value.isBusiness ?? null
  );
});

function handleBooking() {
  if (!findAvailableSlotDto.value) {

    window.$toast('Vui lòng chọn loại vé và vị trí!', 'error');

    return;
  }
  router.push({
    path: "/plane/pay",
    query: { dto: JSON.stringify(findAvailableSlotDto.value.toObject()) },
  });
}

const { hasFlightAdminAccess } = useAdminAuth();

function goToFlightAdmin() {
  router.push({ name: 'AdminFlightEdit', params: { id: route.params.id } });
}
</script>

<style scoped></style>
