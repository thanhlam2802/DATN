<template>
    <div class="max-w-4xl w-full mx-auto">
        <div class="flex border border-gray-200 rounded-t-md overflow-hidden text-center text-sm select-none">
            <button :class="[
                'flex-1 py-2 border-r border-gray-200',
                currentTab === 'basic'
                    ? 'bg-white text-black font-semibold'
                    : 'bg-gray-100 text-gray-500'
            ]" @click="currentTab = 'basic'" type="button">
                Thông tin cơ bản
            </button>
            <button :class="[
                'flex-1 py-2 border-r border-gray-200',
                currentTab === 'room'
                    ? 'bg-white text-black font-semibold'
                    : 'bg-gray-100 text-gray-500'
            ]" @click="currentTab = 'room'" type="button">
                Phòng &amp; Giá
            </button>
            <button :class="[
                'flex-1 py-2',
                currentTab === 'amenities'
                    ? 'bg-white text-black font-semibold'
                    : 'bg-gray-100 text-gray-500'
            ]" @click="currentTab = 'amenities'" type="button">
                Tiện nghi
            </button>
        </div>

        <div v-if="currentTab === 'basic'">
            <form @submit.prevent="handleSubmit" class="border border-gray-300 rounded-b-md p-6 space-y-6"
                autocomplete="off" novalidate>
                <div>
                    <label for="hotelName" class="block text-sm font-semibold text-gray-900 mb-1">Tên khách sạn</label>
                    <input v-model="form.hotelName" id="hotelName" type="text" placeholder="Nhập tên khách sạn"
                        class="w-full border border-gray-300 rounded-md px-3 py-2 placeholder-gray-400 text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent" />
                </div>

                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <div>
                        <label for="type" class="block text-sm font-semibold text-gray-900 mb-1">Loại hình</label>
                        <select v-model="form.type" id="type"
                            class="w-full border border-gray-300 rounded-md px-3 py-2 text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent">
                            <option value="">Chọn loại hình</option>
                            <option value="hotel">Khách sạn</option>
                            <option value="villa">Villa</option>
                            <option value="apartment">Căn hộ</option>
                        </select>
                    </div>
                    <div>
                        <label for="star" class="block text-sm font-semibold text-gray-900 mb-1">Hạng sao</label>
                        <select v-model="form.star" id="star"
                            class="w-full border border-gray-300 rounded-md px-3 py-2 text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent">
                            <option value="">Chọn hạng sao</option>
                            <option v-for="i in 5" :key="i" :value="i">{{ i }} sao</option>
                        </select>
                    </div>
                </div>

                <div>
                    <label for="address" class="block text-sm font-semibold text-gray-900 mb-1">Địa chỉ</label>
                    <div class="flex">
                        <input v-model="form.address" id="address" type="text" placeholder="Nhập địa chỉ khách sạn"
                            class="flex-grow border border-gray-300 rounded-l-md px-3 py-2 placeholder-gray-400 text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent" />
                        <button type="button"
                            class="border border-l-0 border-gray-300 rounded-r-md bg-white px-3 flex items-center justify-center text-gray-600 hover:text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-600"
                            aria-label="Location icon button">
                            <i class="fas fa-map-marker-alt"></i>
                        </button>
                    </div>
                </div>

                <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
                    <div>
                        <label for="city" class="block text-sm font-semibold text-gray-900 mb-1">Thành phố</label>
                        <select v-model="form.city" id="city"
                            class="w-full border border-gray-300 rounded-md px-3 py-2 text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent">
                            <option value="">Chọn thành phố</option>
                            <option>Hồ Chí Minh</option>
                            <option>Hà Nội</option>
                            <option>Đà Nẵng</option>
                            <option>Cần Thơ</option>
                            <option>Vũng Tàu</option>
                            <option>Huế</option>
                            <option>Hội An</option>
                            <option>Bình Định</option>
                        </select>
                    </div>
                    <div>
                        <label for="district" class="block text-sm font-semibold text-gray-900 mb-1">Quận/Huyện</label>
                        <select v-model="form.district" id="district"
                            class="w-full border border-gray-300 rounded-md px-3 py-2 text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent">
                            <option value="">Chọn quận/huyện</option>
                        </select>
                    </div>
                    <div>
                        <label for="postalCode" class="block text-sm font-semibold text-gray-900 mb-1">Mã bưu
                            chí</label>
                        <input v-model="form.postalCode" id="postalCode" type="text" placeholder="Nhập mã bưu chính"
                            class="w-full border border-gray-300 rounded-md px-3 py-2 placeholder-gray-400 text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent" />
                    </div>
                </div>

                <div>
                    <label for="description" class="block text-sm font-semibold text-gray-900 mb-1">Mô tả</label>
                    <textarea v-model="form.description" id="description" rows="4" placeholder="Mô tả về khách sạn"
                        class="w-full border border-gray-300 rounded-md px-3 py-2 placeholder-gray-400 text-gray-900 resize-none focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent"></textarea>
                </div>

                <div>
                    <label for="hotelImage" class="block text-sm font-semibold text-gray-900 mb-2">Hình ảnh khách
                        sạn</label>
                    <button type="button"
                        class="w-36 h-28 border-2 border-dashed border-gray-300 rounded-md flex flex-col items-center justify-center text-gray-500 hover:text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-600">
                        <i class="far fa-image text-2xl mb-1"></i>
                        Thêm ảnh
                    </button>
                </div>

                <div class="flex justify-end">
                    <button type="submit"
                        class="bg-black text-white text-sm font-semibold rounded-md px-5 py-2 hover:bg-gray-900 focus:outline-none focus:ring-2 focus:ring-offset-1 focus:ring-black">
                        Tiếp tục
                    </button>
                </div>
            </form>
        </div>

        <div v-if="currentTab === 'room'">
            <form @submit.prevent="handleSubmit" class="w-full max-w-5xl mt-6 border border-gray-200 rounded-lg p-6"
                autocomplete="off">
                <div class="flex justify-between items-center mb-4">
                    <h2 class="font-semibold text-gray-900 text-base">Thông tin phòng</h2>
                    <button type="button"
                        class="text-black font-semibold border border-gray-300 rounded-md px-4 py-2 hover:bg-gray-100 transition"
                        @click="addRoom">
                        + Thêm loại phòng
                    </button>
                </div>

                <div v-for="(room, idx) in rooms" :key="idx"
                    class="border border-gray-200 rounded-md p-4 space-y-4 mb-4">
                    <div class="flex justify-between items-center">
                        <h3 class="font-semibold text-gray-800">Loại phòng {{ idx + 1 }}</h3>
                        <button type="button" class="text-red-500" @click="removeRoom(idx)">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>

                    <div>
                        <label :for="`room-type-${idx}`" class="block text-sm font-semibold text-gray-900 mb-1">Loại
                            phòng</label>
                        <input v-model="room.type" :id="`room-type-${idx}`" type="text"
                            placeholder="Ví dụ: Phòng Deluxe, Phòng Superior"
                            class="w-full border border-gray-300 rounded-md px-3 py-2 text-gray-700 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-black focus:border-black" />
                    </div>

                    <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
                        <div>
                            <label :for="`capacity-${idx}`" class="block text-sm font-semibold text-gray-900 mb-1">Sức
                                chứa</label>
                            <select v-model="room.capacity" :id="`capacity-${idx}`"
                                class="w-full border border-gray-300 rounded-md px-3 py-2 text-gray-700 focus:outline-none focus:ring-2 focus:ring-black focus:border-black">
                                <option value="" disabled>Chọn sức chứa</option>
                                <option>1</option>
                                <option>2</option>
                                <option>3</option>
                                <option>4</option>
                                <option>5+</option>
                            </select>
                        </div>
                        <div>
                            <label :for="`bed-type-${idx}`"
                                class="block text-sm font-semibold text-gray-900 mb-1">Giường</label>
                            <select v-model="room.bedType" :id="`bed-type-${idx}`"
                                class="w-full border border-gray-300 rounded-md px-3 py-2 text-gray-700 focus:outline-none focus:ring-2 focus:ring-black focus:border-black">
                                <option value="" disabled>Chọn loại giường</option>
                                <option>Giường đơn</option>
                                <option>Giường đôi</option>
                                <option>Giường queen</option>
                                <option>Giường king</option>
                            </select>
                        </div>
                        <div>
                            <label :for="`room-quantity-${idx}`"
                                class="block text-sm font-semibold text-gray-900 mb-1">Số lượng phòng</label>
                            <input v-model="room.quantity" :id="`room-quantity-${idx}`" name="room-quantity"
                                type="number" min="0" placeholder="Nhập số lượng phòng"
                                class="w-full border border-gray-300 rounded-md px-3 py-2 text-gray-400 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-black focus:border-black" />
                        </div>
                    </div>

                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div>
                            <label :for="`price-${idx}`" class="block text-sm font-semibold text-gray-900 mb-1">Giá
                                phòng (VND)</label>
                            <input v-model="room.price" :id="`price-${idx}`" name="price" type="number" min="0"
                                placeholder="Nhập giá phòng"
                                class="w-full border border-gray-300 rounded-md px-3 py-2 text-gray-400 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-black focus:border-black" />
                        </div>
                        <div>
                            <label :for="`discount-${idx}`" class="block text-sm font-semibold text-gray-900 mb-1">Giảm
                                giá (%)</label>
                            <input v-model="room.discount" :id="`discount-${idx}`" name="discount" type="number" min="0"
                                max="100" placeholder="Nhập % giảm giá (nếu có)"
                                class="w-full border border-gray-300 rounded-md px-3 py-2 text-gray-400 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-black focus:border-black" />
                        </div>
                    </div>

                    <div>
                        <label class="block text-sm font-semibold text-gray-900 mb-2">Hình ảnh phòng</label>
                        <button type="button"
                            class="w-40 h-28 border-2 border-dashed border-gray-300 rounded-md flex flex-col items-center justify-center text-gray-400 hover:border-gray-400 transition">
                            <i class="far fa-image text-2xl mb-1"></i>
                            <span class="text-sm font-semibold">Thêm ảnh</span>
                        </button>
                    </div>
                </div>

                <div class="flex justify-between mt-8">
                    <button type="button"
                        class="font-semibold text-black border border-gray-300 rounded-md px-4 py-2 hover:bg-gray-100 transition"
                        @click="currentTab = 'basic'">
                        Quay lại
                    </button>
                    <button type="submit"
                        class="font-semibold text-white bg-black rounded-md px-6 py-2 hover:bg-gray-900 transition">
                        Tiếp tục
                    </button>
                </div>
            </form>
        </div>

        <div v-if="currentTab === 'amenities'">
            <form @submit.prevent="handleSubmit" class="p-6 space-y-6">
                <section>
                    <h2 class="text-lg font-bold mb-4">Tiện nghi khách sạn</h2>
                    <div class="grid grid-cols-1 sm:grid-cols-3 gap-x-12 gap-y-2 text-base font-semibold text-gray-900">
                        <label class="flex items-center space-x-2">
                            <input type="checkbox" v-model="form.amenities" value="wifi"
                                class="w-4 h-4 border border-gray-300 rounded" />
                            <span>WiFi miễn phí</span>
                        </label>
                        <label class="flex items-center space-x-2">
                            <input type="checkbox" v-model="form.amenities" value="parking"
                                class="w-4 h-4 border border-gray-300 rounded" />
                            <span>Bãi đỗ xe</span>
                        </label>
                        <label class="flex items-center space-x-2">
                            <input type="checkbox" v-model="form.amenities" value="pool"
                                class="w-4 h-4 border border-gray-300 rounded" />
                            <span>Hồ bơi</span>
                        </label>

                        <label class="flex items-center space-x-2">
                            <input type="checkbox" v-model="form.amenities" value="restaurant"
                                class="w-4 h-4 border border-gray-300 rounded" />
                            <span>Nhà hàng</span>
                        </label>
                        <label class="flex items-center space-x-2">
                            <input type="checkbox" v-model="form.amenities" value="spa"
                                class="w-4 h-4 border border-gray-300 rounded" />
                            <span>Spa</span>
                        </label>
                        <label class="flex items-center space-x-2">
                            <input type="checkbox" v-model="form.amenities" value="gym"
                                class="w-4 h-4 border border-gray-300 rounded" />
                            <span>Phòng tập gym</span>
                        </label>

                        <label class="flex items-center space-x-2">
                            <input type="checkbox" v-model="form.amenities" value="ac"
                                class="w-4 h-4 border border-gray-300 rounded" />
                            <span>Điều hòa</span>
                        </label>
                        <label class="flex items-center space-x-2">
                            <input type="checkbox" v-model="form.amenities" value="breakfast"
                                class="w-4 h-4 border border-gray-300 rounded" />
                            <span>Bữa sáng</span>
                        </label>
                        <label class="flex items-center space-x-2">
                            <input type="checkbox" v-model="form.amenities" value="elevator"
                                class="w-4 h-4 border border-gray-300 rounded" />
                            <span>Thang máy</span>
                        </label>
                    </div>
                </section>

                <section>
                    <h2 class="text-lg font-bold mb-4">Chính sách khách sạn</h2>
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-4 mb-6">
                        <div>
                            <label for="checkin" class="block text-sm font-semibold mb-1">Giờ nhận phòng</label>
                            <select id="checkin" name="checkin" v-model="form.checkinTime"
                                class="w-full border border-gray-300 rounded-md px-3 py-2 text-base placeholder-gray-400 focus:outline-none focus:ring-1 focus:ring-gray-300">
                                <option value="">Chọn giờ nhận phòng</option>
                                <option>12:00 PM</option>
                                <option>01:00 PM</option>
                                <option>02:00 PM</option>
                            </select>
                        </div>
                        <div>
                            <label for="checkout" class="block text-sm font-semibold mb-1">Giờ trả phòng</label>
                            <select id="checkout" name="checkout" v-model="form.checkoutTime"
                                class="w-full border border-gray-300 rounded-md px-3 py-2 text-base placeholder-gray-400 focus:outline-none focus:ring-1 focus:ring-gray-300">
                                <option value="">Chọn giờ trả phòng</option>
                                <option>11:00 AM</option>
                                <option>12:00 PM</option>
                                <option>01:00 PM</option>
                            </select>
                        </div>
                    </div>
                    <div>
                        <label for="otherPolicy" class="block text-sm font-semibold mb-1">Chính sách khác</label>
                        <textarea id="otherPolicy" name="otherPolicy" v-model="form.otherPolicy" rows="4"
                            placeholder="Nhập các chính sách khác của khách sạn"
                            class="w-full border border-gray-300 rounded-md px-3 py-2 text-base placeholder-gray-400 resize-y focus:outline-none focus:ring-1 focus:ring-gray-300"></textarea>
                    </div>
                </section>

                <div class="flex justify-between">
                    <button type="button"
                        class="px-4 py-2 border border-gray-300 rounded-md font-semibold text-gray-900 hover:bg-gray-100 focus:outline-none"
                        @click="currentTab = 'room'">
                        Quay lại
                    </button>
                    <button type="submit"
                        class="px-6 py-3 bg-black text-white rounded-md font-semibold hover:bg-gray-900 focus:outline-none">
                        Hoàn tất đăng ký
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>

