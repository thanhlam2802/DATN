<template>
  <div class="max-w-5xl mx-auto py-8 px-4">
    <h1 class="text-4xl font-bold mb-8 text-gray-900 drop-shadow flex items-center gap-3">
      <i class="fa-solid fa-plane-departure text-indigo-400 text-3xl"></i>
      Cập nhật thông tin chuyến bay
    </h1>
    <!-- Form cập nhật chuyến bay (có upload ảnh) -->
    <form @submit.prevent="submitUpdate" class="bg-white rounded-2xl shadow-2xl p-10 mb-12 border border-sky-100">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div>
          <label class="block text-base font-semibold text-gray-700 mb-2">Số hiệu chuyến bay</label>
          <input v-model="flight.flightNumber" type="text" class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition" />
        </div>
        <div>
          <label class="block text-base font-semibold text-gray-700 mb-2">Tên chuyến bay</label>
          <input v-model="flight.name" type="text" class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition" />
        </div>
        <div>
          <label class="block text-base font-semibold text-gray-700 mb-2">Hãng hàng không</label>
          <select v-model="flight.airline" class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition">
            <option value="">Chọn hãng</option>
            <option v-for="airline in airlines" :key="airline.id" :value="airline">
              {{ airline.name }}
            </option>
          </select>
        </div>
        <div>
          <label class="block text-base font-semibold text-gray-700 mb-2">Danh mục</label>
          <select v-model="flight.category" class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition">
            <option value="">Chọn danh mục</option>
            <option v-for="category in categories" :key="category.id" :value="category">
              {{ category.name }}
            </option>
          </select>
        </div>
        <div>
          <label class="block text-base font-semibold text-gray-700 mb-2">Sân bay đi</label>
          <select v-model="flight.departureAirport" class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition">
            <option value="">Chọn sân bay đi</option>
            <option v-for="airport in airports" :key="airport.id" :value="airport">
              {{ airport.name }}
            </option>
          </select>
        </div>
        <div>
          <label class="block text-base font-semibold text-gray-700 mb-2">Sân bay đến</label>
          <select v-model="flight.arrivalAirport" class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition">
            <option value="">Chọn sân bay đến</option>
            <option v-for="airport in airports" :key="airport.id" :value="airport">
              {{ airport.name }}
            </option>
          </select>
        </div>
        <div>
          <label class="block text-base font-semibold text-gray-700 mb-2">Thời gian khởi hành</label>
          <input v-model="flight.departureTime" type="datetime-local" class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition" />
        </div>
        <div>
          <label class="block text-base font-semibold text-gray-700 mb-2">Thời gian đến</label>
          <input v-model="flight.arrivalTime" type="datetime-local" class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition" />
        </div>
        <div class="col-span-2">
          <label class="block text-base font-semibold text-gray-700 mb-2">Ảnh chuyến bay</label>
          <input type="file" multiple accept="image/*" @change="onImageChange" class="mb-2" />
          <div class="flex flex-wrap gap-3">
            <div v-for="(img, idx) in images" :key="img.id || idx" class="relative w-28 h-28">
              <img :src="img.url" class="w-full h-full object-cover rounded-xl border shadow" />
              <button type="button" @click="removeImage(idx)" class="absolute top-1 right-1 bg-white/80 rounded-full p-1 text-red-500 hover:bg-red-100 shadow"><i class="fa fa-times"></i></button>
            </div>
          </div>
        </div>
      </div>
      <div class="mt-10 flex justify-end">
        <button type="submit" class="px-8 py-3 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold rounded-xl shadow transition-colors">Cập nhật</button>
      </div>
    </form>
    <!-- Table danh sách vé ghế -->
    <h2 class="text-2xl font-bold mb-6 text-gray-800 flex items-center gap-2"><i class="fa-solid fa-chair text-sky-400"></i>Danh sách vé ghế</h2>
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
          <tr v-for="seat in seats" :key="seat.id" class="hover:bg-sky-50 transition">
            <td class="px-4 py-3">{{ seat.seatNumber }}</td>
            <td class="px-4 py-3">{{ seat.isBusiness ? 'Thương gia' : 'Phổ thông' }}</td>
            <td class="px-4 py-3">{{ seat.isWindow ? 'Cửa sổ' : seat.isAisle ? 'Lối đi' : 'Giữa' }}</td>
            <td class="px-4 py-3">{{ formatCurrency(seat.price) }}</td>
            <td class="px-4 py-3">{{ seat.carryOnLuggage }} kg</td>
            <td class="px-4 py-3">
              <button @click="editSeat(seat)" class="p-2 bg-sky-100 text-sky-600 rounded-full hover:bg-sky-200 shadow transition" title="Sửa">
                <i class="fa fa-pen"></i>
              </button>
              <button @click="deleteSeat(seat)" class="ml-2 p-2 bg-red-100 text-red-600 rounded-full hover:bg-red-200 shadow transition" title="Xóa">
                <i class="fa fa-trash"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <!-- Dialog chỉnh sửa vé ghế -->
    <EditFlightSeatAdmin v-if="editingSeat" :seat="editingSeat" @close="editingSeat = null" @saved="onSeatSaved" @deleted="onSeatDeleted" />
  </div>
