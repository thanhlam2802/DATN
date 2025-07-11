<template>
  <div class="space-y-8">
    <div class="flex items-center justify-between">
      <h3 class="text-3xl font-bold text-gray-900">
        {{
          isViewMode
            ? "Chi tiết Tour"
            : isEditMode
            ? "Chỉnh sửa Tour"
            : "Thêm Tour Mới"
        }}
      </h3>
    </div>

    <form @submit.prevent="handleSubmit" class="space-y-8">
      <div
        class="bg-white rounded-2xl shadow-lg p-6 space-y-6 border border-gray-200"
      >
        <h4
          class="text-xl font-semibold text-gray-800 border-b pb-3 flex items-center gap-2"
        >
          <i class="fas fa-info-circle text-blue-500"></i>
          Thông tin cơ bản
        </h4>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div class="col-span-1 md:col-span-2 lg:col-span-1">
            <label class="block text-sm font-medium text-gray-700 mb-1"
              >Tên tour <span class="text-red-500">*</span></label
            >
            <input
              v-model="tourForm.ten_tour"
              type="text"
              required
              :disabled="isViewMode"
              class="form-input w-full"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1"
              >Giá (VND) <span class="text-red-500">*</span></label
            >
            <input
              v-model="tourForm.gia"
              type="number"
              required
              min="0"
              :disabled="isViewMode"
              class="form-input"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1"
              >Thời gian (Số ngày) <span class="text-red-500">*</span></label
            >
            <input
              v-model="tourForm.durationDays"
              type="number"
              required
              min="1"
              :disabled="isViewMode"
              class="form-input"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1"
              >Điểm khởi hành <span class="text-red-500">*</span></label
            >
            <input
              v-model="tourForm.diem_khoi_hanh"
              type="text"
              required
              :disabled="isViewMode"
              class="form-input"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1"
              >Điểm đến <span class="text-red-500">*</span></label
            >
            <input
              v-model="tourForm.diem_den"
              type="text"
              required
              :disabled="isViewMode"
              class="form-input"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1"
              >Trạng thái</label
            >
            <select
              v-model="tourForm.status"
              :disabled="isViewMode"
              class="form-input"
            >
              <option value="ACTIVE">Hoạt động</option>
              <option value="INACTIVE">Tạm dừng</option>
            </select>
          </div>
        </div>
      </div>

      <div
        class="bg-white rounded-2xl shadow-lg p-6 space-y-4 border border-gray-200"
      >
        <h4
          class="text-xl font-semibold text-gray-800 border-b pb-3 flex items-center gap-2"
        >
          <i class="fas fa-images text-teal-500"></i>
          Hình ảnh
        </h4>
        <input
          type="file"
          @change="handleImageUpload"
          multiple
          :disabled="isViewMode"
          accept="image/*"
          class="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
        />
        <div
          v-if="tourForm.hinh_anh && tourForm.hinh_anh.length > 0"
          class="grid grid-cols-2 sm:grid-cols-4 md:grid-cols-6 gap-4 mt-4"
        >
          <div
            v-for="(image, index) in tourForm.hinh_anh"
            :key="image.url || index"
            class="relative group"
          >
            <img
              :src="image.url"
              :alt="'Hình ảnh tour ' + (index + 1)"
              class="w-full h-32 object-cover rounded-lg shadow-md"
            />
            <button
              v-if="!isViewMode"
              @click.prevent="removeImage(index)"
              class="absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"
            >
              &times;
            </button>
          </div>
        </div>
      </div>

      <div
        class="bg-white rounded-2xl shadow-lg p-6 space-y-4 border border-gray-200"
      >
        <h4
          class="text-xl font-semibold text-gray-800 border-b pb-3 flex items-center gap-2"
        >
          <i class="fas fa-file-alt text-indigo-500"></i>
          Mô tả chi tiết
        </h4>
        <Ckeditor v-model="tourForm.mo_ta" :disabled="isViewMode" />
      </div>

      <div
        class="bg-white rounded-2xl shadow-lg p-6 space-y-6 border border-gray-200"
      >
        <div class="flex items-center justify-between border-b pb-3">
          <h4
            class="text-xl font-semibold text-gray-800 flex items-center gap-2"
          >
            <i class="fas fa-calendar-alt text-orange-500"></i>
            Lịch trình chi tiết
          </h4>
          <button
            v-if="!isViewMode"
            type="button"
            @click="addScheduleDay"
            class="btn-primary-sm"
          >
            + Thêm ngày
          </button>
        </div>
        <div
          v-for="(day, dayIndex) in tourForm.lich_trinh"
          :key="dayIndex"
          class="space-y-4 p-4 border rounded-lg bg-gray-50"
        >
          <div class="flex items-center justify-between">
            <h5 class="font-semibold text-lg text-gray-700">
              Ngày {{ day.dayNumber }}
            </h5>
            <button
              v-if="!isViewMode"
              type="button"
              @click="removeScheduleDay(dayIndex)"
              class="text-red-500 hover:text-red-700 font-bold"
            >
              &times; Xóa ngày
            </button>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1"
              >Chủ đề ngày</label
            >
            <input
              v-model="day.title"
              type="text"
              :disabled="isViewMode"
              class="form-input w-full"
              placeholder="VD: Khám phá Vịnh Hạ Long"
            />
          </div>
          <div class="space-y-3 pl-4 border-l-2 border-blue-200">
            <h6 class="font-medium text-gray-600">Các hoạt động:</h6>
            <div
              v-for="(activity, activityIndex) in day.activities"
              :key="activityIndex"
              class="p-3 border rounded-md bg-white space-y-2 relative"
            >
              <div class="absolute top-2 right-2">
                <button
                  v-if="!isViewMode"
                  type="button"
                  @click="removeActivity(dayIndex, activityIndex)"
                  class="text-xs text-red-500 font-bold"
                >
                  &times;
                </button>
              </div>
              <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <label class="block text-xs font-medium text-gray-600 mb-1"
                    >Icon</label
                  >
                  <select
                    v-model="activity.icon"
                    :disabled="isViewMode"
                    class="form-input-sm"
                  >
                    <option value="">Chọn icon</option>
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
                  <label class="block text-xs font-medium text-gray-600 mb-1"
                    >Thời gian</label
                  >
                  <input
                    v-model="activity.time"
                    type="text"
                    :disabled="isViewMode"
                    class="form-input-sm"
                    placeholder="VD: 08:00"
                  />
                </div>
                <div class="">
                  <label class="block text-xs font-medium text-gray-600 mb-1"
                    >Tên hoạt động</label
                  >
                  <input
                    v-model="activity.activity"
                    type="text"
                    :disabled="isViewMode"
                    class="form-input-sm"
                    placeholder="VD: Ăn sáng tại khách sạn"
                  />
                </div>
              </div>
              <div>
                <label class="block text-xs font-medium text-gray-600 mb-1 mt-5"
                  >Mô tả hoạt động</label
                >
                <textarea
                  v-model="activity.description"
                  :disabled="isViewMode"
                  rows="2"
                  class="form-input w-full h-[100px]"
                  placeholder="Mô tả chi tiết..."
                ></textarea>
              </div>
            </div>
            <button
              v-if="!isViewMode"
              type="button"
              @click="addActivity(dayIndex)"
              class="btn-secondary-sm"
            >
              + Thêm hoạt động
            </button>
          </div>
        </div>
      </div>

      <div
        class="bg-white rounded-2xl shadow-lg p-6 space-y-6 border border-gray-200"
      >
        <div class="flex items-center justify-between border-b pb-3">
          <h4
            class="text-xl font-semibold text-gray-800 flex items-center gap-2"
          >
            <i class="fas fa-plane-departure text-green-500"></i>
            Ngày khởi hành
          </h4>
          <button
            v-if="!isViewMode"
            type="button"
            @click="addDepartureDate"
            class="btn-primary-sm"
          >
            + Thêm ngày
          </button>
        </div>
        <div
          v-for="(date, index) in tourForm.ngay_khoi_hanh"
          :key="index"
          class="p-4 border rounded-lg bg-gray-50 space-y-4"
        >
          <div class="flex justify-end">
            <button
              v-if="!isViewMode"
              type="button"
              @click="removeDepartureDate(index)"
              class="text-red-500 hover:text-red-700 font-bold"
            >
              &times; Xóa
            </button>
          </div>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1"
                >Ngày đi</label
              >
              <input
                v-model="date.ngay"
                type="date"
                required
                :disabled="isViewMode"
                class="form-input"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1"
                >Giá người lớn</label
              >
              <input
                v-model="date.gia_nguoi_lon"
                type="number"
                required
                min="0"
                :disabled="isViewMode"
                class="form-input"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1"
                >Giá trẻ em</label
              >
              <input
                v-model="date.gia_tre_em"
                type="number"
                required
                min="0"
                :disabled="isViewMode"
                class="form-input"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1"
                >Giảm giá (%)</label
              >
              <input
                v-model="date.giam_gia"
                type="number"
                min="0"
                max="100"
                :disabled="isViewMode"
                class="form-input"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1"
                >Số chỗ</label
              >
              <input
                v-model="date.so_cho"
                type="number"
                required
                min="1"
                :disabled="isViewMode"
                class="form-input"
              />
            </div>
            <div v-if="isViewMode || isEditMode">
              <label class="block text-sm font-medium text-gray-700 mb-1"
                >Chỗ đã đặt</label
              >
              <input
                :value="date.so_cho_da_dat"
                type="number"
                disabled
                class="form-input bg-gray-100"
              />
            </div>
          </div>
        </div>
      </div>

      <div
        class="bg-white rounded-2xl shadow-lg p-6 space-y-4 border border-gray-200"
      >
        <h4
          class="text-xl font-semibold text-gray-800 border-b pb-3 flex items-center gap-2"
        >
          <i class="fas fa-tags text-purple-500"></i>
          Thẻ (Tags)
        </h4>
        <div
          class="flex flex-wrap gap-2 items-center p-2 border border-gray-300 rounded-lg min-h-[42px]"
        >
          <span
            v-for="(tag, index) in tourForm.tags"
            :key="tag.id || tag.name"
            class="flex items-center gap-2 bg-blue-100 text-blue-800 text-sm font-medium pl-3 pr-2 py-1 rounded-full"
          >
            {{ tag.name }}
            <button
              v-if="!isViewMode"
              @click.prevent="removeTag(index)"
              class="text-blue-600 hover:text-blue-900 font-bold"
            >
              &times;
            </button>
          </span>
          <div v-if="!isViewMode" class="relative flex-grow">
            <input
              type="text"
              v-model="tagSearch"
              @focus="showTagSuggestions = true"
              @blur="() => setTimeout(() => (showTagSuggestions = false), 200)"
              @keydown.enter.prevent="addNewTag"
              placeholder="Chọn hoặc thêm tag mới..."
              class="outline-none bg-transparent"
            />
            <div
              v-if="showTagSuggestions && tagSuggestions.length > 0"
              class="absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded-lg shadow-lg max-h-40 overflow-y-auto"
            >
              <ul>
                <li
                  v-for="suggestion in tagSuggestions"
                  :key="suggestion.id"
                  @click="selectTag(suggestion)"
                  class="px-4 py-2 hover:bg-gray-100 cursor-pointer"
                >
                  {{ suggestion.name }}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <div class="flex justify-end gap-4 pt-4 border-t" v-if="!isViewMode">
        <button type="button" @click="handleCancel" class="btn-secondary">
          Hủy
        </button>
        <button type="submit" class="btn-primary">
          {{ isEditMode ? "Cập nhật Tour" : "Thêm Tour Mới" }}
        </button>
      </div>
    </form>
  </div>
