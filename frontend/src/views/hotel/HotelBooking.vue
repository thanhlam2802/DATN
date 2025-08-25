<template>
    <div class="max-w-full lg:w-[1320px] mx-auto px-6">
        <main class="max-w-full flex flex-col lg:flex-row gap-10 items-start pt-10">
            <div class="w-full">
                <div v-if="currentStep === 1" class="w-full">
                    <h1 class="text-2xl font-bold mb-2">Đặt phòng của bạn</h1>
                    <div class="text-base text-gray-600 mb-8">
                        Hãy đảm bảo tất cả thông tin chi tiết trên trang này đã chính xác trước khi tiến hành thanh
                        toán.
                    </div>
                    <div class="mb-8 border border-gray-200 rounded-xl bg-white p-6 shadow-sm">
                        <h2 class="text-xl font-bold mb-1">Thông tin liên hệ</h2>
                        <div class="text-sm text-gray-600 mb-4">Hãy điền chính xác tất cả thông tin để đảm bảo bạn nhận
                            được
                            Phiếu xác nhận đặt phòng qua email của mình.</div>
                        <form class="grid gap-4 w-full" @submit.prevent>
                            <div>
                                <label class="block text-sm font-semibold text-gray-800 mb-1">Tên đầy đủ (theo Hộ
                                    chiếu/Thẻ căn
                                    cước công dân)</label>
                                <input type="text" v-model="fullName" @blur="validateFullName"
                                    placeholder="Nhập họ và tên"
                                    :class="['form-input w-full border p-3 text-base rounded-md', errors.fullName ? 'border-red-500' : 'border-gray-300']" />
                                <div class="text-xs text-gray-500 mt-1">Vui lòng chỉ dùng chữ cái (A-Z), không có chức
                                    danh, ký
                                    tự đặc biệt và dấu câu.</div>
                                <div v-if="errors.fullName" class="text-xs text-red-500 mt-1">{{ errors.fullName }}
                                </div>
                            </div>
                            <div class="flex flex-col md:flex-row gap-4">
                                <div class="flex-1">
                                    <label class="block text-sm font-semibold text-gray-800 mb-1">E-mail</label>
                                    <input type="email" v-model="email" @blur="validateEmail" placeholder="Nhập email"
                                        :class="['form-input w-full border p-3 text-base rounded-md', errors.email ? 'border-red-500' : 'border-gray-300']" />
                                    <div class="text-xs text-gray-500 mt-1">Chúng tôi sẽ gửi e-voucher tới email này.</div>
                                    <div v-if="errors.email" class="text-xs text-red-500 mt-1">{{ errors.email }}</div>
                                </div>
                                <div class="flex-1">
                                    <label class="block text-sm font-semibold text-gray-800 mb-1">Số điện thoại</label>
                                    <input type="tel" v-model="phone" @input="phone = phone.replace(/[^\d]/g, '')"
                                        @blur="validatePhone" placeholder="Nhập số điện thoại"
                                        :class="['form-input w-full border p-3 text-base rounded-md', errors.phone ? 'border-red-500' : 'border-gray-300']" />
                                    <div class="text-xs text-gray-500 mt-1">ví dụ. 0912345678</div>
                                    <div v-if="errors.phone" class="text-xs text-red-500 mt-1">{{ errors.phone }}</div>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="mb-8 border border-gray-200 rounded-xl bg-white p-6 shadow shadow-orange-100">
                        <h3 class="text-xl font-bold mb-2">Chi tiết giá</h3>
                        <div class="text-green-700 text-sm font-semibold mb-4">Hãy áp thêm mã giảm giá tại trang thanh
                            toán để
                            có giá tốt hơn nhé</div>
                        <div class="flex justify-between items-end mb-2">
                            <div>
                                <div class="font-semibold text-gray-700 text-base">Giá phòng</div>
                                <div class="text-xs text-gray-600 mt-0.5">({{ rooms }}x) {{ variantName }} ({{ nights }} đêm)</div>
                            </div>
                            <div class="font-semibold text-gray-700 text-right whitespace-nowrap min-w-[110px]">
                                {{ formatPrice(variantDiscountedPrice * nights * rooms) }}
                            </div>
                        </div>
                        <div class="flex justify-between items-center mb-2">
                            <div class="font-semibold text-gray-700 flex items-center gap-1">Thuế và phí
                                <i class="fas fa-info-circle text-gray-400 ml-1"
                                    title="Đã bao gồm thuế/phí theo quy định"></i>
                            </div>
                            <div class="font-semibold text-gray-700">
                                {{ formatPrice((taxAndFeeAmount || 0) * nights * rooms) }}
                            </div>
                        </div>
                        <hr class="my-2 border-gray-200" />
                        <div class="flex justify-between items-center mb-2">
                            <div class="font-bold text-lg text-gray-900">Tổng giá</div>
                            <div class="font-bold text-orange-600 text-2xl">{{ formatPrice(subtotal) }}</div>
                        </div>
                        <div class="flex items-center gap-2 text-blue-600 text-base font-semibold my-3">
                            <i class="far fa-clock"></i>
                            Bạn chưa bị trừ tiền!
                        </div>
                        <button v-if="activeCartId" @click="addRoomToOrder"
                            class="w-full mt-2 mb-2 px-6 py-3 bg-green-600 text-white rounded-lg font-bold text-lg shadow hover:bg-green-700 transition">
                            <i class="fa-solid fa-plus mr-2"></i> Thêm phòng vào chuyến đi
                        </button>
                        <button @click="goToNextStep"
                            class="w-full mt-2 mb-2 px-6 py-3 bg-orange-500 text-white rounded-lg font-bold text-lg shadow hover:bg-orange-600 transition">Tiếp
                            tục thanh toán</button>
                        <div class="text-xs text-gray-500 mt-2 text-center">
                            Bằng việc chấp nhận thanh toán, bạn đã đồng ý với
                            <a class="underline text-blue-600" href="#">Điều khoản & Điều kiện</a>,
                            <a class="underline text-blue-600" href="#">Chính sách quyền riêng tư</a>
                            và <a class="underline text-blue-600" href="#">Quy trình hoàn tiền</a> chỗ ở của chúng tôi.
                        </div>
                    </div>
                </div>
            </div>
            <aside class="bg-white border border-gray-100 rounded-xl p-6 w-full max-w-md shadow-sm mb-10">
                <div class="mb-4">
                    <div class="flex flex-col items-center text-center pb-3 border-b border-gray-200 mb-3">
                        <div class="flex items-center justify-center gap-1 mb-1 text-xs">
                            <i class="fas fa-hotel text-blue-500 fa-xs"></i>
                            <span class="font-semibold text-blue-700">Khách sạn</span>
                            <span class="flex items-center gap-0.5 ml-1">
                                <span v-for="n in starRating" :key="n" class="text-yellow-400">
                                    <i class="fas fa-star"></i>
                                </span>
                            </span>
                        </div>
                        <div class="font-extrabold text-xl text-gray-900 leading-tight mb-1">{{ hotelTitle }}</div>
                        <div class="text-xs text-gray-500 mb-1 flex items-center justify-center gap-1">
                            <i class="fas fa-map-marker-alt"></i>
                            <span>{{ hotellocation }}</span>
                        </div>
                        <div class="flex items-center justify-center gap-2 mt-1">
                            <span class="flex items-center text-sm text-gray-700">
                                <i class="fas fa-star text-indigo-600 mr-1"></i>
                                <span class="font-semibold text-gray-900">{{ Number(hotelRating).toFixed(1) }}</span>
                                <span class="ml-1">({{ hotelReviews }} đánh giá)</span>
                            </span>
                        </div>
                    </div>
                    <div class="relative w-full rounded-lg overflow-hidden mb-2" style="height: 180px;">
                        <transition :name="slideDirection === 'next' ? 'slide-right' : 'slide-left'">
                            <img :key="hotelImageIndex" :src="hotelImages[hotelImageIndex]" :alt="hotelTitle"
                                class="w-full h-full object-cover transition-all duration-300 rounded-lg" />
                        </transition>
                        <button v-if="hotelImages.length > 1" @click="prevHotelImage"
                            class="absolute left-2 top-1/2 transform -translate-y-1/2 bg-white/70 hover:bg-white/90 rounded-full p-2 z-10"><i
                                class="fas fa-chevron-left text-gray-700"></i></button>
                        <button v-if="hotelImages.length > 1" @click="nextHotelImage"
                            class="absolute right-2 top-1/2 transform -translate-y-1/2 bg-white/70 hover:bg-white/90 rounded-full p-2 z-10"><i
                                class="fas fa-chevron-right text-gray-700"></i></button>
                    </div>
                </div>

                <div class="mb-4">
                    <div class="mb-4 flex items-center justify-between gap-4">
                        <div
                            class="flex-1 flex flex-col items-center bg-gray-50 rounded-lg p-2 border border-gray-300 min-w-[120px] max-w-[160px]">
                            <span class="text-xs text-gray-500 mb-0.5">Nhận phòng</span>
                            <span class="font-bold text-sm">{{ checkinDayOfWeek }}, {{ formattedCheckin }}</span>
                            <span class="text-xs text-gray-500 mt-0.5">14:00 - 23:59</span>
                        </div>
                        <div class="flex flex-col items-center min-w-[60px]">
                            <span class="text-gray-900 font-semibold text-xs">{{ nights }} đêm</span>
                            <span class="w-15 h-0.5 bg-gray-200 my-1"></span>
                        </div>
                        <div
                            class="flex-1 flex flex-col items-center bg-gray-50 rounded-lg p-2 border border-gray-300 min-w-[120px] max-w-[160px]">
                            <span class="text-xs text-gray-500 mb-0.5">Trả phòng</span>
                            <span class="font-bold text-sm">{{ checkoutDayOfWeek }}, {{ formattedCheckout }}</span>
                            <span class="text-xs text-gray-500 mt-0.5">01:00 - 12:00</span>
                        </div>
                    </div>
                </div>

                <div class="mb-4">
                    <h3 class="font-bold text-base">({{ rooms }}x) {{ variantName }}</h3>
                    <p class="text-sm font-semibold text-gray-700">
                        <i class="fas fa-bed mr-1 mt-3"></i> {{ roomBed }} <br />
                        <i class="fas fa-utensils mr-3 mt-4"></i>{{ variantBreakfast }}
                    </p>
                    <div class="flex items-center text-sm text-gray-700 font-semibold mt-3">
                        <i class="fas fa-users mr-2"></i>
                        <span>{{ maxAdults }} người lớn<span v-if="maxChildren > 0">, {{ maxChildren }} trẻ
                                em</span></span>
                    </div>
                </div>

                <div>
                    <h3 class="font-bold text-gray-900 mb-3">Chi tiết giá</h3>
                    <div class="flex justify-between items-end mb-2">
                        <div class="max-w-[70%]">
                            <div class="font-semibold text-gray-700 text-base break-words">Giá phòng</div>
                            <div class="text-xs text-gray-600 mt-0.5 break-words">
                                ({{ rooms }}x) {{ variantName }} ({{ nights }} đêm)
                            </div>
                        </div>
                        <div class="font-semibold text-gray-700 text-right whitespace-nowrap min-w-[110px]">
                            {{ formatPrice(variantDiscountedPrice * nights * rooms) }}
                        </div>
                    </div>
                    <div class="flex justify-between items-center mb-2">
                        <div class="font-semibold text-gray-700 flex items-center gap-1">Thuế và phí
                            <i class="fas fa-info-circle text-gray-400 ml-1"
                                title="Đã bao gồm thuế/phí theo quy định"></i>
                        </div>
                        <div class="font-semibold text-gray-700">{{
                            formatPrice((taxAndFeeAmount || 0) * nights * rooms) }}</div>
                    </div>
                    <div class="flex justify-between items-center mb-2">
                        <div class="font-semibold text-gray-700 flex items-center gap-1">Phí dịch vụ
                            <i class="fas fa-info-circle text-gray-400 ml-1"
                                title="Phí xử lý, hỗ trợ khách hàng..."></i>
                        </div>
                        <div class="font-semibold text-gray-700 text-base">{{ formatPrice(serviceFee) }}</div>
                    </div>
                    <div class="flex justify-between items-center border-t border-gray-200 pt-2 mt-3">
                        <div class="font-bold text-gray-900">Tổng cộng (VND)</div>
                        <div class="font-bold text-orange-600">{{ formatPrice(subtotal) }}</div>
                    </div>
                </div>
            </aside>
        </main>
    </div>
    <template v-if="showVoucherPopup">
        <div class="fixed inset-0 z-50 flex items-center justify-center" style="background: rgba(0,0,0,0.25);"
            @click.self="showVoucherPopup = false">
            <div class="bg-white rounded-xl shadow-2xl w-full max-w-lg p-8 relative animate-fadeIn">
                <button class="absolute top-4 right-4 text-gray-400 hover:text-gray-700 text-2xl"
                    @click="showVoucherPopup = false">
                    <i class="fas fa-times"></i>
                </button>
                <div class="flex items-center gap-2 mb-2">
                    <i class="fas fa-percent text-blue-500 text-2xl"></i>
                    <span class="font-bold text-xl">Thêm mã giảm</span>
                </div>
                <div class="text-gray-500 text-sm mb-4">Enter coupon code or select available coupon(s)</div>
                <input v-model="voucherInput" type="text" placeholder="Nhập mã giảm giá"
                    class="w-full border border-gray-300 rounded-lg px-4 py-2 text-base mb-3 focus:outline-none focus:ring-2 focus:ring-blue-400" />
                <button
                    class="w-full bg-blue-600 text-white font-semibold py-2 rounded-lg mb-4 hover:bg-blue-700 transition"
                    @click="applyVoucher">Áp dụng mã</button>
                <div class="text-xs text-gray-400 mb-2">Hoặc chọn mã có sẵn:</div>
                <div class="flex gap-4 overflow-x-auto pb-2 scrollbar-hide scroll-smooth">
                    <label v-for="voucher in suggestedVouchers" :key="voucher.code"
                        class="min-w-[220px] flex-shrink-0 bg-white p-4 rounded-xl shadow border-2 border-indigo-200 hover:border-indigo-500 cursor-pointer flex flex-col gap-2 transition relative">
                        <input type="radio" name="voucher" class="form-radio text-indigo-600 absolute top-3 left-3"
                            :value="voucher.code" v-model="voucherInput" />
                        <div class="flex flex-col pl-7">
                            <span
                                class="voucher-badge bg-amber-400 text-white px-2 py-0.5 rounded-full text-xs font-bold shadow-md w-max mb-1">{{
                                    voucher.badge }}</span>
                            <span class="font-bold text-base text-gray-900 mb-0.5">{{ voucher.title }}</span>
                            <span class="text-xs text-gray-600">{{ voucher.desc }}</span>
                            <span class="text-xs text-gray-600">{{ voucher.valid }}</span>
                        </div>
                    </label>
                </div>
            </div>
        </div>
    </template>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getBankList } from '@/api/bankApi'
