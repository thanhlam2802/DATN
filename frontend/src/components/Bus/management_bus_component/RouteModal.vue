<template>
  <teleport to="body">
    <transition name="modal-overlay" appear>
      <div v-if="isOpen" @click="closeModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
        <transition name="modal-content" appear>
          <div @click.stop class="relative bg-white rounded-xl shadow-2xl max-w-4xl w-full mx-4 overflow-hidden max-h-[90vh] overflow-y-auto">
            <div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
              <div class="sm:flex sm:items-start">
                <div class="w-full mt-3 text-center sm:mt-0 sm:text-left">
                  <h3 class="text-lg leading-6 font-medium text-gray-900 mb-6">
                    {{ isEditing ? 'Chỉnh sửa tuyến đường' : 'Thêm tuyến đường mới' }}
                  </h3>
                  
                  <!-- Form -->
                  <form @submit.prevent="handleSubmit" class="space-y-6">
                    <!-- Grid Layout for Origin and Destination -->
                    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
                      <!-- Origin Location (Điểm đi) -->
                      <div class="bg-blue-50 p-4 rounded-lg">
                        <h4 class="text-md font-medium text-gray-900 mb-4 flex items-center">
                          <svg class="w-5 h-5 mr-2 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
                          </svg>
                          Điểm đi
                        </h4>
                        
                        <!-- Origin Name -->
                        <div class="mb-4">
                          <label for="originName" class="block text-sm font-medium text-gray-700 mb-1">
                            Tên điểm đi <span class="text-red-500">*</span>
                      </label>
                      <input
                            id="originName"
                            v-model="form.originLocation.name"
                        type="text"
                        required
                            placeholder="VD: Bến xe Mỹ Đình"
                        class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                      />
                          <p v-if="errors.originName" class="text-red-500 text-xs mt-1">{{ errors.originName }}</p>
                        </div>

                        <!-- Origin Province -->
                        <div class="mb-4">
                          <label for="originProvince" class="block text-sm font-medium text-gray-700 mb-1">
                            Tỉnh/Thành phố <span class="text-red-500">*</span>
                          </label>
                          <select
                            id="originProvince"
                            v-model="form.originLocation.provinceCity"
                            @change="onOriginProvinceChange"
                            required
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                          >
                            <option value="">Chọn tỉnh/thành phố</option>
                            <option v-for="province in provinces" :key="province.code" :value="province.name">
                              {{ province.name }}
                            </option>
                          </select>
                          <p v-if="errors.originProvince" class="text-red-500 text-xs mt-1">{{ errors.originProvince }}</p>
                        </div>

                        <!-- Origin District -->
                        <div class="mb-4">
                          <label for="originDistrict" class="block text-sm font-medium text-gray-700 mb-1">
                            Quận/Huyện <span class="text-red-500">*</span>
                          </label>
                          <select
                            id="originDistrict"
                            v-model="form.originLocation.district"
                            @change="onOriginDistrictChange"
                            :disabled="!originDistricts.length"
                            required
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100"
                          >
                            <option value="">Chọn quận/huyện</option>
                            <option v-for="district in originDistricts" :key="district.code" :value="district.name">
                              {{ district.name }}
                            </option>
                          </select>
                          <p v-if="errors.originDistrict" class="text-red-500 text-xs mt-1">{{ errors.originDistrict }}</p>
                        </div>

                        <!-- Origin Ward -->
                        <div class="mb-4">
                          <label for="originWard" class="block text-sm font-medium text-gray-700 mb-1">
                            Phường/Xã
                          </label>
                          <select
                            id="originWard"
                            v-model="form.originLocation.ward"
                            :disabled="!originWards.length"
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100"
                          >
                            <option value="">Chọn phường/xã</option>
                            <option v-for="ward in originWards" :key="ward.code" :value="ward.name">
                              {{ ward.name }}
                            </option>
                          </select>
                        </div>

                        <!-- Origin Address Details -->
                    <div>
                          <label for="originAddress" class="block text-sm font-medium text-gray-700 mb-1">
                            Địa chỉ chi tiết
                          </label>
                          <textarea
                            id="originAddress"
                            v-model="form.originLocation.addressDetails"
                            rows="2"
                            placeholder="VD: Số 123, Đường ABC..."
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                          ></textarea>
                        </div>
                      </div>

                      <!-- Destination Location (Điểm đến) -->
                      <div class="bg-green-50 p-4 rounded-lg">
                        <h4 class="text-md font-medium text-gray-900 mb-4 flex items-center">
                          <svg class="w-5 h-5 mr-2 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
                          </svg>
                          Điểm đến
                        </h4>
                        
                        <!-- Destination Name -->
                        <div class="mb-4">
                          <label for="destinationName" class="block text-sm font-medium text-gray-700 mb-1">
                            Tên điểm đến <span class="text-red-500">*</span>
                      </label>
                      <input
                            id="destinationName"
                            v-model="form.destinationLocation.name"
                        type="text"
                        required
                            placeholder="VD: Bến xe Miền Đông"
                        class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                      />
                          <p v-if="errors.destinationName" class="text-red-500 text-xs mt-1">{{ errors.destinationName }}</p>
                        </div>

                        <!-- Destination Province -->
                        <div class="mb-4">
                          <label for="destinationProvince" class="block text-sm font-medium text-gray-700 mb-1">
                            Tỉnh/Thành phố <span class="text-red-500">*</span>
                          </label>
                          <select
                            id="destinationProvince"
                            v-model="form.destinationLocation.provinceCity"
                            @change="onDestinationProvinceChange"
                            required
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                          >
                            <option value="">Chọn tỉnh/thành phố</option>
                            <option v-for="province in provinces" :key="province.code" :value="province.name">
                              {{ province.name }}
                            </option>
                          </select>
                          <p v-if="errors.destinationProvince" class="text-red-500 text-xs mt-1">{{ errors.destinationProvince }}</p>
                        </div>

                        <!-- Destination District -->
                        <div class="mb-4">
                          <label for="destinationDistrict" class="block text-sm font-medium text-gray-700 mb-1">
                            Quận/Huyện <span class="text-red-500">*</span>
                          </label>
                          <select
                            id="destinationDistrict"
                            v-model="form.destinationLocation.district"
                            @change="onDestinationDistrictChange"
                            :disabled="!destinationDistricts.length"
                            required
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100"
                          >
                            <option value="">Chọn quận/huyện</option>
                            <option v-for="district in destinationDistricts" :key="district.code" :value="district.name">
                              {{ district.name }}
                            </option>
                          </select>
                          <p v-if="errors.destinationDistrict" class="text-red-500 text-xs mt-1">{{ errors.destinationDistrict }}</p>
                        </div>

                        <!-- Destination Ward -->
                        <div class="mb-4">
                          <label for="destinationWard" class="block text-sm font-medium text-gray-700 mb-1">
                            Phường/Xã
                          </label>
                          <select
                            id="destinationWard"
                            v-model="form.destinationLocation.ward"
                            :disabled="!destinationWards.length"
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100"
                          >
                            <option value="">Chọn phường/xã</option>
                            <option v-for="ward in destinationWards" :key="ward.code" :value="ward.name">
                              {{ ward.name }}
                            </option>
                          </select>
                        </div>

                        <!-- Destination Address Details -->
                        <div>
                          <label for="destinationAddress" class="block text-sm font-medium text-gray-700 mb-1">
                            Địa chỉ chi tiết
                          </label>
                          <textarea
                            id="destinationAddress"
                            v-model="form.destinationLocation.addressDetails"
                            rows="2"
                            placeholder="VD: Số 456, Đường XYZ..."
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                          ></textarea>
                        </div>
                      </div>
                    </div>

                    <!-- Route Information -->
                    <div class="border-t border-gray-200 pt-6">
                      <h4 class="text-lg font-medium text-gray-900 mb-4 flex items-center">
                        <svg class="w-5 h-5 mr-2 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"/>
                        </svg>
                        Thông tin tuyến đường
                      </h4>

                      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <!-- Distance -->
                    <div>
                      <label for="distanceKm" class="block text-sm font-medium text-gray-700 mb-1">
                        Khoảng cách (km) <span class="text-red-500">*</span>
                      </label>
                      <input
                        id="distanceKm"
                        v-model.number="form.distanceKm"
                        type="number"
                        min="1"
                        required
                        placeholder="VD: 1700"
                        class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                      />
                      <p v-if="errors.distanceKm" class="text-red-500 text-xs mt-1">{{ errors.distanceKm }}</p>
                    </div>

                    <!-- Estimated Duration -->
                    <div>
                      <label for="estimatedHours" class="block text-sm font-medium text-gray-700 mb-1">
                        Thời gian di chuyển ước tính <span class="text-red-500">*</span>
                      </label>
                      <div class="grid grid-cols-2 gap-3">
                        <div>
                          <input
                            id="estimatedHours"
                            v-model.number="form.estimatedHours"
                            type="number"
                            min="0"
                            max="24"
                            required
                            placeholder="Giờ"
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                          />
                          <p class="text-xs text-gray-500 mt-1">Giờ</p>
                        </div>
                        <div>
                          <input
                            id="estimatedMinutes"
                            v-model.number="form.estimatedMinutes"
                            type="number"
                            min="0"
                            max="59"
                            placeholder="Phút"
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                          />
                          <p class="text-xs text-gray-500 mt-1">Phút</p>
                        </div>
                      </div>
                      <p v-if="errors.estimatedMinutes" class="text-red-500 text-xs mt-1">{{ errors.estimatedMinutes }}</p>
                        </div>
                      </div>
                    </div>

                    <!-- Price Section -->
                    <div class="border-t border-gray-200 pt-4 mt-6">
                      <h4 class="text-lg font-medium text-gray-900 mb-4 flex items-center">
                        <svg class="w-5 h-5 mr-2 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1"/>
                        </svg>
                        Thiết lập giá vé
                      </h4>

                      <!-- Bus Categories Selection -->
                      <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-700 mb-2">
                          Loại xe áp dụng <span class="text-red-500">*</span>
                        </label>
                        <div class="grid grid-cols-2 gap-2 max-h-32 overflow-y-auto border border-gray-300 rounded-md p-2">
                          <label v-for="category in busCategories" :key="category.id" class="flex items-center space-x-2 p-2 hover:bg-gray-50 rounded">
                            <input 
                              type="checkbox" 
                              :value="category.id"
                              v-model="form.selectedCategories"
                              class="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                            />
                            <span class="text-sm text-gray-700">{{ category.name }}</span>
                          </label>
                        </div>
                        <p v-if="errors.selectedCategories" class="text-red-500 text-xs mt-1">{{ errors.selectedCategories }}</p>
                      </div>

                      <!-- Price Settings for Each Category -->
                      <div v-if="form.selectedCategories.length > 0" class="space-y-4 mb-4">
                        <h5 class="text-sm font-medium text-gray-700">Thiết lập giá cho từng loại xe:</h5>
                        
                        <div v-for="categoryId in form.selectedCategories" :key="categoryId" class="border border-gray-200 rounded-lg p-4">
                          <div class="flex items-center justify-between mb-3">
                            <h6 class="text-sm font-medium text-gray-900">
                              {{ getCategoryName(categoryId) }}
                            </h6>
                            <span class="text-xs text-gray-500">ID: {{ categoryId }}</span>
                          </div>
                          
                          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                              <label :for="'basePrice_' + categoryId" class="block text-sm font-medium text-gray-700 mb-1">
                            Giá cơ sở (VND) <span class="text-red-500">*</span>
                          </label>
                          <input
                                :id="'basePrice_' + categoryId"
                                v-model.number="form.priceRules[categoryId].basePrice"
                            type="number"
                            min="10000"
                            step="10000"
                            required
                            placeholder="500000"
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                          />
                        </div>
                        <div>
                              <label :for="'promotionPrice_' + categoryId" class="block text-sm font-medium text-gray-700 mb-1">
                            Giá khuyến mãi (VND)
                          </label>
                          <input
                                :id="'promotionPrice_' + categoryId"
                                v-model.number="form.priceRules[categoryId].promotionPrice"
                            type="number"
                            min="10000"
                            step="10000"
                            placeholder="Để trống nếu không có"
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                          />
                        </div>
                      </div>

                          <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-3">
                        <div>
                              <label :for="'validFrom_' + categoryId" class="block text-sm font-medium text-gray-700 mb-1">
                            Áp dụng từ ngày <span class="text-red-500">*</span>
                          </label>
                          <input
                                :id="'validFrom_' + categoryId"
                                v-model="form.priceRules[categoryId].validFrom"
                            type="date"
                            required
                            :min="today"
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                          />
                        </div>
                        <div>
                              <label :for="'validTo_' + categoryId" class="block text-sm font-medium text-gray-700 mb-1">
                            Áp dụng đến ngày <span class="text-red-500">*</span>
                          </label>
                          <input
                                :id="'validTo_' + categoryId"
                                v-model="form.priceRules[categoryId].validTo"
                            type="date"
                            required
                                :min="form.priceRules[categoryId].validFrom || today"
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                          />
                        </div>
                      </div>

                          <div class="mt-3">
                            <label :for="'notes_' + categoryId" class="block text-sm font-medium text-gray-700 mb-1">
                          Ghi chú (tùy chọn)
                        </label>
                        <textarea
                              :id="'notes_' + categoryId"
                              v-model="form.priceRules[categoryId].notes"
                          rows="2"
                              placeholder="Ghi chú cho loại xe này..."
                          class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                        ></textarea>
                          </div>
                        </div>
                      </div>


                    </div>

                    <!-- Preview -->
                    <div v-if="form.originLocation.name && form.destinationLocation.name" class="mt-4 p-3 bg-gray-50 rounded-md">
                      <h4 class="text-sm font-medium text-gray-900 mb-2">Xem trước:</h4>
                      <div class="text-sm text-gray-600 space-y-1">
                        <div>🛣️ <strong>{{ getFullOriginName() }}</strong> → <strong>{{ getFullDestinationName() }}</strong></div>
                        <div>📏 Khoảng cách: <strong>{{ form.distanceKm || 0 }}km</strong></div>
                        <div>⏱️ Thời gian: <strong>{{ getFormattedDuration() }}</strong></div>
                        <div>🚌 Loại xe: <strong>{{ getSelectedCategoryNames() }}</strong></div>
                        <div v-if="form.selectedCategories.length > 0" class="space-y-1">
                          <div class="font-medium">💰 Giá vé theo loại xe:</div>
                          <div v-for="categoryId in form.selectedCategories" :key="categoryId" class="text-xs">
                            • {{ getCategoryName(categoryId) }}: 
                            <strong>{{ formatPriceRange(categoryId) }}</strong>
                          </div>
                        </div>

                      </div>
                    </div>
                  </form>
                </div>
              </div>
            </div>
            
            <!-- Modal actions -->
            <div class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
              <button
                type="button"
                @click="handleSubmit"
                :disabled="isSubmitting"
                class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-base font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:ml-3 sm:w-auto sm:text-sm disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <svg v-if="isSubmitting" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                {{ isSubmitting ? 'Đang xử lý...' : (isEditing ? 'Cập nhật' : 'Tạo tuyến') }}
              </button>
              <button
                type="button"
                @click="closeModal"
                :disabled="isSubmitting"
                class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm disabled:opacity-50"
              >
                Hủy
              </button>
            </div>
          </div>
        </transition>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
