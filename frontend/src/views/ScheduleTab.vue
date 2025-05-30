<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h2 class="text-3xl font-bold text-gray-800">Lịch trình Tour</h2>
      <button
        @click="openAddForm"
        class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 flex items-center"
      >
        <PlusIcon class="w-4 h-4 mr-2" />
        Thêm lịch trình
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

    <!-- Schedules List -->
    <div class="space-y-4">
      <div
        v-for="schedule in filteredSchedules"
        :key="schedule.id"
        class="bg-white rounded-lg shadow-md p-6"
      >
        <div class="flex items-start justify-between mb-4">
          <div>
            <h3 class="text-lg font-semibold text-gray-800">
              {{ schedule.tour_name }}
            </h3>
            <p class="text-sm text-gray-600">{{ schedule.ngay }}</p>
          </div>
          <div class="flex space-x-2">
            <button
              @click="editSchedule(schedule)"
              class="text-blue-600 hover:text-blue-800"
            >
              <EditIcon class="w-4 h-4" />
            </button>
            <button
              @click="deleteSchedule(schedule.id)"
              class="text-red-600 hover:text-red-800"
            >
              <TrashIcon class="w-4 h-4" />
            </button>
          </div>
        </div>
        <div class="bg-gray-50 rounded-lg p-4">
          <pre class="text-sm text-gray-700 whitespace-pre-wrap font-sans"
            >{{ schedule.hoat_dong }}
          </pre>
        </div>
      </div>
    </div>

    <!-- Add/Edit Form Modal -->
    <div
      v-if="showAddForm"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    >
      <div class="bg-white rounded-lg p-6 w-full max-w-2xl">
        <h3 class="text-lg font-semibold mb-4">
          {{ editingSchedule ? "Chỉnh sửa lịch trình" : "Thêm lịch trình mới" }}
        </h3>
        <form @submit.prevent="submitForm" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1"
              >Tour</label
            >
            <select
              v-model="form.tour_id"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500"
            >
              <option value="1">Hạ Long Bay 3N2Đ</option>
              <option value="2">Sapa Trekking 2N1Đ</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1"
              >Ngày thứ</label
            >
            <input
              v-model="form.ngay"
              type="text"
              placeholder="Ví dụ: Ngày 1"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1"
              >Hoạt động</label
            >
            <textarea
              v-model="form.hoat_dong"
              rows="6"
              placeholder="Nhập chi tiết hoạt động trong ngày"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div class="flex space-x-3 pt-4">
            <button
              type="button"
              @click="closeForm"
              class="flex-1 bg-gray-300 text-gray-700 py-2 rounded-lg hover:bg-gray-400"
            >
              Hủy
            </button>
            <button
              type="submit"
              class="flex-1 bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700"
            >
              {{ editingSchedule ? "Cập nhật" : "Thêm" }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { PlusIcon, EditIcon, TrashIcon, SearchIcon } from "lucide-vue-next";

const schedules = ref([
  {
    id: 1,
    tour_id: 1,
    tour_name: "Hạ Long Bay 3N2Đ",
    ngay: "Ngày 1",
    hoat_dong: `- 08:00: Khởi hành từ Hà Nội
- 12:00: Đến Hạ Long, ăn trưa
- 14:00: Check-in khách sạn
- 15:00: Thăm hang Sửng Sốt
- 19:00: Ăn tối trên vịnh`,
  },
  {
    id: 2,
    tour_id: 1,
    tour_name: "Hạ Long Bay 3N2Đ",
    ngay: "Ngày 2",
    hoat_dong: `- 07:00: Ăn sáng
- 09:00: Chèo kayak
- 12:00: Ăn trưa
- 15:00: Thăm làng chài
- 19:00: BBQ dinner`,
  },
]);

const searchTerm = ref("");
const showAddForm = ref(false);
const editingSchedule = ref(null);

const form = ref({
  tour_id: 1,
  tour_name: "Hạ Long Bay 3N2Đ",
  ngay: "",
  hoat_dong: "",
});

const filteredSchedules = computed(() =>
  schedules.value.filter((schedule) =>
    schedule.tour_name.toLowerCase().includes(searchTerm.value.toLowerCase())
  )
);

function openAddForm() {
  form.value = {
    tour_id: 1,
    tour_name: "Hạ Long Bay 3N2Đ",
    ngay: "",
    hoat_dong: "",
  };
  editingSchedule.value = null;
  showAddForm.value = true;
}

function editSchedule(schedule) {
  editingSchedule.value = schedule;
  form.value = { ...schedule };
  showAddForm.value = true;
}

function deleteSchedule(id) {
  schedules.value = schedules.value.filter((s) => s.id !== id);
}

function closeForm() {
  showAddForm.value = false;
  editingSchedule.value = null;
}

function submitForm() {
  if (editingSchedule.value) {
    // Update
    const idx = schedules.value.findIndex(
      (s) => s.id === editingSchedule.value.id
    );
    if (idx !== -1) {
      schedules.value[idx] = { ...form.value };
    }
  } else {
    // Add
    const newId = Math.max(...schedules.value.map((s) => s.id)) + 1;
    schedules.value.push({ id: newId, ...form.value });
  }
  closeForm();
}
</script>

<style scoped>
/* Add any scoped styles if needed */
</style>
