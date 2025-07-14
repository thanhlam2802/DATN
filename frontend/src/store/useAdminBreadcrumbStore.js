import { defineStore } from 'pinia';

export const useAdminBreadcrumbStore = defineStore('adminBreadcrumb', {
  state: () => ({
    items: []
  }),
  actions: {
    setBreadcrumb(items) {
      this.items = items;
    }
  }
}); 