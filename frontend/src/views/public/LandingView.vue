<template>
  <div class="min-h-full flex items-center justify-center text-text bg-primary">
    <h1 class="text-3xl font-bold">Добро пожаловать в платформу</h1>
  </div>
</template>

<script setup>
import { onMounted, inject } from 'vue'

import apiClient from '@/utils/api/apiClient'
import createAuthModel from '@/models/authModel'
import { checkTokenIntent } from '@/intents/authIntents'
import { handleAuthIntent } from '@/actions/authActions'

const coordinator = inject('coordinator')
const model = createAuthModel(apiClient)

onMounted(async () => {
  const token = localStorage.getItem('token')
  if (!token) return

  try {
    const intent = checkTokenIntent()
    await handleAuthIntent(intent, { model, coordinator })
    coordinator.navigateToHome()
  } catch (e) {
    console.warn('Invalid token, staying on landing')
  }
})
</script>