import { bookHotel, addItemToCart } from '@/api/hotelApi'
import { getAccessToken } from '@/services/TokenService'

export default {
    name: 'HotelBooking',
    setup() {
        const route = useRoute()
        const router = useRouter()

        const parsePriceFromQuery = (queryValue) => {
            if (!queryValue) return 0;
            const strValue = queryValue.toString();
            const parsed = parseFloat(strValue.replace(/\./g, '').replace(',', '.'));
            return isNaN(parsed) ? 0 : parsed;
        };

        const checkin = route.query.checkin || ''
        const checkout = route.query.checkout || ''
        const maxAdults = parseInt(route.query.maxAdults) || 0;
        const maxChildren = parseInt(route.query.maxChildren) || 0;

        const hotelTitle = route.query.hotelTitle || ''
        const hotellocation = route.query.hotellocation || ''
        const hotelRating = route.query.hotelRating || ''
        const hotelReviews = route.query.hotelReviews || ''

        const variantName = route.query.variantName || ''
        const variantBreakfast = route.query.variantBreakfast || ''
        const roomBed = route.query.roomBed || ''

        const variantOriginalPrice = parsePriceFromQuery(route.query.variantOriginalPrice);

        const variantDiscountedPrice = parsePriceFromQuery(route.query.variantDiscountedPrice);

        const taxAndFeeAmount = parsePriceFromQuery(route.query.taxAndFeeAmount);
        const serviceFee = parsePriceFromQuery(route.query.serviceFee);

        const nights = parseInt(route.query.nights) || 1
        const rooms = parseInt(route.query.rooms) || 1

        const subtotal = (variantDiscountedPrice * nights * rooms) + ((taxAndFeeAmount || 0) * nights * rooms) + serviceFee;

        const discount = parsePriceFromQuery(route.query.discount)

        const isVoucherVisible = ref(false)
        const toggleVoucher = () => {
            isVoucherVisible.value = !isVoucherVisible.value
        }

        const paymentMethod = ref('credit')
        const selectPaymentMethod = (method) => {
            paymentMethod.value = method
        }

        const formattedCheckin = computed(() => {
            if (!checkin) return ''
            return new Date(checkin).toLocaleDateString('vi-VN', {
                month: '2-digit',
                day: '2-digit',
                year: 'numeric',
            })
        })
        const formattedCheckout = computed(() => {
            if (!checkout) return ''
            return new Date(checkout).toLocaleDateString('vi-VN', {
                month: '2-digit',
                day: '2-digit',
                year: 'numeric',
            })
        })

        const formatPrice = (val) => {
            if (typeof val === 'string') val = Number(val)
            const roundedVal = Math.round(val);
            return roundedVal.toLocaleString({
                style: 'currency',
                currency: 'VND',
                minimumFractionDigits: 0,
                maximumFractionDigits: 0,
            }) + ' VND'
        }

        const reservationCode = Math.random()
            .toString(36)
            .substring(2, 12)
            .toUpperCase()

        const fullName = ref('');
        const email = ref('');
        const phone = ref('');
        const errors = ref({ fullName: '', email: '', phone: '' });

        const validateFullName = () => {
            if (!fullName.value.trim()) {
                errors.value.fullName = 'Vui lòng nhập họ và tên.';
            } else if (!/^[A-Za-zÀ-ỹ\s]+$/.test(fullName.value.trim())) {
                errors.value.fullName = 'Tên chỉ được chứa chữ cái và khoảng trắng.';
            } else {
                errors.value.fullName = '';
            }
        };
        const validateEmail = () => {
            if (!email.value.trim()) {
                errors.value.email = 'Vui lòng nhập email.';
            } else if (!/^\S+@\S+\.\S+$/.test(email.value.trim())) {
                errors.value.email = 'Email không hợp lệ.';
            } else {
                errors.value.email = '';
            }
        };
        const validatePhone = () => {
            if (!phone.value.trim()) {
                errors.value.phone = 'Vui lòng nhập số điện thoại.';
            } else if (!/^\d{8,15}$/.test(phone.value.trim())) {
                errors.value.phone = 'Số điện thoại chỉ gồm số, từ 8-15 ký tự.';
            } else {
                errors.value.phone = '';
            }
        };
        const validateForm = () => {
            validateFullName();
            validateEmail();
            validatePhone();
            return !errors.value.fullName && !errors.value.email && !errors.value.phone;
        };

        const roomVariantId = route.query.roomVariantId ? parseInt(route.query.roomVariantId) : null;

        const adults = parseInt(route.query.adults) || 1;
        const children = parseInt(route.query.children) || 0;

        const onSubmitPayment = async () => {
            if (!validateForm()) return;
            if (!roomVariantId) {
                window.$toast && window.$toast('Không xác định được loại phòng!', 'error');
                return;
            }
            const token = getAccessToken();
            if (!token) {
                window.$toast && window.$toast('Bạn cần đăng nhập để đặt phòng!', 'error');
                return;
            }
            if (paymentMethod.value === 'bank' && !selectedBank.value) return;
            try {
                const res = await bookHotel({
                    fullName: fullName.value,
                    email: email.value,
                    phone: phone.value,
                    roomVariantId: roomVariantId,
                    checkInDate: checkin,
                    checkOutDate: checkout,
                    numAdults: adults,
                    numChildren: children,
                    totalPrice: subtotal,
                    rooms: rooms,
                });
                console.log('Booking response:', res);
                const order = res.data && res.data.data ? res.data.data : res.data;
                console.log('Order:', order);
                if (!order || !order.id) {
                    window.$toast && window.$toast('Đặt phòng thất bại! (Không lấy được orderId)', 'error');
                    return;
                }
                console.log('Chuyển sang PaymentView với orderId:', order.id);
                try {
                    router.push({
                        name: 'order-detail',
                        params: { id: order.id },
                    });
                } catch (pushErr) {
                    console.error('Lỗi khi chuyển trang PaymentView:', pushErr);
                    window.$toast && window.$toast('Chuyển trang PaymentView thất bại!', 'error');
                }
            } catch (e) {
                console.error('Booking error:', e, e?.response);
                window.$toast && window.$toast('Đặt phòng thất bại!', 'error');
            }
        }

        const getDayOfWeek = (dateStr) => {
            if (!dateStr) return '';
            const date = new Date(dateStr);
            return date.toLocaleDateString('vi-VN', { weekday: 'short' });
        };
        const checkinDayOfWeek = computed(() => getDayOfWeek(checkin));
        const checkoutDayOfWeek = computed(() => getDayOfWeek(checkout));

        const starRating = parseInt(route.query.starRating) || 0

        let hotelImages = [];
        try {
            hotelImages = JSON.parse(route.query.hotelImages || '[]');
        } catch (e) {
            hotelImages = [];
        }
        const hotelImageIndex = ref(0);
        const slideDirection = ref('next');
        const nextHotelImage = () => {
            slideDirection.value = 'next';
            hotelImageIndex.value = (hotelImageIndex.value + 1) % hotelImages.length;
        };
        const prevHotelImage = () => {
            slideDirection.value = 'prev';
            hotelImageIndex.value = (hotelImageIndex.value - 1 + hotelImages.length) % hotelImages.length;
        };

        const banks = ref([]);
        const selectedBank = ref('');
        const bankLoading = ref(false);
        const bankError = ref('');
        const showBankModal = ref(false);
        const bankSearch = ref('');
        const selectedBankObj = computed(() => banks.value.find(b => b.code === selectedBank.value));
        const filteredBanks = computed(() => {
            if (!bankSearch.value) return banks.value;
            const kw = bankSearch.value.toLowerCase();
            return banks.value.filter(
                b => b.shortName.toLowerCase().includes(kw) || b.name.toLowerCase().includes(kw)
            );
        });
        const selectBank = (bank) => {
            selectedBank.value = bank.code;
            showBankModal.value = false;
            bankSearch.value = '';
        };

        const currentStep = ref(1);
        const goToNextStep = () => {
            if (!validateForm()) return;
            onSubmitPayment();
        };
        const paymentMethodLabel = computed(() => {
            switch (paymentMethod.value) {
                case 'credit': return 'Thẻ tín dụng';
                case 'paypal': return 'Paypal';
                case 'googlepay': return 'Google Pay';
                case 'bank': return 'Chuyển khoản ngân hàng';
                case 'at_hotel': return 'Thanh toán tại khách sạn';
                default: return '';
            }
        });

        const stepLabels = ['Đặt'];

        const paymentMethods = [
            {
                value: 'credit',
                label: 'Thẻ tín dụng',
                desc: 'Thanh toán bằng thẻ Visa, MasterCard, JCB...',
                icon: 'fas fa-credit-card',
            },
            {
                value: 'paypal',
                label: 'Paypal',
                desc: 'Thanh toán qua tài khoản Paypal',
                icon: 'fab fa-paypal',
            },
            {
                value: 'googlepay',
                label: 'Google Pay',
                desc: 'Thanh toán qua Google Pay',
                icon: 'fab fa-google-pay',
            },
            {
                value: 'bank',
                label: 'Chuyển khoản ngân hàng',
                desc: 'Chuyển khoản trực tiếp qua ngân hàng nội địa',
                icon: 'fas fa-university',
            },
            {
                value: 'at_hotel',
                label: 'Thanh toán tại khách sạn',
                desc: 'Thanh toán khi nhận phòng',
                icon: 'fas fa-hotel',
            },
        ];

        const showVoucherPopup = ref(false);
        const voucherInput = ref("");
        const suggestedVouchers = [
            { code: "summer-sale", badge: "Only a few left!", title: "Summer Sale", desc: "10% off all bookings", valid: "Valid until Aug 31, 2024" },
            { code: "new-user", badge: "Limited time!", title: "New User Discount", desc: "$87 off your first booking", valid: "Valid for new customers only" },
            { code: "holiday-special", badge: "Popular", title: "Holiday Special", desc: "Free service fee on bookings", valid: "Valid Dec 1 - Dec 31, 2024" },
        ];
        const applyVoucher = () => {
            showVoucherPopup.value = false;
        };

        const activeCartId = localStorage.getItem('activeCartId');

        const addRoomToOrder = async () => {
            if (!validateForm()) {
                window.$toast && window.$toast('Vui lòng kiểm tra lại thông tin!', 'error');
                return;
            }
            if (!roomVariantId) {
                window.$toast && window.$toast('Không xác định được loại phòng!', 'error');
                return;
            }
            if (!activeCartId) {
                window.$toast && window.$toast('Không tìm thấy đơn hàng!', 'error');
                return;
            }
            
            try {
                await addItemToCart(activeCartId, {
                    itemId: roomVariantId,
                    itemType: 'HOTEL',
                    numberOfAdults: adults,
                    numberOfChildren: children,
                    roomId: roomVariantId,
                    checkInDate: checkin,
                    checkOutDate: checkout,
                    totalPrice: Number(subtotal),
                    numberOfRooms: rooms,
                    fullName: fullName.value,
                    email: email.value,
                    phone: phone.value
                });
                
                window.$toast && window.$toast('Đã thêm phòng vào chuyến đi!', 'success');
                localStorage.removeItem('activeCartId');
                router.push(`/orders/${activeCartId}`);
                
            } catch (e) {
                window.$toast && window.$toast('Có lỗi khi thêm phòng vào chuyến đi!', 'error');
            }
        };

        onMounted(async () => {
            bankLoading.value = true;
            try {
                const res = await getBankList();
                banks.value = res.data.data || [];
            } catch (e) {
                bankError.value = 'Không lấy được danh sách ngân hàng.';
            } finally {
                bankLoading.value = false;
            }
        });

        return {
            checkin,
            checkout,
            maxAdults,
            maxChildren,
            hotelTitle,
            hotellocation,
            hotelRating,
            hotelReviews,
            variantName,
            variantBreakfast,
            roomBed,
            variantOriginalPrice,
            variantDiscountedPrice,
            nights,
            rooms,
            subtotal,
            discount,
            serviceFee,
            taxAndFeeAmount,
            isVoucherVisible,
            toggleVoucher,
            paymentMethod,
            selectPaymentMethod,
            formattedCheckin,
            formattedCheckout,
            formatPrice,
            onSubmitPayment,
            checkinDayOfWeek,
            checkoutDayOfWeek,
            starRating,
            hotelImages,
            hotelImageIndex,
            nextHotelImage,
            prevHotelImage,
            slideDirection,
            fullName,
            email,
            phone,
            errors,
            validateFullName,
            validateEmail,
            validatePhone,
            banks,
            selectedBank,
            bankLoading,
            bankError,
            showBankModal,
            bankSearch,
            selectedBankObj,
            filteredBanks,
            selectBank,
            currentStep,
            goToNextStep,
            paymentMethodLabel,
            stepLabels,
            paymentMethods,
            showVoucherPopup,
            voucherInput,
            suggestedVouchers,
            applyVoucher,
            addRoomToOrder,
            activeCartId,
        }
    },
}
</script>

<style scoped>
.slide-left-enter-active,
.slide-left-leave-active,
.slide-right-enter-active,
.slide-right-leave-active {
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    position: absolute;
    width: 100%;
    height: 100%;
}

.slide-left-enter-from {
    transform: translateX(100%);
    opacity: 0.7;
}

.slide-left-leave-to {
    transform: translateX(-100%);
    opacity: 0.7;
}

.slide-right-enter-from {
    transform: translateX(-100%);
    opacity: 0.7;
}

.slide-right-leave-to {
    transform: translateX(100%);
    opacity: 0.7;
}

@keyframes fadeIn {
    from {
        opacity: 0;
        transform: scale(0.95);
    }

    to {
        opacity: 1;
        transform: scale(1);
    }
}

.animate-fadeIn {
    animation: fadeIn 0.2s ease;
}
</style>