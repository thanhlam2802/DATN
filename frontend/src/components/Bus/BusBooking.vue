<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  selectedBus: {
    type: Object,
    required: true
  },
  busType: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['back'])

// Form data
const customerInfo = ref({
  fullName: '',
  phone: '',
  email: '',
  idCard: '',
  notes: ''
})

const paymentInfo = ref({
  method: 'credit-card',
  cardNumber: '',
  cardName: '',
  expiryDate: '',
  cvv: ''
})

// State
const currentStep = ref(1) // 1: Customer Info, 2: Payment, 3: Confirmation

// Lấy thông tin ghế đã chọn hoặc mặc định
const selectedSeats = computed(() => {
  return props.selectedBus.selectedSeats || ['A1'] // Mock selected seat nếu không có ghế được chọn
})

// Methods
const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price)
}

const calculateTotal = () => {
  // Sử dụng totalPrice từ chọn ghế nếu có, không thì tính theo giá gốc
  if (props.selectedBus.totalPrice) {
    return props.selectedBus.totalPrice + 25000 // Phí dịch vụ
  }
  
  const subtotal = props.selectedBus.price * selectedSeats.value.length
  const serviceFee = 25000
  return subtotal + serviceFee
}

const nextStep = () => {
  if (currentStep.value < 3) {
    currentStep.value++
  }
}

const prevStep = () => {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}

const confirmBooking = () => {
  // Simulate booking confirmation
  alert('Đặt vé thành công! Mã vé: BUS' + Date.now())
  emit('back')
}

const goBack = () => {
  emit('back')
}
</script>

