<template>
  <div class="space-y-6">
    <h2 class="text-xl font-semibold">{{ t('profile.settings.tabs.security') }}</h2>

    <form @submit.prevent="onSubmit" class="space-y-4">
      <div>
        <label class="block font-medium">{{ t('profile.email') }}</label>
        <input
            type="email"
            v-model="form.email"
            class="w-full p-2 border rounded bg-primary text-text"
        />
      </div>

      <div>
        <label class="block font-medium">{{ t('profile.settings.security.oldPassword') }}</label>
        <input
            type="password"
            v-model="form.oldPassword"
            class="w-full p-2 border rounded bg-primary text-text"
        />
        <p v-if="error" class="text-red-600 text-sm mt-1">{{ error }}</p>
      </div>

      <div>
        <label class="block font-medium">{{ t('profile.settings.security.newPassword') }}</label>
        <input
            type="password"
            v-model="form.newPassword"
            class="w-full p-2 border rounded bg-primary text-text"
        />
      </div>

      <div class="flex items-center gap-2">
        <input
            id="active"
            type="checkbox"
            v-model="form.active"
            class="accent-accent-primary"
        />
        <label for="active" class="text-sm">{{ t('profile.active') }}</label>
      </div>

      <button
          type="submit"
          class="bg-accent-primary text-white px-6 py-2 rounded shadow transition"
      >
        {{ t('profile.settings.save') }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken'

import createUserModel from '@/models/userModel'
import { fetchUserProfileIntent, updateUserIntent } from '@/intents/userIntents'
import { handleUserIntent } from '@/actions/userActions'

const { t } = useI18n()
const model = createUserModel()

const userId = getUserIdFromToken()
const form = ref({
  email: '',
  oldPassword: '',
  newPassword: '',
  active: true
})

const error = ref(null)

onMounted(async () => {
  const user = await handleUserIntent(fetchUserProfileIntent(userId), { model })
  form.value.email = user.email
  form.value.active = user.active
})

const onSubmit = async () => {
  error.value = null
  try {
    await handleUserIntent(updateUserIntent(userId, form.value), { model })
    alert(t('profile.settings.updated'))
  } catch (err) {
    if (err.response?.status === 400) {
      error.value = t('profile.settings.security.incorrectPassword')
    } else {
      alert(t('profile.settings.error'))
    }
  }
}
</script>
