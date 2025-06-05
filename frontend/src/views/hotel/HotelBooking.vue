<template>
    <main class="w-full max-w-none px-6 pt-0 flex flex-col lg:flex-row gap-10">
        <section class="flex-1 w-full">
            <h1 class="text-2xl font-semibold mb-8">Xác nhận và thanh toán</h1>

            <div class="mb-8">
                <h2 class="font-semibold mb-3">Chuyến đi của bạn</h2>
                <div class="flex flex-col sm:flex-row gap-4">
                    <div
                        class="flex justify-between items-center bg-gray-100 rounded-lg px-4 py-3 text-sm text-gray-700 w-full sm:w-1/2">
                        <div>
                            <div class="font-semibold mb-0.5">Ngày</div>
                            <div>{{ formattedCheckin }} - {{ formattedCheckout }}</div>
                        </div>
                        <button aria-label="Chỉnh sửa ngày" class="text-gray-400 hover:text-gray-600">
                            <i class="fas fa-pencil-alt"></i>
                        </button>
                    </div>
                    <div
                        class="flex justify-between items-center bg-gray-100 rounded-lg px-4 py-3 text-sm text-gray-700 w-full sm:w-1/2">
                        <div>
                            <div class="font-semibold mb-0.5">Số khách</div>
                            <div>{{ guests }} khách</div>
                        </div>
                        <button aria-label="Chỉnh sửa số khách" class="text-gray-400 hover:text-gray-600">
                            <i class="fas fa-pencil-alt"></i>
                        </button>
                    </div>
                </div>
            </div>

            <div class="mb-8">
                <h2 class="font-semibold mb-3">Thông tin khách hàng</h2>
                <form class="grid gap-4 md:grid-cols-1 w-full">
                    <input type="text" placeholder="Họ và tên"
                        class="form-input w-full border border-gray-300 p-3 text-base mb-2 rounded-md" />
                    <input type="email" placeholder="Địa chỉ email"
                        class="form-input w-full border border-gray-300 p-3 text-base mb-2 rounded-md" />
                    <input type="tel" placeholder="Số điện thoại"
                        class="form-input w-full border border-gray-300 p-3 text-base mb-2 rounded-md" />
                </form>
            </div>
            <div class="mb-8">
                <button @click="toggleVoucher"
                    class="w-full text-left text-indigo-600 font-semibold border border-indigo-600 rounded-md px-4 py-3 hover:bg-indigo-50 focus:outline-none focus:ring-2 focus:ring-indigo-500 flex justify-between items-center"
                    :aria-expanded="isVoucherVisible.toString()">
                    <span>{{ isVoucherVisible ? 'Ẩn mã khuyến mãi' : 'Chọn voucher' }}</span>
                    <svg class="w-5 h-5 transition-transform duration-300" :class="{ 'rotate-180': isVoucherVisible }"
                        fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7"></path>
                    </svg>
                </button>
                <div class="mt-4 border border-gray-300 rounded-md p-4 bg-gray-50"
                    :class="{ hidden: !isVoucherVisible }" :aria-hidden="(!isVoucherVisible).toString()">
                    <input id="promoInput" type="text" placeholder="Enter promo code"
                        class="w-full border border-gray-300 rounded-md px-4 py-2 text-gray-700 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-400 mb-4"
                        autocomplete="off" />
                    <div id="promoList" class="flex space-x-4 overflow-x-auto pb-4 scrollbar-hide scroll-smooth">
                        <label
                            class="voucher-label min-w-[220px] flex-shrink-0 bg-white p-3 rounded-lg shadow cursor-pointer border border-gray-300 hover:border-indigo-600 flex items-start space-x-3">
                            <input type="radio" name="voucher" class="form-radio text-indigo-600 mt-1"
                                value="summer-sale" />
                            <div class="flex-grow">
                                <div
                                    class="voucher-badge ml-20 bg-amber-400 text-white px-2 py-0.5 rounded-full text-xs font-bold shadow-md whitespace-nowrap">
                                    Only a few left!
                                </div>
                                <h3 class="font-bold text-sm text-gray-900 mb-1">Summer Sale</h3>
                                <p class="text-xs text-gray-600">10% off all bookings</p>
                                <p class="text-xs text-gray-600">Valid until Aug 31, 2024</p>
                            </div>
                        </label>
                        <label
                            class="voucher-label min-w-[220px] flex-shrink-0 bg-white p-3 rounded-lg shadow cursor-pointer border border-gray-300 hover:border-indigo-600 flex items-start space-x-3">
                            <input type="radio" name="voucher" class="form-radio text-indigo-600 mt-1"
                                value="new-user" />
                            <div class="flex-grow">
                                <div
                                    class="voucher-badge ml-20 bg-amber-400 text-white px-2 py-0.5 rounded-full text-xs font-bold shadow-md whitespace-nowrap">
                                    Limited time!
                                </div>
                                <h3 class="font-bold text-sm text-gray-900 mb-1">New User Discount</h3>
                                <p class="text-xs text-gray-600">$87 off your first booking</p>
                                <p class="text-xs text-gray-600">Valid for new customers only</p>
                            </div>
                        </label>
                        <label
                            class="voucher-label min-w-[220px] flex-shrink-0 bg-white p-3 rounded-lg shadow cursor-pointer border border-gray-300 hover:border-indigo-600 flex items-start space-x-3">
                            <input type="radio" name="voucher" class="form-radio text-indigo-600 mt-1"
                                value="holiday-special" />
                            <div class="flex-grow">
                                <div
                                    class="voucher-badge ml-30 bg-amber-400 text-white px-2 py-0.5 rounded-full text-xs font-bold shadow-md whitespace-nowrap">
                                    Popular
                                </div>
                                <h3 class="font-bold text-sm text-gray-900 mb-1">Holiday Special</h3>
                                <p class="text-xs text-gray-600">Free service fee on bookings</p>
                                <p class="text-xs text-gray-600">Valid Dec 1 - Dec 31, 2024</p>
                            </div>
                        </label>
                    </div>
                </div>
            </div>
            <div>
                <h2 class="font-semibold mb-3">Thanh toán bằng</h2>
                <div class="flex items-center space-x-3 mb-6 text-sm font-normal text-gray-700">
                    <button class="bg-pink-500 text-white rounded-full px-4 py-1.5">Thẻ tín dụng</button>
                    <button class="bg-gray-200 rounded-full px-4 py-1.5 hover:bg-gray-300">Paypal</button>
                    <button class="bg-gray-200 rounded-full px-4 py-1.5 hover:bg-gray-300">Google Pay</button>
                </div>
                <form @submit.prevent="onSubmitPayment" class="space-y-4">
                    <input type="text" placeholder="Số thẻ"
                        class="form-input text-gray-700 w-full border border-gray-300 p-3 text-base rounded-md" />
                    <input type="text" placeholder="Tên chủ thẻ"
                        class="form-input text-gray-700 w-full border border-gray-300 p-3 text-base rounded-md" />
                    <div class="flex flex-col sm:flex-row gap-4">
                        <input type="text" placeholder="Ngày hết hạn"
                            class="form-input flex-1 text-gray-700 border border-gray-300 p-3 text-base rounded-md" />
                        <input type="text" placeholder="CVV"
                            class="form-input flex-1 text-gray-700 border border-gray-300 p-3 text-base rounded-md" />
                    </div>
                    <label class="inline-flex items-center space-x-2 text-sm text-gray-900">
                        <input type="checkbox" checked class="w-4 h-4 text-indigo-600 border-gray-300 rounded" />
                        <span>Lưu thẻ cho lần đặt sau</span>
                    </label>
                    <hr class="border-gray-200 my-6" />
                    <p class="text-xs text-gray-600">
                        Bằng cách nhấn vào nút bên dưới, tôi đồng ý với
                        <a class="underline" href="#">Nội quy chỗ nghỉ</a>,
                        <a class="underline" href="#">Điều khoản & Điều kiện</a>,
                        <a class="underline" href="#">Chính sách bảo mật</a> và
                        <a class="underline" href="#">Yêu cầu an toàn COVID-19.</a>
                    </p>
                    <button type="submit"
                        class="w-full mt-6 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold rounded-lg py-3 text-base">
                        Xác nhận và thanh toán
                    </button>
                </form>
            </div>
        </section>

        <aside class="bg-white border border-gray-100 rounded-xl p-6 w-full max-w-md shadow-sm">
            <div class="flex gap-4 mb-4">
                <img :src="hotelImage" :alt="hotelTitle" class="rounded-lg w-30 h-22 object-cover flex-shrink-0"
                    width="120" height="90" />
                <div class="flex flex-col justify-between">
                    <div class="text-xs text-gray-500 mb-0.5">{{ hotelSubtitle }}</div>
                    <div class="font-semibold text-gray-900 text-base leading-tight">{{ hotelTitle }}</div>
                    <div class="text-xs text-gray-500 mt-1">{{ hotelDetails }}</div>
                    <div class="flex items-center text-sm mt-2 text-gray-600">
                        <i class="fas fa-star text-indigo-600 mr-1"></i>
                        <span class="font-semibold text-gray-900">{{ hotelRating }}</span>
                        <span class="ml-1">({{ hotelReviews }} đánh giá)</span>
                    </div>
                </div>
            </div>

            <div>
                <h3 class="font-bold text-lg mb-3">Chi tiết giá</h3>
                <dl class="text-sm text-gray-900 space-y-2">
                    <div class="flex justify-between">
                        <dt>{{ formatPrice(hotelPricePerNight) }} x {{ nights }} đêm</dt>
                        <dd>{{ formatPrice(subtotal) }}</dd>
                    </div>
                    <div class="flex justify-between text-green-600 font-semibold">
                        <dt>Giảm giá người dùng mới</dt>
                        <dd>-{{ formatPrice(discount) }}</dd>
                    </div>
                    <div class="flex justify-between items-center text-gray-600">
                        <dt>Phí dịch vụ</dt>
                        <dd>
                            {{ formatPrice(serviceFee) }}
                            <button aria-label="Thông tin phí dịch vụ" class="text-gray-400 hover:text-gray-600">
                                <i class="fas fa-info-circle"></i>
                            </button>
                        </dd>
                    </div>
                    <div class="flex justify-between font-bold text-gray-900 border-t border-gray-200 pt-2">
                        <dt>Tổng cộng (VND)</dt>
                        <dd>{{ formatPrice(total) }}</dd>
                    </div>
                </dl>
                <p class="text-xs text-gray-500 mt-3 text-center">
                    Miễn phí huỷ đến 15:00 ngày 15 tháng 7 năm 2022.
                    <a class="underline" href="#">Tìm hiểu thêm</a>
                </p>
            </div>
        </aside>
    </main>
