<template>
  <aside class="w-full lg:w-72 bg-white rounded-xl p-1 space-y-4">

    <div class="border border-gray-200 rounded-lg">
      <button aria-controls="price-range-slider" aria-expanded="true"
        class="w-full flex justify-between items-center px-4 py-3 bg-indigo-50 hover:bg-indigo-100 transition-colors text-indigo-800 font-medium text-base rounded-t-lg">
        <span>Khoảng giá</span>
      </button>

      <div id="price-range-slider" class="px-5 pt-6 pb-4 space-y-4">
        <Slider v-model="priceRange" :min="MIN_PRICE" :max="MAX_PRICE" :step="PRICE_STEP" :tooltips="false"
          :lazy="false" class="price-slider" />

        <div class="flex items-center justify-between gap-3 text-sm">
          <div class="w-1/2">
            <label for="min-price" class="block text-xs text-gray-500 mb-1">Tối thiểu</label>
            <div class="relative">
              <input id="min-price" type="text" v-model="minPriceFormatted"
                class="w-full border-gray-300 rounded-md shadow-sm text-sm focus:ring-indigo-500 focus:border-indigo-500" />
            </div>
          </div>
          <div class="w-1/2">
            <label for="max-price" class="block text-xs text-gray-500 mb-1">Tối đa</label>
            <div class="relative">
              <input id="max-price" type="text" v-model="maxPriceFormatted"
                class="w-full border-gray-300 rounded-md shadow-sm text-sm focus:ring-indigo-500 focus:border-indigo-500" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="border border-gray-200 rounded-lg">
      <button aria-controls="star-rating-filter" aria-expanded="true"
        class="w-full flex justify-between items-center px-4 py-3 bg-indigo-50 hover:bg-indigo-100 transition-colors text-indigo-800 font-medium text-base rounded-t-lg">
        <span>Hạng sao</span>
      </button>

      <div id="star-rating-filter" class="px-4 pt-3 pb-2 space-y-2">
        <label
          class="flex items-center space-x-2 text-gray-700 text-sm font-medium cursor-pointer hover:text-indigo-600 transition">
          <input type="radio" name="star-rating" :value="0" v-model="starRatingModel"
            class="form-radio text-indigo-600 border-gray-300 focus:ring-indigo-500" />
          <span>Tất cả</span>
        </label>
        <label v-for="star in [5, 4, 3, 2, 1]" :key="star"
          class="flex items-center space-x-2 text-gray-700 text-sm font-medium cursor-pointer hover:text-indigo-600 transition">
          <input type="radio" name="star-rating" :value="star" v-model="starRatingModel"
            class="form-radio text-indigo-600 border-gray-300 focus:ring-indigo-500" />
          <span class="flex items-center text-yellow-400"><i v-for="n in star" :key="n" class="fas fa-star text-sm"></i></span>
        </label>
      </div>
    </div>

    <div v-for="group in filterGroups" :key="group.id" class="border border-gray-200 rounded-lg">
      <button :aria-controls="group.id" :aria-expanded="group.expanded.toString()" @click="toggleGroup(group)"
        class="w-full flex justify-between items-center px-4 py-3 bg-indigo-50 hover:bg-indigo-100 transition-colors text-indigo-800 font-medium text-base rounded-t-lg">
        <span>{{ group.title }}</span>
        <i :class="[
          'fas transition-transform duration-300 text-indigo-600',
          group.expanded ? 'fa-chevron-down' : 'fa-chevron-up',
        ]"></i>
      </button>

      <div v-show="group.expanded" :id="group.id" class="px-4 pt-3 pb-2 space-y-2">
        <div v-if="anyAmenitySelected" class="flex justify-end">
          <button @click="clearAmenities"
            class="text-indigo-600 hover:text-indigo-800 text-sm font-semibold cursor-pointer">Xóa tất cả</button>
        </div>
        <label
          v-for="option in (group.showAll ? group.options : group.options.slice(0, group.visibleLimit || group.options.length))"
          :key="option.value"
          class="flex items-center space-x-2 text-gray-700 text-sm font-medium cursor-pointer hover:text-indigo-600 transition">
          <input type="checkbox"
            class="form-checkbox text-indigo-600 border-gray-300 rounded focus:ring-indigo-500" :checked="amenitiesModel[option.value] || false" @change="updateChecked(option, $event.target.checked)" />
          <span>{{ option.label }}</span>
        </label>

        <div v-if="group.options.length > (group.visibleLimit || group.options.length)" @click="toggleShowAll(group)"
          class="text-indigo-600 hover:text-indigo-800 text-sm font-semibold cursor-pointer pt-2 flex items-center gap-1">
          <span>{{ group.showAll ? 'Thu gọn' : 'Xem tất cả' }}</span>
          <i :class="['fas text-xs', group.showAll ? 'fa-arrow-up' : 'fa-arrow-down']"></i>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue';
