<script setup>

import { ref, watch, computed, onMounted, nextTick } from 'vue'
import ProvinceAPI, { clearProvinceCache } from '@/api/provinceApi'

import { graphqlRequest } from '@/api/graphqlClient'
import { toast } from '@/utils/notifications'
import { useRouteStore } from '@/stores/routeStore'

// Props
const props = defineProps({
  activeTab: {
    type: String,
    default: 'sleeping-bus'
  }
})

const emit = defineEmits(['search'])

// Route store
const { getRouteData, clearRouteData } = useRouteStore()

// Province name mapping để match với API
const provinceNameMapping = {
  'Hồ Chí Minh': 'Thành phố Hồ Chí Minh',
  'HCM': 'Thành phố Hồ Chí Minh',
  'Sài Gòn': 'Thành phố Hồ Chí Minh',
  'TP.HCM': 'Thành phố Hồ Chí Minh',
  'Hà Nội': 'Thành phố Hà Nội',
  'HN': 'Thành phố Hà Nội',
  'Đà Nẵng': 'Thành phố Đà Nẵng',
  'Vũng Tàu': 'Tỉnh Bà Rịa - Vũng Tàu',
  'Bà Rịa - Vũng Tàu': 'Tỉnh Bà Rịa - Vũng Tàu',
  'Cần Thơ': 'Thành phố Cần Thơ',
  'Nha Trang': 'Tỉnh Khánh Hòa',
  'Khánh Hòa': 'Tỉnh Khánh Hòa',
  'Đà Lạt': 'Tỉnh Lâm Đồng',
  'Lâm Đồng': 'Tỉnh Lâm Đồng',
  'Phan Thiết': 'Tỉnh Bình Thuận',
  'Bình Thuận': 'Tỉnh Bình Thuận',
  'Hải Phòng': 'Thành phố Hải Phòng',
  'Huế': 'Tỉnh Thừa Thiên Huế',
  'Thừa Thiên Huế': 'Tỉnh Thừa Thiên Huế',
  'Sapa': 'Tỉnh Lào Cai',
  'Lào Cai': 'Tỉnh Lào Cai',
  'Bình Dương': 'Tỉnh Bình Dương'
}

// Loading states
const loadingProvinces = ref(false)
const loadingDepartureDistricts = ref(false)
const loadingArrivalDistricts = ref(false)

// Form data với tỉnh/huyện
const searchForm = ref({
  departureProvince: '',
  departureDistrict: '',
  arrivalProvince: '',
  arrivalDistrict: '',
  departureDate: '',
  seats: 1
})

// Data từ API
const provinces = ref([])
const departureDistricts = ref([])
const arrivalDistricts = ref([])

// Computed
const isFormValid = computed(() => {
  return searchForm.value.departureProvince && 
         searchForm.value.arrivalProvince && 
         searchForm.value.departureDate
})

// Methods
const loadProvinces = async () => {
  try {
    loadingProvinces.value = true
    const provinceList = await ProvinceAPI.getProvinceList()
    provinces.value = provinceList
  } catch (error) {
  } finally {
    loadingProvinces.value = false
  }
}

// Fill form from store data
const fillFormFromStore = async () => {
  try {
    const routeData = getRouteData();
    
    if (!routeData) {
      return;
    }
    
    // Fill form data
    if (routeData.departureProvince) {
      // Map tên tỉnh nếu cần
      const mappedProvinceName = provinceNameMapping[routeData.departureProvince] || routeData.departureProvince;
      
      // Kiểm tra xem tỉnh có tồn tại trong danh sách không
      const provinceExists = provinces.value.some(p => p.name === mappedProvinceName);
      
      if (provinceExists) {
        searchForm.value.departureProvince = mappedProvinceName;
        await loadDepartureDistricts(mappedProvinceName);
      }
    }
    if (routeData.arrivalProvince) {
      // Map tên tỉnh nếu cần
      const mappedProvinceName = provinceNameMapping[routeData.arrivalProvince] || routeData.arrivalProvince;
      
      // Kiểm tra xem tỉnh có tồn tại trong danh sách không
      const provinceExists = provinces.value.some(p => p.name === mappedProvinceName);
      
      if (provinceExists) {
        searchForm.value.arrivalProvince = mappedProvinceName;
        await loadArrivalDistricts(mappedProvinceName);
      }
    }
    if (routeData.departureDate) {
      searchForm.value.departureDate = routeData.departureDate;
    }
    
    // Xóa dữ liệu sau khi đã fill để tránh fill lại
    clearRouteData();
    
  } catch (error) {
    console.error('Lỗi khi đọc dữ liệu từ store:', error);
    clearRouteData();
  }
}

