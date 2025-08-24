<template>
  <div class="space-y-8">
    <div
      class="flex flex-col md:flex-row md:items-center md:justify-between gap-4"
    >
      <h1 class="text-3xl font-bold text-gray-900">Quản lý Ngày Khởi Hành</h1>
      <div class="relative w-full md:w-72">
        <SearchIcon
          class="w-5 h-5 absolute left-3.5 top-1/2 transform -translate-y-1/2 text-gray-400"
        />
        <input
          type="text"
          placeholder="Tìm kiếm theo tên tour..."
          v-model="searchTerm"
          class="w-full pl-11 pr-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 transition"
        />
      </div>
    </div>

    <div v-if="isLoading" class="text-center py-10 text-gray-600">
      <p>Đang tải dữ liệu từ server...</p>
    </div>

    <div v-else-if="error" class="bg-red-50 text-red-700 p-4 rounded-lg">
      <p>Đã xảy ra lỗi khi tải dữ liệu: {{ error }}</p>
    </div>

    <div
      v-else-if="!filteredTours.length"
      class="text-center py-10 text-gray-500"
    >
      <p>Không tìm thấy tour nào phù hợp.</p>
    </div>

    <div v-else class="space-y-3">
      <div
        v-for="tour in filteredTours"
        :key="tour.id"
        class="bg-white rounded-xl shadow-sm border border-gray-200 transition-all duration-300"
        :class="{ 'shadow-lg': activeTourId === tour.id }"
      >
        <div
          class="p-5 flex items-center justify-between cursor-pointer hover:bg-gray-50/50 rounded-xl"
          @click="toggleTour(tour.id)"
        >
          <h2 class="text-xl font-bold text-gray-800">{{ tour.name }}</h2>
          <div class="flex items-center gap-4">
            <button
              @click.stop="openAddModal(tour.id)"
              class="bg-blue-600 text-white px-3 py-2 rounded-lg hover:bg-blue-700 flex items-center text-sm font-semibold transition-colors"
            >
              <PlusIcon class="w-4 h-4 mr-2" />
              Thêm ngày
            </button>
            <ChevronDownIcon
              class="w-6 h-6 text-gray-500 transition-transform duration-300"
              :class="{ 'rotate-180': activeTourId === tour.id }"
            />
          </div>
        </div>

        <div
          v-if="activeTourId === tour.id"
          class="px-5 pb-5 border-t border-gray-200"
        >
          <div
            v-if="!tour.departures || !tour.departures.length"
            class="text-center py-8 text-gray-500"
          >
            <p>Tour này chưa có ngày khởi hành nào.</p>
          </div>
          <div v-else class="space-y-6 pt-4">
            <section
              v-if="paginatedCategorizedDepartures.upcoming.items.length"
            >
              <h3
                class="text-md font-semibold text-gray-700 mb-3 flex items-center"
              >
                <CalendarClockIcon class="w-5 h-5 mr-2 text-blue-500" />Sắp tới
              </h3>
              <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
                <DepartureCard
                  v-for="dep in paginatedCategorizedDepartures.upcoming.items"
                  :key="dep.id"
                  :departure="dep"
                  :tour-id="tour.id"
                  @edit="openEditModal"
                  @delete="handleDelete"
                />
              </div>
              <Pagination
                v-if="paginatedCategorizedDepartures.upcoming.totalPages > 1"
                :current-page="departurePagination.upcoming.currentPage"
                :total-pages="
                  paginatedCategorizedDepartures.upcoming.totalPages
                "
                @change-page="handleDeparturePageChange('upcoming', $event)"
                class="mt-4"
              />
            </section>

            <section v-if="paginatedCategorizedDepartures.today.items.length">
              <h3
                class="text-md font-semibold text-gray-700 mb-3 flex items-center"
              >
                <CalendarCheckIcon class="w-5 h-5 mr-2 text-green-500" />Hôm nay
              </h3>
              <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
                <DepartureCard
                  v-for="dep in paginatedCategorizedDepartures.today.items"
                  :key="dep.id"
                  :departure="dep"
                  :tour-id="tour.id"
                  @edit="openEditModal"
                  @delete="handleDelete"
                />
              </div>
              <Pagination
                v-if="paginatedCategorizedDepartures.today.totalPages > 1"
                :current-page="departurePagination.today.currentPage"
                :total-pages="paginatedCategorizedDepartures.today.totalPages"
                @change-page="handleDeparturePageChange('today', $event)"
                class="mt-4"
              />
            </section>

            <section v-if="paginatedCategorizedDepartures.past.items.length">
              <h3
                class="text-md font-semibold text-gray-700 mb-3 flex items-center"
              >
                <CalendarXIcon class="w-5 h-5 mr-2 text-red-500" />Đã qua
              </h3>
              <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
                <DepartureCard
                  v-for="dep in paginatedCategorizedDepartures.past.items"
                  :key="dep.id"
                  :departure="dep"
                  :tour-id="tour.id"
                  :is-past="true"
                  @edit="openEditModal"
                  @delete="handleDelete"
                />
              </div>
              <Pagination
                v-if="paginatedCategorizedDepartures.past.totalPages > 1"
                :current-page="departurePagination.past.currentPage"
                :total-pages="paginatedCategorizedDepartures.past.totalPages"
                @change-page="handleDeparturePageChange('past', $event)"
                class="mt-4"
              />
            </section>
          </div>
        </div>
      </div>
    </div>

    <div
      v-if="showModal"
      class="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50 p-4"
    >
      <div
        class="bg-white rounded-xl shadow-2xl p-6 w-full max-w-lg"
        @click.stop
      >
        <h3 class="text-xl font-bold mb-5 text-gray-800">
          {{
            isEditing ? "Chỉnh sửa ngày khởi hành" : "Thêm ngày khởi hành mới"
          }}
        </h3>
        <form @submit.prevent="handleSave" class="space-y-4">
          <div v-for="field in formFields" :key="field.key">
            <label
              :for="field.key"
              class="block text-sm font-medium text-gray-700 mb-1"
              >{{ field.label }}</label
            >
            <input
              :id="field.key"
              :type="field.type"
              v-model="form[field.key]"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 transition"
              required
              :min="
                field.key === 'departureDate' && !isEditing ? todayString : null
              "
              :step="field.step || null"
            />
          </div>

          <div
            v-if="!isEditing"
            class="space-y-4 pt-4 border-t border-gray-200"
          >
            <div class="flex items-center">
              <input
                id="isBatch"
                type="checkbox"
                v-model="form.isBatch"
                class="h-4 w-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
              />
              <label
                for="isBatch"
                class="ml-3 block text-sm font-medium text-gray-800"
                >Tạo hàng loạt (lặp lại)</label
              >
            </div>

            <div
              v-if="form.isBatch"
              class="grid grid-cols-1 sm:grid-cols-2 gap-4 animate-fade-in"
            >
              <div>
                <label
                  for="count"
                  class="block text-sm font-medium text-gray-700 mb-1"
                  >Số lần lặp</label
                >
                <input
                  id="count"
                  type="number"
                  v-model="form.count"
                  min="2"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label
                  for="intervalDays"
                  class="block text-sm font-medium text-gray-700 mb-1"
                  >Lặp lại sau (ngày)</label
                >
                <select
                  id="intervalDays"
                  v-model="form.intervalDays"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-white focus:ring-2 focus:ring-blue-500"
                >
                  <option value="10">10 ngày</option>
                  <option value="30">30 ngày</option>
                  <option value="60">60 ngày</option>
                  <option value="7">7 ngày (hàng tuần)</option>
                  <option value="14">14 ngày (2 tuần)</option>
                </select>
              </div>
            </div>
          </div>

          <div v-if="isEditing">
            <label
              for="bookedSeats"
              class="block text-sm font-medium text-gray-700 mb-1"
              >Số chỗ đã đặt</label
            >
            <input
              id="bookedSeats"
              type="number"
              :value="form.bookedSeats"
              class="w-full px-3 py-2 border border-gray-200 rounded-lg bg-gray-100 cursor-not-allowed"
              disabled
            />
          </div>

          <div class="flex justify-end space-x-3 pt-5">
            <button
              type="button"
              @click="closeModal"
              class="px-5 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 font-semibold"
            >
              Hủy
            </button>
            <button
              type="submit"
              class="px-5 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-semibold"
            >
              {{ isEditing ? "Cập nhật" : "Lưu" }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import {
  PlusIcon,
  ChevronDownIcon,
  CalendarClockIcon,
  CalendarCheckIcon,
  CalendarXIcon,
  SearchIcon,
} from "lucide-vue-next";
import DepartureCard from "../components/Tours/DepartureCard.vue";
import Pagination from "../components/Tours/Pagination.vue"; // Đảm bảo đã import
import tourAdminApi from "../api/tourAdminApi";
import { departureApi } from "../api/DepartureApi.js";

// --- STATE & LOGIC ---
const tours = ref([]);
const isLoading = ref(true);
const error = ref(null);
const searchTerm = ref("");
const activeTourId = ref(null);
const showModal = ref(false);
const isEditing = ref(false);
const form = ref({});
const currentTourIdForModal = ref(null);

// MỚI: State để quản lý phân trang cho các ngày khởi hành
const departurePagination = ref({
  upcoming: { currentPage: 0, pageSize: 4 },
  today: { currentPage: 0, pageSize: 4 },
  past: { currentPage: 0, pageSize: 4 },
});

const formFields = [
  {
    key: "departureDate",
    label: "Ngày khởi hành (hoặc ngày bắt đầu)",
    type: "date",
  },
  {
    key: "adultPrice",
    label: "Giá người lớn (VNĐ)",
    type: "number",
    step: 1000,
  },
  { key: "childPrice", label: "Giá trẻ em (VNĐ)", type: "number", step: 1000 },
  { key: "discount", label: "Giảm giá (VNĐ)", type: "number", step: 1 },
  { key: "seatCount", label: "Tổng số chỗ", type: "number", step: 1 },
];

const formatDateForInput = (dateStr) => {
  if (!dateStr) return "";
  const d = new Date(dateStr);
  const year = d.getFullYear();
  const month = `0${d.getMonth() + 1}`.slice(-2);
  const day = `0${d.getDate()}`.slice(-2);
  return `${year}-${month}-${day}`;
};

const todayString = formatDateForInput(new Date());

const createEmptyForm = () => ({
  departureDate: todayString,
  adultPrice: 1000000,
  childPrice: 500000,
  discount: 0,
  seatCount: 30,
  isBatch: false,
  count: 3,
  intervalDays: 30,
});

// --- COMPUTED PROPERTIES ---
const filteredTours = computed(() => {
  if (!searchTerm.value) return tours.value;
  return tours.value.filter((tour) =>
    tour.name.toLowerCase().includes(searchTerm.value.toLowerCase())
  );
});

const getDeparturesForActiveTour = () => {
  if (!activeTourId.value) return [];
  const activeTour = tours.value.find((t) => t.id === activeTourId.value);
  return activeTour && activeTour.departures ? activeTour.departures : [];
};

// Đã cập nhật: Lấy tất cả các ngày khởi hành và phân loại chúng
const categorizedDepartures = computed(() => {
  const categories = { upcoming: [], today: [], past: [] };
  const departures = getDeparturesForActiveTour();
  if (!departures.length) return categories;

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  departures.forEach((dep) => {
    const departureDate = new Date(dep.departureDate);
    departureDate.setHours(0, 0, 0, 0);
    if (departureDate.getTime() > today.getTime())
      categories.upcoming.push(dep);
    else if (departureDate.getTime() === today.getTime())
      categories.today.push(dep);
    else categories.past.push(dep);
  });

  categories.upcoming.sort(
    (a, b) => new Date(a.departureDate) - new Date(b.departureDate)
  );
  categories.past.sort(
    (a, b) => new Date(b.departureDate) - new Date(a.departureDate)
  );
  return categories;
});

// MỚI: Computed property để phân trang các danh mục đã phân loại
const paginatedCategorizedDepartures = computed(() => {
  const allDepartures = categorizedDepartures.value;
  const paginatedResult = {};

  for (const category in allDepartures) {
    const { currentPage, pageSize } = departurePagination.value[category];
    const items = allDepartures[category];

    const startIndex = currentPage * pageSize;
    const endIndex = startIndex + pageSize;

    paginatedResult[category] = {
      items: items.slice(startIndex, endIndex),
      totalPages: Math.ceil(items.length / pageSize),
    };
  }
  return paginatedResult;
});

// --- METHODS ---
const toggleTour = (tourId) => {
  // Reset phân trang về trang đầu tiên mỗi khi mở một tour
  departurePagination.value.upcoming.currentPage = 0;
  departurePagination.value.today.currentPage = 0;
  departurePagination.value.past.currentPage = 0;

  activeTourId.value = activeTourId.value === tourId ? null : tourId;
};

// MỚI: Hàm xử lý chuyển trang cho các ngày khởi hành
const handleDeparturePageChange = (category, newPage) => {
  if (departurePagination.value[category]) {
    departurePagination.value[category].currentPage = newPage;
  }
};

onMounted(async () => {
  try {
    const tourData = await tourAdminApi.getAllTours();
    if (!tourData) {
      throw new Error("API không trả về dữ liệu.");
    }
    tours.value = Array.isArray(tourData) ? tourData : [tourData];
  } catch (e) {
    console.error("Lỗi chi tiết khi tải tour:", e);
    error.value =
      (e.response && e.response.data.message) ||
      e.message ||
      "Không thể tải dữ liệu.";
  } finally {
    isLoading.value = false;
  }
});

const openAddModal = (tourId) => {
  isEditing.value = false;
  currentTourIdForModal.value = tourId;
  form.value = createEmptyForm();
  showModal.value = true;
};

const openEditModal = (departure, tourId) => {
  isEditing.value = true;
  currentTourIdForModal.value = tourId;
  form.value = {
    ...departure,
    departureDate: formatDateForInput(departure.departureDate),
  };
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
};

const handleSave = async () => {
  try {
    const tour = tours.value.find((t) => t.id === currentTourIdForModal.value);

    if (isEditing.value) {
      const updatedData = await departureApi.updateDeparture(
        form.value.id,
        form.value
      );
      const index = tour.departures.findIndex((d) => d.id === updatedData.id);
      if (index !== -1) {
        tour.departures[index] = updatedData;
      }
    } else {
      if (form.value.isBatch) {
        const payload = {
          templateDto: {
            departureDate: form.value.departureDate,
            adultPrice: form.value.adultPrice,
            childPrice: form.value.childPrice,
            discount: form.value.discount,
            seatCount: form.value.seatCount,
          },
          intervalDays: parseInt(form.value.intervalDays, 10),
          count: parseInt(form.value.count, 10),
        };
        const newDepartures = await departureApi.createRecurringDepartures(
          currentTourIdForModal.value,
          payload
        );
        if (!tour.departures) tour.departures = [];
        tour.departures.push(...newDepartures);
      } else {
        const newData = await departureApi.createDeparture(
          currentTourIdForModal.value,
          form.value
        );
        if (!tour.departures) tour.departures = [];
        tour.departures.push(newData);
      }
    }
    closeModal();
  } catch (e) {
    alert(`Lỗi khi lưu: ${e.message}`);
  }
};

const handleDelete = async (departureId, tourId) => {
  if (confirm("Bạn có chắc chắn muốn xóa ngày khởi hành này?")) {
    try {
      await departureApi.deleteDeparture(departureId);
      const tour = tours.value.find((t) => t.id === tourId);
      if (tour && tour.departures) {
        tour.departures = tour.departures.filter((d) => d.id !== departureId);
      }
    } catch (e) {
      alert(
        `Lỗi khi xóa: ${(e.response && e.response.data.message) || e.message}`
      );
    }
  }
};
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