</template>
<script setup>
import { ref, watch, computed, onMounted } from "vue";
import tagApi from "../api/tagApi.js";
import Ckeditor from "../components/Ckeditor.vue";

// --- PROPS & EMITS ---
const props = defineProps({
  tourData: { type: Object, default: () => null },
  mode: { type: String, default: "add" },
});
const emit = defineEmits(["cancel", "submit"]);

// --- STATE ---
const isViewMode = computed(() => props.mode === "view");
const isEditMode = computed(() => props.mode === "edit");

const availableIcons = ref([
  // Nhóm chung & Ăn uống
  { name: "Ăn uống", value: "fas fa-utensils" },
  { name: "Cà phê/Ăn sáng", value: "fas fa-coffee" },
  { name: "Tiệc BBQ", value: "fas fa-fire" },
  { name: "Thông tin/Họp mặt", value: "fas fa-info-circle" },
  { name: "Mua sắm", value: "fas fa-shopping-bag" },

  // Nhóm di chuyển
  { name: "Khách sạn", value: "fas fa-hotel" },
  { name: "Hành lý", value: "fas fa-suitcase-rolling" },
  { name: "Máy bay", value: "fas fa-plane" },
  { name: "Xe buýt", value: "fas fa-bus" },
  { name: "Tàu hỏa", value: "fas fa-train" },
  { name: "Tàu thủy/Cano", value: "fas fa-ship" },

  // Nhóm hoạt động & tham quan
  { name: "Tham quan", value: "fas fa-camera-retro" },
  { name: "Di tích/Bảo tàng", value: "fas fa-landmark" },
  { name: "Đền/Chùa", value: "fas fa-gopuram" },
  { name: "Đi bộ/Leo núi", value: "fas fa-hiking" },
  { name: "Bãi biển", value: "fas fa-umbrella-beach" },
  { name: "Bơi lội", value: "fas fa-swimmer" },
  { name: "Lặn biển", value: "fas fa-water" },
  { name: "Bản đồ/Phương hướng", value: "fas fa-map-marked-alt" },
]);