</template>
<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import EditFlightSeatAdmin from './EditFlightSeatAdmin.vue'
import { getAllAirlines, getAllAirports, getAllFlightCategories, updateAdminFlight, getFlightDetail, deleteAdminSeat, addFlightImages, deleteFlightImage } from '@/api/flightApi'

const props = defineProps({ flightId: [String, Number] })
const route = useRoute()
const flightId = computed(() => props.flightId || route.params.id || route.params.flightId)
const flight = ref({ name: '', airline: '', images: [] })
const images = ref([])
const airlines = ref([])
const categories = ref([])
const airports = ref([])
const seats = ref([])
const editingSeat = ref(null)
const flightNumberManuallyChanged = ref(false)

onMounted(async () => {
  try {
    const [airlinesRes, airportsRes, categoriesRes, flightRes] = await Promise.all([
      getAllAirlines(),
      getAllAirports(),
      getAllFlightCategories(),
      getFlightDetail(flightId.value)
    ])
    airlines.value = airlinesRes.data
    airports.value = airportsRes.data
    categories.value = categoriesRes.data
    // Map flight detail
    const f = flightRes.data
    // Map các trường quan hệ về object (nếu backend trả về id)
    flight.value = {
      ...f,
      airline: f.airline || (f.airlineId ? airlines.value.find(a => a.id === f.airlineId) : null),
      category: f.category || (f.categoryId ? categories.value.find(c => c.id === f.categoryId) : null),
      departureAirport: f.departureAirport || (f.departureAirportId ? airports.value.find(a => a.id === f.departureAirportId) : null),
      arrivalAirport: f.arrivalAirport || (f.arrivalAirportId ? airports.value.find(a => a.id === f.arrivalAirportId) : null)
    }
    // Nếu có seats thì load vào bảng
    if (f.flightSlots) seats.value = f.flightSlots
    // Nếu có images thì load vào images
    if (f.images) images.value = f.images.map(img => ({ id: img.id, url: img.imageUrl }))
    console.log('images:', f.images)
  } catch (e) {
    console.error('Không thể tải dữ liệu:', e)
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

// Xử lý thêm ảnh mới - gọi API ngay lập tức
async function onImageChange(e) {
  const files = Array.from(e.target.files)
  if (files.length === 0) return
  
  try {
    const formData = new FormData()
    files.forEach(file => formData.append('files', file))
    
    const response = await addFlightImages(flightId.value, formData)
    const newImages = response.data.map(img => ({ id: img.id, url: img.imageUrl }))
    images.value.push(...newImages)
    
    // Reset input file
    e.target.value = ''  
    console.log('Thêm ảnh thành công:', newImages.length, 'ảnh')
  } catch (error) {
    console.error('Lỗi khi thêm ảnh:', error)
    alert('Lỗi khi thêm ảnh!')
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
    return
  }
  
  try {
    await deleteFlightImage(flightId.value, imageToRemove.id)
    images.value.splice(idx, 1)
    console.log('Xóa ảnh thành công:', imageToRemove.id)
  } catch (error) {
    console.error('Lỗi khi xóa ảnh:', error)
    alert('Lỗi khi xóa ảnh!')
  }
}

// Cập nhật thông tin chuyến bay (không xử lý ảnh)
async function submitUpdate() {
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
    alert('Cập nhật chuyến bay thành công!')
  } catch (error) {
    console.error('Lỗi khi cập nhật chuyến bay:', error)
    alert('Lỗi khi cập nhật chuyến bay!')
  }
}

function editSeat(seat) {
  editingSeat.value = { ...seat }
}

function onSeatSaved(updatedSeat) {
  // Cập nhật lại seat trong danh sách
  const idx = seats.value.findIndex(s => s.id === updatedSeat.id)
  if (idx !== -1) seats.value[idx] = updatedSeat
  editingSeat.value = null
}

function deleteSeat(seat) {
  if (!confirm('Bạn có chắc muốn xóa ghế này?')) return
  deleteAdminSeat(flightId.value, seat.id)
    .then(() => {
      seats.value = seats.value.filter(s => s.id !== seat.id)
    })
    .catch(() => {
      alert('Xóa ghế thất bại!')
    })
}

function onSeatDeleted(seatId) {
  seats.value = seats.value.filter(s => s.id !== seatId)
  editingSeat.value = null
}

function formatCurrency(val) {
  if (!val) return ''
  return Number(val).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })
}
</script> 