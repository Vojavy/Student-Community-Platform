<template>
  <div class="p-6 max-w-3xl mx-auto bg-secondary rounded-lg shadow">
    <!-- Шапка профиля -->
    <div class="flex items-center gap-6 mb-8">
      <img
          alt="Аватар"
          class="w-20 h-20 rounded-full object-cover border-2 border-accent-primary"
      />
      <div>
        <h1 class="text-3xl font-bold text-accent-primary">
          {{ userData.username || t('profile.defaultName') }}
        </h1>
        <p class="text-sm text-text/70">{{ formattedDate }}</p>
      </div>
    </div>

    <!-- STAG данные -->
    <div v-if="studentInfo" class="bg-primary p-6 rounded-lg border border-gray-200 mb-8">
      <h2 class="text-xl font-semibold text-accent-secondary mb-4">
        {{ t('stag.studentInfo') }}
      </h2>
      <dl class="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <div>
          <dt class="font-medium text-text">{{ t('stag.field.name') }}</dt>
          <dd class="text-text">{{ studentInfo.firstName }} {{ studentInfo.lastName }}</dd>
        </div>
        <div>
          <dt class="font-medium text-text">{{ t('stag.field.osCislo') }}</dt>
          <dd class="text-text">{{ studentInfo.osCislo }}</dd>
        </div>
      </dl>
    </div>

    <!-- Кнопки действий -->
    <div class="flex flex-wrap gap-4">
      <button
          @click="goToSettings"
          class="flex-1 sm:flex-none px-6 py-3 bg-accent-primary text-white rounded hover:bg-accent-primary/90 transition"
      >
        {{ t('profile.buttons.settings') }}
      </button>
      <button
          @click="goToStag"
          class="flex-1 sm:flex-none px-6 py-3 bg-accent-secondary text-white rounded hover:bg-accent-secondary/90 transition"
      >
        {{ t('profile.buttons.connectStag') }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { inject, ref, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'

import createUserModel from '@/models/userModel'
import createStagModel from '@/models/stagModel'
import { fetchUserByIdIntent } from '@/intents/userIntents'
import { fetchStudentInfoIntent } from '@/intents/stagIntents'
import { handleUserIntent } from '@/actions/userActions'
import { handleStagIntent } from '@/actions/stagActions'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken'

const { t } = useI18n()
const coordinator = inject('coordinator')

const userModel = createUserModel()
const stagModel = createStagModel()

const userData = ref({ username: '', createdAt: '' })
const studentInfo = ref(null)

const formattedDate = computed(() => {
  if (!userData.value.createdAt) return ''
  const date = new Date(userData.value.createdAt)
  return `${t('profile.registered')}: ${date.toLocaleDateString()}`
})

const goToSettings = () => coordinator.navigateToUserSettings()
const goToStag     = () => coordinator.navigateToUserStag()

onMounted(async () => {
  // Загрузка данных пользователя
  const userId = getUserIdFromToken()
  const userIntent = fetchUserByIdIntent(userId)
  userData.value = await handleUserIntent(userIntent, { model: userModel })

  // // Пытаемся получить STAG‑информацию
  // try {
  //   const domain = 'upce' // или 'zcu'
  //   const studentIntent = fetchStudentInfoIntent(domain)
  //   studentInfo.value = await handleStagIntent(studentIntent, { model: stagModel, coordinator })
  // } catch (e) {
  //   console.warn('Ошибка при получении STAG данных:', e)
  // }
})
</script>