import { ref, reactive, computed, watch, nextTick, onMounted } from 'vue'
import { RouteAPI } from '@/api/busApi/route/api';
import { BusCategoryAPI } from '@/api/busApi';
import { PriceAPI } from '@/api/busApi';
import ProvinceAPI from '@/api/provinceApi';
// @ts-ignore
import { toast, handleError } from '@/utils/notifications'

// Emits
const emit = defineEmits(['route-created', 'route-updated'])

// Lifecycle
onMounted(async () => {
  await Promise.all([
    loadBusCategories(),
    loadProvinces()
  ])
})

// State
const isOpen = ref(false)
const isEditing = ref(false)
const isSubmitting = ref(false)
const editingRouteId = ref(null)

// Province data
const provinces = ref([])
const originDistricts = ref([])
const destinationDistricts = ref([])
const originWards = ref([])
const destinationWards = ref([])

// Existing state
const busCategories = ref([])
const today = new Date().toISOString().split('T')[0]
const existingCategoryIds = ref([])

// Form data 
const form = reactive({
  originLocation: {
    name: '',
    provinceCity: '',
    district: '',
    ward: '',
    addressDetails: ''
  },
  destinationLocation: {
    name: '',
    provinceCity: '',
    district: '',
    ward: '',
    addressDetails: ''
  },
  distanceKm: null,
  estimatedHours: null,
  estimatedMinutes: 0,
  selectedCategories: [],
  priceRules: {},
  basePrice: null,
  promotionPrice: null,
  validFrom: today,
  validTo: new Date(Date.now() + 365 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
  notes: ''
})

// Errors 
const errors = reactive({
  originName: '',
  originProvince: '',
  originDistrict: '',
  destinationName: '',
  destinationProvince: '',
  destinationDistrict: '',
  distanceKm: '',
  estimatedMinutes: '',
  selectedCategories: '',
  basePrice: '',
  validFrom: '',
  validTo: ''
})

// Helper computed properties
const estimatedDurationMinutes = computed(() => {
  const hours = form.estimatedHours || 0
  const minutes = form.estimatedMinutes || 0
  return hours * 60 + minutes
})

// Methods for provinces
const loadProvinces = async () => {
  try {
    provinces.value = await ProvinceAPI.getProvinceList()
  } catch (error) {
    console.error('❌ Error loading provinces:', error)
    handleError.api(error, 'tải danh sách tỉnh thành')
  }
}

const onOriginProvinceChange = async () => {
  form.originLocation.district = ''
  form.originLocation.ward = ''
  originWards.value = []
  if (form.originLocation.provinceCity) {
    try {
      originDistricts.value = await ProvinceAPI.getDistrictsByProvince(form.originLocation.provinceCity)
    } catch (error) {
      console.error('❌ Error loading origin districts:', error)
      originDistricts.value = []
    }
  } else {
    originDistricts.value = []
  }
}

const onDestinationProvinceChange = async () => {
  form.destinationLocation.district = ''
  form.destinationLocation.ward = ''
  destinationWards.value = []
  if (form.destinationLocation.provinceCity) {
    try {
      destinationDistricts.value = await ProvinceAPI.getDistrictsByProvince(form.destinationLocation.provinceCity)
    } catch (error) {
      console.error('❌ Error loading destination districts:', error)
      destinationDistricts.value = []
    }
  } else {
    destinationDistricts.value = []
  }
}

const onOriginDistrictChange = async () => {
  form.originLocation.ward = ''
  if (form.originLocation.district && form.originLocation.provinceCity) {
    try {
      originWards.value = await ProvinceAPI.getWardsByDistrict(form.originLocation.provinceCity, form.originLocation.district)
    } catch (error) {
      console.error('❌ Error loading origin wards:', error)
      originWards.value = []
    }
  } else {
    originWards.value = []
  }
}

const onDestinationDistrictChange = async () => {
  form.destinationLocation.ward = ''
  if (form.destinationLocation.district && form.destinationLocation.provinceCity) {
    try {
      destinationWards.value = await ProvinceAPI.getWardsByDistrict(form.destinationLocation.provinceCity, form.destinationLocation.district)
    } catch (error) {
      console.error('❌ Error loading destination wards:', error)
      destinationWards.value = []
    }
  } else {
    destinationWards.value = []
  }
}

// Helper display methods
const getFullOriginName = () => {
  const parts = [
    form.originLocation.name,
    form.originLocation.ward,
    form.originLocation.district,
    form.originLocation.provinceCity
  ].filter(Boolean)
  return parts.join(', ')
}

const getFullDestinationName = () => {
  const parts = [
    form.destinationLocation.name,
    form.destinationLocation.ward,
    form.destinationLocation.district,
    form.destinationLocation.provinceCity
  ].filter(Boolean)
  return parts.join(', ')
}

const getFormattedDuration = () => {
  const hours = form.estimatedHours || 0
  const minutes = form.estimatedMinutes || 0
  
  if (hours === 0 && minutes === 0) {
    return 'Chưa xác định'
  }
  
  if (hours === 0) {
    return `${minutes} phút`
  }
  
  if (minutes === 0) {
    return `${hours} giờ`
  }
  
  return `${hours} giờ ${minutes} phút`
}

const getSelectedCategoryNames = () => {
  if (form.selectedCategories.length === 0) {
    return 'Chưa chọn'
  }
  
  const names = form.selectedCategories.map(categoryId => {
    const category = busCategories.value.find(cat => cat.id === categoryId)
    return category ? category.name : 'Không xác định'
  })
  
  return names.join(', ')
}

const getFormattedPrice = () => {
  if (!form.basePrice || form.basePrice <= 0) {
    return 'Chưa xác định'
  }
  
  const base = new Intl.NumberFormat('vi-VN').format(form.basePrice) + ' VND'
  if (form.promotionPrice && form.promotionPrice > 0) {
    const promo = new Intl.NumberFormat('vi-VN').format(form.promotionPrice) + ' VND'
    return `${base} (Khuyến mãi: ${promo})`
  }
  return base
}

// Existing methods (keep but update for Location objects)
const loadBusCategories = async () => {
  try {
    const categories = await BusCategoryAPI.getAllBusCategories()
    busCategories.value = categories
  } catch (error) {
    console.error('❌ Error loading bus categories:', error)
    handleError.api(error, 'tải danh sách loại xe bus')
  }
}

// Load existing price rules when editing a route
const loadExistingPriceRules = async (routeId) => {
  if (!routeId) return
  
  try {
    const routePrices = await PriceAPI.findAllPrices()
    const filteredPrices = routePrices.filter(price => 
      price.route && String(price.route.id) === String(routeId)
    )
    
    if (filteredPrices.length > 0) {
      // Map existing prices to form
      filteredPrices.forEach(price => {
        if (price.busCategory && price.busCategory.id) {
          const categoryId = String(price.busCategory.id)
          // Add category to selected categories if not already selected
          if (!form.selectedCategories.includes(categoryId)) {
            form.selectedCategories.push(categoryId)
          }
          
          // Initialize price rule for this category
          form.priceRules[categoryId] = {
            basePrice: price.basePrice || 0,
            promotionPrice: price.promotionPrice || 0,
            validFrom: price.validFrom || '',
            validTo: price.validTo || '',
            notes: price.notes || ''
          }
        }
      })
    }
  } catch (error) {
    console.warn('⚠️ [RouteModal] Price loading failed, continuing without price data')
  }
}

// Load existing price rules from provided data
const loadExistingPriceRulesFromData = async (routeId, priceData) => {
  if (!routeId) return
  
  try {
    const filteredPrices = priceData.filter(price => 
      price.route && String(price.route.id) === String(routeId)
    )
    
    if (filteredPrices.length > 0) {
      // Map existing prices to form
      filteredPrices.forEach(price => {
        if (price.busCategory && price.busCategory.id) {
          const categoryId = String(price.busCategory.id)
          // Add category to selected categories if not already selected
          if (!form.selectedCategories.includes(categoryId)) {
            form.selectedCategories.push(categoryId)
          }
          
          // Initialize price rule for this category
          form.priceRules[categoryId] = {
            basePrice: price.basePrice || 0,
            promotionPrice: price.promotionPrice || 0,
            validFrom: price.validFrom || '',
            validTo: price.validTo || '',
            notes: price.notes || ''
          }
        }
      })
    }
  } catch (error) {
    console.warn('⚠️ [RouteModal] Price loading from data failed, continuing without price data')
  }
}

// Validation (UPDATED for Location objects)
const validateForm = () => {
  let isValid = true
  
  // Reset errors
  Object.keys(errors).forEach(key => {
    errors[key] = ''
  })
  
  // Origin validation
  if (!form.originLocation.name.trim()) {
    errors.originName = 'Tên điểm đi là bắt buộc'
    isValid = false
  }
  
  if (!form.originLocation.provinceCity) {
    errors.originProvince = 'Tỉnh/thành phố điểm đi là bắt buộc'
    isValid = false
  }
  
  if (!form.originLocation.district) {
    errors.originDistrict = 'Quận/huyện điểm đi là bắt buộc'
    isValid = false
  }
  
  // Destination validation
  if (!form.destinationLocation.name.trim()) {
    errors.destinationName = 'Tên điểm đến là bắt buộc'
    isValid = false
  }
  
  if (!form.destinationLocation.provinceCity) {
    errors.destinationProvince = 'Tỉnh/thành phố điểm đến là bắt buộc'
    isValid = false
  }
  
  if (!form.destinationLocation.district) {
    errors.destinationDistrict = 'Quận/huyện điểm đến là bắt buộc'
    isValid = false
  }
  
  // Check if origin and destination are the same
  if (form.originLocation.name.trim() && form.destinationLocation.name.trim() && 
      form.originLocation.provinceCity === form.destinationLocation.provinceCity &&
      form.originLocation.name.trim().toLowerCase() === form.destinationLocation.name.trim().toLowerCase()) {
    errors.destinationName = 'Điểm đến phải khác điểm đi'
    isValid = false
  }
  
  // Other validations (unchanged)
  if (!form.distanceKm || form.distanceKm <= 0) {
    errors.distanceKm = 'Khoảng cách phải lớn hơn 0'
    isValid = false
  }
  
  if (estimatedDurationMinutes.value <= 0) {
    errors.estimatedMinutes = 'Thời gian di chuyển phải lớn hơn 0'
    isValid = false
  }
  
  // Price validation
  if (form.selectedCategories.length === 0) {
    errors.selectedCategories = 'Vui lòng chọn ít nhất một loại xe'
    isValid = false
  }
  
  // Validate price rules for each selected category
  form.selectedCategories.forEach(categoryId => {
    const priceRule = form.priceRules[categoryId]
    if (!priceRule) {
      errors.selectedCategories = `Thiếu thông tin giá cho loại xe ${getCategoryName(categoryId)}`
      isValid = false
      return
    }
    
    if (!priceRule.basePrice || priceRule.basePrice <= 0) {
      errors.selectedCategories = `Giá cơ bản cho ${getCategoryName(categoryId)} phải lớn hơn 0`
    isValid = false
      return
  }
  
    if (!priceRule.validFrom) {
      errors.selectedCategories = `Ngày bắt đầu cho ${getCategoryName(categoryId)} là bắt buộc`
    isValid = false
      return
  }
  
    if (!priceRule.validTo) {
      errors.selectedCategories = `Ngày kết thúc cho ${getCategoryName(categoryId)} là bắt buộc`
    isValid = false
      return
  }
  })
  
  if (form.validFrom && form.validTo && form.validFrom > form.validTo) {
    errors.validTo = 'Ngày kết thúc phải sau ngày bắt đầu'
    isValid = false
  }
  
  return isValid
}

// Reset form (UPDATED for Location objects)
const resetForm = () => {
  Object.assign(form, {
    originLocation: {
      name: '',
      provinceCity: '',
      district: '',
      addressDetails: ''
    },
    destinationLocation: {
      name: '',
      provinceCity: '',
      district: '',
      addressDetails: ''
    },
    distanceKm: null,
    estimatedHours: null,
    estimatedMinutes: 0,
    selectedCategories: [],
    priceRules: {},
    basePrice: null,
    promotionPrice: null,
    validFrom: today,
    validTo: new Date(Date.now() + 365 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
    notes: ''
  })
  
  Object.keys(errors).forEach(key => {
    errors[key] = ''
  })
  
  // Reset district and ward lists
  originDistricts.value = []
  destinationDistricts.value = []
  originWards.value = []
  destinationWards.value = []
  existingCategoryIds.value = []
}

// Open modal for create
const openForCreate = () => {
  resetForm()
  isEditing.value = false
  editingRouteId.value = null
  isOpen.value = true
  
  nextTick(() => {
    const firstInput = document.querySelector('#originName')
    if (firstInput) {
      firstInput.focus()
    }
  })
}

// Open modal for edit (UPDATED for Location objects)
const openForEdit = async (route, existingPriceData = null) => {
  resetForm()
  isEditing.value = true
  editingRouteId.value = route.id
  
  // Fill basic location data (WITHOUT district first)
  form.originLocation.name = route.originLocation.name
  form.originLocation.provinceCity = route.originLocation.provinceCity || ''
  form.originLocation.addressDetails = route.originLocation.addressDetails || ''
  
  form.destinationLocation.name = route.destinationLocation.name
  form.destinationLocation.provinceCity = route.destinationLocation.provinceCity || ''
  form.destinationLocation.addressDetails = route.destinationLocation.addressDetails || ''
  
  // Load districts FIRST (this will populate the dropdowns)
  if (form.originLocation.provinceCity) {
    try {
      originDistricts.value = await ProvinceAPI.getDistrictsByProvince(form.originLocation.provinceCity)
    } catch (error) {
      console.error('❌ Error loading origin districts:', error)
      originDistricts.value = []
    }
  }
  if (form.destinationLocation.provinceCity) {
    try {
      destinationDistricts.value = await ProvinceAPI.getDistrictsByProvince(form.destinationLocation.provinceCity)
    } catch (error) {
      console.error('❌ Error loading destination districts:', error)
      destinationDistricts.value = []
    }
  }
  
  // ✅ THEN set district values (after districts are loaded)
  form.originLocation.district = route.originLocation.district || ''
  form.destinationLocation.district = route.destinationLocation.district || ''
  form.originLocation.ward = route.originLocation.ward || ''
  form.destinationLocation.ward = route.destinationLocation.ward || ''
  
  // Load wards if districts are set
  if (form.originLocation.district) {
    try {
      originWards.value = await ProvinceAPI.getWardsByDistrict(form.originLocation.provinceCity, form.originLocation.district)
    } catch (error) {
      console.error('❌ Error loading origin wards:', error)
    }
  }
  
  if (form.destinationLocation.district) {
    try {
      destinationWards.value = await ProvinceAPI.getWardsByDistrict(form.destinationLocation.provinceCity, form.destinationLocation.district)
    } catch (error) {
      console.error('❌ Error loading destination wards:', error)
    }
  }
  
  // Fill route data
  form.distanceKm = route.distanceKm
  
  if (route.estimatedDurationMinutes) {
    form.estimatedHours = Math.floor(route.estimatedDurationMinutes / 60)
    form.estimatedMinutes = route.estimatedDurationMinutes % 60
  }
  
  // Load existing price rules (use provided data if available)
  if (existingPriceData) {
    await loadExistingPriceRulesFromData(route.id, existingPriceData)
  } else {
  await loadExistingPriceRules(route.id)
  }
  
  isOpen.value = true
  
  nextTick(() => {
    const firstInput = document.querySelector('#originName')
    if (firstInput) {
      firstInput.focus()
    }
  })
}

// Close modal
const closeModal = () => {
  isOpen.value = false
  setTimeout(() => {
    resetForm()
    isEditing.value = false
    editingRouteId.value = null
  }, 300)
}

// Create price rule for each selected category
const createPriceRule = async (routeId, categoryId) => {
  const priceRule = form.priceRules[categoryId]
  if (!priceRule) {
    throw new Error(`No price rule found for category ${categoryId}`)
  }
  
  const priceData = {
    routeId: routeId,
    busCategoryId: categoryId,
    basePrice: priceRule.basePrice,
    promotionPrice: priceRule.promotionPrice || null,
    validFrom: priceRule.validFrom,
    validTo: priceRule.validTo,
    notes: priceRule.notes || null
  }
  
  try {
    return await PriceAPI.createPrice(priceData)
  } catch (error) {
    console.error(`❌ Error creating price rule for category ${categoryId}:`, error)
    throw error
  }
}

// Submit form (UPDATED for Location objects)
const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }
  
  isSubmitting.value = true
  
  try {
    const routeData = {
      originLocationDetails: {
        name: form.originLocation.name.trim(),
        provinceCity: form.originLocation.provinceCity,
        district: form.originLocation.district || null,
        addressDetails: form.originLocation.addressDetails || null
      },
      destinationLocationDetails: {
        name: form.destinationLocation.name.trim(),
        provinceCity: form.destinationLocation.provinceCity,
        district: form.destinationLocation.district || null,
        addressDetails: form.destinationLocation.addressDetails || null
      },
      distanceKm: form.distanceKm,
      estimatedDurationMinutes: estimatedDurationMinutes.value
    }
    
    let route
    
    if (isEditing.value) {
      // Update route
      route = await RouteAPI.updateRoute(editingRouteId.value, routeData)
      
      // Create price rules only for newly selected categories
      const newCategoryIds = form.selectedCategories.filter(
        categoryId => !existingCategoryIds.value.includes(categoryId)
      )
      
      if (newCategoryIds.length > 0) {
        try {
        const pricePromises = newCategoryIds.map(categoryId => 
          createPriceRule(route.id, categoryId)
        )
        
        await Promise.all(pricePromises)
        } catch (priceError) {
          console.warn('⚠️ [RouteModal] Price creation failed for update, but route was saved')
          toast.warning('Route đã được cập nhật thành công, nhưng không thể tạo quy tắc giá. Vui lòng thiết lập giá riêng.')
        }
      }
      
      toast.updated('tuyến đường')
      emit('route-updated', route)
    } else {
      // Create route
      route = await RouteAPI.createRoute(routeData)
      
      // Create price rules for all selected categories
      if (form.selectedCategories.length > 0) {
        try {
      const pricePromises = form.selectedCategories.map(categoryId => 
        createPriceRule(route.id, categoryId)
      )
      
      await Promise.all(pricePromises)
        } catch (priceError) {
          console.warn('⚠️ [RouteModal] Price creation failed for new route, but route was created')
          toast.warning('Route đã được tạo thành công, nhưng không thể tạo quy tắc giá. Vui lòng thiết lập giá riêng.')
        }
      }
      
      toast.created('tuyến đường')
      emit('route-created', route)
    }
    
    closeModal()
    
  } catch (error) {
    console.error('❌ Error saving route:', error)
    handleError.api(error, isEditing.value ? 'cập nhật tuyến đường' : 'tạo tuyến đường')
  } finally {
    isSubmitting.value = false
  }
}

