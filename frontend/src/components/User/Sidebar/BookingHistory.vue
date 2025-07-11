<template>
  <div class="max-w-4xl mx-auto font-sans text-sm text-blue-900">
    <!-- Tabs -->
    <div class="grid grid-cols-3 text-center border-b border-blue-200 mb-4">
      <button
        v-for="tab in tabs"
        :key="tab"
        @click="activeTab = tab"
        :class="[
          'py-2 font-semibold border-b-2 transition',
          activeTab === tab
            ? 'border-blue-600 text-blue-600'
            : 'border-transparent text-gray-500 hover:text-blue-600'
        ]"
      >
        {{ tab }}
      </button>
    </div>

    <!-- Toggle View Buttons -->
    <div class="grid grid-cols-2 gap-4 mb-6">
      <button
        :class="[
          'w-full px-4 py-2 rounded-full text-sm font-medium flex items-center justify-center gap-2 border transition',
          viewMode === 'Hành trình'
            ? 'bg-blue-100 text-blue-600 border-blue-300'
            : 'bg-white text-blue-600 border-blue-300 hover:bg-blue-50'
        ]"
        @click="viewMode = 'Hành trình'"
      >
        <img src="https://img.icons8.com/ios-filled/24/000000/around-the-globe.png" class="h-4 w-4 text-blue-600" alt="route icon" />
        Hành trình
      </button>
      <button
        :class="[
          'w-full px-4 py-2 rounded-full text-sm font-medium flex items-center justify-center gap-2 border transition',
          viewMode === 'Danh sách'
            ? 'bg-blue-100 text-blue-600 border-blue-300'
            : 'bg-white text-blue-600 border-blue-300 hover:bg-blue-50'
        ]"
        @click="viewMode = 'Danh sách'"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-blue-600" viewBox="0 0 24 24"><path d="M3 4h18v2H3V4zm0 6h18v2H3v-2zm0 6h18v2H3v-2z"/></svg>
        Danh sách
      </button>
    </div>

    <!-- Empty State -->
    <div class="text-center bg-blue-50 p-6 rounded-lg border border-blue-100 mb-8">
      <img src="https://img.icons8.com/ios-filled/100/suitcase.png" alt="Luggage" class="mx-auto mb-4" />
      <h2 class="text-xl font-bold text-blue-800 mb-1">Chưa có chuyến đi sắp tới</h2>
      <p class="text-blue-600 text-sm">
        Thế giới đang chờ đợi! Hãy tìm hiểu các điểm đến và để cuộc phiêu lưu tiếp theo tìm đến quý khách.
      </p>
    </div>

    <!-- Planning Options -->
    <div>
      <h3 class="text-lg font-bold mb-4">Bắt đầu lên kế hoạch cho chuyến đi tiếp theo?</h3>
      <div class="grid grid-cols-2 sm:grid-cols-3 gap-4">
        <div
          v-for="option in planningOptions"
          :key="option.label"
          class="bg-blue-50 border border-blue-200 p-4 rounded-lg text-center hover:bg-white cursor-pointer transition"
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
  name: "BookingEmptyState",
  data() {
    return {
      tabs: ['Sắp tới', 'Hoàn tất', 'Đã hủy'],
      activeTab: 'Sắp tới',
      viewMode: 'Hành trình',
      planningOptions: [
        { label: 'Tìm Nơi lưu trú', icon: 'https://img.icons8.com/ios/50/bed.png' },
        { label: 'Tìm Chuyến bay', icon: 'https://img.icons8.com/ios/50/airplane-take-off.png' },
        { label: 'Tìm lựa chọn di chuyển', icon: 'https://img.icons8.com/ios/50/bus.png' },
      ]
    };
  }
};
</script>

<style scoped>
</style>
