<template>
  <div class="min-h-full flex items-center justify-center text-text bg-primary">
    <h1 class="text-3xl font-bold">
      Добро пожаловать в платформу
    </h1>
  </div>
</template>

<script setup>
import { onMounted, inject } from 'vue'
import { useAuthStore }      from '@/iam/stores/authStore.js'

const coordinator = inject('coordinator')
const authStore  = useAuthStore()

onMounted(async () => {
  if (!authStore.token) return
  try {
    await authStore.checkToken(coordinator)
    coordinator.navigateToHome()
  } catch (e) {
    console.warn('Invalid token, staying on landing')
  }
})
</script>
