<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h2 class="text-3xl font-bold text-gray-800">Quản lý Tour</h2>
    </div>

    <!-- Tab navigation -->
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

    <!-- Tab content -->
    <div v-if="currentTab === 'danh-sach'">
      <!-- Search bar -->
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

      <!-- Tours table -->
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
                  Booking
                </th>
                <th
                  class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase"
                >
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr v-for="tour in filteredTours" :key="tour.id">
                <td
                  class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900"
                >
                  {{ tour.name }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  ₫{{ tour.price }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {{ tour.duration }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span
                    :class="[
                      'px-2 py-1 text-xs rounded-full',
                      tour.status === 'Hoạt động'
                        ? 'bg-green-100 text-green-800'
                        : 'bg-red-100 text-red-800',
                    ]"
                  >
                    {{ tour.status }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {{ tour.bookings }} booking
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  <div class="flex space-x-2">
                    <button
                      @click="viewTour(tour)"
                      class="text-blue-600 hover:text-blue-900"
                    >
                      <EyeIcon class="w-4 h-4" />
                    </button>
                    <button
                      @click="editTour(tour)"
                      class="text-green-600 hover:text-green-900"
                    >
                      <EditIcon class="w-4 h-4" />
                    </button>
                    <button
                      @click="deleteTour(tour.id)"
                      class="text-red-600 hover:text-red-900"
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

    <!-- Add/Edit Tour tab -->
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
import { ref, computed } from "vue";
import { EditIcon, TrashIcon, EyeIcon, SearchIcon } from "lucide-vue-next";
import AddTourTab from "./AddTourTab.vue";

const currentTab = ref("danh-sach");
const formMode = ref("add"); // 'add', 'edit', or 'view'
const selectedTour = ref(null);

const tours = ref([
  {
    id: 1,
    name: "Hạ Long Bay 3N2Đ",
    price: "2,500,000",
    duration: "3 ngày 2 đêm",
    status: "Hoạt động",
    bookings: 15,
    ten_tour: "Hạ Long Bay 3N2Đ",
    mo_ta: "Tour du lịch Hạ Long 3 ngày 2 đêm",
    gia: "2500000",
    thoi_gian: "3 ngày 2 đêm",
    diem_khoi_hanh: "Hà Nội",
    diem_den: "Hạ Long",
    hinh_anh: [],
    lich_trinh: [
      {
        hoat_dong: "Ngày 1: Khởi hành từ Hà Nội - Check in khách sạn",
      },
      {
        hoat_dong: "Ngày 2: Tham quan vịnh Hạ Long",
      },
      {
        hoat_dong: "Ngày 3: Tham quan hang động - Về Hà Nội",
      },
    ],
    ngay_khoi_hanh: [
      {
        ngay: "2024-04-15",
        gia_nguoi_lon: "2500000",
        gia_tre_em: "1250000",
        giam_gia: "0",
        so_cho: "20",
        so_cho_da_dat: 5,
      },
    ],
  },
  {
    id: 2,
    name: "Sapa Trekking 2N1Đ",
    price: "1,800,000",
    duration: "2 ngày 1 đêm",
    status: "Hoạt động",
    bookings: 8,
  },
  {
    id: 3,
    name: "Phú Quốc 4N3Đ",
    price: "3,200,000",
    duration: "4 ngày 3 đêm",
    status: "Tạm dừng",
    bookings: 0,
  },
  {
    id: 4,
    name: "Đà Nẵng - Hội An 3N2Đ",
    price: "2,100,000",
    duration: "3 ngày 2 đêm",
    status: "Hoạt động",
    bookings: 12,
  },
]);

const searchTerm = ref("");

const filteredTours = computed(() => {
  return tours.value.filter((tour) =>
    tour.name.toLowerCase().includes(searchTerm.value.toLowerCase())
  );
});

function deleteTour(id) {
  if (confirm("Bạn có chắc chắn muốn xóa tour này?")) {
    tours.value = tours.value.filter((t) => t.id !== id);
  }
}

function editTour(tour) {
  selectedTour.value = { ...tour };
  formMode.value = "edit";
  currentTab.value = "them-moi";
}

function viewTour(tour) {
  selectedTour.value = { ...tour };
  formMode.value = "view";
  currentTab.value = "them-moi";
}

function handleCancel() {
  selectedTour.value = null;
  formMode.value = "add";
  currentTab.value = "danh-sach";
}

function handleSubmit(tourData) {
  if (formMode.value === "edit") {
    const index = tours.value.findIndex((t) => t.id === tourData.id);
    if (index !== -1) {
      tours.value[index] = {
        ...tours.value[index],
        ...tourData,
        name: tourData.ten_tour,
        price: new Intl.NumberFormat("vi-VN").format(tourData.gia),
        duration: tourData.thoi_gian,
        status: tours.value[index].status,
      };
    }
  } else if (formMode.value === "add") {
    const newTour = {
      id: tours.value.length + 1,
      ...tourData,
      name: tourData.ten_tour,
      price: new Intl.NumberFormat("vi-VN").format(tourData.gia),
      duration: tourData.thoi_gian,
      status: "Hoạt động",
      bookings: 0,
    };
    tours.value.push(newTour);
  }
  handleCancel();
}

function switchTab(tab) {
  if (tab === "them-moi") {
    selectedTour.value = null;
    formMode.value = "add";
  }
  currentTab.value = tab;
}

function getTabLabel(tab) {
  if (tab === "danh-sach") return "Danh sách tour";
  if (tab === "them-moi") {
    if (formMode.value === "edit") return "Sửa tour";
    if (formMode.value === "view") return "Xem tour";
    return "Thêm tour mới";
  }
  return "";
}
</script>

<style scoped></style>
