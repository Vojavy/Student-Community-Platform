<template>
  <div class="p-6 text-center">
    <p class="mb-4">{{ t('groups.blocked') }}</p>
    <button
        @click="coordinator.navigateToLogin"
        class="px-4 py-2 bg-accent-primary text-white rounded"
    >
      {{ t('navBar.login') }}
    </button>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
import { inject }  from 'vue'

const { t } = useI18n()
const coordinator = inject('coordinator')
</script>
