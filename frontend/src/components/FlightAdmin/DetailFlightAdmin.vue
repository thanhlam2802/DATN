<template>
  <div class="max-w-5xl mx-auto py-8 px-4">
    <h1 class="text-4xl font-bold mb-8 text-gray-900 drop-shadow flex items-center gap-3">
      <i class="fa-solid fa-plane-departure text-indigo-400 text-3xl"></i>
      Cập nhật thông tin chuyến bay
    </h1>
    <div v-if="loadingPage" class="fixed inset-0 flex items-center justify-center bg-white bg-opacity-80 z-50">
      <div class="flex flex-col items-center">
        <span class="animate-spin w-12 h-12 border-4 border-indigo-300 border-t-indigo-600 rounded-full mb-4"></span>
        <span class="text-lg text-indigo-700 font-semibold">Đang tải dữ liệu chuyến bay...</span>
      </div>
    </div>
    <div v-else>
      <!-- Form cập nhật chuyến bay (có upload ảnh) -->
      <form @submit.prevent="submitUpdate" class="bg-white rounded-2xl shadow-2xl p-10 mb-12 border border-sky-100">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
          <div>
            <label class="block text-base font-semibold text-gray-700 mb-2">Số hiệu chuyến bay</label>
            <input v-model="flight.flightNumber" type="text"
              class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition" />
          </div>
          <div>
            <label class="block text-base font-semibold text-gray-700 mb-2">Tên chuyến bay</label>
            <input v-model="flight.name" type="text"
              class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition" />
          </div>
          <div>
            <label class="block text-base font-semibold text-gray-700 mb-2">Hãng hàng không</label>
            <select v-model="flight.airline"
              class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition">
              <option value="">Chọn hãng</option>
              <option v-for="airline in airlines" :key="airline.id" :value="airline">
                {{ airline.name }}
              </option>
            </select>
          </div>
          <div>
            <label class="block text-base font-semibold text-gray-700 mb-2">Danh mục</label>
            <select v-model="flight.category"
              class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition">
              <option value="">Chọn danh mục</option>
              <option v-for="category in categories" :key="category.id" :value="category">
                {{ category.name }}
              </option>
            </select>
          </div>
          <div>
            <label class="block text-base font-semibold text-gray-700 mb-2">Sân bay đi</label>
            <select v-model="flight.departureAirport"
              class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition">
              <option value="">Chọn sân bay đi</option>
              <option v-for="airport in airports" :key="airport.id" :value="airport">
                {{ airport.name }}
              </option>
            </select>
          </div>
          <div>
            <label class="block text-base font-semibold text-gray-700 mb-2">Sân bay đến</label>
            <select v-model="flight.arrivalAirport"
              class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition">
              <option value="">Chọn sân bay đến</option>
              <option v-for="airport in airports" :key="airport.id" :value="airport">
                {{ airport.name }}
              </option>
            </select>
          </div>
          <div>
            <label class="block text-base font-semibold text-gray-700 mb-2">Thời gian khởi hành</label>
            <input v-model="flight.departureTime" type="datetime-local"
              class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition" />
          </div>
          <div>
            <label class="block text-base font-semibold text-gray-700 mb-2">Thời gian đến</label>
            <input v-model="flight.arrivalTime" type="datetime-local"
              class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition" />
          </div>
          <div class="">
            <button type="submit" :disabled="loadingUpdate"
              class="px-8 py-3 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold rounded-xl shadow transition-colors flex items-center justify-center min-w-[120px]">
              <span v-if="loadingUpdate" class="animate-spin mr-2 w-4 h-4 border-2 border-white border-t-indigo-500 rounded-full"></span>
              <span>{{ loadingUpdate ? 'Đang cập nhật...' : 'Cập nhật' }}</span>
            </button>
          </div>
          <div class="col-span-2">
            <label class="block text-base font-semibold text-gray-700 mb-2">Ảnh chuyến bay</label>
            <label
              class="block w-full max-w-xs m-3 px-4 py-3 text-center text-sm text-gray-700 bg-white border border-gray-300 rounded-lg cursor-pointer hover:bg-gray-100 transition">
              Chọn ảnh (có thể chọn nhiều)
              <input type="file" multiple accept="image/*" @change="onImageChange" class="hidden" :disabled="loadingImage" />
            </label>
            <div v-if="loadingImage" class="text-indigo-600 text-sm flex items-center gap-2 mb-2"><span class="animate-spin w-4 h-4 border-2 border-indigo-500 border-t-white rounded-full"></span> Đang tải ảnh...</div>

            <div class="flex flex-wrap gap-3">
              <div v-for="(img, idx) in images" :key="img.id || idx" class="relative w-28 h-28">
                <img :src="img.imageUrl" class="w-full h-full object-cover rounded-xl border shadow" />
                <button type="button" @click="removeImage(idx)" :disabled="loadingRemove"
                  class="absolute top-1 right-1 bg-white/80 rounded-full p-1 text-red-500 hover:bg-red-100 shadow flex items-center justify-center">
                  <i v-if="!loadingRemove" class="fa fa-times"></i>
                  <span v-else class="animate-spin w-4 h-4 border-2 border-red-500 border-t-white rounded-full"></span>
                </button>
              </div>
            </div>
          </div>
        </div>

      </form>
      <!-- Table danh sách vé ghế -->
      <h2 class="text-2xl font-bold mb-6 text-gray-800 flex items-center gap-2"><i
          class="fa-solid fa-chair text-sky-400"></i>Danh sách vé ghế đã đặt</h2>
      <div class="overflow-x-auto">
        <table class="min-w-full bg-white rounded-2xl shadow-lg border border-sky-100">
          <thead class="bg-sky-50">
            <tr>
              <th class="px-4 py-3 text-left text-base font-semibold text-sky-700">Số ghế</th>
              <th class="px-4 py-3 text-left text-base font-semibold text-sky-700">Loại vé</th>
              <th class="px-4 py-3 text-left text-base font-semibold text-sky-700">Vị trí</th>
              <th class="px-4 py-3 text-left text-base font-semibold text-sky-700">Giá</th>
              <th class="px-4 py-3 text-left text-base font-semibold text-sky-700">Hành lý</th>
              <th class="px-4 py-3 text-left text-base font-semibold text-sky-700">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="seat in slotBooked" :key="seat.id" class="hover:bg-sky-50 transition">
              <td class="px-4 py-3">{{ seat.seatNumber }}</td>
              <td class="px-4 py-3">{{ seat.isBusiness ? 'Thương gia' : 'Phổ thông' }}</td>
              <td class="px-4 py-3">{{ seat.isWindow ? 'Cửa sổ' : seat.isAisle ? 'Lối đi' : 'Giữa' }}</td>
              <td class="px-4 py-3">{{ formatCurrency(seat.price) }}</td>
              <td class="px-4 py-3">{{ seat.carryOnLuggage }} kg</td>
              <td class="px-4 py-3">
                <button @click="editSeat(seat)"
                  class=" text-[#4f39f6] rounded-full hover:bg-sky-200 p-2 transition" title="Sửa">
                  <i class="fa fa-pen"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <h2 class="text-2xl font-bold my-6 text-gray-800 flex items-center gap-2">
      <i class="fa-solid fa-layer-group text-sky-400"></i>Quản lý nhóm ghế chưa đặt
    </h2>
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-12">
      <div v-for="group in seatGroups" :key="group.key" class="bg-white rounded-xl shadow p-6 border">
        <div class="font-semibold mb-2">
          {{ group.label }}
        </div>
        <div class="mb-2">
          <label class="block text-sm font-medium">Số lượng:</label>
          <input type="number" min="0" v-model.number="group.count"
            class="w-full border rounded px-2 py-1" />
        </div>
        <div class="mb-2">
          <label class="block text-sm font-medium">Giá:</label>
          <input type="number" min="0" v-model.number="group.price"
            class="w-full border rounded px-2 py-1" />
        </div>
        <div>
          <label class="block text-sm font-medium">Giới hạn hành lý (kg):</label>
          <input type="number" min="0" v-model.number="group.carryOnLuggage"
            class="w-full border rounded px-2 py-1" />
        </div>
        <button @click="saveGroupEdit(group)" :disabled="loadingGroup[group.key]" class="mt-3 px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700 flex items-center justify-center min-w-[100px]">
          <span v-if="loadingGroup[group.key]" class="animate-spin mr-2 w-4 h-4 border-2 border-white border-t-indigo-500 rounded-full"></span>
          <span>{{ loadingGroup[group.key] ? 'Đang lưu...' : 'Lưu nhóm' }}</span>
        </button>
      </div>
    </div>
      <!-- Dialog chỉnh sửa vé ghế -->
      <EditFlightSeatAdmin v-if="editingSeat" :seat="editingSeat" @close="editingSeat = null" @saved="onSeatSaved" />
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import EditFlightSeatAdmin from './EditFlightSeatAdmin.vue'
import { getAllAirlines, getAllAirports, getAllFlightCategories, updateAdminFlight, getFlightDetail, flightBooked, addFlightImages, deleteFlightImage ,getAvailableSeats,updateGroupSeat} from '@/api/flightApi'

