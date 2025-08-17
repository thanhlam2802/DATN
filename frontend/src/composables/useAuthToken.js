import { ref, computed } from 'vue'
import { getUserIdFromToken, isTokenValid, getAccessToken } from '@/services/TokenService'

/**
 * Composable for authentication token management
 * Tập trung quản lý authentication cho toàn bộ ứng dụng
 */
export const useAuthToken = () => {
  // Reactive state
  const isAuthenticated = computed(() => isTokenValid())
  const userId = computed(() => getUserIdFromToken())
  const userIdString = computed(() => userId.value?.toString() || null)
  const token = computed(() => getAccessToken())

  /**
   * Kiểm tra user có được authentication không
   * @returns {boolean}
   */
  const checkAuth = () => {
    return isAuthenticated.value && userId.value
  }

  /**
   * Lấy userId cho API calls, throw error nếu không có auth
   * @returns {string} User ID
   * @throws {Error} Nếu user chưa đăng nhập
   */
  const requireUserId = () => {
    if (!isAuthenticated.value) {
      throw new Error('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.')
    }
    
    const id = userIdString.value
    if (!id) {
      throw new Error('Không thể xác thực người dùng. Vui lòng đăng nhập lại.')
    }
    
    return id
  }

  /**
   * Lấy userId cho API calls, return fallback nếu không có auth
   * @param {string|number} fallback - Fallback user ID (chỉ dùng cho development)
   * @returns {string} User ID hoặc fallback
   */
  const getUserIdOrFallback = (fallback = null) => {
    if (process.env.NODE_ENV === 'development' && fallback) {
      console.warn('🔶 [DEV] Using fallback userId:', fallback)
      return userIdString.value || fallback.toString()
    }
    
    return userIdString.value || null
  }

  /**
   * Kiểm tra và redirect nếu user chưa đăng nhập
   * @param {Object} router - Vue router instance
   */
  const requireAuthOrRedirect = (router) => {
    if (!checkAuth()) {
      router.push('/login')
      return false
    }
    return true
  }

  /**
   * Debug authentication state
   */
  const debugAuth = () => {
    console.log('🔍 [AUTH DEBUG]', {
      isAuthenticated: isAuthenticated.value,
      userId: userId.value,
      userIdString: userIdString.value,
      hasToken: !!token.value
    })
  }

  return {
    // Computed properties
    isAuthenticated,
    userId,
    userIdString,
    token,
    
    // Methods
    checkAuth,
    requireUserId,
    getUserIdOrFallback,
    requireAuthOrRedirect,
    debugAuth
  }
}

// Export singleton instance for global usage
const globalAuthToken = useAuthToken()
export default globalAuthToken
