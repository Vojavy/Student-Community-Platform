<!-- src/views/authorized/User/UserStagView.vue -->
<template>
  <div class="p-6 max-w-lg mx-auto bg-secondary rounded-lg shadow">
    <h1 class="text-2xl font-semibold text-accent-primary mb-6">
      {{ t('stag.selectUniversity') }}
    </h1>

    <!-- Loading -->
    <div v-if="isLoading" class="text-center text-text/70 py-12">
      ⏳ {{ t('common.loading') }}
    </div>

    <!-- Main -->
    <div v-else>
      <select
          v-model="selected"
          class="w-full bg-white border border-gray-300 rounded px-4 py-2 mb-6 focus:border-accent-primary focus:outline-none"
      >
        <option disabled value="">{{ t('stag.selectUniversity') }}</option>
        <option
            v-for="dom in domains"
            :key="dom.domain"
            :value="dom.domain"
        >
          {{ dom.domainName }}
        </option>
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
import { useI18n }              from 'vue-i18n'

import {
  checkStagTicketIntent,
  deleteStagTicket,
  saveStagToken,
  startStagLoginIntent
} from '@/iam/intents/stagIntents.js'
import { handleStagIntent }   from '@/iam/actions/stagActions.js'
import createStagModel        from '@/iam/models/stagModel.js'

import { fetchDomainsIntent } from '@/iam/intents/domainIntents.js'
import { handleDomainIntent } from '@/iam/actions/domainActions.js'
import createDomainModel      from '@/iam/models/domainModel.js'

const { t }      = useI18n()
const coordinator= inject('coordinator')

const stagModel      = createStagModel()
const status         = ref(null)
const confirmingDelete = ref(false)

const domainModel    = createDomainModel()
const domains        = ref([])
const selected       = ref('')

const isLoading      = ref(true)

onMounted(async () => {
  domains.value = await handleDomainIntent(fetchDomainsIntent(), { model: domainModel })

  const params = new URLSearchParams(window.location.search)
  const ticket      = params.get('stagUserTicket')
  const longTicket  = params.get('longTicket')
  const domainCode  = params.get('domain')
  if (ticket && domainCode) {
    await handleStagIntent(
        saveStagToken(ticket, longTicket, domainCode),
        { model: stagModel, coordinator }
    )
  }

  status.value = await handleStagIntent(checkStagTicketIntent(), { model: stagModel, coordinator })
  isLoading.value = false
})

const onLogin = async () => {
  if (!selected.value) return
  await handleStagIntent(startStagLoginIntent(selected.value), { model: stagModel, coordinator })
}

const onDelete = async () => {
  isLoading.value = true
  await handleStagIntent(deleteStagTicket(), { model: stagModel, coordinator })
  confirmingDelete.value = false
  status.value = await handleStagIntent(checkStagTicketIntent(), { model: stagModel, coordinator })
  isLoading.value = false
}
</script>
