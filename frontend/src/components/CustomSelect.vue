<template>
  <div class="relative w-full">
    <button
      type="button"
      class="w-full flex justify-between items-center px-4 py-2 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors duration-200 focus:outline-none"
      :class="{ 'bg-gray-200': showDropdown }"
      @click="toggleDropdown"
      :disabled="disabled"
    >
             <span class="truncate text-left text-slate-600 hover:text-orange-600 font-medium transition-colors duration-200">
         {{ selectedLabel || placeholder }}
       </span>
      <i class="fas text-black text-sm transition-transform duration-200" :class="showDropdown ? 'fa-chevron-up' : 'fa-chevron-down'" />
    </button>
    <transition name="fade">
      <ul
        v-if="showDropdown"
        :class="['absolute left-0 w-full bg-white rounded-lg shadow-lg border border-gray-200 z-30 max-h-60 overflow-auto py-1', direction === 'up' ? 'bottom-full mb-2' : 'mt-2']"
      >
        <li
          v-for="option in options"
          :key="option.value"
          @click="selectOption(option.value)"
          class="w-full text-left px-4 py-2 hover:bg-orange-50 transition-colors duration-150 cursor-pointer"
          :class="option.value === modelValue ? 'bg-orange-50 text-orange-600' : 'text-gray-700'"
        >
          {{ option.label }}
        </li>
        <li v-if="options.length === 0" class="px-4 py-2 text-slate-400">Không có lựa chọn</li>
      </ul>
    </transition>
  </div>
</template>

<script>
export default {
  name: 'CustomSelect',
  props: {
    modelValue: [String, Number],
    options: {
      type: Array,
      required: true,
    },
    placeholder: {
      type: String,
      default: 'Chọn...'
    },
    disabled: {
      type: Boolean,
      default: false
    },
    direction: {
      type: String,
      default: 'down',
    }
  },
  data() {
    return {
      showDropdown: false,
    };
  },
  computed: {
    selectedLabel() {
      const found = this.options.find(o => o.value === this.modelValue);
      return found ? found.label : '';
    }
  },
  methods: {
    toggleDropdown() {
      if (this.disabled) return;
      this.showDropdown = !this.showDropdown;
      if (this.showDropdown) {
        document.addEventListener('click', this.handleClickOutside, true);
      } else {
        document.removeEventListener('click', this.handleClickOutside, true);
      }
    },
    selectOption(value) {
      this.$emit('update:modelValue', value);
      this.showDropdown = false;
      document.removeEventListener('click', this.handleClickOutside, true);
    },
    handleClickOutside(e) {
      if (!this.$el.contains(e.target)) {
        this.showDropdown = false;
        document.removeEventListener('click', this.handleClickOutside, true);
      }
    }
  },
  beforeUnmount() {
    document.removeEventListener('click', this.handleClickOutside, true);
  }
};
</script>

<style scoped>
.fade-enter-active, .fade-leave-active {
  transition: opacity .2s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style> 