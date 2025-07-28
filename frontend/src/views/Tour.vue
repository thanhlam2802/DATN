<script setup>
import { ref, reactive, onMounted, watch } from "vue";
import SearchBar from "../components/Tours/SearchBar.vue";
import SideBar from "../components/Tours/Sidebar.vue";
import TourGrid from "../components/Tours/TourGrid.vue";
import { useGeolocation } from "../composables/useGeolocation";
import { FilterIcon, XIcon } from "lucide-vue-next";

// --- CÁC HẰNG SỐ VÀ TRẠNG THÁI GIAO DIỆN ---
const BANNER_IMAGE =
  "https://ik.imagekit.io/tvlk/xpe-asset/AyJ40ZAo1DOyPyKLZ9c3RGQHTP2oT4ZXW+QmPVVkFQiXFSv42UaHGzSmaSzQ8DO5QIbWPZuF+VkYVRk6gh-Vg4ECbfuQRQ4pHjWJ5Rmbtkk=/5255025890550/Phong-Nha-Ke-Bang-National-Park-Tour-from-Hue-158e7222-35b7-4296-95bb-690972765d35.jpeg?_src=imagekit&tr=dpr-2,c-at_max,h-400,q-100,w-1280";
const isOpen = ref(false); // State cho modal chọn điểm đến
const isFilterOpen = ref(false); // State cho sidebar mobile

// --- TRẠNG THÁI DỮ LIỆU ---
const tourList = ref([]);
const tourCount = ref(0);

// --- QUẢN LÝ TRẠNG THÁI BỘ LỌC TẬP TRUNG ---
// Tất cả các bộ lọc từ sidebar và dropdown sắp xếp sẽ được quản lý tại đây.
const filters = reactive({
  destination: "",
  minPrice: 0,
  maxPrice: 5000000,
  minRating: 0,
  tags: [],
  sortBy: "popular", // `sortBy` từ state cũ giờ là một phần của filters
  page: 0,
  size: 12,
});

// --- CÁC HÀM XỬ LÝ ---

// 1. Hàm nhận dữ liệu bộ lọc từ Sidebar
const handleUpdateFilters = (sidebarFilters) => {
  // Cập nhật các giá trị từ Sidebar
  filters.destination = sidebarFilters.destination;
  filters.minPrice = sidebarFilters.minPrice;
  filters.maxPrice = sidebarFilters.maxPrice;
  filters.minRating = sidebarFilters.minRating;
  filters.tags = sidebarFilters.tags;
  // Reset về trang đầu tiên mỗi khi có bộ lọc mới
  filters.page = 0;
};

// 2. Hàm gọi API đã được nâng cấp để sử dụng tất cả bộ lọc
const fetchTours = async () => {
  try {
    const params = new URLSearchParams();

    // Chỉ thêm các tham số có giá trị vào URL
    if (filters.destination) params.append("destination", filters.destination);
    if (filters.minPrice > 0) params.append("minPrice", filters.minPrice);
    if (filters.maxPrice < 5000000) params.append("maxPrice", filters.maxPrice);
    if (filters.minRating > 0) params.append("minRating", filters.minRating);
    if (filters.tags && filters.tags.length > 0) {
      filters.tags.forEach((tag) => params.append("tags", tag));
    }

    // Luôn thêm các tham số sắp xếp và phân trang
    params.append("sortBy", filters.sortBy);
    params.append("page", filters.page);
    params.append("size", filters.size);

    const response = await fetch(
      `http://localhost:8080/api/v1/tours?${params.toString()}`
    );
    const responseData = await response.json();

    if (responseData.statusCode === 200 && responseData.data.content) {
      tourCount.value = responseData.data.totalElements;
      const mappedTours = responseData.data.content.map((tour) => ({
        id: tour.id,
        title: tour.name,
        imageUrl: tour.imageUrl,
        price: tour.price.toLocaleString("vi-VN"),
        location: tour.location,

        rating:
          tour.reviewCount > 0
            ? {
                score: tour.rating.toFixed(1),
                reviews: tour.reviewCount.toString(),
              }
            : null,
      }));
      tourList.value = mappedTours;
    } else {
      tourList.value = [];
      tourCount.value = 0;
    }
  } catch (error) {
    console.error("Lỗi khi lấy dữ liệu tour:", error);
    tourList.value = [];
    tourCount.value = 0;
  }
};

