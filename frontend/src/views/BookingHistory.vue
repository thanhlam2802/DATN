<template>
  <div class="max-w-4xl mx-auto font-sans text-sm">
    <!-- Tabs -->
    <div class="grid grid-cols-3 gap-2 mb-4">
      <button
        v-for="tab in tabs"
        :key="tab"
        @click="activeTab = tab"
        :class="[
          'px-5 py-2 rounded-full font-semibold border transition',
          activeTab === tab
            ? 'bg-blue-600 text-white border-blue-600'
            : 'bg-white text-blue-600 border-blue-300 hover:bg-blue-50'
        ]"
      >
        {{ tab }}
      </button>
    </div>

    <!-- Bộ lọc phụ: Hành trình / Danh sách -->
    <div class="flex justify-between items-center mb-6">
      <button class="flex items-center px-4 py-2 rounded-full border text-blue-600 border-blue-300 bg-white hover:bg-blue-50 transition">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2 fill-current text-blue-600" viewBox="0 0 24 24"><path d="M3 3h18v2H3zm0 7h12v2H3zm0 7h6v2H3z"/></svg>
        Hành trình
      </button>
      <button class="flex items-center px-4 py-2 rounded-full border text-blue-600 border-blue-300 bg-white hover:bg-blue-50 transition">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2 fill-current text-blue-600" viewBox="0 0 24 24"><path d="M3 4h18v2H3V4zm0 6h18v2H3v-2zm0 6h18v2H3v-2z"/></svg>
        Danh sách
      </button>
    </div>

    <!-- Upcoming Trips Section -->
    <div v-if="activeTab === 'Sắp tới'" class="text-center bg-blue-50 p-6 rounded-lg border border-blue-100">
      <img src="https://img.icons8.com/ios-filled/100/suitcase.png" alt="Luggage" class="mx-auto mb-4" />
      <h2 class="text-xl font-bold text-blue-800 mb-2">Chưa có chuyến đi sắp tới</h2>
      <p class="text-blue-600">
        Thế giới đang chờ đợi! Hãy tìm hiểu các điểm đến và để cuộc phiêu lưu tiếp theo tìm đến quý khách.
      </p>
    </div>

    <!-- Planning Options -->
    <div class="mt-10">
      <h3 class="text-lg font-bold mb-4">Bắt đầu lên kế hoạch cho chuyến đi tiếp theo?</h3>
      <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
        <div
          v-for="option in planningOptions"
          :key="option.label"
          class="bg-white border border-blue-100 p-4 rounded-lg text-center hover:bg-blue-50 cursor-pointer transition"
        >
          <img :src="option.icon" alt="" class="w-10 h-10 mx-auto mb-2" />
          <p class="text-sm font-semibold text-blue-700">{{ option.label }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "BookingHistory",
  data() {
    return {
      tabs: ['Sắp tới', 'Hoàn tất', 'Đã hủy'],
      activeTab: 'Sắp tới',
      planningOptions: [
        { label: 'Tìm Nơi lưu trú', icon: 'https://img.icons8.com/ios/50/bed.png' },
        { label: 'Tìm Hoạt động', icon: 'https://img.icons8.com/ios/50/tourist.png' },
        { label: 'Tìm Chuyến bay', icon: 'https://img.icons8.com/ios/50/airplane-take-off.png' },
        { label: 'Tìm Đưa đón sân bay', icon: 'https://img.icons8.com/ios/50/shuttle-bus.png' },
        { label: 'Tìm Thuê xe hơi', icon: 'https://img.icons8.com/ios/50/car.png' },
        { label: 'Tìm lựa chọn di chuyển', icon: 'https://img.icons8.com/ios/50/train.png' },
        { label: 'Tìm thẻ eSIM', icon: 'https://img.icons8.com/ios/50/sim-card-chip.png' }
      ]
    };
  }
};
</script>