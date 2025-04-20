<template>
  <div class="p-6 max-w-lg mx-auto bg-secondary rounded-lg shadow">
    <h1 class="text-2xl font-semibold text-accent-primary mb-6">
      {{ t('stag.selectUniversity') }}
    </h1>

    <div v-if="isLoading" class="text-center text-text/70 py-12">
      ⏳ {{ t('common.loading') }}
    </div>

    <div v-else>
      <select
          v-model="selected"
          class="w-full bg-white border border-gray-300 rounded px-4 py-2 mb-6 focus:border-accent-primary focus:outline-none"
      >
        <option disabled value="">{{ t('stag.selectUniversity') }}</option>
        <option value="upce">{{ t('stag.universities.upce') }}</option>
        <option value="zcu">{{ t('stag.universities.zcu') }}</option>
      </select>

      <div class="mb-6">
        <p v-if="status === null" class="text-text/80">{{ t('stag.status.noToken') }}</p>
        <p v-else-if="status.hasValidTicket" class="text-success">{{ t('stag.status.valid') }}</p>
        <p v-else class="text-error">{{ t('stag.status.invalid') }}</p>
      </div>

      <div class="flex flex-wrap gap-4">
        <button
            v-if="!status?.hasValidTicket"
            @click="onLogin"
            :disabled="!selected"
            class="flex-1 bg-accent-primary text-white px-4 py-2 rounded hover:bg-accent-primary/90 disabled:opacity-50 transition"
        >
          {{ t('stag.login') }}
        </button>

        <template v-if="status?.ticket">
          <button
              v-if="!confirmingDelete"
              @click="confirmingDelete = true"
              class="flex-1 bg-error text-white px-4 py-2 rounded hover:bg-error/90 transition"
          >
            {{ t('stag.delete.show') }}
          </button>

          <div v-else class="flex gap-2 w-full">
            <button
                @click="onDelete"
                class="flex-1 bg-error text-white px-4 py-2 rounded hover:bg-error/90 transition"
            >
              {{ t('stag.delete.confirm') }}
            </button>
            <button
                @click="confirmingDelete = false"
                class="flex-1 bg-gray-400 text-white px-4 py-2 rounded hover:bg-gray-500/90 transition"
            >
              {{ t('stag.delete.cancel') }}
            </button>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import {
  checkStagTicketIntent,
  deleteStagTicket,
  saveStagToken,
  startStagLoginIntent
} from '@/intents/stagIntents.js'
import { handleStagIntent } from '@/actions/stagActions.js'
import createStagModel from '@/models/stagModel.js'

const { t } = useI18n()
const coordinator = inject('coordinator')
const model = createStagModel()

const status = ref(null)
const selected = ref('')
const confirmingDelete = ref(false)
const isLoading = ref(true)

onMounted(async () => {
  // Обработка редиректа с STAG
  const params = new URLSearchParams(window.location.search)
  const ticket = params.get('stagUserTicket')
  const longTicket = params.get('longTicket')
  const domain = params.get('domain')

  if (ticket && domain) {
    await handleStagIntent(
        saveStagToken(ticket, longTicket, domain),
        { model, coordinator }
    )
  }

  // Проверка токена
  status.value = await handleStagIntent(checkStagTicketIntent(), { model, coordinator })
  isLoading.value = false
})

const onLogin = async () => {
  if (!selected.value) return
  await handleStagIntent(startStagLoginIntent(selected.value), { model, coordinator })
}

const onDelete = async () => {
  isLoading.value = true
  await handleStagIntent(deleteStagTicket(), { model, coordinator })
  confirmingDelete.value = false
  status.value = await handleStagIntent(checkStagTicketIntent(), { model, coordinator })
  isLoading.value = false
}
</script>
