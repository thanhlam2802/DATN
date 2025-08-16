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
              <label
                class="flex flex-col items-center justify-center w-full max-w-xs px-4 py-6 bg-white border-2 border-dashed border-gray-300 rounded-lg cursor-pointer hover:bg-gray-100 transition">
                
                <span class="text-sm text-gray-600">Chọn ảnh (có thể chọn nhiều)</span>
                <input type="file" multiple accept="image/*" @change="onImageChange" class="hidden" :disabled="loadingImage" />
              </label>
              <div v-if="loadingImage" class="text-indigo-600 text-sm flex items-center gap-2 mb-2"><span class="animate-spin w-4 h-4 border-2 border-indigo-500 border-t-white rounded-full"></span> Đang tải ảnh...</div>

              <div class="flex flex-wrap gap-2">
                <div v-for="(img, idx) in images" :key="idx" class="relative w-24 h-24">
                  <img :src="img.preview" class="w-full h-full object-cover rounded border" />
                  <button type="button" @click="removeImage(idx)"
                    class="absolute top-1 right-1 bg-white/80 rounded-full p-1 text-red-500 hover:bg-red-100"><i
                      class="fa fa-times"></i></button>
                </div>
              </div>
            </div>
            <!-- Flight Number (preview only) -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Số hiệu chuyến bay (tự sinh)</label>
              <input :value="previewFlightNumber" type="text" disabled
                class="w-full border border-gray-300 rounded-lg px-4 py-2 bg-gray-100 text-gray-500"
                placeholder="Sẽ tự sinh khi nhập đủ thông tin" />
            </div>

            <!-- Tỉnh/Thành phố đi và đến -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Tỉnh/Thành phố đi</label>
              <select v-model="departureCity" required
                :class="`w-full border rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${validationErrors.departureCity ? 'border-red-500' : 'border-gray-300'}`">
                <option value="">Chọn tỉnh/thành phố đi</option>
                <option v-for="city in allCities" :key="city" :value="city">{{ city }}</option>
              </select>
              <div v-if="validationErrors.departureCity" class="text-red-500 text-sm mt-1">
                {{ validationErrors.departureCity[0] }}
              </div>
              <div v-if="departureCity && arrivalCity && departureCity === arrivalCity" class="text-red-500 text-sm mt-1">
                Tỉnh/thành phố đi không được trùng với tỉnh/thành phố đến
              </div>
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Tỉnh/Thành phố đến</label>
              <select v-model="arrivalCity" required
                :class="`w-full border rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${validationErrors.arrivalCity ? 'border-red-500' : 'border-gray-300'}`">
                <option value="">Chọn tỉnh/thành phố đến</option>
                <option v-for="city in allCities" :key="city" :value="city">{{ city }}</option>
              </select>
              <div v-if="validationErrors.arrivalCity" class="text-red-500 text-sm mt-1">
                {{ validationErrors.arrivalCity[0] }}
              </div>
              <div v-if="departureCity && arrivalCity && departureCity === arrivalCity" class="text-red-500 text-sm mt-1">
                Tỉnh/thành phố đến không được trùng với tỉnh/thành phố đi
              </div>
            </div>
            
            

            <!-- Hãng hàng không -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Hãng hàng không</label>
              <select v-model="flight.airline" required
                :class="`w-full border rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${validationErrors.airline ? 'border-red-500' : 'border-gray-300'}`">
                <option value="">Chọn hãng</option>
                <option v-for="airline in airlines" :key="airline.id" :value="airline">
                  {{ airline.name }}
                </option>
              </select>
              <div v-if="validationErrors.airline" class="text-red-500 text-sm mt-1">
                {{ validationErrors.airline[0] }}
              </div>
            </div>

            <!-- Departure Airport -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Sân bay đi</label>
              <select v-model="flight.departureAirport" required
                :class="`w-full border rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${validationErrors.departureAirport ? 'border-red-500' : 'border-gray-300'}`">
                <option value="">Chọn sân bay đi</option>
                <option v-for="airport in airports" :key="airport.id" :value="airport">
                  {{ airport.name }}
                </option>
              </select>
              <div v-if="validationErrors.departureAirport" class="text-red-500 text-sm mt-1">
                {{ validationErrors.departureAirport[0] }}
              </div>
              <div v-if="flight.departureAirport && flight.arrivalAirport && flight.departureAirport.id === flight.arrivalAirport.id" class="text-red-500 text-sm mt-1">
                Sân bay đi không được trùng với sân bay đến
              </div>
            </div>

            <!-- Arrival Airport -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Sân bay đến</label>
              <select v-model="flight.arrivalAirport" required
                :class="`w-full border rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${validationErrors.arrivalAirport ? 'border-red-500' : 'border-gray-300'}`">
                <option value="">Chọn sân bay đến</option>
                <option v-for="airport in airports" :key="airport.id" :value="airport">
                  {{ airport.name }}
                </option>
              </select>
              <div v-if="validationErrors.arrivalAirport" class="text-red-500 text-sm mt-1">
                {{ validationErrors.arrivalAirport[0] }}
              </div>
              <div v-if="flight.departureAirport && flight.arrivalAirport && flight.departureAirport.id === flight.arrivalAirport.id" class="text-red-500 text-sm mt-1">
                Sân bay đến không được trùng với sân bay đi
              </div>
            </div>

            <!-- Departure Time -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Thời gian khởi hành</label>
              <input v-model="flight.departureTime" type="datetime-local" required
                :class="`w-full border rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${validationErrors.departureTime ? 'border-red-500' : 'border-gray-300'}`" />
              <div v-if="validationErrors.departureTime" class="text-red-500 text-sm mt-1">
                {{ validationErrors.departureTime[0] }}
              </div>
            </div>

            <!-- Arrival Time -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Thời gian đến</label>
              <input v-model="flight.arrivalTime" type="datetime-local" required
                :class="`w-full border rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${validationErrors.arrivalTime ? 'border-red-500' : 'border-gray-300'}`" />
              <div v-if="validationErrors.arrivalTime" class="text-red-500 text-sm mt-1">
                {{ validationErrors.arrivalTime[0] }}
              </div>
            </div>

            <!-- Category -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Danh mục</label>
              <select v-model="flight.category"
                :class="`w-full border rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${validationErrors.category ? 'border-red-500' : 'border-gray-300'}`">
                <option value="">Chọn danh mục</option>
                <option v-for="category in flightCategories" :key="category.id" :value="category">
                  {{ category.name }}
                </option>
              </select>
              <div v-if="validationErrors.category" class="text-red-500 text-sm mt-1">
                {{ validationErrors.category[0] }}
              </div>
            </div>

            <!-- Tổng số vé -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Tổng số vé</label>
              <div class="flex gap-2 mb-2">
                <button v-for="preset in seatPresets" :key="preset" type="button" @click="ticketForm.total = preset"
                  class="px-3 py-1 rounded bg-gray-100 hover:bg-blue-100 text-sm border border-gray-200">{{ preset
                  }}</button>
              </div>
              <input v-model.number="ticketForm.total" type="number" min="1" required
                :class="`w-full border rounded-lg px-4 py-2 ${validationErrors.total ? 'border-red-500' : 'border-gray-300'}`" placeholder="Tổng số vé" />
              <div v-if="validationErrors.total" class="text-red-500 text-sm mt-1">
                {{ validationErrors.total[0] }}
              </div>
            </div>

            <!-- Tỷ lệ phổ thông/thương gia -->
            <div class="col-span-2">
              <label class="block text-sm font-medium text-gray-700 mb-2">Tỷ lệ phổ thông / thương gia</label>
              <div class="flex items-center gap-4">
                <span class="text-indigo-700 font-bold">Phổ thông:
                  <input type="number" min="0" max="100" v-model.number="economyRatio"
                    class="w-16 border border-gray-300 rounded px-1 py-0.5 text-center" />%
                </span>
                <input type="range" min="0" max="100" v-model.number="economyRatio" class="w-1/2" />
                <span class="text-yellow-700 font-bold">Thương gia:
                  <input type="number" min="0" max="100" :value="businessRatio" disabled
                    class="w-16 border border-gray-200 rounded px-1 py-0.5 text-center bg-gray-100" />%
                </span>
              </div>
              <div class="flex gap-8 mt-2">
                <span class="text-xs text-gray-500">Số vé phổ thông:
                  <input type="number" min="0" :max="ticketForm.total" v-model.number="ticketForm.economy"
                    class="w-16 border border-gray-300 rounded px-1 py-0.5 text-center" />
                </span>
                <span class="text-xs text-gray-500">Số vé thương gia:
                  <input type="number" min="0" :max="ticketForm.total" v-model.number="ticketForm.business"
                    class="w-16 border border-gray-300 rounded px-1 py-0.5 text-center" />
                </span>
              </div>
            </div>

            <!-- Tỷ lệ cửa sổ/lối đi phổ thông -->
            <div class="col-span-2">
              <label class="block text-sm font-medium text-gray-700 mb-2">Tỷ lệ cửa sổ / lối đi (Phổ thông)</label>
              <div class="flex items-center gap-4">
                <span class="text-blue-700 font-bold">Cửa sổ:
                  <input type="number" min="0" max="100" v-model.number="economyWindowRatio"
                    class="w-16 border border-gray-300 rounded px-1 py-0.5 text-center" />%
                </span>
                <input type="range" min="0" max="100" v-model.number="economyWindowRatio" class="w-1/2" />
                <span class="text-green-700 font-bold">Lối đi:
                  <input type="number" min="0" max="100" :value="100 - economyWindowRatio" disabled
                    class="w-16 border border-gray-200 rounded px-1 py-0.5 text-center bg-gray-100" />%
                </span>
              </div>
              <div class="flex gap-8 mt-2">
                <span class="text-xs text-gray-500">Số vé cửa sổ:
                  <input type="number" min="0" :max="ticketForm.economy" v-model.number="ticketForm.economyWindow"
                    class="w-16 border border-gray-300 rounded px-1 py-0.5 text-center" />
                </span>
                <span class="text-xs text-gray-500">Số vé lối đi:
                  <input type="number" min="0" :max="ticketForm.economy" v-model.number="ticketForm.economyAisle"
                    class="w-16 border border-gray-300 rounded px-1 py-0.5 text-center" />
                </span>
              </div>
            </div>

            <!-- Tỷ lệ cửa sổ/lối đi thương gia -->
            <div class="col-span-2">
              <label class="block text-sm font-medium text-gray-700 mb-2">Tỷ lệ cửa sổ / lối đi (Thương gia)</label>
              <div class="flex items-center gap-4">
                <span class="text-blue-700 font-bold">Cửa sổ:
                  <input type="number" min="0" max="100" v-model.number="businessWindowRatio"
                    class="w-16 border border-gray-300 rounded px-1 py-0.5 text-center" />%
                </span>
                <input type="range" min="0" max="100" v-model.number="businessWindowRatio" class="w-1/2" />
                <span class="text-green-700 font-bold">Lối đi:
                  <input type="number" min="0" max="100" :value="100 - businessWindowRatio" disabled
                    class="w-16 border border-gray-200 rounded px-1 py-0.5 text-center bg-gray-100" />%
                </span>
              </div>
              <div class="flex gap-8 mt-2">
                <span class="text-xs text-gray-500">Số vé cửa sổ:
                  <input type="number" min="0" :max="ticketForm.business" v-model.number="ticketForm.businessWindow"
                    class="w-16 border border-gray-300 rounded px-1 py-0.5 text-center" />
                </span>
                <span class="text-xs text-gray-500">Số vé lối đi:
                  <input type="number" min="0" :max="ticketForm.business" v-model.number="ticketForm.businessAisle"
                    class="w-16 border border-gray-300 rounded px-1 py-0.5 text-center" />
                </span>
              </div>
            </div>

            <!-- Giá vé phổ thông -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Giá vé phổ thông (VND)</label>
              <input v-model.number="ticketForm.economyPrice" type="number" min="0" required
                :class="`w-full border rounded-lg px-4 py-2 ${validationErrors.economyPrice ? 'border-red-500' : 'border-gray-300'}`" placeholder="Giá vé phổ thông" />
              <div v-if="validationErrors.economyPrice" class="text-red-500 text-sm mt-1">
                {{ validationErrors.economyPrice[0] }}
              </div>
            </div>
            <!-- Giá vé thương gia -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Giá vé thương gia (VND)</label>
              <input v-model.number="ticketForm.businessPrice" type="number" min="0" required
                :class="`w-full border rounded-lg px-4 py-2 ${validationErrors.businessPrice ? 'border-red-500' : 'border-gray-300'}`" placeholder="Giá vé thương gia" />
              <div v-if="validationErrors.businessPrice" class="text-red-500 text-sm mt-1">
                {{ validationErrors.businessPrice[0] }}
              </div>
            </div>

            <!-- Trọng lượng hành lý phổ thông -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Hành lý phổ thông (kg)</label>
              <input v-model.number="ticketForm.economyLuggage" type="number" min="1" max="50" required
                :class="`w-full border rounded-lg px-4 py-2 ${validationErrors.economyLuggage ? 'border-red-500' : 'border-gray-300'}`"
                placeholder="Trọng lượng hành lý phổ thông" />
              <div v-if="validationErrors.economyLuggage" class="text-red-500 text-sm mt-1">
                {{ validationErrors.economyLuggage[0] }}
              </div>
            </div>

            <!-- Trọng lượng hành lý thương gia -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Hành lý thương gia (kg)</label>
              <input v-model.number="ticketForm.businessLuggage" type="number" min="1" max="50" required
                :class="`w-full border rounded-lg px-4 py-2 ${validationErrors.businessLuggage ? 'border-red-500' : 'border-gray-300'}`"
                placeholder="Trọng lượng hành lý thương gia" />
              <div v-if="validationErrors.businessLuggage" class="text-red-500 text-sm mt-1">
                {{ validationErrors.businessLuggage[0] }}
              </div>
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
                  = Ghế cửa sổ
                </div>
                <div class="flex items-center gap-1">
                  <span class="inline-block w-4 h-4 rounded bg-yellow-50 border border-yellow-200"></span>
                  <span class="inline-block w-4 h-4 rounded bg-indigo-50 border border-indigo-200"></span>
                  = Ghế lối đi/giữa
                </div>
              </div>
              <div class="w-full flex gap-8 justify-center">
                <!-- Thương gia -->
                <div v-if="seatPreviewRows.businessRows.length" class="flex flex-col gap-4">
                  <div class="text-center text-yellow-700 font-bold mb-1">Thương gia</div>
                  <div v-for="(row, ridx) in seatPreviewRows.businessRows" :key="'b' + ridx"
                    class="flex items-center gap-1 justify-center mb-2">
                    <template v-for="(seat, cidx) in row" :key="cidx">
                      <div v-if="cidx === 3" class="w-4"></div>
                      <div
                        :class="['w-12 h-12 flex flex-col items-center justify-center border-2 rounded-lg', seat.posColor, 'mx-0.5']">
                        <i :class="['fa', seat.icon, seat.color]"></i>
                        <span class="font-bold text-xs">{{ seat.label }}</span>
                      </div>
                    </template>
                  </div>
                </div>
                <!-- Phổ thông -->
                <div v-if="seatPreviewRows.economyRows.length" class="flex flex-col gap-4">
                  <div class="text-center text-indigo-700 font-bold mb-1">Phổ thông</div>
                  <div v-for="(row, ridx) in seatPreviewRows.economyRows" :key="'e' + ridx"
                    class="flex items-center gap-1 justify-center mb-2">
                    <template v-for="(seat, cidx) in row" :key="cidx">
                      <div v-if="cidx === 3" class="w-4"></div>
                      <div
                        :class="['w-12 h-12 flex flex-col items-center justify-center border-2 rounded-lg', seat.posColor, 'mx-0.5']">
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
            <button type="button" @click="$router.go(-1)"
              class="px-6 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50">
              Hủy
            </button>
            <button type="submit" :disabled="loading"
              class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50">
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

