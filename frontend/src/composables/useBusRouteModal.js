import { ref, reactive, computed } from 'vue'

export function useBusRouteModal() {
  // State
  const isVisible = ref(false)
  const currentStep = ref(1)
  const isEditing = ref(false)
  const editingRouteId = ref(null)
  
  // Steps Configuration
  const steps = ref([
    { id: 1, title: 'Thông tin cơ bản', key: 'basic' },
    { id: 2, title: 'Lịch trình', key: 'schedule' },
    { id: 3, title: 'Giá vé & Dịch vụ', key: 'pricing' }
  ])

  // Form Data
  const formData = reactive({
    // Bước 1: Thông tin cơ bản
    companyName: '',
    busType: '',
    licensePlate: '',
    description: '',
    images: {
      exterior: [],
      interior: []
    },
    
    // Bước 2: Lịch trình
    departure: {
      city: '',
      address: '',
      time: ''
    },
    arrival: {
      city: '',
      address: '',
      time: ''
    },
    operatingDays: [],
    travelTime: '',
    stopPoints: [],
    
    // Bước 3: Giá vé & Dịch vụ
    ticketPrice: '',
    discountPercent: 0,
    services: [],
    policies: {
      ticketPolicy: '',
      cancelPolicy: '',
      baggagePolicy: ''
    }
  })

  // Validation Errors
  const validationErrors = ref({})

  // Available Services
  const availableServices = ref([
    { id: 'wifi', name: 'WiFi miễn phí', icon: '📶' },
    { id: 'tv', name: 'TV/Giải trí', icon: '📺' },
    { id: 'usb', name: 'Cổng sạc USB', icon: '🔌' },
    { id: 'water', name: 'Nước uống', icon: '💧' },
    { id: 'blanket', name: 'Chăn/Gối', icon: '🛏️' },
    { id: 'toilet', name: 'Toilet trên xe', icon: '🚽' },
    { id: 'ac', name: 'Điều hòa', icon: '❄️' },
    { id: 'snacks', name: 'Đồ ăn nhẹ', icon: '🍪' }
  ])

  // Week Days
  const weekDays = ref([
    { id: 'monday', name: 'Thứ 2', value: 1 },
    { id: 'tuesday', name: 'Thứ 3', value: 2 },
    { id: 'wednesday', name: 'Thứ 4', value: 3 },
    { id: 'thursday', name: 'Thứ 5', value: 4 },
    { id: 'friday', name: 'Thứ 6', value: 5 },
    { id: 'saturday', name: 'Thứ 7', value: 6 },
    { id: 'sunday', name: 'Chủ nhật', value: 0 }
  ])

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
    // Populate form with existing route data
    Object.assign(formData, {
      companyName: routeData.companyName || '',
      busType: routeData.busType || '',
      licensePlate: routeData.licensePlate || '',
      description: routeData.description || '',
      images: routeData.images || { exterior: [], interior: [] },
      departure: routeData.departure || { city: '', address: '', time: '' },
      arrival: routeData.arrival || { city: '', address: '', time: '' },
      operatingDays: routeData.operatingDays || [],
      travelTime: routeData.travelTime || '',
      stopPoints: routeData.stopPoints || [],
      ticketPrice: routeData.ticketPrice || '',
      discountPercent: routeData.discountPercent || 0,
      services: routeData.services || [],
      policies: routeData.policies || {
        ticketPolicy: '',
        cancelPolicy: '',
        baggagePolicy: ''
      }
    })
  }

  const resetFormData = () => {
    Object.assign(formData, {
      companyName: '',
      busType: '',
      licensePlate: '',
      description: '',
      images: { exterior: [], interior: [] },
      departure: { city: '', address: '', time: '' },
      arrival: { city: '', address: '', time: '' },
      operatingDays: [],
      travelTime: '',
      stopPoints: [],
      ticketPrice: '',
      discountPercent: 0,
      services: [],
      policies: {
        ticketPolicy: '',
        cancelPolicy: '',
        baggagePolicy: ''
      }
    })
  }

  const validateCurrentStep = () => {
    clearValidationErrors()
    
    switch (currentStep.value) {
      case 1:
        return validateBasicInfo()
      case 2:
        return validateSchedule()
      case 3:
        return validatePricing()
      default:
        return true
    }
  }

  const validateBasicInfo = () => {
    const errors = {}
    
    if (!formData.companyName.trim()) {
      errors.companyName = 'Tên nhà xe là bắt buộc'
    }
    
    if (!formData.busType.trim()) {
      errors.busType = 'Loại xe là bắt buộc'
    }
    
    if (!formData.licensePlate.trim()) {
      errors.licensePlate = 'Biển số xe là bắt buộc'
    }
    
    // Simplified image validation - images are optional for now
    
    validationErrors.value = errors
    return Object.keys(errors).length === 0
  }

  const validateSchedule = () => {
    const errors = {}
    
    if (!formData.departure.city.trim()) {
      errors.departureCity = 'Điểm đi là bắt buộc'
    }
    
    if (!formData.arrival.city.trim()) {
      errors.arrivalCity = 'Điểm đến là bắt buộc'
    }
    
    if (!formData.departure.time.trim()) {
      errors.departureTime = 'Giờ khởi hành là bắt buộc'
    }
    
    if (!formData.arrival.time.trim()) {
      errors.arrivalTime = 'Giờ đến là bắt buộc'
    }
    
    if (formData.operatingDays.length === 0) {
      errors.operatingDays = 'Chọn ít nhất 1 ngày hoạt động'
    }
    
    if (!formData.travelTime.trim()) {
      errors.travelTime = 'Thời gian di chuyển là bắt buộc'
    }
    
    validationErrors.value = errors
    return Object.keys(errors).length === 0
  }

  const validatePricing = () => {
    const errors = {}
    
    if (!formData.ticketPrice || formData.ticketPrice <= 0) {
      errors.ticketPrice = 'Giá vé phải lớn hơn 0'
    }
    
    if (formData.discountPercent < 0 || formData.discountPercent > 100) {
      errors.discountPercent = 'Phần trăm giảm giá phải từ 0-100'
    }
    
    validationErrors.value = errors
    return Object.keys(errors).length === 0
  }

  const clearValidationErrors = () => {
    validationErrors.value = {}
  }

  // Stop Points Management
  const addStopPoint = () => {
    formData.stopPoints.push({
      id: Date.now(),
      name: '',
      address: '',
      arrivalTime: '',
      departureTime: ''
    })
  }

  const removeStopPoint = (index) => {
    formData.stopPoints.splice(index, 1)
  }

  const saveDraft = () => {
    // Implement save draft logic
    console.log('Saving draft:', formData)
    // You can emit this to parent or call API
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({ success: true, message: 'Đã lưu nháp thành công' })
      }, 1000)
    })
  }

  const saveForm = () => {
    if (validateCurrentStep()) {
      // Implement save form logic
      console.log('Saving form:', formData)
      // You can emit this to parent or call API
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve({ 
            success: true, 
            message: isEditing.value ? 'Cập nhật tuyến đường thành công' : 'Thêm tuyến đường thành công' 
          })
        }, 1000)
      })
    }
    return Promise.reject({ success: false, message: 'Vui lòng kiểm tra lại thông tin' })
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
    availableServices,
    weekDays,
    
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
    addStopPoint,
    removeStopPoint,
    saveDraft,
    saveForm
  }
} 