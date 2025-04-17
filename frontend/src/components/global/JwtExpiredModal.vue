<template>
  <div
      v-if="visible"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm"
  >
    <div class="bg-white text-text rounded-xl shadow-lg p-6 max-w-sm w-full text-center">
      <h2 class="text-lg font-semibold mb-4">{{ t('sessionExpired.title') }}</h2>
      <p class="text-sm mb-6">{{ t('sessionExpired.message') }}</p>
      <div class="flex justify-center gap-4">
        <button
            class="px-4 py-2 bg-accent-primary text-white rounded"
            @click="goToLogin"
        >
          {{ t('sessionExpired.login') }}
        </button>
        <button
            class="px-4 py-2 border border-gray-300 rounded"
            @click="goToHome"
        >
          {{ t('sessionExpired.close') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const coordinator = inject('coordinator')
const visible = ref(false)

const goToLogin = () => {
  visible.value = false
  coordinator.navigateToLogin()
}

const goToHome = () => {
  visible.value = false
  coordinator.navigateToLanding?.()
}

onMounted(() => {
  window.addEventListener('jwt-expired', () => {
    visible.value = true
  })
})
</script>
