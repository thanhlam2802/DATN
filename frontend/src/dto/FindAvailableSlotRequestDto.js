// DTO tương ứng với backend FindAvailableSlotRequestDto
export default class FindAvailableSlotRequestDto {
  constructor(flightId, isAisle, isWindow, isBusiness) {
    this.flightId = flightId;
    this.isAisle = isAisle;
    this.isWindow = isWindow;
    this.isBusiness = isBusiness;
  }

  // Factory method để tạo từ form data
  static fromFormData(flightId, seatType, cabinClass) {
    let isAisle = null;
    let isWindow = null;
    let isBusiness = null;

    // Xử lý cabin class
    if (cabinClass === 'business') {
      isBusiness = true;
    } else if (cabinClass === 'economy') {
      isBusiness = false;
    }

    // Xử lý seat type
    if (seatType === 'window') {
      isWindow = true;
      isAisle = false;
    } else if (seatType === 'aisle') {
      isAisle = true;
      isWindow = false;
    }

    return new FindAvailableSlotRequestDto(flightId, isAisle, isWindow, isBusiness);
  }

  // Method để log DTO
  log() {
    console.log('=== FindAvailableSlotRequestDto ===');
    console.log('Flight ID:', this.flightId);
    console.log('Is Aisle:', this.isAisle);
    console.log('Is Window:', this.isWindow);
    console.log('Is Business:', this.isBusiness);
    console.log('===================================');
  }

  // Method để convert thành object cho API call
  toObject() {
    return {
      flightId: this.flightId,
      isAisle: this.isAisle,
      isWindow: this.isWindow,
      isBusiness: this.isBusiness
    };
  }
} 