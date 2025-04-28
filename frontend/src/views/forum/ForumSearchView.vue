<template>
  <div class="space-y-6">
    <!-- Название + кнопка поиска -->
    <div class="flex flex-col sm:flex-row items-center gap-4">
      <input
          v-model="filters.name"
          type="text"
          :placeholder="t('forum.filters.searchPlaceholder')"
          class="w-full sm:w-3/4 p-3 border-2 border-gray-200 rounded-lg shadow-sm focus:border-indigo-400 focus:ring-2 focus:ring-indigo-200 transition"
          @keyup.enter="applyFilters"
      />
      <button
          @click="applyFilters"
          class="w-full sm:w-1/4 px-5 py-3 bg-indigo-600 text-white font-semibold rounded-lg shadow-md hover:bg-indigo-700 transform hover:-translate-y-0.5 transition text-sm"
      >
        {{ t('common.search') }}
      </button>
    </div>

    <!-- Toggle доп. фильтров -->
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
      {{ showFilters ? t('forum.actions.hideFilters') : t('forum.actions.showFilters') }}
    </button>

    <!-- Дополнительные фильтры -->
    <div v-show="showFilters" class="grid gap-4 grid-cols-1 sm:grid-cols-2 lg:grid-cols-4">
      <input
          v-model="topicsInput"
          type="text"
          :placeholder="t('forum.filters.topicsPlaceholder')"
          class="p-3 border-2 border-gray-200 rounded-lg shadow-sm focus:border-indigo-400 focus:ring-2 focus:ring-indigo-200 transition"
      />
      <select v-model="filters.isPublic" class="p-3 border-2 border-gray-200 rounded-lg shadow-sm focus:border-indigo-400 focus:ring-2 focus:ring-indigo-200 transition">
        <option :value="null">{{ t('forum.filters.publicAll') }}</option>
        <option :value="true">{{ t('forum.filters.public') }}</option>
        <option :value="false" v-if="isAuth">{{ t('forum.filters.private') }}</option>
      </select>
      <select v-model="filters.domain" class="p-3 border-2 border-gray-200 rounded-lg shadow-sm focus:border-indigo-400 focus:ring-2 focus:ring-indigo-200 transition">
        <option :value="''">{{ t('forum.filters.domainAll') }}</option>
        <option v-for="d in domains" :key="d.id" :value="d.domain">
          {{ d.domainName }}
        </option>
      </select>
      <select v-model="filters.status" class="p-3 border-2 border-gray-200 rounded-lg shadow-sm focus:border-indigo-400 focus:ring-2 focus:ring-indigo-200 transition">
        <option :value="null">{{ t('forum.filters.statusAll') }}</option>
        <option value="active">{{ t('forum.status.active') }}</option>
        <option value="informational">{{ t('forum.status.informational') }}</option>
      </select>
      <select v-model="filters.sort" class="p-3 border-2 border-gray-200 rounded-lg shadow-sm focus:border-indigo-400 focus:ring-2 focus:ring-indigo-200 transition">
        <option value="newest">{{ t('forum.sort.newest') }}</option>
        <option value="oldest">{{ t('forum.sort.oldest') }}</option>
      </select>
    </div>

    <!-- Список форумов -->
    <div class="space-y-4">
      <div
          v-for="forum in page.content"
          :key="forum.id"
          @click="goDetails(forum.id)"
          class="bg-white p-6 rounded-2xl shadow-md hover:shadow-xl transform hover:-translate-y-1 transition cursor-pointer flex flex-col justify-between"
      >
        <!-- Верхняя строка -->
        <div class="flex justify-between items-start">
          <h3 class="text-xl font-bold text-gray-800">{{ forum.name }}</h3>
          <div class="flex flex-wrap gap-2">
            <span
                v-for="topic in forum.topics"
                :key="topic"
                class="text-xs bg-indigo-100 text-indigo-800 px-2 py-0.5 rounded-full"
            >
              {{ topic }}
            </span>
          </div>
        </div>

        <!-- Описание -->
        <p class="text-gray-600 mt-2 line-clamp-2" v-html="forum.description" />

        <!-- Метаданные -->
        <div class="mt-4 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-2 text-xs text-gray-500">
          <span><strong>ID:</strong> {{ forum.id }}</span>
          <span>
            <strong>{{ t('forum.columns.domain') }}:</strong>
            {{ forum.universityDomain.domain }}
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
            <strong>{{ t('forum.columns.pinned') }}:</strong>
            <span v-if="forum.pinned">🔖</span><span v-else>—</span>
          </span>
          <span>
            <strong>{{ t('forum.columns.createdAt') }}:</strong>
            {{ formatDate(forum.createdAt) }}
          </span>
        </div>
      </div>
    </div>

    <!-- Пагинация -->
    <div v-if="page.totalPages!==0" class="flex justify-center space-x-2 mt-6">
      <button
          :disabled="page.first"
          @click="changePage(page.number - 1)"
          class="px-4 py-2 bg-white border rounded-lg shadow-sm disabled:opacity-50 hover:bg-indigo-50 transition"
      >←</button>
      <span class="px-3 py-2 font-medium">{{ page.number + 1 }} / {{ page.totalPages }}</span>
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

import { fetchForumsIntent } from '@/iam/intents/forumIntents.js'
import { handleForumIntent } from '@/iam/actions/forumActions.js'
import createForumModel from '@/iam/models/forumModel.js'

import { fetchDomainsIntent } from '@/iam/intents/domainIntents.js'
import { handleDomainIntent } from '@/iam/actions/domainActions.js'
import createDomainModel from '@/iam/models/domainModel.js'

import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken.js'

const { t } = useI18n()
const coord = inject('coordinator')
const role = inject('user_roles')

const isAuth = computed(() => !!getUserIdFromToken())

// models
const forumModel = createForumModel()
const domainModel = createDomainModel()

// state
const showFilters = ref(false)
const filters = ref({
  page:     0,
  size:     20,
  isPublic: true,
  pinned:   null,
  domain:   '',
  status:   null,
  name:     '',
  topics:   [],
  sort:     'newest'
})
const topicsInput = ref('')
const domains = ref([])
const page = ref({ content: [], number: 0, totalPages: 1, first: true, last: true })

async function load() {
  const topics = topicsInput.value
      .split(',')
      .map(s => s.trim())
      .filter(Boolean)

  const payload = {
    ...filters.value,
    topics,
    domain: filters.value.domain || undefined,
    pinned: filters.value.pinned !== null ? filters.value.pinned : undefined
  }

  const data = await handleForumIntent(
      fetchForumsIntent(payload),
      { model: forumModel, coordinator: coord }
  )
  page.value = data
}

function applyFilters() {
  filters.value.page = 0
  filters.value.name = filters.value.name.trim()
  load()
}

function changePage(n) {
  filters.value.page = n
  load()
}

function goDetails(id) {
  coord.navigateToForum(id)
}

function formatDate(dateStr) {
  return new Date(dateStr).toLocaleDateString()
}

onMounted(async () => {
  domains.value = await handleDomainIntent(
      fetchDomainsIntent(),
      { model: domainModel, coordinator: coord }
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
