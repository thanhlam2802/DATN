<script setup>
import { ref, watch, computed, nextTick, onMounted, onUnmounted } from 'vue'
import { LocationAPI } from '@/api/busAPI_Client'

// DEV MODE: No auth features

// Props
const props = defineProps({
  activeTab: {
    type: String,
    default: 'sleeping-bus'
  }
})

const emit = defineEmits(['search', 'roundtrip-change'])

// Refs
const returnDateSection = ref(null)

// Loading states
const loadingOrigins = ref(false)
const loadingDestinations = ref(false)

// Component lifecycle tracking
const isMounted = ref(false)

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

// Real data from API
const originsList = ref([])
const destinationsList = ref([])

// Dropdown states
const showFromDropdown = ref(false)
const showToDropdown = ref(false)

// Available options based on real API data
const availableOrigins = computed(() => {
  return originsList.value
})

const availableDestinations = computed(() => {
  return destinationsList.value
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

// Load origins on component mount
onMounted(async () => {
  isMounted.value = true
  await loadOrigins()
})

// Cleanup on unmount
onUnmounted(() => {
  isMounted.value = false
})

// API calls
const loadOrigins = async () => {
  if (!isMounted.value) return
  
  try {
    loadingOrigins.value = true
    const origins = await LocationAPI.getAvailableOrigins()
    
    // Check if still mounted before updating state
    if (isMounted.value) {
      originsList.value = origins
    }
  } catch (error) {
    // Use fallback data on error
    if (isMounted.value) {
      originsList.value = LocationAPI.getFallbackOrigins()
    }
  } finally {
    if (isMounted.value) {
      loadingOrigins.value = false
    }
  }
}

const loadDestinations = async (origin) => {
  if (!isMounted.value || !origin) {
    if (isMounted.value) {
      destinationsList.value = []
    }
    return
  }
  
  try {
    loadingDestinations.value = true
    const destinations = await LocationAPI.getAvailableDestinations(origin)
    
    // Check if still mounted before updating state
    if (isMounted.value) {
      destinationsList.value = destinations
    }
  } catch (error) {
    // Use fallback data on error
    if (isMounted.value) {
      destinationsList.value = LocationAPI.getFallbackDestinations(origin)
    }
  } finally {
    if (isMounted.value) {
      loadingDestinations.value = false
    }
  }
}

// Watch origin selection to load destinations
watch(selectedFrom, async (newOrigin) => {
  if (newOrigin) {
    await loadDestinations(newOrigin.name)
    // Clear destination if it's no longer valid
    if (selectedTo.value && !destinationsList.value.find(dest => dest.name === selectedTo.value.name)) {
      searchForm.value.to = ''
      selectedTo.value = null
    }
  } else {
    destinationsList.value = []
    searchForm.value.to = ''
    selectedTo.value = null
  }
})

// Watch roundtrip change và emit về parent
watch(
  () => searchForm.value.roundtrip,
  async (newValue) => {
    emit('roundtrip-change', newValue)
    
    // Auto scroll to return date section when enabled
    if (newValue) {
      await nextTick()
      setTimeout(() => {
        // Add null check to prevent DOM errors
        if (returnDateSection.value && returnDateSection.value.scrollIntoView) {
          returnDateSection.value.scrollIntoView({ 
            behavior: 'smooth', 
            block: 'nearest' 
          })
        }
      }, 300) // Wait for transition to start
    }
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
const selectOrigin = async (location) => {
  searchForm.value.from = location.name
  selectedFrom.value = location
  showFromDropdown.value = false
  
  // Load destinations for selected origin
  await loadDestinations(location.name)
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
  // Load destinations if origin is selected but destinations not loaded
  if (selectedFrom.value && destinationsList.value.length === 0 && !loadingDestinations.value) {
    loadDestinations(selectedFrom.value.name)
  }
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

  // Prepare search criteria for BusSearchAPI
  const searchCriteria = {
    from: searchForm.value.from,
    to: searchForm.value.to,
    departureDate: searchForm.value.departureDate,
    returnDate: searchForm.value.returnDate,
    seats: searchForm.value.seats,
    roundtrip: searchForm.value.roundtrip
  }

  // Emit search event with real criteria
  emit('search', searchCriteria)
}

// Set default date to today
const today = new Date().toISOString().split('T')[0]
searchForm.value.departureDate = today
</script>

<template>
  <div class="bg-white rounded-lg shadow-lg p-3 sm:p-6 md:p-8 max-w-6xl mx-auto">
    <!-- Header -->
    <div class="mb-4 sm:mb-6">
      <h2 class="text-base sm:text-lg md:text-xl font-bold text-gray-800 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2">
        <span class="flex items-center">
          <i class="fas fa-bus text-indigo-600 mr-2 text-sm sm:text-base"></i>
          <span class="text-sm sm:text-base md:text-xl">Vé Xe Khách & Xe Trung Chuyển</span>
        </span>
        <span v-if="searchForm.roundtrip" class="text-xs sm:text-sm bg-green-100 text-green-700 px-2 py-1 rounded-full w-fit">
          <i class="fas fa-check-circle mr-1"></i>
          Khứ Hồi
        </span>
      </h2>
    </div>

    <!-- Search Form -->
    <form @submit.prevent="handleSearch" class="space-y-3 sm:space-y-4 md:space-y-6">
      <!-- From and To Row -->
      <div class="space-y-3 md:space-y-0 md:grid md:grid-cols-2 md:gap-4 md:relative">
        <!-- From -->
        <div class="space-y-2 relative">
          <label class="block text-xs sm:text-sm font-medium text-gray-700">
            Điểm đi
            <span v-if="loadingOrigins" class="text-blue-500 ml-1">
              <i class="fas fa-spinner fa-spin text-xs"></i>
            </span>
          </label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <i class="fas fa-bus text-gray-400 text-sm"></i>
            </div>
            <input
              v-model="searchForm.from"
              type="text"
              placeholder="Nhập thành phố, bến xe..."
              :disabled="loadingOrigins"
              class="w-full pl-9 sm:pl-10 pr-4 py-2.5 sm:py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none text-sm disabled:bg-gray-100 disabled:cursor-not-allowed"
              @focus="handleFromFocus"
              @blur="handleFromBlur"
              autocomplete="off"
            />
            
            <!-- From Dropdown -->
            <div v-if="showFromDropdown && filteredOrigins.length > 0" 
                 class="absolute z-50 w-full mt-1 bg-white border border-gray-300 rounded-lg shadow-lg max-h-48 sm:max-h-60 overflow-y-auto">
              <div v-for="location in filteredOrigins" 
                   :key="location.id"
                   @click="selectOrigin(location)"
                   class="px-3 sm:px-4 py-2.5 sm:py-3 hover:bg-indigo-50 cursor-pointer border-b border-gray-100 last:border-b-0 flex items-center justify-between">
                <div class="flex items-center">
                  <i class="fas fa-map-marker-alt text-gray-400 mr-2 sm:mr-3 text-sm"></i>
                  <div>
                    <div class="font-medium text-gray-900 text-sm">{{ location.name }}</div>
                  </div>
                </div>
                <div v-if="location.count" class="text-xs text-gray-500 bg-gray-100 px-2 py-1 rounded-full">
                  {{ location.count }} chuyến
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Mobile Swap Button -->
        <div class="md:hidden flex justify-center py-1">
          <button
            type="button"
            @click="swapLocations"
            :disabled="!selectedFrom || !selectedTo"
            class="bg-indigo-600 hover:bg-indigo-700 disabled:bg-gray-400 text-white p-2 rounded-full shadow-lg transition-colors duration-200"
          >
            <i class="fas fa-exchange-alt rotate-90 text-sm"></i>
          </button>
        </div>

        <!-- Desktop Swap Button -->
        <div class="hidden md:flex absolute left-1/2 top-8 transform -translate-x-1/2 z-10">
          <button
            type="button"
            @click="swapLocations"
            :disabled="!selectedFrom || !selectedTo"
            class="bg-indigo-600 hover:bg-indigo-700 disabled:bg-gray-400 text-white p-2 rounded-full shadow-lg transition-colors duration-200"
          >
            <i class="fas fa-exchange-alt"></i>
          </button>
        </div>

        <!-- To -->
        <div class="space-y-2 relative">
          <label class="block text-xs sm:text-sm font-medium text-gray-700">
            Điểm đến
            <span v-if="loadingDestinations" class="text-blue-500 ml-1">
              <i class="fas fa-spinner fa-spin text-xs"></i>
            </span>
          </label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <i class="fas fa-bus text-gray-400 text-sm"></i>
            </div>
            <input
              v-model="searchForm.to"
              type="text"
              placeholder="Chọn điểm đi trước..."
              :disabled="!selectedFrom || loadingDestinations"
              class="w-full pl-9 sm:pl-10 pr-4 py-2.5 sm:py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none text-sm disabled:bg-gray-100 disabled:cursor-not-allowed"
              @focus="handleToFocus"
              @blur="handleToBlur"
              autocomplete="off"
            />
            
            <!-- To Dropdown -->
            <div v-if="showToDropdown && filteredDestinations.length > 0" 
                 class="absolute z-50 w-full mt-1 bg-white border border-gray-300 rounded-lg shadow-lg max-h-48 sm:max-h-60 overflow-y-auto">
              <div v-for="location in filteredDestinations" 
                   :key="location.id"
                   @click="selectDestination(location)"
                   class="px-3 sm:px-4 py-2.5 sm:py-3 hover:bg-indigo-50 cursor-pointer border-b border-gray-100 last:border-b-0 flex items-center justify-between">
                <div class="flex items-center">
                  <i class="fas fa-map-marker-alt text-gray-400 mr-2 sm:mr-3 text-sm"></i>
                  <div>
                    <div class="font-medium text-gray-900 text-sm">{{ location.name }}</div>
                  </div>
                </div>
                <div v-if="location.count" class="text-xs text-gray-500 bg-gray-100 px-2 py-1 rounded-full">
                  {{ location.count }} chuyến
                </div>
              </div>
            </div>

            <!-- No destinations available message -->
            <div v-if="selectedFrom && showToDropdown && !loadingDestinations && filteredDestinations.length === 0"
                 class="absolute z-50 w-full mt-1 bg-white border border-gray-300 rounded-lg shadow-lg p-4 text-center text-gray-500 text-sm">
              <i class="fas fa-info-circle mr-2"></i>
              Không có điểm đến từ {{ selectedFrom.name }}
            </div>
          </div>
        </div>
      </div>

      <!-- Date, Seats, Roundtrip and Search Row -->
      <div class="space-y-3 sm:space-y-0 sm:grid sm:grid-cols-2 lg:grid-cols-4 sm:gap-3 lg:gap-4">
        <!-- Departure Date -->
        <div class="space-y-2">
          <label class="block text-xs sm:text-sm font-medium text-gray-700">Ngày đi</label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <i class="fas fa-calendar text-gray-400 text-sm"></i>
            </div>
            <input
              v-model="searchForm.departureDate"
              type="date"
              :min="today"
              class="w-full pl-9 sm:pl-10 pr-4 py-2.5 sm:py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none text-sm"
            />
          </div>
        </div>

        <!-- Number of Seats -->
        <div class="space-y-2">
          <label class="block text-xs sm:text-sm font-medium text-gray-700">Số ghế</label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <i class="fas fa-user text-gray-400 text-sm"></i>
            </div>
            <input
              v-model="searchForm.seats"
              type="number"
              min="1"
              max="10"
              class="w-full pl-9 sm:pl-10 pr-4 py-2.5 sm:py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none text-sm"
            />
          </div>
        </div>

        <!-- Roundtrip Checkbox -->
        <div class="space-y-2">
          <label class="block text-xs sm:text-sm font-medium text-gray-700">Loại vé</label>
          <div class="flex items-center h-10 sm:h-12 bg-gray-50 rounded-lg px-3 sm:px-4 border border-gray-200">
            <label class="flex items-center cursor-pointer">
              <input
                v-model="searchForm.roundtrip"
                @change="handleRoundtripChange"
                type="checkbox"
                class="w-4 h-4 text-indigo-600 border-gray-300 rounded focus:ring-indigo-500"
              />
              <span class="ml-2 text-xs sm:text-sm font-medium text-gray-700">Khứ hồi</span>
            </label>
          </div>
        </div>

        <!-- Search Button -->
        <div class="space-y-2">
          <label class="block text-xs sm:text-sm font-medium text-gray-700 invisible">Tìm</label>
          <button
            type="submit"
            :disabled="loadingOrigins || loadingDestinations || !selectedFrom || !selectedTo"
            class="w-full bg-indigo-600 hover:bg-indigo-700 disabled:bg-gray-400 disabled:cursor-not-allowed text-white font-semibold py-2.5 sm:py-3 px-4 sm:px-6 rounded-lg transition-colors duration-200 flex items-center justify-center text-sm"
          >
            <i class="fas fa-search mr-2 text-sm"></i>
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
        <div v-if="searchForm.roundtrip" ref="returnDateSection" class="mt-3 sm:mt-4 p-3 sm:p-4 rounded-lg bg-blue-50 border border-blue-200">
          <div class="space-y-3 sm:space-y-0 sm:grid sm:grid-cols-2 lg:grid-cols-4 sm:gap-3 lg:gap-4">
            <div class="space-y-2 sm:col-span-2 lg:col-span-1">
              <label class="block text-xs sm:text-sm font-medium text-blue-700">
                <i class="fas fa-calendar-alt text-blue-500 mr-1 text-sm"></i>
                Ngày về
              </label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <i class="fas fa-calendar text-gray-400 text-sm"></i>
                </div>
                <input
                  v-model="searchForm.returnDate"
                  type="date"
                  :min="searchForm.departureDate || today"
                  class="w-full pl-9 sm:pl-10 pr-4 py-2.5 sm:py-3 border border-blue-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none bg-white text-sm"
                  placeholder="Chọn ngày về"
                />
              </div>
            </div>
            <div class="sm:col-span-2 lg:col-span-3 flex items-end">
              <div class="text-xs sm:text-sm text-blue-600 bg-blue-100 px-2 sm:px-3 py-1.5 sm:py-2 rounded-lg w-full sm:w-auto text-center sm:text-left">
                <i class="fas fa-info-circle mr-1"></i>
                <span class="hidden sm:inline">Vé khứ hồi - </span>Chọn ngày về để tiếp tục
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

/* Smooth transitions */
.transition-all {
  transition-property: all;
}
</style> 