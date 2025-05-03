<template>
  <div class="space-y-6">
    <!-- Search + “New post” -->
    <div class="flex flex-col sm:flex-row justify-between items-stretch sm:items-center gap-4">
      <div class="flex-1 flex">
        <input
            v-model="searchTerm"
            type="text"
            :placeholder="t('groups.searchPlaceholder')"
            class="w-full p-2 border rounded-l"
        />
        <button
            @click="onSearchClick"
            class="px-4 py-2 bg-accent-primary text-white rounded-r hover:bg-accent-primary/90 transition"
        >
          🔍
        </button>
      </div>
      <button
          v-if="canPost"
          @click="goNewPost"
          class="w-full sm:w-auto px-4 py-2 bg-accent-primary text-white rounded hover:bg-accent-primary/90 transition"
      >
        {{ t('posts.newPost') }}
      </button>
    </div>

    <!-- Posts List -->
    <div
        v-for="(post, i) in filteredPosts"
        :key="post.id"
        class="border rounded-lg p-4 sm:p-6 space-y-2"
        :ref="el => postRefs[i] = el"
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
      >
        <div v-html="post.content" class="prose max-w-none"></div>
        <div
            v-if="post.isOverflow && !post.expanded"
            class="absolute inset-x-0 bottom-0 flex justify-center pb-2 bg-gradient-to-b from-transparent to-white"
        >
          <button
              @click="post.expanded = true"
              class="px-4 py-1 bg-accent-secondary text-white rounded hover:bg-accent-secondary/90 transition text-sm"
          >
            {{ t('posts.expand') }}
          </button>
        </div>
      </div>

      <!-- Footer: author/date/actions -->
      <div class="flex justify-between items-center text-sm text-text/70 mt-2">
        <div>
          {{ t('posts.by') }}
          <strong>{{ post.author.name || post.author.username }}</strong>
          {{ t('posts.on') }} {{ formatDate(post.createdAt) }}
        </div>
        <div class="flex gap-2">
          <button
              v-if="canEdit"
              @click="goEdit(post.id)"
              class="px-2 py-1 border rounded hover:bg-gray-100 text-xs"
          >
            {{ t('posts.edit') }}
          </button>
          <button
              v-if="canDelete"
              @click="removePost(post.id)"
              class="px-2 py-1 border rounded text-red-600 hover:bg-red-50 text-xs"
          >
            {{ t('posts.delete') }}
          </button>
        </div>
      </div>

      <div v-if="post.expanded && post.isOverflow" class="flex justify-end mt-2">
        <button
            @click="post.expanded = false"
            class="px-2 py-1 border rounded hover:bg-gray-100 text-xs"
        >
          {{ t('posts.hide') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useI18n } from 'vue-i18n'
import { inject } from 'vue'
import checkRights from '@/utils/groups/checkRights'
import { useGroupStore } from '@/iam/stores/groupStore.js'

const { t }       = useI18n()
const coord       = inject('coordinator')
const group       = inject('group')
const role        = inject('role')
const store       = useGroupStore()

const searchTerm    = ref('')
const posts         = ref([])
const postRefs      = ref([])
const previewHeight = '400px'
const isLoading     = ref(false)

const canPost   = computed(() => checkRights(role.value, group.value.minRoleForPosts))
const canEdit   = computed(() => checkRights(role.value, group.value.minRoleForPosts))
const canDelete = computed(() => checkRights(role.value, 'admin'))

async function loadPosts() {
  if (isLoading.value) return
  isLoading.value = true
  try {
    const { content } = await store.fetchGroupPosts(
        group.value.id,
        0,
        store.posts.size,
        searchTerm.value
    )

    posts.value = content.map(p => ({
      ...p,
      expanded: false,
      isOverflow: false,
      parsedTopics: store._parseTopics(p.topics)
    }))

    await nextTick()
    posts.value.forEach((post, i) => {
      const el = postRefs.value[i]
      if (el && el.scrollHeight > parseInt(previewHeight)) {
        post.isOverflow = true
      } else {
        post.expanded = true
      }
    })
  } finally {
    isLoading.value = false
  }
}

function onSearchClick() {
  loadPosts()
}

function goNewPost() { coord.navigateToGroupNewPost(group.value.id) }
function goEdit(id)  { coord.navigateToGroupEditPost(group.value.id, id) }
async function removePost(id) {
  await store.deleteGroupPost(group.value.id, id)
  await loadPosts()
}

function formatDate(d) {
  return new Date(d).toLocaleString()
}

const filteredPosts = computed(() => {
  const q = searchTerm.value.trim().toLowerCase()
  if (!q) return posts.value
  return posts.value.filter(p =>
      p.title.toLowerCase().includes(q) ||
      stripHtml(p.content).toLowerCase().includes(q)
  )
})
function stripHtml(html) {
  const div = document.createElement('div')
  div.innerHTML = html
  return div.textContent || div.innerText || ''
}

onMounted(() => {
  console.log('[GroupPostsView] onMounted')
  loadPosts()
})

</script>
