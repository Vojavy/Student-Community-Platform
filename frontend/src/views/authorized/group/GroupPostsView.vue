<template>
  <div class="space-y-6">
    <!-- Search + “New post” -->
    <div class="flex flex-col sm:flex-row justify-between items-stretch sm:items-center gap-4">
      <input
          v-model="searchTerm"
          type="text"
          :placeholder="t('groups.searchPlaceholder')"
          class="w-full sm:flex-1 p-2 border rounded"
      />
      <button
          v-if="canPost"
          @click="goNewPost"
          class="w-full sm:w-auto px-4 py-2 bg-accent-primary text-white rounded hover:bg-accent-primary/90 transition text-center"
      >
        {{ t('posts.newPost') }}
      </button>
    </div>

    <!-- Posts List -->
    <div
        v-for="(post, i) in filteredPosts"
        :key="post.id"
        class="border rounded-lg p-4 sm:p-6 space-y-2"
    >
      <!-- Header: title + topics -->
      <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2">
        <h2 class="text-lg sm:text-xl font-semibold">{{ post.title }}</h2>
        <div class="flex flex-wrap gap-2">
          <span
              v-for="topic in post.parsedTopics"
              :key="topic"
              class="text-xs sm:text-sm bg-gray-200 px-2 py-0.5 rounded-full"
          >
            {{ topic }}
          </span>
        </div>
      </div>

      <!-- Content preview / full -->
      <div
          class="relative"
          :class="{ 'overflow-hidden': post.isOverflow && !post.expanded }"
          :style="post.isOverflow && !post.expanded ? { maxHeight: previewHeight } : {}"
          :ref="el => postRefs[i] = el"
      >
        <div v-html="post.content" class="prose max-w-none"></div>

        <!-- Fade + Expand button (только при isOverflow и !expanded) -->
        <div
            v-if="post.isOverflow && !post.expanded"
            class="absolute inset-x-0 bottom-0 flex justify-center pb-2 bg-gradient-to-b from-transparent to-white z-10"
        >
          <button
              @click="toggleExpand(post)"
              class="px-4 py-1 bg-accent-secondary text-white rounded hover:bg-accent-secondary/90 transition z-20 relative text-sm"
          >
            {{ t('posts.expand') }}
          </button>
        </div>
      </div>

      <!-- Author / Date / Actions (всегда, но кнопки редактирования/удаления только с правами) -->
      <div class="flex justify-between items-center text-sm text-text/70 mt-2">
        <div>
          {{ t('posts.by') }}
          <strong>{{ post.author.name || post.author.username }}</strong>
          {{ t('posts.on') }}
          {{ formatDate(post.createdAt) }}
        </div>
        <div class="flex gap-2">
          <button
              v-if="canEdit"
              @click="goEdit(post.id)"
              class="px-2 py-1 border rounded hover:bg-gray-100 text-xs sm:text-sm"
          >
            {{ t('posts.edit') }}
          </button>
          <button
              v-if="canDelete"
              @click="deletePost(post.id)"
              class="px-2 py-1 border rounded text-red-600 hover:bg-red-50 text-xs sm:text-sm"
          >
            {{ t('posts.delete') }}
          </button>
        </div>
      </div>

      <!-- Footer: скрыть (только когда expanded и isOverflow) -->
      <div
          v-if="post.expanded && post.isOverflow"
          class="flex justify-end mt-2"
      >
        <button
            @click="toggleExpand(post)"
            class="px-2 py-1 border rounded hover:bg-gray-100 text-xs sm:text-sm"
        >
          {{ t('posts.hide') }}
        </button>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useI18n } from 'vue-i18n'
import { inject }  from 'vue'
import checkRights from '@/utils/groups/checkRights'

import {
  fetchGroupPostsIntent,
  deleteGroupPostIntent
} from '@/iam/intents/groupIntents.js'
import { handleGroupIntent }   from '@/iam/actions/groupActions.js'
import createGroupPostModel    from '@/iam/models/group/groupPostModel.js'

const { t }       = useI18n()
const coord       = inject('coordinator')
const group       = inject('group')
const role        = inject('role')

// права
const canPost   = computed(() => checkRights(role.value, group.value.minRoleForPosts))
const canEdit   = computed(() => checkRights(role.value, group.value.minRoleForPosts))
const canDelete = computed(() => checkRights(role.value, 'admin'))

// поиск и данные
const searchTerm    = ref('')
const posts         = ref([])
const previewHeight = '400px'

// для хранения DOM элементов
const postRefs = ref([])

async function loadPosts() {
  try {
    const page = await handleGroupIntent(
        fetchGroupPostsIntent(group.value.id, { search: searchTerm.value }),
        { model: createGroupPostModel(), coordinator: coord }
    )
    const items = Array.isArray(page) ? page : (page.content || [])
    posts.value = items.map(p => ({
      ...p,
      expanded: false,
      isOverflow: false,
      parsedTopics: (() => {
        try {
          if (p.topics.length === 1 && p.topics[0].trim().startsWith('[')) {
            return JSON.parse(p.topics[0])
          }
        } catch {}
        return p.topics
      })()
    }))
    await nextTick()
    measureOverflow()
  } catch (e) {
    console.error('Fetch posts failed', e)
  }
}

function measureOverflow() {
  posts.value.forEach((post, i) => {
    const el = postRefs.value[i]
    if (el) {
      const overflow = el.scrollHeight > parseInt(previewHeight)
      post.isOverflow = overflow
      if (!overflow) post.expanded = true
    }
  })
}

onMounted(loadPosts)
watch(searchTerm, async () => {
  await loadPosts()
})

// навигация / действия
function goNewPost()      { coord.navigateToGroupNewPost(group.value.id) }
function goEdit(id)       { coord.navigateToGroupEditPost(group.value.id, id) }
async function deletePost(id) {
  if (!canDelete.value) return
  await handleGroupIntent(
      deleteGroupPostIntent(group.value.id, id),
      { model: createGroupPostModel(), coordinator: coord }
  )
  await loadPosts()
}
function toggleExpand(post) { post.expanded = !post.expanded }
function formatDate(d)       { return new Date(d).toLocaleString() }

// фильтрация
const filteredPosts = computed(() => {
  const term = searchTerm.value.trim().toLowerCase()
  return posts.value.filter(p =>
      !term
      || p.title.toLowerCase().includes(term)
      || stripHtml(p.content).toLowerCase().includes(term)
  )
})

function stripHtml(html) {
  const div = document.createElement('div')
  div.innerHTML = html
  return div.textContent || div.innerText || ''
}
</script>

<style scoped>
.mask-gradient {
  /* фейд начинается с 75% от блока и до конца */
  -webkit-mask-image: -webkit-linear-gradient(
      to bottom,
      rgba(0,0,0,1) 75%,
      rgba(0,0,0,0) 100%
  );
  mask-image: linear-gradient(
      to bottom,
      rgba(0,0,0,1) 75%,
      rgba(0,0,0,0) 100%
  );
}
</style>