</template>

<script>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

export default {
    name: 'HotelBooking',
    setup() {
        const route = useRoute()
        const router = useRouter()

        const checkin = route.query.checkin
        const checkout = route.query.checkout
        const guests = route.query.guests
        const total = parseFloat(route.query.total)
        const nights = parseInt(route.query.nights)
        const subtotal = parseFloat(route.query.subtotal)
        const discount = parseFloat(route.query.discount)
        const serviceFee = parseFloat(route.query.serviceFee)
        const hotelTitle = route.query.hotelTitle
        const hotelSubtitle = route.query.hotelSubtitle
        const hotelRating = route.query.hotelRating
        const hotelReviews = route.query.hotelReviews
        const hotelImage = route.query.hotelImage
        const hotelDetails = route.query.hotelDetails
        const hotelPricePerNight = parseFloat(route.query.hotelPricePerNight)

        const isVoucherVisible = ref(false)

        const toggleVoucher = () => {
            isVoucherVisible.value = !isVoucherVisible.value
        }

        const formatPrice = (val) => {
            if (typeof val === 'string') val = Number(val)
            return val.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })
        }

        const formattedCheckin = computed(() => {
            if (!checkin) return ''
            return new Date(checkin).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
        })
        const formattedCheckout = computed(() => {
            if (!checkout) return ''
            return new Date(checkout).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
        })

        const reservationCode = Math.random()
            .toString(36)
            .substring(2, 12)
            .toUpperCase()

        const onSubmitPayment = () => {
            router.push({
                name: 'BookingSuccess',
                query: {
                    checkin,
                    checkout,
                    guests,
                    amountPaid: total,
                    reservationCode,
                    hotelImage,
                    hotelTitle,
                    hotelSubtitle,
                    hotelDetails,
                    hotelRating,
                    hotelReviews,
                    hotelPricePerNight,
                    hotelAddress: '1234 Higashiasakusa, Taito City, Tokyo, Japan',
                },
            })
        }

        return {
            checkin,
            checkout,
            guests,
            total,
            nights,
            subtotal,
            discount,
            serviceFee,
            hotelTitle,
            hotelSubtitle,
            hotelRating,
            hotelReviews,
            hotelImage,
            hotelDetails,
            hotelPricePerNight,
            formatPrice,
            formattedCheckin,
            formattedCheckout,
            isVoucherVisible,
            toggleVoucher,
            onSubmitPayment,
        }
    },
}
</script>

<style scoped></style>