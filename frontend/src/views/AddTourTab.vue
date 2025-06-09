<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h3 class="text-2xl font-semibold text-gray-800">
        {{
          isViewMode
            ? "Chi tiết Tour"
            : isEditMode
            ? "Chỉnh sửa Tour"
            : "Thêm Tour Mới"
        }}
      </h3>
      <div class="flex gap-4" v-if="isViewMode">
        <button
          @click="$emit('submit', { ...tourForm, mode: 'edit' })"
          class="px-4 py-2 text-white bg-blue-600 rounded-lg hover:bg-blue-700"
        >
          Chỉnh sửa
        </button>
        <button
          @click="handleCancel"
          class="px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200"
        >
          Quay lại
        </button>
      </div>
    </div>

    <form @submit.prevent="handleSubmit" class="space-y-6">
      <!-- Thông tin cơ bản -->
      <div class="bg-white rounded-lg shadow p-6 space-y-4">
        <h4 class="text-lg font-medium text-gray-700">Thông tin cơ bản</h4>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Tên tour <span class="text-red-500">*</span>
            </label>
            <input
              v-model="tourForm.ten_tour"
              type="text"
              required
              :disabled="isViewMode"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-500"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Giá <span class="text-red-500">*</span>
            </label>
            <input
              v-model="tourForm.gia"
              type="number"
              required
              min="0"
              :disabled="isViewMode"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-500"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Thời gian <span class="text-red-500">*</span>
            </label>
            <input
              v-model="tourForm.thoi_gian"
              type="text"
              required
              placeholder="VD: 3 ngày 2 đêm"
              :disabled="isViewMode"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-500"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Điểm khởi hành <span class="text-red-500">*</span>
            </label>
            <input
              v-model="tourForm.diem_khoi_hanh"
              type="text"
              required
              :disabled="isViewMode"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-500"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Điểm đến <span class="text-red-500">*</span>
            </label>
            <input
              v-model="tourForm.diem_den"
              type="text"
              required
              :disabled="isViewMode"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-500"
            />
          </div>

          <div class="col-span-full">
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Hình ảnh <span class="text-red-500">*</span>
            </label>
            <div class="space-y-4">
              <div
                class="border-2 border-dashed border-gray-300 rounded-lg p-4 text-center hover:border-blue-500 transition-colors"
                :class="{ 'border-red-500': imageError }"
              >
                <input
                  type="file"
                  @change="handleImageUpload"
                  :required="!hasImages"
                  accept="image/*"
                  multiple
                  :disabled="isViewMode"
                  class="hidden"
                  ref="fileInput"
                />

                <div class="space-y-2 cursor-pointer" @click="triggerFileInput">
                  <i class="fas fa-cloud-upload-alt text-3xl text-gray-400"></i>
                  <div class="text-sm text-gray-600">
                    <span class="text-blue-500 hover:text-blue-700"
                      >Tải lên hình ảnh</span
                    >
                    hoặc kéo thả vào đây
                  </div>
                  <div class="text-xs text-gray-500">
                    PNG, JPG, GIF tối đa 5MB mỗi ảnh
                  </div>
                </div>
              </div>

              <div
                v-if="imageError"
                class="text-red-500 text-sm p-2 bg-red-50 rounded-lg"
              >
                <i class="fas fa-exclamation-circle mr-1"></i>
                {{ imageError }}
              </div>

              <div
                v-if="hasImages"
                class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4"
              >
                <div
                  v-for="(image, index) in existingImages"
                  :key="'existing-' + index"
                  class="relative group aspect-square bg-gray-100 rounded-lg overflow-hidden"
                >
                  <img
                    :src="image.url"
                    class="w-full h-full object-cover"
                    :class="{ 'opacity-75': isViewMode }"
                    @error="handleImageError($event, index, 'existing')"
                  />
                  <div
                    v-if="!isViewMode"
                    class="absolute inset-0 bg-black/0 group-hover:bg-black/40 transition-all duration-300"
                  >
                    <button
                      @click.prevent="removeExistingImage(index)"
                      type="button"
                      class="absolute top-2 right-2 bg-red-500 text-white rounded-full p-2 opacity-0 group-hover:opacity-100 transition-opacity hover:bg-red-600"
                    >
                      <i class="fas fa-trash-alt"></i>
                    </button>
                  </div>
                </div>

                <div
                  v-for="(preview, index) in imagePreviewUrls"
                  :key="'new-' + index"
                  class="relative group aspect-square bg-gray-100 rounded-lg overflow-hidden"
                >
                  <img
                    :src="preview.url"
                    class="w-full h-full object-cover"
                    @error="handleImageError($event, index, 'new')"
                  />
                  <div
                    v-if="!isViewMode"
                    class="absolute inset-0 bg-black/0 group-hover:bg-black/40 transition-all duration-300"
                  >
                    <button
                      @click.prevent="removeNewImage(index)"
                      type="button"
                      class="absolute top-2 right-2 bg-red-500 text-white rounded-full p-2 opacity-0 group-hover:opacity-100 transition-opacity hover:bg-red-600"
                    >
                      <i class="fas fa-trash-alt"></i>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            Mô tả <span class="text-red-500">*</span>
          </label>
          <Ckeditor v-model="tourForm.mo_ta" :disabled="isViewMode" />
        </div>
      </div>

      <!-- Lịch trình -->
      <div class="bg-white rounded-lg shadow p-6 space-y-4">
        <div class="flex items-center justify-between">
          <h4 class="text-lg font-medium text-gray-700">Lịch trình</h4>
          <button
            v-if="!isViewMode"
            type="button"
            @click="addScheduleDay"
            class="text-blue-600 hover:text-blue-800 flex items-center gap-1"
          >
            <i class="fas fa-plus"></i>
            Thêm ngày
          </button>
        </div>

        <div
          v-for="(day, index) in tourForm.lich_trinh"
          :key="index"
          class="space-y-4 border-b pb-4"
        >
          <div class="flex items-center justify-between">
            <h5 class="font-medium">Ngày {{ index + 1 }}</h5>
            <button
              v-if="!isViewMode"
              type="button"
              @click="removeScheduleDay(index)"
              class="text-red-600 hover:text-red-800"
            >
              <i class="fas fa-trash"></i>
            </button>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Hoạt động
            </label>
            <textarea
              v-model="day.hoat_dong"
              rows="3"
              required
              :disabled="isViewMode"
              class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-500"
            ></textarea>
          </div>
        </div>
      </div>

      <!-- Ngày khởi hành -->
      <div class="bg-white rounded-lg shadow p-6 space-y-4">
        <div class="flex items-center justify-between">
          <h4 class="text-lg font-medium text-gray-700">Ngày khởi hành</h4>
          <button
            v-if="!isViewMode"
            type="button"
            @click="addDepartureDate"
            class="text-blue-600 hover:text-blue-800 flex items-center gap-1"
          >
            <i class="fas fa-plus"></i>
            Thêm ngày
          </button>
        </div>

        <div
          v-for="(date, index) in tourForm.ngay_khoi_hanh"
          :key="index"
          class="space-y-4 border-b pb-4"
        >
          <div class="flex items-center justify-between">
            <h5 class="font-medium">Lịch {{ index + 1 }}</h5>
            <button
              v-if="!isViewMode"
              type="button"
              @click="removeDepartureDate(index)"
              class="text-red-600 hover:text-red-800"
            >
              <i class="fas fa-trash"></i>
            </button>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Ngày khởi hành
              </label>
              <input
                v-model="date.ngay"
                type="date"
                required
                :disabled="isViewMode"
                class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Giá người lớn
              </label>
              <input
                v-model="date.gia_nguoi_lon"
                type="number"
                required
                min="0"
                :disabled="isViewMode"
                class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Giá trẻ em
              </label>
              <input
                v-model="date.gia_tre_em"
                type="number"
                required
                min="0"
                :disabled="isViewMode"
                class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Giảm giá
              </label>
              <input
                v-model="date.giam_gia"
                type="number"
                min="0"
                :disabled="isViewMode"
                class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Số chỗ
              </label>
              <input
                v-model="date.so_cho"
                type="number"
                required
                min="1"
                :disabled="isViewMode"
                class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-500"
              />
            </div>

            <div v-if="isViewMode">
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Số chỗ đã đặt
              </label>
              <input
                v-model="date.so_cho_da_dat"
                type="number"
                disabled
                class="w-full border border-gray-300 rounded-lg px-3 py-2 bg-gray-50 text-gray-500"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- Buttons -->
      <div class="flex justify-end gap-4" v-if="!isViewMode">
        <button
          type="button"
          @click="handleCancel"
          class="px-6 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
        >
          Hủy
        </button>
        <button
          type="submit"
          class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          {{ isEditMode ? "Cập nhật" : "Thêm tour" }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, watch, computed, onUnmounted, onMounted } from "vue";