const props = defineProps({ flightId: [String, Number] })
const route = useRoute()
const flightId = computed(() => props.flightId || route.params.id || route.params.flightId)
const flight = ref({ name: '', airline: '', images: [] })
const images = ref([])
const airlines = ref([])
const categories = ref([])
const airports = ref([])
const editingSeat = ref(null)
const flightNumberManuallyChanged = ref(false)
 const slotBooked = ref([])
const loading = ref(false)
const loadingGroup = ref({})
const loadingImage = ref(false)
const loadingRemove = ref(false)
const loadingUpdate = ref(false)
const loadingPage = ref(true)
onMounted(async () => {
  loadingPage.value = true;
  try { 
    const [airlinesRes, airportsRes, categoriesRes,flightSlotRes, flightRes ,resavailableSeats] =await  Promise.all([
      getAllAirlines(),
      getAllAirports(),
      getAllFlightCategories(),
      flightBooked(flightId.value),
      getFlightDetail(flightId.value),
      getAvailableSeats(flightId.value)
    ])
    airlines.value = airlinesRes.data
    airports.value = airportsRes.data
    categories.value = categoriesRes.data
     slotBooked.value = flightSlotRes.data
    const f = flightRes.data
    // Map các trường quan hệ về object (nếu backend trả về id)
    flight.value = {
      ...f,
      airline: f.airline || (f.airlineId ? airlines.value.find(a => a.id === f.airlineId) : null),
      category: f.category || (f.categoryId ? categories.value.find(c => c.id === f.categoryId) : null),
      departureAirport: f.departureAirport || (f.departureAirportId ? airports.value.find(a => a.id === f.departureAirportId) : null),
      arrivalAirport: f.arrivalAirport || (f.arrivalAirportId ? airports.value.find(a => a.id === f.arrivalAirportId) : null)
    }
    images.value = flight.value.images
    
    availableSeats.value = resavailableSeats.data;
    console.log(images.value);
  } catch (e) {
    window.$toast('Không thể tải thông tin chuyến bay.', 'error');
    error.value = "Không thể tải dữ liệu chuyến bay.";
  } finally {
    loadingPage.value = false;
  }
})
// Tự động sinh số hiệu chuyến bay
const autoFlightNumber = computed(() => {
  if (!flight.value.name || !flight.value.airline || !flight.value.departureTime) return ''
  const nameAcronym = flight.value.name
    .normalize('NFD').replace(/\p{Diacritic}/gu, '')
    .split(/\s+/).map(w => w[0]).join('').toUpperCase()
  const airlineAcronym = flight.value.airline ? flight.value.airline.name.split(' ').map(w => w[0]).join('').toUpperCase() : ''
  const d = new Date(flight.value.departureTime)
  const hour = d.getHours().toString().padStart(2, '0')
  const min = d.getMinutes().toString().padStart(2, '0')
  const rand = Math.floor(10000 + Math.random() * 90000)
  return `${nameAcronym}-${airlineAcronym}-${hour}${min}-${rand}`
})

