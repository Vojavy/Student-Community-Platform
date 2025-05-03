<!-- src/views/layouts/HomeLayout.vue -->
<template>
  <div class="min-h-screen bg-primary text-text">
    <!-- Мобильная версия -->
    <div v-if="isMobile">
      <BurgerComponent />
      <RouterView class="p-4" />
      <BottomNavbarComponent />
    </div>

    <!-- Десктопная версия -->
    <div v-else class="flex flex-col h-screen">
      <div class="flex justify-between items-center p-4 border-b">
        <div class="flex gap-4 items-center">
          <button @click="goHome" class="text-sm">
            {{ t('navBar.index') }}
          </button>
        </div>
        <div class="flex gap-4 items-center">
          <button @click="onLogout" class="text-sm">
            {{ t('navBar.signout') }}
          </button>
          <select v-model="lang" @change="changeLang" class="px-2 py-1 border rounded">
            <option value="ru">Русский</option>
            <option value="en">English</option>
            <option value="cz">Čeština</option>
          </select>
        </div>
      </div>

      <div class="flex flex-1">
        <NavBarComponent class="w-[240px] border-r" />
        <ContainerCentralComponent class="flex-1 p-4" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject, provide, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useIsMobile } from '@/utils/device/useIsMobile'

import BurgerComponent           from '@/components/mobile/BurgerComponent.vue'
import BottomNavbarComponent     from '@/components/mobile/BottomNavbarComponent.vue'
import NavBarComponent           from '@/components/desktop/NavBarComponent.vue'
import ContainerCentralComponent from '@/components/desktop/ContainerCentralComponent.vue'

import { useAuthStore }   from '@/iam/stores/authStore'
import { useLocaleStore } from '@/iam/stores/localeStore'
import { useUserStore }   from '@/iam/stores/userStore'

const { t, locale } = useI18n()
const coordinator    = inject('coordinator')
const isMobile       = useIsMobile()

const authStore   = useAuthStore()
const localeStore = useLocaleStore()
const userStore   = useUserStore()

const lang = ref(localeStore.locale)

provide('user_roles', computed(() => userStore.roles))

onMounted(async () => {
  locale.value = localeStore.locale
  lang.value   = localeStore.locale
  await userStore.fetchRoles()
})

function goHome() {
  coordinator.navigateToHome()
}

async function onLogout() {
  await authStore.logout(coordinator)
}

async function changeLang() {
  localeStore.setLocale(lang.value)
  locale.value = localeStore.locale
}
</script>