import Ckeditor from "../components/Ckeditor.vue";
const props = defineProps({
  tourData: {
    type: Object,
    default: () => null,
  },
  mode: {
    type: String,
    default: "add",
  },
});

const emit = defineEmits(["cancel", "submit"]);

const isViewMode = computed(() => props.mode === "view");
const isEditMode = computed(() => props.mode === "edit");

const tourForm = ref({
  ten_tour: "",
  mo_ta: "",
  gia: "",
  thoi_gian: "",
  diem_khoi_hanh: "",
  diem_den: "",
  hinh_anh: [],
  lich_trinh: [
    {
      hoat_dong: "",
    },
  ],
  ngay_khoi_hanh: [
    {
      ngay: "",
      gia_nguoi_lon: "",
      gia_tre_em: "",
      giam_gia: "",
      so_cho: "",
      so_cho_da_dat: 0,
    },
  ],
});

const fileInput = ref(null);
const imagePreviewUrls = ref([]);
const imageError = ref("");
const existingImages = ref([]);
const dragCounter = ref(0);

// Computed property to check if there are any images
const hasImages = computed(() => {
  return imagePreviewUrls.value.length > 0 || existingImages.value.length > 0;
});

// Watch for changes in tourData prop
watch(
  () => props.tourData,
  (newVal) => {
    if (newVal) {
      tourForm.value = { ...newVal };
      // Handle existing images
      if (Array.isArray(newVal.hinh_anh)) {
        existingImages.value = newVal.hinh_anh
          .map((img) => {
            if (typeof img === "string") {
              return { url: img, file: null };
            } else if (img instanceof File) {
              return { url: URL.createObjectURL(img), file: img };
            }
            return null;
          })
          .filter(Boolean);
      }
    }
  },
  { immediate: true }
);

