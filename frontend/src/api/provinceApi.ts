/**
 * Province API service to fetch Vietnam provinces and districts data
 * Data source: https://provinces.open-api.vn/api/?depth=2
 */

// Types for province data
export interface Ward {
  name: string;
  code: number;
  division_type: string;
  codename: string;
  district_code: number;
}

export interface District {
  name: string;
  code: number;
  division_type: string;
  codename: string;
  province_code: number;
  wards: Ward[] | null; // v2: có thể null ở depth=2
}

export interface Province {
  name: string;
  code: number;
  division_type: string;
  codename: string;
  phone_code: number;
  districts: District[]; // có thể không kèm wards khi depth=2
}

// Simple types for dropdown usage
export interface ProvinceOption {
  code: number;
  name: string;
  label: string; // hiển thị
  value: string; // code dưới dạng string để dùng làm value ổn định
}

export interface DistrictOption {
  code: number;
  name: string;
  label: string; // hiển thị
  value: string; // code dưới dạng string để dùng làm value ổn định
}

// Cache để tránh fetch lại nhiều lần
let provincesCache: Province[] | null = null;
let lastFetchTime = 0;
const CACHE_DURATION = 5 * 60 * 1000; // 5 phút

const API_URL = 'https://provinces.open-api.vn/api/v2/?depth=2';

/**
 * Fetch tất cả tỉnh thành và quận huyện
 */
export const fetchProvinces = async (): Promise<Province[]> => {
  const now = Date.now();
  
  // Sử dụng cache nếu còn hiệu lực
  if (provincesCache && (now - lastFetchTime) < CACHE_DURATION) {
    return provincesCache;
  }
  
  try {
    const response = await fetch(API_URL);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const provinces: Province[] = await response.json();
    
    // Cập nhật cache
    provincesCache = provinces;
    lastFetchTime = now;
    
    return provinces;
  } catch (error) {
    console.error('Error fetching provinces:', error);
    throw new Error('Không thể tải dữ liệu tỉnh thành. Vui lòng thử lại.');
  }
};

/**
 * BACKWARD COMPATIBILITY: Legacy function for existing code
 * Returns provinces in the format expected by old code
 */
export const getAllProvinces = async () => {
  try {
    const provinces = await fetchProvinces();
    
    // Return in axios-like format that old code expects
    return {
      data: provinces.map(province => ({
        code: province.code,
        name: province.name,
        divisionType: province.division_type,
        codename: province.codename,
        phoneCode: province.phone_code,
        districts: province.districts?.map(district => ({
          code: district.code,
          name: district.name,
          divisionType: district.division_type,
          codename: district.codename,
          provinceCode: district.province_code
        })) || []
      }))
    };
  } catch (error) {
    throw error;
  }
};

/**
 * Lấy danh sách tỉnh thành (không bao gồm quận huyện)
 */
export const getProvinceList = async (): Promise<ProvinceOption[]> => {
  const provinces = await fetchProvinces();
  return provinces.map(province => ({
    code: province.code,
    name: province.name,
    label: province.name,
    value: String(province.code)
  }));
};

/**
 * Lấy danh sách quận huyện theo tỉnh
 */
export const getDistrictsByProvince = async (provinceName: string): Promise<DistrictOption[]> => {
  const provinces = await fetchProvinces();
  const province = provinces.find(p => p.name === provinceName);
  
  if (!province) {
    return [];
  }
  
  return (province.districts || []).map(district => ({
    code: district.code,
    name: district.name,
    label: district.name,
    value: String(district.code)
  }));
};

/**
 * Tìm tỉnh theo tên (hỗ trợ fuzzy search)
 */
export const searchProvinces = async (searchTerm: string): Promise<ProvinceOption[]> => {
  const provinces = await getProvinceList();
  
  if (!searchTerm) {
    return provinces;
  }
  
  const term = searchTerm.toLowerCase().trim();
  return provinces.filter(province => 
    province.name.toLowerCase().includes(term) ||
    province.name.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '').includes(term)
  );
};

/**
 * Tìm quận huyện theo tên trong một tỉnh
 */
export const searchDistricts = async (provinceName: string, searchTerm: string): Promise<DistrictOption[]> => {
  const districts = await getDistrictsByProvince(provinceName);
  
  if (!searchTerm) {
    return districts;
  }
  
  const term = searchTerm.toLowerCase().trim();
  return districts.filter(district => 
    district.name.toLowerCase().includes(term) ||
    district.name.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '').includes(term)
  );
};

/**
 * Validate xem tỉnh và huyện có tồn tại không
 */
export const validateProvinceDistrict = async (provinceName: string, districtName?: string) => {
  try {
    const districts = await getDistrictsByProvince(provinceName);
    const districtExists = districtName ? districts.some(d => d.name === districtName) : true;
    
    return {
      provinceExists: districts.length > 0,
      districtExists: districtExists
    };
  } catch (error) {
    return {
      provinceExists: false,
      districtExists: false
    };
  }
};

/**
 * Utility: Format địa chỉ đầy đủ
 */
export const formatFullAddress = (addressDetails?: string, district?: string, province?: string): string => {
  const parts = [addressDetails, district, province].filter(Boolean);
  return parts.join(', ');
};

// Các hàm tiện ích mới theo code (không phá vỡ tương thích cũ)
export const getProvinceByCode = async (provinceCode: number): Promise<Province | undefined> => {
  const provinces = await fetchProvinces();
  return provinces.find(p => p.code === provinceCode);
};

export const getDistrictsByProvinceCode = async (provinceCode: number): Promise<DistrictOption[]> => {
  const province = await getProvinceByCode(provinceCode);
  if (!province) return [];
  return (province.districts || []).map(district => ({
    code: district.code,
    name: district.name,
    label: district.name,
    value: String(district.code)
  }));
};

// Export default object for easier import (with backward compatibility)
const ProvinceAPI = {
  fetchProvinces,
  getAllProvinces, // ADDED: For backward compatibility
  getProvinceList,
  getDistrictsByProvince,
  getProvinceByCode,
  getDistrictsByProvinceCode,
  searchProvinces,
  searchDistricts,
  validateProvinceDistrict,
  formatFullAddress
};

export default ProvinceAPI; 