<template>
  <div class="p-6 max-w-3xl mx-auto text-text">
    <div class="flex items-center gap-4 mb-6">
      <img
          src="https://via.placeholder.com/80"
          alt="Аватар"
          class="w-20 h-20 rounded-full object-cover"
      />
      <div>
        <h1 class="text-2xl font-semibold">{{ userData.username || 'Пользователь' }}</h1>
        <p class="text-sm text-gray-500">{{ formattedDate }}</p>
      </div>
    </div>

    <div class="mt-4">
      <h2 class="text-lg font-semibold mb-2">О пользователе</h2>
      <p class="text-sm text-gray-700">
        {{ userData.description || 'Пользователь пока не заполнил информацию о себе.' }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import createUserModel from '@/models/userModel'
import { fetchUserByIdIntent } from '@/intents/userIntents'
import { handleUserIntent } from '@/actions/userActions'

const route = useRoute()
const userData = ref({
  username: '',
  createdAt: '',
  description: ''
})
const model = createUserModel()

const formattedDate = computed(() => {
  if (!userData.value.createdAt) return ''
  const date = new Date(userData.value.createdAt)
  return `Зарегистрирован: ${date.toLocaleDateString()}`
})

onMounted(async () => {
  const intent = fetchUserByIdIntent(route.params.id)
  userData.value = await handleUserIntent(intent, { model })
})
</script>
