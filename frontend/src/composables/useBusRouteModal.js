import { ref, reactive, computed } from 'vue'
import { createBus } from '@/api/busApi'
import { createRoute, findRouteByOriginDestination } from '@/api/routeApi'  
import { createBusRoute } from '@/api/busRouteApi'
import { 
  parseTimeToMinutes, 
  getBusCategoryId,
  generateBusName,
  combineDateTime,
  calculateDistance,
  validateTimeOrder,
  getCityFullName
} from '@/utils/busHelper'

export function useBusRouteModal() {
  // State
  const isVisible = ref(false)
  const currentStep = ref(1)
  const isEditing = ref(false)
  const editingRouteId = ref(null)
  const isLoading = ref(false)
  
  // Steps Configuration - Giảm xuống 2 bước
  const steps = ref([
    { id: 1, title: 'Thông tin xe & tuyến', key: 'basic' },
    { id: 2, title: 'Thời gian & giá vé', key: 'schedule' }
  ])

  // Form Data - CHỈ GIỮ 7 TRƯỜNG CẦN THIẾT
  const formData = reactive({
    // Bus entity fields
    busType: '',              // → BusCategory.id
    licensePlate: '',         // → Bus.name (kết hợp)
    
    // Route + Bus entity fields
    departure: {
      city: '',               // → Route.origin + Bus.origin
      time: ''               // → Bus.departureTime
    },
    arrival: {
      city: '',               // → Route.destination + Bus.destination
      time: ''               // → Bus.arrivalTime
    },
    
    // Route entity fields
    travelTime: '',           // → Route.estimatedDurationMinutes
    
    // BusRoute entity fields
    ticketPrice: ''           // → BusRoute.price
  })

  // Validation Errors
  const validationErrors = ref({})

  // Computed
  const totalSteps = computed(() => steps.value.length)
  
  const currentStepData = computed(() => {
    return steps.value.find(step => step.id === currentStep.value)
  })

  const canGoNext = computed(() => {
    return currentStep.value < totalSteps.value && validateCurrentStep()
  })

  const canGoPrevious = computed(() => {
    return currentStep.value > 1
  })

  // Methods
  const openModal = (routeData = null) => {
    if (routeData) {
      isEditing.value = true
      editingRouteId.value = routeData.id
      populateFormData(routeData)
    } else {
      isEditing.value = false
      editingRouteId.value = null
      resetFormData()
    }
    
    isVisible.value = true
    currentStep.value = 1
    clearValidationErrors()
  }

  const closeModal = () => {
    isVisible.value = false
    setTimeout(() => {
      resetFormData()
      currentStep.value = 1
      isEditing.value = false
      editingRouteId.value = null
      clearValidationErrors()
      isLoading.value = false
    }, 300)
  }

  const nextStep = () => {
    if (validateCurrentStep()) {
      if (currentStep.value < totalSteps.value) {
        currentStep.value++
        clearValidationErrors()
      }
    }
  }

  const previousStep = () => {
    if (currentStep.value > 1) {
      currentStep.value--
      clearValidationErrors()
    }
  }

  const updateFormData = (data) => {
    Object.assign(formData, data)
  }

  const populateFormData = (routeData) => {
    // Populate form với data từ BusRoute kết hợp
    Object.assign(formData, {
      busType: routeData.busType || '',
      licensePlate: routeData.licensePlate || '',
      departure: {
        city: routeData.startLocation || '',
        time: routeData.departureTime || ''
      },
      arrival: {
        city: routeData.endLocation || '',
        time: routeData.arrivalTime || ''
      },
      travelTime: routeData.duration ? `${routeData.duration} giờ` : '',
      ticketPrice: routeData.basePrice || ''
    })
  }

  const resetFormData = () => {
    Object.assign(formData, {
      busType: '',
      licensePlate: '',
      departure: { city: '', time: '' },
      arrival: { city: '', time: '' },
      travelTime: '',
      ticketPrice: ''
    })
  }

  const validateCurrentStep = () => {
    clearValidationErrors()
    
    switch (currentStep.value) {
      case 1:
        return validateBasicInfo()
      case 2:
        return validateScheduleAndPricing()
      default:
        return true
    }
  }

  const validateBasicInfo = () => {
    const errors = {}
    
    if (!formData.busType.trim()) {
      errors.busType = 'Loại xe là bắt buộc'
    }
    
    if (!formData.licensePlate.trim()) {
      errors.licensePlate = 'Biển số xe là bắt buộc'
    }
    
    if (!formData.departure.city.trim()) {
      errors.departureCity = 'Điểm đi là bắt buộc'
    }
    
    if (!formData.arrival.city.trim()) {
      errors.arrivalCity = 'Điểm đến là bắt buộc'
    }
    
    // Validate không được trùng điểm đi và đến
    if (formData.departure.city && formData.arrival.city && 
        formData.departure.city === formData.arrival.city) {
      errors.arrivalCity = 'Điểm đến phải khác điểm đi'
    }
    
    validationErrors.value = errors
    return Object.keys(errors).length === 0
  }

  const validateScheduleAndPricing = () => {
    const errors = {}
    
    if (!formData.departure.time.trim()) {
      errors.departureTime = 'Giờ khởi hành là bắt buộc'
    }
    
    if (!formData.arrival.time.trim()) {
      errors.arrivalTime = 'Giờ đến là bắt buộc'
    }
    
    // Validate thứ tự thời gian
    if (formData.departure.time && formData.arrival.time && 
        !validateTimeOrder(formData.departure.time, formData.arrival.time)) {
      errors.arrivalTime = 'Giờ đến phải sau giờ khởi hành'
    }
    
    if (!formData.travelTime.trim()) {
      errors.travelTime = 'Thời gian di chuyển là bắt buộc'
    }
    
    if (!formData.ticketPrice || formData.ticketPrice <= 0) {
      errors.ticketPrice = 'Giá vé phải lớn hơn 0'
    }
    
    validationErrors.value = errors
    return Object.keys(errors).length === 0
  }

  const clearValidationErrors = () => {
    validationErrors.value = {}
  }

  const saveDraft = () => {
    // Implement save draft logic
    console.log('Saving draft:', formData)
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({ success: true, message: 'Đã lưu nháp thành công' })
      }, 1000)
    })
  }

  const saveForm = async () => {
    if (!validateCurrentStep()) {
      return Promise.reject({ success: false, message: 'Vui lòng kiểm tra lại thông tin' })
    }

    isLoading.value = true
    
    try {
      console.log('🚀 Bắt đầu tạo Bus Route với dữ liệu:', formData)
      
      // **BƯỚC 1: TÌM HOẶC TẠO ROUTE**
      let route = null
      
      try {
        // Tìm route đã tồn tại
        const existingRouteResponse = await findRouteByOriginDestination(
          formData.departure.city, 
          formData.arrival.city
        )
        
        if (existingRouteResponse.data && existingRouteResponse.data.length > 0) {
          route = existingRouteResponse.data[0]
          console.log('✅ Sử dụng Route đã tồn tại:', route)
        }
      } catch (error) {
        console.log('ℹ️ Không tìm thấy Route đã tồn tại, sẽ tạo mới')
      }
      
      // Tạo Route mới nếu chưa tồn tại
      if (!route) {
        const routeData = {
          origin: getCityFullName(formData.departure.city),
          destination: getCityFullName(formData.arrival.city),
          distanceKm: calculateDistance(formData.departure.city, formData.arrival.city),
          estimatedDurationMinutes: parseTimeToMinutes(formData.travelTime)
        }
        
        console.log('🛣️ Tạo Route mới:', routeData)
        const routeResponse = await createRoute(routeData)
        route = routeResponse.data
        console.log('✅ Route được tạo thành công:', route)
      }

      // **BƯỚC 2: TẠO BUS**
      const busData = {
        name: generateBusName(formData),
        categoryId: getBusCategoryId(formData.busType),
        origin: getCityFullName(formData.departure.city),
        destination: getCityFullName(formData.arrival.city),
        departureTime: combineDateTime(formData.departure.time),
        arrivalTime: combineDateTime(formData.arrival.time),
        ownerId: 1 // Mock current user ID - sẽ lấy từ auth context
      }
      
      console.log('🚌 Tạo Bus:', busData)
      const busResponse = await createBus(busData)
      const bus = busResponse.data
      console.log('✅ Bus được tạo thành công:', bus)

      // **BƯỚC 3: TẠO BUSROUTE**
      const busRouteData = {
        busId: bus.id,
        routeId: route.id,
        travelDate: new Date().toISOString(), // Ngày hiện tại
        price: parseFloat(formData.ticketPrice),
        status: 'active'
      }
      
      console.log('🔗 Tạo BusRoute:', busRouteData)
      const busRouteResponse = await createBusRoute(busRouteData)
      const busRoute = busRouteResponse.data
      console.log('✅ BusRoute được tạo thành công:', busRoute)

      // **THÀNH CÔNG**
      isLoading.value = false
      
      const successMessage = isEditing.value 
        ? 'Cập nhật tuyến xe thành công!' 
        : 'Thêm tuyến xe mới thành công!'
      
      console.log('🎉 Hoàn thành tạo Bus Route:', {
        bus,
        route,
        busRoute
      })
      
      return Promise.resolve({ 
        success: true, 
        message: successMessage,
        data: {
          bus,
          route,
          busRoute
        }
      })
      
    } catch (error) {
      isLoading.value = false
      console.error('❌ Lỗi khi tạo Bus Route:', error)
      
      return Promise.reject({ 
        success: false, 
        message: 'Có lỗi xảy ra khi tạo tuyến xe. Vui lòng thử lại.'
      })
    }
  }

  return {
    // State
    isVisible,
    currentStep,
    isEditing,
    editingRouteId,
    steps,
    formData,
    validationErrors,
    isLoading,
    
    // Computed
    totalSteps,
    currentStepData,
    canGoNext,
    canGoPrevious,
    
    // Methods
    openModal,
    closeModal,
    nextStep,
    previousStep,
    updateFormData,
    validateCurrentStep,
    clearValidationErrors,
    saveDraft,
    saveForm
  }
} 