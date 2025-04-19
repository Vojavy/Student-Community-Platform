<template>
  <div class="space-y-4">
    <div>
      <label class="block font-medium mb-1">{{ t('profile.personal.birthDate') }}</label>
      <input type="date" v-model="local.birthDate" class="w-full input" />
    </div>

    <div>
      <label class="block font-medium mb-1">{{ t('profile.personal.languages') }}</label>
      <select v-model="local.languages" multiple class="w-full input h-32">
        <option value="en">English</option>
        <option value="ru">Русский</option>
        <option value="cs">Čeština</option>
      </select>
    </div>

    <div>
      <label class="block font-medium mb-1">{{ t('profile.personal.location') }}</label>
      <input type="text" v-model="local.location" class="w-full input" />
    </div>

    <div>
      <label class="block font-medium mb-1">{{ t('profile.personal.website') }}</label>
      <input type="text" v-model="local.website" class="w-full input" />
    </div>

    <button
        @click="onSave"
        class="bg-accent-primary text-white px-6 py-2 rounded shadow-accent-secondary transition"
    >
      {{ t('common.save') }}
    </button>
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
const userId = getUserIdFromToken()
const model = createUserModel()

const local = ref({
  birthDate: '',
  languages: [],
  location: '',
  website: ''
})

onMounted(async () => {
  const fullProfile = await handleUserIntent(fetchUserProfileIntent(userId), { model })
  local.value = {
    birthDate: fullProfile.details?.birthDate || '',
    languages: fullProfile.details?.languages || [],
    location: fullProfile.details?.location || '',
    website: fullProfile.details?.website || ''
  }
})

const onSave = async () => {
  await handleUserIntent(updateUserDetailsIntent(userId, local.value), { model })
  alert(t('common.saved'))
}
</script>

<style scoped>
</style>
