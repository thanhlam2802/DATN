<template>
    <transition name="fade">
        <div v-if="show && hotel"
            class="fixed inset-0 z-[60] flex items-center justify-center px-4 sm:px-0 pt-12 bg-slate-900/50 backdrop-blur-sm"
            @click.self="$emit('close')">
            <div
                class="relative bg-white rounded-xl shadow-xl w-full max-w-3xl max-h-[85vh] overflow-hidden flex flex-col border border-slate-300">
                <div class="flex justify-between items-center border-b border-slate-200 px-6 py-4">
                    <h2 class="text-xl font-bold text-slate-800">{{ hotel.name }}</h2>
                    <button @click="$emit('close')"
                        class="text-slate-500 hover:text-slate-700 p-2 rounded-full hover:bg-slate-100 transition-colors">✕</button>
                </div>

                <div class="p-6 space-y-6 overflow-y-auto flex-1">
                    <section class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div>
                            <h3 class="text-sm font-semibold text-slate-500 uppercase mb-1">Thông tin chung</h3>
                            <div class="space-y-2 text-slate-700">
                                <p><strong class="font-medium">Loại hình:</strong> {{ hotel.type }}</p>
                                <p><strong class="font-medium">Hạng sao:</strong> {{ hotel.star }} <i class="fas fa-star text-yellow-400"></i></p>
                                <p><strong class="font-medium">Địa chỉ:</strong> {{ hotel.address }}, {{ hotel.district || '' }}, {{ hotel.city }}</p>
                                <p v-if="hotel.postalCode"><strong class="font-medium">Mã bưu chính:</strong> {{ hotel.postalCode }}</p>
                                <p><strong class="font-medium">Giá từ:</strong> <span class="font-semibold text-blue-600">{{ formatCurrency(hotel.minPrice) }} VND</span></p>
                                <p><strong class="font-medium">Đánh giá:</strong> {{ hotel.rating }}/5 ({{ hotel.reviews }} lượt)</p>
                            </div>
                        </div>
                        <div>
                            <h3 class="text-sm font-semibold text-slate-500 uppercase mb-1">Mô tả</h3>
                            <p class="text-slate-700 leading-relaxed text-sm whitespace-pre-line">{{ hotel.description || 'Chưa có mô tả.' }}</p>
                        </div>
                    </section>

                    <section v-if="hotel.rooms && hotel.rooms.length > 0">
                        <h3 class="text-lg font-semibold text-slate-800 mb-3 border-b pb-2">Các loại phòng</h3>
                        <div class="space-y-4">
                            <div v-for="(room, index) in hotel.rooms" :key="index" class="p-4 border border-slate-200 rounded-lg bg-slate-50/50">
                                <h4 class="font-semibold text-md text-slate-700 mb-2">{{ room.type }}</h4>
                                <div class="grid grid-cols-2 sm:grid-cols-3 gap-x-4 gap-y-1 text-sm text-slate-600">
                                    <p><strong class="font-medium">Sức chứa:</strong> {{ room.capacity }} người</p>
                                    <p><strong class="font-medium">Loại giường:</strong> {{ room.bedType }}</p>
                                    <p><strong class="font-medium">Số lượng:</strong> {{ room.quantity }} phòng</p>
                                    <p><strong class="font-medium">Giá gốc:</strong> {{ formatCurrency(room.price) }} VND</p>
                                    <p v-if="room.discount > 0"><strong class="font-medium">Giảm giá:</strong> {{ room.discount }}%</p>
                                    <p class="font-semibold text-blue-700"><strong class="font-medium text-slate-600">Giá cuối:</strong> {{ formatCurrency(room.price * (1 - (room.discount || 0) / 100)) }} VND</p>
                                </div>
                            </div>
                        </div>
                    </section>

                    <section v-if="hotel.amenities">
                        <h3 class="text-lg font-semibold text-slate-800 mb-3 border-b pb-2">Tiện nghi</h3>
                        <ul class="grid grid-cols-2 sm:grid-cols-3 gap-x-4 gap-y-2 text-sm text-slate-700">
                            <li v-for="(label, key) in amenityLabels" :key="key" class="flex items-center">
                                <i :class="hotel.amenities[key] ? 'fas fa-check-circle text-green-500' : 'fas fa-times-circle text-slate-400'" class="mr-2 w-4"></i>
                                {{ label }}
                            </li>
                        </ul>
                    </section>

                     <section v-if="hotel.policy">
                        <h3 class="text-lg font-semibold text-slate-800 mb-3 border-b pb-2">Chính sách</h3>
                        <div class="space-y-2 text-sm text-slate-700">
                            <p v-if="hotel.policy.checkin"><strong class="font-medium">Giờ nhận phòng:</strong> {{ hotel.policy.checkin }}</p>
                            <p v-if="hotel.policy.checkout"><strong class="font-medium">Giờ trả phòng:</strong> {{ hotel.policy.checkout }}</p>
                            <div v-if="hotel.policy.other">
                                <strong class="font-medium">Chính sách khác:</strong>
                                <p class="whitespace-pre-line pl-1">{{ hotel.policy.other }}</p>
                            </div>
                        </div>
                    </section>
                </div>

                <div class="px-6 py-4 border-t border-slate-200 bg-slate-50 text-right">
                    <button @click="$emit('close')"
                        class="bg-slate-600 hover:bg-slate-700 text-white px-4 py-2 rounded-md text-sm font-medium shadow-sm">
                        Đóng
                    </button>
                </div>
            </div>
        </div>
    </transition>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
    show: Boolean,
    hotel: Object,
    amenityLabels: Object
});

defineEmits(['close']);

const formatCurrency = (value) => {
    return value == null ? 'N/A' : new Intl.NumberFormat('vi-VN').format(value);
};
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
    transition: opacity .3s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}
.whitespace-pre-line {
    white-space: pre-line;
}
</style>
