<template>
  <div class="space-y-6">
    <div>
      <div class="flex items-center justify-between">
        <h2 class="text-3xl font-bold text-gray-800">Quản lý Lịch trình</h2>
        <button
          v-if="currentTab === 'list'"
          @click="openAddForm"
          class="btn btn-primary"
        >
          <PlusIcon class="w-4 h-4 mr-2" />
          Thêm Lịch trình
        </button>
      </div>
      <div class="border-b border-gray-200 mt-4">
        <nav class="-mb-px flex space-x-8" aria-label="Tabs">
          <button
            @click="switchToListView"
            :class="[
              'tab-button',
              currentTab === 'list'
                ? 'tab-button-active'
                : 'tab-button-inactive',
            ]"
          >
            Danh sách Lịch trình
          </button>
          <button
            v-if="currentTab === 'form'"
            :class="['tab-button', 'tab-button-active']"
          >
            {{ formTabLabel }}
          </button>
        </nav>
      </div>
    </div>
    <div v-if="currentTab === 'list'" class="space-y-6">
      <div v-if="isLoading" class="text-center py-10 text-gray-500">
        <p>Đang tải dữ liệu...</p>
      </div>
      <div v-else class="space-y-6">
        <div
          v-for="tour in toursWithSchedules"
          :key="tour.id"
          class="bg-white rounded-xl shadow-lg border"
        >
          <div class="bg-gray-50 p-4 border-b rounded-t-xl">
            <h3 class="text-xl font-bold text-gray-900">{{ tour.name }}</h3>
          </div>
          <template v-if="tour.tourSchedules && tour.tourSchedules.length > 0">
            <div class="divide-y">
              <div
                v-for="schedule in tour.tourSchedules"
                :key="schedule.id"
                class="p-4 group hover:bg-slate-50"
              >
                <div class="flex items-start justify-between gap-4">
                  <div class="flex-grow min-w-0">
                    <h4 class="font-semibold text-blue-800">
                      Ngày {{ schedule.day }}: {{ schedule.title }}
                    </h4>
                    <div class="mt-3 pl-4 border-l-2 border-blue-200 space-y-2">
                      <div
                        v-for="activity in schedule.activities"
                        :key="activity.id"
                        class="flex items-start gap-3 text-sm"
                      >
                        <i
                          :class="[
                            activity.icon || 'fas fa-check',
                            'text-gray-500 w-4 text-center mt-1',
                          ]"
                        ></i>
                        <div class="min-w-0">
                          <p class="font-medium text-gray-800 truncate">
                            {{ activity.time }} -
                            {{ activity.activity }}
                          </p>
                          <p class="text-gray-600">
                            {{ activity.description }}
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div
                    class="flex items-center space-x-1 flex-shrink-0 opacity-0 group-hover:opacity-100 transition-opacity"
                  >
                    <button
                      @click="copySchedule(schedule, tour.id)"
                      class="btn-icon"
                      title="Sao chép"
                    >
                      <CopyIcon class="w-4 h-4" />
                    </button>
                    <button
                      @click="editSchedule(schedule, tour.id)"
                      class="btn-icon"
                      title="Sửa"
                    >
                      <EditIcon class="w-4 h-4" />
                    </button>
                    <button
                      @click="handleDelete(schedule.id)"
                      class="btn-icon-danger"
                      title="Xóa"
                    >
                      <TrashIcon class="w-4 h-4" />
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </template>
          <template v-else>
            <div class="p-6 text-center text-gray-500">
              <p>Chưa có lịch trình nào cho tour này.</p>
            </div>
          </template>
        </div>
      </div>
    </div>
    <div v-if="currentTab === 'form'">
      <form
        @submit.prevent="submitForm"
        class="bg-white p-8 rounded-xl shadow-lg border space-y-6"
      >
        <div class="space-y-5">
          <div class="bg-slate-50 p-4 rounded-lg border">
            <h4 class="font-semibold text-lg mb-3 text-gray-800">
              Thông tin chung
            </h4>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="label">Tour (*)</label>
                <select
                  v-model="form.tourId"
                  required
                  class="input"
                  :disabled="isCopyMode"
                >
                  <option
                    v-for="tour in allTours"
                    :key="tour.id"
                    :value="tour.id"
                  >
                    {{ tour.name }}
                  </option>
                </select>
              </div>
              <div>
                <label class="label">Ngày thứ (*)</label>
                <input
                  v-model.number="form.day"
                  type="number"
                  min="1"
                  required
                  class="input"
                />
              </div>
            </div>
            <div class="mt-4">
              <label class="label">Tiêu đề ngày (*)</label>
              <input
                v-model="form.title"
                type="text"
                required
                placeholder="VD: Khám phá trung tâm thành phố"
                class="input"
              />
            </div>
          </div>

          <div class="pt-4 space-y-3">
            <h4 class="font-semibold text-lg text-gray-800">
              Các hoạt động trong ngày
            </h4>
            <div
              v-for="(activity, index) in form.activities"
              :key="index"
              class="bg-white p-4 rounded-lg space-y-2 relative border-2 border-dashed"
            >
              <button
                @click.prevent="removeActivity(index)"
                class="absolute -top-3 -right-3 p-1 bg-red-500 text-white rounded-full shadow hover:bg-red-600"
                title="Xóa hoạt động này"
              >
                <TrashIcon class="w-4 h-4" />
              </button>
              <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <label class="label-sm">Thời gian</label>
                  <input
                    v-model="activity.time"
                    type="text"
                    placeholder="08:00"
                    class="input-sm"
                  />
                </div>
                <div class="col-span-2">
                  <label class="label-sm">Tên hoạt động (*)</label>
                  <input
                    v-model="activity.activity"
                    type="text"
                    required
                    class="input-sm"
                  />
                </div>
              </div>
              <div>
                <label class="label-sm">Icon</label>
                <select v-model="activity.icon" class="input-sm">
                  <option value="">-- Chọn icon --</option>
                  <option
                    v-for="icon in availableIcons"
                    :key="icon.value"
                    :value="icon.value"
                  >
                    {{ icon.name }}
                  </option>
                </select>
              </div>
              <div>
                <label class="label-sm">Mô tả</label>
                <textarea
                  v-model="activity.description"
                  rows="2"
                  class="input-sm"
                ></textarea>
              </div>
            </div>
            <button
              @click.prevent="addActivity"
              class="mt-2 text-sm font-semibold text-blue-600 hover:text-blue-800 flex items-center gap-1"
            >
              <PlusIcon class="w-4 h-4" /> Thêm hoạt động
            </button>
          </div>
        </div>
        <div class="flex justify-end space-x-4 pt-6 border-t">
          <button
            type="button"
            @click="switchToListView"
            class="btn btn-secondary"
          >
            Hủy
          </button>
          <button
            type="submit"
            class="btn btn-primary"
            :disabled="isSubmitting"
          >
            <span v-if="isSubmitting">Đang xử lý...</span>
            <span v-else>{{ editingSchedule ? "Cập nhật" : "Lưu" }}</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { PlusIcon, EditIcon, TrashIcon, CopyIcon } from "lucide-vue-next";
