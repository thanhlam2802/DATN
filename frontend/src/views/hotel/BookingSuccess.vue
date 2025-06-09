<template>
    <main class="max-w-xl mx-auto mt-6 px-6">
        <div class="flex justify-center mb-6">
            <img alt="Minh họa hành lý với bong bóng thoại 'Sẵn sàng?'" class="w-40 h-30 object-contain" height="120"
                src="https://storage.googleapis.com/a1aa/image/d55672e4-e65a-43ca-02dd-b3f5bc4ca01f.jpg" width="160" />
        </div>
        <h1 class="text-center text-2xl font-extrabold mb-6">
            Hoàn tất, bạn sẽ đến Tokyo!
        </h1>
        <section aria-label="Chi tiết đặt phòng"
            class="bg-white border border-gray-100 rounded-lg shadow-sm p-6 text-sm text-gray-700">
            <ul class="space-y-3 mb-6">
                <li class="flex items-center space-x-2">
                    <i class="far fa-calendar-alt text-indigo-500"></i>
                    <span>{{ formattedCheckin }} – {{ formattedCheckout }}</span>
                </li>
                <li class="flex items-center space-x-2">
                    <i class="fas fa-map-marker-alt text-pink-500"></i>
                    <span>{{ hotelSubtitle }}</span>
                </li>
                <li class="flex items-center space-x-2">
                    <i class="far fa-user text-cyan-400"></i>
                    <span>{{ guests }} khách</span>
                </li>
                <li class="flex items-center space-x-2">
                    <i class="far fa-clock text-cyan-400"></i>
                    <span>Nhận phòng: sau 2PM</span>
                </li>
                <li class="flex items-center space-x-2">
                    <i class="far fa-clock text-cyan-400"></i>
                    <span>Trả phòng: 11AM</span>
                </li>
            </ul>

            <hr class="border-gray-200 mb-6" />

            <dl class="text-sm text-gray-700 mb-6">
                <div class="flex justify-between mb-2">
                    <dt>Số tiền đã thanh toán</dt>
                    <dd class="font-normal">{{ formatPrice(amountPaid) }}</dd>
                </div>
                <div class="flex justify-between mb-4">
                    <dt>Mã đặt chỗ</dt>
                    <dd class="font-semibold">{{ reservationCode }}</dd>
                </div>
            </dl>

            <div class="flex space-x-4">
                <img alt="Ảnh phòng khách sạn" class="w-40 h-24 rounded-lg object-cover flex-shrink-0" height="100"
                    :src="hotelImage" width="160" />
                <div class="flex flex-col justify-between text-xs text-gray-700">
                    <p class="text-gray-500">Phòng khách sạn</p>
                    <p class="font-semibold text-gray-900 leading-tight">{{ hotelTitle }}</p>
                    <p class="text-gray-500 mt-1">{{ hotelDetails }}</p>
                    <p class="mt-2 flex items-center space-x-1 text-indigo-900 font-semibold">
                        <i class="fas fa-star text-indigo-700"></i>
                        <span>{{ hotelRating }}</span>
                        <span class="text-gray-400 font-normal">({{ hotelReviews }} đánh giá)</span>
                    </p>
                </div>
            </div>
        </section>
    </main>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const checkin = route.query.checkin
const checkout = route.query.checkout
const guests = route.query.guests || '1'
const amountPaid = Number(route.query.amountPaid || 0)
const reservationCode = route.query.reservationCode || 'N/A'
const hotelImage = route.query.hotelImage || ''
const hotelTitle = route.query.hotelTitle || ''
const hotelSubtitle = route.query.hotelSubtitle || ''
const hotelDetails = route.query.hotelDetails || ''
const hotelRating = route.query.hotelRating || ''
const hotelReviews = route.query.hotelReviews || ''

const formattedCheckin = computed(() => {
    if (!checkin) return ''
    return new Date(checkin).toLocaleDateString('vi-VN', {
        day: 'numeric',
        month: 'long',
        year: 'numeric',
    })
})
const formattedCheckout = computed(() => {
    if (!checkout) return ''
    return new Date(checkout).toLocaleDateString('vi-VN', {
        day: 'numeric',
        month: 'long',
        year: 'numeric',
    })
})

const formatPrice = (val) => {
    if (typeof val === 'string') val = Number(val)
    return val.toLocaleString('vi-VN', {
        style: 'currency',
        currency: 'VND',
        minimumFractionDigits: 0,
    })
}
</script>

<style scoped>
img {
    transition: transform 0.3s ease;
    will-change: transform;
}

img:hover {
    transform: scale(1.05);
    cursor: pointer;
}
</style>
