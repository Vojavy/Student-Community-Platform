<template>
  <div class="space-y-6">
    <h1 class="text-2xl font-bold">{{ t('groups.title') }}</h1>

    <!-- Tabs -->
    <div class="flex gap-4">
      <button
          :class="tab==='my' ? 'border-b-2 border-accent-primary text-accent-primary' : 'text-text/70'"
          @click="tab='my'"
      >
        {{ t('groups.tabs.my') }}
      </button>
      <button
          :class="tab==='browse' ? 'border-b-2 border-accent-primary text-accent-primary' : 'text-text/70'"
          @click="tab='browse'"
      >
        {{ t('groups.tabs.browse') }}
      </button>
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
          <div class="text-sm text-text/70">
            {{ Array.isArray(g.topics) ? g.topics.join(', ') : '' }}
          </div>
          <div class="text-xs text-text/60 mt-1">
            {{ g?.domain || t('groups.domainUnknown') }}
            &middot;
            <span :class="g.public ? 'text-green-600' : 'text-red-600'">
              {{ g.public ? t('groups.public') : t('groups.private') }}
            </span>
          </div>
        </li>
        <li v-if="filteredMyGroups.length === 0" class="text-text/60 italic">
          {{ t('groups.noMyGroups') }}
        </li>
      </ul>

      <!-- Pagination for My Groups -->
      <div v-if="myTotalPages > 1" class="flex items-center justify-center gap-4">
        <button
            @click="loadMyGroups(myPage - 1)"
            :disabled="myPage === 0"
            class="px-3 py-1 border rounded disabled:opacity-50"
        >
          {{ t('groups.pagination.prev') }}
        </button>
        <span>{{ myPage + 1 }} / {{ myTotalPages }}</span>
        <button
            @click="loadMyGroups(myPage + 1)"
            :disabled="myPage + 1 >= myTotalPages"
            class="px-3 py-1 border rounded disabled:opacity-50"
        >
          {{ t('groups.pagination.next') }}
        </button>
      </div>
    </div>

    <!-- Browse Groups -->
    <div v-else class="space-y-6">
      <!-- Filters -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <input
            v-model="filters.name"
            type="text"
            :placeholder="t('groups.filters.name')"
            class="p-2 border rounded"
        />
        <select v-model="filters.domain" class="p-2 border rounded">
          <option :value="''">{{ t('groups.filters.domainAll') }}</option>
          <option v-for="d in domains" :key="d.id" :value="d.id">
            {{ d.domainName }}
          </option>
        </select>
        <select v-model="filters.isPublic" class="p-2 border rounded">
          <option :value="''">{{ t('groups.filters.accessAll') }}</option>
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

      <div class="flex flex-wrap items-center gap-4">
        <button @click="loadBrowse(0)" class="px-4 py-2 bg-accent-primary text-white rounded">
          {{ t('groups.filters.apply') }}
        </button>
        <div class="flex items-center gap-2">
          <label>{{ t('groups.pagination.size') }}:</label>
          <select v-model.number="browseSize" @change="loadBrowse(0)" class="p-1 border rounded">
            <option v-for="opt in [25,50,100]" :key="opt" :value="opt">{{ opt }}</option>
          </select>
        </div>
      </div>

      <ul class="divide-y">
        <li
            v-for="g in browseResults"
            :key="g.id"
            @click="goToGroup(g.id)"
            class="py-2 cursor-pointer hover:bg-primary/10"
        >
          <div class="font-medium">{{ g.name }}</div>
          <div class="text-sm text-text/70">
            {{ Array.isArray(g.topics) ? g.topics.join(', ') : '' }}
          </div>
          <div class="text-xs text-text/60 mt-1">
            {{ g?.domain || t('groups.domainUnknown') }}
            &middot;
            <span :class="g.public ? 'text-green-600' : 'text-red-600'">
              {{ g.public ? t('groups.public') : t('groups.private') }}
            </span>
          </div>
        </li>
        <li v-if="browseResults.length === 0" class="text-text/60 italic">
          {{ t('groups.noBrowseResults') }}
        </li>
      </ul>

      <!-- Pagination for Browse -->
      <div v-if="browseTotalPages > 1" class="flex items-center justify-center gap-4">
        <button
            @click="loadBrowse(browsePage - 1)"
            :disabled="browsePage === 0"
            class="px-3 py-1 border rounded disabled:opacity-50"
        >
          {{ t('groups.pagination.prev') }}
        </button>
        <span>{{ browsePage + 1 }} / {{ browseTotalPages }}</span>
        <button
            @click="loadBrowse(browsePage + 1)"
            :disabled="browsePage + 1 >= browseTotalPages"
            class="px-3 py-1 border rounded disabled:opacity-50"
        >
          {{ t('groups.pagination.next') }}
        </button>
      </div>
    </div>

    <!-- Floating Add -->
    <button
        @click="coordinator.navigateToCreateGroup()"
        class="fixed right-6 bg-accent-primary text-white w-14 h-14 rounded-full shadow-lg flex items-center justify-center hover:bg-accent-primary/90 transition"
        :class="isMobile ? 'bottom-20' : 'bottom-6'"
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

