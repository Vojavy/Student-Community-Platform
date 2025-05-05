<template>
  <div class="space-y-6">
    <div class="flex flex-col sm:flex-row items-center gap-4">
      <input
          v-model="filters.name"
          type="text"
          :placeholder="t('profile.search.placeholderName')"
          class="w-full sm:w-3/4 p-3 border-2 rounded-lg shadow-sm focus:border-indigo-400 focus:ring-2 focus:ring-indigo-200 transition"
          @keyup.enter="applyFilters"
      />
      <button
          @click="applyFilters"
          class="w-full sm:w-1/4 px-5 py-3 bg-indigo-600 text-white font-semibold rounded-lg shadow-md hover:bg-indigo-700 transform hover:-translate-y-0.5 transition text-sm"
      >
        {{ t('common.search') }}
      </button>
    </div>

    <button
        @click="showFilters = !showFilters"
        class="flex items-center text-indigo-600 hover:text-indigo-800 text-sm font-medium transition"
    >
      <svg
          :class="showFilters ? 'rotate-180' : ''"
          class="w-4 h-4 mr-1 transition-transform"
          fill="none" stroke="currentColor" viewBox="0 0 24 24"
      >
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M19 9l-7 7-7-7"/>
      </svg>
      {{ showFilters
        ? t('common.hideFilters')
        : t('common.showFilters') }}
    </button>

    <div v-show="showFilters" class="grid gap-4 grid-cols-1 sm:grid-cols-2 lg:grid-cols-4">
      <input
          v-model="filters.email"
          type="text"
          :placeholder="t('profile.search.placeholderEmail')"
          class="p-3 border-2 rounded-lg shadow-sm transition"
      />
      <select v-model="filters.domain" class="p-3 border-2 rounded-lg shadow-sm transition">
        <option value="">{{ t('profile.search.domainAll') }}</option>
        <option
            v-for="d in domains"
            :key="d.id"
            :value="d.domain"
        >
          {{ d.domainName }}
        </option>
      </select>
      <input
          v-model="filters.rocnik"
          type="text"
          :placeholder="t('profile.search.placeholderRocnik')"
          class="p-3 border-2 rounded-lg shadow-sm transition"
      />
      <select v-model="filters.titul" class="p-3 border-2 rounded-lg shadow-sm transition">
        <option value="">{{ t('profile.search.titulAll') }}</option>
        <option
            v-for="title in titles"
            :key="title"
            :value="title"
        >
          {{ title }}
        </option>
      </select>
      <input
          v-model="filters.fakulta"
          type="text"
          :placeholder="t('profile.search.placeholderFakulta')"
          class="p-3 border-2 rounded-lg shadow-sm transition"
      />
      <input
          v-model="filters.obor"
          type="text"
          :placeholder="t('profile.search.placeholderObor')"
          class="p-3 border-2 rounded-lg shadow-sm transition"
      />
    </div>

    <div class="space-y-4">
      <div
          v-for="u in page.content"
          :key="u.userId"
          @click="goDetails(u.userId)"
          class="bg-white p-6 rounded-2xl shadow-md hover:shadow-xl transform hover:-translate-y-1 transition cursor-pointer"
      >
        <div class="flex justify-between items-start">
          <div>
            <h3 class="text-lg font-bold">{{ u.prijmeni }} {{ u.jmeno }}</h3>
            <p class="text-sm text-gray-600">{{ u.email }}</p>
            <p class="text-sm text-gray-500">{{ u.domain || '—' }}</p>
          </div>
        </div>
        <p class="mt-2 text-gray-700 line-clamp-2" v-html="u.details.bio || '—'"></p>
      </div>
    </div>

    <div v-if="page.totalPages > 0" class="flex justify-center space-x-2 mt-6">
      <button
          :disabled="page.first"
          @click="changePage(page.number - 1)"
          class="px-4 py-2 bg-white border rounded-lg shadow-sm disabled:opacity-50 hover:bg-indigo-50 transition"
      >←</button>
      <span class="px-3 py-2 font-medium">
        {{ page.number  + 1 }} / {{ page.totalPages }}
      </span>
      <button
          :disabled="page.last"
          @click="changePage(page.number + 1)"
          class="px-4 py-2 bg-white border rounded-lg shadow-sm disabled:opacity-50 hover:bg-indigo-50 transition"
      >→</button>
    </div>
  </div>
</template>


<script setup>
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { inject } from 'vue'
import { useUserStore } from '@/iam/stores/userStore.js'
import { useDomainStore } from '@/iam/stores/domainStore.js'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken.js'

const { t }        = useI18n()
const coord        = inject('coordinator')
const userStore    = useUserStore()
const domainStore  = useDomainStore()

const showFilters = ref(false)
const filters     = ref({
  name:   '',
  email:  '',
  domain: '',
  rocnik: '',
  titul:  '',
  fakulta:'',
  obor:   '',
  page:   0,
  size:   20
})

const titles = ['Bc.', 'Mgr.', 'Ing.', 'Ph.D.', 'Dr.', 'Prof.', 'Doc.']
const domains = computed(() => domainStore.domains.map(d => ({
  id: d.id,
  domain: d.domain,
  domainName: d.domainName
})))

const page = computed(() => userStore.searchResults)

async function loadDomains() {
  await domainStore.fetchDomains()
}

async function loadSearch() {
  await userStore.searchUsers(filters.value)
}

function applyFilters() {
  filters.value.page = 0
  loadSearch()
}

function changePage(n) {
  filters.value.page = n
  loadSearch()
}

function goDetails(id) {
  coord.navigateToUser(id)
}

onMounted(async () => {
  await loadDomains()
  await loadSearch()
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