const loadDepartureDistricts = async (provinceName) => {
  if (!provinceName) {
    departureDistricts.value = []
    return
  }

  try {
    loadingDepartureDistricts.value = true
    const districts = await ProvinceAPI.getDistrictsByProvince(provinceName)
    departureDistricts.value = districts
      } catch (error) {
    departureDistricts.value = []
  } finally {
    loadingDepartureDistricts.value = false
  }
}

const loadArrivalDistricts = async (provinceName) => {
  if (!provinceName) {
    arrivalDistricts.value = []
    return
  }

  try {
    loadingArrivalDistricts.value = true
    const districts = await ProvinceAPI.getDistrictsByProvince(provinceName)
    arrivalDistricts.value = districts
      } catch (error) {
    arrivalDistricts.value = []
  } finally {
    loadingArrivalDistricts.value = false
  }
}

// Watch province changes
watch(() => searchForm.value.departureProvince, (newProvince) => {
  searchForm.value.departureDistrict = '' // Reset district
  if (newProvince) {
    loadDepartureDistricts(newProvince)
  } else {
    departureDistricts.value = []
  }
})

watch(() => searchForm.value.arrivalProvince, (newProvince) => {
  searchForm.value.arrivalDistrict = '' // Reset district
  if (newProvince) {
    loadArrivalDistricts(newProvince)
  } else {
    arrivalDistricts.value = []
  }
})



const handleSearch = async () => {
  if (!isFormValid.value) {
    alert('Vui lòng điền đầy đủ thông tin tìm kiếm')
    return
  }

  // Starting search with form data

  try {
    // Tạo search criteria
  const searchCriteria = {
      departureProvince: searchForm.value.departureProvince,
      departureDistrict: searchForm.value.departureDistrict || null,
      arrivalProvince: searchForm.value.arrivalProvince,
      arrivalDistrict: searchForm.value.arrivalDistrict || null,
      slotDate: searchForm.value.departureDate,
      minAvailableSeats: searchForm.value.seats
    }

    // Search criteria prepared

    // Gọi GraphQL query
    const response = await graphqlRequest({
      query: `
        query SearchBusSlotsDetailed(
          $departureProvince: String!
          $departureDistrict: String
          $arrivalProvince: String!
          $arrivalDistrict: String
          $slotDate: String!
          $minAvailableSeats: Int
        ) {
          searchBusSlotsDetailed(
            departureProvince: $departureProvince
            departureDistrict: $departureDistrict
            arrivalProvince: $arrivalProvince
            arrivalDistrict: $arrivalDistrict
            slotDate: $slotDate
            minAvailableSeats: $minAvailableSeats
          ) {
            id
            slotDate
            departureTime
            arrivalTime
            price
            totalSeats
            availableSeats
            status
            bus {
              id
              name
              licensePlate
              categoryName
              totalSeats
              busImages {
                busId
                imageId
                image {
                  id
                  url
                  altText
                  publicId
                }
              }
            }
            route {
              id
              originLocation {
                id
                name
                provinceCity
                district
              }
              destinationLocation {
                id
                name
                provinceCity
                district
              }
            }
            seats {
              id
              seatNumber
              isBooked
              price
              seatType
            }
          }
        }
      `,
      variables: {
        departureProvince: searchForm.value.departureProvince,
        departureDistrict: searchForm.value.departureDistrict || null,
        arrivalProvince: searchForm.value.arrivalProvince,
        arrivalDistrict: searchForm.value.arrivalDistrict || null,
        slotDate: searchForm.value.departureDate,
        minAvailableSeats: searchForm.value.seats
      }
    })

    // GraphQL response received

    // Transform data và emit với format mong đợi của BusSearchModal
    const searchResults = {
      busSlots: response.data.searchBusSlotsDetailed,
      searchParams: {
        departureProvince: searchForm.value.departureProvince,
        departureDistrict: searchForm.value.departureDistrict,
        arrivalProvince: searchForm.value.arrivalProvince,
        arrivalDistrict: searchForm.value.arrivalDistrict,
    departureDate: searchForm.value.departureDate,
    seats: searchForm.value.seats
      }
    }

    // Emitting search results

    // Emit kết quả tìm kiếm
    emit('search', searchResults)
  } catch (error) {
    toast.warning('Không tìm thấy kết quả')
  }
}

// Set default date to today
const today = new Date().toISOString().split('T')[0]
searchForm.value.departureDate = today