// Function to trigger file input click
const triggerFileInput = () => {
  if (!isViewMode.value) {
    fileInput.value.click();
  }
};

// Validate single file
const validateFile = (file) => {
  // Check file type
  if (!file.type.startsWith("image/")) {
    return "Chỉ chấp nhận file hình ảnh (PNG, JPG, GIF)";
  }

  // Check file size (5MB)
  const maxSize = 5 * 1024 * 1024;
  if (file.size > maxSize) {
    return `File "${file.name}" vượt quá kích thước cho phép (5MB)`;
  }

  return null;
};

// Handle file upload
const handleImageUpload = async (event) => {
  const files = Array.from(event.target.files || event.dataTransfer.files);
  imageError.value = "";

  // Validate each file
  for (const file of files) {
    const error = validateFile(file);
    if (error) {
      imageError.value = error;
      if (event.target) event.target.value = "";
      return;
    }
  }

  // Create preview URLs and add to preview array
  const newPreviews = files.map((file) => ({
    url: URL.createObjectURL(file),
    file: file,
  }));

  imagePreviewUrls.value = [...imagePreviewUrls.value, ...newPreviews];

  // Update form data with new files
  tourForm.value.hinh_anh = [
    ...tourForm.value.hinh_anh.filter((img) => !(img instanceof File)),
    ...files,
  ];

  // Clear input value to allow selecting the same file again
  if (event.target) event.target.value = "";
};

// Handle image load error
const handleImageError = (event, index, type) => {
  console.error(`Failed to load image: ${type} - ${index}`);
  event.target.src = "https://placehold.co/400x400?text=Error+Loading+Image";
};

// Remove new image
const removeNewImage = (index) => {
  const preview = imagePreviewUrls.value[index];
  if (preview && preview.url) {
    URL.revokeObjectURL(preview.url);
  }
  imagePreviewUrls.value.splice(index, 1);

  // Remove from form data
  const fileImages = tourForm.value.hinh_anh.filter(
    (img) => img instanceof File
  );
  fileImages.splice(index, 1);
  tourForm.value.hinh_anh = [
    ...tourForm.value.hinh_anh.filter((img) => !(img instanceof File)),
    ...fileImages,
  ];
};

// Remove existing image
const removeExistingImage = (index) => {
  // Remove from existing images
  const removedImage = existingImages.value[index];
  if (removedImage.url.startsWith("blob:")) {
    URL.revokeObjectURL(removedImage.url);
  }
  existingImages.value.splice(index, 1);

  // Remove from form data
  tourForm.value.hinh_anh = tourForm.value.hinh_anh.filter(
    (_, i) => i !== index
  );
};

// Drag and drop handlers
const handleDragEnter = (e) => {
  e.preventDefault();
  dragCounter.value++;
  if (e.currentTarget) {
    e.currentTarget.classList.add("border-blue-500");
  }
};

