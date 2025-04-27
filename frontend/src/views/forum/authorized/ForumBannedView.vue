<!-- src/views/forum/authorized/ForumBannedView.vue -->
<template>
  <div class="space-y-6">
    <!-- Name + Search -->
    <div class="flex items-center gap-4">
      <input
          v-model="filters.name"
          type="text"
          :placeholder="t('forum.filters.searchPlaceholder')"
          class="w-3/4 p-2 border rounded"
          @keyup.enter="applyFilters"
      />
      <button
          @click="applyFilters"
          class="w-1/4 px-4 py-2 bg-accent-primary text-white rounded hover:bg-accent-primary/90 transition text-sm"
      >
        {{ t('common.search') }}
      </button>
    </div>

    <!-- Toggle extra filters -->
    <button
        @click="showFilters = !showFilters"
        class="text-sm text-accent-primary hover:underline"
    >
      {{ showFilters ? t('forum.actions.hideFilters') : t('forum.actions.showFilters') }}
    </button>

    <!-- Extra: Domain + Sort -->
    <div v-show="showFilters" class="grid gap-4 grid-cols-1 sm:grid-cols-2 lg:grid-cols-4">
      <select v-model="filters.domain" class="p-2 border rounded">
        <option value="">{{ t('forum.filters.domainAll') }}</option>
        <option v-for="d in domains" :key="d.id" :value="d.domain">
          {{ d.domainName }}
        </option>
      </select>
      <select v-model="filters.sort" class="p-2 border rounded">
        <option value="newest">{{ t('forum.sort.newest') }}</option>
        <option value="oldest">{{ t('forum.sort.oldest') }}</option>
      </select>
    </div>

    <!-- Results -->
    <div class="space-y-4">
      <div
          v-for="forum in page.content"
          :key="forum.id"
          @click="goDetails(forum.id)"
          class="bg-secondary p-4 rounded-lg cursor-pointer hover:bg-secondary/90 transition flex flex-col justify-between"
      >
        <!-- Top row -->
        <div class="flex justify-between items-start">
          <h3 class="text-lg font-semibold">{{ forum.name }}</h3>
          <div class="flex flex-wrap gap-2">
            <span v-for="topic in forum.topics" :key="topic" class="text-xs bg-gray-200 px-2 py-0.5 rounded-full">
              {{ topic }}
            </span>
          </div>
        </div>
        <!-- Description -->
        <p class="text-sm text-text/70 mt-2 line-clamp-2" v-html="forum.description" />
        <!-- Bottom metadata -->
        <div class="mt-4 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-2 text-xs text-text/60">
          <span><strong>ID:</strong> {{ forum.id }}</span>
          <span>
            <strong>{{ t('forum.columns.domain') }}:</strong> {{ forum.universityDomain.domain }}
          </span>
          <span>
            <strong>{{ t('forum.columns.status') }}:</strong> {{ t(`forum.status.${forum.status}`) }}
          </span>
          <span>
            <strong>{{ t('forum.columns.createdAt') }}:</strong> {{ formatDate(forum.createdAt) }}
          </span>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div class="flex justify-center space-x-2 mt-6">
      <button
          :disabled="page.first"
          @click="changePage(page.number - 1)"
          class="px-3 py-1 border rounded disabled:opacity-50"
      >←</button>
      <span>{{ page.number + 1 }} / {{ page.totalPages }}</span>
      <button
          :disabled="page.last"
          @click="changePage(page.number + 1)"
          class="px-3 py-1 border rounded disabled:opacity-50"
      >→</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useI18n }        from 'vue-i18n'
import { inject }         from 'vue'

import { fetchForumsIntent } from '@/iam/intents/forumIntents.js'
import { handleForumIntent } from '@/iam/actions/forumActions.js'
import createForumModel      from '@/iam/models/forumModel.js'

import { fetchDomainsIntent } from '@/iam/intents/domainIntents.js'
import { handleDomainIntent } from '@/iam/actions/domainActions.js'
import createDomainModel      from '@/iam/models/domainModel.js'

const { t }   = useI18n()
const coord   = inject('coordinator')

const domains     = ref([])
const page        = ref({ content: [], number:0, totalPages:1, first:true, last:true })
const showFilters = ref(false)
const filters     = ref({
  page:    0,
  size:    20,
  status:  'banned',
  domain:  '',
  name:    '',
  sort:    'newest'
})

async function load() {
  page.value = await handleForumIntent(
      fetchForumsIntent({ ...filters.value, domain: filters.value.domain || undefined }),
      { model: createForumModel(), coordinator: coord }
  )
}
function applyFilters() { filters.value.page = 0; load() }
function changePage(n)    { filters.value.page = n; load() }
function goDetails(id)    { coord.navigateToForum(id) }
function formatDate(s)    { return new Date(s).toLocaleDateString() }

onMounted(async () => {
  domains.value = await handleDomainIntent(
      fetchDomainsIntent(),
      { model: createDomainModel(), coordinator: coord }
  )
  load()
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
