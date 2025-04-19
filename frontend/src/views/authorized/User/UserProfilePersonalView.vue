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
            {{ profile.titulPred ? profile.titulPred + ' ' : '' }}
            {{ profile.jmeno }} {{ profile.prijmeni }}
            {{ profile.titulZa ? ', ' + profile.titulZa : '' }}
        </h1>
        <p class="text-sm text-text/70">{{ formattedDate }}</p>
        <p class="text-sm">
          <span class="font-medium">{{ t('profile.email') }}:</span>
          {{ profile.email }}
        </p>
        <p class="text-sm">
          <span class="font-medium">{{ t('profile.active') }}:</span>
          {{ profile.active ? t('profile.yes') : t('profile.no') }}
        </p>
      </div>
    </div>

    <!-- STAG данные -->
    <div v-if="profile.osCislo" class="bg-primary p-6 rounded-lg border border-gray-200 mb-8">
      <h2 class="text-xl font-semibold text-accent-secondary mb-4">
        {{ t('stag.studentInfo') }}
      </h2>
      <dl class="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <div>
          <dt class="font-medium text-text">{{ t('stag.field.name') }}</dt>
          <dd class="text-text">
            {{ profile.userName || t('profile.defaultName') }}
          </dd>
        </div>
        <div>
          <dt class="font-medium text-text">{{ t('stag.field.osCislo') }}</dt>
          <dd class="text-text">{{ profile.osCislo }}</dd>
        </div>
        <div>
          <dt class="font-medium text-text">{{ t('stag.field.faculty') }}</dt>
          <dd class="text-text">{{ profile.fakultaSp }}</dd>
        </div>
        <div>
          <dt class="font-medium text-text">{{ t('stag.field.program') }}</dt>
          <dd class="text-text">{{ profile.nazevSp }}</dd>
        </div>
        <div>
          <dt class="font-medium text-text">{{ t('stag.field.year') }}</dt>
          <dd class="text-text">{{ profile.rocnik }}</dd>
        </div>
      </dl>
    </div>

    <!-- Кнопки действий -->
    <div class="flex flex-wrap gap-4">
      <button
          @click="goToSettings"
          class="flex-1 sm:flex-none px-6 py-3 bg-accent-primary hover:bg-accent-primary/90 text-white rounded shadow-accent-secondary transition"
      >
        {{ t('profile.buttons.settings') }}
      </button>
      <button
          @click="goToStag"
          class="flex-1 sm:flex-none px-6 py-3 border-2 border-accent-secondary bg-accent-secondary/80 hover:bg-accent-secondary/70 text-white rounded shadow-accent-secondary transition"
      >
        {{ t('profile.buttons.connectStag') }}
      </button>
    </div>
  </div>
</template>


<script setup>
import { ref, onMounted, computed, inject } from 'vue'
import { useI18n } from 'vue-i18n'

import createUserModel from '@/models/userModel'
import { fetchUserProfileIntent } from '@/intents/userIntents'
import { handleUserIntent } from '@/actions/userActions'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken'

const { t } = useI18n()
const coordinator = inject('coordinator')

const model = createUserModel()
const profile = ref({})

const formattedDate = computed(() => {
  if (!profile.value.createdAt) return ''
  const d = new Date(profile.value.createdAt)
  return `${t('profile.registered')}: ${d.toLocaleDateString()}`
})

const goToSettings = () => coordinator.navigateToUserSettings()
const goToStag     = () => coordinator.navigateToUserStag()

onMounted(async () => {
  const userId = getUserIdFromToken()
  const intent = fetchUserProfileIntent(userId)
  profile.value = await handleUserIntent(intent, { model })
})
</script>
