<template>
  <div class="p-6 max-w-3xl mx-auto text-text">
    <div class="flex items-center gap-4 mb-6">
      <img
          src="https://via.placeholder.com/80"
          alt="Аватар"
          class="w-20 h-20 rounded-full object-cover"
      />
      <div>
        <h1 class="text-2xl font-semibold">{{ userData.username || 'Мой профиль' }}</h1>
        <p class="text-sm text-gray-500">{{ formattedDate }}</p>
      </div>
    </div>

    <div class="space-x-4 mt-6">
      <button
          @click="goToSettings"
          class="px-4 py-2 bg-accent-primary text-white rounded"
      >
        Настройки профиля
      </button>
      <button
          @click="goToStag"
          class="px-4 py-2 bg-accent-secondary text-white rounded"
      >
        Подключить Stag
      </button>
    </div>
  </div>
</template>

<script setup>
import { inject, ref, onMounted, computed } from 'vue'
import createUserModel from '@/models/userModel'
import { fetchUserByIdIntent } from '@/intents/userIntents'
import { handleUserIntent } from '@/actions/userActions'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken'

const coordinator = inject('coordinator')
const model = createUserModel()

const userData = ref({
  username: '',
  createdAt: '',
  description: ''
})

const formattedDate = computed(() => {
  if (!userData.value.createdAt) return ''
  const date = new Date(userData.value.createdAt)
  return `Зарегистрирован: ${date.toLocaleDateString()}`
})

const goToSettings = () => {
  coordinator.navigateToUserSettings()
}

const goToStag = () => {
  coordinator.navigateToUserStag()
}

onMounted(async () => {
  const userId = getUserIdFromToken()
  const intent = fetchUserByIdIntent(userId)
  userData.value = await handleUserIntent(intent, { model })
})
</script>
