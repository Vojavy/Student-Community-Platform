<template>
  <div class="min-h-full bg-primary text-text flex flex-col items-center justify-center p-6">
    <h1 class="text-2xl font-semibold mb-6">{{ t('login.title') }}</h1>

    <input
        v-model="email"
        type="email"
        :placeholder="t('login.email')"
        class="w-full max-w-sm px-4 py-2 border border-gray-300 rounded mb-2"
    />
    <input
        v-model="password"
        type="password"
        :placeholder="t('login.password')"
        class="w-full max-w-sm px-4 py-2 border border-gray-300 rounded mb-4"
    />

    <button
        @click="onLogin"
        class="w-full max-w-sm bg-accent-primary text-white px-4 py-2 rounded"
    >
      {{ t('login.button') }}
    </button>

    <p v-if="errorMessage" class="text-red-500 mt-4 text-sm text-center max-w-sm">
      {{ errorMessage }}
    </p>
  </div>
</template>

<script setup>
import { ref, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { loginIntent } from '@/intents/authIntents'
import { handleAuthIntent } from '@/actions/authActions'
import createAuthModel from '@/models/authModel'
import apiClient from '@/utils/api/apiClient'

const { t } = useI18n()
const coordinator = inject('coordinator')
const model = createAuthModel(apiClient)

const email = ref('')
const password = ref('')
const errorMessage = ref('')

const isValidEmail = (value) => {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)
}

const onLogin = async () => {
  errorMessage.value = ''

  if (!isValidEmail(email.value)) {
    errorMessage.value = t('errors.invalidEmail')
    return
  }

  if (!password.value) {
    errorMessage.value = t('errors.emptyPassword')
    return
  }

  try {
    const intent = loginIntent(email.value, password.value)
    await handleAuthIntent(intent, { model, coordinator })
  } catch (e) {
    errorMessage.value = t('errors.invalidCredentials')
  }
}
</script>