const handleDragLeave = (e) => {
  e.preventDefault();
  dragCounter.value--;
  if (dragCounter.value === 0 && e.currentTarget) {
    e.currentTarget.classList.remove("border-blue-500");
  }
};

const handleDragOver = (e) => {
  e.preventDefault();
};

const handleDrop = (e) => {
  e.preventDefault();
  dragCounter.value = 0;
  if (e.currentTarget) {
    e.currentTarget.classList.remove("border-blue-500");
  }
  handleImageUpload(e);
};

// Set up drag and drop listeners
onMounted(() => {
  const dropZone = document.querySelector(".border-dashed");
  if (dropZone) {
    dropZone.addEventListener("dragenter", handleDragEnter);
    dropZone.addEventListener("dragleave", handleDragLeave);
    dropZone.addEventListener("dragover", handleDragOver);
    dropZone.addEventListener("drop", handleDrop);
  }
});

// Clean up
onUnmounted(() => {
  const dropZone = document.querySelector(".border-dashed");
  if (dropZone) {
    dropZone.removeEventListener("dragenter", handleDragEnter);
    dropZone.removeEventListener("dragleave", handleDragLeave);
    dropZone.removeEventListener("dragover", handleDragOver);
    dropZone.removeEventListener("drop", handleDrop);
  }

  // Clean up preview URLs
  imagePreviewUrls.value.forEach((preview) => {
    if (preview.url) {
      URL.revokeObjectURL(preview.url);
    }
  });

  // Clean up existing image URLs
  existingImages.value.forEach((img) => {
    if (img.url && img.url.startsWith("blob:")) {
      URL.revokeObjectURL(img.url);
    }
  });
});

const addScheduleDay = () => {
  tourForm.value.lich_trinh.push({
    hoat_dong: "",
  });
};

const removeScheduleDay = (index) => {
  tourForm.value.lich_trinh.splice(index, 1);
};

const addDepartureDate = () => {
  tourForm.value.ngay_khoi_hanh.push({
    ngay: "",
    gia_nguoi_lon: "",
    gia_tre_em: "",
    giam_gia: "",
    so_cho: "",
    so_cho_da_dat: 0,
  });
};

const removeDepartureDate = (index) => {
  tourForm.value.ngay_khoi_hanh.splice(index, 1);
};

const handleSubmit = () => {
  // Validate required fields
  if (
    !tourForm.value.ten_tour ||
    !tourForm.value.mo_ta ||
    !tourForm.value.gia ||
    !tourForm.value.thoi_gian ||
    !tourForm.value.diem_khoi_hanh ||
    !tourForm.value.diem_den
  ) {
    alert("Vui lòng điền đầy đủ thông tin bắt buộc");
    return;
  }

  // Validate at least one schedule day
  if (tourForm.value.lich_trinh.length === 0) {
    alert("Vui lòng thêm ít nhất một ngày trong lịch trình");
    return;
  }

  // Validate at least one departure date
  if (tourForm.value.ngay_khoi_hanh.length === 0) {
    alert("Vui lòng thêm ít nhất một ngày khởi hành");
    return;
  }

  // Format the data
  const formattedData = {
    ...tourForm.value,
    gia: Number(tourForm.value.gia),
    hinh_anh: tourForm.value.hinh_anh.map((img) => {
      if (img instanceof File) {
        return URL.createObjectURL(img);
      }
      return img;
    }),
    ngay_khoi_hanh: tourForm.value.ngay_khoi_hanh.map((date) => ({
      ...date,
      gia_nguoi_lon: Number(date.gia_nguoi_lon),
      gia_tre_em: Number(date.gia_tre_em),
      giam_gia: Number(date.giam_gia || 0),
      so_cho: Number(date.so_cho),
      so_cho_da_dat: Number(date.so_cho_da_dat || 0),
    })),
  };

  emit("submit", formattedData);
};

const handleCancel = () => {
  emit("cancel");
};
</script>

<style scoped>
.aspect-square {
  aspect-ratio: 1 / 1;
}

/* Drag and drop animation */
.border-dashed {
  transition: all 0.3s ease;
}

.border-dashed:hover {
  border-color: #3b82f6;
}

/* Image preview container */
.image-preview {
  position: relative;
  width: 100%;
  height: 100%;
  background-color: #f3f4f6;
  overflow: hidden;
}

/* Image preview */
.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.image-preview:hover img {
  transform: scale(1.05);
}
</style>
