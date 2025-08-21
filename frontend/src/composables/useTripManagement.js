import { ref, computed, watch } from 'vue'
import { BusSlotAPI, BusSlotStatus, DelayReason } from '@/api/busApi/busSlot'
import { graphqlRequest, gql } from '@/api/graphqlClient'
import { RouteAPI } from '@/api/busApi/route/api'
import { useAuth } from '@/composables/useAuth'

// GraphQL queries cho buses
const GET_BUSES_BY_OWNER = gql`
  query GetBusesByOwner($ownerId: ID!) {
    findBusesByOwnerId(ownerId: $ownerId) {
      id
      name
      licensePlate
      totalSeats
      categoryName
    }
  }
`

// ✅ THÊM: GraphQL query cho bus slots by owner
const GET_BUS_SLOTS_BY_OWNER = gql`
  query GetBusSlotsByOwner($ownerId: ID!) {
    findBusSlotsByOwnerId(ownerId: $ownerId) {
      id
      bus {
        id
        name
        licensePlate
      }
      route {
        id
        originLocation {
          id
          name
          provinceCity
          district
        }
        destinationLocation {
          id
          name
          provinceCity
          district
        }
      }
      ownerId
      slotDate
      departureTime
      arrivalTime
      price
      totalSeats
      availableSeats
      status
      createdAt
      updatedAt
    }
  }
`