import createGroupModel from '@/iam/models/group/groupModel.js'
import { fetchUserGroupsIntent, fetchBrowseGroupsIntent } from '@/iam/intents/groupIntents'
import { handleGroupIntent } from '@/iam/actions/groupActions'

import createDomainModel from '@/iam/models/domainModel'
import { fetchDomainsIntent } from '@/iam/intents/domainIntents'
import { handleDomainIntent } from '@/iam/actions/domainActions'

const { t } = useI18n()
const coordinator = inject('coordinator')

import { useIsMobile } from '@/utils/device/useIsMobile'

// models
const groupModel = createGroupModel()
const domainModel = createDomainModel()

// My groups
const tab = ref('my')
const myGroups = ref([])
const searchMy = ref('')
const myPage = ref(0)
const mySize = ref(25)
const myTotalPages = ref(1)
const isMobile = useIsMobile()

const filteredMyGroups = computed(() => {
  const term = searchMy.value.toLowerCase()
  return myGroups.value.filter(g =>
      g.name.toLowerCase().includes(term) ||
      (Array.isArray(g.topics) && g.topics.some(tp => tp.toLowerCase().includes(term)))
  )
})

// Browse
const domains = ref([])
const filters = ref({ name: '', domain: '', isPublic: '', topics: '' })
const browseResults = ref([])
const browsePage = ref(0)
const browseSize = ref(25)
const browseTotalPages = ref(1)

onMounted(async () => {
  await loadMyGroups(0)
  domains.value = await handleDomainIntent(fetchDomainsIntent(), { model: domainModel })
  await loadBrowse(0)
})

async function loadMyGroups(pageNum) {
  myPage.value = pageNum
  const resp = await handleGroupIntent(
      fetchUserGroupsIntent({ page: myPage.value, size: mySize.value }),
      { model: groupModel }
  )
  myTotalPages.value = resp.totalPages
  myGroups.value = resp.content.map(g => ({
    ...g,
    topics: Array.isArray(g.topics) ? g.topics : JSON.parse(g.topics || '[]')
  }))
  console.log(myGroups.value)
}

async function loadBrowse(pageNum) {
  browsePage.value = pageNum
  const q = {
    page: browsePage.value,
    size: browseSize.value,
    ...(filters.value.name ? { name: filters.value.name } : {}),
    ...(filters.value.domain ? { domainId: Number(filters.value.domain) } : {}),
    ...(filters.value.isPublic !== '' ? { isPublic: filters.value.isPublic === true || filters.value.isPublic === 'true' } : {}),
    ...(filters.value.topics
        ? { topics: filters.value.topics.split(',').map(s => s.trim()).filter(Boolean) }
        : {})
  }
  const resp = await handleGroupIntent(fetchBrowseGroupsIntent(q), { model: groupModel })
  browseTotalPages.value = resp.totalPages
  browseResults.value = resp.content.map(g => ({
    ...g,
    topics: Array.isArray(g.topics) ? g.topics : JSON.parse(g.topics || '[]')
  }))
}

function goToGroup(id) {
  coordinator.navigateToGroup(id)
}
</script>
