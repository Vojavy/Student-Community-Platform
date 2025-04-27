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
          <button @click="goHome" class="text-sm" >{{ t('navBar.index') }}</button>
        </div>
        <div>
          <div class="flex justify-end gap-4 items-center">
            <button @click="onLogout" class="text-sm">{{t('navBar.signout')}}</button>
            <select v-model="lang" @change="changeLang" class="px-2 py-1 border rounded">
              <option value="ru">Русский</option>
              <option value="en">English</option>
              <option value="cz">Čeština</option>
            </select>
          </div>
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

import { changeLocaleIntent }     from '@/iam/intents/localeIntents.js'
import { handleLocaleIntent }     from '@/iam/actions/localeActions.js'
import BurgerComponent            from '@/components/mobile/BurgerComponent.vue'
import BottomNavbarComponent      from '@/components/mobile/BottomNavbarComponent.vue'
import { useIsMobile }            from '@/utils/device/useIsMobile'

import NavBarComponent            from '@/components/desktop/NavBarComponent.vue'
import ContainerCentralComponent  from '@/components/desktop/ContainerCentralComponent.vue'

import createAuthModel            from "@/iam/models/authModel.js";
import createLocaleModel          from '@/iam/models/localeModel.js'
import createUserModel            from "@/iam/models/userModel.js";
import apiClient                  from "@/utils/api/apiClient.js"
import { logoutIntent }           from "@/iam/intents/authIntents.js";
import { handleAuthIntent }       from "@/iam/actions/authActions.js";
import { fetchUserRolesIntent }   from "@/iam/intents/userIntents.js";
import { handleUserIntent }       from "@/iam/actions/userActions.js";

const { locale, t } = useI18n()
const lang         = ref(locale.value)
const coordinator  = inject('coordinator')

const model = {
  auth:   createAuthModel(apiClient),
  locale: createLocaleModel(),
  user:   createUserModel(apiClient)
}

const userRoles = ref([])
provide('user_roles', userRoles)

onMounted(() => {
  const savedLocale = localStorage.getItem('locale')
  if (savedLocale) {
    lang.value   = savedLocale
    locale.value = savedLocale
  }
  fetchRoles()
})

const changeLang = async () => {
  locale.value = lang.value
  const intent = changeLocaleIntent(lang.value)
  await handleLocaleIntent(intent, { model: model.locale })
}

const goHome = () => {
  coordinator.navigateToHome()
}

const onLogout = async () => {
  const intent = logoutIntent()
  await handleAuthIntent(intent, { model: model.auth, coordinator })
}

async function fetchRoles() {
  try {
    const roles = await handleUserIntent(
        fetchUserRolesIntent(),
        { model: model.user, coordinator }
    )
    userRoles.value = Array.isArray(roles) ? roles : []
  } catch (e) {
    console.error('Could not fetch user roles', e)
  }
}

const isMobile = useIsMobile()
</script>
