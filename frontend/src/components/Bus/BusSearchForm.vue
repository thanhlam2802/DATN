<script setup>
import { ref, watch, computed } from 'vue'
import { getOrigins, getDestinations } from '@/data/locationData.js'

// Props
const props = defineProps({
  activeTab: {
    type: String,
    default: 'sleeping-bus'
  }
})

const emit = defineEmits(['search', 'roundtrip-change'])

// Form data
const searchForm = ref({
  from: '',
  to: '',
  departureDate: '',
  returnDate: '',
  roundtrip: false,
  seats: 1
})

// Selected locations for logic
const selectedFrom = ref(null)
const selectedTo = ref(null)

// Dropdown states
const showFromDropdown = ref(false)
const showToDropdown = ref(false)

// Available options based on bus type and selections
const availableOrigins = computed(() => {
  return getOrigins(props.activeTab)
})

const availableDestinations = computed(() => {
  return getDestinations(props.activeTab, selectedFrom.value)
})

// Filtered options based on input
const filteredOrigins = computed(() => {
  if (!searchForm.value.from) return availableOrigins.value
  return availableOrigins.value.filter(location => 
    location.name.toLowerCase().includes(searchForm.value.from.toLowerCase())
  )
})

const filteredDestinations = computed(() => {
  if (!searchForm.value.to) return availableDestinations.value
  return availableDestinations.value.filter(location => 
    location.name.toLowerCase().includes(searchForm.value.to.toLowerCase())
  )
})

// Watch roundtrip change và emit về parent
watch(
  () => searchForm.value.roundtrip,
  (newValue) => {
    emit('roundtrip-change', newValue)
  }
)

// Methods
const swapLocations = () => {
  const temp = searchForm.value.from
  const tempSelected = selectedFrom.value
  
  searchForm.value.from = searchForm.value.to
  searchForm.value.to = temp
  
  selectedFrom.value = selectedTo.value
  selectedTo.value = tempSelected
}

// Handle dropdown selections
const selectOrigin = (location) => {
  searchForm.value.from = location.name
  selectedFrom.value = location
  showFromDropdown.value = false
  
  // Clear destination if it's no longer valid
  if (selectedTo.value && !availableDestinations.value.find(dest => dest.id === selectedTo.value.id)) {
    searchForm.value.to = ''
    selectedTo.value = null
  }
}

const selectDestination = (location) => {
  searchForm.value.to = location.name
  selectedTo.value = location
  showToDropdown.value = false
}

// Handle input focus/blur
const handleFromFocus = () => {
  showFromDropdown.value = true
}

const handleToFocus = () => {
  showToDropdown.value = true
}

const handleFromBlur = () => {
  // Delay to allow click on dropdown item
  setTimeout(() => {
    showFromDropdown.value = false
  }, 200)
}

const handleToBlur = () => {
  // Delay to allow click on dropdown item
  setTimeout(() => {
    showToDropdown.value = false
  }, 200)
}

const handleRoundtripChange = () => {
  emit('roundtrip-change', searchForm.value.roundtrip)
}

const handleSearch = () => {
  // Validate form
  if (!searchForm.value.from || !searchForm.value.to || !searchForm.value.departureDate) {
    alert('Vui lòng điền đầy đủ thông tin tìm kiếm')
    return
  }

  if (searchForm.value.roundtrip && !searchForm.value.returnDate) {
    alert('Vui lòng chọn ngày về')
    return
  }

  // Emit search event
  emit('search', { ...searchForm.value })
}

// Set default date to today
const today = new Date().toISOString().split('T')[0]
searchForm.value.departureDate = today
</script>

