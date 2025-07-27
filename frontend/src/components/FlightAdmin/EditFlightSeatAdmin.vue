<template>
  <div class="fixed inset-0 bg-black/30 flex items-center justify-center z-50">
    <div class="relative w-full max-w-md">
      <div class="absolute -top-20 -left-20 w-72 h-72 bg-sky-300 rounded-full blur-3xl opacity-60 -z-10"></div>
      <div class="bg-white rounded-2xl shadow-2xl p-8 border border-sky-100 relative">
        <h3 class="text-2xl font-bold mb-6 text-gray-900 flex items-center gap-2">
          <i class="fa-solid fa-chair text-sky-400"></i>Chỉnh sửa vé ghế
        </h3>
        <form @submit.prevent="save">
          <div class="mb-5">
            <label class="block text-base font-semibold mb-2 text-gray-700">Số ghế</label>
            <input v-model="localSeat.seatNumber" type="text" class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition" />
          </div>
          <div class="mb-5">
            <label class="block text-base font-semibold mb-2 text-gray-700">Loại vé</label>
            <select v-model="localSeat.isBusiness" class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition">
              <option :value="true">Thương gia</option>
              <option :value="false">Phổ thông</option>
            </select>
          </div>
          <div class="mb-5">
            <label class="block text-base font-semibold mb-2 text-gray-700">Vị trí</label>
            <select v-model="localSeat.position" class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition">
              <option value="window">Cửa sổ</option>
              <option value="aisle">Lối đi</option>
              <option value="middle">Giữa</option>
            </select>
          </div>
          <div class="mb-5">
            <label class="block text-base font-semibold mb-2 text-gray-700">Giá</label>
            <input v-model.number="localSeat.price" type="number" class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition" />
          </div>
          <div class="mb-5">
            <label class="block text-base font-semibold mb-2 text-gray-700">Hành lý (kg)</label>
            <input v-model.number="localSeat.carryOnLuggage" type="number" class="w-full border border-gray-300 rounded-xl px-4 py-3 bg-gray-50 focus:ring-2 focus:ring-sky-300 focus:border-sky-400 transition" />
          </div>
          <div class="flex justify-end gap-3 mt-8">
            <button type="button" @click="$emit('close')" class="px-5 py-2 bg-gray-200 rounded-xl hover:bg-gray-300 font-semibold transition">Hủy</button>
          <button type="submit" class="px-6 py-2 bg-sky-500 text-white rounded-xl hover:bg-sky-600 font-semibold shadow transition" :disabled="loading">
            <span v-if="loading">
              <i class="fa fa-spinner fa-spin"></i>
            </span>
            <span v-else>Lưu</span>
          </button>
          </div>
        </form>
        <button @click="$emit('close')" class="absolute top-3 right-3 text-gray-400 hover:text-red-500 text-xl"><i class="fa fa-times"></i></button>
      </div>
    </div>
  </div>
</template>
<script setup>
import { reactive, watch, toRefs, ref } from 'vue'
import { useRoute } from 'vue-router'
import { updateAdminSeat, deleteAdminSeat } from '@/api/flightApi'
const props = defineProps({ seat: Object })
const emit = defineEmits(['close', 'saved'])
const localSeat = reactive({
  id: '',
  seatNumber: '',
  isBusiness: false,
  position: 'window',
  price: 0,
  carryOnLuggage: 0
})
const loading = ref(false)
const route = useRoute()
watch(() => props.seat, (val) => {
  if (val) {
    localSeat.id = val.id
    localSeat.seatNumber = val.seatNumber
    localSeat.isBusiness = val.isBusiness
    localSeat.position = val.isWindow ? 'window' : val.isAisle ? 'aisle' : 'middle'
    localSeat.price = val.price
    localSeat.carryOnLuggage = val.carryOnLuggage
  }
}, { immediate: true })
async function save() {
  loading.value = true;
  try {
    const payload = {
      id: localSeat.id,
      seatNumber: localSeat.seatNumber,
      isBusiness: localSeat.isBusiness,
      isWindow: localSeat.position === 'window',
      isAisle: localSeat.position === 'aisle',
      price: localSeat.price,
      carryOnLuggage: localSeat.carryOnLuggage
    } 
    const res = await updateAdminSeat( localSeat.id, payload)
    emit('saved', res.data)
    window.$toast('Cập nhật ghế thành công!', 'success');
  } catch (e) {
    const message = e?.response?.data?.message || e.message || 'Lỗi không xác định'
    window.$toast('Cập nhật ghế thất bại!\n' + message, 'error');
  } finally {
    loading.value = false;
  }
}
</script> 