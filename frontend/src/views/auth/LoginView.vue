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
        :disabled="authStore.loading"
        class="w-full max-w-sm bg-accent-primary text-white px-4 py-2 rounded disabled:opacity-50 disabled:cursor-not-allowed"
    >
      <span v-if="!authStore.loading">{{ t('login.button') }}</span>
      <span v-else>⏳</span>
    </button>

    <p v-if="authStore.error" class="text-red-500 mt-4 text-sm text-center max-w-sm">
      {{ authStore.error }}
    </p>
  </div>
</template>

<script setup>
import { ref, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/iam/stores/authStore'

const { t } = useI18n()
const coordinator = inject('coordinator')
const authStore = useAuthStore()

const email = ref('')
const password = ref('')

const isValidEmail = v => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)

const onLogin = async () => {
  authStore.error = ''
  if (!isValidEmail(email.value)) {
    authStore.error = t('errors.invalidEmail')
    return
  }
  if (!password.value) {
    authStore.error = t('errors.emptyPassword')
    return
  }
  await authStore.login({ email: email.value, password: password.value }, coordinator)
}
</script>