<template>
  <div class="bg-white rounded-lg shadow-lg p-6 md:p-8 max-w-6xl mx-auto">
    <!-- Header -->
    <div class="">
      <h2 class="text-xl font-bold text-gray-800 flex items-center justify-between">
        <span class="flex items-center">
          <i class="fas fa-bus text-indigo-600 mr-2"></i>
          Vé Xe Khách & Xe Trung Chuyển
        </span>
        <span v-if="searchForm.roundtrip" class="text-sm bg-green-100 text-green-700 px-2 py-1 rounded-full">
          <i class="fas fa-check-circle mr-1"></i>
          Khứ Hồi
        </span>
      </h2>
    </div>

    <!-- Search Form -->
    <form @submit.prevent="handleSearch" class="space-y-6">
      <!-- From and To Row -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4 relative">
        <!-- From -->
        <div class="space-y-2 relative">
          <label class="block text-sm font-medium text-gray-700">Điểm đi</label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <i class="fas fa-bus text-gray-400"></i>
            </div>
            <input
              v-model="searchForm.from"
              type="text"
              placeholder="Nhập thành phố, bến xe hoặc điểm đón khác"
              class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
              @focus="handleFromFocus"
              @blur="handleFromBlur"
              autocomplete="off"
            />
            
            <!-- From Dropdown -->
            <div v-if="showFromDropdown && filteredOrigins.length > 0" 
                 class="absolute z-50 w-full mt-1 bg-white border border-gray-300 rounded-lg shadow-lg max-h-60 overflow-y-auto">
              <div v-for="location in filteredOrigins" 
                   :key="location.id"
                   @click="selectOrigin(location)"
                   class="px-4 py-3 hover:bg-blue-50 cursor-pointer border-b border-gray-100 last:border-b-0 flex items-center">
                <i class="fas fa-map-marker-alt text-gray-400 mr-3"></i>
                <div>
                  <div class="font-medium text-gray-900">{{ location.name }}</div>
                  <div v-if="location.parentId" class="text-sm text-gray-500">TP. Hồ Chí Minh</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Swap Button -->
        <div class="hidden md:flex absolute left-1/2 top-8 transform -translate-x-1/2 z-10">
          <button
            type="button"
            @click="swapLocations"
            class="bg-indigo-600 hover:bg-indigo-700 text-white p-2 rounded-full shadow-lg transition-colors duration-200"
          >
            <i class="fas fa-exchange-alt"></i>
          </button>
        </div>

        <!-- To -->
        <div class="space-y-2 relative">
          <label class="block text-sm font-medium text-gray-700">Điểm đến</label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <i class="fas fa-bus text-gray-400"></i>
            </div>
            <input
              v-model="searchForm.to"
              type="text"
              placeholder="Nhập thành phố, bến xe hoặc điểm trả khách"
              class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
              @focus="handleToFocus"
              @blur="handleToBlur"
              autocomplete="off"
            />
            
            <!-- To Dropdown -->
            <div v-if="showToDropdown && filteredDestinations.length > 0" 
                 class="absolute z-50 w-full mt-1 bg-white border border-gray-300 rounded-lg shadow-lg max-h-60 overflow-y-auto">
              <div v-for="location in filteredDestinations" 
                   :key="location.id"
                   @click="selectDestination(location)"
                   class="px-4 py-3 hover:bg-blue-50 cursor-pointer border-b border-gray-100 last:border-b-0 flex items-center">
                <i class="fas fa-map-marker-alt text-gray-400 mr-3"></i>
                <div>
                  <div class="font-medium text-gray-900">{{ location.name }}</div>
                  <div v-if="location.parentId" class="text-sm text-gray-500">TP. Hồ Chí Minh</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Date, Roundtrip, Seats and Search Row -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4 items-end">
        <!-- Departure Date -->
        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700">Ngày đi</label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <i class="fas fa-calendar text-gray-400"></i>
            </div>
            <input
              v-model="searchForm.departureDate"
              type="date"
              class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
            />
          </div>
        </div>

        <!-- Roundtrip Checkbox -->
        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700">&nbsp;</label>
          <div class="flex items-center h-12">
            <label class="flex items-center cursor-pointer">
              <input
                v-model="searchForm.roundtrip"
                @change="handleRoundtripChange"
                type="checkbox"
                class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
              />
              <span class="ml-2 text-sm text-gray-700">Khứ hồi?</span>
            </label>
          </div>
        </div>

        <!-- Number of Seats -->
        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700">Số ghế</label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <i class="fas fa-user text-gray-400"></i>
            </div>
            <input
              v-model="searchForm.seats"
              type="number"
              min="1"
              max="10"
              class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
            />
          </div>
        </div>

        <!-- Search Button -->
        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700 md:hidden ">Tìm kiếm</label>
          <button
            type="submit"
            class="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-3 px-6 rounded-lg transition-colors duration-200 flex items-center justify-center"
          >
            <i class="fas fa-search mr-2"></i>
            Tìm kiếm
          </button>
        </div>
      </div>

      <!-- Return Date Row (conditionally rendered) -->
      <Transition
        enter-active-class="transition-all duration-300 ease-out"
        enter-from-class="opacity-0 transform scale-95 -translate-y-2"
        enter-to-class="opacity-100 transform scale-100 translate-y-0"
        leave-active-class="transition-all duration-200 ease-in"
        leave-from-class="opacity-100 transform scale-100 translate-y-0"
        leave-to-class="opacity-0 transform scale-95 -translate-y-2"
      >
        <div v-if="searchForm.roundtrip" class="mt-6 p-4 rounded-lg shadow-sm">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div class="space-y-2">
            <label class="block text-sm font-medium text-gray-700">
              <i class="fas fa-calendar-alt text-blue-500 mr-1"></i>
              Ngày về
            </label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <i class="fas fa-calendar text-gray-400"></i>
              </div>
              <input
                v-model="searchForm.returnDate"
                type="date"
                class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none bg-white"
                placeholder="Chọn ngày về"
              />
            </div>
          </div>
          <div class="md:col-span-3 flex items-end">
            <div class="text-sm text-blue-600 bg-blue-100 px-3 py-2 rounded-lg">
              <i class="fas fa-info-circle mr-1"></i>
              Vé khứ hồi - Chọn ngày về
            </div>
          </div>
                  </div>
        </div>
      </Transition>
    </form>
  </div>
</template>

<style scoped>
/* Custom scrollbar for date inputs */
input[type="date"]::-webkit-calendar-picker-indicator {
  cursor: pointer;
}

/* Remove number input arrows */
input[type="number"]::-webkit-outer-spin-button,
input[type="number"]::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

input[type="number"] {
  -moz-appearance: textfield;
}
</style> 