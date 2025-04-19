<template>
  <div>
    <h2 class="text-xl font-semibold mb-4">{{ t('profile.settings.tabs.about') }}</h2>

    <form @submit.prevent="onSubmit" class="space-y-4">
      <div>
        <label class="block font-medium">{{ t('profile.settings.fields.bio') }}</label>
        <textarea v-model="bio" class="w-full p-2 rounded border bg-primary text-text"></textarea>
      </div>
      <div>
        <label class="block font-medium">{{ t('profile.settings.fields.status') }}</label>
        <input v-model="status" class="w-full p-2 rounded border bg-primary text-text" />
      </div>
      <div>
        <label class="block font-medium">{{ t('profile.settings.fields.skills') }}</label>
        <input v-model="skills" class="w-full p-2 rounded border bg-primary text-text" />
      </div>

      <button
          type="submit"
          class="px-6 py-2 bg-accent-primary text-white rounded shadow-accent-secondary hover:bg-accent-primary/90"
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
import { fetchUserProfileIntent, updateUserDetailsIntent } from '@/intents/userIntents'
import { handleUserIntent } from '@/actions/userActions'

const { t } = useI18n()
const model = createUserModel()
const userId = getUserIdFromToken()

const bio = ref('')
const status = ref('')
const skills = ref('')

onMounted(async () => {
  const profile = await handleUserIntent(fetchUserProfileIntent(userId), { model })
  const details = profile.details || {}

  bio.value = details.bio || ''
  status.value = details.status || ''
  skills.value = details.skills || ''
})

const onSubmit = async () => {
  await handleUserIntent(updateUserDetailsIntent(userId, {
    bio: bio.value,
    status: status.value,
    skills: skills.value
  }), { model })
}
</script>
