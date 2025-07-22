import {createApp} from "vue";
import {createI18n} from 'vue-i18n'
import { createPinia } from 'pinia'
import { DefaultApolloClient } from '@vue/apollo-composable'
import App from "./App.vue";
import router from "./router";
import apolloClient from './api/graphqlClient'

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

// Provide Apollo Client for Vue Apollo Composable
app.provide(DefaultApolloClient, apolloClient)

app.mount("#app");
