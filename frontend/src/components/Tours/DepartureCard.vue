<template>
  <div
    class="rounded-lg border p-4 hover:shadow-md transition-shadow duration-200"
    :class="isPast ? 'bg-gray-50 opacity-70' : 'bg-white border-gray-200'"
  >
    <div class="flex items-start justify-between mb-4">
      <div class="flex items-center text-md font-bold text-gray-800">
        <CalendarIcon class="w-5 h-5 mr-2.5 text-gray-500" />
        {{ formatDate(departure.departureDate) }}
      </div>
      <div class="flex space-x-2" v-if="!isPast">
        <button @click="handleEdit" class="text-gray-500 hover:text-blue-600" title="Chỉnh sửa">
          <PencilIcon class="w-5 h-5" />
        </button>
        <button @click="handleDelete" class="text-gray-500 hover:text-red-600" title="Xóa">
          <Trash2Icon class="w-5 h-5" />
        </button>
      </div>
    </div>
    <div class="space-y-2.5 text-sm">
      <div class="flex justify-between items-center">
        <span class="text-gray-600">Giá người lớn:</span>
        <span class="font-semibold text-gray-900">{{ formatCurrency(departure.adultPrice) }}</span>
      </div>
      <div class="flex justify-between items-center">
        <span class="text-gray-600">Giá trẻ em:</span>
        <span class="font-semibold text-gray-900">{{ formatCurrency(departure.childPrice) }}</span>
      </div>
      <div v-if="departure.discount > 0" class="flex justify-between items-center text-green-600">
        <span class="font-medium">Giảm giá:</span>
        <span class="font-semibold">-{{ formatCurrency(departure.discount) }}</span>
      </div>
      <div class="pt-3 border-t mt-3">
        <div class="flex justify-between items-center mb-1.5">
          <span class="text-gray-600">Số chỗ đã đặt:</span>
          <span class="font-semibold text-gray-900">{{ departure.bookedSeats }}/{{ departure.seatCount }}</span>
        </div>
        <div class="w-full bg-gray-200 rounded-full h-2.5">
          <div
            class="h-2.5 rounded-full"
            :class="isPast ? 'bg-gray-400' : 'bg-blue-600'"
            :style="{ width: (departure.bookedSeats / departure.seatCount) * 100 + '%' }"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { PencilIcon, Trash2Icon, CalendarIcon } from 'lucide-vue-next';

// --- PROPS & EMITS ---
const props = defineProps({
  departure: { type: Object, required: true },
  tourId: { type: Number, required: true },
  isPast: { type: Boolean, default: false },
});

const emit = defineEmits(['edit', 'delete']);

// --- HELPER FUNCTIONS ---
const formatCurrency = (amount) => new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(amount);
const formatDate = (dateStr) => new Date(dateStr).toLocaleDateString("vi-VN", { day: '2-digit', month: '2-digit', year: 'numeric' });

// --- METHODS ---
const handleEdit = () => emit('edit', props.departure, props.tourId);
const handleDelete = () => emit('delete', props.departure.id, props.tourId);
</script>