<!-- src/views/auth/AccessDeniedView.vue -->
<template>
  <div class="min-h-screen bg-primary text-text flex flex-col items-center justify-center p-6 text-center">
    <h1 class="text-2xl font-bold mb-4">🚫 {{ t('accessDenied.title') }}</h1>
    <p class="mb-6">{{ t('accessDenied.message') }}</p>
    <button
        @click="goHome"
        class="px-4 py-2 bg-accent-primary text-white rounded"
    >
      {{ t('accessDenied.back') }}
    </button>
  </div>
</template>

<script setup>
import { inject } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const coordinator = inject('coordinator')

const goHome = () => {
  coordinator.navigateToHome?.()
}
</script>