<template>
  <div class="p-4 sm:p-6 lg:p-8 space-y-4 sm:space-y-6">
    <!-- Enhanced Header với responsive -->
    <div class="flex flex-col space-y-3 sm:space-y-0 sm:flex-row sm:items-center sm:justify-between">
      <button
        @click="goBack"
        class="flex items-center text-gray-600 hover:text-gray-800 transition-colors text-sm sm:text-base"
      >
        <i class="fas fa-arrow-left mr-2"></i>
        <span class="hidden sm:inline">Quay lại kết quả tìm kiếm</span>
        <span class="sm:hidden">Quay lại</span>
      </button>
      
      <!-- Enhanced Step Progress với responsive -->
      <div class="flex items-center justify-center sm:justify-end">
        <div class="flex items-center space-x-2 sm:space-x-4">
          <div
            v-for="step in 3"
            :key="step"
            class="flex items-center"
          >
            <div
              class="w-6 h-6 sm:w-8 sm:h-8 rounded-full flex items-center justify-center text-xs sm:text-sm font-medium transition-all duration-300"
              :class="step <= currentStep 
                ? 'bg-purple-600 text-white scale-110' 
                : 'bg-gray-200 text-gray-500'"
            >
              {{ step }}
            </div>
            <div v-if="step < 3" class="w-8 sm:w-12 h-0.5 mx-1 sm:mx-2 bg-gray-200"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Main Content với responsive grid và FIXED HEIGHT -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 lg:gap-8">
      <!-- Left Column - Customer Info & Payment với FIXED HEIGHT -->
      <div class="lg:col-span-2">
        <!-- Fixed container để giữ kích thước cố định -->
        <div class="bg-gray-50 rounded-lg sm:rounded-xl p-4 sm:p-6 min-h-[600px] sm:min-h-[700px] flex flex-col">
          
          <!-- Step 1: Customer Information -->
          <div v-if="currentStep === 1" class="flex-1 flex flex-col">
            <h2 class="text-lg sm:text-2xl font-bold text-gray-800 mb-4 sm:mb-6">
              <i class="fas fa-user mr-2 text-purple-600"></i>
              Thông tin khách hàng
            </h2>
            
            <div class="flex-1 space-y-4 sm:space-y-6">
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 sm:gap-6">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    Họ và tên *
                  </label>
                  <input
                    v-model="customerInfo.fullName"
                    type="text"
                    class="w-full px-3 sm:px-4 py-3 sm:py-4 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all text-sm sm:text-base"
                    placeholder="Nguyễn Văn A"
                  />
                </div>
                
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    Số điện thoại *
                  </label>
                  <input
                    v-model="customerInfo.phone"
                    type="tel"
                    class="w-full px-3 sm:px-4 py-3 sm:py-4 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all text-sm sm:text-base"
                    placeholder="0901234567"
                  />
                </div>
                
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    Email *
                  </label>
                  <input
                    v-model="customerInfo.email"
                    type="email"
                    class="w-full px-3 sm:px-4 py-3 sm:py-4 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all text-sm sm:text-base"
                    placeholder="example@email.com"
                  />
                </div>
                
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    CMND/CCCD *
                  </label>
                  <input
                    v-model="customerInfo.idCard"
                    type="text"
                    class="w-full px-3 sm:px-4 py-3 sm:py-4 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all text-sm sm:text-base"
                    placeholder="123456789"
                  />
                </div>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  Ghi chú (tùy chọn)
                </label>
                <textarea
                  v-model="customerInfo.notes"
                  rows="4"
                  class="w-full px-3 sm:px-4 py-3 sm:py-4 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all text-sm sm:text-base"
                  placeholder="Yêu cầu đặc biệt..."
                ></textarea>
              </div>
            </div>
            
            <!-- Fixed bottom button -->
            <div class="flex justify-end mt-auto pt-6">
              <button
                @click="nextStep"
                class="w-full sm:w-auto px-6 sm:px-8 py-3 sm:py-4 bg-purple-600 hover:bg-purple-700 text-white font-semibold rounded-lg transition-all duration-200 text-sm sm:text-base min-w-[120px]"
              >
                <span>Tiếp tục</span>
                <i class="fas fa-arrow-right ml-2"></i>
              </button>
            </div>
          </div>

          <!-- Step 2: Payment Information -->
          <div v-if="currentStep === 2" class="flex-1 flex flex-col">
            <h2 class="text-lg sm:text-2xl font-bold text-gray-800 mb-4 sm:mb-6">
              <i class="fas fa-credit-card mr-2 text-purple-600"></i>
              Thông tin thanh toán
            </h2>
            
            <div class="flex-1 space-y-4 sm:space-y-6">
              <!-- Payment Methods với responsive -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-3">
                  Phương thức thanh toán
                </label>
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 sm:gap-4">
                  <label class="flex items-center p-4 sm:p-5 border-2 rounded-lg cursor-pointer transition-all hover:bg-gray-50"
                         :class="paymentInfo.method === 'credit-card' ? 'border-purple-500 bg-purple-50' : 'border-gray-200'">
                    <input v-model="paymentInfo.method" type="radio" value="credit-card" class="sr-only">
                    <i class="fas fa-credit-card text-lg sm:text-xl mr-3 text-purple-600"></i>
                    <div>
                      <div class="font-medium text-sm sm:text-base">Thẻ tín dụng</div>
                      <div class="text-xs sm:text-sm text-gray-500">Visa, MasterCard</div>
                    </div>
                  </label>
                  
                  <label class="flex items-center p-4 sm:p-5 border-2 rounded-lg cursor-pointer transition-all hover:bg-gray-50"
                         :class="paymentInfo.method === 'momo' ? 'border-purple-500 bg-purple-50' : 'border-gray-200'">
                    <input v-model="paymentInfo.method" type="radio" value="momo" class="sr-only">
                    <i class="fas fa-mobile-alt text-lg sm:text-xl mr-3 text-pink-600"></i>
                    <div>
                      <div class="font-medium text-sm sm:text-base">Ví MoMo</div>
                      <div class="text-xs sm:text-sm text-gray-500">Thanh toán nhanh</div>
                    </div>
                  </label>
                </div>
              </div>
              
              <!-- Credit Card Form (hiển thị khi chọn thẻ tín dụng) -->
              <div v-if="paymentInfo.method === 'credit-card'" class="space-y-4">
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div class="sm:col-span-2">
                    <label class="block text-sm font-medium text-gray-700 mb-2">
                      Số thẻ *
                    </label>
                    <input
                      v-model="paymentInfo.cardNumber"
                      type="text"
                      class="w-full px-3 sm:px-4 py-3 sm:py-4 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all text-sm sm:text-base"
                      placeholder="1234 5678 9012 3456"
                    />
                  </div>
                  
                  <div class="sm:col-span-2">
                    <label class="block text-sm font-medium text-gray-700 mb-2">
                      Tên chủ thẻ *
                    </label>
                    <input
                      v-model="paymentInfo.cardName"
                      type="text"
                      class="w-full px-3 sm:px-4 py-3 sm:py-4 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all text-sm sm:text-base"
                      placeholder="NGUYEN VAN A"
                    />
                  </div>
                  
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">
                      Ngày hết hạn *
                    </label>
                    <input
                      v-model="paymentInfo.expiryDate"
                      type="text"
                      class="w-full px-3 sm:px-4 py-3 sm:py-4 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all text-sm sm:text-base"
                      placeholder="MM/YY"
                    />
                  </div>
                  
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">
                      CVV *
                    </label>
                    <input
                      v-model="paymentInfo.cvv"
                      type="text"
                      class="w-full px-3 sm:px-4 py-3 sm:py-4 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all text-sm sm:text-base"
                      placeholder="123"
                    />
                  </div>
                </div>
              </div>
              
              <!-- MoMo Info (hiển thị khi chọn MoMo) -->
              <div v-if="paymentInfo.method === 'momo'" class="bg-pink-50 border border-pink-200 rounded-lg p-4 sm:p-6">
                <div class="flex items-center mb-3">
                  <i class="fas fa-mobile-alt text-pink-600 text-lg mr-3"></i>
                  <span class="font-medium text-gray-800">Thanh toán qua ví MoMo</span>
                </div>
                <p class="text-sm text-gray-600 mb-4">
                  Bạn sẽ được chuyển hướng đến ứng dụng MoMo để hoàn tất thanh toán.
                </p>
                <div class="flex items-center text-sm text-pink-600">
                  <i class="fas fa-shield-alt mr-2"></i>
                  <span>Bảo mật cao - Thanh toán nhanh chóng</span>
                </div>
              </div>
            </div>
            
            <!-- Fixed bottom buttons -->
            <div class="flex flex-col sm:flex-row justify-between mt-auto pt-6 space-y-3 sm:space-y-0 sm:space-x-4">
              <button
                @click="prevStep"
                class="w-full sm:w-auto px-6 sm:px-8 py-3 sm:py-4 border border-gray-300 text-gray-700 font-semibold rounded-lg hover:bg-gray-50 transition-all duration-200 text-sm sm:text-base min-w-[120px]"
              >
                <i class="fas fa-arrow-left mr-2"></i>
                Quay lại
              </button>
              <button
                @click="nextStep"
                class="w-full sm:w-auto px-6 sm:px-8 py-3 sm:py-4 bg-purple-600 hover:bg-purple-700 text-white font-semibold rounded-lg transition-all duration-200 text-sm sm:text-base min-w-[120px]"
              >
                Tiếp tục
                <i class="fas fa-arrow-right ml-2"></i>
              </button>
            </div>
          </div>

          <!-- Step 3: Confirmation -->
          <div v-if="currentStep === 3" class="flex-1 flex flex-col">
            <h2 class="text-lg sm:text-2xl font-bold text-gray-800 mb-4 sm:mb-6">
              <i class="fas fa-check-circle mr-2 text-green-600"></i>
              Xác nhận thông tin
            </h2>
            
            <div class="flex-1 space-y-4 sm:space-y-6">
              <!-- Customer Info Summary -->
              <div class="bg-white rounded-lg p-4 sm:p-6">
                <h3 class="font-semibold text-gray-800 mb-4 text-sm sm:text-base flex items-center">
                  <i class="fas fa-user mr-2 text-purple-600"></i>
                  Thông tin khách hàng
                </h3>
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 sm:gap-4 text-sm">
                  <div class="flex justify-between sm:block">
                    <span class="text-gray-600">Họ tên:</span>
                    <span class="ml-2 font-medium">{{ customerInfo.fullName || 'Chưa nhập' }}</span>
                  </div>
                  <div class="flex justify-between sm:block">
                    <span class="text-gray-600">Điện thoại:</span>
                    <span class="ml-2 font-medium">{{ customerInfo.phone || 'Chưa nhập' }}</span>
                  </div>
                  <div class="flex justify-between sm:block">
                    <span class="text-gray-600">Email:</span>
                    <span class="ml-2 font-medium">{{ customerInfo.email || 'Chưa nhập' }}</span>
                  </div>
                  <div class="flex justify-between sm:block">
                    <span class="text-gray-600">CMND/CCCD:</span>
                    <span class="ml-2 font-medium">{{ customerInfo.idCard || 'Chưa nhập' }}</span>
                  </div>
                </div>
                <div v-if="customerInfo.notes" class="mt-4 pt-4 border-t border-gray-200">
                  <span class="text-gray-600 text-sm">Ghi chú:</span>
                  <p class="mt-1 text-sm font-medium">{{ customerInfo.notes }}</p>
                </div>
              </div>
              
              <!-- Payment Info Summary -->
              <div class="bg-white rounded-lg p-4 sm:p-6">
                <h3 class="font-semibold text-gray-800 mb-4 text-sm sm:text-base flex items-center">
                  <i class="fas fa-credit-card mr-2 text-purple-600"></i>
                  Thông tin thanh toán
                </h3>
                <div class="text-sm">
                  <div class="flex items-center">
                    <i :class="paymentInfo.method === 'credit-card' ? 'fas fa-credit-card text-purple-600' : 'fas fa-mobile-alt text-pink-600'" class="mr-2"></i>
                    <span class="font-medium">
                      {{ paymentInfo.method === 'credit-card' ? 'Thẻ tín dụng' : 'Ví MoMo' }}
                    </span>
                  </div>
                  <div v-if="paymentInfo.method === 'credit-card' && paymentInfo.cardNumber" class="mt-2 text-gray-600">
                    **** **** **** {{ paymentInfo.cardNumber.slice(-4) }}
                  </div>
                </div>
              </div>
              
              <!-- Final Notice -->
              <div class="bg-green-50 border border-green-200 rounded-lg p-4 sm:p-6">
                <div class="flex items-start">
                  <i class="fas fa-check-circle text-green-600 mt-0.5 mr-3 flex-shrink-0"></i>
                  <div class="text-sm text-green-800">
                    <p class="font-medium mb-2">Sẵn sàng đặt vé!</p>
                    <p>Vui lòng kiểm tra lại thông tin trước khi xác nhận. Sau khi đặt vé thành công, bạn sẽ nhận được mã vé qua email và SMS.</p>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Fixed bottom buttons -->
            <div class="flex flex-col sm:flex-row justify-between mt-auto pt-6 space-y-3 sm:space-y-0 sm:space-x-4">
              <button
                @click="prevStep"
                class="w-full sm:w-auto px-6 sm:px-8 py-3 sm:py-4 border border-gray-300 text-gray-700 font-semibold rounded-lg hover:bg-gray-50 transition-all duration-200 text-sm sm:text-base min-w-[120px]"
              >
                <i class="fas fa-arrow-left mr-2"></i>
                Quay lại
              </button>
              <button
                @click="confirmBooking"
                class="w-full sm:w-auto px-6 sm:px-8 py-3 sm:py-4 bg-green-600 hover:bg-green-700 text-white font-semibold rounded-lg transition-all duration-200 text-sm sm:text-base min-w-[140px]"
              >
                <i class="fas fa-check mr-2"></i>
                Xác nhận đặt vé
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Column - Booking Summary với enhanced responsive -->
      <div class="lg:col-span-1">
        <div class="sticky top-4 bg-white rounded-lg sm:rounded-xl shadow-lg border border-gray-200 p-4 sm:p-6">
          <h3 class="text-lg sm:text-xl font-bold text-gray-800 mb-4 sm:mb-6 flex items-center">
            <i class="fas fa-receipt mr-2 text-purple-600"></i>
            Chi tiết đặt vé
          </h3>
          
          <!-- Bus Info -->
          <div class="mb-4 sm:mb-6 p-3 sm:p-4 bg-gray-50 rounded-lg">
            <h4 class="font-semibold text-gray-800 mb-2 text-sm sm:text-base">{{ selectedBus.company }}</h4>
            <div class="space-y-1 text-xs sm:text-sm text-gray-600">
              <div class="flex justify-between">
                <span>Tuyến:</span>
                <span class="font-medium">{{ selectedBus.route }}</span>
              </div>
              <div class="flex justify-between">
                <span>Giờ khởi hành:</span>
                <span class="font-medium">{{ selectedBus.departureTime }}</span>
              </div>
              <div class="flex justify-between">
                <span>Loại xe:</span>
                <span class="font-medium">{{ selectedBus.busType }}</span>
              </div>
            </div>
          </div>
          
          <!-- Selected Seats (nếu có) -->
          <div v-if="selectedSeats.length > 0" class="mb-4 sm:mb-6">
            <h4 class="font-semibold text-gray-800 mb-2 text-sm sm:text-base">Ghế đã chọn</h4>
            <div class="flex flex-wrap gap-2">
              <span
                v-for="seat in selectedSeats"
                :key="seat"
                class="px-2 py-1 bg-purple-100 text-purple-800 rounded-full text-xs font-medium"
              >
                {{ seat }}
              </span>
            </div>
          </div>
          
          <!-- Price Breakdown -->
          <div class="space-y-3 text-sm">
            <div class="flex justify-between">
              <span>Giá vé ({{ selectedSeats.length }} ghế):</span>
              <span>{{ formatPrice(props.selectedBus.totalPrice || props.selectedBus.price * selectedSeats.length) }}</span>
            </div>
            <div class="flex justify-between">
              <span>Phí dịch vụ:</span>
              <span>{{ formatPrice(25000) }}</span>
            </div>
            <div class="border-t pt-3 flex justify-between items-center">
              <span class="font-bold text-base sm:text-lg">Tổng cộng:</span>
              <span class="font-bold text-lg sm:text-xl text-purple-600">{{ formatPrice(calculateTotal()) }}</span>
            </div>
          </div>
          
          <!-- Notes -->
          <div class="mt-4 sm:mt-6 p-3 bg-amber-50 border border-amber-200 rounded-lg">
            <div class="flex items-start">
              <i class="fas fa-info-circle text-amber-600 mt-0.5 mr-2 flex-shrink-0"></i>
              <div class="text-xs sm:text-sm text-amber-800">
                <p class="font-medium mb-1">Lưu ý:</p>
                <ul class="space-y-1">
                  <li>• Vui lòng có mặt trước 30 phút</li>
                  <li>• Mang theo CMND/CCCD gốc</li>
                  <li>• Hoàn vé trước 2h: 70% giá vé</li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Fixed height container để tránh layout shift */
.step-container {
  min-height: 600px;
}

@media (min-width: 640px) {
  .step-container {
    min-height: 700px;
  }
}

/* Smooth transitions cho step changes */
.step-content {
  transition: opacity 0.3s ease-in-out;
}

/* Enhanced form styling */
input, textarea, select {
  transition: all 0.2s ease-in-out;
}

input:focus, textarea:focus, select:focus {
  box-shadow: 0 0 0 3px rgba(147, 51, 234, 0.1);
  transform: translateY(-1px);
}

/* Button hover effects */
button {
  transition: all 0.2s ease-in-out;
}

button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

button:active {
  transform: translateY(0);
}

/* Mobile responsive adjustments */
@media (max-width: 639px) {
  .grid {
    gap: 1rem;
  }
  
  .space-y-4 > * + * {
    margin-top: 1rem;
  }
}
</style> 