// Danh sách tỉnh thành phố
const allCities = [
  'Hà Nội',
  'TP Hồ Chí Minh',
  'Hải Phòng',
  'Đà Nẵng',
  'Cần Thơ',
  'Thừa Thiên Huế',
  'Cao Bằng',
  'Lạng Sơn',
  'Quảng Ninh',
  'Lai Châu',
  'Điện Biên',
  'Sơn La',
  'Tuyên Quang',
  'Lào Cai',
  'Thái Nguyên',
  'Phú Thọ',
  'Bắc Ninh',
  'Nam Định',
  'Hà Nam',
  'Thanh Hóa',
  'Nghệ An',
  'Hà Tĩnh',
  'Quảng Bình',
  'Quảng Nam',
  'Kon Tum',
  'Đắk Lắk',
  'Lâm Đồng',
  'Khánh Hòa',
  'Bình Định',
  'Đồng Nai',
  'Long An',
  'Tiền Giang',
  'Vĩnh Long',
  'Cà Mau'
]

// Biến lưu tỉnh/thành phố đi và đến
const departureCity = ref('')
const arrivalCity = ref('')

// Validation errors
const validationErrors = ref({})

// Tự động tạo tên chuyến bay từ tỉnh/thành phố đi và đến
const flightName = computed(() => {
  if (departureCity.value && arrivalCity.value) {
    return `${departureCity.value} - ${arrivalCity.value}`
  }
  return ''
})