// Helper function to get category name by ID
const getCategoryName = (categoryId) => {
  const category = busCategories.value.find(cat => cat.id === categoryId)
  return category ? category.name : `Loại xe ${categoryId}`
}

// Helper function to format price range for a category
const formatPriceRange = (categoryId) => {
  const priceRule = form.priceRules[categoryId]
  if (!priceRule) return 'Chưa thiết lập'
  
  const basePrice = priceRule.basePrice || 0
  const promotionPrice = priceRule.promotionPrice || 0
  
  if (promotionPrice > 0 && promotionPrice < basePrice) {
    return `${promotionPrice.toLocaleString('vi-VN')} VND (khuyến mãi từ ${basePrice.toLocaleString('vi-VN')} VND)`
  } else {
    return `${basePrice.toLocaleString('vi-VN')} VND`
  }
}

// Watch for selected categories changes
watch(() => form.selectedCategories, (newCategories) => {
  // Remove price rules for unselected categories
  Object.keys(form.priceRules).forEach(categoryId => {
    if (!newCategories.includes(categoryId)) {
      delete form.priceRules[categoryId]
    }
  })
  
  // Initialize price rules for newly selected categories
  newCategories.forEach(categoryId => {
    if (!form.priceRules[categoryId]) {
      form.priceRules[categoryId] = {
        basePrice: 0,
        promotionPrice: 0,
        validFrom: today,
        validTo: new Date(Date.now() + 365 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
        notes: ''
      }
    }
  })
}, { deep: true })

// Export methods
defineExpose({
  openForCreate,
  openForEdit
})
</script> 