import Slider from '@vueform/slider';
import '@vueform/slider/themes/default.css';
import { getAllAmenities } from '@/api/AmenityApi.js';

const props = defineProps({
  filters: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(['update:filters']);

const MIN_PRICE = 0;
const MAX_PRICE = 24000000;
const PRICE_STEP = 100000;

const priceRange = computed({
  get: () => props.filters.priceRange,
  set: (newValue) => {
    emit('update:filters', { ...props.filters, priceRange: newValue });
  },
});

const starRatingModel = computed({
  get: () => props.filters.starRating,
  set: (newValue) => {
    emit('update:filters', { ...props.filters, starRating: newValue });
  },
});

const amenitiesModel = computed({
  get: () => props.filters.amenities,
  set: (newAmenities) => {
    emit('update:filters', { ...props.filters, amenities: newAmenities });
  }
});

const minPriceFormatted = computed({
  get: () => priceRange.value[0].toLocaleString('vi-VN'),
  set: (value) => {
    const num = parseInt(value.replace(/[^0-9]/g, ''), 10);
    const currentRange = [...priceRange.value];
    if (!isNaN(num) && num >= MIN_PRICE && num <= currentRange[1]) {
      currentRange[0] = num;
      priceRange.value = currentRange;
    }
  }
});

const maxPriceFormatted = computed({
  get: () => priceRange.value[1].toLocaleString('vi-VN'),
  set: (value) => {
    const num = parseInt(value.replace(/[^0-9]/g, ''), 10);
    const currentRange = [...priceRange.value];
    if (!isNaN(num) && num <= MAX_PRICE && num >= currentRange[0]) {
      currentRange[1] = num;
      priceRange.value = currentRange;
    }
  }
});

const filterGroups = reactive([
  {
    id: 'amenities',
    title: 'Tiện nghi',
    expanded: true,
    visibleLimit: 5,
    showAll: false,
    options: [],
  },
]);

onMounted(async () => {
  try {
    const response = await getAllAmenities();
    if (response.data?.statusCode === 200) {
      const amenitiesGroup = filterGroups.find(g => g.id === 'amenities');
      if (amenitiesGroup) {
        amenitiesGroup.options = response.data.data.map(amenity => ({
          label: amenity.name,
          value: amenity.name,
        }));

        const currentAmenities = { ...props.filters.amenities };
        let needsUpdate = false;
        amenitiesGroup.options.forEach(opt => {
          if (currentAmenities[opt.value] === undefined) {
            currentAmenities[opt.value] = false;
            needsUpdate = true;
          }
        });
        if (needsUpdate) {
          emit('update:filters', { ...props.filters, amenities: currentAmenities });
        }
      }
    }
  } catch (error) {
    console.error("Failed to fetch amenities:", error);
  }
});

const anyAmenitySelected = computed(() => {
  if (!amenitiesModel.value) return false;
  return Object.values(amenitiesModel.value).some(isSelected => isSelected);
});

function clearAmenities() {
  const newAmenities = { ...amenitiesModel.value };
  for (const key in newAmenities) {
    newAmenities[key] = false;
  }
  amenitiesModel.value = newAmenities;
}

function toggleGroup(group) {
  group.expanded = !group.expanded;
}

function toggleShowAll(group) {
  group.showAll = !group.showAll;
}

function updateChecked(option, checked) {
  const newAmenities = { ...amenitiesModel.value };
  newAmenities[option.value] = checked;
  amenitiesModel.value = newAmenities;
}
</script>

<style>
.price-slider {
  --slider-connect-bg: #4f46e5;
  --slider-tooltip-bg: #4f46e5;
  --slider-handle-ring-color: #a5b4fc;
}
</style>