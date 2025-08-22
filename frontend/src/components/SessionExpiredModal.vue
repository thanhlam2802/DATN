<template>
  <div 
    v-if="isVisible" 
    class="fixed inset-0 z-[9999] flex items-center justify-center bg-black bg-opacity-60"
    @click.self="handleBackdropClick"
  >
    <div class="bg-white rounded-lg shadow-2xl max-w-md w-full mx-4 transform transition-all">
      <!-- Header -->
      <div class="flex items-center justify-between p-6 border-b border-gray-200">
        <div class="flex items-center space-x-3">
          <div class="w-10 h-10 bg-red-100 rounded-full flex items-center justify-center">
            <svg class="w-6 h-6 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                    d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.98-.833-2.75 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
            </svg>
          </div>
          <h3 class="text-lg font-semibold text-gray-900">
            Phiên đăng nhập đã hết hạn
          </h3>
        </div>
      </div>

      <!-- Content -->
      <div class="p-6">
        <p class="text-gray-600 mb-4">
          Phiên đăng nhập của bạn đã hết hạn. Vui lòng đăng nhập lại để tiếp tục sử dụng hệ thống.
        </p>
        <div class="bg-yellow-50 border border-yellow-200 rounded-md p-3">
          <div class="flex">
            <svg class="w-5 h-5 text-yellow-400 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
            </svg>
            <div class="ml-3">
              <p class="text-sm text-yellow-800">
                Tất cả dữ liệu chưa lưu sẽ bị mất.
              </p>
            </div>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="flex items-center justify-end space-x-3 p-6 border-t border-gray-200 bg-gray-50">
        <button
          @click="handleRedirectToLogin"
          class="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-colors"
        >
          Đăng nhập lại
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'SessionExpiredModal',
  setup() {
    const isVisible = ref(false)
    const router = useRouter()

    const show = () => {
      isVisible.value = true
    }

    const hide = () => {
      isVisible.value = false
    }

    const handleBackdropClick = () => {
      // Không cho phép đóng modal bằng click backdrop
      // Buộc user phải redirect để đăng nhập lại
    }

    const handleRedirectToLogin = () => {
      // Clear token và redirect
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      
      // Clear any other session data
      sessionStorage.clear()
      
      hide()
      
      // Redirect to login page
      router.push('/login')
      
      // Optional: Reload page để reset app state
      setTimeout(() => {
        window.location.reload()
      }, 100)
    }

    return {
      isVisible,
      show,
      hide,
      handleBackdropClick,
      handleRedirectToLogin
    }
  }
}
</script>

<style scoped>
/* Additional animations if needed */
.transform {
  transform: scale(1);
}

/* Prevent scrolling when modal is open */
.fixed {
  overflow: hidden;
}
</style>