// Cập nhật flight.name khi flightName thay đổi
watch(flightName, (newName) => {
  if (newName) {
    flight.value.name = newName
  }
})

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
      else { pos = 'Lối đi'; posColor = 'bg-yellow-50 border-yellow-200'; businessAisle--; }
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
      else { pos = 'Lối đi'; posColor = 'bg-indigo-50 border-indigo-200'; economyAisle--; }
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
  if (!flightName.value || !flight.value.airline || !flight.value.departureTime) return '';
  
  // Tạo viết tắt từ tên chuyến bay (từ tỉnh/thành phố đi và đến)
  const nameAcronym = flightName.value
    .normalize('NFD').replace(/\p{Diacritic}/gu, '') // Loại bỏ dấu
    .split(/\s+/).map(w => w[0]).join('').toUpperCase(); // Lấy chữ cái đầu của mỗi từ
  
  // Tạo viết tắt từ tên hãng hàng không
  const airlineAcronym = flight.value.airline ? flight.value.airline.name.split(' ').map(w => w[0]).join('').toUpperCase() : '';
  
  // Lấy giờ và phút từ thời gian khởi hành
  const d = new Date(flight.value.departureTime);
  const hour = d.getHours().toString().padStart(2, '0');
  const min = d.getMinutes().toString().padStart(2, '0');
  
  // Tạo số ngẫu nhiên
  const rand = Math.floor(10000 + Math.random() * 90000);
  
  // Kết hợp các thành phần để tạo mã chuyến bay
  return `${nameAcronym}-${airlineAcronym}-${hour}${min}-${rand}`;
})

