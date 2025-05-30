<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <h2 class="text-3xl font-bold text-gray-800">Ngày khởi hành</h2>
      <button
        @click="openAddForm"
        class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 flex items-center"
      >
        <PlusIcon class="w-4 h-4 mr-2" />
        Thêm ngày khởi hành
      </button>
    </div>

    <!-- Search -->
    <div class="bg-white rounded-lg shadow p-4">
      <div class="relative">
        <SearchIcon
          class="w-5 h-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"
        />
        <input
          type="text"
          placeholder="Tìm kiếm theo tên tour..."
          v-model="searchTerm"
          class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
        />
      </div>
    </div>

    <!-- Departures Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div
        v-for="departure in filteredDepartures"
        :key="departure.id"
        class="bg-white rounded-lg shadow-md p-6"
      >
        <div class="flex items-start justify-between mb-4">
          <div>
            <h3 class="text-lg font-semibold text-gray-800">
              {{ departure.tour_name }}
            </h3>
            <div class="flex items-center text-sm text-gray-600 mt-1">
              <CalendarIcon class="w-4 h-4 mr-2" />
              {{ formatDate(departure.ngay) }}
            </div>
          </div>
          <div class="flex space-x-2">
            <button
              @click="editDeparture(departure)"
              class="text-blue-600 hover:text-blue-800"
            >
              <EditIcon class="w-4 h-4" />
            </button>
            <button
              @click="deleteDeparture(departure.id)"
              class="text-red-600 hover:text-red-800"
            >
              <TrashIcon class="w-4 h-4" />
            </button>
          </div>
        </div>
        <div class="space-y-3">
          <div class="flex justify-between items-center">
            <span class="text-sm text-gray-600">Giá người lớn:</span>
            <span class="font-semibold text-gray-800">{{
              formatCurrency(departure.gia_nguoi_lon)
            }}</span>
          </div>
          <div class="flex justify-between items-center">
            <span class="text-sm text-gray-600">Giá trẻ em:</span>
            <span class="font-semibold text-gray-800">{{
              formatCurrency(departure.gia_tre_em)
            }}</span>
          </div>
          <div
            v-if="departure.giam_gia > 0"
            class="flex justify-between items-center text-green-600"
          >
            <span class="text-sm">Giảm giá:</span>
            <span class="font-semibold">{{
              formatCurrency(departure.giam_gia)
            }}</span>
          </div>
          <div class="pt-3 border-t">
            <div class="flex justify-between items-center">
              <span class="text-sm text-gray-600">Số chỗ:</span>
              <span class="font-semibold">
                {{ departure.so_cho_da_dat }}/{{ departure.so_cho }}
              </span>
            </div>
            <div class="mt-2 w-full bg-gray-200 rounded-full h-2">
              <div
                class="bg-blue-600 h-2 rounded-full"
                :style="{
                  width: `${
                    (departure.so_cho_da_dat / departure.so_cho) * 100
                  }%`,
                }"
              />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal -->
    <div
      v-if="showAddForm"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    >
      <div class="bg-white rounded-lg p-6 w-full max-w-md">
        <h3 class="text-lg font-semibold mb-4">
          {{
            editingDeparture
              ? "Chỉnh sửa ngày khởi hành"
              : "Thêm ngày khởi hành mới"
          }}
        </h3>
        <form @submit.prevent="saveDeparture" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1"
              >Tour</label
            >
            <select
              v-model="form.tour_name"
              class="w-full border rounded-lg px-3 py-2"
            >
              <option value="Hạ Long Bay 3N2Đ">Hạ Long Bay 3N2Đ</option>
              <option value="Sapa Trekking 2N1Đ">Sapa Trekking 2N1Đ</option>
            </select>
          </div>
          <div v-for="field in fields" :key="field.key">
            <label class="block text-sm font-medium text-gray-700 mb-1">
              {{ field.label }}
            </label>
            <input
              :type="field.type"
              v-model="form[field.key]"
              class="w-full border rounded-lg px-3 py-2"
            />
          </div>
          <div class="flex space-x-3 pt-4">
            <button
              type="button"
              @click="closeForm"
              class="flex-1 bg-gray-300 py-2 rounded-lg"
            >
              Hủy
            </button>
            <button
              type="submit"
              class="flex-1 bg-blue-600 text-white py-2 rounded-lg"
            >
              {{ editingDeparture ? "Cập nhật" : "Thêm" }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import {
  PlusIcon,
  EditIcon,
  TrashIcon,
  CalendarIcon,
  SearchIcon,
} from "lucide-vue-next";

const departures = ref([
  {
    id: 1,
    tour_id: 1,
    tour_name: "Hạ Long Bay 3N2Đ",
    ngay: "2024-02-15",
    gia_nguoi_lon: 2500000,
    gia_tre_em: 1250000,
    giam_gia: 0,
    so_cho: 30,
    so_cho_da_dat: 12,
  },
  {
    id: 2,
    tour_id: 1,
    tour_name: "Hạ Long Bay 3N2Đ",
    ngay: "2024-02-20",
    gia_nguoi_lon: 2500000,
    gia_tre_em: 1250000,
    giam_gia: 200000,
    so_cho: 30,
    so_cho_da_dat: 5,
  },
]);

const showAddForm = ref(false);
const editingDeparture = ref(null);
const searchTerm = ref("");
const form = ref({
  tour_name: "",
  ngay: "",
  gia_nguoi_lon: 0,
  gia_tre_em: 0,
  giam_gia: 0,
  so_cho: 30,
  so_cho_da_dat: 0,
});

const fields = [
  { key: "ngay", label: "Ngày khởi hành", type: "date" },
  { key: "gia_nguoi_lon", label: "Giá người lớn (VNĐ)", type: "number" },
  { key: "gia_tre_em", label: "Giá trẻ em (VNĐ)", type: "number" },
  { key: "giam_gia", label: "Giảm giá (VNĐ)", type: "number" },
  { key: "so_cho", label: "Số chỗ", type: "number" },
];

const filteredDepartures = computed(() =>
  departures.value.filter((d) =>
    d.tour_name.toLowerCase().includes(searchTerm.value.toLowerCase())
  )
);

function formatCurrency(amount) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount);
}

function formatDate(dateStr) {
  return new Date(dateStr).toLocaleDateString("vi-VN");
}

function openAddForm() {
  editingDeparture.value = null;
  Object.assign(form.value, {
    tour_name: "",
    ngay: "",
    gia_nguoi_lon: 0,
    gia_tre_em: 0,
    giam_gia: 0,
    so_cho: 30,
    so_cho_da_dat: 0,
  });
  showAddForm.value = true;
}

function editDeparture(dep) {
  editingDeparture.value = dep;
  Object.assign(form.value, { ...dep });
  showAddForm.value = true;
}

function closeForm() {
  showAddForm.value = false;
  editingDeparture.value = null;
}

function saveDeparture() {
  if (editingDeparture.value) {
    Object.assign(editingDeparture.value, form.value);
  } else {
    const newId = Math.max(...departures.value.map((d) => d.id)) + 1;
    departures.value.push({ id: newId, ...form.value });
  }
  closeForm();
}

function deleteDeparture(id) {
  departures.value = departures.value.filter((d) => d.id !== id);
}
</script>

<style scoped>
/* Thêm style tùy chỉnh nếu cần */
</style>
