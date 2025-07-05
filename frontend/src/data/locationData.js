// Mock data for provinces and districts
export const provinces = [
  {
    id: 'hcm',
    name: 'TP. Hồ Chí Minh',
    code: 'HCM',
    type: 'city'
  },
  {
    id: 'hanoi',
    name: 'Hà Nội',
    code: 'HN',
    type: 'city'
  },
  {
    id: 'danang',
    name: 'Đà Nẵng',
    code: 'DN',
    type: 'city'
  },
  {
    id: 'cantho',
    name: 'Cần Thơ',
    code: 'CT',
    type: 'city'
  },
  {
    id: 'vungtau',
    name: 'Vũng Tàu',
    code: 'VT',
    type: 'province'
  },
  {
    id: 'dalat',
    name: 'Đà Lạt',
    code: 'DL',
    type: 'province'
  },
  {
    id: 'nhatrang',
    name: 'Nha Trang',
    code: 'NT',
    type: 'province'
  },
  {
    id: 'phanrang',
    name: 'Phan Rang',
    code: 'PR',
    type: 'province'
  },
  {
    id: 'muine',
    name: 'Mũi Né',
    code: 'MN',
    type: 'province'
  },
  {
    id: 'halong',
    name: 'Hạ Long',
    code: 'HL',
    type: 'province'
  }
]

// Districts for Ho Chi Minh City (for shuttle service)
export const hcmDistricts = [
  {
    id: 'q1',
    name: 'Quận 1',
    parentId: 'hcm'
  },
  {
    id: 'q3',
    name: 'Quận 3',
    parentId: 'hcm'
  },
  {
    id: 'q5',
    name: 'Quận 5',
    parentId: 'hcm'
  },
  {
    id: 'q7',
    name: 'Quận 7',
    parentId: 'hcm'
  },
  {
    id: 'q10',
    name: 'Quận 10',
    parentId: 'hcm'
  },
  {
    id: 'binhtan',
    name: 'Bình Tân',
    parentId: 'hcm'
  },
  {
    id: 'govap',
    name: 'Gò Vấp',
    parentId: 'hcm'
  },
  {
    id: 'phunhuan',
    name: 'Phú Nhuận',
    parentId: 'hcm'
  },
  {
    id: 'tanbinh',
    name: 'Tân Bình',
    parentId: 'hcm'
  },
  {
    id: 'tanphu',
    name: 'Tân Phú',
    parentId: 'hcm'
  }
]

// Function to get destinations based on bus type and selected origin
export const getDestinations = (busType, selectedOrigin) => {
  if (busType === 'shuttle-bus') {
    // For shuttle bus, show districts if origin is HCM, or show other provinces
    if (selectedOrigin?.id === 'hcm') {
      return hcmDistricts
    } else {
      return provinces.filter(p => p.id !== selectedOrigin?.id)
    }
  } else {
    // For sleeping bus, show provinces only (exclude districts)
    return provinces.filter(p => p.id !== selectedOrigin?.id)
  }
}

// Function to get origins based on bus type
export const getOrigins = (busType) => {
  if (busType === 'shuttle-bus') {
    // For shuttle bus, show all provinces and HCM districts
    return [...provinces, ...hcmDistricts]
  } else {
    // For sleeping bus, show provinces only
    return provinces
  }
}

// Mock data for bus trips
export const mockBusTrips = [
  {
    id: 'trip1',
    busOperator: 'Thanh Vinh',
    busType: 'Limousine 9 chỗ',
    type: 'shuttle-bus', // Added for booking flow
    company: 'Thanh Vinh',
    route: {
      from: 'Sân Bay Tân Sơn Nhất 2',
      to: 'VP Vũng Tàu',
      fromCode: 'HCM',
      toCode: 'VT'
    },
    schedule: {
      departure: '23:02',
      arrival: '01:32',
      nextDay: true,
      duration: '2h 30m'
    },
    price: 218500,
    currency: 'VND',
    facilities: ['wifi', 'ac', 'water', 'food'],
    availableSeats: 6,
    totalSeats: 9,
    image: 'https://images.unsplash.com/photo-1570125909232-eb263c188f7e?q=80&w=2340&auto=format&fit=crop',
    features: {
      seats: '9 seats',
      seatLayout: '1-1',
      facilities: [
        { icon: 'fas fa-snowflake', name: 'Air Conditioning' },
        { icon: 'fas fa-wifi', name: 'WiFi' },
        { icon: 'fas fa-tv', name: 'Central Multimedia' },
        { icon: 'fas fa-tint', name: 'Mineral Water' }
      ]
    },
    policies: {
      reschedule: {
        available: false,
        description: 'Bus schedule cannot be changed after booking.'
      },
      refund: {
        available: true,
        description: 'Contact Traveloka CS to request a refund.',
        link: 'Info'
      }
    }
  },
  {
    id: 'trip2',
    busOperator: 'Thanh Vinh',
    busType: 'Ghế massage - Limousine 9 chỗ',
    type: 'sleeping-bus', // Added for booking flow
    company: 'Thanh Vinh',
    route: {
      from: 'Sân Bay Tân Sơn Nhất 2',
      to: 'VP Vũng Tàu',
      fromCode: 'HCM',
      toCode: 'VT'
    },
    schedule: {
      departure: '23:02',
      arrival: '01:32',
      nextDay: true,
      duration: '2h 30m'
    },
    price: 230000,
    currency: 'VND',
    facilities: ['wifi', 'ac', 'water', 'food', 'massage'],
    availableSeats: 4,
    totalSeats: 9,
    image: 'https://images.unsplash.com/photo-1570125909232-eb263c188f7e?q=80&w=2340&auto=format&fit=crop',
    features: {
      seats: '9 seats',
      seatLayout: '1-1',
      facilities: [
        { icon: 'fas fa-snowflake', name: 'Air Conditioning' },
        { icon: 'fas fa-wifi', name: 'WiFi' },
        { icon: 'fas fa-tv', name: 'Central Multimedia' },
        { icon: 'fas fa-tint', name: 'Mineral Water' },
        { icon: 'fas fa-spa', name: 'Massage Seat' }
      ]
    },
    policies: {
      reschedule: {
        available: false,
        description: 'Bus schedule cannot be changed after booking.'
      },
      refund: {
        available: true,
        description: 'Contact Traveloka CS to request a refund.',
        link: 'Info'
      }
    }
  }
]

// Facility icons mapping
export const facilityIcons = {
  wifi: 'fas fa-wifi',
  ac: 'fas fa-snowflake',
  water: 'fas fa-tint',
  food: 'fas fa-utensils',
  massage: 'fas fa-spa'
}

// Time slots for filtering
export const timeSlots = [
  { id: 'morning', label: '06:00 - 12:00', start: '06:00', end: '12:00' },
  { id: 'afternoon', label: '12:00 - 18:00', start: '12:00', end: '18:00' },
  { id: 'evening', label: '18:00 - 00:00', start: '18:00', end: '23:59' },
  { id: 'night', label: '00:00 - 06:00', start: '00:00', end: '06:00' }
]

// Available facilities for filtering
export const availableFacilities = [
  { id: 'wifi', name: 'WiFi', icon: 'fas fa-wifi' },
  { id: 'ac', name: 'Điều hòa', icon: 'fas fa-snowflake' },
  { id: 'water', name: 'Nước uống', icon: 'fas fa-tint' },
  { id: 'food', name: 'Ăn uống', icon: 'fas fa-utensils' }
] 