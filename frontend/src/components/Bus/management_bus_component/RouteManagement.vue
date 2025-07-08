<template>
  <div class="space-y-6">
    <!-- Header Section -->
    <div class="sm:flex sm:items-center sm:justify-between">
      <div>
        <h3 class="text-lg font-medium leading-6 text-gray-900">Quản lý Tuyến đường</h3>
        <p class="mt-1 text-sm text-gray-500">Tạo và quản lý các tuyến đường bus của nhà xe</p>
      </div>
      <div class="mt-4 sm:mt-0">
        <button @click="modalState.openModal()" class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
          <svg class="-ml-1 mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Thêm tuyến mới
        </button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-blue-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Tổng tuyến</dt>
                <dd class="text-lg font-medium text-gray-900">{{ routes.length }}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>
      
      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-green-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Đang hoạt động</dt>
                <dd class="text-lg font-medium text-gray-900">{{ activeRoutes }}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-yellow-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Giá trung bình</dt>
                <dd class="text-lg font-medium text-gray-900">{{ averagePrice }}đ</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-purple-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Thời gian TB</dt>
                <dd class="text-lg font-medium text-gray-900">{{ averageDuration }}h</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Routes Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-for="route in routes" :key="route.id" class="bg-white overflow-hidden shadow rounded-lg hover:shadow-md transition-shadow duration-200">
        <div class="p-6">
          <div class="flex items-center justify-between mb-4">
            <div class="flex items-center space-x-3">
              <div class="flex-shrink-0">
                <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                  <svg class="w-6 h-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"/>
                  </svg>
                </div>
              </div>
              <div>
                <h4 class="text-lg font-medium text-gray-900">{{ route.name }}</h4>
                <p class="text-sm text-gray-500">{{ route.code }}</p>
              </div>
            </div>
            <span :class="getStatusBadgeClass(route.status)" class="inline-flex px-2 py-1 text-xs font-semibold rounded-full">
              {{ getStatusText(route.status) }}
            </span>
          </div>

          <div class="space-y-3">
            <div class="flex items-center text-sm text-gray-600">
              <svg class="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
              </svg>
              <span class="font-medium">{{ route.startLocation }}</span>
              <svg class="mx-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6"/>
              </svg>
              <span class="font-medium">{{ route.endLocation }}</span>
            </div>

            <div class="flex items-center justify-between text-sm">
              <div class="flex items-center text-gray-600">
                <svg class="mr-1 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
                {{ route.duration }}h
              </div>
              <div class="flex items-center text-gray-600">
                <svg class="mr-1 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 11h.01"/>
                </svg>
                {{ route.distance }}km
              </div>
              <div class="text-lg font-semibold text-blue-600">
                {{ formatPrice(route.basePrice) }}đ
              </div>
            </div>

            <div class="pt-3 border-t border-gray-200">
              <div class="flex justify-between items-center">
                <div class="text-sm text-gray-500">
                  <span class="font-medium">{{ route.dailyTrips }}</span> chuyến/ngày
                </div>
                <div class="flex space-x-2">
                  <button @click="modalState.openModal(route)" class="text-blue-600 hover:text-blue-800 text-sm font-medium">
                    Sửa
                  </button>
                  <button @click="deleteRoute(route.id)" class="text-red-600 hover:text-red-800 text-sm font-medium">
                    Xóa
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Bus Route Modal -->
    <BusRouteModal
      :isVisible="modalState.isVisible.value"
      :currentStep="modalState.currentStep.value"
      :totalSteps="modalState.totalSteps.value"
      :steps="modalState.steps.value"
      :formData="modalState.formData"
      :isEditing="modalState.isEditing.value"
      @close="modalState.closeModal"
      @save-draft="handleSaveDraft"
      @save-form="handleSaveForm"
      @next-step="modalState.nextStep"
      @previous-step="modalState.previousStep"
      @update-form-data="modalState.updateFormData"
    >
      <template #default="{ currentStep, formData, updateFormData }">
        <BusRouteSteps
          :currentStep="currentStep"
          :formData="formData"
          :validationErrors="modalState.validationErrors.value"
          :availableServices="modalState.availableServices.value"
          :weekDays="modalState.weekDays.value"
          :addStopPoint="modalState.addStopPoint"
          :removeStopPoint="modalState.removeStopPoint"
        />
      </template>
    </BusRouteModal>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import BusRouteModal from './BusRouteModal.vue'
