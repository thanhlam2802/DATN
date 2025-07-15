<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h2 class="text-3xl font-bold text-gray-800">Quản lý Tour</h2>
    </div>

    <div class="bg-white rounded-lg shadow">
      <div class="border-b border-gray-200">
        <nav class="flex space-x-8 px-4" aria-label="Tabs">
          <button
            v-for="tab in ['danh-sach', 'them-moi']"
            :key="tab"
            @click="switchTab(tab)"
            :class="[
              currentTab === tab
                ? 'border-blue-500 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300',
              'whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm',
            ]"
          >
            {{ getTabLabel(tab) }}
          </button>
        </nav>
      </div>
    </div>

    <div v-if="currentTab === 'danh-sach'">
      <div class="bg-white rounded-lg shadow p-4">
        <div class="relative">
          <SearchIcon
            class="w-5 h-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"
          />
          <input
            type="text"
            placeholder="Tìm kiếm tour..."
            v-model="searchTerm"
            class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>
      </div>

      <div class="bg-white rounded-lg shadow overflow-hidden mt-4">
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th
                  class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase"
                >
                  Tên Tour
                </th>
                <th
                  class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase"
                >
                  Giá
                </th>
                <th
                  class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase"
                >
                  Thời gian
                </th>
                <th
                  class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase"
                >
                  Trạng thái
                </th>
                <th
                  class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase"
                >
                  Bookings
                </th>
                <th
                  class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase"
                >
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr v-if="isLoading" class="text-center">
                <td colspan="6" class="p-4">Đang tải dữ liệu...</td>
              </tr>
              <tr v-else-if="filteredTours.length === 0" class="text-center">
                <td colspan="6" class="p-4 text-gray-500">
                  Không tìm thấy tour nào.
                </td>
              </tr>
              <tr v-for="tour in filteredTours" :key="tour.id" v-else>
                <td
                  class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900"
                >
                  {{ tour.name }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {{
                    new Intl.NumberFormat("vi-VN", {
                      style: "currency",
                      currency: "VND",
                    }).format(tour.price)
                  }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {{ tour.durationDays }} ngày
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span
                    :class="[
                      'px-2 py-1 text-xs rounded-full',
                      tour.status === 'ACTIVE'
                        ? 'bg-green-100 text-green-800'
                        : 'bg-red-100 text-red-800',
                    ]"
                    >{{
                      tour.status === "ACTIVE" ? "Hoạt động" : "Tạm dừng"
                    }}</span
                  >
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {{ tour.bookings_count ?? 0 }} booking
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  <div class="flex space-x-2">
                    <button
                      @click="viewTour(tour)"
                      class="text-blue-600 hover:text-blue-900"
                      title="Xem"
                    >
                      <EyeIcon class="w-4 h-4" />
                    </button>
                    <button
                      @click="editTour(tour)"
                      class="text-green-600 hover:text-green-900"
                      title="Sửa"
                    >
                      <EditIcon class="w-4 h-4" />
                    </button>
                    <button
                      @click="handleDelete(tour.id)"
                      class="text-red-600 hover:text-red-900"
                      title="Xóa"
                    >
                      <TrashIcon class="w-4 h-4" />
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <div v-else>
      <AddTourTab
        :tour-data="selectedTour"
        :mode="formMode"
        @cancel="handleCancel"
        @submit="handleSubmit"
      />
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from "vue";
import { EditIcon, TrashIcon, EyeIcon, SearchIcon } from "lucide-vue-next";
import AddTourTab from "./AddTourTab.vue";
import apiService from "../api/tourAdminApi.js";

// --- STATE MANAGEMENT ---
const currentTab = ref("danh-sach");
const formMode = ref("add"); // 'add', 'edit', 'view'
const selectedTour = ref(null);
const tours = ref([]);
const searchTerm = ref("");
const isLoading = ref(true);

// --- COMPUTED PROPERTIES ---
const filteredTours = computed(() => {
  if (!searchTerm.value) return tours.value;
  const lowerCaseSearch = searchTerm.value.toLowerCase();
  return tours.value.filter(
    (tour) =>
      tour.name.toLowerCase().includes(lowerCaseSearch) ||
      tour.destination.toLowerCase().includes(lowerCaseSearch) ||
      tour.departurePoint.toLowerCase().includes(lowerCaseSearch)
  );
});

// --- API & DATA HANDLING ---
async function fetchTours() {
  isLoading.value = true;
  console.log(
    "--- [ToursTab] Bắt đầu gọi API để lấy danh sách tour (fetchTours) ---"
  );
  try {
    const tourList = await apiService.getAllTours();
    console.log(
      "--- [ToursTab] Nhận được danh sách tour thành công từ API: ---",
      tourList
    );
    tours.value = tourList;
  } catch (error) {
    alert("Lỗi khi tải danh sách tour: " + error.message);
    console.error("Lỗi khi fetchTours:", error);
  } finally {
    isLoading.value = false;
  }
}

onMounted(fetchTours);

async function handleDelete(id) {
  console.log(`--- [ToursTab] Người dùng yêu cầu xóa tour có ID: ${id} ---`);
  if (
    confirm("Bạn có chắc chắn muốn xóa tour này? Thao tác không thể hoàn tác.")
  ) {
    try {
      console.log(`--- [ToursTab] Gọi API để xóa tour ID: ${id} ---`);
      await apiService.deleteTour(id);
      console.log(
        `--- [ToursTab] Xóa tour ID: ${id} thành công. Tải lại danh sách. ---`
      );
      alert("Xóa tour thành công!");
      await fetchTours();
    } catch (error) {
      alert("Xóa tour thất bại: " + error.message);
      console.error("Lỗi khi xóa tour:", error);
    }
  } else {
    console.log("--- [ToursTab] Người dùng đã hủy thao tác xóa.");
  }
}

async function handleSubmit(dataFromForm) {
  console.log(
    "--- [ToursTab] Nhận được sự kiện SUBMIT từ form con với dữ liệu: ---",
    JSON.parse(JSON.stringify(dataFromForm))
  );

  // 1. Phân loại hình ảnh
  const newImageFiles = [];
  const existingImageUrls = [];
  if (dataFromForm.hinh_anh) {
    dataFromForm.hinh_anh.forEach((img) => {
      if (img.file instanceof File) {
        newImageFiles.push(img.file);
      } else if (img.url) {
        existingImageUrls.push(img.url);
      }
    });
  }
  console.log("--- [ToursTab] Đã phân loại hình ảnh: ---", {
    newImageFiles,
    existingImageUrls,
  });

  // 2. Tạo DTO để gửi đi
  const tourRequestDTO = {
    name: dataFromForm.ten_tour,
    description: dataFromForm.mo_ta,
    price: dataFromForm.gia,
    durationDays: dataFromForm.durationDays,
    departurePoint: dataFromForm.diem_khoi_hanh,
    destination: dataFromForm.diem_den,
    status: dataFromForm.status,
    imageUrls: existingImageUrls,
    schedules: dataFromForm.lich_trinh.map((s) => ({
      id: s.id,
      dayNumber: s.dayNumber,
      title: s.title,
      activities: (s.activities || []).map((act) => ({
        ...act,
        activityTitle: act.activity,
      })),
    })),
    departures: dataFromForm.ngay_khoi_hanh.map((d) => ({
      id: d.id,
      departureDate: d.ngay,
      adultPrice: d.gia_nguoi_lon,
      childPrice: d.gia_tre_em,
      discount: d.giam_gia || 0,
      seatCount: d.so_cho,
    })),
    tags: dataFromForm.tags.map((t) => ({ id: t.id, name: t.name })),
  };
  console.log(
    "--- [ToursTab] Đã tạo DTO để gửi cho backend: ---",
    tourRequestDTO
  );

  // 3. Tạo FormData
  const formData = new FormData();
  formData.append(
    "tourData",
    new Blob([JSON.stringify(tourRequestDTO)], { type: "application/json" })
  );
  const imageFilesKey = formMode.value === "edit" ? "newImages" : "images";
  if (newImageFiles.length > 0) {
    newImageFiles.forEach((file) => {
      formData.append(imageFilesKey, file);
    });
  }
  console.log(
    `--- [ToursTab] Đã tạo FormData. Key cho ảnh là: '${imageFilesKey}'.`
  );

  // 4. Gọi API
  console.log(
    `--- [ToursTab] Chuẩn bị gọi API ở chế độ: '${formMode.value}' ---`
  );
  try {
    if (formMode.value === "edit") {
      await apiService.updateTour(dataFromForm.id, formData);
      alert("Cập nhật tour thành công!");
    } else {
      await apiService.createTour(formData);
      alert("Tạo tour mới thành công!");
    }
    console.log(
      "--- [ToursTab] Thao tác API thành công. Tải lại danh sách và chuyển tab. ---"
    );
    await fetchTours();
    handleCancel();
  } catch (error) {
    alert("Thao tác thất bại: " + error.message);
    console.error("Lỗi khi submit tour:", error);
  }
}

// --- UI & NAVIGATION ---
function mapApiToForm(apiTour) {
  const formReadyData = {
    id: apiTour.id,
    ten_tour: apiTour.name,
    mo_ta: apiTour.description,
    gia: apiTour.price,
    durationDays: apiTour.durationDays,
    diem_khoi_hanh: apiTour.departurePoint,
    diem_den: apiTour.destination,
    status: apiTour.status,
    hinh_anh: (apiTour.tourImages || []).map((img) => ({
      url: img.url,
      id: img.id,
    })),
    lich_trinh: (apiTour.tourSchedules || []).map((s) => ({
      ...s,
      dayNumber: s.day,
    })),
    ngay_khoi_hanh: (apiTour.departures || []).map((d) => ({
      id: d.id,
      ngay: d.departureDate,
      gia_nguoi_lon: d.adultPrice,
      gia_tre_em: d.childPrice,
      giam_gia: d.discount,
      so_cho: d.seatCount,
      so_cho_da_dat: d.bookedSeats,
    })),
    tags: apiTour.tags || [],
  };
  console.log(
    "--- [ToursTab] Đã map dữ liệu từ API sang cấu trúc cho form: ---",
    formReadyData
  );
  return formReadyData;
}

function editTour(tour) {
  console.log(
    "--- [ToursTab] Người dùng bấm nút Sửa. Dữ liệu gốc từ bảng: ---",
    tour
  );
  selectedTour.value = mapApiToForm(tour);
  formMode.value = "edit";
  currentTab.value = "them-moi";
}

function viewTour(tour) {
  console.log(
    "--- [ToursTab] Người dùng bấm nút Xem. Dữ liệu gốc từ bảng: ---",
    tour
  );
  selectedTour.value = mapApiToForm(tour);
  formMode.value = "view";
  currentTab.value = "them-moi";
}

function handleCancel() {
  console.log(
    "--- [ToursTab] Thực hiện handleCancel: Reset form và chuyển về tab danh sách. ---"
  );
  selectedTour.value = null;
  formMode.value = "add";
  currentTab.value = "danh-sach";
}

function switchTab(tab) {
  console.log(`--- [ToursTab] Chuyển tab sang: '${tab}' ---`);
  if (tab === "them-moi" && currentTab.value !== "them-moi") {
    console.log(
      "--- [ToursTab] Reset form về trạng thái Thêm mới do chuyển tab. ---"
    );
    selectedTour.value = null;
    formMode.value = "add";
  }
  currentTab.value = tab;
}

function getTabLabel(tab) {
  if (tab === "danh-sach") return "Danh sách tour";
  if (tab === "them-moi") {
    if (formMode.value === "edit") return "Chỉnh sửa tour";
    if (formMode.value === "view") return "Xem chi tiết tour";
    return "Thêm tour mới";
  }
  return "";
}
</script>
<style scoped>
/* CSS không thay đổi */
</style>
