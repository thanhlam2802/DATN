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
  
</template>

<style scoped>
</style> 