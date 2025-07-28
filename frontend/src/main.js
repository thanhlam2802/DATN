import {createApp} from "vue";
import {createI18n} from 'vue-i18n'
import { createPinia } from 'pinia'
import App from "./App.vue";
import router from "./router";

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
app.use(createPinia())

// For development debugging
if (import.meta.env.MODE === 'development') {
  // Expose BusAPI globally for debugging
  window.debugGraphQL = async () => {
    try {
      
      const { BusAPI } = await import('./api/busApi/bus/api')
      window.BusAPI = BusAPI
     
      return BusAPI
    } catch (error) {
      console.error('❌ Debug setup failed:', error)
    }
  }
  
}

app.mount("#app");
