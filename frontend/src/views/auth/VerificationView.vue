<template>
  <div class="min-h-full bg-primary text-text flex flex-col items-center justify-center p-6">
    <div
        v-if="email && !authStore.verification.success && !authStore.verification.error"
        class="text-center w-full"
        style="max-width: 24rem; margin: 0 auto;"
    >
      <h1 class="text-2xl font-semibold mb-6">
        {{ t('verification.title') + ': ' + email }}
      </h1>

      <input
          v-model="verificationCode"
          type="text"
          :placeholder="t('verification.placeholder')"
          class="w-full max-w-sm px-4 py-2 border border-gray-300 rounded mb-4"
      />

      <button
          @click="onVerify"
          class="w-full max-w-sm bg-accent-primary text-white px-4 py-2 rounded"
          :disabled="authStore.verification.loading"
      >
        <span v-if="!authStore.verification.loading">{{ t('verification.button') }}</span>
        <span v-else>⏳</span>
      </button>

      <button
          @click="onResend"
          class="w-full max-w-sm text-accent-secondary mt-2"
          :disabled="authStore.verification.resending"
      >
        <span v-if="!authStore.verification.resending">{{ t('verification.resend') }}</span>
        <span v-else>⏳ {{ t('verification.resending') }}</span>
      </button>

      <p v-if="authStore.verification.resendSuccess" class="text-green-500 mt-2">
        {{ t('verification.sent') }}
      </p>
    </div>

    <div v-else-if="authStore.verification.success">
      <p class="text-green-500 text-center">
        {{ t('verification.success') }}<br />
        {{ t('verification.redirecting') }}
      </p>
    </div>

    <div v-else-if="authStore.verification.error">
      <p class="text-red-500 text-center">{{ t('verification.failed') }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, inject, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/iam/stores/authStore'

const { t } = useI18n()
const route = useRoute()
const coordinator = inject('coordinator')
const authStore = useAuthStore()

const verificationCode = ref('')
const email = route.query.email || null
const codeFromUrl = route.query.code || null

const onVerify = async () => {
  await authStore.verify(email, verificationCode.value, coordinator);
}

const onResend = async () => {
  await authStore.resend(email)
}

onMounted(() => {
  if (!email) {
    coordinator.navigateToLanding()
    return
  }
  if (codeFromUrl) {
    authStore.verify(email, codeFromUrl, coordinator);
  }
})
</script>
