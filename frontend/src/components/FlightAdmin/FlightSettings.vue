<template>
  <div class="space-y-6">
    <!-- General Settings -->
    <div class="bg-white rounded-xl shadow-lg p-6">
      <h2 class="text-2xl font-bold text-gray-800 mb-6">Cài đặt chung</h2>
      
      <div class="space-y-6">
        <!-- Time Zone -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Múi giờ
          </label>
          <select
            v-model="settings.timezone"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="Asia/Ho_Chi_Minh">(GMT+7) Việt Nam</option>
            <option value="Asia/Bangkok">(GMT+7) Thái Lan</option>
            <option value="Asia/Singapore">(GMT+8) Singapore</option>
          </select>
        </div>

        <!-- Currency -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Đơn vị tiền tệ
          </label>
          <select
            v-model="settings.currency"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="VND">VND - Việt Nam Đồng</option>
            <option value="USD">USD - US Dollar</option>
            <option value="EUR">EUR - Euro</option>
          </select>
        </div>

        <!-- Date Format -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Định dạng ngày tháng
          </label>
          <select
            v-model="settings.dateFormat"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="DD/MM/YYYY">DD/MM/YYYY</option>
            <option value="MM/DD/YYYY">MM/DD/YYYY</option>
            <option value="YYYY-MM-DD">YYYY-MM-DD</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Notification Settings -->
    <div class="bg-white rounded-xl shadow-lg p-6">
      <h2 class="text-2xl font-bold text-gray-800 mb-6">Cài đặt thông báo</h2>
      
      <div class="space-y-6">
        <!-- Email Notifications -->
        <div>
          <h3 class="text-lg font-medium text-gray-900 mb-4">Thông báo qua email</h3>
          <div class="space-y-4">
            <label class="flex items-center">
              <input
                type="checkbox"
                v-model="settings.notifications.email.newBooking"
                class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              <span class="ml-2 text-sm text-gray-700">Thông báo khi có đặt chỗ mới</span>
            </label>
            <label class="flex items-center">
              <input
                type="checkbox"
                v-model="settings.notifications.email.bookingCancelled"
                class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              <span class="ml-2 text-sm text-gray-700">Thông báo khi hủy đặt chỗ</span>
            </label>
            <label class="flex items-center">
              <input
                type="checkbox"
                v-model="settings.notifications.email.flightDelayed"
                class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              <span class="ml-2 text-sm text-gray-700">Thông báo khi chuyến bay bị trễ</span>
            </label>
          </div>
        </div>

        <!-- SMS Notifications -->
        <div>
          <h3 class="text-lg font-medium text-gray-900 mb-4">Thông báo qua SMS</h3>
          <div class="space-y-4">
            <label class="flex items-center">
              <input
                type="checkbox"
                v-model="settings.notifications.sms.newBooking"
                class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              <span class="ml-2 text-sm text-gray-700">Thông báo khi có đặt chỗ mới</span>
            </label>
            <label class="flex items-center">
              <input
                type="checkbox"
                v-model="settings.notifications.sms.bookingCancelled"
                class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              <span class="ml-2 text-sm text-gray-700">Thông báo khi hủy đặt chỗ</span>
            </label>
            <label class="flex items-center">
              <input
                type="checkbox"
                v-model="settings.notifications.sms.flightDelayed"
                class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              <span class="ml-2 text-sm text-gray-700">Thông báo khi chuyến bay bị trễ</span>
            </label>
          </div>
        </div>
      </div>
    </div>

    <!-- Booking Settings -->
    <div class="bg-white rounded-xl shadow-lg p-6">
      <h2 class="text-2xl font-bold text-gray-800 mb-6">Cài đặt đặt chỗ</h2>
      
      <div class="space-y-6">
        <!-- Booking Window -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Thời gian đặt chỗ trước chuyến bay (giờ)
          </label>
          <input
            type="number"
            v-model="settings.booking.minHoursBeforeFlight"
            min="1"
            max="72"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          />
        </div>

        <!-- Cancellation Policy -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Chính sách hủy chuyến bay
          </label>
          <select
            v-model="settings.booking.cancellationPolicy"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="flexible">Linh hoạt - Hoàn tiền đầy đủ</option>
            <option value="moderate">Trung bình - Hoàn 50%</option>
            <option value="strict">Nghiêm ngặt - Không hoàn tiền</option>
          </select>
        </div>

        <!-- Seat Selection -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Cho phép chọn ghế
          </label>
          <div class="space-y-4">
            <label class="flex items-center">
              <input
                type="checkbox"
                v-model="settings.booking.allowSeatSelection"
                class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              <span class="ml-2 text-sm text-gray-700">Cho phép hành khách chọn ghế khi đặt chỗ</span>
            </label>
          </div>
        </div>
      </div>
    </div>

    <!-- Save Button -->
    <div class="flex justify-end">
      <button
        @click="saveSettings"
        class="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
      >
        <i class="fas fa-save mr-2"></i>
        Lưu cài đặt
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// State
const settings = ref({
  timezone: 'Asia/Ho_Chi_Minh',
  currency: 'VND',
  dateFormat: 'DD/MM/YYYY',
  notifications: {
    email: {
      newBooking: true,
      bookingCancelled: true,
      flightDelayed: true
    },
    sms: {
      newBooking: false,
      bookingCancelled: true,
      flightDelayed: true
    }
  },
  booking: {
    minHoursBeforeFlight: 24,
    cancellationPolicy: 'moderate',
    allowSeatSelection: true
  }
})

// Methods
const saveSettings = () => {
  // Implement save functionality
  console.log('Saving settings:', settings.value)
}
</script> 