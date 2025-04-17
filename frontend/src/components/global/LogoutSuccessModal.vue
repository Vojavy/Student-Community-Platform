<template>
  <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
    <div class="bg-white text-text rounded-xl shadow-lg p-6 max-w-sm w-full text-center">
      <h2 class="text-lg font-semibold mb-4">{{ t('logout.title') }}</h2>
      <p class="text-sm mb-6">{{ t('logout.message') }}</p>
      <button
          class="px-4 py-2 bg-accent-primary text-white rounded"
          @click="goBack"
      >
        {{ t('logout.button') }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useI18n } from 'vue-i18n'

const visible = ref(false)
const { t } = useI18n()
const coordinator = inject('coordinator')

const goBack = () => {
  visible.value = false
  coordinator.navigateToLanding?.()
}

onMounted(() => {
  window.addEventListener('logout-success', () => {
    visible.value = true
  })
})
</script>
