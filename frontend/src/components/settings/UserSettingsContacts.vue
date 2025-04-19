<template>
  <div>
    <h2 class="text-xl font-semibold mb-4">{{ t('profile.settings.tabs.contacts') }}</h2>

    <form @submit.prevent="onSubmit" class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <div v-for="(label, key) in contactFields" :key="key">
        <label class="block text-sm font-medium mb-1">{{ t(label) }}</label>
        <input
            v-model="form[key]"
            type="text"
            class="w-full px-4 py-2 border rounded bg-primary text-text"
        />
      </div>

      <div class="sm:col-span-2 mt-4">
        <button type="submit" class="px-6 py-2 bg-accent-primary text-white rounded shadow">
          {{ t('profile.settings.save') }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken'
import createUserModel from '@/models/userModel'
import { fetchUserDetailsIntent, updateUserDetailsIntent } from '@/intents/userIntents'
import { handleUserIntent } from '@/actions/userActions'

const { t } = useI18n()
const model = createUserModel()

const form = ref({
  inst: '',
  tg: '',
  fb: '',
  steam: '',
  ln: '',
  telephone: '',
})

const contactFields = {
  inst: 'profile.contacts.inst',
  tg: 'profile.contacts.tg',
  fb: 'profile.contacts.fb',
  steam: 'profile.contacts.steam',
  ln: 'profile.contacts.ln',
  telephone: 'profile.contacts.telephone'
}

onMounted(async () => {
  const userId = getUserIdFromToken()
  const intent = fetchUserDetailsIntent(userId)
  const details = await handleUserIntent(intent, { model })
  if (details?.contacts) {
    Object.assign(form.value, details.contacts)
  }
})

const onSubmit = async () => {
  const userId = getUserIdFromToken()
  const intent = updateUserDetailsIntent(userId, { contacts: form.value })
  await handleUserIntent(intent, { model })
  alert(t('profile.settings.updated'))
}
</script>