const images = ref([])
const loadingImage = ref(false)
function onImageChange(e) {
  const files = Array.from(e.target.files)
  if (files.length === 0) return
  loadingImage.value = true
  try {
    for (const file of files) {
      const reader = new FileReader()
      reader.onload = (ev) => {
        images.value.push({ file, preview: ev.target.result })
      }
      reader.readAsDataURL(file)
    }
    window.$toast('Thêm ảnh thành công!', 'success')
  } catch (error) {
    window.$toast('Lỗi khi thêm ảnh!', 'error')
  } finally {
    loadingImage.value = false
  }
}
function removeImage(idx) {
  images.value.splice(idx, 1)
  window.$toast('Xóa ảnh thành công!', 'success')
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

// Hàm validate trực tiếp trong component
function validateFlight() {
  const errors = {}
  let isValid = true

  // Validate tỉnh/thành phố đi và đến
  if (!departureCity.value) {
    errors.departureCity = ['Tỉnh/thành phố đi là bắt buộc']
    isValid = false
  }
  
  if (!arrivalCity.value) {
    errors.arrivalCity = ['Tỉnh/thành phố đến là bắt buộc']
    isValid = false
  } else if (departureCity.value && arrivalCity.value === departureCity.value) {
    errors.arrivalCity = ['Tỉnh/thành phố đến phải khác tỉnh/thành phố đi']
    isValid = false
  }
  
  // Validate tên chuyến bay
  if (!flight.value.name || flight.value.name.trim() === '') {
    errors.name = ['Tên chuyến bay là bắt buộc']
    isValid = false
  } else if (flight.value.name.length < 3) {
    errors.name = ['Tên chuyến bay phải có ít nhất 3 ký tự']
    isValid = false
  }

  // Validate hãng hàng không
  if (!flight.value.airline) {
    errors.airline = ['Hãng hàng không là bắt buộc']
    isValid = false
  }

  // Validate sân bay đi
  if (!flight.value.departureAirport) {
    errors.departureAirport = ['Sân bay đi là bắt buộc']
    isValid = false
  }

  // Validate sân bay đến
  if (!flight.value.arrivalAirport) {
    errors.arrivalAirport = ['Sân bay đến là bắt buộc']
    isValid = false
  } else if (flight.value.departureAirport && flight.value.arrivalAirport.id === flight.value.departureAirport.id) {
    errors.arrivalAirport = ['Sân bay đến phải khác sân bay đi']
    isValid = false
  }

  // Validate thời gian khởi hành
  if (!flight.value.departureTime) {
    errors.departureTime = ['Thời gian khởi hành là bắt buộc']
    isValid = false
  }

  // Validate thời gian đến
  if (!flight.value.arrivalTime) {
    errors.arrivalTime = ['Thời gian đến là bắt buộc']
    isValid = false
  } else if (flight.value.departureTime && new Date(flight.value.arrivalTime) <= new Date(flight.value.departureTime)) {
    errors.arrivalTime = ['Thời gian đến phải sau thời gian đi']
    isValid = false
  }
  
  // Validate danh mục
  if (!flight.value.category) {
    errors.category = ['Danh mục là bắt buộc']
    isValid = false
  }

  // Validate thông tin vé
  if (ticketForm.value.total <= 0) {
    errors.total = ['Tổng số vé phải lớn hơn 0']
    isValid = false
  }

  if (ticketForm.value.economyPrice < 0) {
    errors.economyPrice = ['Giá vé phổ thông không được âm']
    isValid = false
  }

  if (ticketForm.value.businessPrice < 0) {
    errors.businessPrice = ['Giá vé thương gia không được âm']
    isValid = false
  }
  
  // Validate trọng lượng hành lý
  if (ticketForm.value.economyLuggage < 1 || ticketForm.value.economyLuggage > 50) {
    errors.economyLuggage = ['Trọng lượng hành lý phổ thông phải từ 1 đến 50 kg']
    isValid = false
  }
  
  if (ticketForm.value.businessLuggage < 1 || ticketForm.value.businessLuggage > 50) {
    errors.businessLuggage = ['Trọng lượng hành lý thương gia phải từ 1 đến 50 kg']
    isValid = false
  }

  return { isValid, errors }
}

async function submitFlight() {
  // Validate form before submit
  const { isValid, errors } = validateFlight()
  validationErrors.value = errors
  
  if (!isValid) {
    window.$toast('Vui lòng kiểm tra lại thông tin chuyến bay!', 'error')
    return
  }
  
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
