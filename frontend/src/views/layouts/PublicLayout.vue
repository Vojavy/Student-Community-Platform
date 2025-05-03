<template>
  <div class="min-h-screen bg-primary text-text">
    <div v-if="isMobile">
      <BurgerComponent />
      <RouterView class="p-4" />
    </div>

    <div v-else class="flex flex-col h-screen">
      <div class="flex justify-between items-center p-4 border-b">
        <div class="flex gap-4 items-center">
          <button class="text-sm" @click="goHome">{{ t('navBar.index') }}</button>
        </div>
        <div class="flex gap-4 items-center">
          <select v-model="lang" @change="changeLang" class="px-2 py-1 border rounded">
            <option value="ru">Русский</option>
            <option value="en">English</option>
            <option value="cz">Čeština</option>
          </select>
          <button @click="goLogin" class="text-sm">{{ t('navBar.login') }}</button>
          <button @click="goRegister" class="text-sm">{{ t('navBar.registration') }}</button>
        </div>
      </div>

      <div class="flex-1 p-4">
        <RouterView />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useIsMobile } from '@/utils/device/useIsMobile'
import BurgerComponent from '@/components/mobile/BurgerComponent.vue'
import { useLocaleStore } from '@/iam/stores/localeStore'

const { locale, t } = useI18n()
const localeStore = useLocaleStore()
const lang = ref(localeStore.locale)
const coordinator = inject('coordinator')
const isMobile = useIsMobile()

onMounted(() => {
  locale.value = localeStore.locale
  lang.value = localeStore.locale
})

const changeLang = () => {
  localeStore.setLocale(lang.value)
  locale.value = localeStore.locale
}

const goHome     = () => coordinator.navigateToLanding()
const goLogin    = () => coordinator.navigateToLogin()
const goRegister = () => coordinator.navigateToRegister()
</script>
