import {createApp} from "vue";
import {createI18n} from 'vue-i18n'
import { createPinia } from 'pinia'
import ToastContainer from './components/ToastContainer.vue'
import ConfirmDialog from './components/ConfirmDialog.vue'
import { setupNotifications } from './utils/notifications.js'
import App from "./App.vue";
import router from "./router";
import { useUserStore } from './store/UserStore.js'

import en from './locales/en.json'
import vi from './locales/vi.json'

const messages = {
    en, vi
}

const i18n = createI18n({
    legacy: false,
    locale: 'vi',
    fallbackLocale: 'en',
    messages,
})

const app = createApp(App)
app.use(i18n)
app.use(router);
const pinia = createPinia()
app.use(pinia)

// Mount global toast container and expose window.$toast
const toastRoot = document.createElement('div')
document.body.appendChild(toastRoot)
const toastApp = createApp(ToastContainer)
const toastInstance = toastApp.mount(toastRoot)

window.$toast = (message, type = 'info', title) => {
  if (!toastInstance || typeof toastInstance.addToast !== 'function') return
  toastInstance.addToast({ message, type, title })
}

// Mount global confirm dialog
const confirmRoot = document.createElement('div')
document.body.appendChild(confirmRoot)
const confirmApp = createApp(ConfirmDialog)
const confirmInstance = confirmApp.mount(confirmRoot)

// Setup notifications system
setupNotifications(toastInstance, confirmInstance)

const userStore = useUserStore();
userStore.restoreUserFromToken().then(() => {
  app.mount("#app");
});
