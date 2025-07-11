<template>
    <transition name="fade">
        <div v-if="show && hotel"
            class="fixed inset-0 z-[60] flex items-center justify-center px-2 sm:px-0 pt-12 bg-slate-900/50 backdrop-blur-sm"
            @click.self="$emit('close')">
            <div
                class="relative bg-white rounded-2xl shadow-2xl w-full max-w-5xl max-h-[94vh] overflow-hidden flex flex-col border border-slate-200 mb-8">
                <div
                    class="flex justify-between items-center border-b border-slate-200 px-10 py-6 bg-gradient-to-r from-slate-50 to-slate-100">
                    <h2 class="text-2xl font-bold text-slate-800 flex items-center gap-2">
                        <i class="fas fa-hotel text-blue-500"></i> {{ hotel.name }}
                    </h2>
                    <button @click="$emit('close')"
                        class="text-slate-500 hover:text-slate-700 p-2 rounded-full hover:bg-slate-100 transition-colors">✕</button>
                </div>

                <div class="p-10 space-y-10 overflow-y-auto flex-1 bg-white">
                    <div class="grid grid-cols-1 lg:grid-cols-3 gap-10">
                        <div class="col-span-2 space-y-4">
                            <div class="flex flex-wrap gap-2 items-center">
                                <span v-if="hotel.starRating"
                                    class="inline-flex items-center px-3 py-1 rounded-full bg-yellow-50 text-yellow-700 text-xs font-semibold"><i
                                        class="fas fa-star text-yellow-400 mr-1"></i> {{ hotel.starRating }} sao</span>
                                <span v-if="hotel.provinceName"
                                    class="inline-flex items-center px-3 py-1 rounded-full bg-blue-50 text-blue-700 text-xs font-semibold"><i
                                        class="fas fa-map-marker-alt mr-1"></i> {{ hotel.provinceName }}</span>
                                <span v-if="hotel.rating"
                                    class="inline-flex items-center px-3 py-1 rounded-full bg-green-50 text-green-700 text-xs font-semibold"><i
                                        class="fas fa-star-half-alt mr-1"></i> {{ hotel.rating }}/5</span>
                                <span v-if="hotel.reviewCount"
                                    class="inline-flex items-center px-3 py-1 rounded-full bg-slate-100 text-slate-700 text-xs font-semibold"><i
                                        class="fas fa-comment-dots mr-1"></i> {{ hotel.reviewCount }} đánh giá</span>
                            </div>
                            <div class="space-y-2 text-slate-700 text-sm mt-2">
                                <div v-if="hotel.address"><i class="fas fa-location-dot mr-2 text-slate-400"></i> <span
                                        class="font-medium">Địa chỉ:</span> {{ hotel.address }}</div>
                                <div v-if="hotel.email"><i class="fas fa-envelope mr-2 text-slate-400"></i> <span
                                        class="font-medium">Email:</span> {{ hotel.email }}</div>
                                <div v-if="hotel.phone"><i class="fas fa-phone mr-2 text-slate-400"></i> <span
                                        class="font-medium">SĐT:</span> {{ hotel.phone }}</div>
                            </div>
                            <div v-if="hotel.description" class="mt-4">
                                <h3 class="text-base font-semibold text-slate-800 mb-1">Mô tả</h3>
                                <p class="text-slate-700 whitespace-pre-line text-sm"
                                    :class="{ 'line-clamp-3': !showFullDesc }">{{ hotel.description }}</p>
                                <button v-if="hotel.description && hotel.description.length > 120"
                                    @click="showFullDesc = !showFullDesc"
                                    class="mt-1 text-blue-600 hover:underline text-xs font-medium">
                                    {{ showFullDesc ? 'Ẩn bớt' : 'Xem thêm' }}
                                </button>
                            </div>
                        </div>
                        <div class="flex flex-col items-center gap-2">
                            <div v-if="hotel.imageUrls && hotel.imageUrls.length" class="grid grid-cols-3 gap-2 w-full">
                                <img v-for="(img, idx) in hotel.imageUrls.slice(0, 6)" :key="idx" :src="img"
                                    alt="hotel image" class="w-full h-24 object-cover rounded shadow" />
                            </div>
                            <div v-else
                                class="w-full h-24 flex items-center justify-center bg-slate-100 rounded text-slate-400">
                                Không có hình ảnh</div>
                        </div>
                    </div>

                    <div v-if="hotel.amenities && hotel.amenities.length"
                        class="bg-slate-50 rounded-xl p-5 border border-slate-100">
                        <h3 class="text-lg font-semibold text-slate-800 mb-2 flex items-center gap-2"><i
                                class="fas fa-concierge-bell text-blue-400"></i> Tiện nghi khách sạn</h3>
                        <ul class="flex flex-wrap gap-2 text-sm text-slate-700">
                            <li v-for="amenity in hotel.amenities" :key="amenity.id"
                                class="flex items-center bg-white rounded px-3 py-1 border border-slate-200 shadow-sm">
                                <i v-if="amenity.icon" :class="amenity.icon + ' mr-1 text-blue-500'" />
                                {{ amenity.name }}
                            </li>
                        </ul>
                    </div>

                    <div v-if="hotel.availableRooms && hotel.availableRooms.length" class="space-y-6">
                        <h3 class="text-lg font-semibold text-slate-800 mb-2 flex items-center gap-2"><i
                                class="fas fa-bed text-purple-400"></i> Các loại phòng khả dụng</h3>
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div v-for="(room, index) in hotel.availableRooms" :key="room.id"
                                class="bg-white border border-slate-200 rounded-xl shadow p-5 flex flex-col gap-2">
                                <div class="flex items-center gap-2 mb-1">
                                    <h4 class="font-semibold text-md text-slate-700">{{ room.roomType }}</h4>
                                    <span v-if="room.bedType"
                                        class="ml-2 px-2 py-0.5 rounded-full bg-slate-100 text-xs text-slate-600">{{
                                        room.bedType }}</span>
                                </div>
                                <div class="flex flex-wrap gap-2 text-xs text-slate-600">
                                    <span v-if="room.maxAdults"><i class="fas fa-user-friends mr-1"></i> {{
                                        room.maxAdults }} người lớn</span>
                                    <span v-if="room.maxChildren"><i class="fas fa-child mr-1"></i> {{ room.maxChildren
                                        }} trẻ em</span>
                                    <span v-if="room.roomQuantity"><i class="fas fa-door-open mr-1"></i> {{
                                        room.roomQuantity }} phòng</span>
                                    <span v-if="room.roomArea"><i class="fas fa-ruler-combined mr-1"></i> {{
                                        room.roomArea }} m²</span>
                                </div>
                                <div v-if="room.imageUrls && room.imageUrls.length" class="flex flex-wrap gap-2 mt-1">
                                    <img v-for="(img, idx) in room.imageUrls.slice(0, 3)" :key="idx" :src="img"
                                        alt="room image" class="w-16 h-12 object-cover rounded" />
                                </div>
                                <div v-if="room.amenities && room.amenities.length" class="flex flex-wrap gap-1 mt-1">
                                    <span v-for="amenity in room.amenities" :key="amenity.id"
                                        class="inline-flex items-center bg-slate-50 rounded px-2 py-0.5 text-xs text-slate-700 border border-slate-200">
                                        <i v-if="amenity.icon" :class="amenity.icon + ' mr-1 text-blue-400'" />
                                        {{ amenity.name }}
                                    </span>
                                </div>
                                <div v-if="room.availableVariants && room.availableVariants.length" class="mt-2">
                                    <div class="overflow-x-auto rounded-lg">
                                        <table
                                            class="w-full text-xs border border-slate-100 rounded-lg overflow-hidden">
                                            <thead>
                                                <tr class="bg-slate-100 text-slate-700">
                                                    <th class="py-2 px-3 font-semibold text-left">Tên gói</th>
                                                    <th class="py-2 px-3 font-semibold text-center">Giá</th>
                                                    <th class="py-2 px-3 font-semibold text-left">Tùy chọn</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <tr v-for="variant in room.availableVariants" :key="variant.id"
                                                    class="border-t border-slate-100 hover:bg-slate-50 transition">
                                                    <td class="py-2 px-3">{{ variant.variantName }}</td>
                                                    <td class="py-2 px-3 text-blue-700 font-bold text-center">{{
                                                        formatCurrency(variant.price + (variant.taxAndFeeAmount || 0))
                                                        }} VND</td>
                                                    <td class="py-2 px-3 space-x-1">
                                                        <span v-if="variant.hasBreakfast"
                                                            class="inline-block bg-green-50 text-green-700 px-2 py-0.5 rounded text-xs">Bữa
                                                            sáng</span>
                                                        <span v-if="variant.cancellable"
                                                            class="inline-block bg-blue-50 text-blue-700 px-2 py-0.5 rounded text-xs">Hủy
                                                            miễn phí</span>
                                                        <span v-if="variant.payAtHotel"
                                                            class="inline-block bg-purple-50 text-purple-700 px-2 py-0.5 rounded text-xs">Thanh
                                                            toán tại KS</span>
                                                    </td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="px-10 py-6 border-t border-slate-200 bg-slate-50 text-right">
                    <button @click="$emit('close')"
                        class="bg-slate-600 hover:bg-slate-700 text-white px-6 py-2 rounded-md text-base font-medium shadow-sm">
                        Đóng
                    </button>
                </div>
            </div>
        </div>
    </transition>
</template>

<script setup>
import { defineProps, defineEmits, ref } from 'vue';

const props = defineProps({
    show: Boolean,
    hotel: Object,
    amenityLabels: Object
});

defineEmits(['close']);

const showFullDesc = ref(false);

const formatCurrency = (value) => {
    if (value == null) return 'N/A';
    if (typeof value === 'object' && value.toFixed) return new Intl.NumberFormat('vi-VN').format(Number(value));
    return new Intl.NumberFormat('vi-VN').format(value);
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

.line-clamp-3 {
    display: -webkit-box;
    -webkit-line-clamp: 3;
    line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
}
</style>
