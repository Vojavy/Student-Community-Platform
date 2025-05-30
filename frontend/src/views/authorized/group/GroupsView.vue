<template>
  <div class="space-y-6 p-8 max-w-4xl mx-auto bg-gray-50 rounded-2xl shadow-lg">
    <h1 class="text-3xl font-extrabold text-gray-800">{{ t('groups.title') }}</h1>

    <!-- Tabs -->
    <div class="flex space-x-6 border-b border-gray-200 mb-6">
      <button
          @click="tab = 'my'"
          :class="tab==='my'
          ? 'pb-2 border-b-2 border-accent-primary text-accent-primary font-semibold'
          : 'pb-2 text-text/70 hover:text-text/90 transition'"
      >
        {{ t('groups.tabs.my') }}
      </button>
      <button
          @click="tab = 'browse'"
          :class="tab==='browse'
          ? 'pb-2 border-b-2 border-accent-primary text-accent-primary font-semibold'
          : 'pb-2 text-text/70 hover:text-text/90 transition'"
      >
        {{ t('groups.tabs.browse') }}
      </button>
    </div>

    <!-- My Groups -->
    <div v-if="tab==='my'" class="space-y-6">
      <div class="flex gap-4">
        <input
            v-model="searchMy"
            type="text"
            :placeholder="t('groups.searchPlaceholder')"
            class="w-3/4 p-3 border-2 border-gray-200 rounded-lg shadow-sm focus:border-accent-primary focus:ring-2 focus:ring-accent-primary/20 transition"
        />
        <button
            @click="loadMy(0)"
            class="w-1/4 px-4 py-3 bg-accent-primary text-white rounded-lg shadow-md hover:bg-accent-primary/90 transition"
        >
          {{ t('common.search') }}
        </button>
      </div>

      <ul class="space-y-4">
        <li
            v-for="g in filteredMy"
            :key="g.id"
            @click="goToGroup(g.id)"
            class="bg-white p-5 rounded-xl shadow-md hover:shadow-xl transform hover:-translate-y-0.5 transition cursor-pointer"
        >
          <div class="flex justify-between items-center">
            <span class="text-lg font-medium text-gray-800">{{ g.name }}</span>
            <span class="text-xs text-text/60">{{ g.domain || t('groups.domainUnknown') }}</span>
          </div>
          <p class="mt-1 text-sm text-text/70">{{ g.topics.join(', ') }}</p>
          <p class="mt-2 text-xs">
            <span :class="g.public ? 'text-green-600' : 'text-red-600'">
              {{ g.public ? t('groups.public') : t('groups.private') }}
            </span>
          </p>
        </li>
        <li v-if="filteredMy.length===0" class="py-6 text-center text-text/60 italic">
          {{ t('groups.noMyGroups') }}
        </li>
      </ul>

      <div v-if="myTotalPages>1" class="flex items-center justify-center space-x-4">
        <button
            @click="loadMy(myPage-1)"
            :disabled="myPage===0"
            class="px-4 py-2 bg-white border rounded-lg shadow-sm disabled:opacity-50 hover:bg-gray-100 transition"
        >
          {{ t('groups.pagination.prev') }}
        </button>
        <span class="font-medium">{{ myPage+1 }} / {{ myTotalPages }}</span>
        <button
            @click="loadMy(myPage+1)"
            :disabled="myPage+1>=myTotalPages"
            class="px-4 py-2 bg-white border rounded-lg shadow-sm disabled:opacity-50 hover:bg-gray-100 transition"
        >
          {{ t('groups.pagination.next') }}
        </button>
      </div>
    </div>

    <!-- Browse Groups -->
    <div v-else class="space-y-6">
      <!-- simple search -->
      <div class="flex gap-4">
        <input
            v-model="filters.name"
            type="text"
            :placeholder="t('groups.filters.name')"
            class="w-3/4 p-3 border-2 border-gray-200 rounded-lg shadow-sm focus:border-accent-primary focus:ring-2 focus:ring-accent-primary/20 transition"
        />
        <button
            @click="loadBrowse(0)"
            class="w-1/4 px-4 py-3 bg-accent-primary text-white rounded-lg shadow-md hover:bg-accent-primary/90 transition"
        >
          {{ t('common.search') }}
        </button>
      </div>

      <!-- toggle filters -->
      <button
          @click="showFilters = !showFilters"
          class="text-sm text-accent-primary hover:underline"
      >
        {{ showFilters ? t('forum.actions.hideFilters') : t('forum.actions.showFilters') }}
      </button>
      <transition name="slide-fade">
        <div v-if="showFilters" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
          <select v-model="filters.domain" class="p-3 border-2 border-gray-200 rounded-lg shadow-sm focus:border-accent-primary focus:ring-2 focus:ring-accent-primary/20 transition">
            <option :value="''">{{ t('groups.filters.domainAll') }}</option>
            <option v-for="d in domains" :key="d.id" :value="d.id">{{ d.domainName }}</option>
          </select>
          <select v-model="filters.isPublic" class="p-3 border-2 border-gray-200 rounded-lg shadow-sm focus:border-accent-primary focus:ring-2 focus:ring-accent-primary/20 transition">
            <option :value="''">{{ t('groups.filters.accessAll') }}</option>
            <option :value="true">{{ t('groups.filters.public') }}</option>
            <option :value="false">{{ t('groups.filters.private') }}</option>
          </select>
          <input
              v-model="filters.topics"
              type="text"
              :placeholder="t('groups.filters.topics')"
              class="p-3 border-2 border-gray-200 rounded-lg shadow-sm focus:border-accent-primary focus:ring-2 focus:ring-accent-primary/20 transition"
          />
          <div class="flex items-center gap-2">
            <label class="text-sm">{{ t('groups.pagination.size') }}:</label>
            <select v-model.number="browseSize" @change="loadBrowse(0)" class="p-2 border-2 border-gray-200 rounded-lg shadow-sm focus:border-accent-primary focus:ring-2 focus:ring-accent-primary/20 transition">
              <option v-for="opt in [25,50,100]" :key="opt" :value="opt">{{ opt }}</option>
            </select>
          </div>
        </div>
      </transition>

      <!-- results -->
      <ul class="space-y-4">
        <li
            v-for="g in browseResults"
            :key="g.id"
            @click="goToGroup(g.id)"
            class="bg-white p-5 rounded-xl shadow-md hover:shadow-xl transform hover:-translate-y-0.5 transition cursor-pointer"
        >
          <div class="flex justify-between items-center">
            <span class="text-lg font-medium text-gray-800">{{ g.name }}</span>
            <span class="text-xs text-text/60">{{ g.domain || t('groups.domainUnknown') }}</span>
          </div>
          <p class="mt-1 text-sm text-text/70">{{ g.topics.join(', ') }}</p>
          <p class="mt-2 text-xs">
            <span :class="g.public ? 'text-green-600' : 'text-red-600'">
              {{ g.public ? t('groups.public') : t('groups.private') }}
            </span>
          </p>
        </li>
        <li v-if="browseResults.length===0" class="py-6 text-center text-text/60 italic">
          {{ t('groups.noBrowseResults') }}
        </li>
      </ul>

      <div v-if="browseTotalPages>1" class="flex items-center justify-center space-x-4">
        <button
            @click="loadBrowse(browsePage-1)"
            :disabled="browsePage===0"
            class="px-4 py-2 bg-white border rounded-lg shadow-sm disabled:opacity-50 hover:bg-gray-100 transition"
        >
          {{ t('groups.pagination.prev') }}
        </button>
        <span class="font-medium">{{ browsePage+1 }} / {{ browseTotalPages }}</span>
        <button
            @click="loadBrowse(browsePage+1)"
            :disabled="browsePage+1>=browseTotalPages"
            class="px-4 py-2 bg-white border rounded-lg shadow-sm disabled:opacity-50 hover:bg-gray-100 transition"
        >
          {{ t('groups.pagination.next') }}
        </button>
      </div>
    </div>

    <button
        @click="coordinator.navigateToCreateGroup()"
        class="fixed right-6 bg-accent-primary text-white w-14 h-14 rounded-full shadow-lg flex items-center justify-center hover:bg-accent-primary/90 transform hover:-translate-y-0.5 transition"
        :class="isMobile ? 'bottom-20' : 'bottom-6'"
        aria-label="Create Group"
    >
      <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
        <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
      </svg>
    </button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useIsMobile } from '@/utils/device/useIsMobile'
