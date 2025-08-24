<template>
  <form @submit.prevent="handleSearch" class="p-4 rounded-lg">
    <div class="flex flex-col lg:flex-row items-center gap-2 pt-15 pb-2">
      <div class="w-full lg:flex-grow relative" ref="searchContainer">
        <div class="relative w-full h-full group">
          <label
            class="absolute -top-2.5 left-3 px-1 bg-white text-gray-500 text-xs font-semibold rounded-md z-10"
          >
            Bạn muốn đi đâu?
          </label>
          <div
            class="flex items-center bg-white rounded-lg border border-gray-300 overflow-hidden shadow-sm h-16"
          >
            <i class="fas fa-map-marked-alt text-blue-500 text-xl px-4"></i>
            <div class="py-2 flex-1">
              <input
                v-model="destination"
                @focus="showSuggestions = true"
                type="text"
                id="tour-destination"
                placeholder="Nhập điểm đến hoặc tên tour"
                class="w-full bg-transparent font-bold text-lg focus:outline-none text-gray-800"
                autocomplete="off"
              />
            </div>
          </div>
        </div>
        <ul
          v-if="showSuggestions && suggestions.length > 0"
          class="absolute top-full mt-1 z-30 w-full bg-white border border-gray-300 rounded-lg shadow-lg max-h-60 overflow-y-auto"
        >
          <li
            v-for="(suggestion, index) in suggestions"
            :key="index"
            @click="selectSuggestion(suggestion)"
            class="px-4 py-3 hover:bg-blue-100 cursor-pointer flex items-center gap-3"
          >
            <i
              :class="
                suggestion.type === 'Destination'
                  ? 'fas fa-map-marker-alt text-gray-400'
                  : 'fas fa-flag text-gray-400'
              "
            ></i>
            <div>
              <p class="font-semibold text-gray-800">{{ suggestion.name }}</p>
              <p class="text-xs text-gray-500">
                {{ suggestion.type === "Destination" ? "Điểm đến" : "Tour" }}
              </p>
            </div>
          </li>
        </ul>
      </div>

      <div
        class="w-full lg:w-auto lg:min-w-[200px] flex items-center justify-center pt-0"
      >
        <button
          type="submit"
          :disabled="isSearching"
          class="w-full h-16 bg-orange-500 hover:bg-orange-600 transition-colors text-white font-bold text-xl rounded-lg shadow-lg flex items-center justify-center gap-3"
          :class="{
            'bg-gray-400 hover:bg-gray-400 cursor-not-allowed': isSearching,
          }"
        >
          <div
            v-if="isSearching"
            class="flex items-center justify-center space-x-1"
          >
            <span
              class="w-2.5 h-2.5 bg-white rounded-full animate-bounce [animation-delay:-0.3s]"
            ></span>
            <span
              class="w-2.5 h-2.5 bg-white rounded-full animate-bounce [animation-delay:-0.15s]"
            ></span>
            <span
              class="w-2.5 h-2.5 bg-white rounded-full animate-bounce"
            ></span>
          </div>
          <div v-else class="flex items-center justify-center gap-3">
            <i class="fas fa-search"></i>
            <span>Tìm kiếm</span>
          </div>
        </button>
      </div>
    </div>
  </form>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { getTourSuggestions } from "@/api/tourApi";

const router = useRouter();
const destination = ref("");
const isSearching = ref(false);

// State cho phần gợi ý
const suggestions = ref([]);
const showSuggestions = ref(false);
const debounceTimer = ref(null);
const searchContainer = ref(null);

watch(destination, (newValue) => {
  clearTimeout(debounceTimer.value);
  if (newValue.trim().length > 1) {
    debounceTimer.value = setTimeout(async () => {
      try {
        const response = await getTourSuggestions(newValue);
        suggestions.value = response.data;
        showSuggestions.value = true;
      } catch (error) {
        console.error("Lỗi khi lấy gợi ý:", error);
        suggestions.value = [];
      }
    }, 300);
  } else {
    suggestions.value = [];
  }
});

// Hàm khi chọn một gợi ý
const selectSuggestion = (suggestion) => {
  destination.value = suggestion.name;
  showSuggestions.value = false;
  suggestions.value = [];
};

const handleSearch = async () => {
  showSuggestions.value = false;
  if (!destination.value.trim() || isSearching.value) {
    return;
  }
  isSearching.value = true;
  try {
    await router.push({
      name: "Tour",
      query: {
        destination: destination.value.trim(),
      },
    });
  } catch (error) {
    console.error("Lỗi điều hướng:", error);
  } finally {
    isSearching.value = false;
  }
};

// Xử lý click ra ngoài để ẩn dropdown
const handleClickOutside = (event) => {
  if (searchContainer.value && !searchContainer.value.contains(event.target)) {
    showSuggestions.value = false;
  }
};

onMounted(() => {
  document.addEventListener("click", handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
});
</script>
