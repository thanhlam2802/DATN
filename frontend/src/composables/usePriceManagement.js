import { ref, computed, watch } from 'vue'
import { PriceAPI, PriceStatus, RouteAPI, BusCategoryAPI } from '@/api/busApi'

export function usePriceManagement() {
  // === REACTIVE STATE ===
  
  // Data
  const prices = ref([])
  const allRoutes = ref([])
  const allBusCategories = ref([])
  
  // UI State
  const loading = ref(false)
  const error = ref(null)
  const isInitialized = ref(false)
  
  // Form State
  const priceForm = ref({
    routeId: '',
    busCategoryId: '',
    basePrice: 0,
    promotionPrice: null,
    validFrom: '',
    validTo: '',
    notes: ''
  })
  
  // Filter State
  const filters = ref({
    route: '',
    busCategory: '',
    status: '',
    dateRange: null
  })
  
  // Bulk Update State
  const bulkUpdate = ref({
    type: '',
    value: 0,
    applyTo: '',
    routeId: '',
    busCategoryId: ''
  })

  // Editing State
  const editingPriceId = ref(null)
  
  // === COMPUTED PROPERTIES ===
  
  // Filtered prices based on current filters
  const filteredPrices = computed(() => {
    let filtered = prices.value
    
    // Filter by route
    if (filters.value.route) {
      filtered = filtered.filter(price => price.route.id === filters.value.route)
    }
    
    // Filter by bus category
    if (filters.value.busCategory) {
      filtered = filtered.filter(price => price.busCategory.id === filters.value.busCategory)
    }
    
    // Filter by status
    if (filters.value.status) {
      filtered = filtered.filter(price => {
        const status = PriceAPI.calculatePriceStatus(price)
        return status === filters.value.status
      })
    }
    
    // Filter by date range
    if (filters.value.dateRange) {
      const { from, to } = filters.value.dateRange
      filtered = filtered.filter(price => {
        if (from && price.validTo < from) return false
        if (to && price.validFrom > to) return false
        return true
      })
    }
    
    return filtered
  })
  
  // Price statistics
  const priceStats = computed(() => {
    const allPrices = prices.value
    
    return {
      total: allPrices.length,
      active: allPrices.filter(price => PriceAPI.calculatePriceStatus(price) === PriceStatus.ACTIVE).length,
      scheduled: allPrices.filter(price => PriceAPI.calculatePriceStatus(price) === PriceStatus.SCHEDULED).length,
      expired: allPrices.filter(price => PriceAPI.calculatePriceStatus(price) === PriceStatus.EXPIRED).length,
      averageBasePrice: allPrices.length > 0 
        ? Math.round(allPrices.reduce((sum, price) => sum + price.basePrice, 0) / allPrices.length)
        : 0,
      totalPromotions: allPrices.filter(price => price.promotionPrice && price.promotionPrice > 0).length
    }
  })

  // Get prices for bulk update based on current selection
  const getBulkUpdateTargets = computed(() => {
    const { applyTo, routeId, busCategoryId } = bulkUpdate.value
    
    if (applyTo === 'all') {
      return prices.value
    } else if (applyTo === 'route' && routeId) {
      return prices.value.filter(price => price.route.id === routeId)
    } else if (applyTo === 'busCategory' && busCategoryId) {
      return prices.value.filter(price => price.busCategory.id === busCategoryId)
    }
    
    return []
  })

  // === DATA LOADING ===
  
  async function loadAllPrices() {
    try {
      loading.value = true
      console.log('🔄 [PriceManagement] Loading all prices...')
      
      const fetchedPrices = await PriceAPI.findAllPrices()
      prices.value = fetchedPrices
      
      console.log(`✅ [PriceManagement] Loaded ${fetchedPrices.length} prices`)
    } catch (err) {
      console.error('❌ [PriceManagement] Error loading prices:', err)
      error.value = err.message || 'Lỗi tải danh sách giá vé'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function loadAllRoutes() {
    try {
      console.log('🔄 [PriceManagement] Loading all routes...')
      const fetchedRoutes = await RouteAPI.getAllRoutes()
      allRoutes.value = fetchedRoutes
      console.log(`✅ [PriceManagement] Loaded ${fetchedRoutes.length} routes`)
    } catch (err) {
      console.error('❌ [PriceManagement] Error loading routes:', err)
      // Don't throw - routes are supplementary data
    }
  }

  async function loadAllBusCategories() {
    try {
      console.log('🔄 [PriceManagement] Loading all bus categories...')
      const fetchedCategories = await BusCategoryAPI.getAllBusCategories()
      allBusCategories.value = fetchedCategories
      console.log(`✅ [PriceManagement] Loaded ${fetchedCategories.length} bus categories`)
    } catch (err) {
      console.error('❌ [PriceManagement] Error loading bus categories:', err)
      // Don't throw - categories are supplementary data
    }
  }

  // === CRUD OPERATIONS ===
  
  async function createPrice() {
    try {
      loading.value = true
      console.log('➕ [PriceManagement] Creating new price:', priceForm.value)
      
      // Validate form
      validatePriceForm()
      
      // Check for time conflicts
      const conflictCheck = PriceAPI.checkTimeConflict(
        prices.value,
        {
          routeId: priceForm.value.routeId,
          busCategoryId: priceForm.value.busCategoryId,
          validFrom: priceForm.value.validFrom,
          validTo: priceForm.value.validTo
        }
      )
      
      if (conflictCheck.hasConflict) {
        const conflictMessages = conflictCheck.conflictingPrices.map(price => 
          `${price.route.origin} → ${price.route.destination} - ${price.busCategory.name} (${price.validFrom} đến ${price.validTo})`
        )
        throw new Error(`Xung đột thời gian với các quy tắc giá sau:\n${conflictMessages.join('\n')}`)
      }
      
      const createdPrice = await PriceAPI.createPrice(priceForm.value)
      prices.value.push(createdPrice)
      
      resetForm()
      console.log('✅ [PriceManagement] Price created successfully')
      return createdPrice
    } catch (err) {
      console.error('❌ [PriceManagement] Error creating price:', err)
      error.value = err.message || 'Lỗi tạo quy tắc giá'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updatePrice() {
    try {
    if (!editingPriceId.value) {
        throw new Error('Không có quy tắc giá nào đang được chỉnh sửa')
    }
    
      loading.value = true
      console.log(`🔄 [PriceManagement] Updating price ${editingPriceId.value}:`, priceForm.value)
      
      // Validate form
      validatePriceForm()
      
      // Check for time conflicts (excluding current price)
      const conflictCheck = PriceAPI.checkTimeConflict(
        prices.value,
        {
          routeId: priceForm.value.routeId,
          busCategoryId: priceForm.value.busCategoryId,
          validFrom: priceForm.value.validFrom,
          validTo: priceForm.value.validTo
        },
        editingPriceId.value
      )
      
      if (conflictCheck.hasConflict) {
        const conflictMessages = conflictCheck.conflictingPrices.map(price => 
          `${price.route.origin} → ${price.route.destination} - ${price.busCategory.name} (${price.validFrom} đến ${price.validTo})`
        )
        throw new Error(`Xung đột thời gian với các quy tắc giá sau:\n${conflictMessages.join('\n')}`)
      }
      
      const updatedPrice = await PriceAPI.updatePrice(editingPriceId.value, priceForm.value)
      
      // Update in local state
      const index = prices.value.findIndex(price => price.id === editingPriceId.value)
      if (index !== -1) {
        prices.value[index] = updatedPrice
      }
      
      resetForm()
      console.log('✅ [PriceManagement] Price updated successfully')
      return updatedPrice
    } catch (err) {
      console.error('❌ [PriceManagement] Error updating price:', err)
      error.value = err.message || 'Lỗi cập nhật quy tắc giá'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  async function deletePrice(priceId) {
    try {
      loading.value = true
      console.log(`🗑️ [PriceManagement] Deleting price: ${priceId}`)
      
      const success = await PriceAPI.deletePrice(priceId)
      
      if (success) {
        // Remove from local state
        prices.value = prices.value.filter(price => price.id !== priceId)
        console.log('✅ [PriceManagement] Price deleted successfully')
      } else {
        throw new Error('Không thể xóa quy tắc giá')
      }
      
      return success
    } catch (err) {
      console.error('❌ [PriceManagement] Error deleting price:', err)
      error.value = err.message || 'Lỗi xóa quy tắc giá'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // === BULK OPERATIONS ===
  
  async function applyBulkUpdate() {
    try {
      loading.value = true
      console.log('🔄 [PriceManagement] Applying bulk update:', bulkUpdate.value)
      
      const targetPrices = getBulkUpdateTargets.value
      
      if (targetPrices.length === 0) {
        throw new Error('Không có quy tắc giá nào được chọn cho cập nhật hàng loạt')
      }
      
      const priceIds = targetPrices.map(price => price.id)
      const updatedPrices = await PriceAPI.bulkUpdatePrices(
        priceIds,
        bulkUpdate.value.type,
        bulkUpdate.value.value
      )
      
      // Update local state
      updatedPrices.forEach(updatedPrice => {
        const index = prices.value.findIndex(price => price.id === updatedPrice.id)
        if (index !== -1) {
          prices.value[index] = updatedPrice
        }
      })
      
      resetBulkUpdate()
      console.log(`✅ [PriceManagement] Bulk updated ${updatedPrices.length} prices`)
      return updatedPrices
    } catch (err) {
      console.error('❌ [PriceManagement] Error in bulk update:', err)
      error.value = err.message || 'Lỗi cập nhật hàng loạt'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // === BUSINESS LOGIC ===
  
  async function findActivePriceForTrip(routeId, busCategoryId, date) {
    try {
      console.log(`💰 [PriceManagement] Finding active price for route ${routeId}, category ${busCategoryId}, date ${date}`)
      
      const result = await PriceAPI.findActivePrice({
        routeId,
        busCategoryId,
        date
      })
      
      console.log('💰 [PriceManagement] Price calculation result:', result)
      return result
    } catch (err) {
      console.error('❌ [PriceManagement] Error finding active price:', err)
      throw err
    }
  }

  // === FORM MANAGEMENT ===
    
  function setEditingPrice(price) {
    editingPriceId.value = price.id
    priceForm.value = {
      routeId: price.route.id,
      busCategoryId: price.busCategory.id,
      basePrice: price.basePrice,
      promotionPrice: price.promotionPrice,
      validFrom: price.validFrom,
      validTo: price.validTo,
      notes: price.notes || ''
    }
    console.log('✏️ [PriceManagement] Set editing price:', price.id)
  }

  function resetForm() {
    editingPriceId.value = null
    priceForm.value = {
      routeId: '',
      busCategoryId: '',
      basePrice: 0,
      promotionPrice: null,
      validFrom: '',
      validTo: '',
      notes: ''
    }
    console.log('🔄 [PriceManagement] Form reset')
  }

  function resetBulkUpdate() {
    bulkUpdate.value = {
      type: '',
      value: 0,
      applyTo: '',
      routeId: '',
      busCategoryId: ''
    }
    console.log('🔄 [PriceManagement] Bulk update reset')
  }
  
  function applyFilters() {
    console.log('🔍 [PriceManagement] Applying filters:', filters.value)
    // Filters are applied automatically via computed property
  }

  function clearFilters() {
    filters.value = {
      route: '',
      busCategory: '',
      status: '',
      dateRange: null
    }
    console.log('🧹 [PriceManagement] Filters cleared')
  }
  
  // === VALIDATION ===
  
  function validatePriceForm() {
    const { routeId, busCategoryId, basePrice, validFrom, validTo, promotionPrice } = priceForm.value
    
    if (!routeId) throw new Error('Vui lòng chọn tuyến đường')
    if (!busCategoryId) throw new Error('Vui lòng chọn loại xe')
    if (!basePrice || basePrice <= 0) throw new Error('Giá cơ sở phải lớn hơn 0')
    if (!validFrom) throw new Error('Vui lòng chọn ngày bắt đầu hiệu lực')
    if (!validTo) throw new Error('Vui lòng chọn ngày kết thúc hiệu lực')
    if (validFrom > validTo) throw new Error('Ngày bắt đầu không thể lớn hơn ngày kết thúc')
    if (promotionPrice !== null && promotionPrice !== undefined && promotionPrice <= 0) {
      throw new Error('Giá khuyến mãi phải lớn hơn 0 hoặc để trống')
    }
  }
  
  // === UTILITY FUNCTIONS ===
  
  function formatPrice(price) {
    return PriceAPI.formatPrice(price)
  }

  function getPriceStatus(price) {
    return PriceAPI.calculatePriceStatus(price)
  }

  function clearError() {
    error.value = null
  }
  
  // === INITIALIZATION ===
  
  async function initialize() {
    try {
      console.log('🚀 [PriceManagement] Initializing...')
      loading.value = true
      
      await Promise.all([
        loadAllPrices(),
        loadAllRoutes(),
        loadAllBusCategories()
      ])
      
      isInitialized.value = true
      console.log('✅ [PriceManagement] Initialization completed')
    } catch (err) {
      console.error('❌ [PriceManagement] Initialization failed:', err)
      error.value = err.message || 'Lỗi khởi tạo module quản lý giá'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  function cleanup() {
    console.log('🧹 [PriceManagement] Cleaning up...')
    // Reset all state
    prices.value = []
    allRoutes.value = []
    allBusCategories.value = []
    resetForm()
    resetBulkUpdate()
    clearFilters()
    clearError()
    isInitialized.value = false
  }
  
  // === WATCHERS ===
  
  // Auto-set validTo to 1 year from validFrom if not set
  watch(() => priceForm.value.validFrom, (newValidFrom) => {
    if (newValidFrom && !priceForm.value.validTo) {
      const fromDate = new Date(newValidFrom)
      const toDate = new Date(fromDate.getFullYear() + 1, fromDate.getMonth(), fromDate.getDate())
      priceForm.value.validTo = toDate.toISOString().split('T')[0]
      console.log('📅 [PriceManagement] Auto-set validTo to 1 year from validFrom')
    }
  })
  
  // === RETURN PUBLIC API ===

  return {
    // State
    prices,
    allRoutes,
    allBusCategories,
    loading,
    error,
    isInitialized,
    
    // Forms
    priceForm,
    filters,
    bulkUpdate,
    editingPriceId,
    
    // Computed
    filteredPrices,
    priceStats,
    getBulkUpdateTargets,
    
    // Data loading
    loadAllPrices,
    loadAllRoutes,
    loadAllBusCategories,
    
    // CRUD operations
    createPrice,
    updatePrice,
    deletePrice,
    
    // Bulk operations
    applyBulkUpdate,
    
    // Business logic
    findActivePriceForTrip,
    
    // Form management
    setEditingPrice,
    resetForm,
    resetBulkUpdate,
    applyFilters,
    clearFilters,
    
    // Utilities
    formatPrice,
    getPriceStatus,
    clearError,
    
    // Lifecycle
    initialize,
    cleanup
  }
} 