import {createApp} from "vue";
import {createI18n} from 'vue-i18n'
import { createPinia } from 'pinia'
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

const userStore = useUserStore();
userStore.restoreUserFromToken().then(() => {
  app.mount("#app");
});