// Nếu user chưa sửa thủ công, tự động cập nhật flightNumber
watch([
  () => flight.value.name,
  () => flight.value.airline,
  () => flight.value.departureTime
], () => {
  if (!flightNumberManuallyChanged.value) {
    flight.value.flightNumber = autoFlightNumber.value
  }
})

// Nếu user sửa flightNumber thủ công, không tự động nữa
watch(() => flight.value.flightNumber, (val, oldVal) => {
  if (val && val !== autoFlightNumber.value) {
    flightNumberManuallyChanged.value = true
  }
})
function saveGroupEdit(entity){
  loadingGroup.value[entity.key] = true;
  updateGroupSeat(flight.value.id,entity)
    .then(() => {
      window.$toast('Cập nhật nhóm ghế thành công!', 'success');
    })
    .catch(() => {
      window.$toast('Cập nhật nhóm ghế thất bại!', 'error');
    })
    .finally(() => {
      loadingGroup.value[entity.key] = false;
    });
}
// Xử lý thêm ảnh mới - gọi API ngay lập tức
async function onImageChange(e) {
  const files = Array.from(e.target.files)
  if (files.length === 0) return
  loadingImage.value = true;
  try {
    const formData = new FormData()
    files.forEach(file => formData.append('files', file))

    const response = await addFlightImages(flightId.value, formData)
    const newImages = response.data.map(img => ({ id: img.id, imageUrl: img.imageUrl }))
    images.value.push(...newImages)

    // Reset input file
    e.target.value = ''
    console.log('Thêm ảnh thành công:', newImages.length, 'ảnh')
    window.$toast('Thêm ảnh thành công!', 'success')
  } catch (error) {
    console.error('Lỗi khi thêm ảnh:', error)
    window.$toast('Lỗi khi thêm ảnh!', 'error')
  } finally {
    loadingImage.value = false;
  }
}

