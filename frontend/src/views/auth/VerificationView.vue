<template>
  <div class="min-h-full bg-primary text-text flex flex-col items-center justify-center p-6">
    <div v-if="email && !autoVerified && !verificationError" class="text-center w-full" style="max-width: 24rem; margin: 0 auto;">
      <h1 class="text-2xl font-semibold mb-6">{{ t('verification.title') + ": " + email }}</h1>

      <input
          v-model="verificationCode"
          type="text"
          :placeholder="t('verification.placeholder')"
          class="w-full max-w-sm px-4 py-2 border border-gray-300 rounded mb-4"
      />

      <button
          @click="onVerify"
          class="w-full max-w-sm bg-accent-primary text-white px-4 py-2 rounded"
          :disabled="loading"
      >
        <span v-if="!loading">{{ t('verification.button') }}</span>
        <span v-else>⏳</span>
      </button>

      <button
          @click="onResend"
          class="w-full max-w-sm text-accent-secondary mt-2"
          :disabled="resending"
      >
        <span v-if="!resending">{{ t('verification.resend') }}</span>
        <span v-else>⏳ {{ t('verification.resending') }}</span>
      </button>

      <p v-if="resendSuccess" class="text-green-500 mt-2">{{ t('verification.sent') }}</p>
    </div>

    <div v-else-if="verificationSuccess">
      <p class="text-green-500 text-center">
        {{ t('verification.success') }}<br />
        {{ t('verification.redirecting') }}
      </p>
    </div>

    <div v-else-if="verificationError">
      <p class="text-red-500 text-center">
        {{ t('verification.failed') }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, inject, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'

import { verifyIntent, resendIntent } from '@/iam/intents/authIntents'
import { handleAuthIntent } from '@/iam/actions/authActions'
import createAuthModel from '@/iam/models/authModel'

const { t } = useI18n()
const route = useRoute()
const coordinator = inject('coordinator')
const model = createAuthModel()

const verificationCode = ref('')
const email = route.query.email || null
const codeFromUrl = route.query.code || null
const autoVerified = ref(false)

const loading = ref(false)
const resending = ref(false)
const resendSuccess = ref(false)

const verificationSuccess = ref(false)
const verificationError = ref(false)

const onVerify = async () => {
  loading.value = true
  const intent = verifyIntent(email, verificationCode.value)
  await handleAuthIntent(intent, { model, coordinator })
  loading.value = false
}

const onResend = async () => {
  resending.value = true
  resendSuccess.value = false
  const intent = resendIntent(email)
  await handleAuthIntent(intent, { model })
  resendSuccess.value = true
  resending.value = false
}

onMounted(async () => {
  if (!email) {
    coordinator.navigateToLanding()
    return
  }

  if (email && codeFromUrl) {
    autoVerified.value = true
    try {
      const intent = verifyIntent(email, codeFromUrl)
      await handleAuthIntent(intent, { model, coordinator })
      verificationSuccess.value = true
      setTimeout(() => {
        coordinator.navigateToLogin()
      }, 3000)
    } catch (e) {
      verificationError.value = true
    }
  }
})
</script>