const allAvailableTags = ref([]);

const tagSearch = ref("");
const showTagSuggestions = ref(false);

onMounted(async () => {
  try {
    const tagsFromApi = await tagApi.getAllTags();
    allAvailableTags.value = tagsFromApi;
    console.log(
      "--- [AddTourTab] Đã tải danh sách tags từ API thành công: ---",
      tagsFromApi
    );
  } catch (error) {
    console.error("--- [AddTourTab] Lỗi khi tải danh sách tags: ---", error);
  }
});

const getInitialFormState = () => ({
  id: null,
  ten_tour: "",
  mo_ta: "",
  gia: 0,
  durationDays: 1,
  diem_khoi_hanh: "",
  diem_den: "",
  status: "ACTIVE",
  hinh_anh: [],
  lich_trinh: [],
  ngay_khoi_hanh: [],
  tags: [],
});

const tourForm = ref(getInitialFormState());

// --- WATCHER ---
watch(
  () => props.tourData,
  (newVal) => {
    // LOG: Kiểm tra dữ liệu được truyền vào từ component cha
    if (newVal) {
      console.log(
        `--- [AddTourTab] Nhận được dữ liệu cho chế độ '${props.mode}'. Đang điền vào form: ---`,
        newVal
      );
      tourForm.value = { ...getInitialFormState(), ...newVal };
    } else {
      console.log(
        "--- [AddTourTab] Không có dữ liệu đầu vào. Đang reset form về trạng thái thêm mới. ---"
      );
      tourForm.value = getInitialFormState();
    }
  },
  { immediate: true, deep: true }
);

