<template>
    <main v-if="hotel" class="max-w-full mx-auto px-4 sm:px-6 lg:px-8 py-1">
        <section>
            <h1 class="text-lg font-semibold text-gray-900 mb-1">
                {{ hotel.title }}
            </h1>

            <div class="flex items-center space-x-1 text-sm text-gray-600 mb-6 select-none">
                <i class="fas fa-star text-blue-600"></i>
                <span class="text-blue-600 font-semibold">{{ hotel.rating }}</span>
                <span>({{ hotel.reviews }} reviews)</span>
                <span>•</span>
                <span>{{ hotel.subtitle }}</span>
            </div>

            <div class="flex flex-col md:flex-row md:space-x-4 mb-8">
                <div class="flex-1 rounded-lg overflow-hidden">
                    <img :alt="hotel.alt" class="w-full h-[450px] object-cover rounded-lg" :src="hotel.image" />
                </div>

                <div class="flex flex-col w-full max-w-xs rounded-lg overflow-hidden mt-4 md:mt-0"
                    style="height: 450px;">
                    <img v-if="hotel.additionalImages && hotel.additionalImages[0]" :src="hotel.additionalImages[0]"
                        alt="Room view 1" class="w-full object-cover"
                        style="height: calc((450px - 16px) / 3); margin-bottom: 8px;" />
                    <img v-if="hotel.additionalImages && hotel.additionalImages[1]" :src="hotel.additionalImages[1]"
                        alt="Room view 2" class="w-full object-cover"
                        style="height: calc((450px - 16px) / 3); margin-bottom: 8px;" />
                    <div v-if="hotel.additionalImages && hotel.additionalImages[2]"
                        class="relative w-full overflow-hidden" :style="{ height: `calc((450px - 16px) / 3)` }">
                        <img :src="hotel.additionalImages[2]" alt="Bathroom view" class="w-full h-full object-cover" />
                        <button aria-label="Show all photos"
                            class="absolute bottom-2 right-2 bg-white text-xs text-gray-700 border border-gray-300 rounded px-2 py-0.5 hover:bg-gray-100">
                            Show all photos
                        </button>
                    </div>
                </div>
            </div>

            <div class="flex flex-col md:flex-row md:space-x-8 mb-10">
                <div class="flex-1">
                    <div class="mb-6">
                        <h2 class="text-sm font-semibold mb-2">Overview</h2>
                        <ul class="text-xs text-gray-700 space-y-1 mb-2">
                            <li class="flex items-center space-x-1">
                                <i class="fas fa-home text-blue-600"></i>
                                <span style="font-weight: bold;">{{ hotel.details }}</span>
                            </li>
                            <li class="flex items-center space-x-1 pt-3">
                                <i class="far fa-calendar-alt text-pink-600"></i>
                                <span style="font-weight: bold;">Free cancellation for 48 hours</span>
                            </li>
                        </ul>
                        <div>
                            <p class="text-xs text-gray-700 mb-2 font-medium pt-3">
                                Sunt ut elit cupidatat do quis incididunt sint mollit culpa
                                consequat occaecat exercitati anim ad sint adipisicing nulla:
                            </p>
                            <ul class="text-xs text-gray-700 font-medium list-disc list-inside mb-2 pt-3">
                                <li>Sit reprehenderit elit incididunt</li>
                                <li>Aute aliqua anim et duis occa...</li>
                                <li v-if="showMoreOverview">Proident tempor deserunt laborum nostrud.</li>
                                <li v-if="showMoreOverview">Dolore pariatur exercitation nisi commodo.</li>
                            </ul>
                            <button @click="showMoreOverview = !showMoreOverview"
                                class="text-xs font-semibold text-gray-700 hover:underline focus:outline-none">
                                {{ showMoreOverview ? 'Show less' : 'Show more' }}
                            </button>
                        </div>
                        <hr class="border-t border-gray-200 mt-4" />
                    </div>

                    <div class="mb-6">
                        <h3 class="text-sm font-semibold mb-3">This place offers</h3>
                        <div class="grid grid-cols-2 gap-y-2 text-xs text-gray-700">
                            <div v-for="(amenity, index) in (showAllAmenities ? hotel.amenitiesList : hotel.amenitiesList.slice(0, 4))"
                                :key="index" class="flex items-center space-x-2 font-medium">
                                <i :class="amenity.icon"></i>
                                <span>{{ amenity.name }}</span>
                            </div>
                        </div>
                        <button @click="showAllAmenities = !showAllAmenities"
                            class="mt-3 text-xs border border-gray-300 rounded px-3 py-1 hover:bg-gray-100 focus:outline-none">
                            {{ showAllAmenities ? 'Show less' : 'Show all amenities' }}
                        </button>
                    </div>
                </div>

                <aside aria-label="Booking summary"
                    class="bg-white border border-gray-200 rounded-lg p-6 w-full max-w-sm shadow-sm self-start">
                    <div class="flex justify-between items-center mb-4">
                        <div class="text-sm font-semibold">
                            <span class="text-gray-900">{{ formatPrice(hotel.price) }}</span>
                            <span class="text-gray-600">/đêm</span>
                        </div>
                        <div class="text-blue-600 font-semibold flex items-center space-x-1 select-none">
                            <i class="fas fa-star"></i>
                            <span>{{ hotel.rating }}</span>
                        </div>
                    </div>
                    <form @submit.prevent="bookNow" class="text-xs text-gray-700 space-y-4">
                        <div class="grid grid-cols-2 gap-x-4">
                            <div>
                                <label class="block font-semibold mb-1" for="checkin">Ngày nhận phòng</label>
                                <input id="checkin" type="date" v-model="checkin" :min="minCheckin"
                                    class="w-full border border-gray-300 rounded px-2 py-1 text-xs text-gray-700"
                                    required />
                            </div>
                            <div>
                                <label class="block font-semibold mb-1" for="checkout">Ngày trả phòng</label>
                                <input id="checkout" type="date" v-model="checkout" :min="minCheckout"
                                    class="w-full border border-gray-300 rounded px-2 py-1 text-xs text-gray-700"
                                    required />
                            </div>
                        </div>

                        <div>
                            <label class="block font-semibold mb-1" for="guests">Số khách</label>
                            <select id="guests" v-model.number="guests"
                                class="w-full border border-gray-300 rounded px-2 py-1 text-xs text-gray-700">
                                <option v-for="n in maxGuests" :key="n" :value="n">{{ n }} khách</option>
                            </select>
                        </div>

                        <div class="flex justify-between text-xs text-gray-700">
                            <span>{{ formatPrice(hotel.price) }} x {{ nights }} đêm</span>
                            <span>{{ formatPrice(subtotal) }}</span>
                        </div>
                        <div class="flex justify-between text-xs text-green-600 font-semibold">
                            <span>Giảm giá người dùng mới</span>
                            <span>-{{ formatPrice(discount) }}</span>
                        </div>
                        <div class="flex justify-between text-xs text-gray-700">
                            <span>
                                Phí dịch vụ
                                <i class="fas fa-info-circle text-gray-400" title="Phí dịch vụ"></i>
                            </span>
                            <span>{{ formatPrice(serviceFee) }}</span>
                        </div>
                        <hr class="my-3" />
                        <div class="flex justify-between text-sm font-semibold text-gray-900">
                            <span>Tổng cộng</span>
                            <span>{{ formatPrice(total) }}</span>
                        </div>
                        <button
                            class="mt-5 w-full bg-blue-600 hover:bg-blue-700 focus:ring-2 focus:ring-blue-400 focus:outline-none rounded text-white text-sm font-semibold px-4 py-3"
                            type="submit">
                            Đặt phòng ngay
                        </button>
                    </form>
                </aside>
            </div>

            <section>
                <h2 class="text-lg font-semibold mb-6">Reviews ({{ hotel.reviews }})</h2>
                <ul class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                    <li v-for="(review, index) in (showAllReviews ? reviewsList : reviewsList.slice(0, 4))" :key="index"
                        class="p-4 rounded-lg border border-gray-200 bg-white">
                        <div class="flex items-center mb-3">
                            <img :alt="review.name" class="w-10 h-10 rounded-full" :src="review.avatar" />
                            <div class="ml-3">
                                <h3 class="text-sm font-semibold text-gray-900">{{ review.name }}</h3>
                                <div class="flex items-center text-yellow-400 text-xs">
                                    <i v-for="i in Math.floor(review.rating)" :key="i" class="fas fa-star"></i>
                                    <i v-if="review.rating % 1 >= 0.5" class="fas fa-star-half-alt"></i>
                                </div>
                            </div>
                        </div>
                        <p class="text-xs text-gray-700">
                            {{ review.comment }}
                        </p>
                    </li>
                </ul>

                <div class="text-center">
                    <button @click="showAllReviews = !showAllReviews"
                        class="text-xs font-semibold text-gray-700 hover:underline focus:outline-none">
                        {{ showAllReviews ? 'Show less' : 'Show more reviews' }}
                    </button>
                </div>
            </section>
        </section>
    </main>
    <div v-else class="text-center py-10">
        <p class="text-xl text-gray-700">Hotel not found.</p>
    </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { hotels } from '@/data/hotelData.js'