import tourAdminApi from "../api/tourAdminApi";
import scheduleApi from "../api/scheduleApi";

// === STATE MANAGEMENT ===
const currentTab = ref("list");
const toursWithSchedules = ref([]);
const allTours = ref([]);
const isLoading = ref(true);
const isSubmitting = ref(false);
const editingSchedule = ref(null);
const isCopyMode = ref(false);

const availableIcons = ref([
  { name: "Ăn uống", value: "fas fa-utensils" },
  { name: "Cà phê/Ăn sáng", value: "fas fa-coffee" },
  { name: "Tiệc BBQ", value: "fas fa-fire" },
  { name: "Thông tin/Họp mặt", value: "fas fa-info-circle" },
  { name: "Mua sắm", value: "fas fa-shopping-bag" },
  { name: "Khách sạn", value: "fas fa-hotel" },
  { name: "Hành lý", value: "fas fa-suitcase-rolling" },
  { name: "Máy bay", value: "fas fa-plane" },
  { name: "Xe buýt", value: "fas fa-bus" },
  { name: "Tàu hỏa", value: "fas fa-train" },
  { name: "Tàu thủy/Cano", value: "fas fa-ship" },
  { name: "Tham quan", value: "fas fa-camera-retro" },
  { name: "Di tích/Bảo tàng", value: "fas fa-landmark" },
  { name: "Đền/Chùa", value: "fas fa-gopuram" },
  { name: "Đi bộ/Leo núi", value: "fas fa-hiking" },
  { name: "Bãi biển", value: "fas fa-umbrella-beach" },
  { name: "Bơi lội", value: "fas fa-swimmer" },
  { name: "Lặn biển", value: "fas fa-water" },
  { name: "Bản đồ/Phương hướng", value: "fas fa-map-marked-alt" },
]);

const getInitialFormState = () => ({
  id: null,
  tourId: null,
  day: 1,
  title: "",
  activities: [],
});

const form = ref(getInitialFormState());

// === DATA FETCHING ===
async function fetchData() {
  isLoading.value = true;
  try {
    const tourList = (await tourAdminApi.getAllTours()) || [];
    tourList.forEach((tour) => {
      if (tour.tourSchedules) {
        tour.tourSchedules.sort((a, b) => a.day - b.day);
      }
    });
    toursWithSchedules.value = tourList;
    allTours.value = tourList.map((tour) => ({ id: tour.id, name: tour.name }));
  } catch (error) {
    console.error("Lỗi khi tải dữ liệu:", error);
    alert("Không thể tải dữ liệu tour và lịch trình.");
  } finally {
    isLoading.value = false;
  }
}
onMounted(fetchData);