// --- COMPUTED ---
const tagSuggestions = computed(() => {
  if (!tagSearch.value) return [];
  const selectedTagNames = new Set(tourForm.value.tags.map((t) => t.name));
  return allAvailableTags.value.filter(
    (tag) =>
      !selectedTagNames.has(tag.name) &&
      tag.name.toLowerCase().includes(tagSearch.value.toLowerCase())
  );
});

// --- METHODS ---
// Lịch trình
const addScheduleDay = () =>
  tourForm.value.lich_trinh.push({
    dayNumber: tourForm.value.lich_trinh.length + 1,
    title: "",
    activities: [],
  });
const removeScheduleDay = (dayIndex) =>
  tourForm.value.lich_trinh.splice(dayIndex, 1);
const addActivity = (dayIndex) =>
  tourForm.value.lich_trinh[dayIndex].activities.push({
    time: "",
    activity: "",
    description: "",
    icon: "",
  });
const removeActivity = (dayIndex, activityIndex) =>
  tourForm.value.lich_trinh[dayIndex].activities.splice(activityIndex, 1);

// Ngày khởi hành
const addDepartureDate = () =>
  tourForm.value.ngay_khoi_hanh.push({
    ngay: "",
    gia_nguoi_lon: 0,
    gia_tre_em: 0,
    giam_gia: 0,
    so_cho: 1,
  });
