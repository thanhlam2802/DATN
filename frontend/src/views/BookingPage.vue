<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

// Props từ route params hoặc query
const selectedBus = ref(null)
const busType = ref('sleeping-bus')

// Form data theo thực tế VN
const customerInfo = ref({
  fullName: '',
  phone: '',
  idCard: '',
  notes: ''
})

const paymentInfo = ref({
  method: 'vnpay', // Mặc định VNPay
  bankCode: '', // Cho thanh toán ngân hàng
  phoneNumber: '' // Cho ví điện tử
})

// State
const currentStep = ref(1) // 1: Thông tin khách hàng, 2: Thanh toán, 3: Xác nhận
const isLoading = ref(false)

// Vietnamese banks
const vietnameseBanks = [
  { code: 'BIDV', name: 'Ngân hàng BIDV', logo: '🏦' },
  { code: 'VCB', name: 'Vietcombank', logo: '🏛️' },
  { code: 'TCB', name: 'Techcombank', logo: '💳' },
  { code: 'MB', name: 'MBBank', logo: '🏪' },
  { code: 'VTB', name: 'VietinBank', logo: '🏦' },
  { code: 'ACB', name: 'ACB', logo: '💰' },
  { code: 'TPB', name: 'TPBank', logo: '🏛️' },
  { code: 'STB', name: 'Sacombank', logo: '💳' }
]

// Payment methods theo thực tế VN
const paymentMethods = [
  {
    id: 'vnpay',
    name: 'VNPay',
    description: 'Thanh toán qua cổng VNPay',
    icon: '💳',
    popular: true
  },
  {
    id: 'banking',
    name: 'Chuyển khoản ngân hàng',
    description: 'Chuyển khoản qua Internet Banking',
    icon: '🏦',
    popular: true
  },
  {
    id: 'momo',
    name: 'Ví MoMo',
    description: 'Thanh toán qua ví điện tử MoMo',
    icon: '📱',
    popular: false
  },
  {
    id: 'zalopay',
    name: 'ZaloPay',
    description: 'Thanh toán qua ví ZaloPay',
    icon: '💙',
    popular: false
  },
  {
    id: 'cash',
    name: 'Thanh toán tại quầy',
    description: 'Thanh toán tiền mặt khi lên xe',
    icon: '💵',
    popular: false
  }
]

// Lifecycle
onMounted(() => {
  // Get bus data from route state hoặc localStorage
  const busData = history.state?.selectedBus || JSON.parse(localStorage.getItem('selectedBus') || '{}')
  if (busData && busData.id) {
    selectedBus.value = busData
    busType.value = history.state?.busType || localStorage.getItem('busType') || 'sleeping-bus'
  } else {
    // Redirect về trang bus nếu không có data
    router.push('/bus')
  }
})

// Computed
const selectedSeats = computed(() => {
  return selectedBus.value?.selectedSeats || ['A11'] // Mock seat
})

const totalPrice = computed(() => {
  if (!selectedBus.value) return 0
  const basePrice = selectedBus.value.totalPrice || (selectedBus.value.price * selectedSeats.value.length)
  const serviceFee = 15000 // Phí dịch vụ thực tế VN
  return basePrice + serviceFee
})

// Methods
const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price)
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

const validateStep = (step) => {
  if (step === 1) {
    return customerInfo.value.fullName && customerInfo.value.phone && customerInfo.value.idCard
  }
  if (step === 2) {
    if (paymentInfo.value.method === 'banking') {
      return paymentInfo.value.bankCode
    }
    if (['momo', 'zalopay'].includes(paymentInfo.value.method)) {
      return paymentInfo.value.phoneNumber
    }
    return true
  }
  return true
}

const handleConfirmBooking = async () => {
  isLoading.value = true
  
  try {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // Generate booking code
    const bookingCode = 'BUS' + Date.now().toString().slice(-8)
    
    alert(`🎉 Đặt vé thành công!\n\nMã đặt vé: ${bookingCode}\nVui lòng lưu lại mã này để tra cứu.\n\nThông tin thanh toán sẽ được gửi qua SMS.`)
    
    // Redirect về trang chủ
    router.push('/bus')
  } catch (error) {
    alert('Có lỗi xảy ra. Vui lòng thử lại!')
  } finally {
    isLoading.value = false
  }
}