import { useRouter } from 'vue-router'

const router = useRouter()

const showMoreOverview = ref(false)
const showAllAmenities = ref(false)
const showAllReviews = ref(false)

const reviewsList = ref([
    {
        name: 'John Smith',
        avatar: 'https://randomuser.me/api/portraits/men/32.jpg',
        rating: 4.5,
        comment: 'Great place, excellent service, and very clean. Highly recommended!',
    },
    {
        name: 'Emily Johnson',
        avatar: 'https://randomuser.me/api/portraits/women/45.jpg',
        rating: 5,
        comment: 'Perfect location and super helpful host!',
    },
    {
        name: 'Michael Brown',
        avatar: 'https://randomuser.me/api/portraits/men/28.jpg',
        rating: 4,
        comment: 'Nice room, a bit noisy but manageable.',
    },
    {
        name: 'Sarah Davis',
        avatar: 'https://randomuser.me/api/portraits/women/55.jpg',
        rating: 4.5,
        comment: 'Very comfy beds and great amenities.',
    },
    {
        name: 'Tom Wilson',
        avatar: 'https://randomuser.me/api/portraits/men/52.jpg',
        rating: 3.5,
        comment: 'Could improve on cleanliness, but overall fine.',
    },
    {
        name: 'Tom Wilson',
        avatar: 'https://randomuser.me/api/portraits/men/52.jpg',
        rating: 3.5,
        comment: 'Could improve on cleanliness, but overall fine.',
    },
])