<script>
export default {
    name: 'HotelAdmin',
    data() {
        return {
            currentTab: 'basic',
            form: {
                hotelName: '',
                type: '',
                star: '',
                address: '',
                city: '',
                district: '',
                postalCode: '',
                description: '',
                amenities: [],
                checkinTime: '',
                checkoutTime: '',
                otherPolicy: '',
            },
            rooms: [
                {
                    type: '',
                    capacity: '',
                    bedType: '',
                    quantity: 0,
                    price: 0,
                    discount: 0,
                },
            ],
        };
    },
    methods: {
        handleSubmit() {
            const payload = {
                basicInfo: {
                    hotelName: this.form.hotelName,
                    type: this.form.type,
                    star: this.form.star,
                    address: this.form.address,
                    city: this.form.city,
                    district: this.form.district,
                    postalCode: this.form.postalCode,
                    description: this.form.description,
                },
                rooms: this.rooms.map((r) => ({
                    type: r.type,
                    capacity: r.capacity,
                    bedType: r.bedType,
                    quantity: r.quantity,
                    price: r.price,
                    discount: r.discount,
                })),
                amenities: this.form.amenities,
                policies: {
                    checkinTime: this.form.checkinTime,
                    checkoutTime: this.form.checkoutTime,
                    otherPolicy: this.form.otherPolicy,
                },
            };
            console.log('Dữ liệu khách sạn gửi lên:', payload);
            if (this.currentTab === 'basic') {
                this.currentTab = 'room';
            } else if (this.currentTab === 'room') {
                this.currentTab = 'amenities';
            } else if (this.currentTab === 'amenities') {

            }
        },
        addRoom() {
            this.rooms.push({
                type: '',
                capacity: '',
                bedType: '',
                quantity: 0,
                price: 0,
                discount: 0,
            });
        },
        removeRoom(index) {
            this.rooms.splice(index, 1);
        },
    },
};
</script>

<style scoped></style>