const goBack = () => {
  router.back()
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 via-purple-50 to-indigo-50">
    <div class="container mx-auto px-4 py-6 max-w-6xl">
      <!-- Enhanced Header -->
      <div class="mb-6">
        <div class="flex items-center justify-between mb-4">
          <button @click="goBack" class="flex items-center text-gray-600 hover:text-gray-800 transition-colors">
            <i class="fas fa-arrow-left mr-2"></i>
            <span>Quay lại</span>
          </button>
          
          <div class="text-center">
            <h1 class="text-2xl md:text-3xl font-bold text-gray-800">Đặt Vé Xe Bus</h1>
            <p class="text-gray-600 text-sm mt-1">Hoàn tất thông tin để đặt vé</p>
          </div>
          
          <div class="w-20"></div> <!-- Spacer -->
        </div>

        <!-- Progress Steps -->
        <div class="flex items-center justify-center mb-8">
          <div class="flex items-center space-x-4">
            <div
              v-for="step in 3"
              :key="step"
              class="flex items-center"
            >
              <div
                class="w-10 h-10 rounded-full flex items-center justify-center text-sm font-bold transition-all duration-300"
                :class="step <= currentStep 
                  ? 'bg-gradient-to-r from-purple-600 to-blue-600 text-white shadow-lg scale-110' 
                  : 'bg-gray-200 text-gray-500'"
              >
                <i v-if="step < currentStep" class="fas fa-check"></i>
                <span v-else>{{ step }}</span>
              </div>
              <div v-if="step < 3" class="w-16 h-1 mx-2 rounded-full bg-gray-200">
                <div 
                  class="h-full rounded-full bg-gradient-to-r from-purple-600 to-blue-600 transition-all duration-500"
                  :class="step < currentStep ? 'w-full' : 'w-0'"
                ></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Step Labels -->
        <div class="grid grid-cols-3 gap-4 text-center text-sm text-gray-600 mb-8">
          <div :class="currentStep >= 1 ? 'text-purple-600 font-semibold' : ''">
            <i class="fas fa-user mb-1 block"></i>
            Thông tin khách hàng
          </div>
          <div :class="currentStep >= 2 ? 'text-purple-600 font-semibold' : ''">
            <i class="fas fa-credit-card mb-1 block"></i>
            Phương thức thanh toán
          </div>
          <div :class="currentStep >= 3 ? 'text-purple-600 font-semibold' : ''">
            <i class="fas fa-check-circle mb-1 block"></i>
            Xác nhận đặt vé
          </div>
        </div>
      </div>

      <!-- Main Content -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Left: Form Steps -->
        <div class="lg:col-span-2">
          <div class="bg-white rounded-2xl shadow-xl border border-gray-100 overflow-hidden">
            
            <!-- Step 1: Customer Information -->
            <div v-if="currentStep === 1" class="p-6 md:p-8">
              <div class="flex items-center mb-6">
                <div class="w-12 h-12 bg-gradient-to-r from-purple-600 to-blue-600 rounded-full flex items-center justify-center mr-4">
                  <i class="fas fa-user text-white text-lg"></i>
                </div>
                <div>
                  <h2 class="text-xl font-bold text-gray-800">Thông tin khách hàng</h2>
                  <p class="text-gray-600 text-sm">Vui lòng nhập thông tin chính xác</p>
                </div>
              </div>

              <div class="space-y-6">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <!-- Họ và tên -->
                  <div class="md:col-span-2">
                    <label class="block text-sm font-semibold text-gray-700 mb-2">
                      Họ và tên *
                    </label>
                    <div class="relative">
                      <input
                        v-model="customerInfo.fullName"
                        type="text"
                        class="w-full px-4 py-4 border border-gray-300 rounded-xl focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all text-base"
                        placeholder="Nhập họ và tên đầy đủ"
                        required
                      />
                      <i class="fas fa-user absolute right-4 top-4 text-gray-400"></i>
                    </div>
                  </div>
                  
                  <!-- Số điện thoại -->
                  <div>
                    <label class="block text-sm font-semibold text-gray-700 mb-2">
                      Số điện thoại *
                    </label>
                    <div class="relative">
                      <input
                        v-model="customerInfo.phone"
                        type="tel"
                        class="w-full px-4 py-4 border border-gray-300 rounded-xl focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all text-base"
                        placeholder="0912345678"
                        required
                      />
                      <i class="fas fa-phone absolute right-4 top-4 text-gray-400"></i>
                    </div>
                  </div>
                  
                  <!-- CMND/CCCD -->
                  <div>
                    <label class="block text-sm font-semibold text-gray-700 mb-2">
                      CMND/CCCD *
                    </label>
                    <div class="relative">
                      <input
                        v-model="customerInfo.idCard"
                        type="text"
                        class="w-full px-4 py-4 border border-gray-300 rounded-xl focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all text-base"
                        placeholder="123456789012"
                        required
                      />
                      <i class="fas fa-id-card absolute right-4 top-4 text-gray-400"></i>
                    </div>
                  </div>
                </div>
                
                <!-- Ghi chú -->
                <div>
                  <label class="block text-sm font-semibold text-gray-700 mb-2">
                    Ghi chú (tùy chọn)
                  </label>
                  <textarea
                    v-model="customerInfo.notes"
                    rows="4"
                    class="w-full px-4 py-4 border border-gray-300 rounded-xl focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all text-base"
                    placeholder="Yêu cầu đặc biệt, điểm đón/trả cụ thể..."
                  ></textarea>
                </div>

                <!-- Important Notice -->
                <div class="bg-amber-50 border-l-4 border-amber-400 p-4 rounded-r-xl">
                  <div class="flex items-start">
                    <i class="fas fa-exclamation-triangle text-amber-600 mt-1 mr-3"></i>
                    <div class="text-sm text-amber-800">
                      <p class="font-semibold mb-1">Lưu ý quan trọng:</p>
                      <ul class="space-y-1">
                        <li>• Vui lòng mang theo CMND/CCCD gốc khi lên xe</li>
                        <li>• Có mặt tại điểm đón trước 15-30 phút</li>
                        <li>• Số điện thoại sẽ được dùng để gửi thông tin vé</li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Next Button -->
              <div class="flex justify-end mt-8">
                <button
                  @click="nextStep"
                  :disabled="!validateStep(1)"
                  class="px-8 py-4 bg-gradient-to-r from-purple-600 to-blue-600 text-white font-semibold rounded-xl transition-all duration-200 transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
                >
                  Tiếp tục
                  <i class="fas fa-arrow-right ml-2"></i>
                </button>
              </div>
            </div>

            <!-- Step 2: Payment Method -->
            <div v-if="currentStep === 2" class="p-6 md:p-8">
              <div class="flex items-center mb-6">
                <div class="w-12 h-12 bg-gradient-to-r from-purple-600 to-blue-600 rounded-full flex items-center justify-center mr-4">
                  <i class="fas fa-credit-card text-white text-lg"></i>
                </div>
                <div>
                  <h2 class="text-xl font-bold text-gray-800">Phương thức thanh toán</h2>
                  <p class="text-gray-600 text-sm">Chọn cách thức thanh toán phù hợp</p>
                </div>
              </div>

              <div class="space-y-4">
                <!-- Payment Methods Grid -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div
                    v-for="method in paymentMethods"
                    :key="method.id"
                    class="relative"
                  >
                    <input
                      v-model="paymentInfo.method"
                      :value="method.id"
                      type="radio"
                      :id="method.id"
                      class="sr-only"
                    />
                    <label
                      :for="method.id"
                      class="block p-4 border-2 rounded-xl cursor-pointer transition-all hover:bg-gray-50"
                      :class="paymentInfo.method === method.id 
                        ? 'border-purple-500 bg-purple-50 ring-2 ring-purple-200' 
                        : 'border-gray-200'"
                    >
                      <div class="flex items-center space-x-3">
                        <div class="text-2xl">{{ method.icon }}</div>
                        <div class="flex-1">
                          <div class="flex items-center space-x-2">
                            <span class="font-semibold text-gray-800">{{ method.name }}</span>
                            <span v-if="method.popular" class="px-2 py-0.5 bg-red-100 text-red-600 rounded-full text-xs font-medium">
                              Phổ biến
                            </span>
                          </div>
                          <p class="text-sm text-gray-600 mt-1">{{ method.description }}</p>
                        </div>
                        <div v-if="paymentInfo.method === method.id" class="text-purple-600">
                          <i class="fas fa-check-circle text-xl"></i>
                        </div>
                      </div>
                    </label>
                  </div>
                </div>

                <!-- Banking Details -->
                <div v-if="paymentInfo.method === 'banking'" class="mt-6 p-6 bg-blue-50 rounded-xl border border-blue-200">
                  <h3 class="font-semibold text-gray-800 mb-4 flex items-center">
                    <i class="fas fa-university mr-2 text-blue-600"></i>
                    Chọn ngân hàng
                  </h3>
                  <div class="grid grid-cols-2 md:grid-cols-4 gap-3">
                    <div
                      v-for="bank in vietnameseBanks"
                      :key="bank.code"
                      class="relative"
                    >
                      <input
                        v-model="paymentInfo.bankCode"
                        :value="bank.code"
                        type="radio"
                        :id="bank.code"
                        class="sr-only"
                      />
                      <label
                        :for="bank.code"
                        class="block p-3 border-2 rounded-lg cursor-pointer transition-all hover:bg-white text-center"
                        :class="paymentInfo.bankCode === bank.code 
                          ? 'border-blue-500 bg-white ring-2 ring-blue-200' 
                          : 'border-gray-200'"
                      >
                        <div class="text-2xl mb-1">{{ bank.logo }}</div>
                        <div class="text-xs font-medium text-gray-800">{{ bank.name }}</div>
                      </label>
                    </div>
                  </div>
                </div>

                <!-- E-wallet Details -->
                <div v-if="['momo', 'zalopay'].includes(paymentInfo.method)" class="mt-6 p-6 bg-pink-50 rounded-xl border border-pink-200">
                  <h3 class="font-semibold text-gray-800 mb-4 flex items-center">
                    <i class="fas fa-mobile-alt mr-2 text-pink-600"></i>
                    Số điện thoại ví điện tử
                  </h3>
                  <input
                    v-model="paymentInfo.phoneNumber"
                    type="tel"
                    class="w-full px-4 py-3 border border-pink-300 rounded-lg focus:ring-2 focus:ring-pink-500 focus:border-transparent"
                    placeholder="Nhập số điện thoại đã đăng ký ví"
                  />
                </div>

                <!-- Cash Payment Info -->
                <div v-if="paymentInfo.method === 'cash'" class="mt-6 p-6 bg-green-50 rounded-xl border border-green-200">
                  <div class="flex items-start space-x-3">
                    <i class="fas fa-info-circle text-green-600 mt-1"></i>
                    <div class="text-sm text-green-800">
                      <p class="font-semibold mb-2">Thanh toán tiền mặt khi lên xe:</p>
                      <ul class="space-y-1">
                        <li>• Chuẩn bị đủ tiền mặt: <strong>{{ formatPrice(totalPrice) }}</strong></li>
                        <li>• Thanh toán cho nhân viên soát vé khi lên xe</li>
                        <li>• Nhận hóa đơn VAT (nếu có yêu cầu)</li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Navigation Buttons -->
              <div class="flex justify-between mt-8">
                <button
                  @click="prevStep"
                  class="px-6 py-3 border border-gray-300 text-gray-700 font-semibold rounded-xl hover:bg-gray-50 transition-all"
                >
                  <i class="fas fa-arrow-left mr-2"></i>
                  Quay lại
                </button>
                <button
                  @click="nextStep"
                  :disabled="!validateStep(2)"
                  class="px-8 py-4 bg-gradient-to-r from-purple-600 to-blue-600 text-white font-semibold rounded-xl transition-all duration-200 transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
                >
                  Tiếp tục
                  <i class="fas fa-arrow-right ml-2"></i>
                </button>
              </div>
            </div>

            <!-- Step 3: Confirmation -->
            <div v-if="currentStep === 3" class="p-6 md:p-8">
              <div class="flex items-center mb-6">
                <div class="w-12 h-12 bg-gradient-to-r from-green-500 to-green-600 rounded-full flex items-center justify-center mr-4">
                  <i class="fas fa-check-circle text-white text-lg"></i>
                </div>
                <div>
                  <h2 class="text-xl font-bold text-gray-800">Xác nhận thông tin</h2>
                  <p class="text-gray-600 text-sm">Kiểm tra lại thông tin trước khi đặt vé</p>
                </div>
              </div>

              <div class="space-y-6">
                <!-- Customer Info Summary -->
                <div class="bg-gray-50 rounded-xl p-6">
                  <h3 class="font-semibold text-gray-800 mb-4 flex items-center">
                    <i class="fas fa-user mr-2 text-purple-600"></i>
                    Thông tin khách hàng
                  </h3>
                  <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                    <div>
                      <span class="text-gray-600">Họ tên:</span>
                      <span class="ml-2 font-semibold">{{ customerInfo.fullName }}</span>
                    </div>
                    <div>
                      <span class="text-gray-600">Điện thoại:</span>
                      <span class="ml-2 font-semibold">{{ customerInfo.phone }}</span>
                    </div>
                    <div>
                      <span class="text-gray-600">CMND/CCCD:</span>
                      <span class="ml-2 font-semibold">{{ customerInfo.idCard }}</span>
                    </div>
                  </div>
                  <div v-if="customerInfo.notes" class="mt-4 pt-4 border-t border-gray-200">
                    <span class="text-gray-600 text-sm">Ghi chú:</span>
                    <p class="mt-1 text-sm font-medium">{{ customerInfo.notes }}</p>
                  </div>
                </div>

                <!-- Payment Method Summary -->
                <div class="bg-gray-50 rounded-xl p-6">
                  <h3 class="font-semibold text-gray-800 mb-4 flex items-center">
                    <i class="fas fa-credit-card mr-2 text-purple-600"></i>
                    Phương thức thanh toán
                  </h3>
                  <div class="text-sm">
                    <div class="flex items-center space-x-2">
                      <span class="text-2xl">{{ paymentMethods.find(m => m.id === paymentInfo.method)?.icon }}</span>
                      <span class="font-semibold">{{ paymentMethods.find(m => m.id === paymentInfo.method)?.name }}</span>
                    </div>
                    <div v-if="paymentInfo.method === 'banking' && paymentInfo.bankCode" class="mt-2 text-gray-600">
                      Ngân hàng: {{ vietnameseBanks.find(b => b.code === paymentInfo.bankCode)?.name }}
                    </div>
                  </div>
                </div>

                <!-- Final Notice -->
                <div class="bg-gradient-to-r from-green-50 to-blue-50 border border-green-200 rounded-xl p-6">
                  <div class="flex items-start space-x-3">
                    <i class="fas fa-check-circle text-green-600 text-xl mt-1"></i>
                    <div class="text-sm text-gray-800">
                      <p class="font-semibold mb-2">🎉 Sẵn sàng đặt vé!</p>
                      <p class="mb-2">Sau khi xác nhận, bạn sẽ nhận được:</p>
                      <ul class="space-y-1 text-gray-700">
                        <li>• SMS xác nhận với mã đặt vé</li>
                        <li>• Hướng dẫn thanh toán (nếu chưa thanh toán)</li>
                        <li>• Thông tin điểm đón và liên hệ nhà xe</li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Final Action Buttons -->
              <div class="flex justify-between mt-8">
                <button
                  @click="prevStep"
                  class="px-6 py-3 border border-gray-300 text-gray-700 font-semibold rounded-xl hover:bg-gray-50 transition-all"
                >
                  <i class="fas fa-arrow-left mr-2"></i>
                  Quay lại
                </button>
                <button
                  @click="handleConfirmBooking"
                  :disabled="isLoading"
                  class="px-8 py-4 bg-gradient-to-r from-green-500 to-green-600 text-white font-semibold rounded-xl transition-all duration-200 transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
                >
                  <span v-if="isLoading">
                    <i class="fas fa-spinner fa-spin mr-2"></i>
                    Đang xử lý...
                  </span>
                  <span v-else>
                    <i class="fas fa-check mr-2"></i>
                    Xác nhận đặt vé
                  </span>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Right: Booking Summary -->
        <div class="lg:col-span-1">
          <div class="sticky top-6">
            <div class="bg-white rounded-2xl shadow-xl border border-gray-100 p-6">
              <h3 class="text-lg font-bold text-gray-800 mb-6 flex items-center">
                <i class="fas fa-receipt mr-2 text-purple-600"></i>
                Chi tiết đặt vé
              </h3>
              
              <!-- Bus Info -->
              <div v-if="selectedBus" class="space-y-4">
                <div class="bg-gradient-to-r from-purple-50 to-blue-50 rounded-xl p-4 border border-purple-200">
                  <h4 class="font-semibold text-gray-800 mb-3">{{ selectedBus.company }}</h4>
                  <div class="space-y-2 text-sm text-gray-600">
                    <div class="flex justify-between">
                      <span>Tuyến:</span>
                      <span class="font-medium text-gray-800">{{ selectedBus.route }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span>Khởi hành:</span>
                      <span class="font-medium text-purple-600">{{ selectedBus.departureTime }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span>Loại xe:</span>
                      <span class="font-medium">{{ selectedBus.busType }}</span>
                    </div>
                  </div>
                </div>
                
                <!-- Selected Seats -->
                <div v-if="selectedSeats.length > 0">
                  <h4 class="font-semibold text-gray-800 mb-2">Ghế đã chọn</h4>
                  <div class="flex flex-wrap gap-2">
                    <span
                      v-for="seat in selectedSeats"
                      :key="seat"
                      class="px-3 py-1 bg-purple-100 text-purple-800 rounded-full text-sm font-medium"
                    >
                      {{ seat }}
                    </span>
                  </div>
                </div>
                
                <!-- Price Breakdown -->
                <div class="space-y-3 pt-4 border-t border-gray-200">
                  <div class="flex justify-between text-sm">
                    <span>Giá vé ({{ selectedSeats.length }} ghế):</span>
                    <span>{{ formatPrice(selectedBus.totalPrice || selectedBus.price * selectedSeats.length) }}</span>
                  </div>
                  <div class="flex justify-between text-sm">
                    <span>Phí dịch vụ:</span>
                    <span>{{ formatPrice(15000) }}</span>
                  </div>
                  <div class="flex justify-between items-center pt-3 border-t border-gray-200">
                    <span class="font-bold text-lg">Tổng cộng:</span>
                    <span class="font-bold text-xl text-purple-600">{{ formatPrice(totalPrice) }}</span>
                  </div>
                </div>
              </div>
              
              <!-- Support Contact -->
              <div class="mt-6 pt-6 border-t border-gray-200">
                <div class="bg-amber-50 border border-amber-200 rounded-xl p-4">
                  <h4 class="font-semibold text-gray-800 mb-2 flex items-center">
                    <i class="fas fa-headset mr-2 text-amber-600"></i>
                    Hỗ trợ 24/7
                  </h4>
                  <div class="space-y-2 text-sm text-gray-700">
                    <div class="flex items-center">
                      <i class="fas fa-phone mr-2 text-amber-600"></i>
                      <span>Hotline: 1900 1234</span>
                    </div>
                    <div class="flex items-center">
                      <i class="fab fa-facebook-messenger mr-2 text-blue-600"></i>
                      <span>Chat hỗ trợ online</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Enhanced form styling */
input:focus, textarea:focus, select:focus {
  box-shadow: 0 0 0 3px rgba(147, 51, 234, 0.1);
  transform: translateY(-1px);
}

/* Enhanced button hover effects */
button:not(:disabled):hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

button:not(:disabled):active {
  transform: translateY(0);
}

/* Loading spinner animation */
.fa-spin {
  animation: fa-spin 1s infinite linear;
}

@keyframes fa-spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Smooth transitions */
.transition-all {
  transition: all 0.3s ease;
}

/* Custom radio button styling */
input[type="radio"]:checked + label {
  animation: pulse 0.3s ease-in-out;
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.02); }
  100% { transform: scale(1); }
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .container {
    padding-left: 1rem;
    padding-right: 1rem;
  }
  
  .grid-cols-2 {
    grid-template-columns: 1fr;
  }
  
  .text-2xl {
    font-size: 1.25rem;
  }
}
</style> 