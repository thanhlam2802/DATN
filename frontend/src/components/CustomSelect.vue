<template>
  <div class="relative w-full">
    <button
      type="button"
      class="w-full flex justify-between items-center px-3 py-2 border border-slate-300 rounded-md bg-white text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition shadow-sm"
      :class="{ 'ring-2 ring-blue-500 border-blue-500': showDropdown }"
      @click="toggleDropdown"
      :disabled="disabled"
    >
      <span class="truncate text-left">
        {{ selectedLabel || placeholder }}
      </span>
      <i class="fas ml-2" :class="showDropdown ? 'fa-chevron-up' : 'fa-chevron-down'" />
    </button>
    <transition name="fade">
      <ul
        v-if="showDropdown"
        class="absolute left-0 mt-2 w-full bg-white border border-slate-200 rounded-xl shadow-xl z-30 max-h-60 overflow-auto py-1"
      >
        <li
          v-for="option in options"
          :key="option.value"
          @click="selectOption(option.value)"
          class="px-4 py-2 cursor-pointer hover:bg-blue-50 hover:text-blue-700 text-slate-800 transition rounded"
          :class="{ 'bg-blue-600 text-white': option.value === modelValue }"
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