// Load provinces on mount
onMounted(async () => {
  // Scroll to top khi component mount
  window.scrollTo({ top: 0, behavior: 'smooth' });
  
  clearProvinceCache()
  await loadProvinces()
  // Fill form from store data after provinces are loaded
  await fillFormFromStore()
})
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
      </h2>
    </div>

    <!-- Search Form -->
    <form @submit.prevent="handleSearch" class="space-y-3 sm:space-y-4 md:space-y-6">
      <!-- Departure Location Row -->
      <div class="space-y-3 md:space-y-0 md:grid md:grid-cols-2 md:gap-4">
        <!-- Departure Province -->
        <div class="space-y-2">
          <label class="block text-xs sm:text-sm font-medium text-gray-700">
            Tỉnh/Thành phố đi
            <span v-if="loadingProvinces" class="text-blue-500 ml-1">
              <i class="fas fa-spinner fa-spin text-xs"></i>
            </span>
          </label>
          <select
            v-model="searchForm.departureProvince"
            :disabled="loadingProvinces"
            class="w-full px-3 py-2.5 sm:py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none text-sm disabled:bg-gray-100 disabled:cursor-not-allowed"
          >
            <option value="">Chọn tỉnh/thành phố</option>
            <option v-for="province in provinces" :key="province.code" :value="province.name">
              {{ province.name }}
            </option>
          </select>
        </div>

        <!-- Departure District (Optional) -->
        <div class="space-y-2">
          <label class="block text-xs sm:text-sm font-medium text-gray-700">
            Quận/Huyện đi (tùy chọn)
            <span v-if="loadingDepartureDistricts" class="text-blue-500 ml-1">
              <i class="fas fa-spinner fa-spin text-xs"></i>
            </span>
          </label>
          <select
            v-model="searchForm.departureDistrict"
            :disabled="!searchForm.departureProvince || loadingDepartureDistricts"
            class="w-full px-3 py-2.5 sm:py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none text-sm disabled:bg-gray-100 disabled:cursor-not-allowed"
          >
            <option value="">Tất cả quận/huyện</option>
            <option v-for="district in departureDistricts" :key="district.code" :value="district.value">
              {{ district.label }}
            </option>
          </select>
        </div>
        </div>

      <!-- Arrival Location Row -->
      <div class="space-y-3 md:space-y-0 md:grid md:grid-cols-2 md:gap-4">
        <!-- Arrival Province -->
        <div class="space-y-2">
          <label class="block text-xs sm:text-sm font-medium text-gray-700">
            Tỉnh/Thành phố đến
            <span v-if="loadingProvinces" class="text-blue-500 ml-1">
              <i class="fas fa-spinner fa-spin text-xs"></i>
            </span>
          </label>
          <select
            v-model="searchForm.arrivalProvince"
            :disabled="loadingProvinces"
            class="w-full px-3 py-2.5 sm:py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none text-sm disabled:bg-gray-100 disabled:cursor-not-allowed"
          >
            <option value="">Chọn tỉnh/thành phố</option>
            <option v-for="province in provinces" :key="province.code" :value="province.name">
              {{ province.name }}
            </option>
          </select>
        </div>

        <!-- Arrival District (Optional) -->
        <div class="space-y-2">
          <label class="block text-xs sm:text-sm font-medium text-gray-700">
            Quận/Huyện đến (tùy chọn)
            <span v-if="loadingArrivalDistricts" class="text-blue-500 ml-1">
              <i class="fas fa-spinner fa-spin text-xs"></i>
            </span>
          </label>
          <select
            v-model="searchForm.arrivalDistrict"
            :disabled="!searchForm.arrivalProvince || loadingArrivalDistricts"
            class="w-full px-3 py-2.5 sm:py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none text-sm disabled:bg-gray-100 disabled:cursor-not-allowed"
          >
            <option value="">Tất cả quận/huyện</option>
            <option v-for="district in arrivalDistricts" :key="district.code" :value="district.value">
              {{ district.label }}
            </option>
          </select>
        </div>
      </div>

      <!-- Date, Seats and Search Row -->
      <div class="space-y-3 sm:space-y-0 sm:grid sm:grid-cols-3 sm:gap-3 lg:gap-4">
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

        <!-- Search Button -->
        <div class="space-y-2">
          <label class="block text-xs sm:text-sm font-medium text-gray-700 invisible">Tìm</label>
          <button
            type="submit"
            :disabled="!isFormValid"
            class="w-full bg-indigo-600 hover:bg-indigo-700 disabled:bg-gray-400 disabled:cursor-not-allowed text-white font-semibold py-2.5 sm:py-3 px-4 sm:px-6 rounded-lg transition-colors duration-200 flex items-center justify-center text-sm"
          >
            <i class="fas fa-search mr-2 text-sm"></i>
            Tìm kiếm
          </button>
        </div>
      </div>
    </form>
  </div>
</template>