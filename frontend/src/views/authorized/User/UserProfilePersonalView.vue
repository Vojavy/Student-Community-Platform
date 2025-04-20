<template>
  <div class="p-6 max-w-3xl mx-auto bg-secondary rounded-lg shadow">
    <!-- Заголовок профиля -->
    <div class="flex items-center gap-6 mb-8">
      <img
          alt="Аватар"
          class="w-20 h-20 rounded-full object-cover border-2 border-accent-primary"
          :src="profile.avatarUrl || defaultAvatar"
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
    <div class="flex flex-wrap gap-4 mb-8">
      <button
          @click="goToSettings"
          class="flex-1 sm:flex-none px-6 py-3 bg-accent-primary hover:bg-accent-primary/90 text-white rounded shadow transition"
      >
        {{ t('profile.buttons.settings') }}
      </button>
      <button
          @click="goToStag"
          class="flex-1 sm:flex-none px-6 py-3 border-2 border-accent-secondary bg-accent-secondary/80 hover:bg-accent-secondary/70 text-white rounded shadow transition"
      >
        {{ t('profile.buttons.connectStag') }}
      </button>
    </div>

    <!-- Детали -->
    <div class="mt-6 flex items-center justify-between">
      <h2 class="text-lg font-semibold text-text">{{ t('profile.detailsTitle') }}</h2>
      <button @click="showDetails = !showDetails" class="p-1">
        <svg
            :class="{ 'transform rotate-180': showDetails }"
            class="w-5 h-5 text-text transition-transform"
            fill="none" stroke="currentColor" viewBox="0 0 24 24"
        >
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M19 9l-7 7-7-7"/>
        </svg>
      </button>
    </div>
    <transition name="slide-fade">
      <div
          v-if="showDetails"
          class="mt-2 p-4 bg-primary rounded border border-gray-200"
      >
        <dl class="space-y-4">
          <template v-for="key in filledDetailsKeys" :key="key">
            <div class="flex flex-col sm:flex-row sm:items-center">
              <dt class="w-1/3 font-medium text-text">
                {{ detailLabels[key] || key }}
              </dt>
              <dd class="flex-1 text-text">
                <template v-if="isLink(filledDetails[key])">
                  <a
                      :href="normalizeLink(filledDetails[key])"
                      target="_blank"
                      rel="noopener"
                      class="text-accent-primary hover:underline"
                  >{{ filledDetails[key] }}</a>
                </template>
                <template v-else>
                  {{ filledDetails[key] }}
                </template>
              </dd>
            </div>
          </template>
          <div v-if="filledDetailsKeys.length === 0" class="text-text/60 italic">
            {{ t('profile.noDetails') }}
          </div>
        </dl>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import createUserModel from '@/models/userModel'
import { fetchUserProfileIntent } from '@/intents/userIntents'
import { handleUserIntent } from '@/actions/userActions'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken'

const { t }       = useI18n()
const coordinator = inject('coordinator')
const model       = createUserModel()
const profile     = ref({})

const showDetails   = ref(false)
const defaultAvatar = '/default-avatar.png'

const formattedDate = computed(() => {
  if (!profile.value.createdAt) return ''
  const d = new Date(profile.value.createdAt)
  return `${t('profile.registered')}: ${d.toLocaleDateString()}`
})

// Подсказки для ключей
const detailLabels = {
  bio:       t('profile.settings.fields.bio'),
  status:    t('profile.settings.fields.status'),
  skills:    t('profile.settings.fields.skills'),
  languages: t('profile.personal.languages'),
  location:  t('profile.personal.location'),
  website:   t('profile.personal.website'),
  ...Object.fromEntries(
      Object.entries(t('profile.contacts')).map(([k,v]) => [k, v])
  )
}

// Формируем объект только с непустыми
const filledDetails = computed(() => {
  const d = profile.value.details || {}
  return Object.fromEntries(
      Object.entries(d)
          .filter(([k,v]) => {
            if (v == null) return false
            if (Array.isArray(v) && v.length === 0) return false
            if (typeof v === 'object' && !Array.isArray(v) && Object.keys(v).length === 0) return false
            return true
          })
          .map(([k,v]) => [k, Array.isArray(v) ? v.join(', ') : v])
  )
})
const filledDetailsKeys = computed(() => Object.keys(filledDetails.value))

function isLink(str) {
  return /^https?:\/\//.test(str) || /^www\./.test(str)
}
function normalizeLink(str) {
  return str.startsWith('http') ? str : `https://${str}`
}

onMounted(async () => {
  const userId = getUserIdFromToken()
  const intent = fetchUserProfileIntent(userId)
  profile.value = await handleUserIntent(intent, { model })
})

const goToSettings = () => coordinator.navigateToUserSettings()
const goToStag     = () => coordinator.navigateToUserStag()
</script>

<style>
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all .2s ease;
}
.slide-fade-enter-from,
.slide-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
