<template>
  <div class="w-full min-h-screen py-10 overflow-hidden relative 
              after:absolute after:w-80 after:h-80 after:bg-gradient-to-tr after:from-blue-300 after:to-indigo-400 after:-z-10
              after:rounded-full after:-top-56 after:left-6 after:blur-3xl
              before:absolute before:w-80 before:h-80 before:bg-gradient-to-br before:from-indigo-300 before:to-blue-400 before:-z-10
              before:rounded-full before:top-[300px] before:right-[-200px] before:blur-3xl">
    <div class="max-w-[100%] mx-auto px-4">
      <!-- ========== HEADER ========== -->
      <div class="mb-8 text-center relative">
        <h1 class="text-5xl font-extrabold text-indigo-700">Admin Đăng Chuyến Bay</h1>
        <p class="mt-2 text-indigo-500">Điền đầy đủ thông tin để tạo chuyến bay mới</p>
        <i class="fa-solid fa-plane absolute top-6 right-20 text-6xl text-indigo-500 opacity-20 rotate-[25deg]"></i>
      </div>

      <!-- ========== TABS ========== -->
      <div class="mb-10 flex justify-center">
        <div class="inline-flex space-x-2 bg-indigo-50 rounded-full p-1">
          <button
            @click="currentTab = 'basic'"
            :class="currentTab === 'basic' ? tabActiveClass : tabInactiveClass"
            class="px-5 py-2 rounded-full text-sm font-medium transition">
            Thông tin cơ bản
          </button>
          <button
            @click="currentTab = 'schedule'"
            :class="currentTab === 'schedule' ? tabActiveClass : tabInactiveClass"
            class="px-5 py-2 rounded-full text-sm font-medium transition">
            Lịch trình
          </button>
          <button
            @click="currentTab = 'pricing'"
            :class="currentTab === 'pricing' ? tabActiveClass : tabInactiveClass"
            class="px-5 py-2 rounded-full text-sm font-medium transition">
            Giá vé & Dịch vụ
          </button>
        </div>
      </div>

      <!-- ========== TAB CONTENT ========== -->
      <div>
        <!-- ==== TAB 1: Thông tin cơ bản ==== -->
        <div v-show="currentTab === 'basic'">
          <div class="relative bg-white rounded-2xl shadow-xl p-8 mb-10 transform transition hover:-translate-y-1 hover:shadow-2xl overflow-hidden">
            <h2 class="text-2xl font-semibold text-gray-800 mb-6">Thông tin cơ bản</h2>
            <form @submit.prevent="goToSchedule">
              <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-x-6 gap-y-6">
                <!-- Hãng hàng không -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Hãng hàng không</label>
                  <select
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    v-model="basic.airline">
                    <option value="" disabled>Chọn hãng hàng không</option>
                    <option v-for="(label, key) in airlineOptions" :key="key" :value="key">
                      {{ label }}
                    </option>
                  </select>
                </div>

                <!-- Số hiệu chuyến bay -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Số hiệu chuyến bay</label>
                  <input
                    type="text"
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    placeholder="Ví dụ: VN123, VJ456"
                    v-model="basic.flightNumber"
                  />
                </div>

                <!-- Loại máy bay -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Loại máy bay</label>
                  <select
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    v-model="basic.planeType">
                    <option value="" disabled>Chọn loại máy bay</option>
                    <option v-for="type in planeTypes" :key="type">{{ type }}</option>
                  </select>
                </div>

                <!-- Loại chuyến bay -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Loại chuyến bay</label>
                  <select
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    v-model="basic.flightType">
                    <option value="" disabled>Chọn loại chuyến bay</option>
                    <option value="Nội địa">Nội địa</option>
                    <option value="Quốc tế">Quốc tế</option>
                  </select>
                </div>

                <!-- Mô tả chuyến bay -->
                <div class="sm:col-span-2 lg:col-span-3">
                  <label class="block text-sm font-medium text-gray-700 mb-1">Mô tả</label>
                  <textarea
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    rows="4"
                    placeholder="Mô tả về chuyến bay…"
                    v-model="basic.description"
                  ></textarea>
                </div>

                <!-- Upload hình ảnh -->
                <div class="sm:col-span-2 lg:col-span-3">
                  <label class="block text-sm font-medium text-gray-700 mb-2">Hình ảnh</label>
                  <div class="flex flex-wrap gap-4">
                    <div
                      class="w-1/2 border-2 border-dashed border-gray-300 rounded-lg p-4 flex flex-col items-center justify-center
                             hover:border-indigo-400 hover:bg-indigo-50 transition cursor-pointer">
                      <i class="fa-solid fa-plane text-3xl text-gray-400 mb-2"></i>
                      <p class="text-gray-500 text-sm">Thêm ảnh máy bay</p>
                      <input type="file" class="mt-2" @change="onFileChange($event, 'plane')" />
                    </div>
                    <div
                      class="w-1/2 border-2 border-dashed border-gray-300 rounded-lg p-4 flex flex-col items-center justify-center
                             hover:border-indigo-400 hover:bg-indigo-50 transition cursor-pointer">
                      <i class="fa-solid fa-couch text-3xl text-gray-400 mb-2"></i>
                      <p class="text-gray-500 text-sm">Thêm ảnh nội thất</p>
                      <input type="file" class="mt-2" @change="onFileChange($event, 'interior')" />
                    </div>
                  </div>
                </div>
              </div>

              <div class="mt-8 flex justify-end space-x-4">
                <button
                  type="button"
                  class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-100 transition"
                  @click="resetBasic">
                  Làm mới
                </button>
                <button
                  type="submit"
                  class="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 transition">
                  Tiếp
                </button>
              </div>
            </form>
          </div>
        </div>

        <!-- ==== TAB 2: Lịch trình ==== -->
        <div v-show="currentTab === 'schedule'">
          <div class="relative bg-white rounded-2xl shadow-xl p-8 mb-10 transform transition hover:-translate-y-1 hover:shadow-2xl overflow-hidden">
            <h2 class="text-2xl font-semibold text-gray-800 mb-6">Lịch trình chuyến bay</h2>
            
            <div v-for="(row, index) in scheduleList" :key="index"
                 class="border border-gray-200 rounded-lg px-6 py-6 mb-6 relative transform transition hover:shadow-lg">
              <!-- Nút xóa dòng lịch trình -->
              <button
                type="button"
                class="absolute top-3 right-3 text-red-500 hover:text-red-700 transition"
                @click="removeSchedule(index)">
                <i class="fa-solid fa-times"></i>
              </button>

              <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-x-6 gap-y-6">
                <!-- Sân bay đi -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Sân bay đi</label>
                  <select
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    v-model="row.departAirport">
                    <option value="" disabled>Chọn sân bay đi</option>
                    <option v-for="airport in airports" :key="airport" :value="airport">{{ airport }}</option>
                  </select>
                </div>
                <!-- Sân bay đến -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Sân bay đến</label>
                  <select
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    v-model="row.arriveAirport">
                    <option value="" disabled>Chọn sân bay đến</option>
                    <option v-for="airport in airports" :key="airport" :value="airport">{{ airport }}</option>
                  </select>
                </div>
                <!-- Ngày khởi hành -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Ngày khởi hành</label>
                  <input
                    type="date"
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    v-model="row.departDate"
                  />
                </div>
                <!-- Tần suất -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Tần suất</label>
                  <select
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    v-model="row.frequency">
                    <option value="" disabled>Chọn tần suất</option>
                    <option v-for="freq in frequencies" :key="freq">{{ freq }}</option>
                  </select>
                </div>
                <!-- Giờ khởi hành -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Giờ khởi hành</label>
                  <input
                    type="time"
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    v-model="row.departTime"
                  />
                </div>
                <!-- Giờ đến (dự kiến) -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Giờ đến (dự kiến)</label>
                  <input
                    type="time"
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    v-model="row.arriveTime"
                  />
                </div>
                <!-- Thời gian bay (phút) -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Thời gian bay (phút)</label>
                  <input
                    type="number"
                    min="0"
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    placeholder="Nhập thời gian"
                    v-model.number="row.duration"
                  />
                </div>
                <!-- Điểm trung chuyển (nếu có) -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Điểm trung chuyển (nếu có)</label>
                  <select
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    v-model="row.stopoverAirport">
                    <option value="" selected>Không</option>
                    <option v-for="airport in airports" :key="airport" :value="airport">{{ airport }}</option>
                  </select>
                </div>
                <!-- Thời gian dừng trung chuyển (phút) -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Thời gian dừng (phút)</label>
                  <input
                    type="number"
                    min="0"
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    placeholder="Nhập thời gian"
                    v-model.number="row.stopoverDuration"
                  />
                </div>
              </div>
            </div>

            <!-- Nút thêm dòng lịch trình -->
            <div class="mb-6">
              <button
                type="button"
                class="px-4 py-2 border border-indigo-600 text-indigo-600 rounded-md hover:bg-indigo-50 transition"
                @click="addSchedule">
                <i class="fa-solid fa-plus mr-2"></i>Thêm lịch trình
              </button>
            </div>

            <!-- Nút điều hướng -->
            <div class="flex justify-between">
              <button
                type="button"
                class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-100 transition"
                @click="currentTab = 'basic'">
                ← Quay lại
              </button>
              <button
                type="button"
                class="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 transition"
                @click="goToPricing">
                Tiếp →
              </button>
            </div>
          </div>
        </div>

        <!-- ==== TAB 3: Giá vé & Dịch vụ ==== -->
        <div v-show="currentTab === 'pricing'">
          <div class="relative bg-white rounded-2xl shadow-xl p-8 mb-10 transform transition hover:-translate-y-1 hover:shadow-2xl overflow-hidden">
            <h2 class="text-2xl font-semibold text-gray-800 mb-6">Giá vé & Dịch vụ</h2>
            
            <div v-for="(tier, idx) in pricingList" :key="idx"
                 class="border border-gray-200 rounded-lg px-6 py-6 mb-6 relative transform transition hover:shadow-lg">
              <!-- Nút xóa hạng ghế -->
              <button
                type="button"
                class="absolute top-3 right-3 text-red-500 hover:text-red-700 transition"
                @click="removePricing(idx)">
                <i class="fa-solid fa-times"></i>
              </button>

              <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-x-6 gap-y-6">
                <!-- Hạng ghế -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Hạng ghế</label>
                  <select
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    v-model="tier.class">
                    <option value="" disabled>Chọn hạng ghế</option>
                    <option v-for="klass in seatClasses" :key="klass">{{ klass }}</option>
                  </select>
                </div>
                <!-- Giá vé cơ bản (VND) -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Giá vé cơ bản (VND)</label>
                  <input
                    type="number"
                    min="0"
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    placeholder="Nhập giá vé"
                    v-model.number="tier.basePrice"
                  />
                </div>
                <!-- Thuế & Phí (VND) -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Thuế & Phí (VND)</label>
                  <input
                    type="number"
                    min="0"
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    placeholder="Nhập thuế & phí"
                    v-model.number="tier.taxes"
                  />
                </div>
                <!-- Hành lý ký gửi (kg) -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Hành lý ký gửi (kg)</label>
                  <input
                    type="number"
                    min="0"
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    placeholder="Nhập ký gửi"
                    v-model.number="tier.checkedBaggage"
                  />
                </div>
                <!-- Hành lý xách tay (kg) -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Hành lý xách tay (kg)</label>
                  <input
                    type="number"
                    min="0"
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    placeholder="Nhập xách tay"
                    v-model.number="tier.cabinBaggage"
                  />
                </div>
                <!-- Tiện ích ghế ngồi -->
                <div class="sm:col-span-2 lg:col-span-3">
                  <label class="block text-sm font-medium text-gray-700 mb-1">Tiện ích ghế ngồi</label>
                  <textarea
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    rows="3"
                    placeholder="Mô tả tiện ích ghế ngồi…"
                    v-model="tier.amenities"
                  ></textarea>
                </div>
              </div>

              <!-- Dịch vụ trên chuyến bay (checkboxes) -->
              <div class="mt-4">
                <label class="block text-sm font-medium text-gray-700 mb-2">Dịch vụ trên chuyến bay</label>
                <div class="flex flex-wrap gap-4">
                  <div class="flex items-center">
                    <input
                      class="h-4 w-4 text-indigo-600 border-gray-300 rounded focus:ring-indigo-500"
                      type="checkbox"
                      :id="`meal_${idx}`"
                      v-model="tier.services.meal"
                    />
                    <label class="ml-2 block text-sm text-gray-700" :for="`meal_${idx}`">Bữa ăn</label>
                  </div>
                  <div class="flex items-center">
                    <input
                      class="h-4 w-4 text-indigo-600 border-gray-300 rounded focus:ring-indigo-500"
                      type="checkbox"
                      :id="`wifi_${idx}`"
                      v-model="tier.services.wifi"
                    />
                    <label class="ml-2 block text-sm text-gray-700" :for="`wifi_${idx}`">WiFi</label>
                  </div>
                  <div class="flex items-center">
                    <input
                      class="h-4 w-4 text-indigo-600 border-gray-300 rounded focus:ring-indigo-500"
                      type="checkbox"
                      :id="`ent_${idx}`"
                      v-model="tier.services.entertainment"
                    />
                    <label class="ml-2 block text-sm text-gray-700" :for="`ent_${idx}`">Giải trí</label>
                  </div>
                  <div class="flex items-center">
                    <input
                      class="h-4 w-4 text-indigo-600 border-gray-300 rounded focus:ring-indigo-500"
                      type="checkbox"
                      :id="`power_${idx}`"
                      v-model="tier.services.power"
                    />
                    <label class="ml-2 block text-sm text-gray-700" :for="`power_${idx}`">Ổ cắm điện</label>
                  </div>
                  <div class="flex items-center">
                    <input
                      class="h-4 w-4 text-indigo-600 border-gray-300 rounded focus:ring-indigo-500"
                      type="checkbox"
                      :id="`usb_${idx}`"
                      v-model="tier.services.usb"
                    />
                    <label class="ml-2 block text-sm text-gray-700" :for="`usb_${idx}`">Cổng USB</label>
                  </div>
                </div>
              </div>

              <!-- Chính sách hủy & đổi vé -->
              <div class="mt-4 grid grid-cols-1 lg:grid-cols-2 gap-6">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Chính sách hủy vé</label>
                  <textarea
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    rows="3"
                    placeholder="Nhập chính sách hủy vé…"
                    v-model="tier.cancelPolicy"
                  ></textarea>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Chính sách đổi vé</label>
                  <textarea
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    rows="3"
                    placeholder="Nhập chính sách đổi vé…"
                    v-model="tier.changePolicy"
                  ></textarea>
                </div>
              </div>
            </div>

            <div class="mb-6">
              <button
                type="button"
                class="px-4 py-2 border border-indigo-600 text-indigo-600 rounded-md hover:bg-indigo-50 transition"
                @click="addPricing">
                <i class="fa-solid fa-plus mr-2"></i>Thêm hạng ghế
              </button>
            </div>

            <!-- Nút điều hướng cuối -->
            <div class="flex justify-between">
              <button
                type="button"
                class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-100 transition"
                @click="currentTab = 'schedule'">
                ← Quay lại
              </button>
              <button
                type="button"
                class="px-4 py-2 bg-[#4f38f8] text-white rounded-md hover:bg-[#7a6af3] transition"
                @click="submitAll">
                Hoàn tất
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      currentTab: "basic",

      // Dữ liệu tab 1: Thông tin cơ bản
      basic: {
        airline: "",
        flightNumber: "",
        planeType: "",
        flightType: "",
        description: "",
        imagePlane: null,
        imageInterior: null,
      },
      airlineOptions: {
        VN: "Vietnam Airlines",
        VJ: "Vietjet Air",
        QH: "Bamboo Airways",
        BL: "Pacific Airlines",
        //… bạn có thể thêm
      },
      planeTypes: ["Boeing 737", "Airbus A320", "Embraer 195", "ATR 72"],

      // Dữ liệu tab 2: Lịch trình
      scheduleList: [
        {
          departAirport: "",
          arriveAirport: "",
          departDate: "",
          frequency: "",
          departTime: "",
          arriveTime: "",
          duration: null,
          stopoverAirport: "",
          stopoverDuration: null,
        },
      ],
      airports: ["HAN", "SGN", "DAD", "CXR", "VCA", "PQC", "DLI"],
      frequencies: ["Hàng ngày", "Chỉ Thứ 2-6", "Chủ Nhật", "Chỉ Thứ 7, Chủ Nhật"],

      // Dữ liệu tab 3: Giá vé & Dịch vụ
      pricingList: [
        {
          class: "",
          basePrice: null,
          taxes: null,
          checkedBaggage: null,
          cabinBaggage: null,
          amenities: "",
          services: {
            meal: false,
            wifi: false,
            entertainment: false,
            power: false,
            usb: false,
          },
          cancelPolicy: "",
          changePolicy: "",
        },
      ],
      seatClasses: ["Nguyên bản", "Thương gia", "Phổ thông linh hoạt", "Phổ thông tiết kiệm"],

      // Class cho tab active/inactive
      tabActiveClass: "bg-indigo-600 text-white shadow-lg",
      tabInactiveClass: "text-indigo-600 hover:bg-indigo-100",
    };
  },
  methods: {
    // Tab 1 → Tab 2
    goToSchedule() {
      // Validate nếu cần
      this.currentTab = "schedule";
    },
    // Tab 2 → Tab 3
    goToPricing() {
      // Validate nếu cần
      this.currentTab = "pricing";
    },
    // Tab 3: Submit
    submitAll() {
      const payload = {
        basic: this.basic,
        schedule: this.scheduleList,
        pricing: this.pricingList,
      };
      console.log("DATA SUBMIT:", payload);
      alert("Đã gửi dữ liệu lên server (xem console)!");
    },

    // Tab 1: reset
    resetBasic() {
      this.basic = {
        airline: "",
        flightNumber: "",
        planeType: "",
        flightType: "",
        description: "",
        imagePlane: null,
        imageInterior: null,
      };
    },
    onFileChange(evt, type) {
      const file = evt.target.files[0];
      if (type === "plane") this.basic.imagePlane = file;
      else if (type === "interior") this.basic.imageInterior = file;
    },

    // Tab 2: add/remove schedule row
    addSchedule() {
      this.scheduleList.push({
        departAirport: "",
        arriveAirport: "",
        departDate: "",
        frequency: "",
        departTime: "",
        arriveTime: "",
        duration: null,
        stopoverAirport: "",
        stopoverDuration: null,
      });
    },
    removeSchedule(idx) {
      if (this.scheduleList.length > 1) this.scheduleList.splice(idx, 1);
    },

    // Tab 3: add/remove pricing tier
    addPricing() {
      this.pricingList.push({
        class: "",
        basePrice: null,
        taxes: null,
        checkedBaggage: null,
        cabinBaggage: null,
        amenities: "",
        services: {
          meal: false,
          wifi: false,
          entertainment: false,
          power: false,
          usb: false,
        },
        cancelPolicy: "",
        changePolicy: "",
      });
    },
    removePricing(idx) {
      if (this.pricingList.length > 1) this.pricingList.splice(idx, 1);
    },

    formatCurrency(value) {
      return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
      }).format(value);
    },
  },
};
</script>

<style scoped>
/* Nếu cần override thêm, bạn có thể bổ sung ở đây. 
   Ví dụ custom scrollbar cho phần form. */
</style>
