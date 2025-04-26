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
        class="w-full max-w-sm bg-accent-primary text-white px-4 py-2 rounded"
    >
      {{ t('register.button') }}
    </button>

    <p v-if="errorMessage" class="text-red-500 mt-4 text-sm">{{ errorMessage }}</p>
  </div>
</template>

<script setup>
import { ref, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { registerIntent } from '@/iam/intents/authIntents'
import { handleAuthIntent } from '@/iam/actions/authActions'
import createAuthModel from '@/iam/models/authModel'

const { t } = useI18n()
const coordinator = inject('coordinator')
const model = createAuthModel()

const email = ref('')
const password = ref('')
const repeatPassword = ref('')
const showPassword = ref(false)
const errorMessage = ref('')

const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value
}

const isValidEmail = (value) => {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)
}

const onRegister = async () => {
  errorMessage.value = ''

  if (!isValidEmail(email.value)) {
    errorMessage.value = t('errors.invalidEmail') || 'Неверный email'
    return
  }

  if (!password.value) {
    errorMessage.value = t('errors.emptyPassword') || 'Введите пароль'
    return
  }

  if (password.value !== repeatPassword.value) {
    errorMessage.value = t('errors.passwordMismatch') || 'Пароли не совпадают'
    return
  }

  const intent = registerIntent({ email: email.value, password: password.value })
  await handleAuthIntent(intent, { model, coordinator })
}
</script>