import BusRouteSteps from './BusRouteSteps.vue'
import { useBusRouteModal } from '../../../composables/useBusRouteModal'

// Modal State using composable
const modalState = useBusRouteModal()

// Routes data (keep existing data)
const routes = ref([
  {
    id: 1,
    name: 'Hà Nội - TP.HCM',
    code: 'HN-HCM-001',
    startLocation: 'Hà Nội',
    endLocation: 'TP. Hồ Chí Minh',
    duration: 24,
    distance: 1700,
    basePrice: 350000,
    dailyTrips: 4,
    status: 'active'
  },
  {
    id: 2,
    name: 'Hà Nội - Đà Nẵng',
    code: 'HN-DN-002',
    startLocation: 'Hà Nội',
    endLocation: 'Đà Nẵng',
    duration: 12,
    distance: 800,
    basePrice: 220000,
    dailyTrips: 6,
    status: 'active'
  },
  {
    id: 3,
    name: 'TP.HCM - Cần Thơ',
    code: 'HCM-CT-003',
    startLocation: 'TP. Hồ Chí Minh',
    endLocation: 'Cần Thơ',
    duration: 4,
    distance: 170,
    basePrice: 120000,
    dailyTrips: 8,
    status: 'active'
  }
])

// Computed properties
const activeRoutes = computed(() => routes.value.filter(route => route.status === 'active').length)

const averagePrice = computed(() => {
  const total = routes.value.reduce((sum, route) => sum + route.basePrice, 0)
  return Math.round(total / routes.value.length)
})

const averageDuration = computed(() => {
  const total = routes.value.reduce((sum, route) => sum + route.duration, 0)
  return Math.round(total / routes.value.length)
})

// Methods
const getStatusBadgeClass = (status) => {
  const classes = {
    active: 'bg-green-100 text-green-800',
    inactive: 'bg-red-100 text-red-800',
    maintenance: 'bg-yellow-100 text-yellow-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

const getStatusText = (status) => {
  const texts = {
    active: 'Hoạt động',
    inactive: 'Tạm dừng',
    maintenance: 'Bảo trì'
  }
  return texts[status] || 'Không xác định'
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN').format(price)
}

const deleteRoute = (routeId) => {
  if (confirm('Bạn có chắc muốn xóa tuyến đường này?')) {
    routes.value = routes.value.filter(route => route.id !== routeId)
  }
}

// Modal event handlers
const handleSaveDraft = async (formData) => {
  try {
    await modalState.saveDraft()
    // Show success message
    console.log('Draft saved successfully')
  } catch (error) {
    console.error('Error saving draft:', error)
  }
}

const handleSaveForm = async (formData) => {
  try {
    const result = await modalState.saveForm()
    
    if (result.success) {
      // Add or update route in the list
      if (modalState.isEditing.value) {
        const index = routes.value.findIndex(r => r.id === modalState.editingRouteId.value)
        if (index !== -1) {
          routes.value[index] = { ...routes.value[index], ...convertFormDataToRoute(formData) }
        }
      } else {
        const newRoute = {
          id: Date.now(),
          ...convertFormDataToRoute(formData),
          status: 'active'
        }
        routes.value.push(newRoute)
      }
      
      modalState.closeModal()
      console.log(result.message)
    }
  } catch (error) {
    console.error('Error saving form:', error)
  }
}

// Helper function to convert form data to route format
const convertFormDataToRoute = (formData) => {
  return {
    name: `${formData.departure.city} - ${formData.arrival.city}`,
    code: `${formData.departure.city.toUpperCase()}-${formData.arrival.city.toUpperCase()}-${String(Date.now()).slice(-3)}`,
    startLocation: formData.departure.city,
    endLocation: formData.arrival.city,
    duration: parseInt(formData.travelTime) || 0,
    distance: 0, // Would be calculated
    basePrice: formData.ticketPrice,
    dailyTrips: formData.operatingDays.length || 1
  }
}
</script> 