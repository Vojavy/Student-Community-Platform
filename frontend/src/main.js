import { createApp } from 'vue'
import App from './App.vue'

import router from './coordinator/router'
import createCoordinator from './coordinator/coordinator'

import './assets/main.css'

import { createI18n } from 'vue-i18n'
import en from './locales/en'
import ru from './locales/ru'
import cz from './locales/cz'

const i18n = createI18n({
    legacy: false,
    locale: 'cz',
    fallbackLocale: 'en',
    messages: {
        en,
        cz,
        ru
    }
})

const app = createApp(App)

app.use(i18n)
app.use(router)
app.provide('coordinator', createCoordinator(router))
app.mount('#app')
