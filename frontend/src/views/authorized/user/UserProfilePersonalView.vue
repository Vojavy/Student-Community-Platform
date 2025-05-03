<template>
  <div class="p-8 max-w-4xl mx-auto bg-gray-50 rounded-xl shadow-lg">
    <div v-if="isLoading" class="text-center text-gray-400 py-16">
      ⏳ {{ t('common.loading') }}
    </div>
    <div v-else-if="notFound" class="text-center text-gray-400 py-16">
      {{ t('profile.notFound') }}
    </div>

    <div v-else>
      <!-- Header -->
      <div class="flex items-center gap-6 mb-8">
        <img
            :src="defaultAvatar"
            alt="Аватар"
            class="w-24 h-24 rounded-full ring-4 ring-indigo-300 object-cover"
        />
        <div>
          <h1 class="text-4xl font-extrabold text-gray-800">
            <span v-if="userData.titulPred" class="mr-1">{{ userData.titulPred }}</span>
            {{ userData.jmeno }} {{ userData.prijmeni }}
            <span v-if="userData.titulZa" class="ml-1 text-lg text-gray-500">, {{ userData.titulZa }}</span>
          </h1>
          <p class="mt-1 text-sm text-gray-500">{{ formattedDate }}</p>
          <div class="mt-2 space-x-4 text-sm text-gray-700">
            <span><strong>{{ t('profile.email') }}:</strong> {{ userData.email }}</span>
            <span>
              <strong>{{ t('profile.active') }}:</strong>
              <span :class="userData.active ? 'text-green-600' : 'text-red-600'">
                {{ userData.active ? t('profile.yes') : t('profile.no') }}
              </span>
            </span>
          </div>
        </div>
      </div>

      <!-- Bio / Status / Skills -->
      <div v-if="userData.details" class="mb-8 px-6 py-4 bg-white rounded-lg shadow-sm">
        <p v-if="userData.details.status" class="text-lg text-gray-800">
          <strong>{{ t('profile.settings.fields.status') }}:</strong>
          {{ userData.details.status }}
        </p>
        <p v-if="userData.details.bio" class="mt-2 text-gray-600 italic">
          “{{ userData.details.bio }}”
        </p>
        <div v-if="userData.details.skills?.length" class="mt-4 flex flex-wrap gap-2">
          <span
              v-for="skill in userData.details.skills"
              :key="skill"
              class="px-3 py-1 bg-indigo-100 text-indigo-700 rounded-full text-sm font-medium"
          >
            {{ skill }}
          </span>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="mb-8 flex flex-col sm:flex-row gap-4">
        <button
            @click="goToSettings"
            class="flex-1 px-6 py-3 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition"
        >
          {{ t('profile.buttons.settings') }}
        </button>
        <button
            @click="goToStag"
            class="flex-1 px-6 py-3 border-2 border-indigo-600 text-indigo-600 font-medium rounded-lg hover:bg-indigo-50 transition"
        >
          {{ t('profile.buttons.connectStag') }}
        </button>
      </div>

      <!-- STAG Info -->
      <div
          v-if="userData.osCislo"
          class="mb-8 px-6 py-6 bg-white rounded-lg shadow-sm border-l-4 border-indigo-600"
      >
        <h2 class="text-2xl font-semibold text-indigo-600 mb-4">
          {{ t('stag.studentInfo') }}
        </h2>
        <dl class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-700">
          <div>
            <dt class="font-medium">{{ t('stag.field.osCislo') }}</dt>
            <dd>{{ userData.osCislo }}</dd>
          </div>
          <div>
            <dt class="font-medium">{{ t('stag.field.faculty') }}</dt>
            <dd>{{ userData.fakultaSp }}</dd>
          </div>
          <div>
            <dt class="font-medium">{{ t('stag.field.program') }}</dt>
            <dd>{{ userData.nazevSp }}</dd>
          </div>
          <div>
            <dt class="font-medium">{{ t('stag.field.year') }}</dt>
            <dd>{{ userData.rocnik }}</dd>
          </div>
        </dl>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken'
import { useUserStore } from '@/iam/stores/userStore'

const { t }       = useI18n()
const coordinator = inject('coordinator')
const userStore   = useUserStore()

const userId      = getUserIdFromToken()
const isLoading   = computed(() => userStore.loading)
const notFound    = ref(false)
const userData    = computed(() => userStore.profile)

const defaultAvatar = '/default-avatar.png'
const formattedDate = computed(() => {
  if (!userData.value.createdAt) return ''
  return `${t('profile.registered')}: ${new Date(userData.value.createdAt).toLocaleDateString()}`
})

onMounted(async () => {
  try {
    await userStore.fetchProfile(userId)
  } catch (err) {
    if (err.response?.status === 404) notFound.value = true
  }
})

function goToSettings() { coordinator.navigateToUserSettings() }
function goToStag()     { coordinator.navigateToUserStag() }
</script>

<style scoped>
.slide-fade-enter-active,
.slide-fade-leave-active { transition: all .2s ease; }
.slide-fade-enter-from,
.slide-fade-leave-to     { opacity: 0; transform: translateY(-8px); }
</style>
