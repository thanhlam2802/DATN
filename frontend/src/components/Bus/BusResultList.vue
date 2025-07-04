<script setup>
const props = defineProps({
  results: {
    type: Array,
    required: true
  },
  busType: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['select-bus'])

// Methods
const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price)
}

const selectBus = (bus) => {
  emit('select-bus', bus)
}

// Mock additional bus details cho API dễ dàng hơn
const getBusDetails = (bus) => {
  return {
    rating: (4.0 + Math.random() * 1).toFixed(1),
    totalReviews: Math.floor(Math.random() * 500) + 100,
    licensePlate: `${Math.floor(Math.random() * 90) + 10}${String.fromCharCode(65 + Math.floor(Math.random() * 26))}-${Math.floor(Math.random() * 900) + 100}.${Math.floor(Math.random() * 90) + 10}`,
    discount: Math.random() > 0.7 ? Math.floor(Math.random() * 20) + 5 : null,
    lastUpdated: '5 phút trước'
  }
}

// Format currency without symbol
const formatPriceShort = (price) => {
  return new Intl.NumberFormat('vi-VN').format(price)
}
</script>

<template>
  
</template>

<style scoped>

</style> 