import { useGroupStore } from '@/iam/stores/groupStore.js'
import { useDomainStore } from '@/iam/stores/domainStore.js'

const { t } = useI18n()
const coordinator = inject('coordinator')
const isMobile    = useIsMobile()
const store       = useGroupStore()
const domainStore = useDomainStore()

const tab = ref('my')

const searchMy     = ref('')
const myPage       = ref(0)
const mySize       = ref(25)
const myTotalPages = computed(() => store.userGroups.totalPages || 1)
const myGroups     = computed(() => store.userGroups.content || [])
const filteredMy   = computed(() => {
  const term = searchMy.value.toLowerCase()
  return myGroups.value.filter(g =>
      g.name.toLowerCase().includes(term) ||
      g.topics.some(tp => tp.toLowerCase().includes(term))
  )
})

const filters       = ref({ name: '', domain: '', isPublic: '', topics: '' })
const browsePage    = ref(0)
const browseSize    = ref(25)
const browseTotalPages = computed(() => store.browseGroups.totalPages || 1)
const browseResults = computed(() => store.browseGroups.content || [])
const domains       = computed(() => domainStore.domains)
const showFilters   = ref(false)

onMounted(async () => {
  await loadMy(0)
  await domainStore.fetchDomains()
  await loadBrowse(0)
})

async function loadMy(page) {
  myPage.value = page
  await store.fetchUserGroups(page, mySize.value)
}

async function loadBrowse(page) {
  browsePage.value = page
  const q = {
    page,
    size: browseSize.value,
    ...(filters.value.name && { name: filters.value.name }),
    ...(filters.value.domain && { domainId: Number(filters.value.domain) }),
    ...(filters.value.isPublic !== '' && { isPublic: filters.value.isPublic === true || filters.value.isPublic === 'true' }),
    ...(filters.value.topics && { topics: filters.value.topics.split(',').map(s=>s.trim()).filter(Boolean) })
  }
  await store.fetchBrowseGroups(q)
}

function goToGroup(id) {
  coordinator.navigateToGroup(id)
}
</script>

<style scoped>
.slide-fade-enter-active,
.slide-fade-leave-active { transition: all .2s ease; }
.slide-fade-enter-from,
.slide-fade-leave-to     { opacity: 0; transform: translateY(-8px); }
</style>
