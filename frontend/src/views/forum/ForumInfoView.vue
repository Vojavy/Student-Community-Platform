<template>
  <div class="space-y-6">
    <!-- Filters grid -->
    <div class="grid grid-cols-1 sm:grid-cols-4 gap-4">
      <input
          v-model="filters.name"
          type="text"
          :placeholder="t('forum.filters.searchPlaceholder')"
          class="w-full p-2 border rounded"
          @keyup.enter="applyFilters"
      />
      <select v-model="filters.domain" class="w-full p-2 border rounded">
        <option value="">{{ t('forum.filters.domainAll') }}</option>
        <option v-for="d in domains" :key="d.id" :value="d.domain">
          {{ d.domainName }}
        </option>
      </select>
      <select v-model="filters.isPublic" class="w-full p-2 border rounded">
        <option :value="null">{{ t('forum.filters.visibilityAll') }}</option>
        <option :value="true">{{ t('forum.filters.public') }}</option>
        <option :value="false">{{ t('forum.filters.private') }}</option>
      </select>
      <select v-model="filters.sort" class="w-full p-2 border rounded">
        <option value="newest">{{ t('forum.sort.newest') }}</option>
        <option value="oldest">{{ t('forum.sort.oldest') }}</option>
      </select>
    </div>

    <!-- Search button -->
    <div class="flex justify-end">
      <button
          @click="applyFilters"
          class="px-4 py-2 bg-accent-primary text-white rounded hover:bg-accent-primary/90 transition text-sm"
      >
        {{ t('common.search') }}
      </button>
    </div>

    <!-- Results list -->
    <div class="space-y-4">
      <div
          v-for="forum in forumsPage.content"
          :key="forum.id"
          @click="goDetails(forum.id)"
          class="bg-secondary p-4 rounded-lg cursor-pointer hover:bg-secondary/90 transition flex flex-col justify-between"
      >
        <!-- top: title + topics -->
        <div class="flex justify-between items-start">
          <h3 class="text-lg font-semibold">{{ forum.name }}</h3>
          <div class="flex flex-wrap gap-2">
            <span
                v-for="topic in forum.topics"
                :key="topic"
                class="text-xs bg-gray-200 px-2 py-0.5 rounded-full"
            >
              {{ topic }}
            </span>
          </div>
        </div>

        <!-- description -->
        <p class="text-sm text-text/70 mt-2 line-clamp-2" v-html="forum.description" />

        <!-- bottom metadata -->
        <div class="mt-4 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-2 text-xs text-text/60">
          <span><strong>ID:</strong> {{ forum.id }}</span>
          <span>
            <strong>{{ t('forum.columns.domain') }}:</strong>
            {{ forum.universityDomain.domainName }}
          </span>
          <span>
            <strong>{{ t('forum.columns.visibility') }}:</strong>
            <span :class="forum.public ? 'text-green-600' : 'text-red-600'">
              {{ forum.public ? t('forum.columns.public') : t('forum.columns.private') }}
            </span>
          </span>
          <span>
            <strong>{{ t('forum.columns.status') }}:</strong>
            {{ t(`forum.status.${forum.status}`) }}
          </span>
          <span>
            <strong>{{ t('forum.columns.createdAt') }}:</strong>
            {{ formatDate(forum.createdAt) }}
          </span>
        </div>
      </div>
    </div>

    <!-- pagination -->
    <div v-if="forumsPage.totalPages > 0" class="flex justify-center space-x-2 mt-6">
      <button
          :disabled="forumsPage.first"
          @click="changePage(forumsPage.page - 1)"
          class="px-3 py-1 border rounded disabled:opacity-50"
      >←</button>
      <span>{{ forumsPage.page + 1 }} / {{ forumsPage.totalPages }}</span>
      <button
          :disabled="forumsPage.last"
          @click="changePage(forumsPage.page + 1)"
          class="px-3 py-1 border rounded disabled:opacity-50"
      >→</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useI18n }               from 'vue-i18n'
import { inject }                from 'vue'
import { useForumStore }         from '@/iam/stores/forumStore.js'
import { useDomainStore }        from '@/iam/stores/domainStore.js'

const { t }       = useI18n()
const coord       = inject('coordinator')
const forumStore  = useForumStore()
const domainStore = useDomainStore()

// domains for filter dropdown
const domains = computed(() => domainStore.domains)

// filters (info-only: status informational)
const filters = ref({
  page:      0,
  size:      20,
  status:    'informational',
  isPublic:  null,
  domain:    '',
  name:      '',
  topics:    [],
  sort:      'newest'
})

const forumsPage = computed(() => forumStore.forums)

async function loadDomains() {
  await domainStore.fetchDomains()
}
async function loadForums() {
  await forumStore.fetchForums({ ...filters.value, domain: filters.value.domain || undefined })
}

function applyFilters() {
  filters.value.page = 0
  loadForums()
}
function changePage(n) {
  filters.value.page = n
  loadForums()
}
function goDetails(id) {
  coord.navigateToForum(id)
}
function formatDate(s) {
  return new Date(s).toLocaleDateString()
}

onMounted(async () => {
  await loadDomains()
  await loadForums()
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
