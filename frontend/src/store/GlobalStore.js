import {defineStore} from "pinia";

export const useLoadingStore = defineStore('loading', {
    state: () => ({
        isLoading: false,
    }),
    actions: {
        startLoading() {
            this.isLoading = true
        },
        stopLoading() {
            this.isLoading = false
        },
    },
})