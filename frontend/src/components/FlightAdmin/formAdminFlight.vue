<template class="">
  <div class="w-full min-h-screen py-10 z-0 relative 
            after:absolute 
            after:w-96 after:h-96 
            after:bg-sky-300 
            after:-z-10 
            after:rounded-full 
            after:-top-72
            after:left-4 
            after:blur-xl 
            after:[box-shadow:-100px_50px_30px_100px_#7dd3fc]">
  
    <div class=" px-4 w-full ">
      <!-- Header -->
      <div class="mb-8 text-center">
        <h1 class="text-4xl font-bold text-gray-900">Quản lý chuyến bay</h1>
        <p class="mt-2 text-gray-600">Thêm hoặc chỉnh sửa thông tin chuyến bay</p>
      </div>

      <!-- Form -->
      <div class="bg-white rounded-xl shadow-lg p-8 relative z-10 overflow-hidden
      after:absolute 
            after:w-96 after:h-96 
            after:bg-sky-300 
            after:-z-10 
            after:rounded-full 
            after:-top-60
            after:-right-48
            after:blur-xl 
            after:[box-shadow:-100px_50px_30px_100px_#7dd3fc]">
        <form @submit.prevent="submitFlight">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- Upload ảnh chuyến bay -->
            <div class="col-span-2">
              <label class="block text-sm font-medium text-gray-700 mb-2">Ảnh chuyến bay</label>
              <input type="file" multiple accept="image/*" @change="onImageChange" class="mb-2" />
              <div class="flex flex-wrap gap-2">
                <div v-for="(img, idx) in images" :key="idx" class="relative w-24 h-24">
                  <img :src="img.preview" class="w-full h-full object-cover rounded border" />
                  <button type="button" @click="removeImage(idx)" class="absolute top-1 right-1 bg-white/80 rounded-full p-1 text-red-500 hover:bg-red-100"><i class="fa fa-times"></i></button>
                </div>
              </div>
            </div>
            <!-- Flight Number (preview only) -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Số hiệu chuyến bay (tự sinh)</label>
              <input
                :value="previewFlightNumber"
                type="text"
                disabled
                class="w-full border border-gray-300 rounded-lg px-4 py-2 bg-gray-100 text-gray-500"
                placeholder="Sẽ tự sinh khi nhập đủ thông tin"
              />
            </div>

            <!-- Name -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Tên chuyến bay</label>
              <input
                v-model="flight.name"
                type="text"
                required
                class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="VD: Hà Nội - Đà Nẵng"
              />
            </div>

            <!-- Hãng hàng không -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Hãng hàng không</label>
              <select
                v-model="flight.airline"
                required
                class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="">Chọn hãng</option>
                <option v-for="airline in airlines" :key="airline.id" :value="airline">
                  {{ airline.name }}
                </option>
              </select>
            </div>

            <!-- Departure Airport -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Sân bay đi</label>
              <select
                v-model="flight.departureAirport"
                required
                class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="">Chọn sân bay đi</option>
                <option v-for="airport in airports" :key="airport.id" :value="airport">
                  {{ airport.name }}
                </option>
              </select>
            </div>

            <!-- Arrival Airport -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Sân bay đến</label>
              <select
                v-model="flight.arrivalAirport"
                required
                class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="">Chọn sân bay đến</option>
                <option v-for="airport in airports" :key="airport.id" :value="airport">
                  {{ airport.name }}
                </option>
              </select>
            </div>

            <!-- Departure Time -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Thời gian khởi hành</label>
              <input
                v-model="flight.departureTime"
                type="datetime-local"
                required
                class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              />
            </div>

            <!-- Arrival Time -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Thời gian đến</label>
              <input
                v-model="flight.arrivalTime"
                type="datetime-local"
                required
                class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              />
            </div>

            <!-- Category -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Danh mục</label>
              <select
                v-model="flight.category"
                class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="">Chọn danh mục</option>
                <option v-for="category in flightCategories" :key="category.id" :value="category">
                  {{ category.name }}
                </option>
              </select>
            </div>

            <!-- Tổng số vé -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Tổng số vé</label>
              <div class="flex gap-2 mb-2">
                <button v-for="preset in seatPresets" :key="preset" type="button" @click="ticketForm.total = preset" class="px-3 py-1 rounded bg-gray-100 hover:bg-blue-100 text-sm border border-gray-200">{{ preset }}</button>
              </div>
              <input
                v-model.number="ticketForm.total"
                type="number"
                min="1"
                required
                class="w-full border border-gray-300 rounded-lg px-4 py-2"
                placeholder="Tổng số vé"
              />
            </div>

            <!-- Tỷ lệ phổ thông/thương gia -->
            <div class="col-span-2">
              <label class="block text-sm font-medium text-gray-700 mb-2">Tỷ lệ phổ thông / thương gia</label>
              <div class="flex items-center gap-4">
                <span class="text-indigo-700 font-bold">Phổ thông:
                  <input type="number" min="0" max="100" v-model.number="economyRatio" class="w-16 border border-gray-300 rounded px-1 py-0.5 text-center" />%
                </span>
                <input type="range" min="0" max="100" v-model.number="economyRatio" class="w-1/2" />
                <span class="text-yellow-700 font-bold">Thương gia:
                  <input type="number" min="0" max="100" :value="businessRatio" disabled class="w-16 border border-gray-200 rounded px-1 py-0.5 text-center bg-gray-100" />%
                </span>
              </div>
              <div class="flex gap-8 mt-2">
                <span class="text-xs text-gray-500">Số vé phổ thông:
                  <input type="number" min="0" :max="ticketForm.total" v-model.number="ticketForm.economy" class="w-16 border border-gray-300 rounded px-1 py-0.5 text-center" />
                </span>
                <span class="text-xs text-gray-500">Số vé thương gia:
                  <input type="number" min="0" :max="ticketForm.total" v-model.number="ticketForm.business" class="w-16 border border-gray-300 rounded px-1 py-0.5 text-center" />
                </span>
              </div>
            </div>

            <!-- Tỷ lệ cửa sổ/lối đi phổ thông -->
            <div class="col-span-2">
              <label class="block text-sm font-medium text-gray-700 mb-2">Tỷ lệ cửa sổ / lối đi (Phổ thông)</label>
              <div class="flex items-center gap-4">
                <span class="text-blue-700 font-bold">Cửa sổ:
                  <input type="number" min="0" max="100" v-model.number="economyWindowRatio" class="w-16 border border-gray-300 rounded px-1 py-0.5 text-center" />%
                </span>
                <input type="range" min="0" max="100" v-model.number="economyWindowRatio" class="w-1/2" />
                <span class="text-green-700 font-bold">Lối đi:
                  <input type="number" min="0" max="100" :value="100 - economyWindowRatio" disabled class="w-16 border border-gray-200 rounded px-1 py-0.5 text-center bg-gray-100" />%
                </span>
              </div>
              <div class="flex gap-8 mt-2">
                <span class="text-xs text-gray-500">Số vé cửa sổ:
                  <input type="number" min="0" :max="ticketForm.economy" v-model.number="ticketForm.economyWindow" class="w-16 border border-gray-300 rounded px-1 py-0.5 text-center" />
                </span>
                <span class="text-xs text-gray-500">Số vé lối đi:
                  <input type="number" min="0" :max="ticketForm.economy" v-model.number="ticketForm.economyAisle" class="w-16 border border-gray-300 rounded px-1 py-0.5 text-center" />
                </span>
              </div>
            </div>

            <!-- Tỷ lệ cửa sổ/lối đi thương gia -->
            <div class="col-span-2">
              <label class="block text-sm font-medium text-gray-700 mb-2">Tỷ lệ cửa sổ / lối đi (Thương gia)</label>
              <div class="flex items-center gap-4">
                <span class="text-blue-700 font-bold">Cửa sổ:
                  <input type="number" min="0" max="100" v-model.number="businessWindowRatio" class="w-16 border border-gray-300 rounded px-1 py-0.5 text-center" />%
                </span>
                <input type="range" min="0" max="100" v-model.number="businessWindowRatio" class="w-1/2" />
                <span class="text-green-700 font-bold">Lối đi:
                  <input type="number" min="0" max="100" :value="100 - businessWindowRatio" disabled class="w-16 border border-gray-200 rounded px-1 py-0.5 text-center bg-gray-100" />%
                </span>
              </div>
              <div class="flex gap-8 mt-2">
                <span class="text-xs text-gray-500">Số vé cửa sổ:
                  <input type="number" min="0" :max="ticketForm.business" v-model.number="ticketForm.businessWindow" class="w-16 border border-gray-300 rounded px-1 py-0.5 text-center" />
                </span>
                <span class="text-xs text-gray-500">Số vé lối đi:
                  <input type="number" min="0" :max="ticketForm.business" v-model.number="ticketForm.businessAisle" class="w-16 border border-gray-300 rounded px-1 py-0.5 text-center" />
                </span>
              </div>
            </div>

            <!-- Giá vé phổ thông -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Giá vé phổ thông (VND)</label>
              <input
                v-model.number="ticketForm.economyPrice"
                type="number"
                min="0"
                required
                class="w-full border border-gray-300 rounded-lg px-4 py-2"
                placeholder="Giá vé phổ thông"
              />
            </div>
            <!-- Giá vé thương gia -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Giá vé thương gia (VND)</label>
              <input
                v-model.number="ticketForm.businessPrice"
                type="number"
                min="0"
                required
                class="w-full border border-gray-300 rounded-lg px-4 py-2"
                placeholder="Giá vé thương gia"
              />
            </div>
            
            <!-- Trọng lượng hành lý phổ thông -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Hành lý phổ thông (kg)</label>
              <input
                v-model.number="ticketForm.economyLuggage"
                type="number"
                min="1"
                max="50"
                required
                class="w-full border border-gray-300 rounded-lg px-4 py-2"
                placeholder="Trọng lượng hành lý phổ thông"
              />
            </div>
            
            <!-- Trọng lượng hành lý thương gia -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Hành lý thương gia (kg)</label>
              <input
                v-model.number="ticketForm.businessLuggage"
                type="number"
                min="1"
                max="50"
                required
                class="w-full border border-gray-300 rounded-lg px-4 py-2"
                placeholder="Trọng lượng hành lý thương gia"
              />
            </div>
          </div>

          <!-- Preview sơ đồ ghế -->
          <div class="mt-8">
            <label class="block text-sm font-medium text-gray-700 mb-2">Preview sơ đồ ghế</label>
            <div class="flex flex-wrap gap-2 w-full">
              <div class="flex flex-wrap gap-6 mb-4 items-center text-sm">
                <div class="flex items-center gap-1"><i class="fa fa-chair text-indigo-500"></i> = Phổ thông</div>
                <div class="flex items-center gap-1"><i class="fa fa-crown text-yellow-500"></i> = Thương gia</div>
                <div class="flex items-center gap-1">
                  <span class="inline-block w-4 h-4 rounded bg-indigo-100 border-2 border-indigo-400"></span> 
                  <span class="inline-block w-4 h-4 rounded bg-yellow-100 border-2 border-yellow-400"></span> 
                  = Ghế cửa sổ</div>
                <div class="flex items-center gap-1">
                  <span class="inline-block w-4 h-4 rounded bg-yellow-50 border border-yellow-200"></span> 
                  <span class="inline-block w-4 h-4 rounded bg-indigo-50 border border-indigo-200"></span> 
                  = Ghế lối đi/giữa</div>
              </div> 
              <div class="w-full flex gap-8 justify-center">
                <!-- Thương gia -->
                <div v-if="seatPreviewRows.businessRows.length" class="flex flex-col gap-4">
                  <div class="text-center text-yellow-700 font-bold mb-1">Thương gia</div>
                  <div v-for="(row, ridx) in seatPreviewRows.businessRows" :key="'b'+ridx" class="flex items-center gap-1 justify-center mb-2">
                    <template v-for="(seat, cidx) in row" :key="cidx">
                      <div v-if="cidx === 3" class="w-4"></div>
                      <div :class="['w-12 h-12 flex flex-col items-center justify-center border-2 rounded-lg', seat.posColor, 'mx-0.5']">
                        <i :class="['fa', seat.icon, seat.color]"></i>
                        <span class="font-bold text-xs">{{ seat.label }}</span>
                      </div>
                    </template>
                  </div>
                </div>
                <!-- Phổ thông -->
                <div v-if="seatPreviewRows.economyRows.length" class="flex flex-col gap-4">
                  <div class="text-center text-indigo-700 font-bold mb-1">Phổ thông</div>
                  <div v-for="(row, ridx) in seatPreviewRows.economyRows" :key="'e'+ridx" class="flex items-center gap-1 justify-center mb-2">
                    <template v-for="(seat, cidx) in row" :key="cidx">
                      <div v-if="cidx === 3" class="w-4"></div>
                      <div :class="['w-12 h-12 flex flex-col items-center justify-center border-2 rounded-lg', seat.posColor, 'mx-0.5']">
                        <i :class="['fa', seat.icon, seat.color]"></i>
                        <span class="font-bold text-xs">{{ seat.label }}</span>
                      </div>
                    </template>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Buttons -->
          <div class="mt-8 flex justify-end space-x-4">
            <button
              type="button"
              @click="$router.go(-1)"
              class="px-6 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50"
            >
              Hủy
            </button>
            <button
              type="submit"
              :disabled="loading"
              class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
            >
              {{ loading ? 'Đang lưu...' : (isEdit ? 'Cập nhật' : 'Thêm mới') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { createAdminFlight, updateAdminFlight, getAdminAirports, getAllAirlines, getAllAirports, getAllFlightCategories, uploadFlightImages } from '@/api/flightApi'
import Flight from '@/entity/Flight'

const flight = ref({ ...Flight })
const airports = ref([])
const airlines = ref([])
const flightCategories = ref([])
const isEdit = ref(false)
const loading = ref(false)
const error = ref('')

// Thông tin vé
const ticketForm = ref({
  total: 0,
  economy: 0,
  business: 0,
  economyWindow: 0,
  economyAisle: 0,
  businessWindow: 0,
  businessAisle: 0,
  economyPrice: 0,
  businessPrice: 0,
  economyLuggage: 7, // Trọng lượng hành lý phổ thông (kg)
  businessLuggage: 10 // Trọng lượng hành lý thương gia (kg)
})

const seatPresets = [100, 150, 200, 250];
const economyRatio = ref(50); // % phổ thông
const businessRatio = computed(() => 100 - economyRatio.value);
const economyWindowRatio = ref(50); // % cửa sổ phổ thông
const businessWindowRatio = ref(50); // % cửa sổ thương gia

// Khi thay đổi tổng số vé hoặc tỷ lệ, cập nhật số lượng
watch([() => ticketForm.value.total, economyRatio], () => {
  ticketForm.value.economy = Math.round(ticketForm.value.total * economyRatio.value / 100);
  ticketForm.value.business = ticketForm.value.total - ticketForm.value.economy;
});
watch([() => ticketForm.value.economy, () => ticketForm.value.business], () => {
  // Đảm bảo tổng đúng
  ticketForm.value.total = ticketForm.value.economy + ticketForm.value.business;
  if (ticketForm.value.total > 0) {
    economyRatio.value = Math.round(ticketForm.value.economy * 100 / ticketForm.value.total);
  }
});
watch([() => ticketForm.value.economy, economyWindowRatio], () => {
  ticketForm.value.economyWindow = Math.round(ticketForm.value.economy * economyWindowRatio.value / 100);
  ticketForm.value.economyAisle = ticketForm.value.economy - ticketForm.value.economyWindow;
});
watch([() => ticketForm.value.business, businessWindowRatio], () => {
  ticketForm.value.businessWindow = Math.round(ticketForm.value.business * businessWindowRatio.value / 100);
  ticketForm.value.businessAisle = ticketForm.value.business - ticketForm.value.businessWindow;
});
watch([() => ticketForm.value.economyWindow, () => ticketForm.value.economy], () => {
  if (ticketForm.value.economy > 0) {
    economyWindowRatio.value = Math.round(ticketForm.value.economyWindow * 100 / ticketForm.value.economy);
  }
});
watch([() => ticketForm.value.businessWindow, () => ticketForm.value.business], () => {
  if (ticketForm.value.business > 0) {
    businessWindowRatio.value = Math.round(ticketForm.value.businessWindow * 100 / ticketForm.value.business);
  }
});

// Tự động phân bổ ghế cửa sổ/lối đi
function autoDistribute(type) {
  if (type === 'economy') {
    ticketForm.value.economyWindow = Math.floor(ticketForm.value.economy / 2);
    ticketForm.value.economyAisle = ticketForm.value.economy - ticketForm.value.economyWindow;
  } else if (type === 'business') {
    ticketForm.value.businessWindow = Math.floor(ticketForm.value.business / 2);
    ticketForm.value.businessAisle = ticketForm.value.business - ticketForm.value.businessWindow;
  }
}

// Số ghế mỗi hàng
const SEATS_PER_ROW = 6;
const SEAT_LABELS = ['A', 'B', 'C', 'D', 'E', 'F'];

// Sơ đồ ghế đối xứng như máy bay thật
const seatPreviewRows = computed(() => {
  const SEATS_PER_ROW = 6;
  const SEAT_LABELS = ['A', 'B', 'C', 'D', 'E', 'F'];
  // Thương gia
  let businessCount = ticketForm.value.business;
  let businessWindow = ticketForm.value.businessWindow;
  let businessAisle = ticketForm.value.businessAisle + (ticketForm.value.business - ticketForm.value.businessWindow - ticketForm.value.businessAisle); // lối đi + giữa
  let businessRows = [];
  let rowNum = 1;
  while (businessCount > 0) {
    const row = [];
    for (let c = 0; c < SEATS_PER_ROW && businessCount > 0; c++) {
      let pos = '', posColor = '';
      if ((c === 0 || c === 5) && businessWindow > 0) { pos = 'Cửa sổ'; posColor = 'bg-yellow-100 border-yellow-400'; businessWindow--; }
      else if ((c === 1 || c === 2 || c === 3 || c === 4) && businessAisle > 0) { pos = 'Lối đi'; posColor = 'bg-yellow-50 border-yellow-200'; businessAisle--; }
      row.push({
        label: SEAT_LABELS[c] + rowNum,
        type: 'Thương gia',
        icon: 'fa-crown',
        color: 'text-yellow-500',
        pos,
        posColor
      });
      businessCount--;
    }
    businessRows.push(row);
    rowNum++;
  }
  // Phổ thông
  let economyCount = ticketForm.value.economy;
  let economyWindow = ticketForm.value.economyWindow;
  let economyAisle = ticketForm.value.economyAisle + (ticketForm.value.economy - ticketForm.value.economyWindow - ticketForm.value.economyAisle); // lối đi + giữa
  let economyRows = [];
  rowNum = 1;
  while (economyCount > 0) {
    const row = [];
    for (let c = 0; c < SEATS_PER_ROW && economyCount > 0; c++) {
      let pos = '', posColor = '';
      if ((c === 0 || c === 5) && economyWindow > 0) { pos = 'Cửa sổ'; posColor = 'bg-indigo-100 border-indigo-400'; economyWindow--; }
      else if ((c === 1 || c === 2 || c === 3 || c === 4) && economyAisle > 0) { pos = 'Lối đi'; posColor = 'bg-indigo-50 border-indigo-200'; economyAisle--; }
      row.push({
        label: SEAT_LABELS[c] + rowNum,
        type: 'Phổ thông',
        icon: 'fa-chair',
        color: 'text-indigo-500',
        pos,
        posColor
      });
      economyCount--;
    }
    economyRows.push(row);
    rowNum++;
  }
  return { businessRows, economyRows };
});

const previewFlightNumber = computed(() => {
  if (!flight.value.name || !flight.value.airline || !flight.value.departureTime) return '';
  const nameAcronym = flight.value.name
    .normalize('NFD').replace(/\p{Diacritic}/gu, '')
    .split(/\s+/).map(w => w[0]).join('').toUpperCase();
  const airlineAcronym = flight.value.airline ? flight.value.airline.name.split(' ').map(w => w[0]).join('').toUpperCase() : '';
  const d = new Date(flight.value.departureTime);
  const hour = d.getHours().toString().padStart(2, '0');
  const min = d.getMinutes().toString().padStart(2, '0');
  const rand = Math.floor(10000 + Math.random() * 90000);
  return `${nameAcronym}-${airlineAcronym}-${hour}${min}-${rand}`;
})

const images = ref([])
function onImageChange(e) {
  const files = Array.from(e.target.files)
  for (const file of files) {
    const reader = new FileReader()
    reader.onload = (ev) => {
      images.value.push({ file, preview: ev.target.result })
    }
    reader.readAsDataURL(file)
  }
}
function removeImage(idx) {
  images.value.splice(idx, 1)
}

onMounted(async () => {
  try {
    const [airportsRes, airlinesRes, categoriesRes] = await Promise.all([
      getAllAirports(),
      getAllAirlines(),
      getAllFlightCategories()
    ])
    airports.value = airportsRes.data
    airlines.value = airlinesRes.data
    flightCategories.value = categoriesRes.data
  } catch (e) {
    console.error('Không thể tải danh sách sân bay, hãng hàng không hoặc danh mục:', e)
  }
})

async function submitFlight() {
  loading.value = true

  // Sinh mã chuyến bay tự động
  const flightNumber = previewFlightNumber.value;

  // Tạo object JSON để gửi data
  const flightData = {
    flightNumber: flightNumber,
    name: flight.value.name,
    departureTime: flight.value.departureTime,
    arrivalTime: flight.value.arrivalTime,
    airlineId: flight.value.airline ? flight.value.airline.id : null,
    categoryId: flight.value.category ? flight.value.category.id : null,
    departureAirportId: flight.value.departureAirport ? flight.value.departureAirport.id : null,
    arrivalAirportId: flight.value.arrivalAirport ? flight.value.arrivalAirport.id : null,
    ticketInfo: {
      total: ticketForm.value.total,
      economy: ticketForm.value.economy,
      business: ticketForm.value.business,
      economyWindow: ticketForm.value.economyWindow,
      economyAisle: ticketForm.value.economyAisle,
      businessWindow: ticketForm.value.businessWindow,
      businessAisle: ticketForm.value.businessAisle,
      economyPrice: ticketForm.value.economyPrice,
      businessPrice: ticketForm.value.businessPrice,
      economyLuggage: ticketForm.value.economyLuggage,
      businessLuggage: ticketForm.value.businessLuggage
    }
  };

  // Log ra console với format đẹp
  console.log('=== FLIGHT DATA TO CREATE ===')
  console.log('Flight Data:', flightData)
  console.log('Images count:', images.value.length)
  console.log('=============================')

  try {
    // Gọi API tạo flight
    const response = await createAdminFlight(flightData);
    // const response = {
    //   data: {
    //     id: 7
    //   }
    // }
    console.log('Flight created:', response);
    
    // Nếu có ảnh, upload ảnh riêng
    if (images.value.length > 0) {
      const formData = new FormData();
      images.value.forEach((img, index) => {
        formData.append('files', img.file);
      });
      
      const uploadResponse = await uploadFlightImages(response.data.id, formData);
      console.log('Images uploaded:', uploadResponse);
    }
    
    window.$toast(isEdit.value ? 'Cập nhật thành công!' : 'Thêm mới thành công!', 'success');
  } catch (error) {
    console.error('Error creating flight:', error);
    window.$toast('Có lỗi xảy ra khi tạo chuyến bay: ' + (error.response?.data?.message || error.message), 'error');
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
/* Nếu cần override thêm, bạn có thể bổ sung ở đây. 
   Ví dụ custom scrollbar cho phần form. */
</style>