const props = defineProps({
    id: {
        type: [String, Number],
        required: true,
    },
})

const hotel = ref(null)

const findHotel = () => {
    const foundHotel = hotels.find(h => h.id === parseInt(props.id))
    if (foundHotel) {
        hotel.value = {
            ...foundHotel,
            details: '6 khách • 4 giường • 1 phòng tắm riêng',
            amenitiesList: [
                { name: 'Nhà bếp', icon: 'fas fa-utensils' },
                { name: 'Bộ sơ cứu', icon: 'fas fa-first-aid' },
                { name: 'TV', icon: 'fas fa-tv' },
                { name: 'Đồ uống miễn phí', icon: 'fas fa-wine-glass-alt' },
                { name: 'Điều hòa', icon: 'fas fa-snowflake' },
                { name: 'Thuê xe đạp', icon: 'fas fa-bicycle' },
                { name: 'Wifi', icon: 'fas fa-wifi' },
                { name: 'Hệ thống sưởi', icon: 'fas fa-thermometer-half' },
            ],
            additionalImages: [
                'https://storage.googleapis.com/a1aa/image/68c6fc18-96ef-43c2-3bae-b96409260526.jpg',
                'https://storage.googleapis.com/a1aa/image/0216c56f-c55e-4854-998f-8a6098451dd3.jpg',
                'https://storage.googleapis.com/a1aa/image/27a56a3d-1d5e-4fa7-720b-0fe8b04c4867.jpg',
            ],
            price: '273000',
        }
    } else {
        hotel.value = null
    }
}

onMounted(findHotel)
watch(() => props.id, findHotel)

const checkin = ref('')
const checkout = ref('')
const guests = ref(2)
const maxGuests = 10

const today = new Date().toISOString().split('T')[0]
const minCheckin = ref(today)
const minCheckout = ref(today)

watch(checkin, (val) => {
    if (val) {
        const nextDay = new Date(val)
        nextDay.setDate(nextDay.getDate() + 1)
        minCheckout.value = nextDay.toISOString().split('T')[0]

        if (checkout.value && checkout.value <= val) {
            checkout.value = ''
        }
    }
})

const nights = computed(() => {
    if (!checkin.value || !checkout.value) return 0
    const d1 = new Date(checkin.value)
    const d2 = new Date(checkout.value)
    const diff = (d2 - d1) / (1000 * 3600 * 24)
    return diff > 0 ? diff : 0
})

const priceNumber = computed(() => {
    if (!hotel.value) return 0
    return Number(hotel.value.price)
})

const subtotal = computed(() => {
    return priceNumber.value * nights.value
})

const discount = computed(() => {
    return subtotal.value > 0 ? 87000 : 0
})

const serviceFee = 12000

const total = computed(() => {
    return subtotal.value - discount.value + serviceFee
})

const formatPrice = (val) => {
    if (typeof val === 'string') val = Number(val)
    return val.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })
}

const bookNow = () => {
    if (!checkin.value || !checkout.value || nights.value === 0) {
        alert('Vui lòng chọn ngày nhận và trả phòng hợp lệ.')
        return
    }

    router.push({
        name: 'HotelBooking',
        params: { id: hotel.value.id },
        query: { 
            checkin: checkin.value,
            checkout: checkout.value,
            guests: guests.value,
            total: total.value,
            nights: nights.value,
            subtotal: subtotal.value,
            discount: discount.value,
            serviceFee: serviceFee,
            hotelTitle: hotel.value.title,
            hotelSubtitle: hotel.value.subtitle,
            hotelRating: hotel.value.rating,
            hotelReviews: hotel.value.reviews,
            hotelImage: hotel.value.image,
            hotelDetails: hotel.value.details,
            hotelPricePerNight: hotel.value.price
        }
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