import { defineStore } from 'pinia'

export const useLocaleStore = defineStore('localeStore', {
    state: () => ({
        locale: localStorage.getItem('locale') || 'en'
    }),
    actions: {
        setLocale(newLocale) {
            this.locale = newLocale
            localStorage.setItem('locale', newLocale)
        }
    }
})