// 3. Watcher theo dõi toàn bộ đối tượng filters
// Bất kỳ thay đổi nào trong `filters` (từ sidebar hoặc dropdown) đều sẽ kích hoạt lại `fetchTours`
watch(
  filters,
  () => {
    fetchTours();
  },
  { deep: true }
);

// Tải dữ liệu lần đầu khi component được tạo
onMounted(fetchTours);

// Các hàm còn lại không thay đổi
const { getCurrentLocation } = useGeolocation();
const destinations = [
  /* ... */
];
const handleLocationClick = async () => {
  /* ... */
};
const closeModal = () => {
  isOpen.value = false;
};
const openModal = () => {
  isOpen.value = true;
};
</script>

<template>
  <div class="mt-4 w-full">
    <section
      class="relative w-full h-40 xs:h-48 sm:h-36 md:h-44 lg:h-52 xl:h-80 overflow-hidden mb-6"
    >
      <img
        :src="BANNER_IMAGE"
        alt="Tour Banner"
        class="absolute rounded-br-3xl inset-0 w-full h-full object-cover brightness-75 transition-transform duration-700 hover:scale-105"
      />
      <div
        class="relative z-10 grid grid-cols-12 gap-2 sm:gap-4 h-full items-center px-2 sm:px-6"
      >
        <div class="col-span-12 md:col-span-6 flex flex-col gap-2">
          <h1
            class="text-white text-xl xs:text-2xl sm:text-3xl font-semibold animate-fade-in mb-2"
          >
            Du lịch
          </h1>
          <div>
            <button
              @click="openModal"
              class="inline-flex items-center gap-2 bg-white bg-opacity-90 hover:bg-opacity-100 text-gray-900 text-sm font-medium rounded-full py-2 px-4 shadow-sm focus:outline-none w-fit transition-all duration-300 hover:shadow-md"
            >
              <i class="fas fa-map-marker-alt text-blue-500"></i>
              <span>Chọn một điểm đến</span>
              <svg
                class="w-4 h-4 text-gray-600 transition-transform duration-300"
                :class="{ 'rotate-180': isOpen }"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                viewBox="0 0 24 24"
              >
                <path
                  d="M19 9l-7 7-7-7"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>
            </button>
            <transition
              name="modal"
              enter-active-class="transition duration-300 ease-out"
              enter-from-class="transform scale-95 opacity-0"
              enter-to-class="transform scale-100 opacity-100"
              leave-active-class="transition duration-200 ease-in"
              leave-from-class="transform scale-100 opacity-100"
              leave-to-class="transform scale-95 opacity-0"
            >
              <div
                v-if="isOpen"
                class="fixed inset-0 z-50 bg-black-200 bg-opacity-5 backdrop-blur-sm flex items-end md:items-center justify-center"
                @click.self="closeModal"
              >
                <div
                  class="relative bg-gray-100 rounded-t-2xl md:rounded-2xl shadow-lg w-full max-w-3xl max-h-[90vh] overflow-y-auto"
                >
                  <div
                    class="grid grid-cols-1 md:grid-cols-2 gap-4 bg-white p-4 mb-2 items-center justify-center sticky top-0 z-10 border-b"
                  >
                    <div class="col-span-1">
                      <p class="text-xs text-gray-500">
                        Khám phá những điều xung quanh bạn:
                      </p>
                      <h2
                        class="text-lg font-medium text-blue-500 cursor-pointer hover:text-blue-600 transition-colors"
                        @click="handleLocationClick"
                      >
                        Thành phố Quy Nhơn, Vietnam
                      </h2>
                    </div>
                    <SearchBar
                      class="col-span-1"
                      placeholder="Tìm thành phố hoặc khu vực"
                    />
                  </div>
                  <div
                    class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4 p-4 bg-white"
                  >
                    <div
                      v-for="destination in destinations"
                      :key="destination.id"
                      class="rounded-lg overflow-hidden shadow-md cursor-pointer group transition-all duration-300 hover:shadow-xl"
                    >
                      <div class="relative overflow-hidden">
                        <img
                          :src="destination.image"
                          :alt="destination.name"
                          class="w-full h-32 object-cover transition-transform duration-500 group-hover:scale-110"
                        />
                        <div
                          class="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-20 transition-opacity duration-300"
                        ></div>
                      </div>
                      <div class="p-3 bg-white">
                        <h3 class="font-medium text-gray-800 mb-1">
                          {{ destination.name }}
                        </h3>
                        <p class="text-sm text-gray-500">
                          {{ destination.description }}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </transition>
          </div>
        </div>
      </div>
    </section>

    <section class="pt-8 pb-6 text-center md:text-left flex w-full">
      <h2
        class="font-extrabold text-2xl sm:text-3xl text-black leading-tight mr-5"
      >
        Tour
      </h2>
      <p
        class="text-gray-600 text-base sm:text-lg leading-relaxed max-w-3xl mx-auto md:mx-0"
      >
        Khám phá những địa điểm mới và đặc biệt bằng cách tham gia các chuyến
        tham quan trong ngày với hướng dẫn viên giàu kinh nghiệm.
      </p>
    </section>

    <section class="max-w-7xl mx-auto px-4 py-6 mt-4">
      <div class="flex flex-col md:flex-row gap-6">
        <SideBar
          class="hidden md:block md:w-1/4"
          @update-filters="handleUpdateFilters"
        />

        <main class="flex-1">
          <div
            class="flex flex-col sm:flex-row justify-between items-center mb-6 gap-4"
          >
            <h2 class="text-lg sm:text-xl text-gray-600">
              Về {{ tourCount }} kết quả
            </h2>

            <button @click="isFilterOpen = true" class="md:hidden ...">
              <FilterIcon class="w-4 h-4" />
              <span>Lọc kết quả</span>
            </button>
            <div class="hidden sm:flex items-center gap-2">
              <span class="text-gray-600">Xếp theo:</span>
              <div class="relative">
                <select
                  v-model="filters.sortBy"
                  class="appearance-none bg-white border rounded-lg px-4 py-2 pr-8 cursor-pointer focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="popular">Phổ biến nhất</option>
                  <option value="price-asc">Giá tăng dần</option>
                  <option value="price-desc">Giá giảm dần</option>
                  <option value="rating">Đánh giá cao</option>
                </select>
                <div class="absolute right-3 top-1/2 ..."></div>
              </div>
            </div>
          </div>
          <TourGrid :tours="tourList" />
        </main>
      </div>
    </section>

    <Transition name="slide-fade">
      <div v-if="isFilterOpen" class="fixed inset-0 z-50 flex md:hidden">
        <div
          @click="isFilterOpen = false"
          class="fixed inset-0 bg-black bg-opacity-40"
        ></div>
        <div
          class="relative bg-gray-100 w-4/5 max-w-sm h-full shadow-xl overflow-y-auto"
        >
          <div
            class="p-4 flex justify-between items-center border-b bg-white sticky top-0"
          >
            <h3 class="font-bold text-lg">Bộ lọc</h3>
            <button @click="isFilterOpen = false" class="p-2 -mr-2">
              <XIcon class="w-6 h-6 text-gray-600" />
            </button>
          </div>

          <SideBar @update-filters="handleUpdateFilters" />
        </div>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
/* CSS không đổi */
</style>
<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  transform: scale(0.9);
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in {
  animation: fadeIn 0.6s ease-out;
}

/* Smooth scrollbar for modal */
.overflow-y-auto {
  scrollbar-width: thin;
  scrollbar-color: rgba(156, 163, 175, 0.5) transparent;
}

.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: transparent;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background-color: rgba(156, 163, 175, 0.5);
  border-radius: 3px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background-color: rgba(156, 163, 175, 0.7);
}
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s ease-out;
}

.slide-fade-enter-active .relative,
.slide-fade-leave-active .relative {
  transition: transform 0.3s ease-out;
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  opacity: 0;
}

.slide-fade-enter-from .relative,
.slide-fade-leave-to .relative {
  transform: translateX(-100%);
}
</style>
