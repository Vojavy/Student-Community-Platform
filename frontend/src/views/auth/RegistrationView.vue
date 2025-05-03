<template>
  <div class="min-h-full bg-primary text-text flex flex-col items-center justify-center p-6">
    <h1 class="text-2xl font-semibold mb-6">{{ t('register.title') }}</h1>

    <input
        v-model="email"
        type="email"
        :placeholder="t('register.email')"
        class="w-full max-w-sm px-4 py-2 border border-gray-300 rounded mb-4"
    />

    <div class="w-full max-w-sm relative mb-4">
      <input
          v-model="password"
          :type="showPassword ? 'text' : 'password'"
          :placeholder="t('register.password')"
          class="w-full px-4 py-2 border border-gray-300 rounded"
      />
      <button
          type="button"
          @click="togglePasswordVisibility"
          class="absolute right-3 top-1/2 -translate-y-1/2 text-sm text-gray-500"
      >
        {{ showPassword ? '🙈' : '👁️' }}
      </button>
    </div>

    <div class="w-full max-w-sm relative mb-4">
      <input
          v-model="repeatPassword"
          :type="showPassword ? 'text' : 'password'"
          :placeholder="t('register.repeat')"
          class="w-full px-4 py-2 border border-gray-300 rounded"
      />
    </div>

    <button
        @click="onRegister"
        :disabled="authStore.loading"
        class="w-full max-w-sm bg-accent-primary text-white px-4 py-2 rounded disabled:opacity-50 disabled:cursor-not-allowed"
    >
      <span v-if="!authStore.loading">{{ t('register.button') }}</span>
      <span v-else>⏳ {{ t('common.loading') }}</span>
    </button>

    <p v-if="authStore.error" class="text-red-500 mt-4 text-sm">{{ authStore.error }}</p>
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
const repeatPassword = ref('')
const showPassword = ref(false)

const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value
}

const isValidEmail = v => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)

const onRegister = async () => {
  authStore.error = ''
  if (!isValidEmail(email.value)) {
    authStore.error = t('errors.invalidEmail')
    return
  }
  if (!password.value) {
    authStore.error = t('errors.emptyPassword')
    return
  }
  if (password.value !== repeatPassword.value) {
    authStore.error = t('errors.passwordMismatch')
    return
  }
  await authStore.register({ email: email.value, password: password.value }, coordinator)
}
</script>