// === COMPUTED & UI FUNCTIONS ===
const formTabLabel = computed(() => {
  if (editingSchedule.value) return "Chỉnh sửa Lịch trình";
  if (isCopyMode.value) return "Sao chép Lịch trình";
  return "Thêm Lịch trình mới";
});

function switchToListView() {
  currentTab.value = "list";
  editingSchedule.value = null;
  isCopyMode.value = false;
}

function openAddForm() {
  editingSchedule.value = null;
  isCopyMode.value = false;
  form.value = getInitialFormState();
  if (allTours.value.length > 0) {
    form.value.tourId = allTours.value[0].id;
  }
  currentTab.value = "form";
}

// SỬA HÀM NÀY
function editSchedule(schedule, tourId) {
  editingSchedule.value = schedule;
  isCopyMode.value = false;
  // Dùng deep copy để tránh thay đổi dữ liệu gốc
  const scheduleCopy = JSON.parse(JSON.stringify(schedule));
  form.value = {
    id: scheduleCopy.id,
    tourId: tourId,
    day: scheduleCopy.day,
    title: scheduleCopy.title,
    activities: scheduleCopy.activities || [],
  };
  currentTab.value = "form";
}

// SỬA HÀM NÀY
function copySchedule(schedule, tourId) {
  editingSchedule.value = null;
  isCopyMode.value = true;
  const scheduleCopy = JSON.parse(JSON.stringify(schedule));
  form.value = {
    id: null,
    tourId: tourId,
    day: scheduleCopy.day,
    title: scheduleCopy.title + " (Bản sao)",
    activities: (scheduleCopy.activities || []).map((act) => ({
      ...act,
      id: null,
    })),
  };
  currentTab.value = "form";
}


function addActivity() {
  form.value.activities.push({
    id: null,
    time: "08:00",
    activity: "",
    description: "",
    icon: "fas fa-utensils",
  });
}

function removeActivity(index) {
  form.value.activities.splice(index, 1);
}

// === CRUD OPERATIONS ===
async function handleDelete(scheduleId) {
  if (!window.confirm("Bạn có chắc chắn muốn xóa lịch trình này không?"))
    return;
  try {
    await scheduleApi.delete(scheduleId);
    alert("Xóa lịch trình thành công!");
    await fetchData();
  } catch (error) {
    console.error("Lỗi khi xóa:", error);
    const errorMessage =
      error.response?.data?.message || "Xóa thất bại. Vui lòng thử lại.";
    alert(errorMessage);
  }
}

// SỬA HÀM NÀY (chỉ sửa console.log để gỡ lỗi)
async function submitForm() {
  console.log("Dữ liệu gửi đi:", form.value); // Log toàn bộ object cho chính xác

  if (isSubmitting.value) return;
  isSubmitting.value = true;

  try {
    if (editingSchedule.value) {
      await scheduleApi.update(editingSchedule.value.id, form.value);
      alert("Cập nhật lịch trình thành công!");
    } else {
      await scheduleApi.create(form.value);
      alert("Thêm lịch trình thành công!");
    }
    switchToListView();
    await fetchData();
  } catch (error) {
    console.error("Lỗi khi gửi form:", error);
    const errorMessage =
      error.response?.data?.message ||
      "Thao tác thất bại. Vui lòng kiểm tra lại thông tin.";
    alert(errorMessage);
  } finally {
    isSubmitting.value = false;
  }
}
</script>

<style scoped>
.label {
  @apply block text-sm font-medium text-gray-700 mb-1;
}
.label-sm {
  @apply block text-xs font-medium text-gray-600 mb-1;
}
.input {
  @apply w-full border border-gray-300 rounded-lg px-3 py-2;
}
.input-sm {
  @apply w-full border border-gray-300 rounded-md px-2 py-1 text-sm;
}
.btn {
  @apply px-4 py-2 rounded-lg font-semibold transition-colors duration-200 flex items-center justify-center;
}
.btn-primary {
  @apply btn bg-blue-600 text-white hover:bg-blue-700;
}
.btn-secondary {
  @apply btn bg-gray-200 text-gray-800 hover:bg-gray-300;
}
.btn-icon {
  @apply p-2 text-gray-500 rounded-full;
}
.btn-icon:hover {
  @apply bg-gray-200 text-gray-800;
}
.btn-icon-danger:hover {
  @apply bg-red-100 text-red-600;
}
.tab-button {
  @apply whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm;
}
.tab-button-active {
  @apply border-blue-500 text-blue-600;
}
.tab-button-inactive {
  @apply border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300;
}
</style>