export function useTripManagement() {
  // Core State
  const busSlots = ref([])
  const allBuses = ref([]) // Store all buses
  const allRoutes = ref([]) // Store all routes
  
  // Loading States
  const loading = ref(false)
  const loadingBuses = ref(false)
  const loadingRoutes = ref(false)
  const syncLoading = ref(false)
  const loadingTripIds = ref({}) // Theo dõi loading state của từng trip
  
  // Error Handling
  const error = ref(null)
  
  // Auto Management
  const autoManagerEnabled = ref(true)
  const autoManagerInterval = ref(null)
  
  // Form State
  const tripForm = ref({
    busId: '',
    routeId: '',
    slotDate: '',
    departureTime: '',
    arrivalTime: '',
    price: 500000,
    totalSeats: 40
  })
  
  // Computed filtered data based on selected date/time
  const availableBuses = computed(() => {
    if (!tripForm.value.slotDate || !tripForm.value.departureTime) {
      return allBuses.value
    }
    
    // Filter buses that are not conflicting with existing trips
    return allBuses.value.filter(bus => {
      return !hasConflictingTrip(bus.id, tripForm.value.slotDate, tripForm.value.departureTime)
    })
  })
  
  const availableRoutes = computed(() => {
    // For now, return all routes. Can be filtered based on bus selection if needed
    return allRoutes.value
  })
  
  // Helper function to check if bus has conflicting trip TRONG CÙNG OWNER
  function hasConflictingTrip(busId, slotDate, departureTime, arrivalTime) {
    const { requireUserId } = useAuth()
    const currentOwnerId = requireUserId()
    
    const conflictingSlots = busSlots.value.filter(slot => {
      // Skip completed or cancelled trips
      if (slot.status === BusSlotStatus.COMPLETED || slot.status === BusSlotStatus.CANCELLED) {
        return false
      }
      
      // Skip if editing current trip
      if (editingTripId.value && slot.id === editingTripId.value) {
        return false
      }
      
      // ✅ MULTI-TENANT: Chỉ check conflict trong cùng owner
      if (String(slot.ownerId) !== String(currentOwnerId)) {
        return false // Skip trips từ owners khác
      }
      
      // Check same bus and same date
      if (slot.bus?.id !== busId || slot.slotDate !== slotDate) {
        return false
      }
      
      // ✅ NEW LOGIC: Check thời gian overlap thông minh
      const existingStart = timeToMinutes(slot.departureTime)
      const existingEnd = timeToMinutes(slot.arrivalTime)
      const newStart = timeToMinutes(departureTime)
      const newEnd = timeToMinutes(arrivalTime)
      
      // Kiểm tra overlap: Chuyến mới overlap với chuyến hiện tại
      // Overlap xảy ra khi: newStart < existingEnd && newEnd > existingStart
      const hasOverlap = newStart < existingEnd && newEnd > existingStart
      
      if (hasOverlap) {
        console.warn(`⚠️ [VALIDATION] Time overlap detected:`, {
          existing: `${slot.departureTime} - ${slot.arrivalTime}`,
          new: `${departureTime} - ${arrivalTime}`,
          busId,
          slotDate
        })
        return true
      }
      
      return false
    })
    
    return conflictingSlots.length > 0
  }
  
  // ✅ NEW LOGIC: Cho phép nhiều chuyến cùng xe, cùng tuyến, cùng ngày
  // Chỉ kiểm tra time overlap thông qua hasConflictingTrip
  function hasDuplicateTrip(busId, routeId, slotDate, departureTime, arrivalTime) {
    if (!busId || !routeId || !slotDate || !departureTime || !arrivalTime) return false
    
    // ✅ CHỈ CHECK TIME OVERLAP - không còn restrict exact match
    return hasConflictingTrip(busId, slotDate, departureTime, arrivalTime)
  }
  
  // Helper function để lấy tất cả chuyến xe của một bus trong một ngày TRONG CÙNG OWNER
  function getTripsForBusAndDate(busId, slotDate) {
    if (!busId || !slotDate) return []
    
    const { requireUserId } = useAuth()
    const currentOwnerId = requireUserId()
    
    return busSlots.value.filter(slot => {
      return slot.bus?.id === busId && 
             slot.slotDate === slotDate &&
             String(slot.ownerId) === String(currentOwnerId) && // ✅ MULTI-TENANT filter
             ['SCHEDULED', 'IN_PROGRESS'].includes(slot.status)
    })
  }

  function timeToMinutes(timeString) {
    if (!timeString) return 0
    const timeParts = timeString.split(':')
    const hours = parseInt(timeParts[0])
    const minutes = parseInt(timeParts[1])
    return hours * 60 + minutes
  }

  function minutesToTime(minutes) {
    const hours = Math.floor(minutes / 60)
    const mins = minutes % 60
    return `${String(hours).padStart(2, '0')}:${String(mins).padStart(2, '0')}`
  }

  // ✅ NEW: Tìm thời gian available tiếp theo cho bus
  function getNextAvailableTime(busId, slotDate, preferredDepartureTime) {
    const { requireUserId } = useAuth()
    const currentOwnerId = requireUserId()
    
    const busTripsOnDate = busSlots.value.filter(slot => {
      return String(slot.bus?.id) === String(busId) && 
             slot.slotDate === slotDate &&
             String(slot.ownerId) === String(currentOwnerId) &&
             ['SCHEDULED', 'IN_PROGRESS'].includes(slot.status)
    }).sort((a, b) => timeToMinutes(a.departureTime) - timeToMinutes(b.departureTime))

    if (busTripsOnDate.length === 0) {
      return preferredDepartureTime // Không có chuyến nào, dùng thời gian mong muốn
    }

    const preferredMinutes = timeToMinutes(preferredDepartureTime)
    
    // Tìm slot trống phù hợp
    for (let i = 0; i < busTripsOnDate.length; i++) {
      const currentTrip = busTripsOnDate[i]
      const currentEnd = timeToMinutes(currentTrip.arrivalTime)
      
      // Check nếu preferred time có thể fit trước chuyến đầu tiên
      if (i === 0) {
        const firstStart = timeToMinutes(currentTrip.departureTime)
        if (preferredMinutes + 60 <= firstStart) { // 1h buffer
          return preferredDepartureTime
        }
      }
      
      // Check gap giữa các chuyến
      if (i < busTripsOnDate.length - 1) {
        const nextTrip = busTripsOnDate[i + 1]
        const nextStart = timeToMinutes(nextTrip.departureTime)
        const gapMinutes = nextStart - currentEnd
        
        if (gapMinutes >= 120) { // Cần ít nhất 2h gap cho 1 chuyến mới
          const suggestedStart = currentEnd + 30 // 30 phút nghỉ
          if (suggestedStart >= preferredMinutes) {
            return minutesToTime(suggestedStart)
          }
        }
      }
    }
    
    // Không tìm được gap, suggest sau chuyến cuối
    const lastTrip = busTripsOnDate[busTripsOnDate.length - 1]
    const lastEnd = timeToMinutes(lastTrip.arrivalTime)
    const suggestedStart = lastEnd + 30 // 30 phút nghỉ
    
    if (suggestedStart < 24 * 60) { // Không quá 24h
      return minutesToTime(suggestedStart)
    }
    
    return null // Không thể thêm chuyến nào trong ngày
  }
  
  // Track editing trip ID for conflict checking
  const editingTripId = ref(null)
  
  // Computed Stats
  const stats = computed(() => ({
    totalTrips: busSlots.value.length,
    activeTrips: busSlots.value.filter(slot => slot.status === BusSlotStatus.IN_PROGRESS).length,
    scheduledTrips: busSlots.value.filter(slot => slot.status === BusSlotStatus.SCHEDULED).length,
    completedTrips: busSlots.value.filter(slot => slot.status === BusSlotStatus.COMPLETED).length,
    delayedTrips: busSlots.value.filter(slot => slot.status === BusSlotStatus.DELAYED).length
  }))

  // === CORE CRUD OPERATIONS ===
  
  async function loadBusSlots() {
    try {
      loading.value = true
      error.value = null
      
      const slots = await BusSlotAPI.findAllBusSlots()
      busSlots.value = slots || []
      
    } catch (err) {
      error.value = 'Không thể tải danh sách chuyến đi'
      } finally {
      loading.value = false
    }
  }

  async function loadAllBuses() {
    try {
      loadingBuses.value = true
      
      // ✅ Sử dụng requireUserId thay vì user.value?.id
      const { requireUserId } = useAuth()
      const ownerId = requireUserId()

      const response = await graphqlRequest({
        query: GET_BUSES_BY_OWNER,
        variables: { ownerId }
      })
      
      if (!response?.data?.findBusesByOwnerId) {
        throw new Error('Dữ liệu xe bus không hợp lệ')
      }
      
      const buses = response.data.findBusesByOwnerId || []
      allBuses.value = buses.map(bus => ({
        id: bus.id,
        name: bus.name || `Xe bus #${bus.id}`,
        licensePlate: bus.licensePlate || 'Chưa có biển số',
        totalSeats: bus.totalSeats || 40,
        categoryName: bus.categoryName || 'N/A'
      }))
      
    } catch (err) {
      error.value = 'Không thể tải danh sách xe bus. Vui lòng tạo xe bus trước.'
    } finally {
      loadingBuses.value = false
    }
  }

  async function loadAllRoutes() {
    try {
      loadingRoutes.value = true
      
      // ✅ Sử dụng token động
      const { requireUserId } = useAuth()
      const ownerId = requireUserId()
      
      const routes = await RouteAPI.getRoutesByOwnerId(ownerId)
      allRoutes.value = routes.map(route => ({
        id: route.id,
        origin: route.originLocation.name,
        destination: route.destinationLocation.name,
        name: `${route.originLocation.name} - ${route.destinationLocation.name}`,
        distanceKm: route.distanceKm,
        estimatedDurationMinutes: route.estimatedDurationMinutes
      }))
      
    } catch (err) {
      console.error('Error loading routes:', err)
      error.value = 'Không thể tải danh sách tuyến đường'
    } finally {
      loadingRoutes.value = false
    }
  }

  // === TRIP LOADING ===
  
  async function loadBusSlots() {
    try {
      loading.value = true
      const { requireUserId } = useAuth()
      const ownerId = requireUserId()
      
      console.log('🚌 [TRIP] Loading bus slots for owner:', ownerId)
      
      const response = await graphqlRequest({
        query: GET_BUS_SLOTS_BY_OWNER,
        variables: { ownerId }
      })
      
      busSlots.value = response.data.findBusSlotsByOwnerId || []
      console.log('✅ [TRIP] Loaded bus slots:', busSlots.value.length)
      
    } catch (err) {
      console.error('❌ [TRIP] Load bus slots error:', err)
      error.value = err.message || 'Không thể tải danh sách chuyến đi'
      busSlots.value = []
    } finally {
      loading.value = false
    }
  }

  // === TRIP OPERATIONS ===
  
  async function createTrip(tripData) {
    try {
      loading.value = true
      
      // Validate and format data before sending to backend
      const formattedData = validateAndFormatTripData(tripData)
      const newTrip = await BusSlotAPI.createBusSlot(formattedData)
      busSlots.value = [...busSlots.value, newTrip]
      
      return newTrip
    } catch (err) {
      error.value = err.message || 'Không thể tạo chuyến đi mới'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateTrip(tripId, tripData) {
    try {
      loading.value = true
      
      // Validate and format data before sending to backend
      const formattedData = validateAndFormatTripData(tripData)
      const updatedTrip = await BusSlotAPI.updateBusSlot(tripId, formattedData)
      updateTripInUI(updatedTrip)
      
      return updatedTrip
    } catch (err) {
      error.value = err.message || 'Không thể cập nhật chuyến đi'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function deleteTrip(tripId) {
    try {
      setTripLoading(tripId, 'delete')
      
      await BusSlotAPI.deleteBusSlot(tripId)
      busSlots.value = busSlots.value.filter(trip => trip.id !== tripId)
      
      return true
    } catch (err) {
      error.value = err.message || 'Không thể xóa chuyến đi'
      throw err
    } finally {
      clearTripLoading(tripId)
    }
  }

  // === STATUS MANAGEMENT ===
  
  async function quickMarkInProgress(trip) {
    try {
      setTripLoading(trip.id, 'start')      
      
      const updatedTrip = await BusSlotAPI.quickStatusUpdate(trip.id, {
        status: BusSlotStatus.IN_PROGRESS,
        autoSetActualTime: true
      })
      
      updateTripInUI(updatedTrip)
      
      return updatedTrip
    } catch (err) {
      error.value = err.message || 'Không thể cập nhật trạng thái chuyến đi'
      throw err
    } finally {
      clearTripLoading(trip.id)
    }
  }

  async function quickMarkCompleted(trip) {
    try {
      setTripLoading(trip.id, 'complete')
      
      const updatedTrip = await BusSlotAPI.quickStatusUpdate(trip.id, {
        status: BusSlotStatus.COMPLETED,
        autoSetActualTime: true
      })
      
      updateTripInUI(updatedTrip)
      
      return updatedTrip
    } catch (err) {
      error.value = err.message || 'Không thể cập nhật trạng thái chuyến đi'
      throw err
    } finally {
      clearTripLoading(trip.id)
    }
  }

  async function updateTripStatus(tripId, statusData) {
    try {
      loading.value = true
      
      let updatedTrip
      if (statusData.actualDepartureTime || statusData.actualArrivalTime) {
        updatedTrip = await BusSlotAPI.updateActualTimes(tripId, statusData)
      } else {
        updatedTrip = await BusSlotAPI.quickStatusUpdate(tripId, statusData)
      }
      
      updateTripInUI(updatedTrip)
      
      return updatedTrip
    } catch (err) {
      error.value = err.message || 'Không thể cập nhật trạng thái chuyến đi'
      throw err
    } finally {
      loading.value = false
    }
  }

  // === AUTO MANAGEMENT ===
  
  async function manualTriggerAutoManager() {
    try {
      syncLoading.value = true
      
      const tripsNeedingUpdate = await BusSlotAPI.findTripsNeedingStatusUpdate()
      
      await loadBusSlots() // Refresh data
    } catch (err) {     
      error.value = err.message || 'Không thể đồng bộ trạng thái chuyến đi'
      throw err
    } finally {
      syncLoading.value = false
    }
  }

  // Client-side auto status check (for testing/fallback)
  async function checkAndUpdateTripStatuses() {
    try {
      const now = new Date()
      let updatesCount = 0
      
      for (const trip of busSlots.value) {
        const tripDate = trip.slotDate
        const departureTime = trip.departureTime
        const arrivalTime = trip.arrivalTime
        
        if (!tripDate || !departureTime || !arrivalTime) {
          continue
        }
        
        // Create datetime objects for comparison
        let scheduledDeparture, scheduledArrival
        
        try {
          // Handle different time formats
          if (departureTime.includes('T')) {
            // Full datetime format
            scheduledDeparture = new Date(departureTime)
            scheduledArrival = new Date(arrivalTime)
          } else {
            // Time-only format, combine with date
            scheduledDeparture = new Date(`${tripDate}T${departureTime}`)
            scheduledArrival = new Date(`${tripDate}T${arrivalTime}`)
          }
        } catch (timeError) {
          continue
        }
        
        // Add buffer time (5 minutes)
        const departureWithBuffer = new Date(scheduledDeparture.getTime() + 5 * 60 * 1000)
        const arrivalWithBuffer = new Date(scheduledArrival.getTime() + 5 * 60 * 1000)
        
        
        
        try {
          // Check if trip should start (SCHEDULED -> IN_PROGRESS)
          if (trip.status === BusSlotStatus.SCHEDULED && now >= departureWithBuffer) {
            
            
            const updatedTrip = await BusSlotAPI.quickStatusUpdate(trip.id, {
              status: BusSlotStatus.IN_PROGRESS,
              autoSetActualTime: true
            })
            
            updateTripInUI(updatedTrip)
            updatesCount++
          }
          
          // Check if trip should complete (IN_PROGRESS -> COMPLETED)
          else if (trip.status === BusSlotStatus.IN_PROGRESS && now >= arrivalWithBuffer) {
            
            
            const updatedTrip = await BusSlotAPI.quickStatusUpdate(trip.id, {
              status: BusSlotStatus.COMPLETED,
              autoSetActualTime: true
            })
            
            updateTripInUI(updatedTrip)
            updatesCount++
          }
        } catch (updateError) {
        }
      }
      
      if (updatesCount > 0) {
      } else {
      }
      
      return updatesCount
    } catch (err) {     
      throw err
    }
  }

  async function forceRefreshAllData() {
    try {
      loading.value = true
      
      // Clear current data
      busSlots.value = []
      allBuses.value = []
      allRoutes.value = []
      
      // Reload everything
      await Promise.all([
        loadBusSlots(),
        loadAllBuses(),
        loadAllRoutes()
      ])
      
      // Clear any existing errors
      error.value = null
    } catch (err) {     
      error.value = err.message || 'Không thể tải lại dữ liệu'
      throw err
    } finally {
      loading.value = false
    }
  }

  function startAutoManager() {
    if (autoManagerInterval.value) {
      clearInterval(autoManagerInterval.value)
    }
    
    if (autoManagerEnabled.value) {
      autoManagerInterval.value = setInterval(async () => {
        try {
          // Try backend auto-management first
          const tripsNeedingUpdate = await BusSlotAPI.findTripsNeedingStatusUpdate()
          if (tripsNeedingUpdate.length > 0) {
            await loadBusSlots()
          } else {
            // Fallback to client-side check
            await checkAndUpdateTripStatuses()
          }
        } catch (err) {
          // If backend fails, try client-side logic
          try {
            await checkAndUpdateTripStatuses()
          } catch (clientErr) {
          }
        }
      }, 30 * 1000) // 30 seconds interval for responsive auto-management
    }
  }

  function stopAutoManager() {
    if (autoManagerInterval.value) {
      clearInterval(autoManagerInterval.value)
      autoManagerInterval.value = null
    } else {
    }
  }

  function toggleAutoManager() {
    autoManagerEnabled.value = !autoManagerEnabled.value
    if (autoManagerEnabled.value) {
      startAutoManager()
    } else {
      stopAutoManager()
    }
  }

  // Debug function to manually trigger auto-management with detailed logging
  async function debugAutoManager() {
    try {
      
      if (busSlots.value.length === 0) {
        return
      }
      
      
      
      await checkAndUpdateTripStatuses()
    } catch (err) {
    }
  }

  // === UTILITY FUNCTIONS ===
  
  function formatTimeForBackend(timeString) {
    if (!timeString) {
      // Return default time as fallback
      return '08:00:00'
    }
    
    // If already in HH:MM:SS format, return as is
    if (timeString.match(/^\d{2}:\d{2}:\d{2}$/)) {
      return timeString
    }
    
    // If in HH:MM format, add :00 seconds
    if (timeString.match(/^\d{2}:\d{2}$/)) {
      return `${timeString}:00`
    }
    
    // Invalid format, return default
    console.warn('Invalid time format:', timeString, 'using default')
    return '08:00:00'
  }
  
  function validateAndFormatTripData(tripData) {
    const { requireUserId } = useAuth()
    
    // Ensure all required fields are present and properly formatted
    const formatted = {
      ...tripData,
      departureTime: formatTimeForBackend(tripData.departureTime),
      arrivalTime: formatTimeForBackend(tripData.arrivalTime),
      ownerId: requireUserId() // ✅ THÊM: ownerId cho multi-tenant
    }
    
    // Additional validation
    if (!formatted.busId || !formatted.routeId || !formatted.slotDate) {
      throw new Error('Thiếu thông tin bắt buộc: xe bus, tuyến đường, hoặc ngày khởi hành')
    }

    // ✅ NEW VALIDATION: Check time overlap thay vì exact duplicate
    if (hasDuplicateTrip(formatted.busId, formatted.routeId, formatted.slotDate, formatted.departureTime, formatted.arrivalTime)) {
      throw new Error('Thời gian chuyến xe bị trùng lặp! Vui lòng chọn thời gian khác để tránh xung đột với chuyến đã có.')
    }

    console.log('🚌 [TRIP] Formatted trip data with ownerId:', formatted.ownerId)
    return formatted
  }

  function updateTripInUI(updatedTrip) {
    try {
      
      
      const index = busSlots.value.findIndex(trip => {
        // Handle both string and number IDs
        return String(trip.id) === String(updatedTrip.id)
      })
      
      
      
      if (index !== -1) {
        // Create new array with updated trip
        const newBusSlots = [...busSlots.value]
        newBusSlots[index] = {
          ...newBusSlots[index],  // Keep existing data
          ...updatedTrip,         // Override with new data
          // Ensure proper data structure
          id: String(updatedTrip.id),
          status: updatedTrip.status,
          actualDepartureTime: updatedTrip.actualDepartureTime,
          actualArrivalTime: updatedTrip.actualArrivalTime,
          delayReason: updatedTrip.delayReason,
          currentLocation: updatedTrip.currentLocation
        }
        
        busSlots.value = newBusSlots
        
        
      } else {
        
        
        // Fallback: reload all data if we can't find the trip
        setTimeout(async () => {
          
          await loadBusSlots()
          
        }, 1000)
      }
    } catch (err) {
      
      
      // Fallback: reload all data
      
      setTimeout(async () => {
        
        await loadBusSlots()
        
      }, 1500)
    }
  }

  function setTripLoading(tripId, action) {
    loadingTripIds.value[tripId] = action
  }

  function clearTripLoading(tripId) {
    delete loadingTripIds.value[tripId]
  }

  function isTripButtonLoading(tripId, action) {
    return loadingTripIds.value[tripId] === action
  }

  function resetForm() {
    const tomorrow = new Date()
    tomorrow.setDate(tomorrow.getDate() + 1)
    const defaultDate = tomorrow.toISOString().split('T')[0]
    
    tripForm.value = {
      busId: '',
      routeId: '',
      slotDate: defaultDate,
      departureTime: '08:00',
      arrivalTime: '10:00',
      price: 500000,
      totalSeats: 40
    }
    editingTripId.value = null
  }

  function clearError() {
    error.value = null
  }

  function setEditingTrip(trip) {
    editingTripId.value = trip?.id || null
  }

  // === WATCHERS ===
  
  // Auto-fill totalSeats when bus is selected
  watch(() => tripForm.value.busId, (newBusId) => {
    if (newBusId) {
      const selectedBus = allBuses.value.find(bus => bus.id === newBusId)
      if (selectedBus && selectedBus.totalSeats) {
        tripForm.value.totalSeats = selectedBus.totalSeats
        
      }
    }
  })

  // Calculate arrival time when route is selected
  watch(() => tripForm.value.routeId, () => {
    if (tripForm.value.routeId && tripForm.value.departureTime) {
      const selectedRoute = allRoutes.value.find(route => route.id === tripForm.value.routeId)
      if (selectedRoute && selectedRoute.estimatedDurationMinutes) {
        const [hours, minutes] = tripForm.value.departureTime.split(':').map(Number)
        const departureMinutes = hours * 60 + minutes
        const arrivalMinutes = departureMinutes + selectedRoute.estimatedDurationMinutes
        
        const arrivalHours = Math.floor(arrivalMinutes / 60) % 24
        const arrivalMins = arrivalMinutes % 60
        
        tripForm.value.arrivalTime = `${arrivalHours.toString().padStart(2, '0')}:${arrivalMins.toString().padStart(2, '0')}`
        
      }
    }
  })

  // Initialize
  async function initialize() {
    try {
      
      
      
      await Promise.all([
        loadBusSlots(),
        loadAllBuses(),
        loadAllRoutes()
      ])
      

      
      
      if (autoManagerEnabled.value) {
        
        startAutoManager()
      } else {
        
      }
    } catch (err) {
      
    }
  }

  function cleanup() {
      
    stopAutoManager()
    
  }

  return {
    // State
    busSlots,
    availableBuses, // Computed - filtered based on conflicts
    availableRoutes, // Computed - all routes for now
    tripForm,
    
    // Loading states
    loading,
    loadingBuses,
    loadingRoutes,
    syncLoading,
    
    // Error states
    error,
    
    // Auto management
    autoManagerEnabled,
    
    // Computed
    stats,
    
    // Core operations
    loadBusSlots,
    loadAllBuses,
    loadAllRoutes,
    createTrip,
    updateTrip,
    deleteTrip,
    
    // Status management
    quickMarkInProgress,
    quickMarkCompleted,
    updateTripStatus,
    
    // Auto management
    manualTriggerAutoManager,
    toggleAutoManager,
    forceRefreshAllData,
    debugAutoManager, // Expose the new function
    
    // Utility
    isTripButtonLoading,
    resetForm,
    clearError,
    setEditingTrip,
    hasDuplicateTrip,
    hasConflictingTrip,
    getTripsForBusAndDate,
    getNextAvailableTime,
    timeToMinutes,
    minutesToTime,
    
    // Lifecycle
    initialize,
    cleanup
  }
} 