// Xử lý xóa ảnh - gọi API ngay lập tức
async function removeImage(idx) {
  console.log('idx:', images.value)
  console.log('Xóa ảnh:', images.value[idx])
  const imageToRemove = images.value[idx]
  if (!imageToRemove.id) {
    console.log('Xóa ảnh mới:', imageToRemove.id)
    // Nếu là ảnh mới chưa lưu, chỉ xóa khỏi danh sách local
    images.value.splice(idx, 1)
    window.$toast('Xóa ảnh thành công!', 'success')
    return
  }
  loadingRemove.value = true;
  try {
    await deleteFlightImage(flightId.value, imageToRemove.id)
    images.value.splice(idx, 1)
    console.log('Xóa ảnh thành công:', imageToRemove.id)
    window.$toast('Xóa ảnh thành công!', 'success')
  } catch (error) {
    console.error('Lỗi khi xóa ảnh:', error)
    window.$toast('Lỗi khi xóa ảnh!', 'error')
  } finally {
    loadingRemove.value = false;
  }
}

// Cập nhật thông tin chuyến bay (không xử lý ảnh)
async function submitUpdate() {
  loadingUpdate.value = true;
  try {
    // Tạo object chỉ chứa các trường chính
    const flightData = {
      id: flight.value.id,
      name: flight.value.name,
      flightNumber: flight.value.flightNumber,
      departureTime: flight.value.departureTime,
      arrivalTime: flight.value.arrivalTime,
      airlineId: flight.value.airline ? flight.value.airline.id : null,
      categoryId: flight.value.category ? flight.value.category.id : null,
      departureAirportId: flight.value.departureAirport ? flight.value.departureAirport.id : null,
      arrivalAirportId: flight.value.arrivalAirport ? flight.value.arrivalAirport.id : null
    }

    await updateAdminFlight(flight.value.id, flightData)
    window.$toast('Cập nhật chuyến bay thành công!', 'success')
  } catch (error) {
    console.error('Lỗi khi cập nhật chuyến bay:', error)
    window.$toast('Có lỗi khi cập nhật chuyến bay!', 'error')
  } finally {
    loadingUpdate.value = false;
  }
}

function editSeat(seat) {
  editingSeat.value = { ...seat }
}

 async function onSeatSaved() {
  const restslot =await flightBooked(flightId.value);
  console.log(restslot);
  
  slotBooked.value = restslot.data;
}

function formatCurrency(val) {
  if (!val) return ''
  return Number(val).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })
}


const availableSeats = ref({
  economyWindow: 0,
  economyAisle: 0,
  businessWindow: 0,
  businessAisle: 0,
});
const seatGroups = computed(() => {
  const slots = flight.value.flightSlots|| [];
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
        carryOnLuggage: filtered.length ? filtered[0].carryOnLuggage : 0
      };
    })
    ;
});
</script>