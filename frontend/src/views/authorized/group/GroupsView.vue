<template>
  <div class="space-y-6">
    <h1 class="text-2xl font-bold">{{ t('groups.title') }}</h1>

    <!-- Tabs -->
    <div class="flex gap-4">
      <button
          :class="tab==='my'
          ? 'border-b-2 border-accent-primary text-accent-primary'
          : 'text-text/70'"
          @click="tab='my'"
      >{{ t('groups.tabs.my') }}</button>

      <button
          :class="tab==='browse'
          ? 'border-b-2 border-accent-primary text-accent-primary'
          : 'text-text/70'"
          @click="tab='browse'"
      >{{ t('groups.tabs.browse') }}</button>
    </div>

    <!-- My Groups -->
    <div v-if="tab==='my'" class="space-y-4">
      <input
          v-model="searchMy"
          type="text"
          :placeholder="t('groups.searchPlaceholder')"
          class="w-full p-2 border rounded"
      />
      <ul class="divide-y">
        <li
            v-for="g in filteredMyGroups"
            :key="g.id"
            @click="goToGroup(g.id)"
            class="py-2 cursor-pointer hover:bg-primary/10"
        >
          <div class="font-medium">{{ g.name }}</div>
          <div class="text-sm text-text/70">{{ g.topics.join(', ') }}</div>
        </li>
        <li v-if="!filteredMyGroups.length" class="text-text/60 italic">
          {{ t('groups.noMyGroups') }}
        </li>
      </ul>
    </div>

    <!-- Browse -->
    <div v-else class="space-y-4">
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <input
            v-model="filters.name"
            type="text"
            :placeholder="t('groups.filters.name')"
            class="p-2 border rounded"
        />
        <select v-model="filters.domain" class="p-2 border rounded">
          <option value="">{{ t('groups.filters.domain') }}</option>
          <option v-for="d in domains" :key="d" :value="d">{{ d }}</option>
        </select>
        <select v-model="filters.isPublic" class="p-2 border rounded">
          <option :value="''">{{ t('groups.filters.access') }}</option>
          <option :value="true">{{ t('groups.filters.public') }}</option>
          <option :value="false">{{ t('groups.filters.private') }}</option>
        </select>
        <input
            v-model="filters.topics"
            type="text"
            :placeholder="t('groups.filters.topics')"
            class="p-2 border rounded"
        />
      </div>
      <button
          @click="searchBrowse"
          class="px-4 py-2 bg-accent-primary text-white rounded"
      >
        {{ t('groups.filters.apply') }}
      </button>
      <ul class="divide-y mt-4">
        <li
            v-for="g in browseResults"
            :key="g.id"
            @click="goToGroup(g.id)"
            class="py-2 cursor-pointer hover:bg-primary/10"
        >
          <div class="font-medium">{{ g.name }}</div>
          <div class="text-sm text-text/70">{{ g.topics.join(', ') }}</div>
        </li>
        <li v-if="!browseResults.length" class="text-text/60 italic">
          {{ t('groups.noBrowseResults') }}
        </li>
      </ul>
    </div>

    <!-- Floating "Add Group" button -->
    <button
        @click="coordinator.navigateToCreateGroup()"
        class="fixed bottom-6 right-6 bg-accent-primary text-white w-14 h-14 rounded-full shadow-lg flex items-center justify-center hover:bg-accent-primary/90 transition"
        aria-label="Create Group"
    >
      <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" fill="none"
           viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
        <path stroke-linecap="round" stroke-linejoin="round"
              d="M12 4v16m8-8H4" />
      </svg>
    </button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import createGroupModel from '@/models/groupModel'
import {
  fetchUserGroupsIntent,
  fetchBrowseGroupsIntent
} from '@/intents/groupIntents'
import { handleGroupIntent } from '@/actions/groupActions'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken'

const { t } = useI18n()
const coordinator = inject('coordinator')
const model = createGroupModel()

const tab = ref('my')
const myGroups = ref([])
const searchMy = ref('')
const filteredMyGroups = computed(() => {
  const term = searchMy.value.toLowerCase()
  return myGroups.value.filter(g =>
      g.name.toLowerCase().includes(term)
      || g.topics.some(tp => tp.toLowerCase().includes(term))
  )
})

const domains = ref([])
const filters = ref({ name: '', domain: '', isPublic: '', topics: '' })
const browseResults = ref([])

onMounted(async () => {
  myGroups.value = await handleGroupIntent(fetchUserGroupsIntent(), { model })
})

async function searchBrowse() {
  const f = { ...filters.value }
  if (f.isPublic === '') delete f.isPublic
  else f.isPublic = f.isPublic === 'true' || f.isPublic === true

  if (f.topics) {
    f.topics = f.topics.split(',').map(s => s.trim()).filter(Boolean)
  } else delete f.topics

  browseResults.value = await handleGroupIntent(fetchBrowseGroupsIntent(f), { model })
}

function goToGroup(id) {
  coordinator.navigateToGroup(id)
}
</script>
