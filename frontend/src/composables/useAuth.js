import { ref, computed } from 'vue'

// Hardcoded user for bus management
const HARDCODED_USER_ID = 11

export const useAuth = () => {
  // State
  const isAuthenticated = ref(true) // Always authenticated for bus management
  const user = ref({
    id: HARDCODED_USER_ID,
    name: 'Bus Manager',
    email: 'bus.manager@example.com'
  })
  const userRole = ref('BUS_SUPPLIER') // Hardcoded role for bus management

  // Computed properties
  const isAdmin = computed(() => false) // Not admin
  const isBusAdmin = computed(() => true) // Is bus supplier/admin

  // Methods
  const checkAuth = async () => {
    // Always return true for bus management
    return Promise.resolve(true)
  }

  const logout = () => {
    // Reset to default state
    isAuthenticated.value = false
    user.value = null
    userRole.value = null
  }

  return {
    // State
    isAuthenticated,
    user,
    userRole,
    
    // Computed
    isAdmin,
    isBusAdmin,
    
    // Methods
    checkAuth,
    logout
  }
} 