const removeDepartureDate = (index) =>
  tourForm.value.ngay_khoi_hanh.splice(index, 1);

// Hình ảnh
const handleImageUpload = (event) => {
  const files = Array.from(event.target.files);

  console.log("--- [AddTourTab] Người dùng đã chọn các tệp ảnh: ---", files);

  const newImages = files.map((file) => ({
    url: URL.createObjectURL(file),
    file: file,
  }));
  tourForm.value.hinh_anh.push(...newImages);
};
const removeImage = (index) => tourForm.value.hinh_anh.splice(index, 1);

// Tags
const selectTag = (tag) => {
  tourForm.value.tags.push(tag);
  tagSearch.value = "";
  showTagSuggestions.value = false;
};
const addNewTag = () => {
  if (tagSearch.value.trim() !== "") {
    const newTagName = tagSearch.value.trim();
    const existingTag = tourForm.value.tags.find(
      (t) => t.name.toLowerCase() === newTagName.toLowerCase()
    );
    const availableTag = allAvailableTags.value.find(
      (t) => t.name.toLowerCase() === newTagName.toLowerCase()
    );

    if (!existingTag) {
      if (availableTag) {
        selectTag(availableTag);
      } else {
        tourForm.value.tags.push({ name: newTagName });
        tagSearch.value = "";
      }
    } else {
      tagSearch.value = "";
    }
  }
};
const removeTag = (index) => tourForm.value.tags.splice(index, 1);

// Form Actions
const handleSubmit = () => {
  // LOG: Kiểm tra trạng thái cuối cùng của dữ liệu form trước khi gửi đi
  console.log(
    "--- [AddTourTab] Form được BẤM SUBMIT. Dữ liệu chuẩn bị gửi đi: ---",
    JSON.parse(JSON.stringify(tourForm.value))
  );
  emit("submit", tourForm.value);
};

const handleCancel = () => {
  console.log("--- [AddTourTab] Người dùng bấm nút Hủy. ---");
  emit("cancel");
};
</script>

<style scoped>
.form-input {
  @apply w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100 disabled:text-gray-500 transition-colors duration-200;
}
.form-input-sm {
  @apply w-full border border-gray-300 rounded-md px-2 py-1 text-sm focus:ring-1 focus:ring-blue-500 disabled:bg-gray-100 disabled:text-gray-500 transition-colors duration-200;
}
.btn-primary {
  @apply px-6 py-2 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all duration-200;
}
.btn-primary-sm {
  @apply px-3 py-1 bg-blue-500 text-white font-semibold rounded-md hover:bg-blue-600 text-sm;
}
.btn-secondary {
  @apply px-6 py-2 bg-gray-200 text-gray-800 font-semibold rounded-lg hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-400 transition-all duration-200;
}
.btn-secondary-sm {
  @apply px-3 py-1 bg-gray-200 text-gray-700 font-semibold rounded-md hover:bg-gray-300 text-sm;
}
</style>
