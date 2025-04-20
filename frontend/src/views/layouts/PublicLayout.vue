<template>
  <div class="min-h-screen bg-primary text-text">
    <!-- Мобильная версия -->
    <div v-if="isMobile">
      <BurgerComponent />
      <RouterView class="p-4" />
    </div>

    <!-- Десктопная версия -->
    <div v-else class="flex flex-col h-screen">
      <!-- Верхняя панель -->
      <div class="flex justify-between items-center p-4 border-b">
        <div class="flex gap-4 items-center">
          <button class="text-sm" @click="goHome">{{ t('navBar.index') }}</button>
        </div>
        <div>
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
      </div>

      <!-- Контент -->
      <div class="flex-1 p-4">
        <RouterView />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useIsMobile } from '@/utils/device/useIsMobile'

import BurgerComponent from '@/components/mobile/BurgerComponent.vue'
import { changeLocaleIntent } from '@/intents/localeIntents.js'
import { handleLocaleIntent } from '@/actions/localeActions.js'
import createLocaleModel from '@/models/localeModel.js'
import { inject } from "vue";


const { locale, t } = useI18n()
const lang = ref(locale.value)
const model = createLocaleModel()
const coordinator = inject('coordinator')

onMounted(() => {
  const savedLocale = localStorage.getItem('locale')
  if (savedLocale) {
    lang.value = savedLocale
    locale.value = savedLocale
  }
})

const changeLang = async () => {
  locale.value = lang.value
  const intent = changeLocaleIntent(lang.value)
  await handleLocaleIntent(intent, { model })
}

const goHome = () => {
  coordinator.navigateToLanding();
}

const goLogin = () => {
  coordinator.navigateToLogin();
}

const goRegister = () => {
  coordinator.navigateToRegister();
}

const isMobile = useIsMobile()
</script>
